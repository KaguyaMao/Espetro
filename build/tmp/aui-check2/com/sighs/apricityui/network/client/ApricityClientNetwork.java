/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 */
package com.sighs.apricityui.network.client;

import com.sighs.apricityui.network.api.NetworkManager;
import com.sighs.apricityui.network.packet.CloseContainerRequestPacket;
import com.sighs.apricityui.network.packet.OpenScreenRequestPacket;
import net.minecraft.client.Minecraft;

public final class ApricityClientNetwork {
    private ApricityClientNetwork() {
    }

    public static void requestOpenScreen(String path) {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null) {
            return;
        }
        NetworkManager.sendToServer(new OpenScreenRequestPacket(path));
    }

    public static void requestCloseScreen() {
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft.f_91074_ == null) {
            return;
        }
        NetworkManager.sendToServer(new CloseContainerRequestPacket());
    }
}

