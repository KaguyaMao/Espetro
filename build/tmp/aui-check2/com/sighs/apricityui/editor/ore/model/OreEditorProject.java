/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import com.sighs.apricityui.editor.ore.model.OreCanvasNode;
import com.sighs.apricityui.editor.ore.model.OreContainerNode;
import com.sighs.apricityui.editor.ore.model.OreDocumentMetadata;
import com.sighs.apricityui.editor.ore.model.OreTheme;
import java.util.UUID;

public final class OreEditorProject {
    private final OreContainerNode root;
    private final OreTheme theme = new OreTheme();
    private final OreDocumentMetadata documentMetadata = new OreDocumentMetadata();

    public OreEditorProject() {
        this(new OreContainerNode(true));
    }

    public OreEditorProject(OreContainerNode root) {
        this.root = root == null ? new OreContainerNode(true) : root;
    }

    public OreContainerNode root() {
        return this.root;
    }

    public OreTheme theme() {
        return this.theme;
    }

    public OreDocumentMetadata documentMetadata() {
        return this.documentMetadata;
    }

    public OreCanvasNode find(UUID id) {
        return this.find(this.root, id);
    }

    private OreCanvasNode find(OreCanvasNode node, UUID id) {
        if (node == null || id == null) {
            return null;
        }
        if (id.equals(node.id())) {
            return node;
        }
        if (node instanceof OreContainerNode) {
            OreContainerNode container = (OreContainerNode)node;
            for (OreCanvasNode child : container.children()) {
                OreCanvasNode match = this.find(child, id);
                if (match == null) continue;
                return match;
            }
        }
        return null;
    }
}

