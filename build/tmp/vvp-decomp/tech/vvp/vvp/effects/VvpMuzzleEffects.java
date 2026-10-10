/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.data.gun.GunData
 *  com.atsuishio.superbwarfare.data.gun.GunProp
 *  com.atsuishio.superbwarfare.data.gun.ProjectileInfo
 *  com.atsuishio.superbwarfare.data.gun.ShootParameters
 *  com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.particles.BlockParticleOption
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.Mth
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.block.state.BlockState
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package tech.vvp.vvp.effects;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.ProjectileInfo;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import tech.vvp.vvp.client.particle.VvpMuzzleParticleOption;
import tech.vvp.vvp.effects.GunAxes;
import tech.vvp.vvp.effects.HimarsBackblastResolver;
import tech.vvp.vvp.effects.MuzzleAnchor;
import tech.vvp.vvp.effects.MuzzleBurstTracker;
import tech.vvp.vvp.effects.MuzzleEffectPreset;
import tech.vvp.vvp.effects.MuzzleFlashLight;
import tech.vvp.vvp.effects.MuzzlePositionResolver;

public final class VvpMuzzleEffects {
    private static final float MTS_VEL = 0.1f;
    private static final int HIGH_RPM_AUTOCANNON = 400;
    private static final int HIMARS_SMOKE_ANIM = 0;

    private VvpMuzzleEffects() {
    }

    public static boolean isVvpVehicle(VehicleEntity vehicle) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        return id != null && "vvp".equals(id.m_135827_());
    }

    public static MuzzleEffectPreset resolvePreset(GunData gunData) {
        if (gunData == null) {
            return MuzzleEffectPreset.AUTOCANNON;
        }
        ProjectileInfo projectile = (ProjectileInfo)gunData.get(GunProp.PROJECTILE);
        if (projectile == null) {
            return MuzzleEffectPreset.AUTOCANNON;
        }
        String path = projectile.getId();
        if (path.contains("missile") || path.contains("rocket") || path.contains("atgm")) {
            return MuzzleEffectPreset.MISSILE;
        }
        if (path.contains("small_cannon") || path.contains("30mm")) {
            return MuzzleEffectPreset.AUTOCANNON;
        }
        if (path.contains("cannon_shell") || path.contains("mortar") || path.contains("apfsds")) {
            return MuzzleEffectPreset.TANK;
        }
        if (path.contains("projectile") || path.contains("bullet")) {
            return MuzzleEffectPreset.MACHINE_GUN;
        }
        return MuzzleEffectPreset.AUTOCANNON;
    }

    public static void spawnFromShoot(ShootParameters parameters) {
        boolean sustainedAutocannon;
        ServerLevel level = parameters.level;
        if (level == null) {
            return;
        }
        MuzzlePositionResolver.MuzzlePose muzzle = MuzzlePositionResolver.resolve(parameters);
        Vec3 pos = muzzle.position();
        Vec3 direction = muzzle.direction();
        if (pos == null || direction == null || direction.m_82556_() < 1.0E-6) {
            return;
        }
        MuzzleEffectPreset preset = VvpMuzzleEffects.resolvePreset(parameters.data);
        GunAxes axes = GunAxes.fromDirection(direction.m_82541_());
        MuzzleAnchor anchor = MuzzleAnchor.from(parameters);
        long gameTick = level.m_46467_();
        int shotIndex = anchor.isValid() ? MuzzleBurstTracker.recordShot(anchor.vehicleId(), gameTick) : 0;
        int rpm = (Integer)parameters.data.get(GunProp.RPM);
        boolean bl = sustainedAutocannon = preset == MuzzleEffectPreset.AUTOCANNON && rpm >= 400 && anchor.isValid() && MuzzleBurstTracker.isSustained(anchor.vehicleId(), gameTick);
        if (sustainedAutocannon) {
            VvpMuzzleEffects.spawnSustainedAutocannon(level, anchor, pos, axes, level.m_213780_(), shotIndex);
            return;
        }
        VvpMuzzleEffects.spawnInstantFlash(level, anchor, pos, axes, level.m_213780_(), preset);
        MuzzleFlashLight.spawn(level, pos, axes.forward(), preset);
        int smokeTicks = switch (preset) {
            default -> throw new IncompatibleClassChangeError();
            case MuzzleEffectPreset.MACHINE_GUN -> 2;
            case MuzzleEffectPreset.AUTOCANNON -> 4;
            case MuzzleEffectPreset.TANK -> 3;
            case MuzzleEffectPreset.MISSILE -> 2;
        };
        int tick = 0;
        while (tick < smokeTicks) {
            int smokeTick = tick++;
            Mod.queueServerWork((int)smokeTick, () -> VvpMuzzleEffects.spawnSmokeTick(level, anchor, level.m_213780_(), preset, smokeTick));
        }
        if (preset == MuzzleEffectPreset.TANK || preset == MuzzleEffectPreset.AUTOCANNON) {
            Mod.queueServerWork((int)5, () -> VvpMuzzleEffects.spawnCooldownSmoke(level, anchor, level.m_213780_(), preset));
        }
    }

    private static void spawnSustainedAutocannon(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, int shotIndex) {
        MuzzleFlashLight.spawn(level, pos, axes.forward(), MuzzleEffectPreset.AUTOCANNON);
        VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 0.98f, 0.9f, 3.4f, 0.11f, 2);
        VvpMuzzleEffects.spawnBangSparks(level, pos, axes, random, 2, 1.4, 1.4, 1.0, 1.5f);
        VvpMuzzleEffects.spawnBlastPuff(level, anchor, pos, axes, random, 2);
        Vec3 muzzle = new Vec3(0.0, -0.1, 0.0);
        Vec3 jitter = new Vec3(0.08, 0.08, 0.12);
        Vec3 forwardVel = new Vec3(0.0, 0.0, 1.840062);
        Vec3 forwardSpread = new Vec3(1.0, 1.0, 4.0);
        VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 45, 1.0f, 7.0f, forwardVel, forwardSpread, false, muzzle, jitter, 12894395, 6, false, 0, false);
        VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 1, 35, 0.9f, 4.5f, forwardVel, new Vec3(0.8, 0.8, 3.0), false, muzzle, jitter, 0xC1C1C1, 5, false, 0, false);
        if (shotIndex % 3 == 0) {
            VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 55, 1.06f, 9.0f, forwardVel, forwardSpread, false, muzzle, jitter, 12170413, 7, false, 0, false);
        }
        if (shotIndex % 5 == 0) {
            VvpMuzzleEffects.spawnCooldownSmoke(level, anchor, random, MuzzleEffectPreset.AUTOCANNON);
        }
    }

    private static void spawnInstantFlash(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, MuzzleEffectPreset preset) {
        switch (preset) {
            case MACHINE_GUN: {
                VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 1.0f, 2.8f, 0.12f, 3);
                VvpMuzzleEffects.spawnBangSparks(level, pos, axes, random, 3, 1.2, 1.2, 0.8, 1.2f);
                break;
            }
            case AUTOCANNON: {
                VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 1.0f, 4.5f, 0.14f, 3);
                VvpMuzzleEffects.spawnBloom(level, pos, 0.98f, 0.72f, 0.45f, 3.8f, 0.12f, 3);
                VvpMuzzleEffects.spawnBangSparks(level, pos, axes, random, 6, 2.0, 2.5, 2.0, 1.8f);
                VvpMuzzleEffects.spawnBlastPuff(level, anchor, pos, axes, random, 3);
                break;
            }
            case TANK: {
                VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 1.0f, 10.5f, 0.22f, 3);
                VvpMuzzleEffects.spawnBloom(level, pos, 0.99f, 0.94f, 0.71f, 8.5f, 0.18f, 3);
                VvpMuzzleEffects.spawnBloom(level, pos, 0.98f, 0.67f, 0.45f, 6.5f, 0.14f, 4);
                VvpMuzzleEffects.spawnBangStatic(level, pos, 3.6f, 4.8f, 10);
                VvpMuzzleEffects.spawnBangSparks(level, pos, axes, random, 10, 4.0, 5.0, 4.0, 2.6f);
                VvpMuzzleEffects.spawnBlastPuff(level, anchor, pos, axes, random, 6);
                break;
            }
            case MISSILE: {
                VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 0.9f, 0.6f, 3.5f, 0.2f, 4);
                VvpMuzzleEffects.spawnBangSparks(level, pos, axes, random, 4, 1.5, 1.5, 1.2, 1.0f);
            }
        }
    }

    private static void spawnSmokeTick(ServerLevel level, MuzzleAnchor anchor, RandomSource random, MuzzleEffectPreset preset, int tick) {
        MuzzlePositionResolver.MuzzlePose pose = anchor.resolve((Level)level);
        if (pose == null) {
            return;
        }
        Vec3 pos = pose.position();
        GunAxes axes = GunAxes.fromDirection(pose.direction());
        Vec3 muzzle = new Vec3(0.0, -0.1, 0.0);
        Vec3 jitter = new Vec3(0.08, 0.08, 0.12);
        Vec3 forwardSpread = new Vec3(1.0, 1.0, 5.0);
        Vec3 forwardVel = new Vec3(0.0, 0.0, 1.840062);
        switch (preset) {
            case MACHINE_GUN: {
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 35, 0.55f, 2.5f, forwardVel, forwardSpread, false, muzzle, jitter, 13157053, 4, false, 0, false);
                break;
            }
            case AUTOCANNON: {
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 60, 1.06f, 10.02f, forwardVel, forwardSpread, false, muzzle, jitter, 0xC1C1C1, 9, false, 14, false);
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 35, 1.06f, 2.06f, forwardVel, forwardSpread, false, muzzle, jitter, 0xC1C1C1, 4, false, 10, false);
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 35, 1.06f, 3.02f, new Vec3(0.0, -0.30625, 0.025), forwardSpread, false, muzzle, jitter, 0xC1C1C1, 4, false, 10, false);
                break;
            }
            case TANK: {
                if (tick == 0) {
                    VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 10, 120, 4.0f, 10.0f, new Vec3(0.0, 0.0, 8.0), new Vec3(2.0, 2.0, 7.0), false, new Vec3(0.0, 0.0, 0.35), new Vec3(0.15, 0.15, 0.2), VvpMuzzleEffects.pickTankSmokeColor(random), 9, false, 14, false);
                }
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 4, 150, 9.0f, 20.0f, new Vec3(0.0, 0.5, 0.0), new Vec3(18.0, 0.5, 18.0), true, new Vec3(0.0, -0.15, 0.0), new Vec3(0.25, 0.05, 0.25), 14340554, 9, false, 5, true);
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 45, 1.06f, 3.5f, new Vec3(0.0, -0.4, 0.05), new Vec3(2.0, 2.0, 5.0), false, muzzle, jitter, 12894395, 4, false, 0, false);
                break;
            }
            case MISSILE: {
                VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 50, 1.2f, 5.0f, new Vec3(0.0, 0.0, 2.5), new Vec3(1.5, 1.5, 3.0), false, muzzle, jitter, 13157053, 4, false, 0, false);
            }
        }
    }

    private static int pickTankSmokeColor(RandomSource random) {
        int[] colors = new int[]{12894395, 12170413, 13816013, 11380639, 10721933, 10065295, 0xDDDBD8};
        return colors[random.m_188503_(colors.length)];
    }

    private static void spawnCooldownSmoke(ServerLevel level, MuzzleAnchor anchor, RandomSource random, MuzzleEffectPreset preset) {
        MuzzlePositionResolver.MuzzlePose pose = anchor.resolve((Level)level);
        if (pose == null) {
            return;
        }
        Vec3 pos = pose.position();
        GunAxes axes = GunAxes.fromDirection(pose.direction());
        int count = preset == MuzzleEffectPreset.TANK ? 4 : 3;
        VvpMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, count, 45, 0.22f, 1.0f, new Vec3(0.0, 0.0, 0.6), new Vec3(0.5, 0.5, 0.5), false, new Vec3(0.0, 0.0, 0.1), new Vec3(0.05, 0.05, 0.05), 8156784, 4, true, 0, false);
    }

    private static void spawnBloom(ServerLevel level, Vec3 pos, float r, float g, float b, float from, float to, int life) {
        VvpMuzzleEffects.send(level, new VvpMuzzleParticleOption(r, g, b, life, 0.68f, 1, from, to, 1, 3), pos, Vec3.f_82478_);
    }

    private static void spawnBangStatic(ServerLevel level, Vec3 pos, float from, float to, int life) {
        VvpMuzzleEffects.send(level, new VvpMuzzleParticleOption(1.0f, 1.0f, 1.0f, life, 0.75f, 1, from, to, 9, 1), pos, Vec3.f_82478_);
    }

    private static void spawnBangSparks(ServerLevel level, Vec3 pos, GunAxes axes, RandomSource random, int count, double spreadX, double spreadY, double spreadZ, float forwardSpeed) {
        for (int i = 0; i < count; ++i) {
            Vec3 local = VvpMuzzleEffects.spreadVelocity(random, spreadX, spreadY, spreadZ, new Vec3(0.0, 0.0, (double)forwardSpeed));
            Vec3 velocity = axes.toWorld(local).m_82490_((double)0.1f);
            VvpMuzzleEffects.send(level, new VvpMuzzleParticleOption(1.0f, 0.95f, 0.75f, 4, 0.72f, 1, 1.0f, 0.01f, 9, 2), pos, velocity);
        }
    }

    private static void spawnBlastPuff(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, int count) {
        Vec3 spawn = pos.m_82549_(axes.toWorld(new Vec3(0.0, 0.0, -0.05)));
        for (int i = 0; i < count; ++i) {
            Vec3 local = VvpMuzzleEffects.spreadVelocity(random, 1.0, 1.0, 5.0, new Vec3(0.0, 0.0, 19.840062));
            Vec3 velocity = axes.toWorld(local).m_82490_((double)0.1f);
            VvpMuzzleEffects.send(level, new VvpMuzzleParticleOption(0.76f, 0.75f, 0.72f, 5, 0.995f, 2, 0.06f, 2.1f, 9, 0), spawn, velocity, anchor);
        }
    }

    private static void spawnSmokeBatch(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, int count, int life, float mtsFromScale, float mtsToScale, Vec3 localVelocity, Vec3 spread, boolean worldVelocity, Vec3 muzzleOffset, Vec3 spawnJitter, int rgb, int animSpeed, boolean linger, int movementDuration, boolean worldSpawnJitter) {
        float r = (float)(rgb >> 16 & 0xFF) / 255.0f;
        float g = (float)(rgb >> 8 & 0xFF) / 255.0f;
        float b = (float)(rgb & 0xFF) / 255.0f;
        Vec3 baseSpawn = pos.m_82549_(worldSpawnJitter ? muzzleOffset : axes.toWorld(muzzleOffset));
        for (int i = 0; i < count; ++i) {
            Vec3 jitter = new Vec3((random.m_188500_() * 2.0 - 1.0) * spawnJitter.f_82479_, (random.m_188500_() * 2.0 - 1.0) * spawnJitter.f_82480_, (random.m_188500_() * 2.0 - 1.0) * spawnJitter.f_82481_);
            Vec3 spawnPos = worldSpawnJitter ? baseSpawn.m_82549_(jitter) : baseSpawn.m_82549_(axes.toWorld(jitter));
            Vec3 local = VvpMuzzleEffects.spreadVelocity(random, spread.f_82479_, spread.f_82480_, spread.f_82481_, localVelocity);
            Vec3 velocity = (worldVelocity ? local : axes.toWorld(local)).m_82490_((double)0.1f);
            VvpMuzzleEffects.send(level, new VvpMuzzleParticleOption(r, g, b, life + random.m_188503_(Math.max(1, life / 8)), linger ? 0.998f : 0.9992f, animSpeed, mtsFromScale, mtsToScale, 9, 0, linger, movementDuration), spawnPos, velocity, anchor);
        }
    }

    private static Vec3 spreadVelocity(RandomSource random, double spreadX, double spreadY, double spreadZ, Vec3 base) {
        return new Vec3(base.f_82479_ + (random.m_188500_() * 2.0 - 1.0) * spreadX, base.f_82480_ + (random.m_188500_() * 2.0 - 1.0) * spreadY, base.f_82481_ + (random.m_188500_() * 2.0 - 1.0) * spreadZ);
    }

    private static void send(ServerLevel level, VvpMuzzleParticleOption option, Vec3 pos, Vec3 velocity) {
        VvpMuzzleEffects.send(level, option, pos, velocity, MuzzleAnchor.NONE);
    }

    private static void send(ServerLevel level, VvpMuzzleParticleOption option, Vec3 pos, Vec3 velocity, MuzzleAnchor anchor) {
        VvpMuzzleParticleOption payload = anchor.isValid() && option.layer() == 0 && option.lingerSmoke() ? option.withBarrelAttach(anchor.vehicleId(), anchor.seatIndex()) : option;
        level.m_8767_((ParticleOptions)payload, pos.f_82479_, pos.f_82480_, pos.f_82481_, 0, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_, 1.0);
    }

    public static void spawnHimarsGmlrsLaunch(ShootParameters parameters) {
        ServerLevel level = parameters.level;
        if (level == null) {
            return;
        }
        MuzzlePositionResolver.MuzzlePose muzzle = MuzzlePositionResolver.resolve(parameters);
        Vec3 pos = muzzle.position();
        Vec3 direction = muzzle.direction();
        if (pos == null || direction == null || direction.m_82556_() < 1.0E-6) {
            return;
        }
        direction = direction.m_82541_();
        GunAxes axes = GunAxes.fromDirection(direction);
        MuzzleAnchor anchor = MuzzleAnchor.from(parameters);
        RandomSource random = level.m_213780_();
        VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 0.92f, 7.0f, 0.08f, 2);
        VvpMuzzleEffects.spawnBloom(level, pos, 1.0f, 0.9f, 0.65f, 5.5f, 0.16f, 3);
        VvpMuzzleEffects.spawnBloom(level, pos, 0.98f, 0.62f, 0.35f, 3.8f, 0.1f, 3);
        VvpMuzzleEffects.spawnBangStatic(level, pos, 4.0f, 7.5f, 5);
        Vec3 tubeMouth = pos.m_82549_(direction.m_82490_(0.15));
        Entity entity = parameters.ammoSupplier;
        if (!(entity instanceof VehicleEntity)) {
            return;
        }
        VehicleEntity vehicle = (VehicleEntity)entity;
        HimarsBackblastResolver.Pose backblast = HimarsBackblastResolver.resolve(vehicle);
        if (backblast == null) {
            return;
        }
        Vec3 rear = backblast.position();
        Vec3 backDir = backblast.backDirection();
        VvpMuzzleEffects.spawnHimarsLauncherRearFlash(level, anchor, backblast, random);
        VvpMuzzleEffects.spawnHimarsCustomSmoke(level, anchor, tubeMouth, rear, axes, random, true);
        Vec3 groundPos = null;
        BlockState groundState = null;
        for (int dy = 0; dy <= 6; ++dy) {
            BlockPos checkPos = BlockPos.m_274561_((double)rear.f_82479_, (double)(rear.f_82480_ - (double)dy), (double)rear.f_82481_);
            BlockState state = level.m_8055_(checkPos);
            if (state.m_60795_() || !state.m_60819_().m_76178_()) continue;
            groundPos = new Vec3((double)checkPos.m_123341_() + 0.5, (double)checkPos.m_123342_() + 1.0, (double)checkPos.m_123343_() + 0.5);
            groundState = state;
            break;
        }
        if (groundPos != null && groundState != null) {
            BlockParticleOption particle = new BlockParticleOption(ParticleTypes.f_123794_, groundState);
            int particleCount = 40;
            for (int j = 0; j < particleCount; ++j) {
                double angle = (double)(j * 2) * Math.PI / (double)particleCount;
                double cos = Math.cos(angle);
                double sin = Math.sin(angle);
                double r = 0.6 + random.m_188500_() * 1.2;
                double px = groundPos.f_82479_ + cos * r;
                double py = groundPos.f_82480_ + 0.1;
                double pz = groundPos.f_82481_ + sin * r;
                double vx = cos * (0.8 + random.m_188500_() * 0.4);
                double vy = 0.3 + random.m_188500_() * 0.25;
                double vz = sin * (0.8 + random.m_188500_() * 0.4);
                level.m_8767_((ParticleOptions)particle, px, py, pz, 1, vx, vy, vz, 0.0);
            }
        }
        int tick = 0;
        while (tick < 6) {
            int smokeTick = tick++;
            Mod.queueServerWork((int)smokeTick, () -> VvpMuzzleEffects.spawnHimarsSmokeTick(level, anchor, random, smokeTick));
        }
        Entity entity2 = parameters.shooter;
        if (entity2 instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity)entity2;
            VvpMuzzleEffects.applyBackblastHazard(level, rear, backDir, shooter, vehicle);
        }
    }

    private static void spawnHimarsLauncherRearFlash(ServerLevel level, MuzzleAnchor anchor, HimarsBackblastResolver.Pose backblast, RandomSource random) {
        Vec3 launcherRear = backblast.position();
        GunAxes axes = backblast.axes();
        Vec3 backDir = backblast.backDirection();
        Vec3 ventLeft = launcherRear.m_82549_(axes.right().m_82490_(-0.85));
        Vec3 ventRight = launcherRear.m_82549_(axes.right().m_82490_(0.85));
        VvpMuzzleEffects.spawnLauncherFlashBurst(level, launcherRear, backDir, axes, random, 1.65f);
        VvpMuzzleEffects.spawnLauncherFlashBurst(level, ventLeft, backDir, axes, random, 1.25f);
        VvpMuzzleEffects.spawnLauncherFlashBurst(level, ventRight, backDir, axes, random, 1.25f);
        MuzzleFlashLight.spawnBackblast(level, launcherRear, backDir);
        int tick = 0;
        while (tick < 16) {
            int flashTick = tick++;
            Mod.queueServerWork((int)flashTick, () -> VvpMuzzleEffects.spawnHimarsLauncherFlashPulse(level, anchor, flashTick));
        }
    }

    private static void spawnLauncherFlashBurst(ServerLevel level, Vec3 pos, Vec3 backDir, GunAxes axes, RandomSource random, float scale) {
        Vec3 core = pos.m_82549_(backDir.m_82490_(0.35));
        Vec3 wide = pos.m_82549_(backDir.m_82490_(0.9));
        VvpMuzzleEffects.spawnBloom(level, core, 1.0f, 0.94f, 0.74f, 11.0f * scale, 0.28f * scale, 5);
        VvpMuzzleEffects.spawnBloom(level, core, 1.0f, 0.7f, 0.34f, 9.0f * scale, 0.24f * scale, 5);
        VvpMuzzleEffects.spawnBloom(level, wide, 0.98f, 0.55f, 0.2f, 7.5f * scale, 0.18f * scale, 4);
        VvpMuzzleEffects.spawnBangStatic(level, core, 8.5f * scale, 12.0f * scale, 10);
        VvpMuzzleEffects.spawnBangStatic(level, wide, 6.0f * scale, 9.5f * scale, 8);
        for (int i = 0; i < 5; ++i) {
            Vec3 local = new Vec3((random.m_188500_() - 0.5) * 1.4, random.m_188500_() * 0.25, -random.m_188500_() * 1.6 - 0.2);
            Vec3 sparkPos = pos.m_82549_(axes.toWorld(local));
            VvpMuzzleEffects.spawnBloom(level, sparkPos, 1.0f, 0.8f, 0.44f, 3.2f * scale, 0.09f * scale, 2);
        }
    }

    private static void spawnHimarsLauncherFlashPulse(ServerLevel level, MuzzleAnchor anchor, int tick) {
        HimarsBackblastResolver.Pose backblast = HimarsBackblastResolver.resolve((Level)level, anchor);
        if (backblast == null) {
            return;
        }
        Vec3 launcherRear = backblast.position();
        Vec3 backDir = backblast.backDirection();
        float fade = Math.max(0.12f, 1.0f - (float)tick * 0.08f);
        if (tick % 2 != 0) {
            fade *= 0.55f;
        }
        VvpMuzzleEffects.spawnBloom(level, launcherRear, 1.0f, 0.82f, 0.48f, 6.5f * fade, 0.14f * fade, 4);
        if (tick < 8) {
            VvpMuzzleEffects.spawnBangStatic(level, launcherRear.m_82549_(backDir.m_82490_(0.4)), 5.0f * fade, 8.0f * fade, 6);
        }
        if (tick < 4) {
            MuzzleFlashLight.spawnBackblast(level, launcherRear, backDir);
        }
    }

    private static void spawnHimarsCustomSmoke(ServerLevel level, MuzzleAnchor anchor, Vec3 tubeMouth, Vec3 rear, GunAxes axes, RandomSource random, boolean heavy) {
        VvpMuzzleEffects.spawnSmokeBatch(level, anchor, tubeMouth, axes, random, heavy ? 5 : 2, 35, 1.4f, 8.0f, new Vec3(0.0, -0.4, 0.0), new Vec3(1.5, 0.3, 1.5), true, new Vec3(0.0, -0.05, 0.0), new Vec3(0.5, 0.1, 0.5), 13815230, 0, false, 0, false);
        VvpMuzzleEffects.spawnSmokeBatch(level, anchor, rear, axes, random, heavy ? 18 : 8, 90, 3.2f, 44.0f, new Vec3(0.0, 0.8, -6.5), new Vec3(11.0, 3.0, 5.0), false, Vec3.f_82478_, new Vec3(0.65, 0.3, 0.55), 12499375, 0, false, 0, false);
        VvpMuzzleEffects.spawnSmokeBatch(level, anchor, rear, axes, random, heavy ? 9 : 4, 120, 4.5f, 68.0f, new Vec3(0.0, 0.5, 0.0), new Vec3(35.0, 2.0, 35.0), true, new Vec3(0.0, -0.1, 0.0), new Vec3(0.7, 0.15, 0.7), 14078666, 0, false, 18, true);
    }

    private static void spawnHimarsSmokeTick(ServerLevel level, MuzzleAnchor anchor, RandomSource random, int tick) {
        MuzzlePositionResolver.MuzzlePose pose = anchor.resolve((Level)level);
        if (pose == null) {
            return;
        }
        Vec3 pos = pose.position();
        Vec3 direction = pose.direction().m_82541_();
        GunAxes axes = GunAxes.fromDirection(direction);
        Vec3 tubeMouth = pos.m_82549_(direction.m_82490_(0.15));
        HimarsBackblastResolver.Pose backblast = HimarsBackblastResolver.resolve((Level)level, anchor);
        Vec3 rear = backblast != null ? backblast.position() : pos.m_82549_(direction.m_82490_(-4.8));
        VvpMuzzleEffects.spawnHimarsCustomSmoke(level, anchor, tubeMouth, rear, axes, random, tick < 2);
    }

    private static void applyBackblastHazard(ServerLevel level, Vec3 rear, Vec3 backDir, LivingEntity shooter, VehicleEntity vehicle) {
        AABB hazard = new AABB(rear, rear).m_82377_(16.0, 3.0, 16.0);
        for (LivingEntity victim : level.m_6443_(LivingEntity.class, hazard, entity -> entity != shooter && entity.m_20202_() != vehicle)) {
            Vec3 toNorm;
            Vec3 toVictim = victim.m_20182_().m_82546_(rear);
            if (toVictim.m_82556_() < 0.01 || (toNorm = toVictim.m_82541_()).m_82526_(backDir) < 0.45) continue;
            double dist = Math.sqrt(toVictim.m_82556_());
            float damage = (float)Mth.m_14008_((double)(36.0 - dist * 1.8), (double)4.0, (double)28.0);
            victim.m_6469_(level.m_269111_().m_269264_(), damage);
        }
    }
}

