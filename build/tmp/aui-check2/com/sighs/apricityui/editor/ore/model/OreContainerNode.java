/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreFlexStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public final class OreContainerNode
extends OreCanvasNode {
    private final List<OreCanvasNode> children = new ArrayList<OreCanvasNode>();
    private final OreFlexStyle flex = new OreFlexStyle();
    private final boolean root;
    private String tag = "div";

    public OreContainerNode(boolean root) {
        this.root = root;
        if (root) {
            super.setLocked(true);
        }
    }

    public OreContainerNode(boolean root, UUID id) {
        super(id);
        this.root = root;
        if (root) {
            super.setLocked(true);
        }
    }

    public boolean isRoot() {
        return this.root;
    }

    public boolean acceptsStructuralChildren() {
        return this.root || !this.locked();
    }

    public String tag() {
        return this.tag;
    }

    public void setTag(String tag) {
        if (tag != null && tag.matches("[A-Za-z][A-Za-z0-9-]*")) {
            this.tag = tag.toLowerCase(Locale.ROOT);
        }
    }

    @Override
    public void setLocked(boolean locked) {
        super.setLocked(this.root || locked);
    }

    public OreFlexStyle flex() {
        return this.flex;
    }

    public List<OreCanvasNode> children() {
        return List.copyOf(this.children);
    }

    public void add(OreCanvasNode child) {
        this.insert(this.children.size(), child);
    }

    public void insert(int index, OreCanvasNode child) {
        OreContainerNode container;
        if (child == null || child == this || child instanceof OreContainerNode && (this.contains(container = (OreContainerNode)child) || container.contains(this))) {
            throw new IllegalArgumentException("Invalid canvas tree insertion");
        }
        if (child.parent() != null) {
            child.parent().remove(child);
        }
        this.children.add(Math.max(0, Math.min(index, this.children.size())), child);
        child.setParent(this);
    }

    public boolean remove(OreCanvasNode child) {
        if (child == null) {
            return false;
        }
        if (!this.children.remove(child)) {
            return false;
        }
        child.setParent(null);
        return true;
    }

    private boolean contains(OreCanvasNode candidate) {
        if (this == candidate) {
            return true;
        }
        for (OreCanvasNode child : this.children) {
            OreContainerNode container;
            if (child == candidate) {
                return true;
            }
            if (!(child instanceof OreContainerNode) || !(container = (OreContainerNode)child).contains(candidate)) continue;
            return true;
        }
        return false;
    }
}

