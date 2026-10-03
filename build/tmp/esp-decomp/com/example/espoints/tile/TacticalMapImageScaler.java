/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

final class TacticalMapImageScaler {
    private static final double SHARPEN_AMOUNT = 0.35;

    private TacticalMapImageScaler() {
    }

    static BufferedImage scale(BufferedImage source, int width, int height) {
        return TacticalMapImageScaler.sharpen(TacticalMapImageScaler.resizeBicubic(source, width, height));
    }

    static BufferedImage resizeBicubic(BufferedImage source, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, 2);
        Graphics2D graphics = result.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        graphics.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        graphics.drawImage(source, 0, 0, width, height, null);
        graphics.dispose();
        return result;
    }

    static BufferedImage sharpen(BufferedImage source) {
        int width = source.getWidth();
        int height = source.getHeight();
        if (width < 3 || height < 3) {
            return source;
        }
        int[] input = source.getRGB(0, 0, width, height, null, 0, width);
        int[] output = (int[])input.clone();
        for (int y = 1; y < height - 1; ++y) {
            for (int x = 1; x < width - 1; ++x) {
                int index = y * width + x;
                int center = input[index];
                int north = input[index - width];
                int south = input[index + width];
                int west = input[index - 1];
                int east = input[index + 1];
                int alpha = center >>> 24;
                int red = TacticalMapImageScaler.sharpenChannel(center, north, south, west, east, 16);
                int green = TacticalMapImageScaler.sharpenChannel(center, north, south, west, east, 8);
                int blue = TacticalMapImageScaler.sharpenChannel(center, north, south, west, east, 0);
                output[index] = alpha << 24 | red << 16 | green << 8 | blue;
            }
        }
        BufferedImage sharpened = new BufferedImage(width, height, 2);
        sharpened.setRGB(0, 0, width, height, output, 0, width);
        source.flush();
        return sharpened;
    }

    private static int sharpenChannel(int center, int north, int south, int west, int east, int shift) {
        int value = center >>> shift & 0xFF;
        int blurred = (value * 4 + (north >>> shift & 0xFF) + (south >>> shift & 0xFF) + (west >>> shift & 0xFF) + (east >>> shift & 0xFF)) / 8;
        return Math.max(0, Math.min(255, (int)Math.round((double)value + 0.35 * (double)(value - blurred))));
    }
}

