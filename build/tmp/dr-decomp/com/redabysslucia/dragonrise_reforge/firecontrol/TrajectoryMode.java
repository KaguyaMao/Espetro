/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.firecontrol;

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
        return this == LOW ? "screen.dragonrise_reforge.fire_control.trajectory.low" : "screen.dragonrise_reforge.fire_control.trajectory.high";
    }
}

