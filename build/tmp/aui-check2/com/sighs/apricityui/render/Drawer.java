/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.BodyRenderNodeProvider;
import com.sighs.apricityui.render.ForegroundRenderNodeProvider;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.style.Animation;
import com.sighs.apricityui.style.Filter;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.style.Transform;
import com.sighs.apricityui.style.Transition;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

public class Drawer {
    public static final int REPAINT = 1;
    public static final int REORDER = 2;
    public static final int RELAYOUT = 4;
    public static final int HITTEST = 8;
    public static final int COMMIT_LAYOUT = 16;
    private static final Comparator<Paintable> PAINTABLE_ORDER = Comparator.comparingInt(Paintable::zValue).thenComparingDouble(Paintable::translateZ).thenComparingInt(Paintable::domOrder);

    public static void flushUpdates(Document document) {
        Set<Element> dirtyElements = document.getDirtyElements();
        if (dirtyElements.isEmpty()) {
            return;
        }
        List<Element> sortedDirty = Drawer.consolidateDirtyElements(dirtyElements);
        Set<Element> reorderRoots = Collections.newSetFromMap(new IdentityHashMap());
        for (Element e : sortedDirty) {
            Element contextRoot;
            if (e.hasDirtyFlag(4)) {
                e.forEachRoute(element -> element.getRenderer().invalidateLayoutVersion());
                e.forEachRoute(element -> element.getRenderer().size.clear());
                e.forEachRoute(element -> element.getRenderer().position.clear());
                e.addDirtyFlags(1);
            }
            if (!e.hasDirtyFlag(2) || (contextRoot = Drawer.findNearestStackingContext(e)) == null) continue;
            reorderRoots.add(contextRoot);
        }
        if (!reorderRoots.isEmpty()) {
            Drawer.updatePaintList(document, reorderRoots);
        }
        for (Element e : sortedDirty) {
            e.clearDirtyFlags();
        }
        dirtyElements.clear();
    }

    private static List<Element> consolidateDirtyElements(Set<Element> dirtyElements) {
        ArrayList<Element> list = new ArrayList<Element>(dirtyElements);
        list.sort(Comparator.comparingInt(Element::getDepth));
        return list;
    }

    public static ArrayList<RenderNode> createPaintList(Element body) {
        ArrayList<RenderNode> paintList = new ArrayList<RenderNode>();
        Drawer.processStackingContext(body, paintList);
        for (Element topLayer : Drawer.collectTopLayerRoots(body)) {
            Drawer.processStackingContext(topLayer, paintList);
        }
        return paintList;
    }

    private static List<Element> collectTopLayerRoots(Element root) {
        if (root == null) {
            return List.of();
        }
        ArrayList<Element> result = new ArrayList<Element>();
        Drawer.collectTopLayerRoots(root, result);
        return result;
    }

    private static void collectTopLayerRoots(Element parent, List<Element> result) {
        for (Element child : parent.getRenderChildren()) {
            if (child.isTopLayer()) {
                result.add(child);
            }
            Drawer.collectTopLayerRoots(child, result);
        }
    }

    private static void updatePaintList(Document document, Set<Element> reorderRoots) {
        ArrayList<RenderNode> globalList = document.getPaintList();
        Element paintRoot = Drawer.getDocumentPaintRoot(document);
        if (paintRoot == null) {
            globalList.clear();
            return;
        }
        globalList.removeIf(node -> {
            Element target = RenderNode.getRenderNodeTarget(node);
            return target != null && !target.isConnected();
        });
        List<Element> roots = Drawer.minimizeRoots(reorderRoots);
        boolean rebuildAll = globalList.isEmpty();
        for (Element root : roots) {
            if (root == null || !root.isConnected() || root == paintRoot) {
                rebuildAll = true;
                break;
            }
            ArrayList<RenderNode> rebuiltSubtree = Drawer.createPaintList(root);
            if (Drawer.updateGlobalPaintList(globalList, root, rebuiltSubtree)) continue;
            rebuildAll = true;
            break;
        }
        if (rebuildAll) {
            ArrayList<RenderNode> rebuilt = Drawer.createPaintList(paintRoot);
            globalList.clear();
            globalList.addAll(rebuilt);
        }
    }

    private static Element getDocumentPaintRoot(Document document) {
        if (document == null) {
            return null;
        }
        if (document.documentElement != null) {
            return document.documentElement;
        }
        return document.body;
    }

    private static void processStackingContext(Element contextRoot, List<RenderNode> paintList) {
        boolean splitContentForNegativeZ;
        boolean hasFilter;
        Filter.FilterState bfState;
        String backdropFilterStr;
        boolean hasClipPath;
        Style rootStyle = contextRoot.getRawComputedStyle();
        if ("none".equals(rootStyle.display)) {
            return;
        }
        boolean bl = hasClipPath = !"none".equals(rootStyle.clipPath);
        if (hasClipPath) {
            paintList.add(new RenderNode.ClipPathPushNode(contextRoot));
        }
        if ((backdropFilterStr = rootStyle.backdropFilter) != null && !backdropFilterStr.equals("none") && !(bfState = Filter.getBackdropFilterOf(contextRoot)).isEmpty()) {
            paintList.add(new RenderNode.BackdropFilterNode(contextRoot));
        }
        if (hasFilter = Drawer.hasCompositedFilter(contextRoot, rootStyle)) {
            paintList.add(new RenderNode.FilterPushNode(contextRoot));
        }
        paintList.add(new RenderNode.ElementPhaseNode(contextRoot, Base.RenderPhase.SHADOW));
        List<Element> children = contextRoot.getRenderChildren();
        if (children.isEmpty()) {
            boolean needsMask = Interaction.clipsOverflow(rootStyle);
            if (needsMask) {
                paintList.add(new RenderNode.ElementPhaseNode(contextRoot, Base.RenderPhase.BORDER));
                paintList.add(new RenderNode.MaskPushNode(contextRoot));
                Drawer.appendBodyRenderNodes(contextRoot, paintList);
                Drawer.appendForegroundRenderNodes(contextRoot, paintList);
                paintList.add(new RenderNode.MaskPopNode(contextRoot));
            } else {
                Drawer.appendBodyRenderNodes(contextRoot, paintList);
                paintList.add(new RenderNode.ElementPhaseNode(contextRoot, Base.RenderPhase.BORDER));
                Drawer.appendForegroundRenderNodes(contextRoot, paintList);
            }
            if (contextRoot.mayRenderScrollbar()) {
                paintList.add(new RenderNode.ScrollbarNode(contextRoot));
            }
            if (hasFilter) {
                paintList.add(new RenderNode.FilterPopNode(contextRoot));
            }
            if (hasClipPath) {
                paintList.add(new RenderNode.ClipPathPopNode(contextRoot));
            }
            return;
        }
        ArrayList<Paintable> negativeZ = new ArrayList<Paintable>();
        ArrayList<Element> normalFlow = new ArrayList<Element>();
        ArrayList<Paintable> autoOrZeroContext = new ArrayList<Paintable>();
        ArrayList<Paintable> positiveZ = new ArrayList<Paintable>();
        for (int i = 0; i < children.size(); ++i) {
            Element child = children.get(i);
            if (child.isTopLayer()) continue;
            Style style = child.getRawComputedStyle();
            if ("none".equals(style.display)) continue;
            String zIndexStr = style.zIndex;
            double translateZ = Transform.getTranslateZ(style.transform);
            boolean createsContext = Drawer.createsPaintStackingContext(child, style);
            if (!createsContext) {
                normalFlow.add(child);
                continue;
            }
            int zValue = "auto".equals(zIndexStr) ? 0 : Size.parse(zIndexStr);
            Paintable p = new Paintable(child, zValue, translateZ, i);
            if (zValue < 0) {
                negativeZ.add(p);
                continue;
            }
            if (zValue == 0) {
                autoOrZeroContext.add(p);
                continue;
            }
            positiveZ.add(p);
        }
        if (negativeZ.size() > 1) {
            negativeZ.sort(PAINTABLE_ORDER);
        }
        if (autoOrZeroContext.size() > 1) {
            autoOrZeroContext.sort(PAINTABLE_ORDER);
        }
        if (positiveZ.size() > 1) {
            positiveZ.sort(PAINTABLE_ORDER);
        }
        boolean needsMask = Interaction.clipsOverflow(rootStyle);
        boolean bl2 = splitContentForNegativeZ = !negativeZ.isEmpty();
        if (needsMask) {
            if (splitContentForNegativeZ) {
                paintList.add(new RenderNode.ElementBackgroundNode(contextRoot));
            }
            paintList.add(new RenderNode.ElementPhaseNode(contextRoot, Base.RenderPhase.BORDER));
            paintList.add(new RenderNode.MaskPushNode(contextRoot));
            if (!splitContentForNegativeZ) {
                Drawer.appendBodyRenderNodes(contextRoot, paintList);
            }
        } else {
            if (splitContentForNegativeZ) {
                paintList.add(new RenderNode.ElementBackgroundNode(contextRoot));
            } else {
                Drawer.appendBodyRenderNodes(contextRoot, paintList);
            }
            paintList.add(new RenderNode.ElementPhaseNode(contextRoot, Base.RenderPhase.BORDER));
        }
        for (Paintable p : negativeZ) {
            Drawer.processStackingContext(p.element, paintList);
        }
        if (splitContentForNegativeZ) {
            Drawer.appendContentRenderNodes(contextRoot, paintList);
        }
        for (Element e : normalFlow) {
            Drawer.processStackingContext(e, paintList);
        }
        for (Paintable p : autoOrZeroContext) {
            Drawer.processStackingContext(p.element, paintList);
        }
        for (Paintable p : positiveZ) {
            Drawer.processStackingContext(p.element, paintList);
        }
        Drawer.appendForegroundRenderNodes(contextRoot, paintList);
        if (needsMask) {
            paintList.add(new RenderNode.MaskPopNode(contextRoot));
        }
        if (contextRoot.mayRenderScrollbar()) {
            paintList.add(new RenderNode.ScrollbarNode(contextRoot));
        }
        if (hasFilter) {
            paintList.add(new RenderNode.FilterPopNode(contextRoot));
        }
        if (hasClipPath) {
            paintList.add(new RenderNode.ClipPathPopNode(contextRoot));
        }
    }

    private static void appendBodyRenderNodes(Element contextRoot, List<RenderNode> paintList) {
        BodyRenderNodeProvider provider;
        List<RenderNode> nodes;
        if (contextRoot instanceof BodyRenderNodeProvider && (nodes = (provider = (BodyRenderNodeProvider)((Object)contextRoot)).createBodyRenderNodes()) != null && !nodes.isEmpty()) {
            paintList.addAll(nodes);
            return;
        }
        paintList.add(new RenderNode.ElementPhaseNode(contextRoot, Base.RenderPhase.BODY));
    }

    private static void appendContentRenderNodes(Element contextRoot, List<RenderNode> paintList) {
        BodyRenderNodeProvider provider;
        List<RenderNode> nodes;
        if (contextRoot instanceof BodyRenderNodeProvider && (nodes = (provider = (BodyRenderNodeProvider)((Object)contextRoot)).createBodyRenderNodes()) != null && !nodes.isEmpty()) {
            for (RenderNode node : nodes) {
                RenderNode.ElementBackgroundNode background;
                if (node instanceof RenderNode.ElementBackgroundNode && (background = (RenderNode.ElementBackgroundNode)node).target() == contextRoot) continue;
                paintList.add(node);
            }
            return;
        }
        paintList.add(new RenderNode.ElementContentNode(contextRoot));
    }

    private static void appendForegroundRenderNodes(Element contextRoot, List<RenderNode> paintList) {
        if (!(contextRoot instanceof ForegroundRenderNodeProvider)) {
            return;
        }
        ForegroundRenderNodeProvider provider = (ForegroundRenderNodeProvider)((Object)contextRoot);
        List<RenderNode> nodes = provider.createForegroundRenderNodes();
        if (nodes != null && !nodes.isEmpty()) {
            paintList.addAll(nodes);
        }
    }

    private static List<Element> minimizeRoots(Set<Element> roots) {
        ArrayList<Element> list = new ArrayList<Element>(roots);
        list.sort(Comparator.comparingInt(Element::getDepth));
        ArrayList<Element> result = new ArrayList<Element>();
        for (Element candidate : list) {
            boolean covered = false;
            for (Element selected : result) {
                if (!RenderNode.isSameOrDescendant(candidate, selected)) continue;
                covered = true;
                break;
            }
            if (covered) continue;
            result.add(candidate);
        }
        return result;
    }

    private static Element findNearestStackingContext(Element e) {
        Element paintRoot = Drawer.getDocumentPaintRoot(e == null ? null : e.document);
        if (e == null) {
            return paintRoot;
        }
        Element current = e.parentElement;
        while (current != null) {
            if (current == paintRoot || Drawer.createsPaintStackingContext(current, current.getRawComputedStyle())) {
                return current;
            }
            current = current.parentElement;
        }
        return paintRoot;
    }

    private static boolean updateGlobalPaintList(List<RenderNode> globalList, Element root, List<RenderNode> newSubtree) {
        RenderNode node;
        int endIndex;
        int startIndex = -1;
        for (int i = 0; i < globalList.size(); ++i) {
            if (RenderNode.getRenderNodeTarget(globalList.get(i)) != root) continue;
            startIndex = i;
            break;
        }
        if (startIndex == -1) {
            return false;
        }
        for (endIndex = startIndex + 1; endIndex < globalList.size() && Drawer.isNodeRelatedTo(node = globalList.get(endIndex), root); ++endIndex) {
        }
        globalList.subList(startIndex, endIndex).clear();
        globalList.addAll(startIndex, newSubtree);
        return true;
    }

    private static boolean isNodeRelatedTo(RenderNode node, Element potentialParent) {
        Element target = RenderNode.getRenderNodeTarget(node);
        if (target != null) {
            return RenderNode.isSameOrDescendant(target, potentialParent);
        }
        return false;
    }

    private static boolean hasCompositedFilter(Element element, Style style) {
        if (!Filter.isDisabled(style.filter, style.opacity)) {
            return true;
        }
        if (Transition.affectsFilter(element)) {
            return true;
        }
        return Animation.affectsFilter(style);
    }

    private static boolean createsPaintStackingContext(Element element, Style style) {
        if (style == null) {
            return false;
        }
        String zIndex = style.zIndex == null ? "auto" : style.zIndex;
        String position = style.position == null ? "static" : style.position;
        boolean hasBackdrop = style.backdropFilter != null && !style.backdropFilter.equals("none");
        return !zIndex.equals("auto") || !position.equals("static") || Drawer.hasCompositedFilter(element, style) || hasBackdrop || Transform.createsStackingContext(style.transform);
    }

    private record Paintable(Element element, int zValue, double translateZ, int domOrder) {
    }
}

