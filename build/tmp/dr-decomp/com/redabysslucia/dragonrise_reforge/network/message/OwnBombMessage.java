/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package com.redabysslucia.dragonrise_reforge.network.message;

import com.redabysslucia.dragonrise_reforge.events.ClientEvent;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

public class OwnBombMessage {
    private final UUID bombUuid;

    public OwnBombMessage(UUID bombUuid) {
        this.bombUuid = bombUuid;
    }

    public static void encode(OwnBombMessage message, FriendlyByteBuf buffer) {
        buffer.m_130077_(message.bombUuid);
    }

    public static OwnBombMessage decode(FriendlyByteBuf buffer) {
        return new OwnBombMessage(buffer.m_130259_());
    }

    public static void handle(OwnBombMessage message, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientEvent.onOwnBombMessage(message.bombUuid));
        ctx.get().setPacketHandled(true);
    }
}

