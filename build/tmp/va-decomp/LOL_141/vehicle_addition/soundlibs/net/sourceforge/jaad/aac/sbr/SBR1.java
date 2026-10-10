/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Channel;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.NoiseEnvelope;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.PS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SynthesisFilterbank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import java.util.Arrays;

public class SBR1
extends SBR {
    static final int EXTENSION_ID_PS = 2;
    final Channel ch0 = new Channel(this);
    final SynthesisFilterbank qmfs0 = this.openFilterbank();
    SynthesisFilterbank qmfs1;
    PS ps;

    public SBR1(DecoderConfig config) {
        super(config);
    }

    @Override
    protected int sbr_data(BitStream ld) {
        int result;
        if (ld.readBool()) {
            ld.readBits(4);
        }
        if ((result = this.ch0.sbr_grid(ld)) > 0) {
            return result;
        }
        this.ch0.sbr_dtdf(ld);
        this.ch0.invf_mode(ld);
        this.ch0.sbr_envelope(ld, false);
        this.ch0.sbr_noise(ld, false);
        NoiseEnvelope.dequantChannel(this, this.ch0);
        Arrays.fill(this.ch0.bs_add_harmonic, 0, 64, 0);
        this.ch0.bs_add_harmonic_flag = ld.readBool();
        if (this.ch0.bs_add_harmonic_flag) {
            this.sinusoidal_coding(ld, this.ch0);
        }
        this.readExtendedData(ld);
        return 0;
    }

    @Override
    protected void sbr_extension(BitStream ld, int bs_extension_id) {
        if (bs_extension_id == 2 && this.config.isPSEnabled()) {
            if (this.ps == null) {
                this.ps = this.config.openPS(this);
                this.qmfs1 = this.openFilterbank();
            }
            this.ps.decode(ld);
        } else {
            super.sbr_extension(ld, bs_extension_id);
        }
    }

    @Override
    public void process(float[] left_chan, float[] right_chan) {
        if (this.isPSUsed()) {
            this.processPS(left_chan, right_chan);
        } else {
            this.process(left_chan);
            System.arraycopy(left_chan, 0, right_chan, 0, right_chan.length);
        }
    }

    private void process(float[] channel) {
        float[][][] X = new float[32][64][2];
        this.ch0.process_channel(channel, X, this.reset);
        this.qmfs0.synthesis(this.numTimeSlotsRate, X, channel);
        if (this.hdr != null) {
            this.sbr_save_prev_data(this.ch0);
        }
        this.sbr_save_matrix(this.ch0);
        ++this.frame;
    }

    private int processPS(float[] left_channel, float[] right_channel) {
        boolean ret = false;
        float[][][] X_left = new float[38][64][2];
        float[][][] X_right = new float[38][64][2];
        this.ch0.process_channel(left_channel, X_left, this.reset);
        for (int l = this.numTimeSlotsRate; l < this.numTimeSlotsRate + 6; ++l) {
            for (int k = 0; k < 5; ++k) {
                X_left[l][k][0] = this.ch0.Xsbr[this.tHFAdj + l][k][0];
                X_left[l][k][1] = this.ch0.Xsbr[this.tHFAdj + l][k][1];
            }
        }
        this.ps.process(X_left, X_right);
        this.qmfs0.synthesis(this.numTimeSlotsRate, X_left, left_channel);
        this.qmfs1.synthesis(this.numTimeSlotsRate, X_right, right_channel);
        if (this.hdr != null && !ret) {
            this.sbr_save_prev_data(this.ch0);
        }
        this.sbr_save_matrix(this.ch0);
        ++this.frame;
        return 0;
    }

    public boolean isPSUsed() {
        return this.ps != null && this.ps.isDataAvailable();
    }
}

