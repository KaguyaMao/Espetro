/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.network.protocol.Packet
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import org.jetbrains.annotations.Nullable;

public interface ICustomPacket<T extends Packet<?>> {
    @Nullable
    public FriendlyByteBuf getInternalData();

    public ResourceLocation getName();

    public int getIndex();

    default public NetworkDirection getDirection() {
        return NetworkDirection.directionFor(this.getClass());
    }

    default public T getThis() {
        return (T)((Packet)this);
    }
}

