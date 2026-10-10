/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.level.Level
 */
package dev.latvian.mods.kubejs.block;

import dev.latvian.mods.kubejs.level.BlockContainerJS;
import dev.latvian.mods.kubejs.level.LevelEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

@Info(value="Invoked when a detector block registered in KubeJS receives a block update.\n\n`Powered`/`Unpowered` event will be fired when the detector block is powered/unpowered.\n")
public class DetectorBlockEventJS
extends LevelEventJS {
    private final String detectorId;
    private final Level level;
    private final BlockPos pos;
    private final boolean powered;
    private final BlockContainerJS block;

    public DetectorBlockEventJS(String i, Level l, BlockPos p, boolean pow) {
        this.detectorId = i;
        this.level = l;
        this.pos = p;
        this.powered = pow;
        this.block = new BlockContainerJS(this.level, this.pos);
    }

    @Info(value="The id of the detector block when it was registered.")
    public String getDetectorId() {
        return this.detectorId;
    }

    @Override
    @Info(value="The level where the detector block is located.")
    public Level getLevel() {
        return this.level;
    }

    @Info(value="If the detector block is powered.")
    public boolean isPowered() {
        return this.powered;
    }

    @Info(value="The detector block.")
    public BlockContainerJS getBlock() {
        return this.block;
    }
}

