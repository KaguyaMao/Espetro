/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.renderer.texture.atlas.SpriteSource
 *  net.minecraft.client.renderer.texture.atlas.sources.SingleFile
 *  net.minecraft.data.PackOutput
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.common.data;

import java.util.Optional;
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.sources.SingleFile;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.SpriteSourceProvider;

public class ForgeSpriteSourceProvider
extends SpriteSourceProvider {
    public ForgeSpriteSourceProvider(PackOutput output, ExistingFileHelper fileHelper) {
        super(output, fileHelper, "forge");
    }

    @Override
    protected void addSources() {
        this.atlas(SpriteSourceProvider.BLOCKS_ATLAS).addSource((SpriteSource)new SingleFile(new ResourceLocation("forge:white"), Optional.empty()));
    }
}

