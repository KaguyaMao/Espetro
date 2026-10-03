/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModItems
 *  com.atsuishio.superbwarfare.item.misc.FiringParametersItem$Parameters
 *  com.atsuishio.superbwarfare.item.misc.FiringParametersItemKt
 *  net.minecraft.core.BlockPos
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.world.InteractionHand
 *  net.minecraft.world.InteractionResult
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 */
package frontline.combat.fcp.entity.vehicle;

import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModItems;
import com.atsuishio.superbwarfare.item.misc.FiringParametersItem;
import com.atsuishio.superbwarfare.item.misc.FiringParametersItemKt;
import frontline.combat.fcp.entity.vehicle.CamoVehicleBase;
import frontline.combat.fcp.entity.vehicle.IndirectFireVehicle;
import frontline.combat.fcp.firecontrol.FireControlComputation;
import frontline.combat.fcp.firecontrol.FireControlSolution;
import frontline.combat.fcp.firecontrol.FireControlStatus;
import frontline.combat.fcp.firecontrol.IndirectFireBallistics;
import frontline.combat.fcp.firecontrol.TrajectoryMode;
import frontline.combat.fcp.integration.EsWeatherFireControlBridge;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public abstract class IndirectFireVehicleBase
extends CamoVehicleBase
implements IndirectFireVehicle {
    private static final EntityDataAccessor<Boolean> FIRE_CONTROL_ACTIVE = SynchedEntityData.m_135353_(IndirectFireVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<BlockPos> FIRE_CONTROL_TARGET = SynchedEntityData.m_135353_(IndirectFireVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135038_);
    private static final EntityDataAccessor<Integer> FIRE_CONTROL_RADIUS = SynchedEntityData.m_135353_(IndirectFireVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FIRE_CONTROL_TRAJECTORY = SynchedEntityData.m_135353_(IndirectFireVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final EntityDataAccessor<Integer> FIRE_CONTROL_STATUS = SynchedEntityData.m_135353_(IndirectFireVehicleBase.class, (EntityDataSerializer)EntityDataSerializers.f_135028_);
    private static final double MOVEMENT_THRESHOLD = 0.05;
    private static final int STABILIZATION_TICKS = 10;
    private static final double READY_TOLERANCE_DEGREES = 1.0;
    private static final int MAX_SAMPLE_ATTEMPTS = 16;
    private int stationaryTicks;
    private int lastBlockedMessageTick = -1000;

    protected IndirectFireVehicleBase(EntityType<? extends VehicleEntity> type, Level level) {
        super(type, level);
    }

    @Override
    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(FIRE_CONTROL_ACTIVE, (Object)false);
        this.f_19804_.m_135372_(FIRE_CONTROL_TARGET, (Object)BlockPos.f_121853_);
        this.f_19804_.m_135372_(FIRE_CONTROL_RADIUS, (Object)0);
        this.f_19804_.m_135372_(FIRE_CONTROL_TRAJECTORY, (Object)TrajectoryMode.LOW.ordinal());
        this.f_19804_.m_135372_(FIRE_CONTROL_STATUS, (Object)FireControlStatus.INACTIVE.ordinal());
    }

    @Override
    public boolean isFireControlActive() {
        return (Boolean)this.f_19804_.m_135370_(FIRE_CONTROL_ACTIVE);
    }

    @Override
    public BlockPos getFireControlTarget() {
        return (BlockPos)this.f_19804_.m_135370_(FIRE_CONTROL_TARGET);
    }

    @Override
    public int getFireControlRadius() {
        return (Integer)this.f_19804_.m_135370_(FIRE_CONTROL_RADIUS);
    }

    @Override
    public TrajectoryMode getFireControlTrajectory() {
        return TrajectoryMode.fromId((Integer)this.f_19804_.m_135370_(FIRE_CONTROL_TRAJECTORY));
    }

    @Override
    public FireControlStatus getFireControlStatus() {
        return FireControlStatus.fromId((Integer)this.f_19804_.m_135370_(FIRE_CONTROL_STATUS));
    }

    protected void setFireControlStatus(FireControlStatus status) {
        if (!this.m_9236_().f_46443_) {
            this.f_19804_.m_135381_(FIRE_CONTROL_STATUS, (Object)status.ordinal());
        }
    }

    @Override
    public FireControlComputation getFireControlComputation() {
        return IndirectFireBallistics.solve((VehicleEntity)this, this.getTurretControllerIndex(), this.getFireControlTarget(), this.getFireControlTrajectory());
    }

    @Override
    public boolean applyFireControl(BlockPos target, int radius, TrajectoryMode mode, Entity actor) {
        if (this.m_9236_().f_46443_) {
            return false;
        }
        if (EsWeatherFireControlBridge.isDisrupted((Entity)this)) {
            IndirectFireVehicleBase.notifyActor(actor, "message.fcp.fire_control.weather_jammed");
            return false;
        }
        if (this.isWreck()) {
            IndirectFireVehicleBase.notifyActor(actor, FireControlStatus.WRECKED.translationKey());
            return false;
        }
        if (radius < 0 || radius > 99 || target.m_123342_() < this.m_9236_().m_141937_() || target.m_123342_() >= this.m_9236_().m_151558_() || !this.m_9236_().m_6857_().m_61937_(target)) {
            IndirectFireVehicleBase.notifyActor(actor, "message.fcp.fire_control.invalid_input");
            return false;
        }
        FireControlComputation computation = IndirectFireBallistics.solve((VehicleEntity)this, this.getTurretControllerIndex(), target, mode);
        if (!computation.isSuccess()) {
            IndirectFireVehicleBase.notifyActor(actor, computation.status().translationKey());
            return false;
        }
        this.f_19804_.m_135381_(FIRE_CONTROL_TARGET, (Object)target.m_7949_());
        this.f_19804_.m_135381_(FIRE_CONTROL_RADIUS, (Object)radius);
        this.f_19804_.m_135381_(FIRE_CONTROL_TRAJECTORY, (Object)mode.ordinal());
        this.f_19804_.m_135381_(FIRE_CONTROL_ACTIVE, (Object)true);
        this.stationaryTicks = 0;
        this.setFireControlStatus(this.isMovingForFireControl() ? FireControlStatus.MOVING : FireControlStatus.ALIGNING);
        if (actor instanceof Player) {
            Player player = (Player)actor;
            player.m_5661_((Component)Component.m_237110_((String)"message.fcp.fire_control.applied", (Object[])new Object[]{target.m_123341_(), target.m_123342_(), target.m_123343_(), radius, Component.m_237115_((String)mode.translationKey())}), true);
        }
        return true;
    }

    @Override
    public void clearFireControl(Entity actor) {
        if (this.m_9236_().f_46443_) {
            return;
        }
        this.f_19804_.m_135381_(FIRE_CONTROL_ACTIVE, (Object)false);
        this.f_19804_.m_135381_(FIRE_CONTROL_STATUS, (Object)FireControlStatus.INACTIVE.ordinal());
        this.stationaryTicks = 0;
        IndirectFireVehicleBase.notifyActor(actor, "message.fcp.fire_control.cleared");
    }

    @Override
    public InteractionResult m_6096_(Player player, InteractionHand hand) {
        ItemStack stack = player.m_21120_(hand);
        if (player.m_6144_() && stack.m_150930_((Item)ModItems.FIRING_PARAMETERS.get())) {
            if (!this.m_9236_().f_46443_) {
                FiringParametersItem.Parameters parameters = FiringParametersItemKt.getFiringParameters((ItemStack)stack);
                this.applyFireControl(parameters.pos(), parameters.radius(), TrajectoryMode.fromFiringParameters(parameters.isDepressed()), (Entity)player);
            }
            player.m_6674_(hand);
            return InteractionResult.m_19078_((boolean)this.m_9236_().f_46443_);
        }
        return super.m_6096_(player, hand);
    }

    public void m_6075_() {
        super.m_6075_();
        this.tickFireControl();
    }

    private void tickFireControl() {
        if (!this.isFireControlActive()) {
            this.stationaryTicks = 0;
            this.setFireControlStatus(FireControlStatus.INACTIVE);
            return;
        }
        if (EsWeatherFireControlBridge.isDisrupted((Entity)this)) {
            this.clearFireControl(null);
            return;
        }
        if (this.isWreck()) {
            this.stationaryTicks = 0;
            this.setFireControlStatus(FireControlStatus.WRECKED);
            return;
        }
        if (this.isMovingForFireControl()) {
            this.stationaryTicks = 0;
            this.setFireControlStatus(FireControlStatus.MOVING);
            return;
        }
        if (this.stationaryTicks < 10) {
            ++this.stationaryTicks;
            this.setFireControlStatus(FireControlStatus.MOVING);
            return;
        }
        FireControlComputation computation = this.getFireControlComputation();
        if (!computation.isSuccess()) {
            this.setFireControlStatus(computation.status());
            return;
        }
        this.setMouseMoveSpeedX(0.0f);
        this.setMouseMoveSpeedY(0.0f);
        FireControlSolution solution = computation.solution();
        Vec3 desired = solution.direction();
        this.turretAutoAimFromVector(desired);
        Vec3 actual = this.getBarrelVector(1.0f);
        if (actual == null || actual.m_82556_() < 1.0E-8) {
            actual = this.getShootVec(this.getTurretControllerIndex(), 1.0f);
        }
        if (actual == null || actual.m_82556_() < 1.0E-8 || desired.m_82556_() < 1.0E-8) {
            this.setFireControlStatus(FireControlStatus.ALIGNING);
            return;
        }
        double dot = Mth.m_14008_((double)actual.m_82541_().m_82526_(desired.m_82541_()), (double)-1.0, (double)1.0);
        double error = Math.toDegrees(Math.acos(dot));
        this.setFireControlStatus(error <= 1.0 ? FireControlStatus.READY : FireControlStatus.ALIGNING);
    }

    protected boolean canStartIndirectShot(LivingEntity shooter) {
        if (!this.isFireControlActive()) {
            return true;
        }
        if (this.getFireControlStatus() != FireControlStatus.READY || this.isMovingForFireControl()) {
            this.notifyBlocked(shooter, this.getFireControlStatus());
            return false;
        }
        return true;
    }

    protected boolean fireIndirectRound(LivingEntity shooter, UUID targetEntityUuid, boolean playShootSound) {
        ServerLevel server;
        block10: {
            block9: {
                Level level = this.m_9236_();
                if (!(level instanceof ServerLevel)) break block9;
                server = (ServerLevel)level;
                if (this.isFireControlActive() && this.canStartIndirectShot(shooter)) break block10;
            }
            return false;
        }
        int seatIndex = this.getSeatIndex((Entity)shooter);
        GunData data = this.getGunData(seatIndex);
        if (seatIndex < 0 || data == null || !data.canShoot(this.getAmmoSupplier())) {
            return false;
        }
        FireControlComputation computation = null;
        Vec3 sampledTarget = null;
        for (int i = 0; i < 16; ++i) {
            Vec3 candidate = IndirectFireBallistics.sampleTarget(this.getFireControlTarget(), this.getFireControlRadius(), this.getRandom());
            FireControlComputation candidateComputation = IndirectFireBallistics.solve((VehicleEntity)this, seatIndex, candidate, this.getFireControlTrajectory());
            if (!candidateComputation.isSuccess()) continue;
            sampledTarget = candidate;
            computation = candidateComputation;
            break;
        }
        if (computation == null) {
            sampledTarget = this.getFireControlTarget().m_252807_();
            computation = IndirectFireBallistics.solve((VehicleEntity)this, seatIndex, sampledTarget, this.getFireControlTrajectory());
        }
        if (!computation.isSuccess()) {
            this.setFireControlStatus(computation.status());
            this.notifyBlocked(shooter, computation.status());
            return false;
        }
        Vec3 direction = computation.solution().direction();
        Vec3 shootPosition = this.getShootPos((Entity)shooter, 1.0f);
        boolean[] fired = new boolean[]{false};
        Vec3 finalSampledTarget = sampledTarget;
        this.modifyGunData(seatIndex, current -> {
            if (current.canShoot(this.getAmmoSupplier())) {
                current.shoot(new ShootParameters(this.getAmmoSupplier(), (Entity)shooter, server, shootPosition, direction, current, 0.0, true, targetEntityUuid, finalSampledTarget));
                fired[0] = true;
            }
        });
        if (fired[0]) {
            this.afterShoot(this.getGunData(seatIndex), direction);
            if (playShootSound) {
                this.playShootSound3p(shooter, seatIndex);
            }
        }
        return fired[0];
    }

    public void vehicleShoot(LivingEntity shooter, UUID targetEntityUuid, Vec3 targetPos) {
        if (!this.m_9236_().f_46443_ && this.isFireControlActive()) {
            this.fireIndirectRound(shooter, targetEntityUuid, true);
            return;
        }
        super.vehicleShoot(shooter, targetEntityUuid, targetPos);
    }

    protected boolean isMovingForFireControl() {
        return this.m_20184_().m_165924_() > 0.05;
    }

    private void notifyBlocked(LivingEntity actor, FireControlStatus status) {
        Player player;
        block3: {
            block2: {
                if (!(actor instanceof Player)) break block2;
                player = (Player)actor;
                if (this.f_19797_ - this.lastBlockedMessageTick >= 20) break block3;
            }
            return;
        }
        this.lastBlockedMessageTick = this.f_19797_;
        player.m_5661_((Component)Component.m_237110_((String)"message.fcp.fire_control.blocked", (Object[])new Object[]{Component.m_237115_((String)status.translationKey())}), true);
    }

    private static void notifyActor(Entity actor, String translationKey) {
        if (actor instanceof Player) {
            Player player = (Player)actor;
            player.m_5661_((Component)Component.m_237115_((String)translationKey), true);
        }
    }

    @Override
    public void m_7380_(CompoundTag tag) {
        super.m_7380_(tag);
        tag.m_128379_("FCPFireControlActive", this.isFireControlActive());
        tag.m_128356_("FCPFireControlTarget", this.getFireControlTarget().m_121878_());
        tag.m_128405_("FCPFireControlRadius", this.getFireControlRadius());
        tag.m_128405_("FCPFireControlTrajectory", this.getFireControlTrajectory().ordinal());
    }

    @Override
    public void m_7378_(CompoundTag tag) {
        super.m_7378_(tag);
        if (!tag.m_128441_("FCPFireControlActive")) {
            return;
        }
        this.f_19804_.m_135381_(FIRE_CONTROL_ACTIVE, (Object)tag.m_128471_("FCPFireControlActive"));
        this.f_19804_.m_135381_(FIRE_CONTROL_TARGET, (Object)BlockPos.m_122022_((long)tag.m_128454_("FCPFireControlTarget")));
        this.f_19804_.m_135381_(FIRE_CONTROL_RADIUS, (Object)Mth.m_14045_((int)tag.m_128451_("FCPFireControlRadius"), (int)0, (int)99));
        this.f_19804_.m_135381_(FIRE_CONTROL_TRAJECTORY, (Object)TrajectoryMode.fromId(tag.m_128451_("FCPFireControlTrajectory")).ordinal());
        this.f_19804_.m_135381_(FIRE_CONTROL_STATUS, (Object)(this.isFireControlActive() ? FireControlStatus.MOVING.ordinal() : FireControlStatus.INACTIVE.ordinal()));
    }
}

