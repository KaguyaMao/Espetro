/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.example.espoints.network;

import com.example.espoints.ESPointsMod;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import com.example.espoints.tile.TacticalMapTileService;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

public final class SyncTacticalMapBackgroundMessage {
    private static final String CLIENT_CACHE_CLASS = "com.example.espoints.client.ClientTacticalMapTileCache";
    private final TacticalMapTileService.Descriptor descriptor;

    private SyncTacticalMapBackgroundMessage(TacticalMapTileService.Descriptor descriptor) {
        this.descriptor = descriptor == null ? TacticalMapTileService.Descriptor.EMPTY : descriptor;
    }

    public static void encode(SyncTacticalMapBackgroundMessage message, FriendlyByteBuf buf) {
        TacticalMapTileService.Descriptor descriptor = message.descriptor;
        buf.writeBoolean(descriptor.present());
        if (!descriptor.present()) {
            return;
        }
        buf.m_130103_(descriptor.session());
        buf.m_130072_(descriptor.imagePath(), 256);
        buf.m_130072_(descriptor.sha256(), 64);
        buf.m_130130_(descriptor.width());
        buf.m_130130_(descriptor.height());
        buf.m_130130_(descriptor.tileSize());
        buf.m_130130_(descriptor.maxLevel());
    }

    public static SyncTacticalMapBackgroundMessage decode(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return new SyncTacticalMapBackgroundMessage(TacticalMapTileService.Descriptor.EMPTY);
        }
        long session = buf.m_130258_();
        String imagePath = buf.m_130136_(256);
        String sha256 = buf.m_130136_(64);
        int width = buf.m_130242_();
        int height = buf.m_130242_();
        int tileSize = buf.m_130242_();
        int maxLevel = buf.m_130242_();
        if (session <= 0L || !sha256.matches("[0-9a-f]{64}") || tileSize != 512 || maxLevel < 0 || maxLevel >= 16) {
            throw new IllegalArgumentException("Invalid tactical map descriptor");
        }
        TacticalMapPyramidLayout layout = new TacticalMapPyramidLayout(width, height);
        if (layout.maxLevel() != maxLevel) {
            throw new IllegalArgumentException("Inconsistent tactical map descriptor");
        }
        return new SyncTacticalMapBackgroundMessage(new TacticalMapTileService.Descriptor(session, imagePath, sha256, width, height, tileSize, maxLevel));
    }

    public static void handle(SyncTacticalMapBackgroundMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> {
            if (context.getDirection().getReceptionSide().isClient()) {
                SyncTacticalMapBackgroundMessage.applyOnClient(message.descriptor);
            }
        });
        context.setPacketHandled(true);
    }

    private static void applyOnClient(TacticalMapTileService.Descriptor descriptor) {
        try {
            Class<?> type = Class.forName(CLIENT_CACHE_CLASS);
            Object cache = type.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            type.getMethod("applyDescriptor", TacticalMapTileService.Descriptor.class).invoke(cache, descriptor);
        }
        catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unable to apply tactical map descriptor on client", error);
        }
    }

    public static void applyDescriptorOnClient(TacticalMapTileService.Descriptor descriptor) {
        SyncTacticalMapBackgroundMessage.applyOnClient(descriptor);
    }

    public static void sendDescriptorOnly(ServerPlayer player) {
        TacticalMapTileService.Descriptor descriptor = TacticalMapTileService.get().descriptor();
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncTacticalMapBackgroundMessage(descriptor));
        if (descriptor.present()) {
            ESPointsMod.LOGGER.info("\u5df2\u5411 {} \u53d1\u9001\u6218\u672f\u5730\u56fe descriptor session={} {}x{} preview={}", (Object)player.m_36316_().getName(), (Object)descriptor.session(), (Object)descriptor.width(), (Object)descriptor.height(), (Object)descriptor.maxLevel());
        }
    }

    public static void sendToPlayer(ServerPlayer player) {
        SyncTacticalMapBackgroundMessage.sendDescriptorOnly(player);
        TacticalMapTileService.Descriptor descriptor = TacticalMapTileService.get().descriptor();
        if (player != null && descriptor.present()) {
            TacticalMapTileService.get().enqueuePreviewOnce(player.m_20148_());
        }
    }

    public static void broadcastToAll() {
        TacticalMapTileService.Descriptor descriptor = TacticalMapTileService.get().descriptor();
        NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncTacticalMapBackgroundMessage(descriptor));
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null || !descriptor.present()) {
            return;
        }
        for (ServerPlayer player : server.m_6846_().m_11314_()) {
            TacticalMapTileService.get().enqueuePreviewOnce(player.m_20148_());
        }
    }
}

