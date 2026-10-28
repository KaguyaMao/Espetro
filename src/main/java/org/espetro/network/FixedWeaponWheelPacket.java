package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

/**
 * 服务端 → 客户端：固定武器兑换轮盘数据。
 *
 * <p>一个固定武器可以有多个兑换条目（例如迫击炮同时提供"高爆弹 / 烟幕弹"），
 * 每个条目独立配置弹种与价格，在轮盘里各占一个槽位。</p>
 *
 * @param weaponEntityId 固定武器实体
 * @param displayName    显示名（如"短号反坦克导弹"）
 * @param fobAmmo        所在 FOB 当前弹药
 * @param slots          兑换条目（顺序 = 轮盘槽位顺序）
 */
public record FixedWeaponWheelPacket(UUID weaponEntityId, String displayName, int fobAmmo,
                                     List<Slot> slots) {

    public static final int MAX_SLOTS = 8;

    /**
     * 一个兑换条目。
     *
     * @param label      条目名（如"高爆弹 3 发"）
     * @param ammoItemId 弹药物品注册名（客户端显示图标；虚拟弹药为空串）
     * @param amount     每次兑换产出多少发
     * @param cost       每次兑换消耗多少 FOB 弹药
     * @param affordable 当前是否够兑换
     * @param reason     不够兑换的原因（可空串）
     */
    public record Slot(String label, String ammoItemId, int amount, int cost,
                       boolean affordable, String reason) {
    }

    public static FixedWeaponWheelPacket read(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        String name = buf.readUtf(128);
        int fobAmmo = buf.readVarInt();
        int size = Math.min(MAX_SLOTS, Math.max(0, buf.readVarInt()));
        List<Slot> slots = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            slots.add(new Slot(buf.readUtf(128), buf.readUtf(128),
                buf.readVarInt(), buf.readVarInt(), buf.readBoolean(), buf.readUtf(128)));
        }
        return new FixedWeaponWheelPacket(id, name, fobAmmo, List.copyOf(slots));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUUID(weaponEntityId);
        buf.writeUtf(displayName == null ? "" : displayName, 128);
        buf.writeVarInt(Math.max(0, fobAmmo));
        List<Slot> list = slots == null ? List.of() : slots;
        buf.writeVarInt(Math.min(list.size(), MAX_SLOTS));
        for (int i = 0; i < list.size() && i < MAX_SLOTS; i++) {
            Slot slot = list.get(i);
            buf.writeUtf(slot.label() == null ? "" : slot.label(), 128);
            buf.writeUtf(slot.ammoItemId() == null ? "" : slot.ammoItemId(), 128);
            buf.writeVarInt(Math.max(0, slot.amount()));
            buf.writeVarInt(Math.max(0, slot.cost()));
            buf.writeBoolean(slot.affordable());
            buf.writeUtf(slot.reason() == null ? "" : slot.reason(), 128);
        }
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
