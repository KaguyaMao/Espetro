package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.RedeployService;

import java.util.function.Supplier;

/**
 * 客户端请求「重新部署」（C→S）。
 * 客户端蓝色菜单里的红色按钮经二次确认后发出；服务端会立刻击杀玩家，
 * 对战阶段按阵亡规则扣除兵力，部署阶段不扣。
 */
public final class RedeployRequestPacket {

    public RedeployRequestPacket() {
    }

    public static RedeployRequestPacket read(FriendlyByteBuf buf) {
        return new RedeployRequestPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;
            RedeployService.requestRedeploy(player);
        });
        context.setPacketHandled(true);
    }
}
