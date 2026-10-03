/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.canvas.CanvasImageBitmap;
import com.sighs.apricityui.canvas.CanvasImageData;
import com.sighs.apricityui.canvas.CanvasStyleUtil;
import com.sighs.apricityui.element.Canvas;
import com.sighs.apricityui.element.Img;
import com.sighs.apricityui.init.Window;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.util.AuiLog;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

public final class CanvasImageSupport {
    private CanvasImageSupport() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static BufferedImage resolveImageSource(Object image) {
        if (image instanceof Canvas) {
            Canvas sourceCanvas = (Canvas)image;
            BufferedImage source = sourceCanvas.getSurface();
            BufferedImage copy = new BufferedImage(source.getWidth(), source.getHeight(), 2);
            Graphics2D copyGraphics = copy.createGraphics();
            try {
                copyGraphics.drawImage((Image)source, 0, 0, null);
                return copy;
            }
            finally {
                copyGraphics.dispose();
            }
        }
        if (image instanceof BufferedImage) {
            return (BufferedImage)image;
        }
        if (image instanceof CanvasImageBitmap) {
            CanvasImageBitmap bitmap = (CanvasImageBitmap)image;
            return bitmap.image();
        }
        if (image instanceof CanvasImageData) {
            CanvasImageData imageData = (CanvasImageData)image;
            return CanvasImageSupport.fromImageData(imageData);
        }
        if (image instanceof Window.FetchResponse) {
            Window.FetchResponse response = (Window.FetchResponse)image;
            return CanvasImageSupport.readImageBytes(response.bytes());
        }
        if (image instanceof byte[]) {
            byte[] bytes = (byte[])image;
            return CanvasImageSupport.readImageBytes(bytes);
        }
        if (image instanceof String) {
            String text = (String)image;
            return CanvasImageSupport.resolveStringSource(text);
        }
        if (!(image instanceof Img)) return null;
        Img img = (Img)image;
        String src = img.getAttribute("src");
        if (src == null || src.isBlank() || img.document == null) {
            ApricityUI.LOGGER.warn("[AUI Canvas] image element has no usable src element={}", (Object)AuiLog.element(img));
            return null;
        }
        String resolvedPath = Loader.resolve(img.document.getPath(), src);
        try (InputStream stream = Loader.getResourceStream(resolvedPath);){
            if (stream == null) {
                ApricityUI.LOGGER.warn("[AUI Canvas] image resource is missing path={}", (Object)resolvedPath);
                BufferedImage bufferedImage2 = null;
                return bufferedImage2;
            }
            BufferedImage result = ImageIO.read(stream);
            if (result == null) {
                ApricityUI.LOGGER.warn("[AUI Canvas] ImageIO could not decode path={}", (Object)resolvedPath);
            }
            BufferedImage bufferedImage = result;
            return bufferedImage;
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.error("[AUI Canvas] failed to read image path={}", (Object)resolvedPath, (Object)exception);
            return null;
        }
    }

    private static BufferedImage fromImageData(CanvasImageData imageData) {
        if (imageData == null) {
            return null;
        }
        BufferedImage bufferedImage = new BufferedImage(imageData.width, imageData.height, 2);
        for (int y = 0; y < imageData.height; ++y) {
            for (int x = 0; x < imageData.width; ++x) {
                int index = (y * imageData.width + x) * 4;
                if (index + 3 >= imageData.data.length) continue;
                int r = CanvasStyleUtil.clampChannel(imageData.data[index]);
                int g = CanvasStyleUtil.clampChannel(imageData.data[index + 1]);
                int b = CanvasStyleUtil.clampChannel(imageData.data[index + 2]);
                int a = CanvasStyleUtil.clampChannel(imageData.data[index + 3]);
                bufferedImage.setRGB(x, y, a << 24 | r << 16 | g << 8 | b);
            }
        }
        return bufferedImage;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    private static BufferedImage resolveStringSource(String text) {
        if (text == null) return null;
        if (text.isBlank()) {
            return null;
        }
        String trimmed = text.trim();
        if (trimmed.regionMatches(true, 0, "data:", 0, 5)) {
            int comma = trimmed.indexOf(44);
            if (comma < 0) {
                ApricityUI.LOGGER.warn("[AUI Canvas] malformed data image URI");
                return null;
            }
            String meta22 = trimmed.substring(0, comma);
            String body = trimmed.substring(comma + 1);
            if (!meta22.toLowerCase().contains(";base64")) {
                ApricityUI.LOGGER.warn("[AUI Canvas] unsupported non-base64 data image URI");
                return null;
            }
            try {
                return CanvasImageSupport.readImageBytes(Base64.getDecoder().decode(body));
            }
            catch (IllegalArgumentException exception) {
                ApricityUI.LOGGER.warn("[AUI Canvas] invalid base64 image URI", (Throwable)exception);
                return null;
            }
        }
        try (InputStream stream = Loader.getResourceStream(trimmed);){
            if (stream == null) {
                ApricityUI.LOGGER.warn("[AUI Canvas] image resource is missing path={}", (Object)trimmed);
                BufferedImage meta22 = null;
                return meta22;
            }
            BufferedImage result = ImageIO.read(stream);
            if (result == null) {
                ApricityUI.LOGGER.warn("[AUI Canvas] ImageIO could not decode path={}", (Object)trimmed);
            }
            BufferedImage bufferedImage = result;
            return bufferedImage;
        }
        catch (IOException exception) {
            ApricityUI.LOGGER.error("[AUI Canvas] failed to read image path={}", (Object)trimmed, (Object)exception);
            return null;
        }
    }

    private static BufferedImage readImageBytes(byte[] bytes) {
        BufferedImage bufferedImage;
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        try {
            bufferedImage = ImageIO.read(stream);
        }
        catch (Throwable throwable) {
            try {
                try {
                    stream.close();
                }
                catch (Throwable throwable2) {
                    throwable.addSuppressed(throwable2);
                }
                throw throwable;
            }
            catch (IOException exception) {
                ApricityUI.LOGGER.error("[AUI Canvas] failed to decode image bytes size={}", (Object)bytes.length, (Object)exception);
                return null;
            }
        }
        stream.close();
        return bufferedImage;
    }

    static BufferedImage tintImageAlpha(BufferedImage source, Color tint) {
        if (source == null) {
            return null;
        }
        BufferedImage tinted = new BufferedImage(source.getWidth(), source.getHeight(), 2);
        int tr = tint.getRed();
        int tg = tint.getGreen();
        int tb = tint.getBlue();
        int ta = tint.getAlpha();
        for (int y = 0; y < source.getHeight(); ++y) {
            for (int x = 0; x < source.getWidth(); ++x) {
                int argb = source.getRGB(x, y);
                int alpha = (argb >>> 24 & 0xFF) * ta / 255;
                tinted.setRGB(x, y, alpha << 24 | tr << 16 | tg << 8 | tb);
            }
        }
        return tinted;
    }
}

