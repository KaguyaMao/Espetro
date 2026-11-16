package org.espetro.mixin.client;

import java.util.concurrent.CompletableFuture;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraft.util.Unit;
import org.espetro.client.splash.SplashStartupReloadGate;
import org.espetro.client.splash.SplashVideoAssets;
import org.espetro.client.splash.SplashVideoController;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ReloadableResourceManager.class)
public abstract class SplashStartupReloadMixin {
    @Shadow @Final private PackType type;

    @ModifyVariable(method = "createReload", at = @At("HEAD"), argsOnly = true, require = 1)
    private CompletableFuture<Unit> espetro$deferStartupApply(CompletableFuture<Unit> initial) {
        if (type != PackType.CLIENT_RESOURCES || SplashVideoController.wasPlayedByScreen()
                || !SplashVideoAssets.available()) return initial;
        return SplashStartupReloadGate.holdStartup(initial);
    }
}
