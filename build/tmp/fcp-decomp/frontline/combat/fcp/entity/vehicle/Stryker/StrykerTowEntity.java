/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.level.Level
 */
package frontline.combat.fcp.entity.vehicle.Stryker;

import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import frontline.combat.fcp.entity.vehicle.CamoVehicleBase;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

public class StrykerTowEntity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("fcp", "textures/entity/stryker/stryker_1.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_2.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_3.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_4.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_5.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_3_wrecked.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_4_wrecked.png"), new ResourceLocation("fcp", "textures/entity/stryker/stryker_5_wrecked.png")};
    private static final String[] CAMO_NAMES = new String[]{"Camo 1", "Camo 2", "Camo 3", "Camo 4", "Camo 5"};
    private static final EntityDataAccessor<Float> STEERING_ANGLE = SynchedEntityData.m_135353_(StrykerTowEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private float prevSteeringAngle = 0.0f;
    private float wheelRotation = 0.0f;
    private float prevWheelRotation = 0.0f;

    public StrykerTowEntity(EntityType<StrykerTowEntity> type, Level world) {
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

    @Override
    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(STEERING_ANGLE, (Object)Float.valueOf(0.0f));
    }

    public float getSteeringAngle() {
        return ((Float)this.f_19804_.m_135370_(STEERING_ANGLE)).floatValue();
    }

    public void setSteeringAngle(float a) {
        this.f_19804_.m_135381_(STEERING_ANGLE, (Object)Float.valueOf(a));
    }

    public float getPrevSteeringAngle() {
        return this.prevSteeringAngle;
    }

    public float getWheelRotation() {
        return this.wheelRotation;
    }

    public float getPrevWheelRotation() {
        return this.prevWheelRotation;
    }

    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> Float.valueOf(this.getSourceAngle((DamageSource)source, 0.4f) * damage.floatValue()));
    }

    @Override
    public void m_7380_(CompoundTag c) {
        super.m_7380_(c);
        c.m_128350_("SteeringAngle", this.getSteeringAngle());
    }

    @Override
    public void m_7378_(CompoundTag c) {
        super.m_7378_(c);
        if (c.m_128441_("SteeringAngle")) {
            this.setSteeringAngle(c.m_128457_("SteeringAngle"));
        }
    }

    public void m_6075_() {
        boolean moving;
        super.m_6075_();
        this.prevSteeringAngle = this.getSteeringAngle();
        float a = this.getSteeringAngle();
        double speed = Math.sqrt(this.m_20184_().f_82479_ * this.m_20184_().f_82479_ + this.m_20184_().f_82481_ * this.m_20184_().f_82481_);
        boolean bl = moving = speed > 0.05;
        if (this.leftInputDown() && !this.rightInputDown()) {
            a = Math.min(45.0f, a + 2.0f);
            this.setSteeringAngle(a);
        } else if (this.rightInputDown() && !this.leftInputDown()) {
            a = Math.max(-45.0f, a - 2.0f);
            this.setSteeringAngle(a);
        } else if (moving && Math.abs(a) > 0.5f) {
            this.setSteeringAngle(a *= 0.9f);
        }
        if (moving && Math.abs(a) > 1.0f) {
            this.m_146922_(this.m_146908_() + a * 0.009f * (float)speed);
        }
        this.prevWheelRotation = this.wheelRotation;
        this.wheelRotation += (float)(speed * 20.0);
    }

    public boolean GetWeaponState(String WeaponName, int Count) {
        if (this.getAmmoCount(WeaponName) == Count) {
            return true;
        }
        return this.getAmmoCount(WeaponName) < Count;
    }
}

