/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

public enum GamePhase {
    WAITING_FOR_PLAYERS("\u7b49\u5f85\u73a9\u5bb6\u96c6\u7ed3"),
    LOBBY("\u4e3b\u57ce\u7b49\u5f85"),
    MAP_VOTE("\u5730\u56fe\u6295\u7968"),
    MAP_LOADING("\u5730\u56fe\u52a0\u8f7d"),
    TEAM_SELECT("\u653b\u5b88\u65b9\u9009\u62e9"),
    DEFEND_COMMANDER_VOTE("\u5b88\u65b9\u6307\u6325\u5b98\u6295\u7968"),
    ATTACK_COMMANDER_VOTE("\u653b\u65b9\u6307\u6325\u5b98\u6295\u7968"),
    DEFEND_FACTION_SELECT("\u5b88\u65b9\u7f16\u5236\u9009\u62e9"),
    ATTACK_FACTION_SELECT("\u653b\u65b9\u7f16\u5236\u9009\u62e9"),
    FACTION_REVEAL("\u53cc\u65b9\u7f16\u5236\u63ed\u793a"),
    DEPLOYING("\u90e8\u7f72\u9636\u6bb5"),
    BATTLE("\u5bf9\u6218\u5f00\u59cb"),
    ROUND_END("\u56de\u5408\u7ed3\u7b97"),
    CLEANUP("\u6218\u573a\u6e05\u7406");

    private final String displayName;

    private GamePhase(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }

    public boolean isCommanderVotePhase() {
        return this == DEFEND_COMMANDER_VOTE || this == ATTACK_COMMANDER_VOTE;
    }

    public boolean isFactionSelectPhase() {
        return this == DEFEND_FACTION_SELECT || this == ATTACK_FACTION_SELECT;
    }

    public boolean isLobbyLike() {
        return this == LOBBY || this == WAITING_FOR_PLAYERS;
    }

    public boolean isMatchActive() {
        return this != LOBBY && this != WAITING_FOR_PLAYERS && this != CLEANUP;
    }

    public String getActiveTeam() {
        return switch (this) {
            case DEFEND_COMMANDER_VOTE, DEFEND_FACTION_SELECT -> "DEFEND";
            case ATTACK_COMMANDER_VOTE, ATTACK_FACTION_SELECT -> "ATTACK";
            default -> null;
        };
    }
}

