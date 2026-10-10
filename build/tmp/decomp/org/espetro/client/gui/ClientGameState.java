/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import java.util.Objects;
import net.minecraft.client.Minecraft;
import org.espetro.client.gui.PartyScreen;
import org.espetro.client.gui.TroopCountOverlay;
import org.espetro.network.NetworkManager;
import org.espetro.team.GamePhase;

public class ClientGameState {
    private static GamePhase currentPhase = GamePhase.LOBBY;
    private static String playerFactionId = null;
    private static String playerTeam = null;
    private static int battleTimerAnchorSeconds = -1;
    private static long battleTimerAnchorMs;
    private static String currentMapFolder;
    private static String objectiveMode;

    public static void setCurrentPhase(GamePhase phase) {
        currentPhase = phase;
    }

    public static GamePhase getCurrentPhase() {
        return currentPhase;
    }

    public static void setCurrentMapFolder(String mapFolder) {
        currentMapFolder = mapFolder == null || mapFolder.isBlank() ? null : mapFolder.trim();
    }

    public static String getCurrentMapFolder() {
        return currentMapFolder;
    }

    public static void setObjectiveMode(String mode) {
        String next;
        String string = next = mode == null || mode.isBlank() ? null : mode.trim();
        if (Objects.equals(objectiveMode, next)) {
            return;
        }
        objectiveMode = next;
        TroopCountOverlay.onObjectiveModeChanged();
    }

    public static String getObjectiveMode() {
        return objectiveMode;
    }

    public static void setPlayerFactionId(String factionId) {
        playerFactionId = factionId;
    }

    public static String getPlayerFactionId() {
        return playerFactionId;
    }

    public static void setPlayerTeam(String team) {
        playerTeam = team;
        TroopCountOverlay.onTeamChanged(team);
    }

    public static String getPlayerTeam() {
        return playerTeam;
    }

    public static void setBattleTimeRemaining(int seconds) {
        battleTimerAnchorSeconds = seconds < 0 ? -1 : seconds;
        battleTimerAnchorMs = System.currentTimeMillis();
    }

    public static int getBattleTimeRemaining() {
        return ClientGameState.calculateAnchoredRemaining(battleTimerAnchorSeconds, battleTimerAnchorMs, System.currentTimeMillis());
    }

    static int calculateAnchoredRemaining(int anchorSeconds, long anchorMs, long nowMs) {
        if (anchorSeconds < 0) {
            return -1;
        }
        long elapsedMs = Math.max(0L, nowMs - anchorMs);
        long elapsedSeconds = elapsedMs / 1000L;
        return (int)Math.max(0L, (long)anchorSeconds - elapsedSeconds);
    }

    public static boolean canOpenTeamSelection() {
        return currentPhase == GamePhase.TEAM_SELECT || currentPhase == GamePhase.DEPLOYING || currentPhase == GamePhase.BATTLE || currentPhase.isCommanderVotePhase() || currentPhase.isFactionSelectPhase() || currentPhase == GamePhase.FACTION_REVEAL;
    }

    public static boolean canOpenClassSelection() {
        return currentPhase == GamePhase.DEPLOYING || currentPhase == GamePhase.BATTLE;
    }

    public static boolean canOpenCommanderSkill() {
        return currentPhase == GamePhase.DEPLOYING || currentPhase == GamePhase.BATTLE;
    }

    public static void tryOpenJKeyScreen() {
        if (ClientGameState.canOpenClassSelection()) {
            String playerTeam = ClientGameState.getPlayerTeam();
            if (playerTeam == null) {
                NetworkManager.requestGameState();
            } else {
                String factionId = ClientGameState.getPlayerFactionId();
                NetworkManager.requestClassSelection(factionId);
            }
        } else if (currentPhase.isLobbyLike()) {
            NetworkManager.requestPartyList();
            Minecraft mc = Minecraft.m_91087_();
            if (mc.f_91074_ != null && !(mc.f_91080_ instanceof PartyScreen)) {
                mc.m_91152_(new PartyScreen());
            }
        }
    }

    static {
        currentMapFolder = null;
        objectiveMode = null;
    }
}

