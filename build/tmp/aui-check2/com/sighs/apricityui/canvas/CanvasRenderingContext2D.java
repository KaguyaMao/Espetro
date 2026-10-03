/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.AdditiveComposite;
import com.sighs.apricityui.canvas.BlendComposite;
import com.sighs.apricityui.canvas.CanvasBlob;
import com.sighs.apricityui.canvas.CanvasFilterSupport;
import com.sighs.apricityui.canvas.CanvasImageData;
import com.sighs.apricityui.canvas.CanvasImageSupport;
import com.sighs.apricityui.canvas.CanvasLinearGradient;
import com.sighs.apricityui.canvas.CanvasPath2D;
import com.sighs.apricityui.canvas.CanvasPathSupport;
import com.sighs.apricityui.canvas.CanvasPattern;
import com.sighs.apricityui.canvas.CanvasRadialGradient;
import com.sighs.apricityui.canvas.CanvasState;
import com.sighs.apricityui.canvas.CanvasStyleUtil;
import com.sighs.apricityui.canvas.CanvasTextMetrics;
import com.sighs.apricityui.canvas.CanvasTextSupport;
import com.sighs.apricityui.canvas.DOMMatrix;
import com.sighs.apricityui.element.Canvas;
import com.sighs.apricityui.init.Window;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Paint;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Locale;
import java.util.function.Consumer;

public class CanvasRenderingContext2D {
    private final Canvas canvas;
    private final Deque<CanvasState> stack = new ArrayDeque<CanvasState>();
    private final Path2D.Double currentPath = new Path2D.Double();
    private CanvasState state = new CanvasState();

    public CanvasRenderingContext2D(Canvas canvas) {
        this.canvas = canvas;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public Object getFillStyle() {
        return this.state.fillStyle;
    }

    public void setFillStyle(Object fillStyle) {
        this.state.fillStyle = CanvasStyleUtil.normalizeStyle(fillStyle);
    }

    public Object getStrokeStyle() {
        return this.state.strokeStyle;
    }

    public void setStrokeStyle(Object strokeStyle) {
        this.state.strokeStyle = CanvasStyleUtil.normalizeStyle(strokeStyle);
    }

    public double getLineWidth() {
        return this.state.lineWidth;
    }

    public void setLineWidth(double lineWidth) {
        this.state.lineWidth = Math.max(0.1, lineWidth);
    }

    public String getLineCap() {
        return this.state.lineCap;
    }

    public void setLineCap(String lineCap) {
        this.state.lineCap = CanvasStyleUtil.normalizeLineCap(lineCap);
    }

    public String getLineJoin() {
        return this.state.lineJoin;
    }

    public void setLineJoin(String lineJoin) {
        this.state.lineJoin = CanvasStyleUtil.normalizeLineJoin(lineJoin);
    }

    public double getMiterLimit() {
        return this.state.miterLimit;
    }

    public void setMiterLimit(double miterLimit) {
        if (Double.isFinite(miterLimit) && miterLimit > 0.0) {
            this.state.miterLimit = miterLimit;
        }
    }

    public Object getLineDash() {
        return this.state.lineDash.clone();
    }

    public void setLineDash(Object segments) {
        this.state.lineDash = CanvasStyleUtil.normalizeLineDash(segments);
    }

    public double getLineDashOffset() {
        return this.state.lineDashOffset;
    }

    public void setLineDashOffset(double lineDashOffset) {
        this.state.lineDashOffset = Double.isFinite(lineDashOffset) ? lineDashOffset : 0.0;
    }

    public double getGlobalAlpha() {
        return this.state.globalAlpha;
    }

    public void setGlobalAlpha(double globalAlpha) {
        this.state.globalAlpha = CanvasStyleUtil.clamp(globalAlpha, 0.0, 1.0);
    }

    public String getGlobalCompositeOperation() {
        return this.state.globalCompositeOperation;
    }

    public void setGlobalCompositeOperation(String globalCompositeOperation) {
        String normalized;
        if (globalCompositeOperation == null || globalCompositeOperation.isBlank()) {
            this.state.globalCompositeOperation = "source-over";
            return;
        }
        this.state.globalCompositeOperation = switch (normalized = globalCompositeOperation.trim().toLowerCase(Locale.ROOT)) {
            case "source-in", "source-out", "source-atop", "destination-over", "destination-in", "destination-out", "destination-atop", "xor", "copy", "lighter", "multiply", "screen", "darken", "lighten" -> normalized;
            default -> "source-over";
        };
    }

    public String getFilter() {
        return this.state.filter;
    }

    public void setFilter(String filter) {
        this.state.filter = filter == null || filter.isBlank() ? "none" : filter.trim();
    }

    public String getFont() {
        return this.state.font;
    }

    public void setFont(String font) {
        this.state.font = font == null || font.isBlank() ? "16px SansSerif" : font;
    }

    public String getTextAlign() {
        return this.state.textAlign;
    }

    public void setTextAlign(String textAlign) {
        this.state.textAlign = textAlign == null || textAlign.isBlank() ? "start" : textAlign;
    }

    public String getTextBaseline() {
        return this.state.textBaseline;
    }

    public void setTextBaseline(String textBaseline) {
        this.state.textBaseline = textBaseline == null || textBaseline.isBlank() ? "alphabetic" : textBaseline;
    }

    public String getShadowColor() {
        return this.state.shadowColor;
    }

    public void setShadowColor(String shadowColor) {
        this.state.shadowColor = shadowColor == null ? "transparent" : shadowColor;
    }

    public double getShadowBlur() {
        return this.state.shadowBlur;
    }

    public void setShadowBlur(double shadowBlur) {
        this.state.shadowBlur = Math.max(0.0, shadowBlur);
    }

    public double getShadowOffsetX() {
        return this.state.shadowOffsetX;
    }

    public void setShadowOffsetX(double shadowOffsetX) {
        this.state.shadowOffsetX = shadowOffsetX;
    }

    public double getShadowOffsetY() {
        return this.state.shadowOffsetY;
    }

    public void setShadowOffsetY(double shadowOffsetY) {
        this.state.shadowOffsetY = shadowOffsetY;
    }

    public boolean isImageSmoothingEnabled() {
        return this.state.imageSmoothingEnabled;
    }

    public void setImageSmoothingEnabled(boolean imageSmoothingEnabled) {
        this.state.imageSmoothingEnabled = imageSmoothingEnabled;
    }

    public String getImageSmoothingQuality() {
        return this.state.imageSmoothingQuality;
    }

    public void setImageSmoothingQuality(String imageSmoothingQuality) {
        String normalized;
        if (imageSmoothingQuality == null || imageSmoothingQuality.isBlank()) {
            this.state.imageSmoothingQuality = "medium";
            return;
        }
        this.state.imageSmoothingQuality = switch (normalized = imageSmoothingQuality.trim().toLowerCase(Locale.ROOT)) {
            case "low", "high" -> normalized;
            default -> "medium";
        };
    }

    public void clearRect(double x, double y, double width, double height) {
        if (width <= 0.0 || height <= 0.0) {
            return;
        }
        if (this.state.transform.isIdentity() && this.state.clip == null) {
            int ix = (int)Math.floor(x);
            int iy = (int)Math.floor(y);
            int iw = (int)Math.ceil(x + width) - ix;
            int ih = (int)Math.ceil(y + height) - iy;
            this.canvas.clearSurfaceRect(ix, iy, iw, ih);
            return;
        }
        this.canvas.renderOperation(g -> {
            this.applyTransformAndClip((Graphics2D)g);
            g.setComposite(AlphaComposite.Clear);
            g.fill(new Rectangle2D.Double(x, y, width, height));
        });
    }

    public void fillRect(double x, double y, double width, double height) {
        this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, new Rectangle2D.Double(x, y, width, height), true));
    }

    public void strokeRect(double x, double y, double width, double height) {
        this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, new Rectangle2D.Double(x, y, width, height), false));
    }

    public void fillText(String text, double x, double y) {
        if (text == null || text.isEmpty()) {
            return;
        }
        this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, this.buildTextOutline((Graphics2D)g, text, x, y), true));
    }

    public void strokeText(String text, double x, double y) {
        if (text == null || text.isEmpty()) {
            return;
        }
        this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, this.buildTextOutline((Graphics2D)g, text, x, y), false));
    }

    public CanvasTextMetrics measureText(String text) {
        return CanvasTextSupport.measureText(this.canvas, this.state, text);
    }

    public CanvasImageData createImageData(int width, int height) {
        return new CanvasImageData(width, height);
    }

    public CanvasImageData createImageData(CanvasImageData source) {
        if (source == null) {
            return new CanvasImageData(1, 1);
        }
        return new CanvasImageData(source.width, source.height);
    }

    public CanvasImageData getImageData(int x, int y, int width, int height) {
        BufferedImage surface = this.canvas.getSurface();
        CanvasImageData imageData = new CanvasImageData(width, height);
        for (int row = 0; row < imageData.height; ++row) {
            for (int col = 0; col < imageData.width; ++col) {
                int srcX = x + col;
                int srcY = y + row;
                int dataIndex = (row * imageData.width + col) * 4;
                if (srcX < 0 || srcY < 0 || srcX >= this.canvas.getWidth() || srcY >= this.canvas.getHeight()) {
                    imageData.data[dataIndex] = 0;
                    imageData.data[dataIndex + 1] = 0;
                    imageData.data[dataIndex + 2] = 0;
                    imageData.data[dataIndex + 3] = 0;
                    continue;
                }
                int argb = surface.getRGB(srcX, srcY);
                imageData.data[dataIndex] = argb >>> 16 & 0xFF;
                imageData.data[dataIndex + 1] = argb >>> 8 & 0xFF;
                imageData.data[dataIndex + 2] = argb & 0xFF;
                imageData.data[dataIndex + 3] = argb >>> 24 & 0xFF;
            }
        }
        return imageData;
    }

    public void putImageData(CanvasImageData imageData, int dx, int dy) {
        if (imageData == null || imageData.data == null) {
            return;
        }
        this.canvas.renderOperation(g -> {
            BufferedImage surface = this.canvas.getSurface();
            for (int row = 0; row < imageData.height; ++row) {
                for (int col = 0; col < imageData.width; ++col) {
                    int dstX = dx + col;
                    int dstY = dy + row;
                    if (dstX < 0 || dstY < 0 || dstX >= this.canvas.getWidth() || dstY >= this.canvas.getHeight()) continue;
                    int dataIndex = (row * imageData.width + col) * 4;
                    if (dataIndex + 3 >= imageData.data.length) {
                        return;
                    }
                    int r = CanvasStyleUtil.clampChannel(imageData.data[dataIndex]);
                    int gChannel = CanvasStyleUtil.clampChannel(imageData.data[dataIndex + 1]);
                    int b = CanvasStyleUtil.clampChannel(imageData.data[dataIndex + 2]);
                    int a = CanvasStyleUtil.clampChannel(imageData.data[dataIndex + 3]);
                    surface.setRGB(dstX, dstY, a << 24 | r << 16 | gChannel << 8 | b);
                }
            }
        });
    }

    public void beginPath() {
        this.currentPath.reset();
    }

    public CanvasPath2D createPath2D() {
        return new CanvasPath2D();
    }

    public CanvasPath2D createPath2D(Object source) {
        if (source instanceof CanvasPath2D) {
            CanvasPath2D path = (CanvasPath2D)source;
            return new CanvasPath2D(path);
        }
        if (source instanceof String) {
            String text = (String)source;
            return new CanvasPath2D(text);
        }
        return new CanvasPath2D();
    }

    public void closePath() {
        this.currentPath.closePath();
    }

    public void moveTo(double x, double y) {
        this.currentPath.moveTo(x, y);
    }

    public void lineTo(double x, double y) {
        this.currentPath.lineTo(x, y);
    }

    public void quadraticCurveTo(double cpx, double cpy, double x, double y) {
        this.currentPath.quadTo(cpx, cpy, x, y);
    }

    public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
        this.currentPath.curveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void arcTo(double x1, double y1, double x2, double y2, double radius) {
        CanvasPathSupport.arcTo(this.currentPath, x1, y1, x2, y2, radius);
    }

    public void rect(double x, double y, double width, double height) {
        this.currentPath.append(new Rectangle2D.Double(x, y, width, height), false);
    }

    public void roundRect(double x, double y, double width, double height, Object radii) {
        CanvasPathSupport.appendRoundRect(this.currentPath, x, y, width, height, radii);
    }

    public void addPath(Object path) {
        if (path instanceof CanvasPath2D) {
            CanvasPath2D source = (CanvasPath2D)path;
            this.currentPath.append(source.raw(), false);
        }
    }

    public void addPath(Object path, double a, double b, double c, double d, double e, double f) {
        if (path instanceof CanvasPath2D) {
            CanvasPath2D source = (CanvasPath2D)path;
            this.currentPath.append(new AffineTransform(a, b, c, d, e, f).createTransformedShape(source.raw()), false);
        }
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle) {
        this.arc(x, y, radius, startAngle, endAngle, false);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
        CanvasPathSupport.appendArc(this.currentPath, x, y, radius, startAngle, endAngle, anticlockwise);
    }

    public void ellipse(double x, double y, double radiusX, double radiusY, double rotation, double startAngle, double endAngle) {
        this.ellipse(x, y, radiusX, radiusY, rotation, startAngle, endAngle, false);
    }

    public void ellipse(double x, double y, double radiusX, double radiusY, double rotation, double startAngle, double endAngle, boolean anticlockwise) {
        double extent;
        if (radiusX <= 0.0 || radiusY <= 0.0) {
            return;
        }
        double startDeg = Math.toDegrees(startAngle);
        double endDeg = Math.toDegrees(endAngle);
        if (!anticlockwise) {
            for (extent = endDeg - startDeg; extent <= 0.0; extent += 360.0) {
            }
        } else {
            while (extent >= 0.0) {
                extent -= 360.0;
            }
        }
        Arc2D.Double arc = new Arc2D.Double(-1.0, -1.0, 2.0, 2.0, -startDeg, -extent, 0);
        AffineTransform transform = new AffineTransform();
        transform.translate(x, y);
        transform.rotate(rotation);
        transform.scale(radiusX, radiusY);
        this.currentPath.append(transform.createTransformedShape(arc), true);
    }

    public void fill() {
        this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, this.currentPath, true));
    }

    public void fill(Object path) {
        if (path instanceof CanvasPath2D) {
            CanvasPath2D source = (CanvasPath2D)path;
            this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, source.raw(), true));
            return;
        }
        this.fill();
    }

    public void stroke() {
        this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, this.currentPath, false));
    }

    public void stroke(Object path) {
        if (path instanceof CanvasPath2D) {
            CanvasPath2D source = (CanvasPath2D)path;
            this.canvas.renderOperation(g -> this.renderShape((Graphics2D)g, source.raw(), false));
            return;
        }
        this.stroke();
    }

    public boolean isPointInPath(double x, double y) {
        return CanvasPathSupport.isPointInPath(this.state, this.currentPath, x, y);
    }

    public boolean isPointInPath(Object path, double x, double y) {
        if (path instanceof CanvasPath2D) {
            CanvasPath2D source = (CanvasPath2D)path;
            return CanvasPathSupport.isPointInPath(this.state, source.raw(), x, y);
        }
        return this.isPointInPath(x, y);
    }

    public boolean isPointInStroke(double x, double y) {
        return CanvasPathSupport.isPointInStroke(this.state, this.currentPath, x, y);
    }

    public boolean isPointInStroke(Object path, double x, double y) {
        if (path instanceof CanvasPath2D) {
            CanvasPath2D source = (CanvasPath2D)path;
            return CanvasPathSupport.isPointInStroke(this.state, source.raw(), x, y);
        }
        return this.isPointInStroke(x, y);
    }

    public void clip() {
        Shape clipShape = this.state.transform.createTransformedShape(new Path2D.Double(this.currentPath));
        if (this.state.clip == null) {
            this.state.clip = clipShape;
            return;
        }
        Area area = new Area(this.state.clip);
        area.intersect(new Area(clipShape));
        this.state.clip = area;
    }

    public void clip(Object path) {
        if (!(path instanceof CanvasPath2D)) {
            this.clip();
            return;
        }
        CanvasPath2D source = (CanvasPath2D)path;
        Shape clipShape = this.state.transform.createTransformedShape(source.raw());
        if (this.state.clip == null) {
            this.state.clip = clipShape;
            return;
        }
        Area area = new Area(this.state.clip);
        area.intersect(new Area(clipShape));
        this.state.clip = area;
    }

    public void save() {
        this.stack.push(this.state.copy());
    }

    public void restore() {
        if (!this.stack.isEmpty()) {
            this.state = this.stack.pop();
        }
    }

    public void translate(double x, double y) {
        this.state.transform.translate(x, y);
    }

    public void rotate(double angle) {
        this.state.transform.rotate(angle);
    }

    public void scale(double x, double y) {
        this.state.transform.scale(x, y);
    }

    public DOMMatrix getTransform() {
        return DOMMatrix.fromAffineTransform(this.state.transform);
    }

    public void transform(double a, double b, double c, double d, double e, double f) {
        this.state.transform.concatenate(new AffineTransform(a, b, c, d, e, f));
    }

    public void transform(Object matrix) {
        this.state.transform.concatenate(DOMMatrix.from(matrix));
    }

    public void setTransform(double a, double b, double c, double d, double e, double f) {
        this.state.transform = new AffineTransform(a, b, c, d, e, f);
    }

    public void setTransform(Object matrix) {
        this.state.transform = DOMMatrix.from(matrix);
    }

    public void resetTransform() {
        this.state.transform = new AffineTransform();
    }

    public void clear() {
        this.canvas.renderOperation(g -> {
            this.applyClip((Graphics2D)g);
            g.setComposite(AlphaComposite.Clear);
            g.fillRect(0, 0, this.canvas.getWidth(), this.canvas.getHeight());
        });
    }

    public CanvasLinearGradient createLinearGradient(double x0, double y0, double x1, double y1) {
        return new CanvasLinearGradient((float)x0, (float)y0, (float)x1, (float)y1);
    }

    public CanvasRadialGradient createRadialGradient(double x0, double y0, double r0, double x1, double y1, double r1) {
        return new CanvasRadialGradient((float)x0, (float)y0, (float)r0, (float)x1, (float)y1, (float)r1);
    }

    public CanvasPattern createPattern(Object image, String repetition) {
        BufferedImage source = this.resolveImageSource(image);
        return source == null ? null : new CanvasPattern(source, repetition);
    }

    public CanvasBlob toBlob() {
        return this.toBlob("image/png", null);
    }

    public CanvasBlob toBlob(String type) {
        return this.toBlob(type, null);
    }

    public CanvasBlob toBlob(String type, Double quality) {
        String mime = CanvasRenderingContext2D.normalizeMime(type);
        return new CanvasBlob(this.canvas.toBytes(mime, quality), mime);
    }

    public void toBlob(Consumer<CanvasBlob> callback) {
        this.toBlob(callback, "image/png", null);
    }

    public void toBlob(Consumer<CanvasBlob> callback, String type) {
        this.toBlob(callback, type, null);
    }

    public void toBlob(Consumer<CanvasBlob> callback, String type, Double quality) {
        if (callback == null) {
            return;
        }
        Window.window.setTimeout(handle -> callback.accept(this.toBlob(type, quality)), 0);
    }

    private void applyImageSmoothing(Graphics2D g) {
        if (!this.state.imageSmoothingEnabled) {
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
            return;
        }
        Object interpolation = switch (this.state.imageSmoothingQuality) {
            case "low" -> RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR;
            case "high" -> RenderingHints.VALUE_INTERPOLATION_BICUBIC;
            default -> RenderingHints.VALUE_INTERPOLATION_BILINEAR;
        };
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, interpolation);
    }

    public void drawImage(Object image, double dx, double dy) {
        BufferedImage source = this.resolveImageSource(image);
        if (source == null) {
            return;
        }
        this.drawImageInternal(source, 0.0, 0.0, source.getWidth(), source.getHeight(), dx, dy, source.getWidth(), source.getHeight());
    }

    public void drawImage(Object image, double dx, double dy, double dw, double dh) {
        BufferedImage source = this.resolveImageSource(image);
        if (source == null) {
            return;
        }
        this.drawImageInternal(source, 0.0, 0.0, source.getWidth(), source.getHeight(), dx, dy, dw, dh);
    }

    public void drawImage(Object image, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh) {
        BufferedImage source = this.resolveImageSource(image);
        if (source == null) {
            return;
        }
        this.drawImageInternal(source, sx, sy, sw, sh, dx, dy, dw, dh);
    }

    public void resetState() {
        this.stack.clear();
        this.state = new CanvasState();
        this.currentPath.reset();
    }

    private void renderShape(Graphics2D g, Shape shape, boolean fill) {
        if (shape == null) {
            return;
        }
        CanvasFilterSupport.renderWithFilter(this.canvas, this.state.filter, g, layer -> {
            Object style;
            this.drawShadowIfNeeded((Graphics2D)layer, shape, fill);
            Object object = style = fill ? this.state.fillStyle : this.state.strokeStyle;
            if (style instanceof CanvasPattern) {
                CanvasPattern pattern = (CanvasPattern)style;
                Shape targetShape = fill ? shape : this.createStroke(this.state.lineWidth).createStrokedShape(shape);
                this.renderPatternShape((Graphics2D)layer, targetShape, pattern);
                return;
            }
            this.applyPaintState((Graphics2D)layer, fill);
            if (fill) {
                layer.fill(shape);
            } else {
                layer.draw(shape);
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void renderPatternShape(Graphics2D g, Shape shape, CanvasPattern pattern) {
        BufferedImage image = pattern.getImage();
        if (image == null || image.getWidth() <= 0 || image.getHeight() <= 0) {
            return;
        }
        Graphics2D patternGraphics = (Graphics2D)g.create();
        try {
            this.applyClip(patternGraphics);
            patternGraphics.setTransform(this.state.transform);
            patternGraphics.clip(shape);
            patternGraphics.setComposite(this.resolveComposite((float)this.state.globalAlpha));
            patternGraphics.transform(pattern.getTransform());
            this.applyImageSmoothing(patternGraphics);
            Rectangle2D bounds = shape.getBounds2D();
            double tileW = image.getWidth();
            double tileH = image.getHeight();
            double startX = switch (pattern.getRepetition()) {
                case "repeat", "repeat-x" -> Math.floor(bounds.getMinX() / tileW) * tileW;
                default -> 0.0;
            };
            double endX = switch (pattern.getRepetition()) {
                case "repeat", "repeat-x" -> bounds.getMaxX();
                default -> tileW;
            };
            double startY = switch (pattern.getRepetition()) {
                case "repeat", "repeat-y" -> Math.floor(bounds.getMinY() / tileH) * tileH;
                default -> 0.0;
            };
            double endY = switch (pattern.getRepetition()) {
                case "repeat", "repeat-y" -> bounds.getMaxY();
                default -> tileH;
            };
            for (double x = startX; x <= endX; x += tileW) {
                for (double y = startY; y <= endY; y += tileH) {
                    patternGraphics.drawImage((Image)image, (int)Math.round(x), (int)Math.round(y), null);
                    if ("no-repeat".equals(pattern.getRepetition())) {
                        return;
                    }
                    if ("repeat-x".equals(pattern.getRepetition())) break;
                }
                if (!"repeat-y".equals(pattern.getRepetition())) continue;
                return;
            }
        }
        finally {
            patternGraphics.dispose();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawShadowIfNeeded(Graphics2D g, Shape shape, boolean fill) {
        Color shadow = CanvasStyleUtil.parseAwtColor(this.state.shadowColor);
        if (shadow.getAlpha() <= 0) {
            return;
        }
        double blur = Math.max(0.0, this.state.shadowBlur);
        int passes = Math.max(1, (int)Math.ceil(blur));
        float baseAlpha = (float)shadow.getAlpha() / 255.0f * (float)this.state.globalAlpha;
        if (baseAlpha <= 0.0f) {
            return;
        }
        for (int pass = passes; pass >= 1; --pass) {
            Graphics2D shadowGraphics = (Graphics2D)g.create();
            try {
                this.applyTransformAndClip(shadowGraphics);
                shadowGraphics.translate(this.state.shadowOffsetX, this.state.shadowOffsetY);
                shadowGraphics.setStroke(this.createStroke(Math.max(0.1, this.state.lineWidth + (double)pass * 0.8)));
                float alpha = baseAlpha * (0.16f + 0.12f * (float)pass / (float)passes);
                shadowGraphics.setComposite(this.resolveComposite(alpha));
                shadowGraphics.setPaint(new Color(shadow.getRed(), shadow.getGreen(), shadow.getBlue(), shadow.getAlpha()));
                if (fill) {
                    shadowGraphics.fill(shape);
                    continue;
                }
                shadowGraphics.draw(shape);
                continue;
            }
            finally {
                shadowGraphics.dispose();
            }
        }
    }

    private void applyPaintState(Graphics2D g, boolean fill) {
        this.applyTransformAndClip(g);
        g.setComposite(this.resolveComposite((float)this.state.globalAlpha));
        g.setStroke(this.createStroke(this.state.lineWidth));
        g.setFont(CanvasStyleUtil.parseFont(this.state.font));
        g.setPaint(this.resolvePaint(fill ? this.state.fillStyle : this.state.strokeStyle));
    }

    private BasicStroke createStroke(double width) {
        float[] dashArray = this.state.lineDash.length == 0 ? null : CanvasStyleUtil.toFloatDashArray(this.state.lineDash);
        float dashPhase = dashArray == null ? 0.0f : (float)this.state.lineDashOffset;
        return new BasicStroke((float)Math.max(0.1, width), CanvasStyleUtil.resolveLineCap(this.state.lineCap), CanvasStyleUtil.resolveLineJoin(this.state.lineJoin), (float)Math.max(1.0, this.state.miterLimit), dashArray, dashPhase);
    }

    private void applyTransformAndClip(Graphics2D g) {
        this.applyClip(g);
        g.setTransform(this.state.transform);
    }

    private void applyClip(Graphics2D g) {
        g.setTransform(new AffineTransform());
        g.setClip(null);
        if (this.state.clip != null) {
            g.clip(this.state.clip);
        }
    }

    private Composite resolveComposite(float alpha) {
        return switch (this.state.globalCompositeOperation) {
            case "source-in" -> AlphaComposite.getInstance(5, alpha);
            case "source-out" -> AlphaComposite.getInstance(7, alpha);
            case "source-atop" -> AlphaComposite.getInstance(10, alpha);
            case "destination-over" -> AlphaComposite.getInstance(4, alpha);
            case "destination-in" -> AlphaComposite.getInstance(6, alpha);
            case "destination-out" -> AlphaComposite.getInstance(8, alpha);
            case "destination-atop" -> AlphaComposite.getInstance(11, alpha);
            case "xor" -> AlphaComposite.getInstance(12, alpha);
            case "copy" -> AlphaComposite.getInstance(2, alpha);
            case "lighter" -> new AdditiveComposite(alpha);
            case "multiply" -> new BlendComposite(BlendComposite.Mode.MULTIPLY, alpha);
            case "screen" -> new BlendComposite(BlendComposite.Mode.SCREEN, alpha);
            case "darken" -> new BlendComposite(BlendComposite.Mode.DARKEN, alpha);
            case "lighten" -> new BlendComposite(BlendComposite.Mode.LIGHTEN, alpha);
            default -> AlphaComposite.getInstance(3, alpha);
        };
    }

    private Paint resolvePaint(Object style) {
        CanvasPattern pattern;
        Paint paint;
        if (style instanceof CanvasLinearGradient) {
            CanvasLinearGradient gradient = (CanvasLinearGradient)style;
            return gradient.toPaint();
        }
        if (style instanceof CanvasRadialGradient) {
            CanvasRadialGradient gradient = (CanvasRadialGradient)style;
            return gradient.toPaint();
        }
        if (style instanceof CanvasPattern && (paint = (pattern = (CanvasPattern)style).toPaint()) != null) {
            return paint;
        }
        return CanvasStyleUtil.parseAwtColor(style == null ? "#000000" : style.toString());
    }

    private Shape buildTextOutline(Graphics2D g, String text, double x, double y) {
        return CanvasTextSupport.buildTextOutline(g, this.state, text, x, y);
    }

    private BufferedImage resolveImageSource(Object image) {
        return CanvasImageSupport.resolveImageSource(image);
    }

    private static String normalizeMime(String type) {
        if (type == null || type.isBlank()) {
            return "image/png";
        }
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        return "image/jpeg".equals(normalized) || "image/jpg".equals(normalized) ? "image/jpeg" : "image/png";
    }

    private void drawImageInternal(BufferedImage source, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh) {
        if (source == null || sw <= 0.0 || sh <= 0.0 || dw == 0.0 || dh == 0.0) {
            return;
        }
        this.canvas.renderOperation(g -> CanvasFilterSupport.renderWithFilter(this.canvas, this.state.filter, g, layer -> {
            this.applyTransformAndClip((Graphics2D)layer);
            layer.setComposite(this.resolveComposite((float)this.state.globalAlpha));
            this.applyImageSmoothing((Graphics2D)layer);
            this.drawImageShadow((Graphics2D)layer, source, sx, sy, sw, sh, dx, dy, dw, dh);
            int srcX1 = (int)Math.round(sx);
            int srcY1 = (int)Math.round(sy);
            int srcX2 = (int)Math.round(sx + sw);
            int srcY2 = (int)Math.round(sy + sh);
            int dstX1 = (int)Math.round(dx);
            int dstY1 = (int)Math.round(dy);
            int dstX2 = (int)Math.round(dx + dw);
            int dstY2 = (int)Math.round(dy + dh);
            layer.drawImage(source, dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2, null);
        }));
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drawImageShadow(Graphics2D g, BufferedImage source, double sx, double sy, double sw, double sh, double dx, double dy, double dw, double dh) {
        Color shadow = CanvasStyleUtil.parseAwtColor(this.state.shadowColor);
        if (shadow.getAlpha() <= 0) {
            return;
        }
        BufferedImage shadowSource = CanvasImageSupport.tintImageAlpha(source, shadow);
        if (shadowSource == null) {
            return;
        }
        double blur = Math.max(0.0, this.state.shadowBlur);
        int passes = Math.max(1, (int)Math.ceil(blur));
        float baseAlpha = (float)shadow.getAlpha() / 255.0f * (float)this.state.globalAlpha;
        if (baseAlpha <= 0.0f) {
            return;
        }
        for (int pass = passes; pass >= 1; --pass) {
            Graphics2D shadowGraphics = (Graphics2D)g.create();
            try {
                this.applyTransformAndClip(shadowGraphics);
                shadowGraphics.translate(this.state.shadowOffsetX, this.state.shadowOffsetY);
                shadowGraphics.setComposite(this.resolveComposite(baseAlpha * (0.16f + 0.12f * (float)pass / (float)passes)));
                int spread = Math.max(0, pass - 1);
                int srcX1 = (int)Math.round(sx);
                int srcY1 = (int)Math.round(sy);
                int srcX2 = (int)Math.round(sx + sw);
                int srcY2 = (int)Math.round(sy + sh);
                int dstX1 = (int)Math.round(dx) - spread;
                int dstY1 = (int)Math.round(dy) - spread;
                int dstX2 = (int)Math.round(dx + dw) + spread;
                int dstY2 = (int)Math.round(dy + dh) + spread;
                shadowGraphics.drawImage(shadowSource, dstX1, dstY1, dstX2, dstY2, srcX1, srcY1, srcX2, srcY2, null);
                continue;
            }
            finally {
                shadowGraphics.dispose();
            }
        }
    }
}

