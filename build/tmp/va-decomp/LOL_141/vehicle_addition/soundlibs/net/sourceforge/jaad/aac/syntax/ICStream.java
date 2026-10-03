/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.ChannelConfiguration;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error.RVLC;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.filterbank.FilterBank;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.gain.GainControl;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.huffman.HCB;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.huffman.Huffman;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.IQTable;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ScaleFactorTable;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.TNS;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ICStream
implements HCB,
ScaleFactorTable,
IQTable {
    static final Logger LOGGER = Logger.getLogger("jaad.aac.syntax.ICStream");
    public static final int MAX_SECTIONS = 120;
    private static final int SF_DELTA = 60;
    private static final int SF_OFFSET = 200;
    private static int randomState;
    private final int frameLength;
    private final ICSInfo info;
    private final int[] sfbCB;
    private final int[] sectEnd;
    private final float[] iqData;
    private final float[] scaleFactors;
    private int globalGain;
    private boolean pulseDataPresent;
    private boolean tnsDataPresent;
    private boolean gainControlPresent;
    private TNS tns;
    private GainControl gainControl;
    private int[] pulseOffset;
    private int[] pulseAmp;
    private int pulseCount;
    private int pulseStartSWB;
    private boolean noiseUsed;
    private int reorderedSpectralDataLen;
    private int longestCodewordLen;
    private RVLC rvlc;
    private float[] overlap;

    public ICStream(DecoderConfig config) {
        this.frameLength = config.getFrameLength();
        this.info = new ICSInfo(config);
        this.sfbCB = new int[120];
        this.sectEnd = new int[120];
        this.iqData = new float[this.frameLength];
        this.scaleFactors = new float[120];
        this.overlap = new float[this.frameLength];
    }

    public void decode(BitStream in, boolean commonWindow, DecoderConfig conf) {
        if (conf.isScalefactorResilienceUsed() && this.rvlc == null) {
            this.rvlc = new RVLC();
        }
        boolean er = conf.getProfile().isErrorResilientProfile();
        this.globalGain = in.readBits(8);
        if (!commonWindow) {
            this.info.decode(in, commonWindow);
        }
        this.decodeSectionData(in, conf.isSectionDataResilienceUsed());
        this.decodeScaleFactors(in);
        this.pulseDataPresent = in.readBool();
        if (this.pulseDataPresent) {
            if (this.info.isEightShortFrame()) {
                throw new AACException("pulse data not allowed for short frames");
            }
            LOGGER.log(Level.FINE, "PULSE");
            this.decodePulseData(in);
        }
        this.tnsDataPresent = in.readBool();
        if (this.tnsDataPresent && !er) {
            if (this.tns == null) {
                this.tns = new TNS();
            }
            this.tns.decode(in, this.info);
        }
        this.gainControlPresent = in.readBool();
        if (this.gainControlPresent) {
            if (this.gainControl == null) {
                this.gainControl = new GainControl(this.frameLength);
            }
            LOGGER.log(Level.FINE, "GAIN");
            this.gainControl.decode(in, this.info.getWindowSequence());
        }
        if (conf.isSpectralDataResilienceUsed()) {
            int max = conf.getChannelConfiguration() == ChannelConfiguration.STEREO ? 6144 : 12288;
            this.reorderedSpectralDataLen = Math.max(in.readBits(14), max);
            this.longestCodewordLen = Math.max(in.readBits(6), 49);
        } else {
            this.decodeSpectralData(in);
        }
    }

    public void decodeSectionData(BitStream in, boolean sectionDataResilienceUsed) {
        Arrays.fill(this.sfbCB, 0);
        Arrays.fill(this.sectEnd, 0);
        int bits = this.info.isEightShortFrame() ? 3 : 5;
        int escVal = (1 << bits) - 1;
        int windowGroupCount = this.info.getWindowGroupCount();
        int maxSFB = this.info.getMaxSFB();
        int idx = 0;
        for (int g = 0; g < windowGroupCount; ++g) {
            int k = 0;
            while (k < maxSFB) {
                int incr;
                int end = k;
                int cb = in.readBits(4);
                if (cb == 12) {
                    throw new AACException("invalid huffman codebook: 12");
                }
                do {
                    incr = in.readBits(bits);
                    end += incr;
                } while (incr == escVal);
                if (end > maxSFB) {
                    throw new AACException("too many bands: " + end + ", allowed: " + maxSFB);
                }
                while (k < end) {
                    this.sfbCB[idx] = cb;
                    this.sectEnd[idx] = end;
                    ++k;
                    ++idx;
                }
            }
        }
    }

    private void decodePulseData(BitStream in) {
        this.pulseCount = in.readBits(2) + 1;
        this.pulseStartSWB = in.readBits(6);
        if (this.pulseStartSWB >= this.info.getSWBCount()) {
            throw new AACException("pulse SWB out of range: " + this.pulseStartSWB + " > " + this.info.getSWBCount());
        }
        if (this.pulseOffset == null || this.pulseCount != this.pulseOffset.length) {
            this.pulseOffset = new int[this.pulseCount];
            this.pulseAmp = new int[this.pulseCount];
        }
        this.pulseOffset[0] = this.info.getSWBOffsets()[this.pulseStartSWB];
        this.pulseOffset[0] = this.pulseOffset[0] + in.readBits(5);
        this.pulseAmp[0] = in.readBits(4);
        for (int i = 1; i < this.pulseCount; ++i) {
            this.pulseOffset[i] = in.readBits(5) + this.pulseOffset[i - 1];
            if (this.pulseOffset[i] > 1023) {
                throw new AACException("pulse offset out of range: " + this.pulseOffset[0]);
            }
            this.pulseAmp[i] = in.readBits(4);
        }
    }

    public void decodeScaleFactors(BitStream in) {
        int windowGroups = this.info.getWindowGroupCount();
        int maxSFB = this.info.getMaxSFB();
        int[] offset = new int[]{this.globalGain, this.globalGain - 90, 0};
        boolean noiseFlag = true;
        int idx = 0;
        for (int g = 0; g < windowGroups; ++g) {
            int sfb = 0;
            block6: while (sfb < maxSFB) {
                int end = this.sectEnd[idx];
                switch (this.sfbCB[idx]) {
                    case 0: {
                        while (sfb < end) {
                            this.scaleFactors[idx] = 0.0f;
                            ++sfb;
                            ++idx;
                        }
                        continue block6;
                    }
                    case 14: 
                    case 15: {
                        int tmp;
                        while (sfb < end) {
                            offset[2] = offset[2] + (Huffman.decodeScaleFactor(in) - 60);
                            tmp = Math.min(Math.max(offset[2], -155), 100);
                            this.scaleFactors[idx] = SCALEFACTOR_TABLE[-tmp + 200];
                            ++sfb;
                            ++idx;
                        }
                        continue block6;
                    }
                    case 13: {
                        int tmp;
                        while (sfb < end) {
                            if (noiseFlag) {
                                offset[1] = offset[1] + (in.readBits(9) - 256);
                                noiseFlag = false;
                            } else {
                                offset[1] = offset[1] + (Huffman.decodeScaleFactor(in) - 60);
                            }
                            tmp = Math.min(Math.max(offset[1], -100), 155);
                            this.scaleFactors[idx] = -SCALEFACTOR_TABLE[tmp + 200];
                            ++sfb;
                            ++idx;
                        }
                        continue block6;
                    }
                }
                while (sfb < end) {
                    offset[0] = offset[0] + (Huffman.decodeScaleFactor(in) - 60);
                    if (offset[0] > 255) {
                        throw new AACException("scalefactor out of range: " + offset[0]);
                    }
                    this.scaleFactors[idx] = SCALEFACTOR_TABLE[offset[0] - 100 + 200];
                    ++sfb;
                    ++idx;
                }
            }
        }
    }

    private void decodeSpectralData(BitStream in) {
        Arrays.fill(this.iqData, 0.0f);
        int maxSFB = this.info.getMaxSFB();
        int windowGroups = this.info.getWindowGroupCount();
        int[] offsets = this.info.getSWBOffsets();
        int[] buf = new int[4];
        int idx = 0;
        int groupOff = 0;
        for (int g = 0; g < windowGroups; ++g) {
            int groupLen = this.info.getWindowGroupLength(g);
            int sfb = 0;
            while (sfb < maxSFB) {
                int hcb = this.sfbCB[idx];
                int off = groupOff + offsets[sfb];
                int width = offsets[sfb + 1] - offsets[sfb];
                if (hcb == 0 || hcb == 15 || hcb == 14) {
                    w = 0;
                    while (w < groupLen) {
                        Arrays.fill(this.iqData, off, off + width, 0.0f);
                        ++w;
                        off += 128;
                    }
                } else if (hcb == 13) {
                    w = 0;
                    while (w < groupLen) {
                        float energy = 0.0f;
                        for (int k = 0; k < width; ++k) {
                            randomState = 1664525 * randomState + 1013904223;
                            this.iqData[off + k] = randomState;
                            energy += this.iqData[off + k] * this.iqData[off + k];
                        }
                        float scale = (float)((double)this.scaleFactors[idx] / Math.sqrt(energy));
                        for (int k = 0; k < width; ++k) {
                            int n = off + k;
                            this.iqData[n] = this.iqData[n] * scale;
                        }
                        ++w;
                        off += 128;
                    }
                } else {
                    w = 0;
                    while (w < groupLen) {
                        int num = hcb >= 5 ? 2 : 4;
                        for (int k = 0; k < width; k += num) {
                            Huffman.decodeSpectralData(in, hcb, buf, 0);
                            for (int j = 0; j < num; ++j) {
                                this.iqData[off + k + j] = buf[j] > 0 ? IQ_TABLE[buf[j]] : -IQ_TABLE[-buf[j]];
                                int n = off + k + j;
                                this.iqData[n] = this.iqData[n] * this.scaleFactors[idx];
                            }
                        }
                        ++w;
                        off += 128;
                    }
                }
                ++sfb;
                ++idx;
            }
            groupOff += groupLen << 7;
        }
    }

    public float[] getInvQuantData() {
        return this.iqData;
    }

    public float[] getOverlap() {
        return this.overlap;
    }

    public ICSInfo getInfo() {
        return this.info;
    }

    public int[] getSectEnd() {
        return this.sectEnd;
    }

    public int[] getSfbCB() {
        return this.sfbCB;
    }

    public float[] getScaleFactors() {
        return this.scaleFactors;
    }

    public void process(float[] data, FilterBank filterBank) {
        filterBank.process(this.info.getWindowSequence(), this.info.getWindowShape(1), this.info.getWindowShape(0), this.iqData, data, this.overlap);
    }

    private void processTNS(float[] data, boolean decode) {
        if (this.tns != null && this.tnsDataPresent) {
            this.tns.process(this, data, this.info.sf, decode);
        }
    }

    public void processICP() {
        this.info.processICP(this.iqData);
    }

    public void processTNS() {
        this.processTNS(this.iqData, false);
    }

    public void processTNS(float[] data) {
        this.processTNS(data, true);
    }

    public void processLTP(FilterBank filterBank) {
        if (this.info.ltPredict != null) {
            this.info.ltPredict.process(this, filterBank);
        }
    }

    public void updateLTP(float[] data) {
        if (this.info.ltPredict != null) {
            this.info.ltPredict.updateState(data, this.getOverlap(), this.info.config.getProfile());
        }
    }

    public int getGlobalGain() {
        return this.globalGain;
    }

    public boolean isNoiseUsed() {
        return this.noiseUsed;
    }

    public int getLongestCodewordLength() {
        return this.longestCodewordLen;
    }

    public int getReorderedSpectralDataLength() {
        return this.reorderedSpectralDataLen;
    }

    public void processGainControl() {
        if (this.gainControl != null && this.gainControlPresent) {
            this.gainControl.process(this.iqData, this.info.getWindowShape(1), this.info.getWindowShape(0), this.info.getWindowSequence());
        }
    }

    static {
        if (!Utils.isDebug) {
            LOGGER.setLevel(Level.FINEST);
        }
        randomState = 523124044;
    }
}

