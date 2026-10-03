/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType
 *  com.atsuishio.superbwarfare.entity.projectile.MissileProjectile
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  com.atsuishio.superbwarfare.init.ModSounds
 *  com.atsuishio.superbwarfare.init.ModTags$EntityTypes
 *  net.minecraft.core.BlockPos
 *  net.minecraft.network.syncher.EntityDataAccessor
 *  net.minecraft.network.syncher.EntityDataSerializer
 *  net.minecraft.network.syncher.EntityDataSerializers
 *  net.minecraft.network.syncher.SynchedEntityData
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.sounds.SoundEvents
 *  net.minecraft.sounds.SoundSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Pig
 *  net.minecraft.world.level.BlockGetter
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.level.levelgen.Heightmap$Types
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.network.PacketDistributor
 *  org.jetbrains.annotations.Nullable
 */
package tech.vvp.vvp.entity.vehicle;

import com.atsuishio.superbwarfare.data.vehicle.subdata.VehicleType;
import com.atsuishio.superbwarfare.entity.projectile.MissileProjectile;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.init.ModSounds;
import com.atsuishio.superbwarfare.init.ModTags;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;
import tech.vvp.vvp.client.PantsirClientHandler;
import tech.vvp.vvp.entity.projectile.PantsirMissileEntity;
import tech.vvp.vvp.entity.vehicle.CamoVehicleBase;
import tech.vvp.vvp.init.ModTags;
import tech.vvp.vvp.network.VVPNetwork;
import tech.vvp.vvp.network.message.PantsirRadarSyncMessage;

public class PantsirS1Entity
extends CamoVehicleBase {
    private static final EntityDataAccessor<Boolean> AUTO_AIM_ACTIVE = SynchedEntityData.m_135353_(PantsirS1Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135035_);
    private static final EntityDataAccessor<Float> AIM_DIR_X = SynchedEntityData.m_135353_(PantsirS1Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> AIM_DIR_Y = SynchedEntityData.m_135353_(PantsirS1Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final EntityDataAccessor<Float> AIM_DIR_Z = SynchedEntityData.m_135353_(PantsirS1Entity.class, (EntityDataSerializer)EntityDataSerializers.f_135029_);
    private static final ResourceLocation[] CAMO_TEXTURES = new ResourceLocation[]{new ResourceLocation("vvp", "textures/entity/pantsir_s1.png")};
    private static final String[] CAMO_NAMES = new String[]{"Standard"};
    private static final double RADAR_RANGE = 1100.0;
    private static final double RADAR_TRACK_RANGE = 1000.0;
    private static final int LOCK_TIME_TICKS = 40;
    private static final float RADAR_ROTATION_TICKS = 42.5f;
    private static final float RADAR_BEAM_WIDTH = 30.0f;
    private int radarState = 0;
    private Entity trackedTarget = null;
    private Entity lockedTarget = null;
    private int lockingProgress = 0;
    private int syncTimer = 0;
    private float radarRotationTicks = 0.0f;
    private Vec3 lastKnownTargetPos = null;
    private boolean signalLost = false;
    private int noSignalTicks = 0;
    private static final int MAX_REACQUIRE_TICKS = 40;
    private final List<Entity> detectedTargets = new ArrayList<Entity>();
    private int targetScanTimer = 0;
    private int scanOffset = 0;
    private final Map<Integer, CachedLineOfSight> losCache = new HashMap<Integer, CachedLineOfSight>();
    private final List<PantsirMissileEntity> activeMissiles = new ArrayList<PantsirMissileEntity>();
    private LivingEntity cachedOperator = null;
    private int operatorCacheExpiry = 0;
    private int soundCooldown = 0;
    private Vec3 lastTargetPos = null;
    private int lastTargetId = -1;
    private Vec3 calculatedTargetVel = Vec3.f_82478_;

    public PantsirS1Entity(EntityType<PantsirS1Entity> type, Level world) {
        super(type, world);
    }

    @Override
    protected void m_8097_() {
        super.m_8097_();
        this.f_19804_.m_135372_(AUTO_AIM_ACTIVE, (Object)false);
        this.f_19804_.m_135372_(AIM_DIR_X, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(AIM_DIR_Y, (Object)Float.valueOf(0.0f));
        this.f_19804_.m_135372_(AIM_DIR_Z, (Object)Float.valueOf(1.0f));
    }

    @Override
    public ResourceLocation[] getCamoTextures() {
        return CAMO_TEXTURES;
    }

    @Override
    public String[] getCamoNames() {
        return CAMO_NAMES;
    }

    public void adjustTurretAngle() {
        if (((Boolean)this.f_19804_.m_135370_(AUTO_AIM_ACTIVE)).booleanValue()) {
            Vec3 aimDirection = new Vec3((double)((Float)this.f_19804_.m_135370_(AIM_DIR_X)).floatValue(), (double)((Float)this.f_19804_.m_135370_(AIM_DIR_Y)).floatValue(), (double)((Float)this.f_19804_.m_135370_(AIM_DIR_Z)).floatValue());
            if (aimDirection.m_82556_() > 0.001) {
                this.turretAutoAimFromVector(aimDirection);
            }
            return;
        }
        super.adjustTurretAngle();
    }

    public void m_6075_() {
        super.m_6075_();
        if (!this.m_9236_().f_46443_) {
            this.tickRadar();
        }
    }

    private void tickRadar() {
        LivingEntity operator;
        this.radarRotationTicks += 1.0f;
        if (this.radarRotationTicks >= 42.5f) {
            this.radarRotationTicks -= 42.5f;
        }
        if (this.soundCooldown > 0) {
            --this.soundCooldown;
        }
        if ((operator = this.getOperator()) == null) {
            this.resetRadar();
            return;
        }
        ++this.targetScanTimer;
        if (this.targetScanTimer >= 5) {
            this.targetScanTimer = 0;
            this.scanForTargets();
            this.scanForMissiles();
        }
        this.updateRadarState(operator);
        ++this.syncTimer;
        if (this.syncTimer >= 2) {
            this.syncTimer = 0;
            this.syncRadarToClient(operator);
        }
    }

    private void scanForTargets() {
        Vec3 radarPos = this.m_20182_().m_82520_(0.0, 2.5, 0.0);
        AABB searchBox = new AABB(radarPos, radarPos).m_82400_(1100.0);
        List allEntities = this.m_9236_().m_45933_((Entity)this, searchBox);
        ArrayList<Entity> candidates = new ArrayList<Entity>();
        for (Entity entity : allEntities) {
            if (!this.isValidRadarTargetBasic(entity, radarPos)) continue;
            candidates.add(entity);
        }
        int batchSize = 4;
        int startIdx = this.scanOffset % Math.max(candidates.size(), 1);
        int endIdx = Math.min(startIdx + batchSize, candidates.size());
        for (int i = startIdx; i < endIdx; ++i) {
            Entity entity = (Entity)candidates.get(i);
            if (this.hasLineOfSightCached(entity, radarPos)) {
                if (this.detectedTargets.contains(entity)) continue;
                this.detectedTargets.add(entity);
                continue;
            }
            this.detectedTargets.remove(entity);
        }
        this.detectedTargets.removeIf(e -> !e.m_6084_() || e.m_20182_().m_82554_(radarPos) > 1100.0 || !this.isValidRadarTargetBasic((Entity)e, radarPos));
        this.scanOffset += batchSize;
        if (this.scanOffset < 0) {
            this.scanOffset = 0;
        }
        if (this.f_19797_ % 100 == 0) {
            this.losCache.entrySet().removeIf(entry -> ((CachedLineOfSight)entry.getValue()).expiresAt < this.f_19797_);
        }
    }

    private boolean isValidRadarTargetBasic(Entity entity, Vec3 radarPos) {
        VehicleEntity vehicle;
        VehicleType vt;
        if (entity == null || !entity.m_6084_()) {
            return false;
        }
        if (entity == this || this.m_20363_(entity)) {
            return false;
        }
        double distance = entity.m_20182_().m_82554_(radarPos);
        if (distance > 1100.0) {
            return false;
        }
        EntityType type = entity.m_6095_();
        if (type.m_204039_(ModTags.EntityTypes.PANTSIR_AIR_TARGET) || type.m_204039_(ModTags.EntityTypes.PANTSIR_MISSILE_TARGET)) {
            return true;
        }
        if (entity instanceof MissileProjectile) {
            PantsirMissileEntity pm;
            return !(entity instanceof PantsirMissileEntity) || (pm = (PantsirMissileEntity)entity).getLauncherId() != this.m_19879_();
        }
        if (entity instanceof VehicleEntity && ((vt = (vehicle = (VehicleEntity)entity).getVehicleType()) == VehicleType.HELICOPTER || vt == VehicleType.AIRPLANE)) {
            return true;
        }
        String className = entity.getClass().getSimpleName();
        return className.contains("Missile") || className.contains("Rocket") || className.contains("Bomb");
    }

    private boolean hasLineOfSightCached(Entity entity, Vec3 radarPos) {
        int entityId = entity.m_19879_();
        CachedLineOfSight cached = this.losCache.get(entityId);
        if (cached != null && cached.expiresAt > this.f_19797_) {
            return cached.hasLOS;
        }
        Vec3 targetPos = entity.m_20182_().m_82520_(0.0, (double)entity.m_20206_() * 0.5, 0.0);
        boolean hasLOS = this.hasLineOfSightOptimized(radarPos, targetPos);
        this.losCache.put(entityId, new CachedLineOfSight(hasLOS, this.f_19797_ + 20));
        return hasLOS;
    }

    private void scanForMissiles() {
        Vec3 radarPos = this.m_20182_().m_82520_(0.0, 2.5, 0.0);
        this.activeMissiles.clear();
        AABB searchBox = new AABB(radarPos, radarPos).m_82400_(1100.0);
        List missiles = this.m_9236_().m_6443_(PantsirMissileEntity.class, searchBox, m -> m.getLauncherId() == this.m_19879_() && m.m_6084_());
        this.activeMissiles.addAll(missiles);
    }

    private void playLockWarningSound(Entity target, boolean locked) {
        if (target == null || this.soundCooldown > 0) {
            return;
        }
        if (!target.m_20197_().isEmpty() || target instanceof VehicleEntity) {
            target.m_9236_().m_5594_(null, target.m_20097_(), target instanceof Pig ? SoundEvents.f_12235_ : (locked ? (SoundEvent)ModSounds.LOCKED_WARNING.get() : (SoundEvent)ModSounds.LOCKING_WARNING.get()), SoundSource.PLAYERS, 2.0f, 1.0f);
            this.soundCooldown = locked ? 2 : 3;
        }
    }

    @Nullable
    private LivingEntity getOperator() {
        if (this.cachedOperator != null && this.cachedOperator.m_6084_() && this.operatorCacheExpiry > this.f_19797_) {
            return this.cachedOperator;
        }
        List passengers = this.m_20197_();
        if (passengers.isEmpty()) {
            this.cachedOperator = null;
            return null;
        }
        for (Entity passenger : passengers) {
            LivingEntity living;
            int seatIndex;
            if (!(passenger instanceof LivingEntity) || (seatIndex = this.getSeatIndex((Entity)(living = (LivingEntity)passenger))) != 1) continue;
            this.cachedOperator = living;
            this.operatorCacheExpiry = this.f_19797_ + 10;
            return living;
        }
        this.cachedOperator = null;
        return null;
    }

    private void updateRadarState(LivingEntity operator) {
        Vec3 radarPos = this.m_20182_().m_82520_(0.0, 2.5, 0.0);
        switch (this.radarState) {
            case 0: {
                this.trackedTarget = this.findClosestTarget(radarPos);
                this.disableAutoAim();
                if (this.trackedTarget == null) break;
                this.radarState = 1;
                break;
            }
            case 1: {
                this.disableAutoAim();
                if (this.isTargetValidForTracking(this.trackedTarget, radarPos)) break;
                this.trackedTarget = this.findClosestTarget(radarPos);
                if (this.trackedTarget != null) break;
                this.radarState = 0;
                break;
            }
            case 2: {
                if (!this.isTargetValidForTracking(this.trackedTarget, radarPos)) {
                    this.lockingProgress = 0;
                    this.disableAutoAim();
                    this.radarState = 4;
                    break;
                }
                Entity decoyNearTarget = this.findDecoyNearTarget(this.trackedTarget, 50.0);
                if (decoyNearTarget != null) {
                    this.trackedTarget = decoyNearTarget;
                }
                ++this.lockingProgress;
                this.disableAutoAim();
                this.playLockWarningSound(this.trackedTarget, false);
                if (this.lockingProgress < 40) break;
                this.lockedTarget = this.trackedTarget;
                this.radarState = 3;
                break;
            }
            case 3: {
                boolean targetInRange = this.lockedTarget != null && this.lockedTarget.m_6084_();
                boolean hasLOS = false;
                if (targetInRange) {
                    Vec3 targetPos = this.lockedTarget.m_20182_().m_82520_(0.0, (double)this.lockedTarget.m_20206_() * 0.5, 0.0);
                    double distance = radarPos.m_82554_(targetPos);
                    if (distance <= 1000.0) {
                        hasLOS = this.hasLineOfSightOptimized(radarPos, targetPos);
                    }
                    if (hasLOS) {
                        this.lastKnownTargetPos = targetPos;
                        this.signalLost = false;
                        this.noSignalTicks = 0;
                    } else {
                        if (!this.signalLost) {
                            this.signalLost = true;
                            this.noSignalTicks = 0;
                        }
                        ++this.noSignalTicks;
                        if (this.noSignalTicks > 40) {
                            this.lockedTarget = null;
                            this.disableAutoAim();
                            this.signalLost = false;
                            this.lastKnownTargetPos = null;
                            this.radarState = 4;
                        }
                    }
                }
                if (!this.isTargetValidForLock(this.lockedTarget, radarPos)) {
                    this.lockedTarget = null;
                    this.disableAutoAim();
                    this.signalLost = false;
                    this.lastKnownTargetPos = null;
                    this.radarState = 4;
                    break;
                }
                if (!hasLOS) break;
                this.updateAutoAimTarget(this.lockedTarget);
                this.playLockWarningSound(this.lockedTarget, true);
                break;
            }
            case 4: {
                this.disableAutoAim();
                ++this.lockingProgress;
                if (this.lockingProgress <= 20) break;
                this.lockingProgress = 0;
                this.radarState = 0;
            }
        }
    }

    private void updateAutoAimTarget(Entity target) {
        Vec3 aimVector;
        if (target == null) {
            this.f_19804_.m_135381_(AUTO_AIM_ACTIVE, (Object)false);
            this.lastTargetPos = null;
            this.lastTargetId = -1;
            return;
        }
        LivingEntity operator = this.getOperator();
        if (operator == null) {
            this.f_19804_.m_135381_(AUTO_AIM_ACTIVE, (Object)false);
            return;
        }
        Vec3 targetPos = target.m_20191_().m_82399_();
        Vec3 shootPos = this.getShootPos((Entity)operator, 1.0f);
        int seatIndex = this.getSeatIndex((Entity)operator);
        if (seatIndex < 0) {
            this.f_19804_.m_135381_(AUTO_AIM_ACTIVE, (Object)false);
            return;
        }
        int weaponIndex = this.getSelectedWeapon(seatIndex);
        if (weaponIndex == 1) {
            aimVector = targetPos.m_82546_(shootPos).m_82541_();
        } else {
            Vec3 targetVel = this.getTargetVelocity(target, targetPos);
            double projectileSpeed = 20.0;
            double gravity = 0.03;
            Vec3 predictedPos = this.calculateBallisticAimPointForTarget(shootPos, targetPos, targetVel, projectileSpeed, gravity, target);
            aimVector = predictedPos.m_82546_(shootPos).m_82541_();
        }
        this.f_19804_.m_135381_(AUTO_AIM_ACTIVE, (Object)true);
        this.f_19804_.m_135381_(AIM_DIR_X, (Object)Float.valueOf((float)aimVector.f_82479_));
        this.f_19804_.m_135381_(AIM_DIR_Y, (Object)Float.valueOf((float)aimVector.f_82480_));
        this.f_19804_.m_135381_(AIM_DIR_Z, (Object)Float.valueOf((float)aimVector.f_82481_));
    }

    private Vec3 getTargetVelocity(Entity target, Vec3 currentPos) {
        if (target.m_19879_() != this.lastTargetId) {
            this.lastTargetId = target.m_19879_();
            this.lastTargetPos = currentPos;
            this.calculatedTargetVel = target.m_20184_();
            return this.calculatedTargetVel;
        }
        if (this.lastTargetPos != null) {
            Vec3 newVel = currentPos.m_82546_(this.lastTargetPos);
            this.calculatedTargetVel = newVel.m_82490_(0.8).m_82549_(this.calculatedTargetVel.m_82490_(0.2));
        }
        this.lastTargetPos = currentPos;
        return this.calculatedTargetVel;
    }

    private Vec3 calculateBallisticAimPointForTarget(Vec3 shootPos, Vec3 targetPos, Vec3 targetVel, double projectileSpeed, double gravity, @Nullable Entity target) {
        boolean isTaggedTarget;
        double distance = shootPos.m_82554_(targetPos);
        double timeToTarget = distance / projectileSpeed;
        boolean bl = isTaggedTarget = target != null && target.m_6095_().m_204039_(ModTags.EntityTypes.PANTSIR_AIR_TARGET);
        if (isTaggedTarget) {
            double flightTime = distance / projectileSpeed;
            Vec3 predictedPos = targetPos.m_82549_(targetVel.m_82490_(flightTime));
            double gravityComp = 0.5 * gravity * flightTime * flightTime;
            return predictedPos.m_82520_(0.0, gravityComp, 0.0);
        }
        Vec3 aimPoint = targetPos;
        for (int i = 0; i < 5; ++i) {
            Vec3 predictedTargetPos = targetPos.m_82549_(targetVel.m_82490_(timeToTarget));
            Vec3 toTarget = predictedTargetPos.m_82546_(shootPos);
            double horizontalDist = Math.sqrt(toTarget.f_82479_ * toTarget.f_82479_ + toTarget.f_82481_ * toTarget.f_82481_);
            timeToTarget = horizontalDist / projectileSpeed;
            double gravityDrop = 0.5 * gravity * timeToTarget * timeToTarget;
            aimPoint = predictedTargetPos.m_82520_(0.0, gravityDrop, 0.0);
            distance = shootPos.m_82554_(aimPoint);
            timeToTarget = distance / projectileSpeed;
        }
        return aimPoint;
    }

    private void disableAutoAim() {
        this.f_19804_.m_135381_(AUTO_AIM_ACTIVE, (Object)false);
    }

    public float getRadarAngle() {
        return -(this.radarRotationTicks / 42.5f) * 360.0f;
    }

    @Nullable
    private Entity findClosestTarget(Vec3 radarPos) {
        LivingEntity operator = this.getOperator();
        Entity closest = null;
        double minDistSq = Double.MAX_VALUE;
        for (Entity e : this.detectedTargets) {
            double distSq;
            if (!e.m_6084_() || this.isAllyTarget(e, operator) || !((distSq = e.m_20182_().m_82557_(radarPos)) < minDistSq)) continue;
            minDistSq = distSq;
            closest = e;
        }
        return closest;
    }

    private boolean isTargetValidForTracking(Entity target, Vec3 radarPos) {
        if (target == null || !target.m_6084_()) {
            return false;
        }
        double distance = target.m_20182_().m_82554_(radarPos);
        if (distance > 1100.0) {
            return false;
        }
        Vec3 targetPos = target.m_20182_().m_82520_(0.0, (double)target.m_20206_() * 0.5, 0.0);
        return this.hasLineOfSightOptimized(radarPos, targetPos);
    }

    private boolean hasLineOfSightOptimized(Vec3 from, Vec3 to) {
        int fromX = (int)from.f_82479_;
        int fromZ = (int)from.f_82481_;
        int toX = (int)to.f_82479_;
        int toZ = (int)to.f_82481_;
        double horizontalDist = Math.sqrt((toX - fromX) * (toX - fromX) + (toZ - fromZ) * (toZ - fromZ));
        int steps = Math.min(5, Math.max(1, (int)(horizontalDist / 50.0)));
        boolean targetIsHigh = true;
        for (int i = 0; i <= steps; ++i) {
            double t = (double)i / (double)steps;
            int x = (int)((double)fromX + (double)(toX - fromX) * t);
            int z = (int)((double)fromZ + (double)(toZ - fromZ) * t);
            int highestY = this.m_9236_().m_6924_(Heightmap.Types.MOTION_BLOCKING, x, z);
            if (!(to.f_82480_ < (double)(highestY + 10))) continue;
            targetIsHigh = false;
            break;
        }
        if (targetIsHigh) {
            return true;
        }
        Vec3 direction = to.m_82546_(from);
        for (double t : new double[]{0.25, 0.5, 0.75}) {
            Vec3 checkPoint = from.m_82549_(direction.m_82490_(t));
            BlockPos pos = BlockPos.m_274561_((double)checkPoint.f_82479_, (double)checkPoint.f_82480_, (double)checkPoint.f_82481_);
            BlockState state = this.m_9236_().m_8055_(pos);
            if (state.m_60795_() || !state.m_60804_((BlockGetter)this.m_9236_(), pos)) continue;
            return false;
        }
        return true;
    }

    private boolean isTargetValidForLock(Entity target, Vec3 radarPos) {
        if (target == null || !target.m_6084_()) {
            return false;
        }
        double distance = target.m_20182_().m_82554_(radarPos);
        return distance <= 1200.0;
    }

    @Nullable
    private Entity findDecoyNearTarget(Entity target, double radius) {
        if (target == null) {
            return null;
        }
        Vec3 targetPos = target.m_20182_();
        AABB searchBox = new AABB(targetPos, targetPos).m_82400_(radius);
        List entities = this.m_9236_().m_6249_(target, searchBox, e -> {
            if (e == null || !e.m_6084_()) {
                return false;
            }
            return e.m_6095_().m_204039_(ModTags.EntityTypes.DECOY);
        });
        return entities.stream().min(Comparator.comparingDouble(e -> e.m_20182_().m_82554_(targetPos))).orElse(null);
    }

    private int getTargetType(Entity entity) {
        if (entity instanceof MissileProjectile) {
            return 3;
        }
        if (entity.m_6095_().m_204039_(ModTags.EntityTypes.PANTSIR_MISSILE_TARGET)) {
            return 3;
        }
        if (entity instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)entity;
            VehicleType type = vehicle.getVehicleType();
            if (type == VehicleType.HELICOPTER) {
                return 1;
            }
            if (type == VehicleType.AIRPLANE) {
                return 2;
            }
        }
        return 0;
    }

    private void syncRadarToClient(LivingEntity operator) {
        if (!(operator instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer serverPlayer = (ServerPlayer)operator;
        Entity radarTarget = this.radarState == 3 ? this.lockedTarget : this.trackedTarget;
        int targetId = radarTarget != null ? radarTarget.m_19879_() : -1;
        double targetX = radarTarget != null ? radarTarget.m_20185_() : 0.0;
        double targetY = radarTarget != null ? radarTarget.m_20186_() + (double)(radarTarget.m_20206_() / 2.0f) : 0.0;
        double targetZ = radarTarget != null ? radarTarget.m_20189_() : 0.0;
        double targetVelX = radarTarget != null ? radarTarget.m_20184_().f_82479_ : 0.0;
        double targetVelY = radarTarget != null ? radarTarget.m_20184_().f_82480_ : 0.0;
        double targetVelZ = radarTarget != null ? radarTarget.m_20184_().f_82481_ : 0.0;
        int progress = this.radarState == 2 ? this.lockingProgress * 100 / 40 : 0;
        double distance = radarTarget != null ? radarTarget.m_20182_().m_82554_(operator.m_20182_()) : 0.0;
        float radarAngle = this.getRadarAngle();
        Vec3 barrelDir = this.getBarrelVector(1.0f);
        float turretAngle = (float)Math.toDegrees(-Math.atan2(barrelDir.f_82479_, barrelDir.f_82481_));
        int[] targetIds = new int[Math.min(this.detectedTargets.size(), 16)];
        double[] targetXs = new double[targetIds.length];
        double[] targetYs = new double[targetIds.length];
        double[] targetZs = new double[targetIds.length];
        int[] targetTypes = new int[targetIds.length];
        boolean[] targetIsAlly = new boolean[targetIds.length];
        for (int i = 0; i < targetIds.length; ++i) {
            Entity target = this.detectedTargets.get(i);
            targetIds[i] = target.m_19879_();
            targetXs[i] = target.m_20185_();
            targetYs[i] = target.m_20186_() + (double)(target.m_20206_() / 2.0f);
            targetZs[i] = target.m_20189_();
            targetTypes[i] = this.getTargetType(target);
            targetIsAlly[i] = this.isAllyTarget(target, operator);
        }
        this.activeMissiles.removeIf(m -> !m.m_6084_());
        int missileCount = Math.min(this.activeMissiles.size(), 8);
        double[] missileX = new double[missileCount];
        double[] missileY = new double[missileCount];
        double[] missileZ = new double[missileCount];
        for (int i = 0; i < missileCount; ++i) {
            PantsirMissileEntity missile = this.activeMissiles.get(i);
            missileX[i] = missile.m_20185_();
            missileY[i] = missile.m_20186_();
            missileZ[i] = missile.m_20189_();
        }
        PantsirRadarSyncMessage message = new PantsirRadarSyncMessage(this.m_19879_(), this.radarState, targetId, targetX, targetY, targetZ, targetVelX, targetVelY, targetVelZ, progress, distance, radarAngle, turretAngle, targetIds, targetXs, targetYs, targetZs, targetTypes, targetIsAlly, missileX, missileY, missileZ, this.signalLost, this.lastKnownTargetPos != null ? this.lastKnownTargetPos.f_82479_ : 0.0, this.lastKnownTargetPos != null ? this.lastKnownTargetPos.f_82480_ : 0.0, this.lastKnownTargetPos != null ? this.lastKnownTargetPos.f_82481_ : 0.0);
        VVPNetwork.VVP_HANDLER.send(PacketDistributor.PLAYER.with(() -> serverPlayer), (Object)message);
    }

    private boolean isAllyTarget(Entity target, LivingEntity operator) {
        if (operator == null || target == null) {
            return false;
        }
        if (operator.m_5647_() == null) {
            return false;
        }
        if (operator.m_7307_(target)) {
            return true;
        }
        if (target instanceof VehicleEntity) {
            VehicleEntity vehicle = (VehicleEntity)target;
            for (Entity passenger : vehicle.m_20197_()) {
                LivingEntity living;
                if (!(passenger instanceof LivingEntity) || !operator.m_7307_((Entity)(living = (LivingEntity)passenger))) continue;
                return true;
            }
        }
        return false;
    }

    private void resetRadar() {
        this.radarState = 0;
        this.trackedTarget = null;
        this.lockedTarget = null;
        this.lockingProgress = 0;
        this.disableAutoAim();
    }

    public void requestLock(LivingEntity operator) {
        if (this.radarState == 1 && this.trackedTarget != null) {
            if (this.isAllyTarget(this.trackedTarget, operator)) {
                return;
            }
            this.radarState = 2;
            this.lockingProgress = 0;
        }
    }

    public void cancelLock(LivingEntity operator) {
        if (this.radarState == 2 || this.radarState == 3) {
            this.lockedTarget = null;
            this.lockingProgress = 0;
            this.radarState = 1;
        }
    }

    public void selectNextTarget() {
        if (this.detectedTargets.isEmpty()) {
            return;
        }
        if (this.radarState == 2 || this.radarState == 3) {
            return;
        }
        LivingEntity operator = this.getOperator();
        int currentIndex = -1;
        if (this.trackedTarget != null) {
            currentIndex = this.detectedTargets.indexOf(this.trackedTarget);
        }
        if (currentIndex < 0) {
            currentIndex = this.detectedTargets.size() - 1;
        }
        int startIndex = currentIndex;
        int nextIndex = currentIndex;
        int checked = 0;
        do {
            nextIndex = (nextIndex + 1) % this.detectedTargets.size();
            if (++checked > this.detectedTargets.size()) {
                return;
            }
            Entity candidate = this.detectedTargets.get(nextIndex);
            if (this.isAllyTarget(candidate, operator)) continue;
            this.trackedTarget = candidate;
            if (this.radarState == 0) {
                this.radarState = 1;
            }
            return;
        } while (nextIndex != startIndex && nextIndex != currentIndex);
    }

    public void selectPrevTarget() {
        if (this.detectedTargets.isEmpty()) {
            return;
        }
        if (this.radarState == 2 || this.radarState == 3) {
            return;
        }
        LivingEntity operator = this.getOperator();
        int currentIndex = -1;
        if (this.trackedTarget != null) {
            currentIndex = this.detectedTargets.indexOf(this.trackedTarget);
        }
        if (currentIndex < 0) {
            currentIndex = 0;
        }
        int startIndex = currentIndex;
        int prevIndex = currentIndex;
        int checked = 0;
        do {
            if (--prevIndex < 0) {
                prevIndex = this.detectedTargets.size() - 1;
            }
            if (++checked > this.detectedTargets.size()) {
                return;
            }
            Entity candidate = this.detectedTargets.get(prevIndex);
            if (this.isAllyTarget(candidate, operator)) continue;
            this.trackedTarget = candidate;
            if (this.radarState == 0) {
                this.radarState = 1;
            }
            return;
        } while (prevIndex != startIndex && prevIndex != currentIndex);
    }

    @Nullable
    public Entity getLockedTarget() {
        return this.lockedTarget;
    }

    public boolean hasLockedTarget() {
        return this.radarState == 3 && this.lockedTarget != null;
    }

    private boolean safeCanShoot(LivingEntity living) {
        if (living == null || !living.m_6084_() || this.m_213877_()) {
            return false;
        }
        try {
            return super.canShoot(living);
        }
        catch (RuntimeException ignored) {
            return false;
        }
    }

    public boolean canShoot(LivingEntity living) {
        int seatIndex = this.getSeatIndex((Entity)living);
        int weaponIndex = this.getSelectedWeapon(seatIndex);
        if (weaponIndex == 1) {
            if (this.m_9236_().f_46443_) {
                return this.isTargetLocked() && this.safeCanShoot(living);
            }
            return this.hasLockedTarget() && this.safeCanShoot(living);
        }
        return this.safeCanShoot(living);
    }

    @OnlyIn(value=Dist.CLIENT)
    private boolean isTargetLocked() {
        return PantsirClientHandler.isTargetLocked(this.m_19879_());
    }

    public void vehicleShoot(@Nullable LivingEntity living, @Nullable UUID uuid, @Nullable Vec3 targetPos) {
        if (living == null) {
            try {
                super.vehicleShoot(living, uuid, targetPos);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            return;
        }
        int seatIndex = this.getSeatIndex((Entity)living);
        int weaponIndex = this.getSelectedWeapon(seatIndex);
        if (weaponIndex == 0) {
            try {
                super.vehicleShoot(living, uuid, targetPos);
            }
            catch (RuntimeException runtimeException) {
                // empty catch block
            }
            return;
        }
        if (weaponIndex == 1) {
            if (this.hasLockedTarget() && this.lockedTarget.m_6084_()) {
                UUID targetUuid = this.lockedTarget.m_20148_();
                Vec3 targetPosition = this.lockedTarget.m_20182_().m_82520_(0.0, (double)(this.lockedTarget.m_20206_() / 2.0f), 0.0);
                try {
                    super.vehicleShoot(living, targetUuid, targetPosition);
                }
                catch (RuntimeException runtimeException) {
                    // empty catch block
                }
            }
            return;
        }
        try {
            super.vehicleShoot(living, uuid, targetPos);
        }
        catch (RuntimeException runtimeException) {
            // empty catch block
        }
    }

    private static class CachedLineOfSight {
        boolean hasLOS;
        int expiresAt;

        CachedLineOfSight(boolean hasLOS, int expiresAt) {
            this.hasLOS = hasLOS;
            this.expiresAt = expiresAt;
        }
    }
}

