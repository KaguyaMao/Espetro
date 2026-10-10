/*
 * Decompiled with CFR 0.152.
 */
package frontline.combat.fcp.firecontrol;

import frontline.combat.fcp.firecontrol.FireControlSolution;
import frontline.combat.fcp.firecontrol.FireControlStatus;

public record FireControlComputation(FireControlStatus status, FireControlSolution solution) {
    public static FireControlComputation failure(FireControlStatus status) {
        return new FireControlComputation(status, null);
    }

    public static FireControlComputation success(FireControlSolution solution) {
        return new FireControlComputation(FireControlStatus.ALIGNING, solution);
    }

    public boolean isSuccess() {
        return this.solution != null;
    }
}

