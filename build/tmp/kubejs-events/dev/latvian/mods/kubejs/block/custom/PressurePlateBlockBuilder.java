/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.PressurePlateBlock
 *  net.minecraft.world.level.block.PressurePlateBlock$Sensitivity
 *  net.minecraft.world.level.block.state.properties.BlockSetType
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.custom.ShapedBlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.PressurePlateBlock;
import net.minecraft.world.level.block.state.properties.BlockSetType;

public class PressurePlateBlockBuilder
extends ShapedBlockBuilder {
    public transient BlockSetType behaviour;

    public PressurePlateBlockBuilder(ResourceLocation i) {
        super(i, "_pressure_plate");
        this.noCollision();
        this.tagBoth(BlockTags.f_13099_.f_203868_());
        this.behaviour = BlockSetType.f_271198_;
    }

    public PressurePlateBlockBuilder behaviour(BlockSetType wt) {
        this.behaviour = wt;
        return this;
    }

    public PressurePlateBlockBuilder behaviour(String wt) {
        for (BlockSetType type : BlockSetType.m_271801_().toList()) {
            if (!type.f_271253_().equals(wt)) continue;
            this.behaviour = type;
            return this;
        }
        return this;
    }

    @Override
    public Block createObject() {
        return new PressurePlateBlock(PressurePlateBlock.Sensitivity.EVERYTHING, this.createProperties(), this.behaviour);
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        bs.variant("powered=true", v -> v.model(this.newID("block/", "_down").toString()));
        bs.variant("powered=false", v -> v.model(this.newID("block/", "_up").toString()));
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        String texture = this.textures.get("texture").getAsString();
        generator.blockModel(this.newID("", "_down"), m -> {
            m.parent("minecraft:block/pressure_plate_down");
            m.texture("texture", texture);
        });
        generator.blockModel(this.newID("", "_up"), m -> {
            m.parent("minecraft:block/pressure_plate_up");
            m.texture("texture", texture);
        });
    }

    @Override
    protected void generateItemModelJson(ModelGenerator m) {
        m.parent(this.newID("block/", "_up").toString());
    }
}

