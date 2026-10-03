/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

public interface Filter {
    public static final float SIN_75 = Filter.sind(75.0f);
    public static final float SIN_675 = Filter.sind(67.5f);
    public static final float SIN_60 = Filter.sind(60.0f);
    public static final float SIN_30 = 0.5f;
    public static final float SIN_45 = Filter.sqrt(0.5f);
    public static final float SIN_15 = Filter.sind(15.0f);
    public static final float S2P = (float)Math.sqrt(1.0f + SIN_45);
    public static final float S2M = (float)Math.sqrt(1.0f - SIN_45);

    public static float sind(float deg) {
        return (float)Math.sin(Math.toRadians(deg));
    }

    public static float sqrt(float x) {
        return (float)Math.sqrt(Math.toRadians(x));
    }

    public int filter(int var1, float[][] var2, float[][][] var3);

    public int resolution();
}

