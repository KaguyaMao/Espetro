/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.LayoutMeasureCache;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.render.RectFrameCache;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.render.TransformFrameCache;
import com.sighs.apricityui.style.Interaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import org.joml.Matrix4f;

public final class LayoutCommit {
    private LayoutCommit() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void commit(Document document) {
        if (document == null || !document.isActive()) {
            return;
        }
        ArrayList<RenderNode> paintList = document.getPaintList();
        if (paintList == null || paintList.isEmpty()) {
            return;
        }
        boolean firstLayout = document.markFirstLayoutCommitForTiming();
        long startedNs = firstLayout ? System.nanoTime() : 0L;
        Set<Element> visited = Collections.newSetFromMap(new IdentityHashMap());
        RectFrameCache.begin();
        TransformFrameCache.begin();
        RectFrameCache.disableCommittedFallback();
        TransformFrameCache.disableCommittedFallback();
        LayoutMeasureCache.begin();
        try {
            for (RenderNode node : paintList) {
                Element target = RenderNode.getRenderNodeTarget(node);
                if (target == null || target.document != document || !visited.add(target)) continue;
                LayoutCommit.commitElement(target);
            }
            for (Element element : visited) {
                if (!element.mayRenderScrollbar()) continue;
                element.commitScrollMetricsFromLayout();
            }
        }
        catch (Throwable throwable) {
            LayoutMeasureCache.end();
            TransformFrameCache.enableCommittedFallback();
            RectFrameCache.enableCommittedFallback();
            TransformFrameCache.end();
            RectFrameCache.end();
            if (firstLayout) {
                ApricityUI.LOGGER.info("[AUI Layout] first commit path={} generation={} elements={} total={}ms", new Object[]{document.getPath(), document.getRefreshGeneration(), visited.size(), (System.nanoTime() - startedNs) / 1000000L});
            }
            throw throwable;
        }
        LayoutMeasureCache.end();
        TransformFrameCache.enableCommittedFallback();
        RectFrameCache.enableCommittedFallback();
        TransformFrameCache.end();
        RectFrameCache.end();
        if (firstLayout) {
            ApricityUI.LOGGER.info("[AUI Layout] first commit path={} generation={} elements={} total={}ms", new Object[]{document.getPath(), document.getRefreshGeneration(), visited.size(), (System.nanoTime() - startedNs) / 1000000L});
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void commitTransforms(Document document, Set<Element> roots) {
        if (document == null || !document.isActive() || roots == null || roots.isEmpty()) {
            return;
        }
        ArrayList<RenderNode> paintList = document.getPaintList();
        if (paintList == null || paintList.isEmpty()) {
            return;
        }
        Set visited = Collections.newSetFromMap(new IdentityHashMap());
        RectFrameCache.begin();
        TransformFrameCache.begin();
        try {
            for (RenderNode node : paintList) {
                Element target = RenderNode.getRenderNodeTarget(node);
                if (target == null || target.document != document || !visited.add(target) || !LayoutCommit.isInTransformSubtree(target, roots)) continue;
                LayoutCommit.commitTransformElement(target);
            }
        }
        finally {
            TransformFrameCache.end();
            RectFrameCache.end();
        }
    }

    private static void commitElement(Element target) {
        block5: {
            RenderNode.ensureRendererLoaded(target);
            if (!Interaction.isDisplayed(target)) {
                return;
            }
            long rectDependency = target.getRenderer().rectDependency(target.document);
            if (!target.getRenderer().hasCommittedRect(rectDependency)) {
                Rect rect = Rect.createAndCache(target);
                rect.getVisualBounds();
                target.getRenderer().commitRect(rect, rectDependency);
            }
            long transformDependency = target.getRenderer().transformDependency(target.document);
            if (!target.getRenderer().hasCommittedWorldTransform(transformDependency)) {
                try {
                    Matrix4f matrix = Base.createAndCacheWorldTransform(target);
                    target.getRenderer().commitWorldTransform(matrix, transformDependency);
                }
                catch (NoClassDefFoundError unavailableRenderRuntime) {
                    if (LayoutCommit.isOptionalRenderDependency(unavailableRenderRuntime)) break block5;
                    throw unavailableRenderRuntime;
                }
            }
        }
    }

    private static void commitTransformElement(Element target) {
        block4: {
            RenderNode.ensureRendererLoaded(target);
            if (!Interaction.isDisplayed(target)) {
                return;
            }
            long transformDependency = target.getRenderer().transformDependency(target.document);
            if (target.getRenderer().hasCommittedWorldTransform(transformDependency)) {
                return;
            }
            try {
                Matrix4f matrix = Base.createAndCacheWorldTransform(target);
                target.getRenderer().commitWorldTransform(matrix, transformDependency);
            }
            catch (NoClassDefFoundError unavailableRenderRuntime) {
                if (LayoutCommit.isOptionalRenderDependency(unavailableRenderRuntime)) break block4;
                throw unavailableRenderRuntime;
            }
        }
    }

    private static boolean isInTransformSubtree(Element target, Set<Element> roots) {
        Element current = target;
        while (current != null) {
            if (roots.contains(current)) {
                return true;
            }
            current = current.parentElement;
        }
        return false;
    }

    private static boolean isOptionalRenderDependency(NoClassDefFoundError error) {
        String missing = error.getMessage();
        return missing != null && (missing.startsWith("org/joml/") || missing.startsWith("com/mojang/blaze3d/"));
    }
}

