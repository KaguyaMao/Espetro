package org.espetro.mixin.hcrpoints;

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

import java.lang.reflect.Method;
import java.util.List;

/** Emits formation audio from authoritative capture state transitions. */
@Pseudo
@Mixin(targets = {
    "com.example.hcrpoints.capturepoint.CapturePoint",
    "com.example.espoints.capturepoint.CapturePoint"
}, remap = false)
public abstract class CapturePointNeutralizedAudioMixin {
    @Shadow(remap = false)
    public abstract String getCaptorName();

    @Unique
    private static Method espetro$getStateMethod;
    @Unique
    private static boolean espetro$stateLookupWarningLogged;
    @Unique
    private String espetro$audioOldState;
    @Unique
    private String espetro$audioOldCaptor;
    @Unique
    private int espetro$audioUpdateDepth;

    @Inject(method = "updateStatus(Ljava/util/List;)V", at = @At("HEAD"),
        require = 0, remap = false)
    private void espetro$rememberOwnerBeforeUpdate(List<?> playersInPoint, CallbackInfo ci) {
        espetro$beginAudioUpdate();
    }

    @Inject(method = "updateStatus(Ljava/util/List;)V", at = @At("RETURN"),
        require = 0, remap = false)
    private void espetro$finishLegacyUpdate(List<?> playersInPoint, CallbackInfo ci) {
        espetro$finishAudioUpdate(playersInPoint);
    }

    @Inject(method = "updateStatus(Ljava/util/List;I)V", at = @At("HEAD"),
        require = 0, remap = false)
    private void espetro$rememberOwnerBeforeElapsedUpdate(List<?> playersInPoint, int elapsedTicks, CallbackInfo ci) {
        if (elapsedTicks >= 0) {
            espetro$beginAudioUpdate();
        }
    }

    @Inject(method = "updateStatus(Ljava/util/List;I)V", at = @At("RETURN"),
        require = 0, remap = false)
    private void espetro$finishElapsedUpdate(List<?> playersInPoint, int elapsedTicks, CallbackInfo ci) {
        espetro$finishAudioUpdate(playersInPoint);
    }

    @Unique
    private void espetro$beginAudioUpdate() {
        if (espetro$audioUpdateDepth++ == 0) {
            espetro$audioOldState = espetro$getStateName();
            espetro$audioOldCaptor = getCaptorName();
        }
    }

    @Unique
    private void espetro$finishAudioUpdate(List<?> playersInPoint) {
        if (--espetro$audioUpdateDepth != 0 || espetro$audioOldState == null) {
            return;
        }
        String state = espetro$getStateName();
        boolean wasOwned = switch (espetro$audioOldState) {
            case "CAPTURED", "CAPTURING_DOWN", "CONTESTED" -> true;
            default -> false;
        };
        if (wasOwned && "NEUTRAL".equals(state)) {
            EspetroAPI.onCapturePointNeutralized(espetro$audioOldCaptor, espetro$findAttackingTeam(playersInPoint));
        } else if (!wasOwned && "CAPTURED".equals(state)) {
            EspetroAPI.onCapturePointCaptured(getCaptorName());
        }
    }

    @Unique
    private String espetro$findAttackingTeam(List<?> playersInPoint) {
        String owner = AudioCuePolicy.normalizeTeam(espetro$audioOldCaptor);
        if (playersInPoint == null) return null;
        for (Object candidate : playersInPoint) {
            if (!(candidate instanceof ServerPlayer player)) continue;
            Team team = player.getTeam();
            if (team == null) continue;
            String teamName = team.getName();
            if (!java.util.Objects.equals(owner, AudioCuePolicy.normalizeTeam(teamName))) {
                return teamName;
            }
        }
        return null;
    }

    @Unique
    private String espetro$getStateName() {
        try {
            Method method = espetro$getStateMethod;
            if (method == null) {
                method = getClass().getMethod("getState");
                espetro$getStateMethod = method;
            }
            Object state = method.invoke(this);
            return state instanceof Enum<?> enumState
                ? enumState.name() : String.valueOf(state);
        } catch (ReflectiveOperationException | RuntimeException exception) {
            if (!espetro$stateLookupWarningLogged) {
                espetro$stateLookupWarningLogged = true;
                Espetro.LOGGER.warn("[据点语音] 无法读取 ESPoints 据点状态，中立化语音已跳过", exception);
            }
            return null;
        }
    }
}
