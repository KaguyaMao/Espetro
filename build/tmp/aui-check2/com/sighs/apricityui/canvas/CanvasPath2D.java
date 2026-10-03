/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasPathSupport;
import com.sighs.apricityui.canvas.CanvasSvgPathParser;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Rectangle2D;

public class CanvasPath2D {
    private final Path2D.Double path = new Path2D.Double();

    public CanvasPath2D() {
    }

    public CanvasPath2D(CanvasPath2D source) {
        this();
        if (source != null) {
            this.path.append(source.path, false);
        }
    }

    public CanvasPath2D(String source) {
        this();
        CanvasSvgPathParser.parseInto(source, this);
    }

    public void setWindingRule(int rule) {
        this.path.setWindingRule(rule);
    }

    Path2D.Double raw() {
        return this.path;
    }

    public Shape asShape() {
        return new Path2D.Double(this.path);
    }

    public void closePath() {
        this.path.closePath();
    }

    public void moveTo(double x, double y) {
        this.path.moveTo(x, y);
    }

    public void lineTo(double x, double y) {
        this.path.lineTo(x, y);
    }

    public void quadraticCurveTo(double cpx, double cpy, double x, double y) {
        this.path.quadTo(cpx, cpy, x, y);
    }

    public void bezierCurveTo(double cp1x, double cp1y, double cp2x, double cp2y, double x, double y) {
        this.path.curveTo(cp1x, cp1y, cp2x, cp2y, x, y);
    }

    public void arcTo(double x1, double y1, double x2, double y2, double radius) {
        CanvasPathSupport.arcTo(this.path, x1, y1, x2, y2, radius);
    }

    public void rect(double x, double y, double width, double height) {
        this.path.append(new Rectangle2D.Double(x, y, width, height), false);
    }

    public void roundRect(double x, double y, double width, double height, Object radii) {
        CanvasPathSupport.appendRoundRect(this.path, x, y, width, height, radii);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle) {
        this.arc(x, y, radius, startAngle, endAngle, false);
    }

    public void arc(double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
        CanvasPathSupport.appendArc(this.path, x, y, radius, startAngle, endAngle, anticlockwise);
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
        this.path.append(transform.createTransformedShape(arc), true);
    }

    public void addPath(CanvasPath2D source) {
        if (source == null) {
            return;
        }
        this.path.append(source.path, false);
    }

    public void addPath(CanvasPath2D source, double a, double b, double c, double d, double e, double f) {
        if (source == null) {
            return;
        }
        Shape transformed = new AffineTransform(a, b, c, d, e, f).createTransformedShape(source.path);
        this.path.append(transformed, false);
    }
}

