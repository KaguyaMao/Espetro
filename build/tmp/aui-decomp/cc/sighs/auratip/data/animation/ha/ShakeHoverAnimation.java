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

public class ShakeHoverAnimation
implements HoverAnimation {
    public static final HoverAnimation INSTANCE = new ShakeHoverAnimation(2.0, 2.0, 2.0, 3.0, 1.5707963267948966, 0.4f);
    private static final double TWO_PI = Math.PI * 2;
    private final double amplitudeX;
    private final double amplitudeY;
    private final double freqX;
    private final double freqY;
    private final double phase;
    private final float rampDuration;

    public ShakeHoverAnimation(double amplitudeX, double amplitudeY, double freqX, double freqY, double phase, float rampDuration) {
        this.amplitudeX = amplitudeX;
        this.amplitudeY = amplitudeY;
        this.freqX = freqX;
        this.freqY = freqY;
        this.phase = phase;
        this.rampDuration = rampDuration;
    }

    public static HoverAnimation create(Map<String, Dynamic<?>> params) {
        double ampX = SerializationUtil.getDouble(params, "amplitude_x", 2.0);
        double ampY = SerializationUtil.getDouble(params, "amplitude_y", 2.0);
        double freqX = SerializationUtil.getDouble(params, "frequency_x", 2.0);
        double freqY = SerializationUtil.getDouble(params, "frequency_y", 3.0);
        double phase = SerializationUtil.getDouble(params, "phase", 1.5707963267948966);
        double ramp = SerializationUtil.getDouble(params, "ramp_duration", 0.4);
        return new ShakeHoverAnimation(ampX, ampY, freqX, freqY, phase, (float)ramp);
    }

    private float ramp(float seconds) {
        if (seconds >= this.rampDuration) {
            return 1.0f;
        }
        return Mth.m_14036_((float)(seconds / this.rampDuration), (float)0.0f, (float)1.0f);
    }

    @Override
    public int offsetX(long nowMs, long startMs, int panelWidth, int panelHeight, float speed) {
        float seconds = (float)Math.max(0L, nowMs - startMs) / 1000.0f;
        float effectiveSpeed = speed <= 0.0f ? 1.0f : speed;
        float r = this.ramp(seconds);
        double t = (double)seconds * (Math.PI * 2) * (double)effectiveSpeed;
        double x = Math.sin(t * this.freqX + this.phase) * this.amplitudeX * (double)r;
        return (int)Math.round(x);
    }

    @Override
    public int offsetY(long nowMs, long startMs, int panelWidth, int panelHeight, float speed) {
        float seconds = (float)Math.max(0L, nowMs - startMs) / 1000.0f;
        float effectiveSpeed = speed <= 0.0f ? 1.0f : speed;
        float r = this.ramp(seconds);
        double t = (double)seconds * (Math.PI * 2) * (double)effectiveSpeed;
        double y = Math.sin(t * this.freqY) * this.amplitudeY * (double)r;
        return (int)Math.round(y);
    }
}

