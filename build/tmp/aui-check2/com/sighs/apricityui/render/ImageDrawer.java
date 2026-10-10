/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.render.TextureRenderQueue;
import com.sighs.apricityui.resource.Image;
import com.sighs.apricityui.resource.async.image.ImageAsyncHandler;
import com.sighs.apricityui.resource.async.image.ImageHandle;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.RenderHandle;
import com.sighs.apricityui.spi.TextureKey;
import com.sighs.apricityui.style.Background;
import com.sighs.apricityui.style.Style;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImageDrawer {
    private static final Map<RenderKey, RenderHandle> RENDER_TYPE_CACHE = new ConcurrentHashMap<RenderKey, RenderHandle>();
    private static final int PLACEHOLDER_COLOR = 0x33404040;
    public static final float[] NO_RADIUS = new float[]{0.0f, 0.0f, 0.0f, 0.0f};
    private static final TextureRenderQueue TEXTURE_QUEUE = new TextureRenderQueue();

    private static RenderHandle getRenderHandle(TextureKey texture, boolean blur) {
        return ImageDrawer.getRenderHandle(texture, blur, true);
    }

    private static RenderHandle getRenderHandle(TextureKey texture, boolean blur, boolean depthTest) {
        depthTest = depthTest && Base.isDepthTestEnabled();
        return RENDER_TYPE_CACHE.computeIfAbsent(new RenderKey(texture, blur, depthTest), key -> AuiServices.resources().smoothRenderType(key.location(), key.blur(), key.depthTest()));
    }

    public static void draw(PoseStack poseStack, TextureKey texture, float x, float y, float width, float height, boolean blur) {
        if (texture == null) {
            return;
        }
        ImageDrawer.innerBlit(poseStack, texture, x, y, width, height, 0.0f, 0.0f, 1, 1, 1, 1, blur, true);
    }

    public static void drawWithUvWindow(PoseStack poseStack, TextureKey texture, float x, float y, float width, float height, boolean blur, int textureWidth, int textureHeight, float uTexel, float vTexel, float widthTexels, float heightTexels) {
        if (texture == null || textureWidth <= 0 || textureHeight <= 0) {
            return;
        }
        ImageDrawer.innerBlit(poseStack, texture, x, y, width, height, uTexel, vTexel, Math.max(0.0f, widthTexels), Math.max(0.0f, heightTexels), textureWidth, textureHeight, blur, true);
    }

    public static void drawOverlay(PoseStack poseStack, TextureKey texture, float x, float y, float width, float height, boolean blur) {
        if (texture == null) {
            return;
        }
        ImageDrawer.innerBlit(poseStack, texture, x, y, width, height, 0.0f, 0.0f, 1, 1, 1, 1, blur, false);
    }

    public static void draw(PoseStack poseStack, Element element, Rect rect) {
        String src = element.getAttribute("src");
        if (src == null || src.isEmpty()) {
            return;
        }
        Position position = rect.getBodyRectPosition();
        Size size = rect.getBodyRectSize();
        String contextPath = element.document.getPath();
        String resolvedPath = Loader.resolve(contextPath, src);
        float x = (float)position.x;
        float y = (float)position.y;
        float width = (float)size.width();
        float height = (float)size.height();
        boolean needRelayout = width == 0.0f || height == 0.0f;
        ImageDrawer.draw(poseStack, resolvedPath, x, y, width, height, "true".equals(element.getAttribute("blur")), element, needRelayout);
    }

    public static void draw(PoseStack poseStack, String path, int x, int y, int width, int height, boolean blur) {
        ImageDrawer.draw(poseStack, path, x, y, width, height, blur, null, false);
    }

    private static void draw(PoseStack poseStack, String path, int x, int y, int width, int height, boolean blur, Element requester, boolean needRelayout) {
        ImageDrawer.draw(poseStack, path, (float)x, (float)y, (float)width, (float)height, blur, requester, needRelayout);
    }

    private static void draw(PoseStack poseStack, String path, float x, float y, float width, float height, boolean blur, Element requester, boolean needRelayout) {
        ImageHandle handle = ImageAsyncHandler.INSTANCE.request(path, requester, needRelayout);
        if (handle == null || handle.state() != AbstractAsyncHandler.AsyncState.READY || handle.texture() == null) {
            ImageDrawer.drawPlaceholder(poseStack, x, y, width, height);
            return;
        }
        Image.ITexture texture = handle.texture();
        TextureKey currentLocation = AuiServices.resources().locationOf(texture.getKey());
        if (currentLocation == null) {
            return;
        }
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        if (width == 0.0f && textureHeight > 0) {
            width = (float)(1.0 * (double)height / (double)textureHeight * (double)textureWidth);
        }
        if (height == 0.0f && textureWidth > 0) {
            height = (float)(1.0 * (double)width / (double)textureWidth * (double)textureHeight);
        }
        ObjectFitRect drawRect = requester == null ? new ObjectFitRect(x, y, width, height) : ImageDrawer.resolveObjectFitRect(requester.getComputedStyle(), x, y, width, height, textureWidth, textureHeight);
        ImageDrawer.innerBlit(poseStack, currentLocation, drawRect.x(), drawRect.y(), drawRect.width(), drawRect.height(), 0.0f, 0.0f, textureWidth, textureHeight, textureWidth, textureHeight, blur, true);
    }

    public static ObjectFitRect resolveObjectFitRect(Style style, float boxX, float boxY, float boxW, float boxH, int intrinsicW, int intrinsicH) {
        if (boxW <= 0.0f || boxH <= 0.0f || intrinsicW <= 0 || intrinsicH <= 0) {
            return new ObjectFitRect(boxX, boxY, Math.max(0.0f, boxW), Math.max(0.0f, boxH));
        }
        String fit = style == null || style.objectFit == null ? "fill" : style.objectFit.trim().toLowerCase(Locale.ROOT);
        float drawW = boxW;
        float drawH = boxH;
        float intrinsicWidth = intrinsicW;
        float intrinsicHeight = intrinsicH;
        switch (fit) {
            case "contain": {
                float scale = Math.min(boxW / intrinsicWidth, boxH / intrinsicHeight);
                drawW = intrinsicWidth * scale;
                drawH = intrinsicHeight * scale;
                break;
            }
            case "cover": {
                float scale = Math.max(boxW / intrinsicWidth, boxH / intrinsicHeight);
                drawW = intrinsicWidth * scale;
                drawH = intrinsicHeight * scale;
                break;
            }
            case "none": {
                drawW = intrinsicWidth;
                drawH = intrinsicHeight;
                break;
            }
            case "scale-down": {
                float scale = Math.min(boxW / intrinsicWidth, boxH / intrinsicHeight);
                if (scale < 1.0f) {
                    drawW = intrinsicWidth * scale;
                    drawH = intrinsicHeight * scale;
                    break;
                }
                drawW = intrinsicWidth;
                drawH = intrinsicHeight;
                break;
            }
            case "fill": {
                drawW = boxW;
                drawH = boxH;
                break;
            }
            default: {
                drawW = boxW;
                drawH = boxH;
            }
        }
        float[] offset = ImageDrawer.parseObjectPosition(style == null ? null : style.objectPosition, boxW, boxH, drawW, drawH);
        return new ObjectFitRect(boxX + offset[0], boxY + offset[1], drawW, drawH);
    }

    private static float[] parseObjectPosition(String value, float boxW, float boxH, float objectW, float objectH) {
        String yToken;
        String normalized = value == null || value.isBlank() || "unset".equalsIgnoreCase(value.trim()) ? "50% 50%" : value.trim().toLowerCase(Locale.ROOT);
        String[] parts = normalized.split("\\s+");
        String xToken = parts.length > 0 ? parts[0] : "50%";
        String string = yToken = parts.length > 1 ? parts[1] : "50%";
        if (parts.length == 1 && ImageDrawer.isVerticalPositionKeyword(xToken)) {
            yToken = xToken;
            xToken = "50%";
        } else if (parts.length == 1 && ImageDrawer.isHorizontalPositionKeyword(xToken)) {
            yToken = "50%";
        }
        float freeX = boxW - objectW;
        float freeY = boxH - objectH;
        return new float[]{ImageDrawer.resolveObjectPositionToken(xToken, freeX, true), ImageDrawer.resolveObjectPositionToken(yToken, freeY, false)};
    }

    private static float resolveObjectPositionToken(String token, float freeSpace, boolean horizontal) {
        if (token == null || token.isBlank()) {
            return freeSpace * 0.5f;
        }
        String value = token.trim().toLowerCase(Locale.ROOT);
        if (horizontal && "left".equals(value) || !horizontal && "top".equals(value)) {
            return 0.0f;
        }
        if ("center".equals(value)) {
            return freeSpace * 0.5f;
        }
        if (horizontal && "right".equals(value) || !horizontal && "bottom".equals(value)) {
            return freeSpace;
        }
        if (value.endsWith("%")) {
            try {
                return freeSpace * Float.parseFloat(value.substring(0, value.length() - 1).trim()) / 100.0f;
            }
            catch (NumberFormatException ignored) {
                return freeSpace * 0.5f;
            }
        }
        String raw = value.endsWith("px") ? value.substring(0, value.length() - 2).trim() : value;
        try {
            return Float.parseFloat(raw);
        }
        catch (NumberFormatException ignored) {
            return freeSpace * 0.5f;
        }
    }

    private static boolean isHorizontalPositionKeyword(String token) {
        return "left".equals(token) || "right".equals(token) || "center".equals(token);
    }

    private static boolean isVerticalPositionKeyword(String token) {
        return "top".equals(token) || "bottom".equals(token) || "center".equals(token);
    }

    public static void clearCache() {
        ImageAsyncHandler.INSTANCE.clearAndBumpGeneration();
        ImageDrawer.clearRenderTypeCache();
    }

    public static void clearRenderTypeCache() {
        RENDER_TYPE_CACHE.clear();
    }

    public static void flushBatch() {
        TEXTURE_QUEUE.flush();
    }

    public static void drawComplexBackground(PoseStack poseStack, float x, float y, float width, float height, Background bg) {
        ImageDrawer.drawComplexBackground(poseStack, x, y, width, height, bg, null);
    }

    public static void drawComplexBackground(PoseStack poseStack, float x, float y, float width, float height, Background bg, Element requester) {
        if (bg == null) {
            return;
        }
        Background.Layer layer = new Background.Layer();
        layer.imagePath = bg.imagePath;
        layer.repeat = bg.repeat;
        layer.size = bg.size;
        layer.position = bg.position;
        ImageDrawer.drawComplexBackground(poseStack, x, y, width, height, layer, requester);
    }

    public static void drawComplexBackground(PoseStack poseStack, float x, float y, float width, float height, Background.Layer layer) {
        ImageDrawer.drawComplexBackground(poseStack, x, y, width, height, layer, null);
    }

    public static void drawComplexBackground(PoseStack poseStack, float x, float y, float width, float height, Background.Layer layer, Element requester) {
        float startY;
        if (layer == null) {
            return;
        }
        String path = layer.imagePath;
        ReadyTexture readyTexture = ImageDrawer.requestReadyTexture(path, poseStack, x, y, width, height, requester);
        if (readyTexture == null) {
            return;
        }
        int tw = readyTexture.width();
        int th = readyTexture.height();
        TextureKey loc = readyTexture.location();
        float[] renderSize = ImageDrawer.resolveRenderSize(layer.size, width, height, tw, th);
        float renderW = renderSize[0];
        float renderH = renderSize[1];
        if (renderW <= 0.0f || renderH <= 0.0f) {
            return;
        }
        float[] offset = ImageDrawer.parseBackgroundPosition(layer.position, width, height, renderW, renderH);
        float offsetX = offset[0];
        float offsetY = offset[1];
        RepeatMode repeatMode = ImageDrawer.parseRepeatMode(layer.repeat);
        float startX = repeatMode.repeatX ? ImageDrawer.normalizeRepeatStart(offsetX, renderW) : offsetX;
        float f = startY = repeatMode.repeatY ? ImageDrawer.normalizeRepeatStart(offsetY, renderH) : offsetY;
        if (!ImageDrawer.requiresBackgroundClip(width, height, startX, startY, renderW, renderH, repeatMode.repeatX, repeatMode.repeatY)) {
            ImageDrawer.innerBlit(poseStack, loc, x + startX, y + startY, renderW, renderH, 0.0f, 0.0f, tw, th, tw, th, false, true);
            return;
        }
        ImageDrawer.flushBatch();
        Mask.pushMask(poseStack, x, y, width, height, NO_RADIUS);
        if (!repeatMode.repeatX && !repeatMode.repeatY) {
            ImageDrawer.innerBlit(poseStack, loc, x + startX, y + startY, renderW, renderH, 0.0f, 0.0f, tw, th, tw, th, false, true);
        } else {
            float xEnd = repeatMode.repeatX ? width : startX + 1.0f;
            float yEnd = repeatMode.repeatY ? height : startY + 1.0f;
            for (float ix = startX; ix < xEnd; ix += renderW) {
                for (float iy = startY; iy < yEnd; iy += renderH) {
                    ImageDrawer.innerBlit(poseStack, loc, x + ix, y + iy, renderW, renderH, 0.0f, 0.0f, tw, th, tw, th, false, true);
                }
            }
        }
        Mask.popMask(poseStack, x, y, width, height, NO_RADIUS);
    }

    static boolean requiresBackgroundClip(float boxW, float boxH, float startX, float startY, float renderW, float renderH, boolean repeatX, boolean repeatY) {
        if (repeatX || repeatY) {
            return true;
        }
        if (!(Float.isFinite(boxW) && Float.isFinite(boxH) && Float.isFinite(startX) && Float.isFinite(startY) && Float.isFinite(renderW) && Float.isFinite(renderH))) {
            return true;
        }
        return startX < 0.0f || startY < 0.0f || startX + renderW > boxW || startY + renderH > boxH;
    }

    public static GradientTile resolveGradientTile(Background.Layer layer, float width, float height) {
        if (layer == null) {
            return new GradientTile(0.0f, 0.0f, width, height, 0.0f, 0.0f, width, height, false);
        }
        float[] renderSize = ImageDrawer.resolveRenderSize(layer.size, width, height, Math.max(1, Math.round(width)), Math.max(1, Math.round(height)));
        float renderW = Math.max(0.001f, renderSize[0]);
        float renderH = Math.max(0.001f, renderSize[1]);
        float[] offset = ImageDrawer.parseBackgroundPosition(layer.position, width, height, renderW, renderH);
        RepeatMode repeatMode = ImageDrawer.parseRepeatMode(layer.repeat);
        float startX = repeatMode.repeatX ? ImageDrawer.normalizeRepeatStart(offset[0], renderW) : offset[0];
        float startY = repeatMode.repeatY ? ImageDrawer.normalizeRepeatStart(offset[1], renderH) : offset[1];
        float endX = repeatMode.repeatX ? width : startX + 1.0f;
        float endY = repeatMode.repeatY ? height : startY + 1.0f;
        return new GradientTile(offset[0], offset[1], renderW, renderH, startX, startY, endX, endY, repeatMode.repeatX || repeatMode.repeatY);
    }

    private static float[] resolveRenderSize(String backgroundSize, float boxW, float boxH, int texW, int texH) {
        String size;
        switch (size = backgroundSize == null || backgroundSize.isEmpty() || "unset".equals(backgroundSize) ? "auto" : backgroundSize.trim().toLowerCase(Locale.ROOT)) {
            case "cover": {
                float scale = Math.max(boxW / (float)texW, boxH / (float)texH);
                return new float[]{(float)texW * scale, (float)texH * scale};
            }
            case "contain": {
                float scale = Math.min(boxW / (float)texW, boxH / (float)texH);
                return new float[]{(float)texW * scale, (float)texH * scale};
            }
            case "auto": {
                return new float[]{texW, texH};
            }
        }
        String[] parts = size.split("\\s+");
        String widthToken = parts.length > 0 ? parts[0] : "auto";
        String heightToken = parts.length > 1 ? parts[1] : "auto";
        float intrinsicW = texW;
        float intrinsicH = texH;
        float aspect = intrinsicH == 0.0f ? 1.0f : intrinsicW / intrinsicH;
        Float resolvedW = ImageDrawer.resolveBackgroundSizeToken(widthToken, boxW, intrinsicW);
        Float resolvedH = ImageDrawer.resolveBackgroundSizeToken(heightToken, boxH, intrinsicH);
        if (parts.length == 1 && !"auto".equals(widthToken) && resolvedW != null) {
            return new float[]{resolvedW.floatValue(), aspect == 0.0f ? intrinsicH : resolvedW.floatValue() / aspect};
        }
        if (resolvedW == null && resolvedH == null) {
            return new float[]{intrinsicW, intrinsicH};
        }
        if (resolvedW == null) {
            float height = resolvedH == null ? intrinsicH : resolvedH.floatValue();
            return new float[]{height * aspect, height};
        }
        if (resolvedH == null) {
            return new float[]{resolvedW.floatValue(), aspect == 0.0f ? intrinsicH : resolvedW.floatValue() / aspect};
        }
        return new float[]{resolvedW.floatValue(), resolvedH.floatValue()};
    }

    private static float normalizeRepeatStart(float offset, float tileSize) {
        if (tileSize <= 0.0f) {
            return 0.0f;
        }
        float start = ImageDrawer.mod(offset, tileSize);
        if (start > 0.0f) {
            start -= tileSize;
        }
        return start;
    }

    private static float mod(float a, float b) {
        if (b == 0.0f) {
            return 0.0f;
        }
        float m = a % b;
        return m < 0.0f ? m + b : m;
    }

    private static float[] parseBackgroundPosition(String position, float boxW, float boxH, float renderW, float renderH) {
        String yPart;
        String normalized = position == null || position.isEmpty() || "unset".equals(position) ? "0 0" : position.trim().toLowerCase(Locale.ROOT);
        String[] parts = normalized.split("\\s+");
        String xPart = parts.length > 0 ? parts[0] : "0";
        String string = yPart = parts.length > 1 ? parts[1] : "0";
        if (parts.length == 1 && ImageDrawer.isPositionKeyword(xPart)) {
            if ("top".equals(xPart) || "bottom".equals(xPart)) {
                yPart = xPart;
                xPart = "center";
            } else {
                yPart = "center";
            }
        }
        float x = ImageDrawer.parsePositionToken(xPart, boxW, renderW, true);
        float y = ImageDrawer.parsePositionToken(yPart, boxH, renderH, false);
        return new float[]{x, y};
    }

    private static boolean isPositionKeyword(String token) {
        return "left".equals(token) || "right".equals(token) || "center".equals(token) || "top".equals(token) || "bottom".equals(token);
    }

    private static float parsePositionToken(String token, float boxSize, float renderSize, boolean isX) {
        if (token == null || token.isEmpty()) {
            return 0.0f;
        }
        String normalized = token.trim().toLowerCase(Locale.ROOT);
        if ("center".equals(normalized)) {
            return (boxSize - renderSize) / 2.0f;
        }
        if (isX && "left".equals(normalized) || !isX && "top".equals(normalized)) {
            return 0.0f;
        }
        if (isX && "right".equals(normalized) || !isX && "bottom".equals(normalized)) {
            return boxSize - renderSize;
        }
        if (normalized.endsWith("%")) {
            try {
                float percent = Float.parseFloat(normalized.substring(0, normalized.length() - 1).trim()) / 100.0f;
                return (boxSize - renderSize) * percent;
            }
            catch (NumberFormatException ignored) {
                return 0.0f;
            }
        }
        String raw = normalized.endsWith("px") ? normalized.substring(0, normalized.length() - 2).trim() : normalized;
        try {
            return Float.parseFloat(raw);
        }
        catch (NumberFormatException ignored) {
            return 0.0f;
        }
    }

    private static Float resolveBackgroundSizeToken(String token, float boxSize, float intrinsicSize) {
        if (token == null || token.isEmpty()) {
            return null;
        }
        String normalized = token.trim().toLowerCase(Locale.ROOT);
        if ("auto".equals(normalized)) {
            return null;
        }
        if (normalized.endsWith("%")) {
            try {
                float percent = Float.parseFloat(normalized.substring(0, normalized.length() - 1).trim()) / 100.0f;
                return Float.valueOf(boxSize * percent);
            }
            catch (NumberFormatException ignored) {
                return Float.valueOf(intrinsicSize);
            }
        }
        String raw = normalized.endsWith("px") ? normalized.substring(0, normalized.length() - 2).trim() : normalized;
        try {
            return Float.valueOf(Float.parseFloat(raw));
        }
        catch (NumberFormatException ignored) {
            return Float.valueOf(intrinsicSize);
        }
    }

    private static RepeatMode parseRepeatMode(String repeat) {
        String normalized;
        if (repeat == null || repeat.isBlank() || "unset".equalsIgnoreCase(repeat.trim())) {
            return new RepeatMode(false, false);
        }
        return switch (normalized = repeat.trim().toLowerCase(Locale.ROOT)) {
            case "repeat-x" -> new RepeatMode(true, false);
            case "repeat-y" -> new RepeatMode(false, true);
            case "repeat", "space", "round" -> new RepeatMode(true, true);
            default -> new RepeatMode(false, false);
        };
    }

    public static void drawNineSlice(PoseStack poseStack, String path, int x, int y, int w, int h, Box.BorderImage bi) {
        ReadyTexture readyTexture = ImageDrawer.requestReadyTexture(path, poseStack, x, y, w, h);
        if (readyTexture == null) {
            return;
        }
        int texW = readyTexture.width();
        int texH = readyTexture.height();
        TextureKey loc = readyTexture.location();
        int sT = bi.slice[0];
        int sR = bi.slice[1];
        int sB = bi.slice[2];
        int sL = bi.slice[3];
        int bT = bi.width[0];
        int bR = bi.width[1];
        int bB = bi.width[2];
        int bL = bi.width[3];
        int finalX = x - bi.outset[3];
        int finalY = y - bi.outset[0];
        int finalW = w + bi.outset[3] + bi.outset[1];
        int finalH = h + bi.outset[0] + bi.outset[2];
        int srcCW = texW - sL - sR;
        int srcCH = texH - sT - sB;
        int destCW = finalW - bL - bR;
        int destCH = finalH - bT - bB;
        String repeatH = bi.repeat;
        String repeatV = bi.repeat;
        if (bL > 0 && bT > 0) {
            ImageDrawer.innerBlit(poseStack, loc, (float)finalX, (float)finalY, (float)bL, (float)bT, 0.0f, 0.0f, sL, sT, texW, texH, false, true);
        }
        if (bR > 0 && bT > 0) {
            ImageDrawer.innerBlit(poseStack, loc, (float)(finalX + finalW - bR), (float)finalY, (float)bR, (float)bT, (float)(texW - sR), 0.0f, sR, sT, texW, texH, false, true);
        }
        if (bL > 0 && bB > 0) {
            ImageDrawer.innerBlit(poseStack, loc, (float)finalX, (float)(finalY + finalH - bB), (float)bL, (float)bB, 0.0f, (float)(texH - sB), sL, sB, texW, texH, false, true);
        }
        if (bR > 0 && bB > 0) {
            ImageDrawer.innerBlit(poseStack, loc, (float)(finalX + finalW - bR), (float)(finalY + finalH - bB), (float)bR, (float)bB, (float)(texW - sR), (float)(texH - sB), sR, sB, texW, texH, false, true);
        }
        ImageDrawer.drawTiledPart(poseStack, loc, finalX + bL, finalY, destCW, bT, sL, 0.0f, srcCW, sT, texW, texH, repeatH, "stretch");
        ImageDrawer.drawTiledPart(poseStack, loc, finalX + bL, finalY + finalH - bB, destCW, bB, sL, texH - sB, srcCW, sB, texW, texH, repeatH, "stretch");
        ImageDrawer.drawTiledPart(poseStack, loc, finalX, finalY + bT, bL, destCH, 0.0f, sT, sL, srcCH, texW, texH, "stretch", repeatV);
        ImageDrawer.drawTiledPart(poseStack, loc, finalX + finalW - bR, finalY + bT, bR, destCH, texW - sR, sT, sR, srcCH, texW, texH, "stretch", repeatV);
        if (bi.fill && destCW > 0 && destCH > 0) {
            ImageDrawer.drawTiledPart(poseStack, loc, finalX + bL, finalY + bT, destCW, destCH, sL, sT, srcCW, srcCH, texW, texH, repeatH, repeatV);
        }
    }

    private static void drawTiledPart(PoseStack poseStack, TextureKey loc, int dx, int dy, int dw, int dh, float sx, float sy, int sw, int sh, int texW, int texH, String repeatX, String repeatY) {
        if (dw <= 0 || dh <= 0 || sw <= 0 || sh <= 0) {
            return;
        }
        float tileW = dw;
        float tileV = dh;
        if (repeatX.equals("repeat") || repeatX.equals("round")) {
            float f = tileW = repeatX.equals("round") ? (float)dw / (float)Math.max(1, Math.round((float)dw / (float)sw)) : (float)sw;
        }
        if (repeatY.equals("repeat") || repeatY.equals("round")) {
            float f = tileV = repeatY.equals("round") ? (float)dh / (float)Math.max(1, Math.round((float)dh / (float)sh)) : (float)sh;
        }
        if (tileW == (float)dw && tileV == (float)dh) {
            ImageDrawer.innerBlit(poseStack, loc, (float)dx, (float)dy, (float)dw, (float)dh, sx, sy, sw, sh, texW, texH, false, true);
            return;
        }
        ImageDrawer.flushBatch();
        Mask.pushMask(poseStack, dx, dy, dw, dh, NO_RADIUS);
        for (float curX = 0.0f; curX < (float)dw; curX += tileW) {
            for (float curY = 0.0f; curY < (float)dh; curY += tileV) {
                int drawW = (int)Math.min(tileW, (float)dw - curX + 1.0f);
                int drawH = (int)Math.min(tileV, (float)dh - curY + 1.0f);
                ImageDrawer.innerBlit(poseStack, loc, (float)((int)((float)dx + curX)), (float)((int)((float)dy + curY)), (float)drawW, (float)drawH, sx, sy, sw, sh, texW, texH, false, true);
            }
        }
        Mask.popMask(poseStack, dx, dy, dw, dh, NO_RADIUS);
    }

    private static void drawPlaceholder(PoseStack poseStack, float x, float y, float width, float height) {
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }
        ImageDrawer.flushBatch();
        Base.resolveOffset(poseStack);
        Graph.drawFillRect(poseStack.m_85850_().m_252922_(), x, y, x + width, y + height, 0x33404040);
    }

    private static ReadyTexture requestReadyTexture(String path, PoseStack poseStack, float x, float y, float width, float height) {
        return ImageDrawer.requestReadyTexture(path, poseStack, x, y, width, height, null);
    }

    private static ReadyTexture requestReadyTexture(String path, PoseStack poseStack, float x, float y, float width, float height, Element requester) {
        if (path == null || path.isEmpty() || "unset".equals(path)) {
            return null;
        }
        ImageHandle handle = ImageAsyncHandler.INSTANCE.request(path, requester, false);
        if (handle == null || handle.state() != AbstractAsyncHandler.AsyncState.READY || handle.texture() == null) {
            ImageDrawer.drawPlaceholder(poseStack, x, y, width, height);
            return null;
        }
        Image.ITexture texture = handle.texture();
        int textureWidth = texture.getWidth();
        int textureHeight = texture.getHeight();
        TextureKey key = AuiServices.resources().locationOf(texture.getKey());
        if (textureWidth <= 0 || textureHeight <= 0 || key == null) {
            return null;
        }
        Base.resolveOffset(poseStack);
        return new ReadyTexture(key, textureWidth, textureHeight);
    }

    private static void innerBlit(PoseStack poseStack, TextureKey texture, float x, float y, float width, float height, float uTexture, float vTexture, int widthTexture, int heightTexture, int textureWidth, int textureHeight, boolean blur, boolean depthTest) {
        ImageDrawer.innerBlit(poseStack, texture, x, y, width, height, uTexture, vTexture, (float)widthTexture, (float)heightTexture, textureWidth, textureHeight, blur, depthTest);
    }

    private static void innerBlit(PoseStack poseStack, TextureKey texture, float x, float y, float width, float height, float uTexture, float vTexture, float widthTexture, float heightTexture, int textureWidth, int textureHeight, boolean blur, boolean depthTest) {
        Graph.endBatch();
        RenderHandle renderHandle = ImageDrawer.getRenderHandle(texture, blur, depthTest);
        float minU = uTexture / (float)textureWidth;
        float maxU = (uTexture + widthTexture) / (float)textureWidth;
        float minV = vTexture / (float)textureHeight;
        float maxV = (vTexture + heightTexture) / (float)textureHeight;
        TEXTURE_QUEUE.add(renderHandle, depthTest && Base.isDepthTestEnabled(), poseStack.m_85850_().m_252922_(), x, y, width, height, minU, minV, maxU, maxV);
    }

    private record RenderKey(TextureKey location, boolean blur, boolean depthTest) {
    }

    public record ObjectFitRect(float x, float y, float width, float height) {
    }

    private record ReadyTexture(TextureKey location, int width, int height) {
    }

    private record RepeatMode(boolean repeatX, boolean repeatY) {
    }

    public record GradientTile(float x, float y, float width, float height, float startX, float startY, float endX, float endY, boolean repeats) {
    }
}

