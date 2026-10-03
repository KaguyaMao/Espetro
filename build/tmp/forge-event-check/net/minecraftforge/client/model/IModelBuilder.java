/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.SimpleBakedModel$Builder
 *  net.minecraft.core.Direction
 */
package net.minecraftforge.client.model;

import java.util.List;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.Direction;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.EmptyModel;

public interface IModelBuilder<T extends IModelBuilder<T>> {
    public static IModelBuilder<?> of(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle, RenderTypeGroup renderTypes) {
        return new Simple(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes);
    }

    public static IModelBuilder<?> of(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle, RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast) {
        return new Simple(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes, renderTypesFast);
    }

    public static IModelBuilder<?> collecting(List<BakedQuad> quads) {
        return new Collecting(quads);
    }

    public T addCulledFace(Direction var1, BakedQuad var2);

    public T addUnculledFace(BakedQuad var1);

    public BakedModel build();

    public static class Simple
    implements IModelBuilder<Simple> {
        private final SimpleBakedModel.Builder builder;
        private final RenderTypeGroup renderTypes;
        private final RenderTypeGroup renderTypesFast;

        private Simple(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle, RenderTypeGroup renderTypes) {
            this(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides, particle, renderTypes, RenderTypeGroup.EMPTY);
        }

        private Simple(boolean hasAmbientOcclusion, boolean usesBlockLight, boolean isGui3d, ItemTransforms transforms, ItemOverrides overrides, TextureAtlasSprite particle, RenderTypeGroup renderTypes, RenderTypeGroup renderTypesFast) {
            this.builder = new SimpleBakedModel.Builder(hasAmbientOcclusion, usesBlockLight, isGui3d, transforms, overrides).m_119528_(particle);
            this.renderTypes = renderTypes;
            this.renderTypesFast = renderTypesFast;
        }

        @Override
        public Simple addCulledFace(Direction facing, BakedQuad quad) {
            this.builder.m_119530_(facing, quad);
            return this;
        }

        @Override
        public Simple addUnculledFace(BakedQuad quad) {
            this.builder.m_119526_(quad);
            return this;
        }

        @Override
        @Deprecated
        public BakedModel build() {
            return this.builder.build(this.renderTypes, this.renderTypesFast);
        }
    }

    public static class Collecting
    implements IModelBuilder<Collecting> {
        private final List<BakedQuad> quads;

        private Collecting(List<BakedQuad> quads) {
            this.quads = quads;
        }

        @Override
        public Collecting addCulledFace(Direction facing, BakedQuad quad) {
            this.quads.add(quad);
            return this;
        }

        @Override
        public Collecting addUnculledFace(BakedQuad quad) {
            this.quads.add(quad);
            return this;
        }

        @Override
        public BakedModel build() {
            return EmptyModel.BAKED;
        }
    }
}

