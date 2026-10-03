/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.espetro.mapconfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.espetro.bastion.BastionManager;
import org.espetro.config.GameConfig;
import org.espetro.logistics.LogisticsConfig;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.team.OutpostManager;
import org.espetro.team.SpawnPointConfig;
import org.espetro.team.TeamPackManager;
import org.espetro.vehicle.VehicleConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class GameConfigBridge {
    private static final Logger LOGGER = LoggerFactory.getLogger(GameConfigBridge.class);
    private static int attackBatchCompletionReinforcement = 0;

    private GameConfigBridge() {
    }

    public static int getAttackBatchCompletionReinforcement() {
        return attackBatchCompletionReinforcement;
    }

    public static void apply(ActiveMapConfig config) {
        if (config == null || !config.usable) {
            return;
        }
        GameConfig.applySnapshot(config.game);
        if (config.spawnPoints != null && config.spawnPoints.valid) {
            SpawnPointConfig.setSpawnPoint("ATTACK", config.spawnPoints.attack.x(), config.spawnPoints.attack.y(), config.spawnPoints.attack.z(), config.spawnPoints.attack.yaw());
            SpawnPointConfig.setSpawnPoint("DEFEND", config.spawnPoints.defend.x(), config.spawnPoints.defend.y(), config.spawnPoints.defend.z(), config.spawnPoints.defend.yaw());
        }
        BastionManager.getInstance().applyExternalJson(config.bastionJson);
        OutpostManager.getInstance().applyExternalJson(config.outpostsJson);
        LogisticsConfig.applyExternalJson(config.logisticsJson);
        TeamPackManager.getInstance().applyExternalJson(config.teamPackJson);
        VehicleConfig.applyActiveMap(config);
        String cpJson = config.capturePointsJson();
        if (cpJson != null && !cpJson.isEmpty()) {
            GameConfigBridge.parseCapturePointsConfig(cpJson);
        }
    }

    private static void parseCapturePointsConfig(String json) {
        try {
            int value;
            JsonObject root = JsonParser.parseString((String)json).getAsJsonObject();
            if (root.has("attackBatchCompletionReinforcement") && (value = root.get("attackBatchCompletionReinforcement").getAsInt()) > 0) {
                attackBatchCompletionReinforcement = value;
                LOGGER.info("\u4ece CapturePoints.json \u8bfb\u53d6\u8fdb\u653b\u6279\u6b21\u5956\u52b1\u5175\u529b: {}", (Object)value);
            }
        }
        catch (Exception e) {
            LOGGER.warn("\u89e3\u6790 CapturePoints.json \u4e2d\u7684 attackBatchCompletionReinforcement \u5931\u8d25: {}", (Object)e.getMessage());
        }
    }
}

