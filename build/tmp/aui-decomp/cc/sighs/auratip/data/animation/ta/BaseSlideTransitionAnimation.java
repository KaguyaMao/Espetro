/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 */
package cc.sighs.auratip.data.animation.ta;

import cc.sighs.auratip.api.animation.TransitionAnimation;
import cc.sighs.auratip.util.AnimationUtil;
import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import java.util.function.Function;

public abstract class BaseSlideTransitionAnimation
implements TransitionAnimation {
    protected final float extraPadding;

    protected BaseSlideTransitionAnimation(float extraPadding) {
        this.extraPadding = extraPadding;
    }

    public static TransitionAnimation create(Map<String, Dynamic<?>> params, Function<Float, TransitionAnimation> constructor) {
        float padding = SerializationUtil.getFloat(params, "extra_padding", 24.0f);
        return constructor.apply(Float.valueOf(padding));
    }

    @Override
    public abstract int offsetX(float var1, int var2, int var3);

    @Override
    public abstract int offsetY(float var1, int var2, int var3);

    @Override
    public float easedProgress(long nowMs, long startMs, boolean closing, int openMs, int closeMs) {
        return AnimationUtil.smoothProgress(nowMs, startMs, closing, openMs, closeMs);
    }
}

