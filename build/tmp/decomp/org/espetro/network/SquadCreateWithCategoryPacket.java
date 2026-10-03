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
import org.espetro.network.NetworkManager;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.SquadManager;
import org.espetro.team.TeamPackManager;

public class SquadCreateWithCategoryPacket {
    private final String squadName;
    private final String categoryId;

    public SquadCreateWithCategoryPacket(String squadName, String categoryId) {
        this.squadName = squadName == null ? "" : squadName;
        this.categoryId = categoryId == null ? "none" : categoryId;
    }

    public static SquadCreateWithCategoryPacket read(FriendlyByteBuf buf) {
        return new SquadCreateWithCategoryPacket(buf.m_130277_(), buf.m_130277_());
    }

    public void write(FriendlyByteBuf buf) {
        buf.m_130070_(this.squadName);
        buf.m_130070_(this.categoryId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ((NetworkEvent.Context)ctx.get()).getSender();
            if (player == null) {
                return;
            }
            SquadManager.ActionResult result = SquadManager.getInstance().createSquad(player, this.squadName, this.categoryId);
            player.m_213846_(Component.m_237113_((result.success ? "\u00a7a" : "\u00a7c") + result.message));
            if (result.success && result.team != null) {
                TeamPackManager.getInstance().reconcileTeam(result.team);
                TeamPackManager.getInstance().handleSquadLeaderTransition(player, result.team, -1, false, result.team, SquadManager.getInstance().getPlayerSquadId(player.m_20148_()), true);
                NetworkManager.sendCommanderSkillSync(player);
                NetworkManager.syncSquadsToTeam(result.team);
                NetworkManager.broadcastClassCounts(result.team, ClassCountManager.getInstance().getPlayerFaction(player.m_20148_()));
                NetworkManager.broadcastMatchStats(PlayerMatchStatsManager.getInstance());
                NetworkManager.syncUnifiedDeployScreen(player, -1);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}

