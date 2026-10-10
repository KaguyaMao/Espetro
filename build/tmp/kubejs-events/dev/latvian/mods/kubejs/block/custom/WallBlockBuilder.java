/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.WallBlock
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.custom.MultipartShapedBlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.MultipartBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallBlock;

public class WallBlockBuilder
extends MultipartShapedBlockBuilder {
    public WallBlockBuilder(ResourceLocation i) {
        super(i, "_wall");
        this.tagBoth(BlockTags.f_13032_.f_203868_());
    }

    @Override
    public Block createObject() {
        return new WallBlock(this.createProperties());
    }

    @Override
    protected void generateMultipartBlockStateJson(MultipartBlockStateGenerator bs) {
        String modPost = this.newID("block/", "_post").toString();
        String modSide = this.newID("block/", "_side").toString();
        String modSideTall = this.newID("block/", "_side_tall").toString();
        bs.part("up=true", modPost);
        bs.part("north=low", p -> p.model(modSide).uvlock());
        bs.part("east=low", p -> p.model(modSide).uvlock().y(90));
        bs.part("south=low", p -> p.model(modSide).uvlock().y(180));
        bs.part("west=low", p -> p.model(modSide).uvlock().y(270));
        bs.part("north=tall", p -> p.model(modSideTall).uvlock());
        bs.part("east=tall", p -> p.model(modSideTall).uvlock().y(90));
        bs.part("south=tall", p -> p.model(modSideTall).uvlock().y(180));
        bs.part("west=tall", p -> p.model(modSideTall).uvlock().y(270));
    }

    @Override
    protected void generateItemModelJson(ModelGenerator m) {
        m.parent("minecraft:block/wall_inventory");
        m.texture("wall", this.textures.get("texture").getAsString());
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        String texture = this.textures.get("texture").getAsString();
        generator.blockModel(this.newID("", "_post"), m -> {
            m.parent("minecraft:block/template_wall_post");
            m.texture("wall", texture);
        });
        generator.blockModel(this.newID("", "_side"), m -> {
            m.parent("minecraft:block/template_wall_side");
            m.texture("wall", texture);
        });
        generator.blockModel(this.newID("", "_side_tall"), m -> {
            m.parent("minecraft:block/template_wall_side_tall");
            m.texture("wall", texture);
        });
    }
}

