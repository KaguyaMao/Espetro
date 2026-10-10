/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.FireMode
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.mojang.logging.LogUtils
 *  net.minecraft.ChatFormatting
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.util.Mth
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  org.slf4j.Logger
 */
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.data.gun.FireMode;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.damage.DamageModifier;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.slf4j.Logger;
import tech.vvp.vvp.entity.vehicle.CamoVehicleBase;
import tech.vvp.vvp.event.GmlrsRocketHandler;
import tech.vvp.vvp.firecontrol.HimarsBallisticsUtil;

public class HimarsEntity
extends CamoVehicleBase {
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final String GMLRS_WEAPON = "GMLRS";
    public static final int OPERATOR_SEAT = 2;
    private static final EntityDataAccessor<Boolean> FDC_HAS_TARGET = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Boolean> FDC_SLEWING = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Float> FDC_AIM_DIR_X = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> FDC_AIM_DIR_Y = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> FDC_AIM_DIR_Z = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Boolean> FDC_AWAITING_FIRE = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Float> FDC_TARGET_TURRET_YAW = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> FDC_TARGET_TURRET_PITCH = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Integer> FDC_TARGET_BLOCK_X = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FDC_TARGET_BLOCK_Y = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FDC_TARGET_BLOCK_Z = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final double MAX_FIRE_SPEED = 0.028;
    private static final int AIM_STABLE_TICKS = 6;
    private static final double AIM_DOT_THRESHOLD = 0.992;
    private static final float FDC_AIM_YAW_TOLERANCE = 6.0f;
    private static final float FDC_AIM_PITCH_TOLERANCE = 6.0f;
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("vvp", "textures/entity/m142_himars.png"), new ResourceLocation("vvp", "textures/entity/m142_himars_sandy.png"), new ResourceLocation("vvp", "textures/entity/m142_himars_green_2.png"), new ResourceLocation("vvp", "textures/entity/m142_himars.png")};
    private static final String[] CAMO_NAMES = new String[]{"Green", "Desert", "Woodland", "Olive"};
    private static final EntityDataAccessor<Float> STEERING_ANGLE = SynchedEntityData.m_135353_(HimarsEntity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private float wheelRotation = 0.0f;
    private float prevWheelRotation = 0.0f;
    private float prevSteeringAngle = 0.0f;
    private boolean fdcAwaitingFire;
    private int fdcAlignTicks;
    private int fdcTargetX;
    private int fdcTargetY;
    private int fdcTargetZ;

    public HimarsEntity(EntityType<HimarsEntity> type, Level world) {
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
        this.f_19804_.m_135372_(FDC_HAS_TARGET, (Object)false);
        this.f_19804_.m_135372_(FDC_SLEWING, (Object)false);
        this.f_19804_.m_135372_(FDC_AWAITING_FIRE, (Object)false);
        this.f_19804_.m_135372_(FDC_AIM_DIR_X, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(FDC_AIM_DIR_Y, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(FDC_AIM_DIR_Z, (Object)Float.valueOf(1.0f));
        this.f_19804_.m_135372_(FDC_TARGET_TURRET_YAW, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(FDC_TARGET_TURRET_PITCH, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(FDC_TARGET_BLOCK_X, (Object)0);
        this.f_19804_.m_135372_(FDC_TARGET_BLOCK_Y, (Object)0);
        this.f_19804_.m_135372_(FDC_TARGET_BLOCK_Z, (Object)0);
    }

    public boolean isFdcTargetDesignated() {
        return (Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET);
    }

    public boolean isFdcSlewing() {
        return (Boolean)this.f_19804_.m_135370_(FDC_SLEWING);
    }

    @Deprecated
    public boolean isFdcAutoAimActive() {
        return this.isFdcSlewing();
    }

    public boolean isFdcAwaitingFire() {
        return (Boolean)this.f_19804_.m_135370_(FDC_AWAITING_FIRE);
    }

    public boolean isFdcBarrelAligned() {
        return this.isTurretOnTarget();
    }

    private boolean isTurretOnTarget() {
        if (!((Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET)).booleanValue()) {
            return false;
        }
        float yawErr = Math.abs(Mth.m_14177_((float)(((Float)this.f_19804_.m_135370_(FDC_TARGET_TURRET_YAW)).floatValue() - this.getTurretYRot())));
        float pitchErr = Math.abs(((Float)this.f_19804_.m_135370_(FDC_TARGET_TURRET_PITCH)).floatValue() - this.getTurretXRot());
        return yawErr <= 6.0f && pitchErr <= 6.0f;
    }

    public int getFdcTargetX() {
        return (Integer)this.f_19804_.m_135370_(FDC_TARGET_BLOCK_X);
    }

    public int getFdcTargetY() {
        return (Integer)this.f_19804_.m_135370_(FDC_TARGET_BLOCK_Y);
    }

    public int getFdcTargetZ() {
        return (Integer)this.f_19804_.m_135370_(FDC_TARGET_BLOCK_Z);
    }

    public float getWheelRotation() {
        return this.wheelRotation;
    }

    public float getPrevWheelRotation() {
        return this.prevWheelRotation;
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

    public double getHorizontalSpeed() {
        Vec3 motion = this.m_20184_();
        return Math.sqrt(motion.f_82479_ * motion.f_82479_ + motion.f_82481_ * motion.f_82481_);
    }

    public boolean isStationaryForFire() {
        return this.getHorizontalSpeed() <= 0.028;
    }

    @Override
    public DamageModifier getDamageModifier() {
        return super.getDamageModifier().custom((source, damage) -> Float.valueOf(this.getSourceAngle((DamageSource)source, 0.35f) * damage.floatValue()));
    }

    @Override
    public void m_7380_(CompoundTag compound) {
        super.m_7380_(compound);
        compound.m_128350_("SteeringAngle", this.getSteeringAngle());
        compound.m_128379_("FdcAwaitingFire", this.fdcAwaitingFire);
        compound.m_128379_("FdcDesignated", ((Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET)).booleanValue());
        compound.m_128405_("FdcTargetX", this.fdcTargetX);
        compound.m_128405_("FdcTargetY", this.fdcTargetY);
        compound.m_128405_("FdcTargetZ", this.fdcTargetZ);
    }

    @Override
    public void m_7378_(CompoundTag compound) {
        super.m_7378_(compound);
        if (compound.m_128441_("SteeringAngle")) {
            this.setSteeringAngle(compound.m_128457_("SteeringAngle"));
        }
        if (compound.m_128441_("FdcAwaitingFire")) {
            this.fdcAwaitingFire = compound.m_128471_("FdcAwaitingFire");
            this.fdcTargetX = compound.m_128451_("FdcTargetX");
            this.fdcTargetY = compound.m_128451_("FdcTargetY");
            this.fdcTargetZ = compound.m_128451_("FdcTargetZ");
            if (compound.m_128441_("FdcDesignated") && compound.m_128471_("FdcDesignated")) {
                this.f_19804_.m_135381_(FDC_HAS_TARGET, (Object)true);
                this.f_19804_.m_135381_(FDC_AWAITING_FIRE, (Object)this.fdcAwaitingFire);
                this.f_19804_.m_135381_(FDC_TARGET_BLOCK_X, (Object)this.fdcTargetX);
                this.f_19804_.m_135381_(FDC_TARGET_BLOCK_Y, (Object)this.fdcTargetY);
                this.f_19804_.m_135381_(FDC_TARGET_BLOCK_Z, (Object)this.fdcTargetZ);
            }
        }
    }

    public void adjustTurretAngle() {
        if (this.shouldRunFdcTurretSlew()) {
            this.performFdcTurretSlew();
        }
    }

    private boolean shouldRunFdcTurretSlew() {
        return (Boolean)this.f_19804_.m_135370_(FDC_SLEWING) != false || (Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET) != false && !this.isTurretOnTarget();
    }

    private boolean isFdcMissionActive() {
        return (Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET) != false || (Boolean)this.f_19804_.m_135370_(FDC_SLEWING) != false || (Boolean)this.f_19804_.m_135370_(FDC_AWAITING_FIRE) != false;
    }

    public void m_6075_() {
        super.m_6075_();
        this.prevSteeringAngle = this.getSteeringAngle();
        float currentAngle = this.getSteeringAngle();
        double speed = this.getHorizontalSpeed();
        boolean isMoving = speed > 0.05;
        boolean turningLeft = this.leftInputDown();
        boolean turningRight = this.rightInputDown();
        if (turningLeft && !turningRight) {
            currentAngle += 1.8f;
            currentAngle = Math.min(40.0f, currentAngle);
            this.setSteeringAngle(currentAngle);
        } else if (turningRight && !turningLeft) {
            currentAngle -= 1.8f;
            currentAngle = Math.max(-40.0f, currentAngle);
            this.setSteeringAngle(currentAngle);
        } else if (isMoving && Math.abs(currentAngle) > 0.5f) {
            this.setSteeringAngle(currentAngle *= 0.88f);
        }
        if (isMoving && Math.abs(currentAngle) > 1.0f) {
            float turnAmount = currentAngle * 0.007f * (float)speed;
            this.m_146922_(this.m_146908_() + turnAmount);
        }
        this.prevWheelRotation = this.wheelRotation;
        this.wheelRotation += (float)(speed * 20.0);
        if (!this.m_9236_().f_46443_) {
            if (((Boolean)this.f_19804_.m_135370_(FDC_SLEWING)).booleanValue() && this.isTurretOnTarget()) {
                this.f_19804_.m_135381_(FDC_SLEWING, (Object)false);
                this.setTurretYRotLock(0.0f);
            }
            this.tickFdcOperatorPresence();
            this.tickFdcMission();
        }
    }

    private void performFdcTurretSlew() {
        this.slewTurretTowardAngles();
        if (!this.m_9236_().m_5776_() && this.f_19797_ % 15 == 0) {
            float targetYaw = ((Float)this.f_19804_.m_135370_(FDC_TARGET_TURRET_YAW)).floatValue();
            float targetPitch = ((Float)this.f_19804_.m_135370_(FDC_TARGET_TURRET_PITCH)).floatValue();
            float yawDiff = Math.abs(Mth.m_14177_((float)(targetYaw - this.getTurretYRot())));
            float pitchDiff = Math.abs(targetPitch - this.getTurretXRot());
            if (yawDiff > 0.1f || pitchDiff > 0.1f) {
                this.m_9236_().m_6263_(null, this.m_20185_(), this.m_20186_(), this.m_20189_(), (SoundEvent)ModSounds.TURRET_TURN.get(), SoundSource.NEUTRAL, 0.6f, 1.0f);
            }
        }
    }

    private Vec3 getFdcTargetBlockPos() {
        if (!((Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET)).booleanValue()) {
            return null;
        }
        return new Vec3((double)((Integer)this.f_19804_.m_135370_(FDC_TARGET_BLOCK_X)).intValue() + 0.5, (double)((Integer)this.f_19804_.m_135370_(FDC_TARGET_BLOCK_Y)).intValue(), (double)((Integer)this.f_19804_.m_135370_(FDC_TARGET_BLOCK_Z)).intValue() + 0.5);
    }

    private void setFdcTargetBlocks(int blockX, int blockY, int blockZ) {
        this.fdcTargetX = blockX;
        this.fdcTargetY = blockY;
        this.fdcTargetZ = blockZ;
        this.f_19804_.m_135381_(FDC_TARGET_BLOCK_X, (Object)blockX);
        this.f_19804_.m_135381_(FDC_TARGET_BLOCK_Y, (Object)blockY);
        this.f_19804_.m_135381_(FDC_TARGET_BLOCK_Z, (Object)blockZ);
    }

    private void slewTurretTowardAngles() {
        float targetYaw = ((Float)this.f_19804_.m_135370_(FDC_TARGET_TURRET_YAW)).floatValue();
        float targetPitch = ((Float)this.f_19804_.m_135370_(FDC_TARGET_TURRET_PITCH)).floatValue();
        float yawDiff = Mth.m_14177_((float)(targetYaw - this.getTurretYRot()));
        float pitchDiff = targetPitch - this.getTurretXRot();
        float yawStep = Mth.m_14036_((float)yawDiff, (float)(-this.getTurretTurnYSpeed()), (float)this.getTurretTurnYSpeed());
        float pitchStep = Mth.m_14036_((float)pitchDiff, (float)(-this.getTurretTurnXSpeed()), (float)this.getTurretTurnXSpeed());
        this.setTurretYRot(this.getTurretYRot() + yawStep);
        this.setTurretXRot(Mth.m_14036_((float)(this.getTurretXRot() + pitchStep), (float)(-this.getTurretMaxPitch()), (float)(-this.getTurretMinPitch())));
    }

    private void tickFdcOperatorPresence() {
        if (!(((Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET)).booleanValue() || ((Boolean)this.f_19804_.m_135370_(FDC_SLEWING)).booleanValue() || this.fdcAwaitingFire)) {
            return;
        }
        if (this.getFdcOperator() == null) {
            this.cancelFdcMission();
        }
    }

    public boolean designateFdcTarget(LivingEntity operator, int blockX, int blockY, int blockZ) {
        int seatIdx = this.getSeatIndex((Entity)operator);
        LOGGER.debug("[HIMARS] designateFdcTarget: seatIdx={} target=({},{},{}) stationary={}", new Object[]{seatIdx, blockX, blockY, blockZ, this.isStationaryForFire()});
        FdcAimSolution aim = this.resolveFdcAim(operator, blockX, blockY, blockZ);
        if (aim == null) {
            LOGGER.debug("[HIMARS] designateFdcTarget: resolveFdcAim returned null (out of arc or wrong seat)");
            return false;
        }
        this.setFdcTargetBlocks(blockX, blockY, blockZ);
        this.fdcAwaitingFire = false;
        this.fdcAlignTicks = 0;
        this.f_19804_.m_135381_(FDC_HAS_TARGET, (Object)true);
        this.f_19804_.m_135381_(FDC_SLEWING, (Object)true);
        this.f_19804_.m_135381_(FDC_AWAITING_FIRE, (Object)false);
        this.f_19804_.m_135381_(FDC_AIM_DIR_X, (Object)Float.valueOf((float)aim.direction().f_82479_));
        this.f_19804_.m_135381_(FDC_AIM_DIR_Y, (Object)Float.valueOf((float)aim.direction().f_82480_));
        this.f_19804_.m_135381_(FDC_AIM_DIR_Z, (Object)Float.valueOf((float)aim.direction().f_82481_));
        this.f_19804_.m_135381_(FDC_TARGET_TURRET_YAW, (Object)Float.valueOf(this.shortestTurretYaw(aim.solution().targetTurretYaw())));
        this.f_19804_.m_135381_(FDC_TARGET_TURRET_PITCH, (Object)Float.valueOf(aim.solution().targetTurretPitch()));
        if (operator instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)operator;
            serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.slewing").m_130940_(ChatFormatting.AQUA), true);
        }
        return true;
    }

    public boolean authorizeFdcFire(LivingEntity operator) {
        if (this.getSeatIndex((Entity)operator) != 2) {
            return false;
        }
        GunData gunData = this.getGunData(GMLRS_WEAPON);
        if (gunData == null || gunData.ammo.get() <= 0) {
            if (operator instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)operator;
                serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.out_of_ammo").m_130940_(ChatFormatting.RED), true);
            }
            return false;
        }
        if (!((Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET)).booleanValue()) {
            if (operator instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)operator;
                serverPlayer.m_5661_((Component)Component.m_237115_((String)"label.vvp.fdc.no_target").m_130940_(ChatFormatting.RED), true);
            }
            return false;
        }
        if (!this.isStationaryForFire()) {
            if (operator instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)operator;
                serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.must_be_stationary").m_130940_(ChatFormatting.RED), true);
            }
            return false;
        }
        FdcAimSolution aim = this.resolveFdcAim(operator, this.fdcTargetX, this.fdcTargetY, this.fdcTargetZ);
        if (aim == null) {
            if (operator instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)operator;
                serverPlayer.m_5661_((Component)Component.m_237115_((String)"label.vvp.fdc.out_of_arc").m_130940_(ChatFormatting.RED), true);
            }
            return false;
        }
        this.f_19804_.m_135381_(FDC_TARGET_TURRET_YAW, (Object)Float.valueOf(this.shortestTurretYaw(aim.solution().targetTurretYaw())));
        this.f_19804_.m_135381_(FDC_TARGET_TURRET_PITCH, (Object)Float.valueOf(aim.solution().targetTurretPitch()));
        this.fdcAwaitingFire = true;
        this.fdcAlignTicks = 0;
        this.f_19804_.m_135381_(FDC_AWAITING_FIRE, (Object)true);
        if (!this.isTurretOnTarget()) {
            this.f_19804_.m_135381_(FDC_SLEWING, (Object)true);
        }
        if (operator instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)operator;
            serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.fire_authorized").m_130940_(ChatFormatting.GREEN), true);
        }
        return true;
    }

    private FdcAimSolution resolveFdcAim(LivingEntity operator, int blockX, int blockY, int blockZ) {
        if (this.getSeatIndex((Entity)operator) != 2) {
            return null;
        }
        if (!this.isStationaryForFire()) {
            if (operator instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)operator;
                serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.must_be_stationary").m_130940_(ChatFormatting.RED), true);
            }
            return null;
        }
        Vec3 origin = HimarsBallisticsUtil.resolveShootOrigin(this);
        HimarsBallisticsUtil.FireSolution solution = HimarsBallisticsUtil.solve(this, origin, (double)blockX + 0.5, blockY, (double)blockZ + 0.5);
        if (!solution.inArc()) {
            return null;
        }
        Vec3 target = new Vec3(solution.targetX(), solution.targetY(), solution.targetZ());
        Vec3 direction = target.m_82546_(origin);
        if (direction.m_82556_() < 1.0E-6) {
            return null;
        }
        return new FdcAimSolution(direction.m_82541_(), solution);
    }

    public void cancelFdcMission() {
        this.fdcAwaitingFire = false;
        this.fdcAlignTicks = 0;
        this.f_19804_.m_135381_(FDC_HAS_TARGET, (Object)false);
        this.f_19804_.m_135381_(FDC_SLEWING, (Object)false);
        this.f_19804_.m_135381_(FDC_AWAITING_FIRE, (Object)false);
        this.f_19804_.m_135381_(FDC_TARGET_BLOCK_X, (Object)0);
        this.f_19804_.m_135381_(FDC_TARGET_BLOCK_Y, (Object)0);
        this.f_19804_.m_135381_(FDC_TARGET_BLOCK_Z, (Object)0);
        this.setTurretYRotLock(0.0f);
    }

    private void tickFdcMission() {
        FireMode mode;
        if (!this.fdcAwaitingFire || !((Boolean)this.f_19804_.m_135370_(FDC_HAS_TARGET)).booleanValue()) {
            return;
        }
        LivingEntity operator = this.getFdcOperator();
        if (operator == null) {
            this.cancelFdcMission();
            return;
        }
        if (!this.isStationaryForFire()) {
            this.fdcAlignTicks = 0;
            return;
        }
        if (((Boolean)this.f_19804_.m_135370_(FDC_SLEWING)).booleanValue()) {
            this.fdcAlignTicks = 0;
            return;
        }
        if (!this.isTurretOnTarget()) {
            this.fdcAlignTicks = 0;
            this.f_19804_.m_135381_(FDC_SLEWING, (Object)true);
            return;
        }
        ++this.fdcAlignTicks;
        if (this.fdcAlignTicks < 6) {
            return;
        }
        GunData gunData = this.getGunData(GMLRS_WEAPON);
        if (gunData == null) {
            this.cancelFdcMission();
            return;
        }
        if (!gunData.canShoot((Entity)this)) {
            this.cancelFdcMission();
            if (operator instanceof ServerPlayer) {
                ServerPlayer serverPlayer = (ServerPlayer)operator;
                if (gunData.ammo.get() <= 0) {
                    serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.out_of_ammo").m_130940_(ChatFormatting.RED), true);
                } else {
                    serverPlayer.m_5661_((Component)Component.m_237115_((String)"message.vvp.fdc.fired").m_130944_(new ChatFormatting[]{ChatFormatting.RED, ChatFormatting.BOLD}), true);
                }
            }
            return;
        }
        if (!this.fireFdcShot(operator)) {
            return;
        }
        GunData updatedGunData = this.getGunData(GMLRS_WEAPON);
        if (updatedGunData != null && ((mode = updatedGunData.selectedFireModeInfo().mode) == FireMode.SEMI || mode == FireMode.BURST && updatedGunData.burstAmount.get() == 0)) {
            this.fdcAwaitingFire = false;
            this.f_19804_.m_135381_(FDC_AWAITING_FIRE, (Object)false);
        }
        this.fdcAlignTicks = 0;
    }

    private boolean fireFdcShot(LivingEntity operator) {
        Level level = this.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return false;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        GunData currentGunData = this.getGunData(GMLRS_WEAPON);
        if (currentGunData == null || !currentGunData.canShoot((Entity)this)) {
            return false;
        }
        Vec3 shootPos = this.getShootPos(GMLRS_WEAPON, 1.0f);
        Vec3 shootDir = this.getShootVec(GMLRS_WEAPON, 1.0f);
        if (shootDir.m_82556_() <= 1.0E-6) {
            return false;
        }
        Vec3 shootDirNorm = shootDir.m_82541_();
        this.alignOperatorViewToBarrel(operator);
        boolean[] success = new boolean[]{false};
        this.modifyGunData(GMLRS_WEAPON, gunData -> {
            try {
                Vec3 target;
                double spread = this.getProjectileSpread((GunData)gunData);
                GmlrsRocketHandler.lastGmlrsTarget = target = new Vec3((double)this.fdcTargetX + 0.5, (double)this.fdcTargetY, (double)this.fdcTargetZ + 0.5);
                gunData.shoot(new ShootParameters((Entity)this, (Entity)operator, serverLevel, shootPos, shootDirNorm, gunData, spread, false, null, target));
                this.afterShoot((GunData)gunData, shootDirNorm);
                this.playShootSound3p(operator, GMLRS_WEAPON);
                success[0] = true;
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
        });
        return success[0];
    }

    private void alignOperatorViewToBarrel(LivingEntity operator) {
        Vec3 barrel = this.getShootVec(GMLRS_WEAPON, 1.0f);
        if (barrel.m_82556_() < 1.0E-6) {
            return;
        }
        barrel = barrel.m_82541_();
        float yaw = (float)(Mth.m_14136_((double)barrel.f_82481_, (double)barrel.f_82479_) * 57.2957763671875) - 90.0f;
        float pitch = (float)(Mth.m_14136_((double)(-barrel.f_82480_), (double)Math.sqrt(barrel.f_82479_ * barrel.f_82479_ + barrel.f_82481_ * barrel.f_82481_)) * 57.2957763671875);
        operator.m_146922_(yaw);
        operator.m_146926_(pitch);
        operator.f_19859_ = yaw;
        operator.f_19860_ = pitch;
    }

    private float shortestTurretYaw(float targetYaw) {
        return this.getTurretYRot() + Mth.m_14177_((float)(targetYaw - this.getTurretYRot()));
    }

    private Vec3 getFdcAimDirection() {
        return new Vec3((double)((Float)this.f_19804_.m_135370_(FDC_AIM_DIR_X)).floatValue(), (double)((Float)this.f_19804_.m_135370_(FDC_AIM_DIR_Y)).floatValue(), (double)((Float)this.f_19804_.m_135370_(FDC_AIM_DIR_Z)).floatValue());
    }

    private LivingEntity getFdcOperator() {
        for (Entity passenger : this.m_20197_()) {
            LivingEntity living;
            if (!(passenger instanceof LivingEntity) || this.getSeatIndex((Entity)(living = (LivingEntity)passenger)) != 2) continue;
            return living;
        }
        return null;
    }

    public boolean banHand(LivingEntity entity) {
        if (this.getSeatIndex((Entity)entity) == 2) {
            return false;
        }
        return super.banHand(entity);
    }

    public boolean canShoot(LivingEntity living) {
        if (this.getSeatIndex((Entity)living) != 2) {
            return super.canShoot(living);
        }
        if (!this.isStationaryForFire()) {
            return false;
        }
        GunData gunData = this.getGunData(GMLRS_WEAPON);
        return gunData != null && gunData.canShoot(this.getAmmoSupplier());
    }

    private record FdcAimSolution(Vec3 direction, HimarsBallisticsUtil.FireSolution solution) {
    }
}

