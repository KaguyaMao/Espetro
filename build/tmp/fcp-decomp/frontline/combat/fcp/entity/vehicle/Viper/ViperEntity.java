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
package frontline.combat.fcp.entity.vehicle.Viper;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import frontline.combat.fcp.entity.vehicle.CamoVehicleBase;
import java.lang.reflect.Field;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class ViperEntity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("fcp", "textures/entity/viper/viper_1.png"), new ResourceLocation("fcp", "textures/entity/viper/viper_2.png"), new ResourceLocation("fcp", "textures/entity/viper/viper_3.png"), new ResourceLocation("fcp", "textures/entity/viper/viper_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/viper/viper_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/viper/viper_3_wrecked.png")};
    private static final String[] CAMO_NAMES = new String[]{"Standard", "White", "Shark"};
    private static Field propellerRotField;
    private static Field propellerRotOField;
    private int previousCannonAmmo = -1;
    private float barrelRotation = 0.0f;
    private float barrelRotationOld = 0.0f;

    public ViperEntity(EntityType<ViperEntity> type, Level world) {
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

    public void m_6075_() {
        super.m_6075_();
        this.barrelRotationOld = this.barrelRotation;
        int currentAmmo = this.getAmmoCount("Cannon");
        if (this.previousCannonAmmo == -1) {
            this.previousCannonAmmo = currentAmmo;
        }
        if (currentAmmo < this.previousCannonAmmo) {
            this.barrelRotation += 20.0f;
            if (this.barrelRotation >= 360.0f) {
                this.barrelRotation -= 360.0f;
            }
        }
        this.previousCannonAmmo = currentAmmo;
    }

    public boolean GetWeaponState(String WeaponName, int Count) {
        if (this.getAmmoCount(WeaponName) == Count) {
            return true;
        }
        return this.getAmmoCount(WeaponName) < Count;
    }

    public float getBarrelRot() {
        return this.barrelRotation;
    }

    public float getBarrelRot0() {
        return this.barrelRotationOld;
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

