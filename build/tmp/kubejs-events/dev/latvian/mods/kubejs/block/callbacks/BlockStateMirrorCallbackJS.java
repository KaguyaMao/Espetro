/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.block.Mirror
 *  net.minecraft.world.level.block.Rotation
 *  net.minecraft.world.level.block.state.BlockState
 */
package dev.latvian.mods.kubejs.block.callbacks;

import dev.latvian.mods.kubejs.block.callbacks.BlockStateModifyCallbackJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.util.HideFromJS;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;

public class BlockStateMirrorCallbackJS
extends BlockStateModifyCallbackJS {
    private final Mirror mirror;

    public BlockStateMirrorCallbackJS(BlockState state, Mirror mirror) {
        super(state);
        this.mirror = mirror;
    }

    @Info(value="Mirrors the direction passed in")
    public Direction mirror(Direction dir) {
        return this.mirror.m_54848_(dir);
    }

    @Override
    @HideFromJS
    public BlockStateModifyCallbackJS mirror(Mirror mirror) {
        throw new IllegalCallerException("Do not call this or you will get stuck in a loop!");
    }

    @Info(value="Gets the rotation of the direction passed in relative to this mirror")
    public Rotation getRotation(Direction dir) {
        return this.mirror.m_54846_(dir);
    }

    @Info(value="Gets the Mirror")
    public Mirror getMirror() {
        return this.mirror;
    }
}

