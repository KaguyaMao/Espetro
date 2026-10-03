/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.util.Mth
 */
package cc.sighs.auratip.data.animation.ta;

import cc.sighs.auratip.api.animation.TransitionAnimation;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import net.minecraft.util.Mth;

public class FadeTransitionAnimation
implements TransitionAnimation {
    public static TransitionAnimation create(Map<String, Dynamic<?>> params) {
        return new FadeTransitionAnimation();
    }

    @Override
    public float easedProgress(long nowMs, long startMs, boolean closing, int openMs, int closeMs) {
        int duration;
        int n = duration = closing ? closeMs : openMs;
        if (duration <= 0) {
            return closing ? 0.0f : 1.0f;
        }
        float elapsed = (float)(nowMs - startMs) / (float)duration;
        float t = Mth.m_14036_((float)elapsed, (float)0.0f, (float)1.0f);
        return closing ? 1.0f - t : t;
    }

    @Override
    public int offsetX(float eased, int panelWidth, int panelHeight) {
        return 0;
    }

    @Override
    public int offsetY(float eased, int panelWidth, int panelHeight) {
        return 0;
    }
}

