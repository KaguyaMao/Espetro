/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.util;

public class RiceCodes {
    public int riceBits(int val, int parameter) {
        int uval = val < 0 ? (-(++val) << 1) + 1 : val << 1;
        int msbs = uval >> parameter;
        return 1 + parameter + msbs;
    }
}

