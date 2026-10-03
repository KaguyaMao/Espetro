/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.network.NetworkManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;

public class RequestClassSelectionPacket {
    private final String factionId;

    public RequestClassSelectionPacket(String factionId) {
        this.factionId = factionId;
    }

    public static RequestClassSelectionPacket read(FriendlyByteBuf buf) {
        return new RequestClassSelectionPacket(buf.m_130277_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.factionId != null ? this.factionId : "");
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player;
            if (((NetworkEvent.Context)ctx.get()).getDirection().getReceptionSide().isServer() && (player = ((NetworkEvent.Context)ctx.get()).getSender()) != null) {
                GameStateManager gsm = GameStateManager.getInstance();
                int remaining = gsm.getCurrentPhase() == GamePhase.DEPLOYING ? gsm.getDeployTimeRemainingSeconds() : -1;
                NetworkManager.sendUnifiedDeployScreen(player, remaining);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

