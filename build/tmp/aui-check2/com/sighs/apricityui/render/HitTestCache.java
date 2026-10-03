/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.style.Interaction;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class HitTestCache {
    private final Document owner;
    private final ArrayList<Entry> entries = new ArrayList();
    private boolean dirty = true;

    public HitTestCache(Document owner) {
        this.owner = owner;
    }

    public void markDirty() {
        this.dirty = true;
    }

    public void clear() {
        this.entries.clear();
        this.dirty = true;
    }

    public void rebuild(List<RenderNode> paintOrder) {
        this.entries.clear();
        this.dirty = false;
        if (this.owner == null || this.owner.body == null || paintOrder == null || paintOrder.isEmpty()) {
            return;
        }
        ArrayDeque<Element> clipStack = new ArrayDeque<Element>();
        IdentityHashMap<Element, Bounds> boundsCache = new IdentityHashMap<Element, Bounds>();
        Set seenElements = Collections.newSetFromMap(new IdentityHashMap());
        for (int i = paintOrder.size() - 1; i >= 0; --i) {
            Bounds clip;
            Bounds bounds;
            RenderNode.ElementPhaseNode phaseNode;
            Element element;
            RenderNode node = paintOrder.get(i);
            if (node instanceof RenderNode.ScrollbarNode) {
                RenderNode.ScrollbarNode scrollbarNode = (RenderNode.ScrollbarNode)node;
                HitTestCache.appendScrollbarEntries(scrollbarNode.target(), clipStack, boundsCache, this.entries);
                continue;
            }
            if (node instanceof RenderNode.MaskPopNode) {
                RenderNode.MaskPopNode popNode = (RenderNode.MaskPopNode)node;
                Element target = popNode.target();
                if (target == null) continue;
                clipStack.push(target);
                continue;
            }
            if (node instanceof RenderNode.MaskPushNode) {
                RenderNode.MaskPushNode pushNode = (RenderNode.MaskPushNode)node;
                if (clipStack.isEmpty() || clipStack.peek() != pushNode.target()) continue;
                clipStack.pop();
                continue;
            }
            if (!(node instanceof RenderNode.ElementPhaseNode) || (element = (phaseNode = (RenderNode.ElementPhaseNode)node).target()) == null || element.document != this.owner || !seenElements.add(element) || !Interaction.isDisplayed(element) || !element.isVisible || !element.isPointerEnabled || !(bounds = HitTestCache.resolveCommittedBounds(element, boundsCache)).isValid() || (clip = HitTestCache.resolveCommittedClipBounds(clipStack, boundsCache)) != null && clip.isEmpty()) continue;
            this.entries.add(new Entry(element, bounds, clip));
        }
    }

    public void updateSubtrees(List<RenderNode> paintOrder, Set<Element> dirtyRoots) {
        if (this.dirty || dirtyRoots == null || dirtyRoots.isEmpty()) {
            if (this.dirty) {
                this.rebuild(paintOrder);
            }
            return;
        }
        if (paintOrder == null || paintOrder.isEmpty()) {
            this.clear();
            return;
        }
        Set<Element> roots = HitTestCache.minimizeRoots(dirtyRoots);
        if (roots.isEmpty()) {
            return;
        }
        this.removeEntriesForRoots(roots);
        ArrayDeque<Element> clipStack = new ArrayDeque<Element>();
        IdentityHashMap<Element, Bounds> boundsCache = new IdentityHashMap<Element, Bounds>();
        Set seenElements = Collections.newSetFromMap(new IdentityHashMap());
        ArrayList<Entry> rebuilt = new ArrayList<Entry>();
        for (int i = paintOrder.size() - 1; i >= 0; --i) {
            Bounds clip;
            Bounds bounds;
            RenderNode.ElementPhaseNode phaseNode;
            Element element;
            Element target;
            RenderNode node = paintOrder.get(i);
            if (node instanceof RenderNode.ScrollbarNode) {
                RenderNode.ScrollbarNode scrollbarNode = (RenderNode.ScrollbarNode)node;
                target = scrollbarNode.target();
                if (!HitTestCache.isInAnyRoot(target, roots)) continue;
                HitTestCache.appendScrollbarEntries(target, clipStack, boundsCache, rebuilt);
                continue;
            }
            if (node instanceof RenderNode.MaskPopNode) {
                RenderNode.MaskPopNode popNode = (RenderNode.MaskPopNode)node;
                target = popNode.target();
                if (target == null) continue;
                clipStack.push(target);
                continue;
            }
            if (node instanceof RenderNode.MaskPushNode) {
                RenderNode.MaskPushNode pushNode = (RenderNode.MaskPushNode)node;
                if (clipStack.isEmpty() || clipStack.peek() != pushNode.target()) continue;
                clipStack.pop();
                continue;
            }
            if (!(node instanceof RenderNode.ElementPhaseNode) || (element = (phaseNode = (RenderNode.ElementPhaseNode)node).target()) == null || element.document != this.owner || !HitTestCache.isInAnyRoot(element, roots) || !seenElements.add(element) || !Interaction.isDisplayed(element) || !element.isVisible || !element.isPointerEnabled || !(bounds = HitTestCache.resolveCommittedBounds(element, boundsCache)).isValid() || (clip = HitTestCache.resolveCommittedClipBounds(clipStack, boundsCache)) != null && clip.isEmpty()) continue;
            rebuilt.add(new Entry(element, bounds, clip));
        }
        if (rebuilt.isEmpty()) {
            return;
        }
        int insertIndex = this.entries.size();
        for (int i = 0; i < this.entries.size(); ++i) {
            if (!HitTestCache.comesBeforeInPaintOrder(((Entry)rebuilt.get((int)0)).element, this.entries.get((int)i).element, paintOrder)) continue;
            insertIndex = i;
            break;
        }
        this.entries.addAll(insertIndex, rebuilt);
    }

    public Element hitTest(Position cursorPosition, List<RenderNode> paintOrder) {
        if (cursorPosition == null) {
            return null;
        }
        if (this.dirty) {
            this.rebuild(paintOrder);
        }
        for (Entry entry : this.entries) {
            if (!entry.bounds.contains(cursorPosition) || entry.clip != null && !entry.clip.contains(cursorPosition)) continue;
            return entry.element;
        }
        return null;
    }

    private static Bounds resolveCommittedBounds(Element element, Map<Element, Bounds> boundsCache) {
        Bounds bounds;
        if (element == null) {
            return Bounds.EMPTY;
        }
        Bounds cached = boundsCache.get(element);
        if (cached != null) {
            return cached;
        }
        Rect rect = element.getRenderer().getCommittedRect();
        if (rect == null) {
            return Bounds.EMPTY;
        }
        if ("IMG".equals(element.tagName)) {
            Position position = rect.getBodyRectPosition();
            Size size = rect.getBodyRectSize();
            bounds = new Bounds(position.x, position.y, size.width(), size.height());
        } else {
            Position position = rect.position;
            Box box = rect.box;
            Size size = rect.getElementSize();
            bounds = new Bounds(position.x + box.getMarginLeft(), position.y + box.getMarginTop(), size.width(), size.height());
        }
        boundsCache.put(element, bounds);
        return bounds;
    }

    private static Bounds resolveCommittedClipBounds(ArrayDeque<Element> clipStack, Map<Element, Bounds> boundsCache) {
        if (clipStack.isEmpty()) {
            return null;
        }
        Bounds effective = null;
        for (Element clip : clipStack) {
            Rect rect = clip.getRenderer().getCommittedRect();
            if (rect == null) continue;
            Position position = rect.getBodyRectPosition();
            Size size = rect.getBodyRectSize();
            Bounds clipBounds = new Bounds(position.x, position.y, Math.max(0.0, size.width() - clip.getVerticalScrollbarGutter()), Math.max(0.0, size.height() - clip.getHorizontalScrollbarGutter()));
            if (clipBounds.isEmpty()) {
                return Bounds.EMPTY;
            }
            if (!(effective = effective == null ? clipBounds : effective.intersection(clipBounds)).isEmpty()) continue;
            return Bounds.EMPTY;
        }
        return effective;
    }

    private static void appendScrollbarEntries(Element element, ArrayDeque<Element> clipStack, Map<Element, Bounds> boundsCache, List<Entry> output) {
        Bounds bounds;
        if (element == null || output == null || !element.isPointerEnabled || !element.isVisible) {
            return;
        }
        Rect rect = element.getRenderer().getCommittedRect();
        if (rect == null) {
            return;
        }
        Position position = rect.getBodyRectPosition();
        Size size = rect.getBodyRectSize();
        double vertical = element.getVerticalScrollbarGutter();
        double horizontal = element.getHorizontalScrollbarGutter();
        Bounds clip = HitTestCache.resolveCommittedClipBounds(clipStack, boundsCache);
        if (clip != null && clip.isEmpty()) {
            return;
        }
        if (vertical > 0.0 && (bounds = new Bounds(position.x + size.width() - vertical, position.y, vertical, Math.max(0.0, size.height() - horizontal))).isValid()) {
            output.add(new Entry(element, bounds, clip));
        }
        if (horizontal > 0.0 && (bounds = new Bounds(position.x, position.y + size.height() - horizontal, Math.max(0.0, size.width() - vertical), horizontal)).isValid()) {
            output.add(new Entry(element, bounds, clip));
        }
    }

    private void removeEntriesForRoots(Set<Element> roots) {
        Iterator<Entry> iterator = this.entries.iterator();
        while (iterator.hasNext()) {
            Entry entry = iterator.next();
            if (!HitTestCache.isInAnyRoot(entry.element, roots)) continue;
            iterator.remove();
        }
    }

    private static Set<Element> minimizeRoots(Set<Element> roots) {
        ArrayList<Element> sorted = new ArrayList<Element>(roots);
        sorted.sort(Comparator.comparingInt(Element::getDepth));
        Set<Element> result = Collections.newSetFromMap(new IdentityHashMap());
        for (Element root : sorted) {
            if (root == null || !root.isConnected()) continue;
            boolean covered = false;
            for (Element selected : result) {
                if (!RenderNode.isSameOrDescendant(root, selected)) continue;
                covered = true;
                break;
            }
            if (covered) continue;
            result.add(root);
        }
        return result;
    }

    private static boolean isInAnyRoot(Element element, Set<Element> roots) {
        if (element == null || roots == null || roots.isEmpty()) {
            return false;
        }
        for (Element root : roots) {
            if (!RenderNode.isSameOrDescendant(element, root)) continue;
            return true;
        }
        return false;
    }

    private static boolean comesBeforeInPaintOrder(Element left, Element right, List<RenderNode> paintOrder) {
        int leftIndex = HitTestCache.firstPaintIndex(left, paintOrder);
        int rightIndex = HitTestCache.firstPaintIndex(right, paintOrder);
        if (leftIndex < 0) {
            return false;
        }
        if (rightIndex < 0) {
            return true;
        }
        return leftIndex > rightIndex;
    }

    private static int firstPaintIndex(Element element, List<RenderNode> paintOrder) {
        if (element == null || paintOrder == null) {
            return -1;
        }
        for (int i = paintOrder.size() - 1; i >= 0; --i) {
            RenderNode.ElementPhaseNode phaseNode;
            RenderNode node = paintOrder.get(i);
            if (!(node instanceof RenderNode.ElementPhaseNode) || (phaseNode = (RenderNode.ElementPhaseNode)node).target() != element) continue;
            return i;
        }
        return -1;
    }

    private record Bounds(double x, double y, double width, double height) {
        private static final Bounds EMPTY = new Bounds(0.0, 0.0, -1.0, -1.0);

        private boolean isValid() {
            return this.width >= 0.0 && this.height >= 0.0;
        }

        private boolean isEmpty() {
            return this.width <= 0.0 || this.height <= 0.0;
        }

        private Bounds intersection(Bounds other) {
            if (other == null) {
                return this;
            }
            double left = Math.max(this.x, other.x);
            double top = Math.max(this.y, other.y);
            double right = Math.min(this.x + this.width, other.x + other.width);
            double bottom = Math.min(this.y + this.height, other.y + other.height);
            return new Bounds(left, top, Math.max(0.0, right - left), Math.max(0.0, bottom - top));
        }

        private boolean contains(Position position) {
            return position.x >= this.x && position.x <= this.x + this.width && position.y >= this.y && position.y <= this.y + this.height;
        }
    }

    private record Entry(Element element, Bounds bounds, Bounds clip) {
    }
}

