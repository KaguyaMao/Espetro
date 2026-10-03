/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package tech.vvp.vvp.entity.vehicle;

import java.lang.reflect.Field;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import tech.vvp.vvp.entity.vehicle.CamoVehicleBase;

public class Mi24Entity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("vvp", "textures/entity/mi24_sand_ru.png"), new ResourceLocation("vvp", "textures/entity/mi24_green_ru.png"), new ResourceLocation("vvp", "textures/entity/mi24_green_rb.png"), new ResourceLocation("vvp", "textures/entity/mi24_sand.png"), new ResourceLocation("vvp", "textures/entity/mi24_snow.png"), new ResourceLocation("vvp", "textures/entity/mi24_tan.png"), new ResourceLocation("vvp", "textures/entity/mi24_tan_zov.png")};
    private static final String[] CAMO_NAMES = new String[]{"Sand RU", "Green RU", "Green RB", "Sand", "Snow", "Tan", "Tan ZOV"};
    private static Field propellerRotField;
    private static Field propellerRotOField;

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    public Mi24Entity(EntityType<Mi24Entity> type, Level world) {
        super(type, world);
    }

    public float getPropellerRot() {
        try {
            return propellerRotField != null ? ((Float)propellerRotField.get(this)).floatValue() : 0.0f;
        }
        catch (Exception e) {
            return 0.0f;
        }
    }

    public float getPropellerRotO() {
        try {
            return propellerRotOField != null ? ((Float)propellerRotOField.get(this)).floatValue() : 0.0f;
        }
        catch (Exception e) {
            return 0.0f;
        }
    }

    static {
        try {
            Class<?> vehicleClass = Class.forName("com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity");
            propellerRotField = vehicleClass.getDeclaredField("propellerRot");
            propellerRotField.setAccessible(true);
            propellerRotOField = vehicleClass.getDeclaredField("propellerRotO");
            propellerRotOField.setAccessible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

