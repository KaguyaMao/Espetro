/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model.geometry;

import java.util.function.Function;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;

public abstract class SimpleUnbakedGeometry<T extends SimpleUnbakedGeometry<T>>
implements IUnbakedGeometry<T> {
    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        TextureAtlasSprite particle = spriteGetter.apply(context.getMaterial("particle"));
        ResourceLocation renderTypeHint = context.getRenderTypeHint();
        RenderTypeGroup renderTypes = renderTypeHint != null ? context.getRenderType(renderTypeHint) : RenderTypeGroup.EMPTY;
        ResourceLocation renderTypeFastHint = context.getRenderTypeFastHint();
        RenderTypeGroup renderTypesFast = renderTypeFastHint != null ? context.getRenderType(renderTypeFastHint) : RenderTypeGroup.EMPTY;
        IModelBuilder<?> builder = IModelBuilder.of(context.useAmbientOcclusion(), context.useBlockLight(), context.isGui3d(), context.getTransforms(), overrides, particle, renderTypes, renderTypesFast);
        this.addQuads(context, builder, baker, spriteGetter, modelState, modelLocation);
        return builder.build();
    }

    protected abstract void addQuads(IGeometryBakingContext var1, IModelBuilder<?> var2, ModelBaker var3, Function<Material, TextureAtlasSprite> var4, ModelState var5, ResourceLocation var6);
}

