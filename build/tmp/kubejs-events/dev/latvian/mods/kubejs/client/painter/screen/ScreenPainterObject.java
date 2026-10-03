/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.PainterObject;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenDrawMode;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;

public abstract class ScreenPainterObject
extends PainterObject {
    public Unit x = FixedNumberUnit.ZERO;
    public Unit y = FixedNumberUnit.ZERO;
    public Unit z = FixedNumberUnit.ZERO;
    public ScreenDrawMode draw = ScreenDrawMode.INGAME;

    public void preDraw(PaintScreenEventJS event) {
    }

    public abstract void draw(PaintScreenEventJS var1);

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        this.x = properties.getUnit("x", this.x).add(properties.getUnit("moveX", (Unit)FixedNumberUnit.ZERO));
        this.y = properties.getUnit("y", this.y).add(properties.getUnit("moveY", (Unit)FixedNumberUnit.ZERO));
        this.z = properties.getUnit("z", this.z);
        if (properties.hasString("draw")) {
            switch (properties.getString("draw", "ingame")) {
                case "always": {
                    this.draw = ScreenDrawMode.ALWAYS;
                    break;
                }
                case "gui": {
                    this.draw = ScreenDrawMode.GUI;
                    break;
                }
                default: {
                    this.draw = ScreenDrawMode.INGAME;
                }
            }
        }
    }
}

