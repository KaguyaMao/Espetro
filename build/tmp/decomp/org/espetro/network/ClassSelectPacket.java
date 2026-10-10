/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FortificationConfig;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.network.ClassCountSyncPacket;
import org.espetro.network.NetworkManager;
import org.espetro.network.RadioRadialPacket;
import org.espetro.network.VehicleSupplySyncPacket;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassEquipment;
import org.espetro.team.ClassEquipmentZones;
import org.espetro.team.ClassSelectManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamPackManager;
import org.espetro.vehicle.VehicleConfig;
import org.espetro.vehicle.VehicleManager;

public class ClassSelectPacket {
    private final String teamOrFaction;
    private final String classId;
    private final String variantId;
    private final Source source;
    private final BlockPos sourcePos;
    private final UUID vehicleId;

    public ClassSelectPacket(String teamOrFaction, String classId) {
        this(teamOrFaction, classId, "", Source.DEPLOY_SCREEN, BlockPos.f_121853_, null);
    }

    public ClassSelectPacket(String teamOrFaction, String classId, String variantId) {
        this(teamOrFaction, classId, variantId, Source.DEPLOY_SCREEN, BlockPos.f_121853_, null);
    }

    public ClassSelectPacket(String teamOrFaction, String classId, String variantId, Source source, BlockPos sourcePos) {
        this(teamOrFaction, classId, variantId, source, sourcePos, null);
    }

    public ClassSelectPacket(String teamOrFaction, String classId, String variantId, Source source, BlockPos sourcePos, UUID vehicleId) {
        this.teamOrFaction = teamOrFaction;
        this.classId = classId;
        this.variantId = variantId != null ? variantId : "";
        this.source = source != null ? source : Source.DEPLOY_SCREEN;
        this.sourcePos = sourcePos != null ? sourcePos.m_7949_() : BlockPos.f_121853_;
        this.vehicleId = vehicleId;
    }

    public static ClassSelectPacket fromRadio(String teamOrFaction, String classId, String variantId, BlockPos radioPos) {
        return new ClassSelectPacket(teamOrFaction, classId, variantId, Source.RADIO, radioPos);
    }

    public static ClassSelectPacket fromVehicle(String teamOrFaction, String classId, String variantId, UUID vehicleId) {
        return new ClassSelectPacket(teamOrFaction, classId, variantId, Source.VEHICLE, BlockPos.f_121853_, vehicleId);
    }

    public static ClassSelectPacket read(FriendlyByteBuf buf) {
        Source source;
        String teamOrFaction = buf.m_130277_();
        String classId = buf.m_130277_();
        String variantId = buf.m_130277_();
        try {
            source = Source.valueOf(buf.m_130277_());
        }
        catch (IllegalArgumentException ignored) {
            source = Source.DEPLOY_SCREEN;
        }
        BlockPos sourcePos = buf.m_130135_();
        UUID vehicleId = buf.readBoolean() ? buf.m_130259_() : null;
        return new ClassSelectPacket(teamOrFaction, classId, variantId, source, sourcePos, vehicleId);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.teamOrFaction);
        buf.m_130070_(this.classId);
        buf.m_130070_(this.variantId);
        buf.m_130070_(this.source.name());
        buf.m_130064_(this.sourcePos);
        buf.writeBoolean(this.vehicleId != null);
        if (this.vehicleId != null) {
            buf.m_130077_(this.vehicleId);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            boolean awaitingDeployment;
            ClassCountManager.SelectionResult selection;
            boolean allowed;
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            if (ClassSelectManager.getInstance().isSelectingActive()) {
                boolean success = ClassSelectManager.getInstance().selectClass(player, this.classId);
                if (success) {
                    Espetro.LOGGER.info("\u73a9\u5bb6 {} \u6295\u7968\u7f16\u5236 {}", (Object)player.m_7755_().getString(), (Object)this.classId);
                }
                return;
            }
            ClassCountManager countManager = ClassCountManager.getInstance();
            GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
            if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
                ClassSelectPacket.denyOutOfRange(player);
                return;
            }
            String currentClass = countManager.getPlayerClass(player.m_20148_());
            String currentVariant = countManager.getPlayerVariant(player.m_20148_());
            if (this.source == Source.RADIO) {
                allowed = RadioRadialPacket.isFriendlyRadioNearby(player, this.sourcePos);
            } else if (this.source == Source.VEHICLE) {
                allowed = this.vehicleId != null && VehicleManager.getInstance().canPlayerChangeClassAtVehicle(player, this.vehicleId);
            } else {
                boolean bl = allowed = BastionManager.getInstance().isWaitingForBastion(player.m_20148_()) || ClassEquipmentZones.isPlayerNearOriginalSpawn(player);
            }
            if (!allowed) {
                ClassSelectPacket.denyOutOfRange(player);
                return;
            }
            int vehicleClassCost = 0;
            if (this.source == Source.VEHICLE && this.vehicleId != null) {
                vehicleClassCost = ClassSelectPacket.getVehicleClassChangeCost();
                VehicleManager vsm = VehicleManager.getInstance();
                if (!vsm.canVehicleAffordAmmo(this.vehicleId, vehicleClassCost)) {
                    player.m_5661_(Component.m_237113_("\u00a7c\u8f7d\u5177\u5f39\u836f\u4e0d\u8db3\uff0c\u65e0\u6cd5\u66f4\u6362\u804c\u4e1a\u3002"), true);
                    return;
                }
            }
            if ((selection = countManager.selectClassVariant(player, this.classId, this.variantId)) != ClassCountManager.SelectionResult.SUCCESS) {
                String message = ClassCountManager.messageFor(selection, player.m_20148_());
                if (message.isEmpty()) {
                    message = "\u00a7c\u5f53\u524d\u65e0\u6cd5\u9009\u62e9\u8be5\u804c\u4e1a\u88c5\u5907\u53d8\u4f53\u3002";
                }
                NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ClassCountSyncPacket(message, true));
                return;
            }
            boolean sameSelection = this.classId.equals(currentClass) && countManager.getPlayerVariant(player.m_20148_()).equals(currentVariant);
            String actualFactionId = countManager.getPlayerFaction(player.m_20148_());
            String selectedVariantId = countManager.getPlayerVariant(player.m_20148_());
            if (this.source == Source.VEHICLE && this.vehicleId != null) {
                VehicleManager vsm = VehicleManager.getInstance();
                if (!vsm.consumeVehicleAmmo(this.vehicleId, vehicleClassCost)) {
                    player.m_5661_(Component.m_237113_("\u00a7c\u8f7d\u5177\u5f39\u836f\u4e0d\u8db3\uff0c\u65e0\u6cd5\u66f4\u6362\u804c\u4e1a\u3002"), true);
                    return;
                }
                VehicleManager.VehicleSupplyState supply = vsm.getVehicleSupply(this.vehicleId);
                if (supply != null) {
                    String vehicleFaction = vsm.getVehicleFactionId(this.vehicleId);
                    String vehicleType = vsm.getVehicleType(this.vehicleId);
                    VehicleConfig.VehicleTypeConfig vehicleConfig = vehicleFaction == null || vehicleType == null ? null : VehicleConfig.getVehicleConfig(vehicleFaction, vehicleType);
                    NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)VehicleSupplySyncPacket.state(this.vehicleId, supply.getAmmo(), supply.getConstruction(), supply.getMaxCapacity(), vehicleConfig != null && vehicleConfig.supplyVeh, vehicleConfig != null && vehicleConfig.fightVeh, false, false, FortificationConfig.vehicleService().transferIntervalTicks));
                }
            }
            boolean bl = awaitingDeployment = BastionManager.getInstance().isWaitingForBastion(player.m_20148_()) || player.m_5833_();
            if (!(awaitingDeployment || sameSelection && !ClassEquipment.needsLoadout(player))) {
                ClassEquipment.equipPlayer(player, actualFactionId, this.classId, selectedVariantId);
            }
            TeamPackManager.getInstance().syncTeamPackItem(player);
            String team = countManager.getEffectivePlayerTeam(player.m_20148_());
            String factionId = countManager.getPlayerFaction(player.m_20148_());
            NetworkManager.broadcastClassCounts(team, factionId != null ? factionId : this.teamOrFaction);
            NetworkManager.syncSquadsToTeam(team);
        });
        ctx.get().setPacketHandled(true);
    }

    private static void denyOutOfRange(ServerPlayer player) {
        String message = ClassCountManager.messageFor(ClassCountManager.SelectionResult.OUT_OF_RANGE, player.m_20148_());
        NetworkManager.NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ClassCountSyncPacket(message, true));
        player.m_213846_(Component.m_237113_(message));
    }

    private static int getVehicleClassChangeCost() {
        return LogisticsConfig.get().defaultResupplyAmmoCost;
    }

    public static enum Source {
        DEPLOY_SCREEN,
        RADIO,
        VEHICLE;

    }
}

