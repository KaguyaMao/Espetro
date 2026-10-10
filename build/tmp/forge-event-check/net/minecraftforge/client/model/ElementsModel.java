/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.mojang.math.Transformation
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.BlockElementFace
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 */
package net.minecraftforge.client.model;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.SimpleUnbakedGeometry;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;

public class ElementsModel
extends SimpleUnbakedGeometry<ElementsModel> {
    private final List<BlockElement> elements;

    public ElementsModel(List<BlockElement> elements) {
        this.elements = elements;
    }

    @Override
    protected void addQuads(IGeometryBakingContext context, IModelBuilder<?> modelBuilder, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        IQuadTransformer postTransform = QuadTransformers.empty();
        Transformation rootTransform = context.getRootTransform();
        if (!rootTransform.isIdentity()) {
            postTransform = UnbakedGeometryHelper.applyRootTransform(modelState, rootTransform);
        }
        for (BlockElement element : this.elements) {
            for (Direction direction : element.f_111310_.keySet()) {
                BlockElementFace face = (BlockElementFace)element.f_111310_.get(direction);
                TextureAtlasSprite sprite = spriteGetter.apply(context.getMaterial(face.f_111356_));
                BakedQuad quad = BlockModel.m_111437_((BlockElement)element, (BlockElementFace)face, (TextureAtlasSprite)sprite, (Direction)direction, (ModelState)modelState, (ResourceLocation)modelLocation);
                postTransform.processInPlace(quad);
                if (face.f_111354_ == null) {
                    modelBuilder.addUnculledFace(quad);
                    continue;
                }
                modelBuilder.addCulledFace(modelState.m_6189_().rotateTransform(face.f_111354_), quad);
            }
        }
    }

    public static final class Loader
    implements IGeometryLoader<ElementsModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public ElementsModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) throws JsonParseException {
            if (!jsonObject.has("elements")) {
                throw new JsonParseException("An element model must have an \"elements\" member.");
            }
            ArrayList<BlockElement> elements = new ArrayList<BlockElement>();
            for (JsonElement element : GsonHelper.m_13933_((JsonObject)jsonObject, (String)"elements")) {
                elements.add((BlockElement)deserializationContext.deserialize(element, BlockElement.class));
            }
            return new ElementsModel(elements);
        }
    }
}

