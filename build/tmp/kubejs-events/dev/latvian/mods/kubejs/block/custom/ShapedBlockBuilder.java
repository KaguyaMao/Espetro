/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.level.block.state.properties.BlockStateProperties
 *  net.minecraft.world.level.block.state.properties.Property
 */
package dev.latvian.mods.kubejs.block.custom;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.Property;

public abstract class ShapedBlockBuilder
extends BlockBuilder {
    public ShapedBlockBuilder(ResourceLocation i, String ... suffixes) {
        super(i);
        this.notSolid();
        this.property((Property<?>)BlockStateProperties.f_61362_);
        this.texture("texture", "kubejs:block/detector");
        for (String s : suffixes) {
            if (!this.id.m_135815_().endsWith(s)) continue;
            this.texture("texture", this.id.m_135827_() + ":block/" + this.id.m_135815_().substring(0, this.id.m_135815_().length() - s.length()));
            break;
        }
    }

    @Override
    public BlockBuilder textureAll(String tex) {
        super.textureAll(tex);
        return this.texture("texture", tex);
    }
}

