/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.level.block.Block
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.mixin.common.BlockBehaviourMixin;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.Block;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={Block.class})
public abstract class BlockMixin
extends BlockBehaviourMixin {
    @Override
    @Accessor(value="descriptionId")
    @Mutable
    public abstract void kjs$setNameKey(String var1);
}

