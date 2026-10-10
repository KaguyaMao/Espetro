/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.NativeImage$Format
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.render;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.resource.Font;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.TextureKey;
import com.sighs.apricityui.style.Text;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.nio.charset.StandardCharsets;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class FontDrawer {
    private static final String MODID = "apricityui";
    private static final String TARGET_PHYSICAL_RASTER_PROPERTY = "apricityui.fontRaster.targetPhysical";
    private static final String AA_MODE_PROPERTY = "apricityui.fontRaster.aaMode";
    private static final String COMPOSITE_MODE_PROPERTY = "apricityui.fontRaster.composite";
    private static final String FILTER_MODE_PROPERTY = "apricityui.fontRaster.filter";
    private static final String QUAD_MODE_PROPERTY = "apricityui.fontRaster.quadMode";
    private static final String FRACTIONAL_METRICS_PROPERTY = "apricityui.fontRaster.fractionalMetrics";
    private static final String ALPHA_GAMMA_PROPERTY = "apricityui.fontRaster.alphaGamma";
    private static final String ALPHA_SCALE_PROPERTY = "apricityui.fontRaster.alphaScale";
    private static final String ALPHA_CAP_PROPERTY = "apricityui.fontRaster.alphaCap";
    private static final String ALPHA_REMAP_PROPERTY = "apricityui.fontRaster.alphaRemap";
    private static final String RASTER_SOURCE_PROPERTY = "apricityui.fontRaster.source";
    private static final String STROKE_CONTROL_PROPERTY = "apricityui.fontRaster.strokeControl";
    private static final String FONT_RENDER_CONTEXT_PROPERTY = "apricityui.fontRaster.frc";
    private static final int FONT_ATLAS_SIZE = 2048;
    private static final int FONT_ATLAS_PADDING = 1;
    private static final Map<String, FontEntry> CACHE = new ConcurrentHashMap<String, FontEntry>();
    private static final Map<FontEntry, FontAtlas.Region> ATLAS_REGIONS = Collections.synchronizedMap(new IdentityHashMap());
    private static final Map<Boolean, FontAtlas> FONT_ATLASES = new ConcurrentHashMap<Boolean, FontAtlas>();
    private static final ThreadLocal<ArrayDeque<Double>> DOCUMENT_PIXEL_SCALE_STACK = ThreadLocal.withInitial(ArrayDeque::new);

    public static void pushDocumentPixelScale(double scale) {
        double safeScale = scale > 0.0 && Double.isFinite(scale) ? scale : 1.0;
        DOCUMENT_PIXEL_SCALE_STACK.get().push(safeScale);
    }

    public static void popDocumentPixelScale() {
        ArrayDeque<Double> stack = DOCUMENT_PIXEL_SCALE_STACK.get();
        if (!stack.isEmpty()) {
            stack.pop();
        }
    }

    public static void drawFont(PoseStack poseStack, Element element) {
        FontDrawer.drawFont(poseStack, Text.of(element), Rect.of((Element)element).position);
    }

    public static void drawFont(PoseStack poseStack, Text text, Position position) {
        FontDrawer.drawFont(poseStack, text, position, Double.NaN);
    }

    public static void drawFontOnBaseline(PoseStack poseStack, Text text, Position position, double baselineOffset) {
        FontDrawer.drawFont(poseStack, text, position, baselineOffset);
    }

    private static void drawFont(PoseStack poseStack, Text text, Position position, double baselineOffset) {
        String content = text.content;
        if (content == null || content.isEmpty()) {
            return;
        }
        double baseX = position.x;
        Position linePos = new Position(baseX, position.y);
        int firstNl = content.indexOf(10);
        if (firstNl < 0) {
            FontDrawer.drawLine(poseStack, text, content, linePos, baselineOffset);
            return;
        }
        int len = content.length();
        int start = 0;
        while (start <= len) {
            int nl = content.indexOf(10, start);
            if (nl < 0) {
                FontDrawer.drawLine(poseStack, text, start < len ? content.substring(start) : "", linePos, baselineOffset);
                break;
            }
            FontDrawer.drawLine(poseStack, text, content.substring(start, nl), linePos, baselineOffset);
            linePos.y += text.lineHeight;
            start = nl + 1;
        }
    }

    private static void drawLine(PoseStack poseStack, Text text, String content, Position position, double baselineOffset) {
        int cp;
        if (content == null || content.isEmpty()) {
            return;
        }
        if (Math.abs(text.letterSpacing) <= 1.0E-4) {
            FontDrawer.drawSingleRun(poseStack, text, content, position, baselineOffset);
            return;
        }
        if (!"unset".equals(text.fontFamily)) {
            FontDrawer.drawSingleRun(poseStack, text, content, position, baselineOffset);
            return;
        }
        double cursor = position.x;
        for (int i = 0; i < content.length(); i += Character.charCount(cp)) {
            cp = content.codePointAt(i);
            String glyph = new String(Character.toChars(cp));
            FontDrawer.drawSingleRun(poseStack, text, glyph, new Position(cursor, position.y), baselineOffset);
            cursor += Text.measureLine(text, glyph);
        }
    }

    private static void drawSingleRun(PoseStack poseStack, Text text, String content, Position position, double baselineOffset) {
        double pixelScale;
        float drawY;
        TextQuadMode quadMode;
        boolean baselineAnchored;
        float x = (float)position.x;
        float y = (float)position.y;
        boolean bl = baselineAnchored = !Double.isNaN(baselineOffset);
        if ("unset".equals(text.fontFamily)) {
            Position drawPosition = baselineAnchored ? new Position(position.x, position.y + baselineOffset - Text.renderedAscent(text)) : position;
            AuiServices.client().drawDefaultFont(poseStack, text, content, drawPosition);
            return;
        }
        RasterMode rasterMode = FontDrawer.resolveRasterMode(text);
        FontEntry entry = FontDrawer.textureEntry(text, content, rasterMode, quadMode = FontDrawer.resolveTextQuadMode());
        if (entry == null) {
            Position drawPosition = baselineAnchored ? new Position(position.x, position.y + baselineOffset - Text.renderedAscent(text)) : position;
            AuiServices.client().drawDefaultFont(poseStack, text, content, drawPosition);
            return;
        }
        float drawScale = (float)rasterMode.drawScale();
        float drawW = (float)entry.width() * drawScale;
        float drawH = (float)entry.height() * drawScale;
        RasterLayout layout = entry.rasterLayout();
        float drawX = x - (float)layout.pad() * drawScale;
        float f = drawY = baselineAnchored ? y + (float)baselineOffset - (float)layout.baselineTexel() * drawScale : y + (float)(text.lineHeight / 2.0) - entry.verticalAnchorTexel() * drawScale;
        if (quadMode.snapsAnyPhysicalEdge() && (pixelScale = rasterMode.pixelScale()) > 0.0 && Double.isFinite(pixelScale)) {
            if (quadMode.snapPhysicalX()) {
                drawX = (float)((double)Math.round((double)drawX * pixelScale) / pixelScale);
            }
            if (quadMode.snapPhysicalY()) {
                drawY = (float)((double)Math.round((double)drawY * pixelScale) / pixelScale);
            }
            if (quadMode.snapPhysicalWidth()) {
                drawW = (float)((double)Math.round((double)drawW * pixelScale) / pixelScale);
            }
            if (quadMode.snapPhysicalHeight()) {
                drawH = (float)((double)Math.round((double)drawH * pixelScale) / pixelScale);
            }
            if (quadMode.physicalRightInset() != 0.0) {
                drawW = (float)Math.max(0.0, (double)drawW - quadMode.physicalRightInset() / pixelScale);
            }
        }
        if (quadMode.hasRuntimeRightFracCutoff() && FontDrawer.drawRuntimeRightFracCutoff(poseStack, text, content, position, rasterMode, entry, quadMode, drawX, drawY, drawW, drawH)) {
            return;
        }
        if (quadMode.hasRightEdgeCrop()) {
            pixelScale = rasterMode.pixelScale();
            float croppedDrawW = drawW;
            if (pixelScale > 0.0 && Double.isFinite(pixelScale)) {
                croppedDrawW = (float)Math.max(0.0, (double)drawW - quadMode.physicalRightCropTexels() / pixelScale);
            }
            FontDrawer.drawEntryWithUvWindow(poseStack, entry, drawX, drawY, croppedDrawW, drawH, true, (float)quadMode.uvLeftOffsetTexels(), (float)quadMode.uvTopOffsetTexels(), (float)Math.max(0.0, (double)entry.width() - quadMode.physicalRightCropTexels() - quadMode.uvRightInsetTexels()), (float)((double)entry.height() - quadMode.uvBottomInsetTexels()));
        } else if (quadMode.hasUvWindowOffset()) {
            FontDrawer.drawEntryWithUvWindow(poseStack, entry, drawX, drawY, drawW, drawH, true, (float)quadMode.uvLeftOffsetTexels(), (float)quadMode.uvTopOffsetTexels(), (float)((double)entry.width() - quadMode.uvRightInsetTexels()), (float)((double)entry.height() - quadMode.uvBottomInsetTexels()));
        } else if (quadMode.hasUvInset()) {
            FontDrawer.drawEntryWithUvInset(poseStack, entry, drawX, drawY, drawW, drawH, true, (float)quadMode.uvRightInsetTexels(), (float)quadMode.uvBottomInsetTexels());
        } else {
            FontDrawer.drawEntry(poseStack, entry, drawX, drawY, drawW, drawH, true);
        }
    }

    private static void drawEntry(PoseStack poseStack, FontEntry entry, float x, float y, float width, float height, boolean blur) {
        FontAtlas.Region region = ATLAS_REGIONS.get(entry);
        if (region == null) {
            ImageDrawer.draw(poseStack, entry.location(), x, y, width, height, blur);
            return;
        }
        ImageDrawer.drawWithUvWindow(poseStack, region.location(), x, y, width, height, blur, region.textureWidth(), region.textureHeight(), region.x(), region.y(), entry.width(), entry.height());
    }

    private static void drawEntryWithUvInset(PoseStack poseStack, FontEntry entry, float x, float y, float width, float height, boolean blur, float rightTexelInset, float bottomTexelInset) {
        float sampleWidth = Math.max(0.0f, (float)entry.width() - Math.max(0.0f, rightTexelInset));
        float sampleHeight = Math.max(0.0f, (float)entry.height() - Math.max(0.0f, bottomTexelInset));
        FontDrawer.drawEntryWithUvWindow(poseStack, entry, x, y, width, height, blur, 0.0f, 0.0f, sampleWidth, sampleHeight);
    }

    private static void drawEntryWithUvWindow(PoseStack poseStack, FontEntry entry, float x, float y, float width, float height, boolean blur, float uTexel, float vTexel, float widthTexels, float heightTexels) {
        FontAtlas.Region region = ATLAS_REGIONS.get(entry);
        if (region == null) {
            ImageDrawer.drawWithUvWindow(poseStack, entry.location(), x, y, width, height, blur, entry.width(), entry.height(), uTexel, vTexel, widthTexels, heightTexels);
            return;
        }
        ImageDrawer.drawWithUvWindow(poseStack, region.location(), x, y, width, height, blur, region.textureWidth(), region.textureHeight(), (float)region.x() + uTexel, (float)region.y() + vTexel, widthTexels, heightTexels);
    }

    private static FontEntry textureEntry(Text text, String content, RasterMode rasterMode, TextQuadMode quadMode) {
        String key = FontDrawer.toCacheKey(text, content, rasterMode, quadMode);
        return CACHE.computeIfAbsent(key, ignored -> FontDrawer.rebuildTextureEntry(text, content, key, rasterMode, quadMode));
    }

    private static boolean drawRuntimeRightFracCutoff(PoseStack poseStack, Text text, String content, Position position, RasterMode rasterMode, FontEntry entry, TextQuadMode quadMode, float drawX, float drawY, float drawW, float drawH) {
        boolean apply;
        TextureStats stats = entry.textureStats();
        double pixelScale = rasterMode.pixelScale();
        if (stats == null || !stats.hasInk() || entry.width() <= 0 || entry.height() <= 0 || pixelScale <= 0.0 || !Double.isFinite(pixelScale)) {
            return false;
        }
        double physicalScaleX = (double)drawW * pixelScale / (double)entry.width();
        double physicalInkRight = (double)drawX * pixelScale + (double)(stats.minX() + stats.inkWidth()) * physicalScaleX;
        double rightFrac = physicalInkRight - Math.floor(physicalInkRight);
        int cutoffColumns = quadMode.runtimeSourceRightCutoffColumns(text, stats, physicalInkRight, rightFrac);
        int sourceRightExclusive = stats.minX() + stats.inkWidth() - cutoffColumns;
        boolean long12pxSource = quadMode.runtimeLong12pxSourceCutoff(text, stats);
        boolean bl = apply = cutoffColumns > 0 && sourceRightExclusive > 0 && sourceRightExclusive < entry.width();
        if (!apply) {
            return false;
        }
        TextQuadMode actionMode = quadMode.runtimeTextureModeForCutoffColumns(cutoffColumns);
        if (actionMode != quadMode) {
            FontEntry actionEntry = FontDrawer.textureEntry(text, content, rasterMode, actionMode);
            if (actionEntry == null) {
                return false;
            }
            FontDrawer.drawEntry(poseStack, actionEntry, drawX, drawY, drawW, drawH, true);
            return true;
        }
        float widthTexels = sourceRightExclusive;
        float croppedDrawW = (float)Math.max(0.0, (double)(drawW * (widthTexels / (float)entry.width())));
        FontDrawer.drawEntryWithUvWindow(poseStack, entry, drawX, drawY, croppedDrawW, drawH, true, 0.0f, 0.0f, widthTexels, entry.height());
        return true;
    }

    private static RasterMode resolveRasterMode(Text text) {
        if (!FontDrawer.isTargetPhysicalRasterEnabled()) {
            double scale = text.renderedFontSize() / (double)Font.getBaseFontSize();
            return new RasterMode(Font.getBaseFontSize(), scale <= 1.0E-6 ? 1.0 : scale, 1.0, false);
        }
        double pixelScale = FontDrawer.currentDocumentPixelScale();
        double rasterFontSize = Math.max(1.0, text.renderedFontSize() * pixelScale);
        return new RasterMode(rasterFontSize, 1.0 / pixelScale, pixelScale, true);
    }

    private static boolean isTargetPhysicalRasterEnabled() {
        if (Boolean.getBoolean(TARGET_PHYSICAL_RASTER_PROPERTY)) {
            return true;
        }
        String env = System.getenv("APRICITYUI_FONT_RASTER_TARGET_PHYSICAL");
        if (env == null || env.isBlank()) {
            return false;
        }
        String normalized = env.trim().toLowerCase(Locale.ROOT);
        return normalized.equals("1") || normalized.equals("true") || normalized.equals("yes") || normalized.equals("on");
    }

    private static double currentDocumentPixelScale() {
        ArrayDeque<Double> stack = DOCUMENT_PIXEL_SCALE_STACK.get();
        if (stack.isEmpty()) {
            return 1.0;
        }
        Double scale = stack.peek();
        return scale != null && scale > 0.0 && Double.isFinite(scale) ? scale : 1.0;
    }

    private static String toCacheKey(Text text, String content, RasterMode rasterMode, TextQuadMode quadMode) {
        String raw = text.content;
        String rasterKey = "|raster=" + rasterMode.cacheKey() + "|comp=" + FontDrawer.resolveTextCompositeMode(text).cacheKey() + "|filter=" + FontDrawer.resolveTextureFilterMode().cacheKey() + "|quadTexture=" + quadMode.textureCacheKey();
        if (Objects.equals(raw, content)) {
            return text.toKey() + rasterKey;
        }
        return text.toKey() + "|" + (content == null ? "" : content) + rasterKey;
    }

    private static FontEntry rebuildTextureEntry(Text text, String content, String cacheKey, RasterMode rasterMode, TextQuadMode quadMode) {
        List<Font.FontRun> runs;
        String fontKey = text.fontFamily;
        int fontStyle = 0;
        if (text.isBold()) {
            fontStyle |= 1;
        }
        if (text.isOblique()) {
            fontStyle |= 2;
        }
        if ((runs = Font.planFontRuns(fontKey, fontStyle, (float)rasterMode.rasterFontSize(), content)).isEmpty()) {
            return null;
        }
        TextAntialiasMode aaMode = FontDrawer.resolveTextAntialiasMode();
        FractionalMetricsMode fractionalMetricsMode = FontDrawer.resolveFractionalMetricsMode();
        AlphaGammaMode alphaGammaMode = FontDrawer.resolveAlphaGammaMode();
        AlphaScaleMode alphaScaleMode = FontDrawer.resolveAlphaScaleMode();
        AlphaCapMode alphaCapMode = FontDrawer.resolveAlphaCapMode();
        AlphaRemapMode alphaRemapMode = FontDrawer.resolveAlphaRemapMode();
        GlyphRasterSourceMode sourceMode = FontDrawer.resolveGlyphRasterSourceMode();
        StrokeControlMode strokeControlMode = FontDrawer.resolveStrokeControlMode();
        FontRenderContextMode frcMode = FontDrawer.resolveFontRenderContextMode();
        TextCompositeMode compositeMode = FontDrawer.resolveTextCompositeMode(text);
        TextureFilterMode filterMode = FontDrawer.resolveTextureFilterMode();
        com.sighs.apricityui.parser.Color color = text.color;
        com.sighs.apricityui.parser.Color strokeColor = text.strokeColor;
        int stroke = Math.max(0, (int)Math.ceil(text.strokeWidth));
        String drawText = content == null ? "" : content;
        try {
            BufferedImage tmp = new BufferedImage(1, 1, 2);
            Graphics2D g2d = tmp.createGraphics();
            LineMetrics metrics = FontDrawer.measureRuns(g2d, runs);
            g2d.dispose();
            double rasterLetterSpacing = rasterMode.targetPhysical() ? text.letterSpacing * rasterMode.pixelScale() : text.letterSpacing / rasterMode.drawScale();
            int textW = Math.max(1, FontDrawer.measureRunsWidth(runs, rasterLetterSpacing, frcMode));
            int textH = Math.max(1, metrics.height());
            int pad = 2 + stroke;
            int imgW = textW + pad * 2;
            int imgH = textH + pad * 2;
            BufferedImage img = new BufferedImage(imgW, imgH, 2);
            Graphics2D g = img.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaMode.hint());
            if (fractionalMetricsMode.hint() != null) {
                g.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetricsMode.hint());
            }
            if (strokeControlMode.hint() != null) {
                g.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, strokeControlMode.hint());
            }
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (compositeMode.hasOpaqueRasterBackground()) {
                g.setComposite(AlphaComposite.Src);
                g.setColor(new Color(compositeMode.backgroundR(), compositeMode.backgroundG(), compositeMode.backgroundB()));
                g.fillRect(0, 0, imgW, imgH);
            } else {
                g.setComposite(AlphaComposite.Clear);
                g.fillRect(0, 0, imgW, imgH);
            }
            g.setComposite(AlphaComposite.SrcOver);
            int baseline = pad + metrics.ascent();
            if (sourceMode == GlyphRasterSourceMode.OUTLINE_COVERAGE_4X || sourceMode == GlyphRasterSourceMode.OUTLINE_COVERAGE_4X_ROW_CLAMP) {
                FontDrawer.drawRunsOutlineCoverage(img, g, runs, pad, baseline, rasterLetterSpacing, stroke, strokeColor, color, frcMode, sourceMode.coverageSamples(), sourceMode.rowClamped());
            } else if (sourceMode == GlyphRasterSourceMode.OVERSAMPLE_2X) {
                FontDrawer.drawRunsOversampled(g, runs, pad, baseline, rasterLetterSpacing, stroke, strokeColor, color, sourceMode, frcMode, compositeMode, aaMode, fractionalMetricsMode, strokeControlMode, imgW, imgH);
            } else {
                if (stroke > 0) {
                    g.setColor(new Color(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getA()));
                    for (int ox = -stroke; ox <= stroke; ++ox) {
                        for (int oy = -stroke; oy <= stroke; ++oy) {
                            if (ox == 0 && oy == 0 || ox * ox + oy * oy > stroke * stroke) continue;
                            FontDrawer.drawRuns(g, runs, pad + ox, baseline + oy, rasterLetterSpacing, sourceMode, frcMode);
                        }
                    }
                }
                g.setColor(new Color(color.getR(), color.getG(), color.getB(), color.getA()));
                FontDrawer.drawRuns(g, runs, pad, baseline, rasterLetterSpacing, sourceMode, frcMode);
            }
            TextureStats glyphTextureStats = FontDrawer.computeTextureStats(img);
            FontDrawer.drawTextDecorations(g, text, pad, baseline, textW, metrics, rasterMode);
            g.dispose();
            if (!compositeMode.hasOpaqueRasterBackground()) {
                FontDrawer.applyAlphaGamma(img, alphaGammaMode);
                FontDrawer.applyAlphaScale(img, alphaScaleMode);
                FontDrawer.applyAlphaCap(img, alphaCapMode);
                FontDrawer.applyAlphaRemap(img, alphaRemapMode);
            }
            FontDrawer.applyRightEdgeAlphaAttenuation(img, quadMode);
            FontDrawer.applySourceRightCutoff(img, quadMode);
            img = FontDrawer.applyTextureGutter(img, quadMode);
            imgW = img.getWidth();
            imgH = img.getHeight();
            TextureStats textureStats = FontDrawer.computeTextureStats(img);
            int[] pixels = FontDrawer.readPixels(img);
            NativeImage nativeImg = new NativeImage(NativeImage.Format.RGBA, imgW, imgH, true);
            for (int y = 0; y < imgH; ++y) {
                for (int x = 0; x < imgW; ++x) {
                    int argb = pixels[y * imgW + x];
                    if (compositeMode.solidBackground()) {
                        argb = FontDrawer.uncomposeSolidBackground(argb, color, compositeMode);
                    }
                    AuiServices.render().setImagePixel(nativeImg, x, y, FontDrawer.argbToAbgr(argb));
                }
            }
            FontAtlas.Region atlasRegion = FontDrawer.fontAtlasFor(filterMode.linear()).add(nativeImg);
            if (atlasRegion != null) {
                nativeImg.close();
                FontEntry atlasEntry = new FontEntry(atlasRegion.location(), null, null, imgW, imgH, textureStats, new RasterLayout(pad, metrics.height(), FontDrawer.glyphAnchor(glyphTextureStats, pad, metrics.height()), pad + metrics.ascent()));
                ATLAS_REGIONS.put(atlasEntry, atlasRegion);
                return atlasEntry;
            }
            Object texture = AuiServices.render().createDynamicTexture("apricityui:font/" + UUID.nameUUIDFromBytes(cacheKey.getBytes(StandardCharsets.UTF_8)), nativeImg, filterMode.linear());
            TextureKey location = TextureKey.of("font/" + UUID.nameUUIDFromBytes(cacheKey.getBytes(StandardCharsets.UTF_8)));
            AuiServices.render().registerTexture(texture, AuiServices.resources().textureLocation(location));
            return new FontEntry(location, nativeImg, texture, imgW, imgH, textureStats, new RasterLayout(pad, metrics.height(), FontDrawer.glyphAnchor(glyphTextureStats, pad, metrics.height()), pad + metrics.ascent()));
        }
        catch (Exception e) {
            return null;
        }
    }

    private static TextureStats computeTextureStats(BufferedImage img) {
        if (img == null) {
            return TextureStats.empty();
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = FontDrawer.readPixels(img);
        int ink = 0;
        int minX = width;
        int minY = height;
        int maxX = -1;
        int maxY = -1;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int argb = pixels[y * width + x];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0) continue;
                ++ink;
                minX = Math.min(minX, x);
                minY = Math.min(minY, y);
                maxX = Math.max(maxX, x);
                maxY = Math.max(maxY, y);
            }
        }
        return new TextureStats(ink, ink == 0 ? -1 : minX, ink == 0 ? -1 : minY, ink == 0 ? 0 : maxX - minX + 1, ink == 0 ? 0 : maxY - minY + 1);
    }

    private static int[] readPixels(BufferedImage image) {
        DataBuffer dataBuffer = image.getRaster().getDataBuffer();
        if (dataBuffer instanceof DataBufferInt) {
            DataBufferInt pixels = (DataBufferInt)dataBuffer;
            return pixels.getData();
        }
        return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
    }

    private static float glyphAnchor(TextureStats stats, int pad, int lineHeight) {
        if (stats != null && stats.hasInk()) {
            return (float)stats.minY() + (float)stats.inkHeight() / 2.0f;
        }
        return (float)pad + (float)lineHeight / 2.0f;
    }

    private static void applyAlphaGamma(BufferedImage img, AlphaGammaMode mode) {
        if (img == null || mode == null || !mode.enabled()) {
            return;
        }
        double gamma = mode.gamma();
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = FontDrawer.readPixels(img);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                double normalized;
                int transformed;
                int index = y * width + x;
                int argb = pixels[index];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0 || alpha >= 255 || (transformed = Math.max(0, Math.min(255, (int)Math.round(Math.pow(normalized = (double)alpha / 255.0, gamma) * 255.0)))) == alpha) continue;
                pixels[index] = transformed << 24 | argb & 0xFFFFFF;
            }
        }
    }

    private static void applyAlphaScale(BufferedImage img, AlphaScaleMode mode) {
        if (img == null || mode == null || !mode.enabled()) {
            return;
        }
        double scale = mode.scale();
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = FontDrawer.readPixels(img);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int transformed;
                int index = y * width + x;
                int argb = pixels[index];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0 || (transformed = Math.max(0, Math.min(255, (int)Math.round((double)alpha * scale)))) == alpha) continue;
                pixels[index] = transformed << 24 | argb & 0xFFFFFF;
            }
        }
    }

    private static void applyAlphaCap(BufferedImage img, AlphaCapMode mode) {
        if (img == null || mode == null || !mode.enabled()) {
            return;
        }
        int cap = mode.cap();
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = FontDrawer.readPixels(img);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int index = y * width + x;
                int argb = pixels[index];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0 || alpha <= cap) continue;
                pixels[index] = cap << 24 | argb & 0xFFFFFF;
            }
        }
    }

    private static void applyAlphaRemap(BufferedImage img, AlphaRemapMode mode) {
        if (img == null || mode == null || !mode.enabled()) {
            return;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int[] pixels = FontDrawer.readPixels(img);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int transformed;
                int index = y * width + x;
                int argb = pixels[index];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0 || (transformed = mode.map(alpha)) == alpha) continue;
                pixels[index] = transformed << 24 | argb & 0xFFFFFF;
            }
        }
    }

    private static BufferedImage applyTextureGutter(BufferedImage img, TextQuadMode quadMode) {
        if (img == null || quadMode == null || !quadMode.hasTextureGutter()) {
            return img;
        }
        int right = Math.max(0, (int)Math.ceil(quadMode.textureRightGutter()));
        int bottom = Math.max(0, (int)Math.ceil(quadMode.textureBottomGutter()));
        if (right == 0 && bottom == 0) {
            return img;
        }
        BufferedImage expanded = new BufferedImage(img.getWidth() + right, img.getHeight() + bottom, 2);
        Graphics2D g = expanded.createGraphics();
        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, expanded.getWidth(), expanded.getHeight());
        g.setComposite(AlphaComposite.Src);
        g.drawImage((Image)img, 0, 0, null);
        g.dispose();
        return expanded;
    }

    private static void applyRightEdgeAlphaAttenuation(BufferedImage img, TextQuadMode quadMode) {
        if (img == null || quadMode == null || quadMode.rightEdgeAttenuateColumns() <= 0) {
            return;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int minX = width;
        int maxX = -1;
        int[] pixels = FontDrawer.readPixels(img);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int alpha = pixels[y * width + x] >>> 24 & 0xFF;
                if (alpha <= 0) continue;
                if (x < minX) {
                    minX = x;
                }
                if (x <= maxX) continue;
                maxX = x;
            }
        }
        if (maxX < minX) {
            return;
        }
        int columns = Math.min(quadMode.rightEdgeAttenuateColumns(), maxX - minX + 1);
        for (int i = 0; i < columns; ++i) {
            int x = maxX - i;
            double scale = switch (i) {
                case 0 -> 0.0;
                case 1 -> 0.25;
                default -> 0.5;
            };
            for (int y = 0; y < height; ++y) {
                int index = y * width + x;
                int argb = pixels[index];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0) continue;
                int transformed = Math.max(0, Math.min(255, (int)Math.round((double)alpha * scale)));
                pixels[index] = transformed << 24 | argb & 0xFFFFFF;
            }
        }
    }

    private static void applySourceRightCutoff(BufferedImage img, TextQuadMode quadMode) {
        if (img == null || quadMode == null || quadMode.sourceRightCutoffColumns() <= 0) {
            return;
        }
        int width = img.getWidth();
        int height = img.getHeight();
        int maxX = -1;
        int[] pixels = FontDrawer.readPixels(img);
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                int alpha = pixels[y * width + x] >>> 24 & 0xFF;
                if (alpha <= 0 || x <= maxX) continue;
                maxX = x;
            }
        }
        if (maxX < 0) {
            return;
        }
        int columns = Math.min(quadMode.sourceRightCutoffColumns(), maxX + 1);
        int firstCutoffX = maxX - columns + 1;
        for (int y = 0; y < height; ++y) {
            for (int x = firstCutoffX; x <= maxX; ++x) {
                int index = y * width + x;
                int argb = pixels[index];
                int alpha = argb >>> 24 & 0xFF;
                if (alpha <= 0) continue;
                pixels[index] = argb & 0xFFFFFF;
            }
        }
    }

    public static void clearCache() {
        for (FontEntry entry : CACHE.values()) {
            if (entry == null) continue;
            try {
                if (entry.dynamicTexture() == null) continue;
                AuiServices.render().closeTexture(entry.dynamicTexture());
            }
            catch (Exception exception) {}
        }
        CACHE.clear();
        ATLAS_REGIONS.clear();
        for (FontAtlas atlas : FONT_ATLASES.values()) {
            if (atlas == null) continue;
            atlas.close();
        }
        FONT_ATLASES.clear();
    }

    private static FontAtlas fontAtlasFor(boolean linear) {
        return FONT_ATLASES.computeIfAbsent(linear, FontAtlas::new);
    }

    private static int argbToAbgr(int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        return a << 24 | b << 16 | g << 8 | r;
    }

    private static int uncomposeSolidBackground(int argb, com.sighs.apricityui.parser.Color textColor, TextCompositeMode compositeMode) {
        int pr = argb >>> 16 & 0xFF;
        int pg = argb >>> 8 & 0xFF;
        int pb = argb & 0xFF;
        int tr = textColor.getR();
        int tg = textColor.getG();
        int tb = textColor.getB();
        int alpha = Math.max(FontDrawer.solveCoverage(pr, tr, compositeMode.backgroundR()), Math.max(FontDrawer.solveCoverage(pg, tg, compositeMode.backgroundG()), FontDrawer.solveCoverage(pb, tb, compositeMode.backgroundB())));
        if (alpha <= 0) {
            return 0;
        }
        alpha = Math.min(alpha, textColor.getA());
        return alpha << 24 | tr << 16 | tg << 8 | tb;
    }

    private static int solveCoverage(int observed, int text, int background) {
        int denominator = background - text;
        if (denominator == 0) {
            return observed == background ? 0 : 255;
        }
        double coverage = (double)(background - observed) / (double)denominator;
        if (!Double.isFinite(coverage)) {
            return 0;
        }
        return Math.max(0, Math.min(255, (int)Math.round(coverage * 255.0)));
    }

    private static double measureAwtWidthWithSpacing(java.awt.Font font, String content, double spacing, FontRenderContext renderContext) {
        int cp;
        if (content == null || content.isEmpty() || font == null || renderContext == null) {
            return 0.0;
        }
        if (Math.abs(spacing) <= 1.0E-6) {
            return font.getStringBounds(content, renderContext).getWidth();
        }
        double width = 0.0;
        int count = 0;
        for (int i = 0; i < content.length(); i += Character.charCount(cp)) {
            cp = content.codePointAt(i);
            String glyph = new String(Character.toChars(cp));
            width += font.getStringBounds(glyph, renderContext).getWidth();
            ++count;
        }
        if (count > 1) {
            width += spacing * (double)(count - 1);
        }
        return Math.max(0.0, width);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int measureRunsWidth(List<Font.FontRun> runs, double spacing, FontRenderContextMode frcMode) {
        BufferedImage tmp = new BufferedImage(1, 1, 2);
        Graphics2D g = tmp.createGraphics();
        try {
            FontRenderContext renderContext = FontDrawer.fontRenderContext(g, frcMode);
            int n = Math.max(0, (int)Math.ceil(Font.measureFontRuns(runs, renderContext, spacing, false)));
            return n;
        }
        finally {
            g.dispose();
        }
    }

    private static void drawStringWithSpacing(Graphics2D g, FontMetrics fm, String content, double x, int y, double spacing) {
        int cp;
        double cursor = x;
        for (int i = 0; i < content.length(); i += Character.charCount(cp)) {
            cp = content.codePointAt(i);
            String glyph = new String(Character.toChars(cp));
            g.drawString(glyph, (float)cursor, (float)y);
            cursor += (double)fm.stringWidth(glyph) + spacing;
        }
    }

    private static FontRenderContext fontRenderContext(Graphics2D g, FontRenderContextMode mode) {
        if (mode == null || mode == FontRenderContextMode.GRAPHICS) {
            return g.getFontRenderContext();
        }
        return new FontRenderContext((AffineTransform)null, mode.antialiasHint(), mode.fractionalMetricsHint());
    }

    private static void drawGlyphVectorWithSpacing(Graphics2D g, java.awt.Font font, String content, double x, int y, double spacing, FontRenderContextMode frcMode) {
        int cp;
        double cursor = x;
        FontRenderContext frc = FontDrawer.fontRenderContext(g, frcMode);
        for (int i = 0; i < content.length(); i += Character.charCount(cp)) {
            cp = content.codePointAt(i);
            String glyph = new String(Character.toChars(cp));
            GlyphVector glyphVector = font.createGlyphVector(frc, glyph);
            g.fill(glyphVector.getOutline((float)cursor, y));
            cursor += font.getStringBounds(glyph, frc).getWidth() + spacing;
        }
    }

    private static void drawRuns(Graphics2D g, List<Font.FontRun> runs, double x, int baselineY, double spacing, GlyphRasterSourceMode sourceMode, FontRenderContextMode frcMode) {
        double cursor = x;
        for (Font.FontRun run : runs) {
            if (run == null || run.font() == null || run.text() == null || run.text().isEmpty()) continue;
            g.setFont(run.font());
            FontRenderContext frc = FontDrawer.fontRenderContext(g, frcMode);
            if (sourceMode == GlyphRasterSourceMode.GLYPH_VECTOR) {
                g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (Math.abs(spacing) <= 1.0E-6) {
                    GlyphVector glyphVector = run.font().createGlyphVector(frc, run.text());
                    g.fill(glyphVector.getOutline((float)cursor, baselineY));
                    cursor += run.font().getStringBounds(run.text(), frc).getWidth();
                    continue;
                }
                FontDrawer.drawGlyphVectorWithSpacing(g, run.font(), run.text(), cursor, baselineY, spacing, frcMode);
                cursor += FontDrawer.measureAwtWidthWithSpacing(run.font(), run.text(), spacing, frc);
                continue;
            }
            if (Math.abs(spacing) <= 1.0E-6) {
                g.drawString(run.text(), (float)cursor, (float)baselineY);
                cursor += run.font().getStringBounds(run.text(), frc).getWidth();
                continue;
            }
            FontDrawer.drawStringWithSpacing(g, g.getFontMetrics(), run.text(), cursor, baselineY, spacing);
            cursor += FontDrawer.measureAwtWidthWithSpacing(run.font(), run.text(), spacing, frc);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static void drawRunsOversampled(Graphics2D target, List<Font.FontRun> runs, int pad, int baseline, double spacing, int stroke, com.sighs.apricityui.parser.Color strokeColor, com.sighs.apricityui.parser.Color color, GlyphRasterSourceMode sourceMode, FontRenderContextMode frcMode, TextCompositeMode compositeMode, TextAntialiasMode aaMode, FractionalMetricsMode fractionalMetricsMode, StrokeControlMode strokeControlMode, int targetWidth, int targetHeight) {
        int factor = sourceMode.oversampleFactor();
        if (factor <= 1) {
            return;
        }
        int highWidth = Math.max(1, targetWidth * factor);
        int highHeight = Math.max(1, targetHeight * factor);
        BufferedImage high = new BufferedImage(highWidth, highHeight, 2);
        Graphics2D hg = high.createGraphics();
        try {
            hg.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aaMode.hint());
            if (fractionalMetricsMode.hint() != null) {
                hg.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fractionalMetricsMode.hint());
            }
            if (strokeControlMode.hint() != null) {
                hg.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, strokeControlMode.hint());
            }
            hg.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            if (compositeMode.hasOpaqueRasterBackground()) {
                hg.setComposite(AlphaComposite.Src);
                hg.setColor(new Color(compositeMode.backgroundR(), compositeMode.backgroundG(), compositeMode.backgroundB()));
                hg.fillRect(0, 0, highWidth, highHeight);
            } else {
                hg.setComposite(AlphaComposite.Clear);
                hg.fillRect(0, 0, highWidth, highHeight);
            }
            hg.setComposite(AlphaComposite.SrcOver);
            List<Font.FontRun> highRuns = FontDrawer.scaleRuns(runs, factor);
            int highPad = pad * factor;
            int highBaseline = baseline * factor;
            double highSpacing = spacing * (double)factor;
            int highStroke = stroke * factor;
            if (highStroke > 0) {
                hg.setColor(new Color(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getA()));
                for (int ox = -highStroke; ox <= highStroke; ++ox) {
                    for (int oy = -highStroke; oy <= highStroke; ++oy) {
                        if (ox == 0 && oy == 0 || ox * ox + oy * oy > highStroke * highStroke) continue;
                        FontDrawer.drawRuns(hg, highRuns, highPad + ox, highBaseline + oy, highSpacing, GlyphRasterSourceMode.DRAW_STRING, frcMode);
                    }
                }
            }
            hg.setColor(new Color(color.getR(), color.getG(), color.getB(), color.getA()));
            FontDrawer.drawRuns(hg, highRuns, highPad, highBaseline, highSpacing, GlyphRasterSourceMode.DRAW_STRING, frcMode);
        }
        finally {
            hg.dispose();
        }
        target.setComposite(AlphaComposite.Src);
        target.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        target.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        target.drawImage(high, 0, 0, targetWidth, targetHeight, null);
        target.setComposite(AlphaComposite.SrcOver);
    }

    private static void drawRunsOutlineCoverage(BufferedImage target, Graphics2D metricsGraphics, List<Font.FontRun> runs, double x, int baselineY, double spacing, int stroke, com.sighs.apricityui.parser.Color strokeColor, com.sighs.apricityui.parser.Color color, FontRenderContextMode frcMode, int samples, boolean rowClamped) {
        boolean[] allowedRows;
        int safeSamples = Math.max(1, samples);
        boolean[] blArray = allowedRows = rowClamped ? FontDrawer.baselineInkRows(target.getWidth(), target.getHeight(), metricsGraphics, runs, x, baselineY, spacing, stroke, strokeColor, color, frcMode) : null;
        if (stroke > 0) {
            for (int ox = -stroke; ox <= stroke; ++ox) {
                for (int oy = -stroke; oy <= stroke; ++oy) {
                    if (ox == 0 && oy == 0 || ox * ox + oy * oy > stroke * stroke) continue;
                    Shape strokeShape = FontDrawer.buildRunsOutline(metricsGraphics, runs, x + (double)ox, baselineY + oy, spacing, frcMode);
                    FontDrawer.rasterizeOutlineCoverage(target, strokeShape, strokeColor, safeSamples, allowedRows);
                }
            }
        }
        Shape fillShape = FontDrawer.buildRunsOutline(metricsGraphics, runs, x, baselineY, spacing, frcMode);
        FontDrawer.rasterizeOutlineCoverage(target, fillShape, color, safeSamples, allowedRows);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static boolean[] baselineInkRows(int width, int height, Graphics2D metricsGraphics, List<Font.FontRun> runs, double x, int baselineY, double spacing, int stroke, com.sighs.apricityui.parser.Color strokeColor, com.sighs.apricityui.parser.Color color, FontRenderContextMode frcMode) {
        boolean[] rows = new boolean[Math.max(0, height)];
        if (width <= 0 || height <= 0) {
            return rows;
        }
        BufferedImage baseline = new BufferedImage(width, height, 2);
        Graphics2D bg = baseline.createGraphics();
        try {
            FontDrawer.copyTextRenderingHints(metricsGraphics, bg);
            bg.setComposite(AlphaComposite.Clear);
            bg.fillRect(0, 0, width, height);
            bg.setComposite(AlphaComposite.SrcOver);
            if (stroke > 0) {
                bg.setColor(new Color(strokeColor.getR(), strokeColor.getG(), strokeColor.getB(), strokeColor.getA()));
                for (int ox = -stroke; ox <= stroke; ++ox) {
                    for (int oy = -stroke; oy <= stroke; ++oy) {
                        if (ox == 0 && oy == 0 || ox * ox + oy * oy > stroke * stroke) continue;
                        FontDrawer.drawRuns(bg, runs, x + (double)ox, baselineY + oy, spacing, GlyphRasterSourceMode.DRAW_STRING, frcMode);
                    }
                }
            }
            bg.setColor(new Color(color.getR(), color.getG(), color.getB(), color.getA()));
            FontDrawer.drawRuns(bg, runs, x, baselineY, spacing, GlyphRasterSourceMode.DRAW_STRING, frcMode);
        }
        finally {
            bg.dispose();
        }
        int[] pixels = FontDrawer.readPixels(baseline);
        block5: for (int y = 0; y < height; ++y) {
            for (int px = 0; px < width; ++px) {
                if ((pixels[y * width + px] >>> 24 & 0xFF) <= 0) continue;
                rows[y] = true;
                continue block5;
            }
        }
        return rows;
    }

    private static void copyTextRenderingHints(Graphics2D from, Graphics2D to) {
        if (from == null || to == null) {
            return;
        }
        Object aa = from.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
        Object fm = from.getRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS);
        Object stroke = from.getRenderingHint(RenderingHints.KEY_STROKE_CONTROL);
        Object rendering = from.getRenderingHint(RenderingHints.KEY_RENDERING);
        if (aa != null) {
            to.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, aa);
        }
        if (fm != null) {
            to.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, fm);
        }
        if (stroke != null) {
            to.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, stroke);
        }
        if (rendering != null) {
            to.setRenderingHint(RenderingHints.KEY_RENDERING, rendering);
        }
    }

    private static Shape buildRunsOutline(Graphics2D g, List<Font.FontRun> runs, double x, int baselineY, double spacing, FontRenderContextMode frcMode) {
        Area area = new Area();
        double cursor = x;
        for (Font.FontRun run : runs) {
            int cp;
            if (run == null || run.font() == null || run.text() == null || run.text().isEmpty()) continue;
            g.setFont(run.font());
            FontRenderContext frc = FontDrawer.fontRenderContext(g, frcMode);
            if (Math.abs(spacing) <= 1.0E-6) {
                GlyphVector glyphVector = run.font().createGlyphVector(frc, run.text());
                area.add(new Area(glyphVector.getOutline((float)cursor, baselineY)));
                cursor += run.font().getStringBounds(run.text(), frc).getWidth();
                continue;
            }
            for (int i = 0; i < run.text().length(); i += Character.charCount(cp)) {
                cp = run.text().codePointAt(i);
                String glyph = new String(Character.toChars(cp));
                GlyphVector glyphVector = run.font().createGlyphVector(frc, glyph);
                area.add(new Area(glyphVector.getOutline((float)cursor, baselineY)));
                cursor += run.font().getStringBounds(glyph, frc).getWidth() + spacing;
            }
        }
        return area;
    }

    private static void rasterizeOutlineCoverage(BufferedImage target, Shape shape, com.sighs.apricityui.parser.Color color, int samples, boolean[] allowedRows) {
        if (target == null || shape == null || color == null || color.getA() <= 0) {
            return;
        }
        Rectangle bounds = shape.getBounds();
        int minX = Math.max(0, bounds.x - 1);
        int minY = Math.max(0, bounds.y - 1);
        int maxX = Math.min(target.getWidth(), bounds.x + bounds.width + 2);
        int maxY = Math.min(target.getHeight(), bounds.y + bounds.height + 2);
        int total = samples * samples;
        int targetWidth = target.getWidth();
        int[] pixels = FontDrawer.readPixels(target);
        for (int y = minY; y < maxY; ++y) {
            if (allowedRows != null && (y < 0 || y >= allowedRows.length || !allowedRows[y])) continue;
            for (int x = minX; x < maxX; ++x) {
                int covered = 0;
                for (int sy = 0; sy < samples; ++sy) {
                    double sampleY = (double)y + ((double)sy + 0.5) / (double)samples;
                    for (int sx = 0; sx < samples; ++sx) {
                        double sampleX = (double)x + ((double)sx + 0.5) / (double)samples;
                        if (!shape.contains(sampleX, sampleY)) continue;
                        ++covered;
                    }
                }
                if (covered <= 0) continue;
                int sourceAlpha = FontDrawer.clamp255((int)Math.round((double)color.getA() * ((double)covered / (double)total)));
                int index = y * targetWidth + x;
                pixels[index] = FontDrawer.sourceOver(pixels[index], color, sourceAlpha);
            }
        }
    }

    private static int sourceOver(int dstArgb, com.sighs.apricityui.parser.Color color, int sourceAlpha) {
        double srcA = (double)FontDrawer.clamp255(sourceAlpha) / 255.0;
        if (srcA <= 0.0) {
            return dstArgb;
        }
        double dstA = (double)(dstArgb >>> 24 & 0xFF) / 255.0;
        int dstR = dstArgb >>> 16 & 0xFF;
        int dstG = dstArgb >>> 8 & 0xFF;
        int dstB = dstArgb & 0xFF;
        double outA = srcA + dstA * (1.0 - srcA);
        if (outA <= 1.0E-9) {
            return 0;
        }
        int outR = FontDrawer.clamp255((int)Math.round(((double)color.getR() * srcA + (double)dstR * dstA * (1.0 - srcA)) / outA));
        int outG = FontDrawer.clamp255((int)Math.round(((double)color.getG() * srcA + (double)dstG * dstA * (1.0 - srcA)) / outA));
        int outB = FontDrawer.clamp255((int)Math.round(((double)color.getB() * srcA + (double)dstB * dstA * (1.0 - srcA)) / outA));
        int outAlpha = FontDrawer.clamp255((int)Math.round(outA * 255.0));
        return outAlpha << 24 | outR << 16 | outG << 8 | outB;
    }

    private static int clamp255(int value) {
        return Math.max(0, Math.min(255, value));
    }

    private static List<Font.FontRun> scaleRuns(List<Font.FontRun> runs, int factor) {
        if (runs == null || runs.isEmpty() || factor <= 1) {
            return runs;
        }
        ArrayList<Font.FontRun> scaled = new ArrayList<Font.FontRun>(runs.size());
        for (Font.FontRun run : runs) {
            if (run == null || run.font() == null) continue;
            java.awt.Font font = run.font().deriveFont(run.font().getStyle(), run.font().getSize2D() * (float)factor);
            scaled.add(new Font.FontRun(font, run.text()));
        }
        return List.copyOf(scaled);
    }

    private static LineMetrics measureRuns(Graphics2D g, List<Font.FontRun> runs) {
        int ascent = 0;
        int descent = 0;
        int leading = 0;
        float underlineOffset = 1.0f;
        float underlineThickness = 1.0f;
        float strikethroughOffset = -1.0f;
        float strikethroughThickness = 1.0f;
        boolean measuredDecoration = false;
        for (Font.FontRun run : runs) {
            if (run == null || run.font() == null) continue;
            g.setFont(run.font());
            FontMetrics fm = g.getFontMetrics();
            ascent = Math.max(ascent, fm.getAscent());
            descent = Math.max(descent, fm.getDescent());
            leading = Math.max(leading, fm.getLeading());
            if (measuredDecoration) continue;
            java.awt.font.LineMetrics lineMetrics = run.font().getLineMetrics("Hg", g.getFontRenderContext());
            underlineOffset = lineMetrics.getUnderlineOffset();
            underlineThickness = lineMetrics.getUnderlineThickness();
            strikethroughOffset = lineMetrics.getStrikethroughOffset();
            strikethroughThickness = lineMetrics.getStrikethroughThickness();
            measuredDecoration = true;
        }
        return new LineMetrics(ascent, descent, leading, Math.max(1, ascent + descent + leading), underlineOffset, underlineThickness, strikethroughOffset, strikethroughThickness);
    }

    private static void drawTextDecorations(Graphics2D g, Text text, int x, int baseline, int width, LineMetrics metrics, RasterMode rasterMode) {
        if (text == null || width <= 0 || !text.isUnderlined() && !text.isStrikethrough()) {
            return;
        }
        g.setColor(new Color(text.color.getR(), text.color.getG(), text.color.getB(), text.color.getA()));
        if (text.isUnderlined()) {
            double drawScale = rasterMode == null ? 1.0 : Math.max(1.0E-6, rasterMode.drawScale());
            double naturalCssThickness = (double)metrics.underlineThickness() * drawScale;
            double thickness = Math.ceil(Math.max(1.0, naturalCssThickness)) / drawScale;
            double offset = Math.max(1.0, naturalCssThickness) / drawScale;
            g.fill(new Rectangle2D.Double(x, (double)baseline + offset, width, thickness));
        }
        if (text.isStrikethrough()) {
            double thickness = Math.max(1.0, (double)metrics.strikethroughThickness());
            g.fill(new Rectangle2D.Double(x, (float)baseline + metrics.strikethroughOffset(), width, thickness));
        }
    }

    private static TextAntialiasMode resolveTextAntialiasMode() {
        String normalized;
        String mode = System.getProperty(AA_MODE_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_AA_MODE");
        }
        if (mode == null || mode.isBlank()) {
            return TextAntialiasMode.ON;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "lcd-hrgb", "lcd_hrgb", "lcd" -> TextAntialiasMode.LCD_HRGB;
            case "off", "false", "0", "none" -> TextAntialiasMode.OFF;
            case "gasp" -> TextAntialiasMode.GASP;
            case "on", "true", "1", "aa" -> TextAntialiasMode.ON;
            default -> TextAntialiasMode.ON;
        };
    }

    private static TextCompositeMode resolveTextCompositeMode(Text text) {
        String normalized;
        String mode = System.getProperty(COMPOSITE_MODE_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_COMPOSITE");
        }
        if (mode == null || mode.isBlank()) {
            return TextCompositeMode.TRANSPARENT;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "opaque-white", "opaque_white", "white", "background-white", "background_white" -> TextCompositeMode.OPAQUE_WHITE;
            case "solid-bg", "solid_bg", "solid-background", "solid_background" -> TextCompositeMode.solidBackground(text == null ? null : text.rasterBackgroundColor);
            case "transparent", "alpha", "default" -> TextCompositeMode.TRANSPARENT;
            default -> TextCompositeMode.TRANSPARENT;
        };
    }

    private static FractionalMetricsMode resolveFractionalMetricsMode() {
        String normalized;
        String mode = System.getProperty(FRACTIONAL_METRICS_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_FRACTIONAL_METRICS");
        }
        if (mode == null || mode.isBlank()) {
            return FractionalMetricsMode.ON;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "on", "true", "1", "yes" -> FractionalMetricsMode.ON;
            case "off", "false", "0", "no" -> FractionalMetricsMode.OFF;
            case "default", "unset" -> FractionalMetricsMode.DEFAULT;
            default -> FractionalMetricsMode.DEFAULT;
        };
    }

    private static AlphaGammaMode resolveAlphaGammaMode() {
        String value = System.getProperty(ALPHA_GAMMA_PROPERTY);
        if (value == null || value.isBlank()) {
            value = System.getenv("APRICITYUI_FONT_RASTER_ALPHA_GAMMA");
        }
        if (value == null || value.isBlank()) {
            return AlphaGammaMode.DEFAULT;
        }
        try {
            double gamma = Double.parseDouble(value.trim());
            if (!Double.isFinite(gamma) || gamma <= 0.0) {
                return AlphaGammaMode.DEFAULT;
            }
            gamma = Math.max(0.1, Math.min(5.0, gamma));
            return new AlphaGammaMode("gamma-" + Math.round(gamma * 1000.0), gamma);
        }
        catch (NumberFormatException ignored) {
            return AlphaGammaMode.DEFAULT;
        }
    }

    private static AlphaScaleMode resolveAlphaScaleMode() {
        String value = System.getProperty(ALPHA_SCALE_PROPERTY);
        if (value == null || value.isBlank()) {
            value = System.getenv("APRICITYUI_FONT_RASTER_ALPHA_SCALE");
        }
        if (value == null || value.isBlank()) {
            return AlphaScaleMode.DEFAULT;
        }
        try {
            double scale = Double.parseDouble(value.trim());
            if (!Double.isFinite(scale) || scale <= 0.0) {
                return AlphaScaleMode.DEFAULT;
            }
            scale = Math.max(0.1, Math.min(2.0, scale));
            return new AlphaScaleMode("scale-" + Math.round(scale * 1000.0), scale);
        }
        catch (NumberFormatException ignored) {
            return AlphaScaleMode.DEFAULT;
        }
    }

    private static AlphaCapMode resolveAlphaCapMode() {
        String value = System.getProperty(ALPHA_CAP_PROPERTY);
        if (value == null || value.isBlank()) {
            value = System.getenv("APRICITYUI_FONT_RASTER_ALPHA_CAP");
        }
        if (value == null || value.isBlank()) {
            return AlphaCapMode.DEFAULT;
        }
        try {
            int cap = Integer.parseInt(value.trim());
            if (cap <= 0 || cap >= 255) {
                return AlphaCapMode.DEFAULT;
            }
            cap = Math.max(1, Math.min(254, cap));
            return new AlphaCapMode("cap-" + cap, cap);
        }
        catch (NumberFormatException ignored) {
            return AlphaCapMode.DEFAULT;
        }
    }

    private static AlphaRemapMode resolveAlphaRemapMode() {
        String normalized;
        String value = System.getProperty(ALPHA_REMAP_PROPERTY);
        if (value == null || value.isBlank()) {
            value = System.getenv("APRICITYUI_FONT_RASTER_ALPHA_REMAP");
        }
        if (value == null || value.isBlank()) {
            return AlphaRemapMode.DEFAULT;
        }
        return switch (normalized = value.trim().toLowerCase(Locale.ROOT)) {
            case "off", "false", "0", "default", "none" -> AlphaRemapMode.DEFAULT;
            case "soft-v1", "soft_v1", "cdf-soft-v1", "cdf_soft_v1" -> AlphaRemapMode.fromPoints("soft-v1", new int[][]{{0, 0}, {32, 32}, {64, 60}, {96, 82}, {128, 104}, {160, 128}, {192, 154}, {224, 188}, {240, 224}, {255, 248}});
            default -> AlphaRemapMode.fromSpec(normalized);
        };
    }

    private static GlyphRasterSourceMode resolveGlyphRasterSourceMode() {
        String normalized;
        String mode = System.getProperty(RASTER_SOURCE_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_SOURCE");
        }
        if (mode == null || mode.isBlank()) {
            return GlyphRasterSourceMode.DRAW_STRING;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "glyph-vector", "glyph_vector", "outline", "shape" -> GlyphRasterSourceMode.GLYPH_VECTOR;
            case "outline-coverage-4x-row-clamp", "outline_coverage_4x_row_clamp", "coverage-4x-row-clamp", "coverage_4x_row_clamp", "row-clamp" -> GlyphRasterSourceMode.OUTLINE_COVERAGE_4X_ROW_CLAMP;
            case "outline-coverage-4x", "outline_coverage_4x", "coverage-4x", "coverage_4x" -> GlyphRasterSourceMode.OUTLINE_COVERAGE_4X;
            case "oversample-2x", "oversample_2x", "oversample", "supersample-2x", "supersample_2x" -> GlyphRasterSourceMode.OVERSAMPLE_2X;
            case "draw-string", "draw_string", "string", "default" -> GlyphRasterSourceMode.DRAW_STRING;
            default -> GlyphRasterSourceMode.DRAW_STRING;
        };
    }

    private static StrokeControlMode resolveStrokeControlMode() {
        String normalized;
        String mode = System.getProperty(STROKE_CONTROL_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_STROKE_CONTROL");
        }
        if (mode == null || mode.isBlank()) {
            return StrokeControlMode.DEFAULT;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "normalize", "normalized", "normalise", "normalised" -> StrokeControlMode.NORMALIZE;
            case "pure", "precision" -> StrokeControlMode.PURE;
            case "default", "unset" -> StrokeControlMode.DEFAULT;
            default -> StrokeControlMode.DEFAULT;
        };
    }

    private static FontRenderContextMode resolveFontRenderContextMode() {
        String normalized;
        String mode = System.getProperty(FONT_RENDER_CONTEXT_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_FRC");
        }
        if (mode == null || mode.isBlank()) {
            return FontRenderContextMode.AA_ON_FM_ON;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "aa-on-fm-on", "on-on", "antialias-on-fractional-on" -> FontRenderContextMode.AA_ON_FM_ON;
            case "aa-on-fm-off", "on-off", "antialias-on-fractional-off" -> FontRenderContextMode.AA_ON_FM_OFF;
            case "aa-off-fm-off", "off-off", "antialias-off-fractional-off" -> FontRenderContextMode.AA_OFF_FM_OFF;
            case "graphics", "default", "unset" -> FontRenderContextMode.GRAPHICS;
            default -> FontRenderContextMode.GRAPHICS;
        };
    }

    private static TextureFilterMode resolveTextureFilterMode() {
        String normalized;
        String mode = System.getProperty(FILTER_MODE_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_FILTER");
        }
        if (mode == null || mode.isBlank()) {
            return TextureFilterMode.LINEAR;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "nearest", "nearest-neighbor", "nearest_neighbor", "point" -> TextureFilterMode.NEAREST;
            case "linear", "smooth", "default" -> TextureFilterMode.LINEAR;
            default -> TextureFilterMode.LINEAR;
        };
    }

    private static TextQuadMode resolveTextQuadMode() {
        String normalized;
        String mode = System.getProperty(QUAD_MODE_PROPERTY);
        if (mode == null || mode.isBlank()) {
            mode = System.getenv("APRICITYUI_FONT_RASTER_QUAD_MODE");
        }
        if (mode == null || mode.isBlank()) {
            return TextQuadMode.DEFAULT;
        }
        return switch (normalized = mode.trim().toLowerCase(Locale.ROOT)) {
            case "snap-physical", "physical-snap", "snap_physical", "pixel-snap", "pixel_snap" -> TextQuadMode.SNAP_PHYSICAL;
            case "snap-physical-y", "physical-snap-y", "snap_physical_y", "pixel-snap-y", "pixel_snap_y" -> TextQuadMode.SNAP_PHYSICAL_Y;
            case "snap-physical-y-right-inset-1", "physical-snap-y-right-inset-1", "snap_physical_y_right_inset_1", "pixel-snap-y-right-inset-1", "pixel_snap_y_right_inset_1" -> TextQuadMode.SNAP_PHYSICAL_Y_RIGHT_INSET_1;
            case "snap-physical-y-uv-half-open", "physical-snap-y-uv-half-open", "snap_physical_y_uv_half_open", "pixel-snap-y-uv-half-open", "pixel_snap_y_uv_half_open" -> TextQuadMode.SNAP_PHYSICAL_Y_UV_HALF_OPEN;
            case "snap-physical-y-texture-gutter-1", "physical-snap-y-texture-gutter-1", "snap_physical_y_texture_gutter_1", "pixel-snap-y-texture-gutter-1", "pixel_snap_y_texture_gutter_1" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1;
            case "snap-physical-y-texture-gutter-1-right-inset-1", "physical-snap-y-texture-gutter-1-right-inset-1", "snap_physical_y_texture_gutter_1_right_inset_1", "pixel-snap-y-texture-gutter-1-right-inset-1", "pixel_snap_y_texture_gutter_1_right_inset_1" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RIGHT_INSET_1;
            case "snap-physical-y-texture-gutter-1-edge-attenuate-2", "physical-snap-y-texture-gutter-1-edge-attenuate-2", "snap_physical_y_texture_gutter_1_edge_attenuate_2", "pixel-snap-y-texture-gutter-1-edge-attenuate-2", "pixel_snap_y_texture_gutter_1_edge_attenuate_2" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_EDGE_ATTENUATE_2;
            case "snap-physical-y-texture-gutter-1-uv-shift-right-half", "physical-snap-y-texture-gutter-1-uv-shift-right-half", "snap_physical_y_texture_gutter_1_uv_shift_right_half", "pixel-snap-y-texture-gutter-1-uv-shift-right-half", "pixel_snap_y_texture_gutter_1_uv_shift_right_half" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_UV_SHIFT_RIGHT_HALF;
            case "snap-physical-y-texture-gutter-1-right-crop-1", "physical-snap-y-texture-gutter-1-right-crop-1", "snap_physical_y_texture_gutter_1_right_crop_1", "pixel-snap-y-texture-gutter-1-right-crop-1", "pixel_snap_y_texture_gutter_1_right_crop_1" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RIGHT_CROP_1;
            case "snap-physical-y-texture-gutter-1-right-crop-2", "physical-snap-y-texture-gutter-1-right-crop-2", "snap_physical_y_texture_gutter_1_right_crop_2", "pixel-snap-y-texture-gutter-1-right-crop-2", "pixel_snap_y_texture_gutter_1_right_crop_2" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RIGHT_CROP_2;
            case "snap-physical-y-texture-gutter-1-source-cutoff-1", "physical-snap-y-texture-gutter-1-source-cutoff-1", "snap_physical_y_texture_gutter_1_source_cutoff_1", "pixel-snap-y-texture-gutter-1-source-cutoff-1", "pixel_snap_y_texture_gutter_1_source_cutoff_1" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_SOURCE_CUTOFF_1;
            case "snap-physical-y-texture-gutter-1-source-cutoff-2", "physical-snap-y-texture-gutter-1-source-cutoff-2", "snap_physical_y_texture_gutter_1_source_cutoff_2", "pixel-snap-y-texture-gutter-1-source-cutoff-2", "pixel_snap_y_texture_gutter_1_source_cutoff_2" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_SOURCE_CUTOFF_2;
            case "snap-physical-y-texture-gutter-1-runtime-right-frac-cutoff-0p75", "physical-snap-y-texture-gutter-1-runtime-right-frac-cutoff-0p75", "snap_physical_y_texture_gutter_1_runtime_right_frac_cutoff_0p75", "pixel-snap-y-texture-gutter-1-runtime-right-frac-cutoff-0p75", "pixel_snap_y_texture_gutter_1_runtime_right_frac_cutoff_0p75" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_CUTOFF_0P75;
            case "snap-physical-y-texture-gutter-1-runtime-right-frac-or-long-12px-source-cutoff", "physical-snap-y-texture-gutter-1-runtime-right-frac-or-long-12px-source-cutoff", "snap_physical_y_texture_gutter_1_runtime_right_frac_or_long_12px_source_cutoff", "pixel-snap-y-texture-gutter-1-runtime-right-frac-or-long-12px-source-cutoff", "pixel_snap_y_texture_gutter_1_runtime_right_frac_or_long_12px_source_cutoff" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_OR_LONG_12PX_SOURCE_CUTOFF;
            case "snap-physical-y-texture-gutter-1-runtime-12px-physical-phase", "physical-snap-y-texture-gutter-1-runtime-12px-physical-phase", "snap_physical_y_texture_gutter_1_runtime_12px_physical_phase", "pixel-snap-y-texture-gutter-1-runtime-12px-physical-phase", "pixel_snap_y_texture_gutter_1_runtime_12px_physical_phase", "runtime12pxphysicalphasev1" -> TextQuadMode.SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_12PX_PHYSICAL_PHASE;
            case "default", "none" -> TextQuadMode.DEFAULT;
            default -> TextQuadMode.DEFAULT;
        };
    }

    private record RasterMode(double rasterFontSize, double drawScale, double pixelScale, boolean targetPhysical) {
        String cacheKey() {
            return (this.targetPhysical ? "physical" : "base") + ":" + Math.round(this.rasterFontSize * 1000.0) + ":" + Math.round(this.drawScale * 1000000.0) + ":" + Math.round(this.pixelScale * 1000000.0) + ":aa=" + FontDrawer.resolveTextAntialiasMode().cacheKey() + ":fm=" + FontDrawer.resolveFractionalMetricsMode().cacheKey() + ":ag=" + FontDrawer.resolveAlphaGammaMode().cacheKey() + ":as=" + FontDrawer.resolveAlphaScaleMode().cacheKey() + ":ac=" + FontDrawer.resolveAlphaCapMode().cacheKey() + ":ar=" + FontDrawer.resolveAlphaRemapMode().cacheKey() + ":source=" + FontDrawer.resolveGlyphRasterSourceMode().cacheKey() + ":sc=" + FontDrawer.resolveStrokeControlMode().cacheKey() + ":frc=" + FontDrawer.resolveFontRenderContextMode().cacheKey();
        }
    }

    private record TextQuadMode(String cacheKey, boolean snapPhysicalX, boolean snapPhysicalY, boolean snapPhysicalWidth, boolean snapPhysicalHeight, double physicalRightInset, double uvRightInsetTexels, double uvBottomInsetTexels, double textureRightGutter, double textureBottomGutter, int rightEdgeAttenuateColumns, double uvLeftOffsetTexels, double uvTopOffsetTexels, double physicalRightCropTexels, int sourceRightCutoffColumns) {
        private static final TextQuadMode DEFAULT = new TextQuadMode("default", false, false, false, false, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL = new TextQuadMode("snap-physical", true, true, true, true, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y = new TextQuadMode("snap-physical-y", false, true, false, true, 0.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_RIGHT_INSET_1 = new TextQuadMode("snap-physical-y-right-inset-1", false, true, false, true, 1.0, 0.0, 0.0, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_UV_HALF_OPEN = new TextQuadMode("snap-physical-y-uv-half-open", false, true, false, true, 0.0, 0.5, 0.5, 0.0, 0.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1 = new TextQuadMode("snap-physical-y-texture-gutter-1", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RIGHT_INSET_1 = new TextQuadMode("snap-physical-y-texture-gutter-1-right-inset-1", false, true, false, true, 1.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_EDGE_ATTENUATE_2 = new TextQuadMode("snap-physical-y-texture-gutter-1-edge-attenuate-2", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 2, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_UV_SHIFT_RIGHT_HALF = new TextQuadMode("snap-physical-y-texture-gutter-1-uv-shift-right-half", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.5, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RIGHT_CROP_1 = new TextQuadMode("snap-physical-y-texture-gutter-1-right-crop-1", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 1.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RIGHT_CROP_2 = new TextQuadMode("snap-physical-y-texture-gutter-1-right-crop-2", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 2.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_SOURCE_CUTOFF_1 = new TextQuadMode("snap-physical-y-texture-gutter-1-source-cutoff-1", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 1);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_SOURCE_CUTOFF_2 = new TextQuadMode("snap-physical-y-texture-gutter-1-source-cutoff-2", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 2);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_CUTOFF_0P75 = new TextQuadMode("snap-physical-y-texture-gutter-1-runtime-right-frac-cutoff-0p75", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_OR_LONG_12PX_SOURCE_CUTOFF = new TextQuadMode("snap-physical-y-texture-gutter-1-runtime-right-frac-or-long-12px-source-cutoff", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 0);
        private static final TextQuadMode SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_12PX_PHYSICAL_PHASE = new TextQuadMode("snap-physical-y-texture-gutter-1-runtime-12px-physical-phase", false, true, false, true, 0.0, 0.0, 0.0, 1.0, 1.0, 0, 0.0, 0.0, 0.0, 0);

        private boolean snapsAnyPhysicalEdge() {
            return this.snapPhysicalX || this.snapPhysicalY || this.snapPhysicalWidth || this.snapPhysicalHeight || this.physicalRightInset != 0.0;
        }

        private boolean hasUvInset() {
            return this.uvRightInsetTexels != 0.0 || this.uvBottomInsetTexels != 0.0;
        }

        private boolean hasUvWindowOffset() {
            return this.uvLeftOffsetTexels != 0.0 || this.uvTopOffsetTexels != 0.0;
        }

        private boolean hasRightEdgeCrop() {
            return this.physicalRightCropTexels != 0.0;
        }

        private boolean hasTextureGutter() {
            return this.textureRightGutter != 0.0 || this.textureBottomGutter != 0.0;
        }

        private boolean hasRuntimeRightFracCutoff() {
            return this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_CUTOFF_0P75 || this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_OR_LONG_12PX_SOURCE_CUTOFF || this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_12PX_PHYSICAL_PHASE;
        }

        private double runtimeRightFracThreshold() {
            return this.hasRuntimeRightFracCutoff() ? 0.75 : 0.0;
        }

        private boolean runtimeLong12pxSourceCutoff(Text text, TextureStats stats) {
            return this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_OR_LONG_12PX_SOURCE_CUTOFF && text != null && stats != null && text.fontSize <= 12.0 && stats.inkWidth() >= 300;
        }

        private int runtimeSourceRightCutoffColumns(Text text, TextureStats stats, double physicalInkRight, double rightFrac) {
            if (this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_12PX_PHYSICAL_PHASE) {
                if (!this.runtimeStrictApply(text, stats, rightFrac)) {
                    return 0;
                }
                if (text != null && stats != null && text.fontSize <= 12.0 && stats.inkWidth() >= 340) {
                    boolean evenFloor;
                    double physicalFloor = Math.floor(physicalInkRight);
                    boolean bl = evenFloor = (long)physicalFloor % 2L == 0L;
                    if (rightFrac > 0.18 && rightFrac < 0.82 && (evenFloor || rightFrac < 0.25)) {
                        return 2;
                    }
                }
                return 1;
            }
            if (this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_CUTOFF_0P75) {
                return rightFrac <= this.runtimeRightFracThreshold() ? 1 : 0;
            }
            if (this == SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_RIGHT_FRAC_OR_LONG_12PX_SOURCE_CUTOFF) {
                return rightFrac <= this.runtimeRightFracThreshold() || this.runtimeLong12pxSourceCutoff(text, stats) ? 1 : 0;
            }
            return 0;
        }

        private boolean runtimeStrictApply(Text text, TextureStats stats, double rightFrac) {
            if (text == null || stats == null) {
                return false;
            }
            boolean is12px = text.fontSize <= 12.0;
            boolean long12pxSource = is12px && stats.inkWidth() >= 360;
            boolean fractional12pxEdge = is12px && rightFrac <= 0.75;
            boolean narrow13pxBrowserLikeApply = text.fontSize == 13.0 && stats.inkWidth() <= 95 && rightFrac <= 0.75;
            return long12pxSource || fractional12pxEdge || narrow13pxBrowserLikeApply;
        }

        private TextQuadMode runtimeTextureModeForCutoffColumns(int cutoffColumns) {
            if (this != SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_RUNTIME_12PX_PHYSICAL_PHASE) {
                return this;
            }
            return switch (cutoffColumns) {
                case 1 -> SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_SOURCE_CUTOFF_1;
                case 2 -> SNAP_PHYSICAL_Y_TEXTURE_GUTTER_1_SOURCE_CUTOFF_2;
                default -> this;
            };
        }

        private String textureCacheKey() {
            return this.hasTextureGutter() || this.rightEdgeAttenuateColumns > 0 || this.hasUvWindowOffset() || this.sourceRightCutoffColumns > 0 ? this.cacheKey : "default";
        }
    }

    public record FontEntry(TextureKey location, NativeImage nativeImage, Object dynamicTexture, int width, int height, TextureStats textureStats, RasterLayout rasterLayout) {
        float verticalAnchorTexel() {
            return this.rasterLayout.glyphAnchorTexel();
        }
    }

    private record RasterLayout(int pad, int lineHeight, float glyphAnchorTexel, int baselineTexel) {
    }

    private static final class FontAtlas {
        private final boolean linear;
        private final TextureKey location;
        private NativeImage pixels;
        private Object texture;
        private boolean registered;
        private boolean disabled;
        private int cursorX;
        private int cursorY;
        private int rowHeight;

        private FontAtlas(boolean linear) {
            this.linear = linear;
            this.location = TextureKey.of(linear ? "font/atlas-linear" : "font/atlas-nearest");
        }

        private synchronized Region add(NativeImage source) {
            if (this.disabled || source == null) {
                return null;
            }
            int width = source.m_84982_();
            int height = source.m_85084_();
            int packedWidth = width + 2;
            int packedHeight = height + 2;
            if (width <= 0 || height <= 0 || packedWidth > 2048 || packedHeight > 2048) {
                return null;
            }
            if (this.cursorX + packedWidth > 2048) {
                this.cursorX = 0;
                this.cursorY += this.rowHeight;
                this.rowHeight = 0;
            }
            if (this.cursorY + packedHeight > 2048) {
                return null;
            }
            try {
                this.ensureTexture();
                int x = this.cursorX + 1;
                int y = this.cursorY + 1;
                source.m_260930_(this.pixels, 0, 0, x, y, width, height, false, false);
                this.copyPadding(source, x, y, width, height);
                AuiServices.render().uploadTextureRegion(this.texture, this.pixels, this.cursorX, this.cursorY, packedWidth, packedHeight, this.linear);
                this.cursorX += packedWidth;
                this.rowHeight = Math.max(this.rowHeight, packedHeight);
                return new Region(this.location, x, y, width, height, 2048, 2048);
            }
            catch (RuntimeException exception) {
                this.disable();
                return null;
            }
        }

        private void ensureTexture() {
            if (this.texture != null) {
                return;
            }
            NativeImage image = new NativeImage(NativeImage.Format.RGBA, 2048, 2048, true);
            Object created = AuiServices.render().createDynamicTexture("apricityui:font/atlas-" + (this.linear ? "linear" : "nearest"), image, this.linear);
            this.pixels = image;
            this.texture = created;
            try {
                AuiServices.render().registerTexture(created, AuiServices.resources().textureLocation(this.location));
                this.registered = true;
            }
            catch (RuntimeException exception) {
                this.texture = null;
                this.pixels = null;
                AuiServices.render().closeTexture(created);
                throw exception;
            }
        }

        private void copyPadding(NativeImage source, int x, int y, int width, int height) {
            source.m_260930_(this.pixels, 0, 0, x - 1, y, 1, height, false, false);
            source.m_260930_(this.pixels, width - 1, 0, x + width, y, 1, height, false, false);
            source.m_260930_(this.pixels, 0, 0, x, y - 1, width, 1, false, false);
            source.m_260930_(this.pixels, 0, height - 1, x, y + height, width, 1, false, false);
            source.m_260930_(this.pixels, 0, 0, x - 1, y - 1, 1, 1, false, false);
            source.m_260930_(this.pixels, width - 1, 0, x + width, y - 1, 1, 1, false, false);
            source.m_260930_(this.pixels, 0, height - 1, x - 1, y + height, 1, 1, false, false);
            source.m_260930_(this.pixels, width - 1, height - 1, x + width, y + height, 1, 1, false, false);
        }

        private void disable() {
            this.disabled = true;
            this.close();
        }

        private synchronized void close() {
            if (this.texture == null) {
                return;
            }
            try {
                if (this.registered) {
                    AuiServices.render().releaseTexture(AuiServices.resources().textureLocation(this.location));
                } else {
                    AuiServices.render().closeTexture(this.texture);
                }
            }
            catch (Exception ignored) {
                try {
                    AuiServices.render().closeTexture(this.texture);
                }
                catch (Exception exception) {
                    // empty catch block
                }
            }
            finally {
                this.texture = null;
                this.pixels = null;
                this.registered = false;
            }
        }

        private record Region(TextureKey location, int x, int y, int width, int height, int textureWidth, int textureHeight) {
        }
    }

    private record TextureStats(int ink, int minX, int minY, int inkWidth, int inkHeight) {
        private static TextureStats empty() {
            return new TextureStats(0, -1, -1, 0, 0);
        }

        boolean hasInk() {
            return this.ink > 0;
        }
    }

    private record TextCompositeMode(String cacheKey, boolean opaqueWhite, boolean solidBackground, int backgroundR, int backgroundG, int backgroundB) {
        private static final TextCompositeMode TRANSPARENT = new TextCompositeMode("transparent", false, false, 0, 0, 0);
        private static final TextCompositeMode OPAQUE_WHITE = new TextCompositeMode("opaque-white", true, false, 255, 255, 255);

        boolean hasOpaqueRasterBackground() {
            return this.opaqueWhite || this.solidBackground;
        }

        private static TextCompositeMode solidBackground(String rawColor) {
            int color = com.sighs.apricityui.parser.Color.parse(rawColor == null || rawColor.isBlank() || "unset".equalsIgnoreCase(rawColor) ? "#ffffff" : rawColor);
            int r = color >>> 16 & 0xFF;
            int g = color >>> 8 & 0xFF;
            int b = color & 0xFF;
            return new TextCompositeMode("solid-bg-" + r + "-" + g + "-" + b, false, true, r, g, b);
        }
    }

    private record TextureFilterMode(String cacheKey, boolean linear) {
        private static final TextureFilterMode LINEAR = new TextureFilterMode("linear", true);
        private static final TextureFilterMode NEAREST = new TextureFilterMode("nearest", false);
    }

    private record TextAntialiasMode(String cacheKey, Object hint) {
        private static final TextAntialiasMode ON = new TextAntialiasMode("on", RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        private static final TextAntialiasMode LCD_HRGB = new TextAntialiasMode("lcd-hrgb", RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
        private static final TextAntialiasMode OFF = new TextAntialiasMode("off", RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        private static final TextAntialiasMode GASP = new TextAntialiasMode("gasp", RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
    }

    private record FractionalMetricsMode(String cacheKey, Object hint) {
        private static final FractionalMetricsMode DEFAULT = new FractionalMetricsMode("default", null);
        private static final FractionalMetricsMode ON = new FractionalMetricsMode("on", RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        private static final FractionalMetricsMode OFF = new FractionalMetricsMode("off", RenderingHints.VALUE_FRACTIONALMETRICS_OFF);
    }

    private record AlphaGammaMode(String cacheKey, double gamma) {
        private static final AlphaGammaMode DEFAULT = new AlphaGammaMode("default", 1.0);

        boolean enabled() {
            return Math.abs(this.gamma - 1.0) > 1.0E-6;
        }
    }

    private record AlphaScaleMode(String cacheKey, double scale) {
        private static final AlphaScaleMode DEFAULT = new AlphaScaleMode("default", 1.0);

        boolean enabled() {
            return Math.abs(this.scale - 1.0) > 1.0E-6;
        }
    }

    private record AlphaCapMode(String cacheKey, int cap) {
        private static final AlphaCapMode DEFAULT = new AlphaCapMode("default", 255);

        boolean enabled() {
            return this.cap < 255;
        }
    }

    private record AlphaRemapMode(String cacheKey, int[] table) {
        private static final AlphaRemapMode DEFAULT = new AlphaRemapMode("default", null);

        static AlphaRemapMode fromSpec(String spec) {
            try {
                String[] parts = spec.split(",");
                int[][] points = new int[parts.length][2];
                for (int i = 0; i < parts.length; ++i) {
                    String[] pair = parts[i].trim().split(":");
                    if (pair.length != 2) {
                        return DEFAULT;
                    }
                    points[i][0] = AlphaRemapMode.clamp255(Integer.parseInt(pair[0].trim()));
                    points[i][1] = AlphaRemapMode.clamp255(Integer.parseInt(pair[1].trim()));
                }
                return AlphaRemapMode.fromPoints("custom-" + Math.abs(spec.hashCode()), points);
            }
            catch (Exception ignored) {
                return DEFAULT;
            }
        }

        static AlphaRemapMode fromPoints(String cacheKey, int[][] points) {
            if (points == null || points.length < 2) {
                return DEFAULT;
            }
            Arrays.sort(points, Comparator.comparingInt(point -> point[0]));
            int[] table = new int[256];
            for (int i = 0; i < table.length; ++i) {
                table[i] = AlphaRemapMode.interpolate(points, i);
            }
            return new AlphaRemapMode(cacheKey, table);
        }

        private static int interpolate(int[][] points, int alpha) {
            if (alpha <= points[0][0]) {
                return points[0][1];
            }
            for (int i = 1; i < points.length; ++i) {
                int x0 = points[i - 1][0];
                int y0 = points[i - 1][1];
                int x1 = points[i][0];
                int y1 = points[i][1];
                if (alpha > x1) continue;
                if (x1 == x0) {
                    return y1;
                }
                double t = (double)(alpha - x0) / (double)(x1 - x0);
                return AlphaRemapMode.clamp255((int)Math.round((double)y0 + t * (double)(y1 - y0)));
            }
            return points[points.length - 1][1];
        }

        private static int clamp255(int value) {
            return Math.max(0, Math.min(255, value));
        }

        boolean enabled() {
            return this.table != null;
        }

        int map(int alpha) {
            if (this.table == null) {
                return alpha;
            }
            return this.table[AlphaRemapMode.clamp255(alpha)];
        }
    }

    private static enum GlyphRasterSourceMode {
        DRAW_STRING("draw-string"),
        GLYPH_VECTOR("glyph-vector"),
        OUTLINE_COVERAGE_4X("outline-coverage-4x"),
        OUTLINE_COVERAGE_4X_ROW_CLAMP("outline-coverage-4x-row-clamp"),
        OVERSAMPLE_2X("oversample-2x");

        private final String cacheKey;

        private GlyphRasterSourceMode(String cacheKey) {
            this.cacheKey = cacheKey;
        }

        String cacheKey() {
            return this.cacheKey;
        }

        int oversampleFactor() {
            return this == OVERSAMPLE_2X ? 2 : 1;
        }

        int coverageSamples() {
            return this == OUTLINE_COVERAGE_4X || this == OUTLINE_COVERAGE_4X_ROW_CLAMP ? 4 : 1;
        }

        boolean rowClamped() {
            return this == OUTLINE_COVERAGE_4X_ROW_CLAMP;
        }
    }

    private record StrokeControlMode(String cacheKey, Object hint) {
        private static final StrokeControlMode DEFAULT = new StrokeControlMode("default", null);
        private static final StrokeControlMode NORMALIZE = new StrokeControlMode("normalize", RenderingHints.VALUE_STROKE_NORMALIZE);
        private static final StrokeControlMode PURE = new StrokeControlMode("pure", RenderingHints.VALUE_STROKE_PURE);
    }

    private static enum FontRenderContextMode {
        GRAPHICS("graphics", null, null),
        AA_ON_FM_ON("aa-on-fm-on", RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_FRACTIONALMETRICS_ON),
        AA_ON_FM_OFF("aa-on-fm-off", RenderingHints.VALUE_TEXT_ANTIALIAS_ON, RenderingHints.VALUE_FRACTIONALMETRICS_OFF),
        AA_OFF_FM_OFF("aa-off-fm-off", RenderingHints.VALUE_TEXT_ANTIALIAS_OFF, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

        private final String cacheKey;
        private final Object antialiasHint;
        private final Object fractionalMetricsHint;

        private FontRenderContextMode(String cacheKey, Object antialiasHint, Object fractionalMetricsHint) {
            this.cacheKey = cacheKey;
            this.antialiasHint = antialiasHint;
            this.fractionalMetricsHint = fractionalMetricsHint;
        }

        String cacheKey() {
            return this.cacheKey;
        }

        Object antialiasHint() {
            return this.antialiasHint;
        }

        Object fractionalMetricsHint() {
            return this.fractionalMetricsHint;
        }
    }

    private record LineMetrics(int ascent, int descent, int leading, int height, float underlineOffset, float underlineThickness, float strikethroughOffset, float strikethroughThickness) {
    }
}

