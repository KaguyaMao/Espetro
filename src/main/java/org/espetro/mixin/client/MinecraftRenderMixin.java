package org.espetro.mixin.client;

import net.minecraft.client.Minecraft;
import org.espetro.client.splash.SplashEarlyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 在 {@code Minecraft#runTick} 的最末尾叠加「资源加载期启动视频」。
 *
 * <p>为什么是这里：加载幕布（ForgeLoadingOverlay / LoadingOverlay / DrippyLoading）
 * 的绘制是 {@code GuiGraphics} 批量提交的，真正 flush 到显存发生在 {@code runTick}
 * 收尾。任何在 {@code GameRenderer#render} 内部或紧随其后的即时绘制都会被那批顶点盖掉。
 * 放在 {@code runTick} 的 TAIL，即使此时已经交换过缓冲区，画面也会在下一帧显示出来，
 * 重叠绘制不会闪。</p>
 */
@Mixin(Minecraft.class)
public abstract class MinecraftRenderMixin {

    @Inject(method = "runTick(Z)V", at = @At("TAIL"))
    private void espetro$renderSplashVideo(boolean renderLevel, CallbackInfo ci) {
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null) {
            return;
        }
        SplashEarlyRenderer.get().renderOverlay(minecraft);
    }
}
