/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.aui;

public final class AuiRadialLayout {
    public static final double INNER_RADIUS = 44.0;
    public static final double OUTER_RADIUS = 96.0;

    private AuiRadialLayout() {
    }

    public static int hitIndex(double mouseX, double mouseY, double centerX, double centerY, int slotCount) {
        if (slotCount <= 0) {
            return -1;
        }
        double dx = mouseX - centerX;
        double dy = mouseY - centerY;
        double distance = Math.hypot(dx, dy);
        if (distance < 44.0 || distance > 108.0) {
            return -1;
        }
        double angle = Math.atan2(dx, -dy);
        if (angle < 0.0) {
            angle += Math.PI * 2;
        }
        double step = Math.PI * 2 / (double)slotCount;
        int index = (int)Math.floor((angle + step / 2.0) / step);
        return Math.floorMod(index, slotCount);
    }

    public static double slotX(double centerX, int index, int slotCount) {
        return centerX + Math.sin(AuiRadialLayout.slotAngle(index, slotCount)) * AuiRadialLayout.slotRadius();
    }

    public static double slotY(double centerY, int index, int slotCount) {
        return centerY - Math.cos(AuiRadialLayout.slotAngle(index, slotCount)) * AuiRadialLayout.slotRadius();
    }

    public static double slotRadius() {
        return 70.0;
    }

    public static double slotAngle(int index, int slotCount) {
        if (slotCount <= 0) {
            return 0.0;
        }
        return (double)index * (Math.PI * 2) / (double)slotCount;
    }
}

