/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  org.joml.Matrix4f
 *  org.joml.Matrix4fc
 *  org.joml.Quaternionf
 *  org.joml.Quaternionfc
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.LayoutMeasureCache;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.AABB;
import com.sighs.apricityui.render.DocumentLayerOrder;
import com.sighs.apricityui.render.FilterRenderer;
import com.sighs.apricityui.render.FontDrawer;
import com.sighs.apricityui.render.FrameTimingHud;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.GuiItemDepths;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.LayoutCommit;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.render.RectFrameCache;
import com.sighs.apricityui.render.RenderBatchStats;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.render.TransformFrameCache;
import com.sighs.apricityui.render.WorldPaintDepth;
import com.sighs.apricityui.spi.AuiRenderService;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.MeshBuilder;
import com.sighs.apricityui.style.StyleFrameCache;
import com.sighs.apricityui.style.Transform;
import com.sighs.apricityui.task.FrameScheduler;
import com.sighs.apricityui.viewport.ApricityViewport;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import net.minecraft.client.Minecraft;
import org.joml.Matrix4f;
import org.joml.Matrix4fc;
import org.joml.Quaternionf;
import org.joml.Quaternionfc;

public class Base {
    private static final float DEFAULT_DEPTH_STEP = 0.005f;
    private static final float GLOBAL_DOCUMENT_Z_OFFSET = 1.0f;
    private static final float GUI_ITEM_MODEL_Z_OFFSET = 150.0f;
    private static final float GUI_ITEM_DECORATION_Z_OFFSET = 200.0f;
    private static final float GUI_ITEM_FOREGROUND_Z_OFFSET = 200.125f;
    private static final float GUI_FLOATING_ITEM_MODEL_Z_OFFSET = 200.25f;
    private static final float GUI_FLOATING_ITEM_DECORATION_Z_OFFSET = 250.25f;
    private static final float FLAT_DOCUMENT_LAYER_STEP = 251.25f;
    private static final ArrayDeque<Float> DOCUMENT_Z_OFFSET_STACK = new ArrayDeque();
    private static final ArrayDeque<GuiItemZ> GUI_ITEM_Z_STACK = new ArrayDeque();
    private static float guiItemModelZ = 150.0f;
    private static float guiItemDecorationZ = 200.0f;
    private static final ArrayDeque<Float> DEPTH_STEP_STACK = new ArrayDeque();
    private static float depthStep = 0.005f;
    private static final ArrayDeque<Boolean> DEPTH_MODE_STACK = new ArrayDeque();
    private static final ArrayDeque<Float> DEPTH_CURSOR_STACK = new ArrayDeque();
    private static final ArrayDeque<Boolean> DEPTH_TEST_STACK = new ArrayDeque();
    private static boolean accumulateDepth = false;
    private static float depthCursor = 0.0f;
    private static boolean depthTestEnabled = true;
    private static float documentZOffset = 1.0f;
    private static MeshBuilder currentMesh;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void drawOverlayDocument(PoseStack poseStack, Document document) {
        if (document == null) {
            return;
        }
        try (Document.ContextScope ignored = Document.withContext(document);){
            ApricityViewport viewport = document.getViewport();
            Mask.resetDepth();
            poseStack.m_85836_();
            Mask.pushScissorScale(viewport.scissorScale());
            try {
                poseStack.m_85841_(viewport.renderScale(), viewport.renderScale(), 1.0f);
                Base.drawFlatDocumentInContext(poseStack, document, List.of());
            }
            finally {
                Mask.popScissorScale();
                poseStack.m_85849_();
            }
        }
    }

    public static void drawScreenDocument(PoseStack poseStack, Document document) {
        Base.drawScreenDocument(poseStack, document, List.of());
    }

    public static void drawScreenDocument(PoseStack poseStack, Document document, List<? extends RenderNode> overlayNodes) {
        if (document == null) {
            return;
        }
        try (Document.ContextScope ignored = Document.withContext(document);){
            Mask.resetDepth();
            Base.drawFlatDocumentInContext(poseStack, document, overlayNodes);
        }
    }

    public static void drawDocument(PoseStack poseStack, Document document) {
        if (document == null) {
            return;
        }
        try (Document.ContextScope ignored = Document.withContext(document);){
            Base.drawDocumentInContext(poseStack, document, List.of());
        }
    }

    public static void drawEmbeddedDocument(PoseStack poseStack, Document document, Document ownerDocument) {
        if (document == null) {
            return;
        }
        try (Document.ContextScope ignored = Document.withContext(document);){
            Base.drawFlatDocumentAtZInContext(poseStack, document, List.of(), Base.resolveEmbeddedDocumentBaseZ(ownerDocument));
        }
    }

    private static void drawFlatDocumentInContext(PoseStack poseStack, Document document, List<? extends RenderNode> overlayNodes) {
        Base.drawFlatDocumentAtZInContext(poseStack, document, overlayNodes, Base.resolveFlatDocumentBaseZ(document));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawFlatDocumentAtZInContext(PoseStack poseStack, Document document, List<? extends RenderNode> overlayNodes, float baseZ) {
        Base.pushDocumentZOffset(baseZ);
        Base.pushGuiItemZ(150.0f, 200.0f);
        try {
            Base.drawDocumentInContext(poseStack, document, overlayNodes);
        }
        finally {
            Base.popGuiItemZ();
            Base.popDocumentZOffset();
        }
    }

    static float resolveFlatDocumentBaseZ(Document document) {
        int layer = 0;
        for (Document candidate : DocumentLayerOrder.backToFront(Document.getAll())) {
            if (!Base.isFlatDocument(candidate)) continue;
            if (candidate == document) {
                return 1.0f + (float)layer * 251.25f;
            }
            ++layer;
        }
        return 1.0f;
    }

    static float resolveEmbeddedDocumentBaseZ(Document ownerDocument) {
        return Base.resolveFlatDocumentBaseZ(ownerDocument);
    }

    public static float getFlatOverlayZ() {
        int layerCount = 0;
        for (Document candidate : Document.getAll()) {
            if (!Base.isFlatDocument(candidate)) continue;
            ++layerCount;
        }
        return 1.0f + (float)layerCount * 251.25f;
    }

    private static boolean isFlatDocument(Document document) {
        return document != null && !document.inWorld && !document.isManuallyRendered();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawDocumentInContext(PoseStack poseStack, Document document, List<? extends RenderNode> overlayNodes) {
        long startNs = System.nanoTime();
        AuiRenderService.RenderStateScope renderState = AuiServices.render().pushFilterRenderState();
        if (renderState == null) {
            renderState = AuiRenderService.RenderStateScope.NOOP;
        }
        try {
            FrameScheduler.renderBegin();
            RenderBatchStats.beginDocument();
            RectFrameCache.begin();
            TransformFrameCache.begin();
            LayoutMeasureCache.begin();
            StyleFrameCache.begin();
            FilterRenderer.beginFrame();
            poseStack.m_85836_();
            FontDrawer.pushDocumentPixelScale(document.getViewport().scissorScale());
            try {
                boolean styleChanged = document.commitPendingStyleRecalcForRender();
                boolean styleNeedsGeometryCommit = false;
                if (styleChanged) {
                    styleNeedsGeometryCommit = document.commitRenderStateForMotion();
                } else if (document.hasPendingRenderState()) {
                    document.commitRenderState();
                }
                boolean motionNeedsGeometryCommit = document.stepMotionRender();
                boolean scrollChanged = document.stepScrollRender();
                if (styleNeedsGeometryCommit || scrollChanged) {
                    LayoutCommit.commit(document);
                    document.commitMotionHitTest();
                } else if (motionNeedsGeometryCommit) {
                    Set<Element> layoutRoots = document.drainMotionLayoutRoots();
                    Set<Element> geometryRoots = document.drainMotionGeometryRoots();
                    if (!layoutRoots.isEmpty()) {
                        LayoutCommit.commit(document);
                    } else if (!geometryRoots.isEmpty()) {
                        LayoutCommit.commitTransforms(document, geometryRoots);
                    } else {
                        LayoutCommit.commit(document);
                    }
                    document.commitMotionHitTest();
                }
                poseStack.m_252880_(0.0f, 0.0f, documentZOffset);
                Element skippedSubtree = null;
                Set enteredSubtrees = Collections.newSetFromMap(new IdentityHashMap());
                Element activeTopLayerRoot = null;
                try (TopLayerDepthScope topLayerDepthScope = null;){
                    for (RenderNode renderNode : document.getPaintList()) {
                        Element target = RenderNode.getRenderNodeTarget(renderNode);
                        if (skippedSubtree != null) {
                            if (target != null && RenderNode.isSameOrDescendant(target, skippedSubtree)) continue;
                            skippedSubtree = null;
                        }
                        if (target != null && enteredSubtrees.add(target) && Base.shouldSkipSubtree(target)) {
                            skippedSubtree = target;
                            continue;
                        }
                        Element topLayerRoot = Base.findTopLayerRoot(target);
                        if (topLayerRoot != activeTopLayerRoot) {
                            if (topLayerDepthScope != null) {
                                topLayerDepthScope.close();
                            }
                            topLayerDepthScope = null;
                            activeTopLayerRoot = topLayerRoot;
                            if (topLayerRoot != null) {
                                topLayerDepthScope = TopLayerDepthScope.open();
                            }
                        }
                        poseStack.m_85836_();
                        Base.resolvePaintOffset(poseStack, renderNode);
                        renderNode.render(poseStack);
                        poseStack.m_85849_();
                    }
                }
                if (topLayerDepthScope != null) {
                    topLayerDepthScope.close();
                    topLayerDepthScope = null;
                }
                Base.pushGuiItemZ(200.25f, 250.25f);
                try {
                    for (RenderNode renderNode : overlayNodes) {
                        if (renderNode == null) continue;
                        poseStack.m_85836_();
                        Base.resolvePaintOffset(poseStack, renderNode);
                        renderNode.render(poseStack);
                        poseStack.m_85849_();
                    }
                }
                finally {
                    Base.popGuiItemZ();
                }
            }
            finally {
                FontDrawer.popDocumentPixelScale();
                poseStack.m_85849_();
                StyleFrameCache.end();
                LayoutMeasureCache.end();
                TransformFrameCache.end();
                RectFrameCache.end();
                Base.commitDraws();
                FilterRenderer.endFrame();
                RenderBatchStats.endDocument();
                FrameTimingHud.record(System.nanoTime() - startNs);
            }
        }
        finally {
            renderState.close();
        }
    }

    private static Element findTopLayerRoot(Element target) {
        Element current = target;
        while (current != null) {
            if (current.isTopLayer()) {
                return current;
            }
            current = current.parentElement;
        }
        return null;
    }

    private static boolean shouldSkipSubtree(Element target) {
        if (target == null || target.document == null || target == target.document.documentElement || target == target.document.body) {
            return false;
        }
        if (RenderNode.shouldSkip(target)) {
            return true;
        }
        AABB currentClip = Mask.getCurrentClip();
        if (!currentClip.isValid()) {
            return false;
        }
        Rect cachedRect = RectFrameCache.get(target);
        if (cachedRect == null) {
            return false;
        }
        return !cachedRect.getVisualBounds().intersects(currentClip);
    }

    public static void commitDraws() {
        Graph.endBatch();
        ImageDrawer.flushBatch();
        AuiServices.render().flushSharedBuffers();
    }

    public static void beginRendering() {
        if (depthTestEnabled) {
            AuiServices.render().enableDepthTest();
            AuiServices.render().setDepthMask(true);
        } else {
            AuiServices.render().disableDepthTest();
            AuiServices.render().setDepthMask(false);
        }
        AuiServices.render().disableCull();
        AuiServices.render().enableBlend();
        AuiServices.render().setBlendFuncSeparate(770, 771, 1, 771);
        Base.setPositionColorShader();
    }

    public static void finishRendering() {
        AuiServices.render().enableCull();
        AuiServices.render().disableBlend();
    }

    public static MeshBuilder getMesh() {
        return currentMesh;
    }

    public static void setMesh(MeshBuilder mesh) {
        currentMesh = mesh;
    }

    public static void applyTransform(PoseStack poseStack, Element element) {
        Matrix4f matrix = Base.prepareWorldTransform(element);
        poseStack.m_85850_().m_252922_().mul((Matrix4fc)matrix);
    }

    public static Matrix4f prepareWorldTransform(Element element) {
        Matrix4f cached;
        Matrix4f matrix4f = cached = WorldPaintDepth.canReuseCommittedTransforms() ? TransformFrameCache.get(element) : TransformFrameCache.getFrame(element);
        if (cached != null) {
            return cached;
        }
        Matrix4f matrix = Base.computeWorldTransform(element);
        TransformFrameCache.put(element, matrix);
        return matrix;
    }

    public static Matrix4f createAndCacheWorldTransform(Element element) {
        Matrix4f matrix = Base.computeWorldTransform(element);
        TransformFrameCache.put(element, matrix);
        return matrix;
    }

    private static Matrix4f computeWorldTransform(Element element) {
        Element[] route = element.getRouteArray();
        int routeSize = route.length;
        Matrix4f matrix = new Matrix4f();
        for (int i = routeSize - 1; i >= 0; --i) {
            Element e = route[i];
            Rect rect = Rect.of(e);
            double posX = rect.position.x;
            double posY = rect.position.y;
            Box box = rect.box;
            Size size = rect.getShadowSize();
            double currentAbsX = posX + box.getMarginLeft();
            double currentAbsY = posY + box.getMarginTop();
            List<Transform> functions = Base.prepareTransform(e, size);
            if (functions.isEmpty()) continue;
            double w = size.width();
            double h = size.height();
            float[] origin = Base.resolveTransformOrigin(e.getComputedStyle().transformOrigin, w, h);
            float originX = origin[0];
            float originY = origin[1];
            for (Transform transform : functions) {
                if (transform instanceof Transform.Translate) {
                    Transform.Translate t = (Transform.Translate)transform;
                    matrix.translate((float)t.x(), (float)t.y(), WorldPaintDepth.effectiveTranslateZ(t.z()));
                    continue;
                }
                if (transform instanceof Transform.Rotate) {
                    Transform.Rotate r = (Transform.Rotate)transform;
                    matrix.translate((float)currentAbsX + originX, (float)currentAbsY + originY, 0.0f);
                    if (r.x() != 0.0) {
                        matrix.rotate((Quaternionfc)new Quaternionf().rotationX((float)Math.toRadians(r.x())));
                    }
                    if (r.y() != 0.0) {
                        matrix.rotate((Quaternionfc)new Quaternionf().rotationY((float)Math.toRadians(r.y())));
                    }
                    if (r.z() != 0.0) {
                        matrix.rotate((Quaternionfc)new Quaternionf().rotationZ((float)Math.toRadians(r.z())));
                    }
                    matrix.translate(-((float)currentAbsX + originX), -((float)currentAbsY + originY), 0.0f);
                    continue;
                }
                if (!(transform instanceof Transform.Scale)) continue;
                Transform.Scale s = (Transform.Scale)transform;
                matrix.translate((float)currentAbsX + originX, (float)currentAbsY + originY, 0.0f);
                matrix.scale((float)s.x(), (float)s.y(), 1.0f);
                matrix.translate(-((float)currentAbsX + originX), -((float)currentAbsY + originY), 0.0f);
            }
        }
        return matrix;
    }

    public static List<Transform> prepareTransform(Element element, Size size) {
        List<Transform> functions = element.getRenderer().transform.get();
        if (functions != null) {
            return functions;
        }
        String cssTransform = element.getComputedStyle().transform;
        functions = Transform.parse(cssTransform, size.width(), size.height());
        element.getRenderer().transform.set(functions);
        return functions;
    }

    private static float[] resolveTransformOrigin(String value, double width, double height) {
        if (value == null || value.isBlank() || "unset".equalsIgnoreCase(value)) {
            return new float[]{(float)(width / 2.0), (float)(height / 2.0)};
        }
        String[] raw = value.trim().toLowerCase(Locale.ROOT).split("\\s+");
        String xToken = "50%";
        String yToken = "50%";
        if (raw.length == 1) {
            if (Base.isVerticalOrigin(raw[0])) {
                yToken = raw[0];
            } else {
                xToken = raw[0];
            }
        } else {
            xToken = raw[0];
            yToken = raw[1];
            if (Base.isVerticalOrigin(xToken) && !Base.isVerticalOrigin(yToken)) {
                String tmp = xToken;
                xToken = yToken;
                yToken = tmp;
            }
        }
        return new float[]{(float)Base.resolveOriginToken(xToken, width, true), (float)Base.resolveOriginToken(yToken, height, false)};
    }

    private static boolean isVerticalOrigin(String token) {
        return "top".equals(token) || "bottom".equals(token);
    }

    private static double resolveOriginToken(String token, double basis, boolean horizontal) {
        if (token == null || token.isBlank()) {
            return basis / 2.0;
        }
        return switch (token) {
            case "left" -> {
                if (horizontal) {
                    yield 0.0;
                }
                yield basis / 2.0;
            }
            case "right" -> {
                if (horizontal) {
                    yield basis;
                }
                yield basis / 2.0;
            }
            case "top" -> {
                if (horizontal) {
                    yield basis / 2.0;
                }
                yield 0.0;
            }
            case "bottom" -> {
                if (horizontal) {
                    yield basis / 2.0;
                }
                yield basis;
            }
            case "center" -> basis / 2.0;
            default -> Size.resolveLength(token, basis, basis / 2.0);
        };
    }

    public static void resolveOffset(PoseStack poseStack) {
        if (accumulateDepth) {
            return;
        }
        poseStack.m_252880_(0.0f, 0.0f, depthStep);
    }

    private static void resolvePaintOffset(PoseStack poseStack, RenderNode node) {
        if (!accumulateDepth) {
            poseStack.m_252880_(0.0f, 0.0f, depthStep);
            return;
        }
        poseStack.m_252880_(0.0f, 0.0f, Base.advancePaintDepth(node));
    }

    private static float advancePaintDepth(RenderNode node) {
        depthCursor = WorldPaintDepth.advance(depthCursor, depthStep, node == null || node.advancesPaintDepth());
        return depthCursor;
    }

    public static void offsetPaintDepth(PoseStack poseStack, float fraction) {
        if (!accumulateDepth || poseStack == null || !Float.isFinite(fraction)) {
            return;
        }
        poseStack.m_252880_(0.0f, 0.0f, depthStep * fraction);
    }

    public static void offsetLocalPaintDepth(PoseStack poseStack, int zIndex) {
        if (poseStack == null || zIndex == 0) {
            return;
        }
        float fraction = Math.max(-0.45f, Math.min(0.45f, (float)zIndex / 1000.0f));
        poseStack.m_252880_(0.0f, 0.0f, depthStep * fraction);
    }

    public static void pushDepthStep(float step) {
        DEPTH_STEP_STACK.push(Float.valueOf(depthStep));
        depthStep = step;
    }

    public static void popDepthStep() {
        depthStep = !DEPTH_STEP_STACK.isEmpty() ? DEPTH_STEP_STACK.pop().floatValue() : 0.005f;
    }

    public static void pushDepthMode(boolean accumulate) {
        DEPTH_MODE_STACK.push(accumulateDepth);
        DEPTH_CURSOR_STACK.push(Float.valueOf(depthCursor));
        accumulateDepth = accumulate;
        depthCursor = 0.0f;
    }

    public static void pushDocumentZOffset(float offset) {
        DOCUMENT_Z_OFFSET_STACK.push(Float.valueOf(documentZOffset));
        documentZOffset = Float.isFinite(offset) ? offset : 1.0f;
    }

    public static void popDocumentZOffset() {
        documentZOffset = DOCUMENT_Z_OFFSET_STACK.isEmpty() ? 1.0f : DOCUMENT_Z_OFFSET_STACK.pop().floatValue();
    }

    public static void pushDepthTest(boolean enabled) {
        DEPTH_TEST_STACK.push(depthTestEnabled);
        depthTestEnabled = enabled;
    }

    public static void popDepthTest() {
        depthTestEnabled = DEPTH_TEST_STACK.isEmpty() ? true : DEPTH_TEST_STACK.pop();
    }

    public static boolean isDepthTestEnabled() {
        return depthTestEnabled;
    }

    public static float getGuiItemModelZ() {
        return guiItemModelZ;
    }

    public static float getGuiItemDecorationZ() {
        return guiItemDecorationZ;
    }

    public static float getGuiItemForegroundZ() {
        return GuiItemDepths.foregroundZ(guiItemDecorationZ, accumulateDepth);
    }

    public static void pushGuiItemZ(float modelZ, float decorationZ) {
        GUI_ITEM_Z_STACK.push(new GuiItemZ(guiItemModelZ, guiItemDecorationZ));
        guiItemModelZ = Float.isFinite(modelZ) ? modelZ : 150.0f;
        guiItemDecorationZ = Float.isFinite(decorationZ) ? decorationZ : 200.0f;
    }

    public static void popGuiItemZ() {
        GuiItemZ previous = GUI_ITEM_Z_STACK.poll();
        guiItemModelZ = previous == null ? 150.0f : previous.modelZ();
        guiItemDecorationZ = previous == null ? 200.0f : previous.decorationZ();
    }

    public static void popDepthMode() {
        accumulateDepth = !DEPTH_MODE_STACK.isEmpty() ? DEPTH_MODE_STACK.pop() : false;
        depthCursor = !DEPTH_CURSOR_STACK.isEmpty() ? DEPTH_CURSOR_STACK.pop().floatValue() : 0.0f;
    }

    public static void setProjectionMatrix(Matrix4f matrix) {
        AuiServices.render().setProjectionMatrix(matrix);
    }

    public static Matrix4f getProjectionMatrix() {
        return AuiServices.render().getProjectionMatrix();
    }

    public static void setShader(Object shader) {
        AuiServices.render().setShader(shader);
    }

    public static void setPositionColorShader() {
        AuiServices.render().setPositionColorShader();
    }

    public static void setShaderColor(float a, float r, float g, float b) {
        AuiServices.render().setShaderColor(a, r, g, b);
    }

    public static String getClipboardText() {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.f_91068_ == null) {
            return "";
        }
        String text = minecraft.f_91068_.m_90876_();
        return text == null ? "" : text;
    }

    public static void setClipboardText(String text) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.f_91068_ == null) {
            return;
        }
        minecraft.f_91068_.m_90911_(text == null ? "" : text);
    }

    private static final class TopLayerDepthScope {
        private final boolean previousDepthTest;
        private final boolean previousDepthMask;
        private boolean closed;

        private TopLayerDepthScope(boolean previousDepthTest, boolean previousDepthMask) {
            this.previousDepthTest = previousDepthTest;
            this.previousDepthMask = previousDepthMask;
        }

        private static TopLayerDepthScope open() {
            if (!Base.isDepthTestEnabled()) {
                return null;
            }
            Base.commitDraws();
            boolean previousDepthTest = AuiServices.render().isDepthTestEnabled();
            boolean previousDepthMask = AuiServices.render().isDepthMaskEnabled();
            Base.pushDepthTest(false);
            AuiServices.render().disableDepthTest();
            AuiServices.render().setDepthMask(false);
            return new TopLayerDepthScope(previousDepthTest, previousDepthMask);
        }

        private void close() {
            if (this.closed) {
                return;
            }
            this.closed = true;
            Base.commitDraws();
            Base.popDepthTest();
            if (this.previousDepthTest) {
                AuiServices.render().enableDepthTest();
            } else {
                AuiServices.render().disableDepthTest();
            }
            AuiServices.render().setDepthMask(this.previousDepthMask);
        }
    }

    private record GuiItemZ(float modelZ, float decorationZ) {
    }

    public static enum RenderPhase {
        SHADOW,
        BODY,
        BORDER;

    }
}

