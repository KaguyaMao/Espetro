/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.SlabBlock
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.custom.ShapedBlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SlabBlock;

public class SlabBlockBuilder
extends ShapedBlockBuilder {
    public SlabBlockBuilder(ResourceLocation i) {
        super(i, "_slab");
        this.tagBoth(BlockTags.f_13031_.f_203868_());
    }

    @Override
    public Block createObject() {
        return new SlabBlock(this.createProperties());
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        bs.variant("type=double", v -> v.model(this.newID("block/", "_double").toString()));
        bs.variant("type=bottom", v -> v.model(this.newID("block/", "_bottom").toString()));
        bs.variant("type=top", v -> v.model(this.newID("block/", "_top").toString()));
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        String texture = this.textures.get("texture").getAsString();
        generator.blockModel(this.newID("", "_double"), m -> {
            m.parent("minecraft:block/cube_all");
            m.texture("all", texture);
        });
        generator.blockModel(this.newID("", "_bottom"), m -> {
            m.parent("minecraft:block/slab");
            m.texture("bottom", texture);
            m.texture("top", texture);
            m.texture("side", texture);
        });
        generator.blockModel(this.newID("", "_top"), m -> {
            m.parent("minecraft:block/slab_top");
            m.texture("bottom", texture);
            m.texture("top", texture);
            m.texture("side", texture);
        });
    }

    @Override
    protected void generateItemModelJson(ModelGenerator m) {
        m.parent(this.newID("block/", "_bottom").toString());
    }
}

