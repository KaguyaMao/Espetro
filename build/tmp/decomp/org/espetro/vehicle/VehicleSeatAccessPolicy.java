/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.EntityMountEvent
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.vehicle;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.vehicle.SbwVehicleSeatResolver;
import org.espetro.vehicle.VehicleConfig;
import org.espetro.vehicle.VehicleManager;

@Mod.EventBusSubscriber(modid="espetro")
public final class VehicleSeatAccessPolicy {
    private static final long DENIAL_MESSAGE_INTERVAL_MS = 1000L;
    private static final Map<UUID, Long> LAST_DENIAL_MESSAGE = new ConcurrentHashMap<UUID, Long>();

    private VehicleSeatAccessPolicy() {
    }

    static int legacyVehicleCrewSeatCount(SbwVehicleSeatResolver.Kind kind) {
        if (kind == null) {
            return 0;
        }
        return switch (kind) {
            default -> throw new IncompatibleClassChangeError();
            case SbwVehicleSeatResolver.Kind.TANK -> 3;
            case SbwVehicleSeatResolver.Kind.IFV -> 2;
            case SbwVehicleSeatResolver.Kind.HELICOPTER -> 1;
            case SbwVehicleSeatResolver.Kind.OTHER -> 0;
        };
    }

    static boolean requiresVehicleCrew(int requiredSeatCount, int seatIndex) {
        return seatIndex >= 0 && seatIndex < Math.max(0, requiredSeatCount);
    }

    static int firstAvailableUnrestrictedSeat(int requiredSeatCount, List<?> seatOccupants) {
        if (seatOccupants == null) {
            return -1;
        }
        for (int seatIndex = 0; seatIndex < seatOccupants.size(); ++seatIndex) {
            if (seatOccupants.get(seatIndex) != null || VehicleSeatAccessPolicy.requiresVehicleCrew(requiredSeatCount, seatIndex)) continue;
            return seatIndex;
        }
        return -1;
    }

    public static boolean resolvesVehicleCrew(Boolean configured, String icon) {
        if (configured != null) {
            return configured;
        }
        return icon != null && "crewman".equalsIgnoreCase(icon.trim());
    }

    public static boolean isVehicleCrew(ServerPlayer player) {
        ClassCountManager counts = ClassCountManager.getInstance();
        if (player == null || counts == null) {
            return false;
        }
        String classId = counts.getPlayerClass(player.m_20148_());
        if (classId == null || classId.isBlank()) {
            return false;
        }
        FactionDataLoader.ClassKitData kit = FactionDataProvider.getOrCreateLoader().getClassKit(classId);
        return kit != null && kit.isVehicleCrew();
    }

    public static boolean mayUseSeat(ServerPlayer player, Entity vehicle, int seatIndex) {
        if (!VehicleSeatAccessPolicy.isRestrictionActive(player)) {
            return true;
        }
        int requiredSeatCount = VehicleSeatAccessPolicy.getRequiredVehicleCrewSeatCount(vehicle);
        return !VehicleSeatAccessPolicy.requiresVehicleCrew(requiredSeatCount, seatIndex) || VehicleSeatAccessPolicy.isVehicleCrew(player);
    }

    public static boolean checkSeatChange(ServerPlayer player, Entity vehicle, int seatIndex) {
        boolean allowed = VehicleSeatAccessPolicy.mayUseSeat(player, vehicle, seatIndex);
        if (!allowed) {
            VehicleSeatAccessPolicy.notifyDenied(player);
        }
        return allowed;
    }

    public static void revalidateCurrentSeat(ServerPlayer player) {
        SbwVehicleSeatResolver.SeatState state = SbwVehicleSeatResolver.resolveCurrent(player);
        if (state == null || VehicleSeatAccessPolicy.mayUseSeat(player, state.vehicle(), state.seatIndex())) {
            return;
        }
        player.m_8127_();
        VehicleSeatAccessPolicy.notifyDenied(player);
    }

    public static void clear(UUID playerId) {
        if (playerId != null) {
            LAST_DENIAL_MESSAGE.remove(playerId);
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onMount(EntityMountEvent event) {
        Entity entity;
        if (event.getLevel().m_5776_() || !event.isMounting() || !((entity = event.getEntityMounting()) instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        Entity vehicle = event.getEntityBeingMounted();
        if (!SbwVehicleSeatResolver.isSupportedVehicle(vehicle)) {
            return;
        }
        int requiredSeatCount = VehicleSeatAccessPolicy.getRequiredVehicleCrewSeatCount(vehicle);
        if (!VehicleSeatAccessPolicy.isRestrictionActive(player) || VehicleSeatAccessPolicy.isVehicleCrew(player) || requiredSeatCount <= 0) {
            return;
        }
        List<?> seats = SbwVehicleSeatResolver.getOrderedSeatOccupants(vehicle);
        int targetSeat = VehicleSeatAccessPolicy.firstAvailableUnrestrictedSeat(requiredSeatCount, seats);
        if (targetSeat < 0 || !SbwVehicleSeatResolver.overrideNextMountSeat(vehicle, player, targetSeat)) {
            event.setCanceled(true);
            VehicleSeatAccessPolicy.notifyNoVacancy(player);
        }
    }

    static int getRequiredVehicleCrewSeatCount(Entity vehicle) {
        VehicleConfig.VehicleTypeConfig config;
        if (vehicle == null) {
            return 0;
        }
        VehicleManager manager = VehicleManager.getInstance();
        String factionId = manager.getVehicleFactionId(vehicle.m_20148_());
        String vehicleType = manager.getVehicleType(vehicle.m_20148_());
        if (factionId != null && vehicleType != null && (config = VehicleConfig.getVehicleConfig(factionId, vehicleType)) != null && config.vehicleCrewSeats != null) {
            return Math.max(0, config.vehicleCrewSeats);
        }
        return VehicleSeatAccessPolicy.legacyVehicleCrewSeatCount(SbwVehicleSeatResolver.getKind(vehicle));
    }

    private static boolean isRestrictionActive(ServerPlayer player) {
        if (player == null || !BattlefieldContext.isActiveBattlefield(player.m_284548_())) {
            return false;
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        return phase == GamePhase.DEPLOYING || phase == GamePhase.BATTLE;
    }

    private static void notifyDenied(ServerPlayer player) {
        VehicleSeatAccessPolicy.notify(player, "\u00a7c\u8be5\u5ea7\u4f4d\u4ec5\u9650\u8f7d\u5177\u7ec4\u5458\u3002");
    }

    private static void notifyNoVacancy(ServerPlayer player) {
        VehicleSeatAccessPolicy.notify(player, "\u00a7c\u8f7d\u5177\u4e0a\u6ca1\u6709\u7a7a\u4f59\u4f4d\u7f6e\u4e86\u3002");
    }

    private static void notify(ServerPlayer player, String message) {
        long now = System.currentTimeMillis();
        Long previous = LAST_DENIAL_MESSAGE.put(player.m_20148_(), now);
        if (previous != null && now - previous < 1000L) {
            return;
        }
        player.m_5661_(Component.m_237113_(message), true);
    }
}

