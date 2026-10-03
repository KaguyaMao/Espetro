/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  cpw.mods.modlauncher.api.LamdbaExceptionUtils
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.common.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.loot.IGlobalLootModifier;

public abstract class GlobalLootModifierProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final String modid;
    private final Map<String, JsonElement> toSerialize = new HashMap<String, JsonElement>();
    private boolean replace = false;

    public GlobalLootModifierProvider(PackOutput output, String modid) {
        this.output = output;
        this.modid = modid;
    }

    protected void replacing() {
        this.replace = true;
    }

    protected abstract void start();

    public CompletableFuture<?> m_213708_(CachedOutput cache) {
        this.start();
        Path forgePath = this.output.m_247566_(PackOutput.Target.DATA_PACK).resolve("forge").resolve("loot_modifiers").resolve("global_loot_modifiers.json");
        Path modifierFolderPath = this.output.m_247566_(PackOutput.Target.DATA_PACK).resolve(this.modid).resolve("loot_modifiers");
        ArrayList entries = new ArrayList();
        ImmutableList.Builder futuresBuilder = new ImmutableList.Builder();
        this.toSerialize.forEach(LamdbaExceptionUtils.rethrowBiConsumer((name, json) -> {
            entries.add(new ResourceLocation(this.modid, name));
            Path modifierPath = modifierFolderPath.resolve(name + ".json");
            futuresBuilder.add((Object)DataProvider.m_253162_((CachedOutput)cache, (JsonElement)json, (Path)modifierPath));
        }));
        JsonObject forgeJson = new JsonObject();
        forgeJson.addProperty("replace", Boolean.valueOf(this.replace));
        forgeJson.add("entries", GSON.toJsonTree(entries.stream().map(ResourceLocation::toString).collect(Collectors.toList())));
        futuresBuilder.add((Object)DataProvider.m_253162_((CachedOutput)cache, (JsonElement)forgeJson, (Path)forgePath));
        return CompletableFuture.allOf((CompletableFuture[])futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    public <T extends IGlobalLootModifier> void add(String modifier, T instance) {
        JsonElement json = (JsonElement)IGlobalLootModifier.DIRECT_CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, instance).getOrThrow(false, s -> {});
        this.toSerialize.put(modifier, json);
    }

    public String m_6055_() {
        return "Global Loot Modifiers : " + this.modid;
    }
}

