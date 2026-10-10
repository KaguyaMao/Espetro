/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.TickTask
 *  net.minecraft.util.thread.BlockableEventLoop
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.fml.LogicalSide
 */
package net.minecraftforge.common.util;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
import net.minecraft.util.thread.BlockableEventLoop;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.LogicalSide;

public class LogicalSidedProvider<T> {
    public static final LogicalSidedProvider<BlockableEventLoop<? super TickTask>> WORKQUEUE = new LogicalSidedProvider<BlockableEventLoop>(Supplier::get, Supplier::get);
    public static final LogicalSidedProvider<Optional<Level>> CLIENTWORLD = new LogicalSidedProvider<Optional>(c -> Optional.of(((Minecraft)c.get()).f_91073_), s -> Optional.empty());
    private static Supplier<Minecraft> client;
    private static Supplier<MinecraftServer> server;
    private final Function<Supplier<Minecraft>, T> clientSide;
    private final Function<Supplier<MinecraftServer>, T> serverSide;

    public static void setClient(Supplier<Minecraft> client) {
        LogicalSidedProvider.client = client;
    }

    public static void setServer(Supplier<MinecraftServer> server) {
        LogicalSidedProvider.server = server;
    }

    private LogicalSidedProvider(Function<Supplier<Minecraft>, T> clientSide, Function<Supplier<MinecraftServer>, T> serverSide) {
        this.clientSide = clientSide;
        this.serverSide = serverSide;
    }

    public T get(LogicalSide side) {
        return side == LogicalSide.CLIENT ? this.clientSide.apply(client) : this.serverSide.apply(server);
    }
}

