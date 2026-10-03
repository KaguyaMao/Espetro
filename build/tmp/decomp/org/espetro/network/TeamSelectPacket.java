/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.network.NetworkEvent$Context
 */
package org.espetro.network;

import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.Espetro;
import org.espetro.network.NetworkManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;

public class TeamSelectPacket {
    private final String team;

    public TeamSelectPacket(String team) {
        this.team = team;
    }

    public static TeamSelectPacket read(FriendlyByteBuf buf) {
        return new TeamSelectPacket(buf.m_130277_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.team);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            if (!"ATTACK".equals(this.team) && !"DEFEND".equals(this.team)) {
                player.m_213846_(Component.m_237113_("\u00a7c\u65e0\u6548\u7684\u9635\u8425\u9009\u62e9\u3002"));
                return;
            }
            GameStateManager gsm = GameStateManager.getInstance();
            if (gsm.isMidGameJoiner(player.m_20148_())) {
                if (ClassCountManager.getInstance().getPlayerTeam(player.m_20148_()) != null) {
                    player.m_213846_(Component.m_237113_("\u00a7c\u589e\u63f4\u9635\u8425\u5df2\u7ecf\u786e\u5b9a\uff0c\u4e0d\u80fd\u518d\u6b21\u9009\u62e9\u3002"));
                    return;
                }
                gsm.onMidGameTeamSelected(player, this.team);
                NetworkManager.sendSquadSync(player);
                Espetro.LOGGER.info("\u73a9\u5bb6 {} \u6218\u5c40\u52a0\u5165 {} \u9635\u8425", (Object)player.m_7755_().getString(), (Object)this.team);
            } else {
                if (gsm.getCurrentPhase() != GamePhase.TEAM_SELECT) {
                    player.m_213846_(Component.m_237113_("\u00a7c\u5f53\u524d\u4e0d\u80fd\u9009\u62e9\u9635\u8425\u3002"));
                    return;
                }
                gsm.onTeamSelected(player, this.team);
                NetworkManager.sendSquadSync(player);
                Espetro.LOGGER.info("\u73a9\u5bb6 {} \u9009\u62e9\u4e86 {} \u9635\u8425", (Object)player.m_7755_().getString(), (Object)this.team);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

