/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.client.resources.model.UnbakedModel
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemDisplayContext
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SeparateTransformsModel
implements IUnbakedGeometry<SeparateTransformsModel> {
    private final BlockModel baseModel;
    private final ImmutableMap<ItemDisplayContext, BlockModel> perspectives;

    public SeparateTransformsModel(BlockModel baseModel, ImmutableMap<ItemDisplayContext, BlockModel> perspectives) {
        this.baseModel = baseModel;
        this.perspectives = perspectives;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return new Baked(context.useAmbientOcclusion(), context.isGui3d(), context.useBlockLight(), spriteGetter.apply(context.getMaterial("particle")), overrides, this.baseModel.m_111449_(baker, this.baseModel, spriteGetter, modelState, modelLocation, context.useBlockLight()), (ImmutableMap<ItemDisplayContext, BakedModel>)ImmutableMap.copyOf((Map)Maps.transformValues(this.perspectives, value -> value.m_111449_(baker, value, spriteGetter, modelState, modelLocation, context.useBlockLight()))));
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.baseModel.m_5500_(modelGetter);
        this.perspectives.values().forEach(model -> model.m_5500_(modelGetter));
    }

    public static class Baked
    implements IDynamicBakedModel {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final BakedModel baseModel;
        private final ImmutableMap<ItemDisplayContext, BakedModel> perspectives;

        public Baked(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, BakedModel baseModel, ImmutableMap<ItemDisplayContext, BakedModel> perspectives) {
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.baseModel = baseModel;
            this.perspectives = perspectives;
        }

        @Override
        @NotNull
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            return this.baseModel.getQuads(state, side, rand, data, renderType);
        }

        public boolean m_7541_() {
            return this.isAmbientOcclusion;
        }

        public boolean m_7539_() {
            return this.isGui3d;
        }

        public boolean m_7547_() {
            return this.isSideLit;
        }

        public boolean m_7521_() {
            return false;
        }

        public TextureAtlasSprite m_6160_() {
            return this.particle;
        }

        public ItemOverrides m_7343_() {
            return this.overrides;
        }

        public ItemTransforms m_7442_() {
            return ItemTransforms.f_111786_;
        }

        public BakedModel applyTransform(ItemDisplayContext cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
            if (this.perspectives.containsKey((Object)cameraTransformType)) {
                BakedModel p = (BakedModel)this.perspectives.get((Object)cameraTransformType);
                return p.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
            }
            return this.baseModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        }

        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            return this.baseModel.getRenderTypes(state, rand, data);
        }
    }

    public static final class Loader
    implements IGeometryLoader<SeparateTransformsModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public SeparateTransformsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            BlockModel baseModel = (BlockModel)deserializationContext.deserialize((JsonElement)GsonHelper.m_13930_((JsonObject)jsonObject, (String)"base"), BlockModel.class);
            JsonObject perspectiveData = GsonHelper.m_13930_((JsonObject)jsonObject, (String)"perspectives");
            HashMap<ItemDisplayContext, BlockModel> perspectives = new HashMap<ItemDisplayContext, BlockModel>();
            for (ItemDisplayContext transform : ItemDisplayContext.values()) {
                if (!perspectiveData.has(transform.m_7912_())) continue;
                BlockModel perspectiveModel = (BlockModel)deserializationContext.deserialize((JsonElement)GsonHelper.m_13930_((JsonObject)perspectiveData, (String)transform.m_7912_()), BlockModel.class);
                perspectives.put(transform, perspectiveModel);
            }
            return new SeparateTransformsModel(baseModel, (ImmutableMap<ItemDisplayContext, BlockModel>)ImmutableMap.copyOf(perspectives));
        }
    }
}

