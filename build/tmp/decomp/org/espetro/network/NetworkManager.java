/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.loading.FMLPaths
 *  net.minecraftforge.network.NetworkDirection
 *  net.minecraftforge.network.NetworkRegistry
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.network.simple.SimpleChannel
 */
package org.espetro.network;

import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.espetro.Espetro;
import org.espetro.audio.FactionAudioCoordinator;
import org.espetro.bastion.BastionData;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.governance.CommanderGovernanceManager;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.SquadTypesSnapshot;
import org.espetro.network.AudioCuePacket;
import org.espetro.network.BattleTimerPacket;
import org.espetro.network.BuildFortificationPacket;
import org.espetro.network.CastVotePacket;
import org.espetro.network.ClassCountSyncPacket;
import org.espetro.network.ClassSelectPacket;
import org.espetro.network.ClassSelectScreenPacket;
import org.espetro.network.ClassSelectTimerPacket;
import org.espetro.network.CloseResupplySessionPacket;
import org.espetro.network.CommanderSkillPacket;
import org.espetro.network.CommanderSkillSyncPacket;
import org.espetro.network.CommanderVotePacket;
import org.espetro.network.DeployPointSelectPacket;
import org.espetro.network.DeployPointSyncPacket;
import org.espetro.network.DismountRequestPacket;
import org.espetro.network.EquipZoneSyncPacket;
import org.espetro.network.FactionRevealPacket;
import org.espetro.network.FobSupplySyncPacket;
import org.espetro.network.FortificationCatalogPacket;
import org.espetro.network.FortificationPlacementPacket;
import org.espetro.network.FortificationPreviewPacket;
import org.espetro.network.FortificationProgressPacket;
import org.espetro.network.FortificationWorkPacket;
import org.espetro.network.GamePhaseSyncPacket;
import org.espetro.network.GameStateResponsePacket;
import org.espetro.network.GovernanceActionPacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.MapVoteCastPacket;
import org.espetro.network.MapVoteStatePacket;
import org.espetro.network.MatchStatsActionPacket;
import org.espetro.network.MatchStatsSyncPacket;
import org.espetro.network.MountProgressPacket;
import org.espetro.network.MountRequestPacket;
import org.espetro.network.OpenClassSelectionPacket;
import org.espetro.network.OpenFactionScreenPacket;
import org.espetro.network.OpenHubScreenPacket;
import org.espetro.network.OpenMapVoteScreenPacket;
import org.espetro.network.OutpostSupplySyncPacket;
import org.espetro.network.PartyActionPacket;
import org.espetro.network.PartyListPacket;
import org.espetro.network.RadialActionPacket;
import org.espetro.network.RadioRadialPacket;
import org.espetro.network.RequestClassSelectionPacket;
import org.espetro.network.RequestGameStatePacket;
import org.espetro.network.RequestResupplyCatalogPacket;
import org.espetro.network.RequestVehicleInfoPacket;
import org.espetro.network.ResupplyCatalogPacket;
import org.espetro.network.ResupplyEntryDeltaPacket;
import org.espetro.network.RoundEndPacket;
import org.espetro.network.SeatSwitchReadyPacket;
import org.espetro.network.SelectResupplyEntryPacket;
import org.espetro.network.SquadActionPacket;
import org.espetro.network.SquadCreateWithCategoryPacket;
import org.espetro.network.SquadSyncPacket;
import org.espetro.network.TaczGunPackSyncChunkPacket;
import org.espetro.network.TeamSelectPacket;
import org.espetro.network.TeamSelectStatePacket;
import org.espetro.network.TroopCountSyncPacket;
import org.espetro.network.TutorialActionPacket;
import org.espetro.network.TutorialSyncPacket;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.network.VehicleDeployScreenPacket;
import org.espetro.network.VehicleSupplyActionPacket;
import org.espetro.network.VehicleSupplySyncPacket;
import org.espetro.network.VoteDataPacket;
import org.espetro.network.WaitingStatusPacket;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassEquipmentZones;
import org.espetro.team.ClassLoadoutPreviewResolver;
import org.espetro.team.ClassSelectManager;
import org.espetro.team.CommanderSkillManager;
import org.espetro.team.CommanderSkillType;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.Fireteam;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.MapVoteManager;
import org.espetro.team.OutpostManager;
import org.espetro.team.PartyManager;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamDisplayNames;
import org.espetro.team.TeamPackManager;
import org.espetro.team.VoteManager;
import org.espetro.vehicle.VehicleConfig;
import org.espetro.vehicle.VehicleManager;

public class NetworkManager {
    public static final String PROTOCOL_VERSION = "1.33";
    public static final SimpleChannel NET = NetworkRegistry.newSimpleChannel((ResourceLocation)ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)"main"), () -> "1.33", "1.33"::equals, "1.33"::equals);
    private static int packetId = 0;
    private static final int FULL_DEPLOY_PACKETS_PER_TICK = 8;
    private static final int MAX_SELECTION_IMAGE_BYTES = 180000;
    private static final int MAX_SELECTION_IMAGE_TOTAL_BYTES = 700000;
    private static final Map<UUID, QueuedDeployScreen> QUEUED_FULL_DEPLOY_SCREENS = new LinkedHashMap<UUID, QueuedDeployScreen>();

    public static int nextId() {
        return packetId++;
    }

    public static void queueUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining) {
        NetworkManager.queueUnifiedDeployScreen(player, deployTimeRemaining, false);
    }

    public static void queueUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining, boolean playEntryAudio) {
        if (player != null) {
            QUEUED_FULL_DEPLOY_SCREENS.put(player.m_20148_(), new QueuedDeployScreen(deployTimeRemaining, playEntryAudio));
        }
    }

    public static void drainQueuedFullScreens() {
        MinecraftServer server = Espetro.getServer();
        if (server == null || QUEUED_FULL_DEPLOY_SCREENS.isEmpty()) {
            return;
        }
        int sent = 0;
        Iterator<Map.Entry<UUID, QueuedDeployScreen>> iterator = QUEUED_FULL_DEPLOY_SCREENS.entrySet().iterator();
        while (iterator.hasNext() && sent < 8) {
            Map.Entry<UUID, QueuedDeployScreen> entry = iterator.next();
            iterator.remove();
            ServerPlayer player = server.m_6846_().m_11259_(entry.getKey());
            if (player == null) continue;
            QueuedDeployScreen queued = entry.getValue();
            NetworkManager.sendUnifiedDeployScreen(player, queued.deployTimeRemaining());
            if (queued.playEntryAudio()) {
                FactionAudioCoordinator.sendEntry(player);
            }
            ++sent;
        }
    }

    public static void clearQueuedFullScreens() {
        QUEUED_FULL_DEPLOY_SCREENS.clear();
    }

    public static void registerNetwork() {
        NET.registerMessage(NetworkManager.nextId(), TeamSelectPacket.class, TeamSelectPacket::write, TeamSelectPacket::read, TeamSelectPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), ClassSelectPacket.class, ClassSelectPacket::write, ClassSelectPacket::read, ClassSelectPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), ClassCountSyncPacket.class, ClassCountSyncPacket::write, ClassCountSyncPacket::read, ClassCountSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), WaitingStatusPacket.class, WaitingStatusPacket::write, WaitingStatusPacket::read, WaitingStatusPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), CommanderVotePacket.class, CommanderVotePacket::write, CommanderVotePacket::read, CommanderVotePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), VoteDataPacket.class, VoteDataPacket::write, VoteDataPacket::read, VoteDataPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), CastVotePacket.class, CastVotePacket::write, CastVotePacket::read, CastVotePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), GamePhaseSyncPacket.class, GamePhaseSyncPacket::write, GamePhaseSyncPacket::read, GamePhaseSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), TroopCountSyncPacket.class, TroopCountSyncPacket::write, TroopCountSyncPacket::read, TroopCountSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), OpenFactionScreenPacket.class, OpenFactionScreenPacket::write, OpenFactionScreenPacket::read, OpenFactionScreenPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), ClassSelectScreenPacket.class, ClassSelectScreenPacket::write, ClassSelectScreenPacket::read, ClassSelectScreenPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FactionRevealPacket.class, FactionRevealPacket::write, FactionRevealPacket::read, FactionRevealPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), OpenClassSelectionPacket.class, OpenClassSelectionPacket::write, OpenClassSelectionPacket::read, OpenClassSelectionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), RequestClassSelectionPacket.class, RequestClassSelectionPacket::write, RequestClassSelectionPacket::read, RequestClassSelectionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), RequestGameStatePacket.class, RequestGameStatePacket::write, RequestGameStatePacket::read, RequestGameStatePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), GameStateResponsePacket.class, GameStateResponsePacket::write, GameStateResponsePacket::read, GameStateResponsePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), VehicleDeployScreenPacket.class, VehicleDeployScreenPacket::write, VehicleDeployScreenPacket::read, VehicleDeployScreenPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), RequestVehicleInfoPacket.class, RequestVehicleInfoPacket::write, RequestVehicleInfoPacket::read, RequestVehicleInfoPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), DeployPointSelectPacket.class, DeployPointSelectPacket::write, DeployPointSelectPacket::read, DeployPointSelectPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), UnifiedDeployScreenPacket.class, UnifiedDeployScreenPacket::write, UnifiedDeployScreenPacket::read, UnifiedDeployScreenPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), SquadActionPacket.class, SquadActionPacket::write, SquadActionPacket::read, SquadActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), SquadSyncPacket.class, SquadSyncPacket::write, SquadSyncPacket::read, SquadSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), CommanderSkillPacket.class, CommanderSkillPacket::write, CommanderSkillPacket::read, CommanderSkillPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), CommanderSkillSyncPacket.class, CommanderSkillSyncPacket::write, CommanderSkillSyncPacket::read, CommanderSkillSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), RadialActionPacket.class, RadialActionPacket::write, RadialActionPacket::read, RadialActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), MapVoteStatePacket.class, MapVoteStatePacket::write, MapVoteStatePacket::read, MapVoteStatePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), MapVoteCastPacket.class, MapVoteCastPacket::write, MapVoteCastPacket::read, MapVoteCastPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), OpenMapVoteScreenPacket.class, OpenMapVoteScreenPacket::write, OpenMapVoteScreenPacket::read, OpenMapVoteScreenPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), TeamSelectStatePacket.class, TeamSelectStatePacket::write, TeamSelectStatePacket::read, TeamSelectStatePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), MatchStatsSyncPacket.class, MatchStatsSyncPacket::write, MatchStatsSyncPacket::read, MatchStatsSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), MatchStatsActionPacket.class, MatchStatsActionPacket::write, MatchStatsActionPacket::read, MatchStatsActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), GovernanceStatePacket.class, GovernanceStatePacket::write, GovernanceStatePacket::read, GovernanceStatePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), GovernanceActionPacket.class, GovernanceActionPacket::write, GovernanceActionPacket::read, GovernanceActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), SquadCreateWithCategoryPacket.class, SquadCreateWithCategoryPacket::write, SquadCreateWithCategoryPacket::read, SquadCreateWithCategoryPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), OpenHubScreenPacket.class, OpenHubScreenPacket::write, OpenHubScreenPacket::read, OpenHubScreenPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), RoundEndPacket.class, RoundEndPacket::write, RoundEndPacket::read, RoundEndPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), TutorialSyncPacket.class, TutorialSyncPacket::write, TutorialSyncPacket::read, TutorialSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), TutorialActionPacket.class, TutorialActionPacket::write, TutorialActionPacket::read, TutorialActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), ClassSelectTimerPacket.class, ClassSelectTimerPacket::write, ClassSelectTimerPacket::read, ClassSelectTimerPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), EquipZoneSyncPacket.class, EquipZoneSyncPacket::write, EquipZoneSyncPacket::read, EquipZoneSyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), RadioRadialPacket.class, RadioRadialPacket::write, RadioRadialPacket::read, RadioRadialPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), BattleTimerPacket.class, BattleTimerPacket::write, BattleTimerPacket::read, BattleTimerPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), PartyActionPacket.class, PartyActionPacket::write, PartyActionPacket::read, PartyActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), PartyListPacket.class, PartyListPacket::write, PartyListPacket::read, PartyListPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), VehicleSupplyActionPacket.class, VehicleSupplyActionPacket::write, VehicleSupplyActionPacket::read, VehicleSupplyActionPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), VehicleSupplySyncPacket.class, VehicleSupplySyncPacket::write, VehicleSupplySyncPacket::read, VehicleSupplySyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FobSupplySyncPacket.class, FobSupplySyncPacket::write, FobSupplySyncPacket::read, FobSupplySyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), OutpostSupplySyncPacket.class, OutpostSupplySyncPacket::write, OutpostSupplySyncPacket::read, OutpostSupplySyncPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), BuildFortificationPacket.class, BuildFortificationPacket::write, BuildFortificationPacket::read, BuildFortificationPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FortificationCatalogPacket.class, FortificationCatalogPacket::write, FortificationCatalogPacket::read, FortificationCatalogPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FortificationPreviewPacket.class, FortificationPreviewPacket::write, FortificationPreviewPacket::read, FortificationPreviewPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FortificationPlacementPacket.class, FortificationPlacementPacket::write, FortificationPlacementPacket::read, FortificationPlacementPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FortificationWorkPacket.class, FortificationWorkPacket::write, FortificationWorkPacket::read, FortificationWorkPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), FortificationProgressPacket.class, FortificationProgressPacket::write, FortificationProgressPacket::read, FortificationProgressPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), AudioCuePacket.class, AudioCuePacket::write, AudioCuePacket::read, AudioCuePacket::handle);
        NET.registerMessage(NetworkManager.nextId(), TaczGunPackSyncChunkPacket.class, TaczGunPackSyncChunkPacket::write, TaczGunPackSyncChunkPacket::read, TaczGunPackSyncChunkPacket::handle);
        NET.registerMessage(NetworkManager.nextId(), DeployPointSyncPacket.class, DeployPointSyncPacket::write, DeployPointSyncPacket::read, DeployPointSyncPacket::handle);
        NET.messageBuilder(RequestResupplyCatalogPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(RequestResupplyCatalogPacket::write).decoder(RequestResupplyCatalogPacket::read).consumerMainThread(RequestResupplyCatalogPacket::handle).add();
        NET.messageBuilder(ResupplyCatalogPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_CLIENT).encoder(ResupplyCatalogPacket::write).decoder(ResupplyCatalogPacket::read).consumerMainThread(ResupplyCatalogPacket::handle).add();
        NET.messageBuilder(SelectResupplyEntryPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(SelectResupplyEntryPacket::write).decoder(SelectResupplyEntryPacket::read).consumerMainThread(SelectResupplyEntryPacket::handle).add();
        NET.messageBuilder(ResupplyEntryDeltaPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_CLIENT).encoder(ResupplyEntryDeltaPacket::write).decoder(ResupplyEntryDeltaPacket::read).consumerMainThread(ResupplyEntryDeltaPacket::handle).add();
        NET.messageBuilder(CloseResupplySessionPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(CloseResupplySessionPacket::write).decoder(CloseResupplySessionPacket::read).consumerMainThread(CloseResupplySessionPacket::handle).add();
        NET.messageBuilder(MountRequestPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(MountRequestPacket::write).decoder(MountRequestPacket::read).consumerMainThread(MountRequestPacket::handle).add();
        NET.messageBuilder(MountProgressPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_CLIENT).encoder(MountProgressPacket::write).decoder(MountProgressPacket::read).consumerMainThread(MountProgressPacket::handle).add();
        NET.messageBuilder(DismountRequestPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(DismountRequestPacket::write).decoder(DismountRequestPacket::read).consumerMainThread(DismountRequestPacket::handle).add();
        NET.messageBuilder(SeatSwitchReadyPacket.class, NetworkManager.nextId(), NetworkDirection.PLAY_TO_SERVER).encoder(SeatSwitchReadyPacket::write).decoder(SeatSwitchReadyPacket::read).consumerMainThread(SeatSwitchReadyPacket::handle).add();
    }

    public static void sendBuildFortification(String fortId) {
        NET.sendToServer((Object)new BuildFortificationPacket(fortId));
    }

    public static void sendFortificationPlacement(FortificationPlacementPacket.Action action, UUID token, BlockPos anchor, Direction facing) {
        NET.sendToServer((Object)new FortificationPlacementPacket(action, token, anchor, facing));
    }

    public static void sendFortificationWork(BlockPos target, boolean build) {
        NET.sendToServer((Object)FortificationWorkPacket.block(target, build));
    }

    public static void sendFortificationEntityWork(UUID target, boolean build) {
        NET.sendToServer((Object)FortificationWorkPacket.entity(target, build));
    }

    public static void requestFortificationCatalog() {
        NET.sendToServer((Object)FortificationCatalogPacket.request());
    }

    public static void sendRadioOpen(BlockPos pos) {
        NET.sendToServer((Object)RadioRadialPacket.openRequest(pos));
    }

    public static void sendRadioResupply(BlockPos pos) {
        NET.sendToServer((Object)RadioRadialPacket.resupply(pos));
    }

    public static void sendEquipZones(ServerPlayer player) {
        if (player == null) {
            return;
        }
        List<EquipZoneSyncPacket.Zone> zones = ClassEquipmentZones.collectForPlayer(player);
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new EquipZoneSyncPacket(zones));
    }

    public static void sendVehicleClassSelect(ServerPlayer player, String factionId) {
        if (factionId == null) {
            return;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        MinecraftServer server = player.m_20194_();
        if (server != null) {
            loader.ensureLoaded(server.m_177941_());
        }
        FactionDataLoader.ClassKitData[] kits = loader.getClassesForFaction(factionId);
        ClassCountManager counts = ClassCountManager.getInstance();
        String team = counts.getEffectivePlayerTeam(player.m_20148_());
        int squadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
        boolean inSquad = squadId != -1;
        int squadSize = inSquad ? SquadManager.getInstance().getSquadMemberUuids(team, squadId).size() : 0;
        int cooldown = counts.getClassSwitchCooldownRemaining(player.m_20148_());
        ArrayList<RadioRadialPacket.ClassEntry> list = new ArrayList<RadioRadialPacket.ClassEntry>();
        if (kits != null) {
            for (FactionDataLoader.ClassKitData kit : kits) {
                boolean cooldownBlocked;
                if (kit == null) continue;
                int squadCount = counts.getSquadClassCountForViewer(player.m_20148_(), team, kit.id);
                int maxCount = kit.teamCount ? Math.max(1, kit.maxPlayers) : (kit.maxPerSquad > 0 ? kit.maxPerSquad : Math.max(1, kit.maxPlayers));
                int teamCount = counts.getCount(team, kit.id);
                Object denial = "";
                boolean bl = cooldownBlocked = cooldown > 0;
                if (cooldownBlocked) {
                    denial = "\u804c\u4e1a\u5207\u6362\u51b7\u5374\u4e2d\uff0c\u8fd8\u9700\u7b49\u5f85 " + cooldown + " \u79d2\u3002";
                } else if (!inSquad) {
                    denial = "\u8bf7\u5148\u52a0\u5165\u73ed\u7ec4\u5c0f\u961f\u540e\u518d\u9009\u62e9\u804c\u4e1a\u3002";
                } else if (kit.teammatesNeed > 0 && squadSize < kit.teammatesNeed) {
                    denial = "\u5c0f\u961f\u8fbe\u5230 " + kit.teammatesNeed + " \u4eba\u540e\u624d\u80fd\u9009\u62e9\u8be5\u804c\u4e1a\u3002";
                } else if (kit.teamCount && squadCount >= kit.maxPlayers) {
                    denial = "\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff08" + squadCount + "/" + kit.maxPlayers + "\uff09\u3002";
                } else if (!kit.teamCount && teamCount >= kit.maxPlayers) {
                    denial = "\u8be5\u804c\u4e1a\u5168\u961f\u4eba\u6570\u5df2\u6ee1\uff08" + teamCount + "/" + kit.maxPlayers + "\uff09\u3002";
                } else if (!kit.teamCount && kit.maxPerSquad > 0 && squadCount >= kit.maxPerSquad) {
                    denial = "\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff08" + squadCount + "/" + kit.maxPerSquad + "\uff09\u3002";
                }
                boolean enabled = ((String)denial).isEmpty();
                ArrayList<RadioRadialPacket.VariantEntry> variants = new ArrayList<RadioRadialPacket.VariantEntry>();
                String defaultVariantId = "";
                if (kit.variants != null) {
                    FactionDataLoader.ClassVariantData defaultVariant = kit.variants.get("default");
                    if (defaultVariant != null) {
                        defaultVariantId = defaultVariant.id;
                    } else if (!kit.variants.isEmpty()) {
                        defaultVariantId = kit.variants.values().iterator().next().id;
                    }
                    for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                        int variantCount = kit.teamCount ? counts.countVariantInSquad(team, squadId, kit.id, variant.id) : counts.getVariantCount(team, kit.id, variant.id);
                        boolean variantEnabled = enabled && (!kit.strictCount || variantCount < variant.maxPlayers);
                        Object variantDenial = denial;
                        if (enabled && !variantEnabled) {
                            variantDenial = "\u8be5\u88c5\u5907\u53d8\u4f53\u4eba\u6570\u5df2\u6ee1\uff08" + variantCount + "/" + variant.maxPlayers + "\uff09\u3002";
                        }
                        variants.add(new RadioRadialPacket.VariantEntry(variant.id, variant.name, variantCount, variant.maxPlayers, kit.strictCount, variantEnabled, (String)variantDenial));
                    }
                }
                String icon = kit.icon != null ? kit.icon : "";
                String iconImage = kit.iconImage != null ? kit.iconImage : "";
                list.add(new RadioRadialPacket.ClassEntry(kit.id, kit.name, icon, iconImage, defaultVariantId, squadCount, maxCount, true, enabled, cooldownBlocked, (String)denial, variants));
            }
        }
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)RadioRadialPacket.classList(list));
    }

    private static void sendRadioClassList(ServerPlayer player, BlockPos radioPos) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)RadioRadialPacket.openRequest(radioPos != null ? radioPos : BlockPos.f_121853_));
    }

    public static void broadcastEquipZonesForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(player))) continue;
            NetworkManager.sendEquipZones(player);
        }
    }

    public static void sendClassSelect(String factionId, String classId) {
        NET.sendToServer((Object)new ClassSelectPacket(factionId, classId));
    }

    public static void sendClassSelect(String factionId, String classId, String variantId) {
        NET.sendToServer((Object)new ClassSelectPacket(factionId, classId, variantId));
    }

    public static void sendRadioClassSelect(String factionId, String classId, String variantId, BlockPos radioPos) {
        NET.sendToServer((Object)ClassSelectPacket.fromRadio(factionId, classId, variantId, radioPos));
    }

    public static void sendFactionSelect(String factionId) {
        NET.sendToServer((Object)new TeamSelectPacket(factionId));
    }

    public static void requestClassSelection(String factionId) {
        NET.sendToServer((Object)new RequestClassSelectionPacket(factionId));
    }

    public static void requestClassCounts(String factionId) {
        NET.sendToServer((Object)new ClassCountSyncPacket(factionId));
    }

    public static void refreshUnifiedDeployScreensForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        int remaining = -1;
        if (GameStateManager.getInstance().getCurrentPhase() == GamePhase.DEPLOYING) {
            remaining = GameStateManager.getInstance().getDeployTimeRemainingSeconds();
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(player))) continue;
            NetworkManager.syncUnifiedDeployScreen(player, remaining);
        }
    }

    public static void broadcastClassCounts(String team, String factionId) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null || factionId == null || factionId.isBlank()) {
            return;
        }
        ClassCountManager countManager = ClassCountManager.getInstance();
        Map<String, Integer> counts = countManager.getCountsForFaction(team, factionId);
        HashMap<Integer, ClassCountSyncPacket> packetsBySquad = new HashMap<Integer, ClassCountSyncPacket>();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(countManager.getEffectivePlayerTeam(player.m_20148_()))) continue;
            int squadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
            ClassCountSyncPacket packet = packetsBySquad.computeIfAbsent(squadId, ignored -> new ClassCountSyncPacket(counts, countManager.getSquadCountsForViewer(player.m_20148_(), team, factionId), countManager.getVariantCountsForViewer(player.m_20148_(), team, factionId), factionId));
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void requestGameState() {
        NET.sendToServer((Object)new RequestGameStatePacket());
    }

    public static void sendRadialAction(RadialActionPacket.Action action) {
        NET.sendToServer((Object)new RadialActionPacket(action));
    }

    public static void sendTutorialAction(byte action, String stepId) {
        try {
            NET.sendToServer((Object)new TutorialActionPacket(action, stepId == null ? "" : stepId));
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u53d1\u9001\u6559\u7a0b\u64cd\u4f5c\u5305\u5931\u8d25", (Throwable)e);
        }
    }

    public static void sendTutorialReopen() {
        try {
            NET.sendToServer((Object)TutorialActionPacket.reopen());
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u53d1\u9001\u6559\u7a0b\u91cd\u5f00\u5305\u5931\u8d25", (Throwable)e);
        }
    }

    public static void sendTutorialNext(String stepId) {
        try {
            NET.sendToServer((Object)TutorialActionPacket.next(stepId));
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u53d1\u9001\u6559\u7a0b\u4e0b\u4e00\u6b65\u5305\u5931\u8d25", (Throwable)e);
        }
    }

    public static void createSquad(String squadName) {
        NET.sendToServer((Object)SquadActionPacket.create(squadName));
    }

    public static void joinSquad(int squadId) {
        NET.sendToServer((Object)SquadActionPacket.join(squadId));
    }

    public static void leaveSquad() {
        NET.sendToServer((Object)SquadActionPacket.leave());
    }

    public static void deleteSquad(int squadId) {
        NET.sendToServer((Object)SquadActionPacket.delete(squadId));
    }

    public static void transferSquadLeader(UUID targetUuid) {
        if (targetUuid != null) {
            NET.sendToServer((Object)SquadActionPacket.transferSquadLeader(targetUuid));
        }
    }

    public static void transferFireteamLeader(UUID targetUuid) {
        if (targetUuid != null) {
            NET.sendToServer((Object)SquadActionPacket.transferFireteamLeader(targetUuid));
        }
    }

    public static void appointFireteamLeader(UUID targetUuid, Fireteam fireteam) {
        if (targetUuid != null && fireteam != null && fireteam != Fireteam.A) {
            NET.sendToServer((Object)SquadActionPacket.appointFireteamLeader(targetUuid, fireteam));
        }
    }

    public static void assignFireteam(UUID targetUuid, Fireteam fireteam) {
        if (targetUuid != null && fireteam != null) {
            NET.sendToServer((Object)SquadActionPacket.assignFireteam(targetUuid, fireteam));
        }
    }

    public static void lockSquad() {
        NET.sendToServer((Object)SquadActionPacket.lock());
    }

    public static void unlockSquad() {
        NET.sendToServer((Object)SquadActionPacket.unlock());
    }

    public static void sendOpenFactionScreen(ServerPlayer player) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new OpenFactionScreenPacket());
        NetworkManager.sendCurrentTeamSelectState(player);
    }

    public static void sendCurrentTeamSelectState(ServerPlayer player) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        GameStateManager gsm = GameStateManager.getInstance();
        int attack = 0;
        int defend = 0;
        for (ServerPlayer p : server.m_6846_().m_11314_()) {
            String t = Espetro.getPlayerTeam(p);
            if ("ATTACK".equals(t)) {
                ++attack;
                continue;
            }
            if (!"DEFEND".equals(t)) continue;
            ++defend;
        }
        ClassSelectManager csm = ClassSelectManager.getInstance();
        String atkImg = NetworkManager.getFactionSelectionImage(csm.getFinalAttackClass());
        String defImg = NetworkManager.getFactionSelectionImage(csm.getFinalDefendClass());
        long end = server.m_129783_().m_46467_() + 999999L;
        String myTeam = Espetro.getPlayerTeam(player);
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new TeamSelectStatePacket(attack, defend, 0, end, false, myTeam, null, atkImg, defImg));
    }

    private static String getFactionSelectionImage(String factionId) {
        return NetworkManager.getFactionSelectionImageStatic(factionId);
    }

    public static String getFactionSelectionImageStatic(String factionId) {
        FactionDataLoader.FactionData faction;
        if (factionId == null || factionId.isEmpty()) {
            return null;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        if (loader != null && (faction = loader.getFaction(factionId)) != null && faction.selectionImage != null && !faction.selectionImage.isEmpty()) {
            return faction.selectionImage;
        }
        return null;
    }

    public static <T> void sendToPlayer(ServerPlayer player, T packet) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendClassSelectScreen(ServerPlayer player, String team, boolean isCommander, int timeRemaining) {
        List<ClassSelectScreenPacket.FactionInfo> factionList = NetworkManager.getFactionListForTeam(team);
        String opponentTeamName = NetworkManager.teamDisplayName(NetworkManager.oppositeTeam(team));
        String opponentFaction = NetworkManager.getOpponentFactionDisplayName(team);
        ClassSelectScreenPacket packet = new ClassSelectScreenPacket(team, isCommander, factionList, timeRemaining, opponentTeamName, opponentFaction, -1, ClassSelectManager.getInstance().getPlayerFactionVote(player.m_20148_(), team));
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    private static List<ClassSelectScreenPacket.FactionInfo> getFactionListForTeam(String team) {
        List<String> pool = ClassSelectManager.getInstance().getAvailableFactionPoolForTeam(team);
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        Map<String, Integer> voteCounts = ClassSelectManager.getInstance().getFactionVoteCounts(team);
        ArrayList<ClassSelectScreenPacket.FactionInfo> list = new ArrayList<ClassSelectScreenPacket.FactionInfo>();
        int totalImageBytes = 0;
        for (String id : pool) {
            FactionDataLoader.FactionData faction = loader.getFaction(id);
            String name = faction != null ? faction.name : id;
            String selectionImage = faction != null ? faction.selectionImage : "";
            byte[] imageData = NetworkManager.loadEsFactionsImage(selectionImage);
            if (imageData != null && (imageData.length > 180000 || totalImageBytes + imageData.length > 700000)) {
                imageData = null;
            } else if (imageData != null) {
                totalImageBytes += imageData.length;
            }
            list.add(new ClassSelectScreenPacket.FactionInfo(id, name, selectionImage, voteCounts.getOrDefault(id, 0), imageData));
        }
        return list;
    }

    private static byte[] loadEsFactionsImage(String selectionImage) {
        if (selectionImage == null || selectionImage.isBlank()) {
            return null;
        }
        if (selectionImage.contains(":")) {
            return null;
        }
        try {
            Path imagePath = FMLPaths.GAMEDIR.get().resolve("EsFactions").resolve(selectionImage).normalize();
            Path esFactionsDir = FMLPaths.GAMEDIR.get().resolve("EsFactions").normalize();
            if (!imagePath.startsWith(esFactionsDir)) {
                return null;
            }
            if (!Files.isRegularFile(imagePath, new LinkOption[0])) {
                return null;
            }
            if (Files.size(imagePath) > 180000L) {
                return null;
            }
            return Files.readAllBytes(imagePath);
        }
        catch (Exception e) {
            Espetro.LOGGER.debug("EsFactions \u56fe\u7247\u8bfb\u53d6\u5931\u8d25: {} ({})", (Object)selectionImage, (Object)e.toString());
            return null;
        }
    }

    public static void broadcastOpenFactionScreen() {
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            OpenFactionScreenPacket packet = new OpenFactionScreenPacket();
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
            }
        }
    }

    public static void sendWaitingStatus(ServerPlayer player, String message, boolean isActionBar) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new WaitingStatusPacket(message, isActionBar));
    }

    public static void broadcastWaitingStatus(String message, boolean isActionBar) {
        MinecraftServer server = Espetro.getServer();
        if (server != null) {
            WaitingStatusPacket packet = new WaitingStatusPacket(message, isActionBar);
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
            }
        }
    }

    public static void broadcastCommanderVoteScreenForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        NetworkManager.sendCommanderVoteScreenForTeamView(server, team, timeRemaining, -1);
        NetworkManager.sendCommanderVoteScreenForTeamView(server, NetworkManager.oppositeTeam(team), 0, timeRemaining);
    }

    private static void sendCommanderVoteScreenForTeamView(MinecraftServer server, String viewTeam, int timeRemaining, int opponentTimeRemaining) {
        VoteManager voteManager = VoteManager.getInstance();
        Set<UUID> teamUuids = "ATTACK".equals(viewTeam) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        ArrayList<String> teamPlayers = new ArrayList<String>();
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null) continue;
            teamPlayers.add(player.m_7755_().getString());
        }
        String opponentTeamName = NetworkManager.teamDisplayName(NetworkManager.oppositeTeam(viewTeam));
        String opponentFaction = NetworkManager.getOpponentFactionDisplayName(viewTeam);
        CommanderVotePacket packet = new CommanderVotePacket(viewTeam, teamPlayers, timeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining);
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null) continue;
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void sendCommanderVoteScreenToPlayer(ServerPlayer player, String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        VoteManager voteManager = VoteManager.getInstance();
        Set<UUID> teamUuids = "ATTACK".equals(team) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        ArrayList<String> teamPlayers = new ArrayList<String>();
        for (UUID uuid : teamUuids) {
            ServerPlayer p = server.m_6846_().m_11259_(uuid);
            if (p == null) continue;
            teamPlayers.add(p.m_7755_().getString());
        }
        String opponentTeamName = NetworkManager.teamDisplayName(NetworkManager.oppositeTeam(team));
        String opponentFaction = NetworkManager.getOpponentFactionDisplayName(team);
        String activeTeam = voteManager.getCurrentVotingTeam();
        int ownTimeRemaining = team.equals(activeTeam) ? timeRemaining : 0;
        int opponentTimeRemaining = team.equals(activeTeam) ? -1 : timeRemaining;
        CommanderVotePacket packet = new CommanderVotePacket(team, teamPlayers, ownTimeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining);
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    public static void sendClassSelectScreenForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        NetworkManager.sendClassSelectScreenForTeamView(server, team, timeRemaining, -1);
    }

    public static void broadcastClassSelectScreenForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        NetworkManager.sendClassSelectScreenForTeamView(server, team, timeRemaining, -1);
        NetworkManager.sendClassSelectScreenForTeamView(server, NetworkManager.oppositeTeam(team), 0, timeRemaining);
    }

    public static void broadcastClassSelectTimerForTeam(String team, int timeRemaining) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        VoteManager voteManager = VoteManager.getInstance();
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        NetworkManager.sendClassSelectTimerForTeamView(server, team, timeRemaining, -1, voteManager, selectManager);
        NetworkManager.sendClassSelectTimerForTeamView(server, NetworkManager.oppositeTeam(team), 0, timeRemaining, voteManager, selectManager);
    }

    private static void sendClassSelectTimerForTeamView(MinecraftServer server, String viewTeam, int timeRemaining, int opponentTimeRemaining, VoteManager voteManager, ClassSelectManager selectManager) {
        Set<UUID> teamUuids = "ATTACK".equals(viewTeam) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null) continue;
            boolean isCommander = voteManager.isCommanderOf(uuid, viewTeam);
            String selected = selectManager.getPlayerFactionVote(uuid, viewTeam);
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new ClassSelectTimerPacket(timeRemaining, opponentTimeRemaining, selected, isCommander));
        }
    }

    private static void sendClassSelectScreenForTeamView(MinecraftServer server, String viewTeam, int timeRemaining, int opponentTimeRemaining) {
        VoteManager voteManager = VoteManager.getInstance();
        Set<UUID> teamUuids = "ATTACK".equals(viewTeam) ? voteManager.getAttackPlayers() : voteManager.getDefendPlayers();
        String opponentTeamName = NetworkManager.teamDisplayName(NetworkManager.oppositeTeam(viewTeam));
        String opponentFaction = NetworkManager.getOpponentFactionDisplayName(viewTeam);
        List<ClassSelectScreenPacket.FactionInfo> factionList = NetworkManager.getFactionListForTeam(viewTeam);
        for (UUID uuid : teamUuids) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null) continue;
            boolean isCommander = voteManager.isCommanderOf(uuid, viewTeam);
            ClassSelectScreenPacket packet = new ClassSelectScreenPacket(viewTeam, isCommander, factionList, timeRemaining, opponentTeamName, opponentFaction, opponentTimeRemaining, ClassSelectManager.getInstance().getPlayerFactionVote(uuid, viewTeam));
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void broadcastFactionRevealScreen(String attackFactionId, String defendFactionId, int durationSeconds) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        FactionRevealPacket packet = new FactionRevealPacket(NetworkManager.getFactionDisplayName(attackFactionId), NetworkManager.getFactionDisplayName(defendFactionId), NetworkManager.getFactionSelectionImage(attackFactionId), NetworkManager.getFactionSelectionImage(defendFactionId), durationSeconds);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void sendCastVote(String targetPlayerName) {
        NET.sendToServer((Object)new CastVotePacket(targetPlayerName));
    }

    public static void broadcastGamePhase(GamePhase phase) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        GamePhaseSyncPacket packet = new GamePhaseSyncPacket(phase, GameStateManager.getInstance().getCurrentMapFolder(), BattlefieldContext.getObjectiveMode());
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void broadcastBattleTimer(int remainingSeconds) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        BattleTimerPacket packet = new BattleTimerPacket(remainingSeconds);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void broadcastClassSelectionScreen(String attackFactionId, String defendFactionId) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String factionId;
            String playerTeam = Espetro.getPlayerTeam(player);
            if (playerTeam == null) continue;
            String string = factionId = "ATTACK".equals(playerTeam) ? attackFactionId : defendFactionId;
            if (factionId == null) continue;
            OpenClassSelectionPacket packet = new OpenClassSelectionPacket(factionId, loader);
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void sendClassSelectionScreen(ServerPlayer player, String factionId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        OpenClassSelectionPacket packet = new OpenClassSelectionPacket(factionId, loader);
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    public static void broadcastTroopCounts(int attack, int defend) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        TroopCountSyncPacket packet = new TroopCountSyncPacket(attack, defend);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void sendVehicleDeployScreen(ServerPlayer player, String factionId) {
        NetworkManager.sendVehicleDeployScreen(player, factionId, true);
    }

    public static void syncVehicleDeployScreen(ServerPlayer player, String factionId) {
        NetworkManager.sendVehicleDeployScreen(player, factionId, false);
    }

    public static void requestVehicleInfo() {
        NET.sendToServer((Object)new RequestVehicleInfoPacket());
    }

    private static void sendVehicleDeployScreen(ServerPlayer player, String factionId, boolean openScreen) {
        Map<String, VehicleConfig.VehicleTypeConfig> configs = VehicleConfig.getFactionVehicles(factionId);
        ArrayList<VehicleDeployScreenPacket.VehicleInfo> list = new ArrayList<VehicleDeployScreenPacket.VehicleInfo>();
        VehicleManager vm = VehicleManager.getInstance();
        for (Map.Entry<String, VehicleConfig.VehicleTypeConfig> entry : configs.entrySet()) {
            String type = entry.getKey();
            VehicleConfig.VehicleTypeConfig cfg = entry.getValue();
            String team = Espetro.getPlayerTeam(player);
            int current = team == null ? vm.getActiveCount(factionId, type) : vm.getActiveCount(team, factionId, type);
            long cooldown = vm.getCooldownRemaining(team, factionId, type);
            String displayName = VehicleManager.getDisplayName(factionId, type);
            list.add(new VehicleDeployScreenPacket.VehicleInfo(type, displayName, cfg.max, current, System.currentTimeMillis() + cooldown, cfg.respawnMinutes));
        }
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new VehicleDeployScreenPacket(openScreen, list));
    }

    public static void sendDeployPointSelectScreen(ServerPlayer player) {
        NetworkManager.sendUnifiedDeployScreen(player, -1);
    }

    public static void sendUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining) {
        NetworkManager.sendUnifiedDeployScreen(player, deployTimeRemaining, true);
    }

    public static void syncUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining) {
        NetworkManager.sendUnifiedDeployScreen(player, deployTimeRemaining, false);
    }

    private static void sendUnifiedDeployScreen(ServerPlayer player, int deployTimeRemaining, boolean openScreen) {
        String factionDesc;
        FactionDataLoader.FactionData factionData;
        String factionId;
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        GameStateManager gameState = GameStateManager.getInstance();
        int phaseTimeRemaining = switch (gameState.getCurrentPhase()) {
            case GamePhase.DEPLOYING -> gameState.getDeployTimeRemainingSeconds();
            case GamePhase.BATTLE -> gameState.getBattleTimeRemainingSeconds();
            default -> deployTimeRemaining;
        };
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return;
        }
        VoteManager voteManager = VoteManager.getInstance();
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        BastionManager bm = BastionManager.getInstance();
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        String string = factionId = "ATTACK".equals(team) ? selectManager.getFinalAttackClass() : selectManager.getFinalDefendClass();
        if (factionId == null) {
            factionId = team;
        }
        String factionName = (factionData = loader.getFaction(factionId)) != null ? factionData.name : factionId;
        String string2 = factionDesc = factionData != null ? factionData.description : "";
        String factionIcon = factionData != null ? (factionData.icon != null ? factionData.icon : "") : "";
        ArrayList<UnifiedDeployScreenPacket.ClassInfo> classList = new ArrayList<UnifiedDeployScreenPacket.ClassInfo>();
        HashMap<String, Integer> classCountMap = new HashMap<String, Integer>();
        FactionDataLoader.ClassKitData[] kits = loader.getClassesForFaction(factionId);
        if (kits != null) {
            ClassCountManager counts = ClassCountManager.getInstance();
            boolean isLeader = SquadManager.getInstance().isSquadLeader(player.m_20148_());
            for (FactionDataLoader.ClassKitData kit : kits) {
                if (kit.leaderOnly && !isLeader) continue;
                int count = counts.getEffectiveClassCountForViewer(player.m_20148_(), team, kit.id);
                int squadCount = counts.getSquadClassCountForViewer(player.m_20148_(), team, kit.id);
                ArrayList<UnifiedDeployScreenPacket.VariantInfo> variants = new ArrayList<UnifiedDeployScreenPacket.VariantInfo>();
                if (kit.variants != null) {
                    for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                        int vCount = kit.teamCount ? counts.countVariantInSquad(team, SquadManager.getInstance().getPlayerSquadId(player.m_20148_()), kit.id, variant.id) : counts.getVariantCount(team, kit.id, variant.id);
                        ClassLoadoutPreviewResolver.Preview preview = ClassLoadoutPreviewResolver.resolve(server, kit, variant);
                        variants.add(new UnifiedDeployScreenPacket.VariantInfo(variant.id, variant.name, variant.description, variant.maxPlayers, vCount, new UnifiedDeployScreenPacket.LoadoutPreview(preview.head, preview.chest, preview.legs, preview.feet, preview.mainHand, preview.offHand)));
                    }
                }
                classList.add(new UnifiedDeployScreenPacket.ClassInfo(kit.id, kit.name, kit.description, kit.role, kit.icon, kit.iconImage, kit.maxPlayers, kit.strictCount, count, kit.troopValue, kit.healthBonus, kit.speedBonus, kit.teamCount, kit.maxPerSquad, squadCount, Math.max(0, kit.teammatesNeed), kit.row, kit.unlockPerN, kit.unlockMinSquad, kit.leaderOnly, variants));
                classCountMap.put(kit.id, count);
            }
        }
        boolean hasDeploy = false;
        Object deployPos = "";
        BastionManager.DeployPoint dp = bm.getPlayerDeployPoint(player.m_20148_());
        if (dp != null && dp.pos != null) {
            hasDeploy = true;
            deployPos = dp.pos.m_123341_() + ", " + dp.pos.m_123342_() + ", " + dp.pos.m_123343_();
        }
        ArrayList<UnifiedDeployScreenPacket.BastionItem> bastionList = new ArrayList<UnifiedDeployScreenPacket.BastionItem>();
        for (BastionData bd : bm.getTeamBastions(team)) {
            BlockPos armorStandPos = bm.getRecordedArmorStandPosition(bd);
            if (armorStandPos == null) continue;
            bastionList.add(new UnifiedDeployScreenPacket.BastionItem(bd.getBastionId(), bd.getName(), armorStandPos.m_123341_() + ", " + armorStandPos.m_123342_() + ", " + armorStandPos.m_123343_(), "hab", bm.getFobStatus(bd), 0L, 0, bd.getHabAvailableAt(), LogisticsConfig.get().habActivationSeconds));
        }
        bastionList.addAll(TeamPackManager.getInstance().getDeployItemsForPlayer(player));
        if (OutpostManager.getInstance().canListFor(team)) {
            List<OutpostManager.Outpost> outposts = OutpostManager.getInstance().getOutposts();
            for (int i = 0; i < outposts.size(); ++i) {
                OutpostManager.Outpost op = outposts.get(i);
                bastionList.add(new UnifiedDeployScreenPacket.BastionItem(new UUID(0L, (long)i + 1L), "\u00a7d\u524d\u54e8: " + op.name, op.getPosString()));
            }
        }
        boolean isCmd = voteManager.isCommanderOf(player.m_20148_(), team);
        List<UnifiedDeployScreenPacket.VehicleInfo> vehicleList = Collections.emptyList();
        List<UnifiedDeployScreenPacket.SquadInfo> squadList = NetworkManager.buildSquadInfoList(team);
        List<String> commanderNames = NetworkManager.getCommanderNames(team);
        int mySquadId = SquadManager.getInstance().getPlayerSquadId(player.m_20148_());
        ArrayList<UnifiedDeployScreenPacket.SquadCategoryInfo> squadCategories = new ArrayList<UnifiedDeployScreenPacket.SquadCategoryInfo>();
        ActiveMapConfig activeMap = BattlefieldContext.getOrNull();
        SquadTypesSnapshot types = activeMap != null ? activeMap.squadTypes : SquadTypesSnapshot.defaults();
        for (SquadTypesSnapshot.Category category : types.categories) {
            squadCategories.add(new UnifiedDeployScreenPacket.SquadCategoryInfo(category.id(), category.displayName()));
        }
        UnifiedDeployScreenPacket packet = new UnifiedDeployScreenPacket(factionId, factionName, factionDesc, factionIcon, classList, classCountMap, hasDeploy, (String)deployPos, bastionList, isCmd, vehicleList, squadList, mySquadId, phaseTimeRemaining, team, commanderNames, GameConfig.getTeammateNameTagDistance(), bm.isWaitingForBastion(player.m_20148_()), OutpostManager.getInstance().getRedeployCooldownRemaining(player.m_20148_()), squadCategories, ClassCountManager.getInstance().getClassSwitchCooldownRemaining(player.m_20148_()), openScreen, Objects.toString(ClassCountManager.getInstance().getPlayerClass(player.m_20148_()), ""));
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)MatchStatsSyncPacket.from(PlayerMatchStatsManager.getInstance()));
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)GovernanceStatePacket.from(CommanderGovernanceManager.getInstance(), player.m_20148_()));
        NetworkManager.sendEquipZones(player);
    }

    public static void sendDeployPointSync(ServerPlayer player) {
        if (player == null || player.f_8906_ == null) {
            return;
        }
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return;
        }
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new DeployPointSyncPacket(NetworkManager.buildDeployPointItems(player, team)));
    }

    public static void refreshWaitingDeployPoints() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
            return;
        }
        BastionManager bm = BastionManager.getInstance();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!bm.isWaitingForBastion(player.m_20148_())) continue;
            NetworkManager.sendDeployPointSync(player);
        }
    }

    public static void refreshDeployPointsForTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
            return;
        }
        BastionManager bm = BastionManager.getInstance();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(player)) || !bm.isWaitingForBastion(player.m_20148_())) continue;
            NetworkManager.sendDeployPointSync(player);
        }
    }

    private static List<UnifiedDeployScreenPacket.BastionItem> buildDeployPointItems(ServerPlayer player, String team) {
        BastionManager bm = BastionManager.getInstance();
        ArrayList<UnifiedDeployScreenPacket.BastionItem> items = new ArrayList<UnifiedDeployScreenPacket.BastionItem>();
        for (BastionData bd : bm.getTeamBastions(team)) {
            BlockPos armorStandPos = bm.getRecordedArmorStandPosition(bd);
            if (armorStandPos == null) continue;
            items.add(new UnifiedDeployScreenPacket.BastionItem(bd.getBastionId(), bd.getName(), armorStandPos.m_123341_() + ", " + armorStandPos.m_123342_() + ", " + armorStandPos.m_123343_(), "hab", bm.getFobStatus(bd), 0L, 0, bd.getHabAvailableAt(), LogisticsConfig.get().habActivationSeconds));
        }
        items.addAll(TeamPackManager.getInstance().getDeployItemsForPlayer(player));
        if (OutpostManager.getInstance().canListFor(team)) {
            List<OutpostManager.Outpost> outposts = OutpostManager.getInstance().getOutposts();
            for (int i = 0; i < outposts.size(); ++i) {
                OutpostManager.Outpost op = outposts.get(i);
                items.add(new UnifiedDeployScreenPacket.BastionItem(new UUID(0L, (long)i + 1L), "\u00a7d\u524d\u54e8: " + op.name, op.getPosString()));
            }
        }
        return items;
    }

    public static void sendSquadSync(ServerPlayer player) {
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return;
        }
        NetworkManager.sendSquadSync(player, team, NetworkManager.buildSquadInfoList(team), NetworkManager.getCommanderNames(team));
    }

    public static void syncSquadsToTeam(String team) {
        MinecraftServer server = Espetro.getServer();
        if (server == null || team == null) {
            return;
        }
        List<UnifiedDeployScreenPacket.SquadInfo> squadList = NetworkManager.buildSquadInfoList(team);
        List<String> commanderNames = NetworkManager.getCommanderNames(team);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            if (!team.equals(Espetro.getPlayerTeam(player))) continue;
            NetworkManager.sendSquadSync(player, team, squadList, commanderNames);
        }
    }

    private static void sendSquadSync(ServerPlayer player, String team, List<UnifiedDeployScreenPacket.SquadInfo> squadList, List<String> commanderNames) {
        SquadSyncPacket packet = new SquadSyncPacket(team, squadList, SquadManager.getInstance().getPlayerSquadId(player.m_20148_()), commanderNames, GameConfig.getTeammateNameTagDistance(), Objects.toString(ClassCountManager.getInstance().getPlayerClass(player.m_20148_()), ""));
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    private static List<UnifiedDeployScreenPacket.SquadInfo> buildSquadInfoList(String team) {
        ArrayList<UnifiedDeployScreenPacket.SquadInfo> squadList = new ArrayList<UnifiedDeployScreenPacket.SquadInfo>();
        Set<UUID> commanderUuids = NetworkManager.getCommanderUuids(team);
        for (SquadManager.SquadSnapshot squad : SquadManager.getInstance().getSquadSnapshots(team)) {
            ArrayList<UnifiedDeployScreenPacket.SquadMemberInfo> members = new ArrayList<UnifiedDeployScreenPacket.SquadMemberInfo>();
            for (SquadManager.MemberSnapshot member : squad.members) {
                members.add(new UnifiedDeployScreenPacket.SquadMemberInfo(member.uuid, member.playerName, member.className, member.leader, commanderUuids.contains(member.uuid), member.fireteam.toNetwork(), member.fireteamLeader));
            }
            squadList.add(new UnifiedDeployScreenPacket.SquadInfo(squad.id, squad.displayId, squad.name, members.size(), squad.maxMembers, squad.locked, squad.leaderName, squad.categoryId, squad.categoryDisplayName, members));
        }
        return squadList;
    }

    private static List<String> getCommanderNames(String team) {
        MinecraftServer server = Espetro.getServer();
        ArrayList<String> names = new ArrayList<String>();
        if (server == null) {
            return names;
        }
        for (UUID uuid : NetworkManager.getCommanderUuids(team)) {
            ServerPlayer player = server.m_6846_().m_11259_(uuid);
            if (player == null) continue;
            names.add(player.m_7755_().getString());
        }
        return names;
    }

    private static Set<UUID> getCommanderUuids(String team) {
        UUID uuid;
        HashSet<UUID> result = new HashSet<UUID>();
        VoteManager voteManager = VoteManager.getInstance();
        if ("ATTACK".equals(team)) {
            UUID uuid2 = voteManager.getAttackCommander();
            if (uuid2 != null) {
                result.add(uuid2);
            }
        } else if ("DEFEND".equals(team) && (uuid = voteManager.getDefendCommander()) != null) {
            result.add(uuid);
        }
        return result;
    }

    private static String getOpponentFactionDisplayName(String myTeam) {
        ClassSelectManager selectManager = ClassSelectManager.getInstance();
        String opponentFactionId = "ATTACK".equals(myTeam) ? selectManager.getFinalDefendClass() : selectManager.getFinalAttackClass();
        return NetworkManager.getFactionDisplayName(opponentFactionId);
    }

    private static String getFactionDisplayName(String factionId) {
        FactionDataLoader.FactionData faction;
        if (factionId == null || factionId.isEmpty()) {
            return null;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        if (loader != null && (faction = loader.getFaction(factionId)) != null && faction.name != null) {
            return faction.name;
        }
        return factionId;
    }

    private static String oppositeTeam(String team) {
        return "ATTACK".equals(team) ? "DEFEND" : "ATTACK";
    }

    private static String teamDisplayName(String team) {
        return TeamDisplayNames.displayName(team);
    }

    public static void requestCommanderSkillSync() {
        NET.sendToServer((Object)CommanderSkillPacket.query());
    }

    public static void sendCommanderSkillActivate(CommanderSkillType type) {
        NET.sendToServer((Object)CommanderSkillPacket.activate(type));
    }

    public static void sendCommanderSkillActivate(String skillId) {
        NET.sendToServer((Object)new CommanderSkillPacket(skillId));
    }

    public static void sendCommanderSkillSync(ServerPlayer player) {
        boolean isCommander = VoteManager.getInstance().isCommander(player.m_20148_());
        CommanderSkillManager skills = CommanderSkillManager.getInstance();
        Map<String, Integer> cooldowns = skills.getCooldownData(player.m_20148_());
        List<CommanderSkillManager.SkillView> views = skills.getSkillViewsFor(player);
        boolean showSkillsEntry = isCommander || !views.isEmpty();
        CommanderSkillSyncPacket packet = new CommanderSkillSyncPacket(showSkillsEntry, cooldowns, views);
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
    }

    public static void broadcastMapVoteState(MapVoteManager mgr) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NetworkManager.sendMapVoteState(player, mgr);
        }
    }

    public static void sendMapVoteState(ServerPlayer player, MapVoteManager mgr) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)MapVoteStatePacket.from(mgr, player));
    }

    public static void sendOpenMapVoteScreen(ServerPlayer player) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new OpenMapVoteScreenPacket());
    }

    public static void broadcastTeamSelectState(int attack, int defend, int remaining, long endGameTime, boolean active, String lockedTeam) {
        NetworkManager.broadcastTeamSelectState(attack, defend, remaining, endGameTime, active, lockedTeam, null, null);
    }

    public static void broadcastTeamSelectState(int attack, int defend, int remaining, long endGameTime, boolean active, String lockedTeam, String attackFactionImage, String defendFactionImage) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String team = Espetro.getPlayerTeam(player);
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new TeamSelectStatePacket(attack, defend, remaining, endGameTime, active, team, lockedTeam, attackFactionImage, defendFactionImage));
        }
    }

    public static void broadcastMatchStats(PlayerMatchStatsManager mgr) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        MatchStatsSyncPacket packet = MatchStatsSyncPacket.from(mgr);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void broadcastGovernanceState(CommanderGovernanceManager mgr) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)GovernanceStatePacket.from(mgr, player.m_20148_()));
        }
    }

    public static void sendOpenHubScreen(ServerPlayer player, int online, String status) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)new OpenHubScreenPacket(online, status));
    }

    public static void broadcastRoundEnd(String winner, int seconds, String winnerShowName, String loserShowName, int attackTickets, int defendTickets, int resultLevel, boolean attackerTimedOut) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        RoundEndPacket packet = new RoundEndPacket(winner, seconds, winnerShowName, loserShowName, attackTickets, defendTickets, resultLevel, attackerTimedOut);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)packet);
        }
    }

    public static void sendMapVoteCast(String mapFolder) {
        NET.sendToServer((Object)new MapVoteCastPacket(mapFolder));
    }

    public static void sendGovernanceAction(GovernanceActionPacket.Action action, UUID candidate) {
        NET.sendToServer((Object)new GovernanceActionPacket(action, candidate));
    }

    public static void sendSquadCreateWithCategory(String name, String categoryId) {
        NET.sendToServer((Object)new SquadCreateWithCategoryPacket(name, categoryId));
    }

    public static void sendMatchStatsAction(MatchStatsActionPacket.Action action, UUID target) {
        NET.sendToServer((Object)new MatchStatsActionPacket(action, target));
    }

    public static void sendPartyCreate(String password) {
        NET.sendToServer((Object)PartyActionPacket.create(password));
    }

    public static void sendPartyJoin(UUID partyId, String password) {
        NET.sendToServer((Object)PartyActionPacket.join(partyId, password));
    }

    public static void sendPartyLeave() {
        NET.sendToServer((Object)PartyActionPacket.leave());
    }

    public static void sendPartyKick(UUID partyId, UUID targetId) {
        NET.sendToServer((Object)PartyActionPacket.kick(partyId, targetId));
    }

    public static void sendPartyToggleLock(UUID partyId) {
        NET.sendToServer((Object)PartyActionPacket.toggleLock(partyId));
    }

    public static void sendPartyDisband(UUID partyId) {
        NET.sendToServer((Object)PartyActionPacket.disband(partyId));
    }

    public static void requestPartyList() {
        NET.sendToServer((Object)PartyActionPacket.requestList());
    }

    public static void broadcastPartyList(PartyManager pm) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NetworkManager.sendPartyListTo(player);
        }
    }

    public static void sendPartyListTo(ServerPlayer player) {
        NET.send(PacketDistributor.PLAYER.with(() -> player), (Object)PartyListPacket.from(PartyManager.getInstance(), player.m_20148_()));
    }

    private record QueuedDeployScreen(int deployTimeRemaining, boolean playEntryAudio) {
    }
}

