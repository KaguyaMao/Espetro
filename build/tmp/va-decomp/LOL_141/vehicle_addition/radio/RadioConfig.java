/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraft.sounds.SoundSource
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.radio;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import net.minecraft.sounds.SoundSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RadioConfig {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Paths.get("config", "vehicle_addition", "radio_config.json");
    private static ConfigData data = new ConfigData();

    private RadioConfig() {
    }

    public static void load() {
        block9: {
            try {
                if (!Files.exists(CONFIG_PATH, new LinkOption[0])) break block9;
                try (BufferedReader reader = Files.newBufferedReader(CONFIG_PATH, StandardCharsets.UTF_8);){
                    JsonObject obj = JsonParser.parseReader((Reader)reader).getAsJsonObject();
                    ConfigData loaded = (ConfigData)GSON.fromJson((JsonElement)obj, ConfigData.class);
                    if (loaded != null) {
                        data = loaded;
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error("Failed to load radio config, using defaults", (Throwable)e);
                data = new ConfigData();
            }
        }
        RadioConfig.saveSilently();
        LOGGER.info("Radio config ready: maxPlaylistSongs = {}, soundCategory = {}", (Object)RadioConfig.data.maxPlaylistSongs, (Object)RadioConfig.data.soundCategory);
    }

    public static int getMaxPlaylistSongs() {
        return RadioConfig.data.maxPlaylistSongs;
    }

    public static SoundSource getSoundCategory() {
        try {
            SoundSource source = SoundSource.valueOf((String)RadioConfig.data.soundCategory);
            return source == null ? SoundSource.RECORDS : source;
        }
        catch (Exception e) {
            return SoundSource.RECORDS;
        }
    }

    public static void setMaxPlaylistSongs(int max) {
        RadioConfig.data.maxPlaylistSongs = Math.max(1, max);
        RadioConfig.saveSilently();
    }

    public static void setSoundCategory(SoundSource source) {
        RadioConfig.data.soundCategory = source == null ? SoundSource.RECORDS.name() : source.name();
        RadioConfig.saveSilently();
    }

    private static void saveSilently() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_PATH, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson((Object)data, (Appendable)writer);
            }
        }
        catch (IOException e) {
            LOGGER.error("Failed to save radio config", (Throwable)e);
        }
    }

    public static class ConfigData {
        public int maxPlaylistSongs = 50;
        public String soundCategory = SoundSource.RECORDS.name();
    }
}

