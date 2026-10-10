/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.network.api;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

public interface INetworkContext {
    public boolean isClientSide();

    public boolean isServerSide();

    public ServerPlayer sender();

    public Minecraft client();

    public void enqueueWork(Runnable var1);
}

