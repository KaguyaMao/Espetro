/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.Tag
 */
package net.minecraftforge.common.capabilities;

import net.minecraft.nbt.Tag;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;

public interface ICapabilitySerializable<T extends Tag>
extends ICapabilityProvider,
INBTSerializable<T> {
}

