/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.stamina;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import org.espetro.config.GameConfig;
import org.espetro.network.NetworkManager;
import org.espetro.network.StaminaSyncPacket;
import org.espetro.stamina.StaminaRecoveryPolicy;

public final class StaminaManager {
    private static final int TICKS_PER_SECOND = 20;
    private static final Map<UUID, PlayerStamina> PLAYER_STAMINA = new HashMap<UUID, PlayerStamina>();
    private static final Set<UUID> DISABLED_STATE_SYNCED = new HashSet<UUID>();

    private StaminaManager() {
    }

    public static void onPlayerTick(ServerPlayer player) {
        int restorePerSecond;
        int restored;
        UUID playerId = player.m_20148_();
        if (!GameConfig.isStaminaEnabled()) {
            PLAYER_STAMINA.remove(playerId);
            if (DISABLED_STATE_SYNCED.add(playerId)) {
                StaminaManager.syncDisabled(player);
            }
            return;
        }
        DISABLED_STATE_SYNCED.remove(playerId);
        PlayerStamina state = StaminaManager.getOrCreate(player);
        int maxStamina = GameConfig.getPlayerStamina();
        long currentTick = player.m_284548_().m_46467_();
        boolean changed = false;
        boolean staminaUseActive = false;
        if (state.stamina > maxStamina) {
            state.stamina = maxStamina;
            changed = true;
        }
        if (!changed && state.stamina >= maxStamina && !player.m_20142_()) {
            return;
        }
        if (player.m_20142_()) {
            if (state.stamina <= 0) {
                player.m_6858_(false);
            } else {
                int cost = GameConfig.getSprintStaminaCostPerSecond();
                if (cost > 0) {
                    staminaUseActive = true;
                    StaminaManager.scheduleRegeneration(player, state);
                    if (currentTick >= state.nextSprintCostTick) {
                        state.stamina = Math.max(0, state.stamina - cost);
                        state.nextSprintCostTick = currentTick + 20L;
                        changed = true;
                    }
                }
                if (state.stamina == 0) {
                    player.m_6858_(false);
                }
            }
        }
        if (!staminaUseActive && state.stamina < maxStamina && currentTick >= state.regenAtTick && (restored = Math.min(maxStamina, state.stamina + (restorePerSecond = StaminaRecoveryPolicy.restorePerSecond(maxStamina, GameConfig.getStaminaRegenPerSecond(), GameConfig.getStaminaRegenDelaySeconds(), GameConfig.getStaminaFullRecoverySeconds())))) != state.stamina) {
            state.stamina = restored;
            state.regenAtTick = currentTick + 20L;
            changed = true;
        }
        if (changed) {
            StaminaManager.sync(player, state);
        }
    }

    public static void onPlayerJump(ServerPlayer player) {
        if (!GameConfig.isStaminaEnabled()) {
            return;
        }
        PlayerStamina state = StaminaManager.getOrCreate(player);
        long currentTick = player.m_284548_().m_46467_();
        if (state.lastJumpTick == currentTick) {
            return;
        }
        state.lastJumpTick = currentTick;
        int cost = GameConfig.getJumpStaminaCost();
        if (cost <= 0) {
            return;
        }
        state.stamina = Math.max(0, state.stamina - cost);
        StaminaManager.scheduleRegeneration(player, state);
        StaminaManager.sync(player, state);
    }

    public static void resetPlayer(ServerPlayer player) {
        UUID playerId = player.m_20148_();
        PLAYER_STAMINA.remove(playerId);
        DISABLED_STATE_SYNCED.remove(playerId);
        if (GameConfig.isStaminaEnabled()) {
            StaminaManager.getOrCreate(player);
        } else {
            DISABLED_STATE_SYNCED.add(playerId);
            StaminaManager.syncDisabled(player);
        }
    }

    public static void removePlayer(UUID playerId) {
        PLAYER_STAMINA.remove(playerId);
        DISABLED_STATE_SYNCED.remove(playerId);
    }

    public static void clear() {
        PLAYER_STAMINA.clear();
        DISABLED_STATE_SYNCED.clear();
    }

    private static PlayerStamina getOrCreate(ServerPlayer player) {
        return PLAYER_STAMINA.computeIfAbsent(player.m_20148_(), ignored -> {
            PlayerStamina state = new PlayerStamina(GameConfig.getPlayerStamina());
            StaminaManager.sync(player, state);
            return state;
        });
    }

    private static void scheduleRegeneration(ServerPlayer player, PlayerStamina state) {
        int delaySeconds = StaminaRecoveryPolicy.effectiveDelaySeconds(GameConfig.getStaminaRegenDelaySeconds(), GameConfig.getStaminaFullRecoverySeconds());
        state.regenAtTick = player.m_284548_().m_46467_() + (long)delaySeconds * 20L;
    }

    private static void sync(ServerPlayer player, PlayerStamina state) {
        NetworkManager.sendToPlayer(player, new StaminaSyncPacket(true, state.stamina, GameConfig.getPlayerStamina(), GameConfig.getJumpStaminaCost()));
    }

    private static void syncDisabled(ServerPlayer player) {
        NetworkManager.sendToPlayer(player, new StaminaSyncPacket(false, 0, 0, 0));
    }

    private static final class PlayerStamina {
        private int stamina;
        private long regenAtTick;
        private long nextSprintCostTick;
        private long lastJumpTick = Long.MIN_VALUE;

        private PlayerStamina(int stamina) {
            this.stamina = stamina;
        }
    }
}

