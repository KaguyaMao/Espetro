/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.render.Drawer;
import com.sighs.apricityui.render.HitTestCache;
import com.sighs.apricityui.render.LayoutCommit;
import com.sighs.apricityui.render.RenderNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class RenderQueue {
    private static final int VISUAL_DIRTY_MASK = 23;
    private final Document owner;
    private final Set<Element> dirtyElements = ConcurrentHashMap.newKeySet();
    private final Set<Element> hitTestDirtyRoots = Collections.newSetFromMap(new IdentityHashMap());
    private final HitTestCache hitTestCache;
    private ArrayList<RenderNode> paintList = new ArrayList();
    private int globalDirtyMask = 0;
    private boolean layoutCommitDirty = false;
    private volatile long visualVersion = 1L;

    public RenderQueue(Document owner) {
        this.owner = owner;
        this.hitTestCache = new HitTestCache(owner);
    }

    public ArrayList<RenderNode> getPaintList() {
        return this.paintList;
    }

    public long getVisualVersion() {
        return this.visualVersion;
    }

    public Set<Element> getDirtyElements() {
        return this.dirtyElements;
    }

    public void reset() {
        this.markVisualDirty();
        this.dirtyElements.clear();
        this.hitTestDirtyRoots.clear();
        this.globalDirtyMask = 0;
        this.layoutCommitDirty = false;
        this.paintList = new ArrayList();
        this.hitTestCache.clear();
    }

    public void rebuildPaintList() {
        this.markVisualDirty();
        if (this.owner.documentElement == null) {
            this.paintList = new ArrayList();
            this.hitTestCache.clear();
            this.layoutCommitDirty = false;
            return;
        }
        this.paintList = Drawer.createPaintList(this.owner.documentElement);
        this.layoutCommitDirty = true;
        this.hitTestCache.markDirty();
    }

    public void tickElements() {
        for (Element element : new ArrayList<Element>(this.owner.getElements())) {
            element.tick();
        }
    }

    public void commit() {
        this.commit(true);
    }

    public boolean commit(boolean commitLayoutNow) {
        boolean hadWork = this.globalDirtyMask != 0 || !this.dirtyElements.isEmpty() || this.layoutCommitDirty;
        boolean hadGlobalDirty = this.globalDirtyMask != 0;
        boolean needsLayoutCommit = this.layoutCommitDirty || (this.globalDirtyMask & 0x14) != 0;
        boolean fullHitTestRebuild = hadGlobalDirty || this.hitTestDirtyRoots.contains(this.owner.documentElement);
        Set<Element> incrementalHitRoots = Collections.newSetFromMap(new IdentityHashMap());
        for (Element element : this.dirtyElements) {
            if (element == null || !element.isConnected()) continue;
            if (element.hasDirtyFlag(2)) {
                fullHitTestRebuild = true;
            }
            if (element.hasDirtyFlag(4) || element.hasDirtyFlag(16)) {
                needsLayoutCommit = true;
            }
            if (element.hasDirtyFlag(4)) {
                incrementalHitRoots.add(element.parentElement == null ? element : element.parentElement);
                continue;
            }
            if (element.hasDirtyFlag(16)) {
                incrementalHitRoots.add(element.parentElement == null ? element : element.parentElement);
                continue;
            }
            if (!element.hasDirtyFlag(8)) continue;
            incrementalHitRoots.add(element);
        }
        if (!fullHitTestRebuild) {
            incrementalHitRoots.addAll(this.hitTestDirtyRoots);
        }
        this.applyGlobalDirty();
        Drawer.flushUpdates(this.owner);
        if (needsLayoutCommit && commitLayoutNow) {
            LayoutCommit.commit(this.owner);
        }
        if (hadWork) {
            if (needsLayoutCommit && !commitLayoutNow) {
                this.hitTestCache.markDirty();
                this.hitTestDirtyRoots.clear();
            } else {
                if (fullHitTestRebuild) {
                    this.hitTestCache.markDirty();
                } else if (!incrementalHitRoots.isEmpty()) {
                    this.hitTestCache.updateSubtrees(this.paintList, incrementalHitRoots);
                }
                this.hitTestDirtyRoots.clear();
            }
            this.layoutCommitDirty = false;
        }
        return needsLayoutCommit;
    }

    public void markDirty(int mask) {
        if (mask == 0) {
            return;
        }
        if ((mask & 0x17) != 0) {
            this.markVisualDirty();
        }
        this.globalDirtyMask |= mask;
    }

    public void markDirty(Element element, int mask) {
        if (element == null || mask == 0) {
            return;
        }
        if (!element.isConnected()) {
            return;
        }
        if ((mask & 0x17) != 0) {
            this.markVisualDirty();
        }
        element.addDirtyFlags(mask);
        this.dirtyElements.add(element);
    }

    public boolean hasPendingWork() {
        return this.globalDirtyMask != 0 || !this.dirtyElements.isEmpty() || this.layoutCommitDirty;
    }

    public boolean hasPendingVisualWork() {
        if (this.layoutCommitDirty || (this.globalDirtyMask & 0x17) != 0) {
            return true;
        }
        for (Element element : this.dirtyElements) {
            if (element == null || !element.hasDirtyFlag(23)) continue;
            return true;
        }
        return false;
    }

    public int getGlobalDirtyMask() {
        return this.globalDirtyMask;
    }

    public Element hitTest(Position position) {
        return this.hitTestCache.hitTest(position, this.paintList);
    }

    public void markHitTestDirty() {
        this.hitTestCache.markDirty();
        this.hitTestDirtyRoots.clear();
    }

    public void markHitTestDirty(Element element) {
        if (element == null || !element.isConnected()) {
            return;
        }
        if (this.hitTestDirtyRoots.contains(this.owner.documentElement)) {
            return;
        }
        this.hitTestDirtyRoots.add(element);
    }

    public void updateHitTestSubtrees(Set<Element> roots) {
        Set<Element> combined = Collections.newSetFromMap(new IdentityHashMap());
        combined.addAll(this.hitTestDirtyRoots);
        if (roots != null) {
            combined.addAll(roots);
        }
        this.hitTestCache.updateSubtrees(this.paintList, combined);
        this.hitTestDirtyRoots.clear();
    }

    private void applyGlobalDirty() {
        int mask = this.globalDirtyMask;
        if (mask == 0) {
            return;
        }
        this.globalDirtyMask = 0;
        ArrayList<Element> snapshot = new ArrayList<Element>(this.owner.getElements());
        for (Element element : snapshot) {
            if (element == null || !element.isConnected()) continue;
            element.addDirtyFlags(mask);
            this.dirtyElements.add(element);
        }
    }

    public void markVisualDirty() {
        ++this.visualVersion;
    }
}

