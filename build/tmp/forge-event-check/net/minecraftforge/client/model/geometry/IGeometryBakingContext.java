/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.math.Transformation
 *  net.minecraft.client.renderer.block.model.ItemTransforms
 *  net.minecraft.client.resources.model.Material
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.geometry;

import com.mojang.math.Transformation;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.Material;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.NamedRenderTypeManager;
import net.minecraftforge.client.RenderTypeGroup;
import org.jetbrains.annotations.Nullable;

public interface IGeometryBakingContext {
    public String getModelName();

    public boolean hasMaterial(String var1);

    public Material getMaterial(String var1);

    public boolean isGui3d();

    public boolean useBlockLight();

    public boolean useAmbientOcclusion();

    public ItemTransforms getTransforms();

    public Transformation getRootTransform();

    @Nullable
    public ResourceLocation getRenderTypeHint();

    @Nullable
    default public ResourceLocation getRenderTypeFastHint() {
        return null;
    }

    public boolean isComponentVisible(String var1, boolean var2);

    default public RenderTypeGroup getRenderType(ResourceLocation name) {
        return NamedRenderTypeManager.get(name);
    }
}

