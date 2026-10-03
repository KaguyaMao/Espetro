/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Maps
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonObject
 *  com.mojang.math.Transformation
 *  net.minecraft.client.color.item.ItemColor
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.block.model.BakedQuad
 *  net.minecraft.client.renderer.block.model.BlockElement
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.BlockModelRotation
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.material.Fluid
 *  net.minecraft.world.level.material.Fluids
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package net.minecraftforge.client.model;

import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.mojang.math.Transformation;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.BlockElement;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.BlockModelRotation;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.client.model.CompositeModel;
import net.minecraftforge.client.model.QuadTransformers;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import net.minecraftforge.client.model.geometry.StandaloneGeometryBakingContext;
import net.minecraftforge.client.model.geometry.UnbakedGeometryHelper;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class DynamicFluidContainerModel
implements IUnbakedGeometry<DynamicFluidContainerModel> {
    private static final Transformation FLUID_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1.0f, 1.0f, 1.002f), new Quaternionf());
    private static final Transformation COVER_TRANSFORM = new Transformation(new Vector3f(), new Quaternionf(), new Vector3f(1.0f, 1.0f, 1.004f), new Quaternionf());
    private final Fluid fluid;
    private final boolean flipGas;
    private final boolean coverIsMask;
    private final boolean applyFluidLuminosity;

    private DynamicFluidContainerModel(Fluid fluid, boolean flipGas, boolean coverIsMask, boolean applyFluidLuminosity) {
        this.fluid = fluid;
        this.flipGas = flipGas;
        this.coverIsMask = coverIsMask;
        this.applyFluidLuminosity = applyFluidLuminosity;
    }

    public static RenderTypeGroup getLayerRenderTypes(boolean unlit) {
        return new RenderTypeGroup(RenderType.m_110466_(), unlit ? ForgeRenderTypes.ITEM_UNSORTED_UNLIT_TRANSLUCENT.get() : ForgeRenderTypes.ITEM_UNSORTED_TRANSLUCENT.get());
    }

    public DynamicFluidContainerModel withFluid(Fluid newFluid) {
        return new DynamicFluidContainerModel(newFluid, this.flipGas, this.coverIsMask, this.applyFluidLuminosity);
    }

    @Override
    public BakedModel bake(IGeometryBakingContext context, ModelBaker baker, Function<Material, TextureAtlasSprite> spriteGetter, ModelState modelState, ItemOverrides overrides, ResourceLocation modelLocation) {
        List<BakedQuad> quads;
        List<BlockElement> unbaked;
        SimpleModelState transformedState;
        TextureAtlasSprite templateSprite;
        TextureAtlasSprite particleSprite;
        Material particleLocation = context.hasMaterial("particle") ? context.getMaterial("particle") : null;
        Material baseLocation = context.hasMaterial("base") ? context.getMaterial("base") : null;
        Material fluidMaskLocation = context.hasMaterial("fluid") ? context.getMaterial("fluid") : null;
        Material coverLocation = context.hasMaterial("cover") ? context.getMaterial("cover") : null;
        TextureAtlasSprite baseSprite = baseLocation != null ? spriteGetter.apply(baseLocation) : null;
        TextureAtlasSprite fluidSprite = this.fluid != Fluids.f_76191_ ? spriteGetter.apply(ForgeHooksClient.getBlockMaterial(IClientFluidTypeExtensions.of(this.fluid).getStillTexture())) : null;
        TextureAtlasSprite coverSprite = coverLocation != null && (!this.coverIsMask || baseLocation != null) ? spriteGetter.apply(coverLocation) : null;
        TextureAtlasSprite textureAtlasSprite = particleSprite = particleLocation != null ? spriteGetter.apply(particleLocation) : null;
        if (particleSprite == null) {
            particleSprite = fluidSprite;
        }
        if (particleSprite == null) {
            particleSprite = baseSprite;
        }
        if (particleSprite == null && !this.coverIsMask) {
            particleSprite = coverSprite;
        }
        if (this.flipGas && this.fluid != Fluids.f_76191_ && this.fluid.getFluidType().isLighterThanAir()) {
            modelState = new SimpleModelState(modelState.m_6189_().m_121096_(new Transformation(null, new Quaternionf(0.0f, 0.0f, 1.0f, 0.0f), null, null)));
        }
        StandaloneGeometryBakingContext itemContext = StandaloneGeometryBakingContext.builder(context).withGui3d(false).withUseBlockLight(false).build(modelLocation);
        CompositeModel.Baked.Builder modelBuilder = CompositeModel.Baked.builder(itemContext, particleSprite, new ContainedFluidOverrideHandler(overrides, baker, itemContext, this), context.getTransforms());
        RenderTypeGroup normalRenderTypes = DynamicFluidContainerModel.getLayerRenderTypes(false);
        if (baseLocation != null && baseSprite != null) {
            List<BlockElement> unbaked2 = UnbakedGeometryHelper.createUnbakedItemElements(0, baseSprite.m_245424_());
            List<BakedQuad> quads2 = UnbakedGeometryHelper.bakeElements(unbaked2, $ -> baseSprite, modelState, modelLocation);
            modelBuilder.addQuads(normalRenderTypes, quads2);
        }
        if (fluidMaskLocation != null && fluidSprite != null && (templateSprite = spriteGetter.apply(fluidMaskLocation)) != null) {
            transformedState = new SimpleModelState(modelState.m_6189_().m_121096_(FLUID_TRANSFORM), modelState.m_7538_());
            unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(1, templateSprite.m_245424_());
            quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> fluidSprite, transformedState, modelLocation);
            boolean emissive = this.applyFluidLuminosity && this.fluid.getFluidType().getLightLevel() > 0;
            RenderTypeGroup renderTypes = DynamicFluidContainerModel.getLayerRenderTypes(emissive);
            if (emissive) {
                QuadTransformers.settingMaxEmissivity().processInPlace(quads);
            }
            modelBuilder.addQuads(renderTypes, quads);
        }
        if (coverSprite != null) {
            TextureAtlasSprite sprite;
            TextureAtlasSprite textureAtlasSprite2 = sprite = this.coverIsMask ? baseSprite : coverSprite;
            if (sprite != null) {
                transformedState = new SimpleModelState(modelState.m_6189_().m_121096_(COVER_TRANSFORM), modelState.m_7538_());
                unbaked = UnbakedGeometryHelper.createUnbakedItemMaskElements(2, coverSprite.m_245424_());
                quads = UnbakedGeometryHelper.bakeElements(unbaked, $ -> sprite, transformedState, modelLocation);
                modelBuilder.addQuads(normalRenderTypes, quads);
            }
        }
        modelBuilder.setParticle(particleSprite);
        return modelBuilder.build();
    }

    private static final class ContainedFluidOverrideHandler
    extends ItemOverrides {
        private final Map<String, BakedModel> cache = Maps.newHashMap();
        private final ItemOverrides nested;
        private final ModelBaker baker;
        private final IGeometryBakingContext owner;
        private final DynamicFluidContainerModel parent;

        private ContainedFluidOverrideHandler(ItemOverrides nested, ModelBaker baker, IGeometryBakingContext owner, DynamicFluidContainerModel parent) {
            this.nested = nested;
            this.baker = baker;
            this.owner = owner;
            this.parent = parent;
        }

        public BakedModel m_173464_(BakedModel originalModel, ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity, int seed) {
            BakedModel overridden = this.nested.m_173464_(originalModel, stack, level, entity, seed);
            if (overridden != originalModel) {
                return overridden;
            }
            return FluidUtil.getFluidContained(stack).map(fluidStack -> {
                Fluid fluid = fluidStack.getFluid();
                String name = ForgeRegistries.FLUIDS.getKey(fluid).toString();
                if (!this.cache.containsKey(name)) {
                    DynamicFluidContainerModel unbaked = this.parent.withFluid(fluid);
                    BakedModel bakedModel = unbaked.bake(this.owner, this.baker, Material::m_119204_, (ModelState)BlockModelRotation.X0_Y0, this, new ResourceLocation("forge:bucket_override"));
                    this.cache.put(name, bakedModel);
                    return bakedModel;
                }
                return this.cache.get(name);
            }).orElse(originalModel);
        }
    }

    public static class Colors
    implements ItemColor {
        public int m_92671_(@NotNull ItemStack stack, int tintIndex) {
            if (tintIndex != 1) {
                return -1;
            }
            return FluidUtil.getFluidContained(stack).map(fluidStack -> IClientFluidTypeExtensions.of(fluidStack.getFluid()).getTintColor((FluidStack)fluidStack)).orElse(-1);
        }
    }

    public static final class Loader
    implements IGeometryLoader<DynamicFluidContainerModel> {
        public static final Loader INSTANCE = new Loader();

        private Loader() {
        }

        @Override
        public DynamicFluidContainerModel read(JsonObject jsonObject, JsonDeserializationContext deserializationContext) {
            if (!jsonObject.has("fluid")) {
                throw new RuntimeException("Bucket model requires 'fluid' value.");
            }
            ResourceLocation fluidName = new ResourceLocation(jsonObject.get("fluid").getAsString());
            Fluid fluid = ForgeRegistries.FLUIDS.getValue(fluidName);
            boolean flip = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"flip_gas", (boolean)false);
            boolean coverIsMask = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"cover_is_mask", (boolean)true);
            boolean applyFluidLuminosity = GsonHelper.m_13855_((JsonObject)jsonObject, (String)"apply_fluid_luminosity", (boolean)true);
            return new DynamicFluidContainerModel(fluid, flip, coverIsMask, applyFluidLuminosity);
        }
    }
}

