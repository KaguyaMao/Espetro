/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Direction
 *  net.minecraft.world.level.BlockAndTintGetter
 */
package net.minecraftforge.client.extensions;

import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;

public interface IForgeBlockAndTintGetter {
    private BlockAndTintGetter self() {
        return (BlockAndTintGetter)this;
    }

    default public float getShade(float normalX, float normalY, float normalZ, boolean shade) {
        return this.self().m_7717_(Direction.m_122372_((float)normalX, (float)normalY, (float)normalZ), shade);
    }
}

