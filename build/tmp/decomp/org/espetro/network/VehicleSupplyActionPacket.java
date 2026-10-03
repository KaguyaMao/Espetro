/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.bastion.FortificationConfig;
import org.espetro.bastion.FortificationManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.logistics.resupply.ResupplySessionManager;
import org.espetro.logistics.resupply.ResupplySourceRef;
import org.espetro.network.NetworkManager;
import org.espetro.network.VehicleSupplySyncPacket;
import org.espetro.team.SpawnPointConfig;
import org.espetro.vehicle.VehicleConfig;
import org.espetro.vehicle.VehicleManager;

public final class VehicleSupplyActionPacket {
    private static final Map<UUID, Long> LAST_TRANSFER_TICK = new HashMap<UUID, Long>();
    private final UUID vehicleId;
    private final Action action;

    public VehicleSupplyActionPacket(UUID vehicleId, Action action) {
        this.vehicleId = vehicleId;
        this.action = action;
    }

    public static VehicleSupplyActionPacket read(FriendlyByteBuf buf) {
        return new VehicleSupplyActionPacket(buf.m_130259_(), buf.m_130066_(Action.class));
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.vehicleId);
        buf.m_130068_(this.action);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> this.handleServer(context.getSender()));
        context.setPacketHandled(true);
    }

    private void handleServer(@Nullable ServerPlayer player) {
        if (player == null) {
            return;
        }
        Interaction interaction = VehicleSupplyActionPacket.resolveInteraction(player, this.vehicleId);
        if (interaction == null) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8bf7\u6b63\u5bf9\u4e94\u683c\u5185\u7684\u5df1\u65b9\u8f7d\u5177\u3002"), true);
            return;
        }
        if (this.action == Action.RESUPPLY_INFANTRY) {
            ResupplySessionManager.open(player, ResupplySourceRef.vehicle(this.vehicleId));
            VehicleSupplyActionPacket.sendSync(player, interaction);
            return;
        }
        if (this.action == Action.CHANGE_CLASS) {
            if (interaction.config().canChangeClass()) {
                NetworkManager.sendVehicleClassSelect(player, interaction.factionId());
            }
            VehicleSupplyActionPacket.sendSync(player, interaction);
            return;
        }
        if (!VehicleSupplyActionPacket.allowTransferNow(player)) {
            return;
        }
        if (!(this.action != Action.LOAD_AMMO && this.action != Action.UNLOAD_AMMO || interaction.canTransferAmmo())) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8f7d\u5177\u4e0d\u5728\u6709\u6548\u8865\u7ed9\u8303\u56f4\u5185\u3002"), true);
            return;
        }
        if (!(this.action != Action.LOAD_CONSTRUCTION && this.action != Action.UNLOAD_CONSTRUCTION || interaction.canTransferConstruction())) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8be5\u8f7d\u5177\u65e0\u6cd5\u5728\u6b64\u88c5\u5378\u5efa\u6750\u3002"), true);
            return;
        }
        int amount = FortificationConfig.vehicleService().transferAmount;
        switch (this.action) {
            case LOAD_AMMO: {
                VehicleSupplyActionPacket.loadAmmo(player, interaction, amount);
                break;
            }
            case UNLOAD_AMMO: {
                VehicleSupplyActionPacket.unloadAmmo(player, interaction, amount);
                break;
            }
            case LOAD_CONSTRUCTION: {
                VehicleSupplyActionPacket.loadConstruction(player, interaction, amount);
                break;
            }
            case UNLOAD_CONSTRUCTION: {
                VehicleSupplyActionPacket.unloadConstruction(player, interaction, amount);
                break;
            }
        }
        VehicleSupplyActionPacket.sendSync(player, interaction);
    }

    private static boolean allowTransferNow(ServerPlayer player) {
        int interval;
        long previous;
        long now = player.m_284548_().m_46467_();
        if (now - (previous = LAST_TRANSFER_TICK.getOrDefault(player.m_20148_(), -4611686018427387904L).longValue()) < (long)(interval = FortificationConfig.vehicleService().transferIntervalTicks)) {
            return false;
        }
        LAST_TRANSFER_TICK.put(player.m_20148_(), now);
        return true;
    }

    public static void clearPlayerRateLimit(UUID playerId) {
        LAST_TRANSFER_TICK.remove(playerId);
    }

    public static void clearRateLimits() {
        LAST_TRANSFER_TICK.clear();
    }

    private static void loadAmmo(ServerPlayer player, Interaction interaction, int chunk) {
        int available;
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int wanted = Math.min(chunk, supply.getFreeSpace());
        if (wanted <= 0) {
            player.m_5661_(Component.m_237113_("\u00a7e\u8f7d\u5177\u5bb9\u91cf\u5df2\u6ee1\u3002"), true);
            return;
        }
        int n = available = interaction.mainBase() ? wanted : Math.min(wanted, interaction.radio().getAmmunitionSupplies());
        if (available <= 0) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8865\u7ed9\u70b9\u5f39\u836f\u4e0d\u8db3\u3002"), true);
            return;
        }
        if (!interaction.mainBase() && !interaction.radio().consumeAmmunitionSupplies(available)) {
            return;
        }
        int added = supply.addAmmo(available);
        if (added < available && !interaction.mainBase()) {
            interaction.radio().addAmmunitionSupplies(available - added, LogisticsConfig.get().maxAmmunition);
        }
        VehicleSupplyActionPacket.notifyRadio(interaction.radio());
    }

    private static void unloadAmmo(ServerPlayer player, Interaction interaction, int chunk) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int removable = Math.min(chunk, supply.getAmmo());
        if (!interaction.mainBase()) {
            removable = Math.min(removable, Math.max(0, LogisticsConfig.get().maxAmmunition - interaction.radio().getAmmunitionSupplies()));
        }
        if (removable <= 0) {
            player.m_5661_(Component.m_237113_("\u00a7e\u6ca1\u6709\u53ef\u5378\u8f7d\u7684\u5f39\u836f\u6216 Radio \u5df2\u6ee1\u3002"), true);
            return;
        }
        int removed = supply.removeAmmo(removable);
        if (!interaction.mainBase()) {
            interaction.radio().addAmmunitionSupplies(removed, LogisticsConfig.get().maxAmmunition);
        }
        VehicleSupplyActionPacket.notifyRadio(interaction.radio());
    }

    private static void loadConstruction(ServerPlayer player, Interaction interaction, int chunk) {
        int available;
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int wanted = Math.min(chunk, supply.getFreeSpace());
        if (wanted <= 0) {
            player.m_5661_(Component.m_237113_("\u00a7e\u8f7d\u5177\u5bb9\u91cf\u5df2\u6ee1\u3002"), true);
            return;
        }
        int n = available = interaction.mainBase() ? wanted : Math.min(wanted, interaction.radio().getConstructionSupplies());
        if (available <= 0) {
            player.m_5661_(Component.m_237113_("\u00a7c\u8865\u7ed9\u70b9\u5efa\u6750\u4e0d\u8db3\u3002"), true);
            return;
        }
        if (!interaction.mainBase() && !interaction.radio().consumeConstructionSupplies(available)) {
            return;
        }
        int added = supply.addConstruction(available);
        if (added < available && !interaction.mainBase()) {
            interaction.radio().addConstructionSupplies(available - added, LogisticsConfig.get().maxConstruction);
        }
        VehicleSupplyActionPacket.notifyRadio(interaction.radio());
    }

    private static void unloadConstruction(ServerPlayer player, Interaction interaction, int chunk) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int removable = Math.min(chunk, supply.getConstruction());
        if (!interaction.mainBase()) {
            removable = Math.min(removable, Math.max(0, LogisticsConfig.get().maxConstruction - interaction.radio().getConstructionSupplies()));
        }
        if (removable <= 0) {
            player.m_5661_(Component.m_237113_("\u00a7e\u6ca1\u6709\u53ef\u5378\u8f7d\u7684\u5efa\u6750\u6216 Radio \u5df2\u6ee1\u3002"), true);
            return;
        }
        int removed = supply.removeConstruction(removable);
        if (!interaction.mainBase()) {
            interaction.radio().addConstructionSupplies(removed, LogisticsConfig.get().maxConstruction);
        }
        VehicleSupplyActionPacket.notifyRadio(interaction.radio());
    }

    @Nullable
    public static VehicleSupplySyncPacket createSyncResponse(ServerPlayer player, UUID vehicleId) {
        Interaction interaction = VehicleSupplyActionPacket.resolveInteraction(player, vehicleId);
        return interaction == null ? null : VehicleSupplyActionPacket.toPacket(interaction);
    }

    private static void sendSync(ServerPlayer player, Interaction interaction) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)VehicleSupplyActionPacket.toPacket(interaction));
    }

    private static VehicleSupplySyncPacket toPacket(Interaction interaction) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        return VehicleSupplySyncPacket.state(interaction.vehicleId(), supply.getAmmo(), supply.getConstruction(), supply.getMaxCapacity(), interaction.config().supplyVeh, interaction.config().fightVeh, interaction.canTransferAmmo(), interaction.canTransferConstruction(), FortificationConfig.vehicleService().transferIntervalTicks);
    }

    @Nullable
    public static Interaction resolveInteraction(ServerPlayer player, UUID vehicleId) {
        VehicleManager vehicles = VehicleManager.getInstance();
        String factionId = vehicles.getVehicleFactionId(vehicleId);
        String vehicleType = vehicles.getVehicleType(vehicleId);
        if (factionId == null || vehicleType == null || !vehicles.canPlayerInteractWithVehicle(player, vehicleId)) {
            return null;
        }
        Entity entity = vehicles.getLoadedVehicle(player, vehicleId);
        VehicleConfig.VehicleTypeConfig config = VehicleConfig.getVehicleConfig(factionId, vehicleType);
        String team = Espetro.getPlayerTeam(player);
        if (entity == null || config == null || team == null) {
            return null;
        }
        VehicleManager.VehicleSupplyState supply = vehicles.getOrCreateVehicleSupply(vehicleId, factionId, vehicleType);
        if (supply == null) {
            return null;
        }
        ServerLevel level = player.m_284548_();
        BlockPos vehiclePos = entity.m_20183_();
        boolean mainBase = VehicleSupplyActionPacket.isAtMainBase(level, vehiclePos, team);
        BastionData radio = null;
        if (!mainBase) {
            if (config.supplyVeh) {
                List<BastionData> covering = BastionManager.getInstance().findCoveringRadios(level, vehiclePos, team);
                if (!covering.isEmpty()) {
                    radio = covering.get(0);
                }
            } else if (config.fightVeh) {
                radio = FortificationManager.getInstance().findVehicleServiceRadio(level, vehiclePos, team);
            }
        }
        boolean supplyLike = config.supplyVeh || config.fightVeh;
        boolean canTransferAmmo = supplyLike && (mainBase || radio != null);
        boolean canTransferConstruction = config.supplyVeh && canTransferAmmo;
        return new Interaction(vehicleId, factionId, config, supply, mainBase, radio, canTransferAmmo, canTransferConstruction);
    }

    static boolean isAtMainBase(ServerLevel level, BlockPos vehiclePos, String team) {
        double radius;
        double dz;
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        if (spawn == null) {
            return false;
        }
        double dx = (double)vehiclePos.m_123341_() + 0.5 - spawn.x;
        return dx * dx + (dz = (double)vehiclePos.m_123343_() + 0.5 - spawn.z) * dz <= (radius = FortificationConfig.vehicleService().mainBaseRadius) * radius;
    }

    private static void notifyRadio(@Nullable BastionData radio) {
        if (radio != null) {
            FobSupplyTracker.notifySupplyChanged(radio);
        }
    }

    public static enum Action {
        LOAD_AMMO,
        UNLOAD_AMMO,
        LOAD_CONSTRUCTION,
        UNLOAD_CONSTRUCTION,
        RESUPPLY_INFANTRY,
        CHANGE_CLASS;

    }

    public record Interaction(UUID vehicleId, String factionId, VehicleConfig.VehicleTypeConfig config, VehicleManager.VehicleSupplyState supply, boolean mainBase, @Nullable BastionData radio, boolean canTransferAmmo, boolean canTransferConstruction) {
    }
}

