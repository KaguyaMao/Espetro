/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package frontline.combat.fcp.entity.vehicle.MemeVehicles;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import frontline.combat.fcp.entity.vehicle.CamoVehicleBase;
import java.lang.reflect.Field;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class BigBirdEntity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("fcp", "textures/entity/meme_vehicles/bigbird.png"), new ResourceLocation("fcp", "textures/entity/meme_vehicles/bigbird_wrecked.png")};
    private static final String[] CAMO_NAMES = new String[]{"BIGBIRD!"};
    private static Field propellerRotField;
    private static Field propellerRotOField;

    public BigBirdEntity(EntityType<BigBirdEntity> type, Level world) {
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

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> Float.valueOf(this.getSourceAngle((DamageSource)source, 0.4f) * damage.floatValue()));
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

