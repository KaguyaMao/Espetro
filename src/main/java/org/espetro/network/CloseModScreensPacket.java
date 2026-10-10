package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端→客户端：要求立即关闭当前打开的"对局内"模组界面。
 * 用于玩家被管理员设为观察者等被移出对局的场景——服务端已清空其队伍/小队/职业
 * 记录，客户端残留的部署/投票/选职界面必须优雅关闭，避免残留到后续阶段。
 */
public class CloseModScreensPacket {

    public CloseModScreensPacket() {
    }

    public static CloseModScreensPacket read(FriendlyByteBuf buf) {
        return new CloseModScreensPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleCloseModScreens")
                    .invoke(null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}
