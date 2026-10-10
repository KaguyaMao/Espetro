/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.Pseudo
 *  org.spongepowered.asm.mixin.Shadow
 *  org.spongepowered.asm.mixin.Unique
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 */
package org.espetro.mixin.hcrpoints;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Team;
import org.espetro.Espetro;
import org.espetro.api.EspetroAPI;
import org.espetro.audio.AudioCuePolicy;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets={"com.example.hcrpoints.capturepoint.CapturePoint", "com.example.espoints.capturepoint.CapturePoint"}, remap=false)
public abstract class CapturePointNeutralizedAudioMixin {
    @Unique
    private static Method espetro$getStateMethod;
    @Unique
    private static boolean espetro$stateLookupWarningLogged;
    @Unique
    private String espetro$audioOldState;
    @Unique
    private String espetro$audioOldCaptor;

    @Shadow(remap=false)
    public abstract String getCaptorName();

    @Inject(method={"updateStatus(Ljava/util/List;)V"}, at={@At(value="HEAD")}, require=0, remap=false)
    private void espetro$rememberOwnerBeforeUpdate(List<?> playersInPoint, CallbackInfo ci) {
        this.espetro$audioOldState = this.espetro$getStateName();
        this.espetro$audioOldCaptor = this.getCaptorName();
    }

    @Inject(method={"updateStatus(Ljava/util/List;)V"}, at={@At(value="RETURN")}, require=0, remap=false)
    private void espetro$onPointNeutralized(List<?> playersInPoint, CallbackInfo ci) {
        if (this.espetro$audioOldCaptor == null || this.espetro$audioOldCaptor.isBlank() || "NEUTRAL".equals(this.espetro$audioOldState) || !"NEUTRAL".equals(this.espetro$getStateName())) {
            return;
        }
        EspetroAPI.onCapturePointNeutralized(this.espetro$audioOldCaptor, this.espetro$findAttackingTeam(playersInPoint));
    }

    @Unique
    private String espetro$findAttackingTeam(List<?> playersInPoint) {
        String owner = AudioCuePolicy.normalizeTeam(this.espetro$audioOldCaptor);
        if (playersInPoint == null) {
            return null;
        }
        for (Object candidate : playersInPoint) {
            String teamName;
            ServerPlayer player;
            Team team;
            if (!(candidate instanceof ServerPlayer) || (team = (player = (ServerPlayer)candidate).m_5647_()) == null || Objects.equals(owner, AudioCuePolicy.normalizeTeam(teamName = team.m_5758_()))) continue;
            return teamName;
        }
        return null;
    }

    @Unique
    private String espetro$getStateName() {
        try {
            String string;
            Object state;
            Method method = espetro$getStateMethod;
            if (method == null) {
                espetro$getStateMethod = method = this.getClass().getMethod("getState", new Class[0]);
            }
            if ((state = method.invoke(this, new Object[0])) instanceof Enum) {
                Enum enumState = (Enum)state;
                string = enumState.name();
            } else {
                string = String.valueOf(state);
            }
            return string;
        }
        catch (ReflectiveOperationException | RuntimeException exception) {
            if (!espetro$stateLookupWarningLogged) {
                espetro$stateLookupWarningLogged = true;
                Espetro.LOGGER.warn("[\u636e\u70b9\u8bed\u97f3] \u65e0\u6cd5\u8bfb\u53d6 ESPoints \u636e\u70b9\u72b6\u6001\uff0c\u4e2d\u7acb\u5316\u8bed\u97f3\u5df2\u8df3\u8fc7", (Throwable)exception);
            }
            return null;
        }
    }
}

