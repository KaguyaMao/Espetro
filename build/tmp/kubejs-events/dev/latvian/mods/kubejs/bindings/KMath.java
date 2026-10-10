/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.core.BlockPos
 *  net.minecraft.world.phys.Vec3
 *  org.joml.Matrix3f
 *  org.joml.Matrix4f
 *  org.joml.Quaternionf
 *  org.joml.Vector3d
 *  org.joml.Vector3f
 *  org.joml.Vector4f
 */
package dev.latvian.mods.kubejs.bindings;

import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3d;
import org.joml.Vector3f;
import org.joml.Vector4f;

public class KMath {
    public static final double E = Math.E;
    public static final double PI = Math.PI;
    public static final double DEGREES_TO_RADIANS = Math.PI / 180;
    public static final double RADIANS_TO_DEGREES = 57.29577951308232;

    public static BlockPos block(double x, double y, double z) {
        return BlockPos.m_274561_((double)x, (double)y, (double)z);
    }

    public static Vec3 v3(double x, double y, double z) {
        return new Vec3(x, y, z);
    }

    public static Vector3d v3d(double x, double y, double z) {
        return new Vector3d(x, y, z);
    }

    public static Vector3f v3f(float x, float y, float z) {
        return new Vector3f(x, y, z);
    }

    public static Vector4f v4f(float x, float y, float z, float w) {
        return new Vector4f(x, y, z, w);
    }

    public static Matrix3f m3f() {
        return new Matrix3f();
    }

    public static Matrix4f m4f() {
        return new Matrix4f();
    }

    public static Quaternionf quaternion(float x, float y, float z, float w) {
        return new Quaternionf(x, y, z, w);
    }

    public static double rad(double value) {
        return value * (Math.PI / 180);
    }

    public static double deg(double value) {
        return value * 57.29577951308232;
    }

    public static long floor(double value) {
        long i = (long)value;
        return value < (double)i ? i - 1L : i;
    }

    public static long ceil(double value) {
        long i = (long)value;
        return value > (double)i ? i + 1L : i;
    }

    public static double clamp(double value, double min, double max) {
        return value < min ? min : Math.min(value, max);
    }

    public static double lerp(double value, double min, double max) {
        return min + value * (max - min);
    }

    public static double map(double value, double min0, double max0, double min1, double max1) {
        return min1 + (max1 - min1) * ((value - min0) / (max0 - min0));
    }

    public static double clampedLerp(double value, double min, double max) {
        return value < 0.0 ? min : (value > 1.0 ? max : KMath.lerp(value, min, max));
    }

    public static double wrapDegrees(double d) {
        double e = d % 360.0;
        if (e >= 180.0) {
            e -= 360.0;
        }
        if (e < -180.0) {
            e += 360.0;
        }
        return e;
    }

    public static double degreesDifference(double current, double target) {
        return KMath.wrapDegrees(target - current);
    }

    public static double rotateIfNecessary(double current, double target, double max) {
        double i = KMath.degreesDifference(current, target);
        double j = KMath.clamp(i, -max, max);
        return target - j;
    }

    public static double approach(double current, double target, double speed) {
        speed = Math.abs(speed);
        return current < target ? KMath.clamp(current + speed, current, target) : KMath.clamp(current - speed, target, current);
    }

    public static double approachDegrees(double current, double target, double speed) {
        double i = KMath.degreesDifference(current, target);
        return KMath.approach(current, current + i, speed);
    }

    public static boolean isPowerOfTwo(int value) {
        return value != 0 && (value & value - 1) == 0;
    }
}

