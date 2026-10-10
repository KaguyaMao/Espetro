/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

public record FortificationPreviewPacket(UUID token, String fortId, String displayName, List<Offset> occupiedOffsets) {
    private static final int MAX_BLOCKS = 256;

    public FortificationPreviewPacket {
        fortId = fortId == null ? "" : fortId;
        displayName = displayName == null ? fortId : displayName;
        occupiedOffsets = occupiedOffsets == null ? List.of() : List.copyOf(occupiedOffsets);
    }

    public static FortificationPreviewPacket read(FriendlyByteBuf buf) {
        UUID token = buf.m_130259_();
        String id = buf.m_130136_(64);
        String name = buf.m_130136_(128);
        int size = Math.min(256, Math.max(0, buf.m_130242_()));
        ArrayList<Offset> offsets = new ArrayList<Offset>(size);
        for (int i = 0; i < size; ++i) {
            offsets.add(new Offset(buf.readByte(), buf.readByte(), buf.readByte()));
        }
        return new FortificationPreviewPacket(token, id, name, offsets);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130077_(this.token);
        buf.m_130072_(this.fortId, 64);
        buf.m_130072_(this.displayName, 128);
        int size = Math.min(256, this.occupiedOffsets.size());
        buf.m_130130_(size);
        for (int i = 0; i < size; ++i) {
            Offset offset = this.occupiedOffsets.get(i);
            buf.writeByte(offset.x());
            buf.writeByte(offset.y());
            buf.writeByte(offset.z());
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleFortificationPreview", FortificationPreviewPacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException e) {
                Espetro.LOGGER.error("\u5904\u7406\u5de5\u4e8b\u9884\u89c8\u5931\u8d25", (Throwable)e);
            }
        });
        context.setPacketHandled(true);
    }

    public record Offset(int x, int y, int z) {
    }
}

