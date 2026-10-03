/*
 * Decompiled with CFR 0.152.
 */
package com.redabysslucia.dragonrise_reforge.client.animation;

import java.util.function.DoubleUnaryOperator;

public class AnimationCurves {
    public static final DoubleUnaryOperator LINEAR = x -> x;
    public static final DoubleUnaryOperator EASE_OUT_CIRC = x -> Math.sqrt(1.0 - Math.pow(x - 1.0, 2.0));
    public static final DoubleUnaryOperator EASE_IN_EXPO = x -> x == 0.0 ? 0.0 : Math.pow(2.0, 10.0 * x - 10.0);
    public static final DoubleUnaryOperator EASE_OUT_EXPO = x -> x == 1.0 ? 1.0 : 1.0 - Math.pow(2.0, -10.0 * x);
    public static final DoubleUnaryOperator EASE_IN_OUT_QUINT = x -> x < 0.5 ? 4.0 * x * x * x : 1.0 - Math.pow(-2.0 * x + 2.0, 3.0) / 2.0;
    public static final DoubleUnaryOperator EASE_IN_QUART = x -> Math.pow(x, 4.0);
    public static final DoubleUnaryOperator PARABOLA = x -> -Math.pow(2.0 * x - 1.0, 2.0) + 1.0;
}

