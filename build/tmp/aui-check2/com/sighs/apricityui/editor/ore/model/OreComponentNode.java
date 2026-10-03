/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreNodeStyle;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class OreComponentNode
extends OreCanvasNode {
    private final String type;
    private String content;
    private boolean absolute;
    private int flowIndex = -1;
    private final Map<VisualState, OreNodeStyle> stateStyles = new EnumMap<VisualState, OreNodeStyle>(VisualState.class);
    private final Map<String, String> flowStyleSnapshot = new LinkedHashMap<String, String>();
    private boolean hasFlowStyleSnapshot;
    private static final String[] FLOW_STYLE_PROPERTIES = new String[]{"position", "left", "right", "top", "bottom", "width", "height", "order", "flex-grow", "flex-shrink", "flex-basis", "align-self"};

    public OreComponentNode(String type, String content) {
        this(type, content, null);
    }

    public OreComponentNode(String type, String content, UUID id) {
        super(id);
        this.type = type == null || type.isBlank() ? "div" : type.trim().toLowerCase();
        this.content = content == null ? "" : content;
    }

    public String type() {
        return this.type;
    }

    public String content() {
        return this.content;
    }

    public void setContent(String content) {
        this.content = content == null ? "" : content;
    }

    public boolean absolute() {
        return this.absolute;
    }

    public int flowIndex() {
        return this.flowIndex;
    }

    public void enterAbsolute(int flowIndex) {
        this.absolute = true;
        this.flowIndex = Math.max(0, flowIndex);
    }

    public void leaveAbsolute() {
        this.absolute = false;
    }

    public void captureFlowStyleSnapshot() {
        this.flowStyleSnapshot.clear();
        for (String property : FLOW_STYLE_PROPERTIES) {
            this.flowStyleSnapshot.put(property, this.style().get(property));
        }
        this.hasFlowStyleSnapshot = true;
    }

    public boolean hasFlowStyleSnapshot() {
        return this.hasFlowStyleSnapshot;
    }

    public Map<String, String> flowStyleSnapshot() {
        return new LinkedHashMap<String, String>(this.flowStyleSnapshot);
    }

    public void setFlowStyleSnapshot(Map<String, String> snapshot) {
        this.flowStyleSnapshot.clear();
        if (snapshot != null) {
            this.flowStyleSnapshot.putAll(snapshot);
        }
        this.hasFlowStyleSnapshot = !this.flowStyleSnapshot.isEmpty();
    }

    public void restoreFlowStyleSnapshot() {
        if (!this.hasFlowStyleSnapshot) {
            return;
        }
        for (String property : FLOW_STYLE_PROPERTIES) {
            this.style().set(property, this.flowStyleSnapshot.get(property));
        }
        this.flowStyleSnapshot.clear();
        this.hasFlowStyleSnapshot = false;
    }

    public OreNodeStyle stateStyle(VisualState state) {
        if (state == null || state == VisualState.DEFAULT) {
            return this.style();
        }
        return this.stateStyles.computeIfAbsent(state, ignored -> new OreNodeStyle());
    }

    public Map<VisualState, OreNodeStyle> stateStyles() {
        return Map.copyOf(this.stateStyles);
    }

    public static enum VisualState {
        DEFAULT,
        HOVER,
        ACTIVE,
        FOCUS,
        DISABLED;

    }
}

