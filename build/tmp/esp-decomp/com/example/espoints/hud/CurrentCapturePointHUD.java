/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.client.gui.overlay.ForgeGui
 *  net.minecraftforge.client.gui.overlay.IGuiOverlay
 */
package com.example.espoints.hud;

import com.example.espoints.capturepoint.CapturePoint;
import com.example.espoints.capturepoint.DisplayState;
import com.example.espoints.client.ClientBattleState;
import com.example.espoints.util.EspetroTeamBridge;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CurrentCapturePointHUD
implements IGuiOverlay {
    private static final int BOX_SIZE = 16;
    private static final int PROGRESS_BAR_WIDTH = 80;
    private static final int PROGRESS_BAR_HEIGHT = 5;
    private static final int BOX_TO_PROGRESS_MARGIN = 2;

    public void render(ForgeGui gui, GuiGraphics guiGraphics, float partialTick, int screenWidth, int screenHeight) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91074_ == null) {
            return;
        }
        CapturePoint currentPoint = ClientBattleState.get().pointContaining((Player)mc.f_91074_);
        if (currentPoint == null) {
            return;
        }
        int margin = 10;
        int hudStartX = screenWidth - 16 - margin;
        int hudStartY = 25;
        this.renderCapturePointBox(guiGraphics, currentPoint, hudStartX, hudStartY, mc);
        int progressBarX = screenWidth - 80 - margin;
        this.renderProgressBar(guiGraphics, currentPoint, progressBarX, hudStartY + 16 + 2);
    }

    private void renderCapturePointBox(GuiGraphics guiGraphics, CapturePoint point, int x, int y, Minecraft mc) {
        int color = this.getStatusColor(point.getDisplayState(), point.getCaptorName(), mc);
        guiGraphics.m_280509_(x, y, x + 16, y + 16, color);
        guiGraphics.m_280509_(x, y, x + 16, y + 1, -16777216);
        guiGraphics.m_280509_(x, y, x + 1, y + 16, -16777216);
        guiGraphics.m_280509_(x, y + 16 - 1, x + 16, y + 16, -16777216);
        guiGraphics.m_280509_(x + 16 - 1, y, x + 16, y + 16, -16777216);
        String name = point.getName();
        if (name.length() > 2) {
            name = name.substring(0, 2);
        }
        guiGraphics.m_280488_(mc.f_91062_, name, x + 8 - mc.f_91062_.m_92895_(name) / 2, y + 8 - 4, 0xFFFFFF);
    }

    private void renderProgressBar(GuiGraphics guiGraphics, CapturePoint point, int x, int y) {
        int color = this.getProgressBarColor(point);
        guiGraphics.m_280509_(x, y, x + 80, y + 5, -11184811);
        int progressWidth = (int)((double)point.getProgress() / 100.0 * 80.0);
        guiGraphics.m_280509_(x, y, x + progressWidth, y + 5, color);
    }

    private int getStatusColor(DisplayState displayState, String captorName, Minecraft mc) {
        switch (displayState) {
            case NEUTRAL: {
                return -8355712;
            }
            case CAPTURING_FLAG_SINGLE: {
                return -256;
            }
            case CAPTURING_CONTESTED_MULTI: {
                return Short.MIN_VALUE;
            }
            case CONTESTED_MULTI: {
                return Short.MIN_VALUE;
            }
            case CAPTURING_DOWN: {
                return -65536;
            }
            case CAPTURED: {
                if (mc.f_91074_ != null) {
                    if (this.isFriendlyCapture(mc, captorName)) {
                        return -16711936;
                    }
                    return -65536;
                }
                return -65536;
            }
        }
        return -8355712;
    }

    private boolean isFriendlyCapture(Minecraft mc, String captorName) {
        if (mc.f_91074_ == null) {
            return false;
        }
        return EspetroTeamBridge.isSameTeam(EspetroTeamBridge.getPlayerTeam((Player)mc.f_91074_), captorName);
    }

    private int getProgressBarColor(CapturePoint point) {
        DisplayState displayState = point.getDisplayState();
        switch (displayState) {
            case CAPTURED: {
                Minecraft mc = Minecraft.m_91087_();
                if (this.isFriendlyCapture(mc, point.getCaptorName())) {
                    return -11141291;
                }
                return -43691;
            }
            case CAPTURING_FLAG_SINGLE: 
            case CAPTURING_CONTESTED_MULTI: 
            case CONTESTED_MULTI: 
            case CAPTURING_DOWN: {
                return -22016;
            }
        }
        return -5592406;
    }
}

