/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.logging.LogUtils
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraftforge.event.AddReloadListenerEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.slf4j.Logger
 */
package com.redabysslucia.dragonrise_reforge.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.logging.LogUtils;
import com.redabysslucia.dragonrise_reforge.config.SupplyStationConfig;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLPaths;
import org.slf4j.Logger;

@Mod.EventBusSubscriber(modid="dragonrise_reforge")
public class SupplyStationDataLoader
extends SimpleJsonResourceReloadListener {
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setLenient().create();
    private static final String DIRECTORY = "supply_station";
    private static final Path CONFIG_DIR = FMLPaths.CONFIGDIR.get().resolve("dragonrise_reforge").resolve("supply_station");
    private static volatile Map<String, SupplyStationConfig> loadedConfigs = new HashMap<String, SupplyStationConfig>();

    public SupplyStationDataLoader() {
        super(GSON, DIRECTORY);
    }

    protected Map<ResourceLocation, JsonElement> m_5944_(ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        HashMap map = Maps.newHashMap();
        this.loadFromResourceManager(pResourceManager, map);
        this.loadFromFilesystem(map);
        LOGGER.info("Supply station: found {} config source(s) for reload", (Object)map.size());
        return map;
    }

    private void loadFromResourceManager(ResourceManager resourceManager, Map<ResourceLocation, JsonElement> map) {
        int i = DIRECTORY.length() + 1;
        for (Map.Entry entry : resourceManager.m_214159_(DIRECTORY, loc -> loc.m_135815_().endsWith(".json")).entrySet()) {
            ResourceLocation resourceLocation = (ResourceLocation)entry.getKey();
            String path = resourceLocation.m_135815_();
            ResourceLocation fileId = new ResourceLocation(resourceLocation.m_135827_(), path.substring(i, path.length() - ".json".length()));
            if (map.containsKey(fileId)) continue;
            try {
                BufferedReader rawReader = ((Resource)entry.getValue()).m_215508_();
                try {
                    String stripped = SupplyStationDataLoader.stripJsonComments(rawReader);
                    JsonElement jsonElement = JsonParser.parseString((String)stripped);
                    if (jsonElement == null) continue;
                    map.put(fileId, jsonElement);
                    LOGGER.debug("Loaded from datapack: {}", (Object)fileId);
                }
                finally {
                    if (rawReader == null) continue;
                    ((Reader)rawReader).close();
                }
            }
            catch (Exception e) {
                LOGGER.error("Couldn't parse supply station config from datapack: {}", (Object)fileId, (Object)e);
            }
        }
    }

    private void loadFromFilesystem(Map<ResourceLocation, JsonElement> map) {
        if (!Files.exists(CONFIG_DIR, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> files = Files.list(CONFIG_DIR);){
            files.filter(f -> f.toString().endsWith(".json")).forEach(file -> {
                String fileName = file.getFileName().toString();
                String id = fileName.substring(0, fileName.length() - ".json".length());
                ResourceLocation fileId = new ResourceLocation("dragonrise_reforge", id);
                try {
                    String content = Files.readString(file);
                    String stripped = SupplyStationDataLoader.stripJsonCommentsString(content);
                    JsonElement jsonElement = JsonParser.parseString((String)stripped);
                    if (jsonElement != null) {
                        map.put(fileId, jsonElement);
                        LOGGER.info("Loaded from config dir: {} (overrides datapack)", (Object)fileId);
                    }
                }
                catch (Exception e) {
                    LOGGER.error("Couldn't parse supply station config from filesystem: {}", file, (Object)e);
                }
            });
        }
        catch (Exception e) {
            LOGGER.error("Couldn't list supply station config dir: {}", (Object)CONFIG_DIR, (Object)e);
        }
    }

    protected void apply(Map<ResourceLocation, JsonElement> data, ResourceManager resourceManager, ProfilerFiller profiler) {
        HashMap<String, SupplyStationConfig> newConfigs = new HashMap<String, SupplyStationConfig>();
        for (Map.Entry<ResourceLocation, JsonElement> entry : data.entrySet()) {
            ResourceLocation location = entry.getKey();
            String key = location.toString();
            try {
                SupplyStationConfig config = (SupplyStationConfig)GSON.fromJson(entry.getValue(), SupplyStationConfig.class);
                newConfigs.put(key, config);
                LOGGER.debug("Applied supply station config: {}", (Object)key);
            }
            catch (Exception e) {
                LOGGER.error("Failed to parse supply station config: {}", (Object)key, (Object)e);
            }
        }
        loadedConfigs = newConfigs;
        LOGGER.info("Loaded {} supply station config(s)", (Object)loadedConfigs.size());
    }

    private static String stripJsonComments(Reader reader) throws IOException {
        String line;
        BufferedReader br;
        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = br = reader instanceof BufferedReader ? (BufferedReader)reader : new BufferedReader(reader);
        while ((line = br.readLine()) != null) {
            String trimmed = line.strip();
            if (trimmed.startsWith("//")) continue;
            if (trimmed.startsWith("/*")) {
                while (line != null && !line.strip().endsWith("*/")) {
                    line = br.readLine();
                }
                continue;
            }
            sb.append(line).append('\n');
        }
        return sb.toString();
    }

    private static String stripJsonCommentsString(String content) {
        try {
            return SupplyStationDataLoader.stripJsonComments(new StringReader(content));
        }
        catch (IOException e) {
            return content;
        }
    }

    public static SupplyStationConfig getConfig() {
        String ownId = "dragonrise_reforge:default";
        SupplyStationConfig config = loadedConfigs.get(ownId);
        if (config != null) {
            return config;
        }
        if (!loadedConfigs.isEmpty()) {
            return loadedConfigs.values().iterator().next();
        }
        return new SupplyStationConfig();
    }

    @SubscribeEvent
    public static void onAddReloadListener(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)new SupplyStationDataLoader());
    }
}

