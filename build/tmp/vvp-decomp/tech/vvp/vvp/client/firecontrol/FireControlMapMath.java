/*
 * Decompiled with CFR 0.152.
 */
package tech.vvp.vvp.client.firecontrol;

public final class FireControlMapMath {
    public static final int MAP_WIDTH = 256;
    public static final int MAP_HEIGHT = 128;
    public static final double BASE_RADIUS = 96.0;

    private FireControlMapMath() {
    }

    public static double viewRadius(double zoom) {
        return 96.0 / Math.max(0.35, zoom);
    }

    public static int worldToScreenX(double worldX, double centerX, double zoom) {
        double radius = FireControlMapMath.viewRadius(zoom);
        double dx = worldX - centerX;
        return (int)(128.0 + dx / radius * 128.0);
    }

    public static int worldToScreenY(double worldZ, double centerZ, double zoom) {
        double radius = FireControlMapMath.viewRadius(zoom);
        double dz = worldZ - centerZ;
        return (int)(64.0 + dz / radius * 64.0);
    }

    public static double screenToWorldX(int screenX, double centerX, double zoom) {
        double radius = FireControlMapMath.viewRadius(zoom);
        double lx = ((double)screenX - 128.0) / 128.0 * radius;
        return centerX + lx;
    }

    public static double screenToWorldZ(int screenY, double centerZ, double zoom) {
        double radius = FireControlMapMath.viewRadius(zoom);
        double lz = ((double)screenY - 64.0) / 64.0 * radius;
        return centerZ + lz;
    }

    public static void panCenter(double[] center, double deltaX, double deltaZ, double zoom) {
        double radius = FireControlMapMath.viewRadius(zoom);
        center[0] = center[0] + deltaX / 128.0 * radius;
        center[1] = center[1] + deltaZ / 64.0 * radius;
    }

    public static int mapPixelStep(double zoom) {
        double radius = FireControlMapMath.viewRadius(zoom);
        if (radius > 180.0) {
            return 4;
        }
        if (radius > 80.0) {
            return 2;
        }
        return 1;
    }
}

