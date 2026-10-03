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
 */
package net.minecraftforge.client.textures;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.SpriteContents;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.metadata.animation.AnimationMetadataSection;
import net.minecraft.client.resources.metadata.animation.FrameSize;
import net.minecraft.resources.ResourceLocation;

public class UnitTextureAtlasSprite
extends TextureAtlasSprite {
    public static final ResourceLocation LOCATION = new ResourceLocation("forge", "unit");
    public static final UnitTextureAtlasSprite INSTANCE = new UnitTextureAtlasSprite();

    private UnitTextureAtlasSprite() {
        super(LOCATION, new SpriteContents(LOCATION, new FrameSize(1, 1), new NativeImage(1, 1, false), AnimationMetadataSection.f_119012_, null), 1, 1, 0, 0);
    }

    public float m_118367_(double u) {
        return (float)u / 16.0f;
    }

    public float m_118393_(double v) {
        return (float)v / 16.0f;
    }
}

