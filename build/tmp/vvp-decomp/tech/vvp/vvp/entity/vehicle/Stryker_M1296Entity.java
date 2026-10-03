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

public class Stryker_M1296Entity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("vvp", "textures/entity/strykers_green.png"), new ResourceLocation("vvp", "textures/entity/strykers_haki.png")};
    private static final String[] CAMO_NAMES = new String[]{"Green", "Haki"};

    public Stryker_M1296Entity(EntityType<Stryker_M1296Entity> type, Level world) {
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

