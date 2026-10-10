/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.network.NetworkHandler;
import com.example.espoints.tile.TacticalMapTileService;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public final class SyncTacticalMapTileMessage {
    private static final String CLIENT_CACHE_CLASS = "com.example.espoints.client.ClientTacticalMapTileCache";
    private final long session;
    private final int level;
    private final int x;
    private final int y;
    private final int width;
    private final int height;
    private final byte[] bytes;

    private SyncTacticalMapTileMessage(long session, int level, int x, int y, int width, int height, byte[] bytes) {
        this.session = session;
        this.level = level;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.bytes = bytes;
    }

    public static void encode(SyncTacticalMapTileMessage message, FriendlyByteBuf buf) {
        buf.m_130103_(message.session);
        buf.m_130130_(message.level);
        buf.m_130130_(message.x);
        buf.m_130130_(message.y);
        buf.m_130130_(message.width);
        buf.m_130130_(message.height);
        buf.m_130087_(message.bytes);
    }

    public static SyncTacticalMapTileMessage decode(FriendlyByteBuf buf) {
        long session = buf.m_130258_();
        int level = buf.m_130242_();
        int x = buf.m_130242_();
        int y = buf.m_130242_();
        int width = buf.m_130242_();
        int height = buf.m_130242_();
        byte[] bytes = buf.m_130101_(0x200000);
        if (session <= 0L || level < 0 || level >= 16 || x < 0 || x >= 64 || y < 0 || y >= 64 || width <= 0 || width > 512 || height <= 0 || height > 512 || bytes.length < 8 || !SyncTacticalMapTileMessage.isPng(bytes)) {
            throw new IllegalArgumentException("Invalid tactical tile payload");
        }
        return new SyncTacticalMapTileMessage(session, level, x, y, width, height, bytes);
    }

    private static boolean isPng(byte[] bytes) {
        return bytes[0] == -119 && bytes[1] == 80 && bytes[2] == 78 && bytes[3] == 71 && bytes[4] == 13 && bytes[5] == 10 && bytes[6] == 26 && bytes[7] == 10;
    }

    public static void handle(SyncTacticalMapTileMessage message, Supplier<NetworkEvent.Context> contextSupplier) {
        NetworkEvent.Context context = contextSupplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SyncTacticalMapTileMessage.runOnClientThread(message)));
        context.setPacketHandled(true);
    }

    private static void runOnClientThread(SyncTacticalMapTileMessage message) {
        try {
            Class<?> minecraft = Class.forName("net.minecraft.client.Minecraft");
            Object instance = minecraft.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            minecraft.getMethod("execute", Runnable.class).invoke(instance, () -> SyncTacticalMapTileMessage.applyOnClient(message));
        }
        catch (ReflectiveOperationException error) {
            SyncTacticalMapTileMessage.applyOnClient(message);
        }
    }

    private static void applyOnClient(SyncTacticalMapTileMessage message) {
        try {
            Class<?> type = Class.forName(CLIENT_CACHE_CLASS);
            Object cache = type.getMethod("get", new Class[0]).invoke(null, new Object[0]);
            type.getMethod("accept", Long.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, byte[].class).invoke(cache, message.session, message.level, message.x, message.y, message.width, message.height, message.bytes);
        }
        catch (ReflectiveOperationException error) {
            throw new IllegalStateException("Unable to apply tactical tile on client", error);
        }
    }

    public static void sendToPlayer(ServerPlayer player, long session, int level, int x, int y, byte[] bytes) {
        TacticalMapTileService service = TacticalMapTileService.get();
        if (service.descriptor().session() != session) {
            return;
        }
        int width = service.tileWidth(level, x);
        int height = service.tileHeight(level, y);
        if (width <= 0 || height <= 0 || bytes == null || bytes.length <= 0 || bytes.length > 0x200000) {
            return;
        }
        SyncTacticalMapTileMessage message = new SyncTacticalMapTileMessage(session, level, x, y, width, height, bytes);
        if (player.f_8906_ != null && player.f_8906_.f_9742_.m_129531_()) {
            DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> SyncTacticalMapTileMessage.runOnClientThread(message));
        }
        NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)message);
    }
}

