/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 */
package org.espetro.mapconfig;

import com.google.gson.JsonObject;

public final class GameSettingsSnapshot {
    public final int teamSelectSeconds;
    public final int deployTimeoutSeconds;
    public final int deployWarningSeconds;
    public final int defendCommanderVoteSeconds;
    public final int attackCommanderVoteSeconds;
    public final int defendFactionSelectSeconds;
    public final int attackFactionSelectSeconds;
    public final int factionPoolSize;
    public final int factionRevealSeconds;
    public final int roundEndSeconds;
    public final int respawnInvincibilityTicks;
    public final double mainBaseInvulnerabilityRadius;
    public final int classSwitchCooldownSeconds;
    public final double teammateNameTagDistance;
    public final double waitingY;
    public final int initialAttackTroops;
    public final int initialDefendTroops;
    public final int commanderDeathPenalty;
    public final int playerStamina;
    public final int sprintCostPerSecond;
    public final int jumpCost;
    public final int regenDelaySeconds;
    public final int regenPerSecond;
    public final int fullRecoverySeconds;
    public final int impeachmentVoteSeconds;
    public final int impeachmentCooldownSeconds;
    public final int commanderVacancySeconds;
    public final int battleTimeoutSeconds;
    public final boolean deprecatedRequiredPlayersPresent;
    public final boolean deprecatedTutorialPresent;

    public GameSettingsSnapshot(int teamSelectSeconds, int deployTimeoutSeconds, int deployWarningSeconds, int defendCommanderVoteSeconds, int attackCommanderVoteSeconds, int defendFactionSelectSeconds, int attackFactionSelectSeconds, int factionPoolSize, int factionRevealSeconds, int roundEndSeconds, int respawnInvincibilityTicks, double mainBaseInvulnerabilityRadius, int classSwitchCooldownSeconds, double teammateNameTagDistance, double waitingY, int initialAttackTroops, int initialDefendTroops, int commanderDeathPenalty, int playerStamina, int sprintCostPerSecond, int jumpCost, int regenDelaySeconds, int regenPerSecond, int fullRecoverySeconds, int impeachmentVoteSeconds, int impeachmentCooldownSeconds, int commanderVacancySeconds, int battleTimeoutSeconds, boolean deprecatedRequiredPlayersPresent, boolean deprecatedTutorialPresent) {
        this.teamSelectSeconds = teamSelectSeconds;
        this.deployTimeoutSeconds = deployTimeoutSeconds;
        this.deployWarningSeconds = deployWarningSeconds;
        this.defendCommanderVoteSeconds = defendCommanderVoteSeconds;
        this.attackCommanderVoteSeconds = attackCommanderVoteSeconds;
        this.defendFactionSelectSeconds = defendFactionSelectSeconds;
        this.attackFactionSelectSeconds = attackFactionSelectSeconds;
        this.factionPoolSize = factionPoolSize;
        this.factionRevealSeconds = factionRevealSeconds;
        this.roundEndSeconds = roundEndSeconds;
        this.respawnInvincibilityTicks = respawnInvincibilityTicks;
        this.mainBaseInvulnerabilityRadius = mainBaseInvulnerabilityRadius;
        this.classSwitchCooldownSeconds = classSwitchCooldownSeconds;
        this.teammateNameTagDistance = teammateNameTagDistance;
        this.waitingY = waitingY;
        this.initialAttackTroops = initialAttackTroops;
        this.initialDefendTroops = initialDefendTroops;
        this.commanderDeathPenalty = commanderDeathPenalty;
        this.playerStamina = playerStamina;
        this.sprintCostPerSecond = sprintCostPerSecond;
        this.jumpCost = jumpCost;
        this.regenDelaySeconds = regenDelaySeconds;
        this.regenPerSecond = regenPerSecond;
        this.fullRecoverySeconds = fullRecoverySeconds;
        this.impeachmentVoteSeconds = impeachmentVoteSeconds;
        this.impeachmentCooldownSeconds = impeachmentCooldownSeconds;
        this.commanderVacancySeconds = commanderVacancySeconds;
        this.battleTimeoutSeconds = battleTimeoutSeconds;
        this.deprecatedRequiredPlayersPresent = deprecatedRequiredPlayersPresent;
        this.deprecatedTutorialPresent = deprecatedTutorialPresent;
    }

    public static GameSettingsSnapshot defaults() {
        return new GameSettingsSnapshot(60, 240, 30, 20, 20, 30, 30, 6, 5, 10, 60, 150.0, 0, 10.0, 200.0, 280, 1200, 2, 100, 5, 15, 2, 2, 12, 60, 600, 180, 3600, false, false);
    }

    public static GameSettingsSnapshot parse(JsonObject root) {
        GameSettingsSnapshot d = GameSettingsSnapshot.defaults();
        boolean deprecatedRequired = false;
        boolean deprecatedTutorial = false;
        int teamSelect = d.teamSelectSeconds;
        int deployTimeout = d.deployTimeoutSeconds;
        int deployWarning = d.deployWarningSeconds;
        int defCmd = d.defendCommanderVoteSeconds;
        int atkCmd = d.attackCommanderVoteSeconds;
        int defFac = d.defendFactionSelectSeconds;
        int atkFac = d.attackFactionSelectSeconds;
        int pool = d.factionPoolSize;
        int reveal = d.factionRevealSeconds;
        int roundEnd = d.roundEndSeconds;
        int invuln = d.respawnInvincibilityTicks;
        double mainBaseInvulnerabilityRadius = d.mainBaseInvulnerabilityRadius;
        int classSwitchCooldown = d.classSwitchCooldownSeconds;
        double nameTag = d.teammateNameTagDistance;
        double waitingY = d.waitingY;
        int atkTroops = d.initialAttackTroops;
        int defTroops = d.initialDefendTroops;
        int cmdPenalty = d.commanderDeathPenalty;
        int stamina = d.playerStamina;
        int sprint = d.sprintCostPerSecond;
        int jump = d.jumpCost;
        int regenDelay = d.regenDelaySeconds;
        int regen = d.regenPerSecond;
        int fullRecovery = d.fullRecoverySeconds;
        int impeachVote = d.impeachmentVoteSeconds;
        int impeachCd = d.impeachmentCooldownSeconds;
        int vacancy = d.commanderVacancySeconds;
        int battleTimeout = d.battleTimeoutSeconds;
        if (root.has("game") && root.get("game").isJsonObject()) {
            JsonObject game = root.getAsJsonObject("game");
            if (game.has("required_players")) {
                deprecatedRequired = true;
            }
            teamSelect = GameSettingsSnapshot.getInt(game, "team_select_seconds", teamSelect);
            deployTimeout = GameSettingsSnapshot.getInt(game, "deploy_timeout_seconds", deployTimeout);
            deployWarning = GameSettingsSnapshot.getInt(game, "deploy_warning_seconds", deployWarning);
            defCmd = GameSettingsSnapshot.getInt(game, "defend_commander_vote_seconds", defCmd);
            atkCmd = GameSettingsSnapshot.getInt(game, "attack_commander_vote_seconds", atkCmd);
            defFac = GameSettingsSnapshot.getInt(game, "defend_faction_select_seconds", defFac);
            atkFac = GameSettingsSnapshot.getInt(game, "attack_faction_select_seconds", atkFac);
            pool = GameSettingsSnapshot.getInt(game, "faction_pool_size", pool);
            reveal = GameSettingsSnapshot.getInt(game, "faction_reveal_seconds", reveal);
            roundEnd = GameSettingsSnapshot.getInt(game, "round_end_seconds", roundEnd);
            invuln = GameSettingsSnapshot.getInt(game, "respawn_invincibility_ticks", invuln);
            mainBaseInvulnerabilityRadius = GameSettingsSnapshot.getDouble(game, "main_base_invulnerability_radius", mainBaseInvulnerabilityRadius);
            classSwitchCooldown = GameSettingsSnapshot.getInt(game, "class_switch_cooldown_seconds", classSwitchCooldown);
            nameTag = GameSettingsSnapshot.getDouble(game, "teammate_name_tag_distance", nameTag);
            waitingY = GameSettingsSnapshot.getDouble(game, "waiting_y", waitingY);
            battleTimeout = GameSettingsSnapshot.getInt(game, "battle_timeout_seconds", battleTimeout);
        }
        if (root.has("troops") && root.get("troops").isJsonObject()) {
            JsonObject troops = root.getAsJsonObject("troops");
            atkTroops = GameSettingsSnapshot.getInt(troops, "initial_attack", atkTroops);
            defTroops = GameSettingsSnapshot.getInt(troops, "initial_defend", defTroops);
            cmdPenalty = GameSettingsSnapshot.getInt(troops, "commander_death_penalty", cmdPenalty);
        }
        if (root.has("stamina") && root.get("stamina").isJsonObject()) {
            JsonObject st = root.getAsJsonObject("stamina");
            int configured = GameSettingsSnapshot.getInt(st, "player_stamina", stamina);
            stamina = configured == -1 ? -1 : Math.max(0, configured);
            sprint = Math.max(0, GameSettingsSnapshot.getInt(st, "sprint_cost_per_second", sprint));
            jump = Math.max(0, GameSettingsSnapshot.getInt(st, "jump_cost", jump));
            regenDelay = Math.max(0, GameSettingsSnapshot.getInt(st, "regen_delay_seconds", regenDelay));
            regen = Math.max(0, GameSettingsSnapshot.getInt(st, "regen_per_second", regen));
            fullRecovery = Math.max(0, GameSettingsSnapshot.getInt(st, "full_recovery_seconds", fullRecovery));
        }
        if (root.has("governance") && root.get("governance").isJsonObject()) {
            JsonObject g = root.getAsJsonObject("governance");
            impeachVote = GameSettingsSnapshot.getInt(g, "impeachment_vote_seconds", impeachVote);
            impeachCd = GameSettingsSnapshot.getInt(g, "impeachment_cooldown_seconds", impeachCd);
            vacancy = GameSettingsSnapshot.getInt(g, "commander_vacancy_seconds", vacancy);
        }
        if (root.has("tutorial")) {
            deprecatedTutorial = true;
        }
        return new GameSettingsSnapshot(Math.max(1, teamSelect), Math.max(1, deployTimeout), Math.max(0, deployWarning), Math.max(1, defCmd), Math.max(1, atkCmd), Math.max(1, defFac), Math.max(1, atkFac), Math.max(2, pool), Math.max(1, reveal), Math.max(1, roundEnd), Math.max(0, invuln), Math.max(0.0, mainBaseInvulnerabilityRadius), Math.max(0, classSwitchCooldown), nameTag, waitingY, Math.max(0, atkTroops), Math.max(0, defTroops), Math.max(0, cmdPenalty), stamina, sprint, jump, regenDelay, regen, fullRecovery, Math.max(1, impeachVote), Math.max(0, impeachCd), Math.max(1, vacancy), Math.max(1, battleTimeout), deprecatedRequired, deprecatedTutorial);
    }

    private static int getInt(JsonObject obj, String key, int def) {
        return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsInt() : def;
    }

    private static double getDouble(JsonObject obj, String key, double def) {
        return obj.has(key) && obj.get(key).isJsonPrimitive() ? obj.get(key).getAsDouble() : def;
    }
}

