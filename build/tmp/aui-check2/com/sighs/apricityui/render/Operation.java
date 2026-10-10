/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.behavior.richtext.RichTextEditing;
import com.sighs.apricityui.behavior.richtext.RichTextRange;
import com.sighs.apricityui.behavior.richtext.RichTextSelection;
import com.sighs.apricityui.dev.DevTools;
import com.sighs.apricityui.dev.ResourceManager;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.Input;
import com.sighs.apricityui.element.RichText;
import com.sighs.apricityui.element.Select;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.KeyEvent;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.loader.ClientLoader;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.ClipboardDataBridge;
import com.sighs.apricityui.render.DocumentLayerOrder;
import com.sighs.apricityui.spi.AuiServices;
import java.util.List;

public class Operation {
    public static Position cachedMousePosition = null;
    private static int mouseButtons = 0;
    private static final long KEY_DEDUP_WINDOW_NS = 5000000L;
    private static long lastKeyEventTimeNs = 0L;
    private static int lastKeyCode = -1;
    private static int lastScanCode = -1;
    private static int lastAction = -1;
    private static int lastModifiers = -1;
    private static boolean lastDevToolsInspectConsumed;
    private static String internalClipboardHtml;

    public static boolean onMouseDown() {
        return Operation.onMouseDown(-1);
    }

    public static boolean onMouseDown(int button) {
        lastDevToolsInspectConsumed = false;
        mouseButtons |= Operation.buttonMask(button);
        Position mousePosition = Operation.getMousePositionDirectly();
        if (DevTools.handleInspectMouseDown(mousePosition, button)) {
            lastDevToolsInspectConsumed = true;
            return true;
        }
        MouseEvent event = new MouseEvent("mousedown", mousePosition, button);
        event.setTrusted(true);
        MouseEvent.tiggerEvent(event);
        return event.isNativeConsumed();
    }

    public static boolean onMouseUp() {
        return Operation.onMouseUp(-1);
    }

    public static boolean onMouseUp(int button) {
        lastDevToolsInspectConsumed = false;
        mouseButtons &= ~Operation.buttonMask(button);
        if (DevTools.handleInspectMouseUp(button)) {
            lastDevToolsInspectConsumed = true;
            return true;
        }
        MouseEvent event = new MouseEvent("mouseup", Operation.getMousePositionDirectly(), button);
        event.setTrusted(true);
        MouseEvent.tiggerEvent(event);
        return event.isNativeConsumed();
    }

    public static boolean wasDevToolsInspectConsumed() {
        return lastDevToolsInspectConsumed;
    }

    public static void onMouseMove(Position currentMousePosition) {
        if (currentMousePosition == null) {
            currentMousePosition = Operation.getMousePositionDirectly();
        }
        if (currentMousePosition == null) {
            return;
        }
        if (cachedMousePosition != null) {
            if (Double.compare(currentMousePosition.x, Operation.cachedMousePosition.x) == 0 && Double.compare(currentMousePosition.y, Operation.cachedMousePosition.y) == 0) {
                return;
            }
            MouseEvent mouseEvent = new MouseEvent("mousemove", currentMousePosition);
            mouseEvent.movementX = currentMousePosition.x - Operation.cachedMousePosition.x;
            mouseEvent.movementY = currentMousePosition.y - Operation.cachedMousePosition.y;
            mouseEvent.setTrusted(true);
            MouseEvent.tiggerEvent(mouseEvent);
        }
        cachedMousePosition = currentMousePosition;
    }

    public static boolean scroll(double delta) {
        MouseEvent mouseEvent = new MouseEvent("wheel", Operation.getMousePositionDirectly());
        mouseEvent.scrollDelta = mouseEvent.deltaY = -delta * 50.0;
        mouseEvent.cancelable = true;
        mouseEvent.setTrusted(true);
        MouseEvent.tiggerEvent(mouseEvent);
        return mouseEvent.isNativeConsumed();
    }

    public static int getMouseButtons() {
        return mouseButtons;
    }

    private static int buttonMask(int button) {
        return switch (button) {
            case 0 -> 1;
            case 1 -> 2;
            case 2 -> 4;
            case 3 -> 8;
            case 4 -> 16;
            default -> 0;
        };
    }

    public static boolean onCharTyped(char code) {
        return Operation.onCharTyped((int)code);
    }

    public static boolean onCharTyped(int codePoint) {
        if (!Character.isValidCodePoint(codePoint)) {
            return false;
        }
        String content = new String(Character.toChars(codePoint));
        boolean shouldCancel = false;
        for (Document document : Document.getAll()) {
            RichText richText;
            AbstractText textElement;
            Element focusedElement = document.getFocusedElement();
            if (focusedElement instanceof AbstractText && (textElement = (AbstractText)focusedElement).canEditText()) {
                Event.runTrustedAction(() -> textElement.insertText(content));
                shouldCancel = true;
                continue;
            }
            if (!(focusedElement instanceof RichText) || !(richText = (RichText)focusedElement).canEditText()) continue;
            Event.runTrustedAction(() -> RichTextEditing.insertText(richText, content));
            shouldCancel = true;
        }
        return shouldCancel;
    }

    public static boolean onKeyPressed(int key, int scanCode, int modifiers, boolean repeat, KeyEvent.Source source) {
        Document selectionTargetDocument = Operation.resolveSelectionTargetDocument();
        boolean cancel = false;
        for (Document document : Document.getAll()) {
            boolean[] documentCanceled = new boolean[]{false};
            Event.runTrustedAction(() -> {
                Select select;
                Input input;
                KeyEvent keyEvent = KeyEvent.triggerEvent(document, "keydown", key, scanCode, modifiers, repeat, source);
                if (keyEvent != null && keyEvent.defaultPrevented) {
                    documentCanceled[0] = true;
                    return;
                }
                Element focusedElement = document.getFocusedElement();
                String selectedText = Operation.resolveSelectedText(document, focusedElement);
                boolean ctrlDown = Operation.isCtrlDown();
                if (focusedElement instanceof Input && (input = (Input)focusedElement).handleRangeKey(key)) {
                    documentCanceled[0] = true;
                    return;
                }
                if (focusedElement instanceof RichText) {
                    RichTextSelection selection = document.getRichTextSelection();
                    if (selection == null || !selection.hasAnchor()) {
                        selection.setCollapsed(focusedElement, 0);
                    }
                    boolean keepSelection = Operation.isShiftDown();
                    boolean handled = true;
                    if (key == 263) {
                        selection.moveLeft(keepSelection);
                    } else if (key == 262) {
                        selection.moveRight(keepSelection);
                    } else if (key == 265) {
                        selection.moveUp(keepSelection);
                    } else if (key == 264) {
                        selection.moveDown(keepSelection);
                    } else if (key == 268) {
                        selection.moveToHome(keepSelection);
                    } else if (key == 269) {
                        selection.moveToEnd(keepSelection);
                    } else if (key == 65 && ctrlDown) {
                        selection.selectAll(focusedElement);
                    } else if (key == 256) {
                        document.clearFocus();
                    } else if (key == 259) {
                        RichTextEditing.deleteBackward((RichText)focusedElement);
                    } else if (key == 261) {
                        RichTextEditing.deleteForward((RichText)focusedElement);
                    } else if (key == 257) {
                        RichTextEditing.insertParagraph((RichText)focusedElement);
                    } else if (ctrlDown && key == 90 && !Operation.isShiftDown()) {
                        RichTextEditing.undo((RichText)focusedElement);
                    } else if (ctrlDown && (key == 89 || key == 90 && Operation.isShiftDown())) {
                        RichTextEditing.redo((RichText)focusedElement);
                    } else if (ctrlDown && key == 86) {
                        Event clipboard = new Event(focusedElement, "paste", true);
                        clipboard.cancelable = true;
                        clipboard.clipboardData = new ClipboardDataBridge();
                        Event.markTrustedFromCurrentDispatch(clipboard);
                        Event.tiggerEvent(clipboard);
                        if (!clipboard.defaultPrevented) {
                            String internalHtml = Operation.getInternalClipboardHtml();
                            if (internalHtml != null) {
                                RichTextEditing.pasteHtml((RichText)focusedElement, internalHtml);
                            } else {
                                RichTextEditing.pasteText((RichText)focusedElement, Operation.getClipboardText());
                            }
                        }
                    } else if (ctrlDown && key == 67 && !selection.collapsed()) {
                        Event clipboard = new Event(focusedElement, "copy", true);
                        clipboard.cancelable = true;
                        clipboard.clipboardData = new ClipboardDataBridge();
                        Event.markTrustedFromCurrentDispatch(clipboard);
                        Event.tiggerEvent(clipboard);
                        if (clipboard.defaultPrevented) {
                            handled = false;
                        } else {
                            Operation.setClipboardText(selection.getSelectedText());
                            RichTextRange copyRange = selection.toRange();
                            Operation.setInternalClipboardHtml(copyRange == null ? null : copyRange.toHtml());
                        }
                    } else if (ctrlDown && key == 88 && !selection.collapsed()) {
                        Event clipboard = new Event(focusedElement, "cut", true);
                        clipboard.cancelable = true;
                        clipboard.clipboardData = new ClipboardDataBridge();
                        Event.markTrustedFromCurrentDispatch(clipboard);
                        Event.tiggerEvent(clipboard);
                        if (clipboard.defaultPrevented) {
                            handled = false;
                        } else {
                            Operation.setClipboardText(selection.getSelectedText());
                            RichTextRange cutRange = selection.toRange();
                            Operation.setInternalClipboardHtml(cutRange == null ? null : cutRange.toHtml());
                            RichTextEditing.deleteSelection((RichText)focusedElement);
                        }
                    } else {
                        handled = Operation.shouldConsumeTextEntryKey(focusedElement, key);
                    }
                    if (handled) {
                        documentCanceled[0] = true;
                        return;
                    }
                }
                if (focusedElement != null && ("BUTTON".equalsIgnoreCase(focusedElement.tagName) || focusedElement instanceof Input && ("submit".equalsIgnoreCase((input = (Input)focusedElement).getType()) || "reset".equalsIgnoreCase(input.getType()) || "button".equalsIgnoreCase(input.getType()))) && (key == 257 || key == 32)) {
                    focusedElement.click();
                    documentCanceled[0] = true;
                    return;
                }
                if (focusedElement instanceof Select && (select = (Select)focusedElement).handleKeyDownDefault(keyEvent)) {
                    documentCanceled[0] = true;
                    return;
                }
                if (focusedElement instanceof AbstractText) {
                    AbstractText textElement = (AbstractText)focusedElement;
                    if (ctrlDown) {
                        if (key == 65 && textElement.canSelectText()) {
                            textElement.selectAll();
                            documentCanceled[0] = true;
                            return;
                        }
                        if (key == 67 && textElement.canSelectText() && !selectedText.isEmpty()) {
                            Event clipboard = new Event(focusedElement, "copy", true);
                            clipboard.cancelable = true;
                            Event.markTrustedFromCurrentDispatch(clipboard);
                            Event.tiggerEvent(clipboard);
                            if (!clipboard.defaultPrevented) {
                                Operation.setClipboardText(selectedText);
                                documentCanceled[0] = true;
                                return;
                            }
                        }
                        if (key == 88 && textElement.canEditText() && textElement.hasSelection()) {
                            Event clipboard = new Event(focusedElement, "cut", true);
                            clipboard.cancelable = true;
                            Event.markTrustedFromCurrentDispatch(clipboard);
                            Event.tiggerEvent(clipboard);
                            if (!clipboard.defaultPrevented) {
                                if (!selectedText.isEmpty()) {
                                    Operation.setClipboardText(selectedText);
                                }
                                textElement.replaceSelection("");
                                documentCanceled[0] = true;
                                return;
                            }
                        }
                        if (key == 86 && textElement.canEditText()) {
                            Event clipboard = new Event(focusedElement, "paste", true);
                            clipboard.cancelable = true;
                            clipboard.clipboardData = new ClipboardDataBridge();
                            Event.markTrustedFromCurrentDispatch(clipboard);
                            Event.tiggerEvent(clipboard);
                            if (!clipboard.defaultPrevented) {
                                textElement.insertText(Operation.getClipboardText());
                                documentCanceled[0] = true;
                                return;
                            }
                        }
                        if (key == 90 && textElement.canEditText() && textElement.undo()) {
                            documentCanceled[0] = true;
                            return;
                        }
                    }
                    if (focusedElement instanceof Input) {
                        Input input2 = (Input)focusedElement;
                        if (key == 32 && input2.handleSpaceKey()) {
                            documentCanceled[0] = true;
                            return;
                        }
                    }
                    if (!textElement.canEditText()) {
                        return;
                    }
                    if (key == 259) {
                        textElement.deleteBackward();
                        documentCanceled[0] = true;
                    } else if (key == 261) {
                        textElement.deleteForward();
                        documentCanceled[0] = true;
                    } else if (key == 263) {
                        textElement.moveCursor(-1, Operation.isShiftDown() && textElement.canSelectText());
                        documentCanceled[0] = true;
                    } else if (key == 262) {
                        textElement.moveCursor(1, Operation.isShiftDown() && textElement.canSelectText());
                        documentCanceled[0] = true;
                    } else if (key == 268) {
                        textElement.moveCursorToHome(Operation.isShiftDown() && textElement.canSelectText());
                        documentCanceled[0] = true;
                    } else if (key == 269) {
                        textElement.moveCursorToEnd(Operation.isShiftDown() && textElement.canSelectText());
                        documentCanceled[0] = true;
                    } else if (key == 265) {
                        textElement.moveCursorByLine(-1, Operation.isShiftDown() && textElement.canSelectText());
                        documentCanceled[0] = true;
                    } else if (key == 264) {
                        textElement.moveCursorByLine(1, Operation.isShiftDown() && textElement.canSelectText());
                        documentCanceled[0] = true;
                    } else if (key == 257) {
                        if (textElement.isMultiline()) {
                            textElement.insertText("\n");
                        } else if (!focusedElement.submitEnclosingForm()) {
                            document.clearFocus();
                        }
                        documentCanceled[0] = true;
                    } else if (key == 256) {
                        document.clearFocus();
                        documentCanceled[0] = true;
                    } else if (Operation.shouldConsumeTextEntryKey(focusedElement, key)) {
                        documentCanceled[0] = true;
                    }
                } else {
                    if (ctrlDown) {
                        if (key == 65 && document == selectionTargetDocument && document.selectAllDocumentText()) {
                            documentCanceled[0] = true;
                            return;
                        }
                        if (key == 67 && !selectedText.isEmpty()) {
                            Operation.setClipboardText(selectedText);
                            documentCanceled[0] = true;
                            return;
                        }
                    }
                    if (key == 256 && document.hasDocumentSelection()) {
                        document.clearDocumentSelection();
                        document.clearFocus();
                        documentCanceled[0] = true;
                    }
                }
            });
            cancel |= documentCanceled[0];
        }
        if (!repeat && Operation.handleFrameworkShortcut(key, modifiers)) {
            return true;
        }
        return cancel;
    }

    public static Document resolveSelectionTargetDocument() {
        List<Document> documents = DocumentLayerOrder.frontToBack(Document.getAll());
        for (Document document : documents) {
            if (document == null || !document.hasDocumentSelection()) continue;
            return document;
        }
        if (cachedMousePosition != null) {
            for (Document document : documents) {
                if (document == null || !document.interceptsMouseEventsAt(cachedMousePosition)) continue;
                return document;
            }
        }
        return Document.getContextDocument();
    }

    public static boolean shouldConsumeTextEntryKey(Element focusedElement, int key) {
        AbstractText textElement;
        RichText richText;
        if (focusedElement instanceof RichText && (richText = (RichText)focusedElement).canEditText()) {
            return Operation.isTextEntryKey(key);
        }
        if (!(focusedElement instanceof AbstractText) || !(textElement = (AbstractText)focusedElement).canEditText()) {
            return false;
        }
        return Operation.isTextEntryKey(key);
    }

    private static boolean isTextEntryKey(int key) {
        return key >= 65 && key <= 90 || key >= 48 && key <= 57 || key >= 320 && key <= 336 || key == 32 || key == 39 || key == 44 || key == 45 || key == 46 || key == 47 || key == 59 || key == 61 || key == 91 || key == 92 || key == 93 || key == 96 || key == 161 || key == 162;
    }

    private static boolean handleFrameworkShortcut(int key, int modifiers) {
        boolean devToolsShortcut;
        boolean bl = devToolsShortcut = key == AuiServices.keys().devToolsKey() || key == 73 && (modifiers & 3) == 3;
        if (devToolsShortcut) {
            DevTools.toggle();
            return true;
        }
        if (key == AuiServices.keys().resourceManagerKey()) {
            ResourceManager.toggle();
            return true;
        }
        if (key == AuiServices.keys().reloadKey()) {
            ClientLoader.reload();
            return true;
        }
        return false;
    }

    public static void onKeyReleased(int key) {
        Operation.onKeyReleased(key, 0, 0, KeyEvent.Source.INPUT_EVENT);
    }

    public static void onKeyReleased(int key, int scanCode, int modifiers, KeyEvent.Source source) {
        for (Document document : Document.getAll()) {
            KeyEvent.triggerEvent(document, "keyup", key, scanCode, modifiers, false, source);
        }
    }

    private static String resolveSelectedText(Document document, Element focusedElement) {
        AbstractText textElement;
        String selected;
        String docSelection;
        if (document != null && (docSelection = document.getDocumentSelectedText()) != null && !docSelection.isEmpty()) {
            return docSelection;
        }
        if (focusedElement instanceof AbstractText && (selected = (textElement = (AbstractText)focusedElement).getSelectedText()) != null && !selected.isEmpty()) {
            return selected;
        }
        return "";
    }

    public static Position getMousePosition() {
        return cachedMousePosition;
    }

    public static Position getMousePositionDirectly() {
        Position live = AuiServices.client().getMousePositionDirectly();
        if (live != null) {
            if (cachedMousePosition == null) {
                cachedMousePosition = live;
            }
            return live;
        }
        return cachedMousePosition;
    }

    private static boolean isCtrlDown() {
        return Operation.isKeyPressed("key.keyboard.left.control") || Operation.isKeyPressed("key.keyboard.right.control");
    }

    private static boolean isShiftDown() {
        return Operation.isKeyPressed("key.keyboard.left.shift") || Operation.isKeyPressed("key.keyboard.right.shift");
    }

    public static String getClipboardText() {
        return Base.getClipboardText();
    }

    public static void setClipboardText(String text) {
        Base.setClipboardText(text);
    }

    public static String getInternalClipboardHtml() {
        return internalClipboardHtml;
    }

    public static void setInternalClipboardHtml(String html) {
        internalClipboardHtml = html == null || html.isEmpty() ? null : html;
    }

    public static boolean isKeyPressed(String key) {
        return AuiServices.client().isKeyPressed(key);
    }

    public static boolean handleKeyInput(int key, int scanCode, int action, int modifiers, boolean repeat, KeyEvent.Source source) {
        if (Operation.isDuplicateKeyEvent(key, scanCode, action, modifiers)) {
            return false;
        }
        if (action == 0) {
            Operation.onKeyReleased(key, scanCode, modifiers, source);
            return false;
        }
        return Operation.onKeyPressed(key, scanCode, modifiers, repeat, source);
    }

    private static boolean isDuplicateKeyEvent(int key, int scanCode, int action, int modifiers) {
        long now = System.nanoTime();
        if (key == lastKeyCode && scanCode == lastScanCode && action == lastAction && modifiers == lastModifiers && now - lastKeyEventTimeNs <= 5000000L) {
            return true;
        }
        lastKeyEventTimeNs = now;
        lastKeyCode = key;
        lastScanCode = scanCode;
        lastAction = action;
        lastModifiers = modifiers;
        return false;
    }

    static {
        internalClipboardHtml = null;
    }
}

