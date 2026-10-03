/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.joml.Quaternionf
 *  org.joml.Vector3f
 */
package dev.latvian.mods.kubejs.util;

import org.joml.Quaternionf;
import org.joml.Vector3f;

public enum RotationAxis {
    XN(f -> new Quaternionf().rotationX(-f), new Vector3f(-1.0f, 0.0f, 0.0f)),
    XP(f -> new Quaternionf().rotationX(f), new Vector3f(1.0f, 0.0f, 0.0f)),
    YN(f -> new Quaternionf().rotationY(-f), new Vector3f(0.0f, -1.0f, 0.0f)),
    YP(f -> new Quaternionf().rotationY(f), new Vector3f(0.0f, 1.0f, 0.0f)),
    ZN(f -> new Quaternionf().rotationZ(-f), new Vector3f(0.0f, 0.0f, -1.0f)),
    ZP(f -> new Quaternionf().rotationZ(f), new Vector3f(0.0f, 0.0f, 1.0f));

    private final Func func;
    public final Vector3f vec;

    private RotationAxis(Func func, Vector3f vec) {
        this.func = func;
        this.vec = vec;
    }

    public Quaternionf rad(float f) {
        return this.func.rotation(f);
    }

    public Quaternionf deg(float f) {
        return this.func.rotation(f * ((float)Math.PI / 180));
    }

    private static interface Func {
        public Quaternionf rotation(float var1);
    }
}

