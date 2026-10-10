/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.tutorial;

import org.espetro.team.GamePhase;

public enum TutorialStep {
    WELCOME("welcome"),
    HUB("hub"),
    MAP_VOTE("map_vote"),
    MAP_LOADING("map_loading"),
    TEAM_SELECT("team_select"),
    COMMANDER_VOTE("commander_vote"),
    FACTION_SELECT("faction_select"),
    FACTION_REVEAL("faction_reveal"),
    DEPLOY_PANEL("deploy_panel"),
    SQUAD("squad"),
    CLASS_SELECT("class_select"),
    DEPLOY_POINT("deploy_point"),
    KEYS_RADIAL("keys_radial"),
    RADIO_RALLY("radio_rally"),
    LOGISTICS_FOB("logistics_fob"),
    COMMANDER_SKILLS("commander_skills"),
    OUTPOST("outpost"),
    BATTLE("battle"),
    RESPAWN("respawn"),
    SCORE_ROUND("score_round"),
    MID_JOIN("mid_join");

    private final String id;

    private TutorialStep(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }

    public String titleKey() {
        return "tutorial.step." + this.id + ".title";
    }

    public String bodyKey() {
        return "tutorial.step." + this.id + ".body";
    }

    public int ordinalIndex() {
        return this.ordinal() + 1;
    }

    public static int totalCount() {
        return TutorialStep.values().length;
    }

    public static TutorialStep byId(String id) {
        if (id == null || id.isBlank()) {
            return null;
        }
        for (TutorialStep step : TutorialStep.values()) {
            if (!step.id.equalsIgnoreCase(id)) continue;
            return step;
        }
        return null;
    }

    public static TutorialStep primaryForPhase(GamePhase phase) {
        if (phase == null) {
            return null;
        }
        return switch (phase) {
            default -> throw new IncompatibleClassChangeError();
            case GamePhase.LOBBY, GamePhase.WAITING_FOR_PLAYERS -> HUB;
            case GamePhase.MAP_VOTE -> MAP_VOTE;
            case GamePhase.MAP_LOADING -> MAP_LOADING;
            case GamePhase.TEAM_SELECT -> TEAM_SELECT;
            case GamePhase.DEFEND_COMMANDER_VOTE, GamePhase.ATTACK_COMMANDER_VOTE -> COMMANDER_VOTE;
            case GamePhase.DEFEND_FACTION_SELECT, GamePhase.ATTACK_FACTION_SELECT -> FACTION_SELECT;
            case GamePhase.FACTION_REVEAL -> FACTION_REVEAL;
            case GamePhase.DEPLOYING -> DEPLOY_PANEL;
            case GamePhase.BATTLE -> BATTLE;
            case GamePhase.ROUND_END -> SCORE_ROUND;
            case GamePhase.CLEANUP -> null;
        };
    }
}

