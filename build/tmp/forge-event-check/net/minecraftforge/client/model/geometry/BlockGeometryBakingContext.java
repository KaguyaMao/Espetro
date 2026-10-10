/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Transformation
 *  net.minecraft.client.renderer.block.model.BlockModel
 *  net.minecraft.client.renderer.block.model.ItemOverrides
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.model.BakedModel
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.client.resources.model.ModelBaker
 *  net.minecraft.client.resources.model.ModelState
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.geometry;

import com.mojang.math.Transformation;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.Material;
import net.minecraft.client.resources.model.ModelBaker;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import net.minecraftforge.client.model.geometry.IUnbakedGeometry;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public class BlockGeometryBakingContext
implements IGeometryBakingContext {
    public final BlockModel owner;
    public final VisibilityData visibilityData = new VisibilityData();
    @Nullable
    private IUnbakedGeometry<?> customGeometry;
    @Nullable
    private Transformation rootTransform;
    @Nullable
    private ResourceLocation renderTypeHint;
    @Nullable
    private ResourceLocation renderTypeFastHint;
    private boolean gui3d = true;

    @ApiStatus.Internal
    public BlockGeometryBakingContext(BlockModel owner) {
        this.owner = owner;
    }

    @Override
    public String getModelName() {
        return this.owner.f_111416_;
    }

    public boolean hasCustomGeometry() {
        return this.getCustomGeometry() != null;
    }

    @Nullable
    public IUnbakedGeometry<?> getCustomGeometry() {
        return this.owner.f_111418_ != null && this.customGeometry == null ? this.owner.f_111418_.customData.getCustomGeometry() : this.customGeometry;
    }

    public void setCustomGeometry(IUnbakedGeometry<?> geometry) {
        this.customGeometry = geometry;
    }

    @Override
    public boolean isComponentVisible(String part, boolean fallback) {
        return this.owner.f_111418_ != null && !this.visibilityData.hasCustomVisibility(part) ? this.owner.f_111418_.customData.isComponentVisible(part, fallback) : this.visibilityData.isVisible(part, fallback);
    }

    @Override
    public boolean hasMaterial(String name) {
        return this.owner.m_111477_(name);
    }

    @Override
    public Material getMaterial(String name) {
        return this.owner.m_111480_(name);
    }

    @Override
    public boolean isGui3d() {
        return this.gui3d;
    }

    @Override
    public boolean useBlockLight() {
        return this.owner.m_111479_().m_111526_();
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.owner.m_111476_();
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.owner.m_111491_();
    }

    @Override
    public Transformation getRootTransform() {
        if (this.rootTransform != null) {
            return this.rootTransform;
        }
        return this.owner.f_111418_ != null ? this.owner.f_111418_.customData.getRootTransform() : Transformation.m_121093_();
    }

    public void setRootTransform(Transformation rootTransform) {
        this.rootTransform = rootTransform;
    }

    @Override
    @Nullable
    public ResourceLocation getRenderTypeHint() {
        if (this.renderTypeHint != null) {
            return this.renderTypeHint;
        }
        return this.owner.f_111418_ != null ? this.owner.f_111418_.customData.getRenderTypeHint() : null;
    }

    @Override
    @Nullable
    public ResourceLocation getRenderTypeFastHint() {
        if (this.renderTypeFastHint != null) {
            return this.renderTypeFastHint;
        }
        return this.owner.f_111418_ != null ? this.owner.f_111418_.customData.getRenderTypeFastHint() : null;
    }

    public void setRenderTypeHint(ResourceLocation renderTypeHint) {
        this.renderTypeHint = renderTypeHint;
    }

    public void setRenderTypeFastHint(ResourceLocation renderTypeFastHint) {
        this.renderTypeFastHint = renderTypeFastHint;
    }

    public void setGui3d(boolean gui3d) {
        this.gui3d = gui3d;
    }

    public void copyFrom(BlockGeometryBakingContext other) {
        this.customGeometry = other.customGeometry;
        this.rootTransform = other.rootTransform;
        this.visibilityData.copyFrom(other.visibilityData);
        this.renderTypeHint = other.renderTypeHint;
        this.renderTypeFastHint = other.renderTypeFastHint;
        this.gui3d = other.gui3d;
    }

    public BakedModel bake(ModelBaker baker, Function<Material, TextureAtlasSprite> bakedTextureGetter, ModelState modelTransform, ItemOverrides overrides, ResourceLocation modelLocation) {
        IUnbakedGeometry<?> geometry = this.getCustomGeometry();
        if (geometry == null) {
            throw new IllegalStateException("Can not use custom baking without custom geometry");
        }
        return geometry.bake(this, baker, bakedTextureGetter, modelTransform, overrides, modelLocation);
    }

    public static class VisibilityData {
        private final Map<String, Boolean> data = new HashMap<String, Boolean>();

        public boolean hasCustomVisibility(String part) {
            return this.data.containsKey(part);
        }

        public boolean isVisible(String part, boolean fallback) {
            return this.data.getOrDefault(part, fallback);
        }

        public void setVisibilityState(String partName, boolean type) {
            this.data.put(partName, type);
        }

        public void copyFrom(VisibilityData visibilityData) {
            this.data.clear();
            this.data.putAll(visibilityData.data);
        }
    }
}

