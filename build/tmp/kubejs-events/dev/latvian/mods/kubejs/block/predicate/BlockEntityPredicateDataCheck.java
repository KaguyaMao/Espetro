/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.block.predicate;

import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BlockEntityPredicateDataCheck {
    public boolean checkData(@Nullable CompoundTag var1);
}

