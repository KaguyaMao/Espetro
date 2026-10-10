/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac;

public enum Speaker {
    FL("Front Left"),
    FR("Front Right"),
    FC("Front Center"),
    LFE("Low Frequency"),
    BL("Back Left"),
    BR("Back Right"),
    FLC("Front Left of Center"),
    FRC("Front Right of Center"),
    BC("Back Center"),
    SL("Side Left"),
    SR("Side Right"),
    TC("Top Center"),
    TFL("Front Left Height"),
    TFC("Front Center Height"),
    TFR("Front Right Height"),
    TBL("Rear Left Height"),
    TBC("Rear Center Height"),
    TBR("Rear Right Height");

    final String name;

    private Speaker(String name) {
        this.name = name;
    }
}

