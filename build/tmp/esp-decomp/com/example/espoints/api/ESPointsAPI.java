/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.api;

import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.config.TeamfightJsonConfig;
import com.example.espoints.util.EspetroTeamBridge;

public final class ESPointsAPI {
    private static volatile String cachedObjectiveMode = "";

    private ESPointsAPI() {
    }

    public static void refreshCachedMode(String mode) {
        cachedObjectiveMode = mode == null ? "" : mode.trim().toUpperCase();
    }

    public static String getObjectiveMode() {
        if (cachedObjectiveMode != null && !cachedObjectiveMode.isBlank()) {
            return cachedObjectiveMode;
        }
        return CapturePointManager.getInstance().isRaasFrontline() ? "RAAS" : "AAS";
    }

    public static boolean isRaasFrontline() {
        return CapturePointManager.getInstance().isRaasFrontline() || "RAAS".equalsIgnoreCase(ESPointsAPI.getObjectiveMode());
    }

    public static String getSelectedLaneId() {
        String lane = TeamfightJsonConfig.getLastSelectedLaneId();
        return lane == null ? "" : lane;
    }

    public static String teamDisplayName(String team) {
        return EspetroTeamBridge.displayName(team);
    }

    public static boolean isOperationModeRunning() {
        return CapturePointManager.getInstance().isOperationModeRunning();
    }
}

