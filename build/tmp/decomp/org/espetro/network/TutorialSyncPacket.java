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

public class TutorialSyncPacket {
    public static final byte ACTION_SHOW = 0;
    public static final byte ACTION_CLEAR = 1;
    private final byte action;
    private final String stepId;
    private final int index;
    private final int total;
    private final boolean allowSkip;

    public TutorialSyncPacket(byte action, String stepId, int index, int total, boolean allowSkip) {
        this.action = action;
        this.stepId = stepId == null ? "" : stepId;
        this.index = index;
        this.total = total;
        this.allowSkip = allowSkip;
    }

    public static TutorialSyncPacket show(String stepId, int index, int total, boolean allowSkip) {
        return new TutorialSyncPacket(0, stepId, index, total, allowSkip);
    }

    public static TutorialSyncPacket clear() {
        return new TutorialSyncPacket(1, "", 0, 0, false);
    }

    public static TutorialSyncPacket read(FriendlyByteBuf buf) {
        byte action = buf.readByte();
        String stepId = buf.m_130277_();
        int index = buf.m_130242_();
        int total = buf.m_130242_();
        boolean allowSkip = buf.readBoolean();
        return new TutorialSyncPacket(action, stepId, index, total, allowSkip);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeByte(this.action);
        buf.m_130070_(this.stepId);
        buf.m_130130_(this.index);
        buf.m_130130_(this.total);
        buf.writeBoolean(this.allowSkip);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleTutorialSync", Byte.TYPE, String.class, Integer.TYPE, Integer.TYPE, Boolean.TYPE).invoke(null, this.action, this.stepId, this.index, this.total, this.allowSkip);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public byte getAction() {
        return this.action;
    }

    public String getStepId() {
        return this.stepId;
    }

    public int getIndex() {
        return this.index;
    }

    public int getTotal() {
        return this.total;
    }

    public boolean isAllowSkip() {
        return this.allowSkip;
    }
}

