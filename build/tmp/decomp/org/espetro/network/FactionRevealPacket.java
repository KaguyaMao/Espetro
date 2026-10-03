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

public class FactionRevealPacket {
    private final String attackFactionName;
    private final String defendFactionName;
    private final String attackFactionImage;
    private final String defendFactionImage;
    private final int durationSeconds;

    public FactionRevealPacket(String attackFactionName, String defendFactionName, String attackFactionImage, String defendFactionImage, int durationSeconds) {
        this.attackFactionName = attackFactionName == null ? "" : attackFactionName;
        this.defendFactionName = defendFactionName == null ? "" : defendFactionName;
        this.attackFactionImage = attackFactionImage;
        this.defendFactionImage = defendFactionImage;
        this.durationSeconds = durationSeconds;
    }

    public static FactionRevealPacket read(FriendlyByteBuf buf) {
        String attackFactionName = buf.m_130277_();
        String defendFactionName = buf.m_130277_();
        String attackFactionImage = buf.readBoolean() ? buf.m_130277_() : null;
        String defendFactionImage = buf.readBoolean() ? buf.m_130277_() : null;
        int durationSeconds = buf.m_130242_();
        return new FactionRevealPacket(attackFactionName, defendFactionName, attackFactionImage, defendFactionImage, durationSeconds);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.attackFactionName);
        buf.m_130070_(this.defendFactionName);
        buf.writeBoolean(this.attackFactionImage != null);
        if (this.attackFactionImage != null) {
            buf.m_130070_(this.attackFactionImage);
        }
        buf.writeBoolean(this.defendFactionImage != null);
        if (this.defendFactionImage != null) {
            buf.m_130070_(this.defendFactionImage);
        }
        buf.m_130130_(this.durationSeconds);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleFactionReveal", FactionRevealPacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getAttackFactionName() {
        return this.attackFactionName;
    }

    public String getDefendFactionName() {
        return this.defendFactionName;
    }

    public String getAttackFactionImage() {
        return this.attackFactionImage;
    }

    public String getDefendFactionImage() {
        return this.defendFactionImage;
    }

    public int getDurationSeconds() {
        return this.durationSeconds;
    }
}

