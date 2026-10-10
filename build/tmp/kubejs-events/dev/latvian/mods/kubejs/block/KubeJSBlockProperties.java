/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.state.BlockBehaviour$Properties
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.BlockBuilder;
import net.minecraft.world.level.block.state.BlockBehaviour;

public class KubeJSBlockProperties
extends BlockBehaviour.Properties {
    public final BlockBuilder blockBuilder;

    public KubeJSBlockProperties(BlockBuilder blockBuilder) {
        this.blockBuilder = blockBuilder;
    }
}

