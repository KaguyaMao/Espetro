/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.server.packs.resources.ResourceManager
 *  net.minecraft.tags.TagLoader
 *  net.minecraft.tags.TagLoader$EntryWithSource
 *  org.jetbrains.annotations.Nullable
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.core.TagLoaderKJS;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import java.util.List;
import java.util.Map;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.tags.TagLoader;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={TagLoader.class})
public abstract class TagLoaderMixin<T>
implements TagLoaderKJS<T> {
    @Unique
    @Nullable
    private Registry<T> kjs$storedRegistry;

    @Inject(method={"load"}, at={@At(value="RETURN")})
    private void customTags(ResourceManager resourceManager, CallbackInfoReturnable<Map<ResourceLocation, List<TagLoader.EntryWithSource>>> cir) {
        ServerScriptManager ssm = ServerScriptManager.instance;
        if (ssm != null) {
            this.kjs$customTags(ssm, (Map)cir.getReturnValue());
        }
    }

    @Override
    public void kjs$setRegistry(Registry<T> registry) {
        this.kjs$storedRegistry = registry;
    }

    @Override
    @Nullable
    public Registry<T> kjs$getRegistry() {
        return this.kjs$storedRegistry;
    }
}

