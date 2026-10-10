/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyCallbackJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateRotateCallbackJS
extends BlockStateModifyCallbackJS {
    private final Rotation rotation;

    public BlockStateRotateCallbackJS(BlockState state, Rotation rotation) {
        super(state);
        this.rotation = rotation;
    }

    @Info(value="Rotates the specified direction")
    public Direction rotate(Direction dir) {
        return this.rotation.m_55954_(dir);
    }

    @Override
    @HideFromJS
    public BlockStateModifyCallbackJS rotate(Rotation rotation) {
        throw new IllegalCallerException("Do not call this or you will get stuck in a loop!");
    }

    @Info(value="Get the Rotation that this block is being rotated by")
    public Rotation getRotation() {
        return this.rotation;
    }
}

