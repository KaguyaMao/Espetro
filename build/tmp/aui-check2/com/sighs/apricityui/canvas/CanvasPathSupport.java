/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.canvas;

import com.sighs.apricityui.canvas.CanvasState;
import com.sighs.apricityui.canvas.CanvasStyleUtil;
import java.awt.BasicStroke;
import java.awt.Shape;
import java.awt.geom.Arc2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;

final class CanvasPathSupport {
    private CanvasPathSupport() {
    }

    static void appendRoundRect(Path2D.Double path, double x, double y, double width, double height, Object radiiSpec) {
        if (width == 0.0 || height == 0.0) {
            path.append(new Rectangle2D.Double(x, y, width, height), false);
            return;
        }
        double left = x;
        double top = y;
        double right = x + width;
        double bottom = y + height;
        if (width < 0.0) {
            left = x + width;
            right = x;
            width = -width;
        }
        if (height < 0.0) {
            top = y + height;
            bottom = y;
            height = -height;
        }
        double[] corners = CanvasPathSupport.normalizeCornerRadii(radiiSpec, width, height);
        double tl = corners[0];
        double tr = corners[1];
        double br = corners[2];
        double bl = corners[3];
        path.moveTo(left + tl, top);
        path.lineTo(right - tr, top);
        CanvasPathSupport.appendCorner(path, right - 2.0 * tr, top, 2.0 * tr, 2.0 * tr, 90.0, -90.0);
        path.lineTo(right, bottom - br);
        CanvasPathSupport.appendCorner(path, right - 2.0 * br, bottom - 2.0 * br, 2.0 * br, 2.0 * br, 0.0, -90.0);
        path.lineTo(left + bl, bottom);
        CanvasPathSupport.appendCorner(path, left, bottom - 2.0 * bl, 2.0 * bl, 2.0 * bl, 270.0, -90.0);
        path.lineTo(left, top + tl);
        CanvasPathSupport.appendCorner(path, left, top, 2.0 * tl, 2.0 * tl, 180.0, -90.0);
        path.closePath();
    }

    static void arcTo(Path2D.Double path, double x1, double y1, double x2, double y2, double radius) {
        if (radius < 0.0) {
            return;
        }
        Point2D current = path.getCurrentPoint();
        if (current == null) {
            path.moveTo(x1, y1);
            return;
        }
        double x0 = current.getX();
        double y0 = current.getY();
        if (x0 == x1 && y0 == y1 || x1 == x2 && y1 == y2 || radius == 0.0) {
            path.lineTo(x1, y1);
            return;
        }
        double v1x = x0 - x1;
        double v1y = y0 - y1;
        double v2x = x2 - x1;
        double v2y = y2 - y1;
        double len1 = Math.hypot(v1x, v1y);
        double len2 = Math.hypot(v2x, v2y);
        if (len1 == 0.0 || len2 == 0.0) {
            path.lineTo(x1, y1);
            return;
        }
        double u1x = v1x / len1;
        double u2x = v2x / len2;
        double u1y = v1y / len1;
        double u2y = v2y / len2;
        double dot = CanvasPathSupport.clamp(u1x * u2x + u1y * u2y, -1.0, 1.0);
        if (Math.abs(dot + 1.0) < 1.0E-9 || Math.abs(dot - 1.0) < 1.0E-9) {
            path.lineTo(x1, y1);
            return;
        }
        double angle = Math.acos(dot);
        double tangent = radius / Math.tan(angle / 2.0);
        tangent = Math.min(tangent, Math.min(len1, len2));
        double startX = x1 + u1x * tangent;
        double startY = y1 + u1y * tangent;
        double endX = x1 + u2x * tangent;
        double endY = y1 + u2y * tangent;
        double cross = u1x * u2y - u1y * u2x;
        double sign = cross < 0.0 ? -1.0 : 1.0;
        double nx1 = -u1y * sign;
        double ny1 = u1x * sign;
        double centerX = startX + nx1 * radius;
        double centerY = startY + ny1 * radius;
        double startAngle = Math.atan2(startY - centerY, startX - centerX);
        double endAngle = Math.atan2(endY - centerY, endX - centerX);
        boolean anticlockwise = cross < 0.0;
        path.lineTo(startX, startY);
        CanvasPathSupport.appendArc(path, centerX, centerY, radius, startAngle, endAngle, anticlockwise);
    }

    static boolean isPointInPath(CanvasState state, Shape shape, double x, double y) {
        if (shape == null) {
            return false;
        }
        Shape transformed = state.transform.createTransformedShape(shape);
        return transformed.contains(x, y);
    }

    static boolean isPointInStroke(CanvasState state, Shape shape, double x, double y) {
        if (shape == null) {
            return false;
        }
        Shape transformed = state.transform.createTransformedShape(shape);
        BasicStroke stroke = new BasicStroke((float)Math.max(0.1, state.lineWidth), CanvasStyleUtil.resolveLineCap(state.lineCap), CanvasStyleUtil.resolveLineJoin(state.lineJoin), (float)Math.max(1.0, state.miterLimit), state.lineDash.length == 0 ? null : CanvasStyleUtil.toFloatDashArray(state.lineDash), state.lineDash.length == 0 ? 0.0f : (float)state.lineDashOffset);
        return stroke.createStrokedShape(transformed).contains(x, y);
    }

    static void appendArc(Path2D.Double path, double x, double y, double radius, double startAngle, double endAngle, boolean anticlockwise) {
        double extent;
        if (radius <= 0.0) {
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
        path.append(new Arc2D.Double(x - radius, y - radius, radius * 2.0, radius * 2.0, -startDeg, -extent, 0), true);
    }

    private static void appendCorner(Path2D.Double path, double x, double y, double width, double height, double startDeg, double extentDeg) {
        if (width <= 0.0 || height <= 0.0) {
            return;
        }
        path.append(new Arc2D.Double(x, y, width, height, startDeg, extentDeg, 0), true);
    }

    private static double[] normalizeCornerRadii(Object spec, double width, double height) {
        double[] dArray;
        double[] raw = CanvasPathSupport.toDoubleArray(spec);
        if (raw.length == 0) {
            raw = new double[]{0.0};
        }
        switch (raw.length) {
            case 1: {
                double[] dArray2 = new double[4];
                dArray2[0] = raw[0];
                dArray2[1] = raw[0];
                dArray2[2] = raw[0];
                dArray = dArray2;
                dArray2[3] = raw[0];
                break;
            }
            case 2: {
                double[] dArray3 = new double[4];
                dArray3[0] = raw[0];
                dArray3[1] = raw[1];
                dArray3[2] = raw[0];
                dArray = dArray3;
                dArray3[3] = raw[1];
                break;
            }
            case 3: {
                double[] dArray4 = new double[4];
                dArray4[0] = raw[0];
                dArray4[1] = raw[1];
                dArray4[2] = raw[2];
                dArray = dArray4;
                dArray4[3] = raw[1];
                break;
            }
            default: {
                double[] dArray5 = new double[4];
                dArray5[0] = raw[0];
                dArray5[1] = raw[1];
                dArray5[2] = raw[2];
                dArray = dArray5;
                dArray5[3] = raw[3];
            }
        }
        double[] corners = dArray;
        for (int i = 0; i < corners.length; ++i) {
            corners[i] = Math.max(0.0, corners[i]);
        }
        double topScale = CanvasPathSupport.scaleFactor(corners[0] + corners[1], width);
        double rightScale = CanvasPathSupport.scaleFactor(corners[1] + corners[2], height);
        double bottomScale = CanvasPathSupport.scaleFactor(corners[2] + corners[3], width);
        double leftScale = CanvasPathSupport.scaleFactor(corners[3] + corners[0], height);
        double scale = Math.min(Math.min(topScale, rightScale), Math.min(bottomScale, leftScale));
        if (scale < 1.0) {
            int i = 0;
            while (i < corners.length) {
                int n = i++;
                corners[n] = corners[n] * scale;
            }
        }
        return corners;
    }

    private static double[] toDoubleArray(Object spec) {
        if (spec == null) {
            return new double[0];
        }
        if (spec instanceof Number) {
            Number number = (Number)spec;
            return new double[]{number.doubleValue()};
        }
        if (spec instanceof double[]) {
            double[] values = (double[])spec;
            return (double[])values.clone();
        }
        if (spec instanceof float[]) {
            float[] values = (float[])spec;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = values[i];
            }
            return result;
        }
        if (spec instanceof int[]) {
            int[] values = (int[])spec;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = values[i];
            }
            return result;
        }
        if (spec instanceof long[]) {
            long[] values = (long[])spec;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                result[i] = values[i];
            }
            return result;
        }
        if (spec instanceof Object[]) {
            Object[] values = (Object[])spec;
            double[] result = new double[values.length];
            for (int i = 0; i < values.length; ++i) {
                Object object = values[i];
                if (!(object instanceof Number)) {
                    return new double[0];
                }
                Number number = (Number)object;
                result[i] = number.doubleValue();
            }
            return result;
        }
        if (spec instanceof List) {
            List values = (List)spec;
            double[] result = new double[values.size()];
            for (int i = 0; i < values.size(); ++i) {
                Object value = values.get(i);
                if (!(value instanceof Number)) {
                    return new double[0];
                }
                Number number = (Number)value;
                result[i] = number.doubleValue();
            }
            return result;
        }
        return new double[0];
    }

    private static double scaleFactor(double sum, double limit) {
        if (sum <= 0.0) {
            return 1.0;
        }
        return Math.min(1.0, limit / sum);
    }

    private static double clamp(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        return Math.min(value, max);
    }
}

