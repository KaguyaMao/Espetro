/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.packs.resources.CloseableResourceManager
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.ModifyVariable
 */
package dev.latvian.mods.kubejs.core.mixin.common.inject_resources;

import dev.latvian.mods.kubejs.server.ServerScriptManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.CloseableResourceManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(value={MinecraftServer.class})
public abstract class MinecraftServerMixin {
    @ModifyVariable(method={"*"}, at=@At(value="STORE"), remap=false)
    public CloseableResourceManager wrapResourceManager(CloseableResourceManager original) {
        ServerScriptManager.instance = new ServerScriptManager((MinecraftServer)this);
        return ServerScriptManager.instance.wrapResourceManager(original);
    }
}

