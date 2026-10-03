/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 */
package com.sighs.apricityui.network;

import java.util.function.Supplier;
import net.minecraft.server.MinecraftServer;

public final class NetworkPlatform {
    private static volatile Supplier<MinecraftServer> currentServer = () -> null;

    private NetworkPlatform() {
    }

    public static MinecraftServer currentServer() {
        return currentServer.get();
    }

    public static void setCurrentServerSupplier(Supplier<MinecraftServer> supplier) {
        currentServer = supplier == null ? () -> null : supplier;
    }
}

