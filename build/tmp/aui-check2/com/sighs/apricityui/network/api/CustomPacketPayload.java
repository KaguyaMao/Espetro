/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 */
package com.sighs.apricityui.network.api;

import net.minecraft.resources.ResourceLocation;

public interface CustomPacketPayload {
    public Type<? extends CustomPacketPayload> type();

    default public ResourceLocation id() {
        return this.type().id();
    }

    public record Type<T extends CustomPacketPayload>(ResourceLocation id) {
    }
}

