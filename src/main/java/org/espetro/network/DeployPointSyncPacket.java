package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

/** 轻量同步当前接收玩家可见的 HAB、Rally 与前哨列表，不主动打开界面。 */
public class DeployPointSyncPacket {

    private final List<UnifiedDeployScreenPacket.BastionItem> deployPoints;

    public DeployPointSyncPacket(List<UnifiedDeployScreenPacket.BastionItem> deployPoints) {
        this.deployPoints = deployPoints == null ? new ArrayList<>() : deployPoints;
    }

    public DeployPointSyncPacket(FriendlyByteBuf buf) {
        int size = buf.readVarInt();
        this.deployPoints = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            deployPoints.add(new UnifiedDeployScreenPacket.BastionItem(buf));
        }
    }

    public static DeployPointSyncPacket read(FriendlyByteBuf buf) {
        return new DeployPointSyncPacket(buf);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(deployPoints.size());
        for (UnifiedDeployScreenPacket.BastionItem deployPoint : deployPoints) {
            deployPoint.write(buf);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketContexts.handleOnClient(ctx, "handleDeployPointSync", DeployPointSyncPacket.class, this);
    }

    public List<UnifiedDeployScreenPacket.BastionItem> getDeployPoints() {
        return deployPoints;
    }
}
