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
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.registries.ForgeRegistries
 */
package frontline.combat.fcp.effects;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.data.gun.GunData;
import com.atsuishio.superbwarfare.data.gun.GunProp;
import com.atsuishio.superbwarfare.data.gun.ProjectileInfo;
import com.atsuishio.superbwarfare.data.gun.ShootParameters;
import com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import frontline.combat.fcp.client.particle.FCPMuzzleParticleOption;
import frontline.combat.fcp.effects.GunAxes;
import frontline.combat.fcp.effects.MuzzleAnchor;
import frontline.combat.fcp.effects.MuzzleBurstTracker;
import frontline.combat.fcp.effects.MuzzleEffectPreset;
import frontline.combat.fcp.effects.MuzzleFlashLight;
import frontline.combat.fcp.effects.MuzzlePositionResolver;
import java.util.Set;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

public final class FCPMuzzleEffects {
    private static final float MTS_VEL = 0.1f;
    private static final int HIGH_RPM_AUTOCANNON = 400;
    private static final Set<String> FORWARD_CANNON_BLAST_VEHICLES = Set.of("fcp:stryker_mgs");

    private FCPMuzzleEffects() {
    }

    public static boolean isFCPVehicle(VehicleEntity vehicle) {
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        return id != null && "fcp".equals(id.m_135827_());
    }

    private static boolean isForwardCannonBlast(Entity supplier) {
        if (!(supplier instanceof VehicleEntity)) {
            return false;
        }
        VehicleEntity vehicle = (VehicleEntity)supplier;
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)vehicle.m_6095_());
        return id != null && FORWARD_CANNON_BLAST_VEHICLES.contains(id.toString());
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
        MuzzleEffectPreset preset = FCPMuzzleEffects.resolvePreset(parameters.data);
        GunAxes axes = GunAxes.fromDirection(direction.m_82541_());
        MuzzleAnchor anchor = MuzzleAnchor.from(parameters);
        if (preset == MuzzleEffectPreset.TANK && FCPMuzzleEffects.isForwardCannonBlast(parameters.ammoSupplier)) {
            FCPMuzzleEffects.spawnForwardCannonBlast(level, anchor, pos, axes, level.m_213780_());
            return;
        }
        long gameTick = level.m_46467_();
        int shotIndex = anchor.isValid() ? MuzzleBurstTracker.recordShot(anchor.vehicleId(), gameTick) : 0;
        int rpm = (Integer)parameters.data.get(GunProp.RPM);
        boolean bl = sustainedAutocannon = preset == MuzzleEffectPreset.AUTOCANNON && rpm >= 400 && anchor.isValid() && MuzzleBurstTracker.isSustained(anchor.vehicleId(), gameTick);
        if (sustainedAutocannon) {
            FCPMuzzleEffects.spawnSustainedAutocannon(level, anchor, pos, axes, level.m_213780_(), shotIndex);
            return;
        }
        FCPMuzzleEffects.spawnInstantFlash(level, anchor, pos, axes, level.m_213780_(), preset);
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
            Mod.queueServerWork((int)smokeTick, () -> FCPMuzzleEffects.spawnSmokeTick(level, anchor, level.m_213780_(), preset, smokeTick));
        }
        if (preset == MuzzleEffectPreset.TANK || preset == MuzzleEffectPreset.AUTOCANNON) {
            Mod.queueServerWork((int)5, () -> FCPMuzzleEffects.spawnCooldownSmoke(level, anchor, level.m_213780_(), preset));
        }
    }

    private static void spawnSustainedAutocannon(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, int shotIndex) {
        MuzzleFlashLight.spawn(level, pos, axes.forward(), MuzzleEffectPreset.AUTOCANNON);
        FCPMuzzleEffects.spawnBloom(level, pos, 1.0f, 0.98f, 0.9f, 3.4f, 0.11f, 2);
        FCPMuzzleEffects.spawnBangSparks(level, pos, axes, random, 2, 1.4, 1.4, 1.0, 1.5f);
        FCPMuzzleEffects.spawnBlastPuff(level, anchor, pos, axes, random, 2);
        Vec3 muzzle = new Vec3(0.0, -0.1, 0.0);
        Vec3 jitter = new Vec3(0.08, 0.08, 0.12);
        Vec3 forwardVel = new Vec3(0.0, 0.0, 1.840062);
        Vec3 forwardSpread = new Vec3(1.0, 1.0, 4.0);
        FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 45, 1.0f, 7.0f, forwardVel, forwardSpread, false, muzzle, jitter, 12894395, 6, false, 0, false);
        FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 1, 35, 0.9f, 4.5f, forwardVel, new Vec3(0.8, 0.8, 3.0), false, muzzle, jitter, 0xC1C1C1, 5, false, 0, false);
        if (shotIndex % 3 == 0) {
            FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 55, 1.06f, 9.0f, forwardVel, forwardSpread, false, muzzle, jitter, 12170413, 7, false, 0, false);
        }
        if (shotIndex % 5 == 0) {
            FCPMuzzleEffects.spawnCooldownSmoke(level, anchor, random, MuzzleEffectPreset.AUTOCANNON);
        }
    }

    private static void spawnForwardCannonBlast(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random) {
        Vec3 fwd = axes.forward();
        MuzzleFlashLight.spawn(level, pos, fwd, MuzzleEffectPreset.TANK);
        Vec3 jet = fwd.m_82490_(0.15);
        FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 1.0f, 0.95f, 8, 0.86f, 1, 16.0f, 1.5f, 1, 3), pos, jet);
        Vec3 f1 = pos.m_82549_(fwd.m_82490_(1.0));
        FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 0.9f, 0.55f, 9, 0.87f, 1, 13.0f, 1.2f, 1, 3), f1, jet);
        Vec3 f2 = pos.m_82549_(fwd.m_82490_(2.2));
        FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 0.62f, 0.28f, 10, 0.88f, 1, 9.0f, 1.0f, 1, 3), f2, jet);
        FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 1.0f, 1.0f, 5, 0.88f, 2, 6.0f, 8.0f, 9, 1), pos, Vec3.f_82478_);
        for (int i = 0; i < 14; ++i) {
            Vec3 spark = fwd.m_82490_(0.45 + random.m_188500_() * 0.6).m_82549_(axes.toWorld(new Vec3((random.m_188500_() - 0.5) * 0.22, (random.m_188500_() - 0.5) * 0.22, 0.0)));
            FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 0.95f, 0.75f, 8, 0.86f, 1, 2.8f, 0.02f, 9, 2), pos, spark);
        }
        FCPMuzzleEffects.spawnForwardSmoke(level, anchor, pos, axes, random, true);
        for (int tick = 1; tick <= 3; ++tick) {
            Mod.queueServerWork((int)tick, () -> {
                MuzzlePositionResolver.MuzzlePose pose = anchor.resolve((Level)level);
                if (pose == null) {
                    return;
                }
                GunAxes tickAxes = GunAxes.fromDirection(pose.direction());
                FCPMuzzleEffects.spawnForwardSmoke(level, anchor, pose.position(), tickAxes, level.m_213780_(), false);
            });
        }
    }

    private static void spawnForwardSmoke(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, boolean initial) {
        Vec3 muzzle = new Vec3(0.0, -0.05, 0.0);
        Vec3 jitter = new Vec3(0.1, 0.1, 0.15);
        if (initial) {
            FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 10, 90, 1.2f, 14.0f, new Vec3(0.0, 0.0, 9.0), new Vec3(1.2, 1.2, 4.0), false, muzzle, jitter, 13223102, 9, false, 12, false);
            FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 6, 60, 0.9f, 8.0f, new Vec3(0.0, 0.0, 13.0), new Vec3(0.8, 0.8, 3.0), false, muzzle, jitter, 0xC1C1C1, 7, false, 10, false);
            FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 4, 70, 1.0f, 6.0f, new Vec3(0.0, 0.0, 2.0), new Vec3(1.0, 0.8, 2.0), false, new Vec3(0.0, 0.05, 0.0), new Vec3(0.12, 0.12, 0.15), 12170413, 8, true, 0, false);
        } else {
            FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 4, 70, 1.0f, 9.0f, new Vec3(0.0, 0.0, 6.0), new Vec3(1.0, 1.0, 3.0), false, muzzle, jitter, 12894395, 8, false, 9, false);
        }
        FCPMuzzleEffects.spawnBillow(level, pos, axes, random, initial);
    }

    private static void spawnBillow(ServerLevel level, Vec3 pos, GunAxes axes, RandomSource random, boolean initial) {
        Vec3 fwd = axes.forward();
        int puffs = initial ? 16 : 6;
        double reach = initial ? 3.0 : 1.6;
        double drift = initial ? 0.15 : 0.11;
        for (int i = 0; i < puffs; ++i) {
            Vec3 spawn = pos.m_82549_(fwd.m_82490_(0.4 + random.m_188500_() * reach)).m_82549_(axes.toWorld(new Vec3((random.m_188500_() - 0.5) * 0.22, (random.m_188500_() - 0.5) * 0.22, 0.0)));
            double spd = drift * (0.7 + random.m_188500_() * 0.6);
            Vec3 vel = fwd.m_82490_(spd).m_82549_(axes.toWorld(new Vec3((random.m_188500_() - 0.5) * 0.03, (random.m_188500_() - 0.5) * 0.03 + 0.015, 0.0)));
            ParticleTool.sendParticle((ServerLevel)level, (ParticleOptions)ParticleTypes.f_123777_, (double)spawn.f_82479_, (double)spawn.f_82480_, (double)spawn.f_82481_, (int)0, (double)vel.f_82479_, (double)vel.f_82480_, (double)vel.f_82481_, (double)1.0, (boolean)true);
        }
    }

    private static void spawnInstantFlash(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, MuzzleEffectPreset preset) {
        switch (preset) {
            case MACHINE_GUN: {
                FCPMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 1.0f, 2.8f, 0.12f, 3);
                FCPMuzzleEffects.spawnBangSparks(level, pos, axes, random, 3, 1.2, 1.2, 0.8, 1.2f);
                break;
            }
            case AUTOCANNON: {
                FCPMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 1.0f, 4.5f, 0.14f, 3);
                FCPMuzzleEffects.spawnBloom(level, pos, 0.98f, 0.72f, 0.45f, 3.8f, 0.12f, 3);
                FCPMuzzleEffects.spawnBangSparks(level, pos, axes, random, 6, 2.0, 2.5, 2.0, 1.8f);
                FCPMuzzleEffects.spawnBlastPuff(level, anchor, pos, axes, random, 3);
                break;
            }
            case TANK: {
                FCPMuzzleEffects.spawnBloom(level, pos, 1.0f, 1.0f, 1.0f, 10.5f, 0.22f, 3);
                FCPMuzzleEffects.spawnBloom(level, pos, 0.99f, 0.94f, 0.71f, 8.5f, 0.18f, 3);
                FCPMuzzleEffects.spawnBloom(level, pos, 0.98f, 0.67f, 0.45f, 6.5f, 0.14f, 4);
                FCPMuzzleEffects.spawnBangStatic(level, pos, 3.6f, 4.8f, 10);
                FCPMuzzleEffects.spawnBangSparks(level, pos, axes, random, 10, 4.0, 5.0, 4.0, 2.6f);
                FCPMuzzleEffects.spawnBlastPuff(level, anchor, pos, axes, random, 6);
                break;
            }
            case MISSILE: {
                FCPMuzzleEffects.spawnBloom(level, pos, 1.0f, 0.9f, 0.6f, 3.5f, 0.2f, 4);
                FCPMuzzleEffects.spawnBangSparks(level, pos, axes, random, 4, 1.5, 1.5, 1.2, 1.0f);
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
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 2, 35, 0.55f, 2.5f, forwardVel, forwardSpread, false, muzzle, jitter, 13157053, 4, false, 0, false);
                break;
            }
            case AUTOCANNON: {
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 80, 1.06f, 10.02f, forwardVel, forwardSpread, false, muzzle, jitter, 0xC1C1C1, 9, false, 0, false);
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 40, 1.06f, 2.06f, forwardVel, forwardSpread, false, muzzle, jitter, 0xC1C1C1, 4, false, 0, false);
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 40, 1.06f, 3.02f, new Vec3(0.0, -0.30625, 0.025), forwardSpread, false, muzzle, jitter, 0xC1C1C1, 4, false, 0, false);
                break;
            }
            case TANK: {
                if (tick == 0) {
                    FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 10, 120, 4.0f, 10.0f, new Vec3(0.0, 0.0, 8.0), new Vec3(2.0, 2.0, 7.0), false, new Vec3(0.0, 0.0, 0.35), new Vec3(0.15, 0.15, 0.2), FCPMuzzleEffects.pickTankSmokeColor(random), 9, false, 14, false);
                }
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 4, 150, 9.0f, 20.0f, new Vec3(0.0, 0.5, 0.0), new Vec3(18.0, 0.5, 18.0), true, new Vec3(0.0, -0.15, 0.0), new Vec3(0.25, 0.05, 0.25), 14340554, 9, false, 5, true);
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 45, 1.06f, 3.5f, new Vec3(0.0, -0.4, 0.05), new Vec3(2.0, 2.0, 5.0), false, muzzle, jitter, 12894395, 4, false, 0, false);
                break;
            }
            case MISSILE: {
                FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, 3, 50, 1.2f, 5.0f, new Vec3(0.0, 0.0, 2.5), new Vec3(1.5, 1.5, 3.0), false, muzzle, jitter, 13157053, 4, false, 0, false);
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
        FCPMuzzleEffects.spawnSmokeBatch(level, anchor, pos, axes, random, count, 45, 0.22f, 1.0f, new Vec3(0.0, 0.0, 0.6), new Vec3(0.5, 0.5, 0.5), false, new Vec3(0.0, 0.0, 0.1), new Vec3(0.05, 0.05, 0.05), 8156784, 4, true, 0, false);
    }

    private static void spawnBloom(ServerLevel level, Vec3 pos, float r, float g, float b, float from, float to, int life) {
        FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(r, g, b, life, 0.68f, 1, from, to, 1, 3), pos, Vec3.f_82478_);
    }

    private static void spawnBangStatic(ServerLevel level, Vec3 pos, float from, float to, int life) {
        FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 1.0f, 1.0f, life, 0.75f, 1, from, to, 9, 1), pos, Vec3.f_82478_);
    }

    private static void spawnBangSparks(ServerLevel level, Vec3 pos, GunAxes axes, RandomSource random, int count, double spreadX, double spreadY, double spreadZ, float forwardSpeed) {
        for (int i = 0; i < count; ++i) {
            Vec3 local = FCPMuzzleEffects.spreadVelocity(random, spreadX, spreadY, spreadZ, new Vec3(0.0, 0.0, (double)forwardSpeed));
            Vec3 velocity = axes.toWorld(local).m_82490_((double)0.1f);
            FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(1.0f, 0.95f, 0.75f, 4, 0.72f, 1, 1.0f, 0.01f, 9, 2), pos, velocity);
        }
    }

    private static void spawnBlastPuff(ServerLevel level, MuzzleAnchor anchor, Vec3 pos, GunAxes axes, RandomSource random, int count) {
        Vec3 spawn = pos.m_82549_(axes.toWorld(new Vec3(0.0, 0.0, -0.05)));
        for (int i = 0; i < count; ++i) {
            Vec3 local = FCPMuzzleEffects.spreadVelocity(random, 1.0, 1.0, 5.0, new Vec3(0.0, 0.0, 19.840062));
            Vec3 velocity = axes.toWorld(local).m_82490_((double)0.1f);
            FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(0.76f, 0.75f, 0.72f, 5, 0.995f, 2, 0.06f, 2.1f, 9, 0), spawn, velocity, anchor);
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
            Vec3 local = FCPMuzzleEffects.spreadVelocity(random, spread.f_82479_, spread.f_82480_, spread.f_82481_, localVelocity);
            Vec3 velocity = (worldVelocity ? local : axes.toWorld(local)).m_82490_((double)0.1f);
            FCPMuzzleEffects.send(level, new FCPMuzzleParticleOption(r, g, b, life + random.m_188503_(Math.max(1, life / 8)), linger ? 0.998f : 0.9992f, animSpeed, mtsFromScale, mtsToScale, 9, 0, linger, movementDuration), spawnPos, velocity, anchor);
        }
    }

    private static Vec3 spreadVelocity(RandomSource random, double spreadX, double spreadY, double spreadZ, Vec3 base) {
        return new Vec3(base.f_82479_ + (random.m_188500_() * 2.0 - 1.0) * spreadX, base.f_82480_ + (random.m_188500_() * 2.0 - 1.0) * spreadY, base.f_82481_ + (random.m_188500_() * 2.0 - 1.0) * spreadZ);
    }

    private static void send(ServerLevel level, FCPMuzzleParticleOption option, Vec3 pos, Vec3 velocity) {
        FCPMuzzleEffects.send(level, option, pos, velocity, MuzzleAnchor.NONE);
    }

    private static void send(ServerLevel level, FCPMuzzleParticleOption option, Vec3 pos, Vec3 velocity, MuzzleAnchor anchor) {
        FCPMuzzleParticleOption payload = anchor.isValid() && option.layer() == 0 && option.lingerSmoke() ? option.withBarrelAttach(anchor.vehicleId(), anchor.seatIndex()) : option;
        level.m_8767_((ParticleOptions)payload, pos.f_82479_, pos.f_82480_, pos.f_82481_, 0, velocity.f_82479_, velocity.f_82480_, velocity.f_82481_, 1.0);
    }
}

