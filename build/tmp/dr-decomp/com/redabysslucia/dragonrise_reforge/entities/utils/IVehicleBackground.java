/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public interface IVehicleBackground {
    @OnlyIn(value=Dist.CLIENT)
    public ResourceLocation getBackgroundTexture();

    @OnlyIn(value=Dist.CLIENT)
    default public boolean shouldRenderBackground() {
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public float getBackgroundAlpha() {
        return 1.0f;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public boolean keepAspectRatio() {
        return true;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public float getBackgroundAspectRatio() {
        return 1.0f;
    }

    @OnlyIn(value=Dist.CLIENT)
    default public boolean scaleByHeight() {
        return false;
    }
}

