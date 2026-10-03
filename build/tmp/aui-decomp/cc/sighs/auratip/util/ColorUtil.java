/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package cc.sighs.auratip.util;

import net.minecraft.util.Mth;

public final class ColorUtil {
    private ColorUtil() {
    }

    public static int lerpColor(int c1, int c2, float t) {
        t = Mth.m_14036_((float)t, (float)0.0f, (float)1.0f);
        int a1 = c1 >>> 24 & 0xFF;
        int r1 = c1 >>> 16 & 0xFF;
        int g1 = c1 >>> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = c2 >>> 24 & 0xFF;
        int r2 = c2 >>> 16 & 0xFF;
        int g2 = c2 >>> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        int a = (int)((float)a1 + (float)(a2 - a1) * t);
        int r = (int)((float)r1 + (float)(r2 - r1) * t);
        int g = (int)((float)g1 + (float)(g2 - g1) * t);
        int b = (int)((float)b1 + (float)(b2 - b1) * t);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public static int parseRgb(String hex) {
        if (hex == null || hex.isBlank()) {
            return 0xFFFFFF;
        }
        String v = hex.charAt(0) == '#' ? hex.substring(1) : hex;
        try {
            long value = Long.parseLong(v, 16);
            return (int)(value & 0xFFFFFFL);
        }
        catch (NumberFormatException e) {
            return 0xFFFFFF;
        }
    }

    public static int parseArgb(String hex) {
        if (hex == null || hex.isBlank()) {
            return -15722974;
        }
        String v = hex.charAt(0) == '#' ? hex.substring(1) : hex;
        try {
            long value = Long.parseLong(v, 16);
            if (v.length() <= 6) {
                return (int)(0xFF000000L | value & 0xFFFFFFL);
            }
            return (int)(value & 0xFFFFFFFFL);
        }
        catch (NumberFormatException e) {
            return -15722974;
        }
    }

    public static int withAlpha(int rgb, int alpha) {
        alpha = Mth.m_14045_((int)alpha, (int)0, (int)255);
        return alpha << 24 | rgb & 0xFFFFFF;
    }

    public static int multiplyAlpha(int argb, float factor) {
        int a = (int)((float)(argb >>> 24 & 0xFF) * factor);
        a = Mth.m_14045_((int)a, (int)0, (int)255);
        return a << 24 | argb & 0xFFFFFF;
    }
}

