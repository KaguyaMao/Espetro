/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.behavior;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.Body;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Style;

public final class ScrollModel {
    private static final double SCROLL_EASING_FACTOR = 0.2;
    private static final double SCROLL_OVERSCROLL_DAMPING = 0.4;
    private static final double SCROLL_STOP_EPSILON = 0.01;
    private static final double BASE_FRAME_MS = 16.6666666667;
    private static final double MAX_FRAME_MS = 50.0;
    private static final double SCROLLBAR_GUTTER = 8.0;
    private static final double SCROLLBAR_EPSILON = 0.5;
    private static final double SCROLLBAR_TRACK_SIZE = 6.0;
    private static final double SCROLLBAR_TRACK_INSET = 1.0;
    private static final double SCROLLBAR_MIN_THUMB_LENGTH = 10.0;
    private static final float SCROLLBAR_THUMB_DEPTH_FRACTION = 0.5f;
    private final Element owner;
    private long lastRenderStepNs;
    private boolean verticalScrollbarVisible;
    private boolean horizontalScrollbarVisible;
    private boolean scrollbarLayoutDirty;
    private DragAxis dragAxis = DragAxis.NONE;
    private double dragPointerOffset;
    private boolean scrollbarPointerActive;

    public ScrollModel(Element owner) {
        this.owner = owner;
    }

    public void setScrollLeft(double value) {
        this.owner.targetScrollLeft = this.applyOverscroll(value, this.getHorizontalScrollLimit());
    }

    public void setScrollTop(double value) {
        this.owner.targetScrollTop = this.applyOverscroll(value, this.getVerticalScrollLimit());
    }

    public double getScrollLeft() {
        return this.owner.scrollLeft;
    }

    public double getScrollTop() {
        return this.owner.scrollTop;
    }

    public double getTargetScrollLeft() {
        return this.owner.targetScrollLeft;
    }

    public double getTargetScrollTop() {
        return this.owner.targetScrollTop;
    }

    public boolean canScroll() {
        return this.canScrollVertically() || this.canScrollHorizontally();
    }

    public boolean canScrollVertically() {
        if (this.isViewportScroller()) {
            return this.allowsViewportUserScroll(this.resolveViewportOverflowY());
        }
        return Interaction.allowsUserScrollY(this.owner.getComputedStyle());
    }

    public boolean canScrollHorizontally() {
        if (this.isViewportScroller()) {
            return this.allowsViewportUserScroll(this.resolveViewportOverflowX());
        }
        return Interaction.allowsUserScrollX(this.owner.getComputedStyle());
    }

    public boolean hasVerticalScrollRange() {
        if (this.isViewportScroller() ? !this.canScrollVertically() : !Interaction.allowsUserScrollY(this.owner.getComputedStyle())) {
            return false;
        }
        this.commitLayoutMetrics();
        return this.getVerticalScrollLimitFromMetrics() > 0.5;
    }

    public boolean hasHorizontalScrollRange() {
        if (this.isViewportScroller() ? !this.canScrollHorizontally() : !Interaction.allowsUserScrollX(this.owner.getComputedStyle())) {
            return false;
        }
        this.commitLayoutMetrics();
        return this.getHorizontalScrollLimitFromMetrics() > 0.5;
    }

    public boolean tick() {
        if (!this.scrollbarLayoutDirty) {
            return false;
        }
        this.scrollbarLayoutDirty = false;
        this.owner.getRenderer().invalidateLayoutSubtree();
        if (this.owner.document != null) {
            this.owner.document.markDirty(this.owner, 13);
        }
        return true;
    }

    public boolean stepRender() {
        if (!this.needsRenderStep()) {
            this.lastRenderStepNs = 0L;
            return false;
        }
        double previousLeft = this.owner.scrollLeft;
        double previousTop = this.owner.scrollTop;
        double frameScale = this.consumeFrameScale();
        this.stepHorizontalScroll(frameScale);
        this.stepVerticalScroll(frameScale);
        return Double.compare(previousLeft, this.owner.scrollLeft) != 0 || Double.compare(previousTop, this.owner.scrollTop) != 0;
    }

    public boolean needsRenderStep() {
        return !this.isScrollSettled(this.owner.scrollLeft, this.owner.targetScrollLeft) || !this.isScrollSettled(this.owner.scrollTop, this.owner.targetScrollTop);
    }

    public boolean mayRenderScrollbar() {
        return this.verticalScrollbarVisible || this.horizontalScrollbarVisible || this.mayShowVerticalScrollbar() || this.mayShowHorizontalScrollbar();
    }

    public boolean handleMouseDown(MouseEvent event) {
        double pointer;
        AxisGeometry hit;
        if (event == null || event.button != 0) {
            return false;
        }
        if (!this.mayRenderScrollbar()) {
            return false;
        }
        Rect rect = Rect.of(this.owner);
        AxisGeometry vertical = this.axisGeometry(true, rect);
        AxisGeometry horizontal = this.axisGeometry(false, rect);
        AxisGeometry axisGeometry = ScrollModel.contains(vertical, event.clientX, event.clientY) ? vertical : (hit = ScrollModel.contains(horizontal, event.clientX, event.clientY) ? horizontal : null);
        if (hit == null) {
            return false;
        }
        this.scrollbarPointerActive = true;
        double beforeLeft = this.owner.getTargetScrollLeft();
        double beforeTop = this.owner.getTargetScrollTop();
        double d = pointer = hit.vertical ? event.clientY : event.clientX;
        if (pointer >= hit.thumbStart() && pointer <= hit.thumbEnd()) {
            this.dragAxis = hit.vertical ? DragAxis.VERTICAL : DragAxis.HORIZONTAL;
            this.dragPointerOffset = pointer - hit.thumbStart();
        } else {
            double direction;
            double page = hit.vertical ? this.getScrollportHeight() : this.getScrollportWidth();
            double d2 = direction = pointer < hit.thumbStart() ? -1.0 : 1.0;
            if (hit.vertical) {
                this.setScrollTop(this.clampScrollTarget(this.owner.getTargetScrollTop() + direction * page, this.getVerticalScrollLimitFromMetrics()));
            } else {
                this.setScrollLeft(this.clampScrollTarget(this.owner.getTargetScrollLeft() + direction * page, this.getHorizontalScrollLimitFromMetrics()));
            }
            this.owner.dispatchScrollEventIfChanged(beforeLeft, beforeTop);
        }
        return true;
    }

    public boolean handleMouseMove(MouseEvent event) {
        if (event == null || !this.scrollbarPointerActive) {
            return false;
        }
        if (this.dragAxis == DragAxis.NONE) {
            return true;
        }
        AxisGeometry geometry = this.axisGeometry(this.dragAxis == DragAxis.VERTICAL, Rect.of(this.owner));
        if (geometry == null) {
            return true;
        }
        double pointer = geometry.vertical ? event.clientY : event.clientX;
        double travel = geometry.trackLength() - geometry.thumbLength();
        double ratio = travel <= 0.0 ? 0.0 : (pointer - geometry.trackStart() - this.dragPointerOffset) / travel;
        ratio = Math.max(0.0, Math.min(1.0, ratio));
        double limit = geometry.vertical ? this.getVerticalScrollLimitFromMetrics() : this.getHorizontalScrollLimitFromMetrics();
        this.setScrollImmediate(geometry.vertical, ratio * limit);
        return true;
    }

    public boolean handleMouseUp(MouseEvent event) {
        if (!this.scrollbarPointerActive) {
            return false;
        }
        this.scrollbarPointerActive = false;
        this.dragAxis = DragAxis.NONE;
        this.dragPointerOffset = 0.0;
        return true;
    }

    public boolean isScrollbarInteractionActive() {
        return this.scrollbarPointerActive;
    }

    public void drawScrollbar(PoseStack poseStack, Rect rectRenderer) {
        if (!this.mayShowHorizontalScrollbar() && !this.mayShowVerticalScrollbar()) {
            this.setScrollbarVisibility(false, false);
            return;
        }
        if (!this.verticalScrollbarVisible && !this.horizontalScrollbarVisible) {
            return;
        }
        Position bodyPos = rectRenderer.getBodyRectPosition();
        Size bodySize = rectRenderer.getBodyRectSize();
        if (this.verticalScrollbarVisible) {
            this.drawVerticalScrollbar(poseStack, bodyPos, bodySize);
        }
        if (this.horizontalScrollbarVisible) {
            this.drawHorizontalScrollbar(poseStack, bodyPos, bodySize);
        }
    }

    public double getVerticalScrollbarGutter() {
        return this.verticalScrollbarVisible ? this.scrollbarGutter() : 0.0;
    }

    public double getHorizontalScrollbarGutter() {
        return this.horizontalScrollbarVisible ? this.scrollbarGutter() : 0.0;
    }

    private boolean stepHorizontalScroll(double frameScale) {
        ScrollStep step = this.stepScrollAxis(this.owner.scrollLeft, this.owner.targetScrollLeft, this.getHorizontalScrollLimit(), frameScale);
        this.owner.scrollLeft = step.current();
        this.owner.targetScrollLeft = step.target();
        return step.moving();
    }

    private boolean stepVerticalScroll(double frameScale) {
        ScrollStep step = this.stepScrollAxis(this.owner.scrollTop, this.owner.targetScrollTop, this.getVerticalScrollLimit(), frameScale);
        this.owner.scrollTop = step.current();
        this.owner.targetScrollTop = step.target();
        return step.moving();
    }

    private ScrollStep stepScrollAxis(double current, double target, double limit, double frameScale) {
        double clampedTarget = this.clampScrollTarget(target, limit);
        if (target < 0.0 || target > limit) {
            target = this.easeToward(target, clampedTarget, 0.28, frameScale);
        }
        if (!this.isScrollSettled(current, target)) {
            current = this.easeToward(current, target, 0.2, frameScale);
        }
        if (Math.abs(target - clampedTarget) <= 0.01) {
            target = clampedTarget;
        }
        if (this.isScrollSettled(current, target) && this.isScrollSettled(target, clampedTarget)) {
            current = clampedTarget;
            target = clampedTarget;
        }
        return new ScrollStep(current, target, !this.isScrollSettled(current, target));
    }

    private double consumeFrameScale() {
        long now = System.nanoTime();
        if (this.lastRenderStepNs <= 0L) {
            this.lastRenderStepNs = now;
            return 1.0;
        }
        double elapsedMs = Math.max(0.0, Math.min(50.0, (double)(now - this.lastRenderStepNs) / 1000000.0));
        this.lastRenderStepNs = now;
        return Math.max(0.25, elapsedMs / 16.6666666667);
    }

    private double easeToward(double current, double target, double factor, double frameScale) {
        if (factor <= 0.0) {
            return current;
        }
        if (factor >= 1.0) {
            return target;
        }
        double adjusted = 1.0 - Math.pow(1.0 - factor, Math.max(0.0, frameScale));
        return current + (target - current) * adjusted;
    }

    private double applyOverscroll(double value, double limit) {
        if (value < 0.0) {
            return value * 0.4;
        }
        if (value > limit) {
            return (value - limit) * 0.4 + limit;
        }
        return value;
    }

    private double clampScrollTarget(double value, double limit) {
        if (value < 0.0) {
            return 0.0;
        }
        if (value > limit) {
            return limit;
        }
        return value;
    }

    private double getHorizontalScrollLimit() {
        return this.getHorizontalScrollLimitFromMetrics();
    }

    private double getHorizontalScrollLimitFromMetrics() {
        return Math.max(0.0, this.owner.scrollWidth - this.getScrollportWidth());
    }

    private double getVerticalScrollLimit() {
        return this.getVerticalScrollLimitFromMetrics();
    }

    private double getVerticalScrollLimitFromMetrics() {
        return Math.max(0.0, this.owner.scrollHeight - this.getScrollportHeight());
    }

    private double getScrollportWidth() {
        if (this.isViewportScroller()) {
            return Math.max(0.0, (double)this.owner.document.getViewport().layoutWidth() - this.getVerticalScrollbarGutter());
        }
        return Box.of(this.owner).innerSize().width();
    }

    private double getScrollportHeight() {
        if (this.isViewportScroller()) {
            return Math.max(0.0, (double)this.owner.document.getViewport().layoutHeight() - this.getHorizontalScrollbarGutter());
        }
        return Box.of(this.owner).innerSize().height();
    }

    private boolean isViewportScroller() {
        return this.owner.document != null && this.owner.document.documentElement != null && this.owner == this.owner.document.documentElement;
    }

    private String resolveViewportOverflowX() {
        return this.resolveViewportOverflow(true);
    }

    private String resolveViewportOverflowY() {
        return this.resolveViewportOverflow(false);
    }

    private String resolveOverflowX() {
        return this.isViewportScroller() ? this.resolveViewportOverflowX() : Interaction.resolveOverflowX(this.owner.getComputedStyle());
    }

    private String resolveOverflowY() {
        return this.isViewportScroller() ? this.resolveViewportOverflowY() : Interaction.resolveOverflowY(this.owner.getComputedStyle());
    }

    private String resolveViewportOverflow(boolean horizontal) {
        String rootOverflow;
        String string = rootOverflow = horizontal ? Interaction.resolveOverflowX(this.owner.getComputedStyle()) : Interaction.resolveOverflowY(this.owner.getComputedStyle());
        if (!"visible".equals(rootOverflow)) {
            return rootOverflow;
        }
        Body body = this.owner.document.body;
        if (body == null) {
            return rootOverflow;
        }
        return horizontal ? Interaction.resolveOverflowX(body.getComputedStyle()) : Interaction.resolveOverflowY(body.getComputedStyle());
    }

    private boolean allowsViewportUserScroll(String overflow) {
        String normalized = Interaction.normalizeOverflow(overflow);
        return !"hidden".equals(normalized) && !"clip".equals(normalized);
    }

    public void commitLayoutMetrics() {
        if (!(this.owner instanceof AbstractText)) {
            Size contentSize = this.measureLayoutScrollArea();
            if (this.isViewportScroller() && this.owner.document.body != null) {
                Size bodyContentSize = ScrollModel.measureLayoutScrollArea(this.owner.document.body);
                contentSize = new Size(Math.max(contentSize.width(), bodyContentSize.width()), Math.max(contentSize.height(), bodyContentSize.height()));
            }
            this.owner.scrollWidth = contentSize.width();
            this.owner.scrollHeight = contentSize.height();
        }
        this.updateScrollbarVisibility();
    }

    private Size measureLayoutScrollArea() {
        return ScrollModel.measureLayoutScrollArea(this.owner);
    }

    private static Size measureLayoutScrollArea(Element scrollport) {
        if (scrollport == null) {
            return Size.ZERO;
        }
        Box box = Box.of(scrollport);
        double contentOriginX = box.offset("left");
        double contentOriginY = box.offset("top");
        double width = 0.0;
        double height = 0.0;
        for (Element child : scrollport.getRenderChildren()) {
            Style style = child.getRawComputedStyle();
            if ("none".equals(style.display) || "fixed".equals(style.position)) continue;
            Position offset = Position.getOffset(child);
            Size outerSize = Box.of(child).size();
            width = Math.max(width, offset.x - contentOriginX + outerSize.width());
            height = Math.max(height, offset.y - contentOriginY + outerSize.height());
        }
        return new Size(Math.max(0.0, width), Math.max(0.0, height));
    }

    private void updateScrollbarVisibility() {
        Size rawScrollport = this.rawScrollportSize();
        String overflowX = this.resolveOverflowX();
        String overflowY = this.resolveOverflowY();
        boolean forceHorizontal = "scroll".equals(overflowX);
        boolean forceVertical = "scroll".equals(overflowY);
        boolean autoHorizontal = "auto".equals(overflowX) || this.isViewportScroller() && "visible".equals(overflowX);
        boolean autoVertical = "auto".equals(overflowY) || this.isViewportScroller() && "visible".equals(overflowY);
        boolean nextHorizontal = forceHorizontal;
        boolean nextVertical = forceVertical;
        double gutter = this.scrollbarGutter();
        for (int i = 0; i < 3; ++i) {
            boolean resolvedVertical;
            double availableWidth = Math.max(0.0, rawScrollport.width() - (nextVertical ? gutter : 0.0));
            double availableHeight = Math.max(0.0, rawScrollport.height() - (nextHorizontal ? gutter : 0.0));
            boolean resolvedHorizontal = forceHorizontal || autoHorizontal && this.owner.scrollWidth > availableWidth + 0.5;
            boolean bl = resolvedVertical = forceVertical || autoVertical && this.owner.scrollHeight > availableHeight + 0.5;
            if (resolvedHorizontal == nextHorizontal && resolvedVertical == nextVertical) break;
            nextHorizontal = resolvedHorizontal;
            nextVertical = resolvedVertical;
        }
        this.setScrollbarVisibility(nextHorizontal, nextVertical);
    }

    private void setScrollbarVisibility(boolean horizontal, boolean vertical) {
        if (horizontal == this.horizontalScrollbarVisible && vertical == this.verticalScrollbarVisible) {
            return;
        }
        this.horizontalScrollbarVisible = horizontal;
        this.verticalScrollbarVisible = vertical;
        this.scrollbarLayoutDirty = true;
    }

    private boolean mayShowHorizontalScrollbar() {
        String overflow = this.resolveOverflowX();
        return "auto".equals(overflow) || "scroll".equals(overflow) || this.isViewportScroller() && "visible".equals(overflow);
    }

    private boolean mayShowVerticalScrollbar() {
        String overflow = this.resolveOverflowY();
        return "auto".equals(overflow) || "scroll".equals(overflow) || this.isViewportScroller() && "visible".equals(overflow);
    }

    private Size rawScrollportSize() {
        if (this.isViewportScroller()) {
            return new Size(Math.max(0, this.owner.document.getViewport().layoutWidth()), Math.max(0, this.owner.document.getViewport().layoutHeight()));
        }
        return Box.of(this.owner).rawInnerSize();
    }

    private void drawVerticalScrollbar(PoseStack poseStack, Position bodyPos, Size bodySize) {
        AxisGeometry geometry = this.axisGeometry(true, bodyPos, bodySize);
        if (geometry == null) {
            return;
        }
        this.drawScrollbarTrackAndThumb(poseStack, (float)geometry.trackX, (float)geometry.trackY, (float)geometry.trackWidth, (float)geometry.trackHeight, (float)geometry.thumbX, (float)geometry.thumbY, (float)geometry.thumbWidth, (float)geometry.thumbHeight);
    }

    private void drawHorizontalScrollbar(PoseStack poseStack, Position bodyPos, Size bodySize) {
        AxisGeometry geometry = this.axisGeometry(false, bodyPos, bodySize);
        if (geometry == null) {
            return;
        }
        this.drawScrollbarTrackAndThumb(poseStack, (float)geometry.trackX, (float)geometry.trackY, (float)geometry.trackWidth, (float)geometry.trackHeight, (float)geometry.thumbX, (float)geometry.thumbY, (float)geometry.thumbWidth, (float)geometry.thumbHeight);
    }

    private AxisGeometry axisGeometry(boolean vertical, Rect rect) {
        if (rect == null || (vertical ? !this.verticalScrollbarVisible : !this.horizontalScrollbarVisible)) {
            return null;
        }
        return this.axisGeometry(vertical, rect.getBodyRectPosition(), rect.getBodyRectSize());
    }

    private AxisGeometry axisGeometry(boolean vertical, Position bodyPos, Size bodySize) {
        double trackInset;
        double crossGutter;
        double scrollport = vertical ? this.getScrollportHeight() : this.getScrollportWidth();
        double trackSize = this.scrollbarTrackSize();
        double trackExtent = Math.max(0.0, (vertical ? bodySize.height() : bodySize.width()) - (crossGutter = vertical ? this.getHorizontalScrollbarGutter() : this.getVerticalScrollbarGutter()) - (trackInset = this.scrollbarTrackInset()) * 2.0);
        if (trackExtent <= 0.0) {
            return null;
        }
        double scrollExtent = vertical ? this.owner.scrollHeight : this.owner.scrollWidth;
        double thumbExtent = scrollExtent <= scrollport + 0.5 ? trackExtent : Math.max(this.scrollbarMinThumbLength(), trackExtent * (scrollport / Math.max(scrollport, scrollExtent)));
        thumbExtent = Math.min(trackExtent, thumbExtent);
        double maxTravel = Math.max(0.0, trackExtent - thumbExtent);
        double scrollLimit = Math.max(0.0, scrollExtent - scrollport);
        double scrollPos = vertical ? this.getScrollTop() : this.getScrollLeft();
        double thumbOffset = scrollLimit <= 0.0 ? 0.0 : Math.max(0.0, Math.min(scrollPos, scrollLimit)) / scrollLimit * maxTravel;
        double trackX = vertical ? bodyPos.x + bodySize.width() - trackSize - trackInset : bodyPos.x + trackInset;
        double trackY = vertical ? bodyPos.y + trackInset : bodyPos.y + bodySize.height() - trackSize - trackInset;
        double trackWidth = vertical ? trackSize : trackExtent;
        double trackHeight = vertical ? trackExtent : trackSize;
        double thumbX = vertical ? trackX : trackX + thumbOffset;
        double thumbY = vertical ? trackY + thumbOffset : trackY;
        double thumbWidth = vertical ? trackSize : thumbExtent;
        double thumbHeight = vertical ? thumbExtent : trackSize;
        double hitX = vertical ? bodyPos.x + bodySize.width() - this.scrollbarGutter() : bodyPos.x;
        double hitY = vertical ? bodyPos.y : bodyPos.y + bodySize.height() - this.scrollbarGutter();
        double hitWidth = vertical ? this.scrollbarGutter() : Math.max(0.0, bodySize.width() - this.getVerticalScrollbarGutter());
        double hitHeight = vertical ? Math.max(0.0, bodySize.height() - this.getHorizontalScrollbarGutter()) : this.scrollbarGutter();
        return new AxisGeometry(vertical, trackX, trackY, trackWidth, trackHeight, thumbX, thumbY, thumbWidth, thumbHeight, hitX, hitY, hitWidth, hitHeight);
    }

    private double scrollbarGutter() {
        return this.devicePixelsToDocumentPixels(8.0);
    }

    private double scrollbarTrackSize() {
        return this.devicePixelsToDocumentPixels(6.0);
    }

    private double scrollbarTrackInset() {
        return this.devicePixelsToDocumentPixels(1.0);
    }

    private double scrollbarMinThumbLength() {
        return this.devicePixelsToDocumentPixels(10.0);
    }

    private double devicePixelsToDocumentPixels(double devicePixels) {
        double scale;
        double d = scale = this.owner.document == null || this.owner.document.getViewport() == null ? 1.0 : this.owner.document.getViewport().scissorScale();
        if (!(scale > 0.0) || !Double.isFinite(scale)) {
            scale = 1.0;
        }
        return devicePixels / scale;
    }

    private static boolean contains(AxisGeometry geometry, double x, double y) {
        return geometry != null && x >= geometry.hitX && x <= geometry.hitX + geometry.hitWidth && y >= geometry.hitY && y <= geometry.hitY + geometry.hitHeight;
    }

    private void setScrollImmediate(boolean vertical, double value) {
        double beforeLeft = this.owner.getTargetScrollLeft();
        double beforeTop = this.owner.getTargetScrollTop();
        if (vertical) {
            double clamped;
            this.owner.scrollTop = clamped = this.clampScrollTarget(value, this.getVerticalScrollLimitFromMetrics());
            this.owner.targetScrollTop = clamped;
        } else {
            double clamped;
            this.owner.scrollLeft = clamped = this.clampScrollTarget(value, this.getHorizontalScrollLimitFromMetrics());
            this.owner.targetScrollLeft = clamped;
        }
        this.lastRenderStepNs = 0L;
        this.owner.getRenderer().invalidateScrollVersion();
        if (this.owner.document != null) {
            this.owner.document.markDirty(this.owner, 9);
        }
        this.owner.dispatchScrollEventIfChanged(beforeLeft, beforeTop);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawScrollbarTrackAndThumb(PoseStack poseStack, float trackX, float trackY, float trackWidth, float trackHeight, float thumbX, float thumbY, float thumbWidth, float thumbHeight) {
        float trackRadius = Math.min(trackWidth, trackHeight) / 2.0f;
        float thumbRadius = Math.min(thumbWidth, thumbHeight) / 2.0f;
        poseStack.m_85836_();
        try {
            Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), trackX, trackY, trackWidth, trackHeight, new float[]{trackRadius, trackRadius, trackRadius, trackRadius}, 414804625);
            Base.offsetPaintDepth(poseStack, 0.5f);
            Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), thumbX, thumbY, thumbWidth, thumbHeight, new float[]{thumbRadius, thumbRadius, thumbRadius, thumbRadius}, -1281384545);
        }
        finally {
            poseStack.m_85849_();
        }
    }

    private boolean isScrollSettled(double current, double target) {
        return Math.abs(current - target) <= 0.01;
    }

    private static enum DragAxis {
        NONE,
        VERTICAL,
        HORIZONTAL;

    }

    private record AxisGeometry(boolean vertical, double trackX, double trackY, double trackWidth, double trackHeight, double thumbX, double thumbY, double thumbWidth, double thumbHeight, double hitX, double hitY, double hitWidth, double hitHeight) {
        public double trackStart() {
            return this.vertical ? this.trackY : this.trackX;
        }

        public double trackLength() {
            return this.vertical ? this.trackHeight : this.trackWidth;
        }

        public double thumbStart() {
            return this.vertical ? this.thumbY : this.thumbX;
        }

        public double thumbLength() {
            return this.vertical ? this.thumbHeight : this.thumbWidth;
        }

        public double thumbEnd() {
            return this.thumbStart() + this.thumbLength();
        }
    }

    private record ScrollStep(double current, double target, boolean moving) {
    }
}

