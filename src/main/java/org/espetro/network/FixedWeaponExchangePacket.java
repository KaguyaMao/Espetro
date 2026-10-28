package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.bastion.FixedWeaponExchange;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 客户端对固定武器按 F → 请求用所在 FOB 的弹药兑换一次弹药。
 *
 * @param weaponEntityId 固定武器实体
 * @param index          兑换条目下标（多条目武器如迫击炮：0 = 高爆、1 = 烟幕）
 */
public record FixedWeaponExchangePacket(UUID weaponEntityId, int index) {

    public static FixedWeaponExchangePacket read(FriendlyByteBuf buf) {
        return new FixedWeaponExchangePacket(buf.readUUID(), buf.readVarInt());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(weaponEntityId);
        buf.writeVarInt(Math.max(0, index));
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            FixedWeaponExchange.handle(player, weaponEntityId, index);
        });
        context.setPacketHandled(true);
    }
}
