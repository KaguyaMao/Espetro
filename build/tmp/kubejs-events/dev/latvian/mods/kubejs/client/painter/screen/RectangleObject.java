/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedColorUnit
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.screen.BoxObject;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.FixedNumberUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import net.minecraft.resources.ResourceLocation;

public class RectangleObject
extends BoxObject {
    public Unit color = FixedColorUnit.WHITE;
    public ResourceLocation texture = null;
    public Unit u0 = FixedNumberUnit.ZERO;
    public Unit v0 = FixedNumberUnit.ZERO;
    public Unit u1 = FixedNumberUnit.ONE;
    public Unit v1 = FixedNumberUnit.ONE;

    public RectangleObject(Painter painter) {
        super(painter);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        this.color = properties.getColor("color", this.color);
        this.texture = properties.getResourceLocation("texture", this.texture);
        this.u0 = properties.getUnit("u0", this.u0);
        this.v0 = properties.getUnit("v0", this.v0);
        this.u1 = properties.getUnit("u1", this.u1);
        this.v1 = properties.getUnit("v1", this.v1);
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        float aw = this.w.getFloat((UnitVariables)event);
        float ah = this.h.getFloat((UnitVariables)event);
        float ax = event.alignX(this.x.getFloat((UnitVariables)event), aw, this.alignX);
        float ay = event.alignY(this.y.getFloat((UnitVariables)event), ah, this.alignY);
        float az = this.z.getFloat((UnitVariables)event);
        if (this.texture == null) {
            event.setPositionColorShader();
            event.blend(true);
            event.beginQuads(false);
            event.rectangle(ax, ay, az, aw, ah, this.color.getInt((UnitVariables)event));
            event.end();
        } else {
            float u0f = this.u0.getFloat((UnitVariables)event);
            float v0f = this.v0.getFloat((UnitVariables)event);
            float u1f = this.u1.getFloat((UnitVariables)event);
            float v1f = this.v1.getFloat((UnitVariables)event);
            event.setPositionColorTextureShader();
            event.setShaderTexture(this.texture);
            event.blend(true);
            event.beginQuads(true);
            event.rectangle(ax, ay, az, aw, ah, this.color.getInt((UnitVariables)event), u0f, v0f, u1f, v1f);
            event.end();
        }
    }
}

