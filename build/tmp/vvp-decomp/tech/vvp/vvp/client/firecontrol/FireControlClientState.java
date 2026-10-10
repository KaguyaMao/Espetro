/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.phys.Vec3
 */
package tech.vvp.vvp.client.firecontrol;

import net.minecraft.world.phys.Vec3;
import tech.vvp.vvp.client.firecontrol.FireControlMapMath;
import tech.vvp.vvp.entity.vehicle.HimarsEntity;
import tech.vvp.vvp.firecontrol.HimarsBallisticsUtil;

public final class FireControlClientState {
    public static final int BOOT_DURATION = 120;
    public static final int SHUTDOWN_DURATION = 70;
    private static TabletPower power = TabletPower.OFF;
    private static int powerTicks;
    private static float screenBrightness;
    private static final float BRIGHTNESS_STEP = 0.08f;
    private static boolean tabletOpen;
    private static HimarsEntity boundHimars;
    private static double mapCenterX;
    private static double mapCenterZ;
    private static double zoom;
    private static boolean hasTarget;
    private static int targetX;
    private static int targetY;
    private static int targetZ;
    private static HimarsBallisticsUtil.FireSolution solution;

    private FireControlClientState() {
    }

    public static float getScreenBrightness() {
        return screenBrightness;
    }

    public static void tickBrightness() {
        float target;
        float f = target = power == TabletPower.BOOTING || power == TabletPower.ONLINE ? 1.0f : 0.0f;
        if (screenBrightness < target) {
            screenBrightness = Math.min(1.0f, screenBrightness + 0.08f);
        } else if (screenBrightness > target) {
            screenBrightness = Math.max(0.0f, screenBrightness - 0.08f);
        }
    }

    public static void debugMsg(String msg) {
    }

    public static void beginBoot(HimarsEntity himars) {
        FireControlClientState.debugMsg("beginBoot: starting boot on himars ID " + himars.m_19879_());
        tabletOpen = true;
        boundHimars = himars;
        power = TabletPower.BOOTING;
        powerTicks = 0;
        mapCenterX = himars.m_20185_();
        mapCenterZ = himars.m_20189_();
        zoom = 1.0;
        FireControlClientState.clearTarget();
        if (himars.isFdcTargetDesignated()) {
            int tx = himars.getFdcTargetX();
            int ty = himars.getFdcTargetY();
            int tz = himars.getFdcTargetZ();
            Vec3 origin = HimarsBallisticsUtil.resolveShootOrigin(himars);
            HimarsBallisticsUtil.FireSolution sol = HimarsBallisticsUtil.solve(himars, origin, (double)tx + 0.5, ty, (double)tz + 0.5);
            FireControlClientState.setTarget(tx, ty, tz, sol);
        }
    }

    public static void beginShutdown() {
        if (power == TabletPower.OFF || power == TabletPower.SHUTTING_DOWN) {
            return;
        }
        FireControlClientState.debugMsg("beginShutdown: power was " + power);
        power = TabletPower.SHUTTING_DOWN;
        powerTicks = 0;
    }

    public static void forceOff() {
        FireControlClientState.debugMsg("forceOff: shutting down completely");
        tabletOpen = false;
        boundHimars = null;
        power = TabletPower.OFF;
        powerTicks = 0;
        FireControlClientState.clearTarget();
    }

    public static void tickPower() {
        if (power == TabletPower.OFF) {
            return;
        }
        if (power == TabletPower.BOOTING && ++powerTicks >= 120) {
            power = TabletPower.ONLINE;
            powerTicks = 0;
        } else if (power == TabletPower.SHUTTING_DOWN && powerTicks >= 70) {
            FireControlClientState.forceOff();
        }
    }

    public static boolean isTabletOpen() {
        return tabletOpen;
    }

    public static boolean isScreenLit() {
        return power != TabletPower.OFF;
    }

    public static boolean isMapVisible() {
        return power == TabletPower.ONLINE;
    }

    public static TabletPower getPower() {
        return power;
    }

    public static int getPowerTicks() {
        return powerTicks;
    }

    public static float getBootProgress() {
        return Math.min(1.0f, (float)powerTicks / 120.0f);
    }

    public static float getShutdownProgress() {
        return Math.min(1.0f, (float)powerTicks / 70.0f);
    }

    public static HimarsEntity getBoundHimars() {
        return boundHimars;
    }

    public static double getMapCenterX() {
        return mapCenterX;
    }

    public static double getMapCenterZ() {
        return mapCenterZ;
    }

    public static double getZoom() {
        return zoom;
    }

    public static void setZoom(double value) {
        zoom = Math.max(0.35, Math.min(8.0, value));
    }

    public static void panMap(double deltaX, double deltaZ) {
        if (!FireControlClientState.isMapVisible()) {
            return;
        }
        double[] center = new double[]{mapCenterX, mapCenterZ};
        FireControlMapMath.panCenter(center, deltaX, deltaZ, zoom);
        mapCenterX = center[0];
        mapCenterZ = center[1];
    }

    public static void setTarget(int x, int y, int z, HimarsBallisticsUtil.FireSolution fireSolution) {
        hasTarget = true;
        targetX = x;
        targetY = y;
        targetZ = z;
        solution = fireSolution;
    }

    public static void clearTarget() {
        hasTarget = false;
        solution = null;
    }

    public static boolean hasTarget() {
        return hasTarget;
    }

    public static int getTargetX() {
        return targetX;
    }

    public static int getTargetY() {
        return targetY;
    }

    public static int getTargetZ() {
        return targetZ;
    }

    public static HimarsBallisticsUtil.FireSolution getSolution() {
        return solution;
    }

    public static double getCrosshairWorldX() {
        return FireControlMapMath.screenToWorldX(128, mapCenterX, zoom);
    }

    public static double getCrosshairWorldZ() {
        return FireControlMapMath.screenToWorldZ(64, mapCenterZ, zoom);
    }

    static {
        screenBrightness = 0.0f;
        zoom = 1.0;
    }

    public static enum TabletPower {
        OFF,
        BOOTING,
        ONLINE,
        SHUTTING_DOWN;

    }
}

