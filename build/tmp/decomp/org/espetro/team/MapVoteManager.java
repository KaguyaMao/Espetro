/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.team;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.Espetro;
import org.espetro.dimension.BattlefieldWorldManager;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.network.NetworkManager;
import org.espetro.team.FactionDataLoader;
import org.espetro.team.FactionDataProvider;
import org.espetro.team.GameStateManager;

public final class MapVoteManager {
    private static MapVoteManager INSTANCE;
    private boolean active;
    private int tickCounter;
    private int timeoutSeconds = 30;
    private final List<ActiveMapConfig> candidates = new ArrayList<ActiveMapConfig>();
    private final Map<UUID, String> votes = new HashMap<UUID, String>();
    private ActiveMapConfig winner;
    private boolean voteStateDirty;
    private static final int VOTE_BROADCAST_INTERVAL_TICKS = 4;

    private MapVoteManager() {
        INSTANCE = this;
    }

    public static MapVoteManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new MapVoteManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new MapVoteManager();
    }

    public boolean isActive() {
        return this.active;
    }

    public ActiveMapConfig getWinner() {
        return this.winner;
    }

    public List<ActiveMapConfig> getCandidates() {
        return Collections.unmodifiableList(this.candidates);
    }

    public Map<String, Integer> getTally() {
        LinkedHashMap<String, Integer> tally = new LinkedHashMap<String, Integer>();
        for (ActiveMapConfig c : this.candidates) {
            tally.put(c.mapFolder, 0);
        }
        for (String map : this.votes.values()) {
            tally.computeIfPresent(map, (k, v) -> v + 1);
        }
        return tally;
    }

    public String getPlayerVote(UUID uuid) {
        return this.votes.get(uuid);
    }

    public int getRemainingSeconds() {
        if (!this.active) {
            return 0;
        }
        return Math.max(0, this.timeoutSeconds - this.tickCounter / 20);
    }

    public long getEndGameTime(MinecraftServer server) {
        if (!this.active || server == null) {
            return 0L;
        }
        return server.m_129783_().m_46467_() + (long)this.getRemainingSeconds() * 20L;
    }

    public boolean start(MinecraftServer server) {
        if (!BattlefieldWorldManager.getInstance().isStartupReady()) {
            Espetro.LOGGER.error("\u6218\u573a\u542f\u52a8\u95e8\u7981\u672a READY\uff0c\u62d2\u7edd\u5f00\u59cb\u5730\u56fe\u6295\u7968: {}", (Object)BattlefieldWorldManager.getInstance().getStartupPreparation());
            return false;
        }
        ArrayList<ActiveMapConfig> pool = new ArrayList<ActiveMapConfig>(ExternalConfigBootstrap.getUsableMaps());
        pool.removeIf(map -> {
            boolean prepared = BattlefieldWorldManager.getInstance().isPrepared((ActiveMapConfig)map);
            if (!prepared) {
                Espetro.LOGGER.warn("\u5730\u56fe {} \u5df2\u4ece\u6295\u7968\u6c60\u6392\u9664\uff1a\u542f\u52a8\u9636\u6bb5\u672a\u6210\u529f\u51c6\u5907\u7ef4\u5ea6\u6587\u4ef6", (Object)map.displayName);
            }
            return !prepared;
        });
        FactionDataLoader formations = FactionDataProvider.getOrCreateLoader();
        pool.removeIf(map -> {
            boolean playable = formations.isMapPlayable((ActiveMapConfig)map);
            if (!playable) {
                Espetro.LOGGER.warn("\u5730\u56fe {} \u5df2\u4ece\u6295\u7968\u6c60\u6392\u9664\uff1a\u6ca1\u6709\u81f3\u5c11\u4e24\u4e2a faction_id \u4e0d\u540c\u7684\u517c\u5bb9\u7f16\u5236", (Object)map.displayName);
            }
            return !playable;
        });
        if (pool.isEmpty()) {
            Espetro.LOGGER.error("\u65e0\u53ef\u7528\u5730\u56fe\uff0c\u65e0\u6cd5\u5f00\u59cb\u5730\u56fe\u6295\u7968");
            return false;
        }
        Collections.shuffle(pool, ThreadLocalRandom.current());
        int n = Math.min(6, pool.size());
        this.candidates.clear();
        this.candidates.addAll(pool.subList(0, n));
        this.votes.clear();
        this.winner = null;
        this.timeoutSeconds = ExternalConfigBootstrap.getMapVoteSeconds();
        this.tickCounter = 0;
        this.active = true;
        this.voteStateDirty = false;
        Espetro.LOGGER.info("\u5730\u56fe\u6295\u7968\u5f00\u59cb: {} \u4e2a\u5019\u9009, {} \u79d2", (Object)this.candidates.size(), (Object)this.timeoutSeconds);
        NetworkManager.broadcastMapVoteState(this);
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            NetworkManager.sendOpenMapVoteScreen(player);
        }
        return true;
    }

    public boolean castVote(ServerPlayer player, String mapFolder) {
        if (!this.active) {
            return false;
        }
        boolean ok = this.candidates.stream().anyMatch(c -> c.mapFolder.equals(mapFolder));
        if (!ok) {
            return false;
        }
        this.votes.put(player.m_20148_(), mapFolder);
        this.voteStateDirty = true;
        return true;
    }

    public void onPlayerLeave(UUID playerId) {
        if (playerId != null && this.votes.remove(playerId) != null && this.active) {
            this.voteStateDirty = true;
        }
    }

    public void onServerTick(MinecraftServer server) {
        if (!this.active) {
            return;
        }
        ++this.tickCounter;
        if (this.voteStateDirty && this.tickCounter % 4 == 0) {
            this.voteStateDirty = false;
            NetworkManager.broadcastMapVoteState(this);
        }
        if (this.tickCounter >= this.timeoutSeconds * 20) {
            this.finish(server);
        }
    }

    public void finish(MinecraftServer server) {
        if (!this.active) {
            return;
        }
        this.active = false;
        this.voteStateDirty = false;
        Map<String, Integer> tally = this.getTally();
        int best = -1;
        ArrayList<ActiveMapConfig> tied = new ArrayList<ActiveMapConfig>();
        for (ActiveMapConfig c : this.candidates) {
            int v = tally.getOrDefault(c.mapFolder, 0);
            if (v > best) {
                best = v;
                tied.clear();
                tied.add(c);
                continue;
            }
            if (v != best) continue;
            tied.add(c);
        }
        this.winner = tied.isEmpty() ? this.candidates.get(ThreadLocalRandom.current().nextInt(this.candidates.size())) : (best <= 0 ? this.candidates.get(ThreadLocalRandom.current().nextInt(this.candidates.size())) : (tied.size() == 1 ? (ActiveMapConfig)tied.get(0) : (ActiveMapConfig)tied.get(ThreadLocalRandom.current().nextInt(tied.size()))));
        Espetro.LOGGER.info("\u5730\u56fe\u6295\u7968\u7ed3\u675f: \u80dc\u51fa {} ({}), \u7968\u6570\u7edf\u8ba1 {}", new Object[]{this.winner.displayName, this.winner.mapFolder, tally});
        NetworkManager.broadcastMapVoteState(this);
        GameStateManager.getInstance().onMapVoteFinished(this.winner);
    }

    public void syncToPlayer(ServerPlayer player) {
        NetworkManager.sendMapVoteState(player, this);
        if (this.active) {
            NetworkManager.sendOpenMapVoteScreen(player);
        }
    }

    public void reset() {
        this.active = false;
        this.tickCounter = 0;
        this.candidates.clear();
        this.votes.clear();
        this.winner = null;
        this.voteStateDirty = false;
    }

    public static ActiveMapConfig resolveWinnerForTest(List<ActiveMapConfig> candidates, Map<UUID, String> votes, Random random) {
        LinkedHashMap<String, Integer> tally = new LinkedHashMap<String, Integer>();
        for (ActiveMapConfig c : candidates) {
            tally.put(c.mapFolder, 0);
        }
        for (String map : votes.values()) {
            tally.computeIfPresent(map, (k, v) -> v + 1);
        }
        int best = -1;
        ArrayList<ActiveMapConfig> tied = new ArrayList<ActiveMapConfig>();
        for (ActiveMapConfig c : candidates) {
            int v2 = tally.getOrDefault(c.mapFolder, 0);
            if (v2 > best) {
                best = v2;
                tied.clear();
                tied.add(c);
                continue;
            }
            if (v2 != best) continue;
            tied.add(c);
        }
        if (tied.isEmpty() || best <= 0) {
            return candidates.get(random.nextInt(candidates.size()));
        }
        if (tied.size() == 1) {
            return (ActiveMapConfig)tied.get(0);
        }
        return (ActiveMapConfig)tied.get(random.nextInt(tied.size()));
    }
}

