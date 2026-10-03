/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

public final class BitMath {
    public static int ilog2(int v) {
        int l = 0;
        while ((v >>= 1) != 0) {
            ++l;
        }
        return l;
    }

    public static int silog2(int v) {
        while (v != 0) {
            if (v > 0) {
                int l = 0;
                while (v != 0) {
                    ++l;
                    v >>= 1;
                }
                return l + 1;
            }
            if (v == -1) {
                return 2;
            }
            ++v;
            v = -v;
        }
        return 0;
    }

    public static int silog2Wide(long v) {
        while (v != 0L) {
            if (v > 0L) {
                int l = 0;
                while (v != 0L) {
                    ++l;
                    v >>= 1;
                }
                return l + 1;
            }
            if (v == -1L) {
                return 2;
            }
            ++v;
            v = -v;
        }
        return 0;
    }
}

