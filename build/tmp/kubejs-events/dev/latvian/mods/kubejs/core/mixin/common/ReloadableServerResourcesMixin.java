/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.commands.Commands$CommandSelection
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.RegistryAccess$Frozen
 *  net.minecraft.server.ReloadableServerResources
 *  net.minecraft.world.flag.FeatureFlagSet
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.item.ingredient.TagContext;
import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value={ReloadableServerResources.class})
public abstract class ReloadableServerResourcesMixin {
    @Inject(method={"<init>"}, at={@At(value="RETURN")})
    private void init(RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
        ServerScriptManager.instance.updateResources((ReloadableServerResources)this, (RegistryAccess)frozen);
    }

    @Inject(method={"updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V"}, at={@At(value="RETURN")})
    public void updateRegistryTags(RegistryAccess registryAccess, CallbackInfo ci) {
        TagContext.INSTANCE.setValue((Object)TagContext.usingRegistry(registryAccess));
    }
}

