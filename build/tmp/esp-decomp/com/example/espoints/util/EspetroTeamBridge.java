/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.effect.MobEffects
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.scores.PlayerTeam
 *  net.minecraft.world.scores.Team
 *  org.espetro.api.EspetroAPI
 */
package com.example.espoints.util;

import java.lang.reflect.Method;
import java.util.Locale;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Team;
import org.espetro.api.EspetroAPI;

public final class EspetroTeamBridge {
    public static final String ATTACK = "ATTACK";
    public static final String DEFEND = "DEFEND";
    public static final String ATTACK_SCOREBOARD_TEAM = "espetro_attack";
    public static final String DEFEND_SCOREBOARD_TEAM = "espetro_defend";
    private static final String ESPETRO_CLASS_NAME = "org.espetro.Espetro";
    private static final String ESPETRO_API_CLASS_NAME = "org.espetro.api.EspetroAPI";
    private static final String ESPETRO_VOTE_MANAGER_CLASS_NAME = "org.espetro.team.VoteManager";
    private static final String ESPETRO_SQUAD_MANAGER_CLASS_NAME = "org.espetro.team.SquadManager";
    private static final String ESPETRO_CLIENT_GAME_STATE_CLASS_NAME = "org.espetro.client.gui.ClientGameState";
    private static final String ESPETRO_CLIENT_TACTICAL_STATE_CLASS_NAME = "org.espetro.client.gui.ClientTacticalState";
    private static final int DEFAULT_TEAMMATE_COLOR = -1;
    public static final int MAP_COLOR_COMMANDER = -14490;
    public static final int MAP_COLOR_SQUAD_LEADER = -2847489;
    public static final int MAP_COLOR_SQUAD_MEMBER = -9984001;
    public static final int MAP_COLOR_FRIENDLY = -1;

    private EspetroTeamBridge() {
    }

    public static String getPlayerTeam(Player player) {
        String clientTeam;
        if (player == null) {
            return null;
        }
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            return EspetroTeamBridge.getServerPlayerTeam(serverPlayer);
        }
        String scoreboardTeam = EspetroTeamBridge.getScoreboardTeam(player.m_5647_());
        if (scoreboardTeam != null) {
            return scoreboardTeam;
        }
        if (player.m_7578_() && (clientTeam = EspetroTeamBridge.getClientPlayerTeam()) != null) {
            return clientTeam;
        }
        return null;
    }

    public static String getServerPlayerTeam(ServerPlayer player) {
        if (player == null) {
            return null;
        }
        String scoreboardTeam = EspetroTeamBridge.getScoreboardTeam(player.m_5647_());
        if (scoreboardTeam != null) {
            return scoreboardTeam;
        }
        String espetroTeam = EspetroTeamBridge.getEspetroPlayerTeam(player);
        if (espetroTeam != null) {
            return espetroTeam;
        }
        return null;
    }

    public static String getScoreboardTeam(Team team) {
        if (team == null) {
            return null;
        }
        String canonical = EspetroTeamBridge.canonicalizeTeamName(team.m_5758_());
        if (canonical != null) {
            return canonical;
        }
        if (team instanceof PlayerTeam) {
            PlayerTeam playerTeam = (PlayerTeam)team;
            return EspetroTeamBridge.canonicalizeTeamName(playerTeam.m_83364_().getString());
        }
        return null;
    }

    public static boolean isEspetroTeamPlayer(Player player) {
        return EspetroTeamBridge.getPlayerTeam(player) != null;
    }

    public static boolean isPlayerVisibleOnTacticalMap(Player player) {
        ServerPlayer serverPlayer;
        Boolean fromApi;
        if (player == null) {
            return false;
        }
        if (player instanceof ServerPlayer && (fromApi = EspetroTeamBridge.invokeStaticBooleanIfPresent(ESPETRO_API_CLASS_NAME, "isPlayerVisibleOnTacticalMap", new Class[]{ServerPlayer.class}, serverPlayer = (ServerPlayer)player)) != null) {
            return fromApi;
        }
        return player.m_6084_() && !player.m_5833_() && !player.m_21023_(MobEffects.f_19610_) && EspetroTeamBridge.getPlayerTeam(player) != null;
    }

    public static boolean isSameTeam(String left, String right) {
        String leftTeam = EspetroTeamBridge.canonicalizeTeamName(left);
        String rightTeam = EspetroTeamBridge.canonicalizeTeamName(right);
        return leftTeam != null && leftTeam.equals(rightTeam);
    }

    public static String canonicalizeTeamName(String teamName) {
        if (teamName == null || teamName.isBlank()) {
            return null;
        }
        String stripped = EspetroTeamBridge.stripMinecraftFormatting(teamName).trim();
        String normalized = stripped.toLowerCase(Locale.ROOT);
        if (ATTACK.equalsIgnoreCase(stripped) || ATTACK_SCOREBOARD_TEAM.equals(normalized) || "attacker".equals(normalized)) {
            return ATTACK;
        }
        if (DEFEND.equalsIgnoreCase(stripped) || DEFEND_SCOREBOARD_TEAM.equals(normalized) || "defender".equals(normalized)) {
            return DEFEND;
        }
        if (normalized.contains("attack") || normalized.contains("attacker") || stripped.contains("\u8fdb\u653b") || stripped.contains("\u653b\u65b9")) {
            return ATTACK;
        }
        if (normalized.contains("defend") || normalized.contains("defender") || stripped.contains("\u9632\u5b88") || stripped.contains("\u5b88\u65b9")) {
            return DEFEND;
        }
        return null;
    }

    public static String scoreboardTeamId(String canonicalTeam) {
        String team = EspetroTeamBridge.canonicalizeTeamName(canonicalTeam);
        if (ATTACK.equals(team)) {
            return ATTACK_SCOREBOARD_TEAM;
        }
        if (DEFEND.equals(team)) {
            return DEFEND_SCOREBOARD_TEAM;
        }
        return "";
    }

    public static String roleForTeam(String canonicalTeam) {
        String team = EspetroTeamBridge.canonicalizeTeamName(canonicalTeam);
        if (ATTACK.equals(team)) {
            return "attacker";
        }
        if (DEFEND.equals(team)) {
            return "defender";
        }
        return null;
    }

    public static String displayName(String canonicalTeam) {
        String team = EspetroTeamBridge.canonicalizeTeamName(canonicalTeam);
        if (team == null) {
            return canonicalTeam == null ? "" : canonicalTeam;
        }
        try {
            return EspetroAPI.teamDisplayName((String)team);
        }
        catch (Throwable throwable) {
            if (ATTACK.equals(team)) {
                return "\u8fdb\u653b\u65b9";
            }
            if (DEFEND.equals(team)) {
                return "\u9632\u5b88\u65b9";
            }
            return team;
        }
    }

    public static int getMapPlayerColor(String playerName) {
        try {
            int n;
            Class<?> tacticalStateClass = Class.forName(ESPETRO_CLIENT_TACTICAL_STATE_CLASS_NAME);
            Method method = tacticalStateClass.getMethod("getNameColor", String.class);
            Object result = method.invoke(null, playerName);
            if (result instanceof Number) {
                Number color = (Number)result;
                n = color.intValue();
            } else {
                n = -1;
            }
            return n;
        }
        catch (ReflectiveOperationException ignored) {
            return -1;
        }
    }

    public static int getMapPlayerColor(String playerName, int squadId, boolean squadLeader, boolean commander) {
        if (commander) {
            return -14490;
        }
        int mySquadId = EspetroTeamBridge.getLocalSquadId();
        if (squadId >= 0 && squadId == mySquadId) {
            return squadLeader ? -2847489 : -9984001;
        }
        if (squadId >= 0 && mySquadId >= 0) {
            return -1;
        }
        int byName = EspetroTeamBridge.getMapPlayerColor(playerName);
        if (byName != -1 && byName != -1) {
            return byName;
        }
        return -1;
    }

    public static int getLocalSquadId() {
        try {
            int n;
            Class<?> tacticalStateClass = Class.forName(ESPETRO_CLIENT_TACTICAL_STATE_CLASS_NAME);
            Method method = tacticalStateClass.getMethod("getMySquadId", new Class[0]);
            Object result = method.invoke(null, new Object[0]);
            if (result instanceof Number) {
                Number number = (Number)result;
                n = number.intValue();
            } else {
                n = -1;
            }
            return n;
        }
        catch (ReflectiveOperationException ignored) {
            return -1;
        }
    }

    public static boolean isSquadLeaderPublic(ServerPlayer player) {
        return EspetroTeamBridge.isSquadLeader(player);
    }

    public static boolean canPlaceTacticalMarker(ServerPlayer player) {
        return player != null && EspetroAPI.canPlacePing((ServerPlayer)player);
    }

    public static boolean canPlaceTacticalMarkerClientHint(Player player) {
        if (player == null) {
            return false;
        }
        if (EspetroTeamBridge.getPlayerTeam(player) == null) {
            return false;
        }
        if (player.m_20159_()) {
            return true;
        }
        try {
            Class<?> tacticalState = Class.forName(ESPETRO_CLIENT_TACTICAL_STATE_CLASS_NAME);
            Object value = tacticalState.getMethod("canLocalPlayerPlacePing", String.class).invoke(null, player.m_7755_().getString());
            return Boolean.TRUE.equals(value);
        }
        catch (ReflectiveOperationException ignored) {
            return true;
        }
    }

    public static boolean isCommander(ServerPlayer player) {
        return player != null && EspetroTeamBridge.isCommanderInternal(player);
    }

    public static int getPlayerSquadId(ServerPlayer player) {
        if (player == null) {
            return -1;
        }
        Integer fromApi = EspetroTeamBridge.invokeStaticIntIfPresent(ESPETRO_API_CLASS_NAME, "getPlayerSquadId", new Class[]{UUID.class}, player.m_20148_());
        if (fromApi != null) {
            return fromApi;
        }
        try {
            Class<?> squadManagerClass = Class.forName(ESPETRO_SQUAD_MANAGER_CLASS_NAME);
            Object squadManager = squadManagerClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Object result = squadManagerClass.getMethod("getPlayerSquadId", UUID.class).invoke(squadManager, player.m_20148_());
            if (result instanceof Number) {
                Number number = (Number)result;
                return number.intValue();
            }
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
        return -1;
    }

    public static boolean submitArtillerySupportTarget(ServerPlayer player, double x, double z) {
        return EspetroTeamBridge.submitCommanderSkillTarget(player, x, z);
    }

    public static boolean submitCommanderSkillTarget(ServerPlayer player, double x, double z) {
        if (player == null || !Double.isFinite(x) || !Double.isFinite(z)) {
            return false;
        }
        Boolean result = EspetroTeamBridge.invokeStaticBooleanIfPresent(ESPETRO_API_CLASS_NAME, "submitCommanderSkillTarget", new Class[]{ServerPlayer.class, Double.TYPE, Double.TYPE}, player, x, z);
        if (result != null) {
            return result;
        }
        return EspetroTeamBridge.invokeStaticBoolean(ESPETRO_API_CLASS_NAME, "submitArtillerySupportTarget", new Class[]{ServerPlayer.class, Double.TYPE, Double.TYPE}, player, x, z);
    }

    private static boolean isCommanderInternal(ServerPlayer player) {
        if (EspetroTeamBridge.invokeStaticBoolean(ESPETRO_API_CLASS_NAME, "isCommander", new Class[]{UUID.class}, player.m_20148_())) {
            return true;
        }
        try {
            Class<?> voteManagerClass = Class.forName(ESPETRO_VOTE_MANAGER_CLASS_NAME);
            Object voteManager = voteManagerClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Method isCommanderMethod = voteManagerClass.getMethod("isCommander", UUID.class);
            Object directResult = isCommanderMethod.invoke(voteManager, player.m_20148_());
            if (Boolean.TRUE.equals(directResult)) {
                return true;
            }
            Method getAttackCommander = voteManagerClass.getMethod("getAttackCommander", new Class[0]);
            Method getDefendCommander = voteManagerClass.getMethod("getDefendCommander", new Class[0]);
            return player.m_20148_().equals(getAttackCommander.invoke(voteManager, new Object[0])) || player.m_20148_().equals(getDefendCommander.invoke(voteManager, new Object[0]));
        }
        catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean isSquadLeader(ServerPlayer player) {
        if (EspetroTeamBridge.invokeStaticBoolean(ESPETRO_API_CLASS_NAME, "isSquadLeader", new Class[]{UUID.class}, player.m_20148_())) {
            return true;
        }
        try {
            Class<?> squadManagerClass = Class.forName(ESPETRO_SQUAD_MANAGER_CLASS_NAME);
            Object squadManager = squadManagerClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            Method getSquadSnapshots = squadManagerClass.getMethod("getSquadSnapshots", String.class);
            String team = EspetroTeamBridge.getServerPlayerTeam(player);
            if (team != null && EspetroTeamBridge.isSquadLeaderInSnapshots(getSquadSnapshots.invoke(squadManager, team), player.m_20148_())) {
                return true;
            }
            return EspetroTeamBridge.isSquadLeaderInSnapshots(getSquadSnapshots.invoke(squadManager, ATTACK), player.m_20148_()) || EspetroTeamBridge.isSquadLeaderInSnapshots(getSquadSnapshots.invoke(squadManager, DEFEND), player.m_20148_());
        }
        catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean isSquadLeaderInSnapshots(Object snapshotsObject, UUID playerId) throws ReflectiveOperationException {
        if (!(snapshotsObject instanceof Iterable)) {
            return false;
        }
        Iterable snapshots = (Iterable)snapshotsObject;
        for (Object squad : snapshots) {
            Object membersObject = squad.getClass().getField("members").get(squad);
            if (!(membersObject instanceof Iterable)) continue;
            Iterable members = (Iterable)membersObject;
            for (Object member : members) {
                Object memberId = member.getClass().getField("uuid").get(member);
                Object leader = member.getClass().getField("leader").get(member);
                if (!playerId.equals(memberId) || !Boolean.TRUE.equals(leader)) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean invokeStaticBoolean(String className, String methodName, Class<?>[] parameterTypes, Object ... args) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method method = targetClass.getMethod(methodName, parameterTypes);
            return Boolean.TRUE.equals(method.invoke(null, args));
        }
        catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static Boolean invokeStaticBooleanIfPresent(String className, String methodName, Class<?>[] parameterTypes, Object ... args) {
        try {
            Class<?> targetClass = Class.forName(className);
            Method method = targetClass.getMethod(methodName, parameterTypes);
            return Boolean.TRUE.equals(method.invoke(null, args));
        }
        catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return null;
        }
        catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static Integer invokeStaticIntIfPresent(String className, String methodName, Class<?>[] parameterTypes, Object ... args) {
        try {
            Integer n;
            Class<?> targetClass = Class.forName(className);
            Method method = targetClass.getMethod(methodName, parameterTypes);
            Object result = method.invoke(null, args);
            if (result instanceof Number) {
                Number number = (Number)result;
                n = number.intValue();
            } else {
                n = null;
            }
            return n;
        }
        catch (ClassNotFoundException | NoSuchMethodException ignored) {
            return null;
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static String getEspetroPlayerTeam(ServerPlayer player) {
        try {
            String string;
            Class<?> espetroClass = Class.forName(ESPETRO_CLASS_NAME);
            Method method = espetroClass.getMethod("getPlayerTeam", ServerPlayer.class);
            Object result = method.invoke(null, player);
            if (result instanceof String) {
                String team = (String)result;
                string = EspetroTeamBridge.canonicalizeTeamName(team);
            } else {
                string = null;
            }
            return string;
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static String getClientPlayerTeam() {
        try {
            String string;
            Class<?> clientGameStateClass = Class.forName(ESPETRO_CLIENT_GAME_STATE_CLASS_NAME);
            Method method = clientGameStateClass.getMethod("getPlayerTeam", new Class[0]);
            Object result = method.invoke(null, new Object[0]);
            if (result instanceof String) {
                String team = (String)result;
                string = EspetroTeamBridge.canonicalizeTeamName(team);
            } else {
                string = null;
            }
            return string;
        }
        catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    private static String stripMinecraftFormatting(String value) {
        StringBuilder builder = new StringBuilder(value.length());
        boolean skipNext = false;
        for (int i = 0; i < value.length(); ++i) {
            char current = value.charAt(i);
            if (skipNext) {
                skipNext = false;
                continue;
            }
            if (current == '\u00a7' && i + 1 < value.length()) {
                skipNext = true;
                continue;
            }
            builder.append(current);
        }
        return builder.toString();
    }
}

