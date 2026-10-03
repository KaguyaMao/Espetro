/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 */
package org.espetro.team;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import org.espetro.client.gui.ClientGameState;
import org.espetro.mapconfig.BattlefieldContext;

public final class TeamDisplayNames {
    private TeamDisplayNames() {
    }

    public static boolean isSymmetricMode() {
        return TeamDisplayNames.isSymmetricMode(TeamDisplayNames.resolveObjectiveMode());
    }

    public static boolean isSymmetricMode(String objectiveMode) {
        return objectiveMode != null && "RAAS".equalsIgnoreCase(objectiveMode.trim());
    }

    public static String displayName(String team) {
        return TeamDisplayNames.displayName(team, TeamDisplayNames.isSymmetricMode());
    }

    public static String displayName(String team, boolean symmetric) {
        if (TeamDisplayNames.isAttack(team)) {
            return symmetric ? "\u9635\u8425A" : "\u8fdb\u653b\u65b9";
        }
        return symmetric ? "\u9635\u8425B" : "\u9632\u5b88\u65b9";
    }

    public static String shortLabel(String team) {
        return TeamDisplayNames.shortLabel(team, TeamDisplayNames.isSymmetricMode());
    }

    public static String shortLabel(String team, boolean symmetric) {
        if (TeamDisplayNames.isAttack(team)) {
            return symmetric ? "A" : "\u8fdb\u653b";
        }
        return symmetric ? "B" : "\u9632\u5b88";
    }

    public static String prefix(String team) {
        return TeamDisplayNames.isAttack(team) ? "\u00a7c" : "\u00a79";
    }

    public static String coloredDisplayName(String team) {
        return TeamDisplayNames.prefix(team) + TeamDisplayNames.displayName(team);
    }

    static String resolveObjectiveMode() {
        String serverMode = BattlefieldContext.getObjectiveMode();
        if (serverMode != null && !serverMode.isBlank()) {
            return serverMode;
        }
        String clientMode = TeamDisplayNames.clientObjectiveMode();
        return clientMode == null ? "" : clientMode;
    }

    private static boolean isAttack(String team) {
        return team != null && "ATTACK".equalsIgnoreCase(team.trim());
    }

    private static String clientObjectiveMode() {
        try {
            return (String)DistExecutor.unsafeCallWhenOn((Dist)Dist.CLIENT, () -> ClientMode::get);
        }
        catch (Throwable ignored) {
            return "";
        }
    }

    private static final class ClientMode {
        private ClientMode() {
        }

        static String get() {
            String mode = ClientGameState.getObjectiveMode();
            return mode == null ? "" : mode;
        }
    }
}

