/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Channel;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.FBT;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.Header;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SynthesisFilterbank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SynthesisFilterbank32;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.sbr.SynthesisFilterbank64;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public abstract class SBR {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.sbr.SBR");
    static final int MAX_NTSR = 32;
    static final int MAX_M = 49;
    public static final int MAX_L_E = 5;
    static final int EXT_SBR_DATA = 13;
    static final int EXT_SBR_DATA_CRC = 14;
    static final int NO_TIME_SLOTS_960 = 15;
    static final int NO_TIME_SLOTS = 16;
    static final int RATE = 2;
    static final int NOISE_FLOOR_OFFSET = 6;
    static final int T_HFGEN = 8;
    static final int T_HFADJ = 2;
    protected final DecoderConfig config;
    private final boolean downSampled;
    final SampleFrequency sample_rate;
    boolean valid = false;
    int rate;
    int k0;
    int kx;
    int M;
    int N_master;
    int N_high;
    int N_low;
    int N_Q;
    int[] N_L = new int[4];
    int[] n = new int[2];
    int[] f_master = new int[64];
    int[][] f_table_res = new int[2][64];
    int[] f_table_noise = new int[64];
    int[][] f_table_lim = new int[4][64];
    int[] table_map_k_to_g = new int[64];
    int kx_prev;
    int bsco;
    int bsco_prev;
    int M_prev;
    boolean reset;
    int frame;
    int noPatches;
    int[] patchNoSubbands = new int[64];
    int[] patchStartSubband = new int[64];
    public final int numTimeSlotsRate;
    public final int numTimeSlots;
    int tHFGen;
    int tHFAdj;
    int bs_sbr_crc_bits;
    Header hdr = null;
    Header hdr_saved = null;
    int bs_samplerate_mode;

    public boolean isSBRDownSampled() {
        return this.downSampled;
    }

    SynthesisFilterbank openFilterbank() {
        return this.downSampled ? new SynthesisFilterbank32() : new SynthesisFilterbank64();
    }

    public void invalidate() {
        this.valid = false;
    }

    public boolean isValid() {
        return this.valid;
    }

    public SBR(DecoderConfig config) {
        this.config = config;
        this.downSampled = !config.setSBRPresent();
        this.sample_rate = config.getOutputFrequency().getNominal();
        this.bs_samplerate_mode = 1;
        this.rate = this.bs_samplerate_mode != 0 ? 2 : 1;
        this.tHFGen = 8;
        this.tHFAdj = 2;
        this.bsco = 0;
        this.bsco_prev = 0;
        this.M_prev = 0;
        if (config.isSmallFrameUsed()) {
            this.numTimeSlotsRate = 30;
            this.numTimeSlots = 15;
        } else {
            this.numTimeSlotsRate = 32;
            this.numTimeSlots = 16;
        }
    }

    int calc_sbr_tables(Header hdr) {
        int result = 0;
        this.k0 = FBT.qmf_start_channel(hdr.bs_start_freq, this.bs_samplerate_mode, this.sample_rate);
        int k2 = FBT.qmf_stop_channel(hdr.bs_stop_freq, this.sample_rate, this.k0);
        if (this.sample_rate.getFrequency() >= 48000) {
            if (k2 - this.k0 > 32) {
                ++result;
            }
        } else if (this.sample_rate.getFrequency() <= 32000) {
            if (k2 - this.k0 > 48) {
                ++result;
            }
        } else if (k2 - this.k0 > 45) {
            ++result;
        }
        result = hdr.bs_freq_scale == 0 ? (result += FBT.master_frequency_table_fs0(this, this.k0, k2, hdr.bs_alter_scale)) : (result += FBT.master_frequency_table(this, this.k0, k2, hdr.bs_freq_scale, hdr.bs_alter_scale));
        result = (result += FBT.derived_frequency_table(this, hdr.bs_xover_band, k2)) > 0 ? 1 : 0;
        return result;
    }

    public void decode(BitStream ld, boolean crc) {
        int result;
        int rt;
        this.bs_sbr_crc_bits = crc ? ld.readBits(10) : -1;
        this.reset = this.readHeader(ld);
        if (this.reset && (rt = this.calc_sbr_tables(this.hdr)) > 0) {
            this.calc_sbr_tables(this.swapHeaders());
        }
        this.valid = this.hdr != null ? (result = this.sbr_data(ld)) == 0 : true;
    }

    private Header swapHeaders() {
        Header hdr = this.hdr_saved;
        this.hdr_saved = this.hdr;
        if (hdr == null) {
            hdr = new Header();
        }
        this.hdr = hdr;
        return hdr;
    }

    private boolean readHeader(BitStream ld) {
        boolean bs_header_flag = ld.readBool();
        if (bs_header_flag) {
            Header hdr = this.swapHeaders();
            hdr.decode(ld);
            return hdr.differs(this.hdr_saved);
        }
        return false;
    }

    protected abstract int sbr_data(BitStream var1);

    public abstract void process(float[] var1, float[] var2);

    protected void readExtendedData(BitStream ld) {
        boolean bs_extended_data = ld.readBool();
        if (bs_extended_data) {
            int cnt = ld.readBits(4);
            if (cnt == 15) {
                cnt += ld.readBits(8);
            }
            ld = ld.readSubStream(8 * cnt);
            while (ld.getBitsLeft() > 7) {
                int bs_extension_id = ld.readBits(2);
                this.sbr_extension(ld, bs_extension_id);
            }
        }
    }

    protected void sbr_extension(BitStream ld, int bs_extension_id) {
    }

    protected void sinusoidal_coding(BitStream ld, Channel ch) {
        for (int n = 0; n < this.N_high; ++n) {
            ch.bs_add_harmonic[n] = ld.readBit();
        }
    }

    protected void sbr_save_prev_data(Channel ch) {
        int i;
        this.kx_prev = this.kx;
        this.M_prev = this.M;
        this.bsco_prev = this.bsco;
        ch.L_E_prev = ch.L_E;
        if (ch.L_E <= 0) {
            throw new AACException("L_E<0");
        }
        ch.f_prev = ch.f[ch.L_E - 1];
        for (i = 0; i < 49; ++i) {
            ch.E_prev[i] = ch.E[i][ch.L_E - 1];
            ch.Q_prev[i] = ch.Q[i][ch.L_Q - 1];
        }
        for (i = 0; i < 49; ++i) {
            ch.bs_add_harmonic_prev[i] = ch.bs_add_harmonic[i];
        }
        ch.bs_add_harmonic_flag_prev = ch.bs_add_harmonic_flag;
        ch.prevEnvIsShort = ch.l_A == ch.L_E ? 0 : -1;
    }

    protected void sbr_save_matrix(Channel ch) {
        int j;
        int i;
        for (i = 0; i < this.tHFGen; ++i) {
            for (j = 0; j < 64; ++j) {
                ch.Xsbr[i][j][0] = ch.Xsbr[i + this.numTimeSlotsRate][j][0];
                ch.Xsbr[i][j][1] = ch.Xsbr[i + this.numTimeSlotsRate][j][1];
            }
        }
        for (i = this.tHFGen; i < 40; ++i) {
            for (j = 0; j < 64; ++j) {
                ch.Xsbr[i][j][0] = 0.0f;
                ch.Xsbr[i][j][1] = 0.0f;
            }
        }
    }

    public static void upsample(float[] data) {
        for (int i = data.length / 2 - 1; i > 0; --i) {
            float v;
            data[2 * i] = v = data[i];
            data[2 * i + 1] = v;
        }
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.FINEST);
        }
    }
}

