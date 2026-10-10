/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.client.gui;

import java.util.List;
import java.util.Objects;
import java.util.UUID;
import javax.annotation.Nullable;
import org.espetro.network.GovernanceStatePacket;

public final class ClientGovernanceState {
    private static GovernanceStatePacket latest = new GovernanceStatePacket(List.of());
    private static long receivedAtMs;

    private ClientGovernanceState() {
    }

    public static void update(GovernanceStatePacket packet) {
        latest = packet == null ? new GovernanceStatePacket(List.of()) : packet;
        receivedAtMs = System.currentTimeMillis();
    }

    public static void clear() {
        latest = new GovernanceStatePacket(List.of());
        receivedAtMs = 0L;
    }

    public static GovernanceStatePacket get() {
        return latest;
    }

    public static long getReceivedAtMs() {
        return receivedAtMs;
    }

    @Nullable
    public static GovernanceStatePacket.TeamState forTeam(@Nullable String team) {
        if (team == null) {
            return null;
        }
        for (GovernanceStatePacket.TeamState state : ClientGovernanceState.latest.teams) {
            if (!team.equals(state.team)) continue;
            return state;
        }
        return null;
    }

    public static int secondsLeft(GovernanceStatePacket.TeamState state) {
        if (state == null) {
            return 0;
        }
        int elapsed = (int)Math.max(0L, (System.currentTimeMillis() - receivedAtMs) / 1000L);
        return Math.max(0, state.remainingSeconds - elapsed);
    }

    public static int voteCount(GovernanceStatePacket.TeamState state, UUID candidate) {
        if (state == null || candidate == null || state.voteCounts == null) {
            return 0;
        }
        return state.voteCounts.getOrDefault(candidate.toString(), 0);
    }

    public static boolean isMyVote(GovernanceStatePacket.TeamState state, UUID candidate) {
        return state != null && candidate != null && Objects.equals(state.myVote, candidate);
    }
}

