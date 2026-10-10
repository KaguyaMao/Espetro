/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import org.espetro.client.gui.ClientGameState;
import org.espetro.client.gui.TeamSelectionScreen;
import org.espetro.network.NetworkManager;

public class ClassSelectionGui {
    public static void open() {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ == null) {
            if (ClientGameState.getPlayerTeam() == null) {
                TeamSelectionScreen.open();
            } else {
                String factionId = ClientGameState.getPlayerFactionId();
                NetworkManager.requestClassSelection(factionId);
            }
        }
    }

    public static void open(String factionId) {
        Minecraft mc = Minecraft.m_91087_();
        if (mc.f_91080_ == null) {
            NetworkManager.requestClassSelection(factionId);
        }
    }

    public static void selectClass(String factionId, String classId) {
        NetworkManager.sendClassSelect(factionId, classId);
    }

    public static void selectClass(String factionId, String classId, String variantId) {
        NetworkManager.sendClassSelect(factionId, classId, variantId);
    }
}

