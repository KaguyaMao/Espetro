/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess$RegistryEntry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.server.packs.resources.PreparableReloadListener$PreparationBarrier
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.tags.TagLoader
 *  net.minecraft.tags.TagManager
 *  net.minecraft.tags.TagManager$LoadResult
 *  net.minecraft.util.profiling.ProfilerFiller
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.At$Shift
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 *  org.spongepowered.asm.mixin.injection.callback.LocalCapture
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import net.minecraft.tags.TagManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(value={TagManager.class})
public abstract class TagManagerMixin {
    @Inject(method={"createLoader"}, at={@At(value="INVOKE", target="Lnet/minecraft/tags/TagLoader;<init>(Ljava/util/function/Function;Ljava/lang/String;)V", shift=At.Shift.BY, by=2)}, locals=LocalCapture.CAPTURE_FAILHARD)
    private <T> void kjs$saveRegistryToTagLoader(ResourceManager rm, Executor executor, RegistryAccess.RegistryEntry<T> reg, CallbackInfoReturnable<CompletableFuture<TagManager.LoadResult<T>>> cir, ResourceKey<? extends Registry<T>> key, Registry<T> registry, TagLoader<Holder<T>> loader) {
        ((TagLoaderKJS)loader).kjs$setRegistry(registry);
    }

    @Inject(method={"reload"}, at={@At(value="INVOKE", target="Lnet/minecraft/core/RegistryAccess;registries()Ljava/util/stream/Stream;")})
    private void kjs$reload(PreparableReloadListener.PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2, CallbackInfoReturnable<CompletableFuture<Void>> cir) {
        ItemStackJS.clearAllCaches();
    }
}

