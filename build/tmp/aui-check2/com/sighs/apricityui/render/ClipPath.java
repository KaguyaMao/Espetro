/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.Graph;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.joml.Matrix4f;

public class ClipPath {
    private static final Pattern FUNC_PATTERN = Pattern.compile("([a-z-]+)\\((.*)\\)");

    public static void drawToStencil(Matrix4f mat, float x, float y, float w, float h, String clipPathValue) {
        if (clipPathValue == null || clipPathValue.equals("none")) {
            return;
        }
        Matcher matcher = FUNC_PATTERN.matcher(clipPathValue.trim());
        if (!matcher.find()) {
            return;
        }
        String type = matcher.group(1);
        String args = matcher.group(2);
        switch (type) {
            case "polygon": {
                ClipPath.drawPolygon(mat, x, y, w, h, args);
                break;
            }
            case "circle": {
                ClipPath.drawCircle(mat, x, y, w, h, args);
                break;
            }
            case "ellipse": {
                ClipPath.drawEllipse(mat, x, y, w, h, args);
                break;
            }
            case "inset": {
                ClipPath.drawInset(mat, x, y, w, h, args);
            }
        }
    }

    private static void drawPolygon(Matrix4f mat, float x, float y, float w, float h, String args) {
        int i;
        String[] points = args.split("\\s*,\\s*");
        if (points.length < 3) {
            return;
        }
        float[] px = new float[points.length];
        float[] py = new float[points.length];
        float cx = 0.0f;
        float cy = 0.0f;
        for (i = 0; i < points.length; ++i) {
            String[] coords = points[i].trim().split("\\s+");
            px[i] = x + ClipPath.parseLength(coords[0], w);
            py[i] = y + ClipPath.parseLength(coords[1], h);
            cx += px[i];
            cy += py[i];
        }
        cx /= (float)points.length;
        cy /= (float)points.length;
        for (i = 0; i < points.length; ++i) {
            Graph.vtx(Base.getMesh(), mat, cx, cy, -1);
            Graph.vtx(Base.getMesh(), mat, px[i], py[i], -1);
            Graph.vtx(Base.getMesh(), mat, px[(i + 1) % points.length], py[(i + 1) % points.length], -1);
        }
    }

    private static void drawCircle(Matrix4f mat, float x, float y, float w, float h, String args) {
        float r = ClipPath.parseLength(args.split(" at ")[0], (float)Math.sqrt(w * w + h * h) / 1.4142f);
        float[] center = ClipPath.parsePosition(args, x, y, w, h);
        Graph.addEllipseGeometry(Base.getMesh(), mat, center[0], center[1], r, r, -1);
    }

    private static void drawEllipse(Matrix4f mat, float x, float y, float w, float h, String args) {
        String[] parts = args.split(" at ");
        String[] radii = parts[0].trim().split("\\s+");
        float rx = ClipPath.parseLength(radii[0], w);
        float ry = radii.length > 1 ? ClipPath.parseLength(radii[1], h) : rx;
        float[] center = ClipPath.parsePosition(args, x, y, w, h);
        Graph.addEllipseGeometry(Base.getMesh(), mat, center[0], center[1], rx, ry, -1);
    }

    private static void drawInset(Matrix4f mat, float x, float y, float w, float h, String args) {
        String[] parts = args.split(" round ")[0].trim().split("\\s+");
        float t = ClipPath.parseLength(parts[0], h);
        float r = parts.length > 1 ? ClipPath.parseLength(parts[1], w) : t;
        float b = parts.length > 2 ? ClipPath.parseLength(parts[2], h) : t;
        float l = parts.length > 3 ? ClipPath.parseLength(parts[3], w) : r;
        Graph.addRect(Base.getMesh(), mat, x + l, y + t, x + w - r, y + h - b, -1);
    }

    private static float[] parsePosition(String args, float x, float y, float w, float h) {
        if (!args.contains(" at ")) {
            return new float[]{x + w / 2.0f, y + h / 2.0f};
        }
        String[] pos = args.split(" at ")[1].trim().split("\\s+");
        return new float[]{x + ClipPath.parseLength(pos[0], w), y + ClipPath.parseLength(pos[1], h)};
    }

    private static float parseLength(String val, float ref) {
        Double resolved = Size.tryResolveLength(val, ref);
        return resolved == null ? 0.0f : resolved.floatValue();
    }
}

