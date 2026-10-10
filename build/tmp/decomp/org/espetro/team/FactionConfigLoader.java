/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonObject
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package org.espetro.team;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;
import org.espetro.team.FactionConfig;

public class FactionConfigLoader {
    private static final Gson GSON = new Gson();

    public static FactionConfig loadFaction(String factionId) {
        try {
            Path configPath = FMLPaths.GAMEDIR.get().resolve("config/espetro/factions/" + factionId + ".json");
            if (Files.exists(configPath, new LinkOption[0])) {
                String json = Files.readString(configPath);
                JsonObject jsonObject = (JsonObject)GSON.fromJson(json, JsonObject.class);
                FactionConfig config = new FactionConfig();
                if (jsonObject.has("faction")) {
                    JsonObject faction = jsonObject.getAsJsonObject("faction");
                    config.name = FactionConfigLoader.getString(faction, "name", factionId);
                    config.team = FactionConfigLoader.getString(faction, "team", "DEFEND");
                    config.icon = FactionConfigLoader.getString(faction, "icon", "");
                    config.color = FactionConfigLoader.getString(faction, "color", "FFFFFF");
                }
                return config;
            }
        }
        catch (IOException e) {
            Espetro.LOGGER.error("\u52a0\u8f7d\u9635\u8425\u914d\u7f6e\u5931\u8d25: {}", (Object)factionId, (Object)e);
        }
        return null;
    }

    private static String getString(JsonObject obj, String key, String defaultValue) {
        return obj.has(key) ? obj.get(key).getAsString() : defaultValue;
    }
}

