/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.eventbus.api.Event
 */
package org.espetro.mapconfig;

import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.Event;
import org.espetro.Espetro;
import org.espetro.api.ActiveBattlefieldSnapshot;
import org.espetro.api.EspetroAPI;
import org.espetro.api.event.BattlefieldLifecycleEvent;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.GameConfigBridge;

public final class BattlefieldContext {
    private static final AtomicReference<ActiveMapConfig> ACTIVE = new AtomicReference<Object>(null);
    private static final AtomicLong SESSION_ID = new AtomicLong(0L);
    private static volatile String lastRoundWinner = null;
    private static volatile String resolvedObjectiveMode = "";
    private static volatile String resolvedObjectiveLane = "";

    private BattlefieldContext() {
    }

    public static void activate(ActiveMapConfig config) {
        if (config != null) {
            ActiveMapConfig roundConfig = config.forRound(ThreadLocalRandom.current().nextLong());
            GameConfigBridge.apply(roundConfig);
            ACTIVE.set(roundConfig);
            SESSION_ID.incrementAndGet();
            Espetro.LOGGER.info("\u6fc0\u6d3b\u6218\u573a\u914d\u7f6e: {} ({})\uff0c\u76ee\u6807\u6a21\u5f0f={}\uff0c\u8def\u7ebf={}", new Object[]{roundConfig.displayName, roundConfig.dimensionId, roundConfig.esPoints.objectiveMode, roundConfig.esPoints.objectiveLane.isEmpty() ? "\u56fa\u5b9a\u8def\u7ebf" : roundConfig.esPoints.objectiveLane});
            EspetroAPI.getActiveBattlefieldSnapshot().ifPresent(snapshot -> MinecraftForge.EVENT_BUS.post((Event)new BattlefieldLifecycleEvent.Activated((ActiveBattlefieldSnapshot)snapshot)));
        } else {
            BattlefieldContext.clear();
        }
    }

    public static void clear() {
        SESSION_ID.incrementAndGet();
        ActiveMapConfig previous = ACTIVE.getAndSet(null);
        if (previous != null) {
            String esConfigPath = previous.esConfigDir != null ? previous.esConfigDir.toAbsolutePath().normalize().toString() : "";
            ActiveBattlefieldSnapshot snapshot = new ActiveBattlefieldSnapshot(previous.mapFolder, previous.displayName, previous.dimensionKey, esConfigPath, previous.esPoints != null ? previous.esPoints.tacticalMapJson : "", previous.esPoints != null ? previous.esPoints.capturePointsJson : "", previous.esPoints != null ? previous.esPoints.backgroundImage : "", previous.esPoints != null ? previous.esPoints.backgroundBytes() : new byte[]{}, previous.esPoints != null ? previous.esPoints.backgroundSha256 : "", previous.esPoints != null ? previous.esPoints.backgroundWidth : 0, previous.esPoints != null ? previous.esPoints.backgroundHeight : 0, previous.esPoints != null ? previous.esPoints.objectiveMode : "", previous.esPoints != null ? previous.esPoints.objectiveLane : "", previous.esPoints != null ? previous.esPoints.objectiveSeed : 0L);
            MinecraftForge.EVENT_BUS.post((Event)new BattlefieldLifecycleEvent.Cleared(snapshot));
            resolvedObjectiveMode = "";
            resolvedObjectiveLane = "";
            Espetro.LOGGER.info("\u5df2\u6e05\u9664\u6d3b\u52a8\u6218\u573a\u914d\u7f6e");
        }
    }

    public static Optional<ActiveMapConfig> get() {
        return Optional.ofNullable(ACTIVE.get());
    }

    @Nullable
    public static ActiveMapConfig getOrNull() {
        return ACTIVE.get();
    }

    public static boolean isActive() {
        return ACTIVE.get() != null;
    }

    public static String getObjectiveMode() {
        if (resolvedObjectiveMode != null && !resolvedObjectiveMode.isBlank()) {
            return resolvedObjectiveMode;
        }
        ActiveMapConfig config = ACTIVE.get();
        return config == null || config.esPoints == null ? "" : config.esPoints.objectiveMode;
    }

    public static void setResolvedObjective(String mode, String laneId) {
        resolvedObjectiveMode = mode == null ? "" : mode.trim();
        resolvedObjectiveLane = laneId == null ? "" : laneId.trim();
    }

    public static String getObjectiveLane() {
        return resolvedObjectiveLane == null ? "" : resolvedObjectiveLane;
    }

    public static long getSessionId() {
        return SESSION_ID.get();
    }

    public static Optional<ResourceKey<Level>> getActiveDimensionKey() {
        ActiveMapConfig cfg = ACTIVE.get();
        return cfg == null ? Optional.empty() : Optional.of(cfg.dimensionKey);
    }

    public static boolean isActiveBattlefield(@Nullable ServerLevel level) {
        if (level == null) {
            return false;
        }
        ActiveMapConfig cfg = ACTIVE.get();
        return cfg != null && cfg.dimensionKey.equals(level.m_46472_());
    }

    public static boolean isActiveBattlefield(ResourceKey<Level> key) {
        ActiveMapConfig cfg = ACTIVE.get();
        return cfg != null && cfg.dimensionKey.equals(key);
    }

    public static ServerLevel requireBattlefield(MinecraftServer server) {
        ActiveMapConfig cfg = ACTIVE.get();
        if (cfg != null) {
            ServerLevel level = server.m_129880_(cfg.dimensionKey);
            if (level != null) {
                return level;
            }
            Espetro.LOGGER.error("\u6d3b\u52a8\u6218\u573a\u7ef4\u5ea6\u672a\u52a0\u8f7d: {}", (Object)cfg.dimensionId);
        }
        return server.m_129783_();
    }

    public static void setLastRoundWinner(String winner) {
        lastRoundWinner = winner;
    }

    public static String getLastRoundWinner() {
        return lastRoundWinner;
    }
}

