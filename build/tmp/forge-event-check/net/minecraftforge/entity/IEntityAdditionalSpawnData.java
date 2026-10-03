/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 */
package net.minecraftforge.entity;

import net.minecraft.network.FriendlyByteBuf;

public interface IEntityAdditionalSpawnData {
    public void writeSpawnData(FriendlyByteBuf var1);

    public void readSpawnData(FriendlyByteBuf var1);
}

