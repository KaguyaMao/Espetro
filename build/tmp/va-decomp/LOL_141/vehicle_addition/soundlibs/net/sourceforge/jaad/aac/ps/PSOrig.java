/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.FBType;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filterbank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Huffman;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PSTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.PS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import java.util.Arrays;

public class PSOrig
implements PS {
    final int numTimeSlotsRate;
    boolean enable_iid;
    boolean enable_icc;
    boolean enable_ext;
    int iid_mode;
    int icc_mode;
    int nr_iid_par;
    int nr_ipdopd_par;
    int nr_icc_par;
    int frame_class;
    int num_env;
    int[] border_position = new int[6];
    boolean[] iid_dt = new boolean[5];
    boolean[] icc_dt = new boolean[5];
    boolean enable_ipdopd;
    int ipd_mode;
    boolean[] ipd_dt = new boolean[5];
    boolean[] opd_dt = new boolean[5];
    int[] iid_index_prev = new int[34];
    int[] icc_index_prev = new int[34];
    int[] ipd_index_prev = new int[17];
    int[] opd_index_prev = new int[17];
    int[][] iid_index = new int[5][34];
    int[][] icc_index = new int[5][34];
    int[][] ipd_index = new int[5][17];
    int[][] opd_index = new int[5][17];
    boolean ps_data_available;
    boolean header_read;
    Filterbank hyb;
    FBType fbt = FBType.T20;
    static final int NR_ALLPASS_BANDS = 22;
    int saved_delay;
    int[] delay_buf_index_ser = new int[3];
    int[] num_sample_delay_ser = new int[3];
    static final int SHORT_DELAY_BAND = 35;
    int[] delay_D = new int[64];
    int[] delay_buf_index_delay = new int[64];
    float[][][] delay_Qmf = new float[14][64][2];
    float[][][] delay_SubQmf = new float[2][32][2];
    float[][][][] delay_Qmf_ser = new float[3][5][64][2];
    float[][][][] delay_SubQmf_ser = new float[3][5][32][2];
    static final float ALPHA_DECAY = 0.7659283f;
    static final float ALPHA_SMOOTH = 0.25f;
    float[] P_PeakDecayNrg = new float[34];
    float[] P_prev = new float[34];
    float[] P_SmoothPeakDecayDiffNrg_prev = new float[34];
    float[][] h11_prev = new float[50][2];
    float[][] h12_prev = new float[50][2];
    float[][] h21_prev = new float[50][2];
    float[][] h22_prev = new float[50][2];
    int phase_hist;
    float[][][] ipd_prev = new float[20][2][2];
    float[][][] opd_prev = new float[20][2][2];

    public PSOrig(int numTimeSlotsRate) {
        int i;
        this.numTimeSlotsRate = numTimeSlotsRate;
        this.hyb = new Filterbank(numTimeSlotsRate);
        this.ps_data_available = false;
        this.saved_delay = 0;
        for (i = 0; i < 64; ++i) {
            this.delay_buf_index_delay[i] = 0;
        }
        for (i = 0; i < 3; ++i) {
            this.delay_buf_index_ser[i] = 0;
            this.num_sample_delay_ser[i] = PSTables.delay_length_d[i];
        }
        Arrays.fill(this.delay_D, 0, 35, 14);
        Arrays.fill(this.delay_D, 35, this.delay_D.length, 1);
        for (i = 0; i < 50; ++i) {
            this.h11_prev[i][0] = 1.0f;
            this.h12_prev[i][1] = 1.0f;
            this.h11_prev[i][0] = 1.0f;
            this.h12_prev[i][1] = 1.0f;
        }
        this.phase_hist = 0;
        for (i = 0; i < 20; ++i) {
            this.ipd_prev[i][0][0] = 0.0f;
            this.ipd_prev[i][0][1] = 0.0f;
            this.ipd_prev[i][1][0] = 0.0f;
            this.ipd_prev[i][1][1] = 0.0f;
            this.opd_prev[i][0][0] = 0.0f;
            this.opd_prev[i][0][1] = 0.0f;
            this.opd_prev[i][1][0] = 0.0f;
            this.opd_prev[i][1][1] = 0.0f;
        }
    }

    @Override
    public boolean isDataAvailable() {
        return this.ps_data_available;
    }

    @Override
    public void decode(BitStream ld) {
        int n;
        if (ld.readBool()) {
            this.header_read = true;
            this.fbt = FBType.T20;
            this.enable_iid = ld.readBool();
            if (this.enable_iid) {
                this.iid_mode = ld.readBits(3);
                this.nr_iid_par = PSTables.nr_iid_par_tab[this.iid_mode];
                this.nr_ipdopd_par = PSTables.nr_ipdopd_par_tab[this.iid_mode];
                if (this.iid_mode == 2 || this.iid_mode == 5) {
                    this.fbt = FBType.T34;
                }
                this.ipd_mode = this.iid_mode;
            }
            this.enable_icc = ld.readBool();
            if (this.enable_icc) {
                this.icc_mode = ld.readBits(3);
                this.nr_icc_par = PSTables.nr_icc_par_tab[this.icc_mode];
                if (this.icc_mode == 2 || this.icc_mode == 5) {
                    this.fbt = FBType.T34;
                }
            }
            this.enable_ext = ld.readBool();
        }
        this.frame_class = ld.readBit();
        int tmp = ld.readBits(2);
        this.num_env = PSTables.num_env_tab[this.frame_class][tmp];
        if (this.frame_class != 0) {
            for (n = 1; n < this.num_env + 1; ++n) {
                this.border_position[n] = ld.readBits(5) + 1;
            }
        }
        if (this.enable_iid) {
            for (n = 0; n < this.num_env; ++n) {
                this.iid_dt[n] = ld.readBool();
                if (this.iid_mode < 3) {
                    PSOrig.huff_data(ld, this.iid_dt[n], this.nr_iid_par, Huffman.t_huff_iid_def, Huffman.f_huff_iid_def, this.iid_index[n]);
                    continue;
                }
                PSOrig.huff_data(ld, this.iid_dt[n], this.nr_iid_par, Huffman.t_huff_iid_fine, Huffman.f_huff_iid_fine, this.iid_index[n]);
            }
        }
        if (this.enable_icc) {
            for (n = 0; n < this.num_env; ++n) {
                this.icc_dt[n] = ld.readBool();
                PSOrig.huff_data(ld, this.icc_dt[n], this.nr_icc_par, Huffman.t_huff_icc, Huffman.f_huff_icc, this.icc_index[n]);
            }
        }
        if (this.enable_ext) {
            int cnt = ld.readBits(4);
            if (cnt == 15) {
                cnt += ld.readBits(8);
            }
            ld = ld.readSubStream(8 * cnt);
            while (ld.getBitsLeft() > 7) {
                this.ps_extension(ld);
            }
        }
        this.ps_data_available = true;
    }

    private void ps_extension(BitStream ld) {
        int ps_extension_id = ld.readBits(2);
        if (ps_extension_id == 0) {
            this.enable_ipdopd = ld.readBool();
            if (this.enable_ipdopd) {
                for (int n = 0; n < this.num_env; ++n) {
                    this.ipd_dt[n] = ld.readBool();
                    PSOrig.huff_data(ld, this.ipd_dt[n], this.nr_ipdopd_par, Huffman.t_huff_ipd, Huffman.f_huff_ipd, this.ipd_index[n]);
                    this.opd_dt[n] = ld.readBool();
                    PSOrig.huff_data(ld, this.opd_dt[n], this.nr_ipdopd_par, Huffman.t_huff_opd, Huffman.f_huff_opd, this.opd_index[n]);
                }
            }
            ld.readBit();
        }
    }

    private static void huff_data(BitStream ld, boolean dt, int nr_par, int[][] t_huff, int[][] f_huff, int[] par) {
        if (dt) {
            for (int n = 0; n < nr_par; ++n) {
                par[n] = PSOrig.ps_huff_dec(ld, t_huff);
            }
        } else {
            par[0] = PSOrig.ps_huff_dec(ld, f_huff);
            for (int n = 1; n < nr_par; ++n) {
                par[n] = PSOrig.ps_huff_dec(ld, f_huff);
            }
        }
    }

    private static int ps_huff_dec(BitStream ld, int[][] t_huff) {
        int index = 0;
        while (index >= 0) {
            int bit = ld.readBit();
            index = t_huff[index][bit];
        }
        return index + 31;
    }

    private static int delta_clip(int i, int min, int max) {
        if (i < min) {
            return min;
        }
        if (i > max) {
            return max;
        }
        return i;
    }

    private static void delta_decode(boolean enable, int[] index, int[] index_prev, boolean dt_flag, int nr_par, int stride, int min_index, int max_index) {
        int i;
        if (enable) {
            if (!dt_flag) {
                index[0] = 0 + index[0];
                index[0] = PSOrig.delta_clip(index[0], min_index, max_index);
                for (i = 1; i < nr_par; ++i) {
                    index[i] = index[i - 1] + index[i];
                    index[i] = PSOrig.delta_clip(index[i], min_index, max_index);
                }
            } else {
                for (i = 0; i < nr_par; ++i) {
                    index[i] = index_prev[i * stride] + index[i];
                    index[i] = PSOrig.delta_clip(index[i], min_index, max_index);
                }
            }
        } else {
            for (i = 0; i < nr_par; ++i) {
                index[i] = 0;
            }
        }
        if (stride == 2) {
            for (i = (nr_par << 1) - 1; i > 0; --i) {
                index[i] = index[i >> 1];
            }
        }
    }

    private static void delta_modulo_decode(boolean enable, int[] index, int[] index_prev, boolean dt_flag, int nr_par, int stride, int and_modulo) {
        int i;
        if (enable) {
            if (!dt_flag) {
                index[0] = 0 + index[0];
                index[0] = index[0] & and_modulo;
                i = 1;
                while (i < nr_par) {
                    index[i] = index[i - 1] + index[i];
                    int n = i++;
                    index[n] = index[n] & and_modulo;
                }
            } else {
                i = 0;
                while (i < nr_par) {
                    index[i] = index_prev[i * stride] + index[i];
                    int n = i++;
                    index[n] = index[n] & and_modulo;
                }
            }
        } else {
            for (i = 0; i < nr_par; ++i) {
                index[i] = 0;
            }
        }
        if (stride == 2) {
            index[0] = 0;
            for (i = (nr_par << 1) - 1; i > 0; --i) {
                index[i] = index[i >> 1];
            }
        }
    }

    private static void map20indexto34(int[] index, int bins) {
        index[1] = (index[0] + index[1]) / 2;
        index[2] = index[1];
        index[3] = index[2];
        index[4] = (index[2] + index[3]) / 2;
        index[5] = index[3];
        index[6] = index[4];
        index[7] = index[4];
        index[8] = index[5];
        index[9] = index[5];
        index[10] = index[6];
        index[11] = index[7];
        index[12] = index[8];
        index[13] = index[8];
        index[14] = index[9];
        index[15] = index[9];
        index[16] = index[10];
        if (bins == 34) {
            index[17] = index[11];
            index[18] = index[12];
            index[19] = index[13];
            index[20] = index[14];
            index[21] = index[14];
            index[22] = index[15];
            index[23] = index[15];
            index[24] = index[16];
            index[25] = index[16];
            index[26] = index[17];
            index[27] = index[17];
            index[28] = index[18];
            index[29] = index[18];
            index[30] = index[18];
            index[31] = index[18];
            index[32] = index[19];
            index[33] = index[19];
        }
    }

    private void ps_data_decode() {
        int bin;
        int env;
        if (!this.ps_data_available) {
            this.num_env = 0;
        }
        for (env = 0; env < this.num_env; ++env) {
            int num_iid_steps = this.iid_mode < 3 ? 7 : 15;
            PSOrig.delta_decode(this.enable_iid, this.iid_index[env], env == 0 ? this.iid_index_prev : this.iid_index[env - 1], this.iid_dt[env], this.nr_iid_par, this.iid_mode == 0 || this.iid_mode == 3 ? 2 : 1, -num_iid_steps, num_iid_steps);
            PSOrig.delta_decode(this.enable_icc, this.icc_index[env], env == 0 ? this.icc_index_prev : this.icc_index[env - 1], this.icc_dt[env], this.nr_icc_par, this.icc_mode == 0 || this.icc_mode == 3 ? 2 : 1, 0, 7);
            PSOrig.delta_modulo_decode(this.enable_ipdopd, this.ipd_index[env], env == 0 ? this.ipd_index_prev : this.ipd_index[env - 1], this.ipd_dt[env], this.nr_ipdopd_par, 1, 7);
            PSOrig.delta_modulo_decode(this.enable_ipdopd, this.opd_index[env], env == 0 ? this.opd_index_prev : this.opd_index[env - 1], this.opd_dt[env], this.nr_ipdopd_par, 1, 7);
        }
        if (this.num_env == 0) {
            this.num_env = 1;
            if (this.enable_iid) {
                for (bin = 0; bin < 34; ++bin) {
                    this.iid_index[0][bin] = this.iid_index_prev[bin];
                }
            } else {
                for (bin = 0; bin < 34; ++bin) {
                    this.iid_index[0][bin] = 0;
                }
            }
            if (this.enable_icc) {
                for (bin = 0; bin < 34; ++bin) {
                    this.icc_index[0][bin] = this.icc_index_prev[bin];
                }
            } else {
                for (bin = 0; bin < 34; ++bin) {
                    this.icc_index[0][bin] = 0;
                }
            }
            if (this.enable_ipdopd) {
                for (bin = 0; bin < 17; ++bin) {
                    this.ipd_index[0][bin] = this.ipd_index_prev[bin];
                    this.opd_index[0][bin] = this.opd_index_prev[bin];
                }
            } else {
                for (bin = 0; bin < 17; ++bin) {
                    this.ipd_index[0][bin] = 0;
                    this.opd_index[0][bin] = 0;
                }
            }
        }
        for (bin = 0; bin < 34; ++bin) {
            this.iid_index_prev[bin] = this.iid_index[this.num_env - 1][bin];
        }
        for (bin = 0; bin < 34; ++bin) {
            this.icc_index_prev[bin] = this.icc_index[this.num_env - 1][bin];
        }
        for (bin = 0; bin < 17; ++bin) {
            this.ipd_index_prev[bin] = this.ipd_index[this.num_env - 1][bin];
            this.opd_index_prev[bin] = this.opd_index[this.num_env - 1][bin];
        }
        this.ps_data_available = false;
        if (this.frame_class == 0) {
            this.border_position[0] = 0;
            for (env = 1; env < this.num_env; ++env) {
                this.border_position[env] = env * this.numTimeSlotsRate / this.num_env;
            }
            this.border_position[this.num_env] = this.numTimeSlotsRate;
        } else {
            this.border_position[0] = 0;
            if (this.border_position[this.num_env] < this.numTimeSlotsRate) {
                for (bin = 0; bin < 34; ++bin) {
                    this.iid_index[this.num_env][bin] = this.iid_index[this.num_env - 1][bin];
                    this.icc_index[this.num_env][bin] = this.icc_index[this.num_env - 1][bin];
                }
                for (bin = 0; bin < 17; ++bin) {
                    this.ipd_index[this.num_env][bin] = this.ipd_index[this.num_env - 1][bin];
                    this.opd_index[this.num_env][bin] = this.opd_index[this.num_env - 1][bin];
                }
                ++this.num_env;
                this.border_position[this.num_env] = this.numTimeSlotsRate;
            }
            for (env = 1; env < this.num_env; ++env) {
                int thr = this.numTimeSlotsRate - (this.num_env - env);
                if (this.border_position[env] > thr) {
                    this.border_position[env] = thr;
                    continue;
                }
                thr = this.border_position[env - 1] + 1;
                if (this.border_position[env] >= thr) continue;
                this.border_position[env] = thr;
            }
        }
        if (this.fbt == FBType.T34) {
            for (env = 0; env < this.num_env; ++env) {
                if (this.iid_mode != 2 && this.iid_mode != 5) {
                    PSOrig.map20indexto34(this.iid_index[env], 34);
                }
                if (this.icc_mode != 2 && this.icc_mode != 5) {
                    PSOrig.map20indexto34(this.icc_index[env], 34);
                }
                if (this.ipd_mode == 2 || this.ipd_mode == 5) continue;
                PSOrig.map20indexto34(this.ipd_index[env], 17);
                PSOrig.map20indexto34(this.opd_index[env], 17);
            }
        }
    }

    private void ps_decorrelate(float[][][] X_left, float[][][] X_right, float[][][] X_hybrid_left, float[][][] X_hybrid_right) {
        int bk;
        float[][] P = new float[32][34];
        float[][] G_TransientRatio = new float[32][34];
        for (int n = 0; n < 32; ++n) {
            for (bk = 0; bk < 34; ++bk) {
                P[n][bk] = 0.0f;
            }
        }
        for (int gr = 0; gr < this.fbt.num_groups; ++gr) {
            bk = this.fbt.bk(gr);
            int maxsb = gr < this.fbt.num_hybrid_groups ? this.fbt.group_border[gr] + 1 : this.fbt.group_border[gr + 1];
            float[][][] Xl = gr < this.fbt.num_hybrid_groups ? X_hybrid_left : X_left;
            for (int n = this.border_position[0]; n < this.border_position[this.num_env]; ++n) {
                float[] p = P[n];
                for (int sb = this.fbt.group_border[gr]; sb < maxsb; ++sb) {
                    float[] xl = Xl[n][sb];
                    float re = xl[0];
                    float im = xl[1];
                    int n2 = bk;
                    p[n2] = p[n2] + (re * re + im * im);
                }
            }
        }
        for (int bk2 = 0; bk2 < this.fbt.nr_par_bands; ++bk2) {
            for (int n = this.border_position[0]; n < this.border_position[this.num_env]; ++n) {
                float gamma = 1.5f;
                this.P_PeakDecayNrg[bk2] = this.P_PeakDecayNrg[bk2] * 0.7659283f;
                if (this.P_PeakDecayNrg[bk2] < P[n][bk2]) {
                    this.P_PeakDecayNrg[bk2] = P[n][bk2];
                }
                float P_SmoothPeakDecayDiffNrg = this.P_SmoothPeakDecayDiffNrg_prev[bk2];
                this.P_SmoothPeakDecayDiffNrg_prev[bk2] = P_SmoothPeakDecayDiffNrg += (this.P_PeakDecayNrg[bk2] - P[n][bk2] - this.P_SmoothPeakDecayDiffNrg_prev[bk2]) * 0.25f;
                float nrg = this.P_prev[bk2];
                this.P_prev[bk2] = nrg += (P[n][bk2] - this.P_prev[bk2]) * 0.25f;
                G_TransientRatio[n][bk2] = P_SmoothPeakDecayDiffNrg * gamma <= nrg ? 1.0f : nrg / (P_SmoothPeakDecayDiffNrg * gamma);
            }
        }
        int temp_delay = 0;
        int[] temp_delay_ser = new int[3];
        float[] g_DecaySlope_filt = new float[3];
        for (int gr = 0; gr < this.fbt.num_groups; ++gr) {
            int maxsb = gr < this.fbt.num_hybrid_groups ? this.fbt.group_border[gr] + 1 : this.fbt.group_border[gr + 1];
            float[][][] Xl = gr < this.fbt.num_hybrid_groups ? X_hybrid_left : X_left;
            float[][][] Xr = gr < this.fbt.num_hybrid_groups ? X_hybrid_right : X_right;
            float[][][][] delay_ser = gr < this.fbt.num_hybrid_groups ? this.delay_SubQmf_ser : this.delay_Qmf_ser;
            float[][][] qFractAllpassQmf = gr < this.fbt.num_hybrid_groups ? this.fbt.qFractAllpassSubQmf : PSTables.Q_Fract_allpass_Qmf;
            for (int sb = this.fbt.group_border[gr]; sb < maxsb; ++sb) {
                int n;
                int decay;
                float g_DecaySlope = gr < this.fbt.num_hybrid_groups || sb <= this.fbt.decay_cutoff ? 1.0f : ((decay = this.fbt.decay_cutoff - sb) <= -20 ? 0.0f : 1.0f + 0.05f * (float)decay);
                for (int m = 0; m < 3; ++m) {
                    g_DecaySlope_filt[m] = g_DecaySlope * PSTables.filter_a[m];
                }
                temp_delay = this.saved_delay;
                for (n = 0; n < 3; ++n) {
                    temp_delay_ser[n] = this.delay_buf_index_ser[n];
                }
                for (n = this.border_position[0]; n < this.border_position[this.num_env]; ++n) {
                    float r0Im;
                    float r0Re;
                    float re = Xl[n][sb][0];
                    float im = Xl[n][sb][1];
                    if (sb > 22 && gr >= this.fbt.num_hybrid_groups) {
                        float[] delay = this.delay_Qmf[this.delay_buf_index_delay[sb]][sb];
                        r0Re = delay[0];
                        r0Im = delay[1];
                        delay[0] = re;
                        delay[1] = im;
                    } else {
                        float[] delayQmf = gr < this.fbt.num_hybrid_groups ? this.delay_SubQmf[temp_delay][sb] : this.delay_Qmf[temp_delay][sb];
                        float[] Phi_Fract = gr < this.fbt.num_hybrid_groups ? this.fbt.phiFract[sb] : PSTables.Phi_Fract_Qmf[sb];
                        float tmp0Re = delayQmf[0];
                        float tmp0Im = delayQmf[1];
                        delayQmf[0] = re;
                        delayQmf[1] = im;
                        r0Re = tmp0Re * Phi_Fract[0] + tmp0Im * Phi_Fract[1];
                        r0Im = tmp0Im * Phi_Fract[0] - tmp0Re * Phi_Fract[1];
                        for (int m = 0; m < 3; ++m) {
                            float[] qFractAllpass = qFractAllpassQmf[sb][m];
                            float[] delay = delay_ser[m][temp_delay_ser[m]][sb];
                            tmp0Re = delay[0];
                            tmp0Im = delay[1];
                            float tmpRe = tmp0Re * qFractAllpass[0] + tmp0Im * qFractAllpass[1];
                            float tmpIm = tmp0Im * qFractAllpass[0] - tmp0Re * qFractAllpass[1];
                            delay[0] = r0Re + g_DecaySlope_filt[m] * (tmpRe -= g_DecaySlope_filt[m] * r0Re);
                            delay[1] = r0Im + g_DecaySlope_filt[m] * (tmpIm -= g_DecaySlope_filt[m] * r0Im);
                            r0Re = tmpRe;
                            r0Im = tmpIm;
                        }
                    }
                    int bk3 = this.fbt.bk(gr);
                    Xr[n][sb][0] = G_TransientRatio[n][bk3] * r0Re;
                    Xr[n][sb][1] = G_TransientRatio[n][bk3] * r0Im;
                    if (++temp_delay >= 2) {
                        temp_delay = 0;
                    }
                    if (sb > 22 && gr >= this.fbt.num_hybrid_groups) {
                        int n3 = sb;
                        this.delay_buf_index_delay[n3] = this.delay_buf_index_delay[n3] + 1;
                        if (this.delay_buf_index_delay[n3] >= this.delay_D[sb]) {
                            this.delay_buf_index_delay[sb] = 0;
                        }
                    }
                    for (int m = 0; m < 3; ++m) {
                        int n4 = m;
                        temp_delay_ser[n4] = temp_delay_ser[n4] + 1;
                        if (temp_delay_ser[n4] < this.num_sample_delay_ser[m]) continue;
                        temp_delay_ser[m] = 0;
                    }
                }
            }
        }
        this.saved_delay = temp_delay;
        System.arraycopy(temp_delay_ser, 0, this.delay_buf_index_ser, 0, 3);
    }

    private static float magnitude_c(float[] c) {
        return (float)Math.sqrt(c[0] * c[0] + c[1] * c[1]);
    }

    private void ps_mix_phase(float[][][] X_left, float[][][] X_right, float[][][] X_hybrid_left, float[][][] X_hybrid_right) {
        float[] sf_iid;
        int no_iid_steps;
        float[] h11 = new float[2];
        float[] h12 = new float[2];
        float[] h21 = new float[2];
        float[] h22 = new float[2];
        float[] H11 = new float[2];
        float[] H12 = new float[2];
        float[] H21 = new float[2];
        float[] H22 = new float[2];
        float[] deltaH11 = new float[2];
        float[] deltaH12 = new float[2];
        float[] deltaH21 = new float[2];
        float[] deltaH22 = new float[2];
        float[] tempLeft = new float[2];
        float[] tempRight = new float[2];
        float[] phaseLeft = new float[2];
        float[] phaseRight = new float[2];
        if (this.iid_mode >= 3) {
            no_iid_steps = 15;
            sf_iid = PSTables.sf_iid_fine;
        } else {
            no_iid_steps = 7;
            sf_iid = PSTables.sf_iid_normal;
        }
        int nr_ipdopd_par = this.ipd_mode == 0 || this.ipd_mode == 3 ? 11 : this.nr_ipdopd_par;
        for (int gr = 0; gr < this.fbt.num_groups; ++gr) {
            int bk = this.fbt.bk(gr);
            int maxsb = gr < this.fbt.num_hybrid_groups ? this.fbt.group_border[gr] + 1 : this.fbt.group_border[gr + 1];
            for (int env = 0; env < this.num_env; ++env) {
                if (this.icc_mode < 3) {
                    float sinb;
                    float cosb;
                    float c_1 = sf_iid[no_iid_steps + this.iid_index[env][bk]];
                    float c_2 = sf_iid[no_iid_steps - this.iid_index[env][bk]];
                    float cosa = PSTables.cos_alphas[this.icc_index[env][bk]];
                    float sina = PSTables.sin_alphas[this.icc_index[env][bk]];
                    if (this.iid_mode >= 3) {
                        if (this.iid_index[env][bk] < 0) {
                            cosb = PSTables.cos_betas_fine[-this.iid_index[env][bk]][this.icc_index[env][bk]];
                            sinb = -PSTables.sin_betas_fine[-this.iid_index[env][bk]][this.icc_index[env][bk]];
                        } else {
                            cosb = PSTables.cos_betas_fine[this.iid_index[env][bk]][this.icc_index[env][bk]];
                            sinb = PSTables.sin_betas_fine[this.iid_index[env][bk]][this.icc_index[env][bk]];
                        }
                    } else if (this.iid_index[env][bk] < 0) {
                        cosb = PSTables.cos_betas_normal[-this.iid_index[env][bk]][this.icc_index[env][bk]];
                        sinb = -PSTables.sin_betas_normal[-this.iid_index[env][bk]][this.icc_index[env][bk]];
                    } else {
                        cosb = PSTables.cos_betas_normal[this.iid_index[env][bk]][this.icc_index[env][bk]];
                        sinb = PSTables.sin_betas_normal[this.iid_index[env][bk]][this.icc_index[env][bk]];
                    }
                    float ab1 = cosb * cosa;
                    float ab2 = sinb * sina;
                    float ab3 = sinb * cosa;
                    float ab4 = cosb * sina;
                    h11[0] = c_2 * (ab1 - ab2);
                    h12[0] = c_1 * (ab1 + ab2);
                    h21[0] = c_2 * (ab3 + ab4);
                    h22[0] = c_1 * (ab3 - ab4);
                } else {
                    float sing;
                    float cosg;
                    float sina;
                    float cosa;
                    if (this.iid_mode >= 3) {
                        abs_iid = Math.abs(this.iid_index[env][bk]);
                        cosa = PSTables.sincos_alphas_B_fine[no_iid_steps + this.iid_index[env][bk]][this.icc_index[env][bk]];
                        sina = PSTables.sincos_alphas_B_fine[30 - (no_iid_steps + this.iid_index[env][bk])][this.icc_index[env][bk]];
                        cosg = PSTables.cos_gammas_fine[abs_iid][this.icc_index[env][bk]];
                        sing = PSTables.sin_gammas_fine[abs_iid][this.icc_index[env][bk]];
                    } else {
                        abs_iid = Math.abs(this.iid_index[env][bk]);
                        cosa = PSTables.sincos_alphas_B_normal[no_iid_steps + this.iid_index[env][bk]][this.icc_index[env][bk]];
                        sina = PSTables.sincos_alphas_B_normal[14 - (no_iid_steps + this.iid_index[env][bk])][this.icc_index[env][bk]];
                        cosg = PSTables.cos_gammas_normal[abs_iid][this.icc_index[env][bk]];
                        sing = PSTables.sin_gammas_normal[abs_iid][this.icc_index[env][bk]];
                    }
                    h11[0] = 1.4142135f * (cosa * cosg);
                    h12[0] = 1.4142135f * (sina * cosg);
                    h21[0] = 1.4142135f * (-cosa * sing);
                    h22[0] = 1.4142135f * (sina * sing);
                }
                if (this.enable_ipdopd && bk < nr_ipdopd_par) {
                    int i = this.phase_hist;
                    tempLeft[0] = this.ipd_prev[bk][i][0] * 0.25f;
                    tempLeft[1] = this.ipd_prev[bk][i][1] * 0.25f;
                    tempRight[0] = this.opd_prev[bk][i][0] * 0.25f;
                    tempRight[1] = this.opd_prev[bk][i][1] * 0.25f;
                    this.ipd_prev[bk][i][0] = PSTables.ipdopd_cos_tab[Math.abs(this.ipd_index[env][bk])];
                    this.ipd_prev[bk][i][1] = PSTables.ipdopd_sin_tab[Math.abs(this.ipd_index[env][bk])];
                    this.opd_prev[bk][i][0] = PSTables.ipdopd_cos_tab[Math.abs(this.opd_index[env][bk])];
                    this.opd_prev[bk][i][1] = PSTables.ipdopd_sin_tab[Math.abs(this.opd_index[env][bk])];
                    tempLeft[0] = tempLeft[0] + this.ipd_prev[bk][i][0];
                    tempLeft[1] = tempLeft[1] + this.ipd_prev[bk][i][1];
                    tempRight[0] = tempRight[0] + this.opd_prev[bk][i][0];
                    tempRight[1] = tempRight[1] + this.opd_prev[bk][i][1];
                    if (i == 0) {
                        i = 2;
                    }
                    tempLeft[0] = tempLeft[0] + this.ipd_prev[bk][--i][0] * 0.5f;
                    tempLeft[1] = tempLeft[1] + this.ipd_prev[bk][i][1] * 0.5f;
                    tempRight[0] = tempRight[0] + this.opd_prev[bk][i][0] * 0.5f;
                    tempRight[1] = tempRight[1] + this.opd_prev[bk][i][1] * 0.5f;
                    float xy = PSOrig.magnitude_c(tempRight);
                    float pq = PSOrig.magnitude_c(tempLeft);
                    if (xy != 0.0f) {
                        phaseLeft[0] = tempRight[0] / xy;
                        phaseLeft[1] = tempRight[1] / xy;
                    } else {
                        phaseLeft[0] = 0.0f;
                        phaseLeft[1] = 0.0f;
                    }
                    float xypq = xy * pq;
                    if (xypq != 0.0f) {
                        float tmp1 = tempRight[0] * tempLeft[0] + tempRight[1] * tempLeft[1];
                        float tmp2 = tempRight[1] * tempLeft[0] - tempRight[0] * tempLeft[1];
                        phaseRight[0] = tmp1 / xypq;
                        phaseRight[1] = tmp2 / xypq;
                    } else {
                        phaseRight[0] = 0.0f;
                        phaseRight[1] = 0.0f;
                    }
                    h11[1] = h11[0] * phaseLeft[1];
                    h12[1] = h12[0] * phaseRight[1];
                    h21[1] = h21[0] * phaseLeft[1];
                    h22[1] = h22[0] * phaseRight[1];
                    h11[0] = h11[0] * phaseLeft[0];
                    h12[0] = h12[0] * phaseRight[0];
                    h21[0] = h21[0] * phaseLeft[0];
                    h22[0] = h22[0] * phaseRight[0];
                }
                float L = this.border_position[env + 1] - this.border_position[env];
                deltaH11[0] = (h11[0] - this.h11_prev[gr][0]) / L;
                deltaH12[0] = (h12[0] - this.h12_prev[gr][0]) / L;
                deltaH21[0] = (h21[0] - this.h21_prev[gr][0]) / L;
                deltaH22[0] = (h22[0] - this.h22_prev[gr][0]) / L;
                H11[0] = this.h11_prev[gr][0];
                H12[0] = this.h12_prev[gr][0];
                H21[0] = this.h21_prev[gr][0];
                H22[0] = this.h22_prev[gr][0];
                this.h11_prev[gr][0] = h11[0];
                this.h12_prev[gr][0] = h12[0];
                this.h21_prev[gr][0] = h21[0];
                this.h22_prev[gr][0] = h22[0];
                if (this.enable_ipdopd && bk < nr_ipdopd_par) {
                    deltaH11[1] = (h11[1] - this.h11_prev[gr][1]) / L;
                    deltaH12[1] = (h12[1] - this.h12_prev[gr][1]) / L;
                    deltaH21[1] = (h21[1] - this.h21_prev[gr][1]) / L;
                    deltaH22[1] = (h22[1] - this.h22_prev[gr][1]) / L;
                    H11[1] = this.h11_prev[gr][1];
                    H12[1] = this.h12_prev[gr][1];
                    H21[1] = this.h21_prev[gr][1];
                    H22[1] = this.h22_prev[gr][1];
                    if (this.fbt.bkm(gr)) {
                        deltaH11[1] = -deltaH11[1];
                        deltaH12[1] = -deltaH12[1];
                        deltaH21[1] = -deltaH21[1];
                        deltaH22[1] = -deltaH22[1];
                        H11[1] = -H11[1];
                        H12[1] = -H12[1];
                        H21[1] = -H21[1];
                        H22[1] = -H22[1];
                    }
                    this.h11_prev[gr][1] = h11[1];
                    this.h12_prev[gr][1] = h12[1];
                    this.h21_prev[gr][1] = h21[1];
                    this.h22_prev[gr][1] = h22[1];
                }
                for (int n = this.border_position[env]; n < this.border_position[env + 1]; ++n) {
                    H11[0] = H11[0] + deltaH11[0];
                    H12[0] = H12[0] + deltaH12[0];
                    H21[0] = H21[0] + deltaH21[0];
                    H22[0] = H22[0] + deltaH22[0];
                    if (this.enable_ipdopd && bk < nr_ipdopd_par) {
                        H11[1] = H11[1] + deltaH11[1];
                        H12[1] = H12[1] + deltaH12[1];
                        H21[1] = H21[1] + deltaH21[1];
                        H22[1] = H22[1] + deltaH22[1];
                    }
                    for (int sb = this.fbt.group_border[gr]; sb < maxsb; ++sb) {
                        float[] inLeft = new float[2];
                        float[] inRight = new float[2];
                        if (gr < this.fbt.num_hybrid_groups) {
                            inLeft[0] = X_hybrid_left[n][sb][0];
                            inLeft[1] = X_hybrid_left[n][sb][1];
                            inRight[0] = X_hybrid_right[n][sb][0];
                            inRight[1] = X_hybrid_right[n][sb][1];
                        } else {
                            inLeft[0] = X_left[n][sb][0];
                            inLeft[1] = X_left[n][sb][1];
                            inRight[0] = X_right[n][sb][0];
                            inRight[1] = X_right[n][sb][1];
                        }
                        tempLeft[0] = H11[0] * inLeft[0] + H21[0] * inRight[0];
                        tempLeft[1] = H11[0] * inLeft[1] + H21[0] * inRight[1];
                        tempRight[0] = H12[0] * inLeft[0] + H22[0] * inRight[0];
                        tempRight[1] = H12[0] * inLeft[1] + H22[0] * inRight[1];
                        if (this.enable_ipdopd && bk < nr_ipdopd_par) {
                            tempLeft[0] = tempLeft[0] - (H11[1] * inLeft[1] + H21[1] * inRight[1]);
                            tempLeft[1] = tempLeft[1] + (H11[1] * inLeft[0] + H21[1] * inRight[0]);
                            tempRight[0] = tempRight[0] - (H12[1] * inLeft[1] + H22[1] * inRight[1]);
                            tempRight[1] = tempRight[1] + (H12[1] * inLeft[0] + H22[1] * inRight[0]);
                        }
                        if (gr < this.fbt.num_hybrid_groups) {
                            X_hybrid_left[n][sb][0] = tempLeft[0];
                            X_hybrid_left[n][sb][1] = tempLeft[1];
                            X_hybrid_right[n][sb][0] = tempRight[0];
                            X_hybrid_right[n][sb][1] = tempRight[1];
                            continue;
                        }
                        X_left[n][sb][0] = tempLeft[0];
                        X_left[n][sb][1] = tempLeft[1];
                        X_right[n][sb][0] = tempRight[0];
                        X_right[n][sb][1] = tempRight[1];
                    }
                }
                ++this.phase_hist;
                if (this.phase_hist != 2) continue;
                this.phase_hist = 0;
            }
        }
    }

    @Override
    public void process(float[][][] X_left, float[][][] X_right) {
        float[][][] X_hybrid_left = new float[32][32][2];
        float[][][] X_hybrid_right = new float[32][32][2];
        this.ps_data_decode();
        this.hyb.hybrid_analysis(X_left, X_hybrid_left, this.fbt);
        this.ps_decorrelate(X_left, X_right, X_hybrid_left, X_hybrid_right);
        this.ps_mix_phase(X_left, X_right, X_hybrid_left, X_hybrid_right);
        this.hyb.hybrid_synthesis(X_left, X_hybrid_left, this.fbt);
        this.hyb.hybrid_synthesis(X_right, X_hybrid_right, this.fbt);
    }
}

