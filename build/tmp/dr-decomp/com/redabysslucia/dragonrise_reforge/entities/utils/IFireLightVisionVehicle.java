/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.redabysslucia.dragonrise_reforge.entities.utils;

import net.minecraft.resources.ResourceLocation;

public interface IFireLightVisionVehicle {
    public ResourceLocation getNightVisionShader();

    public ResourceLocation getThermalVisionShader();

    public boolean getNVEnable();

    public void setNVEnable(boolean var1);

    public boolean getTVGEnable();

    public void setTVGEnable(boolean var1);
}

