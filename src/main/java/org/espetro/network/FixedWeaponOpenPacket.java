package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import org.espetro.bastion.FixedWeaponExchange;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 客户端对固定武器按 F → 请求打开兑换轮盘。
 *
 * <p>服务端只在"确实是己方固定武器"时才回 {@link FixedWeaponWheelPacket}；
 * 对普通载具/实体保持**静默**（不提示、不打扰）。</p>
 */
public record FixedWeaponOpenPacket(UUID weaponEntityId) {

    public static FixedWeaponOpenPacket read(FriendlyByteBuf buf) {
        return new FixedWeaponOpenPacket(buf.readUUID());
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
            FixedWeaponExchange.openWheel(player, weaponEntityId);
        });
        context.setPacketHandled(true);
    }
}
