/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.Mod
 *  com.atsuishio.superbwarfare.tools.ParticleTool
 *  com.mojang.logging.LogUtils
 *  net.minecraft.core.particles.ParticleOptions
 *  net.minecraft.core.particles.ParticleTypes
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.slf4j.Logger
 */
package frontline.combat.fcp.compat.vpb;

import com.atsuishio.superbwarfare.Mod;
import com.atsuishio.superbwarfare.tools.ParticleTool;
import com.mojang.logging.LogUtils;
import frontline.combat.fcp.compat.vpb.VpbIntegration;
import frontline.combat.fcp.compat.vpb.VpbIntegrationConfig;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid="fcp")
public final class VpbMuzzleSmokeHandler {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int DOWNRANGE_SMOKE_DELAY_TICKS = 2;

    private VpbMuzzleSmokeHandler() {
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinLevelEvent event) {
        Vec3 muzzle;
        if (!VpbIntegration.isVpbLoaded()) {
            return;
        }
        Level level = event.getLevel();
        if (level.f_46443_ || !(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel serverLevel = (ServerLevel)level;
        VpbIntegrationConfig cfg = VpbIntegrationConfig.get();
        if (cfg.muzzleSmokeProjectiles.isEmpty()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof Projectile)) {
            return;
        }
        Projectile projectile = (Projectile)entity;
        ResourceLocation projId = ForgeRegistries.ENTITY_TYPES.getKey((Object)projectile.m_6095_());
        if (projId == null) {
            return;
        }
        if (!cfg.muzzleSmokeProjectiles.contains(projId)) {
            if (cfg.debugLogging && "pointblank".equals(projId.m_135827_())) {
                LOGGER.info("[FCP/VPB] pointblank projectile {} spawned (add it to muzzleSmokeProjectiles for RPG smoke)", (Object)projId);
            }
            return;
        }
        Entity entity2 = projectile.m_19749_();
        if (entity2 instanceof LivingEntity) {
            LivingEntity shooter = (LivingEntity)entity2;
            Vec3 look = shooter.m_20154_();
            muzzle = new Vec3(shooter.m_20185_() + 1.8 * look.f_82479_, shooter.m_20186_() + (double)shooter.m_20206_() - 0.1 + 1.8 * look.f_82480_, shooter.m_20189_() + 1.8 * look.f_82481_);
        } else {
            muzzle = projectile.m_20182_();
        }
        ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123796_, (double)muzzle.f_82479_, (double)muzzle.f_82480_, (double)muzzle.f_82481_, (int)30, (double)0.4, (double)0.4, (double)0.4, (double)0.005, (boolean)true);
        Mod.queueServerWork((int)2, () -> {
            if (!projectile.m_6084_()) {
                return;
            }
            Vec3 p = projectile.m_20182_();
            ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123796_, (double)p.f_82479_, (double)p.f_82480_, (double)p.f_82481_, (int)15, (double)0.8, (double)0.8, (double)0.8, (double)0.01, (boolean)true);
            ParticleTool.sendParticle((ServerLevel)serverLevel, (ParticleOptions)ParticleTypes.f_123777_, (double)p.f_82479_, (double)p.f_82480_, (double)p.f_82481_, (int)10, (double)0.8, (double)0.8, (double)0.8, (double)0.01, (boolean)true);
        });
    }
}

