/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.List;

enum FrameClass {
    FIXFIX,
    FIXVAR,
    VARFIX,
    VARVAR;

    public static List<FrameClass> VALUES;

    static FrameClass read(BitStream is) {
        int bits = is.readBits(2);
        return VALUES.get(bits);
    }

    static {
        VALUES = Utils.listOf(FrameClass.values());
    }
}

