/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.TextSelection;
import com.sighs.apricityui.behavior.richtext.RichTextEditing;
import com.sighs.apricityui.behavior.richtext.RichTextNavigation;
import com.sighs.apricityui.behavior.richtext.RichTextOperation;
import com.sighs.apricityui.behavior.richtext.RichTextRange;
import com.sighs.apricityui.behavior.richtext.RichTextSelection;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.ui.ContextMenu;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RichText
extends Element {
    private static final int MAX_UNDO_STACK = 128;
    private static final Set<String> REGISTERED_CONTENT_STYLE_DOCUMENTS = ConcurrentHashMap.newKeySet();
    private static final String CONTENT_CSS = String.join((CharSequence)"", "[contenteditable] h1{font-size:2em;font-weight:700;margin:0.67em 0;}", "[contenteditable] h2{font-size:1.5em;font-weight:700;margin:0.83em 0;}", "[contenteditable] h3{font-size:1.17em;font-weight:700;margin:1em 0;}", "[contenteditable] h4{font-size:1em;font-weight:700;margin:1.33em 0;}", "[contenteditable] h5{font-size:0.83em;font-weight:700;margin:1.67em 0;}", "[contenteditable] h6{font-size:0.67em;font-weight:700;margin:2.33em 0;}", "[contenteditable] p{margin:1em 0;}", "[contenteditable] blockquote{margin:1em 40px;}", "[contenteditable] ul,[contenteditable] ol{margin:1em 0;padding-left:40px;}", "[contenteditable] hr{border:none;border-top:1px solid #000000;margin:0.5em 0;}", "[contenteditable] a{color:#0000EE;text-decoration:underline;}", "[contenteditable] code,[contenteditable] pre{font-family:monospace;}", "[contenteditable] i,[contenteditable] em{font-style:oblique;}", "[contenteditable] img{max-width:100%;}");
    private long lastBlinkTime;
    private String focusValueSnapshot = "";
    private final Deque<RichTextOperation> undoStack = new ArrayDeque<RichTextOperation>();
    private final Deque<RichTextOperation> redoStack = new ArrayDeque<RichTextOperation>();
    private Element draggingObject = null;
    private int dragTargetOffset = -1;

    public RichText(Document document, String tagName) {
        super(document, tagName);
        RichText.ensureContentStyles(document);
        this.lastBlinkTime = System.currentTimeMillis();
        this.addInternalEventListener("mousedown", event -> {
            Element element;
            MouseEvent mouse;
            block9: {
                block8: {
                    if (!(event instanceof MouseEvent)) break block8;
                    mouse = (MouseEvent)event;
                    if (document != null) break block9;
                }
                return;
            }
            if (mouse.button != 0 && mouse.button != -1) {
                return;
            }
            document.clearAllTextSelectionsExcept(this);
            RichTextSelection selection = document.getRichTextSelection();
            Object object = event.target;
            if (object instanceof Element) {
                Element targetElement = (Element)object;
                element = targetElement;
            } else {
                element = this;
            }
            RichText hit = element;
            Element objectHit = RichText.hitAtomicObject(hit, mouse.clientX, mouse.clientY);
            if (objectHit != null) {
                Element objectBlock = RichText.blockOf(objectHit);
                int objectStart = SelectionUnits.baseOffsetOfDescendant(objectBlock, objectHit);
                selection.setRange(objectBlock, objectStart, objectBlock, objectStart + 1);
                this.draggingObject = objectHit;
                this.dragTargetOffset = -1;
            } else {
                selection.setFromPoint(hit, mouse, mouse.shiftKey);
                this.draggingObject = null;
                this.dragTargetOffset = -1;
            }
            document.setFocusedElement(this);
            event.preventDefault();
        });
        this.addInternalEventListener("mousemove", event -> {
            Element element;
            MouseEvent mouse;
            block12: {
                block11: {
                    if (!(event instanceof MouseEvent)) break block11;
                    mouse = (MouseEvent)event;
                    if (document != null) break block12;
                }
                return;
            }
            RichTextSelection selection = document.getRichTextSelection();
            if (this.draggingObject != null) {
                Element element2;
                Object object = event.target;
                if (object instanceof Element) {
                    Element targetElement = (Element)object;
                    element2 = targetElement;
                } else {
                    element2 = this;
                }
                RichText hit = element2;
                SelectionUnits.UnitOffset target = TextSelection.resolveUnitOffset(hit, mouse.clientX, mouse.clientY);
                if (target != null && target.unit() != null) {
                    this.dragTargetOffset = target.offset();
                    selection.setCollapsed(target.unit(), target.offset());
                }
                return;
            }
            if (!selection.isSelecting() || document.getPressedElement() == null) {
                return;
            }
            Object object = event.target;
            if (object instanceof Element) {
                Element targetElement = (Element)object;
                element = targetElement;
            } else {
                element = this;
            }
            RichText hit = element;
            SelectionUnits.UnitOffset target = TextSelection.resolveUnitOffset(hit, mouse.clientX, mouse.clientY);
            if (target != null) {
                selection.extendTo(target.unit(), target.offset());
            }
        });
        this.addInternalEventListener("mouseup", event -> {
            if (document == null) {
                return;
            }
            RichTextSelection selection = document.getRichTextSelection();
            if (this.draggingObject != null) {
                Element object = this.draggingObject;
                int target = this.dragTargetOffset;
                this.draggingObject = null;
                this.dragTargetOffset = -1;
                if (target >= 0) {
                    RichTextEditing.moveObject(this, object, target);
                }
                selection.setSelecting(false);
                return;
            }
            selection.setSelecting(false);
        });
        this.addInternalEventListener("blur", event -> {
            if (document == null) {
                return;
            }
            document.getRichTextSelection().setSelecting(false);
            this.draggingObject = null;
            this.dragTargetOffset = -1;
            if (!Objects.equals(this.focusValueSnapshot, this.getInnerHTML())) {
                this.dispatchChangeEvent();
                this.focusValueSnapshot = this.getInnerHTML();
            }
        });
        this.addInternalEventListener("contextmenu", event -> {
            Element element;
            MouseEvent mouse;
            block8: {
                block7: {
                    if (!(event instanceof MouseEvent)) break block7;
                    mouse = (MouseEvent)event;
                    if (document != null) break block8;
                }
                return;
            }
            Object object = event.target;
            if (object instanceof Element) {
                Element targetElement = (Element)object;
                element = targetElement;
            } else {
                element = this;
            }
            RichText hit = element;
            SelectionUnits.UnitOffset target = TextSelection.resolveUnitOffset(hit, mouse.clientX, mouse.clientY);
            if (target != null) {
                boolean inside;
                RichTextSelection selection = document.getRichTextSelection();
                int[] range = selection.localRangeForUnit(target.unit());
                boolean bl = inside = range != null && target.offset() >= range[0] && target.offset() <= range[1];
                if (!inside) {
                    selection.setCollapsed(target.unit(), target.offset());
                }
            }
            this.showContextMenu(mouse);
            event.preventDefault();
        });
        this.addInternalEventListener("focus", event -> {
            this.focusValueSnapshot = this.getInnerHTML();
        });
    }

    private void showContextMenu(MouseEvent mouse) {
        if (this.document == null) {
            return;
        }
        RichTextSelection selection = this.document.getRichTextSelection();
        boolean hasSelection = selection != null && selection.isActive();
        boolean hasClipboard = Operation.getInternalClipboardHtml() != null || Operation.getClipboardText() != null && !Operation.getClipboardText().isEmpty();
        ArrayList<ContextMenu.Item> items = new ArrayList<ContextMenu.Item>();
        items.add(ContextMenu.Item.header("TEXT"));
        ContextMenu.Item cut = ContextMenu.Item.action("Cut", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M4 2h6v1H4V2zM2 4h10v1H2V4zm1 2h8l-1 7H4L3 6zm3 1v5h1V7H6zm2 0v5h1V7H8z\"/></svg>", "Ctrl+X", this::cutSelection);
        ContextMenu.Item copy = ContextMenu.Item.action("Copy", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><rect x=\"4\" y=\"4\" width=\"8\" height=\"8\" fill=\"none\" stroke=\"currentColor\" stroke-width=\"1.2\"/><path d=\"M2 10V3h6v1H3v6H2z\"/></svg>", "Ctrl+C", this::copySelection);
        ContextMenu.Item paste = ContextMenu.Item.action("Paste", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M12 7a5 5 0 1 1-1.5-3.5L12 2v3.5H8.5l1.5-1.5A3.5 3.5 0 1 0 10.5 7H12z\"/></svg>", "Ctrl+V", this::pasteClipboard);
        if (!hasSelection) {
            cut = cut.disabled();
            copy = copy.disabled();
        }
        if (!hasClipboard) {
            paste = paste.disabled();
        }
        items.add(cut);
        items.add(copy);
        items.add(paste);
        items.add(ContextMenu.Item.separator());
        items.add(ContextMenu.Item.action("Select All", "<svg viewBox=\"0 0 14 14\" fill=\"currentColor\"><path d=\"M10 1l3 3-8 8H2v-3l8-8zm-1.5 3L4 8.5V10h1.5L10 5.5 8.5 4z\"/></svg>", "Ctrl+A", this::selectAllContent));
        ContextMenu.show(this.document, new Position(mouse.clientX, mouse.clientY), items);
    }

    private void cutSelection() {
        this.copySelection();
        RichTextEditing.deleteSelection(this);
    }

    private void copySelection() {
        if (this.document == null) {
            return;
        }
        RichTextSelection selection = this.document.getRichTextSelection();
        if (selection == null || !selection.isActive()) {
            return;
        }
        RichTextRange range = selection.toRange();
        Operation.setClipboardText(selection.getSelectedText());
        Operation.setInternalClipboardHtml(range == null ? null : range.toHtml());
    }

    private void pasteClipboard() {
        if (this.document == null) {
            return;
        }
        String html = Operation.getInternalClipboardHtml();
        if (html != null) {
            RichTextEditing.pasteHtml(this, html);
        } else {
            RichTextEditing.pasteText(this, Operation.getClipboardText());
        }
    }

    private void selectAllContent() {
        if (this.document == null) {
            return;
        }
        this.document.getRichTextSelection().selectAllInRoot();
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    public boolean canEditText() {
        String ce = this.getAttribute("contenteditable");
        if (ce != null && "false".equalsIgnoreCase(ce.trim())) {
            return false;
        }
        return !this.hasAttribute("readonly");
    }

    public void pushUndo(RichTextOperation operation) {
        if (operation == null) {
            return;
        }
        RichTextOperation top = this.undoStack.peek();
        if (top != null && operation.mergeableWith(top)) {
            this.undoStack.push(operation.merge(top));
        } else {
            this.undoStack.push(operation);
        }
        while (this.undoStack.size() > 128) {
            this.undoStack.removeLast();
        }
        this.redoStack.clear();
    }

    public boolean undoInternal() {
        if (this.undoStack.isEmpty()) {
            return false;
        }
        RichTextOperation operation = this.undoStack.pop();
        this.redoStack.push(operation);
        RichTextEditing.applyOperation(this, operation.inverse());
        RichTextRange.normalize(this);
        this.markHistoryDirty();
        return true;
    }

    public boolean redoInternal() {
        if (this.redoStack.isEmpty()) {
            return false;
        }
        RichTextOperation operation = this.redoStack.pop();
        this.undoStack.push(operation);
        RichTextEditing.applyOperation(this, operation);
        RichTextRange.normalize(this);
        this.markHistoryDirty();
        return true;
    }

    private void markHistoryDirty() {
        this.getRenderer().text.clear();
        if (this.document != null) {
            this.document.markDirty(this, 5);
        }
    }

    private static void ensureContentStyles(Document document) {
        if (document == null) {
            return;
        }
        if (!REGISTERED_CONTENT_STYLE_DOCUMENTS.add(document.getUuid().toString())) {
            return;
        }
        document.registerUaStylesheet(CONTENT_CSS, "<richtext-content>");
    }

    private void dispatchChangeEvent() {
        if (this.document == null) {
            return;
        }
        Event event = new Event(this, "change", true);
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        super.drawPhase(poseStack, phase);
        if (phase != Base.RenderPhase.BODY || this.document == null) {
            return;
        }
        RichTextSelection selection = this.document.getRichTextSelection();
        if (selection == null) {
            return;
        }
        if (selection.isActive() && selection.getAnchorUnit() != null && RichText.rootOf(selection.getAnchorUnit()) == this) {
            this.drawObjectSelectionFrames(poseStack, selection);
        }
        if (!Element.isElementFocusing(this)) {
            return;
        }
        if (!selection.hasAnchor() || !selection.collapsed()) {
            return;
        }
        Element unit = selection.getAnchorUnit();
        if (unit == null || RichText.rootOf(unit) != this) {
            return;
        }
        RichTextNavigation.Caret caret = RichTextNavigation.caretPosition(unit, selection.getAnchorOffset());
        Text text = Text.of(unit == this ? this : unit);
        Graph.drawCursor(poseStack.m_85850_().m_252922_(), (float)caret.x(), (float)caret.y(), (float)Math.max(caret.lineHeight(), 16.0), Text.getFontColor(unit), this.lastBlinkTime);
    }

    private static Element hitAtomicObject(Element current, double x, double y) {
        if (current == null) {
            return null;
        }
        for (Node child : current.getRenderChildNodes()) {
            Element hit;
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isAtomicObject(childElement)) {
                Element.DOMRect rect = childElement.getBoundingClientRect();
                if (rect == null || !(rect.width > 0.0) || !(rect.height > 0.0) || !(x >= rect.x) || !(x <= rect.x + rect.width) || !(y >= rect.y) || !(y <= rect.y + rect.height)) continue;
                return childElement;
            }
            if (childElement instanceof AbstractText || (hit = RichText.hitAtomicObject(childElement, x, y)) == null) continue;
            return hit;
        }
        return null;
    }

    private static List<Element> collectAtomicObjects(Element current) {
        ArrayList<Element> objects = new ArrayList<Element>();
        RichText.collectAtomicObjectsRecursive(current, objects);
        return objects;
    }

    private static void collectAtomicObjectsRecursive(Element current, List<Element> out) {
        for (Node child : current.getRenderChildNodes()) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            if (SelectionUnits.isAtomicObject(childElement)) {
                out.add(childElement);
                continue;
            }
            if (childElement instanceof AbstractText) continue;
            RichText.collectAtomicObjectsRecursive(childElement, out);
        }
    }

    private void drawObjectSelectionFrames(PoseStack poseStack, RichTextSelection selection) {
        int[] range = selection.localRangeForUnit(this);
        if (range == null) {
            return;
        }
        for (Element object : RichText.collectAtomicObjects(this)) {
            Element.DOMRect rect;
            Element block = RichText.blockOf(object);
            int objectStart = SelectionUnits.baseOffsetOfDescendant(block, object);
            if (objectStart < range[0] || objectStart >= range[1] || (rect = object.getBoundingClientRect()) == null || rect.width <= 0.0 || rect.height <= 0.0) continue;
            this.drawSelectionFrame(poseStack, rect);
        }
    }

    private static Element blockOf(Element object) {
        Element root = RichText.rootOf(object);
        Element e = object.parentElement;
        while (e != null && e != root) {
            if (SelectionUnits.isSelectionUnit(e) && e != root) {
                return e;
            }
            e = e.parentElement;
        }
        return root;
    }

    private static Element rootOf(Element element) {
        Element e = element;
        while (e != null) {
            if (e instanceof RichText) {
                return e;
            }
            e = e.parentElement;
        }
        return null;
    }

    private void drawSelectionFrame(PoseStack poseStack, Element.DOMRect rect) {
        float[][] corners;
        int color = -14774017;
        float x0 = (float)rect.x - 1.0f;
        float y0 = (float)rect.y - 1.0f;
        float x1 = (float)(rect.x + rect.width) + 1.0f;
        float y1 = (float)(rect.y + rect.height) + 1.0f;
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y0, x1, y0 + 1.0f, color);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y1 - 1.0f, x1, y1, color);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y0, x0 + 1.0f, y1, color);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x1 - 1.0f, y0, x1, y1, color);
        float handle = 3.0f;
        for (float[] corner : corners = new float[][]{{x0, y0}, {x1 - handle, y0}, {x0, y1 - handle}, {x1 - handle, y1 - handle}}) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), corner[0], corner[1], corner[0] + handle, corner[1] + handle, color);
        }
    }
}

