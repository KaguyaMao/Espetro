/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atsuishio.superbwarfare.init.ModDamageTypes
 *  com.atsuishio.superbwarfare.tools.CustomExplosion$Builder
 *  com.atsuishio.superbwarfare.tools.DamageHandler
 *  com.mojang.logging.LogUtils
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.projectile.Projectile
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.phys.EntityHitResult
 *  net.minecraft.world.phys.HitResult
 *  net.minecraft.world.phys.Vec3
 *  net.minecraftforge.event.entity.ProjectileImpactEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.registries.ForgeRegistries
 *  org.slf4j.Logger
 */
package frontline.combat.fcp.compat.vpb;

import com.atsuishio.superbwarfare.init.ModDamageTypes;
import com.atsuishio.superbwarfare.tools.CustomExplosion;
import com.atsuishio.superbwarfare.tools.DamageHandler;
import com.mojang.logging.LogUtils;
import frontline.combat.fcp.compat.vpb.VpbIntegration;
import frontline.combat.fcp.compat.vpb.VpbIntegrationConfig;
import frontline.combat.fcp.compat.vpb.WarheadStats;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid="fcp")
public final class VpbProjectileImpactHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    private VpbProjectileImpactHandler() {
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        if (!VpbIntegration.isVpbLoaded()) {
            return;
        }
        VpbIntegrationConfig cfg = VpbIntegrationConfig.get();
        if (cfg.projectileWarheads.isEmpty()) {
            return;
        }
        Projectile projectile = event.getProjectile();
        if (projectile == null) {
            return;
        }
        ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey((Object)projectile.m_6095_());
        WarheadStats stats = cfg.projectileWarheads.get(id);
        if (stats == null) {
            if (cfg.debugLogging && id != null && "pointblank".equals(id.m_135827_())) {
                LOGGER.info("[FCP/VPB] Unmapped pointblank projectile impact: {} (add it to projectileWarheads to replace its impact)", (Object)id);
            }
            return;
        }
        event.setCanceled(true);
        Level level = projectile.m_9236_();
        HitResult result = event.getRayTraceResult();
        Vec3 pos = result.m_82450_();
        if (!level.f_46443_) {
            EntityHitResult entityHit;
            Entity target;
            Entity shooter = projectile.m_19749_();
            if (stats.hasDirectHit() && result instanceof EntityHitResult && (target = (entityHit = (EntityHitResult)result).m_82443_()) != null) {
                DamageHandler.doDamage((Entity)target, (DamageSource)ModDamageTypes.causeProjectileHitDamage((RegistryAccess)level.m_9598_(), (Entity)projectile, (Entity)shooter), (float)stats.directDamage);
            }
            if (stats.hasExplosion()) {
                CustomExplosion.Builder builder = new CustomExplosion.Builder((Entity)projectile).attacker(shooter).damage(stats.explosionDamage).radius(stats.explosionRadius).withParticleType(stats.resolveExplosionParticle()).fireTime(stats.fireTime).position(pos);
                if (!stats.destroyBlocks) {
                    builder.keepBlock();
                }
                builder.explode();
            }
            if (cfg.debugLogging) {
                LOGGER.info("[FCP/VPB] {} -> SBW warhead {} at {}", new Object[]{id, stats, pos});
            }
        }
        projectile.m_146870_();
    }
}

