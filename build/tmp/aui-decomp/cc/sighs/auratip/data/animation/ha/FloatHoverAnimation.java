/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.util.Mth
 */
package cc.sighs.auratip.data.animation.ha;

import cc.sighs.auratip.api.animation.HoverAnimation;
import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import net.minecraft.util.Mth;

public class FloatHoverAnimation
implements HoverAnimation {
    public static final HoverAnimation INSTANCE = new FloatHoverAnimation(3.0, 0.6f);
    private static final double TWO_PI = Math.PI * 2;
    private final double amplitude;
    private final float rampDuration;

    public FloatHoverAnimation(double amplitude, float rampDuration) {
        this.amplitude = amplitude;
        this.rampDuration = rampDuration;
    }

    public static HoverAnimation create(Map<String, Dynamic<?>> params) {
        double amplitude = SerializationUtil.getDouble(params, "amplitude", 3.0);
        double ramp = SerializationUtil.getDouble(params, "ramp_duration", 0.6);
        return new FloatHoverAnimation(amplitude, (float)ramp);
    }

    @Override
    public int offsetX(long nowMs, long startMs, int panelWidth, int panelHeight, float speed) {
        return 0;
    }

    @Override
    public int offsetY(long nowMs, long startMs, int panelWidth, int panelHeight, float speed) {
        long elapsed = Math.max(0L, nowMs - startMs);
        float seconds = (float)elapsed / 1000.0f;
        float effectiveSpeed = speed <= 0.0f ? 1.0f : speed;
        double angle = (double)seconds * (Math.PI * 2) * (double)effectiveSpeed;
        float ramp = seconds >= this.rampDuration ? 1.0f : Mth.m_14036_((float)(seconds / this.rampDuration), (float)0.0f, (float)1.0f);
        double value = Math.sin(angle) * this.amplitude * (double)ramp;
        return (int)Math.round(value);
    }
}

