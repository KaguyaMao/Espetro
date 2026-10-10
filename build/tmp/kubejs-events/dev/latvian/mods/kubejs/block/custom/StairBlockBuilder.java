/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.Blocks
 *  net.minecraft.world.level.block.StairBlock
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.custom.ShapedBlockBuilder;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StairBlock;

public class StairBlockBuilder
extends ShapedBlockBuilder {
    public StairBlockBuilder(ResourceLocation i) {
        super(i, "_stairs");
        this.tagBoth(BlockTags.f_13030_.f_203868_());
    }

    @Override
    public Block createObject() {
        return new StairBlock(Blocks.f_50705_.m_49966_(), this.createProperties());
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        String mod = this.newID("block/", "").toString();
        String modInner = this.newID("block/", "_inner").toString();
        String modOuter = this.newID("block/", "_outer").toString();
        bs.variant("facing=east,half=bottom,shape=inner_left", v -> v.model(modInner).y(270).uvlock());
        bs.variant("facing=east,half=bottom,shape=inner_right", v -> v.model(modInner));
        bs.variant("facing=east,half=bottom,shape=outer_left", v -> v.model(modOuter).y(270).uvlock());
        bs.variant("facing=east,half=bottom,shape=outer_right", v -> v.model(modOuter));
        bs.variant("facing=east,half=bottom,shape=straight", v -> v.model(mod));
        bs.variant("facing=east,half=top,shape=inner_left", v -> v.model(modInner).x(180).uvlock());
        bs.variant("facing=east,half=top,shape=inner_right", v -> v.model(modInner).x(180).y(90).uvlock());
        bs.variant("facing=east,half=top,shape=outer_left", v -> v.model(modOuter).x(180).uvlock());
        bs.variant("facing=east,half=top,shape=outer_right", v -> v.model(modOuter).x(180).y(90).uvlock());
        bs.variant("facing=east,half=top,shape=straight", v -> v.model(mod).x(180).uvlock());
        bs.variant("facing=north,half=bottom,shape=inner_left", v -> v.model(modInner).y(180).uvlock());
        bs.variant("facing=north,half=bottom,shape=inner_right", v -> v.model(modInner).y(270).uvlock());
        bs.variant("facing=north,half=bottom,shape=outer_left", v -> v.model(modOuter).y(180).uvlock());
        bs.variant("facing=north,half=bottom,shape=outer_right", v -> v.model(modOuter).y(270).uvlock());
        bs.variant("facing=north,half=bottom,shape=straight", v -> v.model(mod).y(270).uvlock());
        bs.variant("facing=north,half=top,shape=inner_left", v -> v.model(modInner).x(180).y(270).uvlock());
        bs.variant("facing=north,half=top,shape=inner_right", v -> v.model(modInner).x(180).uvlock());
        bs.variant("facing=north,half=top,shape=outer_left", v -> v.model(modOuter).x(180).y(270).uvlock());
        bs.variant("facing=north,half=top,shape=outer_right", v -> v.model(modOuter).x(180).uvlock());
        bs.variant("facing=north,half=top,shape=straight", v -> v.model(mod).x(180).y(270).uvlock());
        bs.variant("facing=south,half=bottom,shape=inner_left", v -> v.model(modInner));
        bs.variant("facing=south,half=bottom,shape=inner_right", v -> v.model(modInner).y(90).uvlock());
        bs.variant("facing=south,half=bottom,shape=outer_left", v -> v.model(modOuter));
        bs.variant("facing=south,half=bottom,shape=outer_right", v -> v.model(modOuter).y(90).uvlock());
        bs.variant("facing=south,half=bottom,shape=straight", v -> v.model(mod).y(90).uvlock());
        bs.variant("facing=south,half=top,shape=inner_left", v -> v.model(modInner).x(180).y(90).uvlock());
        bs.variant("facing=south,half=top,shape=inner_right", v -> v.model(modInner).x(180).y(180).uvlock());
        bs.variant("facing=south,half=top,shape=outer_left", v -> v.model(modOuter).x(180).y(90).uvlock());
        bs.variant("facing=south,half=top,shape=outer_right", v -> v.model(modOuter).x(180).y(180).uvlock());
        bs.variant("facing=south,half=top,shape=straight", v -> v.model(mod).x(180).y(90).uvlock());
        bs.variant("facing=west,half=bottom,shape=inner_left", v -> v.model(modInner).y(90).uvlock());
        bs.variant("facing=west,half=bottom,shape=inner_right", v -> v.model(modInner).y(180).uvlock());
        bs.variant("facing=west,half=bottom,shape=outer_left", v -> v.model(modOuter).y(90).uvlock());
        bs.variant("facing=west,half=bottom,shape=outer_right", v -> v.model(modOuter).y(180).uvlock());
        bs.variant("facing=west,half=bottom,shape=straight", v -> v.model(mod).y(180).uvlock());
        bs.variant("facing=west,half=top,shape=inner_left", v -> v.model(modInner).x(180).y(180).uvlock());
        bs.variant("facing=west,half=top,shape=inner_right", v -> v.model(modInner).x(180).y(270).uvlock());
        bs.variant("facing=west,half=top,shape=outer_left", v -> v.model(modOuter).x(180).y(180).uvlock());
        bs.variant("facing=west,half=top,shape=outer_right", v -> v.model(modOuter).x(180).y(270).uvlock());
        bs.variant("facing=west,half=top,shape=straight", v -> v.model(mod).x(180).y(180).uvlock());
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        String texture = this.textures.get("texture").getAsString();
        generator.blockModel(this.id, m -> {
            m.parent("minecraft:block/stairs");
            m.texture("bottom", texture);
            m.texture("top", texture);
            m.texture("side", texture);
        });
        generator.blockModel(this.newID("", "_inner"), m -> {
            m.parent("minecraft:block/inner_stairs");
            m.texture("bottom", texture);
            m.texture("top", texture);
            m.texture("side", texture);
        });
        generator.blockModel(this.newID("", "_outer"), m -> {
            m.parent("minecraft:block/outer_stairs");
            m.texture("bottom", texture);
            m.texture("top", texture);
            m.texture("side", texture);
        });
    }
}

