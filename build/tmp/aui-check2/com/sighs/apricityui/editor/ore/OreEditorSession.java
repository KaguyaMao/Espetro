/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore;

import java.util.UUID;

public final class OreEditorSession {
    private Mode mode = Mode.ADD;
    private UUID selectedNode;
    private boolean dirty;

    public Mode mode() {
        return this.mode;
    }

    public UUID selectedNode() {
        return this.selectedNode;
    }

    public boolean dirty() {
        return this.dirty;
    }

    public void setMode(Mode mode) {
        this.mode = mode == null ? Mode.ADD : mode;
    }

    public void select(UUID selectedNode) {
        this.selectedNode = selectedNode;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    public void reset() {
        this.mode = Mode.ADD;
        this.selectedNode = null;
        this.dirty = false;
    }

    public static enum Mode {
        ADD,
        INSPECT,
        THEME;

    }
}

