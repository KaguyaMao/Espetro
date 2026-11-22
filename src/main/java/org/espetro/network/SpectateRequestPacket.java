package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.GameStateManager;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

/** 客户端请求进入观战（中途加入面板的"进入观战"按钮，C→S）。 */
public class SpectateRequestPacket {

    public SpectateRequestPacket() {
    }

    public static SpectateRequestPacket read(FriendlyByteBuf buf) {
        return new SpectateRequestPacket();
    }

    public void write(FriendlyByteBuf buf) {
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnServer(ctx, player -> {
            GameStateManager.getInstance().selfSetObserver(player);
            NetworkManager.sendCloseModScreens(player);
        });
    }
}
