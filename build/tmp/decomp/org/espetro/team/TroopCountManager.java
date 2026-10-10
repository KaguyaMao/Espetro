/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.event.entity.living.LivingDeathEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package org.espetro.team;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.NetworkManager;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassCountManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;
import org.espetro.team.TeamDisplayNames;

@Mod.EventBusSubscriber(modid="espetro", bus=Mod.EventBusSubscriber.Bus.FORGE)
public class TroopCountManager {
    public static final String SCOREBOARD_OBJECTIVE = "troop_count";
    public static final String ATTACK_TROOPS_NAME = "attack_troops";
    public static final String DEFEND_TROOPS_NAME = "defend_troops";
    private static TroopCountManager INSTANCE;

    private TroopCountManager() {
        INSTANCE = this;
    }

    public static TroopCountManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new TroopCountManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new TroopCountManager();
    }

    private Objective getOrCreateObjective(Scoreboard scoreboard) {
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.m_83436_(SCOREBOARD_OBJECTIVE, ObjectiveCriteria.f_83588_, Component.m_237113_("\u00a76\u53cc\u65b9\u5175\u529b"), ObjectiveCriteria.RenderType.INTEGER);
        }
        return objective;
    }

    private Scoreboard getScoreboard() {
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return null;
        }
        return server.m_129896_();
    }

    public int getAttackTroops() {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return 0;
        }
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            return 0;
        }
        Score score = scoreboard.m_83471_(ATTACK_TROOPS_NAME, objective);
        return score.m_83400_();
    }

    public int getDefendTroops() {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return 0;
        }
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            return 0;
        }
        Score score = scoreboard.m_83471_(DEFEND_TROOPS_NAME, objective);
        return score.m_83400_();
    }

    public void setAttackTroops(int value) {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = this.getOrCreateObjective(scoreboard);
        Score score = scoreboard.m_83471_(ATTACK_TROOPS_NAME, objective);
        score.m_83402_(Math.max(0, value));
        Espetro.LOGGER.info("\u653b\u65b9\u5175\u529b\u8bbe\u7f6e\u4e3a: {}", (Object)value);
        this.syncToClients();
    }

    public void setDefendTroops(int value) {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = this.getOrCreateObjective(scoreboard);
        Score score = scoreboard.m_83471_(DEFEND_TROOPS_NAME, objective);
        score.m_83402_(Math.max(0, value));
        Espetro.LOGGER.info("\u5b88\u65b9\u5175\u529b\u8bbe\u7f6e\u4e3a: {}", (Object)value);
        this.syncToClients();
    }

    public void modifyAttackTroops(int delta) {
        int current = this.getAttackTroops();
        this.setAttackTroops(current + delta);
        if (delta <= 0 || TeamDisplayNames.isSymmetricMode()) {
            return;
        }
        boolean fromEsPoints = false;
        for (StackTraceElement frame : Thread.currentThread().getStackTrace()) {
            if (!frame.getClassName().contains("CapturePointManager")) continue;
            fromEsPoints = true;
            break;
        }
        if (!fromEsPoints) {
            return;
        }
        MinecraftServer server = Espetro.getServer();
        if (server == null) {
            return;
        }
        CommandSourceStack cmdSrc = server.m_129893_().m_81324_();
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            String team = Espetro.getPlayerTeam(player);
            if (team == null) continue;
            String name = player.m_7755_().getString();
            if ("ATTACK".equals(team)) {
                server.m_129892_().m_230957_(cmdSrc, "title " + name + " title {\"text\":\"\u636e\u70b9\u5df2\u5360\u9886\",\"color\":\"green\",\"bold\":true}");
                server.m_129892_().m_230957_(cmdSrc, "playsound minecraft:entity.player.levelup master " + name + " ~ ~ ~ 1.0 1.0");
                continue;
            }
            if (!"DEFEND".equals(team)) continue;
            server.m_129892_().m_230957_(cmdSrc, "title " + name + " title {\"text\":\"\u636e\u70b9\u5931\u5b88\",\"color\":\"red\",\"bold\":true}");
            server.m_129892_().m_230957_(cmdSrc, "title " + name + " subtitle {\"text\":\"" + TeamDisplayNames.displayName("ATTACK") + "\u5df2\u653b\u5360\u672c\u6279\u6b21\u6240\u6709\u636e\u70b9\",\"color\":\"gray\"}");
            server.m_129892_().m_230957_(cmdSrc, "playsound minecraft:block.beacon.deactivate master " + name + " ~ ~ ~ 1.0 1.0");
        }
    }

    public void modifyDefendTroops(int delta) {
        int current = this.getDefendTroops();
        this.setDefendTroops(current + delta);
    }

    private void syncToClients() {
        int attack = this.getAttackTroops();
        int defend = this.getDefendTroops();
        NetworkManager.broadcastTroopCounts(attack, defend);
    }

    public void initializeTroops() {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        int initialAttack = GameConfig.getInitialAttackTroops();
        int initialDefend = GameConfig.getInitialDefendTroops();
        Objective objective = this.getOrCreateObjective(scoreboard);
        Score attackScore = scoreboard.m_83471_(ATTACK_TROOPS_NAME, objective);
        attackScore.m_83402_(initialAttack);
        Score defendScore = scoreboard.m_83471_(DEFEND_TROOPS_NAME, objective);
        defendScore.m_83402_(initialDefend);
        Espetro.LOGGER.info("\u5175\u529b\u7edf\u8ba1\u5df2\u521d\u59cb\u5316: \u653b\u65b9 {} | \u5b88\u65b9 {}", (Object)initialAttack, (Object)initialDefend);
        Espetro.broadcastToAll("\u00a76========================================");
        Espetro.broadcastToAll("\u00a7e\u2694 \u6218\u6597\u5f00\u59cb\uff01" + TeamDisplayNames.displayName("ATTACK") + "\u521d\u59cb\u5175\u529b: \u00a7c" + initialAttack + " \u00a77| \u00a79" + TeamDisplayNames.displayName("DEFEND") + "\u521d\u59cb\u5175\u529b: " + initialDefend + " \u00a7e\u2694");
        Espetro.broadcastToAll("\u00a76========================================");
        this.syncToClients();
    }

    public void resetTroops() {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            return;
        }
        scoreboard.m_83502_(objective);
        Espetro.LOGGER.info("\u5175\u529b\u7edf\u8ba1\u5df2\u91cd\u7f6e");
        NetworkManager.broadcastTroopCounts(0, 0);
    }

    private boolean isCommanderClass(String classId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
        if (kit == null) {
            return false;
        }
        return "\u6307\u6325".equals(kit.role);
    }

    private int getTroopValueForClass(String classId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
        if (kit == null) {
            Espetro.LOGGER.warn("\u672a\u627e\u5230\u804c\u4e1a\u914d\u7f6e: {}, \u4f7f\u7528\u9ed8\u8ba4\u503c1", (Object)classId);
            return 1;
        }
        Espetro.LOGGER.debug("\u804c\u4e1a {} \u7684\u5175\u529b\u6d88\u8017\u503c: {}", (Object)classId, (Object)kit.troopValue);
        return kit.troopValue;
    }

    @SubscribeEvent
    public static void onPlayerDeath(LivingDeathEvent event) {
        LivingEntity livingEntity = event.getEntity();
        if (!(livingEntity instanceof ServerPlayer)) {
            return;
        }
        ServerPlayer player = (ServerPlayer)livingEntity;
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE) {
            return;
        }
        if (!BattlefieldContext.isActiveBattlefield(player.m_284548_())) {
            return;
        }
        PlayerMatchStatsManager.getInstance().onPlayerDeath(player, event.getSource());
        String classId = ClassCountManager.getInstance().getPlayerClass(player.m_20148_());
        String team = Espetro.getPlayerTeam(player);
        if (team == null) {
            return;
        }
        int troopValue = classId != null ? TroopCountManager.getInstance().getTroopValueForClass(classId) : 1;
        boolean isCommander = TroopCountManager.getInstance().isCommanderClass(classId);
        int commandPenalty = isCommander ? GameConfig.getCommanderDeathPenalty() : 0;
        TroopCountManager manager = TroopCountManager.getInstance();
        if ("ATTACK".equals(team)) {
            manager.modifyAttackTroops(-(troopValue + commandPenalty));
            String msg = "\u00a7c\u2620 \u653b\u65b9 [" + player.m_7755_().getString() + "] \u9635\u4ea1\uff01- " + troopValue + " \u5175\u529b";
            if (isCommander) {
                msg = msg + " \u00a7c(\u6307\u6325\u5b98\u989d\u5916 -" + commandPenalty + ")";
            }
            Espetro.broadcastToTeam(team, msg);
            Espetro.LOGGER.info("\u653b\u65b9 {} \u9635\u4ea1(\u6307\u6325\u5b98={})\uff0c\u6263\u9664 {}{} \u5175\u529b\uff0c\u5269\u4f59: {}", new Object[]{player.m_7755_().getString(), isCommander, troopValue, commandPenalty > 0 ? "+" + commandPenalty : "", manager.getAttackTroops()});
        } else {
            manager.modifyDefendTroops(-(troopValue + commandPenalty));
            String msg = "\u00a79\u2620 \u5b88\u65b9 [" + player.m_7755_().getString() + "] \u9635\u4ea1\uff01- " + troopValue + " \u5175\u529b";
            if (isCommander) {
                msg = msg + " \u00a79(\u6307\u6325\u5b98\u989d\u5916 -" + commandPenalty + ")";
            }
            Espetro.broadcastToTeam(team, msg);
            Espetro.LOGGER.info("\u5b88\u65b9 {} \u9635\u4ea1(\u6307\u6325\u5b98={})\uff0c\u6263\u9664 {}{} \u5175\u529b\uff0c\u5269\u4f59: {}", new Object[]{player.m_7755_().getString(), isCommander, troopValue, commandPenalty > 0 ? "+" + commandPenalty : "", manager.getDefendTroops()});
        }
        manager.checkVictoryCondition();
    }

    public void checkVictoryCondition() {
        int attackTroops = this.getAttackTroops();
        int defendTroops = this.getDefendTroops();
        if (attackTroops <= 0) {
            Espetro.LOGGER.info("===== \u9632\u5b88\u65b9\u80dc\u5229 =====");
            GameStateManager.getInstance().endRound("DEFEND");
            return;
        }
        if (defendTroops <= 0) {
            Espetro.LOGGER.info("===== \u8fdb\u653b\u65b9\u80dc\u5229 =====");
            GameStateManager.getInstance().endRound("ATTACK");
            return;
        }
    }
}

