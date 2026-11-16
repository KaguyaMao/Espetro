package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 己方载具同步（S→C）：把玩家附近的己方载具 UUID 列表下发一份。
 *
 * <p>客户端的实体标签不会同步（{@code espetro_team_*} 只存在服务端），
 * 所以准心提示没法在客户端自己判断载具归属，由服务端定期对账推送。</p>
 */
public final class FriendlyVehiclesPacket {

    private final List<UUID> vehicles;

    public FriendlyVehiclesPacket(List<UUID> vehicles) {
        this.vehicles = List.copyOf(vehicles);
    }

    public List<UUID> getVehicles() {
        return vehicles;
    }

    public static FriendlyVehiclesPacket read(FriendlyByteBuf buf) {
        int count = buf.readVarInt();
        List<UUID> ids = new ArrayList<>(count);
        for (int i = 0; i < count; i++) {
            ids.add(buf.readUUID());
        }
        return new FriendlyVehiclesPacket(ids);
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeVarInt(vehicles.size());
        for (UUID id : vehicles) {
            buf.writeUUID(id);
        }
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            if (context.getSender() != null) {
                return;
            }
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleFriendlyVehicles", FriendlyVehiclesPacket.class)
                    .invoke(null, this);
            } catch (Throwable ignored) {
                // 客户端类不可用时静默忽略
            }
        });
        context.setPacketHandled(true);
    }
}
