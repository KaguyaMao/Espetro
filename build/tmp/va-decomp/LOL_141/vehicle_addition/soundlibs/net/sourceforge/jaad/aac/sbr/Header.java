/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

class Header {
    boolean bs_amp_res = true;
    int bs_start_freq = 5;
    int bs_stop_freq;
    int bs_xover_band;
    int bs_freq_scale = 2;
    boolean bs_alter_scale = true;
    int bs_noise_bands = 2;
    int bs_limiter_bands = 2;
    int bs_limiter_gains = 2;
    boolean bs_interpol_freq;
    boolean bs_smoothing_mode;

    Header() {
    }

    public void decode(BitStream ld) {
        this.bs_amp_res = ld.readBool();
        this.bs_start_freq = ld.readBits(4);
        this.bs_stop_freq = ld.readBits(4);
        this.bs_xover_band = ld.readBits(3);
        ld.readBits(2);
        boolean bs_header_extra_1 = ld.readBool();
        boolean bs_header_extra_2 = ld.readBool();
        if (bs_header_extra_1) {
            this.bs_freq_scale = ld.readBits(2);
            this.bs_alter_scale = ld.readBool();
            this.bs_noise_bands = ld.readBits(2);
        } else {
            this.bs_freq_scale = 2;
            this.bs_alter_scale = true;
            this.bs_noise_bands = 2;
        }
        if (bs_header_extra_2) {
            this.bs_limiter_bands = ld.readBits(2);
            this.bs_limiter_gains = ld.readBits(2);
            this.bs_interpol_freq = ld.readBool();
            this.bs_smoothing_mode = ld.readBool();
        } else {
            this.bs_limiter_bands = 2;
            this.bs_limiter_gains = 2;
            this.bs_interpol_freq = true;
            this.bs_smoothing_mode = true;
        }
    }

    public boolean differs(Header prev) {
        return prev == null || this.bs_start_freq != prev.bs_start_freq || this.bs_stop_freq != prev.bs_stop_freq || this.bs_freq_scale != prev.bs_freq_scale || this.bs_alter_scale != prev.bs_alter_scale || this.bs_xover_band != prev.bs_xover_band || this.bs_noise_bands != prev.bs_noise_bands;
    }
}

