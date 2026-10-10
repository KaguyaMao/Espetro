/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  joptsimple.ValueConverter
 *  net.minecraft.core.BlockPos
 */
package net.minecraftforge.gametest;

import joptsimple.ValueConverter;
import net.minecraft.core.BlockPos;

public class BlockPosValueConverter
implements ValueConverter<BlockPos> {
    public BlockPos convert(String value) {
        String[] split = value.split(",");
        return BlockPos.m_274561_((double)Double.parseDouble(split[0]), (double)Double.parseDouble(split[1]), (double)Double.parseDouble(split[2]));
    }

    public Class<BlockPos> valueType() {
        return BlockPos.class;
    }

    public String valuePattern() {
        return null;
    }
}

