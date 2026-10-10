/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

public class TVolumeUtils {
    private static final double FACTOR1 = 20.0 / Math.log(10.0);
    private static final double FACTOR2 = 0.05;

    public static double lin2log(double dLinear) {
        return FACTOR1 * Math.log(dLinear);
    }

    public static double log2lin(double dLogarithmic) {
        return Math.pow(10.0, dLogarithmic * 0.05);
    }
}

