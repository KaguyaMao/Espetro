/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.block.Block
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.block.state.BlockStatePredicate;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.function.Consumer;
import net.minecraft.world.level.block.Block;

public class BlockModificationEventJS
extends EventJS {
    @Info(value="Modifies blocks that match the given predicate.\n\n**NOTE**: tag predicates are not supported at this time.\n")
    public void modify(BlockStatePredicate predicate, Consumer<Block> c) {
        for (Block block : predicate.getBlocks()) {
            c.accept(block);
        }
    }
}

