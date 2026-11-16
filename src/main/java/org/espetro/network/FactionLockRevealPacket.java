package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 编制指定展示页数据包（S→C）。
 * <p>地图已指定某一方编制时（例如两栖图的 {@code assault_faction}），跳过该方编制投票，
 * 只展示 5 秒“已自动选择唯一可用编制”。</p>
 */
public class FactionLockRevealPacket {

    /** 被指定的队伍：{@code ATTACK} / {@code DEFEND}。 */
    private final String team;
    /** 编制显示名。 */
    private final String factionDisplayName;
    /** 编制选择图（资源包路径或客户端本地相对路径）。 */
    private final String factionSelectionImage;
    /** 展示秒数。 */
    private final int durationSeconds;

    public FactionLockRevealPacket(String team, String factionDisplayName,
                                   String factionSelectionImage, int durationSeconds) {
        this.team = team == null ? "" : team;
        this.factionDisplayName = factionDisplayName == null ? "" : factionDisplayName;
        this.factionSelectionImage = factionSelectionImage == null ? "" : factionSelectionImage;
        this.durationSeconds = durationSeconds;
    }

    public String getTeam() {
        return team;
    }

    public String getFactionDisplayName() {
        return factionDisplayName;
    }

    public String getFactionSelectionImage() {
        return factionSelectionImage;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }

    public static FactionLockRevealPacket read(FriendlyByteBuf buf) {
        String team = buf.readUtf();
        String name = buf.readUtf();
        String image = buf.readUtf();
        int duration = buf.readVarInt();
        return new FactionLockRevealPacket(team, name, image, duration);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(team);
        buf.writeUtf(factionDisplayName);
        buf.writeUtf(factionSelectionImage);
        buf.writeVarInt(durationSeconds);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleFactionLockReveal", FactionLockRevealPacket.class)
                    .invoke(null, this);
            } catch (Exception ignored) {
                // 专用服务器没有客户端处理器，忽略。
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
