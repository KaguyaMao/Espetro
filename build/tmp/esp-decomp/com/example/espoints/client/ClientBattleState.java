/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.entity.player.Player
 */
package com.example.espoints.client;

import com.example.espoints.capturepoint.CapturePoint;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;

public final class ClientBattleState {
    private static final ClientBattleState INSTANCE = new ClientBattleState();
    private final Map<String, CapturePoint> capturePoints = new LinkedHashMap<String, CapturePoint>();
    private boolean operationModeRunning;
    private int currentBatch = 1;
    private int totalBatches;
    private String endBehavior = "terminate";
    private Map<String, String> teamRoles = Map.of();
    private Map<String, Integer> reinforcements = Map.of();
    private Map<String, Integer> initialReinforcements = Map.of();

    private ClientBattleState() {
    }

    public static ClientBattleState get() {
        return INSTANCE;
    }

    public synchronized List<CapturePoint> replaceCapturePoints(List<CapturePoint.SerializableCapturePoint> snapshots) {
        LinkedHashMap<String, CapturePoint> next = new LinkedHashMap<String, CapturePoint>();
        for (CapturePoint.SerializableCapturePoint snapshot : snapshots) {
            CapturePoint point = this.capturePoints.get(snapshot.name);
            if (point == null || point.getBatch() != snapshot.batch || !point.getPos1().equals((Object)snapshot.pos1) || !point.getPos2().equals((Object)snapshot.pos2)) {
                point = new CapturePoint(snapshot.name, snapshot.pos1, snapshot.pos2, snapshot.batch);
            }
            point.restoreFromSerializable(snapshot);
            next.put(snapshot.name, point);
        }
        this.capturePoints.clear();
        this.capturePoints.putAll(next);
        return this.orderedPoints();
    }

    public synchronized List<CapturePoint> points() {
        return this.orderedPoints();
    }

    public synchronized CapturePoint point(String name) {
        return this.capturePoints.get(name);
    }

    public synchronized CapturePoint pointContaining(Player player) {
        if (player == null) {
            return null;
        }
        BlockPos pos = player.m_20183_();
        for (CapturePoint point : this.capturePoints.values()) {
            if (this.operationModeRunning && point.getBatch() != this.currentBatch || !point.isPositionInside(pos)) continue;
            return point;
        }
        return null;
    }

    public synchronized void applyOperation(boolean running, int batch, int batches, String behavior, Map<String, String> roles, Map<String, Integer> current, Map<String, Integer> initial) {
        this.operationModeRunning = running;
        this.currentBatch = Math.max(1, batch);
        this.totalBatches = Math.max(0, batches);
        this.endBehavior = behavior == null ? "terminate" : behavior;
        this.teamRoles = Map.copyOf(roles);
        this.reinforcements = Map.copyOf(current);
        this.initialReinforcements = Map.copyOf(initial);
    }

    public synchronized String attackerTeam() {
        return this.teamForRole("attacker");
    }

    public synchronized String defenderTeam() {
        return this.teamForRole("defender");
    }

    public synchronized int reinforcements(String team) {
        return this.reinforcements.getOrDefault(team, 0);
    }

    public synchronized int initialReinforcements(String team) {
        return this.initialReinforcements.getOrDefault(team, 0);
    }

    public synchronized int currentBatch() {
        return this.currentBatch;
    }

    public synchronized int totalBatches() {
        return this.totalBatches;
    }

    public synchronized void clear() {
        this.capturePoints.clear();
        this.operationModeRunning = false;
        this.currentBatch = 1;
        this.totalBatches = 0;
        this.endBehavior = "terminate";
        this.teamRoles = Map.of();
        this.reinforcements = Map.of();
        this.initialReinforcements = Map.of();
    }

    private String teamForRole(String expectedRole) {
        return this.teamRoles.entrySet().stream().filter(entry -> expectedRole.equals(entry.getValue() == null ? "" : ((String)entry.getValue()).toLowerCase(Locale.ROOT))).map(Map.Entry::getKey).sorted().findFirst().orElse(null);
    }

    private List<CapturePoint> orderedPoints() {
        ArrayList<CapturePoint> result = new ArrayList<CapturePoint>(this.capturePoints.values());
        result.sort(Comparator.comparingInt(CapturePoint::getBatch).thenComparing(CapturePoint::getName));
        return List.copyOf(result);
    }
}

