package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.bastion.FixedWeaponExchange;

import java.util.UUID;
import java.util.function.Supplier;

/** 客户端对固定武器按 F → 请求用所在 FOB 的弹药兑换一次弹药。 */
public record FixedWeaponExchangePacket(UUID weaponEntityId) {

    public static FixedWeaponExchangePacket read(FriendlyByteBuf buf) {
        return new FixedWeaponExchangePacket(buf.readUUID());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(weaponEntityId);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) {
                return;
            }
            FixedWeaponExchange.handle(player, weaponEntityId);
        });
        context.setPacketHandled(true);
    }
}
