/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedColorUnit
 *  dev.latvian.mods.unit.FixedNumberUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 *  net.minecraft.resources.ResourceLocation
 *  org.joml.Matrix4f
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
import org.joml.Matrix4f;

public class GradientObject
extends BoxObject {
    public Unit colorTL = FixedColorUnit.WHITE;
    public Unit colorTR = FixedColorUnit.WHITE;
    public Unit colorBL = FixedColorUnit.WHITE;
    public Unit colorBR = FixedColorUnit.WHITE;
    public ResourceLocation texture = null;
    public Unit u0 = FixedNumberUnit.ZERO;
    public Unit v0 = FixedNumberUnit.ZERO;
    public Unit u1 = FixedNumberUnit.ONE;
    public Unit v1 = FixedNumberUnit.ONE;

    public GradientObject(Painter painter) {
        super(painter);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        this.colorTL = properties.getColor("colorTL", this.colorTL);
        this.colorTR = properties.getColor("colorTR", this.colorTR);
        this.colorBL = properties.getColor("colorBL", this.colorBL);
        this.colorBR = properties.getColor("colorBR", this.colorBR);
        if (properties.hasAny("colorT")) {
            this.colorTL = this.colorTR = properties.getColor("colorT", (Unit)FixedColorUnit.WHITE);
        }
        if (properties.hasAny("colorB")) {
            this.colorBL = this.colorBR = properties.getColor("colorB", (Unit)FixedColorUnit.WHITE);
        }
        if (properties.hasAny("colorL")) {
            this.colorTL = this.colorBL = properties.getColor("colorL", (Unit)FixedColorUnit.WHITE);
        }
        if (properties.hasAny("colorR")) {
            this.colorTR = this.colorBR = properties.getColor("colorR", (Unit)FixedColorUnit.WHITE);
        }
        if (properties.hasAny("color")) {
            this.colorBL = this.colorBR = properties.getColor("color", (Unit)FixedColorUnit.WHITE);
            this.colorTR = this.colorBR;
            this.colorTL = this.colorBR;
        }
        this.texture = properties.getResourceLocation("texture", this.texture);
        this.u0 = properties.getUnit("u0", this.u0);
        this.v0 = properties.getUnit("v0", this.v0);
        this.u1 = properties.getUnit("u1", this.u1);
        this.v1 = properties.getUnit("v1", this.v1);
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        int colBL = this.colorBL.getInt((UnitVariables)event);
        int colBR = this.colorBR.getInt((UnitVariables)event);
        int colTR = this.colorTR.getInt((UnitVariables)event);
        int colTL = this.colorTL.getInt((UnitVariables)event);
        if ((colBL >> 24 & 0xFF) < 2 && (colBR >> 24 & 0xFF) < 2 && (colTR >> 24 & 0xFF) < 2 && (colTL >> 24 & 0xFF) < 2) {
            return;
        }
        float aw = this.w.getFloat((UnitVariables)event);
        float ah = this.h.getFloat((UnitVariables)event);
        float ax = event.alignX(this.x.getFloat((UnitVariables)event), aw, this.alignX);
        float ay = event.alignY(this.y.getFloat((UnitVariables)event), ah, this.alignY);
        float az = this.z.getFloat((UnitVariables)event);
        Matrix4f m = event.getMatrix();
        if (this.texture == null) {
            event.setPositionColorShader();
            event.blend(true);
            event.beginQuads(false);
            event.vertex(m, ax + aw, ay, az, colTR);
            event.vertex(m, ax, ay, az, colTL);
            event.vertex(m, ax, ay + ah, az, colBL);
            event.vertex(m, ax + aw, ay + ah, az, colBR);
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
            event.vertex(m, ax + aw, ay, az, colTR, u1f, v0f);
            event.vertex(m, ax, ay, az, colTL, u0f, v0f);
            event.vertex(m, ax, ay + ah, az, colBL, u0f, v1f);
            event.vertex(m, ax + aw, ay + ah, az, colBR, u1f, v1f);
            event.end();
        }
    }
}

