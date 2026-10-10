/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.EntityMountEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.ping;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.vehicle.SbwVehicleSeatResolver;

@Mod.EventBusSubscriber(modid="espetro")
public final class VehicleSeatPingCache {
    private static final Map<UUID, SbwVehicleSeatResolver.SeatState> CACHE = new ConcurrentHashMap<UUID, SbwVehicleSeatResolver.SeatState>();

    private VehicleSeatPingCache() {
    }

    public static boolean canPingFromVehicle(UUID playerId) {
        SbwVehicleSeatResolver.SeatState state = CACHE.get(playerId);
        return state != null && VehicleSeatPingCache.allowsPing(state.kind(), state.seatIndex());
    }

    public static boolean canPingFromVehicle(ServerPlayer player) {
        SbwVehicleSeatResolver.SeatState state = SbwVehicleSeatResolver.resolveCurrent(player);
        if (state == null) {
            VehicleSeatPingCache.clear(player != null ? player.m_20148_() : null);
            return false;
        }
        CACHE.put(player.m_20148_(), state);
        return VehicleSeatPingCache.allowsPing(state.kind(), state.seatIndex());
    }

    public static void clear(UUID playerId) {
        if (playerId != null) {
            CACHE.remove(playerId);
        }
    }

    @SubscribeEvent
    public static void onMount(EntityMountEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntityMounting();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        if (event.isDismounting()) {
            CACHE.remove(player.m_20148_());
            return;
        }
        player.f_8924_.execute(() -> VehicleSeatPingCache.canPingFromVehicle(player));
    }

    private static boolean allowsPing(SbwVehicleSeatResolver.Kind kind, int seat) {
        return switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case SbwVehicleSeatResolver.Kind.TANK -> {
                if (seat >= 0 && seat <= 2) {
                    yield true;
                }
                yield false;
            }
            case SbwVehicleSeatResolver.Kind.IFV, SbwVehicleSeatResolver.Kind.HELICOPTER -> {
                if (seat >= 0 && seat <= 1) {
                    yield true;
                }
                yield false;
            }
            case SbwVehicleSeatResolver.Kind.OTHER -> false;
        };
    }
}

