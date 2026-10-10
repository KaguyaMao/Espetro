/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.entity.EntityRendererProvider$Context
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.Entity
 *  software.bernie.geckolib.model.GeoModel
 *  software.bernie.geckolib.renderer.GeoEntityRenderer
 */
package com.redabysslucia.dragonrise_reforge.client.renderer.entity;

import com.redabysslucia.dragonrise_reforge.client.model.entity.TerroristModel;
import com.redabysslucia.dragonrise_reforge.entities.TerroristEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class TerroristRenderer
extends GeoEntityRenderer<TerroristEntity> {
    public TerroristRenderer(EntityRendererProvider.Context renderManager) {
        super(renderManager, (GeoModel)new TerroristModel());
        this.f_114477_ = 0.5f;
    }

    public RenderType getRenderType(TerroristEntity animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.m_110473_((ResourceLocation)this.m_5478_((Entity)animatable));
    }

    protected float getDeathMaxRotation(TerroristEntity entityLivingBaseIn) {
        return 0.0f;
    }
}

