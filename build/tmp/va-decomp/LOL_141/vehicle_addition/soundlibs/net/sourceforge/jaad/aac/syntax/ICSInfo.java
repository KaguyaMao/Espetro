/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Profile;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.SampleFrequency;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ScaleFactorBands;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.ICPrediction;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.LTPrediction;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICSInfo
implements ScaleFactorBands {
    public static final int MAX_WINDOW_COUNT = 8;
    public static final int MAX_WINDOW_GROUP_COUNT = 8;
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.ICSInfo");
    public static final int WINDOW_SHAPE_SINE = 0;
    public static final int WINDOW_SHAPE_KAISER = 1;
    public static final int PREVIOUS = 0;
    public static final int CURRENT = 1;
    final DecoderConfig config;
    final SampleFrequency sf;
    private final int frameLength;
    private WindowSequence windowSequence;
    private int[] windowShape;
    private int maxSFB;
    private boolean predictionDataPresent;
    private ICPrediction icPredict;
    LTPrediction ltPredict;
    private int windowCount;
    private int windowGroupCount;
    private int[] windowGroupLength;
    private int swbCount;
    private int[] swbOffsets;

    public ICSInfo(DecoderConfig config) {
        this.config = config;
        this.sf = config.getSampleFrequency().getNominal();
        this.frameLength = config.getFrameLength();
        this.windowShape = new int[2];
        this.windowSequence = WindowSequence.ONLY_LONG_SEQUENCE;
        this.windowGroupLength = new int[8];
        if (LTPrediction.isLTPProfile(config.getProfile())) {
            this.ltPredict = new LTPrediction(this.frameLength);
        }
    }

    public void decode(BitStream in, boolean commonWindow) {
        in.skipBit();
        this.windowSequence = WindowSequence.forInt(in.readBits(2));
        this.windowShape[0] = this.windowShape[1];
        this.windowShape[1] = in.readBit();
        this.windowGroupCount = 1;
        this.windowGroupLength[0] = 1;
        if (this.windowSequence.equals((Object)WindowSequence.EIGHT_SHORT_SEQUENCE)) {
            this.maxSFB = in.readBits(4);
            for (int i = 0; i < 7; ++i) {
                if (in.readBool()) {
                    int n = this.windowGroupCount - 1;
                    this.windowGroupLength[n] = this.windowGroupLength[n] + 1;
                    continue;
                }
                ++this.windowGroupCount;
                this.windowGroupLength[this.windowGroupCount - 1] = 1;
            }
            this.windowCount = 8;
            this.swbOffsets = SWB_OFFSET_SHORT_WINDOW[this.sf.getIndex()];
            this.swbCount = SWB_SHORT_WINDOW_COUNT[this.sf.getIndex()];
            this.predictionDataPresent = false;
        } else {
            this.maxSFB = in.readBits(6);
            this.windowCount = 1;
            this.swbOffsets = SWB_OFFSET_LONG_WINDOW[this.sf.getIndex()];
            this.swbCount = SWB_LONG_WINDOW_COUNT[this.sf.getIndex()];
            this.predictionDataPresent = in.readBool();
            if (this.predictionDataPresent) {
                this.readPredictionData(in, commonWindow);
            }
        }
    }

    private void readPredictionData(BitStream in, boolean commonWindow) {
        LOGGER.fine("prediction present");
        Profile profile = this.config.getProfile();
        switch (profile) {
            case AAC_MAIN: {
                if (this.icPredict == null) {
                    this.icPredict = new ICPrediction();
                }
                this.icPredict.decode(in, this.maxSFB, this.sf);
                break;
            }
            case AAC_LTP: 
            case ER_AAC_LTP: {
                this.ltPredict.decode(in, this, profile);
                break;
            }
            default: {
                throw new AACException("unexpected profile for LTP: " + String.valueOf((Object)profile));
            }
        }
    }

    public int getMaxSFB() {
        return this.maxSFB;
    }

    public int getSFB() {
        return Math.min(this.sf.getMaximalPredictionSFB(), this.maxSFB);
    }

    public int getSWBCount() {
        return this.swbCount;
    }

    public int[] getSWBOffsets() {
        return this.swbOffsets;
    }

    public int getSWBOffsetMax() {
        return this.swbOffsets[this.swbCount];
    }

    public int getWindowCount() {
        return this.windowCount;
    }

    public int getWindowGroupCount() {
        return this.windowGroupCount;
    }

    public int getWindowGroupLength(int g) {
        return this.windowGroupLength[g];
    }

    public WindowSequence getWindowSequence() {
        return this.windowSequence;
    }

    public boolean isEightShortFrame() {
        return this.windowSequence.equals((Object)WindowSequence.EIGHT_SHORT_SEQUENCE);
    }

    public int getWindowShape(int index) {
        return this.windowShape[index];
    }

    public void processICP(float[] data) {
        if (this.config.getProfile().equals((Object)Profile.AAC_MAIN) && this.icPredict != null && this.predictionDataPresent) {
            this.icPredict.process(this, data);
        }
    }

    public void setCommonData(BitStream in, ICSInfo info) {
        this.windowSequence = WindowSequence.valueOf(info.windowSequence.name());
        this.windowShape[0] = this.windowShape[1];
        this.windowShape[1] = info.windowShape[1];
        this.maxSFB = info.maxSFB;
        this.predictionDataPresent = info.predictionDataPresent;
        this.icPredict = null;
        this.windowCount = info.windowCount;
        this.windowGroupCount = info.windowGroupCount;
        this.windowGroupLength = Utils.copyOf(info.windowGroupLength);
        this.swbCount = info.swbCount;
        this.swbOffsets = Utils.copyOf(info.swbOffsets);
        if (this.predictionDataPresent && this.ltPredict != null) {
            this.ltPredict.decode(in, this, this.config.getProfile());
        }
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.INFO);
        }
    }

    public static enum WindowSequence {
        ONLY_LONG_SEQUENCE,
        LONG_START_SEQUENCE,
        EIGHT_SHORT_SEQUENCE,
        LONG_STOP_SEQUENCE;


        public static WindowSequence forInt(int i) {
            return switch (i) {
                case 0 -> ONLY_LONG_SEQUENCE;
                case 1 -> LONG_START_SEQUENCE;
                case 2 -> EIGHT_SHORT_SEQUENCE;
                case 3 -> LONG_STOP_SEQUENCE;
                default -> throw new AACException("unknown window sequence type");
            };
        }
    }
}

