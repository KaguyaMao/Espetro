/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.HolderSet
 */
package net.minecraftforge.registries.holdersets;

import net.minecraft.core.HolderSet;
import net.minecraftforge.common.extensions.IForgeHolderSet;
import net.minecraftforge.registries.holdersets.HolderSetType;

public interface ICustomHolderSet<T>
extends HolderSet<T> {
    public HolderSetType type();

    default public IForgeHolderSet.SerializationType serializationType() {
        return IForgeHolderSet.SerializationType.OBJECT;
    }
}

