/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.vehicle;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.network.MountProgressPacket;
import org.espetro.network.NetworkManager;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.espetro.vehicle.VehicleInteractionConfig;

public final class VehicleMountServer {
    private static final Map<UUID, PendingMount> PENDING = new HashMap<UUID, PendingMount>();

    private VehicleMountServer() {
    }

    public static void begin(ServerPlayer player, UUID vehicleId) {
        if (player == null || vehicleId == null) {
            return;
        }
        if (player.m_20202_() != null) {
            VehicleMountServer.cancel(player);
            return;
        }
        Entity entity = player.m_284548_().m_8791_(vehicleId);
        if (!SbwVehicleSeatResolver.isSupportedVehicle(entity)) {
            VehicleMountServer.cancel(player);
            return;
        }
        if ((double)player.m_20270_(entity) > VehicleInteractionConfig.mountMaxDistance()) {
            VehicleMountServer.cancel(player);
            return;
        }
        int delay = VehicleInteractionConfig.mountDelayTicks();
        if (delay <= 0) {
            VehicleMountServer.tryMount(player, entity);
            return;
        }
        PendingMount existing = PENDING.get(player.m_20148_());
        if (existing != null && vehicleId.equals(existing.vehicleId)) {
            long elapsed = player.m_284548_().m_46467_() - existing.startGameTime;
            float progress = Math.min(1.0f, (float)elapsed / (float)Math.max(1, existing.delayTicks));
            VehicleMountServer.sendProgress(player, new MountProgressPacket(true, progress, existing.delayTicks));
            return;
        }
        PENDING.put(player.m_20148_(), new PendingMount(vehicleId, player.m_284548_().m_46467_(), delay));
        VehicleMountServer.sendProgress(player, new MountProgressPacket(true, 0.0f, delay));
    }

    public static void cancel(ServerPlayer player) {
        if (player == null) {
            return;
        }
        if (PENDING.remove(player.m_20148_()) != null) {
            VehicleMountServer.sendProgress(player, new MountProgressPacket(false, 0.0f, 0));
        }
    }

    public static void complete(ServerPlayer player, UUID vehicleId) {
        if (player == null || vehicleId == null) {
            return;
        }
        PendingMount pending = PENDING.get(player.m_20148_());
        if (pending == null || !vehicleId.equals(pending.vehicleId)) {
            VehicleMountServer.cancel(player);
            return;
        }
        long elapsed = player.m_284548_().m_46467_() - pending.startGameTime;
        if (elapsed + 3L < (long)pending.delayTicks) {
            return;
        }
        Entity entity = player.m_284548_().m_8791_(vehicleId);
        PENDING.remove(player.m_20148_());
        VehicleMountServer.sendProgress(player, new MountProgressPacket(false, 1.0f, pending.delayTicks));
        VehicleMountServer.tryMount(player, entity);
    }

    public static void tick(ServerPlayer player) {
        PendingMount pending = PENDING.get(player.m_20148_());
        if (pending == null) {
            return;
        }
        Entity entity = player.m_284548_().m_8791_(pending.vehicleId);
        if (!SbwVehicleSeatResolver.isSupportedVehicle(entity) || player.m_20202_() != null || (double)player.m_20270_(entity) > VehicleInteractionConfig.mountMaxDistance() + 0.75) {
            VehicleMountServer.cancel(player);
            return;
        }
        long elapsed = player.m_284548_().m_46467_() - pending.startGameTime;
        float progress = Math.min(1.0f, (float)elapsed / (float)Math.max(1, pending.delayTicks));
        if ((elapsed & 1L) == 0L) {
            VehicleMountServer.sendProgress(player, new MountProgressPacket(true, progress, pending.delayTicks));
        }
        if (elapsed >= (long)pending.delayTicks) {
            VehicleMountServer.complete(player, pending.vehicleId);
        }
    }

    public static void tickAll(Iterable<ServerPlayer> players) {
        for (ServerPlayer player : players) {
            VehicleMountServer.tick(player);
        }
    }

    public static void clear() {
        PENDING.clear();
    }

    private static void tryMount(ServerPlayer player, Entity entity) {
        if (player == null || !SbwVehicleSeatResolver.isSupportedVehicle(entity)) {
            return;
        }
        if (player.m_20202_() != null) {
            return;
        }
        if ((double)player.m_20270_(entity) > VehicleInteractionConfig.mountMaxDistance() + 0.75) {
            return;
        }
        player.m_7998_(entity, true);
    }

    private static void sendProgress(ServerPlayer player, MountProgressPacket packet) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    private record PendingMount(UUID vehicleId, long startGameTime, int delayTicks) {
    }
}

