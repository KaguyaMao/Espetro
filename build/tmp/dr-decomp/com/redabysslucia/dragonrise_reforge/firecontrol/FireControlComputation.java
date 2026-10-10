/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.firecontrol;

import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlSolution;
import com.redabysslucia.dragonrise_reforge.firecontrol.FireControlStatus;

public record FireControlComputation(FireControlStatus status, FireControlSolution solution, double requestedPitch) {
    public static FireControlComputation failure(FireControlStatus status) {
        return new FireControlComputation(status, null, Double.NaN);
    }

    public static FireControlComputation failure(FireControlStatus status, double requestedPitch) {
        return new FireControlComputation(status, null, requestedPitch);
    }

    public static FireControlComputation success(FireControlSolution solution) {
        return new FireControlComputation(FireControlStatus.ALIGNING, solution, solution.pitch());
    }

    public boolean isSuccess() {
        return this.solution != null;
    }

    public boolean hasRequestedPitch() {
        return !Double.isNaN(this.requestedPitch);
    }
}

