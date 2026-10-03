/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import com.sighs.apricityui.editor.ore.model.OreContainerNode;
import com.sighs.apricityui.editor.ore.model.OreNodeStyle;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

public abstract class OreCanvasNode {
    private final UUID id;
    private OreContainerNode parent;
    private final OreNodeStyle style = new OreNodeStyle();
    private final Map<String, String> attributes = new LinkedHashMap<String, String>();
    private boolean locked;

    protected OreCanvasNode() {
        this(UUID.randomUUID());
    }

    protected OreCanvasNode(UUID id) {
        this.id = id == null ? UUID.randomUUID() : id;
    }

    public UUID id() {
        return this.id;
    }

    public OreContainerNode parent() {
        return this.parent;
    }

    public OreNodeStyle style() {
        return this.style;
    }

    public Map<String, String> attributes() {
        return Collections.unmodifiableMap(new LinkedHashMap<String, String>(this.attributes));
    }

    public void setAttribute(String name, String value) {
        String normalized = OreCanvasNode.normalizeAttributeName(name);
        if (normalized == null) {
            return;
        }
        this.attributes.put(normalized, value == null ? "" : value);
    }

    public void removeAttribute(String name) {
        String normalized = OreCanvasNode.normalizeAttributeName(name);
        if (normalized != null) {
            this.attributes.remove(normalized);
        }
    }

    public boolean locked() {
        return this.locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    void setParent(OreContainerNode parent) {
        this.parent = parent;
    }

    private static String normalizeAttributeName(String name) {
        if (name == null) {
            return null;
        }
        String normalized = name.trim().toLowerCase(Locale.ROOT);
        if (!normalized.matches("[a-z_:][a-z0-9:_.-]*") || "style".equals(normalized) || normalized.startsWith("on") || "data-ore-node-id".equals(normalized) || "data-ore-editor-ui".equals(normalized)) {
            return null;
        }
        return normalized;
    }
}

