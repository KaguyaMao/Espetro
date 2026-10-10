/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.nbt.Tag
 */
package net.minecraftforge.common.util;

import net.minecraft.nbt.Tag;

public interface INBTSerializable<T extends Tag> {
    public T serializeNBT();

    public void deserializeNBT(T var1);
}

