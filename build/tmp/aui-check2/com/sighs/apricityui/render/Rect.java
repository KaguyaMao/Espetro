/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.dom.RenderElement;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.Gradient;
import com.sighs.apricityui.render.AABB;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.RectFrameCache;
import com.sighs.apricityui.render.WorldWindowRenderContext;
import com.sighs.apricityui.style.Background;

public class Rect {
    public Element element;
    public Position position;
    public Box box;
    public String documentPath;
    public Background background;
    private final Size elementSize;
    private AABB visualBounds;
    private Position bodyRectPosition;
    private Size bodyRectSize;
    private float[] bodyRadius;
    private Position shadowPosition;
    private Size shadowSize;
    private Position contentPosition;

    public Rect(Element element) {
        this.element = element;
        this.position = Position.forRender(element);
        this.box = Box.of(element);
        this.elementSize = this.box.elementSize();
        this.background = Background.of(element);
        this.documentPath = element.document.getPath();
    }

    public static Rect of(Element element) {
        long dependency;
        RenderElement renderer;
        Rect cached = RectFrameCache.get(element);
        if (cached != null) {
            return cached;
        }
        Rect result = Rect.createAndCache(element);
        if (element != null && !(renderer = element.getRenderer()).hasCommittedRect(dependency = renderer.rectDependency(element.document))) {
            renderer.commitRect(result, dependency);
        }
        return result;
    }

    public static Rect createAndCache(Element element) {
        Rect result = new Rect(element);
        RectFrameCache.put(element, result);
        return result;
    }

    public AABB getVisualBounds() {
        if (this.visualBounds != null) {
            return this.visualBounds;
        }
        double x = this.position.x + this.box.getMarginLeft();
        double y = this.position.y + this.box.getMarginTop();
        double w = this.elementSize.width();
        double h = this.elementSize.height();
        double minExtendX = 0.0;
        double minExtendY = 0.0;
        double maxExtendX = 0.0;
        double maxExtendY = 0.0;
        for (Box.Shadow shadow : this.box.shadows) {
            if (shadow.inset() || shadow.color().getValue() >>> 24 == 0) continue;
            double extent = shadow.size() + shadow.spread();
            minExtendX = Math.min(minExtendX, shadow.x() - extent);
            minExtendY = Math.min(minExtendY, shadow.y() - extent);
            maxExtendX = Math.max(maxExtendX, shadow.x() + extent);
            maxExtendY = Math.max(maxExtendY, shadow.y() + extent);
        }
        if (minExtendX != 0.0 || minExtendY != 0.0 || maxExtendX != 0.0 || maxExtendY != 0.0) {
            x += minExtendX;
            y += minExtendY;
            w += maxExtendX - minExtendX;
            h += maxExtendY - minExtendY;
        }
        this.visualBounds = new AABB((float)x, (float)y, (float)w, (float)h);
        return this.visualBounds;
    }

    private double getMinBorderSize() {
        return Math.min(Math.min(this.box.getBorderLeft(), this.box.getBorderTop()), Math.min(this.box.getBorderRight(), this.box.getBorderBottom()));
    }

    public void drawBorder(PoseStack poseStack) {
        Box.SideBorder topBorder = this.box.getBorderTopSide();
        Box.SideBorder rightBorder = this.box.getBorderRightSide();
        Box.SideBorder bottomBorder = this.box.getBorderBottomSide();
        Box.SideBorder leftBorder = this.box.getBorderLeftSide();
        float topW = (float)topBorder.size();
        float bottomW = (float)bottomBorder.size();
        float leftW = (float)leftBorder.size();
        float rightW = (float)rightBorder.size();
        if (topW <= 0.0f && bottomW <= 0.0f && leftW <= 0.0f && rightW <= 0.0f) {
            return;
        }
        Graph.beginBatch();
        int topC = topBorder.color().getValue();
        int bottomC = bottomBorder.color().getValue();
        int leftC = leftBorder.color().getValue();
        int rightC = rightBorder.color().getValue();
        double x = this.position.x + this.box.getMarginLeft();
        double y = this.position.y + this.box.getMarginTop();
        double w = this.elementSize.width();
        double h = this.elementSize.height();
        float[] radii = this.box.getCalculatedRadii((float)w, (float)h, 0.0f);
        float[] borders = new float[]{topW, rightW, bottomW, leftW};
        int[] colors = new int[]{topC, rightC, bottomC, leftC};
        Graph.drawComplexRoundedBorder(poseStack.m_85850_().m_252922_(), (float)x, (float)y, (float)w, (float)h, radii, borders, colors);
        if (this.box.borderImage != null && WorldWindowRenderContext.shouldRenderBackgroundDetails()) {
            if (this.box.borderImage.gradient != null) {
                Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), (float)x, (float)y, (float)w, (float)h, radii, this.box.borderImage.gradient);
            }
            Position p = this.position.add(new Position(this.box.getMarginLeft(), this.box.getMarginTop()));
            Size s = this.getShadowSize();
            String path = Loader.resolve(this.documentPath, this.box.borderImage.source);
            Base.commitDraws();
            ImageDrawer.drawNineSlice(poseStack, path, (int)p.x, (int)p.y, (int)s.width(), (int)s.height(), this.box.borderImage);
            return;
        }
    }

    public Position getBodyRectPosition() {
        if (this.bodyRectPosition != null) {
            return this.bodyRectPosition;
        }
        double x = this.position.x + this.box.getMarginLeft() + this.box.getBorderLeft();
        double y = this.position.y + this.box.getMarginTop() + this.box.getBorderTop();
        this.bodyRectPosition = new Position(x, y);
        return this.bodyRectPosition;
    }

    public Size getBodyRectSize() {
        if (this.bodyRectSize != null) {
            return this.bodyRectSize;
        }
        double width = this.elementSize.width() - this.box.getBorderHorizontal();
        double height = this.elementSize.height() - this.box.getBorderVertical();
        this.bodyRectSize = new Size(width, height);
        return this.bodyRectSize;
    }

    public Size getElementSize() {
        return this.elementSize;
    }

    public float[] getBodyRadius() {
        if (this.bodyRadius != null) {
            return this.bodyRadius;
        }
        Size s = this.getBodyRectSize();
        this.bodyRadius = this.box.getCalculatedRadii((float)s.width(), (float)s.height(), (float)this.getMinBorderSize());
        return this.bodyRadius;
    }

    public void drawBody(PoseStack poseStack) {
        this.drawBody(poseStack, this.getBodyRectSize());
        if (WorldWindowRenderContext.shouldRenderEffects()) {
            this.drawInsetShadow(poseStack);
        }
    }

    public void drawBody(PoseStack poseStack, Size s) {
        boolean layeredBackground;
        Position p = this.getBodyRectPosition();
        float[] radii = this.getBodyRadius();
        Background.Layer imageOnlyLayer = this.resolveImageOnlyLayer();
        if (imageOnlyLayer != null && WorldWindowRenderContext.shouldRenderBackgroundDetails()) {
            ImageDrawer.drawComplexBackground(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), imageOnlyLayer, this.element);
            return;
        }
        boolean bl = layeredBackground = this.background.getLayers().size() > 1;
        if (layeredBackground) {
            Graph.beginLayeredBatch();
        } else {
            Graph.beginBatch();
        }
        if (!this.background.color.equals("unset")) {
            Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), radii, new Color(this.background.color).getValue());
        }
        if (!WorldWindowRenderContext.shouldRenderBackgroundDetails()) {
            Graph.endBatch();
            return;
        }
        if (!this.background.getLayers().isEmpty()) {
            for (int i = this.background.getLayers().size() - 1; i >= 0; --i) {
                Background.Layer layer = this.background.getLayers().get(i);
                if (layer == null) continue;
                if (layer.gradient != null) {
                    this.drawGradientLayer(poseStack, p, s, radii, layer, layeredBackground);
                }
                if ("unset".equals(layer.imagePath)) continue;
                Graph.endBatch();
                ImageDrawer.drawComplexBackground(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), layer, this.element);
                if (layeredBackground) {
                    Graph.beginLayeredBatch();
                    continue;
                }
                Graph.beginBatch();
            }
            return;
        }
        if (this.background.gradient != null) {
            Background.Layer legacyLayer = new Background.Layer();
            legacyLayer.gradient = this.background.gradient;
            legacyLayer.repeat = this.background.repeat;
            legacyLayer.size = this.background.size;
            legacyLayer.position = this.background.position;
            this.drawGradientLayer(poseStack, p, s, radii, legacyLayer, false);
        }
        if (!this.background.imagePath.equals("unset")) {
            Graph.endBatch();
            ImageDrawer.drawComplexBackground(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), this.background, this.element);
            return;
        }
    }

    private Background.Layer resolveImageOnlyLayer() {
        if (!"unset".equals(this.background.color)) {
            return null;
        }
        if (this.background.getLayers().size() == 1) {
            Background.Layer layer = this.background.getLayers().get(0);
            if (layer != null && layer.gradient == null && !"unset".equals(layer.imagePath)) {
                return layer;
            }
            return null;
        }
        if (!this.background.getLayers().isEmpty() || this.background.gradient != null || "unset".equals(this.background.imagePath)) {
            return null;
        }
        Background.Layer layer = new Background.Layer();
        layer.imagePath = this.background.imagePath;
        layer.repeat = this.background.repeat;
        layer.size = this.background.size;
        layer.position = this.background.position;
        return layer;
    }

    private void drawGradientLayer(PoseStack poseStack, Position p, Size s, float[] radii, Background.Layer layer, boolean layered) {
        if (layer == null || layer.gradient == null) {
            return;
        }
        ImageDrawer.GradientTile tile = ImageDrawer.resolveGradientTile(layer, (float)s.width(), (float)s.height());
        if (!tile.repeats()) {
            float x = (float)p.x + tile.x();
            float y = (float)p.y + tile.y();
            Gradient scaled = layer.gradient.scaledTo(tile.width(), tile.height());
            if (!Graph.requiresStopGeometry(scaled)) {
                Graph.drawUnifiedRoundedRect(poseStack.m_85850_().m_252922_(), x, y, tile.width(), tile.height(), radii, scaled);
                return;
            }
            Graph.endBatch();
            Mask.pushMask(poseStack, x, y, tile.width(), tile.height(), radii);
            if (layered) {
                Graph.beginLayeredBatch();
            } else {
                Graph.beginBatch();
            }
            Graph.drawGradientRect(poseStack.m_85850_().m_252922_(), x, y, tile.width(), tile.height(), scaled);
            Graph.endBatch();
            Mask.popMask(poseStack, x, y, tile.width(), tile.height(), radii);
            if (layered) {
                Graph.beginLayeredBatch();
            } else {
                Graph.beginBatch();
            }
            return;
        }
        Graph.endBatch();
        Mask.pushMask(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), radii);
        if (layered) {
            Graph.beginLayeredBatch();
        } else {
            Graph.beginBatch();
        }
        Gradient scaled = layer.gradient.scaledTo(tile.width(), tile.height());
        for (float ix = tile.startX(); ix < tile.endX(); ix += tile.width()) {
            for (float iy = tile.startY(); iy < tile.endY(); iy += tile.height()) {
                boolean drawn = Graph.drawAxisAlignedHardStopGradientRect(poseStack.m_85850_().m_252922_(), (float)p.x + ix, (float)p.y + iy, tile.width(), tile.height(), scaled);
                if (!drawn) {
                    drawn = Graph.drawAxisAlignedStopGradientRect(poseStack.m_85850_().m_252922_(), (float)p.x + ix, (float)p.y + iy, tile.width(), tile.height(), scaled);
                }
                if (drawn) continue;
                Graph.drawGradientRect(poseStack.m_85850_().m_252922_(), (float)p.x + ix, (float)p.y + iy, tile.width(), tile.height(), scaled);
            }
        }
        Graph.endBatch();
        Mask.popMask(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), radii);
        if (layered) {
            Graph.beginLayeredBatch();
        } else {
            Graph.beginBatch();
        }
    }

    public Position getShadowPosition() {
        if (this.shadowPosition != null) {
            return this.shadowPosition;
        }
        double x = this.position.x + this.box.getMarginLeft() + this.box.shadow.x();
        double y = this.position.y + this.box.getMarginTop() + this.box.shadow.y();
        this.shadowPosition = new Position(x, y);
        return this.shadowPosition;
    }

    public Size getShadowSize() {
        if (this.shadowSize != null) {
            return this.shadowSize;
        }
        double width = this.elementSize.width();
        double height = this.elementSize.height();
        this.shadowSize = new Size(width, height);
        return this.shadowSize;
    }

    public void drawShadow(PoseStack poseStack) {
        boolean layered;
        if (this.box.shadows.isEmpty()) {
            return;
        }
        Size s = this.getShadowSize();
        long outerShadowCount = this.box.shadows.stream().filter(shadow -> !shadow.inset()).count();
        if (outerShadowCount == 0L) {
            return;
        }
        boolean bl = layered = outerShadowCount > 1L;
        if (layered) {
            Graph.beginLayeredBatch();
        } else {
            Graph.beginBatch();
        }
        double sourceX = this.position.x + this.box.getMarginLeft();
        double sourceY = this.position.y + this.box.getMarginTop();
        for (int i = this.box.shadows.size() - 1; i >= 0; --i) {
            Box.Shadow shadow2 = this.box.shadows.get(i);
            if (shadow2.inset() || shadow2.color().getValue() >>> 24 == 0) continue;
            double spread = shadow2.spread();
            double x = sourceX + shadow2.x() - spread;
            double y = sourceY + shadow2.y() - spread;
            double width = Math.max(0.0, s.width() + spread * 2.0);
            double height = Math.max(0.0, s.height() + spread * 2.0);
            if (width <= 0.0 || height <= 0.0) continue;
            if (shadow2.size() <= 0.0) {
                this.drawZeroBlurOuterShadow(poseStack, (float)sourceX, (float)sourceY, (float)s.width(), (float)s.height(), (float)x, (float)y, (float)width, (float)height, shadow2.color().getValue());
                continue;
            }
            float[] shadowRadii = this.box.getCalculatedRadii((float)width, (float)height, (float)(-spread));
            Graph.drawUnifiedShadow(poseStack.m_85850_().m_252922_(), (float)x, (float)y, (float)width, (float)height, shadowRadii, (float)shadow2.size(), shadow2.color().getValue(), Color.parse("#00000000"));
        }
        if (layered) {
            Graph.endBatch();
        }
    }

    private void drawInsetShadow(PoseStack poseStack) {
        if (this.box.shadows.stream().noneMatch(Box.Shadow::inset)) {
            return;
        }
        Position p = this.getBodyRectPosition();
        Size s = this.getBodyRectSize();
        float width = (float)Math.max(0.0, s.width());
        float height = (float)Math.max(0.0, s.height());
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }
        Graph.endBatch();
        Mask.pushMask(poseStack, (float)p.x, (float)p.y, width, height, this.getBodyRadius());
        Graph.beginLayeredBatch();
        for (int i = this.box.shadows.size() - 1; i >= 0; --i) {
            Box.Shadow shadow = this.box.shadows.get(i);
            if (!shadow.inset() || shadow.color().getValue() >>> 24 == 0) continue;
            this.drawInsetShadowLayer(poseStack, p, width, height, shadow);
        }
        Graph.endBatch();
        Mask.popMask(poseStack, (float)p.x, (float)p.y, width, height, this.getBodyRadius());
        Graph.beginBatch();
    }

    private void drawInsetShadowLayer(PoseStack poseStack, Position p, float width, float height, Box.Shadow shadow) {
        float middleTop;
        float middleBottom;
        double blurExtent = Math.max(0.0, shadow.size()) * 0.5;
        double spread = shadow.spread() + blurExtent;
        float top = (float)Math.min((double)height, Math.max(0.0, spread + shadow.y()));
        float bottom = (float)Math.min((double)(height - top), Math.max(0.0, spread - shadow.y()));
        float left = (float)Math.min((double)width, Math.max(0.0, spread + shadow.x()));
        float right = (float)Math.min((double)(width - left), Math.max(0.0, spread - shadow.x()));
        int color = shadow.color().getValue();
        float x0 = (float)p.x;
        float y0 = (float)p.y;
        float x1 = x0 + width;
        float y1 = y0 + height;
        if (top > 0.0f) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y0, x1, y0 + top, color);
        }
        if (bottom > 0.0f) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, y1 - bottom, x1, y1, color);
        }
        if ((middleBottom = y1 - bottom) <= (middleTop = y0 + top)) {
            return;
        }
        if (left > 0.0f) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x0, middleTop, x0 + left, middleBottom, color);
        }
        if (right > 0.0f) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x1 - right, middleTop, x1, middleBottom, color);
        }
    }

    private void drawZeroBlurOuterShadow(PoseStack poseStack, float sourceX, float sourceY, float sourceWidth, float sourceHeight, float shadowX, float shadowY, float shadowWidth, float shadowHeight, int color) {
        float shadowRight = shadowX + shadowWidth;
        float shadowBottom = shadowY + shadowHeight;
        float sourceRight = sourceX + sourceWidth;
        float sourceBottom = sourceY + sourceHeight;
        float ix0 = Math.max(shadowX, sourceX);
        float iy0 = Math.max(shadowY, sourceY);
        float ix1 = Math.min(shadowRight, sourceRight);
        float iy1 = Math.min(shadowBottom, sourceBottom);
        if (ix0 >= ix1 || iy0 >= iy1) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), shadowX, shadowY, shadowRight, shadowBottom, color);
            return;
        }
        if (shadowY < iy0) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), shadowX, shadowY, shadowRight, iy0, color);
        }
        if (iy1 < shadowBottom) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), shadowX, iy1, shadowRight, shadowBottom, color);
        }
        if (shadowX < ix0) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), shadowX, iy0, ix0, iy1, color);
        }
        if (ix1 < shadowRight) {
            Graph.drawFillRect(poseStack.m_85850_().m_252922_(), ix1, iy0, shadowRight, iy1, color);
        }
    }

    public Position getContentPosition() {
        if (this.contentPosition != null) {
            return this.contentPosition;
        }
        double x = this.position.x + this.box.getMarginLeft() + this.box.getBorderLeft() + this.box.getPaddingLeft();
        double y = this.position.y + this.box.getMarginTop() + this.box.getBorderTop() + this.box.getPaddingTop();
        this.contentPosition = new Position(x, y);
        return this.contentPosition;
    }
}

