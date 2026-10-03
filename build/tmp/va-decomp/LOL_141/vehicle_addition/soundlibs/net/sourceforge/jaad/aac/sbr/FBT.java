/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SBR;
import java.util.Arrays;

class FBT {
    static final int[] startMinTable = new int[]{7, 7, 10, 11, 12, 16, 16, 17, 24, 32, 35, 48};
    static final int[] offsetIndexTable = new int[]{5, 5, 4, 4, 4, 3, 2, 1, 0, 6, 6, 6};
    static final int[][] OFFSET = new int[][]{{-8, -7, -6, -5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7}, {-5, -4, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13}, {-5, -3, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16}, {-6, -4, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16}, {-4, -2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20}, {-2, -1, 0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20, 24}, {0, 1, 2, 3, 4, 5, 6, 7, 9, 11, 13, 16, 20, 24, 28, 33}};
    public static final int LO_RES = 0;
    public static final int HI_RES = 1;
    private static final int[] stopMinTable = new int[]{13, 15, 20, 21, 23, 32, 32, 35, 48, 64, 70, 96};
    private static final int[][] STOP_OFFSET_TABLE = new int[][]{{0, 2, 4, 6, 8, 11, 14, 18, 22, 26, 31, 37, 44, 51}, {0, 2, 4, 6, 8, 11, 14, 18, 22, 26, 31, 36, 42, 49}, {0, 2, 4, 6, 8, 11, 14, 17, 21, 25, 29, 34, 39, 44}, {0, 2, 4, 6, 8, 11, 14, 17, 20, 24, 28, 33, 38, 43}, {0, 2, 4, 6, 8, 11, 14, 17, 20, 24, 28, 32, 36, 41}, {0, 2, 4, 6, 8, 10, 12, 14, 17, 20, 23, 26, 29, 32}, {0, 2, 4, 6, 8, 10, 12, 14, 17, 20, 23, 26, 29, 32}, {0, 1, 3, 5, 7, 9, 11, 13, 15, 17, 20, 23, 26, 29}, {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 12, 14, 16}, {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0}, {0, -1, -2, -3, -4, -5, -6, -6, -6, -6, -6, -6, -6, -6}, {0, -3, -6, -9, -12, -15, -18, -20, -22, -24, -26, -28, -30, -32}};
    private static final float[] limiterBandsCompare = new float[]{1.327152f, 1.185093f, 1.119872f};

    FBT() {
    }

    public static int qmf_start_channel(int bs_start_freq, int bs_samplerate_mode, SampleFrequency sample_rate) {
        int startMin = startMinTable[sample_rate.getIndex()];
        int offsetIndex = offsetIndexTable[sample_rate.getIndex()];
        if (bs_samplerate_mode != 0) {
            return startMin + OFFSET[offsetIndex][bs_start_freq];
        }
        return startMin + OFFSET[6][bs_start_freq];
    }

    public static int qmf_stop_channel(int bs_stop_freq, SampleFrequency sample_rate, int k0) {
        if (bs_stop_freq == 15) {
            return Math.min(64, k0 * 3);
        }
        if (bs_stop_freq == 14) {
            return Math.min(64, k0 * 2);
        }
        int stopMin = stopMinTable[sample_rate.getIndex()];
        return Math.min(64, stopMin + STOP_OFFSET_TABLE[sample_rate.getIndex()][Math.min(bs_stop_freq, 13)]);
    }

    public static int master_frequency_table_fs0(SBR sbr, int k0, int k2, boolean bs_alter_scale) {
        int k;
        int[] vDk = new int[64];
        if (k2 <= k0) {
            sbr.N_master = 0;
            return 1;
        }
        int dk = bs_alter_scale ? 2 : 1;
        int nrBands = bs_alter_scale ? k2 - k0 + 2 >> 2 << 1 : k2 - k0 >> 1 << 1;
        if ((nrBands = Math.min(nrBands, 63)) <= 0) {
            return 1;
        }
        int k2Achieved = k0 + nrBands * dk;
        int k2Diff = k2 - k2Achieved;
        for (k = 0; k < nrBands; ++k) {
            vDk[k] = dk;
        }
        if (k2Diff != 0) {
            int k3;
            int incr = k2Diff > 0 ? -1 : 1;
            int n = k3 = k2Diff > 0 ? nrBands - 1 : 0;
            while (k2Diff != 0) {
                int n2 = k3;
                vDk[n2] = vDk[n2] - incr;
                k3 += incr;
                k2Diff += incr;
            }
        }
        sbr.f_master[0] = k0;
        for (k = 1; k <= nrBands; ++k) {
            sbr.f_master[k] = sbr.f_master[k - 1] + vDk[k - 1];
        }
        sbr.N_master = nrBands;
        sbr.N_master = Math.min(sbr.N_master, 64);
        return 0;
    }

    public static int find_bands(int warp, int bands, int a0, int a1) {
        float div = (float)Math.log(2.0);
        if (warp != 0) {
            div *= 1.3f;
        }
        return (int)((double)bands * Math.log((float)a1 / (float)a0) / (double)div + 0.5);
    }

    public static float find_initial_power(int bands, int a0, int a1) {
        return (float)Math.pow((float)a1 / (float)a0, 1.0f / (float)bands);
    }

    public static int master_frequency_table(SBR sbr, int k0, int k2, int bs_freq_scale, boolean bs_alter_scale) {
        int k;
        int k3;
        int k1;
        boolean twoRegions;
        int[] vDk0 = new int[64];
        int[] vDk1 = new int[64];
        int[] vk0 = new int[64];
        int[] vk1 = new int[64];
        int[] temp1 = new int[]{6, 5, 4};
        if (k2 <= k0) {
            sbr.N_master = 0;
            return 1;
        }
        int bands = temp1[bs_freq_scale - 1];
        if ((double)((float)k2 / (float)k0) > 2.2449) {
            twoRegions = true;
            k1 = k0 << 1;
        } else {
            twoRegions = false;
            k1 = k2;
        }
        int nrBand0 = 2 * FBT.find_bands(0, bands, k0, k1);
        nrBand0 = Math.min(nrBand0, 63);
        if (nrBand0 <= 0) {
            return 1;
        }
        float q = FBT.find_initial_power(nrBand0, k0, k1);
        float qk = k0;
        int A_1 = (int)(qk + 0.5f);
        for (k3 = 0; k3 <= nrBand0; ++k3) {
            int A_0 = A_1;
            A_1 = (int)((qk *= q) + 0.5f);
            vDk0[k3] = A_1 - A_0;
        }
        Arrays.sort(vDk0, 0, nrBand0);
        vk0[0] = k0;
        for (k3 = 1; k3 <= nrBand0; ++k3) {
            vk0[k3] = vk0[k3 - 1] + vDk0[k3 - 1];
            if (vDk0[k3 - 1] != 0) continue;
            return 1;
        }
        if (!twoRegions) {
            for (k3 = 0; k3 <= nrBand0; ++k3) {
                sbr.f_master[k3] = vk0[k3];
            }
            sbr.N_master = nrBand0;
            sbr.N_master = Math.min(sbr.N_master, 64);
            return 0;
        }
        int nrBand1 = 2 * FBT.find_bands(1, bands, k1, k2);
        nrBand1 = Math.min(nrBand1, 63);
        q = FBT.find_initial_power(nrBand1, k1, k2);
        qk = k1;
        A_1 = (int)(qk + 0.5f);
        for (k = 0; k <= nrBand1 - 1; ++k) {
            int A_0 = A_1;
            A_1 = (int)((qk *= q) + 0.5f);
            vDk1[k] = A_1 - A_0;
        }
        if (vDk1[0] < vDk0[nrBand0 - 1]) {
            Arrays.sort(vDk1, 0, nrBand1 + 1);
            int change = vDk0[nrBand0 - 1] - vDk1[0];
            vDk1[0] = vDk0[nrBand0 - 1];
            vDk1[nrBand1 - 1] = vDk1[nrBand1 - 1] - change;
        }
        Arrays.sort(vDk1, 0, nrBand1);
        vk1[0] = k1;
        for (k = 1; k <= nrBand1; ++k) {
            vk1[k] = vk1[k - 1] + vDk1[k - 1];
            if (vDk1[k - 1] != 0) continue;
            return 1;
        }
        sbr.N_master = nrBand0 + nrBand1;
        sbr.N_master = Math.min(sbr.N_master, 64);
        for (k = 0; k <= nrBand0; ++k) {
            sbr.f_master[k] = vk0[k];
        }
        for (k = nrBand0 + 1; k <= sbr.N_master; ++k) {
            sbr.f_master[k] = vk1[k - nrBand0];
        }
        return 0;
    }

    public static int derived_frequency_table(SBR sbr, int bs_xover_band, int k2) {
        int k;
        if (sbr.N_master <= bs_xover_band) {
            return 1;
        }
        sbr.N_high = sbr.N_master - bs_xover_band;
        sbr.n[0] = sbr.N_low = (sbr.N_high >> 1) + (sbr.N_high - (sbr.N_high >> 1 << 1));
        sbr.n[1] = sbr.N_high;
        for (int k3 = 0; k3 <= sbr.N_high; ++k3) {
            sbr.f_table_res[1][k3] = sbr.f_master[k3 + bs_xover_band];
        }
        sbr.M = sbr.f_table_res[1][sbr.N_high] - sbr.f_table_res[1][0];
        sbr.kx = sbr.f_table_res[1][0];
        if (sbr.kx > 32) {
            return 1;
        }
        if (sbr.kx + sbr.M > 64) {
            return 1;
        }
        int minus = (sbr.N_high & 1) != 0 ? 1 : 0;
        int i = 0;
        for (k = 0; k <= sbr.N_low; ++k) {
            if (k > 0) {
                i = 2 * k - minus;
            }
            sbr.f_table_res[0][k] = sbr.f_table_res[1][i];
        }
        sbr.N_Q = 0;
        if (sbr.hdr.bs_noise_bands == 0) {
            sbr.N_Q = 1;
        } else {
            sbr.N_Q = Math.max(1, FBT.find_bands(0, sbr.hdr.bs_noise_bands, sbr.kx, k2));
            sbr.N_Q = Math.min(5, sbr.N_Q);
        }
        i = 0;
        for (k = 0; k <= sbr.N_Q; ++k) {
            if (k > 0) {
                i += (sbr.N_low - i) / (sbr.N_Q + 1 - k);
            }
            sbr.f_table_noise[k] = sbr.f_table_res[0][i];
        }
        block3: for (int k4 = 0; k4 < 64; ++k4) {
            for (int g = 0; g < sbr.N_Q; ++g) {
                if (sbr.f_table_noise[g] > k4 || k4 >= sbr.f_table_noise[g + 1]) continue;
                sbr.table_map_k_to_g[k4] = g;
                continue block3;
            }
        }
        return 0;
    }

    public static void limiter_frequency_table(SBR sbr) {
        sbr.f_table_lim[0][0] = sbr.f_table_res[0][0] - sbr.kx;
        sbr.f_table_lim[0][1] = sbr.f_table_res[0][sbr.N_low] - sbr.kx;
        sbr.N_L[0] = 1;
        for (int s = 1; s < 4; ++s) {
            int k;
            int[] limTable = new int[100];
            int[] patchBorders = new int[64];
            patchBorders[0] = sbr.kx;
            for (k = 1; k <= sbr.noPatches; ++k) {
                patchBorders[k] = patchBorders[k - 1] + sbr.patchNoSubbands[k - 1];
            }
            for (k = 0; k <= sbr.N_low; ++k) {
                limTable[k] = sbr.f_table_res[0][k];
            }
            for (k = 1; k < sbr.noPatches; ++k) {
                limTable[k + sbr.N_low] = patchBorders[k];
            }
            Arrays.sort(limTable, 0, sbr.noPatches + sbr.N_low);
            k = 1;
            int nrLim = sbr.noPatches + sbr.N_low - 1;
            if (nrLim < 0) {
                return;
            }
            while (k <= nrLim) {
                float nOctaves = limTable[k - 1] != 0 ? (float)limTable[k] / (float)limTable[k - 1] : 0.0f;
                if (nOctaves < limiterBandsCompare[s - 1]) {
                    if (limTable[k] != limTable[k - 1]) {
                        int i;
                        boolean found = false;
                        boolean found2 = false;
                        for (i = 0; i <= sbr.noPatches; ++i) {
                            if (limTable[k] != patchBorders[i]) continue;
                            found = true;
                        }
                        if (found) {
                            found2 = false;
                            for (i = 0; i <= sbr.noPatches; ++i) {
                                if (limTable[k - 1] != patchBorders[i]) continue;
                                found2 = true;
                            }
                            if (found2) {
                                ++k;
                                continue;
                            }
                            limTable[k - 1] = sbr.f_table_res[0][sbr.N_low];
                            Arrays.sort(limTable, 0, sbr.noPatches + sbr.N_low);
                            --nrLim;
                            continue;
                        }
                    }
                    limTable[k] = sbr.f_table_res[0][sbr.N_low];
                    Arrays.sort(limTable, 0, nrLim);
                    --nrLim;
                    continue;
                }
                ++k;
            }
            sbr.N_L[s] = nrLim;
            for (int l = 0; l <= nrLim; ++l) {
                sbr.f_table_lim[s][l] = limTable[l] - sbr.kx;
            }
        }
    }
}

