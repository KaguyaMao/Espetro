/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.event.TickEvent$ServerTickEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.event.server.ServerStoppingEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.LogicalSide
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package org.espetro.vehicle;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.server.ServerStoppingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.server.ServerLifecycleHooks;
import org.espetro.vehicle.DismountServer;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.espetro.vehicle.SeatSwitchServer;
import org.espetro.vehicle.VehicleInteractionConfig;
import org.espetro.vehicle.VehicleMountServer;

@Mod.EventBusSubscriber(modid="espetro")
public final class VehicleMountEvents {
    private VehicleMountEvents() {
    }

    @SubscribeEvent(priority=EventPriority.HIGH)
    public static void onEntityInteract(PlayerInteractEvent.EntityInteract event) {
        if (event.getSide() != LogicalSide.SERVER) {
            return;
        }
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        Entity target = event.getTarget();
        if (!SbwVehicleSeatResolver.isSupportedVehicle(target)) {
            return;
        }
        if (player2.m_20202_() != null) {
            return;
        }
        if (VehicleInteractionConfig.mountDelayTicks() <= 0) {
            return;
        }
        event.setCanceled(true);
    }

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.phase != TickEvent.Phase.END) {
            return;
        }
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) {
            return;
        }
        VehicleMountServer.tickAll(server.m_6846_().m_11314_());
    }

    @SubscribeEvent
    public static void onStop(ServerStoppingEvent event) {
        VehicleMountServer.clear();
        SeatSwitchServer.clearAll();
        DismountServer.clearAll();
    }
}

