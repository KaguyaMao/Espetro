/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.huffman.HCB;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CPE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;

public final class MS
implements HCB {
    private MS() {
    }

    public static void process(CPE cpe, float[] specL, float[] specR) {
        ICStream ics = cpe.getLeftChannel();
        ICSInfo info = ics.getInfo();
        int[] offsets = info.getSWBOffsets();
        int windowGroups = info.getWindowGroupCount();
        int maxSFB = info.getMaxSFB();
        int[] sfbCBl = ics.getSfbCB();
        int[] sfbCBr = cpe.getRightChannel().getSfbCB();
        int idx = 0;
        int groupOff = 0;
        for (int g = 0; g < windowGroups; ++g) {
            int i = 0;
            while (i < maxSFB) {
                if (cpe.isMSUsed(idx) && sfbCBl[idx] < 13 && sfbCBr[idx] < 13) {
                    for (int w = 0; w < info.getWindowGroupLength(g); ++w) {
                        int off = groupOff + w * 128 + offsets[i];
                        for (int j = 0; j < offsets[i + 1] - offsets[i]; ++j) {
                            float t = specL[off + j] - specR[off + j];
                            int n = off + j;
                            specL[n] = specL[n] + specR[off + j];
                            specR[off + j] = t;
                        }
                    }
                }
                ++i;
                ++idx;
            }
            groupOff += info.getWindowGroupLength(g) * 128;
        }
    }
}

