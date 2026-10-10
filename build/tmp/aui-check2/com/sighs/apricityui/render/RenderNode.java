/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.AABB;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.FilterRenderer;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.render.WorldWindowRenderContext;
import com.sighs.apricityui.spi.AuiItemRenderRequest;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.style.Filter;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Transform;
import java.util.Arrays;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.DoubleSupplier;
import java.util.function.IntSupplier;
import java.util.function.Supplier;

public interface RenderNode {
    public void render(PoseStack var1);

    default public boolean advancesPaintDepth() {
        return true;
    }

    public static void applyWithTransform(PoseStack poseStack, Element target, Consumer<Rect> action) {
        Base.applyTransform(poseStack, target);
        action.accept(Rect.of(target));
    }

    public static boolean shouldSkip(Element target) {
        return target == null || !target.isConnected() || !Interaction.isDisplayed(target) || !target.isVisible;
    }

    public static void ensureRendererLoaded(Element target) {
        if (target == null || target.isLoaded) {
            return;
        }
        target.resetRenderer();
        target.isLoaded = true;
    }

    public static Element getRenderNodeTarget(RenderNode node) {
        if (node instanceof Element) {
            Element e = (Element)((Object)node);
            return e;
        }
        if (node instanceof ElementPhaseNode) {
            ElementPhaseNode n = (ElementPhaseNode)node;
            return n.target();
        }
        if (node instanceof ElementBackgroundNode) {
            ElementBackgroundNode n = (ElementBackgroundNode)node;
            return n.target();
        }
        if (node instanceof ElementContentNode) {
            ElementContentNode n = (ElementContentNode)node;
            return n.target();
        }
        if (node instanceof ElementForegroundNode) {
            ElementForegroundNode n = (ElementForegroundNode)node;
            return n.target();
        }
        if (node instanceof ItemNode) {
            ItemNode n = (ItemNode)node;
            return n.target();
        }
        if (node instanceof MaskPushNode) {
            MaskPushNode n = (MaskPushNode)node;
            return n.target();
        }
        if (node instanceof MaskPopNode) {
            MaskPopNode n = (MaskPopNode)node;
            return n.target();
        }
        if (node instanceof ScrollbarNode) {
            ScrollbarNode n = (ScrollbarNode)node;
            return n.target();
        }
        if (node instanceof ClipPathPushNode) {
            ClipPathPushNode n = (ClipPathPushNode)node;
            return n.target();
        }
        if (node instanceof ClipPathPopNode) {
            ClipPathPopNode n = (ClipPathPopNode)node;
            return n.target();
        }
        if (node instanceof FilterPushNode) {
            FilterPushNode n = (FilterPushNode)node;
            return n.target();
        }
        if (node instanceof FilterPopNode) {
            FilterPopNode n = (FilterPopNode)node;
            return n.target();
        }
        if (node instanceof BackdropFilterNode) {
            BackdropFilterNode n = (BackdropFilterNode)node;
            return n.target();
        }
        return null;
    }

    public static boolean isSameOrDescendant(Element element, Element ancestor) {
        if (element == null || ancestor == null) {
            return false;
        }
        Element current = element;
        while (current != null) {
            if (current == ancestor) {
                return true;
            }
            current = current.parentElement;
        }
        return false;
    }

    private static boolean hasTransformedAncestor(Element target) {
        if (target == null) {
            return false;
        }
        for (Element element : target.getRouteArray()) {
            if (!Transform.affectsXY(element.getComputedStyle().transform)) continue;
            return true;
        }
        return false;
    }

    public record ElementPhaseNode(Element target, Base.RenderPhase phase) implements RenderNode
    {
        @Override
        public void render(PoseStack poseStack) {
            if (this.phase == Base.RenderPhase.SHADOW && !WorldWindowRenderContext.shouldRenderEffects()) {
                return;
            }
            RenderNode.ensureRendererLoaded(this.target);
            if (RenderNode.shouldSkip(this.target)) {
                return;
            }
            AABB currentClip = Mask.getCurrentClip();
            Rect rect = Rect.of(this.target);
            if (!currentClip.isValid() || !rect.getVisualBounds().intersects(currentClip)) {
                return;
            }
            Base.applyTransform(poseStack, this.target);
            AuiServices.render().enableBlend();
            AuiServices.render().setBlendFunc(770, 771);
            if (Boolean.getBoolean("apricityui.test.logRenderPhases") && ElementPhaseNode.shouldLogTarget(this.target)) {
                Position bodyPos = rect.getBodyRectPosition();
                Size bodySize = rect.getBodyRectSize();
                ApricityUI.LOGGER.info("[AUI Render] phase={} tag={} class={} pos={} body={}x{} visualBounds={} clip={}", new Object[]{this.phase, this.target.tagName, this.target.getClassNames(), rect.position, bodySize.width(), bodySize.height(), rect.getVisualBounds(), currentClip});
            }
            if (this.phase == Base.RenderPhase.BODY && !WorldWindowRenderContext.shouldRenderContent()) {
                this.target.drawBackgroundOnly(poseStack);
            } else {
                this.target.drawPhase(poseStack, this.phase);
            }
        }

        static boolean shouldLogTarget(Element target) {
            if (target == null) {
                return false;
            }
            if ("BODY".equalsIgnoreCase(target.tagName)) {
                return true;
            }
            if (target.getClassNames().contains("slot-card")) {
                return true;
            }
            if (target.getClassNames().contains("btn-apply")) {
                return true;
            }
            if ("slots-container".equals(target.id)) {
                return true;
            }
            return "MAIN".equalsIgnoreCase(target.tagName);
        }
    }

    public record ElementBackgroundNode(Element target) implements RenderNode
    {
        @Override
        public void render(PoseStack poseStack) {
            RenderNode.ensureRendererLoaded(this.target);
            if (RenderNode.shouldSkip(this.target)) {
                return;
            }
            AABB currentClip = Mask.getCurrentClip();
            Rect rect = Rect.of(this.target);
            if (!currentClip.isValid() || !rect.getVisualBounds().intersects(currentClip)) {
                return;
            }
            Base.applyTransform(poseStack, this.target);
            AuiServices.render().enableBlend();
            AuiServices.render().setBlendFunc(770, 771);
            this.target.drawBackgroundOnly(poseStack);
        }
    }

    public record ElementContentNode(Element target) implements RenderNode
    {
        @Override
        public void render(PoseStack poseStack) {
            if (!WorldWindowRenderContext.shouldRenderContent()) {
                return;
            }
            RenderNode.ensureRendererLoaded(this.target);
            if (RenderNode.shouldSkip(this.target)) {
                return;
            }
            AABB currentClip = Mask.getCurrentClip();
            Rect rect = Rect.of(this.target);
            if (!currentClip.isValid() || !rect.getVisualBounds().intersects(currentClip)) {
                return;
            }
            Base.applyTransform(poseStack, this.target);
            AuiServices.render().enableBlend();
            AuiServices.render().setBlendFunc(770, 771);
            this.target.drawContentOnly(poseStack);
        }
    }

    public record ElementForegroundNode(Element target, Consumer<PoseStack> painter) implements RenderNode
    {
        public ElementForegroundNode {
            painter = painter == null ? ignored -> {} : painter;
        }

        @Override
        public void render(PoseStack poseStack) {
            if (!WorldWindowRenderContext.shouldRenderContent()) {
                return;
            }
            RenderNode.ensureRendererLoaded(this.target);
            if (RenderNode.shouldSkip(this.target)) {
                return;
            }
            AABB currentClip = Mask.getCurrentClip();
            Rect rect = Rect.of(this.target);
            if (!currentClip.isValid() || !rect.getVisualBounds().intersects(currentClip)) {
                return;
            }
            Base.applyTransform(poseStack, this.target);
            AuiServices.render().enableBlend();
            AuiServices.render().setBlendFunc(770, 771);
            this.painter.accept(poseStack);
        }
    }

    public record ItemNode(Element target, Supplier<Object> stackSupplier, BooleanSupplier enabledSupplier, DoubleSupplier xSupplier, DoubleSupplier ySupplier, DoubleSupplier scaleSupplier, IntSupplier zIndexSupplier, boolean decorations, Supplier<String> overlayTextSupplier, DoubleSupplier decorationOffsetYSupplier, BooleanSupplier ghostSupplier) implements RenderNode
    {
        private static final float ICON_SCALE_EPSILON = 1.0E-4f;

        public ItemNode {
            stackSupplier = stackSupplier == null ? () -> null : stackSupplier;
            enabledSupplier = enabledSupplier == null ? () -> true : enabledSupplier;
            xSupplier = xSupplier == null ? () -> 0.0 : xSupplier;
            ySupplier = ySupplier == null ? () -> 0.0 : ySupplier;
            scaleSupplier = scaleSupplier == null ? () -> 1.0 : scaleSupplier;
            zIndexSupplier = zIndexSupplier == null ? () -> 0 : zIndexSupplier;
            overlayTextSupplier = overlayTextSupplier == null ? () -> null : overlayTextSupplier;
            decorationOffsetYSupplier = decorationOffsetYSupplier == null ? () -> 0.0 : decorationOffsetYSupplier;
            ghostSupplier = ghostSupplier == null ? () -> false : ghostSupplier;
        }

        public ItemNode(Element target, Supplier<Object> stackSupplier, BooleanSupplier enabledSupplier, DoubleSupplier scaleSupplier, IntSupplier zIndexSupplier, boolean decorations) {
            this(target, stackSupplier, enabledSupplier, scaleSupplier, zIndexSupplier, decorations, () -> null, () -> 0.0, () -> false);
        }

        public ItemNode(Element target, Supplier<Object> stackSupplier, BooleanSupplier enabledSupplier, DoubleSupplier scaleSupplier, IntSupplier zIndexSupplier, boolean decorations, Supplier<String> overlayTextSupplier, DoubleSupplier decorationOffsetYSupplier, BooleanSupplier ghostSupplier) {
            this(target, stackSupplier, enabledSupplier, () -> ItemNode.centeredBodyX(target), () -> ItemNode.centeredBodyY(target), scaleSupplier, zIndexSupplier, decorations, overlayTextSupplier, decorationOffsetYSupplier, ghostSupplier);
        }

        public ItemNode(Element target, Supplier<Object> stackSupplier, BooleanSupplier enabledSupplier, DoubleSupplier scaleSupplier, IntSupplier zIndexSupplier, boolean decorations, Supplier<String> overlayTextSupplier, DoubleSupplier decorationOffsetYSupplier) {
            this(target, stackSupplier, enabledSupplier, scaleSupplier, zIndexSupplier, decorations, overlayTextSupplier, decorationOffsetYSupplier, () -> false);
        }

        public static ItemNode positioned(Supplier<Object> stackSupplier, double x, double y, double scale, int zIndex, boolean decorations) {
            return ItemNode.positioned(stackSupplier, x, y, scale, zIndex, decorations, null, 0.0, false);
        }

        public static ItemNode positioned(Supplier<Object> stackSupplier, double x, double y, double scale, int zIndex, boolean decorations, String overlayText, double decorationOffsetY, boolean ghost) {
            return new ItemNode(null, stackSupplier, () -> true, () -> x, () -> y, () -> scale, () -> zIndex, decorations, () -> overlayText, () -> decorationOffsetY, () -> ghost);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        public void render(PoseStack poseStack) {
            boolean hasOverlayText;
            Object stack;
            if (!WorldWindowRenderContext.shouldRenderContent() || !this.enabledSupplier.getAsBoolean()) {
                return;
            }
            if (this.target != null) {
                RenderNode.ensureRendererLoaded(this.target);
                if (RenderNode.shouldSkip(this.target)) {
                    return;
                }
                AABB currentClip = Mask.getCurrentClip();
                Rect rect = Rect.of(this.target);
                if (!currentClip.isValid() || !rect.getVisualBounds().intersects(currentClip)) {
                    return;
                }
            }
            if ((stack = this.stackSupplier.get()) == null) {
                return;
            }
            String overlayText = this.overlayTextSupplier.get();
            boolean bl = hasOverlayText = this.decorations && overlayText != null && !overlayText.isBlank();
            if (AuiServices.items().isEmptyStack(stack) && !hasOverlayText) {
                return;
            }
            float drawX = ItemNode.finiteFloat(this.xSupplier.getAsDouble());
            float drawY = ItemNode.finiteFloat(this.ySupplier.getAsDouble());
            float iconScale = Math.max(0.01f, ItemNode.finiteFloat(this.scaleSupplier.getAsDouble(), 1.0f));
            Base.commitDraws();
            poseStack.m_85836_();
            try {
                if (this.target != null) {
                    Base.applyTransform(poseStack, this.target);
                }
                poseStack.m_252880_(drawX, drawY, 0.0f);
                Base.offsetLocalPaintDepth(poseStack, this.zIndexSupplier.getAsInt());
                if (Math.abs(iconScale - 1.0f) > 1.0E-4f) {
                    poseStack.m_252880_(8.0f, 8.0f, 0.0f);
                    poseStack.m_85841_(iconScale, iconScale, 1.0f);
                    poseStack.m_252880_(-8.0f, -8.0f, 0.0f);
                }
                int seed = this.target == null ? 31 * Float.floatToIntBits(drawX) + Float.floatToIntBits(drawY) : System.identityHashCode(this.target);
                AuiServices.items().render(new AuiItemRenderRequest(poseStack, stack, seed, this.decorations, overlayText, ItemNode.finiteFloat(this.decorationOffsetYSupplier.getAsDouble()), this.ghostSupplier.getAsBoolean()));
            }
            finally {
                poseStack.m_85849_();
            }
        }

        private static double centeredBodyX(Element target) {
            if (target == null) {
                return 0.0;
            }
            Rect rect = Rect.of(target);
            Position body = rect.getBodyRectPosition();
            return body.x + (Math.max(1.0, rect.getBodyRectSize().width()) - 16.0) / 2.0;
        }

        private static double centeredBodyY(Element target) {
            if (target == null) {
                return 0.0;
            }
            Rect rect = Rect.of(target);
            Position body = rect.getBodyRectPosition();
            return body.y + (Math.max(1.0, rect.getBodyRectSize().height()) - 16.0) / 2.0;
        }

        private static float finiteFloat(double value) {
            return ItemNode.finiteFloat(value, 0.0f);
        }

        private static float finiteFloat(double value, float fallback) {
            if (!Double.isFinite(value)) {
                return fallback;
            }
            float converted = (float)value;
            return Float.isFinite(converted) ? converted : fallback;
        }
    }

    public record MaskPushNode(Element target) implements RenderNode
    {
        @Override
        public boolean advancesPaintDepth() {
            return false;
        }

        @Override
        public void render(PoseStack poseStack) {
            RenderNode.applyWithTransform(poseStack, this.target, rect -> {
                Position p = rect.getBodyRectPosition();
                Size bodySize = rect.getBodyRectSize();
                Size s = new Size(Math.max(0.0, bodySize.width() - this.target.getVerticalScrollbarGutter()), Math.max(0.0, bodySize.height() - this.target.getHorizontalScrollbarGutter()));
                if (Boolean.getBoolean("apricityui.test.logRenderPhases") && ElementPhaseNode.shouldLogTarget(this.target)) {
                    ApricityUI.LOGGER.info("[AUI Mask] push tag={} class={} bodyPos={} size={}x{} radius={} clipBefore={}", new Object[]{this.target.tagName, this.target.getClassNames(), p, s.width(), s.height(), Arrays.toString(rect.getBodyRadius()), Mask.getCurrentClip()});
                }
                Mask.pushMask(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), rect.getBodyRadius(), RenderNode.hasTransformedAncestor(this.target));
            });
        }
    }

    public record MaskPopNode(Element target) implements RenderNode
    {
        @Override
        public boolean advancesPaintDepth() {
            return false;
        }

        @Override
        public void render(PoseStack poseStack) {
            RenderNode.applyWithTransform(poseStack, this.target, rect -> {
                Position p = rect.getBodyRectPosition();
                Size bodySize = rect.getBodyRectSize();
                Size s = new Size(Math.max(0.0, bodySize.width() - this.target.getVerticalScrollbarGutter()), Math.max(0.0, bodySize.height() - this.target.getHorizontalScrollbarGutter()));
                if (Boolean.getBoolean("apricityui.test.logRenderPhases") && ElementPhaseNode.shouldLogTarget(this.target)) {
                    ApricityUI.LOGGER.info("[AUI Mask] pop tag={} class={} bodyPos={} size={}x{} clipBefore={}", new Object[]{this.target.tagName, this.target.getClassNames(), p, s.width(), s.height(), Mask.getCurrentClip()});
                }
                Mask.popMask(poseStack, (float)p.x, (float)p.y, (float)s.width(), (float)s.height(), rect.getBodyRadius());
            });
        }
    }

    public record ScrollbarNode(Element target) implements RenderNode
    {
        @Override
        public void render(PoseStack poseStack) {
            if (!WorldWindowRenderContext.shouldRenderContent()) {
                return;
            }
            RenderNode.ensureRendererLoaded(this.target);
            if (RenderNode.shouldSkip(this.target) || !this.target.mayRenderScrollbar()) {
                return;
            }
            Base.applyTransform(poseStack, this.target);
            AuiServices.render().enableBlend();
            AuiServices.render().setBlendFunc(770, 771);
            this.target.drawScrollbar(poseStack, Rect.of(this.target));
        }
    }

    public record ClipPathPushNode(Element target) implements RenderNode
    {
        @Override
        public boolean advancesPaintDepth() {
            return false;
        }

        @Override
        public void render(PoseStack poseStack) {
            String clip = this.target.getComputedStyle().clipPath;
            if (!WorldWindowRenderContext.shouldRenderEffects() || clip == null || clip.equals("none")) {
                return;
            }
            RenderNode.applyWithTransform(poseStack, this.target, rect -> {
                Position p = rect.getBodyRectPosition();
                Size s = rect.getBodyRectSize();
                float x = (float)(p.x - rect.box.getBorderLeft());
                float y = (float)(p.y - rect.box.getBorderTop());
                float w = (float)(s.width() + rect.box.getBorderHorizontal());
                float h = (float)(s.height() + rect.box.getBorderVertical());
                Mask.pushClipPath(poseStack, x, y, w, h, clip, RenderNode.hasTransformedAncestor(this.target));
            });
        }
    }

    public record ClipPathPopNode(Element target) implements RenderNode
    {
        @Override
        public boolean advancesPaintDepth() {
            return false;
        }

        @Override
        public void render(PoseStack poseStack) {
            String clip = this.target.getComputedStyle().clipPath;
            if (!WorldWindowRenderContext.shouldRenderEffects() || clip == null || clip.equals("none")) {
                return;
            }
            RenderNode.applyWithTransform(poseStack, this.target, rect -> {
                Position p = rect.getBodyRectPosition();
                Size s = rect.getBodyRectSize();
                float x = (float)(p.x - rect.box.getBorderLeft());
                float y = (float)(p.y - rect.box.getBorderTop());
                float w = (float)(s.width() + rect.box.getBorderHorizontal());
                float h = (float)(s.height() + rect.box.getBorderVertical());
                Mask.popClipPath(poseStack, x, y, w, h, clip);
            });
        }
    }

    public record FilterPushNode(Element target) implements RenderNode
    {
        @Override
        public boolean advancesPaintDepth() {
            return false;
        }

        @Override
        public void render(PoseStack poseStack) {
            if (!WorldWindowRenderContext.shouldRenderEffects()) {
                return;
            }
            if (!Filter.isDisabled(this.target)) {
                FilterRenderer.pushFilter();
            }
        }
    }

    public record FilterPopNode(Element target) implements RenderNode
    {
        @Override
        public void render(PoseStack poseStack) {
            if (!WorldWindowRenderContext.shouldRenderEffects()) {
                return;
            }
            if (!Filter.isDisabled(this.target)) {
                FilterRenderer.popFilter(Filter.getFilterOf(this.target));
            }
        }
    }

    public record BackdropFilterNode(Element target) implements RenderNode
    {
        @Override
        public void render(PoseStack poseStack) {
            if (!WorldWindowRenderContext.shouldRenderEffects()) {
                return;
            }
            if (RenderNode.shouldSkip(this.target)) {
                return;
            }
            AABB clip = Mask.getCurrentClip();
            if (clip.isValid() && Rect.of(this.target).getVisualBounds().intersects(clip)) {
                FilterRenderer.renderBackdrop(this.target, poseStack);
            }
        }
    }
}

