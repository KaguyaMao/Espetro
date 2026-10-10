/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  com.mojang.blaze3d.platform.NativeImage$Format
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.Rect;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.TextureKey;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.Locale;
import java.util.UUID;
import java.util.function.Consumer;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.MemoryCacheImageOutputStream;

@ElementRegister(value="CANVAS")
public class Canvas
extends Element {
    public static final String TAG_NAME = "CANVAS";
    private static final int DEFAULT_WIDTH = 300;
    private static final int DEFAULT_HEIGHT = 150;
    private BufferedImage surface;
    private NativeImage nativeImage;
    private Object texture;
    protected TextureKey textureLocation;
    private boolean surfaceDirty = true;
    private int bitmapWidth = 300;
    private int bitmapHeight = 150;
    private final CanvasRenderingContext2D context2d = new CanvasRenderingContext2D(this);

    private static Object textureLocation(TextureKey key) {
        return AuiServices.resources().textureLocation(key);
    }

    public Canvas(Document document) {
        super(document, TAG_NAME);
        this.resizeSurface(this.bitmapWidth, this.bitmapHeight, false);
    }

    @Override
    protected void onInitFromDom(Element origin) {
        this.syncDimensionsFromAttributes(false);
    }

    @Override
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
        if ("width".equalsIgnoreCase(name) || "height".equalsIgnoreCase(name)) {
            this.syncDimensionsFromAttributes(true);
        }
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        if ("width".equalsIgnoreCase(name) || "height".equalsIgnoreCase(name)) {
            this.syncDimensionsFromAttributes(true);
        }
    }

    public CanvasRenderingContext2D getContext(String type) {
        if (type == null) {
            return null;
        }
        return "2d".equalsIgnoreCase(type) ? this.context2d : null;
    }

    public int getWidth() {
        return this.bitmapWidth;
    }

    public void setWidth(int width) {
        this.setAttribute("width", Integer.toString(width));
    }

    public int getHeight() {
        return this.bitmapHeight;
    }

    public void setHeight(int height) {
        this.setAttribute("height", Integer.toString(height));
    }

    public Size getIntrinsicSize() {
        return new Size(this.bitmapWidth, this.bitmapHeight);
    }

    public BufferedImage getSurface() {
        this.ensureSurface();
        return this.surface;
    }

    public void ensureSurface() {
        if (this.surface == null) {
            this.resizeSurface(this.bitmapWidth, this.bitmapHeight, false);
        }
    }

    public void renderOperation(Consumer<Graphics2D> action) {
        if (action == null) {
            return;
        }
        this.ensureSurface();
        Graphics2D g = this.surface.createGraphics();
        try {
            Canvas.applyGraphicsDefaults(g);
            action.accept(g);
        }
        finally {
            g.dispose();
        }
        this.surfaceDirty = true;
        if (this.document != null) {
            this.document.markDirty(this, 1);
        }
    }

    public void clearSurfaceRect(int x, int y, int width, int height) {
        this.ensureSurface();
        if (width <= 0 || height <= 0) {
            return;
        }
        int left = Math.max(0, x);
        int top = Math.max(0, y);
        int right = Math.min(this.bitmapWidth, x + width);
        int bottom = Math.min(this.bitmapHeight, y + height);
        if (left >= right || top >= bottom) {
            return;
        }
        int[] pixels = ((DataBufferInt)this.surface.getRaster().getDataBuffer()).getData();
        int rowWidth = right - left;
        if (left == 0 && top == 0 && right == this.bitmapWidth && bottom == this.bitmapHeight) {
            Arrays.fill(pixels, 0);
        } else {
            for (int row = top; row < bottom; ++row) {
                int offset = row * this.bitmapWidth + left;
                Arrays.fill(pixels, offset, offset + rowWidth, 0);
            }
        }
        this.surfaceDirty = true;
        if (this.document != null) {
            this.document.markDirty(this, 1);
        }
    }

    public String toDataURL() {
        return this.toDataURL("image/png");
    }

    public String toDataURL(String type) {
        byte[] bytes = this.toBytes(type, null);
        String normalized = type == null || type.isBlank() ? "image/png" : type.trim().toLowerCase(Locale.ROOT);
        String format = "image/jpeg".equals(normalized) || "image/jpg".equals(normalized) ? "jpg" : "png";
        String mime = "jpg".equals(format) ? "image/jpeg" : "image/png";
        return "data:" + mime + ";base64," + Base64.getEncoder().encodeToString(bytes);
    }

    public byte[] toBytes(String type) {
        return this.toBytes(type, null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public byte[] toBytes(String type, Double quality) {
        this.ensureSurface();
        String normalized = type == null || type.isBlank() ? "image/png" : type.trim().toLowerCase(Locale.ROOT);
        String format = "image/jpeg".equals(normalized) || "image/jpg".equals(normalized) ? "jpg" : "png";
        try (ByteArrayOutputStream output = new ByteArrayOutputStream();){
            block22: {
                block23: {
                    ImageWriter writer;
                    if (!"jpg".equals(format)) break block23;
                    BufferedImage rgbImage = new BufferedImage(this.surface.getWidth(), this.surface.getHeight(), 1);
                    Graphics2D graphics = rgbImage.createGraphics();
                    try {
                        graphics.drawImage((Image)this.surface, 0, 0, null);
                    }
                    finally {
                        graphics.dispose();
                    }
                    ImageWriter imageWriter = writer = ImageIO.getImageWritersByFormatName("jpg").hasNext() ? ImageIO.getImageWritersByFormatName("jpg").next() : null;
                    if (writer == null) {
                        ImageIO.write((RenderedImage)rgbImage, format, output);
                        break block22;
                    } else {
                        try (MemoryCacheImageOutputStream imageOutput = new MemoryCacheImageOutputStream(output);){
                            writer.setOutput(imageOutput);
                            ImageWriteParam params = writer.getDefaultWriteParam();
                            if (params.canWriteCompressed()) {
                                params.setCompressionMode(2);
                                float compression = quality == null ? 0.92f : (float)Math.max(0.0, Math.min(1.0, quality));
                                params.setCompressionQuality(compression);
                            }
                            writer.write(null, new IIOImage(rgbImage, null, null), params);
                            break block22;
                        }
                        finally {
                            writer.dispose();
                        }
                    }
                }
                ImageIO.write((RenderedImage)this.surface, format, output);
            }
            byte[] byArray = output.toByteArray();
            return byArray;
        }
        catch (IOException ignored) {
            return new byte[0];
        }
    }

    @Override
    public void drawPhase(PoseStack poseStack, Base.RenderPhase phase) {
        Rect rectRenderer = Rect.of(this);
        switch (phase) {
            case SHADOW: {
                rectRenderer.drawShadow(poseStack);
                break;
            }
            case BODY: {
                rectRenderer.drawBody(poseStack);
                this.drawCanvas(poseStack, rectRenderer);
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }

    protected void drawCanvas(PoseStack poseStack, Rect rectRenderer) {
        this.syncTexture();
        if (this.textureLocation == null) {
            return;
        }
        Position contentPos = rectRenderer.getContentPosition();
        Size contentSize = Box.of(this).innerSize();
        if (contentSize.width() <= 0.0 || contentSize.height() <= 0.0) {
            return;
        }
        ImageDrawer.draw(poseStack, this.textureLocation, (float)contentPos.x, (float)contentPos.y, (float)contentSize.width(), (float)contentSize.height(), true);
    }

    private void syncDimensionsFromAttributes(boolean notifyLayout) {
        int newWidth = Canvas.parseDimension(this.getAttributes().get("width"), 300);
        int newHeight = Canvas.parseDimension(this.getAttributes().get("height"), 150);
        this.resizeSurface(newWidth, newHeight, true);
        if (notifyLayout && this.document != null) {
            this.document.markDirty(this, 5);
        }
    }

    protected void resizeSurface(int width, int height, boolean resetState) {
        int safeWidth = Math.max(1, width);
        int safeHeight = Math.max(1, height);
        if (this.surface != null && this.bitmapWidth == safeWidth && this.bitmapHeight == safeHeight) {
            return;
        }
        this.bitmapWidth = safeWidth;
        this.bitmapHeight = safeHeight;
        this.surface = new BufferedImage(this.bitmapWidth, this.bitmapHeight, 2);
        this.destroyTexture();
        this.surfaceDirty = true;
        if (resetState) {
            this.context2d.resetState();
        }
    }

    protected void syncTexture() {
        this.ensureSurface();
        if (!this.surfaceDirty && this.textureLocation != null && this.texture != null && this.nativeImage != null) {
            return;
        }
        if (this.nativeImage == null || this.texture == null || this.textureLocation == null || this.nativeImage.m_84982_() != this.bitmapWidth || this.nativeImage.m_85084_() != this.bitmapHeight) {
            this.destroyTexture();
            this.nativeImage = new NativeImage(NativeImage.Format.RGBA, this.bitmapWidth, this.bitmapHeight, true);
            this.texture = AuiServices.render().createDynamicTexture("canvas/" + this.uuid, this.nativeImage, true);
            this.textureLocation = TextureKey.of("canvas/" + UUID.nameUUIDFromBytes(this.uuid.toString().getBytes(StandardCharsets.UTF_8)));
            AuiServices.render().registerTexture(this.texture, Canvas.textureLocation(this.textureLocation));
        }
        int[] pixels = ((DataBufferInt)this.surface.getRaster().getDataBuffer()).getData();
        int index = 0;
        for (int y = 0; y < this.bitmapHeight; ++y) {
            for (int x = 0; x < this.bitmapWidth; ++x) {
                AuiServices.render().setImagePixel(this.nativeImage, x, y, Canvas.argbToAbgr(pixels[index++]));
            }
        }
        AuiServices.render().uploadTextureRegion(this.texture, this.nativeImage, 0, 0, this.bitmapWidth, this.bitmapHeight, true);
        this.surfaceDirty = false;
    }

    private void destroyTexture() {
        if (this.texture != null) {
            try {
                AuiServices.render().closeTexture(this.texture);
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        this.texture = null;
        this.nativeImage = null;
        this.textureLocation = null;
    }

    public static void applyGraphicsDefaults(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
    }

    private static int parseDimension(String value, int fallback) {
        if (value == null || value.isBlank()) {
            return fallback;
        }
        try {
            return Math.max(1, (int)Math.round(Double.parseDouble(value.trim())));
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static int argbToAbgr(int argb) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        return a << 24 | b << 16 | g << 8 | r;
    }

    @Deprecated
    public static class CanvasRenderingContext2D
    extends com.sighs.apricityui.canvas.CanvasRenderingContext2D {
        public CanvasRenderingContext2D(Canvas canvas) {
            super(canvas);
        }

        @Override
        public CanvasLinearGradient createLinearGradient(double x0, double y0, double x1, double y1) {
            return new CanvasLinearGradient((float)x0, (float)y0, (float)x1, (float)y1);
        }

        @Override
        public CanvasRadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
            return new CanvasRadialGradient((float)x0, (float)y0, (float)r0, (float)x1, (float)y1, (float)r1);
        }
    }

    @Deprecated
    public static class CanvasRadialGradient
    extends com.sighs.apricityui.canvas.CanvasRadialGradient {
        public CanvasRadialGradient(float x0, float y0, float r0, float x1, float y1, float r1) {
            super(x0, y0, r0, x1, y1, r1);
        }
    }

    @Deprecated
    public static class CanvasLinearGradient
    extends com.sighs.apricityui.canvas.CanvasLinearGradient {
        public CanvasLinearGradient(float x0, float y0, float x1, float y1) {
            super(x0, y0, x1, y1);
        }
    }
}

