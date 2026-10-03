/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.example.espoints.api;

import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.hud.MapDisplayMode;
import com.example.espoints.hud.MessagePopup;
import com.example.espoints.hud.TacticalMapHUD;
import com.example.espoints.network.ShowMessagePopupMessage;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class HCRAPI {
    private static final CapturePointManager CAPTURE_POINT_MANAGER = CapturePointManager.getInstance();

    public static void showWinMessage(UUID playerUUID) {
        HCRAPI.showMessage(playerUUID, "\u80dc\u5229", 4000L);
    }

    public static void showLoseMessage(UUID playerUUID) {
        HCRAPI.showMessage(playerUUID, "\u5931\u8d25", 4000L);
    }

    public static void showMessage(UUID playerUUID, String message, long duration) {
        HCRAPI.showMessage(playerUUID, message, duration, 150, 40, -16777216, -1, -5592406, 2);
    }

    public static void showMessage(UUID playerUUID, String message, long duration, int width, int height, int backgroundColor, int textColor, int borderColor, int borderWidth) {
        if (ServerLifecycleHooks.getCurrentServer() != null) {
            for (ServerPlayer player : ServerLifecycleHooks.getCurrentServer().m_6846_().m_11314_()) {
                if (!player.m_20148_().equals(playerUUID)) continue;
                ShowMessagePopupMessage.sendToPlayer(player, playerUUID, message, duration, width, height, backgroundColor, textColor, borderColor, borderWidth);
                break;
            }
        } else {
            MessagePopup.getInstance().showMessage(playerUUID, message, duration, width, height, backgroundColor, textColor, borderColor, borderWidth);
        }
    }

    public static void toggleTacticalMap() {
        TacticalMapHUD.getInstance().toggleMapVisibility();
    }

    public static boolean isTacticalMapVisible() {
        return TacticalMapHUD.getInstance().isMapVisible();
    }

    public static void cycleTacticalMapDisplayMode() {
        TacticalMapHUD.getInstance().cycleDisplayMode();
    }

    public static MapDisplayMode getTacticalMapDisplayMode() {
        return TacticalMapHUD.getInstance().getDisplayMode();
    }

    public static CapturePointManager getCapturePointManager() {
        return CAPTURE_POINT_MANAGER;
    }
}

