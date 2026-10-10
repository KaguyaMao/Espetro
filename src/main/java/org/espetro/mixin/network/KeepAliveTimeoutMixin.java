package org.espetro.mixin.network;

import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

/**
 * 放宽原版 KeepAlive 判定窗口（15 秒 → 60 秒）。
 *
 * <p>{@code ServerGamePacketListenerImpl.tick()} 里 {@code Util.getMillis() - keepAliveTime >= 15000L}：
 * 到点若上一次 KeepAlive 仍未收到回应，就直接 {@code disconnect("Timed out")}——
 * 这就是日志里 30/60 秒被踢的来源。放宽后心跳间隔与容忍窗口都变大（约 120 秒容忍），
 * 弱网下不再因为一次卡顿就被踢。</p>
 */
@Mixin(ServerGamePacketListenerImpl.class)
public abstract class KeepAliveTimeoutMixin {

    @ModifyConstant(method = "tick", constant = @Constant(longValue = 15000L))
    private long espetro$extendKeepAliveWindow(long original) {
        return EspetroNetworkTuning.KEEP_ALIVE_WINDOW_MILLIS;
    }
}
