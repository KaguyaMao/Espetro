/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;

public record FortificationProgressPacket(String displayName, int progress, int required, boolean building) {
    public static FortificationProgressPacket read(FriendlyByteBuf buf) {
        return new FortificationProgressPacket(buf.m_130136_(128), buf.m_130242_(), buf.m_130242_(), buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130072_(this.displayName == null ? "" : this.displayName, 128);
        buf.m_130130_(Math.max(0, this.progress));
        buf.m_130130_(Math.max(1, this.required));
        buf.writeBoolean(this.building);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleFortificationProgress", FortificationProgressPacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException e) {
                Espetro.LOGGER.error("\u5904\u7406\u5de5\u4e8b\u8fdb\u5ea6\u5931\u8d25", (Throwable)e);
            }
        });
        context.setPacketHandled(true);
    }
}

