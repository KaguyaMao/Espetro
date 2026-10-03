/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.logging.LogUtils
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DynamicOps
 *  cpw.mods.modlauncher.api.LamdbaExceptionUtils
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.PackOutput$Target
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.PackType
 *  org.slf4j.Logger
 */
package net.minecraftforge.common.data;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DynamicOps;
import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.slf4j.Logger;

public class JsonCodecProvider<T>
implements DataProvider {
    private static final Logger LOGGER = LogUtils.getLogger();
    protected final PackOutput output;
    protected final ExistingFileHelper existingFileHelper;
    protected final String modid;
    protected final DynamicOps<JsonElement> dynamicOps;
    protected final PackType packType;
    protected final String directory;
    protected final Codec<T> codec;
    protected final Map<ResourceLocation, T> entries;
    protected Map<ResourceLocation, ICondition[]> conditions = Collections.emptyMap();

    public JsonCodecProvider(PackOutput output, ExistingFileHelper existingFileHelper, String modid, DynamicOps<JsonElement> dynamicOps, PackType packType, String directory, Codec<T> codec, Map<ResourceLocation, T> entries) {
        ExistingFileHelper.ResourceType resourceType = new ExistingFileHelper.ResourceType(packType, ".json", directory);
        for (ResourceLocation id : entries.keySet()) {
            existingFileHelper.trackGenerated(id, resourceType);
        }
        this.output = output;
        this.existingFileHelper = existingFileHelper;
        this.modid = modid;
        this.dynamicOps = dynamicOps;
        this.packType = packType;
        this.directory = directory;
        this.codec = codec;
        this.entries = entries;
    }

    public CompletableFuture<?> m_213708_(CachedOutput cache) {
        Path outputFolder = this.output.m_247566_(this.packType == PackType.CLIENT_RESOURCES ? PackOutput.Target.RESOURCE_PACK : PackOutput.Target.DATA_PACK);
        ImmutableList.Builder futuresBuilder = new ImmutableList.Builder();
        this.gather(LamdbaExceptionUtils.rethrowBiConsumer((id, value) -> {
            Path path = outputFolder.resolve(id.m_135827_()).resolve(this.directory).resolve(id.m_135815_() + ".json");
            JsonElement encoded = (JsonElement)this.codec.encodeStart(this.dynamicOps, value).getOrThrow(false, msg -> LOGGER.error("Failed to encode {}: {}", (Object)path, msg));
            ICondition[] conditions = this.conditions.get(id);
            if (conditions != null && conditions.length > 0) {
                if (encoded instanceof JsonObject) {
                    JsonObject obj = (JsonObject)encoded;
                    obj.add("forge:conditions", (JsonElement)CraftingHelper.serialize(conditions));
                } else {
                    LOGGER.error("Attempted to apply conditions to a type that is not a JsonObject! - Path: {}", (Object)path);
                }
            }
            futuresBuilder.add((Object)DataProvider.m_253162_((CachedOutput)cache, (JsonElement)encoded, (Path)path));
        }));
        return CompletableFuture.allOf((CompletableFuture[])futuresBuilder.build().toArray(CompletableFuture[]::new));
    }

    protected void gather(BiConsumer<ResourceLocation, T> consumer) {
        this.entries.forEach(consumer);
    }

    public String m_6055_() {
        return String.format("%s generator for %s", this.directory, this.modid);
    }

    public JsonCodecProvider<T> setConditions(Map<ResourceLocation, ICondition[]> conditions) {
        this.conditions = conditions;
        return this;
    }
}

