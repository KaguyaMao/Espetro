/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.util.TextMetrics;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ContentEditable
extends AbstractText {
    private boolean contentEditableEnabled;

    public ContentEditable(Document document, String tagName) {
        super(document, tagName);
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
        }
        if (!this.hasAttribute("maxlength")) {
            this.maxLength = Integer.MAX_VALUE;
        }
        this.contentEditableEnabled = this.resolveContentEditableEnabled();
        if (!this.childNodes.isEmpty()) {
            this.childNodes.clear();
            if (this.children == null) {
                this.children = new ArrayList();
            } else {
                this.children.clear();
            }
        }
        this.innerText = this.value == null ? "" : this.value;
        this.selectionAnchor = this.cursor = 0;
        this.clearSelection();
        this.getRenderer().text.clear();
    }

    @Override
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
        this.syncContentEditableState(name);
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        this.syncContentEditableState(name);
    }

    private void syncContentEditableState(String name) {
        if (!"contenteditable".equalsIgnoreCase(name)) {
            return;
        }
        this.contentEditableEnabled = this.resolveContentEditableEnabled();
        if (!this.contentEditableEnabled) {
            this.clearSelection();
        }
        this.getRenderer().text.clear();
        this.addDirtyFlags(1);
    }

    private boolean resolveContentEditableEnabled() {
        if (!this.hasAttribute("contenteditable")) {
            return false;
        }
        String raw = this.getAttribute("contenteditable");
        return raw == null || !"false".equalsIgnoreCase(raw.trim());
    }

    public boolean isContentEditable() {
        return this.contentEditableEnabled;
    }

    @Override
    public boolean canEditText() {
        return this.contentEditableEnabled;
    }

    @Override
    public boolean canSelectText() {
        return this.contentEditableEnabled && super.canSelectText();
    }

    @Override
    public boolean canFocus() {
        return this.contentEditableEnabled;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.value != null && !Objects.equals(this.innerText, this.value)) {
            this.innerText = this.value;
        }
    }

    @Override
    public void setTextContent(String value) {
        String normalized;
        super.setTextContent(value);
        String current = this.getTextContent();
        this.value = normalized = current == null ? "" : current.replace("\r\n", "\n").replace('\r', '\n');
        this.selectionAnchor = this.cursor = Math.min(this.cursor, normalized.length());
        this.clearSelection();
        this.undoStack.clear();
        this.getRenderer().text.clear();
    }

    @Override
    public void setInnerText(String value) {
        this.setTextContent(value == null ? "" : value.replace("\r\n", "\n").replace('\r', '\n'));
    }

    @Override
    public String getInnerText() {
        return this.value == null ? "" : this.value;
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
        }
        if (phase != Base.RenderPhase.BODY) {
            return;
        }
        rectRenderer.drawBody(poseStack);
        String renderText = this.getRenderText();
        if (renderText.isEmpty()) {
            if (this.canEditText() && Element.isElementFocusing(this)) {
                Text text = Text.of(this);
                Position contentPos = rectRenderer.getContentPosition();
                Graph.drawCursor(poseStack.m_85850_().m_252922_(), (float)contentPos.x, (float)contentPos.y, (float)Math.max(text.lineHeight, 16.0), Text.getFontColor(this), this.lastBlinkTime);
            }
            return;
        }
        Text text = Text.of(this);
        double lineHeight = text.lineHeight;
        Position contentPos = rectRenderer.getContentPosition();
        double currentScrollLeft = this.scrollLeft;
        double currentScrollTop = this.getScrollTop();
        float baseX = (float)(contentPos.x - currentScrollLeft);
        float baseY = (float)(contentPos.y - currentScrollTop);
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
        if (!this.canEditText() || !Element.isElementFocusing(this)) {
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

