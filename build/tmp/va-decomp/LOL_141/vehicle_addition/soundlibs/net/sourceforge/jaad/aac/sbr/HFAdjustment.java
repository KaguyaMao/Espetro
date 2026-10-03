/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Channel;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.FrameClass;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.NoiseTable;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;

class HFAdjustment
implements NoiseTable {
    private static final float[] h_smooth = new float[]{0.0318305f, 0.11516383f, 0.2181695f, 0.30150282f, 0.33333334f};
    private static final int[] phi_re = new int[]{1, 0, -1, 0};
    private static final int[] phi_im = new int[]{0, 1, 0, -1};
    private static final float[] limGain = new float[]{0.5f, 1.0f, 2.0f, 1.0E10f};
    private static final float EPS = 1.0E-12f;
    private float[][] G_lim_boost = new float[5][49];
    private float[][] Q_M_lim_boost = new float[5][49];
    private float[][] S_M_boost = new float[5][49];

    HFAdjustment() {
    }

    public static void hf_adjustment(SBR sbr, float[][][] Xsbr, Channel ch) {
        HFAdjustment adj = new HFAdjustment();
        ch.l_A = ch.bs_frame_class == FrameClass.FIXFIX ? -1 : (ch.bs_frame_class == FrameClass.VARFIX ? (ch.bs_pointer > 1 ? ch.bs_pointer - 1 : -1) : (ch.bs_pointer == 0 ? -1 : ch.L_E + 1 - ch.bs_pointer));
        adj.estimate_current_envelope(sbr, Xsbr, ch);
        adj.calculate_gain(sbr, ch);
        adj.hf_assembly(sbr, Xsbr, ch);
    }

    private static int get_S_mapped(SBR sbr, Channel ch, int l, int current_band) {
        if (ch.f[l] == 1) {
            if (l >= ch.l_A || ch.bs_add_harmonic_prev[current_band] != 0 && ch.bs_add_harmonic_flag_prev) {
                return ch.bs_add_harmonic[current_band];
            }
        } else {
            int lb = 2 * current_band - ((sbr.N_high & 1) != 0 ? 1 : 0);
            int ub = 2 * (current_band + 1) - ((sbr.N_high & 1) != 0 ? 1 : 0);
            for (int b = lb; b < ub; ++b) {
                if (l < ch.l_A && (ch.bs_add_harmonic_prev[b] == 0 || !ch.bs_add_harmonic_flag_prev) || ch.bs_add_harmonic[b] != 1) continue;
                return 1;
            }
        }
        return 0;
    }

    private void estimate_current_envelope(SBR sbr, float[][][] Xsbr, Channel ch) {
        if (sbr.hdr.bs_interpol_freq) {
            for (int l = 0; l < ch.L_E; ++l) {
                int u_i = ch.t_E[l + 1];
                int l_i = ch.t_E[l];
                float div = u_i - l_i;
                if (div == 0.0f) {
                    div = 1.0f;
                }
                for (int m = 0; m < sbr.M; ++m) {
                    float nrg = 0.0f;
                    for (int i = l_i + sbr.tHFAdj; i < u_i + sbr.tHFAdj; ++i) {
                        nrg += Xsbr[i][m + sbr.kx][0] * Xsbr[i][m + sbr.kx][0] + Xsbr[i][m + sbr.kx][1] * Xsbr[i][m + sbr.kx][1];
                    }
                    ch.E_curr[m][l] = nrg / div;
                }
            }
        } else {
            for (int l = 0; l < ch.L_E; ++l) {
                for (int p = 0; p < sbr.n[ch.f[l]]; ++p) {
                    int k_l = sbr.f_table_res[ch.f[l]][p];
                    int k_h = sbr.f_table_res[ch.f[l]][p + 1];
                    for (int k = k_l; k < k_h; ++k) {
                        float nrg = 0.0f;
                        int u_i = ch.t_E[l + 1];
                        int l_i = ch.t_E[l];
                        float div = (u_i - l_i) * (k_h - k_l);
                        if (div == 0.0f) {
                            div = 1.0f;
                        }
                        for (int i = l_i + sbr.tHFAdj; i < u_i + sbr.tHFAdj; ++i) {
                            for (int j = k_l; j < k_h; ++j) {
                                nrg += Xsbr[i][j][0] * Xsbr[i][j][0] + Xsbr[i][j][1] * Xsbr[i][j][1];
                            }
                        }
                        ch.E_curr[k - sbr.kx][l] = nrg / div;
                    }
                }
            }
        }
    }

    private void hf_assembly(SBR sbr, float[][][] Xsbr, Channel ch) {
        int fIndexNoise = 0;
        int fIndexSine = 0;
        boolean assembly_reset = false;
        if (sbr.reset) {
            assembly_reset = true;
            fIndexNoise = 0;
        } else {
            fIndexNoise = ch.index_noise_prev;
        }
        fIndexSine = ch.psi_is_prev;
        for (int l = 0; l < ch.L_E; ++l) {
            boolean no_noise = l == ch.l_A || l == ch.prevEnvIsShort;
            int h_SL = sbr.hdr.bs_smoothing_mode ? 0 : 4;
            int n = h_SL = no_noise ? 0 : h_SL;
            if (assembly_reset) {
                for (int n2 = 0; n2 < 4; ++n2) {
                    System.arraycopy(this.G_lim_boost[l], 0, ch.G_temp_prev[n2], 0, sbr.M);
                    System.arraycopy(this.Q_M_lim_boost[l], 0, ch.Q_temp_prev[n2], 0, sbr.M);
                }
                ch.GQ_ringbuf_index = 4;
                assembly_reset = false;
            }
            for (int i = ch.t_E[l]; i < ch.t_E[l + 1]; ++i) {
                System.arraycopy(this.G_lim_boost[l], 0, ch.G_temp_prev[ch.GQ_ringbuf_index], 0, sbr.M);
                System.arraycopy(this.Q_M_lim_boost[l], 0, ch.Q_temp_prev[ch.GQ_ringbuf_index], 0, sbr.M);
                for (int m = 0; m < sbr.M; ++m) {
                    float[] psi = new float[2];
                    float G_filt = 0.0f;
                    float Q_filt = 0.0f;
                    if (h_SL != 0) {
                        int ri = ch.GQ_ringbuf_index;
                        for (int n3 = 0; n3 <= 4; ++n3) {
                            float curr_h_smooth = h_smooth[n3];
                            if (++ri >= 5) {
                                ri -= 5;
                            }
                            G_filt += ch.G_temp_prev[ri][m] * curr_h_smooth;
                            Q_filt += ch.Q_temp_prev[ri][m] * curr_h_smooth;
                        }
                    } else {
                        G_filt = ch.G_temp_prev[ch.GQ_ringbuf_index][m];
                        Q_filt = ch.Q_temp_prev[ch.GQ_ringbuf_index][m];
                    }
                    Q_filt = this.S_M_boost[l][m] != 0.0f || no_noise ? 0.0f : Q_filt;
                    fIndexNoise = fIndexNoise + 1 & 0x1FF;
                    Xsbr[i + sbr.tHFAdj][m + sbr.kx][0] = G_filt * Xsbr[i + sbr.tHFAdj][m + sbr.kx][0] + Q_filt * NOISE_TABLE[fIndexNoise][0];
                    Xsbr[i + sbr.tHFAdj][m + sbr.kx][1] = G_filt * Xsbr[i + sbr.tHFAdj][m + sbr.kx][1] + Q_filt * NOISE_TABLE[fIndexNoise][1];
                    int rev = (m + sbr.kx & 1) != 0 ? -1 : 1;
                    psi[0] = this.S_M_boost[l][m] * (float)phi_re[fIndexSine];
                    float[] fArray = Xsbr[i + sbr.tHFAdj][m + sbr.kx];
                    fArray[0] = fArray[0] + psi[0];
                    psi[1] = (float)rev * this.S_M_boost[l][m] * (float)phi_im[fIndexSine];
                    float[] fArray2 = Xsbr[i + sbr.tHFAdj][m + sbr.kx];
                    fArray2[1] = fArray2[1] + psi[1];
                }
                fIndexSine = fIndexSine + 1 & 3;
                ++ch.GQ_ringbuf_index;
                if (ch.GQ_ringbuf_index < 5) continue;
                ch.GQ_ringbuf_index = 0;
            }
        }
        ch.index_noise_prev = fIndexNoise;
        ch.psi_is_prev = fIndexSine;
    }

    private void calculate_gain(SBR sbr, Channel ch) {
        int current_t_noise_band = 0;
        float[] Q_M_lim = new float[49];
        float[] G_lim = new float[49];
        float[] S_M = new float[49];
        for (int l = 0; l < ch.L_E; ++l) {
            int current_f_noise_band = 0;
            int current_res_band = 0;
            int current_res_band2 = 0;
            int current_hi_res_band = 0;
            float delta = l == ch.l_A || l == ch.prevEnvIsShort ? 0.0f : 1.0f;
            int S_mapped = HFAdjustment.get_S_mapped(sbr, ch, l, current_res_band2);
            if (ch.t_E[l + 1] > ch.t_Q[current_t_noise_band + 1]) {
                ++current_t_noise_band;
            }
            for (int k = 0; k < sbr.N_L[sbr.hdr.bs_limiter_bands]; ++k) {
                int m;
                float den = 0.0f;
                float acc1 = 0.0f;
                float acc2 = 0.0f;
                boolean current_res_band_size = false;
                int ml1 = sbr.f_table_lim[sbr.hdr.bs_limiter_bands][k];
                int ml2 = sbr.f_table_lim[sbr.hdr.bs_limiter_bands][k + 1];
                for (m = ml1; m < ml2; ++m) {
                    if (m + sbr.kx == sbr.f_table_res[ch.f[l]][current_res_band + 1]) {
                        ++current_res_band;
                    }
                    acc1 += ch.E_orig[current_res_band][l];
                    acc2 += ch.E_curr[m][l];
                }
                float G_max = (1.0E-12f + acc1) / (1.0E-12f + acc2) * limGain[sbr.hdr.bs_limiter_gains];
                G_max = Math.min(G_max, 1.0E10f);
                for (m = ml1; m < ml2; ++m) {
                    if (m + sbr.kx == sbr.f_table_noise[current_f_noise_band + 1]) {
                        ++current_f_noise_band;
                    }
                    if (m + sbr.kx == sbr.f_table_res[ch.f[l]][current_res_band2 + 1]) {
                        S_mapped = HFAdjustment.get_S_mapped(sbr, ch, l, ++current_res_band2);
                    }
                    if (m + sbr.kx == sbr.f_table_res[1][current_hi_res_band + 1]) {
                        ++current_hi_res_band;
                    }
                    int S_index_mapped = 0;
                    if ((l >= ch.l_A || ch.bs_add_harmonic_prev[current_hi_res_band] != 0 && ch.bs_add_harmonic_flag_prev) && m + sbr.kx == sbr.f_table_res[1][current_hi_res_band + 1] + sbr.f_table_res[1][current_hi_res_band] >> 1) {
                        S_index_mapped = ch.bs_add_harmonic[current_hi_res_band];
                    }
                    float Q_div = ch.Q_div[current_f_noise_band][current_t_noise_band];
                    float Q_div2 = ch.Q_div2[current_f_noise_band][current_t_noise_band];
                    float Q_M = ch.E_orig[current_res_band2][l] * Q_div2;
                    if (S_index_mapped == 0) {
                        S_M[m] = 0.0f;
                    } else {
                        S_M[m] = ch.E_orig[current_res_band2][l] * Q_div;
                        den += S_M[m];
                    }
                    float G = ch.E_orig[current_res_band2][l] / (1.0f + ch.E_curr[m][l]);
                    if (S_mapped == 0 && delta == 1.0f) {
                        G *= Q_div;
                    } else if (S_mapped == 1) {
                        G *= Q_div2;
                    }
                    if (G_max > G) {
                        Q_M_lim[m] = Q_M;
                        G_lim[m] = G;
                    } else {
                        Q_M_lim[m] = Q_M * G_max / G;
                        G_lim[m] = G_max;
                    }
                    den += ch.E_curr[m][l] * G_lim[m];
                    if (S_index_mapped != 0 || l == ch.l_A) continue;
                    den += Q_M_lim[m];
                }
                float G_boost = (acc1 + 1.0E-12f) / (den + 1.0E-12f);
                G_boost = Math.min(G_boost, 2.5118864f);
                for (m = ml1; m < ml2; ++m) {
                    this.G_lim_boost[l][m] = (float)Math.sqrt(G_lim[m] * G_boost);
                    this.Q_M_lim_boost[l][m] = (float)Math.sqrt(Q_M_lim[m] * G_boost);
                    this.S_M_boost[l][m] = S_M[m] != 0.0f ? (float)Math.sqrt(S_M[m] * G_boost) : 0.0f;
                }
            }
        }
    }
}

