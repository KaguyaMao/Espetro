/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.element.Canvas;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class CanvasFilterSupport {
    private static final Pattern FILTER_PATTERN = Pattern.compile("([a-zA-Z-]+)\\(([^)]*)\\)");

    private CanvasFilterSupport() {
    }

    static boolean hasFilter(String filter) {
        return filter != null && !filter.isBlank() && !"none".equalsIgnoreCase(filter.trim());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    static void renderWithFilter(Canvas canvas, String filter, Graphics2D target, Consumer<Graphics2D> drawer) {
        if (!CanvasFilterSupport.hasFilter(filter)) {
            drawer.accept(target);
            return;
        }
        BufferedImage layer = new BufferedImage(canvas.getWidth(), canvas.getHeight(), 2);
        Graphics2D g = layer.createGraphics();
        try {
            Canvas.applyGraphicsDefaults(g);
            drawer.accept(g);
        }
        finally {
            g.dispose();
        }
        BufferedImage filtered = CanvasFilterSupport.apply(filter, layer);
        target.drawImage((Image)filtered, 0, 0, null);
    }

    static BufferedImage apply(String filter, BufferedImage source) {
        if (!CanvasFilterSupport.hasFilter(filter) || source == null) {
            return source;
        }
        BufferedImage current = CanvasFilterSupport.copy(source);
        for (FilterOp op : CanvasFilterSupport.parse(filter)) {
            current = CanvasFilterSupport.applySingle(current, op);
        }
        return current;
    }

    private static List<FilterOp> parse(String filter) {
        ArrayList<FilterOp> ops = new ArrayList<FilterOp>();
        Matcher matcher = FILTER_PATTERN.matcher(filter == null ? "" : filter);
        while (matcher.find()) {
            ops.add(new FilterOp(matcher.group(1).trim().toLowerCase(Locale.ROOT), matcher.group(2).trim()));
        }
        return ops;
    }

    private static BufferedImage applySingle(BufferedImage source, FilterOp op) {
        return switch (op.name) {
            case "blur" -> CanvasFilterSupport.blur(source, (int)Math.round(CanvasFilterSupport.parseLength(op.value)));
            case "brightness" -> CanvasFilterSupport.colorMatrix(source, CanvasFilterSupport.parseFactor(op.value, 1.0), 1.0, 0.0, false, false, false);
            case "contrast" -> CanvasFilterSupport.contrast(source, CanvasFilterSupport.parseFactor(op.value, 1.0));
            case "drop-shadow" -> CanvasFilterSupport.dropShadow(source, CanvasFilterSupport.parseDropShadow(op.value));
            case "grayscale" -> CanvasFilterSupport.grayscale(source, CanvasFilterSupport.parseUnit(op.value, 1.0));
            case "invert" -> CanvasFilterSupport.invert(source, CanvasFilterSupport.parseUnit(op.value, 1.0));
            case "opacity" -> CanvasFilterSupport.opacity(source, CanvasFilterSupport.parseUnit(op.value, 1.0));
            case "sepia" -> CanvasFilterSupport.sepia(source, CanvasFilterSupport.parseUnit(op.value, 1.0));
            case "saturate" -> CanvasFilterSupport.saturate(source, CanvasFilterSupport.parseFactor(op.value, 1.0));
            case "hue-rotate" -> CanvasFilterSupport.hueRotate(source, Math.toRadians(CanvasFilterSupport.parseAngle(op.value)));
            default -> source;
        };
    }

    private static BufferedImage copy(BufferedImage source) {
        BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        Graphics2D g = copy.createGraphics();
        try {
            g.drawImage((Image)source, 0, 0, null);
        }
        finally {
            g.dispose();
        }
        return copy;
    }

    private static BufferedImage blur(BufferedImage source, int radius) {
        if (radius <= 0) {
            return source;
        }
        BufferedImage current = source;
        for (int pass = 0; pass < Math.max(1, radius / 2); ++pass) {
            current = CanvasFilterSupport.boxBlur(current, Math.max(1, radius));
        }
        return current;
    }

    private static BufferedImage boxBlur(BufferedImage source, int radius) {
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        int[] pixels = source.getRGB(0, 0, source.getWidth(), source.getHeight(), null, 0, source.getWidth());
        int[] result = new int[pixels.length];
        int width = source.getWidth();
        int height = source.getHeight();
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int a = 0;
                int r = 0;
                int g = 0;
                int b = 0;
                int count = 0;
                for (int oy = -radius; oy <= radius; ++oy) {
                    int py = y + oy;
                    if (py < 0 || py >= height) continue;
                    for (int ox = -radius; ox <= radius; ++ox) {
                        int px = x + ox;
                        if (px < 0 || px >= width) continue;
                        int argb = pixels[py * width + px];
                        a += argb >>> 24 & 0xFF;
                        r += argb >>> 16 & 0xFF;
                        g += argb >>> 8 & 0xFF;
                        b += argb & 0xFF;
                        ++count;
                    }
                }
                result[y * width + x] = a / count << 24 | r / count << 16 | g / count << 8 | b / count;
            }
        }
        output.setRGB(0, 0, width, height, result, 0, width);
        return output;
    }

    private static BufferedImage colorMatrix(BufferedImage source, double brightness, double saturation, double hueRotate, boolean grayscale, boolean sepia, boolean invert) {
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        for (int y = 0; y < source.getHeight(); ++y) {
            for (int x = 0; x < source.getWidth(); ++x) {
                int argb = source.getRGB(x, y);
                int a = argb >>> 24 & 0xFF;
                double r = (double)(argb >>> 16 & 0xFF) / 255.0;
                double g = (double)(argb >>> 8 & 0xFF) / 255.0;
                double b = (double)(argb & 0xFF) / 255.0;
                if (invert) {
                    r = 1.0 - r;
                    g = 1.0 - g;
                    b = 1.0 - b;
                }
                float[] hsb = Color.RGBtoHSB((int)Math.round(r * 255.0), (int)Math.round(g * 255.0), (int)Math.round(b * 255.0), null);
                hsb[1] = (float)CanvasFilterSupport.clamp((double)hsb[1] * saturation, 0.0, 1.0);
                hsb[0] = (float)(((double)hsb[0] + hueRotate / (Math.PI * 2)) % 1.0);
                if (hsb[0] < 0.0f) {
                    hsb[0] = hsb[0] + 1.0f;
                }
                int rgb = Color.HSBtoRGB(hsb[0], hsb[1], (float)CanvasFilterSupport.clamp((double)hsb[2] * brightness, 0.0, 1.0));
                r = (double)(rgb >>> 16 & 0xFF) / 255.0;
                g = (double)(rgb >>> 8 & 0xFF) / 255.0;
                b = (double)(rgb & 0xFF) / 255.0;
                if (grayscale) {
                    double gray;
                    g = b = (gray = r * 0.2126 + g * 0.7152 + b * 0.0722);
                    r = b;
                }
                if (sepia) {
                    double nr = CanvasFilterSupport.clamp(r * 0.393 + g * 0.769 + b * 0.189, 0.0, 1.0);
                    double ng = CanvasFilterSupport.clamp(r * 0.349 + g * 0.686 + b * 0.168, 0.0, 1.0);
                    double nb = CanvasFilterSupport.clamp(r * 0.272 + g * 0.534 + b * 0.131, 0.0, 1.0);
                    r = nr;
                    g = ng;
                    b = nb;
                }
                output.setRGB(x, y, a << 24 | (int)Math.round(r * 255.0) << 16 | (int)Math.round(g * 255.0) << 8 | (int)Math.round(b * 255.0));
            }
        }
        return output;
    }

    private static BufferedImage contrast(BufferedImage source, double factor) {
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        for (int y = 0; y < source.getHeight(); ++y) {
            for (int x = 0; x < source.getWidth(); ++x) {
                int argb = source.getRGB(x, y);
                int a = argb >>> 24 & 0xFF;
                int r = CanvasFilterSupport.adjustContrast(argb >>> 16 & 0xFF, factor);
                int g = CanvasFilterSupport.adjustContrast(argb >>> 8 & 0xFF, factor);
                int b = CanvasFilterSupport.adjustContrast(argb & 0xFF, factor);
                output.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
            }
        }
        return output;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static BufferedImage dropShadow(BufferedImage source, DropShadowSpec spec) {
        if (spec == null) {
            return source;
        }
        BufferedImage shadow = CanvasFilterSupport.tintAlpha(source, spec.colorArgb);
        if (spec.blur > 0.0) {
            shadow = CanvasFilterSupport.blur(shadow, (int)Math.round(spec.blur));
        }
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        Graphics2D g = output.createGraphics();
        try {
            Canvas.applyGraphicsDefaults(g);
            g.drawImage(shadow, null, (int)Math.round(spec.offsetX), (int)Math.round(spec.offsetY));
            g.drawImage(source, new AffineTransform(), null);
        }
        finally {
            g.dispose();
        }
        return output;
    }

    private static BufferedImage grayscale(BufferedImage source, double amount) {
        BufferedImage gray = CanvasFilterSupport.colorMatrix(source, 1.0, 1.0, 0.0, true, false, false);
        return CanvasFilterSupport.blend(source, gray, amount);
    }

    private static BufferedImage invert(BufferedImage source, double amount) {
        BufferedImage inverted = CanvasFilterSupport.colorMatrix(source, 1.0, 1.0, 0.0, false, false, true);
        return CanvasFilterSupport.blend(source, inverted, amount);
    }

    private static BufferedImage opacity(BufferedImage source, double amount) {
        BufferedImage output = CanvasFilterSupport.copy(source);
        for (int y = 0; y < output.getHeight(); ++y) {
            for (int x = 0; x < output.getWidth(); ++x) {
                int argb = output.getRGB(x, y);
                int a = (int)Math.round((double)(argb >>> 24 & 0xFF) * CanvasFilterSupport.clamp(amount, 0.0, 1.0));
                output.setRGB(x, y, a << 24 | argb & 0xFFFFFF);
            }
        }
        return output;
    }

    private static BufferedImage sepia(BufferedImage source, double amount) {
        BufferedImage sepia = CanvasFilterSupport.colorMatrix(source, 1.0, 1.0, 0.0, false, true, false);
        return CanvasFilterSupport.blend(source, sepia, amount);
    }

    private static BufferedImage saturate(BufferedImage source, double factor) {
        return CanvasFilterSupport.colorMatrix(source, 1.0, factor, 0.0, false, false, false);
    }

    private static BufferedImage hueRotate(BufferedImage source, double radians) {
        return CanvasFilterSupport.colorMatrix(source, 1.0, 1.0, radians, false, false, false);
    }

    private static BufferedImage blend(BufferedImage original, BufferedImage modified, double amount) {
        amount = CanvasFilterSupport.clamp(amount, 0.0, 1.0);
        BufferedImage output = new BufferedImage(original.getWidth(), original.getHeight(), 2);
        for (int y = 0; y < original.getHeight(); ++y) {
            for (int x = 0; x < original.getWidth(); ++x) {
                int src = original.getRGB(x, y);
                int dst = modified.getRGB(x, y);
                int a = src >>> 24 & 0xFF;
                int r = (int)Math.round((double)(src >>> 16 & 0xFF) * (1.0 - amount) + (double)(dst >>> 16 & 0xFF) * amount);
                int g = (int)Math.round((double)(src >>> 8 & 0xFF) * (1.0 - amount) + (double)(dst >>> 8 & 0xFF) * amount);
                int b = (int)Math.round((double)(src & 0xFF) * (1.0 - amount) + (double)(dst & 0xFF) * amount);
                output.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
            }
        }
        return output;
    }

    private static BufferedImage tintAlpha(BufferedImage source, int argb) {
        BufferedImage output = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        int tintA = argb >>> 24 & 0xFF;
        int tintR = argb >>> 16 & 0xFF;
        int tintG = argb >>> 8 & 0xFF;
        int tintB = argb & 0xFF;
        for (int y = 0; y < source.getHeight(); ++y) {
            for (int x = 0; x < source.getWidth(); ++x) {
                int src = source.getRGB(x, y);
                int srcA = src >>> 24 & 0xFF;
                if (srcA <= 0) {
                    output.setRGB(x, y, 0);
                    continue;
                }
                int alpha = (int)Math.round((double)srcA * ((double)tintA / 255.0));
                output.setRGB(x, y, alpha << 24 | tintR << 16 | tintG << 8 | tintB);
            }
        }
        return output;
    }

    private static int adjustContrast(int value, double factor) {
        double normalized = (double)value / 255.0;
        double contrasted = (normalized - 0.5) * factor + 0.5;
        return (int)Math.round(CanvasFilterSupport.clamp(contrasted, 0.0, 1.0) * 255.0);
    }

    private static double parseFactor(String value, double fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        try {
            if (trimmed.endsWith("%")) {
                return Double.parseDouble(trimmed.substring(0, trimmed.length() - 1)) / 100.0;
            }
            return Double.parseDouble(trimmed);
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double parseUnit(String value, double fallback) {
        return CanvasFilterSupport.parseFactor(value, fallback);
    }

    private static double parseLength(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        String trimmed = value.trim().toLowerCase(Locale.ROOT).replace("px", "");
        try {
            return Double.parseDouble(trimmed);
        }
        catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    private static double parseAngle(String value) {
        if (value == null || value.isBlank()) {
            return 0.0;
        }
        String trimmed = value.trim().toLowerCase(Locale.ROOT);
        try {
            if (trimmed.endsWith("deg")) {
                return Double.parseDouble(trimmed.substring(0, trimmed.length() - 3));
            }
            if (trimmed.endsWith("rad")) {
                return Math.toDegrees(Double.parseDouble(trimmed.substring(0, trimmed.length() - 3)));
            }
            return Double.parseDouble(trimmed);
        }
        catch (NumberFormatException ignored) {
            return 0.0;
        }
    }

    private static DropShadowSpec parseDropShadow(String value) {
        if (value == null || value.isBlank()) {
            return new DropShadowSpec(0.0, 0.0, 0.0, Integer.MIN_VALUE);
        }
        String trimmed = value.trim();
        Matcher matcher = Pattern.compile("(rgba?\\([^)]*\\)|#[0-9a-fA-F]{3,8}|[a-zA-Z]+)").matcher(trimmed);
        String color = "rgba(0,0,0,0.5)";
        if (matcher.find()) {
            color = matcher.group(1);
            trimmed = (trimmed.substring(0, matcher.start()) + " " + trimmed.substring(matcher.end())).trim();
        }
        String[] parts = trimmed.isBlank() ? new String[]{} : trimmed.split("\\s+");
        double offsetX = parts.length > 0 ? CanvasFilterSupport.parseLength(parts[0]) : 0.0;
        double offsetY = parts.length > 1 ? CanvasFilterSupport.parseLength(parts[1]) : 0.0;
        double blur = parts.length > 2 ? Math.max(0.0, CanvasFilterSupport.parseLength(parts[2])) : 0.0;
        return new DropShadowSpec(offsetX, offsetY, blur, com.sighs.apricityui.parser.Color.parse(color));
    }

    private static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }

    private record FilterOp(String name, String value) {
    }

    private record DropShadowSpec(double offsetX, double offsetY, double blur, int colorArgb) {
    }
}

