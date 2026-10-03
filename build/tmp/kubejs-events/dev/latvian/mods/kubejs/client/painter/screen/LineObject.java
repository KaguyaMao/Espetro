/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedColorUnit
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.ScreenPainterObject;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;

public class LineObject
extends ScreenPainterObject {
    public Unit color = FixedColorUnit.WHITE;
    public Unit x2 = FixedNumberUnit.ZERO;
    public Unit y2 = FixedNumberUnit.ZERO;
    public Unit size = FixedNumberUnit.ZERO;
    public Unit length = FixedNumberUnit.ZERO;
    public Unit rotation = FixedNumberUnit.ZERO;
    public Unit offset = FixedNumberUnit.ZERO;

    public LineObject(Painter painter) {
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        this.color = properties.getColor("color", this.color);
        this.x2 = properties.getUnit("x2", this.x2);
        this.y2 = properties.getUnit("y2", this.y2);
        this.size = properties.getUnit("size", this.size);
        this.length = properties.getUnit("length", this.length);
        this.rotation = properties.getUnit("rotation", this.rotation);
        this.offset = properties.getUnit("offset", this.offset);
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        float ax = this.x.getFloat((UnitVariables)event);
        float ay = this.y.getFloat((UnitVariables)event);
        float az = this.z.getFloat((UnitVariables)event);
        float as = (this.size == FixedNumberUnit.ZERO ? event.painter.defaultLineSizeUnit : this.size).getFloat((UnitVariables)event);
        float alength = this.length.getFloat((UnitVariables)event);
        float aangle = this.rotation.getFloat((UnitVariables)event);
        if (alength <= 1.0E-4f) {
            if (this.x2 == FixedNumberUnit.ZERO && this.y2 == FixedNumberUnit.ZERO) {
                return;
            }
            float ax2 = this.x2.getFloat((UnitVariables)event);
            float ay2 = this.y2.getFloat((UnitVariables)event);
            alength = (float)Math.sqrt((ax2 - ax) * (ax2 - ax) + (ay2 - ay) * (ay2 - ay));
            aangle = (float)Math.toDegrees(Math.atan2(ay2 - ay, ax2 - ax));
        }
        event.push();
        event.translate(ax, ay);
        event.rotateDeg(aangle);
        event.setPositionColorShader();
        event.blend(true);
        event.beginQuads(false);
        event.rectangle(this.offset.getFloat((UnitVariables)event), -as / 2.0f, az, alength, as, this.color.getInt((UnitVariables)event));
        event.end();
        event.pop();
    }
}

