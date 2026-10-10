/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.runtime;

import org.espetro.bastion.BastionManager;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.network.NetworkManager;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.TeamPackManager;
import org.espetro.vehicle.VehicleManager;

public final class ServerRuntimeMaintenance {
    private static final int INVALID_STATE_SCAN_INTERVAL_TICKS = 100;
    private static final int DEPLOY_POINT_SYNC_INTERVAL_TICKS = 100;
    private static final ServerRuntimeMaintenance INSTANCE = new ServerRuntimeMaintenance();
    private long tickCounter;

    private ServerRuntimeMaintenance() {
    }

    public static ServerRuntimeMaintenance getInstance() {
        return INSTANCE;
    }

    public void reset() {
        this.tickCounter = 0L;
        NetworkManager.clearQueuedFullScreens();
    }

    public void onServerTick() {
        BattlefieldWorldManager.getInstance().onServerTick();
        NetworkManager.drainQueuedFullScreens();
        PlayerMatchStatsManager.getInstance().onServerTick();
        TeamPackManager.getInstance().onServerTick();
        VehicleManager.getInstance().processInitialVehicleDeployments();
        long tick = this.tickCounter++;
        if (tick % 100L == 20L) {
            NetworkManager.refreshWaitingDeployPoints();
        }
        if (tick % 100L == 0L) {
            BastionManager.getInstance().removeInvalidBastions();
        } else if (tick % 100L == 33L) {
            TeamPackManager.getInstance().cleanupInvalidTeamPacks();
        } else if (tick % 100L == 66L) {
            VehicleManager.getInstance().removeInvalidVehicles();
        }
    }
}

