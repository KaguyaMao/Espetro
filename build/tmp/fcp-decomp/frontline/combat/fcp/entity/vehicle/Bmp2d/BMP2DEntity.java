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
package frontline.combat.fcp.entity.vehicle.Bmp2d;

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

public class BMP2DEntity
extends CamoVehicleBase {
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_1_1_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_1_afgan.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_2_kom_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_2_kom_2.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_2.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_fin_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_zov_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_zov_2.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_gdr_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_gdr_2.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_gdr_3.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_kom_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_2.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_3_v.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_3.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_4.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_5.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_c_kom_1.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_1_1_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_1_afgan_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_2_kom_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_2_kom_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_ukr_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_fin_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_zov_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_zov_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_gdr_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_gdr_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_gdr_3_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_kom_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_1_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_2_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_3_v_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_3_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_4_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_3_rus_5_wrecked.png"), new ResourceLocation("fcp", "textures/entity/bmp1_2/bmp_2_rem_tex_c_kom_1_wrecked.png")};
    private static final String[] CAMO_NAMES = new String[]{"T1 1 1", "T1 Afgan", "T1", "T2 Ukr 1", "T2 Ukr 2 Kom 1", "T2 Ukr 2 Kom 2", "T2 Ukr 2", "T2", "T3 Fin 1", "T3 Rus Zov 1", "T3 Rus Zov 2", "T3 Rus 1 Gdr 1", "T3 Rus 1 Gdr 2", "T3 Rus 1 Gdr 3", "T3 Rus 1 Kom 1", "T3 Rus 1", "T3 Rus 2", "T3 Rus 3 V", "T3 Rus 3", "T3 Rus 4", "T3 Rus 5", "Tc Kom 1"};
    private static final EntityDataAccessor<Float> STEERING_ANGLE = SynchedEntityData.m_135353_(BMP2DEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private float prevSteeringAngle = 0.0f;
    private float wheelRotation = 0.0f;
    private float prevWheelRotation = 0.0f;

    public BMP2DEntity(EntityType<BMP2DEntity> type, Level world) {
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

    public void setSteeringAngle(float angle) {
        this.f_19804_.m_135381_(STEERING_ANGLE, (Object)Float.valueOf(angle));
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
    public void m_7380_(CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128350_("SteeringAngle", this.getSteeringAngle());
    }

    @Override
    public void m_7378_(CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("SteeringAngle")) {
            this.setSteeringAngle(compound.m_128457_("SteeringAngle"));
        }
    }

    public boolean GetWeaponState(String WeaponName, int Count) {
        if (this.getAmmoCount(WeaponName) == Count) {
            return true;
        }
        return this.getAmmoCount(WeaponName) < Count;
    }

    public void m_6075_() {
        super.m_6075_();
        this.prevSteeringAngle = this.getSteeringAngle();
        float currentAngle = this.getSteeringAngle();
        double speed = Math.sqrt(this.m_20184_().f_82479_ * this.m_20184_().f_82479_ + this.m_20184_().f_82481_ * this.m_20184_().f_82481_);
        boolean isMoving = speed > 0.05;
        boolean turningLeft = this.leftInputDown();
        boolean turningRight = this.rightInputDown();
        if (turningLeft && !turningRight) {
            currentAngle += 2.0f;
            currentAngle = Math.min(45.0f, currentAngle);
            this.setSteeringAngle(currentAngle);
        } else if (turningRight && !turningLeft) {
            currentAngle -= 2.0f;
            currentAngle = Math.max(-45.0f, currentAngle);
            this.setSteeringAngle(currentAngle);
        } else if (isMoving && Math.abs(currentAngle) > 0.5f) {
            this.setSteeringAngle(currentAngle *= 0.9f);
        }
        if (isMoving && Math.abs(currentAngle) > 1.0f) {
            float turnAmount = currentAngle * 0.009f * (float)speed;
            this.m_146922_(this.m_146908_() + turnAmount);
        }
        this.prevWheelRotation = this.wheelRotation;
        this.wheelRotation += (float)(speed * 20.0);
    }
}

