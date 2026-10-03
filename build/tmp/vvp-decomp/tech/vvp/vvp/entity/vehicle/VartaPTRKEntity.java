/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package tech.vvp.vvp.entity.vehicle;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.vvp.vvp.entity.vehicle.CamoVehicleBase;

public class VartaPTRKEntity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("vvp", "textures/entity/varta.png"), new ResourceLocation("vvp", "textures/entity/varta_basic.png"), new ResourceLocation("vvp", "textures/entity/varta_black.png"), new ResourceLocation("vvp", "textures/entity/varta_sandy.png"), new ResourceLocation("vvp", "textures/entity/varta_snow.png")};
    private static final String[] CAMO_NAMES = new String[]{"Standard", "Basic", "Black", "Sandy", "Snow"};

    public VartaPTRKEntity(EntityType<VartaPTRKEntity> type, Level world) {
        super(type, world);
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }
}

