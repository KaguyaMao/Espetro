/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.client.resources.model.SimpleBakedModel
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.textures.UnitTextureAtlasSprite;

public class EmptyModel
extends SimpleUnbakedGeometry<EmptyModel> {
    public static final BakedModel BAKED = new Baked();
    public static final EmptyModel INSTANCE = new EmptyModel();
    public static final IGeometryLoader<EmptyModel> LOADER = (json, ctx) -> INSTANCE;

    private EmptyModel() {
    }

    @Override
    protected void addQuads(IGeometryBakingContext owner, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelTransform, ResourceLocation modelLocation) {
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        return BAKED;
    }

    private static class Baked
    extends SimpleBakedModel {
        private static final Material MISSING_TEXTURE = new Material(TextureAtlas.f_118259_, MissingTextureAtlasSprite.m_118071_());

        public Baked() {
            super(List.of(), Map.of(), false, false, false, (TextureAtlasSprite)UnitTextureAtlasSprite.INSTANCE, ItemTransforms.f_111786_, ItemOverrides.f_111734_, RenderTypeGroup.EMPTY, RenderTypeGroup.EMPTY);
        }

        public TextureAtlasSprite m_6160_() {
            return MISSING_TEXTURE.m_119204_();
        }
    }
}

