/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.math.Transformation
 *  it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectMap
 *  it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import org.jetbrains.annotations.Nullable;

public class ItemLayerModel
implements IUnbakedGeometry<ItemLayerModel> {
    @Nullable
    private ImmutableList<Material> textures;
    private final Int2ObjectMap<ForgeFaceData> layerData;
    private final Int2ObjectMap<ResourceLocation> renderTypeNames;
    private final Int2ObjectMap<ResourceLocation> renderTypeNamesFast;

    private ItemLayerModel(@Nullable ImmutableList<Material> textures, Int2ObjectMap<ForgeFaceData> layerData, Int2ObjectMap<ResourceLocation> renderTypeNames) {
        this(textures, layerData, renderTypeNames, (Int2ObjectMap<ResourceLocation>)new Int2ObjectOpenHashMap());
    }

    private ItemLayerModel(@Nullable ImmutableList<Material> textures, Int2ObjectMap<ForgeFaceData> layerData, Int2ObjectMap<ResourceLocation> renderTypeNames, Int2ObjectMap<ResourceLocation> renderTypeNamesFast) {
        this.textures = textures;
        this.layerData = layerData;
        this.renderTypeNames = renderTypeNames;
        this.renderTypeNamesFast = renderTypeNamesFast;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        if (this.textures == null) {
            ImmutableList.Builder builder = ImmutableList.builder();
            int i = 0;
            while (context.hasMaterial("layer" + i)) {
                builder.add((Object)context.getMaterial("layer" + i));
                ++i;
            }
            this.textures = builder.build();
        }
        TextureAtlasSprite particle = spriteGetter.apply(context.hasMaterial("particle") ? context.getMaterial("particle") : (Material)this.textures.get(0));
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }
        RenderTypeGroup normalRenderTypes = new RenderTypeGroup(RenderType.m_110466_(), ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
        CompositeModel.Baked.Builder builder = CompositeModel.Baked.builder(context, particle, overrides, context.getTransforms());
        for (int i = 0; i < this.textures.size(); ++i) {
            TextureAtlasSprite sprite = spriteGetter.apply((Material)this.textures.get(i));
            List<BlockElement> unbaked = UnbakedGeometryHelper.createUnbakedItemElements(i, sprite.m_245424_(), (ForgeFaceData)this.layerData.get(i));
            List<BakedQuad> quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, modelState, modelLocation);
            ResourceLocation renderTypeName = (ResourceLocation)this.renderTypeNames.get(i);
            RenderTypeGroup renderTypes = renderTypeName != null ? context.getRenderType(renderTypeName) : null;
            ResourceLocation renderTypeNameFast = (ResourceLocation)this.renderTypeNamesFast.get(i);
            RenderTypeGroup renderTypesFast = renderTypeNameFast != null ? context.getRenderType(renderTypeNameFast) : null;
            builder.addQuads(renderTypes != null ? renderTypes : normalRenderTypes, renderTypesFast != null ? renderTypesFast : RenderTypeGroup.EMPTY, quads);
        }
        return builder.build();
    }

    public static final class Loader
    implements IGeometryLoader<ItemLayerModel> {
        public static final Loader INSTANCE = new Loader();

        @Override
        public ItemLayerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            Int2ObjectOpenHashMap renderTypeNames = new Int2ObjectOpenHashMap();
            if (jsonObject.has("render_types")) {
                JsonObject renderTypes = jsonObject.getAsJsonObject("render_types");
                for (Object entry : renderTypes.entrySet()) {
                    ResourceLocation renderType = new ResourceLocation((String)entry.getKey());
                    for (JsonElement layer : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                        if (renderTypeNames.put(layer.getAsInt(), (Object)renderType) == null) continue;
                        throw new JsonParseException("Registered duplicate render type for layer " + String.valueOf(layer));
                    }
                }
            }
            Int2ObjectOpenHashMap renderTypeNamesFast = new Int2ObjectOpenHashMap();
            if (jsonObject.has("render_types_fast")) {
                JsonObject renderTypes = jsonObject.getAsJsonObject("render_types_fast");
                for (Map.Entry entry : renderTypes.entrySet()) {
                    ResourceLocation renderType = new ResourceLocation((String)entry.getKey());
                    for (JsonElement layer : ((JsonElement)entry.getValue()).getAsJsonArray()) {
                        if (renderTypeNamesFast.put(layer.getAsInt(), (Object)renderType) == null) continue;
                        throw new JsonParseException("Registered duplicate fast graphics render type for layer " + String.valueOf(layer));
                    }
                }
            }
            Int2ObjectArrayMap emissiveLayers = new Int2ObjectArrayMap();
            if (jsonObject.has("forge_data")) {
                JsonObject forgeData = jsonObject.get("forge_data").getAsJsonObject();
                this.readLayerData(forgeData, "layers", (Int2ObjectOpenHashMap<ResourceLocation>)renderTypeNames, (Int2ObjectOpenHashMap<ResourceLocation>)renderTypeNamesFast, (Int2ObjectMap<ForgeFaceData>)emissiveLayers, false);
            }
            return new ItemLayerModel(null, (Int2ObjectMap<ForgeFaceData>)emissiveLayers, (Int2ObjectMap<ResourceLocation>)renderTypeNames, (Int2ObjectMap<ResourceLocation>)renderTypeNamesFast);
        }

        protected void readLayerData(JsonObject jsonObject, String name, Int2ObjectOpenHashMap<ResourceLocation> renderTypeNames, Int2ObjectMap<ForgeFaceData> layerData, boolean logWarning) {
            this.readLayerData(jsonObject, name, renderTypeNames, (Int2ObjectOpenHashMap<ResourceLocation>)new Int2ObjectOpenHashMap(), layerData, logWarning);
        }

        @Deprecated(forRemoval=true, since="1.21.4")
        protected void readLayerData(JsonObject jsonObject, String name, Int2ObjectOpenHashMap<ResourceLocation> renderTypeNames, Int2ObjectOpenHashMap<ResourceLocation> renderTypeNamesFast, Int2ObjectMap<ForgeFaceData> layerData, boolean logWarning) {
            if (!jsonObject.has(name)) {
                return;
            }
            JsonObject fullbrightLayers = jsonObject.getAsJsonObject(name);
            for (Map.Entry entry : fullbrightLayers.entrySet()) {
                int layer = Integer.parseInt((String)entry.getKey());
                ForgeFaceData data = ForgeFaceData.read((JsonElement)entry.getValue(), ForgeFaceData.DEFAULT);
                layerData.put(layer, (Object)data);
            }
        }
    }
}

