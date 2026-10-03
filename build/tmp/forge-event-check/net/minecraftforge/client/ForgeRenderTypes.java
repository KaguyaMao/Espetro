/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.systems.RenderSystem
 *  com.mojang.blaze3d.vertex.DefaultVertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat
 *  com.mojang.blaze3d.vertex.VertexFormat$Mode
 *  net.minecraft.Util
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.RenderStateShard$EmptyTextureStateShard
 *  net.minecraft.client.renderer.RenderStateShard$ShaderStateShard
 *  net.minecraft.client.renderer.RenderStateShard$TextureStateShard
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.client.renderer.RenderType$CompositeState
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.renderer.texture.TextureManager
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.common.util.NonNullLazy;
import net.minecraftforge.common.util.NonNullSupplier;

public enum ForgeRenderTypes {
    ITEM_LAYERED_SOLID(() -> ForgeRenderTypes.getItemLayeredSolid(TextureAtlas.f_118259_)),
    ITEM_LAYERED_CUTOUT(() -> ForgeRenderTypes.getItemLayeredCutout(TextureAtlas.f_118259_)),
    ITEM_LAYERED_CUTOUT_MIPPED(() -> ForgeRenderTypes.getItemLayeredCutoutMipped(TextureAtlas.f_118259_)),
    ITEM_LAYERED_TRANSLUCENT(() -> ForgeRenderTypes.getItemLayeredTranslucent(TextureAtlas.f_118259_)),
    ITEM_UNSORTED_TRANSLUCENT(() -> ForgeRenderTypes.getUnsortedTranslucent(TextureAtlas.f_118259_)),
    ITEM_UNLIT_TRANSLUCENT(() -> ForgeRenderTypes.getUnlitTranslucent(TextureAtlas.f_118259_)),
    ITEM_UNSORTED_UNLIT_TRANSLUCENT(() -> ForgeRenderTypes.getUnlitTranslucent(TextureAtlas.f_118259_, false)),
    TRANSLUCENT_ON_PARTICLES_TARGET(() -> ForgeRenderTypes.getTranslucentParticlesTarget(TextureAtlas.f_118259_));

    public static boolean enableTextTextureLinearFiltering;
    private final NonNullSupplier<RenderType> renderTypeSupplier;

    public static RenderType getItemLayeredSolid(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_SOLID.apply(textureLocation);
    }

    public static RenderType getItemLayeredCutout(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_CUTOUT.apply(textureLocation);
    }

    public static RenderType getItemLayeredCutoutMipped(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_CUTOUT_MIPPED.apply(textureLocation);
    }

    public static RenderType getItemLayeredTranslucent(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_TRANSLUCENT.apply(textureLocation);
    }

    public static RenderType getUnsortedTranslucent(ResourceLocation textureLocation) {
        return Internal.UNSORTED_TRANSLUCENT.apply(textureLocation);
    }

    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation) {
        return Internal.UNLIT_TRANSLUCENT_SORTED.apply(textureLocation);
    }

    public static RenderType getUnlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
        return (sortingEnabled ? Internal.UNLIT_TRANSLUCENT_SORTED : Internal.UNLIT_TRANSLUCENT_UNSORTED).apply(textureLocation);
    }

    public static RenderType getEntityCutoutMipped(ResourceLocation textureLocation) {
        return Internal.LAYERED_ITEM_CUTOUT_MIPPED.apply(textureLocation);
    }

    public static RenderType getText(ResourceLocation locationIn) {
        return Internal.TEXT.apply(locationIn);
    }

    public static RenderType getTextIntensity(ResourceLocation locationIn) {
        return Internal.TEXT_INTENSITY.apply(locationIn);
    }

    public static RenderType getTextPolygonOffset(ResourceLocation locationIn) {
        return Internal.TEXT_POLYGON_OFFSET.apply(locationIn);
    }

    public static RenderType getTextIntensityPolygonOffset(ResourceLocation locationIn) {
        return Internal.TEXT_INTENSITY_POLYGON_OFFSET.apply(locationIn);
    }

    public static RenderType getTextSeeThrough(ResourceLocation locationIn) {
        return Internal.TEXT_SEETHROUGH.apply(locationIn);
    }

    public static RenderType getTextIntensitySeeThrough(ResourceLocation locationIn) {
        return Internal.TEXT_INTENSITY_SEETHROUGH.apply(locationIn);
    }

    public static RenderType getTranslucentParticlesTarget(ResourceLocation locationIn) {
        return Internal.TRANSLUCENT_PARTICLES_TARGET.apply(locationIn);
    }

    private ForgeRenderTypes(NonNullSupplier<RenderType> renderTypeSupplier) {
        this.renderTypeSupplier = NonNullLazy.of(renderTypeSupplier);
    }

    public RenderType get() {
        return this.renderTypeSupplier.get();
    }

    static {
        enableTextTextureLinearFiltering = false;
    }

    private static class Internal
    extends RenderType {
        private static final RenderStateShard.ShaderStateShard RENDERTYPE_ENTITY_TRANSLUCENT_UNLIT_SHADER = new RenderStateShard.ShaderStateShard(ForgeHooksClient.ClientEvents::getEntityTranslucentUnlitShader);
        public static Function<ResourceLocation, RenderType> UNSORTED_TRANSLUCENT = Util.m_143827_(Internal::unsortedTranslucent);
        private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT = Util.m_143821_((p_173227_, p_173228_) -> {
            RenderType.CompositeState rendertype$compositestate = RenderType.CompositeState.m_110628_().m_173292_(f_173066_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(p_173227_, false, false)).m_110685_(f_110139_).m_110661_(f_110110_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(p_173228_.booleanValue());
            return Internal.m_173215_((String)"entity_translucent", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)true, (RenderType.CompositeState)rendertype$compositestate);
        });
        public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_SORTED = Util.m_143827_(tex -> Internal.unlitTranslucent(tex, true));
        public static Function<ResourceLocation, RenderType> UNLIT_TRANSLUCENT_UNSORTED = Util.m_143827_(tex -> Internal.unlitTranslucent(tex, false));
        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_SOLID = Util.m_143827_(Internal::layeredItemSolid);
        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_CUTOUT = Util.m_143827_(Internal::layeredItemCutout);
        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_CUTOUT_MIPPED = Util.m_143827_(Internal::layeredItemCutoutMipped);
        public static Function<ResourceLocation, RenderType> LAYERED_ITEM_TRANSLUCENT = Util.m_143827_(Internal::layeredItemTranslucent);
        public static Function<ResourceLocation, RenderType> TEXT = Util.m_143827_(Internal::getText);
        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY = Util.m_143827_(Internal::getTextIntensity);
        public static Function<ResourceLocation, RenderType> TEXT_POLYGON_OFFSET = Util.m_143827_(Internal::getTextPolygonOffset);
        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY_POLYGON_OFFSET = Util.m_143827_(Internal::getTextIntensityPolygonOffset);
        public static Function<ResourceLocation, RenderType> TEXT_SEETHROUGH = Util.m_143827_(Internal::getTextSeeThrough);
        public static Function<ResourceLocation, RenderType> TEXT_INTENSITY_SEETHROUGH = Util.m_143827_(Internal::getTextIntensitySeeThrough);
        public static Function<ResourceLocation, RenderType> TRANSLUCENT_PARTICLES_TARGET = Util.m_143827_(Internal::getTranslucentParticlesTarget);

        private Internal(String name, VertexFormat fmt, VertexFormat.Mode glMode, int size, boolean doCrumbling, boolean depthSorting, Runnable onEnable, Runnable onDisable) {
            super(name, fmt, glMode, size, doCrumbling, depthSorting, onEnable, onDisable);
            throw new IllegalStateException("This class must not be instantiated");
        }

        private static RenderType unsortedTranslucent(ResourceLocation textureLocation) {
            boolean sortingEnabled = false;
            RenderType.CompositeState renderState = RenderType.CompositeState.m_110628_().m_173292_(RenderType.f_173066_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(textureLocation, false, false)).m_110685_(f_110139_).m_110661_(f_110110_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true);
            return Internal.m_173215_((String)"forge_entity_unsorted_translucent", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)renderState);
        }

        private static RenderType unlitTranslucent(ResourceLocation textureLocation, boolean sortingEnabled) {
            RenderType.CompositeState renderState = RenderType.CompositeState.m_110628_().m_173292_(RENDERTYPE_ENTITY_TRANSLUCENT_UNLIT_SHADER).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(textureLocation, false, false)).m_110685_(f_110139_).m_110661_(f_110110_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true);
            return Internal.m_173215_((String)"forge_entity_unlit_translucent", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)sortingEnabled, (RenderType.CompositeState)renderState);
        }

        private static RenderType layeredItemSolid(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(RenderType.f_173112_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(locationIn, false, false)).m_110685_(f_110134_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true);
            return Internal.m_173215_((String)"forge_item_entity_solid", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType layeredItemCutout(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(RenderType.f_173113_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(locationIn, false, false)).m_110685_(f_110134_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true);
            return Internal.m_173215_((String)"forge_item_entity_cutout", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType layeredItemCutoutMipped(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(RenderType.f_173067_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(locationIn, false, true)).m_110685_(f_110134_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true);
            return Internal.m_173215_((String)"forge_item_entity_cutout_mipped", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)false, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType layeredItemTranslucent(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(RenderType.f_173066_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(locationIn, false, false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110677_(f_110154_).m_110691_(true);
            return Internal.m_173215_((String)"forge_item_entity_translucent_cull", (VertexFormat)DefaultVertexFormat.f_85812_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)true, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getText(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173086_).m_173290_((RenderStateShard.EmptyTextureStateShard)new CustomizableTextureState(locationIn, () -> enableTextTextureLinearFiltering, () -> false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110691_(false);
            return Internal.m_173215_((String)"forge_text", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getTextIntensity(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173087_).m_173290_((RenderStateShard.EmptyTextureStateShard)new CustomizableTextureState(locationIn, () -> enableTextTextureLinearFiltering, () -> false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110691_(false);
            return Internal.m_173215_((String)"text_intensity", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getTextPolygonOffset(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173086_).m_173290_((RenderStateShard.EmptyTextureStateShard)new CustomizableTextureState(locationIn, () -> enableTextTextureLinearFiltering, () -> false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110669_(f_110118_).m_110691_(false);
            return Internal.m_173215_((String)"text_intensity", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getTextIntensityPolygonOffset(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173087_).m_173290_((RenderStateShard.EmptyTextureStateShard)new CustomizableTextureState(locationIn, () -> enableTextTextureLinearFiltering, () -> false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110669_(f_110118_).m_110691_(false);
            return Internal.m_173215_((String)"text_intensity", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getTextSeeThrough(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173088_).m_173290_((RenderStateShard.EmptyTextureStateShard)new CustomizableTextureState(locationIn, () -> enableTextTextureLinearFiltering, () -> false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110663_(f_110111_).m_110687_(f_110115_).m_110691_(false);
            return Internal.m_173215_((String)"forge_text_see_through", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getTextIntensitySeeThrough(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173090_).m_173290_((RenderStateShard.EmptyTextureStateShard)new CustomizableTextureState(locationIn, () -> enableTextTextureLinearFiltering, () -> false)).m_110685_(f_110139_).m_110671_(f_110152_).m_110663_(f_110111_).m_110687_(f_110115_).m_110691_(false);
            return Internal.m_173215_((String)"forge_text_see_through", (VertexFormat)DefaultVertexFormat.f_85820_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)256, (boolean)false, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }

        private static RenderType getTranslucentParticlesTarget(ResourceLocation locationIn) {
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.m_110628_().m_173292_(f_173108_).m_173290_((RenderStateShard.EmptyTextureStateShard)new RenderStateShard.TextureStateShard(locationIn, false, true)).m_110685_(f_110139_).m_110671_(f_110152_).m_110675_(f_110126_).m_110691_(true);
            return Internal.m_173215_((String)"forge_translucent_particles_target", (VertexFormat)DefaultVertexFormat.f_85811_, (VertexFormat.Mode)VertexFormat.Mode.QUADS, (int)0x200000, (boolean)true, (boolean)true, (RenderType.CompositeState)rendertype$state);
        }
    }

    private static class CustomizableTextureState
    extends RenderStateShard.TextureStateShard {
        private CustomizableTextureState(ResourceLocation resLoc, Supplier<Boolean> blur, Supplier<Boolean> mipmap) {
            super(resLoc, blur.get().booleanValue(), mipmap.get().booleanValue());
            this.f_110131_ = () -> {
                this.f_110329_ = (Boolean)blur.get();
                this.f_110330_ = (Boolean)mipmap.get();
                TextureManager texturemanager = Minecraft.m_91087_().m_91097_();
                texturemanager.m_118506_(resLoc).m_117960_(this.f_110329_, this.f_110330_);
                RenderSystem.setShaderTexture((int)0, (ResourceLocation)resLoc);
            };
        }
    }
}

