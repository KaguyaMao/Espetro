package org.espetro.mixin.network;

import io.netty.handler.timeout.ReadTimeoutHandler;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Constant;

/**
 * 放宽客户端出站连接的 netty 读超时（{@code new ReadTimeoutHandler(30)} → 120 秒）。
 * 原版 30 秒收不到任何字节就断开，弱网下（抖包/丢包）会直接把玩家踢出。
 */
@Mixin(targets = "net.minecraft.network.Connection$1", remap = false)
public abstract class ClientReadTimeoutMixin {

    @ModifyConstant(method = "initChannel", constant = @Constant(intValue = 30), remap = false)
    private int espetro$extendClientReadTimeout(int original) {
        return EspetroNetworkTuning.READ_TIMEOUT_SECONDS;
    }
}
