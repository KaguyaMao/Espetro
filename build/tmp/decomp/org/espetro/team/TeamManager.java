/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerScoreboard;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import org.espetro.team.TeamDisplayNames;

public class TeamManager {
    public static final String ATTACK_TEAM_ID = "espetro_attack";
    public static final String DEFEND_TEAM_ID = "espetro_defend";
    public static final String ATTACK_DISPLAY_NAME = "\u00a7c\u8fdb\u653b\u65b9";
    public static final String DEFEND_DISPLAY_NAME = "\u00a79\u9632\u5b88\u65b9";

    public static void initTeams(MinecraftServer server) {
        ServerScoreboard scoreboard = server.m_129896_();
        PlayerTeam attackTeam = scoreboard.m_83489_(ATTACK_TEAM_ID);
        if (attackTeam == null) {
            attackTeam = scoreboard.m_83492_(ATTACK_TEAM_ID);
            attackTeam.m_83351_(ChatFormatting.RED);
        }
        attackTeam.m_83346_(Team.Visibility.HIDE_FOR_OTHER_TEAMS);
        PlayerTeam defendTeam = scoreboard.m_83489_(DEFEND_TEAM_ID);
        if (defendTeam == null) {
            defendTeam = scoreboard.m_83492_(DEFEND_TEAM_ID);
            defendTeam.m_83351_(ChatFormatting.BLUE);
        }
        defendTeam.m_83346_(Team.Visibility.HIDE_FOR_OTHER_TEAMS);
        TeamManager.refreshDisplayNames(server);
    }

    public static void refreshDisplayNames(MinecraftServer server) {
        PlayerTeam defendTeam;
        if (server == null) {
            return;
        }
        ServerScoreboard scoreboard = server.m_129896_();
        PlayerTeam attackTeam = scoreboard.m_83489_(ATTACK_TEAM_ID);
        if (attackTeam != null) {
            attackTeam.m_83353_(Component.m_237113_(TeamDisplayNames.prefix("ATTACK") + TeamDisplayNames.displayName("ATTACK")));
        }
        if ((defendTeam = scoreboard.m_83489_(DEFEND_TEAM_ID)) != null) {
            defendTeam.m_83353_(Component.m_237113_(TeamDisplayNames.prefix("DEFEND") + TeamDisplayNames.displayName("DEFEND")));
        }
    }

    public static void joinAttackTeam(MinecraftServer server, String playerName) {
        ServerScoreboard scoreboard = server.m_129896_();
        TeamManager.removeFromAllTeams(scoreboard, playerName);
        PlayerTeam team = scoreboard.m_83489_(ATTACK_TEAM_ID);
        if (team != null) {
            ((Scoreboard)scoreboard).m_6546_(playerName, team);
        }
    }

    public static void joinDefendTeam(MinecraftServer server, String playerName) {
        ServerScoreboard scoreboard = server.m_129896_();
        TeamManager.removeFromAllTeams(scoreboard, playerName);
        PlayerTeam team = scoreboard.m_83489_(DEFEND_TEAM_ID);
        if (team != null) {
            ((Scoreboard)scoreboard).m_6546_(playerName, team);
        }
    }

    public static void removeFromAllTeams(Scoreboard scoreboard, String playerName) {
        scoreboard.m_83495_(playerName);
    }

    public static boolean isInAttackTeam(MinecraftServer server, String playerName) {
        ServerScoreboard scoreboard = server.m_129896_();
        PlayerTeam team = scoreboard.m_83489_(ATTACK_TEAM_ID);
        return team != null && team.m_6809_().contains(playerName);
    }

    public static boolean isInDefendTeam(MinecraftServer server, String playerName) {
        ServerScoreboard scoreboard = server.m_129896_();
        PlayerTeam team = scoreboard.m_83489_(DEFEND_TEAM_ID);
        return team != null && team.m_6809_().contains(playerName);
    }

    public static String getPlayerTeam(MinecraftServer server, String playerName) {
        if (TeamManager.isInAttackTeam(server, playerName)) {
            return "ATTACK";
        }
        if (TeamManager.isInDefendTeam(server, playerName)) {
            return "DEFEND";
        }
        return null;
    }

    public static int getTeamSize(MinecraftServer server, String teamId) {
        ServerScoreboard scoreboard = server.m_129896_();
        PlayerTeam team = scoreboard.m_83489_(teamId);
        return team != null ? team.m_6809_().size() : 0;
    }
}

