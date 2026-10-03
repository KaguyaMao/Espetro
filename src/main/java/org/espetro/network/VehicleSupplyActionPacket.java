package org.espetro.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionEventHandler;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FobSupplyTracker;
import org.espetro.bastion.FortificationConfig;
import org.espetro.bastion.FortificationManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.team.SpawnPointConfig;
import org.espetro.vehicle.VehicleConfig;
import org.espetro.vehicle.VehicleManager;
import org.espetro.vehicle.VehicleNativeWhitelist;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Supplier;

/** Client-to-server vehicle supply action, with all permissions recomputed server-side. */
public final class VehicleSupplyActionPacket {

    public enum Action {
        LOAD_AMMO,
        UNLOAD_AMMO,
        LOAD_CONSTRUCTION,
        UNLOAD_CONSTRUCTION,
        /** 任何载具轮盘的“补给步兵”，消耗载具弹药。 */
        RESUPPLY_INFANTRY,
        CHANGE_CLASS
    }

    private static final Map<UUID, Long> LAST_TRANSFER_TICK = new HashMap<>();

    private final UUID vehicleId;
    private final Action action;

    public VehicleSupplyActionPacket(UUID vehicleId, Action action) {
        this.vehicleId = vehicleId;
        this.action = action;
    }

    public static VehicleSupplyActionPacket read(FriendlyByteBuf buf) {
        return new VehicleSupplyActionPacket(buf.readUUID(), buf.readEnum(Action.class));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(vehicleId);
        buf.writeEnum(action);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> handleServer(context.getSender()));
        context.setPacketHandled(true);
    }

    private void handleServer(@Nullable ServerPlayer player) {
        if (player == null) return;
        Interaction interaction = resolveInteraction(player, vehicleId);
        if (interaction == null) {
            // 白名单载具的轮盘入口：后续做专属轮盘时改这里。
            // 未在 activeVehicleData 中的白名单载具只回"空壳"快照让轮盘能打开，
            // 不执行任何补给/建造/换装写操作。
            VehicleSupplySyncPacket shell = nativeWhitelistSync(player, vehicleId);
            if (shell != null) {
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), shell);
                return;
            }
            player.displayClientMessage(Component.literal("§c请正对五格内的己方载具。"), true);
            return;
        }

        if (action == Action.RESUPPLY_INFANTRY) {
            org.espetro.logistics.resupply.ResupplySessionManager.open(player,
                org.espetro.logistics.resupply.ResupplySourceRef.vehicle(vehicleId));
            sendSync(player, interaction);
            return;
        }
        if (action == Action.CHANGE_CLASS) {
            if (interaction.config().canChangeClass()) {
                NetworkManager.sendVehicleClassSelect(player, interaction.factionId());
            }
            sendSync(player, interaction);
            return;
        }

        if (!allowTransferNow(player)) {
            return;
        }
        if ((action == Action.LOAD_AMMO || action == Action.UNLOAD_AMMO)
            && !interaction.canTransferAmmo()) {
            player.displayClientMessage(Component.literal("§c载具不在有效补给范围内。"), true);
            return;
        }
        if ((action == Action.LOAD_CONSTRUCTION || action == Action.UNLOAD_CONSTRUCTION)
            && !interaction.canTransferConstruction()) {
            player.displayClientMessage(Component.literal("§c该载具无法在此装卸建材。"), true);
            return;
        }

        int amount = FortificationConfig.vehicleService().transferAmount;
        switch (action) {
            case LOAD_AMMO -> loadAmmo(player, interaction, amount);
            case UNLOAD_AMMO -> unloadAmmo(player, interaction, amount);
            case LOAD_CONSTRUCTION -> loadConstruction(player, interaction, amount);
            case UNLOAD_CONSTRUCTION -> unloadConstruction(player, interaction, amount);
            default -> { }
        }
        sendSync(player, interaction);
    }

    private static boolean allowTransferNow(ServerPlayer player) {
        long now = player.serverLevel().getGameTime();
        long previous = LAST_TRANSFER_TICK.getOrDefault(player.getUUID(), Long.MIN_VALUE / 2);
        int interval = FortificationConfig.vehicleService().transferIntervalTicks;
        if (now - previous < interval) return false;
        LAST_TRANSFER_TICK.put(player.getUUID(), now);
        return true;
    }

    public static void clearPlayerRateLimit(UUID playerId) {
        LAST_TRANSFER_TICK.remove(playerId);
    }

    public static void clearRateLimits() {
        LAST_TRANSFER_TICK.clear();
    }

    private static void loadAmmo(ServerPlayer player, Interaction interaction, int chunk) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int wanted = Math.min(chunk, supply.getFreeSpace());
        if (wanted <= 0) {
            player.displayClientMessage(Component.literal("§e载具容量已满。"), true);
            return;
        }
        // 主基地/骑乘在载具上：不经过 FOB Radio 库存，直接按需求装载
        int available = interaction.unrestricted() ? wanted
            : Math.min(wanted, interaction.radio().getAmmunitionSupplies());
        if (available <= 0) {
            player.displayClientMessage(Component.literal("§c补给点弹药不足。"), true);
            return;
        }
        if (!interaction.unrestricted()
            && !interaction.radio().consumeAmmunitionSupplies(available)) return;
        int added = supply.addAmmo(available);
        if (added < available && !interaction.unrestricted()) {
            interaction.radio().addAmmunitionSupplies(
                available - added, LogisticsConfig.get().maxAmmunition);
        }
        notifyRadio(interaction.radio());
    }

    private static void unloadAmmo(ServerPlayer player, Interaction interaction, int chunk) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int removable = Math.min(chunk, supply.getAmmo());
        if (!interaction.unrestricted()) {
            removable = Math.min(removable,
                Math.max(0, LogisticsConfig.get().maxAmmunition
                    - interaction.radio().getAmmunitionSupplies()));
        }
        if (removable <= 0) {
            player.displayClientMessage(Component.literal("§e没有可卸载的弹药或 Radio 已满。"), true);
            return;
        }
        int removed = supply.removeAmmo(removable);
        if (!interaction.unrestricted()) {
            interaction.radio().addAmmunitionSupplies(removed, LogisticsConfig.get().maxAmmunition);
        }
        notifyRadio(interaction.radio());
    }

    private static void loadConstruction(ServerPlayer player, Interaction interaction, int chunk) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int wanted = Math.min(chunk, supply.getFreeSpace());
        if (wanted <= 0) {
            player.displayClientMessage(Component.literal("§e载具容量已满。"), true);
            return;
        }
        // 主基地/骑乘在载具上：不经过 FOB Radio 库存，直接按需求装载
        int available = interaction.unrestricted() ? wanted
            : Math.min(wanted, interaction.radio().getConstructionSupplies());
        if (available <= 0) {
            player.displayClientMessage(Component.literal("§c补给点建材不足。"), true);
            return;
        }
        if (!interaction.unrestricted()
            && !interaction.radio().consumeConstructionSupplies(available)) return;
        int added = supply.addConstruction(available);
        if (added < available && !interaction.unrestricted()) {
            interaction.radio().addConstructionSupplies(
                available - added, LogisticsConfig.get().maxConstruction);
        }
        notifyRadio(interaction.radio());
    }

    private static void unloadConstruction(ServerPlayer player, Interaction interaction, int chunk) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        int removable = Math.min(chunk, supply.getConstruction());
        if (!interaction.unrestricted()) {
            removable = Math.min(removable,
                Math.max(0, LogisticsConfig.get().maxConstruction
                    - interaction.radio().getConstructionSupplies()));
        }
        if (removable <= 0) {
            player.displayClientMessage(Component.literal("§e没有可卸载的建材或 Radio 已满。"), true);
            return;
        }
        int removed = supply.removeConstruction(removable);
        if (!interaction.unrestricted()) {
            interaction.radio().addConstructionSupplies(removed, LogisticsConfig.get().maxConstruction);
        }
        notifyRadio(interaction.radio());
    }

    @Nullable
    public static VehicleSupplySyncPacket createSyncResponse(ServerPlayer player, UUID vehicleId) {
        Interaction interaction = resolveInteraction(player, vehicleId);
        if (interaction != null) {
            return toPacket(interaction);
        }
        // 白名单载具的轮盘入口：后续做专属轮盘时改这里。
        return nativeWhitelistSync(player, vehicleId);
    }

    /**
     * 非 Espetro 部署的白名单载具的"最小交互对象"轮盘快照。
     *
     * <p>未部署载具不在 {@code VehicleManager.activeVehicleData} 中，正常路径返回 null
     * 会让客户端 {@code snapshotReady} 恒为 false、轮盘永不打开。这里按最小合法值构造
     * 空壳 {@link Interaction}（阵营 id 空串、补给/战斗属性全 false、弹药/建材/容量全 0），
     * 使 {@code client/gui/VehicleWheelController} 能照常 {@code open(ROOT)}。</p>
     *
     * <p><b>不</b>写 {@code activeVehicleData}、<b>不</b>调用
     * {@code getOrCreateVehicleSupply}，因此不会产生任何补给/建造/换装写操作；
     * 该快照的所有装卸/换装标志均为 false，唯一可用项是"补给步兵"
     * （见 {@link VehicleSupplySyncPacket#hasAnyAction()}），而
     * {@link #handleServer} 对白名单空壳只回快照、不派发任何 action。</p>
     */
    @Nullable
    private static VehicleSupplySyncPacket nativeWhitelistSync(ServerPlayer player, UUID vehicleId) {
        Interaction shell = nativeWhitelistInteraction(player, vehicleId);
        return shell == null ? null : toPacket(shell);
    }

    @Nullable
    private static Interaction nativeWhitelistInteraction(ServerPlayer player, UUID vehicleId) {
        if (player == null || vehicleId == null) return null;
        Entity entity = player.serverLevel().getEntity(vehicleId);
        if (entity == null || entity.isRemoved() || !VehicleNativeWhitelist.isNative(entity)) {
            return null;
        }
        // 空壳配置：max/respawn 无意义，supplyVeh/fightVeh 保持 false。
        VehicleConfig.VehicleTypeConfig config = new VehicleConfig.VehicleTypeConfig(0, 0);
        VehicleManager.VehicleSupplyState supply =
            new VehicleManager.VehicleSupplyState(0, false);
        boolean ridingThisVehicle = player.getVehicle() != null
            && (vehicleId.equals(player.getVehicle().getUUID())
                || vehicleId.equals(player.getVehicle().getRootVehicle().getUUID()));
        return new Interaction(vehicleId, "", config, supply, false, null,
            ridingThisVehicle, false, false);
    }

    private static void sendSync(ServerPlayer player, Interaction interaction) {
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), toPacket(interaction));
    }

    private static VehicleSupplySyncPacket toPacket(Interaction interaction) {
        VehicleManager.VehicleSupplyState supply = interaction.supply();
        return VehicleSupplySyncPacket.state(
            interaction.vehicleId(), supply.getAmmo(), supply.getConstruction(),
            supply.getMaxCapacity(), interaction.config().supplyVeh,
            interaction.config().fightVeh, interaction.canTransferAmmo(),
            interaction.canTransferConstruction(),
            FortificationConfig.vehicleService().transferIntervalTicks);
    }

    @Nullable
    /** Revalidates a vehicle source for every menu action; never trust the client snapshot. */
    public static Interaction resolveInteraction(ServerPlayer player, UUID vehicleId) {
        VehicleManager vehicles = VehicleManager.getInstance();
        String factionId = vehicles.getVehicleFactionId(vehicleId);
        String vehicleType = vehicles.getVehicleType(vehicleId);
        if (factionId == null || vehicleType == null
            || !vehicles.canPlayerInteractWithVehicle(player, vehicleId)) return null;

        Entity entity = vehicles.getLoadedVehicle(player, vehicleId);
        VehicleConfig.VehicleTypeConfig config =
            VehicleConfig.getVehicleConfig(factionId, vehicleType);
        String team = Espetro.getPlayerTeam(player);
        if (entity == null || config == null || team == null) return null;

        VehicleManager.VehicleSupplyState supply =
            vehicles.getOrCreateVehicleSupply(vehicleId, factionId, vehicleType);
        if (supply == null) return null;

        ServerLevel level = player.serverLevel();
        BlockPos vehiclePos = entity.blockPosition();
        boolean mainBase = isAtMainBase(level, vehiclePos, team);
        // 骑乘在载具上本身不授予装卸权限：装卸必须在主基地或 FOB 覆盖范围内。
        boolean ridingThisVehicle = player.getVehicle() != null
            && (vehicleId.equals(player.getVehicle().getUUID())
                || vehicleId.equals(player.getVehicle().getRootVehicle().getUUID()));
        BastionData radio = null;
        if (!mainBase) {
            if (config.supplyVeh) {
                var covering = BastionManager.getInstance()
                    .findCoveringRadios(level, vehiclePos, team);
                if (!covering.isEmpty()) radio = covering.get(0);
            } else if (config.fightVeh) {
                radio = FortificationManager.getInstance()
                    .findVehicleServiceRadio(level, vehiclePos, team);
            }
        }
        boolean supplyLike = config.supplyVeh || config.fightVeh;
        boolean canTransferAmmo = supplyLike && (mainBase || radio != null);
        boolean canTransferConstruction = config.supplyVeh && canTransferAmmo;
        return new Interaction(vehicleId, factionId, config, supply, mainBase, radio,
            ridingThisVehicle, canTransferAmmo, canTransferConstruction);
    }

    static boolean isAtMainBase(ServerLevel level, BlockPos vehiclePos, String team) {
        SpawnPointConfig.SpawnPoint spawn = SpawnPointConfig.getSpawnPoint(team);
        if (spawn == null) return false;
        double dx = vehiclePos.getX() + 0.5 - spawn.x;
        double dz = vehiclePos.getZ() + 0.5 - spawn.z;
        double radius = FortificationConfig.vehicleService().mainBaseRadius;
        return dx * dx + dz * dz <= radius * radius;
    }

    private static void notifyRadio(@Nullable BastionData radio) {
        if (radio != null) FobSupplyTracker.notifySupplyChanged(radio);
    }

    public record Interaction(
        UUID vehicleId,
        String factionId,
        VehicleConfig.VehicleTypeConfig config,
        VehicleManager.VehicleSupplyState supply,
        boolean mainBase,
        @Nullable BastionData radio,
        boolean ridingThisVehicle,
        boolean canTransferAmmo,
        boolean canTransferConstruction
    ) {
        /** 主基地装卸不经过 FOB Radio 库存；骑乘载具本身不授予装卸权限。 */
        boolean unrestricted() {
            return mainBase;
        }
    }
}
