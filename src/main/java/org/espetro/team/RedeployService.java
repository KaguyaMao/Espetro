package org.espetro.team;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.mapconfig.BattlefieldContext;

/**
 * 玩家主动「重新部署」：立刻阵亡，随后走统一的死亡流程（复活 → 重选部署点/职业）。
 *
 * <p>票数规则（与用户需求一致）：
 * <ul>
 *   <li>部署阶段（{@link GamePhase#DEPLOYING}）：不扣兵力；</li>
 *   <li>对战阶段（{@link GamePhase#BATTLE}）：按阵亡规则扣除本职业兵力（含指挥官额外惩罚）。</li>
 * </ul>
 *
 * <p>对战阶段在战场地图内时，扣票由 {@link TroopCountManager#onPlayerDeath} 的死亡事件自动完成；
 * 玩家不在战场地图内（例如对战期间留在主城）时该事件不会触发，这里显式补扣一次，避免换地图规避扣票。
 * 两条路径互斥，保证同一名玩家只扣一次。
 */
public final class RedeployService {

    private RedeployService() {
    }

    /**
     * 处理一次主动重新部署请求（客户端红色按钮 → 二次确认后发出）。
     *
     * @param player 发起请求的服务端玩家
     */
    public static void requestRedeploy(ServerPlayer player) {
        if (player == null) {
            return;
        }

        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.DEPLOYING && phase != GamePhase.BATTLE) {
            player.sendSystemMessage(Component.literal("§c当前阶段无法重新部署。"));
            return;
        }

        if (player.isDeadOrDying()) {
            player.sendSystemMessage(Component.literal("§c你已阵亡，正在等待部署点选择。"));
            return;
        }

        // 已在等待选择部署点（刚阵亡或刚重新部署过）→ 不重复击杀
        if (BastionManager.getInstance().isWaitingForBastion(player.getUUID())) {
            player.sendSystemMessage(Component.literal("§c你已在等待选择部署点！"));
            return;
        }

        boolean battle = phase == GamePhase.BATTLE;
        boolean onBattlefield = BattlefieldContext.isActiveBattlefield(player.serverLevel());

        // 对战阶段但不在战场地图内：死亡事件不会扣票，这里补扣
        boolean charged = false;
        if (battle && !onBattlefield) {
            charged = TroopCountManager.applyDeathTicketCost(player);
        }

        String team = Espetro.getPlayerTeam(player);
        if (team != null) {
            String msg = battle
                ? "§e[重新部署] " + player.getName().getString() + " 已重新部署"
                : "§a[重新部署] " + player.getName().getString() + " 已重新部署（部署阶段不扣兵力）";
            Espetro.broadcastToTeam(team, msg);
        }

        player.sendSystemMessage(Component.literal(battle
            ? "§e正在重新部署：立刻阵亡，并按职业扣除兵力。"
            : "§a正在重新部署：部署阶段不扣除兵力。"));

        if (battle) {
            Espetro.LOGGER.info("玩家 {} 主动重新部署（对战阶段）：立刻阵亡{}，剩余兵力 攻={} 守={}",
                player.getName().getString(),
                onBattlefield ? "（死亡事件扣票）" : (charged ? "（补扣票）" : "（无队伍/未选职业，未扣票）"),
                TroopCountManager.getInstance().getAttackTroops(),
                TroopCountManager.getInstance().getDefendTroops());
        } else {
            Espetro.LOGGER.info("玩家 {} 主动重新部署（部署阶段，不扣票）",
                player.getName().getString());
        }

        // 立刻击杀：无论玩家在哪个维度/位置
        player.kill();
    }
}
