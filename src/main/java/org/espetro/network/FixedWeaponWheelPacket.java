package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.UUID;
import java.util.function.Supplier;

/**
 * 服务端 → 客户端：固定武器兑换轮盘数据。
 *
 * @param weaponEntityId 固定武器实体
 * @param displayName    显示名（如"短号反坦克导弹"）
 * @param ammoItemId     弹药物品注册名（客户端拿来显示图标；虚拟弹药为空串）
 * @param amount         每次兑换产出多少发
 * @param ammoCost       每次兑换消耗多少 FOB 弹药
 * @param fobAmmo        所在 FOB 当前弹药
 * @param affordable     当前是否够兑换
 * @param reason         不够兑换的原因（可空串）
 */
public record FixedWeaponWheelPacket(UUID weaponEntityId, String displayName, String ammoItemId,
                                     int amount, int ammoCost, int fobAmmo,
                                     boolean affordable, String reason) {

    public static FixedWeaponWheelPacket read(FriendlyByteBuf buf) {
        return new FixedWeaponWheelPacket(buf.readUUID(), buf.readUtf(128), buf.readUtf(128),
            buf.readVarInt(), buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readUtf(128));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(weaponEntityId);
        buf.writeUtf(displayName == null ? "" : displayName, 128);
        buf.writeUtf(ammoItemId == null ? "" : ammoItemId, 128);
        buf.writeVarInt(Math.max(0, amount));
        buf.writeVarInt(Math.max(0, ammoCost));
        buf.writeVarInt(Math.max(0, fobAmmo));
        buf.writeBoolean(affordable);
        buf.writeUtf(reason == null ? "" : reason, 128);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            try {
                Class.forName("org.espetro.client.ClientPacketHandlers")
                    .getMethod("handleFixedWeaponWheel", FixedWeaponWheelPacket.class)
                    .invoke(null, this);
            } catch (ReflectiveOperationException e) {
                org.espetro.Espetro.LOGGER.error("处理固定武器轮盘失败", e);
            }
        });
        context.setPacketHandled(true);
    }
}
