/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.bastion.FortificationManager;

public record FortificationPlacementPacket(Action action, UUID token, BlockPos anchor, Direction facing) {
    public static FortificationPlacementPacket read(FriendlyByteBuf buf) {
        return new FortificationPlacementPacket(buf.m_130066_(Action.class), buf.m_130259_(), buf.m_130135_(), buf.m_130066_(Direction.class));
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130068_(this.action);
        buf.m_130077_(this.token);
        buf.m_130064_(this.anchor);
        buf.m_130068_(this.facing);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            String error;
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            String string = error = this.action == Action.CANCEL ? FortificationManager.getInstance().cancelPreview(player, this.token) : FortificationManager.getInstance().confirmPreview(player, this.token, this.anchor, this.facing);
            if (error != null) {
                player.m_213846_(Component.m_237113_(error));
            }
        });
        context.setPacketHandled(true);
    }

    public static enum Action {
        CONFIRM,
        CANCEL;

    }
}

