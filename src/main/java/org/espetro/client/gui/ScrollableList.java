package org.espetro.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.espetro.client.aui.GuiElement;

import java.util.List;

/**
 * 垂直滚动列表容器 — 参考 mutil 的 GuiHorizontalScrollable 实现。
 * 子元素按 addChild 的顺序垂直排列，超出的部分可通过鼠标滚轮滚动。
 */
public class ScrollableList extends GuiElement {

    private static final int SCROLLBAR_W = 4;
    private static final int MIN_HANDLE_H = 10;
    /** 行 hover 底色圆角（≤ 6，逐行扫描成本可控）。 */
    private static final int ROW_RADIUS = 2;

    private double scrollOffset = 0;
    private double scrollVelocity = 0;
    private int maxScroll = 0;
    private int scrollStep = 12;
    private boolean alwaysShowScrollbar = true;
    private boolean dirty = true;
    private long lastDraw = System.currentTimeMillis();

    public ScrollableList(int x, int y, int width, int height) {
        super(x, y, width, height);
    }

    public double getOffset() {
        return scrollOffset;
    }

    public int getMaxScroll() {
        return maxScroll;
    }

    public void setOffset(double offset) {
        if (dirty) {
            recalculateBounds();
        }
        scrollOffset = Mth.clamp(offset, 0, maxScroll);
    }

    public ScrollableList setScrollStep(int scrollStep) {
        this.scrollStep = Math.max(1, scrollStep);
        return this;
    }

    public ScrollableList setAlwaysShowScrollbar(boolean alwaysShowScrollbar) {
        this.alwaysShowScrollbar = alwaysShowScrollbar;
        return this;
    }

    public void markDirty() {
        dirty = true;
    }

    private void recalculateBounds() {
        int totalHeight = 0;
        for (GuiElement child : getChildren()) {
            totalHeight = Math.max(totalHeight, child.getY() + child.getHeight());
        }
        maxScroll = Math.max(0, totalHeight - getHeight());
        scrollOffset = Mth.clamp(scrollOffset, 0, maxScroll);
        dirty = false;
    }

    @Override
    public void addChild(GuiElement child) {
        super.addChild(child);
        markDirty();
    }

    @Override
    public void clearChildren() {
        super.clearChildren();
        markDirty();
    }

    @Override
    public boolean onMouseScroll(double mouseX, double mouseY, double distance) {
        if (super.onMouseScroll(mouseX, mouseY, distance)) {
            return true;
        }

        if (dirty) {
            recalculateBounds();
        }

        if (maxScroll > 0 && hasFocus()) {
            if (Math.signum(scrollVelocity) != Math.signum(-distance)) {
                scrollVelocity = 0;
            }
            scrollVelocity -= distance * scrollStep;
            scrollOffset = Mth.clamp(scrollOffset - distance * scrollStep, 0, maxScroll);
            return true;
        }
        return false;
    }

    @Override
    public void updateFocusState(int refX, int refY, int mouseX, int mouseY) {
        boolean gainFocus = mouseX >= getX() + refX && mouseX < getX() + refX + getWidth()
            && mouseY >= getY() + refY && mouseY < getY() + refY + getHeight();
        if (gainFocus != hasFocus) {
            hasFocus = gainFocus;
            if (hasFocus) {
                onFocus();
            } else {
                onBlur();
            }
        }

        int childRefX = hasFocus ? refX + getX() : Integer.MIN_VALUE / 4;
        int childRefY = hasFocus ? refY + getY() - (int) scrollOffset : Integer.MIN_VALUE / 4;
        for (GuiElement child : getChildren()) {
            if (child.isVisible()) {
                child.updateFocusState(childRefX, childRefY, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void drawChildren(GuiGraphics graphics, int refX, int refY, int screenWidth,
                                int screenHeight, int mouseX, int mouseY, float opacity) {
        if (dirty) {
            recalculateBounds();
        }

        long now = System.currentTimeMillis();
        if (scrollVelocity != 0) {
            double dist = (scrollVelocity * 0.2 + Math.signum(scrollVelocity) * 1) * (now - lastDraw) / 1000 * 50;
            if (Math.signum(scrollVelocity) != Math.signum(scrollVelocity - dist)) {
                dist = scrollVelocity;
                scrollVelocity = 0;
            } else {
                scrollVelocity -= dist;
            }
            scrollOffset = Mth.clamp(scrollOffset + dist, 0, maxScroll);
        }
        lastDraw = now;

        graphics.enableScissor(refX, refY, refX + getWidth(), refY + getHeight());
        drawRowHover(graphics, refX, refY - (int) scrollOffset, mouseX, mouseY);
        super.drawChildren(graphics, refX, refY - (int) scrollOffset, screenWidth, screenHeight,
            mouseX, mouseY, opacity);
        graphics.disableScissor();

        if (alwaysShowScrollbar || maxScroll > 0) {
            int trackX = refX + getWidth() - SCROLLBAR_W;
            int trackY = refY;
            int trackH = getHeight();
            boolean scrollable = maxScroll > 0;
            // 轨道：内凹圆角槽（SUNKEN），滑块：圆角胶囊。
            UiPaint.roundRect(graphics, trackX, trackY, SCROLLBAR_W, trackH,
                SCROLLBAR_W / 2, UiTheme.SUNKEN);

            int handleH = scrollable
                ? Math.max(MIN_HANDLE_H, (int) ((double) getHeight() / (getHeight() + maxScroll) * trackH))
                : trackH;
            int travel = Math.max(0, trackH - handleH);
            int handleY = scrollable ? trackY + (int) (scrollOffset / maxScroll * travel) : trackY;
            UiPaint.pill(graphics, trackX - 1, handleY, SCROLLBAR_W + 2, handleH,
                scrollable ? UiTheme.withAlpha(UiTheme.TEXT_SECOND, 0xA8)
                    : UiTheme.withAlpha(UiTheme.TEXT_THIRD, 0x50));
        }
    }

    /**
     * 行 hover 底色：画在子元素之下，行本身已有不透明底板时自然被盖住；
     * 纯文字行则透出一层 {@code SURFACE_2}，与"列表行 hover"规范一致。
     */
    private void drawRowHover(GuiGraphics graphics, int refX, int refY, int mouseX, int mouseY) {
        List<GuiElement> children = getChildren();
        for (int i = children.size() - 1; i >= 0; i--) {
            GuiElement child = children.get(i);
            if (!child.isVisible()) continue;
            int childX = refX + child.getX();
            int childY = refY + child.getY();
            if (mouseX < childX || mouseX >= childX + child.getWidth()
                || mouseY < childY || mouseY >= childY + child.getHeight()) {
                continue;
            }
            UiPaint.roundRect(graphics, childX, childY, child.getWidth(), child.getHeight(),
                ROW_RADIUS, UiTheme.SURFACE_2);
            return;
        }
    }
}
