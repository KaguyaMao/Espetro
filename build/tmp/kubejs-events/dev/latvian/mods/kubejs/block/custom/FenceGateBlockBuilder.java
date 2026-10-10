/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.platform.Platform
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.BlockTags
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.block.FenceGateBlock
 *  net.minecraft.world.level.block.state.properties.WoodType
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.architectury.platform.Platform;
import dev.latvian.mods.kubejs.block.custom.ShapedBlockBuilder;
import dev.latvian.mods.kubejs.client.ModelGenerator;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FenceGateBlock;
import net.minecraft.world.level.block.state.properties.WoodType;

public class FenceGateBlockBuilder
extends ShapedBlockBuilder {
    public transient WoodType behaviour;

    public FenceGateBlockBuilder(ResourceLocation i) {
        super(i, "_fence_gate");
        this.tagBoth(BlockTags.f_13055_.f_203868_());
        if (Platform.isForge()) {
            this.tagBoth(new ResourceLocation("forge:fence_gates"));
        }
        this.behaviour = WoodType.f_61830_;
    }

    public FenceGateBlockBuilder behaviour(WoodType wt) {
        this.behaviour = wt;
        return this;
    }

    public FenceGateBlockBuilder behaviour(String wt) {
        for (WoodType type : WoodType.m_61843_().toList()) {
            if (!type.f_61839_().equals(wt)) continue;
            this.behaviour = type;
            return this;
        }
        return this;
    }

    @Override
    public Block createObject() {
        return new FenceGateBlock(this.createProperties(), this.behaviour);
    }

    @Override
    protected void generateBlockStateJson(VariantBlockStateGenerator bs) {
        String mod = this.newID("block/", "").toString();
        String modOpen = this.newID("block/", "_open").toString();
        String modWall = this.newID("block/", "_wall").toString();
        String modWallOpen = this.newID("block/", "_wall_open").toString();
        bs.variant("facing=east,in_wall=false,open=false", v -> v.model(mod).y(270).uvlock());
        bs.variant("facing=east,in_wall=false,open=true", v -> v.model(modOpen).y(270).uvlock());
        bs.variant("facing=east,in_wall=true,open=false", v -> v.model(modWall).y(270).uvlock());
        bs.variant("facing=east,in_wall=true,open=true", v -> v.model(modWallOpen).y(270).uvlock());
        bs.variant("facing=north,in_wall=false,open=false", v -> v.model(mod).y(180).uvlock());
        bs.variant("facing=north,in_wall=false,open=true", v -> v.model(modOpen).y(180).uvlock());
        bs.variant("facing=north,in_wall=true,open=false", v -> v.model(modWall).y(180).uvlock());
        bs.variant("facing=north,in_wall=true,open=true", v -> v.model(modWallOpen).y(180).uvlock());
        bs.variant("facing=south,in_wall=false,open=false", v -> v.model(mod).y(0).uvlock());
        bs.variant("facing=south,in_wall=false,open=true", v -> v.model(modOpen).y(0).uvlock());
        bs.variant("facing=south,in_wall=true,open=false", v -> v.model(modWall).y(0).uvlock());
        bs.variant("facing=south,in_wall=true,open=true", v -> v.model(modWallOpen).y(0).uvlock());
        bs.variant("facing=west,in_wall=false,open=false", v -> v.model(mod).y(90).uvlock());
        bs.variant("facing=west,in_wall=false,open=true", v -> v.model(modOpen).y(90).uvlock());
        bs.variant("facing=west,in_wall=true,open=false", v -> v.model(modWall).y(90).uvlock());
        bs.variant("facing=west,in_wall=true,open=true", v -> v.model(modWallOpen).y(90).uvlock());
    }

    @Override
    protected void generateBlockModelJsons(AssetJsonGenerator generator) {
        String texture = this.textures.get("texture").getAsString();
        generator.blockModel(this.id, m -> {
            m.parent("minecraft:block/template_fence_gate");
            m.texture("texture", texture);
        });
        generator.blockModel(this.newID("", "_open"), m -> {
            m.parent("minecraft:block/template_fence_gate_open");
            m.texture("texture", texture);
        });
        generator.blockModel(this.newID("", "_wall"), m -> {
            m.parent("minecraft:block/template_fence_gate_wall");
            m.texture("texture", texture);
        });
        generator.blockModel(this.newID("", "_wall_open"), m -> {
            m.parent("minecraft:block/template_fence_gate_wall_open");
            m.texture("texture", texture);
        });
    }

    @Override
    protected void generateItemModelJson(ModelGenerator m) {
        m.parent("minecraft:block/template_fence_gate");
        m.texture("texture", this.textures.get("texture").getAsString());
    }
}

