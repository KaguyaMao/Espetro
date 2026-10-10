/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package tech.vvp.vvp.entity.vehicle;

import net.minecraft.resources.ResourceLocation;

public interface ICamoVehicle {
    public int getCamoType();

    public void setCamoType(int var1);

    public void cycleCamo();

    public ResourceLocation[] getCamoTextures();

    public String[] getCamoNames();
}

