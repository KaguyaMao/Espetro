/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Transformation
 *  net.minecraft.Util
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.BlockElementFace
 *  net.minecraft.client.renderer.block.model.BlockFaceUV
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  net.minecraft.client.renderer.block.model.FaceBakery
 *  net.minecraft.client.renderer.block.model.ItemModelGenerator
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.BuiltInModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelBakery
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.core.Direction
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Matrix4f
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.model.geometry;

import com.mojang.math.Transformation;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.Util;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.BlockElementFace;
import net.minecraft.client.renderer.block.model.BlockFaceUV;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.FaceBakery;
import net.minecraft.client.renderer.block.model.ItemModelGenerator;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BuiltInModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.ElementsModel;
import net.minecraftforge.client.model.ForgeFaceData;
import net.minecraftforge.client.model.IModelBuilder;
import net.minecraftforge.client.model.IQuadTransformer;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;
import org.joml.Matrix4f;
import org.joml.Vector3f;

public class UnbakedGeometryHelper {
    private static final ItemModelGenerator ITEM_MODEL_GENERATOR = new ItemModelGenerator();
    private static final FaceBakery FACE_BAKERY = new FaceBakery();
    private static final Pattern FILESYSTEM_PATH_TO_RESLOC = Pattern.compile("(?:.*[\\\\/]assets[\\\\/](?<namespace>[a-z_-]+)[\\\\/]textures[\\\\/])?(?<path>[a-z_\\\\/-]+)\\.png");

    public static Material resolveDirtyMaterial(@Nullable String tex, IGeometryBakingContext owner) {
        if (tex == null) {
            return new Material(TextureAtlas.f_118259_, MissingTextureAtlasSprite.m_118071_());
        }
        if (tex.startsWith("#")) {
            return owner.getMaterial(tex);
        }
        Matcher match = FILESYSTEM_PATH_TO_RESLOC.matcher(tex);
        if (match.matches()) {
            String namespace = match.group("namespace");
            String path = match.group("path").replace("\\", "/");
            tex = namespace != null ? namespace + ":" + path : path;
        }
        return new Material(TextureAtlas.f_118259_, new ResourceLocation(tex));
    }

    @ApiStatus.Internal
    public static BakedModel bake(BlockModel blockModel, ModelBaker modelBaker, BlockModel owner, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation, boolean guiLight3d) {
        IUnbakedGeometry<?> customModel = blockModel.customData.getCustomGeometry();
        if (customModel != null) {
            return customModel.bake(blockModel.customData, modelBaker, spriteGetter, modelState, blockModel.getOverrides(modelBaker, owner, spriteGetter), modelLocation);
        }
        if (blockModel.m_111490_() == ModelBakery.f_119232_) {
            return ITEM_MODEL_GENERATOR.m_111670_(spriteGetter, blockModel).m_111449_(modelBaker, blockModel, spriteGetter, modelState, modelLocation, guiLight3d);
        }
        if (blockModel.m_111490_() == ModelBakery.f_119233_) {
            TextureAtlasSprite particleSprite = spriteGetter.apply(blockModel.m_111480_("particle"));
            return new BuiltInModel(blockModel.m_111491_(), blockModel.getOverrides(modelBaker, owner, spriteGetter), particleSprite, blockModel.m_111479_().m_111526_());
        }
        ElementsModel elementsModel = new ElementsModel(blockModel.m_111436_());
        return elementsModel.bake(blockModel.customData, modelBaker, spriteGetter, modelState, blockModel.getOverrides(modelBaker, owner, spriteGetter), modelLocation);
    }

    public static List<BlockElement> createUnbakedItemElements(int layerIndex, SpriteContents spriteContents) {
        return UnbakedGeometryHelper.createUnbakedItemElements(layerIndex, spriteContents, null);
    }

    public static List<BlockElement> createUnbakedItemElements(int layerIndex, SpriteContents spriteContents, @Nullable ForgeFaceData faceData) {
        List elements = ITEM_MODEL_GENERATOR.m_111638_(layerIndex, "layer" + layerIndex, spriteContents);
        if (faceData != null) {
            elements.forEach(element -> element.setFaceData(faceData));
        }
        return elements;
    }

    public static List<BlockElement> createUnbakedItemMaskElements(int layerIndex, SpriteContents spriteContents) {
        return UnbakedGeometryHelper.createUnbakedItemMaskElements(layerIndex, spriteContents, null);
    }

    public static List<BlockElement> createUnbakedItemMaskElements(int layerIndex, SpriteContents spriteContents, @Nullable ForgeFaceData faceData) {
        List<BlockElement> elements = UnbakedGeometryHelper.createUnbakedItemElements(layerIndex, spriteContents, faceData);
        elements.remove(0);
        int width = spriteContents.m_246492_();
        int height = spriteContents.m_245330_();
        BitSet bits = new BitSet(width * height);
        spriteContents.m_245638_().forEach(frame -> {
            for (int x = 0; x < width; ++x) {
                for (int y = 0; y < height; ++y) {
                    if (spriteContents.m_245970_(frame, x, y)) continue;
                    bits.set(x + y * width);
                }
            }
        });
        for (int y = 0; y < height; ++y) {
            int xStart = -1;
            for (int x = 0; x < width; ++x) {
                int yEnd;
                boolean opaque = bits.get(x + y * width);
                if (opaque != (xStart == -1)) continue;
                if (xStart == -1) {
                    xStart = x;
                    continue;
                }
                block2: for (yEnd = y + 1; yEnd < height; ++yEnd) {
                    for (int x2 = xStart; x2 <= x; ++x2) {
                        if (!bits.get(x2 + yEnd * width)) break block2;
                    }
                }
                for (int i = xStart; i < x; ++i) {
                    for (int j = y; j < yEnd; ++j) {
                        bits.clear(i + j * width);
                    }
                }
                elements.add(new BlockElement(new Vector3f((float)(16 * xStart) / (float)width, 16.0f - (float)(16 * yEnd) / (float)height, 7.5f), new Vector3f((float)(16 * x) / (float)width, 16.0f - (float)(16 * y) / (float)height, 8.5f), (Map)Util.m_137469_(new HashMap(), map -> {
                    for (Direction direction : Direction.values()) {
                        map.put(direction, new BlockElementFace(null, layerIndex, "layer" + layerIndex, new BlockFaceUV(null, 0)));
                    }
                }), null, true));
                xStart = -1;
            }
        }
        return elements;
    }

    public static void bakeElements(IModelBuilder<?> builder, List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        for (BlockElement element : elements) {
            element.f_111310_.forEach((side, face) -> {
                TextureAtlasSprite sprite = (TextureAtlasSprite)spriteGetter.apply(new Material(TextureAtlas.f_118259_, new ResourceLocation(face.f_111356_)));
                BakedQuad quad = UnbakedGeometryHelper.bakeElementFace(element, face, sprite, side, modelState, modelLocation);
                if (face.f_111354_ == null) {
                    builder.addUnculledFace(quad);
                } else {
                    builder.addCulledFace(Direction.m_252919_((Matrix4f)modelState.m_6189_().m_252783_(), (Direction)face.f_111354_), quad);
                }
            });
        }
    }

    public static List<BakedQuad> bakeElements(List<BlockElement> elements, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ResourceLocation modelLocation) {
        if (elements.isEmpty()) {
            return List.of();
        }
        ArrayList<BakedQuad> list = new ArrayList<BakedQuad>();
        UnbakedGeometryHelper.bakeElements(IModelBuilder.collecting(list), elements, spriteGetter, modelState, modelLocation);
        return list;
    }

    public static BakedQuad bakeElementFace(BlockElement element, BlockElementFace face, TextureAtlasSprite sprite, Direction direction, ModelState state, ResourceLocation modelLocation) {
        return FACE_BAKERY.m_111600_(element.f_111308_, element.f_111309_, face, sprite, direction, state, element.f_111311_, element.f_111312_, modelLocation);
    }

    public static IQuadTransformer applyRootTransform(ModelState modelState, Transformation rootTransform) {
        Transformation transform = modelState.m_6189_().applyOrigin(new Vector3f(0.5f, 0.5f, 0.5f));
        return QuadTransformers.applying(transform.m_121096_(rootTransform).m_121096_(transform.m_121103_()));
    }

    public static ModelState composeRootTransformIntoModelState(ModelState modelState, Transformation rootTransform) {
        rootTransform = rootTransform.applyOrigin(new Vector3f(-0.5f, -0.5f, -0.5f));
        return new SimpleModelState(modelState.m_6189_().m_121096_(rootTransform), modelState.m_7538_());
    }
}

