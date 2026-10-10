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

public class TeamSelectStatePacket {
    public final int attackCount;
    public final int defendCount;
    public final int remainingSeconds;
    public final long endGameTime;
    public final boolean active;
    public final String myTeam;
    public final String lockedTeam;
    public final String attackFactionImage;
    public final String defendFactionImage;

    public TeamSelectStatePacket(int attackCount, int defendCount, int remainingSeconds, long endGameTime, boolean active, String myTeam, String lockedTeam, String attackFactionImage, String defendFactionImage) {
        this.attackCount = attackCount;
        this.defendCount = defendCount;
        this.remainingSeconds = remainingSeconds;
        this.endGameTime = endGameTime;
        this.active = active;
        this.myTeam = myTeam;
        this.lockedTeam = lockedTeam;
        this.attackFactionImage = attackFactionImage;
        this.defendFactionImage = defendFactionImage;
    }

    public static TeamSelectStatePacket read(FriendlyByteBuf buf) {
        return new TeamSelectStatePacket(buf.m_130242_(), buf.m_130242_(), buf.m_130242_(), buf.readLong(), buf.readBoolean(), buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean() ? buf.m_130277_() : null, buf.readBoolean() ? buf.m_130277_() : null);
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130130_(this.attackCount);
        buf.m_130130_(this.defendCount);
        buf.m_130130_(this.remainingSeconds);
        buf.writeLong(this.endGameTime);
        buf.writeBoolean(this.active);
        buf.writeBoolean(this.myTeam != null);
        if (this.myTeam != null) {
            buf.m_130070_(this.myTeam);
        }
        buf.writeBoolean(this.lockedTeam != null);
        if (this.lockedTeam != null) {
            buf.m_130070_(this.lockedTeam);
        }
        buf.writeBoolean(this.attackFactionImage != null);
        if (this.attackFactionImage != null) {
            buf.m_130070_(this.attackFactionImage);
        }
        buf.writeBoolean(this.defendFactionImage != null);
        if (this.defendFactionImage != null) {
            buf.m_130070_(this.defendFactionImage);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers").getMethod("handleTeamSelectState", TeamSelectStatePacket.class).invoke(null, this);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

