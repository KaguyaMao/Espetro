/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.EntityJoinLevelEvent
 *  net.minecraftforge.event.entity.EntityLeaveLevelEvent
 *  net.minecraftforge.event.entity.EntityMountEvent
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.event.entity.player.PlayerInteractEvent$EntityInteract
 *  net.minecraftforge.eventbus.api.EventPriority
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package org.espetro.vehicle;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityLeaveLevelEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;
import org.espetro.bastion.FortificationManager;
import org.espetro.team.SquadManager;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.VehicleSquadOwnership;

@Mod.EventBusSubscriber(modid="espetro")
public class VehicleEventHandler {
    static final Map<Integer, PendingClaim> PENDING_CLAIMS = new ConcurrentHashMap<Integer, PendingClaim>();
    private static final long CLAIM_TIMEOUT_MS = 60000L;

    @SubscribeEvent
    public static void onVehicleDeath(LivingDeathEvent event) {
        LivingEntity entity = event.getEntity();
        if (entity.m_19880_().contains("espetro_vehicle")) {
            VehicleManager.getInstance().onVehicleDeath(entity.m_20148_());
            Espetro.LOGGER.debug("\u8f7d\u5177 {} \u5df2\u6b7b\u4ea1\uff0c\u79fb\u9664\u8ffd\u8e2a", (Object)entity.m_20148_());
        }
    }

    @SubscribeEvent
    public static void onVehicleLeaveLevel(EntityLeaveLevelEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (VehicleManager.isMappedSupplyStation(entity) && (entity.m_146911_() == Entity.RemovalReason.KILLED || entity.m_146911_() == Entity.RemovalReason.DISCARDED)) {
            VehicleManager.getInstance().unregisterMappedSupplyStation(entity.m_20148_());
            FortificationManager.getInstance().removeEntity(entity.m_20148_());
        }
        if (entity.m_19880_().contains("espetro_vehicle")) {
            VehicleManager.getInstance().updateVehicleLocation(entity);
            if (entity.m_146911_() == Entity.RemovalReason.KILLED) {
                VehicleManager.getInstance().onVehicleDeath(entity.m_20148_());
                Espetro.LOGGER.debug("\u8f7d\u5177 {} \u5df2\u88ab\u6740\u6bc1\uff0c\u79fb\u9664\u8ffd\u8e2a\u5e76\u5904\u7406\u5175\u529b\u6263\u9664", (Object)entity.m_20148_());
            } else if (entity.m_146911_() == Entity.RemovalReason.DISCARDED) {
                if (VehicleEventHandler.isDestroyedSbwVehicle(entity)) {
                    VehicleManager.getInstance().onVehicleDeath(entity.m_20148_());
                    Espetro.LOGGER.debug("\u8f7d\u5177 {} \u6b8b\u9ab8\u5df2\u79fb\u9664\uff0c\u786e\u4fdd\u81ea\u52a8\u5237\u65b0\u5df2\u767b\u8bb0", (Object)entity.m_20148_());
                } else {
                    VehicleManager.getInstance().onVehicleRemoved(entity.m_20148_());
                    Espetro.LOGGER.debug("\u8f7d\u5177 {} \u5df2\u88ab\u4e3b\u52a8\u79fb\u9664\uff0c\u6e05\u9664\u8ffd\u8e2a", (Object)entity.m_20148_());
                }
            } else {
                Espetro.LOGGER.debug("\u8f7d\u5177 {} \u6682\u65f6\u79bb\u5f00\u5df2\u52a0\u8f7d\u4e16\u754c\uff0c\u4fdd\u7559\u505c\u670d\u6e05\u7406\u8ffd\u8e2a", (Object)entity.m_20148_());
            }
        }
    }

    @SubscribeEvent
    public static void onVehicleJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        Entity entity = event.getEntity();
        if (entity.m_19880_().contains("espetro_vehicle")) {
            VehicleManager.getInstance().updateVehicleLocation(entity);
        }
    }

    @SubscribeEvent
    public static void onSupplyStationJoinLevel(EntityJoinLevelEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel)) {
            return;
        }
        Entity entity = event.getEntity();
        if (!VehicleManager.isAmmoSupplyStationEntity(entity)) {
            return;
        }
        if (VehicleManager.isMappedSupplyStation(entity)) {
            VehicleManager.getInstance().registerMappedSupplyStation(entity);
        }
    }

    private static boolean isSbwVehicle(Entity entity) {
        for (Class<?> clazz = entity.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            if (!"com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity".equals(clazz.getName())) continue;
            return true;
        }
        return false;
    }

    private static boolean isDestroyedSbwVehicle(Entity entity) {
        if (!VehicleEventHandler.isSbwVehicle(entity)) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(entity.getClass().getMethod("isWreck", new Class[0]).invoke(entity, new Object[0]));
        }
        catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private static String getVehicleDisplayName(Entity vehicle) {
        Component custom = vehicle.m_7770_();
        return custom != null ? custom.getString() : vehicle.m_6095_().m_20676_().getString();
    }

    @SubscribeEvent(priority=EventPriority.HIGHEST)
    public static void onVehicleEntityInteract(PlayerInteractEvent.EntityInteract event) {
        boolean vehicleHasPassengers;
        if (event.getLevel().m_5776_()) {
            return;
        }
        Player player = event.getEntity();
        if (!(player instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player2 = (ServerPlayer)player;
        Entity target = event.getTarget();
        if (!VehicleEventHandler.isSbwVehicle(target)) {
            return;
        }
        String team = Espetro.getPlayerTeam(player2);
        if (team == null) {
            return;
        }
        team = team.toUpperCase();
        SquadManager sm = SquadManager.getInstance();
        int playerSquad = sm.getPlayerSquadId(player2.m_20148_());
        boolean isLeader = sm.isSquadLeader(player2.m_20148_());
        int vehicleSquad = VehicleSquadOwnership.getSquadId(target);
        boolean vehicleOwned = vehicleSquad != -1;
        boolean bl = vehicleHasPassengers = !target.m_20197_().isEmpty();
        if (vehicleHasPassengers) {
            return;
        }
        if (!vehicleOwned) {
            if (isLeader) {
                return;
            }
            if (playerSquad != -1) {
                VehicleEventHandler.submitClaim(player2, target, playerSquad, team, sm);
            }
            event.setCanceled(true);
            return;
        }
        if (playerSquad == vehicleSquad) {
            return;
        }
        if (isLeader) {
            return;
        }
        if (playerSquad != -1) {
            VehicleEventHandler.submitClaim(player2, target, playerSquad, team, sm);
        }
        event.setCanceled(true);
    }

    private static void submitClaim(ServerPlayer member, Entity vehicle, int squadId, String team, SquadManager sm) {
        ServerPlayer leader;
        PENDING_CLAIMS.remove(squadId);
        PENDING_CLAIMS.put(squadId, new PendingClaim(member.m_20148_(), vehicle.m_20148_(), System.currentTimeMillis() + 60000L));
        String vehicleName = VehicleEventHandler.getVehicleDisplayName(vehicle);
        member.m_213846_(Component.m_237113_("\u00a7a\u5df2\u5411\u961f\u957f\u7533\u8bf7\u8ba4\u9886\u8be5\u8f7d\u5177"));
        UUID leaderUuid = sm.getSquadLeaderUuid(team, squadId);
        if (leaderUuid != null && (leader = member.m_284548_().m_7654_().m_6846_().m_11259_(leaderUuid)) != null) {
            leader.m_213846_(Component.m_237113_("\u00a7a\u961f\u5458\u7533\u8bf7\u4f7f\u7528" + vehicleName + "\uff0c\u8f93\u5165/veh pass\u4ee5\u901a\u8fc7\uff0c\u8f93\u5165/veh passno\u4ee5\u5426\u51b3"));
        }
    }

    @SubscribeEvent(priority=EventPriority.LOWEST)
    public static void onVehicleMount(EntityMountEvent event) {
        if (event.getLevel().m_5776_()) {
            return;
        }
        if (!event.isMounting()) {
            return;
        }
        Entity entity = event.getEntity();
        if (!(entity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)entity;
        Entity vehicle = event.getEntityBeingMounted();
        if (!VehicleEventHandler.isSbwVehicle(vehicle)) {
            return;
        }
        SquadManager sm = SquadManager.getInstance();
        if (!sm.isSquadLeader(player.m_20148_())) {
            return;
        }
        int playerSquad = sm.getPlayerSquadId(player.m_20148_());
        if (playerSquad == -1) {
            return;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return;
        }
        team = team.toUpperCase();
        int vehicleSquad = VehicleSquadOwnership.getSquadId(vehicle);
        String vehicleSquadTeam = VehicleSquadOwnership.getSquadTeam(vehicle);
        if (vehicleSquad == -1) {
            VehicleSquadOwnership.setOwner(vehicle, playerSquad, team);
            return;
        }
        if (vehicleSquad != playerSquad) {
            ServerPlayer oldLeader;
            UUID oldLeaderUuid;
            if (vehicleSquadTeam != null && (oldLeaderUuid = sm.getSquadLeaderUuid(vehicleSquadTeam, vehicleSquad)) != null && (oldLeader = player.m_284548_().m_7654_().m_6846_().m_11259_(oldLeaderUuid)) != null) {
                String vehicleName = VehicleEventHandler.getVehicleDisplayName(vehicle);
                String newSquadName = sm.getSquadName(team, playerSquad);
                if (newSquadName == null) {
                    newSquadName = "\u672a\u77e5\u5c0f\u961f";
                }
                oldLeader.m_213846_(Component.m_237113_("\u00a7c\u60a8\u7a7a\u95f2\u7684\u8f7d\u5177" + vehicleName + "\u5df2\u88ab" + newSquadName + "\u8ba4\u9886"));
            }
            VehicleSquadOwnership.setOwner(vehicle, playerSquad, team);
        }
    }

    record PendingClaim(UUID memberUuid, UUID vehicleUuid, long expiryMs) {
    }
}

