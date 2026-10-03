/*
 * Decompiled with CFR 0.152.
 */
package frontline.combat.fcp.firecontrol;

import java.util.Locale;

public enum FireControlStatus {
    INACTIVE,
    MOVING,
    ALIGNING,
    READY,
    OUT_OF_RANGE,
    PITCH_LIMIT,
    YAW_LIMIT,
    INVALID_INPUT,
    INVALID_WEAPON,
    WRECKED;


    public static FireControlStatus fromId(int id) {
        FireControlStatus[] values = FireControlStatus.values();
        return id >= 0 && id < values.length ? values[id] : INACTIVE;
    }

    public String translationKey() {
        return "status.fcp.fire_control." + this.name().toLowerCase(Locale.ROOT);
    }
}

