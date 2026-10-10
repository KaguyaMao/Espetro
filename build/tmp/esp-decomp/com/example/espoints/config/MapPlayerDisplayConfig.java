/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.minecraft.server.MinecraftServer
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.example.espoints.config;

import com.example.espoints.util.ModLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.ServerLifecycleHooks;

public class MapPlayerDisplayConfig {
    private static final String CONFIG_FILE_NAME = "hcr_map_player_display.json";
    private boolean showPlayerLocations = true;
    private static MapPlayerDisplayConfig instance;

    private MapPlayerDisplayConfig() {
    }

    public static synchronized MapPlayerDisplayConfig getInstance() {
        if (instance == null) {
            instance = new MapPlayerDisplayConfig();
            instance.loadConfig();
        }
        return instance;
    }

    public void loadConfig() {
        try {
            Path configPath = this.getConfigPath();
            if (Files.exists(configPath, new LinkOption[0])) {
                Gson gson = new Gson();
                String content = Files.readString(configPath, StandardCharsets.UTF_8);
                MapPlayerDisplayConfig loadedConfig = (MapPlayerDisplayConfig)gson.fromJson(content, MapPlayerDisplayConfig.class);
                if (loadedConfig != null) {
                    this.showPlayerLocations = loadedConfig.showPlayerLocations;
                    ModLogger.debug("\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u5df2\u52a0\u8f7d: showPlayerLocations = " + this.showPlayerLocations);
                }
            } else {
                this.saveConfig();
            }
        }
        catch (IOException e) {
            ModLogger.debug("\u52a0\u8f7d\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u5931\u8d25: " + e.getMessage());
        }
    }

    public void saveConfig() {
        try {
            Path configPath = this.getConfigPath();
            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            String content = gson.toJson((Object)this);
            Files.writeString(configPath, (CharSequence)content, StandardCharsets.UTF_8, new OpenOption[0]);
            ModLogger.debug("\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u5df2\u4fdd\u5b58: showPlayerLocations = " + this.showPlayerLocations);
        }
        catch (IOException e) {
            ModLogger.debug("\u4fdd\u5b58\u5730\u56fe\u73a9\u5bb6\u663e\u793a\u914d\u7f6e\u5931\u8d25: " + e.getMessage());
        }
    }

    private Path getConfigPath() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return Paths.get(server.m_6237_().getAbsolutePath(), "config", CONFIG_FILE_NAME);
        }
        return Paths.get("config", CONFIG_FILE_NAME);
    }

    public boolean isShowPlayerLocations() {
        return this.showPlayerLocations;
    }

    public void setShowPlayerLocations(boolean showPlayerLocations) {
        this.showPlayerLocations = showPlayerLocations;
        this.saveConfig();
    }
}

