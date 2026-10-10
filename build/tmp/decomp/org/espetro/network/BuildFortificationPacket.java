/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.bastion.FortificationManager;

public class BuildFortificationPacket {
    private final String fortId;

    public BuildFortificationPacket(String fortId) {
        this.fortId = fortId == null ? "" : fortId;
    }

    public static BuildFortificationPacket read(FriendlyByteBuf buf) {
        return new BuildFortificationPacket(buf.m_130136_(128));
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130072_(this.fortId, 128);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            String err = FortificationManager.getInstance().place(player, this.fortId);
            if (err != null) {
                player.m_213846_(Component.m_237113_(err));
            } else {
                player.m_213846_(Component.m_237113_("\u00a7e\u5df2\u8fdb\u5165\u5de5\u4e8b\u9884\u89c8\uff1a\u5de6\u952e\u786e\u8ba4\uff0c\u53f3\u952e\u53d6\u6d88\u3002"));
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

