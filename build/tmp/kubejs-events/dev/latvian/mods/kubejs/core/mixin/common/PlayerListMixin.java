/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.players.PlayerList
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package dev.latvian.mods.kubejs.core.mixin.common;

import dev.latvian.mods.kubejs.player.KubeJSPlayerEventHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={PlayerList.class})
public abstract class PlayerListMixin {
    @Inject(method={"respawn"}, at={@At(value="RETURN")})
    private void kjs$respawn(ServerPlayer serverPlayer, boolean keepData, CallbackInfoReturnable<ServerPlayer> cir) {
        KubeJSPlayerEventHandler.respawn(serverPlayer, (ServerPlayer)cir.getReturnValue(), keepData);
    }
}

