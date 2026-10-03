/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.entity.projectile.MediumRocketEntity
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.AABB
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.api.distmarker.OnlyIn
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package tech.vvp.vvp.client.effect;

import com.atsuishio.superbwarfare.entity.projectile.MediumRocketEntity;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import tech.vvp.vvp.client.particle.VvpMuzzleParticleOption;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;

@OnlyIn(value=Dist.CLIENT)
@Mod.EventBusSubscriber(modid="vvp", value={Dist.CLIENT})
public final class GmlrsRocketTrailClient {
    private static final int SMOKE_ANIM = 0;
    private static final int ADOPT_MAX_AGE = 120;
    private static final Set<UUID> TRACKED_ROCKETS = ConcurrentHashMap.newKeySet();

    private GmlrsRocketTrailClient() {
    }

    public static boolean isHimarsGmlrsRocket(MediumRocketEntity rocket) {
        if (TRACKED_ROCKETS.contains(rocket.m_20148_())) {
            return true;
        }
        return GmlrsRocketTrailClient.tryAdoptRocket(rocket);
    }

    @SubscribeEvent
    public static void onRocketJoin(EntityJoinLevelEvent event) {
        if (!event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof MediumRocketEntity)) {
            return;
        }
        MediumRocketEntity rocket = (MediumRocketEntity)entity;
        GmlrsRocketTrailClient.tryAdoptRocket(rocket);
    }

    @SubscribeEvent
    public static void onRocketLeave(EntityLeaveLevelEvent event) {
        TRACKED_ROCKETS.remove(event.getEntity().m_20148_());
    }

    private static boolean tryAdoptRocket(MediumRocketEntity rocket) {
        HimarsEntity himars;
        LivingEntity living;
        if (rocket.f_19797_ > 120) {
            return false;
        }
        Entity owner = rocket.m_19749_();
        if (owner instanceof LivingEntity && (living = (LivingEntity)owner).m_20202_() instanceof HimarsEntity) {
            TRACKED_ROCKETS.add(rocket.m_20148_());
            return true;
        }
        if (owner instanceof HimarsEntity && !(himars = (HimarsEntity)owner).m_20197_().isEmpty()) {
            TRACKED_ROCKETS.add(rocket.m_20148_());
            return true;
        }
        if (owner == null && rocket.f_19797_ <= 5) {
            AABB search = new AABB(rocket.m_20185_() - 10.0, rocket.m_20186_() - 5.0, rocket.m_20189_() - 10.0, rocket.m_20185_() + 10.0, rocket.m_20186_() + 5.0, rocket.m_20189_() + 10.0);
            List nearby = rocket.m_9236_().m_45976_(HimarsEntity.class, search);
            for (HimarsEntity h : nearby) {
                if (h.m_20197_().isEmpty()) continue;
                TRACKED_ROCKETS.add(rocket.m_20148_());
                return true;
            }
        }
        return false;
    }

    public static void spawnLargeTrail(MediumRocketEntity rocket) {
        if (!rocket.m_9236_().m_5776_() || rocket.f_19797_ <= 2) {
            return;
        }
        int tick = rocket.f_19797_;
        Vec3 motion = rocket.m_20184_();
        double speed = motion.m_82553_();
        if (speed < 1.0E-6) {
            return;
        }
        Level level = rocket.m_9236_();
        Vec3 direction = motion.m_82541_();
        Vec3 startPos = new Vec3(rocket.f_19854_, rocket.f_19855_ + (double)rocket.m_20206_() * 0.5, rocket.f_19856_);
        RandomSource rng = level.m_213780_();
        if (tick <= 30) {
            GmlrsRocketTrailClient.spawnEngineFire(level, startPos, tick);
        }
        for (double i = 0.0; i < speed; i += 2.0) {
            Vec3 pos = startPos.m_82549_(direction.m_82490_(-i));
            float random = 2.0f * (rng.m_188501_() - 0.5f);
            level.m_7106_((ParticleOptions)new VvpMuzzleParticleOption(0.5f, 0.43f, 0.36f, 160, 0.93f, 0, 2.5f, 8.0f, 9, 0, true, 20), pos.f_82479_ + (double)random * 0.3, pos.f_82480_ + (double)random * 0.3, pos.f_82481_ + (double)random * 0.3, 0.0, 0.0, 0.0);
        }
    }

    private static void spawnEngineFire(Level level, Vec3 tail, int tick) {
        float intensity = Math.max(0.0f, 1.0f - (float)(tick - 3) / 28.0f);
        level.m_7106_((ParticleOptions)new VvpMuzzleParticleOption(1.0f, 0.96f, 0.88f, 3 + (int)(5.0f * intensity), (float)(0.8 + (double)intensity * 0.06), 0, 1.8f + intensity * 3.2f, 7.0f + intensity * 11.0f, 1, 3, false, 0), tail.f_82479_, tail.f_82480_, tail.f_82481_, 0.0, 0.0, 0.0);
        if (tick <= 18) {
            float coreIntensity = Math.max(0.0f, 1.0f - (float)(tick - 3) / 15.0f);
            level.m_7106_((ParticleOptions)new VvpMuzzleParticleOption(1.0f, 0.99f, 0.96f, 2 + (int)(3.0f * coreIntensity), 0.78f, 0, 1.2f + coreIntensity * 2.0f, 5.0f + coreIntensity * 7.0f, 1, 3, false, 0), tail.f_82479_, tail.f_82480_, tail.f_82481_, 0.0, 0.0, 0.0);
        }
    }
}

