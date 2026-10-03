/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class Utils {
    private static final long UNDETERMINED = 0xFFFFFFFFL;
    static final Logger BoxTypesLOGGER = Logger.getLogger("jaad.mp4.boxes.BoxTypes");

    public static String getLanguageCode(long l) {
        char[] c = new char[]{(char)((l >> 10 & 0x1FL) + 96L), (char)((l >> 5 & 0x1FL) + 96L), (char)((l & 0x1FL) + 96L)};
        return new String(c);
    }

    public static long detectUndetermined(long l) {
        long x = l == 0xFFFFFFFFL ? -1L : l;
        return x;
    }

    static {
        if (!LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils.isDebug) {
            BoxTypesLOGGER.setLevel(Level.WARNING);
        }
    }
}

