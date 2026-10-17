package org.espetro.client.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;

import org.espetro.support.FireRequestBoard.Bounds;
import org.espetro.team.ArtilleryZoneBridge;

import java.lang.reflect.*;

/** Optional ESPoints projection; never calls its immediate-fire selection methods. */
final class FireSupportMap {
    private Object hud, viewport;
    private Method render, contains, worldX, worldZ, screenX, screenZ, zoom;
    private Field viewportField;
    private Method nativeRectangle;
    private Constructor<?> nativeZone;
    private int x, y, w, h;
    private double scale = 1;
    private boolean mapped;
    private Bounds bounds;

    FireSupportMap() {
        try {
            Class<?> type = Class.forName("com.example.espoints.hud.TacticalMapHUD");
            hud = type.getMethod("getInstance").invoke(null);
            render =
                    type.getMethod(
                            "renderEmbeddedMap",
                            GuiGraphics.class,
                            int.class,
                            int.class,
                            int.class,
                            int.class,
                            float.class);
            zoom = type.getMethod("zoomFromMouseWheel", double.class);
            viewportField = type.getDeclaredField("lastInteractiveViewport");
            viewportField.setAccessible(true);
            Class<?> v = viewportField.getType();
            contains = method(v, "containsScreen", double.class, double.class);
            worldX = method(v, "worldX", double.class);
            worldZ = method(v, "worldZ", double.class);
            screenX = method(v, "screenXd", double.class);
            screenZ = method(v, "screenYd", double.class);
        } catch (ReflectiveOperationException | LinkageError e) {
            hud = null;
        }
        // Reuse ESPoints' existing red coverage renderer when that version provides it.
        // A local preview object is never added to its active artillery-zone cache.
        if (hud != null) {
            try {
                Class<?> zone =
                        Class.forName("com.example.espoints.tactical.ArtilleryZoneState$Zone");
                nativeZone =
                        zone.getConstructor(
                                String.class,
                                double.class,
                                double.class,
                                double.class,
                                double.class,
                                double.class,
                                ResourceLocation.class,
                                long.class,
                                String.class);
                nativeRectangle =
                        method(
                                hud.getClass(),
                                "drawBarrageRect",
                                GuiGraphics.class,
                                viewportField.getType(),
                                zone,
                                int.class,
                                int.class,
                                int.class,
                                int.class);
            } catch (ReflectiveOperationException | LinkageError ignored) {
                nativeRectangle = null;
            }
        }
    }

    private static Method method(Class<?> t, String n, Class<?>... a)
            throws ReflectiveOperationException {
        var m = t.getDeclaredMethod(n, a);
        m.setAccessible(true);
        return m;
    }

    void render(
            GuiGraphics g, int x, int y, int w, int h, float partial, double scale, Bounds bounds) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.scale = scale;
        this.bounds = bounds;
        mapped = false;
        g.fill(x, y, x + w, y + h, 0xff15191c);
        if (hud != null && bounds != null) {
            g.flush();
            g.pose().pushPose();
            g.pose().scale((float) (1 / scale), (float) (1 / scale), 1);
            try {
                render.invoke(
                        hud,
                        g,
                        (int) (x * scale),
                        (int) (y * scale),
                        (int) (w * scale),
                        (int) (h * scale),
                        partial);
                viewport = viewportField.get(hud);
                mapped = viewport != null;
            } catch (ReflectiveOperationException e) {
                viewport = null;
            } finally {
                g.flush();
                g.pose().popPose();
            }
        }
        if (!mapped) {
            g.fill(x, y, x + w, y + h, 0xff15191c);
            if (bounds != null) fallbackGrid(g);
            FireSupportScreen.text(
                    g,
                    bounds == null ? "本局地图不可用" : "坐标视图 · 未连接 ESPoints 地图",
                    x + 8,
                    y + h - 17,
                    EspetroAuiWidgets.DIM,
                    9,
                    w - 16);
        }
    }

    private void fallbackGrid(GuiGraphics g) {
        double cellX = 150 * w / (bounds.maxX() - bounds.minX()),
                cellZ = 150 * h / (bounds.maxZ() - bounds.minZ());
        long columns = (long) Math.ceil((bounds.maxX() - bounds.minX()) / 150),
                rows = (long) Math.ceil((bounds.maxZ() - bounds.minZ()) / 150);
        long sx = Math.max(1, (long) Math.ceil(6 / cellX)),
                sz = Math.max(1, (long) Math.ceil(6 / cellZ));
        // Hide crowded lines without changing the fixed world-space cell size.
        for (long i = 0; i < columns; i += sx) {
            int xx = x + (int) (i * cellX);
            g.fill(xx, y, xx + 1, y + h, 0xff363c40);
            if (cellX >= 35) {
                StringBuilder label = new StringBuilder();
                for (long n = i + 1; n > 0; n = (n - 1) / 26)
                    label.append((char) ('A' + (n - 1) % 26));
                FireSupportScreen.text(
                        g,
                        label.reverse().toString(),
                        xx + 5,
                        y + 3,
                        EspetroAuiWidgets.DIM,
                        9,
                        Math.min(cellX - 8, x + w - xx - 6));
            }
        }
        for (long i = 0; i < rows; i += sz) {
            int yy = y + (int) (i * cellZ);
            g.fill(x, yy, x + w, yy + 1, 0xff363c40);
            if (cellZ >= 25)
                FireSupportScreen.text(
                        g, Long.toString(i + 1), x + 3, yy + 13, EspetroAuiWidgets.DIM, 9, 30);
        }
    }

    double[] pick(double mx, double my) {
        if (bounds == null || mx < x || mx >= x + w || my < y || my >= y + h) return null;
        double[] result;
        if (mapped) {
            try {
                if (!(boolean) contains.invoke(viewport, mx * scale, my * scale)) return null;
                result =
                        new double[] {
                            ((Number) worldX.invoke(viewport, mx * scale)).doubleValue(),
                            ((Number) worldZ.invoke(viewport, my * scale)).doubleValue()
                        };
            } catch (ReflectiveOperationException e) {
                return null;
            }
        } else
            result =
                    new double[] {
                        bounds.minX() + (mx - x) / w * (bounds.maxX() - bounds.minX()),
                        bounds.minZ() + (my - y) / h * (bounds.maxZ() - bounds.minZ())
                    };
        return bounds.contains(result[0], result[1]) ? result : null;
    }

    double[] project(double wx, double wz) {
        if (bounds == null) return null;
        double[] p;
        if (mapped) {
            try {
                p =
                        new double[] {
                            ((Number) screenX.invoke(viewport, wx)).doubleValue() / scale,
                            ((Number) screenZ.invoke(viewport, wz)).doubleValue() / scale
                        };
            } catch (ReflectiveOperationException e) {
                return null;
            }
        } else
            p =
                    new double[] {
                        x + (wx - bounds.minX()) / (bounds.maxX() - bounds.minX()) * w,
                        y + (wz - bounds.minZ()) / (bounds.maxZ() - bounds.minZ()) * h
                    };
        // Keep offscreen vertices: a rectangle crossing the viewport must still be drawn.
        return p;
    }

    void range(GuiGraphics g, double wx, double wz, double ex, double ez) {
        if (!Double.isFinite(wx) || !Double.isFinite(wz)) return;
        double[] a = project(wx, wz);
        if (a == null) return;
        if (!Double.isFinite(ex) || !Double.isFinite(ez)) {
            pin(g, a, 0xffe04040);
            return;
        }
        if (mapped && nativeRectangle != null) {
            g.flush();
            g.pose().pushPose();
            g.pose().scale((float) (1 / scale), (float) (1 / scale), 1);
            try {
                Object zone =
                        nativeZone.newInstance(
                                "line",
                                wx,
                                wz,
                                ex,
                                ez,
                                ArtilleryZoneBridge.COVERAGE_RADIUS,
                                null,
                                Long.MAX_VALUE,
                                "");
                nativeRectangle.invoke(
                        hud,
                        g,
                        viewport,
                        zone,
                        (int) (x * scale),
                        (int) (y * scale),
                        (int) ((x + w) * scale),
                        (int) ((y + h) * scale));
                return;
            } catch (ReflectiveOperationException | LinkageError ignored) {
                nativeRectangle = null;
            } finally {
                g.flush();
                g.pose().popPose();
            }
        }
        // Older ESPoints and the coordinate-only view use the same geometry and palette.
        double[] b = project(ex, ez),
                radiusPoint = project(wx + ArtilleryZoneBridge.COVERAGE_RADIUS, wz);
        if (b == null || radiusPoint == null) return;
        double dx = b[0] - a[0], dy = b[1] - a[1], length = Math.hypot(dx, dy);
        if (length < .001) return;
        double radius = Math.abs(radiusPoint[0] - a[0]);
        if (radius < 1) {
            radiusPoint = project(wx, wz + ArtilleryZoneBridge.COVERAGE_RADIUS);
            if (radiusPoint == null) return;
            radius = Math.abs(radiusPoint[1] - a[1]);
        }
        radius = Math.max(2, radius);
        double nx = -dy / length * radius, ny = dx / length * radius;
        double[] xs = {a[0] + nx, b[0] + nx, b[0] - nx, a[0] - nx};
        double[] ys = {a[1] + ny, b[1] + ny, b[1] - ny, a[1] - ny};
        for (int row = y; row < y + h; row++) {
            double scan = row + .5, l = Double.POSITIVE_INFINITY, r = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < 4; i++) {
                int j = (i + 1) % 4;
                if (scan < Math.min(ys[i], ys[j]) || scan >= Math.max(ys[i], ys[j])) continue;
                double cross = xs[i] + (xs[j] - xs[i]) * (scan - ys[i]) / (ys[j] - ys[i]);
                l = Math.min(l, cross);
                r = Math.max(r, cross);
            }
            if (r > l) {
                int from = (int) Math.max(x, Math.floor(l)),
                        to = (int) Math.min(x + w, Math.ceil(r));
                if (to > from) g.fill(from, row, to, row + 1, 0x66e04040);
            }
        }
        for (int i = 0; i < 4; i++) {
            int j = (i + 1) % 4;
            border(g, xs[i], ys[i], xs[j], ys[j]);
        }
    }

    private void border(GuiGraphics g, double ax, double ay, double bx, double by) {
        double dx = bx - ax, dy = by - ay, start = 0, end = 1;
        double[] p = {-dx, dx, -dy, dy}, q = {ax - x, x + w - 1 - ax, ay - y, y + h - 1 - ay};
        for (int i = 0; i < 4; i++) {
            if (p[i] == 0) {
                if (q[i] < 0) return;
            } else if (p[i] < 0) start = Math.max(start, q[i] / p[i]);
            else end = Math.min(end, q[i] / p[i]);
        }
        if (start > end) return;
        int steps = Math.max(1, (int) Math.ceil(Math.hypot(dx, dy) * (end - start)));
        for (int i = 0; i <= steps; i++) {
            double t = start + (end - start) * i / steps;
            int px = (int) Math.round(ax + dx * t), py = (int) Math.round(ay + dy * t);
            if (px >= x && px < x + w && py >= y && py < y + h)
                g.fill(px, py, px + 1, py + 1, 0xffe04040);
        }
    }

    private void pin(GuiGraphics g, double[] p, int color) {
        int a = (int) p[0], b = (int) p[1];
        if (a < x || a >= x + w || b < y || b >= y + h) return;
        g.fill(Math.max(x, a - 6), b - 1, Math.min(x + w, a + 7), b + 1, color);
        g.fill(a - 1, Math.max(y, b - 6), a + 1, Math.min(y + h, b + 7), color);
    }

    void zoom(double delta) {
        if (mapped)
            try {
                zoom.invoke(hud, delta);
            } catch (ReflectiveOperationException ignored) {
            }
    }
}
