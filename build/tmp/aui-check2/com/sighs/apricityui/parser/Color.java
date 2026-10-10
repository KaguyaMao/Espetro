/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.parser;

import java.util.Locale;
import java.util.Map;

public class Color {
    private int value;
    public static final Color BLACK = new Color("#000");
    private static final Map<String, Integer> NAMED_COLORS = Map.ofEntries(Map.entry("black", -16777216), Map.entry("white", -1), Map.entry("red", -65536), Map.entry("green", -16744448), Map.entry("blue", -16776961), Map.entry("yellow", -256), Map.entry("cyan", -16711681), Map.entry("magenta", -65281), Map.entry("gray", -8355712), Map.entry("grey", -8355712), Map.entry("lightgray", -2894893), Map.entry("lightgrey", -2894893), Map.entry("darkgray", -5658199), Map.entry("darkgrey", -5658199), Map.entry("orange", -23296), Map.entry("purple", -8388480), Map.entry("pink", -16181), Map.entry("brown", -5952982), Map.entry("navy", -16777088), Map.entry("teal", -16744320), Map.entry("lime", -16711936), Map.entry("silver", -4144960), Map.entry("maroon", -8388608), Map.entry("olive", -8355840), Map.entry("aqua", -16711681), Map.entry("fuchsia", -65281));

    public Color(String string) {
        this.set(string);
    }

    public Color(Number value) {
        this.set(value.intValue());
    }

    public int getValue() {
        return this.value;
    }

    public void set(String string) {
        this.value = Color.parse(string);
    }

    public void set(int value) {
        this.value = value;
    }

    public static int parse(String string) {
        String input;
        if (string == null) {
            return 0;
        }
        if (string.equals("unset")) {
            string = "#000";
        }
        if ((input = string.trim().toLowerCase(Locale.ROOT)).equals("transparent")) {
            return 0;
        }
        if (input.startsWith("#")) {
            return Color.parseHex(input);
        }
        if (input.startsWith("rgb")) {
            return Color.parseRgba(input);
        }
        if (input.startsWith("hsl(")) {
            return Color.parseHsl(input);
        }
        return NAMED_COLORS.getOrDefault(input, 0);
    }

    public static boolean isColorKeyword(String value) {
        if (value == null || value.isBlank()) {
            return false;
        }
        String normalized = value.trim().toLowerCase(Locale.ROOT);
        return "transparent".equals(normalized) || NAMED_COLORS.containsKey(normalized);
    }

    public static double mixColors(double startVal, double endVal, double process) {
        int s = (int)startVal;
        int e = (int)endVal;
        int a1 = s >> 24 & 0xFF;
        int r1 = s >> 16 & 0xFF;
        int g1 = s >> 8 & 0xFF;
        int b1 = s & 0xFF;
        int a2 = e >> 24 & 0xFF;
        int r2 = e >> 16 & 0xFF;
        int g2 = e >> 8 & 0xFF;
        int b2 = e & 0xFF;
        int a = (int)((double)a1 + (double)(a2 - a1) * process);
        int r = (int)((double)r1 + (double)(r2 - r1) * process);
        int g = (int)((double)g1 + (double)(g2 - g1) * process);
        int b = (int)((double)b1 + (double)(b2 - b1) * process);
        return a << 24 | r << 16 | g << 8 | b;
    }

    public String toRgbaString() {
        return String.format("rgba(%d, %d, %d, %.3f)", this.getR(), this.getG(), this.getB(), (double)this.getA() / 255.0);
    }

    public String toHexString() {
        if (this.getA() == 255) {
            return String.format("#%06X", this.value & 0xFFFFFF);
        }
        return String.format("#%02X%02X%02X%02X", this.getR(), this.getG(), this.getB(), this.getA());
    }

    public int getA() {
        return this.value >>> 24 & 0xFF;
    }

    public int getR() {
        return this.value >>> 16 & 0xFF;
    }

    public int getG() {
        return this.value >>> 8 & 0xFF;
    }

    public int getB() {
        return this.value & 0xFF;
    }

    private static int parseHex(String hex) {
        String cleanHex;
        if (hex == null) {
            hex = "#00000000";
        }
        String string = cleanHex = hex.startsWith("#") ? hex.substring(1) : hex;
        if (cleanHex.length() == 3 || cleanHex.length() == 4) {
            StringBuilder expanded = new StringBuilder(cleanHex.length() * 2);
            for (int i = 0; i < cleanHex.length(); ++i) {
                expanded.append(cleanHex.charAt(i)).append(cleanHex.charAt(i));
            }
            cleanHex = expanded.toString();
        }
        if (cleanHex.length() != 6 && cleanHex.length() != 8) {
            return 0;
        }
        try {
            long rgba = Long.parseLong(cleanHex, 16);
            if (cleanHex.length() == 6) {
                return (int)(0xFF000000L | rgba);
            }
            return (int)((rgba & 0xFFL) << 24 | rgba >>> 8);
        }
        catch (NumberFormatException ignored) {
            return 0;
        }
    }

    private static int parseRgba(String input) {
        String[] parts;
        if (input == null) {
            return 0;
        }
        int start = input.indexOf(40);
        int end = input.lastIndexOf(41);
        if (start < 0 || end < 0 || end <= start) {
            return 0;
        }
        String inside = input.substring(start + 1, end).trim();
        if ((inside = inside.replace(",", " ").replaceAll("\\s+", " ")).contains("/")) {
            String[] split = inside.split("/");
            if (split.length != 2) {
                return 0;
            }
            String left = split[0].trim();
            String right = split[1].trim();
            parts = (left + " " + right).trim().split("\\s+");
        } else {
            parts = inside.split("\\s+");
        }
        if (parts.length < 3) {
            return 0;
        }
        try {
            int r = Color.parseColorComponent(parts[0]);
            int g = Color.parseColorComponent(parts[1]);
            int b = Color.parseColorComponent(parts[2]);
            int a = 255;
            if (parts.length >= 4) {
                a = Color.parseAlphaComponent(parts[3]);
            }
            return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static int parseHsl(String input) {
        String[] parts;
        if (input == null) {
            return 0;
        }
        int start = input.indexOf(40);
        int end = input.lastIndexOf(41);
        if (start < 0 || end < 0 || end <= start) {
            return 0;
        }
        String inside = input.substring(start + 1, end).trim();
        if ((inside = inside.replace(",", " ").replaceAll("\\s+", " ")).contains("/")) {
            String[] split = inside.split("/");
            if (split.length != 2) {
                return 0;
            }
            String left = split[0].trim();
            String right = split[1].trim();
            parts = (left + " " + right).trim().split("\\s+");
        } else {
            parts = inside.split("\\s+");
        }
        if (parts.length < 3) {
            return 0;
        }
        try {
            double rD;
            double gD;
            double bD;
            double h = Color.parseHue(parts[0]);
            double s = Color.parsePercentLike(parts[1]);
            double l = Color.parsePercentLike(parts[2]);
            double alpha = 1.0;
            if (parts.length >= 4) {
                alpha = Color.parseAlphaDouble(parts[3]);
            }
            double hd = (h % 360.0 + 360.0) % 360.0 / 360.0;
            if (s == 0.0) {
                gD = bD = l;
                rD = bD;
            } else {
                double q = l < 0.5 ? l * (1.0 + s) : l + s - l * s;
                double p = 2.0 * l - q;
                rD = Color.hueToRgb(p, q, hd + 0.3333333333333333);
                gD = Color.hueToRgb(p, q, hd);
                bD = Color.hueToRgb(p, q, hd - 0.3333333333333333);
            }
            int r = Color.clampInt((int)Math.round(rD * 255.0), 0, 255);
            int g = Color.clampInt((int)Math.round(gD * 255.0), 0, 255);
            int b = Color.clampInt((int)Math.round(bD * 255.0), 0, 255);
            int a = Color.clampInt((int)Math.round(alpha * 255.0), 0, 255);
            return (a & 0xFF) << 24 | (r & 0xFF) << 16 | (g & 0xFF) << 8 | b & 0xFF;
        }
        catch (NumberFormatException ex) {
            return 0;
        }
    }

    private static int parseColorComponent(String token) {
        if ((token = token.trim()).endsWith("%")) {
            double perc = Double.parseDouble(token.substring(0, token.length() - 1).trim());
            return Color.clampInt((int)Math.round(perc / 100.0 * 255.0), 0, 255);
        }
        double v = Double.parseDouble(token);
        return Color.clampInt((int)Math.round(v), 0, 255);
    }

    private static int parseAlphaComponent(String token) {
        double a = Color.parseAlphaDouble(token);
        return Color.clampInt((int)Math.round(a * 255.0), 0, 255);
    }

    private static double parseAlphaDouble(String token) {
        if ((token = token.trim()).endsWith("%")) {
            double perc = Double.parseDouble(token.substring(0, token.length() - 1).trim());
            return Color.clampDouble(perc / 100.0, 0.0, 1.0);
        }
        double v = Double.parseDouble(token);
        if (v > 1.0) {
            return Color.clampDouble(v / 255.0, 0.0, 1.0);
        }
        return Color.clampDouble(v, 0.0, 1.0);
    }

    private static double parsePercentLike(String token) {
        if ((token = token.trim()).endsWith("%")) {
            double perc = Double.parseDouble(token.substring(0, token.length() - 1).trim());
            return Color.clampDouble(perc / 100.0, 0.0, 1.0);
        }
        double v = Double.parseDouble(token);
        if (v > 1.0) {
            return Color.clampDouble(v / 100.0, 0.0, 1.0);
        }
        return Color.clampDouble(v, 0.0, 1.0);
    }

    private static double parseHue(String token) {
        if ((token = token.trim().toLowerCase()).endsWith("deg")) {
            return Double.parseDouble(token.substring(0, token.length() - 3).trim());
        }
        if (token.endsWith("rad")) {
            double rad = Double.parseDouble(token.substring(0, token.length() - 3).trim());
            return Math.toDegrees(rad);
        }
        if (token.endsWith("turn")) {
            double turns = Double.parseDouble(token.substring(0, token.length() - 4).trim());
            return turns * 360.0;
        }
        return Double.parseDouble(token);
    }

    private static double hueToRgb(double p, double q, double t) {
        if (t < 0.0) {
            t += 1.0;
        }
        if (t > 1.0) {
            t -= 1.0;
        }
        if (t < 0.16666666666666666) {
            return p + (q - p) * 6.0 * t;
        }
        if (t < 0.5) {
            return q;
        }
        if (t < 0.6666666666666666) {
            return p + (q - p) * (0.6666666666666666 - t) * 6.0;
        }
        return p;
    }

    private static int clampInt(int v, int lo, int hi) {
        if (v < lo) {
            return lo;
        }
        return Math.min(v, hi);
    }

    private static double clampDouble(double v, double lo, double hi) {
        if (v < lo) {
            return lo;
        }
        return Math.min(v, hi);
    }
}

