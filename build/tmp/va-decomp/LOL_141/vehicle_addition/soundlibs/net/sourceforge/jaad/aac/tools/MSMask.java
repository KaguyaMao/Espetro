/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;

public enum MSMask {
    TYPE_ALL_0(0),
    TYPE_USED(1),
    TYPE_ALL_1(2),
    TYPE_RESERVED(3);

    private int num;

    public static MSMask forInt(int i) {
        return switch (i) {
            case 0 -> TYPE_ALL_0;
            case 1 -> TYPE_USED;
            case 2 -> TYPE_ALL_1;
            case 3 -> TYPE_RESERVED;
            default -> throw new AACException("unknown MS mask type");
        };
    }

    private MSMask(int num) {
        this.num = num;
    }
}

