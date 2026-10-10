/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.Mth
 */
package cc.sighs.auratip.util;

import net.minecraft.util.Mth;

public final class AnimationUtil {
    private AnimationUtil() {
    }

    public static float linearProgress(long nowMs, long startMs, boolean closing, int openMs, int closeMs) {
        float progress = AnimationUtil.calculateBaseProgress(nowMs, startMs, closing, openMs, closeMs);
        return Mth.m_14036_((float)(closing ? 1.0f - progress : progress), (float)0.0f, (float)1.0f);
    }

    public static float smoothProgress(long nowMs, long startMs, boolean closing, int openMs, int closeMs) {
        float t = AnimationUtil.calculateBaseProgress(nowMs, startMs, closing, openMs, closeMs);
        float progress = closing ? 1.0f - t : t;
        return progress * progress * (3.0f - 2.0f * progress);
    }

    private static float calculateBaseProgress(long nowMs, long startMs, boolean closing, int openMs, int closeMs) {
        int duration;
        int n = duration = closing ? closeMs : openMs;
        if (duration <= 0) {
            return closing ? 1.0f : 0.0f;
        }
        float elapsed = (float)(nowMs - startMs) / (float)duration;
        return Mth.m_14036_((float)elapsed, (float)0.0f, (float)1.0f);
    }
}

