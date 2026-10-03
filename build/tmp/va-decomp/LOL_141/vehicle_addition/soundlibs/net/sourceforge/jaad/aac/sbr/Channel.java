/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.AnalysisFilterbank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.FrameClass;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.HFAdjustment;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.HFGeneration;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.HuffmanTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;

class Channel {
    final SBR sbr;
    boolean amp_res;
    int abs_bord_lead;
    int abs_bord_trail;
    int n_rel_lead;
    int n_rel_trail;
    int L_E;
    int L_E_prev;
    int L_Q;
    int[] t_E = new int[6];
    int[] t_Q = new int[3];
    int[] f = new int[6];
    int f_prev;
    float[][] G_temp_prev = new float[5][64];
    float[][] Q_temp_prev = new float[5][64];
    int GQ_ringbuf_index = 0;
    int[][] E = new int[64][5];
    int[] E_prev = new int[64];
    float[][] E_orig = new float[64][5];
    float[][] E_curr = new float[64][5];
    int[][] Q = new int[64][2];
    float[][] Q_div = new float[64][2];
    float[][] Q_div2 = new float[64][2];
    int[] Q_prev = new int[64];
    int l_A;
    int[] bs_invf_mode = new int[5];
    int[] bs_invf_mode_prev = new int[5];
    float[] bwArray = new float[64];
    float[] bwArray_prev = new float[64];
    int[] bs_add_harmonic = new int[64];
    int[] bs_add_harmonic_prev = new int[64];
    int index_noise_prev;
    int psi_is_prev;
    int prevEnvIsShort = -1;
    final AnalysisFilterbank qmfa;
    public static final int MAX_NTSRHFG = 40;
    float[][][] Xsbr = new float[40][64][2];
    FrameClass bs_frame_class;
    int[] bs_rel_bord = new int[9];
    int[] bs_rel_bord_0 = new int[9];
    int[] bs_rel_bord_1 = new int[9];
    int bs_pointer;
    int bs_num_rel_0;
    int bs_num_rel_1;
    int[] bs_df_env = new int[9];
    int[] bs_df_noise = new int[3];
    boolean bs_add_harmonic_flag;
    boolean bs_add_harmonic_flag_prev;
    private static final int[] log2tab = new int[]{0, 0, 1, 2, 2, 3, 3, 3, 3, 4};
    private final int[] eTmp = new int[6];

    public Channel(SBR sbr) {
        this.sbr = sbr;
        this.qmfa = new AnalysisFilterbank(32);
    }

    void sbr_dtdf(BitStream ld) {
        int i;
        for (i = 0; i < this.L_E; ++i) {
            this.bs_df_env[i] = ld.readBit();
        }
        for (i = 0; i < this.L_Q; ++i) {
            this.bs_df_noise[i] = ld.readBit();
        }
    }

    void invf_mode(BitStream ld) {
        for (int n = 0; n < this.sbr.N_Q; ++n) {
            this.bs_invf_mode[n] = ld.readBits(2);
        }
    }

    void couple(Channel oc, int N_Q) {
        int n;
        this.bs_frame_class = oc.bs_frame_class;
        this.L_E = oc.L_E;
        this.L_Q = oc.L_Q;
        this.bs_pointer = oc.bs_pointer;
        for (n = 0; n <= oc.L_E; ++n) {
            this.t_E[n] = oc.t_E[n];
            this.f[n] = oc.f[n];
        }
        for (n = 0; n <= oc.L_Q; ++n) {
            this.t_Q[n] = oc.t_Q[n];
        }
        for (n = 0; n < N_Q; ++n) {
            this.bs_invf_mode[n] = oc.bs_invf_mode[n];
        }
    }

    void sbr_envelope(BitStream ld, boolean coupled) {
        int[][] f_huff;
        int[][] t_huff;
        int delta = 0;
        this.amp_res = this.L_E == 1 && this.bs_frame_class == FrameClass.FIXFIX ? false : this.sbr.hdr.bs_amp_res;
        if (coupled) {
            delta = 1;
            if (this.amp_res) {
                t_huff = HuffmanTables.T_HUFFMAN_ENV_BAL_3_0DB;
                f_huff = HuffmanTables.F_HUFFMAN_ENV_BAL_3_0DB;
            } else {
                t_huff = HuffmanTables.T_HUFFMAN_ENV_BAL_1_5DB;
                f_huff = HuffmanTables.F_HUFFMAN_ENV_BAL_1_5DB;
            }
        } else {
            delta = 0;
            if (this.amp_res) {
                t_huff = HuffmanTables.T_HUFFMAN_ENV_3_0DB;
                f_huff = HuffmanTables.F_HUFFMAN_ENV_3_0DB;
            } else {
                t_huff = HuffmanTables.T_HUFFMAN_ENV_1_5DB;
                f_huff = HuffmanTables.F_HUFFMAN_ENV_1_5DB;
            }
        }
        for (int env = 0; env < this.L_E; ++env) {
            int band;
            if (this.bs_df_env[env] == 0) {
                this.E[0][env] = coupled ? (this.amp_res ? ld.readBits(5) << delta : ld.readBits(6) << delta) : (this.amp_res ? ld.readBits(6) << delta : ld.readBits(7) << delta);
                for (band = 1; band < this.sbr.n[this.f[env]]; ++band) {
                    this.E[band][env] = Channel.decodeHuffman(ld, f_huff) << delta;
                }
                continue;
            }
            for (band = 0; band < this.sbr.n[this.f[env]]; ++band) {
                this.E[band][env] = Channel.decodeHuffman(ld, t_huff) << delta;
            }
        }
        this.extract_envelope_data();
    }

    void extract_envelope_data() {
        for (int l = 0; l < this.L_E; ++l) {
            int prev;
            int i;
            int k;
            int g;
            if (this.bs_df_env[l] == 0) {
                for (int k2 = 1; k2 < this.sbr.n[this.f[l]]; ++k2) {
                    this.E[k2][l] = this.E[k2 - 1][l] + this.E[k2][l];
                    if (this.E[k2][l] >= 0) continue;
                    this.E[k2][l] = 0;
                }
                continue;
            }
            int n = g = l == 0 ? this.f_prev : this.f[l - 1];
            if (this.f[l] == g) {
                for (k = 0; k < this.sbr.n[this.f[l]]; ++k) {
                    int prev2 = l == 0 ? this.E_prev[k] : this.E[k][l - 1];
                    this.E[k][l] = prev2 + this.E[k][l];
                }
                continue;
            }
            if (g == 1 && this.f[l] == 0) {
                for (k = 0; k < this.sbr.n[this.f[l]]; ++k) {
                    for (i = 0; i < this.sbr.N_high; ++i) {
                        if (this.sbr.f_table_res[1][i] != this.sbr.f_table_res[0][k]) continue;
                        prev = l == 0 ? this.E_prev[i] : this.E[i][l - 1];
                        this.E[k][l] = prev + this.E[k][l];
                    }
                }
                continue;
            }
            if (g != 0 || this.f[l] != 1) continue;
            for (k = 0; k < this.sbr.n[this.f[l]]; ++k) {
                for (i = 0; i < this.sbr.N_low; ++i) {
                    if (this.sbr.f_table_res[0][i] > this.sbr.f_table_res[1][k] || this.sbr.f_table_res[1][k] >= this.sbr.f_table_res[0][i + 1]) continue;
                    prev = l == 0 ? this.E_prev[i] : this.E[i][l - 1];
                    this.E[k][l] = prev + this.E[k][l];
                }
            }
        }
    }

    void sbr_noise(BitStream ld, boolean coupled) {
        int[][] f_huff;
        int[][] t_huff;
        int delta = 0;
        if (coupled) {
            delta = 1;
            t_huff = HuffmanTables.T_HUFFMAN_NOISE_BAL_3_0DB;
            f_huff = HuffmanTables.F_HUFFMAN_ENV_BAL_3_0DB;
        } else {
            delta = 0;
            t_huff = HuffmanTables.T_HUFFMAN_NOISE_3_0DB;
            f_huff = HuffmanTables.F_HUFFMAN_ENV_3_0DB;
        }
        for (int noise = 0; noise < this.L_Q; ++noise) {
            int band;
            if (this.bs_df_noise[noise] == 0) {
                this.Q[0][noise] = coupled ? ld.readBits(5) << delta : ld.readBits(5) << delta;
                for (band = 1; band < this.sbr.N_Q; ++band) {
                    this.Q[band][noise] = Channel.decodeHuffman(ld, f_huff) << delta;
                }
                continue;
            }
            for (band = 0; band < this.sbr.N_Q; ++band) {
                this.Q[band][noise] = Channel.decodeHuffman(ld, t_huff) << delta;
            }
        }
        this.extract_noise_floor_data();
    }

    static int decodeHuffman(BitStream ld, int[][] t_huff) {
        int index = 0;
        while (index >= 0) {
            int bit = ld.readBit();
            index = t_huff[index][bit];
        }
        return index + 64;
    }

    void extract_noise_floor_data() {
        for (int l = 0; l < this.L_Q; ++l) {
            int k;
            if (this.bs_df_noise[l] == 0) {
                for (k = 1; k < this.sbr.N_Q; ++k) {
                    this.Q[k][l] = this.Q[k][l] + this.Q[k - 1][l];
                }
                continue;
            }
            if (l == 0) {
                for (k = 0; k < this.sbr.N_Q; ++k) {
                    this.Q[k][l] = this.Q_prev[k] + this.Q[k][0];
                }
                continue;
            }
            for (k = 0; k < this.sbr.N_Q; ++k) {
                this.Q[k][l] = this.Q[k][l - 1] + this.Q[k][l];
            }
        }
    }

    int sbr_grid(BitStream ld) {
        int saved_L_E = this.L_E;
        int saved_L_Q = this.L_Q;
        FrameClass saved_frame_class = this.bs_frame_class;
        this.bs_frame_class = FrameClass.read(ld);
        switch (this.bs_frame_class) {
            case FIXFIX: {
                int i = ld.readBits(2);
                int bs_num_env = Math.min(1 << i, 5);
                i = ld.readBit();
                for (int env = 0; env < bs_num_env; ++env) {
                    this.f[env] = i;
                }
                this.L_E = Math.min(bs_num_env, 4);
                this.abs_bord_lead = 0;
                this.abs_bord_trail = this.sbr.numTimeSlots;
                this.n_rel_lead = bs_num_env - 1;
                this.n_rel_trail = 0;
                break;
            }
            case FIXVAR: {
                int bs_abs_bord = ld.readBits(2) + this.sbr.numTimeSlots;
                int bs_num_env = ld.readBits(2) + 1;
                for (int rel = 0; rel < bs_num_env - 1; ++rel) {
                    this.bs_rel_bord[rel] = 2 * ld.readBits(2) + 2;
                }
                int i = Channel.sbr_log2(bs_num_env + 1);
                this.bs_pointer = ld.readBits(i);
                for (int env = 0; env < bs_num_env; ++env) {
                    this.f[bs_num_env - env - 1] = ld.readBit();
                }
                this.L_E = Math.min(bs_num_env, 4);
                this.abs_bord_lead = 0;
                this.abs_bord_trail = bs_abs_bord;
                this.n_rel_lead = 0;
                this.n_rel_trail = bs_num_env - 1;
                break;
            }
            case VARFIX: {
                int bs_abs_bord = ld.readBits(2);
                int bs_num_env = ld.readBits(2) + 1;
                for (int rel = 0; rel < bs_num_env - 1; ++rel) {
                    this.bs_rel_bord[rel] = 2 * ld.readBits(2) + 2;
                }
                int i = Channel.sbr_log2(bs_num_env + 1);
                this.bs_pointer = ld.readBits(i);
                for (int env = 0; env < bs_num_env; ++env) {
                    this.f[env] = ld.readBit();
                }
                this.L_E = Math.min(bs_num_env, 4);
                this.abs_bord_lead = bs_abs_bord;
                this.abs_bord_trail = this.sbr.numTimeSlots;
                this.n_rel_lead = bs_num_env - 1;
                this.n_rel_trail = 0;
                break;
            }
            case VARVAR: {
                int rel;
                int bs_abs_bord = ld.readBits(2);
                int bs_abs_bord_1 = ld.readBits(2) + this.sbr.numTimeSlots;
                this.bs_num_rel_0 = ld.readBits(2);
                this.bs_num_rel_1 = ld.readBits(2);
                int bs_num_env = Math.min(5, this.bs_num_rel_0 + this.bs_num_rel_1 + 1);
                for (rel = 0; rel < this.bs_num_rel_0; ++rel) {
                    this.bs_rel_bord_0[rel] = 2 * ld.readBits(2) + 2;
                }
                for (rel = 0; rel < this.bs_num_rel_1; ++rel) {
                    this.bs_rel_bord_1[rel] = 2 * ld.readBits(2) + 2;
                }
                int i = Channel.sbr_log2(this.bs_num_rel_0 + this.bs_num_rel_1 + 2);
                this.bs_pointer = ld.readBits(i);
                for (int env = 0; env < bs_num_env; ++env) {
                    this.f[env] = ld.readBit();
                }
                this.L_E = Math.min(bs_num_env, 5);
                this.abs_bord_lead = bs_abs_bord;
                this.abs_bord_trail = bs_abs_bord_1;
                this.n_rel_lead = this.bs_num_rel_0;
                this.n_rel_trail = this.bs_num_rel_1;
                break;
            }
        }
        if (this.L_E <= 0) {
            return 1;
        }
        this.L_Q = this.L_E > 1 ? 2 : 1;
        int result = this.envelope_time_border_vector();
        if (result > 0) {
            this.bs_frame_class = saved_frame_class;
            this.L_E = saved_L_E;
            this.L_Q = saved_L_Q;
            return result;
        }
        this.noise_floor_time_border_vector();
        return 0;
    }

    private static int sbr_log2(int val) {
        if (val < 10 && val >= 0) {
            return log2tab[val];
        }
        return 0;
    }

    int envelope_time_border_vector() {
        this.eTmp[0] = this.sbr.rate * this.abs_bord_lead;
        this.eTmp[this.L_E] = this.sbr.rate * this.abs_bord_trail;
        block0 : switch (this.bs_frame_class) {
            case FIXFIX: {
                switch (this.L_E) {
                    case 4: {
                        int temp = this.sbr.numTimeSlots / 4;
                        this.eTmp[3] = this.sbr.rate * 3 * temp;
                        this.eTmp[2] = this.sbr.rate * 2 * temp;
                        this.eTmp[1] = this.sbr.rate * temp;
                        break block0;
                    }
                    case 2: {
                        this.eTmp[1] = this.sbr.rate * (this.sbr.numTimeSlots / 2);
                        break block0;
                    }
                }
                break;
            }
            case FIXVAR: {
                if (this.L_E <= 1) break;
                int i = this.L_E;
                int border = this.abs_bord_trail;
                for (int l = 0; l < this.L_E - 1; ++l) {
                    if (border < this.bs_rel_bord[l]) {
                        return 1;
                    }
                    this.eTmp[--i] = this.sbr.rate * (border -= this.bs_rel_bord[l]);
                }
                break;
            }
            case VARFIX: {
                if (this.L_E <= 1) break;
                int i = 1;
                int border = this.abs_bord_lead;
                for (int l = 0; l < this.L_E - 1; ++l) {
                    if (this.sbr.rate * (border += this.bs_rel_bord[l]) + this.sbr.tHFAdj > this.sbr.numTimeSlotsRate + this.sbr.tHFGen) {
                        return 1;
                    }
                    this.eTmp[i++] = this.sbr.rate * border;
                }
                break;
            }
            case VARVAR: {
                int l;
                int border;
                int i;
                if (this.bs_num_rel_0 != 0) {
                    i = 1;
                    border = this.abs_bord_lead;
                    for (l = 0; l < this.bs_num_rel_0; ++l) {
                        if (this.sbr.rate * (border += this.bs_rel_bord_0[l]) + this.sbr.tHFAdj > this.sbr.numTimeSlotsRate + this.sbr.tHFGen) {
                            return 1;
                        }
                        this.eTmp[i++] = this.sbr.rate * border;
                    }
                }
                if (this.bs_num_rel_1 == 0) break;
                i = this.L_E;
                border = this.abs_bord_trail;
                for (l = 0; l < this.bs_num_rel_1; ++l) {
                    if (border < this.bs_rel_bord_1[l]) {
                        return 1;
                    }
                    this.eTmp[--i] = this.sbr.rate * (border -= this.bs_rel_bord_1[l]);
                }
                break;
            }
        }
        System.arraycopy(this.eTmp, 0, this.t_E, 0, 6);
        return 0;
    }

    void noise_floor_time_border_vector() {
        this.t_Q[0] = this.t_E[0];
        if (this.L_E == 1) {
            this.t_Q[1] = this.t_E[1];
            this.t_Q[2] = 0;
        } else {
            int index = this.middleBorder();
            this.t_Q[1] = this.t_E[index];
            this.t_Q[2] = this.t_E[this.L_E];
        }
    }

    private int middleBorder() {
        int retval = 0;
        switch (this.bs_frame_class) {
            case FIXFIX: {
                retval = this.L_E / 2;
                break;
            }
            case VARFIX: {
                if (this.bs_pointer == 0) {
                    retval = 1;
                    break;
                }
                if (this.bs_pointer == 1) {
                    retval = this.L_E - 1;
                    break;
                }
                retval = this.bs_pointer - 1;
                break;
            }
            case FIXVAR: 
            case VARVAR: {
                retval = this.bs_pointer > 1 ? this.L_E + 1 - this.bs_pointer : this.L_E - 1;
            }
        }
        return retval > 0 ? retval : 0;
    }

    void process_channel(float[] channel_buf, float[][][] X, boolean reset) {
        this.sbr.bsco = 0;
        boolean dont_process = this.sbr.hdr == null;
        this.qmfa.sbr_qmf_analysis_32(this.sbr.numTimeSlotsRate, channel_buf, this.Xsbr, this.sbr.tHFGen, dont_process ? 32 : this.sbr.kx);
        if (!dont_process) {
            HFGeneration.hf_generation(this.Xsbr, this.Xsbr, this, reset);
            HFAdjustment.hf_adjustment(this.sbr, this.Xsbr, this);
        }
        if (dont_process) {
            for (int l = 0; l < this.sbr.numTimeSlotsRate; ++l) {
                int k;
                for (k = 0; k < 32; ++k) {
                    X[l][k][0] = this.Xsbr[l + this.sbr.tHFAdj][k][0];
                    X[l][k][1] = this.Xsbr[l + this.sbr.tHFAdj][k][1];
                }
                for (k = 32; k < 64; ++k) {
                    X[l][k][0] = 0.0f;
                    X[l][k][1] = 0.0f;
                }
            }
        } else {
            for (int l = 0; l < this.sbr.numTimeSlotsRate; ++l) {
                int k;
                int bsco_band;
                int M_band;
                int kx_band;
                if (l < this.t_E[0]) {
                    kx_band = this.sbr.kx_prev;
                    M_band = this.sbr.M_prev;
                    bsco_band = this.sbr.bsco_prev;
                } else {
                    kx_band = this.sbr.kx;
                    M_band = this.sbr.M;
                    bsco_band = this.sbr.bsco;
                }
                for (k = 0; k < kx_band + bsco_band; ++k) {
                    X[l][k][0] = this.Xsbr[l + this.sbr.tHFAdj][k][0];
                    X[l][k][1] = this.Xsbr[l + this.sbr.tHFAdj][k][1];
                }
                for (k = kx_band + bsco_band; k < kx_band + M_band; ++k) {
                    X[l][k][0] = this.Xsbr[l + this.sbr.tHFAdj][k][0];
                    X[l][k][1] = this.Xsbr[l + this.sbr.tHFAdj][k][1];
                }
                for (k = Math.max(kx_band + bsco_band, kx_band + M_band); k < 64; ++k) {
                    X[l][k][0] = 0.0f;
                    X[l][k][1] = 0.0f;
                }
            }
        }
    }
}

