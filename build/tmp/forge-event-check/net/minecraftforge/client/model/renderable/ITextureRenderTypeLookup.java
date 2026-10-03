/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.client.model.renderable;

import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

@FunctionalInterface
public interface ITextureRenderTypeLookup {
    public RenderType get(ResourceLocation var1);
}

