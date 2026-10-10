/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.ProjectileImpactEvent
 *  net.minecraftforge.event.entity.player.AttackEntityEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$LeftClickBlock
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$RightClickBlock
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.event.level.BlockEvent$EntityPlaceEvent
 *  net.minecraftforge.event.level.ExplosionEvent$Detonate
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.bastion;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.api.event.BastionLifecycleEvent;
import org.espetro.bastion.FortificationManager;
import org.espetro.network.NetworkManager;

@Mod.EventBusSubscriber(modid="espetro")
public final class FortificationEventHandler {
    private FortificationEventHandler() {
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onBlockBroken(BlockEvent.BreakEvent event) {
        LevelAccessor levelAccessor;
        if (event.isCanceled() || !((levelAccessor = event.getLevel()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel level = (ServerLevel)levelAccessor;
        FortificationManager manager = FortificationManager.getInstance();
        if (!manager.contains(level, event.getPos())) {
            return;
        }
        if (event.getPlayer().m_21205_().m_41720_() == Items.f_42384_) {
            event.setCanceled(true);
            return;
        }
        manager.damageAt(level, event.getPos(), event.getPlayer());
    }

    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Detonate event) {
        Level level = event.getLevel();
        if (!(level instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        Explosion explosion = event.getExplosion();
        List<BlockPos> affected = List.copyOf(event.getAffectedBlocks());
        double radius = 6.0;
        if (!affected.isEmpty()) {
            radius = 0.0;
            for (BlockPos pos : affected) {
                radius = Math.max(radius, Math.sqrt(pos.m_203198_(explosion.getPosition().f_82479_, explosion.getPosition().f_82480_, explosion.getPosition().f_82481_)));
            }
            radius += 1.5;
        }
        FortificationManager.getInstance().damageExplosion(level2, explosion.getPosition(), (float)radius, affected, explosion.m_252906_());
    }

    @SubscribeEvent
    public static void onProjectileImpact(ProjectileImpactEvent event) {
        Entity attacker;
        boolean smallCaliberShell;
        Level level;
        if (event.getEntity().m_9236_().m_5776_() || !((level = event.getEntity().m_9236_()) instanceof ServerLevel)) {
            return;
        }
        ServerLevel level2 = (ServerLevel)level;
        String projectileClass = event.getEntity().getClass().getName();
        boolean bl = smallCaliberShell = "com.atsuishio.superbwarfare.entity.projectile.SmallCannonShellEntity".equals(projectileClass) || "com.redabysslucia.dragonrise_reforge.entities.projectile.AAshellEntity".equals(projectileClass);
        if (!smallCaliberShell) {
            return;
        }
        HitResult ray = event.getRayTraceResult();
        Entity projectile = event.getEntity();
        if (projectile instanceof Projectile) {
            Projectile p = (Projectile)projectile;
            v1 = p.m_19749_();
        } else {
            v1 = attacker = null;
        }
        if (ray instanceof BlockHitResult) {
            BlockHitResult blockHit = (BlockHitResult)ray;
            FortificationManager.getInstance().damageAt(level2, blockHit.m_82425_(), attacker, FortificationManager.DamageKind.PROJECTILE);
        } else if (ray instanceof EntityHitResult) {
            EntityHitResult entityHit = (EntityHitResult)ray;
            FortificationManager.getInstance().damageEntity(level2, entityHit.m_82443_().m_20148_(), attacker);
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onEntityLeave(EntityLeaveLevelEvent event) {
        if (!event.getLevel().m_5776_()) {
            FortificationManager.getInstance().removeEntity(event.getEntity().m_20148_());
        }
    }

    @SubscribeEvent
    public static void onLeftClick(PlayerInteractEvent.LeftClickBlock event) {
        FortificationEventHandler.guardShovelWork((PlayerInteractEvent)event, true);
    }

    @SubscribeEvent
    public static void onRightClick(PlayerInteractEvent.RightClickBlock event) {
        FortificationEventHandler.guardShovelWork((PlayerInteractEvent)event, false);
    }

    private static void guardShovelWork(PlayerInteractEvent event, boolean build) {
        block3: {
            block2: {
                Level level;
                if (event.getLevel().m_5776_() || !((level = event.getLevel()) instanceof ServerLevel)) break block2;
                ServerLevel level2 = (ServerLevel)level;
                if (event.getEntity().m_21205_().m_41720_() == Items.f_42384_ && FortificationManager.getInstance().contains(level2, event.getPos())) break block3;
            }
            return;
        }
        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.SUCCESS);
    }

    @SubscribeEvent
    public static void onBlockPlaced(BlockEvent.EntityPlaceEvent event) {
        block5: {
            block4: {
                LevelAccessor levelAccessor = event.getLevel();
                if (!(levelAccessor instanceof ServerLevel)) break block4;
                ServerLevel level = (ServerLevel)levelAccessor;
                if (FortificationManager.getInstance().contains(level, event.getPos())) break block5;
            }
            return;
        }
        event.setCanceled(true);
        Entity entity = event.getEntity();
        if (entity instanceof ServerPlayer) {
            ServerPlayer player = (ServerPlayer)entity;
            player.m_5661_(Component.m_237113_("\u00a7c\u8be5\u4f4d\u7f6e\u5df2\u88ab\u5de5\u4e8b\u65bd\u5de5\u8303\u56f4\u5360\u7528\u3002"), true);
        }
    }

    @SubscribeEvent
    public static void onAttackEntity(AttackEntityEvent event) {
        if (event.getEntity().m_21205_().m_41720_() == Items.f_42384_ && FortificationManager.getInstance().containsEntity(event.getTarget().m_20148_())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onUseEntity(PlayerInteractEvent.EntityInteract event) {
        if (event.getEntity().m_21205_().m_41720_() == Items.f_42384_ && FortificationManager.getInstance().containsEntity(event.getTarget().m_20148_())) {
            event.setCanceled(true);
            event.setCancellationResult(InteractionResult.SUCCESS);
        }
    }

    @SubscribeEvent
    public static void onBastionBuilt(BastionLifecycleEvent.Built event) {
        NetworkManager.refreshDeployPointsForTeam(event.team());
    }

    @SubscribeEvent
    public static void onBastionDestroyed(BastionLifecycleEvent.Destroyed event) {
        FortificationManager.getInstance().onBastionDestroyed(event.bastionId(), event.level(), event.attacker());
        NetworkManager.refreshDeployPointsForTeam(event.team());
    }
}

