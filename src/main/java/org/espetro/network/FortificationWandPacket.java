package org.espetro.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import javax.annotation.Nullable;
import java.util.function.Supplier;

/**
 * 工事编辑器选区同步（S2C）。客户端只按服务端给的坐标画线框与 HUD，
 * 不做任何射线检测或本地判定。
 */
public record FortificationWandPacket(boolean active, @Nullable BlockPos a, @Nullable BlockPos b,
                                      @Nullable BlockPos anchor, int sizeX, int sizeY, int sizeZ,
                                      int blockCount, int entityCount, boolean ready,
                                      String problem) {

    /**
     * 提示字符串上限。预检消息带坐标时可能很长（>200 字符），
     * 超长会让 FriendlyByteBuf.writeUtf 抛 EncoderException —— 而这条包是在
     * 服务端 tick 里发的，异常会直接崩服。所以写前必须截断。
     */
    private static final int MAX_PROBLEM = 256;

    public FortificationWandPacket {
        problem = clamp(problem);
    }

    private static String clamp(String raw) {
        if (raw == null) return "";
        return raw.length() <= MAX_PROBLEM ? raw : raw.substring(0, MAX_PROBLEM);
    }

    public static FortificationWandPacket clear() {
        return new FortificationWandPacket(false, null, null, null, 0, 0, 0, 0, 0, false, "");
    }

    public static FortificationWandPacket read(FriendlyByteBuf buf) {
        int flags = buf.readUnsignedByte();
        boolean active = (flags & 1) != 0;
        BlockPos a = (flags & 2) != 0 ? buf.readBlockPos() : null;
        BlockPos b = (flags & 4) != 0 ? buf.readBlockPos() : null;
        BlockPos anchor = (flags & 8) != 0 ? buf.readBlockPos() : null;
        int sizeX = buf.readVarInt();
        int sizeY = buf.readVarInt();
        int sizeZ = buf.readVarInt();
        int blockCount = buf.readVarInt();
        int entityCount = buf.readVarInt();
        boolean ready = buf.readBoolean();
        String problem = buf.readUtf(MAX_PROBLEM);
        return new FortificationWandPacket(active, a, b, anchor, sizeX, sizeY, sizeZ,
            blockCount, entityCount, ready, problem);
    }

    public void write(FriendlyByteBuf buf) {
        int flags = (active ? 1 : 0)
            | (a != null ? 2 : 0)
            | (b != null ? 4 : 0)
            | (anchor != null ? 8 : 0);
        buf.writeByte(flags);
        if (a != null) buf.writeBlockPos(a);
        if (b != null) buf.writeBlockPos(b);
        if (anchor != null) buf.writeBlockPos(anchor);
        buf.writeVarInt(sizeX);
        buf.writeVarInt(sizeY);
        buf.writeVarInt(sizeZ);
        buf.writeVarInt(blockCount);
        buf.writeVarInt(entityCount);
        buf.writeBoolean(ready);
        buf.writeUtf(clamp(problem), MAX_PROBLEM);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleFortificationWand", FortificationWandPacket.class)
                    .invoke(null, this);
            } catch (ReflectiveOperationException e) {
                org.espetro.Espetro.LOGGER.error("处理工事选定棒同步失败", e);
            }
        });
        context.setPacketHandled(true);
    }
}
