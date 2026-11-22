package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.team.SquadManager;
import org.espetro.network.core.PacketHandling;

import java.util.function.Supplier;

/**
 * Extended create with category. Existing CREATE in SquadActionPacket remains compatible
 * (defaults to none). This packet is preferred when category is selected.
 */
public class SquadCreateWithCategoryPacket {

    private final String squadName;
    private final String categoryId;

    public SquadCreateWithCategoryPacket(String squadName, String categoryId) {
        this.squadName = squadName == null ? "" : squadName;
        this.categoryId = categoryId == null ? "none" : categoryId;
    }

    public static SquadCreateWithCategoryPacket read(FriendlyByteBuf buf) {
        return new SquadCreateWithCategoryPacket(buf.readUtf(), buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(squadName);
        buf.writeUtf(categoryId);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        PacketHandling.handleOnServer(ctx, player -> {
            var result = SquadManager.getInstance().createSquad(player, squadName, categoryId);
            player.sendSystemMessage(net.minecraft.network.chat.Component.literal(
                (result.success ? "§a" : "§c") + result.message));
            if (result.success && result.team != null) {
                org.espetro.team.TeamPackManager.getInstance().reconcileTeam(result.team);
                // 创建小队即成为队长：推送 usableBy 含 squad_leader 的技能列表
                org.espetro.team.TeamPackManager.getInstance().handleSquadLeaderTransition(
                    player,
                    result.team,
                    org.espetro.team.SquadManager.NO_SQUAD,
                    false,
                    result.team,
                    org.espetro.team.SquadManager.getInstance().getPlayerSquadId(player.getUUID()),
                    true
                );
                MatchBroadcasts.sendCommanderSkillSync(player);
                SquadSync.syncSquadsToTeam(result.team);
                DeployScreenSync.broadcastClassCounts(result.team,
                    org.espetro.team.ClassCountManager.getInstance().getPlayerFaction(player.getUUID()));
                MatchBroadcasts.broadcastMatchStats(
                    org.espetro.stats.PlayerMatchStatsManager.getInstance());
                DeployScreenSync.syncUnifiedDeployScreen(player, -1);
            }
        });
    }
}
