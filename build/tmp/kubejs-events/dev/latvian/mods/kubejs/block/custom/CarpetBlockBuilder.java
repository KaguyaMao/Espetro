/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.CarpetBlock
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.custom.ShapedBlockBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CarpetBlock;

public class CarpetBlockBuilder
extends ShapedBlockBuilder {
    public CarpetBlockBuilder(ResourceLocation i) {
        super(i, "_carpet");
        this.tagBoth(BlockTags.f_215838_.f_203868_());
    }

    @Override
    public Block createObject() {
        return new CarpetBlock(this.createProperties());
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        String mod = this.newID("block/", "").toString();
        bs.variant("", v -> v.model(mod));
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        String texture = this.textures.get("texture").getAsString();
        generator.blockModel(this.id, m -> {
            m.parent("minecraft:block/carpet");
            m.texture("wool", texture);
        });
    }

    public CarpetBlockBuilder texture(String texture) {
        return (CarpetBlockBuilder)this.textureAll(texture);
    }
}

