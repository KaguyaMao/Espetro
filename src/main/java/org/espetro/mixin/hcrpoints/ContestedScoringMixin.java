package org.espetro.mixin.hcrpoints;

import net.minecraft.server.level.ServerPlayer;
import org.espetro.stats.RoundScoring;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

/** Reuses the capture manager's filtered player list and final contested state. */
@Pseudo
@Mixin(targets = {"com.example.espoints.capturepoint.CapturePoint",
    "com.example.hcrpoints.capturepoint.CapturePoint"}, remap = false)
abstract class ContestedScoringMixin {
    @Inject(method = "updateStatus(Ljava/util/List;I)V", at = @At("RETURN"), require = 0, remap = false)
    private void espetro$awardContestedTime(List<?> players, int elapsedTicks, CallbackInfo callback) {
        if (players.isEmpty()) return;
        try {
            if (!(getClass().getMethod("getState").invoke(this) instanceof Enum<?> state)
                || (!"CONTESTED".equals(state.name()) && !"CAPTURING_CONTESTED".equals(state.name()))) return;
            for (var player : players) {
                if (player instanceof ServerPlayer serverPlayer) RoundScoring.contested(serverPlayer, elapsedTicks);
            }
        } catch (ReflectiveOperationException error) {
            org.espetro.Espetro.LOGGER.debug("Unable to resolve capture-point state", error);
        }
    }
}
