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

public class FadeAndSlideTransitionAnimation
implements TransitionAnimation {
    private final float offset;

    public FadeAndSlideTransitionAnimation(float offset) {
        this.offset = offset;
    }

    public static TransitionAnimation create(Map<String, Dynamic<?>> params) {
        float offset = (float)SerializationUtil.getDouble(params, "offset", 18.0);
        return new FadeAndSlideTransitionAnimation(offset);
    }

    @Override
    public float easedProgress(long nowMs, long startMs, boolean closing, int openMs, int closeMs) {
        return AnimationUtil.smoothProgress(nowMs, startMs, closing, openMs, closeMs);
    }

    @Override
    public int offsetX(float eased, int panelWidth, int panelHeight) {
        return 0;
    }

    @Override
    public int offsetY(float eased, int panelWidth, int panelHeight) {
        return (int)(this.offset * (1.0f - eased));
    }
}

