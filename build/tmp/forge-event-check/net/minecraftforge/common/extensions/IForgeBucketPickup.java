/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.sounds.SoundEvent
 *  net.minecraft.world.level.block.BucketPickup
 *  net.minecraft.world.level.block.state.BlockState
 */
package net.minecraftforge.common.extensions;

import java.util.Optional;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.state.BlockState;

public interface IForgeBucketPickup {
    private BucketPickup self() {
        return (BucketPickup)this;
    }

    default public Optional<SoundEvent> getPickupSound(BlockState state) {
        return this.self().m_142298_();
    }
}

