package org.espetro.client.support;

import net.minecraft.client.gui.GuiGraphics;

import org.espetro.support.FireRequestBoard.Bounds;

import java.lang.reflect.*;

/** Optional ESPoints projection; never calls its immediate-fire selection methods. */
final class FireSupportMap {
    private Object hud, viewport;
    private Method render, contains, worldX, worldZ, screenX, screenZ, zoom;
    private Field viewportField;
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
        g.fill(x, y, x + w, y + h, 0xff243b30);
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
            g.fill(x, y, x + w, y + h, 0xff243b30);
            if (bounds != null) fallbackGrid(g);
            FireSupportScreen.text(
                    g,
                    bounds == null ? "本局地图不可用" : "坐标视图 · 未连接 ESPoints 地图",
                    x + 8,
                    y + h - 17,
                    0xffadc2ae,
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
            g.fill(xx, y, xx + 1, y + h, 0xff60765f);
            if (cellX >= 35) {
                StringBuilder label = new StringBuilder();
                for (long n = i + 1; n > 0; n = (n - 1) / 26)
                    label.append((char) ('A' + (n - 1) % 26));
                FireSupportScreen.text(
                        g,
                        label.reverse().toString(),
                        xx + 5,
                        y + 3,
                        0xffc7d1bb,
                        9,
                        Math.min(cellX - 8, x + w - xx - 6));
            }
        }
        for (long i = 0; i < rows; i += sz) {
            int yy = y + (int) (i * cellZ);
            g.fill(x, yy, x + w, yy + 1, 0xff60765f);
            if (cellZ >= 25)
                FireSupportScreen.text(g, Long.toString(i + 1), x + 3, yy + 13, 0xffc7d1bb, 9, 30);
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
        return p[0] >= x && p[0] < x + w && p[1] >= y && p[1] < y + h ? p : null;
    }

    void targets(
            GuiGraphics g, double wx, double wz, boolean line, double ex, double ez, int color) {
        if (!Double.isFinite(wx) || !Double.isFinite(wz)) return;
        double[] a = project(wx, wz),
                b = line && Double.isFinite(ex) && Double.isFinite(ez) ? project(ex, ez) : null;
        if (a != null && b != null) {
            int steps = Math.min(2000, (int) Math.hypot(a[0] - b[0], a[1] - b[1]) + 1);
            for (int i = 0; i <= steps; i++) {
                int px = (int) (a[0] + (b[0] - a[0]) * i / steps),
                        py = (int) (a[1] + (b[1] - a[1]) * i / steps);
                g.fill(px, py, px + 2, py + 2, color);
            }
        }
        if (a != null) pin(g, a, color);
        if (b != null) pin(g, b, color);
    }

    private void pin(GuiGraphics g, double[] p, int color) {
        int a = (int) p[0], b = (int) p[1];
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
