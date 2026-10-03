/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.util.MathUtil;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.RenderingHints;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

final class BlendComposite
implements Composite {
    private final Mode mode;
    private final float alpha;

    BlendComposite(Mode mode, float alpha) {
        this.mode = mode;
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new Context(this.mode, this.alpha);
    }

    static enum Mode {
        MULTIPLY,
        SCREEN,
        DARKEN,
        LIGHTEN;

    }

    private record Context(Mode mode, float alpha) implements CompositeContext
    {
        @Override
        public void dispose() {
        }

        @Override
        public void compose(Raster src, Raster dstIn, WritableRaster dstOut) {
            int width = Math.min(src.getWidth(), dstIn.getWidth());
            int height = Math.min(src.getHeight(), dstIn.getHeight());
            int[] srcPixel = new int[4];
            int[] dstPixel = new int[4];
            for (int y = 0; y < height; ++y) {
                for (int x = 0; x < width; ++x) {
                    src.getPixel(x, y, srcPixel);
                    dstIn.getPixel(x, y, dstPixel);
                    float srcA = (float)srcPixel[3] / 255.0f * this.alpha;
                    float dstA = (float)dstPixel[3] / 255.0f;
                    float outA = MathUtil.clamp01(srcA + dstA - srcA * dstA);
                    for (int i = 0; i < 3; ++i) {
                        float srcC = (float)srcPixel[i] / 255.0f;
                        float dstC = (float)dstPixel[i] / 255.0f;
                        float blended = Context.blend(this.mode, srcC, dstC);
                        float out = (1.0f - srcA) * dstC + (1.0f - dstA) * srcC + srcA * dstA * blended;
                        dstPixel[i] = Context.clamp(Math.round(MathUtil.clamp01(out) * 255.0f));
                    }
                    dstPixel[3] = Context.clamp(Math.round(outA * 255.0f));
                    dstOut.setPixel(x, y, dstPixel);
                }
            }
        }

        private static float blend(Mode mode, float src, float dst) {
            return switch (mode) {
                default -> throw new IncompatibleClassChangeError();
                case Mode.MULTIPLY -> src * dst;
                case Mode.SCREEN -> 1.0f - (1.0f - src) * (1.0f - dst);
                case Mode.DARKEN -> Math.min(src, dst);
                case Mode.LIGHTEN -> Math.max(src, dst);
            };
        }

        private static int clamp(int value) {
            if (value < 0) {
                return 0;
            }
            return Math.min(value, 255);
        }
    }
}

