/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.world.level.block.state.BlockBehaviour$BlockStateBase
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Mutable
 *  org.spongepowered.asm.mixin.gen.Accessor
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.BlockStateKJS;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.world.level.block.state.BlockBehaviour;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@RemapPrefixForJS(value="kjs$")
@Mixin(value={BlockBehaviour.BlockStateBase.class})
public abstract class BlockStateBaseMixin
implements BlockStateKJS {
    @Override
    @Accessor(value="destroySpeed")
    @Mutable
    public abstract void kjs$setDestroySpeed(float var1);

    @Override
    @Accessor(value="requiresCorrectToolForDrops")
    @Mutable
    public abstract void kjs$setRequiresTool(boolean var1);

    @Override
    @Accessor(value="lightEmission")
    @Mutable
    public abstract void kjs$setLightEmission(int var1);
}

