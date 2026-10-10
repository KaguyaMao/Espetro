/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonParser
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  org.espetro.api.ActiveBattlefieldSnapshot
 *  org.espetro.api.EspetroAPI
 *  org.espetro.api.event.BattlefieldLifecycleEvent$Activated
 *  org.espetro.api.event.BattlefieldLifecycleEvent$Cleared
 *  org.espetro.api.event.GamePhaseChangedEvent
 *  org.espetro.team.GamePhase
 */
package com.example.espoints.integration;

import com.example.espoints.ESPointsMod;
import com.example.espoints.api.ESPointsAPI;
import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.config.PointsPresetLoader;
import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.config.TeamfightJsonConfig;
import com.example.espoints.network.RequestRateLimiter;
import com.example.espoints.network.RequestTacticalMapTileMessage;
import com.example.espoints.network.SyncTacticalMapBackgroundMessage;
import com.example.espoints.network.SyncTacticalMapConfigMessage;
import com.example.espoints.tile.TacticalMapTileService;
import com.google.gson.JsonParser;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.espetro.api.ActiveBattlefieldSnapshot;
import org.espetro.api.EspetroAPI;
import org.espetro.api.event.BattlefieldLifecycleEvent;
import org.espetro.api.event.GamePhaseChangedEvent;
import org.espetro.team.GamePhase;

public final class EspetroBattlefieldIntegration {
    @SubscribeEvent
    public void onBattlefieldActivated(BattlefieldLifecycleEvent.Activated event) {
        ActiveBattlefieldSnapshot snapshot = event.snapshot();
        TacticalMapTileService.get().activate(snapshot);
        CapturePointManager manager = CapturePointManager.getInstance();
        manager.onBattlefieldActivated();
        Path esConfig = EspetroBattlefieldIntegration.resolveEsConfig(snapshot);
        if (esConfig == null) {
            ESPointsMod.LOGGER.error("\u6d3b\u52a8\u5730\u56fe\u7f3a\u5c11 EsConfig \u8def\u5f84: {}", (Object)snapshot.mapId());
            return;
        }
        try {
            Path tacticalPath = esConfig.resolve("TacticalMap.json");
            String tacticalJson = Files.readString(tacticalPath, StandardCharsets.UTF_8);
            TacticalMapJsonConfig.apply(TacticalMapJsonConfig.fromJson(JsonParser.parseString((String)tacticalJson)), tacticalPath.toString());
        }
        catch (Exception e) {
            ESPointsMod.LOGGER.error("\u6d3b\u52a8\u5730\u56fe\u6218\u672f\u5730\u56fe\u914d\u7f6e\u65e0\u6cd5\u5e94\u7528: {}", (Object)snapshot.mapId(), (Object)e);
            return;
        }
        try {
            long seed = snapshot.objectiveSeed();
            String configuredMode = PointsPresetLoader.readConfiguredMode(esConfig);
            PointsPresetLoader.Selection preset = PointsPresetLoader.select(esConfig, configuredMode, seed);
            TeamfightJsonConfig.LoadResult points = TeamfightJsonConfig.loadFrozenSnapshot(snapshot.mapId(), preset.json(), seed);
            if (!points.isSuccess()) {
                ESPointsMod.LOGGER.error("\u6d3b\u52a8\u5730\u56fe\u636e\u70b9\u914d\u7f6e\u65e0\u6cd5\u5e94\u7528: {} ({})", (Object)snapshot.mapId(), (Object)points.getMessage());
                return;
            }
            String mode = CapturePointManager.getInstance().isRaasFrontline() ? "RAAS" : preset.mode();
            String lane = TeamfightJsonConfig.getLastSelectedLaneId();
            try {
                EspetroAPI.setResolvedObjectiveMode((String)mode, (String)(lane == null ? "" : lane));
            }
            catch (Throwable t) {
                ESPointsMod.LOGGER.debug("\u56de\u5199 objectiveMode \u5931\u8d25: {}", (Object)t.toString());
            }
            ESPointsAPI.refreshCachedMode(mode);
            ESPointsMod.LOGGER.info("\u636e\u70b9\u9884\u8bbe: {} (mode={})", (Object)preset.sourceName(), (Object)mode);
        }
        catch (Exception e) {
            ESPointsMod.LOGGER.error("\u6d3b\u52a8\u5730\u56fe\u636e\u70b9\u914d\u7f6e\u65e0\u6cd5\u5e94\u7528: {}", (Object)snapshot.mapId(), (Object)e);
            return;
        }
        SyncTacticalMapConfigMessage.broadcastToAll();
        SyncTacticalMapBackgroundMessage.broadcastToAll();
        ESPointsMod.LOGGER.info("ESPoints \u5df2\u4ece EsConfig/Points \u88c5\u8f7d\u5730\u56fe: {} ({})", (Object)snapshot.mapId(), (Object)esConfig);
    }

    private static Path resolveEsConfig(ActiveBattlefieldSnapshot snapshot) {
        Path path;
        if (snapshot.esConfigPath() != null && !snapshot.esConfigPath().isBlank() && Files.isDirectory(path = Path.of(snapshot.esConfigPath(), new String[0]), new LinkOption[0])) {
            return path;
        }
        Path fallback = Path.of("EsWorld", snapshot.mapId(), "EsConfig");
        return Files.isDirectory(fallback, new LinkOption[0]) ? fallback : null;
    }

    @SubscribeEvent
    public void onBattlefieldCleared(BattlefieldLifecycleEvent.Cleared event) {
        CapturePointManager.getInstance().onBattlefieldCleared();
        TacticalMapTileService.get().clear();
        RequestTacticalMapTileMessage.clearAll();
        RequestRateLimiter.clearAll();
        TeamfightJsonConfig.clearFrozenSnapshot();
        TacticalMapJsonConfig.apply(TacticalMapJsonConfig.createDefault(), "no active Espetro battlefield");
        SyncTacticalMapConfigMessage.broadcastToAll();
        SyncTacticalMapBackgroundMessage.broadcastToAll();
        ESPointsAPI.refreshCachedMode("");
        ESPointsMod.LOGGER.info("ESPoints \u5df2\u6e05\u9664\u5730\u56fe\u72b6\u6001: {}", (Object)event.snapshot().mapId());
    }

    @SubscribeEvent
    public void onGamePhaseChanged(GamePhaseChangedEvent event) {
        if (event.current() == GamePhase.DEPLOYING) {
            CapturePointManager.getInstance().onEspetroDeployingStarted();
        } else if (event.current() == GamePhase.CLEANUP) {
            CapturePointManager.getInstance().onBattlefieldCleared();
        }
    }
}

