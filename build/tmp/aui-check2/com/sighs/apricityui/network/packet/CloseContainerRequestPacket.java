/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.network.packet;

import com.sighs.apricityui.network.api.INetworkContext;
import com.sighs.apricityui.network.api.INetworkPacket;
import com.sighs.apricityui.network.api.NetworkPacket;
import com.sighs.apricityui.network.api.Side;
import net.minecraft.server.level.ServerPlayer;

@NetworkPacket(modId="apricityui", id="close_container", side=Side.SERVER)
public record CloseContainerRequestPacket() implements INetworkPacket<CloseContainerRequestPacket>
{
    @Override
    public void handle(INetworkContext context) {
        ServerPlayer player = context.sender();
        if (player != null) {
            player.m_6915_();
        }
    }
}

