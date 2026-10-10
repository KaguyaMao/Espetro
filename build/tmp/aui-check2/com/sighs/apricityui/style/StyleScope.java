/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.style;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.parser.Selector;
import com.sighs.apricityui.util.AuiLog;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Set;

public final class StyleScope {
    private final Document owner;
    private final IdentityHashMap<Element, RecalcMode> pendingRoots = new IdentityHashMap();
    private final Object pendingRootsLock = new Object();
    private volatile Selector.Index selectorIndex = null;

    public StyleScope(Document owner) {
        this.owner = owner;
    }

    public void requestRecalc(Element element) {
        this.requestRecalc(element, RecalcMode.SUBTREE);
    }

    public void requestPseudoRecalc(Element element, String pseudoName) {
        if (element == null || element.document != this.owner) {
            return;
        }
        RecalcMode mode = this.getSelectorIndex().pseudoCanAffectDescendants(pseudoName) ? RecalcMode.SUBTREE : RecalcMode.SELF;
        this.requestRecalc(element, mode);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void requestRecalc(Element element, RecalcMode mode) {
        if (element == null) {
            return;
        }
        if (element.document != this.owner) {
            return;
        }
        Object object = this.pendingRootsLock;
        synchronized (object) {
            RecalcMode previous = this.pendingRoots.get(element);
            if (previous == RecalcMode.SUBTREE || mode == null) {
                return;
            }
            this.pendingRoots.put(element, mode);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public boolean flushPendingUpdates() {
        ArrayList<Request> candidates;
        Object object = this.pendingRootsLock;
        synchronized (object) {
            if (this.pendingRoots.isEmpty()) {
                return false;
            }
            candidates = new ArrayList<Request>(this.pendingRoots.size());
            for (Map.Entry<Element, RecalcMode> entry : this.pendingRoots.entrySet()) {
                candidates.add(new Request(entry.getKey(), entry.getValue()));
            }
            this.pendingRoots.clear();
        }
        candidates.sort(Comparator.comparingInt(request -> request.element.getDepth()));
        Set<Element> selectedSubtreeRoots = Collections.newSetFromMap(new IdentityHashMap());
        ArrayList<Request> roots = new ArrayList<Request>();
        for (Request request2 : candidates) {
            RecalcMode mode;
            Element candidate = request2.element;
            if (candidate == null || candidate.document != this.owner) continue;
            RecalcMode recalcMode = mode = request2.mode == null ? RecalcMode.SUBTREE : request2.mode;
            if (StyleScope.isCoveredByAncestor(candidate, selectedSubtreeRoots)) continue;
            roots.add(new Request(candidate, mode));
            if (mode != RecalcMode.SUBTREE) continue;
            selectedSubtreeRoots.add(candidate);
        }
        for (Request request2 : roots) {
            Element root = request2.element;
            if (request2.mode == RecalcMode.SELF) {
                this.recomputeSelfAndMaybeDescendants(root);
                continue;
            }
            this.recomputeSubtree(root);
        }
        return true;
    }

    public void recomputeSubtree(Element root) {
        if (root == null || root.document != this.owner) {
            return;
        }
        ArrayDeque<Element> stack = new ArrayDeque<Element>();
        stack.push(root);
        while (!stack.isEmpty()) {
            Element current = (Element)stack.pop();
            if (current == null || current.document != this.owner) continue;
            this.recomputeStyle(current);
            ArrayList<Element> children = current.children;
            for (int i = children.size() - 1; i >= 0; --i) {
                Element child = (Element)children.get(i);
                if (child == null) continue;
                stack.push(child);
            }
        }
    }

    private void recomputeSelfAndMaybeDescendants(Element element) {
        if (element == null || element.document != this.owner) {
            return;
        }
        boolean descendantsAffected = this.recomputeStyle(element);
        if (!descendantsAffected) {
            return;
        }
        ArrayList<Element> children = element.children;
        for (int i = 0; i < children.size(); ++i) {
            this.recomputeSubtree((Element)children.get(i));
        }
    }

    public void invalidateSelectorIndex() {
        this.selectorIndex = null;
    }

    public void rebuildSelectorIndex() {
        this.selectorIndex = Selector.Index.build(this.owner.CSSCache);
    }

    public Selector.Index getSelectorIndex() {
        Selector.Index index = this.selectorIndex;
        if (index != null) {
            return index;
        }
        this.selectorIndex = index = Selector.Index.build(this.owner.CSSCache);
        return index;
    }

    private boolean recomputeStyle(Element element) {
        try {
            return element.recomputeStyleSelf();
        }
        catch (RuntimeException exception) {
            ApricityUI.LOGGER.error("[AUI CSS] computed style failed path={} element={}", new Object[]{this.owner == null ? "<unknown>" : AuiLog.source(this.owner.getPath()), AuiLog.element(element), exception});
            throw exception;
        }
    }

    private static boolean isCoveredByAncestor(Element element, Set<Element> selected) {
        Element current = element.parentElement;
        while (current != null) {
            if (selected.contains(current)) {
                return true;
            }
            current = current.parentElement;
        }
        return false;
    }

    private static enum RecalcMode {
        SELF,
        SUBTREE;

    }

    private record Request(Element element, RecalcMode mode) {
    }
}

