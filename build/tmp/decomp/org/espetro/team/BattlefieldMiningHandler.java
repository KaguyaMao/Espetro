/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.player.PlayerEvent$BreakSpeed
 *  net.minecraftforge.event.entity.player.PlayerEvent$HarvestCheck
 *  net.minecraftforge.event.level.BlockEvent$BreakEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.team;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.team.GameStateManager;

@Mod.EventBusSubscriber(modid="espetro")
public final class BattlefieldMiningHandler {
    private BattlefieldMiningHandler() {
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void onHarvestCheck(PlayerEvent.HarvestCheck event) {
        if (GameStateManager.getInstance().shouldRestrictBattlefieldMining(event.getEntity())) {
            event.setCanHarvest(false);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void onBreakSpeed(PlayerEvent.BreakSpeed event) {
        if (GameStateManager.getInstance().shouldRestrictBattlefieldMining(event.getEntity())) {
            event.setNewSpeed(0.0f);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        Player player = event.getPlayer();
        if (player != null && GameStateManager.getInstance().shouldRestrictBattlefieldMining(player)) {
            event.setCanceled(true);
        }
    }
}

