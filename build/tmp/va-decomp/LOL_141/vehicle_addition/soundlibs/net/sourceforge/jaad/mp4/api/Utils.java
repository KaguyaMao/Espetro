/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api;

import java.util.Date;

class Utils {
    private static final long DATE_OFFSET = 2082850791998L;

    Utils() {
    }

    static Date getDate(long time) {
        return new Date(time * 1000L - 2082850791998L);
    }
}

