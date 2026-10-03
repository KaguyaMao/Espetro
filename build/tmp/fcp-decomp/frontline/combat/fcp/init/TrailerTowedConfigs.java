/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.PreparableReloadListener
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimplePreparableReloadListener
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraftforge.event.AddReloadListenerEvent
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package frontline.combat.fcp.init;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import frontline.combat.fcp.entity.vehicle.Trailers.TrailerTowedData;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(modid="fcp")
public class TrailerTowedConfigs {
    private static final String FOLDER = "trailer_towed";
    private static final Logger LOGGER = LogManager.getLogger();
    private static final Map<ResourceLocation, TrailerTowedData> CONFIGS = new HashMap<ResourceLocation, TrailerTowedData>();

    public static TrailerTowedData get(ResourceLocation entityId) {
        return CONFIGS.get(entityId);
    }

    public static boolean has(ResourceLocation entityId) {
        return CONFIGS.containsKey(entityId);
    }

    @SubscribeEvent
    public static void onAddReloadListeners(AddReloadListenerEvent event) {
        event.addListener((PreparableReloadListener)new SimplePreparableReloadListener<Map<ResourceLocation, TrailerTowedData>>(){

            protected Map<ResourceLocation, TrailerTowedData> prepare(ResourceManager manager, ProfilerFiller profiler) {
                HashMap<ResourceLocation, TrailerTowedData> loaded = new HashMap<ResourceLocation, TrailerTowedData>();
                for (Map.Entry entry : manager.m_214159_(TrailerTowedConfigs.FOLDER, path -> path != null && path.m_135815_().endsWith(".json")).entrySet()) {
                    String rawPath;
                    ResourceLocation location = (ResourceLocation)entry.getKey();
                    Resource resource = (Resource)entry.getValue();
                    if (location == null || resource == null || (rawPath = location.m_135815_()) == null) continue;
                    try (InputStreamReader reader = new InputStreamReader(resource.m_215507_());){
                        JsonElement json = JsonParser.parseReader((Reader)reader);
                        TrailerTowedData config = (TrailerTowedData)TrailerTowedData.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)json).getOrThrow(false, err -> LOGGER.error("[FCP] Bad trailer_towed config {}: {}", (Object)location, err));
                        String stripped = rawPath.replace("trailer_towed/", "").replace(".json", "");
                        ResourceLocation key = new ResourceLocation(location.m_135827_(), stripped);
                        loaded.put(key, config);
                        LOGGER.debug("[FCP] Loaded trailer_towed config: {}", (Object)key);
                    }
                    catch (Exception e) {
                        LOGGER.error("[FCP] Failed to load trailer_towed config {}: {}", (Object)location, (Object)e.getMessage());
                    }
                }
                return loaded;
            }

            protected void apply(Map<ResourceLocation, TrailerTowedData> loaded, ResourceManager manager, ProfilerFiller profiler) {
                CONFIGS.clear();
                CONFIGS.putAll(loaded);
                LOGGER.info("[FCP] Loaded {} trailer_towed config(s)", (Object)CONFIGS.size());
            }
        });
    }
}

