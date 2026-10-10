/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.client.renderer.texture.SpriteContents
 *  net.minecraft.client.renderer.texture.TextureAtlasSprite
 *  net.minecraft.client.resources.metadata.animation.AnimationMetadataSection
 *  net.minecraft.client.resources.metadata.animation.FrameSize
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.Resource
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.client.textures.ForgeTextureMetadata;
import org.jetbrains.annotations.NotNull;

public interface ITextureAtlasSpriteLoader {
    public SpriteContents loadContents(ResourceLocation var1, Resource var2, FrameSize var3, NativeImage var4, AnimationMetadataSection var5, ForgeTextureMetadata var6);

    @NotNull
    public TextureAtlasSprite makeSprite(ResourceLocation var1, SpriteContents var2, int var3, int var4, int var5, int var6, int var7);
}

