/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.network.ResupplyCatalogPacket;

public record ResupplyEntryDeltaPacket(UUID token, long actionSeq, long stateRevision, int balance, boolean success, boolean close, String message, ResupplyCatalogPacket.Entry entry) {
    private static final int MAX_MESSAGE = 512;

    public ResupplyEntryDeltaPacket {
        String string = message = message == null ? "" : message;
        if (message.length() > 512) {
            message = message.substring(0, 512);
        }
    }

    public static ResupplyEntryDeltaPacket read(FriendlyByteBuf buf) {
        UUID token = buf.m_130259_();
        long action = buf.readLong();
        long state = buf.readLong();
        int balance = buf.m_130242_();
        boolean success = buf.readBoolean();
        boolean close = buf.readBoolean();
        String message = buf.m_130136_(512);
        ResupplyCatalogPacket.Entry entry = buf.readBoolean() ? ResupplyCatalogPacket.Entry.read(buf) : null;
        return new ResupplyEntryDeltaPacket(token, action, state, balance, success, close, message, entry);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.token);
        buf.writeLong(this.actionSeq);
        buf.writeLong(this.stateRevision);
        buf.m_130130_(Math.max(0, this.balance));
        buf.writeBoolean(this.success);
        buf.writeBoolean(this.close);
        buf.m_130072_(this.message, 512);
        buf.writeBoolean(this.entry != null);
        if (this.entry != null) {
            this.entry.write(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleResupplyDelta", ResupplyEntryDeltaPacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException error) {
                Espetro.LOGGER.error("\u5904\u7406\u8865\u7ed9\u589e\u91cf\u5931\u8d25", (Throwable)error);
            }
        });
        context.setPacketHandled(true);
    }
}

