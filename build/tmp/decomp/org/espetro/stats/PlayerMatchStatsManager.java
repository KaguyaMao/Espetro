/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.stats;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.network.NetworkManager;
import org.espetro.team.GamePhase;
import org.espetro.team.GameStateManager;

public final class PlayerMatchStatsManager {
    private static PlayerMatchStatsManager INSTANCE;
    private final Map<UUID, PlayerMatchStats> stats = new LinkedHashMap<UUID, PlayerMatchStats>();
    private boolean dirty;
    private int ticksUntilBroadcast;

    private PlayerMatchStatsManager() {
        INSTANCE = this;
    }

    public static PlayerMatchStatsManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new PlayerMatchStatsManager();
        }
        return INSTANCE;
    }

    public static void init() {
        INSTANCE = new PlayerMatchStatsManager();
    }

    public void resetMatch() {
        this.stats.clear();
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public void resetMatch(Iterable<ServerPlayer> players) {
        this.stats.clear();
        if (players != null) {
            for (ServerPlayer player : players) {
                if (player == null) continue;
                this.stats.put(player.m_20148_(), new PlayerMatchStats(player.m_20148_(), player.m_7755_().getString()));
            }
        }
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public PlayerMatchStats ensure(ServerPlayer player) {
        return this.stats.computeIfAbsent(player.m_20148_(), id -> new PlayerMatchStats((UUID)id, player.m_7755_().getString()));
    }

    public void onPlayerJoin(ServerPlayer player) {
        PlayerMatchStats s = this.ensure(player);
        s.name = player.m_7755_().getString();
        s.online = true;
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public void onPlayerLeave(UUID uuid) {
        PlayerMatchStats s = this.stats.get(uuid);
        if (s != null) {
            s.online = false;
            s.team = null;
            this.dirty = true;
            this.broadcastIfDirty();
        }
    }

    public void onTeamSelected(ServerPlayer player, String team) {
        PlayerMatchStats s = this.ensure(player);
        s.team = team;
        s.lastTeam = team;
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public void onClassSelected(ServerPlayer player, String classId, @Nullable String icon) {
        this.onClassSelected(player, classId, icon, null);
    }

    public void onClassSelected(ServerPlayer player, String classId, @Nullable String iconSlug, @Nullable String iconImage) {
        PlayerMatchStats s = this.ensure(player);
        s.classId = classId;
        s.classIcon = iconSlug;
        s.classIconImage = iconImage;
        if ((s.classIcon == null || s.classIcon.isBlank()) && iconImage != null && !iconImage.isBlank()) {
            s.classIconImage = iconImage;
        } else if (s.classIcon != null && (s.classIcon.contains("/") || s.classIcon.contains("\\"))) {
            s.classIconImage = s.classIcon;
            s.classIcon = null;
        }
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public void onClassCleared(UUID uuid) {
        PlayerMatchStats s = this.stats.get(uuid);
        if (s == null || s.classId == null && s.classIcon == null && s.classIconImage == null) {
            return;
        }
        s.classId = null;
        s.classIcon = null;
        s.classIconImage = null;
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public void onPlayerDeath(ServerPlayer victim, DamageSource source) {
        ServerPlayer killer;
        GamePhase phase = GameStateManager.getInstance().getCurrentPhase();
        if (phase != GamePhase.BATTLE) {
            return;
        }
        if (!BattlefieldContext.isActiveBattlefield(victim.m_284548_())) {
            return;
        }
        PlayerMatchStats victimStats = this.ensure(victim);
        ++victimStats.deaths;
        this.dirty = true;
        Entity killerEntity = source.m_7639_();
        if (killerEntity instanceof ServerPlayer && !(killer = (ServerPlayer)killerEntity).m_20148_().equals(victim.m_20148_())) {
            String killerTeam;
            String victimTeam = victimStats.team != null ? victimStats.team : victimStats.lastTeam;
            PlayerMatchStats killerStats = this.ensure(killer);
            String string = killerTeam = killerStats.team != null ? killerStats.team : killerStats.lastTeam;
            if (victimTeam != null && killerTeam != null && !victimTeam.equals(killerTeam)) {
                ++killerStats.kills;
            }
        }
        this.broadcastIfDirty();
    }

    public List<PlayerMatchStats> snapshot() {
        return new ArrayList<PlayerMatchStats>(this.stats.values());
    }

    public Optional<PlayerMatchStats> get(UUID id) {
        return Optional.ofNullable(this.stats.get(id));
    }

    private void broadcastIfDirty() {
        if (this.ticksUntilBroadcast <= 0) {
            this.ticksUntilBroadcast = 20;
        }
    }

    public void markDirtyAndBroadcast() {
        this.dirty = true;
        this.broadcastIfDirty();
    }

    public void onServerTick() {
        if (!this.dirty) {
            this.ticksUntilBroadcast = 20;
            return;
        }
        if (this.ticksUntilBroadcast > 0) {
            --this.ticksUntilBroadcast;
            return;
        }
        this.dirty = false;
        this.ticksUntilBroadcast = 20;
        NetworkManager.broadcastMatchStats(this);
    }

    public static final class PlayerMatchStats {
        public final UUID uuid;
        public String name;
        public String team;
        public String lastTeam;
        public int kills;
        public int deaths;
        public String classId;
        public String classIcon;
        public String classIconImage;
        public boolean online;

        public PlayerMatchStats(UUID uuid, String name) {
            this.uuid = uuid;
            this.name = name;
            this.online = true;
        }
    }
}

