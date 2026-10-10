/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import com.mojang.blaze3d.platform.NativeImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;
import org.espetro.client.gui.EspetroAuiWidgets;
import org.espetro.client.gui.MapVotePreviewResolver;

final class CurrentMapBackgroundRenderer {
    private static final int MAX_TEXTURE_WIDTH = 320;
    private static final int MAX_TEXTURE_HEIGHT = 180;
    private static final int BLUR_RADIUS = 5;
    private static final int DARK_OVERLAY = -1291845632;
    private static final Map<String, BackgroundTexture> CACHE = new HashMap<String, BackgroundTexture>();
    private static final Set<String> FAILED = new HashSet<String>();

    private CurrentMapBackgroundRenderer() {
    }

    static void render(GuiGraphics graphics, int screenWidth, int screenHeight, String mapFolder) {
        BackgroundTexture texture = CurrentMapBackgroundRenderer.getOrLoad(mapFolder);
        if (texture == null || screenWidth <= 0 || screenHeight <= 0) {
            EspetroAuiWidgets.drawScreenShade(graphics, screenWidth, screenHeight);
            return;
        }
        Crop crop = CurrentMapBackgroundRenderer.aspectFillCrop(texture.width, texture.height, screenWidth, screenHeight);
        graphics.m_280411_(texture.location, 0, 0, screenWidth, screenHeight, crop.u, crop.v, crop.width, crop.height, texture.width, texture.height);
        graphics.m_280509_(0, 0, screenWidth, screenHeight, -1291845632);
    }

    static Crop aspectFillCrop(int textureWidth, int textureHeight, int screenWidth, int screenHeight) {
        if (textureWidth <= 0 || textureHeight <= 0 || screenWidth <= 0 || screenHeight <= 0) {
            return new Crop(0, 0, Math.max(1, textureWidth), Math.max(1, textureHeight));
        }
        long textureScaled = (long)textureWidth * (long)screenHeight;
        long screenScaled = (long)screenWidth * (long)textureHeight;
        if (textureScaled > screenScaled) {
            int cropWidth = Math.max(1, Math.min(textureWidth, (int)((long)textureHeight * (long)screenWidth / (long)screenHeight)));
            return new Crop((textureWidth - cropWidth) / 2, 0, cropWidth, textureHeight);
        }
        if (textureScaled < screenScaled) {
            int cropHeight = Math.max(1, Math.min(textureHeight, (int)((long)textureWidth * (long)screenHeight / (long)screenWidth)));
            return new Crop(0, (textureHeight - cropHeight) / 2, textureWidth, cropHeight);
        }
        return new Crop(0, 0, textureWidth, textureHeight);
    }

    /*
     * Enabled aggressive exception aggregation
     */
    private static BackgroundTexture getOrLoad(String mapFolder) {
        String key;
        String string = key = mapFolder == null ? "" : mapFolder.trim();
        if (key.isEmpty() || FAILED.contains(key)) {
            return null;
        }
        BackgroundTexture cached = CACHE.get(key);
        if (cached != null) {
            return cached;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null) {
            return null;
        }
        Path previewPath = MapVotePreviewResolver.resolve(minecraft.f_91069_.toPath(), key);
        if (previewPath == null) {
            FAILED.add(key);
            return null;
        }
        try (InputStream input = Files.newInputStream(previewPath, new OpenOption[0]);){
            NativeImage source = NativeImage.m_85058_(input);
            try {
                NativeImage blurred = CurrentMapBackgroundRenderer.createBlurredImage(source);
                DynamicTexture dynamicTexture = new DynamicTexture(blurred);
                dynamicTexture.m_117960_(true, false);
                ResourceLocation location = ResourceLocation.fromNamespaceAndPath((String)"espetro", (String)("map_background/" + Integer.toUnsignedString(key.hashCode(), 16)));
                minecraft.m_91097_().m_118495_(location, dynamicTexture);
                BackgroundTexture loaded = new BackgroundTexture(location, blurred.m_84982_(), blurred.m_85084_());
                CACHE.put(key, loaded);
                BackgroundTexture backgroundTexture = loaded;
                if (source != null) {
                    source.close();
                }
                return backgroundTexture;
            }
            catch (Throwable throwable) {
                if (source != null) {
                    try {
                        source.close();
                    }
                    catch (Throwable throwable2) {
                        throwable.addSuppressed(throwable2);
                    }
                }
                throw throwable;
            }
        }
        catch (IOException | RuntimeException ignored) {
            FAILED.add(key);
            return null;
        }
    }

    private static NativeImage createBlurredImage(NativeImage source) {
        double scale = Math.min(1.0, Math.min(320.0 / (double)source.m_84982_(), 180.0 / (double)source.m_85084_()));
        int width = Math.max(1, (int)Math.round((double)source.m_84982_() * scale));
        int height = Math.max(1, (int)Math.round((double)source.m_85084_() * scale));
        int[] pixels = new int[width * height];
        for (int y = 0; y < height; ++y) {
            int sourceY = Math.min(source.m_85084_() - 1, (int)((long)y * (long)source.m_85084_() / (long)height));
            for (int x = 0; x < width; ++x) {
                int sourceX = Math.min(source.m_84982_() - 1, (int)((long)x * (long)source.m_84982_() / (long)width));
                pixels[y * width + x] = source.m_84985_(sourceX, sourceY);
            }
        }
        int[] blurredPixels = CurrentMapBackgroundRenderer.boxBlur(pixels, width, height, 5);
        NativeImage blurred = new NativeImage(width, height, false);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                blurred.m_84988_(x, y, blurredPixels[y * width + x]);
            }
        }
        return blurred;
    }

    static int[] boxBlur(int[] source, int width, int height, int radius) {
        if (source == null || source.length != width * height || width <= 0 || height <= 0) {
            throw new IllegalArgumentException("Invalid image dimensions");
        }
        if (radius <= 0) {
            return (int[])source.clone();
        }
        int[] horizontal = new int[source.length];
        int[] output = new int[source.length];
        CurrentMapBackgroundRenderer.blurPass(source, horizontal, width, height, radius, true);
        CurrentMapBackgroundRenderer.blurPass(horizontal, output, width, height, radius, false);
        return output;
    }

    private static void blurPass(int[] source, int[] target, int width, int height, int radius, boolean horizontal) {
        int lines = horizontal ? height : width;
        int lineLength = horizontal ? width : height;
        for (int line = 0; line < lines; ++line) {
            int position;
            long a = 0L;
            long b = 0L;
            long c = 0L;
            long d = 0L;
            int count = 0;
            for (position = -radius; position <= radius; ++position) {
                if (position < 0 || position >= lineLength) continue;
                int pixel = source[CurrentMapBackgroundRenderer.index(horizontal, line, position, width)];
                a += (long)(pixel >>> 24);
                b += (long)(pixel >>> 16 & 0xFF);
                c += (long)(pixel >>> 8 & 0xFF);
                d += (long)(pixel & 0xFF);
                ++count;
            }
            for (position = 0; position < lineLength; ++position) {
                int incoming;
                target[CurrentMapBackgroundRenderer.index((boolean)horizontal, (int)line, (int)position, (int)width)] = (int)(a / (long)count) << 24 | (int)(b / (long)count) << 16 | (int)(c / (long)count) << 8 | (int)(d / (long)count);
                int outgoing = position - radius;
                if (outgoing >= 0) {
                    int pixel = source[CurrentMapBackgroundRenderer.index(horizontal, line, outgoing, width)];
                    a -= (long)(pixel >>> 24);
                    b -= (long)(pixel >>> 16 & 0xFF);
                    c -= (long)(pixel >>> 8 & 0xFF);
                    d -= (long)(pixel & 0xFF);
                    --count;
                }
                if ((incoming = position + radius + 1) >= lineLength) continue;
                int pixel = source[CurrentMapBackgroundRenderer.index(horizontal, line, incoming, width)];
                a += (long)(pixel >>> 24);
                b += (long)(pixel >>> 16 & 0xFF);
                c += (long)(pixel >>> 8 & 0xFF);
                d += (long)(pixel & 0xFF);
                ++count;
            }
        }
    }

    private static int index(boolean horizontal, int line, int position, int width) {
        return horizontal ? line * width + position : position * width + line;
    }

    private record BackgroundTexture(ResourceLocation location, int width, int height) {
    }

    record Crop(int u, int v, int width, int height) {
    }
}

