package org.espetro.mixin.hcrpoints;

import org.espetro.api.EspetroAPI;
import org.espetro.audio.AudioCuePolicy;
import org.espetro.team.TroopCountManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Optional ESPoints hook for handing the objective result back to Espetro. */
@Pseudo
@Mixin(targets = {
    "com.example.hcrpoints.capturepoint.CapturePointManager",
    "com.example.espoints.capturepoint.CapturePointManager"
}, remap = false)
public abstract class CapturePointAudioMixin {

    /**
     * ESPoints shows its own win/lose popup but does not enter Espetro's round
     * end phase. Hand the result back after ESPoints has finished its cleanup
     * so the authoritative result packet opens the full settlement screen.
     */
    @Inject(
        method = "endOperationModeWithResult(Ljava/lang/String;Ljava/lang/String;)V",
        at = @At("RETURN"), require = 0, remap = false
    )
    private void espetro$onOperationEnded(String winnerTeam, String loserTeam,
                                           CallbackInfo ci) {
        String winner = AudioCuePolicy.normalizeTeam(winnerTeam);
        if (winner != null) {
            // Capturing every point is an attacker objective victory. Persist
            // the defender ticket loss before endRound snapshots the scores.
            if ("ATTACK".equals(winner)) {
                TroopCountManager.getInstance().setDefendTroops(0,
                    org.espetro.network.RoundReport.Reason.of(org.espetro.network.RoundReport.Cause.CAPTURE, "全部据点失守"));
            }
            EspetroAPI.endRound(winner);
        }
    }
}
