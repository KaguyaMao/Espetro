/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.unit.FixedColorUnit
 *  dev.latvian.mods.unit.Unit
 *  dev.latvian.mods.unit.UnitVariables
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.inventory.InventoryMenu
 */
package dev.latvian.mods.kubejs.client.painter.screen;

import dev.latvian.mods.kubejs.client.painter.Painter;
import dev.latvian.mods.kubejs.client.painter.PainterObjectProperties;
import dev.latvian.mods.kubejs.client.painter.screen.BoxObject;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.unit.FixedColorUnit;
import dev.latvian.mods.unit.Unit;
import dev.latvian.mods.unit.UnitVariables;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;

public class AtlasTextureObject
extends BoxObject {
    public Unit color = FixedColorUnit.WHITE;
    public ResourceLocation atlas = InventoryMenu.f_39692_;
    public ResourceLocation texture = null;
    public TextureAtlas textureAtlas;

    public AtlasTextureObject(Painter painter) {
        super(painter);
    }

    @Override
    protected void load(PainterObjectProperties properties) {
        super.load(properties);
        this.color = properties.getColor("color", this.color);
        this.atlas = properties.getResourceLocation("atlas", this.atlas);
        this.texture = properties.getResourceLocation("texture", this.texture);
        this.textureAtlas = null;
    }

    @Override
    public void draw(PaintScreenEventJS event) {
        if (this.texture == null) {
            return;
        }
        if (this.textureAtlas == null) {
            this.textureAtlas = event.mc.m_91304_().m_119428_(this.atlas);
        }
        if (this.textureAtlas == null) {
            return;
        }
        float aw = this.w.getFloat((UnitVariables)event);
        float ah = this.h.getFloat((UnitVariables)event);
        float ax = event.alignX(this.x.getFloat((UnitVariables)event), aw, this.alignX);
        float ay = event.alignY(this.y.getFloat((UnitVariables)event), ah, this.alignY);
        float az = this.z.getFloat((UnitVariables)event);
        TextureAtlasSprite sprite = this.textureAtlas.m_118316_(this.texture);
        float u0 = sprite.m_118409_();
        float v0 = sprite.m_118411_();
        float u1 = sprite.m_118410_();
        float v1 = sprite.m_118412_();
        event.resetShaderColor();
        event.setPositionColorTextureShader();
        event.setShaderTexture(this.atlas);
        event.blend(true);
        event.beginQuads(true);
        event.rectangle(ax, ay, az, aw, ah, this.color.getInt((UnitVariables)event), u0, v0, u1, v1);
        event.end();
    }
}

