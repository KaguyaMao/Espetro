/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Matrix4f
 */
package com.sighs.apricityui.render;

import com.sighs.apricityui.parser.Color;
import com.sighs.apricityui.parser.Gradient;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.render.RenderBatchStats;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.spi.MeshBuilder;
import com.sighs.apricityui.spi.MeshFormat;
import com.sighs.apricityui.spi.MeshMode;
import com.sighs.apricityui.util.MathUtil;
import java.util.ArrayList;
import java.util.List;
import org.joml.Matrix4f;

public class Graph {
    private static final int SEGMENTS = 12;
    private static final int TOTAL_STEPS = 48;
    private static final float[] COS_TABLE = new float[49];
    private static final float[] SIN_TABLE = new float[49];
    private static boolean batchActive = false;
    private static boolean batchHasVertices = false;
    private static boolean batchStarted = false;
    private static boolean batchDepthTest = true;

    public static void vtx(MeshBuilder mesh, Matrix4f mat, float x, float y, int color, float alphaMultiplier) {
        Graph.ensureBatchStarted();
        if (mesh == null) {
            mesh = Base.getMesh();
        }
        if (mesh == null) {
            return;
        }
        mesh.vertex(mat, x, y, color, alphaMultiplier);
        if (batchActive) {
            batchHasVertices = true;
        }
    }

    public static void vtx(MeshBuilder mesh, Matrix4f mat, float x, float y, int color) {
        Graph.vtx(mesh, mat, x, y, color, 1.0f);
    }

    public static void addRect(MeshBuilder mesh, Matrix4f mat, float x0, float y0, float x1, float y1, int color) {
        Graph.addRect(mesh, mat, x0, y0, x1, y1, (x, y) -> color);
    }

    private static void addRect(MeshBuilder mesh, Matrix4f mat, float x0, float y0, float x1, float y1, int cTL, int cBL, int cBR, int cTR) {
        if (Math.abs(x1 - x0) < 0.001f || Math.abs(y1 - y0) < 0.001f) {
            return;
        }
        Graph.vtx(mesh, mat, x0, y0, cTL);
        Graph.vtx(mesh, mat, x0, y1, cBL);
        Graph.vtx(mesh, mat, x1, y1, cBR);
        Graph.vtx(mesh, mat, x0, y0, cTL);
        Graph.vtx(mesh, mat, x1, y1, cBR);
        Graph.vtx(mesh, mat, x1, y0, cTR);
    }

    private static void addRect(MeshBuilder mesh, Matrix4f mat, float x0, float y0, float x1, float y1, ColorResolver colorRes) {
        if (Math.abs(x1 - x0) < 0.001f || Math.abs(y1 - y0) < 0.001f) {
            return;
        }
        int cTL = colorRes.resolve(x0, y0);
        int cBL = colorRes.resolve(x0, y1);
        int cBR = colorRes.resolve(x1, y1);
        int cTR = colorRes.resolve(x1, y0);
        Graph.vtx(mesh, mat, x0, y0, cTL);
        Graph.vtx(mesh, mat, x0, y1, cBL);
        Graph.vtx(mesh, mat, x1, y1, cBR);
        Graph.vtx(mesh, mat, x0, y0, cTL);
        Graph.vtx(mesh, mat, x1, y1, cBR);
        Graph.vtx(mesh, mat, x1, y0, cTR);
    }

    private static void prepare(MeshBuilder mesh) {
        Base.setPositionColorShader();
    }

    public static void beginBatch() {
        if (batchActive) {
            return;
        }
        ImageDrawer.flushBatch();
        batchActive = true;
        batchHasVertices = false;
        batchStarted = false;
        batchDepthTest = true;
    }

    public static void beginLayeredBatch() {
        Graph.endBatch();
        ImageDrawer.flushBatch();
        batchActive = true;
        batchHasVertices = false;
        batchStarted = false;
        batchDepthTest = false;
    }

    public static void endBatch() {
        if (!batchActive) {
            return;
        }
        if (batchStarted) {
            MeshBuilder mesh = Base.getMesh();
            if (mesh != null) {
                mesh.submit();
            }
            Base.setMesh(null);
            if (!batchDepthTest) {
                if (Base.isDepthTestEnabled()) {
                    AuiServices.render().enableDepthTest();
                    AuiServices.render().setDepthMask(true);
                } else {
                    AuiServices.render().disableDepthTest();
                    AuiServices.render().setDepthMask(false);
                }
            }
            Base.finishRendering();
            RenderBatchStats.recordGraphFlush();
        }
        batchActive = false;
        batchHasVertices = false;
        batchStarted = false;
        batchDepthTest = true;
    }

    private static void ensureBatchStarted() {
        if (!batchActive || batchStarted) {
            return;
        }
        MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.TRIANGLES, MeshFormat.POSITION_COLOR);
        Base.setMesh(mesh);
        Base.beginRendering();
        if (!batchDepthTest) {
            AuiServices.render().disableDepthTest();
            AuiServices.render().setDepthMask(false);
        }
        Graph.prepare(mesh);
        batchStarted = true;
    }

    private static void withBatchOrImmediate(Runnable emitVertices) {
        if (batchActive) {
            emitVertices.run();
            return;
        }
        MeshBuilder mesh = AuiServices.render().beginMesh(MeshMode.TRIANGLES, MeshFormat.POSITION_COLOR);
        Base.setMesh(mesh);
        Base.beginRendering();
        Graph.prepare(mesh);
        emitVertices.run();
        mesh.submit();
        Base.setMesh(null);
        Base.finishRendering();
    }

    public static void drawFillRect(Matrix4f matrix, float x0, float y0, float x1, float y1, int color) {
        Graph.withBatchOrImmediate(() -> {
            MeshBuilder mesh = Base.getMesh();
            Graph.addRect(mesh, matrix, x0, y0, x1, y1, color);
        });
    }

    public static void drawUnifiedRoundedRect(Matrix4f mat, float x, float y, float w, float h, float[] radii, int color) {
        Graph.drawUnifiedRoundedRect(mat, x, y, w, h, radii, (float px, float py) -> color);
    }

    public static void drawUnifiedRoundedRect(Matrix4f mat, float x, float y, float w, float h, float[] radii, Gradient gradient) {
        Graph.drawUnifiedRoundedRect(mat, x, y, w, h, radii, (float px, float py) -> gradient.getColorAt(px, py, x, y, w, h));
    }

    public static void drawGradientRect(Matrix4f mat, float x, float y, float w, float h, Gradient gradient) {
        if (gradient == null || w <= 0.0f || h <= 0.0f) {
            return;
        }
        Graph.withBatchOrImmediate(() -> {
            MeshBuilder mesh = Base.getMesh();
            Graph.addLinearGradientVertices(mesh, mat, x, y, w, h, gradient);
        });
    }

    public static boolean requiresStopGeometry(Gradient gradient) {
        return gradient != null && (gradient.hasHardStops() || gradient.stops().size() > 2);
    }

    public static boolean drawAxisAlignedHardStopGradientRect(Matrix4f mat, float x, float y, float w, float h, Gradient gradient) {
        boolean reverse;
        float secondPos;
        boolean horizontal;
        if (gradient == null || w <= 0.0f || h <= 0.0f || gradient.stops().size() != 2) {
            return false;
        }
        Gradient.Stop first = gradient.stops().get(0);
        Gradient.Stop second = gradient.stops().get(1);
        if (first.color == second.color) {
            return false;
        }
        float angle = MathUtil.normalizeAngle(gradient.angle());
        boolean vertical = Math.abs(angle - 180.0f) < 0.01f || Math.abs(angle) < 0.01f;
        boolean bl = horizontal = Math.abs(angle - 90.0f) < 0.01f || Math.abs(angle - 270.0f) < 0.01f;
        if (!vertical && !horizontal) {
            return false;
        }
        float axis = vertical ? h : w;
        float firstPos = MathUtil.clamp01(first.position) * axis;
        if (Math.abs(firstPos - (secondPos = MathUtil.clamp01(second.position) * axis)) > 0.001f) {
            return false;
        }
        float stop = Math.max(0.0f, Math.min(axis, firstPos));
        int beforeColor = first.color;
        int afterColor = second.color;
        boolean bl2 = reverse = Math.abs(angle) < 0.01f || Math.abs(angle - 270.0f) < 0.01f;
        if (reverse) {
            stop = axis - stop;
            beforeColor = second.color;
            afterColor = first.color;
        }
        float stopValue = stop;
        int before = beforeColor;
        int after = afterColor;
        Graph.withBatchOrImmediate(() -> {
            MeshBuilder mesh = Base.getMesh();
            Graph.addAxisAlignedHardStopVertices(mesh, mat, x, y, w, h, vertical, stopValue, before, after);
        });
        return true;
    }

    public static boolean drawAxisAlignedStopGradientRect(Matrix4f mat, float x, float y, float w, float h, Gradient gradient) {
        boolean horizontal;
        if (gradient == null || w <= 0.0f || h <= 0.0f || gradient.stops().size() < 2) {
            return false;
        }
        float angle = MathUtil.normalizeAngle(gradient.angle());
        boolean vertical = Math.abs(angle - 180.0f) < 0.01f || Math.abs(angle) < 0.01f;
        boolean bl = horizontal = Math.abs(angle - 90.0f) < 0.01f || Math.abs(angle - 270.0f) < 0.01f;
        if (!vertical && !horizontal) {
            return false;
        }
        Graph.withBatchOrImmediate(() -> {
            MeshBuilder mesh = Base.getMesh();
            Graph.addAxisAlignedStopGradientVertices(mesh, mat, x, y, w, h, gradient, vertical, angle);
        });
        return true;
    }

    private static void addAxisAlignedStopGradientVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float w, float h, Gradient gradient, boolean vertical, float angle) {
        float axis = vertical ? h : w;
        boolean reverse = Math.abs(angle) < 0.01f || Math.abs(angle - 270.0f) < 0.01f;
        for (int i = 0; i < gradient.stops().size() - 1; ++i) {
            int colorTo;
            Gradient.Stop start = gradient.stops().get(i);
            Gradient.Stop end = gradient.stops().get(i + 1);
            float a = MathUtil.clamp01(start.position) * axis;
            float b = MathUtil.clamp01(end.position) * axis;
            if (Math.abs(b - a) <= 0.001f) continue;
            float from = Math.min(a, b);
            float to = Math.max(a, b);
            int colorFrom = a <= b ? start.color : end.color;
            int n = colorTo = a <= b ? end.color : start.color;
            if (reverse) {
                float rf = axis - to;
                float rt = axis - from;
                from = rf;
                to = rt;
                int tmp = colorFrom;
                colorFrom = colorTo;
                colorTo = tmp;
            }
            Graph.addAxisAlignedSegment(mesh, mat, x, y, w, h, vertical, from, to, colorFrom, colorTo);
        }
    }

    private static void addAxisAlignedSegment(MeshBuilder mesh, Matrix4f mat, float x, float y, float w, float h, boolean vertical, float from, float to, int colorFrom, int colorTo) {
        ColorResolver colorRes;
        if (to - from <= 0.001f) {
            return;
        }
        if (colorFrom == colorTo) {
            if (vertical) {
                Graph.addRect(mesh, mat, x, y + from, x + w, y + to, colorFrom);
            } else {
                Graph.addRect(mesh, mat, x + from, y, x + to, y + h, colorFrom);
            }
            return;
        }
        ColorResolver colorResolver = colorRes = vertical ? (px, py) -> (int)Color.mixColors(colorFrom, colorTo, (py - (y + from)) / Math.max(1.0f, to - from)) : (px, py) -> (int)Color.mixColors(colorFrom, colorTo, (px - (x + from)) / Math.max(1.0f, to - from));
        if (vertical) {
            Graph.addRect(mesh, mat, x, y + from, x + w, y + to, colorRes);
        } else {
            Graph.addRect(mesh, mat, x + from, y, x + to, y + h, colorRes);
        }
    }

    private static void addAxisAlignedHardStopVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float w, float h, boolean vertical, float stop, int beforeColor, int afterColor) {
        if (vertical) {
            if (stop > 0.001f) {
                Graph.addRect(mesh, mat, x, y, x + w, y + stop, beforeColor);
            }
            if (h - stop > 0.001f) {
                Graph.addRect(mesh, mat, x, y + stop, x + w, y + h, afterColor);
            }
        } else {
            if (stop > 0.001f) {
                Graph.addRect(mesh, mat, x, y, x + stop, y + h, beforeColor);
            }
            if (w - stop > 0.001f) {
                Graph.addRect(mesh, mat, x + stop, y, x + w, y + h, afterColor);
            }
        }
    }

    private static void addLinearGradientVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float w, float h, Gradient gradient) {
        List<Gradient.Stop> stops = gradient.stops();
        if (stops.isEmpty()) {
            return;
        }
        if (stops.size() == 1) {
            Graph.addRect(mesh, mat, x, y, x + w, y + h, stops.get((int)0).color);
            return;
        }
        GradientVertex[] rectangle = Graph.gradientRectangle(x, y, w, h, gradient.angle());
        Gradient.Stop first = stops.get(0);
        float firstPosition = MathUtil.clamp01(first.position);
        Graph.addGradientBand(mesh, mat, rectangle, 0.0f, firstPosition, first.color, first.color);
        for (int i = 0; i < stops.size() - 1; ++i) {
            Gradient.Stop start = stops.get(i);
            Gradient.Stop end = stops.get(i + 1);
            Graph.addGradientBand(mesh, mat, rectangle, MathUtil.clamp01(start.position), MathUtil.clamp01(end.position), start.color, end.color);
        }
        Gradient.Stop last = stops.get(stops.size() - 1);
        Graph.addGradientBand(mesh, mat, rectangle, MathUtil.clamp01(last.position), 1.0f, last.color, last.color);
    }

    private static GradientVertex[] gradientRectangle(float x, float y, float w, float h, float angle) {
        double radians = Math.toRadians(90.0f - angle);
        float cos = (float)Math.cos(radians);
        float sin = (float)Math.sin(radians);
        float centerX = x + w * 0.5f;
        float centerY = y + h * 0.5f;
        float maxDistance = Math.abs(w * 0.5f * cos) + Math.abs(h * 0.5f * sin);
        if (maxDistance <= 1.0E-4f) {
            maxDistance = 1.0f;
        }
        return new GradientVertex[]{Graph.gradientVertex(x, y, centerX, centerY, cos, sin, maxDistance), Graph.gradientVertex(x, y + h, centerX, centerY, cos, sin, maxDistance), Graph.gradientVertex(x + w, y + h, centerX, centerY, cos, sin, maxDistance), Graph.gradientVertex(x + w, y, centerX, centerY, cos, sin, maxDistance)};
    }

    private static GradientVertex gradientVertex(float x, float y, float centerX, float centerY, float cos, float sin, float maxDistance) {
        float projection = (x - centerX) * cos + (y - centerY) * -sin;
        float t = 0.5f + projection / (maxDistance * 2.0f);
        return new GradientVertex(x, y, MathUtil.clamp01(t));
    }

    private static void addGradientBand(MeshBuilder mesh, Matrix4f mat, GradientVertex[] rectangle, float from, float to, int fromColor, int toColor) {
        if (to - from <= 1.0E-4f) {
            return;
        }
        List<GradientVertex> polygon = new ArrayList<GradientVertex>(6);
        for (GradientVertex vertex : rectangle) {
            polygon.add(vertex);
        }
        polygon = Graph.clipGradientPolygon(polygon, from, true);
        if ((polygon = Graph.clipGradientPolygon(polygon, to, false)).size() < 3) {
            return;
        }
        GradientVertex anchor = polygon.get(0);
        for (int i = 1; i < polygon.size() - 1; ++i) {
            Graph.addGradientTriangle(mesh, mat, anchor, polygon.get(i), polygon.get(i + 1), from, to, fromColor, toColor);
        }
    }

    private static List<GradientVertex> clipGradientPolygon(List<GradientVertex> source, float boundary, boolean keepAbove) {
        if (source.isEmpty()) {
            return source;
        }
        ArrayList<GradientVertex> clipped = new ArrayList<GradientVertex>(source.size() + 1);
        GradientVertex previous = source.get(source.size() - 1);
        boolean previousInside = keepAbove ? previous.t >= boundary : previous.t <= boundary;
        for (GradientVertex current : source) {
            boolean currentInside;
            boolean bl = keepAbove ? current.t >= boundary : (currentInside = current.t <= boundary);
            if (currentInside != previousInside) {
                float delta = current.t - previous.t;
                float ratio = Math.abs(delta) <= 1.0E-6f ? 0.0f : (boundary - previous.t) / delta;
                clipped.add(new GradientVertex(previous.x + (current.x - previous.x) * ratio, previous.y + (current.y - previous.y) * ratio, boundary));
            }
            if (currentInside) {
                clipped.add(current);
            }
            previous = current;
            previousInside = currentInside;
        }
        return clipped;
    }

    private static void addGradientTriangle(MeshBuilder mesh, Matrix4f mat, GradientVertex a, GradientVertex b, GradientVertex c, float from, float to, int fromColor, int toColor) {
        Graph.vtx(mesh, mat, a.x, a.y, Graph.gradientBandColor(a.t, from, to, fromColor, toColor));
        Graph.vtx(mesh, mat, b.x, b.y, Graph.gradientBandColor(b.t, from, to, fromColor, toColor));
        Graph.vtx(mesh, mat, c.x, c.y, Graph.gradientBandColor(c.t, from, to, fromColor, toColor));
    }

    private static int gradientBandColor(float t, float from, float to, int fromColor, int toColor) {
        if (fromColor == toColor) {
            return fromColor;
        }
        return Graph.lerpColor(fromColor, toColor, MathUtil.clamp01((t - from) / Math.max(1.0E-6f, to - from)));
    }

    private static void drawUnifiedRoundedRect(Matrix4f mat, float x, float y, float w, float h, float[] radii, ColorResolver colorRes) {
        Graph.withBatchOrImmediate(() -> {
            MeshBuilder mesh = Base.getMesh();
            Graph.addUnifiedRoundedRectVertices(mesh, mat, x, y, w, h, radii, colorRes);
        });
    }

    public static void addUnifiedRoundedRectVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float width, float height, float[] radii, int color) {
        Graph.addUnifiedRoundedRectVertices(mesh, mat, x, y, width, height, radii, (px, py) -> color);
    }

    private static float[] expandRadii(float[] radii) {
        if (radii == null) {
            return new float[8];
        }
        if (radii.length >= 8) {
            return radii;
        }
        float[] expanded = new float[8];
        for (int i = 0; i < 4; ++i) {
            float v;
            expanded[i * 2] = v = i < radii.length ? radii[i] : 0.0f;
            expanded[i * 2 + 1] = v;
        }
        return expanded;
    }

    public static void addUnifiedRoundedRectVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float width, float height, float[] radii, ColorResolver colorRes) {
        float[] r = Graph.expandRadii(radii);
        float tlH = r[0];
        float tlV = r[1];
        float trH = r[2];
        float trV = r[3];
        float brH = r[4];
        float brV = r[5];
        float blH = r[6];
        float blV = r[7];
        if (tlH > 0.0f && tlV > 0.0f) {
            Graph.addCorner(mesh, mat, x + tlH, y + tlV, tlH, tlV, 24, colorRes);
        }
        if (trH > 0.0f && trV > 0.0f) {
            Graph.addCorner(mesh, mat, x + width - trH, y + trV, trH, trV, 36, colorRes);
        }
        if (brH > 0.0f && brV > 0.0f) {
            Graph.addCorner(mesh, mat, x + width - brH, y + height - brV, brH, brV, 0, colorRes);
        }
        if (blH > 0.0f && blV > 0.0f) {
            Graph.addCorner(mesh, mat, x + blH, y + height - blV, blH, blV, 12, colorRes);
        }
        float maxTopR = Math.max(tlV, trV);
        float maxBottomR = Math.max(blV, brV);
        Graph.addRect(mesh, mat, x + tlH, y, x + width - trH, y + maxTopR, colorRes);
        Graph.addRect(mesh, mat, x + blH, y + height - maxBottomR, x + width - brH, y + height, colorRes);
        float midY1 = y + maxTopR;
        float midY2 = y + height - maxBottomR;
        if (midY1 < midY2) {
            Graph.addRect(mesh, mat, x, midY1, x + width, midY2, colorRes);
        }
        if (maxTopR > tlV) {
            Graph.addRect(mesh, mat, x, y + tlV, x + tlH, y + maxTopR, colorRes);
        }
        if (maxTopR > trV) {
            Graph.addRect(mesh, mat, x + width - trH, y + trV, x + width, y + maxTopR, colorRes);
        }
        if (maxBottomR > blV) {
            Graph.addRect(mesh, mat, x, y + height - maxBottomR, x + blH, y + height - blV, colorRes);
        }
        if (maxBottomR > brV) {
            Graph.addRect(mesh, mat, x + width - brH, y + height - maxBottomR, x + width, y + height - brV, colorRes);
        }
    }

    public static void addEllipseGeometry(MeshBuilder mesh, Matrix4f mat, float cx, float cy, float rx, float ry, int color) {
        for (int i = 0; i < 48; ++i) {
            Graph.vtx(mesh, mat, cx, cy, color);
            Graph.vtx(mesh, mat, cx + COS_TABLE[i] * rx, cy + SIN_TABLE[i] * ry, color);
            Graph.vtx(mesh, mat, cx + COS_TABLE[i + 1] * rx, cy + SIN_TABLE[i + 1] * ry, color);
        }
    }

    private static void addCorner(MeshBuilder mesh, Matrix4f mat, float cx, float cy, float r, int startIndex, int color) {
        Graph.addCorner(mesh, mat, cx, cy, r, r, startIndex, (px, py) -> color);
    }

    private static void addCorner(MeshBuilder mesh, Matrix4f mat, float cx, float cy, float rx, float ry, int startIndex, ColorResolver colorRes) {
        int centerColor = colorRes.resolve(cx, cy);
        for (int i = 0; i < 12; ++i) {
            int idx0 = startIndex + i;
            int idx1 = startIndex + i + 1;
            if (idx1 >= 48) {
                idx1 -= 48;
            }
            float x0 = cx + COS_TABLE[idx0] * rx;
            float y0 = cy + SIN_TABLE[idx0] * ry;
            float x1 = cx + COS_TABLE[idx1] * rx;
            float y1 = cy + SIN_TABLE[idx1] * ry;
            int c0 = colorRes.resolve(x0, y0);
            int c1 = colorRes.resolve(x1, y1);
            Graph.vtx(mesh, mat, cx, cy, centerColor);
            Graph.vtx(mesh, mat, x0, y0, c0);
            Graph.vtx(mesh, mat, x1, y1, c1);
        }
    }

    public static void drawUnifiedShadow(Matrix4f mat, float x, float y, float w, float h, float[] radii, float blur, int innerColor, int outerColor) {
        Graph.withBatchOrImmediate(() -> {
            MeshBuilder mesh = Base.getMesh();
            Graph.addUnifiedRoundedRectVertices(mesh, mat, x, y, w, h, radii, innerColor);
            Graph.addUnifiedShadowRingVertices(mesh, mat, x, y, w, h, radii, blur, innerColor, outerColor);
        });
    }

    public static void addUnifiedShadowRingVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float width, float height, float[] radii, float blur, int inC, int outC) {
        float[] r = Graph.expandRadii(radii);
        float tlH = r[0];
        float tlV = r[1];
        float trH = r[2];
        float trV = r[3];
        float brH = r[4];
        float brV = r[5];
        float blH = r[6];
        float blV = r[7];
        Graph.addRect(mesh, mat, x + tlH, y - blur, x + width - trH, y, outC, inC, inC, outC);
        Graph.addRect(mesh, mat, x + blH, y + height, x + width - brH, y + height + blur, inC, outC, outC, inC);
        Graph.addRect(mesh, mat, x - blur, y + tlV, x, y + height - blV, outC, outC, inC, inC);
        Graph.addRect(mesh, mat, x + width, y + trV, x + width + blur, y + height - brV, inC, inC, outC, outC);
        if (tlH > 0.0f && tlV > 0.0f || blur > 0.0f) {
            Graph.addCornerShadow(mesh, mat, x + tlH, y + tlV, tlH, tlV, tlH + blur, tlV + blur, 24, inC, outC);
        }
        if (trH > 0.0f && trV > 0.0f || blur > 0.0f) {
            Graph.addCornerShadow(mesh, mat, x + width - trH, y + trV, trH, trV, trH + blur, trV + blur, 36, inC, outC);
        }
        if (brH > 0.0f && brV > 0.0f || blur > 0.0f) {
            Graph.addCornerShadow(mesh, mat, x + width - brH, y + height - brV, brH, brV, brH + blur, brV + blur, 0, inC, outC);
        }
        if (blH > 0.0f && blV > 0.0f || blur > 0.0f) {
            Graph.addCornerShadow(mesh, mat, x + blH, y + height - blV, blH, blV, blH + blur, blV + blur, 12, inC, outC);
        }
    }

    private static void addCornerShadow(MeshBuilder mesh, Matrix4f mat, float cx, float cy, float rInX, float rInY, float rOutX, float rOutY, int startIndex, int inC, int outC) {
        for (int i = 0; i < 12; ++i) {
            int idx0 = startIndex + i;
            int idx1 = startIndex + i + 1;
            if (idx1 >= 48) {
                idx1 -= 48;
            }
            float c0 = COS_TABLE[idx0];
            float s0 = SIN_TABLE[idx0];
            float c1 = COS_TABLE[idx1];
            float s1 = SIN_TABLE[idx1];
            float ix0 = cx + c0 * rInX;
            float iy0 = cy + s0 * rInY;
            float ix1 = cx + c1 * rInX;
            float iy1 = cy + s1 * rInY;
            float ox0 = cx + c0 * rOutX;
            float oy0 = cy + s0 * rOutY;
            float ox1 = cx + c1 * rOutX;
            float oy1 = cy + s1 * rOutY;
            Graph.vtx(mesh, mat, ix0, iy0, inC);
            Graph.vtx(mesh, mat, ox0, oy0, outC);
            Graph.vtx(mesh, mat, ix1, iy1, inC);
            Graph.vtx(mesh, mat, ox0, oy0, outC);
            Graph.vtx(mesh, mat, ox1, oy1, outC);
            Graph.vtx(mesh, mat, ix1, iy1, inC);
        }
    }

    public static void drawComplexRoundedBorder(Matrix4f mat, float x, float y, float w, float h, float[] radii, float[] borders, int[] colors) {
        Graph.withBatchOrImmediate(() -> Graph.addComplexRoundedBorderVertices(Base.getMesh(), mat, x, y, w, h, radii, borders, colors));
    }

    private static void addComplexRoundedBorderVertices(MeshBuilder mesh, Matrix4f mat, float x, float y, float w, float h, float[] radii, float[] borders, int[] colors) {
        float tW = borders[0];
        float rW = borders[1];
        float bW = borders[2];
        float lW = borders[3];
        int tC = colors[0];
        int rC = colors[1];
        int bC = colors[2];
        int lC = colors[3];
        float[] r = Graph.expandRadii(radii);
        float tlH = r[0];
        float tlV = r[1];
        float trH = r[2];
        float trV = r[3];
        float brH = r[4];
        float brV = r[5];
        float blH = r[6];
        float blV = r[7];
        if (tW > 0.0f) {
            Graph.addRect(mesh, mat, x + tlH, y, x + w - trH, y + tW, tC);
        }
        if (bW > 0.0f) {
            Graph.addRect(mesh, mat, x + blH, y + h - bW, x + w - brH, y + h, bC);
        }
        if (lW > 0.0f) {
            Graph.addRect(mesh, mat, x, y + tlV, x + lW, y + h - blV, lC);
        }
        if (rW > 0.0f) {
            Graph.addRect(mesh, mat, x + w - rW, y + trV, x + w, y + h - brV, rC);
        }
        if (tlH > 0.0f && tlV > 0.0f || tW > 0.0f || lW > 0.0f) {
            Graph.addComplexCorner(mesh, mat, x + tlH, y + tlV, tlH, tlV, lW, tW, 24, lW > 0.0f ? lC : tC, tW > 0.0f ? tC : lC);
        }
        if (trH > 0.0f && trV > 0.0f || tW > 0.0f || rW > 0.0f) {
            Graph.addComplexCorner(mesh, mat, x + w - trH, y + trV, trH, trV, rW, tW, 36, tW > 0.0f ? tC : rC, rW > 0.0f ? rC : tC);
        }
        if (brH > 0.0f && brV > 0.0f || rW > 0.0f || bW > 0.0f) {
            Graph.addComplexCorner(mesh, mat, x + w - brH, y + h - brV, brH, brV, rW, bW, 0, rW > 0.0f ? rC : bC, bW > 0.0f ? bC : rC);
        }
        if (blH > 0.0f && blV > 0.0f || bW > 0.0f || lW > 0.0f) {
            Graph.addComplexCorner(mesh, mat, x + blH, y + h - blV, blH, blV, lW, bW, 12, bW > 0.0f ? bC : lC, lW > 0.0f ? lC : bC);
        }
    }

    public static void drawCursor(Matrix4f mat, float x, float y, float height, int color, long lastBlinkTime) {
        boolean blink;
        boolean bl = blink = (System.currentTimeMillis() - lastBlinkTime) % 1000L < 500L;
        if (blink) {
            Graph.withBatchOrImmediate(() -> {
                MeshBuilder mesh = Base.getMesh();
                Graph.addRect(mesh, mat, x - 0.7f, y, x, y + height, color | 0xFF000000);
            });
        }
    }

    private static void addComplexCorner(MeshBuilder mesh, Matrix4f mat, float cx, float cy, float rx, float ry, float thX, float thY, int startIndex, int cS, int cE) {
        for (int i = 0; i < 12; ++i) {
            int idx1 = startIndex + i;
            int idx2 = startIndex + i + 1;
            if (idx2 >= 48) {
                idx2 -= 48;
            }
            float cos1 = COS_TABLE[idx1];
            float sin1 = SIN_TABLE[idx1];
            float cos2 = COS_TABLE[idx2];
            float sin2 = SIN_TABLE[idx2];
            float t1 = (float)i / 12.0f;
            float t2 = (float)(i + 1) / 12.0f;
            float inRx = Math.max(0.0f, rx - thX);
            float inRy = Math.max(0.0f, ry - thY);
            int color1 = Graph.lerpColor(cS, cE, t1);
            int color2 = Graph.lerpColor(cS, cE, t2);
            Graph.vtx(mesh, mat, cx + cos1 * rx, cy + sin1 * ry, color1);
            Graph.vtx(mesh, mat, cx + cos1 * inRx, cy + sin1 * inRy, color1);
            Graph.vtx(mesh, mat, cx + cos2 * inRx, cy + sin2 * inRy, color2);
            Graph.vtx(mesh, mat, cx + cos1 * rx, cy + sin1 * ry, color1);
            Graph.vtx(mesh, mat, cx + cos2 * inRx, cy + sin2 * inRy, color2);
            Graph.vtx(mesh, mat, cx + cos2 * rx, cy + sin2 * ry, color2);
        }
    }

    private static int lerpColor(int c1, int c2, float t) {
        if (c1 == c2) {
            return c1;
        }
        int a1 = c1 >> 24 & 0xFF;
        int r1 = c1 >> 16 & 0xFF;
        int g1 = c1 >> 8 & 0xFF;
        int b1 = c1 & 0xFF;
        int a2 = c2 >> 24 & 0xFF;
        int r2 = c2 >> 16 & 0xFF;
        int g2 = c2 >> 8 & 0xFF;
        int b2 = c2 & 0xFF;
        return (int)((float)a1 + (float)(a2 - a1) * t) << 24 | (int)((float)r1 + (float)(r2 - r1) * t) << 16 | (int)((float)g1 + (float)(g2 - g1) * t) << 8 | (int)((float)b1 + (float)(b2 - b1) * t);
    }

    static {
        double stepAngle = 7.5;
        for (int i = 0; i <= 48; ++i) {
            double angleRad = Math.toRadians((double)i * stepAngle);
            Graph.COS_TABLE[i] = (float)Math.cos(angleRad);
            Graph.SIN_TABLE[i] = (float)Math.sin(angleRad);
        }
    }

    @FunctionalInterface
    public static interface ColorResolver {
        public int resolve(float var1, float var2);
    }

    private record GradientVertex(float x, float y, float t) {
    }
}

