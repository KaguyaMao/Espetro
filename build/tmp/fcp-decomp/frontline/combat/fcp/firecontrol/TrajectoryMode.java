/*
 * Decompiled with CFR 0.152.
 */
package frontline.combat.fcp.firecontrol;

public enum TrajectoryMode {
    LOW,
    HIGH;


    public static TrajectoryMode fromId(int id) {
        return id == HIGH.ordinal() ? HIGH : LOW;
    }

    public static TrajectoryMode fromFiringParameters(boolean depressed) {
        return depressed ? LOW : HIGH;
    }

    public String translationKey() {
        return this == LOW ? "screen.fcp.fire_control.trajectory.low" : "screen.fcp.fire_control.trajectory.high";
    }
}

