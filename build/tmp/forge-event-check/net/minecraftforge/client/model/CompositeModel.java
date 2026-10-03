/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.math.Transformation
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
 *  net.minecraft.core.BlockPos
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.RandomSource
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.BlockAndTintGetter
 *  net.minecraft.world.level.block.state.BlockState
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.ChunkRenderTypeSet;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IDynamicBakedModel;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraftforge.client.model.data.ModelProperty;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.common.util.ConcatenatedListView;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CompositeModel
implements IUnbakedGeometry<CompositeModel> {
    private final ImmutableMap<String, BlockModel> children;
    private final ImmutableList<String> itemPasses;

    public CompositeModel(ImmutableMap<String, BlockModel> children, ImmutableList<String> itemPasses) {
        this.children = children;
        this.itemPasses = itemPasses;
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        Material particleLocation = context.getMaterial("particle");
        TextureAtlasSprite particle = spriteGetter.apply(particleLocation);
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            modelState = UnbakedGeometryHelper.composeRootTransformIntoModelState(modelState, rootTransform);
        }
        ImmutableMap.Builder bakedPartsBuilder = ImmutableMap.builder();
        for (Map.Entry entry : this.children.entrySet()) {
            String name = (String)entry.getKey();
            if (!context.isComponentVisible(name, true)) continue;
            BlockModel model = (BlockModel)entry.getValue();
            bakedPartsBuilder.put((Object)name, (Object)model.m_111449_(baker, model, spriteGetter, modelState, modelLocation, true));
        }
        ImmutableMap bakedParts = bakedPartsBuilder.build();
        ImmutableList.Builder itemPassesBuilder = ImmutableList.builder();
        for (String name : this.itemPasses) {
            BakedModel model = (BakedModel)bakedParts.get((Object)name);
            if (model == null) {
                throw new IllegalStateException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
            }
            itemPassesBuilder.add((Object)model);
        }
        return new Baked(context.isGui3d(), context.useBlockLight(), context.useAmbientOcclusion(), particle, context.getTransforms(), overrides, (ImmutableMap<String, BakedModel>)bakedParts, (ImmutableList<BakedModel>)itemPassesBuilder.build());
    }

    @Override
    public void resolveParents(Function<ResourceLocation, UnbakedModel> modelGetter, IGeometryBakingContext context) {
        this.children.values().forEach(child -> child.m_5500_(modelGetter));
    }

    @Override
    public Set<String> getConfigurableComponentNames() {
        return this.children.keySet();
    }

    public static class Baked
    implements IDynamicBakedModel {
        private final boolean isAmbientOcclusion;
        private final boolean isGui3d;
        private final boolean isSideLit;
        private final TextureAtlasSprite particle;
        private final ItemOverrides overrides;
        private final ItemTransforms transforms;
        private final ImmutableMap<String, BakedModel> children;
        private final ImmutableList<BakedModel> itemPasses;

        public Baked(boolean isGui3d, boolean isSideLit, boolean isAmbientOcclusion, TextureAtlasSprite particle, ItemTransforms transforms, ItemOverrides overrides, ImmutableMap<String, BakedModel> children, ImmutableList<BakedModel> itemPasses) {
            this.children = children;
            this.isAmbientOcclusion = isAmbientOcclusion;
            this.isGui3d = isGui3d;
            this.isSideLit = isSideLit;
            this.particle = particle;
            this.overrides = overrides;
            this.transforms = transforms;
            this.itemPasses = itemPasses;
        }

        @Override
        @NotNull
        public List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side, @NotNull RandomSource rand, @NotNull ModelData data, @Nullable RenderType renderType) {
            ArrayList<List> quadLists = new ArrayList<List>();
            for (Map.Entry entry : this.children.entrySet()) {
                if (renderType != null && (state == null || !((BakedModel)entry.getValue()).getRenderTypes(state, rand, data).contains(renderType))) continue;
                quadLists.add(((BakedModel)entry.getValue()).getQuads(state, side, rand, Data.resolve(data, (String)entry.getKey()), renderType));
            }
            return ConcatenatedListView.of(quadLists);
        }

        @NotNull
        public ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos, @NotNull BlockState state, @NotNull ModelData modelData) {
            Data.Builder builder = Data.builder();
            for (Map.Entry entry : this.children.entrySet()) {
                builder.with((String)entry.getKey(), ((BakedModel)entry.getValue()).getModelData(level, pos, state, Data.resolve(modelData, (String)entry.getKey())));
            }
            return modelData.derive().with(Data.PROPERTY, builder.build()).build();
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
            return this.transforms;
        }

        public ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state, @NotNull RandomSource rand, @NotNull ModelData data) {
            ArrayList<ChunkRenderTypeSet> sets = new ArrayList<ChunkRenderTypeSet>();
            for (Map.Entry entry : this.children.entrySet()) {
                sets.add(((BakedModel)entry.getValue()).getRenderTypes(state, rand, Data.resolve(data, (String)entry.getKey())));
            }
            return ChunkRenderTypeSet.union(sets);
        }

        public List<BakedModel> getRenderPasses(ItemStack itemStack, boolean fabulous) {
            return this.itemPasses;
        }

        @Nullable
        public BakedModel getPart(String name) {
            return (BakedModel)this.children.get((Object)name);
        }

        public static Builder builder(IGeometryBakingContext owner, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
            return Baked.builder(owner.useAmbientOcclusion(), owner.isGui3d(), owner.useBlockLight(), particle, overrides, cameraTransforms);
        }

        public static Builder builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms cameraTransforms) {
            return new Builder(isAmbientOcclusion, isGui3d, isSideLit, particle, overrides, cameraTransforms);
        }

        public static class Builder {
            private final boolean isAmbientOcclusion;
            private final boolean isGui3d;
            private final boolean isSideLit;
            private final List<BakedModel> children = new ArrayList<BakedModel>();
            private final List<BakedQuad> quads = new ArrayList<BakedQuad>();
            private final ItemOverrides overrides;
            private final ItemTransforms transforms;
            private TextureAtlasSprite particle;
            private RenderTypeGroup lastRenderTypes = RenderTypeGroup.EMPTY;
            private RenderTypeGroup lastRenderTypesFast = RenderTypeGroup.EMPTY;

            private Builder(boolean isAmbientOcclusion, boolean isGui3d, boolean isSideLit, TextureAtlasSprite particle, ItemOverrides overrides, ItemTransforms transforms) {
                this.isAmbientOcclusion = isAmbientOcclusion;
                this.isGui3d = isGui3d;
                this.isSideLit = isSideLit;
                this.particle = particle;
                this.overrides = overrides;
                this.transforms = transforms;
            }

            public void addLayer(BakedModel model) {
                this.flushQuads(RenderTypeGroup.EMPTY, RenderTypeGroup.EMPTY);
                this.children.add(model);
            }

            private void addLayer(RenderTypeGroup renderTypes, List<BakedQuad> quads) {
                this.addLayer(renderTypes, RenderTypeGroup.EMPTY, quads);
            }

            private void addLayer(RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast, List<BakedQuad> quads) {
                IModelBuilder<?> modelBuilder = IModelBuilder.of(this.isAmbientOcclusion, this.isSideLit, this.isGui3d, this.transforms, this.overrides, this.particle, renderTypes, renderTypesFast);
                quads.forEach(modelBuilder::addUnculledFace);
                this.children.add(modelBuilder.build());
            }

            private void flushQuads(RenderTypeGroup renderTypes) {
                this.flushQuads(renderTypes, RenderTypeGroup.EMPTY);
            }

            private void flushQuads(RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast) {
                if (!Objects.equals(renderTypes, this.lastRenderTypes)) {
                    if (this.quads.size() > 0) {
                        this.addLayer(this.lastRenderTypes, this.lastRenderTypesFast, this.quads);
                        this.quads.clear();
                    }
                    this.lastRenderTypes = renderTypes;
                    this.lastRenderTypesFast = renderTypesFast;
                }
            }

            public Builder setParticle(TextureAtlasSprite particleSprite) {
                this.particle = particleSprite;
                return this;
            }

            public Builder addQuads(RenderTypeGroup renderTypes, BakedQuad ... quadsToAdd) {
                return this.addQuads(renderTypes, RenderTypeGroup.EMPTY, quadsToAdd);
            }

            public Builder addQuads(RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast, BakedQuad ... quadsToAdd) {
                this.flushQuads(renderTypes, renderTypesFast);
                Collections.addAll(this.quads, quadsToAdd);
                return this;
            }

            public Builder addQuads(RenderTypeGroup renderTypes, Collection<BakedQuad> quadsToAdd) {
                return this.addQuads(renderTypes, RenderTypeGroup.EMPTY, quadsToAdd);
            }

            public Builder addQuads(RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast, Collection<BakedQuad> quadsToAdd) {
                this.flushQuads(renderTypes, renderTypesFast);
                this.quads.addAll(quadsToAdd);
                return this;
            }

            public BakedModel build() {
                if (this.quads.size() > 0) {
                    this.addLayer(this.lastRenderTypes, this.lastRenderTypesFast, this.quads);
                }
                ImmutableMap.Builder childrenBuilder = ImmutableMap.builder();
                ImmutableList.Builder itemPassesBuilder = ImmutableList.builder();
                int i = 0;
                for (BakedModel model : this.children) {
                    childrenBuilder.put((Object)("model_" + i++), (Object)model);
                    itemPassesBuilder.add((Object)model);
                }
                return new Baked(this.isGui3d, this.isSideLit, this.isAmbientOcclusion, this.particle, this.transforms, this.overrides, (ImmutableMap<String, BakedModel>)childrenBuilder.build(), (ImmutableList<BakedModel>)itemPassesBuilder.build());
            }
        }
    }

    public static final class Loader
    implements IGeometryLoader<CompositeModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public CompositeModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            ArrayList<String> itemPasses = new ArrayList<String>();
            ImmutableMap.Builder childrenBuilder = ImmutableMap.builder();
            this.readChildren(jsonObject, "children", deserializationContext, (ImmutableMap.Builder<String, BlockModel>)childrenBuilder, itemPasses);
            ImmutableMap children = childrenBuilder.build();
            if (children.isEmpty()) {
                throw new JsonParseException("Composite model requires a \"children\" element with at least one element.");
            }
            if (jsonObject.has("item_render_order")) {
                itemPasses.clear();
                for (JsonElement element : jsonObject.getAsJsonArray("item_render_order")) {
                    String name = element.getAsString();
                    if (!children.containsKey((Object)name)) {
                        throw new JsonParseException("Specified \"" + name + "\" in \"item_render_order\", but that is not a child of this model.");
                    }
                    itemPasses.add(name);
                }
            }
            return new CompositeModel((ImmutableMap<String, BlockModel>)children, (ImmutableList<String>)ImmutableList.copyOf(itemPasses));
        }

        private void readChildren(JsonObject jsonObject, String name, JsonDeserializationContext deserializationContext, ImmutableMap.Builder<String, BlockModel> children, List<String> itemPasses) {
            if (!jsonObject.has(name)) {
                return;
            }
            JsonObject childrenJsonObject = jsonObject.getAsJsonObject(name);
            for (Map.Entry entry : childrenJsonObject.entrySet()) {
                children.put((Object)((String)entry.getKey()), (Object)((BlockModel)deserializationContext.deserialize((JsonElement)entry.getValue(), BlockModel.class)));
                itemPasses.add((String)entry.getKey());
            }
        }
    }

    public static class Data {
        public static final ModelProperty<Data> PROPERTY = new ModelProperty();
        private final Map<String, ModelData> partData;

        private Data(Map<String, ModelData> partData) {
            this.partData = partData;
        }

        @Nullable
        public ModelData get(String name) {
            return this.partData.get(name);
        }

        public static ModelData resolve(ModelData modelData, String name) {
            Data compositeData = modelData.get(PROPERTY);
            if (compositeData == null) {
                return modelData;
            }
            ModelData partData = compositeData.get(name);
            return partData != null ? partData : modelData;
        }

        public static Builder builder() {
            return new Builder();
        }

        public static final class Builder {
            private final Map<String, ModelData> partData = new IdentityHashMap<String, ModelData>();

            public Builder with(String name, ModelData data) {
                this.partData.put(name, data);
                return this;
            }

            public Data build() {
                return new Data(this.partData);
            }
        }
    }
}

