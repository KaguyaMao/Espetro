/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.network.NetworkEvent$Context
 *  net.minecraftforge.network.PacketDistributor
 */
package com.example.espoints.network;

import com.example.espoints.config.TacticalMapJsonConfig;
import com.example.espoints.network.NetworkHandler;
import com.example.espoints.network.SyncTacticalMapBackgroundMessage;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import com.example.espoints.tile.TacticalMapTileService;
import com.example.espoints.util.ModLogger;
import java.util.function.Supplier;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

public class SyncTacticalMapConfigMessage {
    private static final String TACTICAL_MAP_HUD_CLASS = "com.example.espoints.hud.TacticalMapHUD";
    private final TacticalMapJsonConfig config;
    private final String source;
    private final TacticalMapTileService.Descriptor descriptor;

    public SyncTacticalMapConfigMessage() {
        this(TacticalMapJsonConfig.getInstance().copy(), TacticalMapJsonConfig.getInstance().getSource());
    }

    public SyncTacticalMapConfigMessage(TacticalMapJsonConfig config, String source) {
        this(config, source, TacticalMapTileService.get().descriptor());
    }

    SyncTacticalMapConfigMessage(TacticalMapJsonConfig config, String source, TacticalMapTileService.Descriptor descriptor) {
        this.config = config == null ? TacticalMapJsonConfig.createDefault() : config.copy();
        this.source = source == null ? "EsWorld EsConfig" : source;
        this.descriptor = descriptor == null ? TacticalMapTileService.Descriptor.EMPTY : descriptor;
    }

    public SyncTacticalMapConfigMessage(FriendlyByteBuf buf) {
        TacticalMapJsonConfig decoded = TacticalMapJsonConfig.createDefault();
        decoded.topLeftX = buf.readInt();
        decoded.topLeftZ = buf.readInt();
        decoded.bottomRightX = buf.readInt();
        decoded.bottomRightZ = buf.readInt();
        decoded.initialRange = buf.readInt();
        decoded.minimumRange = buf.readInt();
        decoded.backgroundImage = buf.m_130136_(256);
        decoded.backgroundImageWidth = buf.readInt();
        decoded.backgroundImageHeight = buf.readInt();
        decoded.showGrid = buf.readBoolean();
        decoded.showLabels = buf.readBoolean();
        decoded.tacticalMarkerDurationSeconds = buf.m_130242_();
        decoded.tacticalMarkerFadeSeconds = buf.m_130242_();
        decoded.tacticalMarkerMaxRenderDistance = buf.m_130242_();
        SyncTacticalMapConfigMessage.validate(decoded);
        this.config = decoded;
        this.source = buf.m_130136_(256);
        this.descriptor = SyncTacticalMapConfigMessage.readDescriptor(buf);
    }

    public static void encode(SyncTacticalMapConfigMessage msg, FriendlyByteBuf buf) {
        TacticalMapJsonConfig config = msg.config;
        SyncTacticalMapConfigMessage.validate(config);
        buf.writeInt(config.topLeftX);
        buf.writeInt(config.topLeftZ);
        buf.writeInt(config.bottomRightX);
        buf.writeInt(config.bottomRightZ);
        buf.writeInt(config.initialRange);
        buf.writeInt(config.minimumRange);
        buf.m_130072_(config.backgroundImage == null ? "" : config.backgroundImage, 256);
        buf.writeInt(config.backgroundImageWidth);
        buf.writeInt(config.backgroundImageHeight);
        buf.writeBoolean(config.showGrid);
        buf.writeBoolean(config.showLabels);
        buf.m_130130_(config.tacticalMarkerDurationSeconds);
        buf.m_130130_(config.tacticalMarkerFadeSeconds);
        buf.m_130130_(config.tacticalMarkerMaxRenderDistance);
        buf.m_130072_(msg.source, 256);
        SyncTacticalMapConfigMessage.writeDescriptor(buf, msg.descriptor != null && msg.descriptor.present() ? msg.descriptor : TacticalMapTileService.get().descriptor());
    }

    public static SyncTacticalMapConfigMessage decode(FriendlyByteBuf buf) {
        return new SyncTacticalMapConfigMessage(buf);
    }

    private static void validate(TacticalMapJsonConfig config) {
        if (config.bottomRightX <= config.topLeftX || config.bottomRightZ <= config.topLeftZ || config.initialRange <= 0 || config.initialRange > 60000000 || config.minimumRange <= 0 || config.minimumRange > config.initialRange || config.backgroundImageWidth < 0 || config.backgroundImageWidth > 32768 || config.backgroundImageHeight < 0 || config.backgroundImageHeight > 32768 || config.tacticalMarkerDurationSeconds <= 0 || config.tacticalMarkerDurationSeconds > 86400 || config.tacticalMarkerFadeSeconds <= 0 || config.tacticalMarkerFadeSeconds > 86400 || config.tacticalMarkerMaxRenderDistance < 0 || config.tacticalMarkerMaxRenderDistance > 1000000) {
            throw new IllegalArgumentException("Invalid tactical map configuration payload");
        }
    }

    public static void handle(SyncTacticalMapConfigMessage msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            if (!context.getDirection().getReceptionSide().isClient()) {
                return;
            }
            TacticalMapJsonConfig.apply(msg.config, "server map: " + msg.source);
            SyncTacticalMapConfigMessage.notifyHudConfigSynced();
            if (msg.descriptor != null && msg.descriptor.present()) {
                SyncTacticalMapBackgroundMessage.applyDescriptorOnClient(msg.descriptor);
            }
            ModLogger.debug("\u5ba2\u6237\u7aef\u6218\u672f\u5730\u56fe\u914d\u7f6e\u5df2\u540c\u6b65");
        });
        context.setPacketHandled(true);
    }

    private static void notifyHudConfigSynced() {
        try {
            Class<?> hudClass = Class.forName(TACTICAL_MAP_HUD_CLASS);
            Object hud = hudClass.getMethod("getInstance", new Class[0]).invoke(null, new Object[0]);
            hudClass.getMethod("onTacticalMapConfigSynced", new Class[0]).invoke(hud, new Object[0]);
        }
        catch (ReflectiveOperationException reflectiveOperationException) {
            // empty catch block
        }
    }

    private static void writeDescriptor(FriendlyByteBuf buf, TacticalMapTileService.Descriptor descriptor) {
        TacticalMapTileService.Descriptor value = descriptor == null ? TacticalMapTileService.Descriptor.EMPTY : descriptor;
        buf.writeBoolean(value.present());
        if (!value.present()) {
            return;
        }
        buf.m_130103_(value.session());
        buf.m_130072_(value.imagePath(), 256);
        buf.m_130072_(value.sha256(), 64);
        buf.m_130130_(value.width());
        buf.m_130130_(value.height());
        buf.m_130130_(value.tileSize());
        buf.m_130130_(value.maxLevel());
    }

    private static TacticalMapTileService.Descriptor readDescriptor(FriendlyByteBuf buf) {
        if (!buf.isReadable() || !buf.readBoolean()) {
            return TacticalMapTileService.Descriptor.EMPTY;
        }
        try {
            long session = buf.m_130258_();
            String imagePath = buf.m_130136_(256);
            String sha256 = buf.m_130136_(64);
            int width = buf.m_130242_();
            int height = buf.m_130242_();
            int tileSize = buf.m_130242_();
            int maxLevel = buf.m_130242_();
            if (session <= 0L || !sha256.matches("[0-9a-f]{64}") || tileSize != 512 || maxLevel < 0 || maxLevel >= 16) {
                return TacticalMapTileService.Descriptor.EMPTY;
            }
            TacticalMapPyramidLayout layout = new TacticalMapPyramidLayout(width, height);
            if (layout.maxLevel() != maxLevel) {
                return TacticalMapTileService.Descriptor.EMPTY;
            }
            return new TacticalMapTileService.Descriptor(session, imagePath, sha256, width, height, tileSize, maxLevel);
        }
        catch (RuntimeException ignored) {
            return TacticalMapTileService.Descriptor.EMPTY;
        }
    }

    public static void sendToPlayer(ServerPlayer player) {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), (Object)new SyncTacticalMapConfigMessage());
        }
        catch (Exception e) {
            ModLogger.error("\u5411\u73a9\u5bb6\u53d1\u9001\u6218\u672f\u5730\u56fe\u914d\u7f6e\u540c\u6b65\u6d88\u606f\u5931\u8d25: " + e.getMessage());
        }
    }

    public static void broadcastToAll() {
        try {
            NetworkHandler.INSTANCE.send(PacketDistributor.ALL.noArg(), (Object)new SyncTacticalMapConfigMessage());
            ModLogger.debug("\u5df2\u5411\u6240\u6709\u73a9\u5bb6\u5e7f\u64ad\u6218\u672f\u5730\u56fe\u914d\u7f6e\u540c\u6b65\u6d88\u606f");
        }
        catch (Exception e) {
            ModLogger.error("\u5e7f\u64ad\u6218\u672f\u5730\u56fe\u914d\u7f6e\u540c\u6b65\u6d88\u606f\u5931\u8d25: " + e.getMessage());
        }
    }
}

