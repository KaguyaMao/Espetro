/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.EAC3SpecificBox;
import java.util.ArrayList;

public class EAC3DecoderInfo
extends DecoderInfo {
    private EAC3SpecificBox box;
    private IndependentSubstream[] is;

    public EAC3DecoderInfo(CodecSpecificBox box) {
        this.box = (EAC3SpecificBox)box;
        this.is = new IndependentSubstream[this.box.getIndependentSubstreamCount()];
        for (int i = 0; i < this.is.length; ++i) {
            this.is[i] = new IndependentSubstream(i);
        }
    }

    public int getDataRate() {
        return this.box.getDataRate();
    }

    public IndependentSubstream[] getIndependentSubstreams() {
        return this.is;
    }

    public class IndependentSubstream {
        private final int index;
        private final DependentSubstream[] dependentSubstreams;

        private IndependentSubstream(int index) {
            this.index = index;
            int loc = EAC3DecoderInfo.this.box.getDependentSubstreamLocation()[index];
            ArrayList<DependentSubstream> list = new ArrayList<DependentSubstream>();
            for (int i = 0; i < 9; ++i) {
                if ((loc >> 8 - i & 1) != 1) continue;
                list.add(DependentSubstream.values()[i]);
            }
            this.dependentSubstreams = list.toArray(new DependentSubstream[list.size()]);
        }

        public int getFscod() {
            return EAC3DecoderInfo.this.box.getFscods()[this.index];
        }

        public int getBsid() {
            return EAC3DecoderInfo.this.box.getBsids()[this.index];
        }

        public int getBsmod() {
            return EAC3DecoderInfo.this.box.getBsmods()[this.index];
        }

        public int getAcmod() {
            return EAC3DecoderInfo.this.box.getAcmods()[this.index];
        }

        public boolean isLfeon() {
            return EAC3DecoderInfo.this.box.getLfeons()[this.index];
        }

        public DependentSubstream[] getDependentSubstreams() {
            return this.dependentSubstreams;
        }
    }

    public static enum DependentSubstream {
        LC_RC_PAIR,
        LRS_RRS_PAIR,
        CS,
        TS,
        LSD_RSD_PAIR,
        LW_RW_PAIR,
        LVH_RVH_PAIR,
        CVH,
        LFE2;

    }
}

