/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.drag;

import com.sighs.apricityui.editor.ore.palette.OreComponentDefinition;
import java.util.function.BiConsumer;

public final class OreDragController {
    private OreComponentDefinition payload;
    private double x;
    private double y;
    private boolean active;

    public boolean active() {
        return this.active;
    }

    public OreComponentDefinition payload() {
        return this.payload;
    }

    public double x() {
        return this.x;
    }

    public double y() {
        return this.y;
    }

    public void begin(OreComponentDefinition payload, double x, double y) {
        this.payload = payload;
        this.x = x;
        this.y = y;
        this.active = payload != null;
    }

    public void move(double x, double y) {
        if (this.active) {
            this.x = x;
            this.y = y;
        }
    }

    public void end(BiConsumer<OreComponentDefinition, double[]> drop) {
        if (this.active && this.payload != null) {
            drop.accept(this.payload, new double[]{this.x, this.y});
        }
        this.cancel();
    }

    public void cancel() {
        this.active = false;
        this.payload = null;
    }
}

