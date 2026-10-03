/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.error.BitsBuffer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.BitStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;

public class HCR {
    private static final int NUM_CB = 6;
    private static final int NUM_CB_ER = 22;
    private static final int MAX_CB = 32;
    private static final int VCB11_FIRST = 16;
    private static final int VCB11_LAST = 31;
    private static final int[] PRE_SORT_CB_STD = new int[]{11, 9, 7, 5, 3, 1};
    private static final int[] PRE_SORT_CB_ER = new int[]{11, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 9, 7, 5, 3, 1};
    private static final int[] MAX_CW_LEN = new int[]{0, 11, 9, 20, 16, 13, 11, 14, 12, 17, 14, 49, 0, 0, 0, 0, 14, 17, 21, 21, 25, 25, 29, 29, 29, 29, 33, 33, 33, 37, 37, 41};
    private static final int[] S = new int[]{1, 2, 4, 8, 16};
    private static final int[] B = new int[]{0x55555555, 0x33333333, 0xF0F0F0F, 0xFF00FF, 65535};

    private static int rewindReverse(int v, int len) {
        v = v >> S[0] & B[0] | v << S[0] & ~B[0];
        v = v >> S[1] & B[1] | v << S[1] & ~B[1];
        v = v >> S[2] & B[2] | v << S[2] & ~B[2];
        v = v >> S[3] & B[3] | v << S[3] & ~B[3];
        v = v >> S[4] & B[4] | v << S[4] & ~B[4];
        return v >>= 32 - len;
    }

    static int[] rewindReverse64(int hi, int lo, int len) {
        int[] i = new int[2];
        if (len <= 32) {
            i[0] = 0;
            i[1] = HCR.rewindReverse(lo, len);
        } else {
            lo = lo >> S[0] & B[0] | lo << S[0] & ~B[0];
            hi = hi >> S[0] & B[0] | hi << S[0] & ~B[0];
            lo = lo >> S[1] & B[1] | lo << S[1] & ~B[1];
            hi = hi >> S[1] & B[1] | hi << S[1] & ~B[1];
            lo = lo >> S[2] & B[2] | lo << S[2] & ~B[2];
            hi = hi >> S[2] & B[2] | hi << S[2] & ~B[2];
            lo = lo >> S[3] & B[3] | lo << S[3] & ~B[3];
            hi = hi >> S[3] & B[3] | hi << S[3] & ~B[3];
            lo = lo >> S[4] & B[4] | lo << S[4] & ~B[4];
            hi = hi >> S[4] & B[4] | hi << S[4] & ~B[4];
            i[1] = hi >> 64 - len | lo << len - 32;
            i[1] = lo >> 64 - len;
        }
        return i;
    }

    private static boolean isGoodCB(int cb, int sectCB) {
        boolean b = false;
        if (sectCB > 0 && sectCB <= 11 || sectCB >= 16 && sectCB <= 31) {
            b = cb < 11 ? sectCB == cb || sectCB == cb + 1 : sectCB == cb;
        }
        return b;
    }

    /*
     * WARNING - void declaration
     */
    public static void decodeReorderedSpectralData(ICStream ics, BitStream in, short[] spectralData, boolean sectionDataResilience) {
        int lastCB;
        int[] preSortCB;
        ICSInfo info = ics.getInfo();
        int windowGroupCount = info.getWindowGroupCount();
        int maxSFB = info.getMaxSFB();
        int[] swbOffsets = info.getSWBOffsets();
        int swbOffsetMax = info.getSWBOffsetMax();
        Object sectStart = null;
        Object sectEnd = null;
        Object numSec = null;
        Object sectCB = null;
        Object sectSFBOffsets = null;
        int spDataLen = ics.getReorderedSpectralDataLength();
        if (spDataLen == 0) {
            return;
        }
        int longestLen = ics.getLongestCodewordLength();
        if (longestLen == 0 || longestLen >= spDataLen) {
            throw new AACException("length of longest HCR codeword out of range");
        }
        int[] spOffsets = new int[8];
        int shortFrameLen = spectralData.length / 8;
        spOffsets[0] = 0;
        for (int g = 1; g < windowGroupCount; ++g) {
            spOffsets[g] = spOffsets[g - 1] + shortFrameLen * info.getWindowGroupLength(g - 1);
        }
        Codeword[] codeword = new Codeword[512];
        BitsBuffer[] segment = new BitsBuffer[512];
        if (sectionDataResilience) {
            preSortCB = PRE_SORT_CB_ER;
            lastCB = 22;
        } else {
            preSortCB = PRE_SORT_CB_STD;
            lastCB = 6;
        }
        boolean PCWs_done = false;
        int segmentsCount = 0;
        int numberOfCodewords = 0;
        int bitsread = 0;
        for (int sortloop = 0; sortloop < lastCB; ++sortloop) {
            int thisCB = preSortCB[sortloop];
            for (int sfb = 0; sfb < maxSFB; ++sfb) {
                int w_idx = 0;
                while (4 * w_idx < Math.min(swbOffsets[sfb + 1], swbOffsetMax) - swbOffsets[sfb]) {
                    for (int g = 0; g < windowGroupCount; ++g) {
                        for (int i = 0; i < numSec[g]; ++i) {
                            void thisSectCB;
                            if (sectStart[g][i] > sfb || sectEnd[g][i] <= sfb || !HCR.isGoodCB(thisCB, (int)(thisSectCB = sectCB[g][i]))) continue;
                            void sect_sfb_size = sectSFBOffsets[g][sfb + 1] - sectSFBOffsets[g][sfb];
                            int inc = thisSectCB < 5 ? 4 : 2;
                            int group_cws_count = 4 * info.getWindowGroupLength(g) / inc;
                            int segwidth = Math.min(MAX_CW_LEN[thisSectCB], longestLen);
                            for (int cws = 0; cws < group_cws_count && cws + w_idx * group_cws_count < sect_sfb_size; ++cws) {
                                int sp = spOffsets[g] + sectSFBOffsets[g][sfb] + inc * (cws + w_idx * group_cws_count);
                                if (!PCWs_done) {
                                    if (bitsread + segwidth <= spDataLen) {
                                        segment[segmentsCount].readSegment(segwidth, in);
                                        bitsread += segwidth;
                                        segment[segmentsCount].rewindReverse();
                                        ++segmentsCount;
                                    } else {
                                        if (bitsread < spDataLen) {
                                            int additional_bits = spDataLen - bitsread;
                                            segment[segmentsCount].readSegment(additional_bits, in);
                                            segment[segmentsCount].len += segment[segmentsCount - 1].len;
                                            segment[segmentsCount].rewindReverse();
                                            if (segment[segmentsCount - 1].len > 32) {
                                                segment[segmentsCount - 1].bufb = segment[segmentsCount].bufb + segment[segmentsCount - 1].showBits(segment[segmentsCount - 1].len - 32);
                                                segment[segmentsCount - 1].bufa = segment[segmentsCount].bufa + segment[segmentsCount - 1].showBits(32);
                                            } else {
                                                segment[segmentsCount - 1].bufa = segment[segmentsCount].bufa + segment[segmentsCount - 1].showBits(segment[segmentsCount - 1].len);
                                                segment[segmentsCount - 1].bufb = segment[segmentsCount].bufb;
                                            }
                                            segment[segmentsCount - 1].len += additional_bits;
                                        }
                                        bitsread = spDataLen;
                                        PCWs_done = true;
                                        codeword[0].fill(sp, (int)thisSectCB);
                                    }
                                } else {
                                    codeword[numberOfCodewords - segmentsCount].fill(sp, (int)thisSectCB);
                                }
                                ++numberOfCodewords;
                            }
                        }
                    }
                    ++w_idx;
                }
            }
        }
        if (segmentsCount == 0) {
            throw new AACException("no segments in HCR");
        }
        int numberOfSets = numberOfCodewords / segmentsCount;
        for (int set = 1; set <= numberOfSets; ++set) {
            block8: for (int trial = 0; trial < segmentsCount; ++trial) {
                for (int codewordBase = 0; codewordBase < segmentsCount; ++codewordBase) {
                    int segmentID = (trial + codewordBase) % segmentsCount;
                    int codewordID = codewordBase + set * segmentsCount - segmentsCount;
                    if (codewordID >= numberOfCodewords - segmentsCount) continue block8;
                    if (codeword[codewordID].decoded != 0 || segment[segmentID].len <= 0) continue;
                    if (codeword[codewordID].bits.len != 0) {
                        segment[segmentID].concatBits(codeword[codewordID].bits);
                    }
                    int n = segment[segmentID].len;
                }
            }
            for (int i = 0; i < segmentsCount; ++i) {
                segment[i].rewindReverse();
            }
        }
    }

    private static class Codeword {
        int cb;
        int decoded;
        int sp_offset;
        BitsBuffer bits;

        private Codeword() {
        }

        private void fill(int sp, int cb) {
            this.sp_offset = sp;
            this.cb = cb;
            this.decoded = 0;
            this.bits = new BitsBuffer();
        }
    }
}

