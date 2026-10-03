/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 */
package com.sighs.apricityui.element;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.canvas.CanvasPath2D;
import com.sighs.apricityui.element.Canvas;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.init.Node;
import com.sighs.apricityui.layout.Box;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.registry.annotation.ElementRegister;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.Rect;
import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;
import java.util.Locale;

@ElementRegister(value="SVG")
public class Svg
extends Canvas {
    public static final String TAG_NAME = "SVG";
    private static final int RASTER_SCALE = 4;
    private static final String[] RASTER_FINGERPRINT_ATTRIBUTES = new String[]{"color", "fill", "stroke", "stroke-width", "stroke-linecap", "stroke-linejoin", "opacity", "fill-opacity", "stroke-opacity", "fill-rule", "d", "cx", "cy", "r", "x", "y", "width", "height", "rx", "ry", "x1", "y1", "x2", "y2"};
    private double rasterLayoutWidth = -1.0;
    private double rasterLayoutHeight = -1.0;
    private int intrinsicViewportWidth = 1;
    private int intrinsicViewportHeight = 1;
    private long rasterSubtreeMutationVersion = Long.MIN_VALUE;
    private long rasterStyleFingerprint = Long.MIN_VALUE;
    private int rasterSurfaceWidth = -1;
    private int rasterSurfaceHeight = -1;

    public Svg(Document document) {
        super(document);
        this.tagName = TAG_NAME;
    }

    @Override
    protected void onInitFromDom(Element origin) {
        super.onInitFromDom(origin);
        this.syncViewportAttributes();
    }

    @Override
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
        if (name != null && ("viewbox".equalsIgnoreCase(name) || "width".equalsIgnoreCase(name) || "height".equalsIgnoreCase(name))) {
            this.syncViewportAttributes();
        }
    }

    @Override
    public void removeAttribute(String name) {
        super.removeAttribute(name);
        if (name != null && ("viewbox".equalsIgnoreCase(name) || "width".equalsIgnoreCase(name) || "height".equalsIgnoreCase(name))) {
            this.syncViewportAttributes();
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
                this.renderVectorSurface();
                this.drawCanvas(poseStack, rectRenderer);
                break;
            }
            case BORDER: {
                rectRenderer.drawBorder(poseStack);
            }
        }
    }

    private void syncViewportAttributes() {
        double[] viewBox = this.parseViewBox();
        int width = Svg.parseDimension(this.getAttribute("width"), (int)Math.round(Math.max(1.0, viewBox[2])));
        int height = Svg.parseDimension(this.getAttribute("height"), (int)Math.round(Math.max(1.0, viewBox[3])));
        this.intrinsicViewportWidth = width;
        this.intrinsicViewportHeight = height;
        this.resizeSurface(width, height, false);
    }

    @Override
    public Size getIntrinsicSize() {
        return new Size(this.intrinsicViewportWidth, this.intrinsicViewportHeight);
    }

    private void renderVectorSurface() {
        this.syncSurfaceToLayoutSize();
        long subtreeMutationVersion = this.getSubtreeMutationVersion();
        long styleFingerprint = Svg.svgStyleFingerprint(this, 17L);
        if (this.rasterSubtreeMutationVersion == subtreeMutationVersion && this.rasterStyleFingerprint == styleFingerprint && this.rasterSurfaceWidth == this.getWidth() && this.rasterSurfaceHeight == this.getHeight()) {
            return;
        }
        this.renderOperation(graphics -> {
            graphics.setComposite(AlphaComposite.Clear);
            graphics.fill(new Rectangle2D.Double(0.0, 0.0, this.getWidth(), this.getHeight()));
            graphics.setComposite(AlphaComposite.SrcOver);
            double surfaceWidth = this.getWidth();
            double surfaceHeight = this.getHeight();
            if (surfaceWidth <= 0.0 || surfaceHeight <= 0.0) {
                return;
            }
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            graphics.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
            double[] viewBox = this.parseViewBox();
            double vbWidth = Math.max(1.0, viewBox[2]);
            double vbHeight = Math.max(1.0, viewBox[3]);
            AffineTransform original = graphics.getTransform();
            graphics.translate(-viewBox[0], -viewBox[1]);
            graphics.scale(surfaceWidth / vbWidth, surfaceHeight / vbHeight);
            SvgPaint inheritedPaint = SvgPaint.fromElement(this, this.currentColorArgb(), "black", "none", 1.0, "butt", "miter", 1.0, 1.0, 1.0);
            this.drawSvgSubtree((Graphics2D)graphics, this, inheritedPaint);
            graphics.setTransform(original);
        });
        this.rasterSubtreeMutationVersion = subtreeMutationVersion;
        this.rasterStyleFingerprint = styleFingerprint;
        this.rasterSurfaceWidth = this.getWidth();
        this.rasterSurfaceHeight = this.getHeight();
    }

    private void syncSurfaceToLayoutSize() {
        Size contentSize = Box.of(this).innerSize();
        if (contentSize.width() <= 0.0 || contentSize.height() <= 0.0) {
            return;
        }
        this.rasterLayoutWidth = contentSize.width();
        this.rasterLayoutHeight = contentSize.height();
        int surfaceWidth = Math.max(1, (int)Math.ceil(contentSize.width() * 4.0));
        int surfaceHeight = Math.max(1, (int)Math.ceil(contentSize.height() * 4.0));
        if (surfaceWidth == this.getWidth() && surfaceHeight == this.getHeight()) {
            return;
        }
        this.resizeSurface(surfaceWidth, surfaceHeight, false);
    }

    @Override
    protected void drawCanvas(PoseStack poseStack, Rect rectRenderer) {
        double drawHeight;
        this.syncTexture();
        if (this.textureLocation == null) {
            return;
        }
        Position contentPos = rectRenderer.getContentPosition();
        Size contentSize = Box.of(this).innerSize();
        double drawWidth = this.rasterLayoutWidth > 0.0 ? this.rasterLayoutWidth : contentSize.width();
        double d = drawHeight = this.rasterLayoutHeight > 0.0 ? this.rasterLayoutHeight : contentSize.height();
        if (drawWidth <= 0.0 || drawHeight <= 0.0) {
            return;
        }
        ImageDrawer.draw(poseStack, this.textureLocation, (float)contentPos.x, (float)contentPos.y, (float)drawWidth, (float)drawHeight, true);
    }

    private void drawSvgSubtree(Graphics2D graphics, Element parent, SvgPaint inheritedPaint) {
        if (parent == null) {
            return;
        }
        SvgPaint currentPaint = SvgPaint.fromElement(parent, inheritedPaint.currentColor, inheritedPaint.fill, inheritedPaint.stroke, inheritedPaint.strokeWidth, inheritedPaint.lineCap, inheritedPaint.lineJoin, inheritedPaint.opacity, inheritedPaint.fillOpacity, inheritedPaint.strokeOpacity);
        for (Node child : parent.getChildNodes()) {
            String tag;
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            String string = tag = childElement.tagName == null ? "" : childElement.tagName.toUpperCase(Locale.ROOT);
            if ("PATH".equals(tag)) {
                this.drawPath(graphics, childElement, currentPaint);
                continue;
            }
            Shape shape = this.shapeForElement(childElement, tag);
            if (shape != null) {
                this.drawShape(graphics, childElement, shape, currentPaint);
                continue;
            }
            this.drawSvgSubtree(graphics, childElement, currentPaint);
        }
    }

    private void drawPath(Graphics2D graphics, Element pathElement, SvgPaint inheritedPaint) {
        Shape shape;
        String d = pathElement.getAttribute("d");
        if (d == null || d.isBlank()) {
            return;
        }
        CanvasPath2D canvasPath = new CanvasPath2D(d);
        if ("evenodd".equalsIgnoreCase(pathElement.getAttribute("fill-rule"))) {
            canvasPath.setWindingRule(0);
        }
        if ((shape = canvasPath.asShape()) == null || shape.getBounds2D().isEmpty()) {
            return;
        }
        this.drawShape(graphics, pathElement, shape, inheritedPaint);
    }

    private Shape shapeForElement(Element element, String tag) {
        return switch (tag) {
            case "CIRCLE" -> this.circleShape(element);
            case "ELLIPSE" -> this.ellipseShape(element);
            case "RECT" -> this.rectShape(element);
            case "LINE" -> this.lineShape(element);
            case "POLYLINE" -> this.pointsShape(element, false);
            case "POLYGON" -> this.pointsShape(element, true);
            default -> null;
        };
    }

    private Shape circleShape(Element element) {
        double r = Svg.parseSvgNumber(element.getAttribute("r"), 0.0);
        if (r <= 0.0) {
            return null;
        }
        double cx = Svg.parseSvgNumber(element.getAttribute("cx"), 0.0);
        double cy = Svg.parseSvgNumber(element.getAttribute("cy"), 0.0);
        return new Ellipse2D.Double(cx - r, cy - r, r * 2.0, r * 2.0);
    }

    private Shape ellipseShape(Element element) {
        double rx = Svg.parseSvgNumber(element.getAttribute("rx"), 0.0);
        double ry = Svg.parseSvgNumber(element.getAttribute("ry"), 0.0);
        if (rx <= 0.0 || ry <= 0.0) {
            return null;
        }
        double cx = Svg.parseSvgNumber(element.getAttribute("cx"), 0.0);
        double cy = Svg.parseSvgNumber(element.getAttribute("cy"), 0.0);
        return new Ellipse2D.Double(cx - rx, cy - ry, rx * 2.0, ry * 2.0);
    }

    private Shape rectShape(Element element) {
        double width = Svg.parseSvgNumber(element.getAttribute("width"), 0.0);
        double height = Svg.parseSvgNumber(element.getAttribute("height"), 0.0);
        if (width <= 0.0 || height <= 0.0) {
            return null;
        }
        double x = Svg.parseSvgNumber(element.getAttribute("x"), 0.0);
        double y = Svg.parseSvgNumber(element.getAttribute("y"), 0.0);
        return new Rectangle2D.Double(x, y, width, height);
    }

    private Shape lineShape(Element element) {
        double x1 = Svg.parseSvgNumber(element.getAttribute("x1"), 0.0);
        double y1 = Svg.parseSvgNumber(element.getAttribute("y1"), 0.0);
        double x2 = Svg.parseSvgNumber(element.getAttribute("x2"), 0.0);
        double y2 = Svg.parseSvgNumber(element.getAttribute("y2"), 0.0);
        return new Line2D.Double(x1, y1, x2, y2);
    }

    private Shape pointsShape(Element element, boolean close) {
        String raw = element.getAttribute("points");
        if (raw == null || raw.isBlank()) {
            return null;
        }
        String[] parts = raw.trim().split("[,\\s]+");
        if (parts.length < 4) {
            return null;
        }
        Path2D.Double path = new Path2D.Double();
        path.moveTo(Svg.parseSvgNumber(parts[0], 0.0), Svg.parseSvgNumber(parts[1], 0.0));
        int i = 2;
        while (i + 1 < parts.length) {
            path.lineTo(Svg.parseSvgNumber(parts[i], 0.0), Svg.parseSvgNumber(parts[i + 1], 0.0));
            i += 2;
        }
        if (close) {
            path.closePath();
        }
        return path;
    }

    private void drawShape(Graphics2D graphics, Element pathElement, Shape shape, SvgPaint inheritedPaint) {
        SvgPaint paint = SvgPaint.fromElement(pathElement, inheritedPaint.currentColor, inheritedPaint.fill, inheritedPaint.stroke, inheritedPaint.strokeWidth, inheritedPaint.lineCap, inheritedPaint.lineJoin, inheritedPaint.opacity, inheritedPaint.fillOpacity, inheritedPaint.strokeOpacity);
        if (!"none".equalsIgnoreCase(paint.fill)) {
            graphics.setColor(this.toAwtColor(this.resolveSvgColor(paint.fill, paint.currentColor), paint.opacity * paint.fillOpacity));
            graphics.fill(shape);
        }
        if (!"none".equalsIgnoreCase(paint.stroke)) {
            graphics.setColor(this.toAwtColor(this.resolveSvgColor(paint.stroke, paint.currentColor), paint.opacity * paint.strokeOpacity));
            float f = (float)Math.max(0.1, paint.strokeWidth);
            graphics.setStroke(new BasicStroke(f, switch (paint.lineCap) {
                case "round" -> 1;
                case "square" -> 2;
                default -> 0;
            }, switch (paint.lineJoin) {
                case "round" -> 1;
                case "bevel" -> 2;
                default -> 0;
            }));
            graphics.draw(shape);
        }
    }

    private int currentColorArgb() {
        String computedColor;
        String string = computedColor = this.getComputedStyle() == null ? null : this.getComputedStyle().color;
        if (computedColor == null || computedColor.isBlank() || "unset".equalsIgnoreCase(computedColor)) {
            computedColor = this.getAttribute("color");
        }
        if (computedColor == null || computedColor.isBlank() || "unset".equalsIgnoreCase(computedColor)) {
            computedColor = "#000000";
        }
        return com.sighs.apricityui.parser.Color.parse(computedColor);
    }

    private static int resolveCurrentColor(Element element, int fallback) {
        if (element == null) {
            return fallback;
        }
        String color = Svg.firstNonBlank(element.getAttribute("color"), element.getComputedStyle() == null ? null : element.getComputedStyle().color);
        if (color == null || color.isBlank() || "unset".equalsIgnoreCase(color)) {
            return fallback;
        }
        return com.sighs.apricityui.parser.Color.parse(color);
    }

    private int resolveSvgColor(String value, int currentColor) {
        if (value == null || value.isBlank()) {
            return currentColor;
        }
        if ("currentcolor".equalsIgnoreCase(value)) {
            return currentColor;
        }
        return com.sighs.apricityui.parser.Color.parse(value);
    }

    private Color toAwtColor(int argb, double opacityMultiplier) {
        int a = argb >>> 24 & 0xFF;
        int r = argb >>> 16 & 0xFF;
        int g = argb >>> 8 & 0xFF;
        int b = argb & 0xFF;
        a = (int)Math.round((double)a * Svg.clampOpacity(opacityMultiplier));
        return new Color(r, g, b, a);
    }

    private static long svgStyleFingerprint(Element element, long value) {
        if (element == null) {
            return value;
        }
        String color = element.getComputedStyle() == null ? null : element.getComputedStyle().color;
        value = Svg.mixStyleFingerprint(value, color);
        for (String attribute : RASTER_FINGERPRINT_ATTRIBUTES) {
            value = Svg.mixStyleFingerprint(value, element.getAttribute(attribute));
        }
        for (Node child : element.childNodes) {
            if (!(child instanceof Element)) continue;
            Element childElement = (Element)child;
            value = Svg.svgStyleFingerprint(childElement, value);
        }
        return value;
    }

    private static long mixStyleFingerprint(long value, String component) {
        return value * -7046029288634856825L ^ (long)(component == null ? 0 : component.hashCode());
    }

    private double[] parseViewBox() {
        String raw = Svg.firstNonBlank(this.getAttribute("viewBox"), this.getAttribute("viewbox"));
        if (raw == null || raw.isBlank()) {
            return new double[]{0.0, 0.0, Math.max(1, this.getWidth()), Math.max(1, this.getHeight())};
        }
        String[] parts = raw.trim().split("[,\\s]+");
        if (parts.length < 4) {
            return new double[]{0.0, 0.0, Math.max(1, this.getWidth()), Math.max(1, this.getHeight())};
        }
        return new double[]{Svg.parseSvgNumber(parts[0], 0.0), Svg.parseSvgNumber(parts[1], 0.0), Math.max(1.0, Svg.parseSvgNumber(parts[2], Math.max(1, this.getWidth()))), Math.max(1.0, Svg.parseSvgNumber(parts[3], Math.max(1, this.getHeight())))};
    }

    private static double parseSvgNumber(String raw, double fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Double.parseDouble(raw.trim());
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static String firstNonBlank(String ... values) {
        if (values == null) {
            return null;
        }
        for (String value : values) {
            if (value == null || value.isBlank() || "unset".equalsIgnoreCase(value)) continue;
            return value;
        }
        return null;
    }

    private static int parseDimension(String raw, int fallback) {
        if (raw == null || raw.isBlank()) {
            return Math.max(1, fallback);
        }
        Double parsed = Size.parseNumber(raw);
        return parsed == null ? Math.max(1, fallback) : Math.max(1, (int)Math.round(parsed));
    }

    private static double parseOpacity(String raw, double fallback) {
        if (raw == null || raw.isBlank()) {
            return fallback;
        }
        try {
            return Svg.clampOpacity(Double.parseDouble(raw.trim()));
        }
        catch (NumberFormatException ignored) {
            return fallback;
        }
    }

    private static double clampOpacity(double value) {
        if (!Double.isFinite(value)) {
            return 1.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    private record SvgPaint(int currentColor, String fill, String stroke, double strokeWidth, String lineCap, String lineJoin, double opacity, double fillOpacity, double strokeOpacity) {
        private static SvgPaint fromElement(Element element, int inheritedColor, String inheritedFill, String inheritedStroke, double inheritedStrokeWidth, String inheritedLineCap, String inheritedLineJoin, double inheritedOpacity, double inheritedFillOpacity, double inheritedStrokeOpacity) {
            int color = Svg.resolveCurrentColor(element, inheritedColor);
            String fill = Svg.firstNonBlank(SvgPaint.attribute(element, "fill"), inheritedFill, "black");
            String stroke = Svg.firstNonBlank(SvgPaint.attribute(element, "stroke"), inheritedStroke, "none");
            double strokeWidth = Svg.parseSvgNumber(SvgPaint.attribute(element, "stroke-width"), inheritedStrokeWidth);
            String lineCap = Svg.firstNonBlank(SvgPaint.attribute(element, "stroke-linecap"), inheritedLineCap, "butt").toLowerCase(Locale.ROOT);
            String lineJoin = Svg.firstNonBlank(SvgPaint.attribute(element, "stroke-linejoin"), inheritedLineJoin, "miter").toLowerCase(Locale.ROOT);
            double opacity = inheritedOpacity * Svg.parseOpacity(SvgPaint.attribute(element, "opacity"), 1.0);
            double fillOpacity = Svg.parseOpacity(SvgPaint.attribute(element, "fill-opacity"), inheritedFillOpacity);
            double strokeOpacity = Svg.parseOpacity(SvgPaint.attribute(element, "stroke-opacity"), inheritedStrokeOpacity);
            return new SvgPaint(color, fill, stroke, strokeWidth, lineCap, lineJoin, Svg.clampOpacity(opacity), fillOpacity, strokeOpacity);
        }

        private static String attribute(Element element, String name) {
            return element == null ? null : element.getAttribute(name);
        }
    }
}

