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

public final class MountProgressPacket {
    private final boolean active;
    private final float progress;
    private final int delayTicks;

    public MountProgressPacket(boolean active, float progress, int delayTicks) {
        this.active = active;
        this.progress = progress;
        this.delayTicks = delayTicks;
    }

    public boolean active() {
        return this.active;
    }

    public float progress() {
        return this.progress;
    }

    public int delayTicks() {
        return this.delayTicks;
    }

    public static MountProgressPacket read(FriendlyByteBuf buf) {
        return new MountProgressPacket(buf.readBoolean(), buf.readFloat(), buf.m_130242_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(this.active);
        buf.writeFloat(this.progress);
        buf.m_130130_(this.delayTicks);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleMountProgress", MountProgressPacket.class).invoke(null, this);
            }
            catch (ReflectiveOperationException e) {
                Espetro.LOGGER.error("\u5904\u7406\u4e0a\u8f66\u8fdb\u5ea6\u540c\u6b65\u5931\u8d25", (Throwable)e);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

