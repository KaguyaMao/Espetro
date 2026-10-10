/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 */
package org.espetro.config;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import net.minecraft.server.MinecraftServer;
import org.espetro.Espetro;
import org.espetro.mapconfig.GameSettingsSnapshot;

public class GameConfig {
    private static final Gson GSON = new Gson();
    private static final String CONFIG_PATH = "espetro/config/game.json";
    private static int requiredPlayers = 20;
    private static int deployTimeoutSeconds = 240;
    private static int deployWarningSeconds = 30;
    private static int defendCommanderVoteSeconds = 20;
    private static int attackCommanderVoteSeconds = 20;
    private static int defendFactionSelectSeconds = 30;
    private static int attackFactionSelectSeconds = 30;
    private static int factionPoolSize = 6;
    private static int respawnInvincibilityTicks = 60;
    private static double mainBaseInvulnerabilityRadius = 150.0;
    private static int classSwitchCooldownSeconds = 0;
    private static double teammateNameTagDistance = 10.0;
    private static double waitingY = 200.0;
    private static int initialAttackTroops = 280;
    private static int initialDefendTroops = 1200;
    private static int commanderDeathPenalty = 2;
    private static int playerStamina = 100;
    private static int sprintStaminaCostPerSecond = 5;
    private static int jumpStaminaCost = 15;
    private static int staminaRegenDelaySeconds = 2;
    private static int staminaRegenPerSecond = 2;
    private static int staminaFullRecoverySeconds = 12;
    private static int teamSelectSeconds = 60;
    private static int factionRevealSeconds = 5;
    private static int roundEndSeconds = 10;
    private static int impeachmentVoteSeconds = 60;
    private static int impeachmentCooldownSeconds = 600;
    private static int commanderVacancySeconds = 180;
    private static int battleTimeoutSeconds = 3600;
    private static boolean tutorialEnabled = true;
    private static boolean tutorialShowOnJoin = false;
    private static boolean tutorialAllowSkip = true;
    private static boolean loaded = false;

    @Deprecated
    public static void loadConfig(MinecraftServer server) {
    }

    private static int getInt(JsonObject obj, String key, int defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsInt();
        }
        return defaultValue;
    }

    private static double getDouble(JsonObject obj, String key, double defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsDouble();
        }
        return defaultValue;
    }

    private static boolean getBoolean(JsonObject obj, String key, boolean defaultValue) {
        if (obj.has(key)) {
            return obj.get(key).getAsBoolean();
        }
        return defaultValue;
    }

    public static boolean isLoaded() {
        return loaded;
    }

    @Deprecated
    public static void reloadConfig(MinecraftServer server) {
        Espetro.LOGGER.warn("GameConfig \u4e0d\u652f\u6301\u70ed\u91cd\u8f7d\uff1b\u5730\u56fe\u53c2\u6570\u4ec5\u5728\u6218\u573a\u6fc0\u6d3b\u65f6\u4ece EsConfig \u5e94\u7528\u3002\u8bf7\u91cd\u542f\u670d\u52a1\u7aef\u3002");
    }

    public static int getRequiredPlayers() {
        return requiredPlayers;
    }

    public static int getDeployTimeoutSeconds() {
        return deployTimeoutSeconds;
    }

    public static int getDeployWarningSeconds() {
        return deployWarningSeconds;
    }

    public static int getDefendCommanderVoteSeconds() {
        return defendCommanderVoteSeconds;
    }

    public static int getAttackCommanderVoteSeconds() {
        return attackCommanderVoteSeconds;
    }

    public static int getDefendFactionSelectSeconds() {
        return defendFactionSelectSeconds;
    }

    public static int getAttackFactionSelectSeconds() {
        return attackFactionSelectSeconds;
    }

    public static int getFactionPoolSize() {
        return factionPoolSize;
    }

    public static int getRespawnInvincibilityTicks() {
        return respawnInvincibilityTicks;
    }

    public static double getMainBaseInvulnerabilityRadius() {
        return mainBaseInvulnerabilityRadius;
    }

    public static int getClassSwitchCooldownSeconds() {
        return classSwitchCooldownSeconds;
    }

    public static double getTeammateNameTagDistance() {
        return teammateNameTagDistance;
    }

    public static double getWaitingY() {
        return waitingY;
    }

    public static int getInitialAttackTroops() {
        return initialAttackTroops;
    }

    public static int getInitialDefendTroops() {
        return initialDefendTroops;
    }

    public static int getCommanderDeathPenalty() {
        return commanderDeathPenalty;
    }

    public static boolean isStaminaEnabled() {
        return playerStamina != -1;
    }

    public static int getPlayerStamina() {
        return playerStamina;
    }

    public static int getSprintStaminaCostPerSecond() {
        return sprintStaminaCostPerSecond;
    }

    public static int getJumpStaminaCost() {
        return jumpStaminaCost;
    }

    public static int getStaminaRegenDelaySeconds() {
        return staminaRegenDelaySeconds;
    }

    public static int getStaminaRegenPerSecond() {
        return staminaRegenPerSecond;
    }

    public static int getStaminaFullRecoverySeconds() {
        return staminaFullRecoverySeconds;
    }

    public static int getTeamSelectSeconds() {
        return teamSelectSeconds;
    }

    public static int getFactionRevealSeconds() {
        return factionRevealSeconds;
    }

    public static int getRoundEndSeconds() {
        return roundEndSeconds;
    }

    public static int getImpeachmentVoteSeconds() {
        return impeachmentVoteSeconds;
    }

    public static int getImpeachmentCooldownSeconds() {
        return impeachmentCooldownSeconds;
    }

    public static int getCommanderVacancySeconds() {
        return commanderVacancySeconds;
    }

    public static int getBattleTimeoutSeconds() {
        return battleTimeoutSeconds;
    }

    public static void applySnapshot(GameSettingsSnapshot s) {
        if (s == null) {
            return;
        }
        teamSelectSeconds = s.teamSelectSeconds;
        deployTimeoutSeconds = s.deployTimeoutSeconds;
        deployWarningSeconds = s.deployWarningSeconds;
        defendCommanderVoteSeconds = s.defendCommanderVoteSeconds;
        attackCommanderVoteSeconds = s.attackCommanderVoteSeconds;
        defendFactionSelectSeconds = s.defendFactionSelectSeconds;
        attackFactionSelectSeconds = s.attackFactionSelectSeconds;
        factionPoolSize = s.factionPoolSize;
        factionRevealSeconds = s.factionRevealSeconds;
        roundEndSeconds = s.roundEndSeconds;
        respawnInvincibilityTicks = s.respawnInvincibilityTicks;
        mainBaseInvulnerabilityRadius = s.mainBaseInvulnerabilityRadius;
        classSwitchCooldownSeconds = s.classSwitchCooldownSeconds;
        teammateNameTagDistance = s.teammateNameTagDistance;
        waitingY = s.waitingY;
        initialAttackTroops = s.initialAttackTroops;
        initialDefendTroops = s.initialDefendTroops;
        commanderDeathPenalty = s.commanderDeathPenalty;
        playerStamina = s.playerStamina;
        sprintStaminaCostPerSecond = s.sprintCostPerSecond;
        jumpStaminaCost = s.jumpCost;
        staminaRegenDelaySeconds = s.regenDelaySeconds;
        staminaRegenPerSecond = s.regenPerSecond;
        staminaFullRecoverySeconds = s.fullRecoverySeconds;
        impeachmentVoteSeconds = s.impeachmentVoteSeconds;
        impeachmentCooldownSeconds = s.impeachmentCooldownSeconds;
        commanderVacancySeconds = s.commanderVacancySeconds;
        battleTimeoutSeconds = s.battleTimeoutSeconds;
        loaded = true;
        Espetro.LOGGER.info("\u5df2\u5e94\u7528\u6d3b\u52a8\u5730\u56fe game \u5feb\u7167: \u9009\u8fb9{}s \u90e8\u7f72{}s \u63ed\u793a{}s \u7ed3\u7b97{}s \u539f\u90e8\u7f72\u70b9\u65e0\u654c\u534a\u5f84{} \u6362\u804c\u51b7\u5374{}s \u5f39\u52be{}s/\u51b7\u5374{}s \u6218\u5c40\u9650\u65f6{}s", new Object[]{teamSelectSeconds, deployTimeoutSeconds, factionRevealSeconds, roundEndSeconds, mainBaseInvulnerabilityRadius, classSwitchCooldownSeconds, impeachmentVoteSeconds, impeachmentCooldownSeconds, battleTimeoutSeconds});
    }

    public static boolean isTutorialEnabled() {
        return tutorialEnabled;
    }

    public static boolean isTutorialShowOnJoin() {
        return tutorialShowOnJoin;
    }

    public static boolean isTutorialAllowSkip() {
        return tutorialAllowSkip;
    }
}

