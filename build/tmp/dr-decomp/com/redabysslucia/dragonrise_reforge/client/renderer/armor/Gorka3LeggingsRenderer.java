/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  software.bernie.geckolib.cache.object.GeoBone
 *  software.bernie.geckolib.core.animatable.GeoAnimatable
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoArmorRenderer
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.armor;

import com.redabysslucia.dragonrise_reforge.client.model.armor.Gorka3LeggingsModel;
import com.redabysslucia.dragonrise_reforge.item.armor.Gorka3Leggings;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoArmorRenderer;

public class Gorka3LeggingsRenderer
extends GeoArmorRenderer<Gorka3Leggings> {
    public Gorka3LeggingsRenderer() {
        super((GeoModel)new Gorka3LeggingsModel());
        this.body = new GeoBone(null, "armorBody", Boolean.valueOf(true), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.rightLeg = new GeoBone(null, "armorRightLeg", Boolean.valueOf(true), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
        this.leftLeg = new GeoBone(null, "armorLeftLeg", Boolean.valueOf(true), Double.valueOf(0.0), Boolean.valueOf(false), Boolean.valueOf(false));
    }

    public RenderType getRenderType(Gorka3Leggings animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_((ResourceLocation)this.getTextureLocation((GeoAnimatable)animatable));
    }
}

