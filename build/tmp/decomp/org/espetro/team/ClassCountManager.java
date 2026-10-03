/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.espetro.Espetro;
import org.espetro.config.GameConfig;
import org.espetro.stats.PlayerMatchStatsManager;
import org.espetro.team.ClassEquipment;
import org.espetro.team.ClassSwitchCooldownTracker;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GameStateManager;
import org.espetro.team.SquadManager;
import org.espetro.vehicle.VehicleSeatAccessPolicy;

public class ClassCountManager {
    private static final String SCOREBOARD_OBJECTIVE = "class_count";
    private static final String VARIANT_SCOREBOARD_OBJECTIVE = "class_variant";
    private static final String ATTACK_TEAM = "ATTACK";
    private static final String DEFEND_TEAM = "DEFEND";
    private static final String[] COUNT_TEAMS = new String[]{"ATTACK", "DEFEND"};
    private static ClassCountManager INSTANCE;
    private final Map<UUID, String> playerClasses = new HashMap<UUID, String>();
    private final Map<UUID, String> playerVariants = new HashMap<UUID, String>();
    private final Map<UUID, String> playerFactions = new HashMap<UUID, String>();
    private final Map<UUID, String> playerTeams = new HashMap<UUID, String>();
    private final ClassSwitchCooldownTracker classSwitchCooldowns = new ClassSwitchCooldownTracker();
    private static final Map<UUID, Integer> pendingTeammatesNeedMessage;

    public ClassCountManager() {
        INSTANCE = this;
    }

    public static ClassCountManager getInstance() {
        return INSTANCE;
    }

    private Objective getOrCreateObjective(Scoreboard scoreboard) {
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.m_83436_(SCOREBOARD_OBJECTIVE, ObjectiveCriteria.f_83588_, Component.m_237113_("\u804c\u4e1a\u4eba\u6570"), ObjectiveCriteria.RenderType.INTEGER);
        }
        return objective;
    }

    private Objective getOrCreateVariantObjective(Scoreboard scoreboard) {
        Objective objective = scoreboard.m_83477_(VARIANT_SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            objective = scoreboard.m_83436_(VARIANT_SCOREBOARD_OBJECTIVE, ObjectiveCriteria.f_83588_, Component.m_237113_("\u804c\u4e1a\u53d8\u4f53\u4eba\u6570"), ObjectiveCriteria.RenderType.INTEGER);
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

    public int getCount(String team, String classId) {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return 0;
        }
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            return 0;
        }
        String scoreHolder = this.getScoreHolder(team, classId);
        Score score = scoreboard.m_83471_(scoreHolder, objective);
        return score.m_83400_();
    }

    public int getCount(String classId) {
        return this.getCount(ATTACK_TEAM, classId) + this.getCount(DEFEND_TEAM, classId);
    }

    public int getVariantCount(String team, String classId, String variantId) {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return 0;
        }
        Objective objective = scoreboard.m_83477_(VARIANT_SCOREBOARD_OBJECTIVE);
        if (objective == null) {
            return 0;
        }
        return scoreboard.m_83471_(this.getVariantScoreHolder(team, classId, variantId), objective).m_83400_();
    }

    public int getVariantCount(String classId, String variantId) {
        return this.getVariantCount(ATTACK_TEAM, classId, variantId) + this.getVariantCount(DEFEND_TEAM, classId, variantId);
    }

    public int getMaxCount(String classId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
        return kit != null ? kit.maxPlayers : 5;
    }

    public boolean isFull(String team, String classId) {
        FactionDataLoader.ClassKitData kit = FactionDataProvider.getOrCreateLoader().getClassKit(classId);
        if (kit != null && kit.teamCount) {
            return false;
        }
        return this.getCount(team, classId) >= this.getMaxCount(classId);
    }

    public boolean isFull(String classId) {
        return this.isFull(ATTACK_TEAM, classId) || this.isFull(DEFEND_TEAM, classId);
    }

    public boolean isVariantFull(String team, String classId, String variantId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
        if (kit != null && !kit.strictCount) {
            return false;
        }
        FactionDataLoader.ClassVariantData variant = loader.getClassVariant(classId, variantId);
        return variant == null || this.getVariantCount(team, classId, variantId) >= variant.maxPlayers;
    }

    private void incrementScore(String team, String classId, int delta) {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = this.getOrCreateObjective(scoreboard);
        String scoreHolder = this.getScoreHolder(team, classId);
        int currentScore = this.getCount(team, classId);
        int newScore = Math.max(0, currentScore + delta);
        scoreboard.m_83471_(scoreHolder, objective).m_83402_(newScore);
    }

    private void incrementVariantScore(String team, String classId, String variantId, int delta) {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = this.getOrCreateVariantObjective(scoreboard);
        String scoreHolder = this.getVariantScoreHolder(team, classId, variantId);
        int newScore = Math.max(0, this.getVariantCount(team, classId, variantId) + delta);
        scoreboard.m_83471_(scoreHolder, objective).m_83402_(newScore);
    }

    public boolean selectClass(ServerPlayer player, String classId) {
        FactionDataLoader.ClassKitData kit = FactionDataProvider.getOrCreateLoader().getClassKit(classId);
        if (kit == null || kit.variants == null || kit.variants.size() != 1) {
            return false;
        }
        return this.selectClass(player, classId, kit.variants.keySet().iterator().next());
    }

    public boolean selectClass(ServerPlayer player, String classId, String variantId) {
        return this.selectClassVariant(player, classId, variantId) == SelectionResult.SUCCESS;
    }

    public SelectionResult selectClassVariant(ServerPlayer player, String classId, String variantId) {
        boolean newUsesTeamBoard;
        FactionDataLoader.ClassKitData oldKit;
        boolean changingVariantOnly;
        int squadSize;
        UUID uuid = player.m_20148_();
        String team = this.getEffectivePlayerTeam(uuid);
        if (team == null) {
            Espetro.LOGGER.warn("\u73a9\u5bb6 {} \u65e0\u961f\u4f0d\u8bb0\u5f55\uff0c\u65e0\u6cd5\u9009\u62e9\u804c\u4e1a {}", (Object)player.m_7755_().getString(), (Object)classId);
            return SelectionResult.NO_TEAM;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        FactionDataLoader.ClassKitData kit = loader.getClassKit(classId);
        if (kit == null || kit.factionId == null || !kit.factionId.equals(this.playerFactions.get(uuid))) {
            Espetro.LOGGER.warn("\u73a9\u5bb6 {} \u5c1d\u8bd5\u9009\u62e9\u4e0d\u5c5e\u4e8e\u5f53\u524d\u7f16\u5236\u7684\u804c\u4e1a {}", (Object)player.m_7755_().getString(), (Object)classId);
            return SelectionResult.INVALID_CLASS;
        }
        FactionDataLoader.ClassVariantData variant = kit.getVariant(variantId);
        if (variant == null) {
            return SelectionResult.INVALID_VARIANT;
        }
        variantId = variant.id;
        String oldClassId = this.playerClasses.get(uuid);
        String oldVariantId = this.playerVariants.get(uuid);
        if (classId.equals(oldClassId) && variantId.equals(oldVariantId)) {
            PlayerMatchStatsManager.getInstance().onClassSelected(player, classId, kit.icon, kit.iconImage);
            return SelectionResult.SUCCESS;
        }
        if (this.getClassSwitchCooldownRemaining(uuid) > 0) {
            return SelectionResult.CLASS_SWITCH_COOLDOWN;
        }
        int squadId = SquadManager.getInstance().getPlayerSquadId(uuid);
        if (squadId == -1) {
            return SelectionResult.REQUIRES_SQUAD;
        }
        if (kit.teammatesNeed > 0 && (squadSize = SquadManager.getInstance().getSquadMemberUuids(team, squadId).size()) < kit.teammatesNeed) {
            pendingTeammatesNeedMessage.put(uuid, kit.teammatesNeed);
            return SelectionResult.TEAMMATES_NEED;
        }
        if (kit.leaderOnly && !SquadManager.getInstance().isSquadLeader(uuid)) {
            return SelectionResult.LEADER_ONLY;
        }
        squadSize = SquadManager.getInstance().getSquadMemberUuids(team, squadId).size();
        if (kit.unlockMinSquad > 0 && squadSize < kit.unlockMinSquad) {
            return SelectionResult.UNLOCK_MIN_SQUAD;
        }
        if (kit.unlockPerN > 0) {
            int available = squadSize / kit.unlockPerN;
            if (available <= 0) {
                return SelectionResult.UNLOCK_PER_N;
            }
            if (this.countClassInSquad(team, squadId, classId) >= available) {
                return SelectionResult.UNLOCK_PER_N_FULL;
            }
        }
        boolean changingClass = !classId.equals(oldClassId);
        boolean bl = changingVariantOnly = classId.equals(oldClassId) && !variantId.equals(oldVariantId);
        if (kit.teamCount) {
            if (changingClass && this.countClassInSquad(team, squadId, classId) >= kit.maxPlayers) {
                Espetro.LOGGER.info("{} \u65b9\u5c0f\u961f {} \u804c\u4e1a {} \u5df2\u6ee1\uff0c\u73a9\u5bb6 {} \u65e0\u6cd5\u9009\u62e9", new Object[]{team, squadId, classId, player.m_7755_().getString()});
                return SelectionResult.SQUAD_CLASS_FULL;
            }
            if (kit.strictCount && !variantId.equals(oldVariantId) && this.countVariantInSquad(team, squadId, classId, variantId) >= variant.maxPlayers) {
                return SelectionResult.VARIANT_FULL;
            }
        } else {
            if (changingClass && this.isFull(team, classId)) {
                Espetro.LOGGER.info("{} \u65b9\u804c\u4e1a {} \u5df2\u6ee1\uff0c\u73a9\u5bb6 {} \u65e0\u6cd5\u9009\u62e9", new Object[]{team, classId, player.m_7755_().getString()});
                return SelectionResult.CLASS_FULL;
            }
            if (kit.maxPerSquad > 0 && changingClass && this.countClassInSquad(team, squadId, classId) >= kit.maxPerSquad) {
                return SelectionResult.SQUAD_CLASS_FULL;
            }
            if (kit.strictCount && !variantId.equals(oldVariantId) && this.getVariantCount(team, classId, variantId) >= variant.maxPlayers) {
                return SelectionResult.VARIANT_FULL;
            }
        }
        if (oldClassId != null && oldVariantId == null && (oldKit = loader.getClassKit(oldClassId)) != null && oldKit.variants != null && oldKit.variants.size() == 1) {
            oldVariantId = oldKit.variants.keySet().iterator().next();
        }
        FactionDataLoader.ClassKitData oldKitForBoard = oldClassId != null ? loader.getClassKit(oldClassId) : null;
        boolean oldUsesTeamBoard = oldKitForBoard == null || !oldKitForBoard.teamCount;
        boolean bl2 = newUsesTeamBoard = !kit.teamCount;
        if (oldClassId != null && changingClass && oldUsesTeamBoard) {
            this.incrementScore(team, oldClassId, -1);
        }
        if (oldClassId != null && oldVariantId != null && oldUsesTeamBoard && (changingClass || changingVariantOnly) && (oldKitForBoard == null || oldKitForBoard.strictCount)) {
            this.incrementVariantScore(team, oldClassId, oldVariantId, -1);
        }
        if (changingClass && newUsesTeamBoard) {
            this.incrementScore(team, classId, 1);
        }
        if (kit.strictCount && newUsesTeamBoard && (changingClass || changingVariantOnly)) {
            this.incrementVariantScore(team, classId, variantId, 1);
        }
        this.playerClasses.put(uuid, classId);
        this.playerVariants.put(uuid, variantId);
        ClassEquipment.applyClassBonuses(player, kit);
        if (this.playerFactions.get(uuid) == null) {
            String factionId = this.extractFactionId(classId);
            this.playerFactions.put(uuid, factionId);
        }
        Espetro.LOGGER.debug("\u73a9\u5bb6 {} \u9009\u62e9 {} \u65b9\u804c\u4e1a {} / {} (team_count={}, strict={}, \u7236\u804c\u4e1a\u961f\u4f0d\u8ba1\u6570 {}/{})", new Object[]{player.m_7755_().getString(), team, classId, variantId, kit.teamCount, kit.strictCount, this.getCount(team, classId), this.getMaxCount(classId)});
        PlayerMatchStatsManager.getInstance().onClassSelected(player, classId, kit.icon, kit.iconImage);
        this.classSwitchCooldowns.start(uuid, GameConfig.getClassSwitchCooldownSeconds(), System.currentTimeMillis());
        VehicleSeatAccessPolicy.revalidateCurrentSeat(player);
        return SelectionResult.SUCCESS;
    }

    public int getClassSwitchCooldownRemaining(UUID playerId) {
        return this.classSwitchCooldowns.getRemainingSeconds(playerId, System.currentTimeMillis());
    }

    public static String messageFor(SelectionResult result, UUID playerId) {
        if (result == null) {
            return "\u00a7c\u5f53\u524d\u65e0\u6cd5\u9009\u62e9\u8be5\u804c\u4e1a\u3002";
        }
        return switch (result) {
            default -> throw new IncompatibleClassChangeError();
            case SelectionResult.SUCCESS -> "";
            case SelectionResult.CLASS_FULL -> "\u00a7c\u8be5\u804c\u4e1a\u5168\u961f\u4eba\u6570\u5df2\u6ee1\uff01\u8bf7\u9009\u62e9\u5176\u4ed6\u804c\u4e1a\u3002";
            case SelectionResult.VARIANT_FULL -> "\u00a7c\u8be5\u88c5\u5907\u53d8\u4f53\u4eba\u6570\u5df2\u6ee1\uff01\u8bf7\u9009\u62e9\u5176\u4ed6\u53d8\u4f53\u3002";
            case SelectionResult.SQUAD_CLASS_FULL -> "\u00a7c\u672c\u5c0f\u961f\u8be5\u804c\u4e1a\u4eba\u6570\u5df2\u6ee1\uff01\u8bf7\u9009\u62e9\u5176\u4ed6\u804c\u4e1a\u6216\u5c0f\u961f\u3002";
            case SelectionResult.REQUIRES_SQUAD -> "\u00a7c\u8bf7\u5148\u52a0\u5165\u73ed\u7ec4\u5c0f\u961f\u540e\u518d\u9009\u62e9\u804c\u4e1a\uff01";
            case SelectionResult.TEAMMATES_NEED -> {
                Integer need;
                Integer v1 = need = playerId != null ? pendingTeammatesNeedMessage.remove(playerId) : null;
                if (need != null) {
                    yield ClassCountManager.teammatesNeedMessage(need);
                }
                yield "\u00a7c\u8be5\u804c\u4e1a\u9700\u8981\u5c0f\u961f\u4eba\u6570\u8fbe\u5230\u8981\u6c42\u540e\u624d\u80fd\u9009\u62e9\uff01";
            }
            case SelectionResult.CLASS_SWITCH_COOLDOWN -> {
                int sec = INSTANCE != null ? INSTANCE.getClassSwitchCooldownRemaining(playerId) : 0;
                yield "\u00a7c\u804c\u4e1a\u5207\u6362\u51b7\u5374\u4e2d\uff0c\u8fd8\u9700\u7b49\u5f85 " + Math.max(1, sec) + " \u79d2\u3002";
            }
            case SelectionResult.INVALID_VARIANT -> "\u00a7c\u65e0\u6548\u7684\u804c\u4e1a\u88c5\u5907\u53d8\u4f53\u3002";
            case SelectionResult.INVALID_CLASS -> "\u00a7c\u8be5\u804c\u4e1a\u4e0d\u5c5e\u4e8e\u4f60\u5f53\u524d\u9009\u62e9\u7684\u7f16\u5236\u3002";
            case SelectionResult.NO_TEAM -> "\u00a7c\u4f60\u5c1a\u672a\u52a0\u5165\u653b\u9632\u65b9\uff0c\u65e0\u6cd5\u9009\u62e9\u804c\u4e1a\u3002";
            case SelectionResult.OUT_OF_RANGE -> "\u00a7c\u53ea\u80fd\u5728\u9009\u62e9\u90e8\u7f72\u70b9\u65f6\u3001\u539f\u90e8\u7f72\u70b9\u9644\u8fd1\u6216\u5df1\u65b9 Radio \u8f6e\u76d8\u4e2d\u9009\u62e9\u804c\u4e1a\uff01";
            case SelectionResult.LEADER_ONLY -> "\u00a7c\u8be5\u804c\u4e1a\u4ec5\u5c0f\u961f\u957f\u53ef\u9009\uff01";
            case SelectionResult.UNLOCK_MIN_SQUAD -> "\u00a7c\u5c0f\u961f\u4eba\u6570\u4e0d\u8db3\uff0c\u65e0\u6cd5\u9009\u62e9\u8be5\u804c\u4e1a\uff01";
            case SelectionResult.UNLOCK_PER_N, SelectionResult.UNLOCK_PER_N_FULL -> "\u00a7c\u8be5\u804c\u4e1a\u540d\u989d\u5df2\u7528\u5b8c\u6216\u5c0f\u961f\u4eba\u6570\u4e0d\u8db3\uff01";
        };
    }

    public static String teammatesNeedMessage(int need) {
        return "\u00a7c\u8be5\u804c\u4e1a\u9700\u8981\u5c0f\u961f\u81f3\u5c11 " + Math.max(1, need) + " \u4eba\uff01";
    }

    public int countClassInSquad(String team, int squadId, String classId) {
        if (team == null || classId == null || squadId == -1) {
            return 0;
        }
        int count = 0;
        for (UUID member : SquadManager.getInstance().getSquadMemberUuids(team, squadId)) {
            if (!classId.equals(this.playerClasses.get(member))) continue;
            ++count;
        }
        return count;
    }

    public int countVariantInSquad(String team, int squadId, String classId, String variantId) {
        if (team == null || classId == null || variantId == null || squadId == -1) {
            return 0;
        }
        int count = 0;
        for (UUID member : SquadManager.getInstance().getSquadMemberUuids(team, squadId)) {
            if (!classId.equals(this.playerClasses.get(member)) || !variantId.equals(this.playerVariants.get(member))) continue;
            ++count;
        }
        return count;
    }

    public int getEffectiveClassCountForViewer(UUID viewerId, String team, String classId) {
        FactionDataLoader.ClassKitData kit = FactionDataProvider.getOrCreateLoader().getClassKit(classId);
        if (kit != null && kit.teamCount) {
            int squadId = SquadManager.getInstance().getPlayerSquadId(viewerId);
            if (squadId == -1) {
                return 0;
            }
            return this.countClassInSquad(team, squadId, classId);
        }
        return this.getCount(team, classId);
    }

    public int getSquadClassCountForViewer(UUID viewerId, String team, String classId) {
        int squadId = SquadManager.getInstance().getPlayerSquadId(viewerId);
        if (squadId == -1) {
            return 0;
        }
        return this.countClassInSquad(team, squadId, classId);
    }

    public void onPlayerLeftSquad(ServerPlayer player) {
        if (player == null) {
            return;
        }
        this.clearClassOnSquadExit(player.m_20148_(), player, true);
    }

    public void onPlayerLeftSquadOffline(UUID uuid) {
        this.clearClassOnSquadExit(uuid, null, false);
    }

    private void clearClassOnSquadExit(UUID uuid, ServerPlayer onlinePlayer, boolean notify) {
        FactionDataLoader.ClassKitData kit;
        String team = this.getEffectivePlayerTeam(uuid);
        String variantId = this.playerVariants.remove(uuid);
        String classId = this.playerClasses.remove(uuid);
        FactionDataLoader.ClassKitData classKitData = kit = classId != null ? FactionDataProvider.getOrCreateLoader().getClassKit(classId) : null;
        if (!(team == null || classId == null || kit != null && kit.teamCount)) {
            this.incrementScore(team, classId, -1);
            if (variantId != null && (kit == null || kit.strictCount)) {
                this.incrementVariantScore(team, classId, variantId, -1);
            }
        }
        if (onlinePlayer != null) {
            ClassEquipment.clearEquipment(onlinePlayer);
            VehicleSeatAccessPolicy.revalidateCurrentSeat(onlinePlayer);
        }
        PlayerMatchStatsManager.getInstance().onClassCleared(uuid);
        if (notify && onlinePlayer != null) {
            onlinePlayer.m_213846_(Component.m_237113_("\u00a7e\u5df2\u79bb\u5f00\u73ed\u7ec4\u5c0f\u961f\uff0c\u80cc\u5305\u548c\u804c\u4e1a\u5df2\u6e05\u7a7a\u3002\u8bf7\u91cd\u65b0\u9009\u62e9\u804c\u4e1a\u3002"));
        }
    }

    public void removePlayer(Player player) {
        FactionDataLoader.ClassKitData kit;
        UUID uuid = player.m_20148_();
        String team = this.getEffectivePlayerTeam(uuid);
        String classId = this.playerClasses.remove(uuid);
        String variantId = this.playerVariants.remove(uuid);
        if (!(team == null || classId == null || (kit = FactionDataProvider.getOrCreateLoader().getClassKit(classId)) != null && kit.teamCount)) {
            this.incrementScore(team, classId, -1);
            if (variantId != null && (kit == null || kit.strictCount)) {
                this.incrementVariantScore(team, classId, variantId, -1);
            }
        }
        this.playerFactions.remove(uuid);
        this.playerTeams.remove(uuid);
        PlayerMatchStatsManager.getInstance().onClassCleared(uuid);
    }

    public void setPlayerTeam(UUID uuid, String team) {
        this.playerTeams.put(uuid, team);
    }

    public String getPlayerTeam(UUID uuid) {
        return this.playerTeams.get(uuid);
    }

    public String getEffectivePlayerTeam(UUID uuid) {
        String team = this.playerTeams.get(uuid);
        if (team != null) {
            return this.normalizeTeam(team);
        }
        String factionId = this.playerFactions.get(uuid);
        if (factionId == null) {
            return null;
        }
        return this.normalizeTeam(GameStateManager.getTeamFromFactionStatic(factionId));
    }

    public Map<String, Integer> getAllCounts() {
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        for (FactionDataLoader.FactionData faction : loader.getFactionArray()) {
            if (faction == null || faction.id == null) continue;
            for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(faction.id)) {
                result.put(kit.id, this.getCount(kit.id));
            }
        }
        return result;
    }

    public Map<String, Integer> getCountsForFaction(String team, String factionId) {
        if (team == null) {
            return this.getCountsForFaction(factionId);
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(factionId)) {
            result.put(kit.id, this.getCount(team, kit.id));
        }
        return result;
    }

    public Map<String, Integer> getCountsForFaction(String factionId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(factionId)) {
            result.put(kit.id, this.getCount(kit.id));
        }
        return result;
    }

    public Map<String, Map<String, Integer>> getVariantCountsForFaction(String team, String factionId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        HashMap<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
        for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(factionId)) {
            HashMap<String, Integer> variants = new HashMap<String, Integer>();
            if (kit.variants != null) {
                for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                    variants.put(variant.id, team == null ? this.getVariantCount(kit.id, variant.id) : this.getVariantCount(team, kit.id, variant.id));
                }
            }
            result.put(kit.id, variants);
        }
        return result;
    }

    public Map<String, Integer> getSquadCountsForViewer(UUID viewerId, String team, String factionId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        HashMap<String, Integer> result = new HashMap<String, Integer>();
        for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(factionId)) {
            result.put(kit.id, this.getSquadClassCountForViewer(viewerId, team, kit.id));
        }
        return result;
    }

    public Map<String, Map<String, Integer>> getVariantCountsForViewer(UUID viewerId, String team, String factionId) {
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        int squadId = SquadManager.getInstance().getPlayerSquadId(viewerId);
        HashMap<String, Map<String, Integer>> result = new HashMap<String, Map<String, Integer>>();
        for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(factionId)) {
            HashMap<String, Integer> variants = new HashMap<String, Integer>();
            if (kit.variants != null) {
                for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                    int count = kit.teamCount ? this.countVariantInSquad(team, squadId, kit.id, variant.id) : this.getVariantCount(team, kit.id, variant.id);
                    variants.put(variant.id, count);
                }
            }
            result.put(kit.id, variants);
        }
        return result;
    }

    public String getPlayerClass(UUID uuid) {
        return this.playerClasses.get(uuid);
    }

    public String getPlayerVariant(UUID uuid) {
        return this.playerVariants.get(uuid);
    }

    public String getPlayerFaction(UUID uuid) {
        return this.playerFactions.get(uuid);
    }

    public void setPlayerFaction(UUID uuid, String factionId) {
        this.playerFactions.put(uuid, factionId);
    }

    private String extractFactionId(String classId) {
        if (classId == null) {
            return null;
        }
        int lastUnderscore = classId.lastIndexOf(95);
        if (lastUnderscore > 0) {
            return classId.substring(0, lastUnderscore).toLowerCase();
        }
        return classId.toLowerCase();
    }

    public void initializeAllClassScores() {
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = this.getOrCreateObjective(scoreboard);
        Objective variantObjective = this.getOrCreateVariantObjective(scoreboard);
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        for (FactionDataLoader.FactionData faction : loader.getFactionArray()) {
            if (faction == null || faction.id == null) continue;
            for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(faction.id)) {
                for (String team : COUNT_TEAMS) {
                    scoreboard.m_83471_(this.getScoreHolder(team, kit.id), objective).m_83402_(0);
                }
                scoreboard.m_83471_(this.getLegacyScoreHolder(kit.id), objective).m_83402_(0);
                if (kit.variants == null) continue;
                for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                    for (String team : COUNT_TEAMS) {
                        scoreboard.m_83471_(this.getVariantScoreHolder(team, kit.id, variant.id), variantObjective).m_83402_(0);
                    }
                }
            }
        }
    }

    public void resetAll() {
        this.playerClasses.clear();
        this.playerVariants.clear();
        this.playerFactions.clear();
        this.playerTeams.clear();
        this.classSwitchCooldowns.clearAll();
        Scoreboard scoreboard = this.getScoreboard();
        if (scoreboard == null) {
            return;
        }
        Objective objective = scoreboard.m_83477_(SCOREBOARD_OBJECTIVE);
        Objective variantObjective = scoreboard.m_83477_(VARIANT_SCOREBOARD_OBJECTIVE);
        if (objective == null && variantObjective == null) {
            return;
        }
        FactionDataLoader loader = FactionDataProvider.getOrCreateLoader();
        for (FactionDataLoader.FactionData faction : loader.getFactionArray()) {
            if (faction == null || faction.id == null) continue;
            for (FactionDataLoader.ClassKitData kit : loader.getClassesForFaction(faction.id)) {
                for (String team : COUNT_TEAMS) {
                    String scoreHolder = this.getScoreHolder(team, kit.id);
                    if (objective == null || !scoreboard.m_83461_(scoreHolder, objective)) continue;
                    scoreboard.m_83471_(scoreHolder, objective).m_83402_(0);
                }
                String legacyScoreHolder = this.getLegacyScoreHolder(kit.id);
                if (objective != null && scoreboard.m_83461_(legacyScoreHolder, objective)) {
                    scoreboard.m_83471_(legacyScoreHolder, objective).m_83402_(0);
                }
                if (variantObjective == null || kit.variants == null) continue;
                for (FactionDataLoader.ClassVariantData variant : kit.variants.values()) {
                    for (String team : COUNT_TEAMS) {
                        String holder = this.getVariantScoreHolder(team, kit.id, variant.id);
                        if (!scoreboard.m_83461_(holder, variantObjective)) continue;
                        scoreboard.m_83471_(holder, variantObjective).m_83402_(0);
                    }
                }
            }
        }
    }

    private String getScoreHolder(String team, String classId) {
        return "class_" + this.normalizeTeam(team) + "_" + classId;
    }

    private String getLegacyScoreHolder(String classId) {
        return "class_" + classId;
    }

    private String getVariantScoreHolder(String team, String classId, String variantId) {
        return "variant_" + this.normalizeTeam(team) + "_" + classId + "_" + variantId;
    }

    private String normalizeTeam(String team) {
        if (ATTACK_TEAM.equalsIgnoreCase(team)) {
            return ATTACK_TEAM;
        }
        if (DEFEND_TEAM.equalsIgnoreCase(team)) {
            return DEFEND_TEAM;
        }
        return team == null ? "UNKNOWN" : team.toUpperCase();
    }

    static {
        pendingTeammatesNeedMessage = new ConcurrentHashMap<UUID, Integer>();
    }

    public static enum SelectionResult {
        SUCCESS,
        NO_TEAM,
        INVALID_CLASS,
        INVALID_VARIANT,
        CLASS_FULL,
        VARIANT_FULL,
        REQUIRES_SQUAD,
        SQUAD_CLASS_FULL,
        CLASS_SWITCH_COOLDOWN,
        OUT_OF_RANGE,
        TEAMMATES_NEED,
        LEADER_ONLY,
        UNLOCK_MIN_SQUAD,
        UNLOCK_PER_N,
        UNLOCK_PER_N_FULL;

    }
}

