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
import org.espetro.stamina.StaminaManager;

public class StaminaJumpPacket {
    public static StaminaJumpPacket read(FriendlyByteBuf buf) {
        return new StaminaJumpPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ServerPlayer sender = ctx.get().getSender();
        ctx.get().enqueueWork(() -> {
            if (sender != null) {
                StaminaManager.onPlayerJump(sender);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

