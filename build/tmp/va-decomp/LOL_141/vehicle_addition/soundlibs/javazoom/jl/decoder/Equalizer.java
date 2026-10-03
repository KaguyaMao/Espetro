/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder;

public final class Equalizer {
    public static final float BAND_NOT_PRESENT = Float.NEGATIVE_INFINITY;
    public static final Equalizer PASS_THRU_EQ = new Equalizer();
    private static final int BANDS = 32;
    private final float[] settings = new float[32];

    public Equalizer() {
    }

    public Equalizer(float[] settings) {
        this.setFrom(settings);
    }

    public Equalizer(EQFunction eq) {
        this.setFrom(eq);
    }

    public void setFrom(float[] eq) {
        this.reset();
        int max = eq.length > 32 ? 32 : eq.length;
        for (int i = 0; i < max; ++i) {
            this.settings[i] = this.limit(eq[i]);
        }
    }

    public void setFrom(EQFunction eq) {
        this.reset();
        int max = 32;
        for (int i = 0; i < max; ++i) {
            this.settings[i] = this.limit(eq.getBand(i));
        }
    }

    public void setFrom(Equalizer eq) {
        if (eq != this) {
            this.setFrom(eq.settings);
        }
    }

    public void reset() {
        for (int i = 0; i < 32; ++i) {
            this.settings[i] = 0.0f;
        }
    }

    public int getBandCount() {
        return this.settings.length;
    }

    public float setBand(int band, float neweq) {
        float eq = 0.0f;
        if (band >= 0 && band < 32) {
            eq = this.settings[band];
            this.settings[band] = this.limit(neweq);
        }
        return eq;
    }

    public float getBand(int band) {
        float eq = 0.0f;
        if (band >= 0 && band < 32) {
            eq = this.settings[band];
        }
        return eq;
    }

    private float limit(float eq) {
        if (eq == Float.NEGATIVE_INFINITY) {
            return eq;
        }
        if (eq > 1.0f) {
            return 1.0f;
        }
        if (eq < -1.0f) {
            return -1.0f;
        }
        return eq;
    }

    float[] getBandFactors() {
        float[] factors = new float[32];
        int maxCount = 32;
        for (int i = 0; i < maxCount; ++i) {
            factors[i] = this.getBandFactor(this.settings[i]);
        }
        return factors;
    }

    float getBandFactor(float eq) {
        if (eq == Float.NEGATIVE_INFINITY) {
            return 0.0f;
        }
        float f = (float)Math.pow(2.0, eq);
        return f;
    }

    public static abstract class EQFunction {
        public float getBand(int band) {
            return 0.0f;
        }
    }
}

