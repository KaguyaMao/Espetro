package org.espetro.mixin.network;

import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * 放宽服务端入站连接的 netty 读超时。
 * <p>Forge 把服务端的值提成了字段 {@code ServerConnectionListener.READ_TIMEOUT}（原版 30 秒），
 * 这里用 {@code @Redirect} 改读取结果，避免猜常量是否被内联。</p>
 */
@Mixin(targets = "net.minecraft.server.network.ServerConnectionListener$1", remap = false)
public abstract class ServerReadTimeoutMixin {

    @Redirect(
        method = "initChannel",
        at = @At(
            value = "FIELD",
            target = "Lnet/minecraft/server/network/ServerConnectionListener;READ_TIMEOUT:I",
            opcode = Opcodes.GETSTATIC
        ),
        remap = false
    )
    private int espetro$extendServerReadTimeout() {
        return EspetroNetworkTuning.READ_TIMEOUT_SECONDS;
    }
}
