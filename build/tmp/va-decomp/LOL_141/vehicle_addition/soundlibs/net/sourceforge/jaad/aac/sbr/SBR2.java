/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Channel;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.FrameClass;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.NoiseEnvelope;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SynthesisFilterbank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import java.util.Arrays;

public class SBR2
extends SBR {
    final Channel ch0 = new Channel(this);
    final Channel ch1;
    boolean bs_coupling;
    final SynthesisFilterbank qmfs0 = this.openFilterbank();
    SynthesisFilterbank qmfs1;

    public SBR2(DecoderConfig config) {
        super(config);
        this.ch1 = new Channel(this);
        this.qmfs1 = this.openFilterbank();
    }

    @Override
    protected int sbr_data(BitStream ld) {
        if (ld.readBool()) {
            ld.readBits(4);
            ld.readBits(4);
        }
        this.bs_coupling = ld.readBool();
        if (this.bs_coupling) {
            int result = this.ch0.sbr_grid(ld);
            if (result > 0) {
                return result;
            }
            this.ch0.sbr_dtdf(ld);
            this.ch1.sbr_dtdf(ld);
            this.ch0.invf_mode(ld);
            this.ch1.couple(this.ch0, this.N_Q);
            this.ch0.sbr_envelope(ld, false);
            this.ch0.sbr_noise(ld, false);
            this.ch1.sbr_envelope(ld, this.bs_coupling);
            this.ch1.sbr_noise(ld, this.bs_coupling);
            Arrays.fill(this.ch0.bs_add_harmonic, 0, 64, 0);
            Arrays.fill(this.ch1.bs_add_harmonic, 0, 64, 0);
            this.ch0.bs_add_harmonic_flag = ld.readBool();
            if (this.ch0.bs_add_harmonic_flag) {
                this.sinusoidal_coding(ld, this.ch0);
            }
            this.ch1.bs_add_harmonic_flag = ld.readBool();
            if (this.ch1.bs_add_harmonic_flag) {
                this.sinusoidal_coding(ld, this.ch1);
            }
        } else {
            int n;
            int[] saved_t_E = new int[6];
            int[] saved_t_Q = new int[3];
            int saved_L_E = this.ch0.L_E;
            int saved_L_Q = this.ch0.L_Q;
            FrameClass saved_frame_class = this.ch0.bs_frame_class;
            for (n = 0; n < saved_L_E; ++n) {
                saved_t_E[n] = this.ch0.t_E[n];
            }
            for (n = 0; n < saved_L_Q; ++n) {
                saved_t_Q[n] = this.ch0.t_Q[n];
            }
            int result = this.ch0.sbr_grid(ld);
            if (result > 0) {
                return result;
            }
            result = this.ch1.sbr_grid(ld);
            if (result > 0) {
                this.ch0.bs_frame_class = saved_frame_class;
                this.ch0.L_E = saved_L_E;
                this.ch0.L_Q = saved_L_Q;
                for (n = 0; n < 6; ++n) {
                    this.ch0.t_E[n] = saved_t_E[n];
                }
                for (n = 0; n < 3; ++n) {
                    this.ch0.t_Q[n] = saved_t_Q[n];
                }
                return result;
            }
            this.ch0.sbr_dtdf(ld);
            this.ch1.sbr_dtdf(ld);
            this.ch0.invf_mode(ld);
            this.ch1.invf_mode(ld);
            this.ch0.sbr_envelope(ld, false);
            this.ch1.sbr_envelope(ld, false);
            this.ch0.sbr_noise(ld, this.bs_coupling);
            this.ch1.sbr_noise(ld, this.bs_coupling);
            Arrays.fill(this.ch0.bs_add_harmonic, 0, 64, 0);
            Arrays.fill(this.ch1.bs_add_harmonic, 0, 64, 0);
            this.ch0.bs_add_harmonic_flag = ld.readBool();
            if (this.ch0.bs_add_harmonic_flag) {
                this.sinusoidal_coding(ld, this.ch0);
            }
            this.ch1.bs_add_harmonic_flag = ld.readBool();
            if (this.ch1.bs_add_harmonic_flag) {
                this.sinusoidal_coding(ld, this.ch1);
            }
        }
        if (!this.bs_coupling) {
            NoiseEnvelope.dequantChannel(this, this.ch0);
            NoiseEnvelope.dequantChannel(this, this.ch1);
        } else {
            NoiseEnvelope.unmap(this);
        }
        this.readExtendedData(ld);
        return 0;
    }

    @Override
    public void process(float[] left_chan, float[] right_chan) {
        float[][][] X = new float[32][64][2];
        this.ch0.process_channel(left_chan, X, this.reset);
        this.qmfs0.synthesis(this.numTimeSlotsRate, X, left_chan);
        this.ch1.process_channel(right_chan, X, false);
        this.qmfs1.synthesis(this.numTimeSlotsRate, X, right_chan);
        if (this.hdr != null) {
            this.sbr_save_prev_data(this.ch0);
            this.sbr_save_prev_data(this.ch1);
        }
        this.sbr_save_matrix(this.ch0);
        this.sbr_save_matrix(this.ch1);
        ++this.frame;
    }
}

