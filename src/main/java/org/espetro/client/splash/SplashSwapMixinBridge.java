package org.espetro.client.splash;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;

/** Kept separate so the RenderSystem mixin has no renderer initialization of its own. */
public final class SplashSwapMixinBridge {
    private SplashSwapMixinBridge() { }

    public static void beforeSwap(long windowHandle) {
        if (!RenderSystem.isOnRenderThread()) return;
        Minecraft minecraft = Minecraft.getInstance();
        if (minecraft == null || minecraft.getWindow() == null
                || minecraft.getWindow().getWindow() != windowHandle) return;
        SplashEarlyRenderer.get().renderOverlay(minecraft);
    }
}
