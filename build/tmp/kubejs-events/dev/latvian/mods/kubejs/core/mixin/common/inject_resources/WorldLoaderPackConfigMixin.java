/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.WorldLoader$PackConfig
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.latvian.mods.kubejs.core.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.WorldLoader;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={WorldLoader.PackConfig.class})
public abstract class WorldLoaderPackConfigMixin {
    @ModifyVariable(method={"createResourceManager"}, at=@At(value="STORE"))
    private CloseableResourceManager injectKubeJSPacks(CloseableResourceManager original) {
        ServerScriptManager.instance = new ServerScriptManager(null);
        return ServerScriptManager.instance.wrapResourceManager(original);
    }
}

