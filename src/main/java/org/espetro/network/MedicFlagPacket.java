package org.espetro.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/**
 * 服务端 → 客户端：同步"本机玩家是否为医疗兵"。
 *
 * <p>数据来源是 EsFactions 职业配置里的 {@code "med": true}；客户端只用它决定
 * 是否绘制倒地队友的头顶/地图伤员标识，不参与任何战斗判定。</p>
 */
public record MedicFlagPacket(boolean medic) {

    public static MedicFlagPacket read(FriendlyByteBuf buf) {
        return new MedicFlagPacket(buf.readBoolean());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeBoolean(medic);
    }

    public void handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
            () -> () -> org.espetro.client.ClientMedicState.setMedic(medic)));
        context.setPacketHandled(true);
    }
}
