/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.TeamSelectionScreen;
import org.espetro.network.NetworkManager;

public class TeamSelectionGui {
    public static void open() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ == null) {
            mc.m_91152_(new TeamSelectionScreen());
        }
    }

    public static void selectTeam(String team) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ != null) {
            NetworkManager.sendFactionSelect(team);
            ClientGameState.setPlayerTeam(team);
            Screen screen = mc.f_91080_;
            if (screen instanceof TeamSelectionScreen) {
                TeamSelectionScreen screen2 = (TeamSelectionScreen)screen;
                TeamSelectionScreen.markLocalSelection(team);
                screen2.refreshSelectionBordersPublic();
            }
        }
    }
}

