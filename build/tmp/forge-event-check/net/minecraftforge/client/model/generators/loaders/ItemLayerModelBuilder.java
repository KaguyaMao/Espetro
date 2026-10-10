/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap$Entry
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  it.unimi.dsi.fastutil.ints.IntOpenHashSet
 *  it.unimi.dsi.fastutil.ints.IntSet
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model.generators.loaders;

import com.google.common.base.Preconditions;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.generators.CustomLoaderBuilder;
import net.minecraftforge.client.model.generators.ModelBuilder;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemLayerModelBuilder<T extends ModelBuilder<T>>
extends CustomLoaderBuilder<T> {
    private final Int2ObjectMap<ForgeFaceData> faceData = new Int2ObjectOpenHashMap();
    private final Map<ResourceLocation, IntSet> renderTypes = new LinkedHashMap<ResourceLocation, IntSet>();
    private final Map<ResourceLocation, IntSet> renderTypesFast = new LinkedHashMap<ResourceLocation, IntSet>();
    private final IntSet layersWithRenderTypes = new IntOpenHashSet();

    public static <T extends ModelBuilder<T>> ItemLayerModelBuilder<T> begin(T parent, ExistingFileHelper existingFileHelper) {
        return new ItemLayerModelBuilder<T>(parent, existingFileHelper);
    }

    protected ItemLayerModelBuilder(T parent, ExistingFileHelper existingFileHelper) {
        super(new ResourceLocation("forge:item_layers"), parent, existingFileHelper);
    }

    public ItemLayerModelBuilder<T> emissive(int blockLight, int skyLight, int ... layers) {
        Preconditions.checkNotNull((Object)layers, (Object)"Layers must not be null");
        Preconditions.checkArgument((layers.length > 0 ? 1 : 0) != 0, (Object)"At least one layer must be specified");
        Preconditions.checkArgument((boolean)Arrays.stream(layers).allMatch(i -> i >= 0), (Object)"All layers must be >= 0");
        for (int i2 : layers) {
            this.faceData.compute(i2, (key, value) -> {
                ForgeFaceData fallback = value == null ? ForgeFaceData.DEFAULT : value;
                return new ForgeFaceData(fallback.color(), blockLight, skyLight, fallback.ambientOcclusion(), fallback.calculateNormals());
            });
        }
        return this;
    }

    public ItemLayerModelBuilder<T> color(int color, int ... layers) {
        Preconditions.checkNotNull((Object)layers, (Object)"Layers must not be null");
        Preconditions.checkArgument((layers.length > 0 ? 1 : 0) != 0, (Object)"At least one layer must be specified");
        Preconditions.checkArgument((boolean)Arrays.stream(layers).allMatch(i -> i >= 0), (Object)"All layers must be >= 0");
        for (int i2 : layers) {
            this.faceData.compute(i2, (key, value) -> {
                ForgeFaceData fallback = value == null ? ForgeFaceData.DEFAULT : value;
                return new ForgeFaceData(color, fallback.blockLight(), fallback.skyLight(), fallback.ambientOcclusion(), fallback.calculateNormals());
            });
        }
        return this;
    }

    public ItemLayerModelBuilder<T> renderType(String renderType, int ... layers) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        ResourceLocation asLoc = renderType.contains(":") ? new ResourceLocation(renderType) : new ResourceLocation(this.parent.getLocation().m_135827_(), renderType);
        return this.renderType(asLoc, layers);
    }

    public ItemLayerModelBuilder<T> renderType(String renderType, String renderTypeFast, int ... layers) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        Preconditions.checkNotNull((Object)renderTypeFast, (Object)"Fast graphics render type must not be null");
        ResourceLocation asLoc = renderType.contains(":") ? new ResourceLocation(renderType) : new ResourceLocation(this.parent.getLocation().m_135827_(), renderType);
        ResourceLocation asLocFast = renderTypeFast.contains(":") ? new ResourceLocation(renderTypeFast) : new ResourceLocation(this.parent.getLocation().m_135827_(), renderTypeFast);
        return this.renderType(asLoc, asLocFast, layers);
    }

    public ItemLayerModelBuilder<T> renderType(ResourceLocation renderType, int ... layers) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        Preconditions.checkNotNull((Object)layers, (Object)"Layers must not be null");
        Preconditions.checkArgument((layers.length > 0 ? 1 : 0) != 0, (Object)"At least one layer must be specified");
        Preconditions.checkArgument((boolean)Arrays.stream(layers).allMatch(i -> i >= 0), (Object)"All layers must be >= 0");
        int[] alreadyAssigned = Arrays.stream(layers).filter(arg_0 -> ((IntSet)this.layersWithRenderTypes).contains(arg_0)).toArray();
        Preconditions.checkArgument((alreadyAssigned.length == 0 ? 1 : 0) != 0, (Object)("Attempted to re-assign layer render types: " + Arrays.toString(alreadyAssigned)));
        IntSet renderTypeLayers = this.renderTypes.computeIfAbsent(renderType, $ -> new IntOpenHashSet());
        Arrays.stream(layers).forEach(layer -> {
            renderTypeLayers.add(layer);
            this.layersWithRenderTypes.add(layer);
        });
        return this;
    }

    public ItemLayerModelBuilder<T> renderType(ResourceLocation renderType, ResourceLocation renderTypeFast, int ... layers) {
        Preconditions.checkNotNull((Object)renderType, (Object)"Render type must not be null");
        Preconditions.checkNotNull((Object)renderTypeFast, (Object)"Fast graphics render type must not be null");
        Preconditions.checkNotNull((Object)layers, (Object)"Layers must not be null");
        Preconditions.checkArgument((layers.length > 0 ? 1 : 0) != 0, (Object)"At least one layer must be specified");
        Preconditions.checkArgument((boolean)Arrays.stream(layers).allMatch(i -> i >= 0), (Object)"All layers must be >= 0");
        int[] alreadyAssigned = Arrays.stream(layers).filter(arg_0 -> ((IntSet)this.layersWithRenderTypes).contains(arg_0)).toArray();
        Preconditions.checkArgument((alreadyAssigned.length == 0 ? 1 : 0) != 0, (Object)("Attempted to re-assign layer render types: " + Arrays.toString(alreadyAssigned)));
        IntSet renderTypeLayers = this.renderTypes.computeIfAbsent(renderType, $ -> new IntOpenHashSet());
        IntSet renderTypeFastLayers = this.renderTypesFast.computeIfAbsent(renderType, $ -> new IntOpenHashSet());
        Arrays.stream(layers).forEach(layer -> {
            renderTypeLayers.add(layer);
            renderTypeFastLayers.add(layer);
            this.layersWithRenderTypes.add(layer);
        });
        return this;
    }

    @Override
    public JsonObject toJson(JsonObject json) {
        json = super.toJson(json);
        JsonObject forgeData = new JsonObject();
        JsonObject layerObj = new JsonObject();
        for (Int2ObjectMap.Entry entry : this.faceData.int2ObjectEntrySet()) {
            layerObj.add(String.valueOf(entry.getIntKey()), (JsonElement)ForgeFaceData.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)((ForgeFaceData)entry.getValue())).getOrThrow(false, s -> {}));
        }
        forgeData.add("layers", (JsonElement)layerObj);
        json.add("forge_data", (JsonElement)forgeData);
        JsonObject renderTypes = new JsonObject();
        this.renderTypes.forEach((renderType, layers) -> {
            JsonArray array = new JsonArray();
            layers.intStream().sorted().forEach(arg_0 -> ((JsonArray)array).add(arg_0));
            renderTypes.add(renderType.toString(), (JsonElement)array);
        });
        json.add("render_types", (JsonElement)renderTypes);
        JsonObject renderTypesFast = new JsonObject();
        this.renderTypesFast.forEach((renderTypeFast, layers) -> {
            JsonArray array = new JsonArray();
            layers.intStream().sorted().forEach(arg_0 -> ((JsonArray)array).add(arg_0));
            renderTypesFast.add(renderTypeFast.toString(), (JsonElement)array);
        });
        json.add("render_types_fast", (JsonElement)renderTypesFast);
        return json;
    }
}

