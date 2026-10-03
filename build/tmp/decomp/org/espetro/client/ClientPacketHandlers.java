/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import org.espetro.client.ClientEquipZones;
import org.espetro.client.FortificationPlacementController;
import org.espetro.client.audio.ClientFormationAudioManager;
import org.espetro.client.gui.AuraTipRadialController;
import org.espetro.client.gui.ClassSelectScreen;
import org.espetro.client.gui.ClassSelectionScreen;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.ClientGovernanceState;
import org.espetro.client.gui.ClientTacticalState;
import org.espetro.client.gui.CommanderSkillScreen;
import org.espetro.client.gui.CommanderVoteScreen;
import org.espetro.client.gui.DeployPointSelectScreen;
import org.espetro.client.gui.EspetroTipNotifier;
import org.espetro.client.gui.FactionRevealScreen;
import org.espetro.client.gui.FobSupplyHud;
import org.espetro.client.gui.FortificationProgressHud;
import org.espetro.client.gui.HubScreen;
import org.espetro.client.gui.MapVoteScreen;
import org.espetro.client.gui.MatchScoreboardScreen;
import org.espetro.client.gui.OutpostSupplyHud;
import org.espetro.client.gui.PartyScreen;
import org.espetro.client.gui.ResupplyRadialController;
import org.espetro.client.gui.RoundEndScreen;
import org.espetro.client.gui.SquadScreen;
import org.espetro.client.gui.TeamSelectionScreen;
import org.espetro.client.gui.TroopCountOverlay;
import org.espetro.client.gui.TutorialOverlay;
import org.espetro.client.gui.UnifiedDeployScreen;
import org.espetro.client.gui.VehicleDeployScreen;
import org.espetro.client.gui.VehicleWheelController;
import org.espetro.client.vehicle.VehicleInteractionKind;
import org.espetro.client.vehicle.VehicleInteractionState;
import org.espetro.network.AudioCuePacket;
import org.espetro.network.BattleTimerPacket;
import org.espetro.network.ClassCountSyncPacket;
import org.espetro.network.ClassSelectScreenPacket;
import org.espetro.network.ClassSelectTimerPacket;
import org.espetro.network.CommanderSkillSyncPacket;
import org.espetro.network.CommanderVotePacket;
import org.espetro.network.DeployPointSelectPacket;
import org.espetro.network.DeployPointSyncPacket;
import org.espetro.network.EquipZoneSyncPacket;
import org.espetro.network.FactionRevealPacket;
import org.espetro.network.FobSupplySyncPacket;
import org.espetro.network.FortificationCatalogPacket;
import org.espetro.network.FortificationPreviewPacket;
import org.espetro.network.FortificationProgressPacket;
import org.espetro.network.GameStateResponsePacket;
import org.espetro.network.GovernanceStatePacket;
import org.espetro.network.MapVoteStatePacket;
import org.espetro.network.MatchStatsSyncPacket;
import org.espetro.network.MountProgressPacket;
import org.espetro.network.OpenClassSelectionPacket;
import org.espetro.network.OpenHubScreenPacket;
import org.espetro.network.OutpostSupplySyncPacket;
import org.espetro.network.PartyListPacket;
import org.espetro.network.ResupplyCatalogPacket;
import org.espetro.network.ResupplyEntryDeltaPacket;
import org.espetro.network.RoundEndPacket;
import org.espetro.network.SquadSyncPacket;
import org.espetro.network.TeamSelectStatePacket;
import org.espetro.network.TroopCountSyncPacket;
import org.espetro.network.UnifiedDeployScreenPacket;
import org.espetro.network.VehicleDeployScreenPacket;
import org.espetro.network.VehicleSupplySyncPacket;
import org.espetro.network.VoteDataPacket;
import org.espetro.team.GamePhase;

public class ClientPacketHandlers {
    public static void handleResupplyCatalog(ResupplyCatalogPacket packet) {
        ResupplyRadialController.onCatalog(packet);
    }

    public static void handleResupplyDelta(ResupplyEntryDeltaPacket packet) {
        ResupplyRadialController.onDelta(packet);
    }

    public static void handleOpenFactionScreen() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            ClientGameState.setPlayerTeam(null);
            ClientGameState.setPlayerFactionId(null);
            if (!(mc.f_91080_ instanceof TeamSelectionScreen)) {
                mc.m_91152_(new TeamSelectionScreen());
            }
        }
    }

    public static void handleWaitingStatus(String message, boolean isActionBar) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            MutableComponent component = Component.m_237113_(message);
            if (isActionBar) {
                mc.f_91074_.m_5661_(component, true);
            } else {
                mc.f_91074_.m_213846_(component);
            }
        }
    }

    public static void handleClassSelectScreen(ClassSelectScreenPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        ClientGameState.setPlayerTeam(packet.getTeam());
        Screen screen = mc.f_91080_;
        if (screen instanceof ClassSelectScreen) {
            ClassSelectScreen screen2 = (ClassSelectScreen)screen;
            screen2.updateFromPacket(packet);
        } else {
            mc.m_91152_(new ClassSelectScreen(packet.getTeam(), packet.isCommander(), packet.getFactions(), packet.getTimeRemaining(), packet.getOpponentTeamName(), packet.getOpponentFaction(), packet.getOpponentTimeRemaining(), packet.getSelectedFactionId()));
        }
    }

    public static void handleOpenClassSelection(OpenClassSelectionPacket packet) {
        ClientGameState.setPlayerFactionId(packet.getFactionId());
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            mc.m_91152_(new ClassSelectionScreen(packet.getFactionId(), packet.getFactionName(), packet.getFactionDescription(), packet.getFactionIcon(), packet.getClasses()));
        }
    }

    public static void handleCommanderVote(CommanderVotePacket packet) {
        CommanderVoteScreen screen;
        ClientGameState.setPlayerTeam(packet.getTeam());
        Minecraft mc = Minecraft.m_91087_();
        Screen screen2 = mc.f_91080_;
        if (screen2 instanceof CommanderVoteScreen && (screen = (CommanderVoteScreen)screen2).isForTeam(packet.getTeam())) {
            screen.updatePhaseData(packet.getPlayers(), packet.getTimeRemaining(), packet.getOpponentTeamName(), packet.getOpponentFaction(), packet.getOpponentTimeRemaining());
        } else {
            CommanderVoteScreen.open(packet.getTeam(), packet.getPlayers(), packet.getTimeRemaining(), packet.getOpponentTeamName(), packet.getOpponentFaction(), packet.getOpponentTimeRemaining());
        }
    }

    public static void handleVoteData(VoteDataPacket packet) {
        CommanderVoteScreen.updateVoteData(packet.getVoteCounts(), packet.getTimeRemaining(), packet.getOpponentTimeRemaining());
    }

    public static void handleFactionReveal(FactionRevealPacket packet) {
        FactionRevealScreen screen;
        Screen screen2;
        Minecraft mc = Minecraft.m_91087_();
        if (!(mc.f_91074_ == null || (screen2 = mc.f_91080_) instanceof FactionRevealScreen && (screen = (FactionRevealScreen)screen2).matches(packet.getAttackFactionName(), packet.getDefendFactionName()))) {
            mc.m_91152_(new FactionRevealScreen(packet.getAttackFactionName(), packet.getDefendFactionName(), packet.getAttackFactionImage(), packet.getDefendFactionImage(), packet.getDurationSeconds()));
        }
    }

    public static void handleTroopCount(TroopCountSyncPacket packet) {
        TroopCountOverlay.updateTroopCounts(packet.getAttackTroops(), packet.getDefendTroops());
    }

    public static void handleClassCountSync(ClassCountSyncPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (packet.isError()) {
            String msg = packet.getErrorMessage();
            Screen screen = mc.f_91080_;
            if (screen instanceof ClassSelectionScreen) {
                ClassSelectionScreen screen2 = (ClassSelectionScreen)screen;
                screen2.showError(msg);
            }
            EspetroTipNotifier.showRaw(msg);
            return;
        }
        Screen screen = mc.f_91080_;
        if (screen instanceof ClassSelectionScreen) {
            ClassSelectionScreen screen3 = (ClassSelectionScreen)screen;
            screen3.updateClassCounts(packet.getClassCounts(), packet.getVariantCounts());
        } else {
            screen = mc.f_91080_;
            if (screen instanceof UnifiedDeployScreen) {
                UnifiedDeployScreen screen4 = (UnifiedDeployScreen)screen;
                screen4.updateClassCounts(packet.getClassCounts(), packet.getVariantCounts(), packet.getSquadClassCounts());
            }
        }
    }

    public static void handleGamePhase(String phaseName) {
        ClientPacketHandlers.handleGamePhase(phaseName, "", "");
    }

    public static void handleGamePhase(String phaseName, String mapFolder) {
        ClientPacketHandlers.handleGamePhase(phaseName, mapFolder, "");
    }

    public static void handleGamePhase(String phaseName, String mapFolder, String objectiveMode) {
        ClientGameState.setCurrentMapFolder(mapFolder);
        ClientGameState.setObjectiveMode(objectiveMode);
        try {
            UnifiedDeployScreen screen;
            Screen screen2;
            Minecraft mc;
            GamePhase phase = GamePhase.valueOf(phaseName);
            ClientGameState.setCurrentPhase(phase);
            if (phase.isLobbyLike() || phase == GamePhase.ROUND_END || phase == GamePhase.CLEANUP) {
                ClientGovernanceState.clear();
                ClientEquipZones.clear();
                ClientGameState.setBattleTimeRemaining(-1);
            }
            if (phase.isLobbyLike() || phase == GamePhase.CLEANUP) {
                ClientFormationAudioManager.stopAll();
            }
            Minecraft phaseMc = Minecraft.m_91087_();
            if (phase == GamePhase.MAP_LOADING && phaseMc.f_91080_ instanceof MapVoteScreen) {
                phaseMc.m_91152_(null);
            }
            if (phase.isLobbyLike() && phaseMc.f_91080_ instanceof RoundEndScreen) {
                phaseMc.m_91152_(null);
            }
            if (phase == GamePhase.BATTLE) {
                mc = Minecraft.m_91087_();
                screen2 = mc.f_91080_;
                if (screen2 instanceof UnifiedDeployScreen && !(screen = (UnifiedDeployScreen)screen2).isWaitingForDeploySelection()) {
                    mc.m_91152_(null);
                }
            }
            mc = Minecraft.m_91087_();
            screen2 = mc.f_91080_;
            if (screen2 instanceof UnifiedDeployScreen) {
                screen = (UnifiedDeployScreen)screen2;
                screen.updateBattleTimer();
            }
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
    }

    public static void handleVehicleDeployScreen(VehicleDeployScreenPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        Screen screen = mc.f_91080_;
        if (screen instanceof VehicleDeployScreen) {
            VehicleDeployScreen screen2 = (VehicleDeployScreen)screen;
            screen2.updateFromPacket(packet.getVehicles());
        } else if (packet.shouldOpenScreen()) {
            mc.m_91152_(new VehicleDeployScreen(packet.getVehicles()));
        }
    }

    public static void handleDeployPointSelect(DeployPointSelectPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            mc.m_91152_(new DeployPointSelectScreen(packet.hasDeployPoint(), packet.getDeployPointPos(), packet.getBastions()));
        }
    }

    public static void handleDeployPointSync(DeployPointSyncPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen screen2 = (UnifiedDeployScreen)screen;
            screen2.updateBastions(packet.getDeployPoints());
        }
    }

    public static void handleUnifiedDeployScreen(UnifiedDeployScreenPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        ClientGameState.setPlayerFactionId(packet.getFactionId());
        ClientGameState.setPlayerTeam(packet.getTeam());
        if (ClientGameState.getCurrentPhase() == GamePhase.BATTLE && packet.getDeployTimeRemaining() >= 0) {
            ClientGameState.setBattleTimeRemaining(packet.getDeployTimeRemaining());
        }
        ClientTacticalState.updateSquads(packet.getSquads(), packet.getMySquadId(), packet.getCommanderNames(), packet.getTeammateNameTagDistance());
        Screen screen = mc.f_91080_;
        if (screen instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen screen2 = (UnifiedDeployScreen)screen;
            screen2.updateTimeRemaining(packet.getDeployTimeRemaining());
            screen2.updateDeploymentState(packet.isWaitingForDeploySelection(), packet.getOutpostRedeployCooldownRemaining());
            screen2.updateClassSwitchCooldown(packet.getClassSwitchCooldownRemaining());
            screen2.updateBastions(packet.getBastions());
            screen2.updateSquads(packet.getSquads(), packet.getMySquadId());
            screen2.updateClasses(packet.getClasses(), packet.getClassCounts(), packet.getVariantCounts());
            screen2.updateSelectedClass(packet.getSelectedClassId());
        } else {
            screen = mc.f_91080_;
            if (screen instanceof SquadScreen) {
                SquadScreen screen3 = (SquadScreen)screen;
                screen3.updateFromDeployPacket(packet);
            } else if (packet.shouldOpenScreen()) {
                mc.m_91152_(new UnifiedDeployScreen(packet));
            }
        }
    }

    public static void handleSquadSync(SquadSyncPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        ClientTacticalState.updateSquads(packet.getSquads(), packet.getMySquadId(), packet.getCommanderNames(), packet.getTeammateNameTagDistance());
        Screen screen = mc.f_91080_;
        if (screen instanceof SquadScreen) {
            SquadScreen screen2 = (SquadScreen)screen;
            screen2.updateSquads(packet.getSquads(), packet.getMySquadId());
        } else {
            screen = mc.f_91080_;
            if (screen instanceof UnifiedDeployScreen) {
                UnifiedDeployScreen screen3 = (UnifiedDeployScreen)screen;
                screen3.updateSquads(packet.getSquads(), packet.getMySquadId());
                screen3.updateSelectedClass(packet.getSelectedClassId());
            }
        }
    }

    public static void handleGameStateResponse(GameStateResponsePacket packet) {
        ClientGameState.setCurrentMapFolder(packet.getMapFolder());
        ClientGameState.setObjectiveMode(packet.getObjectiveMode());
        try {
            ClientGameState.setCurrentPhase(GamePhase.valueOf(packet.getPhaseName()));
        }
        catch (IllegalArgumentException illegalArgumentException) {
            // empty catch block
        }
        String playerTeam = packet.getPlayerTeam();
        ClientGameState.setPlayerTeam(playerTeam != null && !playerTeam.isEmpty() ? playerTeam : null);
        String playerFaction = packet.getPlayerFaction();
        ClientGameState.setPlayerFactionId(playerFaction != null && !playerFaction.isEmpty() ? playerFaction : null);
        String phaseName = packet.getPhaseName();
        String activeTeam = packet.getActiveTeam();
        String myTeam = ClientGameState.getPlayerTeam();
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        if ("LOBBY".equals(phaseName) || "WAITING_FOR_PLAYERS".equals(phaseName)) {
            mc.m_91152_(new HubScreen(0, "\u7b49\u5f85\u7ba1\u7406\u5458\u5f00\u59cb\u4e0b\u4e00\u5c40"));
            return;
        }
        if ("MAP_VOTE".equals(phaseName)) {
            if (!(mc.f_91080_ instanceof MapVoteScreen)) {
                mc.m_91152_(new MapVoteScreen());
            }
            return;
        }
        if ("TEAM_SELECT".equals(phaseName)) {
            if (!(mc.f_91080_ instanceof TeamSelectionScreen)) {
                mc.m_91152_(new TeamSelectionScreen());
            }
            return;
        }
        if ("DEFEND_COMMANDER_VOTE".equals(phaseName) && "DEFEND".equals(myTeam) || "ATTACK_COMMANDER_VOTE".equals(phaseName) && "ATTACK".equals(myTeam)) {
            return;
        }
        if ("DEFEND_FACTION_SELECT".equals(phaseName) && "DEFEND".equals(myTeam) || "ATTACK_FACTION_SELECT".equals(phaseName) && "ATTACK".equals(myTeam)) {
            return;
        }
        if (ClientGameState.canOpenTeamSelection() && (myTeam == null || myTeam.isEmpty()) && !(mc.f_91080_ instanceof TeamSelectionScreen)) {
            mc.m_91152_(new TeamSelectionScreen());
        }
    }

    public static void handleCommanderSkillSync(CommanderSkillSyncPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        AuraTipRadialController.updateSkills(packet.isCommander(), packet.getCooldowns(), packet.getSkills());
        Screen screen = mc.f_91080_;
        if (screen instanceof CommanderSkillScreen) {
            CommanderSkillScreen screen2 = (CommanderSkillScreen)screen;
            screen2.updateData(packet.isCommander(), packet.getCooldowns(), packet.getSkills());
        }
    }

    public static void handleOpenHubScreen(OpenHubScreenPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        ClientGameState.setPlayerTeam(null);
        ClientGameState.setPlayerFactionId(null);
        Screen screen = mc.f_91080_;
        if (screen instanceof HubScreen) {
            HubScreen hub = (HubScreen)screen;
            hub.updateStatus(packet.onlineCount, packet.statusMessage);
            return;
        }
        mc.m_91152_(new HubScreen(packet.onlineCount, packet.statusMessage));
    }

    public static void handleClassSelectTimer(ClassSelectTimerPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof ClassSelectScreen) {
            ClassSelectScreen screen2 = (ClassSelectScreen)screen;
            screen2.updateTimer(packet.getTimeRemaining(), packet.getOpponentTimeRemaining(), packet.getSelectedFactionId(), packet.isCommander());
        }
    }

    public static void handleEquipZoneSync(EquipZoneSyncPacket packet) {
        ClientEquipZones.setZones(packet.getZones());
    }

    public static void handleBattleTimer(BattleTimerPacket packet) {
        ClientGameState.setBattleTimeRemaining(packet.getRemainingSeconds());
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen screen2 = (UnifiedDeployScreen)screen;
            screen2.updateBattleTimer();
        }
    }

    public static void handleMapVoteState(MapVoteStatePacket packet) {
        MapVoteScreen.update(packet);
    }

    public static void handleOpenMapVoteScreen() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null && !(mc.f_91080_ instanceof MapVoteScreen)) {
            mc.m_91152_(new MapVoteScreen());
        }
    }

    public static void handleTeamSelectState(TeamSelectStatePacket packet) {
        TeamSelectionScreen.updateTeamState(packet);
    }

    public static void handleMatchStats(MatchStatsSyncPacket packet) {
        MatchScoreboardScreen.updateStats(packet);
    }

    public static void handleGovernanceState(GovernanceStatePacket packet) {
        ClientGovernanceState.update(packet);
        MatchScoreboardScreen.updateGovernance(packet);
        Minecraft mc = Minecraft.m_91087_();
        Screen screen = mc.f_91080_;
        if (screen instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen screen2 = (UnifiedDeployScreen)screen;
            screen2.updateGovernance(packet);
        } else {
            MatchScoreboardScreen scoreboard;
            screen = mc.f_91080_;
            if (screen instanceof MatchScoreboardScreen && (screen = (scoreboard = (MatchScoreboardScreen)screen).getParent()) instanceof UnifiedDeployScreen) {
                UnifiedDeployScreen parent = (UnifiedDeployScreen)screen;
                parent.updateGovernance(packet);
            }
        }
    }

    public static void handleRoundEnd(RoundEndPacket packet) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ != null) {
            mc.m_91152_(new RoundEndScreen(packet.winner, packet.displaySeconds, packet.winnerShowName, packet.loserShowName, packet.attackTickets, packet.defendTickets, packet.resultLevel, packet.attackerTimeout));
        }
    }

    public static void handleAudioCue(AudioCuePacket packet) {
        ClientFormationAudioManager.handle(packet);
    }

    public static void handleTutorialSync(byte action, String stepId, int index, int total, boolean allowSkip) {
        if (action == 1) {
            TutorialOverlay.clear();
            return;
        }
        TutorialOverlay.show(stepId, index, total, allowSkip);
    }

    public static void handlePartyList(PartyListPacket packet) {
        PartyScreen.update(packet);
    }

    public static void handleVehicleSupplySync(VehicleSupplySyncPacket packet) {
        VehicleWheelController.updateSupply(packet);
    }

    public static void handleMountProgress(MountProgressPacket packet) {
        VehicleInteractionKind kind = VehicleInteractionState.kind();
        if (!packet.active()) {
            if (packet.progress() >= 0.999f) {
                VehicleInteractionState.setMount(1.0f);
            } else if (kind == VehicleInteractionKind.MOUNT) {
                VehicleInteractionState.clear();
            }
            return;
        }
        float local = VehicleInteractionState.progress();
        float merged = local < 0.0f ? packet.progress() : Math.max(local, packet.progress());
        VehicleInteractionState.setMount(merged);
    }

    public static void handleFobSupplySync(FobSupplySyncPacket packet) {
        FobSupplyHud.update(packet);
    }

    public static void handleOutpostSupplySync(OutpostSupplySyncPacket packet) {
        OutpostSupplyHud.update(packet);
    }

    public static void handleFortificationCatalog(FortificationCatalogPacket packet) {
        AuraTipRadialController.updateFortifications(packet.entries());
    }

    public static void handleFortificationPreview(FortificationPreviewPacket packet) {
        FortificationPlacementController.begin(packet);
    }

    public static void handleFortificationProgress(FortificationProgressPacket packet) {
        FortificationPlacementController.clear();
        FortificationProgressHud.update(packet);
    }
}

