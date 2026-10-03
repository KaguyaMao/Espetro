/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.util.TextMetrics;
import java.util.List;
import java.util.Locale;

@ElementRegister(value="TEXTAREA")
public class TextArea
extends AbstractText {
    public static final String TAG_NAME = "TEXTAREA";
    private boolean textAreaValueDirty;
    private boolean resizing;
    private double resizeStartX;
    private double resizeStartY;
    private double resizeStartWidth;
    private double resizeStartHeight;

    public TextArea(Document document) {
        super(document, TAG_NAME);
        this.addInternalEventListener("mousedown", this::beginResize);
        this.addInternalEventListener("mousemove", this::continueResize);
        this.addInternalEventListener("mouseup", event -> {
            this.resizing = false;
        });
    }

    @Override
    protected boolean supportsMultilineInput() {
        return true;
    }

    @Override
    protected void onInitFromDom(Element origin) {
        super.onInitFromDom(origin);
        if (!this.hasAttribute("value") && (this.value == null || this.value.isEmpty())) {
            String inlineText;
            String string = inlineText = origin == null ? "" : origin.getTextContent();
            if (inlineText == null) {
                inlineText = "";
            }
            this.value = inlineText.replace("\r\n", "\n").replace('\r', '\n');
            this.textAreaValueDirty = false;
            this.selectionAnchor = this.cursor = Math.min(this.cursor, this.value.length());
            this.clearSelection();
            this.getRenderer().text.clear();
        }
    }

    @Override
    public String getDefaultValue() {
        String text = this.getTextContent();
        return text == null ? "" : text.replace("\r\n", "\n").replace('\r', '\n');
    }

    @Override
    public void setDefaultValue(String value) {
        String normalized = value == null ? "" : value.replace("\r\n", "\n").replace('\r', '\n');
        this.setTextContent(normalized);
        if (!this.textAreaValueDirty) {
            this.value = normalized;
            this.selectionAnchor = this.cursor = Math.min(this.cursor, normalized.length());
            this.clearSelection();
            this.clampScroll();
            this.getRenderer().text.clear();
        }
    }

    @Override
    public void setValue(String value) {
        super.setValue(value == null ? "" : value.replace("\r\n", "\n").replace('\r', '\n'));
        this.textAreaValueDirty = true;
    }

    @Override
    protected void restoreFormValue(String restored) {
        String normalized;
        this.value = normalized = restored == null ? "" : restored.replace("\r\n", "\n").replace('\r', '\n');
        this.textAreaValueDirty = false;
        this.selectionAnchor = this.cursor = Math.min(this.cursor, normalized.length());
        this.clearSelection();
        this.clampScroll();
        this.getRenderer().text.clear();
        this.getRenderer().wrappedText.clear();
        this.invalidateStyle();
    }

    @Override
    protected void locateCursor(double mouseOffsetX, double mouseOffsetY) {
        double charWidth;
        String renderText = this.getRenderText();
        Text text = Text.of(this);
        text.content = renderText;
        Text.WrappedText wrapped = Text.wrap(text, Box.of(this).innerSize().width());
        List<String> lines = wrapped.lines();
        int[] starts = wrapped.starts();
        Box box = Box.of(this);
        double contentStartX = box.getBorderLeft() + box.getPaddingLeft();
        double contentStartY = box.getBorderTop() + box.getPaddingTop();
        double lineHeight = text.lineHeight;
        if (lineHeight <= 0.0) {
            lineHeight = 16.0;
        }
        double relativeY = mouseOffsetY - contentStartY + this.getScrollTop();
        int line = this.clamp((int)Math.floor(relativeY / lineHeight), 0, Math.max(0, lines.size() - 1));
        String lineText = lines.get(line);
        double alignX = this.textAlignX(text, lineText, line);
        double relativeX = mouseOffsetX - contentStartX + this.scrollLeft - alignX;
        double currentWidth = 0.0;
        int column = 0;
        for (int i = 0; i < lineText.length() && !(relativeX <= currentWidth + (charWidth = Size.measureText(this, String.valueOf(lineText.charAt(i)))) / 2.0); ++i) {
            currentWidth += charWidth;
            ++column;
        }
        this.cursor = starts[line] + column;
        this.clampScroll();
    }

    private double textAlignX(Text text, String line, int lineIndex) {
        double contentWidth;
        double lineWidth = Size.measureText(this, line);
        if (lineWidth > (contentWidth = Math.max(0.0, Box.of(this).innerSize().width()))) {
            return 0.0;
        }
        return TextMetrics.computeAlignedX(text, contentWidth, lineWidth, lineIndex == 0);
    }

    @Override
    protected void clampScroll() {
        String renderText = this.getRenderText();
        Text text = Text.of(this);
        text.content = renderText;
        Text.WrappedText wrapped = Text.wrap(text, Box.of(this).innerSize().width());
        List<String> lines = wrapped.lines();
        int[] starts = wrapped.starts();
        this.cursor = this.clamp(this.cursor, 0, renderText.length());
        double lineHeight = text.lineHeight;
        int cursorLine = this.resolveCursorLine(lines, starts, this.cursor);
        int lineStart = starts[cursorLine];
        int column = this.clamp(this.cursor - lineStart, 0, lines.get(cursorLine).length());
        double cursorX = Size.measureText(this, lines.get(cursorLine).substring(0, column));
        double cursorY = (double)cursorLine * lineHeight;
        Size visibleSize = Box.of(this).innerSize();
        double visibleWidth = Math.max(0.0, visibleSize.width());
        double visibleHeight = Math.max(0.0, visibleSize.height());
        this.scrollWidth = wrapped.width();
        this.scrollHeight = wrapped.height(lineHeight);
        double desiredScrollLeft = this.scrollLeft;
        if (cursorX < desiredScrollLeft) {
            desiredScrollLeft = cursorX;
        } else if (cursorX > desiredScrollLeft + visibleWidth) {
            desiredScrollLeft = cursorX - visibleWidth + 2.0;
        }
        this.setTextScrollLeftImmediate(desiredScrollLeft);
        double desiredScrollTop = this.scrollTop;
        if (cursorY < desiredScrollTop) {
            desiredScrollTop = cursorY;
        } else if (cursorY + lineHeight > desiredScrollTop + visibleHeight) {
            desiredScrollTop = cursorY + lineHeight - visibleHeight + 2.0;
        }
        this.setTextScrollTopImmediate(desiredScrollTop);
        this.addDirtyFlags(1);
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        Rect rectRenderer = Rect.of(this);
        if (phase == Base.RenderPhase.SHADOW) {
            rectRenderer.drawShadow(poseStack);
        }
        if (phase == Base.RenderPhase.BORDER) {
            rectRenderer.drawBorder(poseStack);
            this.drawResizeHandle(poseStack, rectRenderer);
        }
        if (phase != Base.RenderPhase.BODY) {
            return;
        }
        rectRenderer.drawBody(poseStack);
        String renderText = this.getRenderText();
        boolean isPlaceholder = renderText.isEmpty() && !this.placeholder.isEmpty();
        Text text = Text.of(this);
        double lineHeight = text.lineHeight;
        Position contentPos = rectRenderer.getContentPosition();
        double currentScrollLeft = this.scrollLeft;
        double currentScrollTop = this.getScrollTop();
        float baseX = (float)(contentPos.x - currentScrollLeft);
        float baseY = (float)(contentPos.y - currentScrollTop);
        if (isPlaceholder) {
            text.content = this.placeholder;
            text.color = new Color("#888888");
            float placeholderX = (float)((double)baseX + this.textAlignX(text, this.placeholder, 0));
            FontDrawer.drawFont(poseStack, text, new Position(placeholderX, baseY));
            if (Element.isElementFocusing(this)) {
                Graph.drawCursor(poseStack.m_85850_().m_252922_(), placeholderX, baseY, (float)lineHeight, Text.getFontColor(this), this.lastBlinkTime);
            }
            return;
        }
        text.content = renderText;
        Text.WrappedText wrapped = Text.wrap(text, Box.of(this).innerSize().width());
        List<String> lines = wrapped.lines();
        int[] starts = wrapped.starts();
        this.drawSelection(poseStack, text, lines, starts, baseX, baseY, lineHeight);
        for (int i = 0; i < lines.size(); ++i) {
            int max;
            String line = lines.get(i);
            float y = (float)((double)baseY + (double)i * lineHeight);
            float lineX = (float)((double)baseX + this.textAlignX(text, line, i));
            if (!this.canSelectText() || !this.hasSelection()) {
                text.content = line;
                text.color = new Color(Text.getFontColor(this));
                FontDrawer.drawFont(poseStack, text, new Position(lineX, y));
                continue;
            }
            int lineStart = starts[i];
            int lineEnd = lineStart + line.length();
            int min = Math.max(this.selMin(), lineStart);
            if (min >= (max = Math.min(this.selMax(), lineEnd))) {
                text.content = line;
                text.color = new Color(Text.getFontColor(this));
                FontDrawer.drawFont(poseStack, text, new Position(lineX, y));
                continue;
            }
            String before = line.substring(0, min - lineStart);
            String selected = line.substring(min - lineStart, max - lineStart);
            String after = line.substring(max - lineStart);
            float segmentX = lineX;
            if (!before.isEmpty()) {
                text.content = before;
                text.color = new Color(Text.getFontColor(this));
                FontDrawer.drawFont(poseStack, text, new Position(segmentX, y));
                segmentX += (float)Size.measureText(this, before);
            }
            if (!selected.isEmpty()) {
                text.content = selected;
                text.color = new Color("#FFFFFF");
                FontDrawer.drawFont(poseStack, text, new Position(segmentX, y));
                segmentX += (float)Size.measureText(this, selected);
            }
            if (after.isEmpty()) continue;
            text.content = after;
            text.color = new Color(Text.getFontColor(this));
            FontDrawer.drawFont(poseStack, text, new Position(segmentX, y));
        }
        if (!Element.isElementFocusing(this)) {
            return;
        }
        int cursorLine = this.resolveCursorLine(lines, starts, this.cursor);
        int lineStart = starts[cursorLine];
        int column = this.clamp(this.cursor - lineStart, 0, lines.get(cursorLine).length());
        double cursorOffset = Size.measureText(this, lines.get(cursorLine).substring(0, column));
        float cursorX = (float)((double)baseX + this.textAlignX(text, lines.get(cursorLine), cursorLine) + cursorOffset);
        float cursorY = (float)((double)baseY + (double)cursorLine * lineHeight);
        Graph.drawCursor(poseStack.m_85850_().m_252922_(), cursorX, cursorY, (float)lineHeight, Text.getFontColor(this), this.lastBlinkTime);
    }

    public boolean isResizeHandleAt(Position documentPosition) {
        if (documentPosition == null || !this.canResize()) {
            return false;
        }
        Box box = Box.of(this);
        Position position = Position.of(this);
        double localX = documentPosition.x - position.x - box.getMarginLeft();
        double localY = documentPosition.y - position.y - box.getMarginTop();
        return this.isResizeHandleOffset(localX, localY);
    }

    public String getResizeCursor() {
        String resize = this.normalizedResize();
        if ("vertical".equals(resize) || "block".equals(resize)) {
            return "ns-resize";
        }
        if ("horizontal".equals(resize) || "inline".equals(resize)) {
            return "ew-resize";
        }
        return "se-resize";
    }

    private void beginResize(Event event) {
        MouseEvent mouseEvent;
        block3: {
            block2: {
                if (!(event instanceof MouseEvent)) break block2;
                mouseEvent = (MouseEvent)event;
                if (this.isResizeHandleOffset(mouseEvent.offsetX, mouseEvent.offsetY)) break block3;
            }
            return;
        }
        Box box = Box.of(this);
        this.resizing = true;
        this.resizeStartX = mouseEvent.clientX;
        this.resizeStartY = mouseEvent.clientY;
        this.resizeStartWidth = box.elementSize().width();
        this.resizeStartHeight = box.elementSize().height();
        this.clearSelection();
        event.preventDefault();
    }

    private void continueResize(Event event) {
        if (!this.resizing || !(event instanceof MouseEvent)) {
            return;
        }
        MouseEvent mouseEvent = (MouseEvent)event;
        String resize = this.normalizedResize();
        Box box = Box.of(this);
        boolean borderBox = box.isBorderBox();
        if ("both".equals(resize) || "horizontal".equals(resize) || "inline".equals(resize)) {
            double width = Math.max(16.0, this.resizeStartWidth + mouseEvent.clientX - this.resizeStartX);
            if (!borderBox) {
                width -= box.getBorderHorizontal() + box.getPaddingHorizontal();
            }
            this.setInlineStyleProperty("width", TextArea.px(Math.max(0.0, width)));
        }
        if ("both".equals(resize) || "vertical".equals(resize) || "block".equals(resize)) {
            double height = Math.max(16.0, this.resizeStartHeight + mouseEvent.clientY - this.resizeStartY);
            if (!borderBox) {
                height -= box.getBorderVertical() + box.getPaddingVertical();
            }
            this.setInlineStyleProperty("height", TextArea.px(Math.max(0.0, height)));
        }
        event.preventDefault();
    }

    private void drawResizeHandle(PoseStack poseStack, Rect rectRenderer) {
        if (!this.canResize()) {
            return;
        }
        Box box = rectRenderer.box;
        float right = (float)(rectRenderer.position.x + box.getMarginLeft() + box.elementSize().width() - 3.0);
        float bottom = (float)(rectRenderer.position.y + box.getMarginTop() + box.elementSize().height() - 3.0);
        int color = new Color(this.isDisabled() ? "#777777" : "#A9A9A9").getValue();
        for (int i = 0; i < 3; ++i) {
            float length = 3 + i * 3;
            int step = 0;
            while ((float)step < length) {
                float x = right - (float)step;
                float y = bottom - (length - (float)step - 1.0f);
                Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x, y, x + 1.0f, y + 1.0f, color);
                step += 2;
            }
        }
    }

    private boolean isResizeHandleOffset(double offsetX, double offsetY) {
        if (!this.canResize()) {
            return false;
        }
        Size size = Box.of(this).elementSize();
        return offsetX >= size.width() - 14.0 && offsetY >= size.height() - 14.0;
    }

    private boolean canResize() {
        return !this.isDisabled() && !"none".equals(this.normalizedResize());
    }

    private String normalizedResize() {
        String resize = this.getComputedStyle().resize;
        if (resize == null) {
            return "none";
        }
        return switch (resize = resize.trim().toLowerCase(Locale.ROOT)) {
            case "both", "horizontal", "vertical", "block", "inline" -> resize;
            default -> "none";
        };
    }

    private static String px(double value) {
        return String.format(Locale.ROOT, "%.2fpx", value);
    }

    private void drawSelection(PoseStack poseStack, Text text, List<String> lines, int[] starts, float baseX, float baseY, double lineHeight) {
        int max;
        if (!this.canSelectText()) {
            return;
        }
        if (!this.hasSelection()) {
            return;
        }
        int min = this.selMin();
        if (min == (max = this.selMax())) {
            return;
        }
        for (int i = 0; i < lines.size(); ++i) {
            int drawEnd;
            String lineText = lines.get(i);
            int lineStart = starts[i];
            int lineEnd = lineStart + lineText.length();
            int drawStart = Math.max(min, lineStart);
            if (drawStart >= (drawEnd = Math.min(max, lineEnd))) continue;
            double alignX = this.textAlignX(text, lineText, i);
            double startX = alignX + Size.measureText(this, lineText.substring(0, drawStart - lineStart));
            double endX = alignX + Size.measureText(this, lineText.substring(0, drawEnd - lineStart));
            float x0 = (float)((double)baseX + startX);
            float x1 = (float)((double)baseX + endX);
            float y0 = (float)((double)baseY + (double)i * lineHeight);
            float y1 = (float)((double)y0 + lineHeight);
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y0, x1, y1, Text.getSelectionColor(this));
        }
    }

    private int resolveCursorLine(List<String> lines, int[] starts, int cursorIndex) {
        int line;
        for (line = 0; line < lines.size() - 1 && cursorIndex > starts[line] + lines.get(line).length(); ++line) {
        }
        return line;
    }
}

