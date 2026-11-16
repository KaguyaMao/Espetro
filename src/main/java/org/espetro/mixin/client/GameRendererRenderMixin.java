package org.espetro.mixin.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import org.espetro.client.splash.SplashEarlyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 备用钩子：{@code GameRenderer#render} 的尾部。
 *
 * <p>与 {@link MinecraftRenderMixin} 同时挂，二者都调用同一个幂等的
 * {@code renderOverlay}（按墙钟选帧），因此重复调用不会影响画面，
 * 只是多一层保险：只要任意一个注入点匹配成功，视频就能出来。</p>
 */
@Mixin(GameRenderer.class)
public abstract class GameRendererRenderMixin {

    @Inject(method = "render(FJZ)V", at = @At("TAIL"))
    private void espetro$renderSplashVideoTail(float partialTick, long nanoTime, boolean renderLevel,
        CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        SplashEarlyRenderer.get().renderOverlay(minecraft);
    }
}
