/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  net.minecraft.client.Minecraft
 *  net.minecraft.resources.ResourceLocation
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package LOL_141.vehicle_addition.radio;

import LOL_141.vehicle_addition.radio.RadioChannel;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class RadioStationManager {
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_FILE = Paths.get("config", "vehicle_addition", "radio_stations.json");
    private static final String BUNDLED_STATIONS = "radio_stations.json";
    private static final List<RadioChannel> STATIONS = new ArrayList<RadioChannel>();

    private RadioStationManager() {
    }

    public static void load() {
        STATIONS.clear();
        if (Files.exists(CONFIG_FILE, new LinkOption[0])) {
            try (BufferedReader reader = Files.newBufferedReader(CONFIG_FILE, StandardCharsets.UTF_8);){
                RadioChannel[] arr = (RadioChannel[])GSON.fromJson((Reader)reader, RadioChannel[].class);
                if (arr != null) {
                    for (RadioChannel channel : arr) {
                        if (channel == null || channel.name() == null || channel.url() == null || channel.url().isBlank()) continue;
                        STATIONS.add(channel);
                    }
                }
            }
            catch (Exception e) {
                LOGGER.error("Failed to load radio stations from config, falling back to bundled", (Throwable)e);
                STATIONS.clear();
            }
        }
        if (STATIONS.isEmpty()) {
            RadioStationManager.loadBundled();
        }
        RadioStationManager.saveSilently();
        LOGGER.info("Radio stations ready: {} channels", (Object)STATIONS.size());
    }

    private static void loadBundled() {
        try {
            InputStream resource = Minecraft.m_91087_().m_91098_().m_215595_(new ResourceLocation("vehicle_addition", BUNDLED_STATIONS));
            try (InputStreamReader reader = new InputStreamReader(resource, StandardCharsets.UTF_8);){
                RadioChannel[] arr = (RadioChannel[])GSON.fromJson((Reader)reader, RadioChannel[].class);
                if (arr != null) {
                    Collections.addAll(STATIONS, arr);
                }
            }
        }
        catch (Exception e) {
            LOGGER.error("Failed to load bundled radio stations", (Throwable)e);
        }
    }

    public static List<RadioChannel> getStations() {
        return Collections.unmodifiableList(STATIONS);
    }

    public static boolean addStation(String name, String url) {
        if (name == null || name.isBlank() || url == null || url.isBlank()) {
            return false;
        }
        String trimmedUrl = url.trim();
        if (!trimmedUrl.startsWith("http") && !trimmedUrl.startsWith("netease_playlist:")) {
            return false;
        }
        for (RadioChannel channel : STATIONS) {
            if (!channel.name().equals(name.trim())) continue;
            return false;
        }
        STATIONS.add(new RadioChannel(name.trim(), trimmedUrl));
        RadioStationManager.saveSilently();
        return true;
    }

    public static void removeStation(int index) {
        if (index >= 0 && index < STATIONS.size()) {
            STATIONS.remove(index);
            RadioStationManager.saveSilently();
        }
    }

    public static void saveSilently() {
        try {
            Files.createDirectories(CONFIG_FILE.getParent(), new FileAttribute[0]);
            try (BufferedWriter writer = Files.newBufferedWriter(CONFIG_FILE, StandardCharsets.UTF_8, new OpenOption[0]);){
                GSON.toJson(STATIONS, (Appendable)writer);
            }
        }
        catch (IOException e) {
            LOGGER.error("Failed to save radio stations", (Throwable)e);
        }
    }
}

