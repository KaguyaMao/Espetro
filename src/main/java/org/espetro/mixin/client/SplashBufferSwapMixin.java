package org.espetro.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import org.espetro.client.splash.SplashSwapMixinBridge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** After queued rendering is drained, immediately before the visible back buffer is swapped. */
@Mixin(RenderSystem.class)
public abstract class SplashBufferSwapMixin {
    @Inject(method = "flipFrame", at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V", remap = false),
        remap = false, require = 1)
    private static void espetro$beforeBufferSwap(long windowHandle, CallbackInfo ci) {
        SplashSwapMixinBridge.beforeSwap(windowHandle);
    }

    @Inject(method = "flipFrame", at = @At(value = "INVOKE",
        target = "Lorg/lwjgl/glfw/GLFW;glfwSwapBuffers(J)V", shift = At.Shift.AFTER, remap = false),
        remap = false, require = 1)
    private static void espetro$afterBufferSwap(long windowHandle, CallbackInfo ci) {
        org.espetro.client.splash.SplashStartupReloadGate.afterSwap();
    }
}
