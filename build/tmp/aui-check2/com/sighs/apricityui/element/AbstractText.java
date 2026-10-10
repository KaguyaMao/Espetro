/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.util.TextMetrics;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Objects;

public abstract class AbstractText
extends Element {
    protected int maxLength = 256;
    protected int cursor = 0;
    protected long lastBlinkTime = 0L;
    protected String placeholder = "";
    protected String cachedValue = "";
    protected int selectionStart = 0;
    protected int selectionEnd = 0;
    protected String selectionDirection = "none";
    protected boolean selecting = false;
    protected int selectionAnchor = 0;
    protected final Deque<TextState> undoStack = new ArrayDeque<TextState>();
    protected boolean restoringUndo = false;
    protected boolean composing = false;
    protected static final int MAX_UNDO_STACK = 128;
    private String focusValueSnapshot = "";

    protected AbstractText(Document document, String tagName) {
        super(document, tagName);
        this.ensureValue();
        this.focusValueSnapshot = this.value;
        this.clearSelection();
        this.addSelectionEventListeners();
        this.addInternalEventListener("focus", event -> {
            this.focusValueSnapshot = this.getValue();
        });
        this.addInternalEventListener("blur", event -> {
            String currentValue = this.getValue();
            if (!Objects.equals(this.focusValueSnapshot, currentValue)) {
                this.dispatchChangeEvent();
                this.focusValueSnapshot = currentValue;
            }
        });
    }

    private void addSelectionEventListeners() {
        this.addEventListener("mousedown", event -> {
            if (!(event instanceof MouseEvent)) {
                return;
            }
            MouseEvent mouseEvent = (MouseEvent)event;
            if (!this.canEditText() && !this.canSelectText()) {
                return;
            }
            if (mouseEvent.button == 2) {
                String primary;
                if (this.canEditText() && this.document != null && (primary = this.document.getDocumentSelectedText()) != null && !primary.isEmpty()) {
                    this.replaceSelection(primary);
                }
                return;
            }
            if (this.document != null) {
                this.document.clearAllTextSelectionsExcept(this);
            }
            if (this.canSelectText() && Interaction.isUserSelectAll(this)) {
                this.selectAll();
                this.selecting = false;
                this.clampScroll();
                return;
            }
            this.locateCursor(mouseEvent.offsetX, mouseEvent.offsetY);
            if (this.canSelectText() && mouseEvent.shiftKey) {
                if (!this.hasSelection()) {
                    this.selectionAnchor = this.selectionStart;
                }
                this.selectionStart = this.selectionAnchor;
                this.selectionEnd = this.cursor;
                this.updateSelectionDirection();
            } else {
                this.selectionAnchor = this.cursor;
                if (this.canSelectText()) {
                    this.clearSelection();
                }
            }
            this.selecting = this.canSelectText();
            this.clampScroll();
        });
        this.addEventListener("mousemove", event -> {
            MouseEvent mouseEvent;
            block5: {
                block4: {
                    if (!(event instanceof MouseEvent)) break block4;
                    mouseEvent = (MouseEvent)event;
                    if (this.canSelectText()) break block5;
                }
                return;
            }
            if (!this.selecting || this.document.getPressedElement() != this) {
                return;
            }
            this.locateCursor(mouseEvent.offsetX, mouseEvent.offsetY);
            this.selectionStart = this.selectionAnchor;
            this.selectionEnd = this.cursor;
            this.updateSelectionDirection();
            this.clampScroll();
        });
        this.addEventListener("mouseup", event -> {
            this.selecting = false;
        });
    }

    @Override
    protected void onInitFromDom(Element origin) {
        this.placeholder = this.getAttribute("placeholder");
        String maxLengthAttr = this.getAttribute("maxlength");
        int parsed = Size.parse(maxLengthAttr);
        if (parsed > 0) {
            this.maxLength = parsed;
        }
        this.ensureValue();
        this.selectionAnchor = this.cursor = Math.min(this.cursor, this.value.length());
        this.clearSelection();
    }

    @Override
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
        this.syncTextAttribute(name, value);
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        this.syncTextAttribute(name, null);
    }

    private void syncTextAttribute(String name, String attrValue) {
        if (name.equals("placeholder")) {
            this.placeholder = attrValue == null ? "" : attrValue;
            return;
        }
        if (name.equals("maxlength")) {
            int parsed = Size.parse(attrValue == null ? "" : attrValue);
            this.maxLength = parsed > 0 ? parsed : 256;
            return;
        }
        if (name.equals("value")) {
            this.ensureValue();
            this.selectionAnchor = this.cursor = Math.min(this.cursor, this.value.length());
            this.clearSelection();
            this.undoStack.clear();
            this.getRenderer().text.clear();
        }
    }

    protected void ensureValue() {
        if (this.value == null) {
            this.value = "";
        }
    }

    public boolean canEditText() {
        return true;
    }

    public boolean canSelectText() {
        return Interaction.isUserSelectable(this);
    }

    public boolean isMultiline() {
        return this.supportsMultilineInput();
    }

    protected boolean supportsMultilineInput() {
        return false;
    }

    public int getCursor() {
        return this.cursor;
    }

    public int getSelectionStart() {
        return Math.min(this.selectionStart, this.value == null ? 0 : this.value.length());
    }

    public int getSelectionEnd() {
        return Math.min(this.selectionEnd, this.value == null ? 0 : this.value.length());
    }

    public String getSelectionDirection() {
        return this.selectionDirection;
    }

    public void setSelectionRange(int start, int end) {
        this.setSelectionRange(start, end, "none");
    }

    public void setSelectionRange(int start, int end, String direction) {
        this.ensureValue();
        int length = this.value.length();
        int safeStart = this.clamp(start, 0, length);
        int safeEnd = this.clamp(end, 0, length);
        this.selectionStart = safeStart;
        this.selectionEnd = safeEnd;
        this.selectionDirection = AbstractText.normalizeSelectionDirection(direction);
        this.selectionAnchor = "backward".equals(this.selectionDirection) ? safeEnd : safeStart;
        this.cursor = safeEnd;
        this.clampScroll();
    }

    public void select() {
        this.ensureValue();
        this.setSelectionRange(0, this.value.length(), "forward");
    }

    public void setRangeText(String replacement) {
        this.setRangeText(replacement, this.getSelectionStart(), this.getSelectionEnd(), "preserve");
    }

    public void setRangeText(String replacement, int start, int end, String selectionMode) {
        this.ensureValue();
        if (!this.canEditText()) {
            return;
        }
        int safeStart = this.clamp(Math.min(start, end), 0, this.value.length());
        int safeEnd = this.clamp(Math.max(start, end), 0, this.value.length());
        String normalized = this.normalizeInsertedText(replacement == null ? "" : replacement);
        if (!this.dispatchBeforeInputEvent("insertReplacementText", normalized)) {
            return;
        }
        this.pushUndoState();
        this.value = this.value.substring(0, safeStart) + normalized + this.value.substring(safeEnd);
        int nextStart = safeStart;
        int nextEnd = safeStart + normalized.length();
        if ("select".equalsIgnoreCase(selectionMode)) {
            this.selectionStart = nextStart;
            this.selectionEnd = nextEnd;
            this.selectionDirection = "forward";
            this.cursor = nextEnd;
        } else if ("start".equalsIgnoreCase(selectionMode)) {
            this.clearSelection();
            this.cursor = nextStart;
            this.clearSelection();
        } else if ("end".equalsIgnoreCase(selectionMode)) {
            this.clearSelection();
            this.cursor = nextEnd;
            this.clearSelection();
        } else {
            int cursorValue;
            int delta = normalized.length() - (safeEnd - safeStart);
            this.cursor = cursorValue = this.clamp(this.cursor + delta, 0, this.value.length());
            this.clearSelection();
        }
        this.clampScroll();
        this.getRenderer().text.clear();
        this.dispatchInputEvent("insertReplacementText", normalized);
    }

    public void beginComposition(String data) {
        Event.CompositionEvent composition = new Event.CompositionEvent((Object)this, "compositionstart", true, data);
        composition.isComposing = true;
        this.composing = true;
        Event.tiggerEvent(composition);
    }

    public void updateComposition(String data) {
        Event.CompositionEvent composition = new Event.CompositionEvent((Object)this, "compositionupdate", true, data);
        composition.isComposing = true;
        Event.tiggerEvent(composition);
    }

    public void endComposition(String data) {
        Event.CompositionEvent composition = new Event.CompositionEvent((Object)this, "compositionend", true, data);
        composition.isComposing = false;
        Event.tiggerEvent(composition);
        this.composing = false;
    }

    public boolean hasSelection() {
        return this.selectionStart != this.selectionEnd;
    }

    protected int selMin() {
        return Math.min(this.selectionStart, this.selectionEnd);
    }

    protected int selMax() {
        return Math.max(this.selectionStart, this.selectionEnd);
    }

    public void clearSelection() {
        this.selectionStart = this.cursor;
        this.selectionEnd = this.cursor;
        this.selectionDirection = "none";
        this.addDirtyFlags(1);
    }

    public void selectAll() {
        this.ensureValue();
        this.cursor = this.value.length();
        this.selectionAnchor = 0;
        this.selectionStart = 0;
        this.selectionEnd = this.cursor;
        this.selectionDirection = "forward";
        this.clampScroll();
    }

    public String getSelectedText() {
        this.ensureValue();
        if (!this.hasSelection()) {
            return "";
        }
        return this.value.substring(this.selMin(), this.selMax());
    }

    public void replaceSelection(String str) {
        String normalized;
        if (!this.canEditText()) {
            return;
        }
        String string = normalized = str == null ? "" : this.normalizeInsertedText(str);
        if (normalized.isEmpty()) {
            if (!this.hasSelection()) {
                return;
            }
            if (!this.dispatchBeforeInputEvent("deleteContentBackward", null)) {
                return;
            }
            this.pushUndoState();
            this.sliceText(this.selMin(), this.selMax(), "deleteContentBackward", false);
            return;
        }
        this.insertText(normalized);
    }

    public void insertText(String str) {
        int allowed;
        if (!this.canEditText()) {
            return;
        }
        if (str == null || str.isEmpty()) {
            return;
        }
        this.ensureValue();
        str = this.normalizeInsertedText(str);
        if (str.isEmpty()) {
            return;
        }
        if (!this.dispatchBeforeInputEvent("insertText", str)) {
            return;
        }
        this.pushUndoState();
        if (this.hasSelection()) {
            int min = this.selMin();
            int max = this.selMax();
            this.value = this.value.substring(0, min) + this.value.substring(max);
            this.cursor = min;
        }
        if ((allowed = this.maxLength - this.value.length()) <= 0) {
            this.selectionAnchor = this.cursor;
            this.clearSelection();
            this.clampScroll();
            return;
        }
        if (str.length() > allowed) {
            str = str.substring(0, allowed);
        }
        String before = this.value.substring(0, this.cursor);
        String after = this.value.substring(this.cursor);
        this.value = before + str + after;
        this.cursor += str.length();
        this.selectionAnchor = this.cursor;
        this.clearSelection();
        this.clampScroll();
        this.getRenderer().text.clear();
        this.dispatchInputEvent("insertText", str);
    }

    private String normalizeInsertedText(String str) {
        if (this.supportsMultilineInput()) {
            return str.replace("\r\n", "\n").replace('\r', '\n');
        }
        return str.replace("\r", "").replace("\n", "");
    }

    public void moveCursor(int offset) {
        this.moveCursor(offset, false);
    }

    public void moveCursor(int offset, boolean keepSelection) {
        this.ensureValue();
        if (keepSelection && !this.hasSelection()) {
            this.selectionAnchor = this.cursor;
        }
        this.cursor += offset;
        this.cursor = this.clamp(this.cursor, 0, this.value.length());
        if (keepSelection) {
            this.selectionStart = this.selectionAnchor;
            this.selectionEnd = this.cursor;
            this.updateSelectionDirection();
        } else {
            this.selectionAnchor = this.cursor;
            this.clearSelection();
        }
        this.clampScroll();
    }

    public void moveCursorToHome(boolean keepSelection) {
        this.ensureValue();
        this.applyNavigationMove(this.lineStartIndex(), keepSelection);
    }

    public void moveCursorToEnd(boolean keepSelection) {
        this.ensureValue();
        this.applyNavigationMove(this.lineEndIndex(), keepSelection);
    }

    public void moveCursorByLine(int delta, boolean keepSelection) {
        if (!this.supportsMultilineInput() || delta == 0) {
            return;
        }
        this.ensureValue();
        Text.WrappedText wrapped = this.wrapForNavigation();
        List<String> lines = wrapped.lines();
        int[] starts = wrapped.starts();
        if (lines.isEmpty()) {
            return;
        }
        int line = this.resolveNavigationLine(lines, starts, this.cursor);
        int targetLine = this.clamp(line + delta, 0, lines.size() - 1);
        if (targetLine == line) {
            return;
        }
        int columnStart = starts[line];
        int column = this.clamp(this.cursor - columnStart, 0, lines.get(line).length());
        double currentX = column == 0 ? 0.0 : Size.measureText(this, lines.get(line).substring(0, column));
        String targetText = lines.get(targetLine);
        int best = 0;
        double bestDistance = Double.MAX_VALUE;
        double acc = 0.0;
        for (int i = 0; i <= targetText.length(); ++i) {
            double distance = Math.abs(acc - currentX);
            if (distance < bestDistance) {
                bestDistance = distance;
                best = i;
            }
            if (i >= targetText.length()) continue;
            acc += Size.measureText(this, String.valueOf(targetText.charAt(i)));
        }
        this.applyNavigationMove(starts[targetLine] + best, keepSelection);
    }

    private Text.WrappedText wrapForNavigation() {
        Text text = Text.of(this);
        text.content = this.getRenderText();
        return Text.wrap(text, Box.of(this).innerSize().width());
    }

    private int resolveNavigationLine(List<String> lines, int[] starts, int index) {
        int line;
        for (line = 0; line < lines.size() - 1 && index > starts[line] + lines.get(line).length(); ++line) {
        }
        return line;
    }

    private int lineStartIndex() {
        if (!this.supportsMultilineInput()) {
            return 0;
        }
        Text.WrappedText wrapped = this.wrapForNavigation();
        List<String> lines = wrapped.lines();
        int[] starts = wrapped.starts();
        if (lines.isEmpty()) {
            return 0;
        }
        return starts[this.resolveNavigationLine(lines, starts, this.cursor)];
    }

    private int lineEndIndex() {
        this.ensureValue();
        if (!this.supportsMultilineInput()) {
            return this.value.length();
        }
        Text.WrappedText wrapped = this.wrapForNavigation();
        List<String> lines = wrapped.lines();
        int[] starts = wrapped.starts();
        if (lines.isEmpty()) {
            return this.value.length();
        }
        int line = this.resolveNavigationLine(lines, starts, this.cursor);
        return starts[line] + lines.get(line).length();
    }

    private void applyNavigationMove(int target, boolean keepSelection) {
        this.ensureValue();
        target = this.clamp(target, 0, this.value.length());
        if (keepSelection && !this.hasSelection()) {
            this.selectionAnchor = this.cursor;
        }
        this.cursor = target;
        if (keepSelection) {
            this.selectionStart = this.selectionAnchor;
            this.selectionEnd = this.cursor;
            this.updateSelectionDirection();
        } else {
            this.selectionAnchor = this.cursor;
            this.clearSelection();
        }
        this.clampScroll();
    }

    public boolean deleteBackward() {
        this.ensureValue();
        if (this.hasSelection()) {
            if (!this.dispatchBeforeInputEvent("deleteContentBackward", null)) {
                return false;
            }
            this.pushUndoState();
            this.sliceText(this.selMin(), this.selMax(), "deleteContentBackward", false);
            return true;
        }
        if (this.cursor <= 0) {
            return false;
        }
        if (!this.dispatchBeforeInputEvent("deleteContentBackward", null)) {
            return false;
        }
        this.pushUndoState();
        this.sliceText(this.cursor - 1, this.cursor, "deleteContentBackward", false);
        return true;
    }

    public boolean deleteForward() {
        this.ensureValue();
        if (this.hasSelection()) {
            if (!this.dispatchBeforeInputEvent("deleteContentForward", null)) {
                return false;
            }
            this.pushUndoState();
            this.sliceText(this.selMin(), this.selMax(), "deleteContentForward", false);
            return true;
        }
        if (this.cursor >= this.value.length()) {
            return false;
        }
        if (!this.dispatchBeforeInputEvent("deleteContentForward", null)) {
            return false;
        }
        this.pushUndoState();
        this.sliceText(this.cursor, this.cursor + 1, "deleteContentForward", false);
        return true;
    }

    public void sliceText(int start, int end) {
        this.sliceText(start, end, "deleteContentBackward", true);
    }

    private void sliceText(int start, int end, String inputType, boolean dispatchInputEvent) {
        this.ensureValue();
        if (start < 0) {
            start = 0;
        }
        if (end > this.value.length()) {
            end = this.value.length();
        }
        if (start >= end) {
            return;
        }
        String before = this.value.substring(0, start);
        String after = this.value.substring(end);
        this.value = before + after;
        this.selectionAnchor = this.cursor = start;
        this.clearSelection();
        this.clampScroll();
        this.getRenderer().text.clear();
        if (dispatchInputEvent) {
            this.dispatchInputEvent(inputType, null);
        }
    }

    public boolean undo() {
        if (!this.canEditText()) {
            return false;
        }
        if (this.undoStack.isEmpty()) {
            return false;
        }
        if (!this.dispatchBeforeInputEvent("historyUndo", null)) {
            return false;
        }
        TextState state = this.undoStack.pop();
        this.restoringUndo = true;
        try {
            this.value = state.value;
            this.cursor = this.clamp(state.cursor, 0, this.value.length());
            this.selectionStart = this.clamp(state.selectionStart, 0, this.value.length());
            this.selectionEnd = this.clamp(state.selectionEnd, 0, this.value.length());
            this.selectionAnchor = this.clamp(state.selectionAnchor, 0, this.value.length());
            this.selectionDirection = AbstractText.normalizeSelectionDirection(state.selectionDirection);
            this.clampScroll();
            this.getRenderer().text.clear();
            this.dispatchInputEvent("historyUndo", null);
        }
        finally {
            this.restoringUndo = false;
        }
        return true;
    }

    protected void dispatchInputEvent(String inputType, String data) {
        Event.InputEvent event = new Event.InputEvent((Object)this, "input", true, inputType, data);
        event.isComposing = this.composing;
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
    }

    protected boolean dispatchBeforeInputEvent(String inputType, String data) {
        Event.InputEvent event = new Event.InputEvent((Object)this, "beforeinput", true, inputType, data);
        event.cancelable = true;
        event.isComposing = this.composing;
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
        return !event.defaultPrevented;
    }

    protected void dispatchChangeEvent() {
        Event event = new Event(this, "change", true);
        Event.markTrustedFromCurrentDispatch(event);
        Event.tiggerEvent(event);
    }

    protected void pushUndoState() {
        if (this.restoringUndo) {
            return;
        }
        this.ensureValue();
        TextState current = new TextState(this.value, this.cursor, this.selectionStart, this.selectionEnd, this.selectionAnchor, this.selectionDirection);
        TextState top = this.undoStack.peek();
        if (top != null && top.equals(current)) {
            return;
        }
        this.undoStack.push(current);
        while (this.undoStack.size() > 128) {
            this.undoStack.removeLast();
        }
    }

    protected void locateCursor(double mouseOffsetX, double mouseOffsetY) {
        this.locateCursor(mouseOffsetX);
    }

    protected void locateCursor(double mouseOffsetX) {
        String charStr;
        double charWidth;
        Box box = Box.of(this);
        double contentStartX = box.getBorderLeft() + box.getPaddingLeft();
        double relativeX = mouseOffsetX - contentStartX + this.scrollLeft - this.resolveTextAlignX(this.getRenderText());
        String text = this.getRenderText();
        if (text.isEmpty()) {
            this.cursor = 0;
            return;
        }
        double currentWidth = 0.0;
        int newCursor = 0;
        for (int i = 0; i < text.length() && !(relativeX <= currentWidth + (charWidth = Size.measureText(this, charStr = String.valueOf(text.charAt(i)))) / 2.0); ++i) {
            currentWidth += charWidth;
            ++newCursor;
        }
        this.cursor = newCursor;
        this.clampScroll();
    }

    protected double resolveTextAlignX(String content) {
        double contentWidth;
        if (content == null || content.isEmpty()) {
            return 0.0;
        }
        double lineWidth = Size.measureText(this, content);
        if (lineWidth > (contentWidth = Math.max(0.0, Box.of(this).innerSize().width()))) {
            return 0.0;
        }
        return TextMetrics.computeAlignedX(Text.of(this), contentWidth, lineWidth, true);
    }

    private void updateSelectionDirection() {
        this.selectionDirection = this.selectionStart == this.selectionEnd ? "none" : (this.selectionEnd < this.selectionStart ? "backward" : "forward");
    }

    private static String normalizeSelectionDirection(String direction) {
        if ("backward".equalsIgnoreCase(direction)) {
            return "backward";
        }
        if ("forward".equalsIgnoreCase(direction)) {
            return "forward";
        }
        return "none";
    }

    protected void clampScroll() {
        String text = this.getRenderText();
        if (this.cursor > text.length()) {
            this.cursor = text.length();
        }
        if (this.cursor < 0) {
            this.cursor = 0;
        }
        String textBeforeCursor = text.substring(0, this.cursor);
        double cursorX = Size.measureText(this, textBeforeCursor);
        this.scrollWidth = Size.measureText(this, text);
        double visibleWidth = Math.max(0.0, Box.of(this).innerSize().width());
        double maxScrollLeft = Math.max(0.0, this.scrollWidth - visibleWidth);
        double desiredScrollLeft = this.scrollLeft;
        if (cursorX < desiredScrollLeft) {
            desiredScrollLeft = cursorX;
        } else if (cursorX > desiredScrollLeft + visibleWidth) {
            desiredScrollLeft = cursorX - visibleWidth + 2.0;
        }
        this.setTextScrollLeftImmediate(Math.max(0.0, Math.min(maxScrollLeft, desiredScrollLeft)));
        this.addDirtyFlags(1);
    }

    protected final void setTextScrollLeftImmediate(double value) {
        double clamped;
        double visibleWidth = Math.max(0.0, Box.of(this).innerSize().width());
        double maxScrollLeft = Math.max(0.0, this.scrollWidth - visibleWidth);
        this.scrollLeft = clamped = Math.max(0.0, Math.min(maxScrollLeft, value));
        this.targetScrollLeft = clamped;
    }

    protected final void setTextScrollTopImmediate(double value) {
        double clamped;
        double visibleHeight = Math.max(0.0, Box.of(this).innerSize().height());
        double maxScrollTop = Math.max(0.0, this.scrollHeight - visibleHeight);
        this.scrollTop = clamped = Math.max(0.0, Math.min(maxScrollTop, value));
        this.targetScrollTop = clamped;
    }

    protected String getRenderText() {
        this.ensureValue();
        return this.value;
    }

    protected void drawSingleLineSelection(PoseStack poseStack, Rect rectRenderer, String renderText, float drawY, double lineHeight) {
        int max;
        if (!this.canSelectText()) {
            return;
        }
        if (!this.hasSelection()) {
            return;
        }
        int min = this.clamp(this.selMin(), 0, renderText.length());
        if (min >= (max = this.clamp(this.selMax(), 0, renderText.length()))) {
            return;
        }
        Position contentPos = rectRenderer.getContentPosition();
        double alignX = this.resolveTextAlignX(renderText);
        double startX = alignX + Size.measureText(this, renderText.substring(0, min)) - this.scrollLeft;
        double endX = alignX + Size.measureText(this, renderText.substring(0, max)) - this.scrollLeft;
        float x0 = (float)(contentPos.x + startX);
        float x1 = (float)(contentPos.x + endX);
        float y0 = drawY;
        float y1 = y0 + (float)lineHeight;
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y0, x1, y1, Text.getSelectionColor(this));
    }

    protected void drawSingleLineCursor(PoseStack poseStack, String renderText, float drawX, float drawY, float lineHeight) {
        if (!Element.isElementFocusing(this)) {
            return;
        }
        String textBefore = renderText.substring(0, Math.min(this.cursor, renderText.length()));
        double cursorXOffset = Size.measureText(this, textBefore);
        float renderX = (float)((double)drawX + cursorXOffset);
        Graph.drawCursor(poseStack.m_85850_().m_252922_(), renderX, drawY, lineHeight, Text.getFontColor(this), this.lastBlinkTime);
    }

    protected List<String> splitLines(String text) {
        return new ArrayList<String>(List.of(text.split("\n", -1)));
    }

    protected int[] buildLineStarts(List<String> lines) {
        int[] starts = new int[lines.size()];
        int offset = 0;
        for (int i = 0; i < lines.size(); ++i) {
            starts[i] = offset;
            offset += lines.get(i).length();
            if (i >= lines.size() - 1) continue;
            ++offset;
        }
        return starts;
    }

    protected int clamp(int value, int min, int max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    @Override
    public void tick() {
        if (!Objects.equals(this.cachedValue, this.value) && this.value != null) {
            this.cachedValue = this.value;
            this.getRenderer().text.clear();
            this.getRenderer().size.clear();
            if (this.document != null) {
                this.document.markDirty(this, 5);
                if (this.parentElement != null) {
                    this.parentElement.getRenderer().size.clear();
                    this.document.markDirty(this.parentElement, 5);
                }
            }
        }
    }

    @Override
    public boolean canFocus() {
        return true;
    }

    protected record TextState(String value, int cursor, int selectionStart, int selectionEnd, int selectionAnchor, String selectionDirection) {
    }
}

