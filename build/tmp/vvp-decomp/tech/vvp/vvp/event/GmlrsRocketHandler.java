/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.MediumRocketEntity
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package tech.vvp.vvp.event;

import com.atsuishio.superbwarfare.entity.projectile.MediumRocketEntity;
import java.util.Iterator;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

@Mod.EventBusSubscriber(modid="vvp")
public final class GmlrsRocketHandler {
    private static final String TAG_GMLRS = "vvp_gmlrs";
    private static final String TAG_TARGET_SPEED = "vvp_gmlrs_target_speed";
    private static final String TAG_ACCEL_AGE = "vvp_gmlrs_accel_age";
    private static final String TAG_DIR_X = "vvp_gmlrs_dir_x";
    private static final String TAG_DIR_Y = "vvp_gmlrs_dir_y";
    private static final String TAG_DIR_Z = "vvp_gmlrs_dir_z";
    private static final String TAG_HAS_TARGET = "vvp_gmlrs_has_target";
    private static final String TAG_TARGET_X = "vvp_gmlrs_target_x";
    private static final String TAG_TARGET_Y = "vvp_gmlrs_target_y";
    private static final String TAG_TARGET_Z = "vvp_gmlrs_target_z";
    private static final String TAG_ACCEL_TICKS = "vvp_gmlrs_accel_ticks";
    private static final double START_SPEED_FACTOR = 0.1;
    private static final int ACCEL_TICKS = 80;
    private static final double TERMINAL_DIST = 300.0;
    private static final Set<UUID> TRACKED_ROCKETS = ConcurrentHashMap.newKeySet();
    public static volatile Vec3 lastGmlrsTarget = null;

    private GmlrsRocketHandler() {
    }

    @SubscribeEvent
    public static void onRocketSpawn(EntityJoinLevelEvent event) {
        HimarsEntity himars;
        Vec3 velocity;
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof MediumRocketEntity)) {
            return;
        }
        MediumRocketEntity rocket = (MediumRocketEntity)entity;
        LivingEntity operator = GmlrsRocketHandler.resolveHimarsOperator(rocket.m_19749_());
        if (operator == null) {
            return;
        }
        if (rocket.m_19749_() != operator) {
            rocket.m_5602_((Entity)operator);
        }
        if ((velocity = rocket.m_20184_()).m_82556_() < 1.0E-6) {
            return;
        }
        double targetSpeed = velocity.m_82553_();
        Vec3 direction = velocity.m_82541_();
        CompoundTag tag = rocket.getPersistentData();
        tag.m_128379_(TAG_GMLRS, true);
        tag.m_128347_(TAG_TARGET_SPEED, targetSpeed);
        tag.m_128347_(TAG_DIR_X, direction.f_82479_);
        tag.m_128347_(TAG_DIR_Y, direction.f_82480_);
        tag.m_128347_(TAG_DIR_Z, direction.f_82481_);
        tag.m_128405_(TAG_ACCEL_AGE, 0);
        int effectiveAccelTicks = 80;
        Vec3 targetPos = lastGmlrsTarget;
        if (targetPos == null && (himars = GmlrsRocketHandler.resolveHimars(rocket.m_19749_())) != null && himars.isFdcTargetDesignated()) {
            targetPos = new Vec3((double)himars.getFdcTargetX() + 0.5, (double)himars.getFdcTargetY(), (double)himars.getFdcTargetZ() + 0.5);
        }
        if (targetPos != null) {
            double tx = targetPos.f_82479_;
            double ty = targetPos.f_82480_;
            double tz = targetPos.f_82481_;
            tag.m_128379_(TAG_HAS_TARGET, true);
            tag.m_128347_(TAG_TARGET_X, tx);
            tag.m_128347_(TAG_TARGET_Y, ty);
            tag.m_128347_(TAG_TARGET_Z, tz);
            Vec3 rocketPos = rocket.m_20182_();
            double dx = tx - rocketPos.f_82479_;
            double dz = tz - rocketPos.f_82481_;
            double range = Math.sqrt(dx * dx + dz * dz);
            effectiveAccelTicks = Math.max(5, (int)Math.min(80.0, range / 20.0));
        }
        tag.m_128405_(TAG_ACCEL_TICKS, effectiveAccelTicks);
        rocket.m_20256_(direction.m_82490_(targetSpeed * 0.1));
        TRACKED_ROCKETS.add(rocket.m_20148_());
    }

    @SubscribeEvent
    public static void onRocketLeave(EntityLeaveLevelEvent event) {
        TRACKED_ROCKETS.remove(event.getEntity().m_20148_());
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || TRACKED_ROCKETS.isEmpty()) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        Iterator<UUID> iterator = TRACKED_ROCKETS.iterator();
        while (iterator.hasNext()) {
            UUID rocketId = iterator.next();
            MediumRocketEntity rocket = GmlrsRocketHandler.findRocket(server, rocketId);
            if (rocket == null || !rocket.m_6084_()) {
                iterator.remove();
                continue;
            }
            GmlrsRocketHandler.tickGmlrsRocket(rocket);
        }
    }

    private static void tickGmlrsRocket(MediumRocketEntity rocket) {
        Level level = rocket.m_9236_();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        CompoundTag tag = rocket.getPersistentData();
        if (!tag.m_128471_(TAG_GMLRS)) {
            TRACKED_ROCKETS.remove(rocket.m_20148_());
            return;
        }
        int age = tag.m_128451_(TAG_ACCEL_AGE);
        tag.m_128405_(TAG_ACCEL_AGE, age + 1);
        int accelTicks = tag.m_128441_(TAG_ACCEL_TICKS) ? tag.m_128451_(TAG_ACCEL_TICKS) : 80;
        Vec3 launchDirection = GmlrsRocketHandler.readLaunchDirection(tag);
        double targetSpeed = tag.m_128459_(TAG_TARGET_SPEED);
        if (age < accelTicks && targetSpeed > 1.0E-6 && launchDirection.m_82556_() > 1.0E-8) {
            float progress = Math.min(1.0f, (float)age / (float)accelTicks);
            float eased = progress * progress * (3.0f - 2.0f * progress);
            double speed = targetSpeed * (0.1 + 0.9 * (double)eased);
            rocket.m_20256_(launchDirection.m_82490_(speed));
        } else if (age == accelTicks && targetSpeed > 1.0E-6 && launchDirection.m_82556_() > 1.0E-8) {
            rocket.m_20256_(launchDirection.m_82490_(targetSpeed));
        }
        if (tag.m_128471_(TAG_HAS_TARGET)) {
            GmlrsRocketHandler.applyGuidance(rocket, tag, age, accelTicks);
        } else if (age > accelTicks) {
            Vec3 vel = rocket.m_20184_();
            if (vel.f_82480_ > 0.5) {
                Vec3 corrected = vel.m_82541_().m_165921_(new Vec3(vel.f_82479_, -1.0, vel.f_82481_).m_82541_(), 0.05);
                rocket.m_20256_(corrected.m_82541_().m_82490_(vel.m_82553_()));
            }
        }
    }

    private static void applyGuidance(MediumRocketEntity rocket, CompoundTag tag, int age, int accelTicks) {
        double tz;
        double ty;
        Vec3 pos = rocket.m_20182_();
        double tx = tag.m_128459_(TAG_TARGET_X);
        Vec3 toTarget = new Vec3(tx - pos.f_82479_, (ty = tag.m_128459_(TAG_TARGET_Y)) - pos.f_82480_, (tz = tag.m_128459_(TAG_TARGET_Z)) - pos.f_82481_);
        double dist = toTarget.m_82553_();
        if (dist < 4.0) {
            return;
        }
        Vec3 vel = rocket.m_20184_();
        double speed = vel.m_82553_();
        if (speed < 1.0E-4) {
            return;
        }
        Vec3 currentDir = vel.m_82541_();
        Vec3 desired = toTarget.m_82541_();
        double gain = age < accelTicks ? 0.04 : (dist < 60.0 ? 0.7 : (dist < 300.0 ? Math.min(0.55, 120.0 / Math.max(dist, 1.0)) : Math.min(0.35, 300.0 / Math.max(dist, 1.0))));
        double cosA = Math.max(-1.0, Math.min(1.0, currentDir.m_82526_(desired)));
        double angle = Math.acos(cosA);
        double maxTurn = 0.45;
        double t = angle < 1.0E-6 ? 1.0 : Math.min(1.0, gain * maxTurn / angle);
        Vec3 steered = currentDir.m_165921_(desired, t).m_82541_();
        rocket.m_20256_(steered.m_82490_(speed));
    }

    private static MediumRocketEntity findRocket(MinecraftServer server, UUID rocketId) {
        for (ServerLevel level : server.m_129785_()) {
            Entity entity = level.m_8791_(rocketId);
            if (!(entity instanceof MediumRocketEntity)) continue;
            MediumRocketEntity rocket = (MediumRocketEntity)entity;
            return rocket;
        }
        return null;
    }

    private static Vec3 readLaunchDirection(CompoundTag tag) {
        return new Vec3(tag.m_128459_(TAG_DIR_X), tag.m_128459_(TAG_DIR_Y), tag.m_128459_(TAG_DIR_Z));
    }

    private static HimarsEntity resolveHimars(Entity owner) {
        LivingEntity living;
        Entity entity;
        if (owner instanceof LivingEntity && (entity = (living = (LivingEntity)owner).m_20202_()) instanceof HimarsEntity) {
            HimarsEntity h = (HimarsEntity)entity;
            return h;
        }
        if (owner instanceof HimarsEntity) {
            HimarsEntity h = (HimarsEntity)owner;
            return h;
        }
        return null;
    }

    private static LivingEntity resolveHimarsOperator(Entity owner) {
        HimarsEntity himars;
        LivingEntity living;
        Entity entity;
        if (owner instanceof LivingEntity && (entity = (living = (LivingEntity)owner).m_20202_()) instanceof HimarsEntity && (himars = (HimarsEntity)entity).getSeatIndex((Entity)living) == 2) {
            return living;
        }
        if (owner instanceof HimarsEntity) {
            HimarsEntity himars2 = (HimarsEntity)owner;
            return GmlrsRocketHandler.findOperator(himars2);
        }
        return null;
    }

    private static LivingEntity findOperator(HimarsEntity himars) {
        for (Entity passenger : himars.m_20197_()) {
            LivingEntity living;
            if (!(passenger instanceof LivingEntity) || himars.getSeatIndex((Entity)(living = (LivingEntity)passenger)) != 2) continue;
            return living;
        }
        return null;
    }
}

