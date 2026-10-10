/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.example.espoints.hud;

import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.config.TacticalMapConfig;
import com.example.espoints.util.EspetroTeamBridge;
import com.example.espoints.util.ModLogger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class ReinforcementsHUD
implements IGuiOverlay {
    private static final int HUD_HEIGHT = 30;
    private static final int BAR_HEIGHT = 10;
    private static final int TEXT_PADDING = 2;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        if (!this.shouldRender()) {
            return;
        }
        CapturePointManager manager = CapturePointManager.getInstance();
        String attackerTeam = manager.getAttackerTeam();
        String defenderTeam = manager.getDefenderTeam();
        if (attackerTeam == null || defenderTeam == null) {
            return;
        }
        int attackerReinforcements = manager.getTeamReinforcements(attackerTeam);
        int defenderReinforcements = manager.getTeamReinforcements(defenderTeam);
        int minMargin = 30;
        int targetBarWidth = 400;
        int maxPossibleWidth = screenWidth - 2 * minMargin;
        int barWidth = Math.min(targetBarWidth, maxPossibleWidth);
        int barTop = 50;
        int barLeft = (screenWidth - barWidth) / 2;
        int barRight = barLeft + barWidth;
        int halfBarWidth = barWidth / 2;
        String defenderHexColor = (String)TacticalMapConfig.defenderProgressBarColor.get();
        String attackerHexColor = (String)TacticalMapConfig.attackerProgressBarColor.get();
        int defenderColor = ModLogger.hexToColor(defenderHexColor, -16755201);
        int attackerColor = ModLogger.hexToColor(attackerHexColor, -43776);
        int defenderInitialReinforcements = manager.getTeamInitialReinforcements(defenderTeam);
        int defenderBarWidth = 0;
        if (defenderInitialReinforcements > 0) {
            defenderBarWidth = (int)((double)defenderReinforcements / (double)defenderInitialReinforcements * (double)halfBarWidth);
        }
        guiGraphics.m_280509_(barLeft, barTop, barLeft + halfBarWidth, barTop + 10, 0x44000000);
        guiGraphics.m_280509_(barLeft, barTop, barLeft + defenderBarWidth, barTop + 10, defenderColor);
        int attackerInitialReinforcements = manager.getTeamInitialReinforcements(attackerTeam);
        int attackerBarWidth = 0;
        if (attackerInitialReinforcements > 0) {
            attackerBarWidth = (int)((double)attackerReinforcements / (double)attackerInitialReinforcements * (double)halfBarWidth);
        }
        int attackerBarLeft = barLeft + halfBarWidth;
        guiGraphics.m_280509_(attackerBarLeft, barTop, attackerBarLeft + halfBarWidth, barTop + 10, 0x44000000);
        guiGraphics.m_280509_(attackerBarLeft + halfBarWidth - attackerBarWidth, barTop, attackerBarLeft + halfBarWidth, barTop + 10, attackerColor);
        Minecraft minecraft = Minecraft.m_91087_();
        String defenderLabel = EspetroTeamBridge.displayName(defenderTeam);
        String defenderText = defenderLabel + ": " + defenderReinforcements;
        guiGraphics.m_280056_(minecraft.f_91062_, defenderText, barLeft, barTop + 10 + 2, 0xFFFFFF, false);
        String attackerLabel = EspetroTeamBridge.displayName(attackerTeam);
        String attackerText = attackerLabel + ": " + attackerReinforcements;
        guiGraphics.m_280056_(minecraft.f_91062_, attackerText, barRight - minecraft.f_91062_.m_92895_(attackerText), barTop + 10 + 2, 0xFFFFFF, false);
    }

    private boolean shouldRender() {
        return false;
    }
}

