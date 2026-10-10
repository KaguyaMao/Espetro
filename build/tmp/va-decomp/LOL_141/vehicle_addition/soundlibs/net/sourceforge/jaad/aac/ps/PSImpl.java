/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Extension;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.FBType;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.Filterbank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.ICCData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDMode;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.IIDTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ps.PSTables;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.PS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.Arrays;

public class PSImpl
implements PS {
    final IIDData iid = new IIDData();
    final ICCData icc = new ICCData();
    Extension ext = new Extension(this.iid);
    FBType fbt = FBType.T20;
    boolean var_borders;
    int num_env;
    int[] border_position = new int[6];
    boolean ps_data_available;
    boolean header_read;
    final Filterbank fb;
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

    public PSImpl(int frameLen) {
        int i;
        this.fb = new Filterbank(frameLen);
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
    }

    @Override
    public boolean isDataAvailable() {
        return this.ps_data_available;
    }

    @Override
    public void decode(BitStream ld) {
        if (ld.readBool()) {
            this.header_read = true;
            this.iid.readMode(ld);
            this.icc.readMode(ld);
            this.ext.readMode(ld);
            this.fbt = FBType.T20.max(this.iid.fbType()).max(this.icc.fbType());
        }
        this.var_borders = ld.readBit() != 0;
        int tmp = ld.readBits(2);
        this.num_env = PSTables.num_env_tab[this.var_borders ? 1 : 0][tmp];
        if (this.var_borders) {
            for (int n = 1; n < this.num_env + 1; ++n) {
                this.border_position[n] = ld.readBits(5) + 1;
            }
        }
        this.iid.readData(ld, this.num_env);
        this.icc.readData(ld, this.num_env);
        this.ext.readData(ld, this.num_env);
        this.ps_data_available = true;
    }

    private void ps_data_decode() {
        if (!this.ps_data_available) {
            this.num_env = 0;
        }
        this.iid.decode(this.num_env);
        this.icc.decode(this.num_env);
        this.ext.decode(this.num_env);
        if (this.num_env == 0) {
            this.num_env = 1;
        }
        this.iid.update(this.num_env);
        this.icc.update(this.num_env);
        this.ext.update(this.num_env);
        this.ps_data_available = false;
        if (!this.var_borders) {
            this.border_position[0] = 0;
            for (int env = 1; env < this.num_env; ++env) {
                this.border_position[env] = env * this.fb.len / this.num_env;
            }
            this.border_position[this.num_env] = this.fb.len;
        } else {
            this.border_position[0] = 0;
            if (this.border_position[this.num_env] < this.fb.len) {
                this.iid.restore(this.num_env);
                this.icc.restore(this.num_env);
                this.ext.restore(this.num_env);
                ++this.num_env;
                this.border_position[this.num_env] = this.fb.len;
            }
            int bpl = this.border_position[0];
            for (int env = 1; env < this.num_env; ++env) {
                int bp = this.border_position[env];
                int max = this.fb.len - (this.num_env - env);
                if ((bpl = Utils.clip(bp, bpl + 1, max)) == bp) continue;
                this.border_position[env] = bpl;
            }
        }
        if (this.fbt == FBType.T34) {
            this.iid.mapTo34(this.num_env);
            this.icc.mapTo34(this.num_env);
            this.ext.mapTo34(this.num_env);
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
            int maxsb = this.fbt.maxsb(gr);
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
            int maxsb = this.fbt.maxsb(gr);
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
        IIDTables tables = ((IIDMode)this.iid.mode()).tables();
        for (int gr = 0; gr < this.fbt.num_groups; ++gr) {
            int bk = this.fbt.bk(gr);
            int maxsb = gr < this.fbt.num_hybrid_groups ? this.fbt.group_border[gr] + 1 : this.fbt.group_border[gr + 1];
            for (int env = 0; env < this.num_env; ++env) {
                int iid_index = this.iid.envs[env].index[bk];
                int iid_sign = iid_index < 0 ? -1 : 1;
                iid_index = Math.abs(iid_index);
                int icc_index = this.icc.envs[env].index[bk];
                if (this.icc.mode().id < 3) {
                    float[] sf_iid = tables.sf;
                    int num_steps = tables.num_steps;
                    float c_1 = sf_iid[num_steps + iid_index];
                    float c_2 = sf_iid[num_steps - iid_index];
                    float cosa = PSTables.cos_alphas[icc_index];
                    float sina = PSTables.sin_alphas[icc_index];
                    float cosb = tables.cos_betas[iid_index][icc_index];
                    float sinb = tables.sin_betas[iid_index][icc_index] * (float)iid_sign;
                    float ab1 = cosb * cosa;
                    float ab2 = sinb * sina;
                    float ab3 = sinb * cosa;
                    float ab4 = cosb * sina;
                    h11[0] = c_2 * (ab1 - ab2);
                    h12[0] = c_1 * (ab1 + ab2);
                    h21[0] = c_2 * (ab3 + ab4);
                    h22[0] = c_1 * (ab3 - ab4);
                } else {
                    int num_steps = tables.num_steps;
                    float cosa = tables.sincos_alphas_b[num_steps + iid_index][icc_index];
                    float sina = tables.sincos_alphas_b[2 * num_steps - (num_steps + iid_index)][icc_index];
                    float cosg = tables.cos_gammas[iid_index][icc_index];
                    float sing = tables.sin_gammas[iid_index][icc_index];
                    h11[0] = 1.4142135f * (cosa * cosg);
                    h12[0] = 1.4142135f * (sina * cosg);
                    h21[0] = 1.4142135f * (-cosa * sing);
                    h22[0] = 1.4142135f * (sina * sing);
                }
                int nr_ipdopd_par = this.ext.nr_par();
                if (bk < nr_ipdopd_par) {
                    float[] ipd_prev = this.ext.data.ipd.prev[bk][this.phase_hist];
                    float[] opd_prev = this.ext.data.opd.prev[bk][this.phase_hist];
                    tempLeft[0] = ipd_prev[0] * 0.25f;
                    tempLeft[1] = ipd_prev[1] * 0.25f;
                    tempRight[0] = opd_prev[0] * 0.25f;
                    tempRight[1] = opd_prev[1] * 0.25f;
                    int ipd_index = Math.abs(this.ext.data.ipd.envs[env].index[bk]);
                    int opd_index = Math.abs(this.ext.data.ipd.envs[env].index[bk]);
                    ipd_prev[0] = PSTables.ipdopd_cos_tab[ipd_index];
                    ipd_prev[1] = PSTables.ipdopd_sin_tab[ipd_index];
                    opd_prev[0] = PSTables.ipdopd_cos_tab[opd_index];
                    opd_prev[1] = PSTables.ipdopd_sin_tab[opd_index];
                    tempLeft[0] = tempLeft[0] + ipd_prev[0];
                    tempLeft[1] = tempLeft[1] + ipd_prev[1];
                    tempRight[0] = tempRight[0] + opd_prev[0];
                    tempRight[1] = tempRight[1] + opd_prev[1];
                    ++this.phase_hist;
                    this.phase_hist %= 2;
                    ipd_prev = this.ext.data.opd.prev[bk][this.phase_hist];
                    opd_prev = this.ext.data.opd.prev[bk][this.phase_hist];
                    tempLeft[0] = tempLeft[0] + ipd_prev[0] * 0.5f;
                    tempLeft[1] = tempLeft[1] + ipd_prev[1] * 0.5f;
                    tempRight[0] = tempRight[0] + opd_prev[0] * 0.5f;
                    tempRight[1] = tempRight[1] + opd_prev[1] * 0.5f;
                    float xy = PSImpl.magnitude_c(tempRight);
                    float pq = PSImpl.magnitude_c(tempLeft);
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
                if (bk < nr_ipdopd_par) {
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
                    if (bk < nr_ipdopd_par) {
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
                        if (bk < nr_ipdopd_par) {
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
            }
        }
    }

    @Override
    public void process(float[][][] X_left, float[][][] X_right) {
        float[][][] X_hybrid_left = new float[32][32][2];
        float[][][] X_hybrid_right = new float[32][32][2];
        this.ps_data_decode();
        this.fb.hybrid_analysis(X_left, X_hybrid_left, this.fbt);
        this.ps_decorrelate(X_left, X_right, X_hybrid_left, X_hybrid_right);
        this.ps_mix_phase(X_left, X_right, X_hybrid_left, X_hybrid_right);
        this.fb.hybrid_synthesis(X_left, X_hybrid_left, this.fbt);
        this.fb.hybrid_synthesis(X_right, X_hybrid_right, this.fbt);
    }
}

