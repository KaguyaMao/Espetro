/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.palette;

import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreComponentNode;
import com.sighs.apricityui.editor.ore.model.OreContainerNode;

public record OreComponentDefinition(String id, String nameKey, String descriptionKey, boolean container, String tagName, String defaultContent) {
    public OreComponentDefinition(String id, String nameKey, String descriptionKey, boolean container) {
        this(id, nameKey, descriptionKey, container, container ? null : id, container ? null : "");
    }

    public OreCanvasNode createNode() {
        if (this.container) {
            OreContainerNode node = new OreContainerNode(false);
            node.flex().setDirection("column".equals(this.id) ? "column" : "row");
            node.style().set("min-height", "64px");
            node.style().set("padding", "8px");
            return node;
        }
        return new OreComponentNode(this.tagName == null || this.tagName.isBlank() ? "div" : this.tagName, this.defaultContent == null ? "" : this.defaultContent);
    }
}

