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

final class AdditiveComposite
implements Composite {
    private final float alpha;

    AdditiveComposite(float alpha) {
        this.alpha = Math.max(0.0f, Math.min(1.0f, alpha));
    }

    @Override
    public CompositeContext createContext(ColorModel srcColorModel, ColorModel dstColorModel, RenderingHints hints) {
        return new Context(this.alpha);
    }

    private record Context(float alpha) implements CompositeContext
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
                    float srcPremulR = (float)srcPixel[0] / 255.0f * srcA;
                    float srcPremulG = (float)srcPixel[1] / 255.0f * srcA;
                    float srcPremulB = (float)srcPixel[2] / 255.0f * srcA;
                    float dstPremulR = (float)dstPixel[0] / 255.0f * dstA;
                    float dstPremulG = (float)dstPixel[1] / 255.0f * dstA;
                    float dstPremulB = (float)dstPixel[2] / 255.0f * dstA;
                    float outPremulR = MathUtil.clamp01(srcPremulR + dstPremulR);
                    float outPremulG = MathUtil.clamp01(srcPremulG + dstPremulG);
                    float outPremulB = MathUtil.clamp01(srcPremulB + dstPremulB);
                    float outA = MathUtil.clamp01(srcA + dstA);
                    if (outA <= 1.0E-6f) {
                        dstPixel[0] = 0;
                        dstPixel[1] = 0;
                        dstPixel[2] = 0;
                        dstPixel[3] = 0;
                    } else {
                        dstPixel[0] = Context.clamp(Math.round(outPremulR / outA * 255.0f));
                        dstPixel[1] = Context.clamp(Math.round(outPremulG / outA * 255.0f));
                        dstPixel[2] = Context.clamp(Math.round(outPremulB / outA * 255.0f));
                        dstPixel[3] = Context.clamp(Math.round(outA * 255.0f));
                    }
                    dstOut.setPixel(x, y, dstPixel);
                }
            }
        }

        private static int clamp(int value) {
            if (value < 0) {
                return 0;
            }
            return Math.min(value, 255);
        }
    }
}

