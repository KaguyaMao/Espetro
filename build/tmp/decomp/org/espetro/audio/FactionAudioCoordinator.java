/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.audio;

import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.audio.AudioCuePolicy;
import org.espetro.audio.AudioPackId;
import org.espetro.network.AudioCuePacket;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.ClassSelectManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;

public final class FactionAudioCoordinator {
    private FactionAudioCoordinator() {
    }

    public static void broadcastEntry() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            FactionAudioCoordinator.sendEntry(player);
        }
    }

    public static void sendEntry(ServerPlayer player) {
        String team = FactionAudioCoordinator.playerTeam(player);
        if ("ATTACK".equals(team)) {
            FactionAudioCoordinator.send(player, team, AudioCuePacket.Cue.ENTRY_ATTACK);
        } else if ("DEFEND".equals(team)) {
            FactionAudioCoordinator.send(player, team, AudioCuePacket.Cue.ENTRY_DEFEND);
        }
    }

    public static boolean broadcastCapture(String capturingTeam) {
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return false;
        }
        String normalizedCaptor = AudioCuePolicy.normalizeTeam(capturingTeam);
        MinecraftServer server = Espetro.getServer();
        if (server == null || normalizedCaptor == null) {
            return false;
        }
        int sent = 0;
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            AudioCuePacket.Cue cue;
            String team = FactionAudioCoordinator.playerTeam(player);
            if (team == null || !FactionAudioCoordinator.send(player, team, cue = normalizedCaptor.equals(team) ? AudioCuePacket.Cue.CAPTURED : AudioCuePacket.Cue.LOST)) continue;
            ++sent;
        }
        return sent > 0;
    }

    public static boolean broadcastNeutralized(String originalOwnerTeam, String activeAttackingTeam) {
        if (GameStateManager.getInstance().getCurrentPhase() != GamePhase.BATTLE) {
            return false;
        }
        String owner = AudioCuePolicy.normalizeTeam(originalOwnerTeam);
        String attacker = AudioCuePolicy.resolveNeutralizingTeam(originalOwnerTeam, activeAttackingTeam);
        MinecraftServer server = Espetro.getServer();
        if (server == null || owner == null || attacker == null) {
            return false;
        }
        int sent = 0;
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            AudioCuePacket.Cue cue;
            String team = FactionAudioCoordinator.playerTeam(player);
            if (team == null) continue;
            if (owner.equals(team)) {
                cue = AudioCuePacket.Cue.LOSING_POINT;
            } else {
                if (!attacker.equals(team)) continue;
                cue = AudioCuePacket.Cue.CAPTURING_POINT;
            }
            if (!FactionAudioCoordinator.send(player, team, cue)) continue;
            ++sent;
        }
        return sent > 0;
    }

    public static void broadcastRoundResult(String winningTeam) {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        String normalizedWinner = AudioCuePolicy.normalizeTeam(winningTeam);
        if (normalizedWinner == null) {
            for (ServerPlayer player : server.m_6846_().m_11314_()) {
                NetworkManager.sendToPlayer(player, new AudioCuePacket(AudioCuePacket.Cue.STOP, null));
            }
            return;
        }
        boolean easterEgg = AudioCuePolicy.useEasterEgg(ThreadLocalRandom.current().nextDouble());
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String team = FactionAudioCoordinator.playerTeam(player);
            if (team == null) continue;
            AudioCuePacket.Cue cue = normalizedWinner.equals(team) ? (easterEgg ? AudioCuePacket.Cue.VICTORY_EASTER_EGG : AudioCuePacket.Cue.VICTORY) : AudioCuePacket.Cue.DEFEAT;
            FactionAudioCoordinator.send(player, team, cue);
        }
    }

    private static boolean send(ServerPlayer player, String team, AudioCuePacket.Cue cue) {
        String audioPack = FactionAudioCoordinator.audioPackForTeam(team);
        if (audioPack == null) {
            return false;
        }
        NetworkManager.sendToPlayer(player, new AudioCuePacket(cue, audioPack));
        return true;
    }

    private static String audioPackForTeam(String team) {
        String factionId;
        ClassSelectManager selection = ClassSelectManager.getInstance();
        String string = "ATTACK".equals(team) ? selection.getFinalAttackClass() : (factionId = "DEFEND".equals(team) ? selection.getFinalDefendClass() : null);
        if (factionId == null) {
            return null;
        }
        FactionDataLoader.FactionData faction = FactionDataProvider.getOrCreateLoader().getFaction(factionId);
        return faction == null ? null : AudioPackId.normalize(faction.audioPack);
    }

    private static String playerTeam(ServerPlayer player) {
        if (player == null) {
            return null;
        }
        String team = ClassCountManager.getInstance().getPlayerTeam(player.m_20148_());
        if (team == null) {
            team = Espetro.getPlayerTeam(player);
        }
        return AudioCuePolicy.normalizeTeam(team);
    }
}

