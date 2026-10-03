/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.UnifiedDeployScreen;

public final class TroopCountOverlay {
    private static int attackTroops;
    private static int defendTroops;
    private static boolean visible;
    private static String cachedTeam;
    private static String displayLine;

    private TroopCountOverlay() {
    }

    public static void updateTroopCounts(int attack, int defend) {
        attackTroops = Math.max(0, attack);
        defendTroops = Math.max(0, defend);
        visible = true;
        if (cachedTeam == null) {
            cachedTeam = ClientGameState.getPlayerTeam();
        }
        TroopCountOverlay.rebuildDisplay();
        TroopCountOverlay.notifyDeployScreen();
    }

    public static void hide() {
        visible = false;
        displayLine = null;
        TroopCountOverlay.notifyDeployScreen();
    }

    public static void show() {
        visible = true;
        TroopCountOverlay.rebuildDisplay();
        TroopCountOverlay.notifyDeployScreen();
    }

    public static void onTeamChanged(String team) {
        cachedTeam = team;
        TroopCountOverlay.rebuildDisplay();
        TroopCountOverlay.notifyDeployScreen();
    }

    static void onObjectiveModeChanged() {
        TroopCountOverlay.rebuildDisplay();
        TroopCountOverlay.notifyDeployScreen();
    }

    public static String getDisplayLine() {
        return displayLine;
    }

    public static boolean isVisible() {
        return visible && displayLine != null;
    }

    private static void rebuildDisplay() {
        if (!visible) {
            displayLine = null;
            return;
        }
        String team = cachedTeam;
        if (team == null || team.isBlank()) {
            displayLine = null;
            return;
        }
        boolean attack = "ATTACK".equals(team);
        boolean defend = "DEFEND".equals(team);
        if (!attack && !defend) {
            displayLine = null;
            return;
        }
        int troops = attack ? attackTroops : defendTroops;
        String label = EspetroAuiWidgets.teamPrefix(team) + "\u25a0 " + EspetroAuiWidgets.teamName(team);
        String color = troops > 50 ? "\u00a7a" : (troops > 20 ? "\u00a7e" : "\u00a7c");
        displayLine = label + ": " + color + troops;
    }

    private static void notifyDeployScreen() {
        Screen screen;
        Minecraft mc = Minecraft.m_91087_();
        if (mc != null && (screen = mc.f_91080_) instanceof UnifiedDeployScreen) {
            UnifiedDeployScreen screen2 = (UnifiedDeployScreen)screen;
            screen2.updateTroopLabel(displayLine);
        }
    }
}

