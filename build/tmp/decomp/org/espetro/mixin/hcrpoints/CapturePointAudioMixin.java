/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.hcrpoints;

import net.minecraft.server.MinecraftServer;
import org.espetro.api.EspetroAPI;
import org.espetro.audio.AudioCuePolicy;
import org.espetro.team.TroopCountManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.example.hcrpoints.capturepoint.CapturePointManager", "com.example.espoints.capturepoint.CapturePointManager"}, remap=false)
public abstract class CapturePointAudioMixin {
    @Inject(method={"giveCaptureReward(Lnet/minecraft/server/MinecraftServer;Ljava/lang/String;Ljava/lang/String;)V"}, at={@At(value="HEAD")}, require=0, remap=false)
    private void espetro$onCapturePointCaptured(MinecraftServer server, String captorName, String pointName, CallbackInfo ci) {
        EspetroAPI.onCapturePointCaptured(captorName);
    }

    @Inject(method={"endOperationModeWithResult(Ljava/lang/String;Ljava/lang/String;)V"}, at={@At(value="RETURN")}, require=0, remap=false)
    private void espetro$onOperationEnded(String winnerTeam, String loserTeam, CallbackInfo ci) {
        String winner = AudioCuePolicy.normalizeTeam(winnerTeam);
        if (winner != null) {
            if ("ATTACK".equals(winner)) {
                TroopCountManager.getInstance().setDefendTroops(0);
            }
            EspetroAPI.endRound(winner);
        }
    }
}

