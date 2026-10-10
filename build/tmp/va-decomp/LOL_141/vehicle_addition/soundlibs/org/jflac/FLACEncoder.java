/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

public class FLACEncoder {
    private static final int ENCODER_IN_MAGIC = 0;
    private static final int ENCODER_IN_METADATA = 1;
    private static final int ENCODER_IN_AUDIO = 2;
    private static final int STREAM_ENCODER_OK = 0;
    private static final int STREAM_ENCODER_VERIFY_DECODER_ERROR = 1;
    private static final int STREAM_ENCODER_VERIFY_MISMATCH_IN_AUDIO_DATA = 2;
    private static final int STREAM_ENCODER_INVALID_CALLBACK = 3;
    private static final int STREAM_ENCODER_INVALID_NUMBER_OF_CHANNELS = 4;
    private static final int STREAM_ENCODER_INVALID_BITS_PER_SAMPLE = 5;
    private static final int STREAM_ENCODER_INVALID_SAMPLE_RATE = 6;
    private static final int STREAM_ENCODER_INVALID_BLOCK_SIZE = 7;
    private static final int STREAM_ENCODER_INVALID_MAX_LPC_ORDER = 8;
    private static final int STREAM_ENCODER_INVALID_QLP_COEFF_PRECISION = 9;
    private static final int STREAM_ENCODER_MID_SIDE_CHANNELS_MISMATCH = 10;
    private static final int STREAM_ENCODER_MID_SIDE_SAMPLE_SIZE_MISMATCH = 11;
    private static final int STREAM_ENCODER_ILLEGAL_MID_SIDE_FORCE = 12;
    private static final int STREAM_ENCODER_BLOCK_SIZE_TOO_SMALL_FOR_LPC_ORDER = 13;
    private static final int STREAM_ENCODER_NOT_STREAMABLE = 14;
    private static final int STREAM_ENCODER_FRAMING_ERROR = 15;
    private static final int STREAM_ENCODER_INVALID_METADATA = 16;
    private static final int STREAM_ENCODER_FATAL_ERROR_WHILE_ENCODING = 17;
    private static final int STREAM_ENCODER_FATAL_ERROR_WHILE_WRITING = 18;
    private static final int STREAM_ENCODER_MEMORY_ALLOCATION_ERROR = 19;
    private static final int STREAM_ENCODER_ALREADY_INITIALIZED = 20;
    private static final int STREAM_ENCODER_UNINITIALIZED = 21;
    boolean precompute_partition_sums;
    boolean disable_fixed_subframes;
    boolean disable_verbatim_subframes;
    int state;
    boolean verify;
    boolean streamable_subset;
    boolean do_mid_side_stereo;
    boolean loose_mid_side_stereo;
    int channels;
    int bits_per_sample;
    int sample_rate;
    int blocksize;
    int max_lpc_order;
    int qlp_coeff_precision;
    boolean do_qlp_coeff_prec_search;
    boolean do_exhaustive_model_search;
    boolean do_escape_coding;
    int min_residual_partition_order;
    int max_residual_partition_order;
    int rice_parameter_search_dist;
    long total_samples_estimate;
    int num_metadata_blocks;
    private static final String[] StreamEncoderStateString = new String[]{"STREAM_ENCODER_OK", "STREAM_ENCODER_VERIFY_DECODER_ERROR", "STREAM_ENCODER_VERIFY_MISMATCH_IN_AUDIO_DATA", "STREAM_ENCODER_INVALID_CALLBACK", "STREAM_ENCODER_INVALID_NUMBER_OF_CHANNELS", "STREAM_ENCODER_INVALID_BITS_PER_SAMPLE", "STREAM_ENCODER_INVALID_SAMPLE_RATE", "STREAM_ENCODER_INVALID_BLOCK_SIZE", "STREAM_ENCODER_INVALID_MAX_LPC_ORDER", "STREAM_ENCODER_INVALID_QLP_COEFF_PRECISION", "STREAM_ENCODER_MID_SIDE_CHANNELS_MISMATCH", "STREAM_ENCODER_MID_SIDE_SAMPLE_SIZE_MISMATCH", "STREAM_ENCODER_ILLEGAL_MID_SIDE_FORCE", "STREAM_ENCODER_BLOCK_SIZE_TOO_SMALL_FOR_LPC_ORDER", "STREAM_ENCODER_NOT_STREAMABLE", "STREAM_ENCODER_FRAMING_ERROR", "STREAM_ENCODER_INVALID_METADATA", "STREAM_ENCODER_FATAL_ERROR_WHILE_ENCODING", "STREAM_ENCODER_FATAL_ERROR_WHILE_WRITING", "STREAM_ENCODER_MEMORY_ALLOCATION_ERROR", "STREAM_ENCODER_ALREADY_INITIALIZED", "STREAM_ENCODER_UNINITIALIZED"};
    private static final String[] StreamEncoderWriteStatusString = new String[]{"STREAM_ENCODER_WRITE_STATUS_OK", "STREAM_ENCODER_WRITE_STATUS_FATAL_ERROR"};

    public FLACEncoder() {
        this.setDefaults();
        this.state = 21;
    }

    public void setVerify(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.verify = value;
    }

    public void setStreamableSubset(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.streamable_subset = value;
    }

    public void setDoMidSideStereo(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.do_mid_side_stereo = value;
    }

    public void setLooseMidSideStereo(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.loose_mid_side_stereo = value;
    }

    public void setChannels(int value) {
        if (this.state != 21) {
            return;
        }
        this.channels = value;
    }

    public void setBitsPerSample(int value) {
        if (this.state != 21) {
            return;
        }
        this.bits_per_sample = value;
    }

    public void setSampleRate(int value) {
        if (this.state != 21) {
            return;
        }
        this.sample_rate = value;
    }

    public void setBlocksize(int value) {
        if (this.state != 21) {
            return;
        }
        this.blocksize = value;
    }

    public void setMaxLPCOrder(int value) {
        if (this.state != 21) {
            return;
        }
        this.max_lpc_order = value;
    }

    public void setQLPCoeffPrecision(int value) {
        if (this.state != 21) {
            return;
        }
        this.qlp_coeff_precision = value;
    }

    public void setDoQLPCoeffPrecSearch(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.do_qlp_coeff_prec_search = value;
    }

    public void setDoExhaustiveModelSearch(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.do_exhaustive_model_search = value;
    }

    public void setMinResidualPartitionOrder(int value) {
        if (this.state != 21) {
            return;
        }
        this.min_residual_partition_order = value;
    }

    public void setMaxResidualPartitionOrder(int value) {
        if (this.state != 21) {
            return;
        }
        this.max_residual_partition_order = value;
    }

    public void setTotalSamplesEstimate(long value) {
        if (this.state != 21) {
            return;
        }
        this.total_samples_estimate = value;
    }

    public void disableConstantSubframes(boolean value) {
        if (this.state != 21) {
            return;
        }
    }

    public void disableFixedSubframes(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.disable_fixed_subframes = value;
    }

    public void disableVerbatimSubframes(boolean value) {
        if (this.state != 21) {
            return;
        }
        this.disable_verbatim_subframes = value;
    }

    public int getState() {
        return this.state;
    }

    public boolean getVerify() {
        return this.verify;
    }

    public boolean getStreamableSubset() {
        return this.streamable_subset;
    }

    public boolean getDoMidSideStereo() {
        return this.do_mid_side_stereo;
    }

    public boolean getLooseMidSideStereo() {
        return this.loose_mid_side_stereo;
    }

    public int getChannels() {
        return this.channels;
    }

    public int getBitsPerSample() {
        return this.bits_per_sample;
    }

    public int getSampleRate() {
        return this.sample_rate;
    }

    public int getBlocksize() {
        return this.blocksize;
    }

    public int getMaxLPCOrder() {
        return this.max_lpc_order;
    }

    public int getQLPCoeffPrecision() {
        return this.qlp_coeff_precision;
    }

    public boolean getDoQLPCoeffPrecSearch() {
        return this.do_qlp_coeff_prec_search;
    }

    public boolean getDoEscapeCoding() {
        return this.do_escape_coding;
    }

    public boolean getDoExhaustiveModelSearch() {
        return this.do_exhaustive_model_search;
    }

    public int getMinResidualPartitionOrder() {
        return this.min_residual_partition_order;
    }

    public int getMaxResidualPartitionOrder() {
        return this.max_residual_partition_order;
    }

    public int getRiceParameterSearchDist() {
        return this.rice_parameter_search_dist;
    }

    public long getTotalSamplesEstimate() {
        return this.total_samples_estimate;
    }

    public void setDefaults() {
        this.verify = false;
        this.streamable_subset = true;
        this.do_mid_side_stereo = false;
        this.loose_mid_side_stereo = false;
        this.channels = 2;
        this.bits_per_sample = 16;
        this.sample_rate = 44100;
        this.blocksize = 1152;
        this.max_lpc_order = 0;
        this.qlp_coeff_precision = 0;
        this.do_qlp_coeff_prec_search = false;
        this.do_exhaustive_model_search = false;
        this.do_escape_coding = false;
        this.min_residual_partition_order = 0;
        this.max_residual_partition_order = 0;
        this.rice_parameter_search_dist = 0;
        this.total_samples_estimate = 0L;
    }
}

