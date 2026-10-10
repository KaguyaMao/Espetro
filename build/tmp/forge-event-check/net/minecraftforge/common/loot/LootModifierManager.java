/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.profiling.ProfilerFiller
 *  net.minecraft.world.level.storage.loot.Deserializers
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraftforge.common.loot;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.storage.loot.Deserializers;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LootModifierManager
extends SimpleJsonResourceReloadListener {
    public static final Logger LOGGER = LogManager.getLogger();
    public static final Gson GSON_INSTANCE = Deserializers.m_78799_().create();
    private Map<ResourceLocation, IGlobalLootModifier> registeredLootModifiers = ImmutableMap.of();
    private static final String folder = "loot_modifiers";

    public LootModifierManager() {
        super(GSON_INSTANCE, folder);
    }

    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManagerIn, ProfilerFiller profilerIn) {
        ImmutableMap.Builder builder = ImmutableMap.builder();
        ArrayList<ResourceLocation> finalLocations = new ArrayList<ResourceLocation>();
        ResourceLocation resourcelocation = new ResourceLocation("forge", "loot_modifiers/global_loot_modifiers.json");
        for (Resource iresource : resourceManagerIn.m_213829_(resourcelocation)) {
            try {
                InputStream inputstream = iresource.m_215507_();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputstream, StandardCharsets.UTF_8));){
                    JsonObject jsonobject = (JsonObject)GsonHelper.m_13776_((Gson)GSON_INSTANCE, (Reader)reader, JsonObject.class);
                    boolean replace = jsonobject.get("replace").getAsBoolean();
                    if (replace) {
                        finalLocations.clear();
                    }
                    JsonArray entryList = jsonobject.get("entries").getAsJsonArray();
                    for (JsonElement entry : entryList) {
                        ResourceLocation loc = new ResourceLocation(entry.getAsString());
                        finalLocations.remove(loc);
                        finalLocations.add(loc);
                    }
                }
                finally {
                    if (inputstream == null) continue;
                    inputstream.close();
                }
            }
            catch (IOException | RuntimeException ioexception) {
                LOGGER.error("Couldn't read global loot modifier list {} in data pack {}", (Object)resourcelocation, (Object)iresource.m_215506_(), (Object)ioexception);
            }
        }
        for (ResourceLocation location : finalLocations) {
            JsonElement json = resourceList.get(location);
            IGlobalLootModifier.DIRECT_CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)json).resultOrPartial(errorMsg -> LOGGER.warn("Could not decode GlobalLootModifier with json id {} - error: {}", (Object)location, errorMsg)).ifPresent(modifier -> builder.put((Object)location, modifier));
        }
        this.registeredLootModifiers = builder.build();
    }

    public Collection<IGlobalLootModifier> getAllLootMods() {
        return this.registeredLootModifiers.values();
    }
}

