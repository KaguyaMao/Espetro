/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicates
 *  com.mojang.math.Transformation
 *  it.unimi.dsi.fastutil.objects.Object2BooleanMap
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.renderer.texture.MissingTextureAtlasSprite
 *  net.minecraft.client.renderer.texture.TextureAtlas
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.geometry;

import com.google.common.base.Predicates;
import com.mojang.math.Transformation;
import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.model.geometry.IGeometryBakingContext;
import org.jetbrains.annotations.Nullable;

public class StandaloneGeometryBakingContext
implements IGeometryBakingContext {
    public static final ResourceLocation LOCATION = new ResourceLocation("forge", "standalone");
    public static final StandaloneGeometryBakingContext INSTANCE = StandaloneGeometryBakingContext.create(LOCATION);
    private final ResourceLocation modelName;
    private final Predicate<String> materialCheck;
    private final Function<String, Material> materialLookup;
    private final boolean isGui3d;
    private final boolean useBlockLight;
    private final boolean useAmbientOcclusion;
    private final ItemTransforms transforms;
    private final Transformation rootTransform;
    @Nullable
    private final ResourceLocation renderTypeHint;
    @Nullable
    private final ResourceLocation renderTypeFastHint;
    private final BiPredicate<String, Boolean> visibilityTest;

    public static StandaloneGeometryBakingContext create(ResourceLocation modelName) {
        return StandaloneGeometryBakingContext.builder().build(modelName);
    }

    public static StandaloneGeometryBakingContext create(Map<String, ResourceLocation> textures) {
        return StandaloneGeometryBakingContext.create(LOCATION, textures);
    }

    public static StandaloneGeometryBakingContext create(ResourceLocation modelName, Map<String, ResourceLocation> textures) {
        return StandaloneGeometryBakingContext.builder().withTextures(textures, MissingTextureAtlasSprite.m_118071_()).build(modelName);
    }

    private StandaloneGeometryBakingContext(ResourceLocation modelName, Predicate<String> materialCheck, Function<String, Material> materialLookup, boolean isGui3d, boolean useBlockLight, boolean useAmbientOcclusion, ItemTransforms transforms, Transformation rootTransform, @Nullable ResourceLocation renderTypeHint, BiPredicate<String, Boolean> visibilityTest) {
        this.modelName = modelName;
        this.materialCheck = materialCheck;
        this.materialLookup = materialLookup;
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.transforms = transforms;
        this.rootTransform = rootTransform;
        this.renderTypeHint = renderTypeHint;
        this.renderTypeFastHint = null;
        this.visibilityTest = visibilityTest;
    }

    private StandaloneGeometryBakingContext(ResourceLocation modelName, Predicate<String> materialCheck, Function<String, Material> materialLookup, boolean isGui3d, boolean useBlockLight, boolean useAmbientOcclusion, ItemTransforms transforms, Transformation rootTransform, @Nullable ResourceLocation renderTypeHint, @Nullable ResourceLocation renderTypeFastHint, BiPredicate<String, Boolean> visibilityTest) {
        this.modelName = modelName;
        this.materialCheck = materialCheck;
        this.materialLookup = materialLookup;
        this.isGui3d = isGui3d;
        this.useBlockLight = useBlockLight;
        this.useAmbientOcclusion = useAmbientOcclusion;
        this.transforms = transforms;
        this.rootTransform = rootTransform;
        this.renderTypeHint = renderTypeHint;
        this.renderTypeFastHint = renderTypeFastHint;
        this.visibilityTest = visibilityTest;
    }

    @Override
    public String getModelName() {
        return this.modelName.toString();
    }

    @Override
    public boolean hasMaterial(String name) {
        return this.materialCheck.test(name);
    }

    @Override
    public Material getMaterial(String name) {
        return this.materialLookup.apply(name);
    }

    @Override
    public boolean isGui3d() {
        return this.isGui3d;
    }

    @Override
    public boolean useBlockLight() {
        return this.useBlockLight;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return this.useAmbientOcclusion;
    }

    @Override
    public ItemTransforms getTransforms() {
        return this.transforms;
    }

    @Override
    public Transformation getRootTransform() {
        return this.rootTransform;
    }

    @Override
    @Nullable
    public ResourceLocation getRenderTypeHint() {
        return this.renderTypeHint;
    }

    @Override
    @Nullable
    public ResourceLocation getRenderTypeFastHint() {
        return this.renderTypeFastHint;
    }

    @Override
    public boolean isComponentVisible(String component, boolean fallback) {
        return this.visibilityTest.test(component, fallback);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(IGeometryBakingContext parent) {
        return new Builder(parent);
    }

    public static final class Builder {
        private static final Material NO_MATERIAL = new Material(TextureAtlas.f_118259_, MissingTextureAtlasSprite.m_118071_());
        private Predicate<String> materialCheck = Predicates.alwaysFalse();
        private Function<String, Material> materialLookup = $ -> NO_MATERIAL;
        private boolean isGui3d = true;
        private boolean useBlockLight = true;
        private boolean useAmbientOcclusion = true;
        private ItemTransforms transforms = ItemTransforms.f_111786_;
        private Transformation rootTransform = Transformation.m_121093_();
        @Nullable
        private ResourceLocation renderTypeHint;
        @Nullable
        private ResourceLocation renderTypeFastHint;
        private BiPredicate<String, Boolean> visibilityTest = (c, def) -> def;

        private Builder() {
        }

        private Builder(IGeometryBakingContext parent) {
            this.materialCheck = parent::hasMaterial;
            this.materialLookup = parent::getMaterial;
            this.isGui3d = parent.isGui3d();
            this.useBlockLight = parent.useBlockLight();
            this.useAmbientOcclusion = parent.useAmbientOcclusion();
            this.transforms = parent.getTransforms();
            this.rootTransform = parent.getRootTransform();
            this.renderTypeHint = parent.getRenderTypeHint();
            this.renderTypeFastHint = parent.getRenderTypeFastHint();
            this.visibilityTest = parent::isComponentVisible;
        }

        public Builder withTextures(Map<String, ResourceLocation> textures, ResourceLocation defaultTexture) {
            return this.withTextures(TextureAtlas.f_118259_, textures, defaultTexture);
        }

        public Builder withTextures(ResourceLocation atlasLocation, Map<String, ResourceLocation> textures, ResourceLocation defaultTexture) {
            this.materialCheck = textures::containsKey;
            this.materialLookup = name -> new Material(atlasLocation, textures.getOrDefault(name, defaultTexture));
            return this;
        }

        public Builder withMaterials(Map<String, Material> materials, Material defaultMaterial) {
            this.materialCheck = materials::containsKey;
            this.materialLookup = name -> materials.getOrDefault(name, defaultMaterial);
            return this;
        }

        public Builder withGui3d(boolean isGui3d) {
            this.isGui3d = isGui3d;
            return this;
        }

        public Builder withUseBlockLight(boolean useBlockLight) {
            this.useBlockLight = useBlockLight;
            return this;
        }

        public Builder withUseAmbientOcclusion(boolean useAmbientOcclusion) {
            this.useAmbientOcclusion = useAmbientOcclusion;
            return this;
        }

        public Builder withTransforms(ItemTransforms transforms) {
            this.transforms = transforms;
            return this;
        }

        public Builder withRootTransform(Transformation rootTransform) {
            this.rootTransform = rootTransform;
            return this;
        }

        public Builder withRenderTypeHint(ResourceLocation renderTypeHint) {
            this.renderTypeHint = renderTypeHint;
            return this;
        }

        public Builder withRenderTypeHint(ResourceLocation renderTypeHint, ResourceLocation renderTypeFastHint) {
            this.renderTypeHint = renderTypeHint;
            this.renderTypeFastHint = renderTypeFastHint;
            return this;
        }

        public Builder withVisibleComponents(Object2BooleanMap<String> parts) {
            this.visibilityTest = (arg_0, arg_1) -> parts.getOrDefault(arg_0, arg_1);
            return this;
        }

        public StandaloneGeometryBakingContext build(ResourceLocation modelName) {
            return new StandaloneGeometryBakingContext(modelName, this.materialCheck, this.materialLookup, this.isGui3d, this.useBlockLight, this.useAmbientOcclusion, this.transforms, this.rootTransform, this.renderTypeHint, this.renderTypeFastHint, this.visibilityTest);
        }
    }
}

