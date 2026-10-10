package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 队伍分配提示页数据包：通知客户端显示「您已被分配至 进攻方/防守方」提示页。
 */
public class TeamAssignPacket {

    /** 玩家被分配到的队伍 "ATTACK" 或 "DEFEND"。 */
    private final String team;
    /** 提示页显示秒数。 */
    private final int durationSeconds;

    public TeamAssignPacket(String team, int durationSeconds) {
        this.team = team == null ? "" : team;
        this.durationSeconds = durationSeconds;
    }

    public static TeamAssignPacket read(FriendlyByteBuf buf) {
        String team = buf.readUtf();
        int durationSeconds = buf.readVarInt();
        return new TeamAssignPacket(team, durationSeconds);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(team);
        buf.writeVarInt(durationSeconds);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleTeamAssign", TeamAssignPacket.class)
                    .invoke(null, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getTeam() {
        return team;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }
}
