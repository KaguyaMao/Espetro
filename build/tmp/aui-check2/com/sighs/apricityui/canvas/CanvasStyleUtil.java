/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasLinearGradient;
import com.sighs.apricityui.canvas.CanvasPattern;
import com.sighs.apricityui.canvas.CanvasRadialGradient;
import java.awt.Color;
import java.awt.Font;
import java.util.List;
import java.util.Locale;

final class CanvasStyleUtil {
    private CanvasStyleUtil() {
    }

    static Color parseAwtColor(String value) {
        int rgba = com.sighs.apricityui.parser.Color.parse(value == null ? "#000000" : value);
        int a = rgba >>> 24 & 0xFF;
        int r = rgba >>> 16 & 0xFF;
        int g = rgba >>> 8 & 0xFF;
        int b = rgba & 0xFF;
        return new Color(r, g, b, a);
    }

    static Object normalizeStyle(Object style) {
        if (style instanceof CanvasLinearGradient || style instanceof CanvasRadialGradient || style instanceof CanvasPattern) {
            return style;
        }
        return style == null ? "#000000" : style.toString();
    }

    static Font parseFont(String fontSpec) {
        String spec = fontSpec == null || fontSpec.isBlank() ? "16px SansSerif" : fontSpec.trim();
        String normalized = spec.toLowerCase(Locale.ROOT);
        int style = 0;
        if (normalized.contains("bold")) {
            style |= 1;
        }
        if (normalized.contains("italic") || normalized.contains("oblique")) {
            style |= 2;
        }
        int size = 16;
        String family = "SansSerif";
        String[] parts = spec.split("\\s+");
        for (int i = 0; i < parts.length; ++i) {
            String token = parts[i];
            if (!token.endsWith("px")) continue;
            try {
                size = Math.max(1, (int)Math.round(Double.parseDouble(token.substring(0, token.length() - 2))));
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
            if (i + 1 >= parts.length) break;
            family = CanvasStyleUtil.join(parts, i + 1);
            break;
        }
        return new Font(family.replace("\"", "").replace("'", ""), style, size);
    }

    static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    static String normalizeLineCap(String value) {
        String normalized;
        if (value == null || value.isBlank()) {
            return "butt";
        }
        return switch (normalized = value.trim().toLowerCase(Locale.ROOT)) {
            case "round", "square" -> normalized;
            default -> "butt";
        };
    }

    static String normalizeLineJoin(String value) {
        String normalized;
        if (value == null || value.isBlank()) {
            return "miter";
        }
        return switch (normalized = value.trim().toLowerCase(Locale.ROOT)) {
            case "round", "bevel" -> normalized;
            default -> "miter";
        };
    }

    static int resolveLineCap(String value) {
        return switch (CanvasStyleUtil.normalizeLineCap(value)) {
            case "round" -> 1;
            case "square" -> 2;
            default -> 0;
        };
    }

    static int resolveLineJoin(String value) {
        return switch (CanvasStyleUtil.normalizeLineJoin(value)) {
            case "round" -> 1;
            case "bevel" -> 2;
            default -> 0;
        };
    }

    static int clampChannel(int value) {
        if (value < 0) {
            return 0;
        }
        return Math.min(value, 255);
    }

    static float[] toFloatDashArray(double[] source) {
        float[] dashArray = new float[source.length];
        for (int i = 0; i < source.length; ++i) {
            dashArray[i] = (float)source[i];
        }
        return dashArray;
    }

    static double[] normalizeLineDash(Object segments) {
        double[] raw = CanvasStyleUtil.toDashArray(segments);
        if (raw.length == 0) {
            return raw;
        }
        boolean hasPositive = false;
        for (double value : raw) {
            if (!Double.isFinite(value) || value < 0.0) {
                return new double[0];
            }
            if (!(value > 0.0)) continue;
            hasPositive = true;
        }
        if (!hasPositive) {
            return new double[0];
        }
        if ((raw.length & 1) == 1) {
            double[] doubled = new double[raw.length * 2];
            System.arraycopy(raw, 0, doubled, 0, raw.length);
            System.arraycopy(raw, 0, doubled, raw.length, raw.length);
            return doubled;
        }
        return raw;
    }

    private static double[] toDashArray(Object segments) {
        if (segments == null) {
            return new double[0];
        }
        if (segments instanceof double[]) {
            double[] values = (double[])segments;
            return (double[])values.clone();
        }
        if (segments instanceof float[]) {
            float[] values = (float[])segments;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = values[i];
            }
            return result;
        }
        if (segments instanceof int[]) {
            int[] values = (int[])segments;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = values[i];
            }
            return result;
        }
        if (segments instanceof long[]) {
            long[] values = (long[])segments;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = values[i];
            }
            return result;
        }
        if (segments instanceof Object[]) {
            Object[] values = (Object[])segments;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                Object value = values[i];
                if (!(value instanceof Number)) {
                    return new double[0];
                }
                Number number = (Number)value;
                result[i] = number.doubleValue();
            }
            return result;
        }
        if (segments instanceof List) {
            List values = (List)segments;
            double[] result = new double[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                Object value = values.get(i);
                if (!(value instanceof Number)) {
                    return new double[0];
                }
                Number number = (Number)value;
                result[i] = number.doubleValue();
            }
            return result;
        }
        return new double[0];
    }

    private static String join(String[] parts, int from) {
        if (from >= parts.length) {
            return "SansSerif";
        }
        StringBuilder builder = new StringBuilder();
        for (int i = from; i < parts.length; ++i) {
            if (i > from) {
                builder.append(' ');
            }
            builder.append(parts[i]);
        }
        return builder.toString();
    }
}

