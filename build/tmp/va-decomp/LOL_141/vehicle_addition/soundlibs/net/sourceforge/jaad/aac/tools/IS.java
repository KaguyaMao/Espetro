/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.huffman.HCB;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.CPE;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICSInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.syntax.ICStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.tools.ISScaleTable;

public final class IS
implements ISScaleTable,
HCB {
    private IS() {
    }

    public static void process(CPE cpe, float[] specL, float[] specR) {
        ICStream ics = cpe.getRightChannel();
        ICSInfo info = ics.getInfo();
        int[] offsets = info.getSWBOffsets();
        int windowGroups = info.getWindowGroupCount();
        int maxSFB = info.getMaxSFB();
        int[] sfbCB = ics.getSfbCB();
        int[] sectEnd = ics.getSectEnd();
        float[] scaleFactors = ics.getScaleFactors();
        int idx = 0;
        int groupOff = 0;
        for (int g = 0; g < windowGroups; ++g) {
            int i = 0;
            while (i < maxSFB) {
                int end;
                if (sfbCB[idx] == 15 || sfbCB[idx] == 14) {
                    end = sectEnd[idx];
                    while (i < end) {
                        int c;
                        int n = c = sfbCB[idx] == 15 ? 1 : -1;
                        if (cpe.isMSMaskPresent()) {
                            c *= cpe.isMSUsed(idx) ? -1 : 1;
                        }
                        float scale = (float)c * scaleFactors[idx];
                        for (int w = 0; w < info.getWindowGroupLength(g); ++w) {
                            int off = groupOff + w * 128 + offsets[i];
                            for (int j = 0; j < offsets[i + 1] - offsets[i]; ++j) {
                                specR[off + j] = specL[off + j] * scale;
                            }
                        }
                        ++i;
                        ++idx;
                    }
                    continue;
                }
                end = sectEnd[idx];
                idx += end - i;
                i = end;
            }
            groupOff += info.getWindowGroupLength(g) * 128;
        }
    }
}

