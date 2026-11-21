package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 地图揭晓页数据包：地图投票结束后显示胜出地图名称与预览图（无图显示占位）。
 */
public class MapRevealPacket {

    /** 地图显示名称。 */
    private final String mapDisplayName;
    /** 地图 folder（用于解析预览图，如 EsWorld/&lt;folder&gt;.png）。 */
    private final String mapFolder;
    /** 揭示页显示秒数。 */
    private final int durationSeconds;

    public MapRevealPacket(String mapDisplayName, String mapFolder, int durationSeconds) {
        this.mapDisplayName = mapDisplayName == null ? "" : mapDisplayName;
        this.mapFolder = mapFolder == null ? "" : mapFolder;
        this.durationSeconds = durationSeconds;
    }

    public static MapRevealPacket read(FriendlyByteBuf buf) {
        String mapDisplayName = buf.readUtf();
        String mapFolder = buf.readUtf();
        int durationSeconds = buf.readVarInt();
        return new MapRevealPacket(mapDisplayName, mapFolder, durationSeconds);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(mapDisplayName);
        buf.writeUtf(mapFolder);
        buf.writeVarInt(durationSeconds);
    }

    public void handle(Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleMapReveal", MapRevealPacket.class)
                    .invoke(null, this);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        ctx.get().setPacketHandled(true);
    }

    public String getMapDisplayName() {
        return mapDisplayName;
    }

    public String getMapFolder() {
        return mapFolder;
    }

    public int getDurationSeconds() {
        return durationSeconds;
    }
}
