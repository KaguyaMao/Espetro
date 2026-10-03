/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.sampleentries.codec.CodecSpecificBox;
import java.io.IOException;

public class EAC3SpecificBox
extends CodecSpecificBox {
    private int dataRate;
    private int independentSubstreamCount;
    private int[] fscods;
    private int[] bsids;
    private int[] bsmods;
    private int[] acmods;
    private int[] dependentSubstreamCount;
    private int[] dependentSubstreamLocation;
    private boolean[] lfeons;

    public EAC3SpecificBox() {
        super("EAC-3 Specific Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        long l = in.readBytes(2);
        this.dataRate = (int)(l >> 3 & 0x1FFFL);
        this.independentSubstreamCount = (int)(l & 7L);
        for (int i = 0; i < this.independentSubstreamCount; ++i) {
            l = in.readBytes(3);
            this.fscods[i] = (int)(l >> 22 & 3L);
            this.bsids[i] = (int)(l >> 17 & 0x1FL);
            this.bsmods[i] = (int)(l >> 12 & 0x1FL);
            this.acmods[i] = (int)(l >> 9 & 7L);
            this.lfeons[i] = (l >> 5 & 1L) == 1L;
            this.dependentSubstreamCount[i] = (int)(l >> 1 & 0xFL);
            if (this.dependentSubstreamCount[i] <= 0) continue;
            l = l << 8 | (long)in.readByte();
            this.dependentSubstreamLocation[i] = (int)(l & 0x1FFL);
        }
    }

    public int getDataRate() {
        return this.dataRate;
    }

    public int getIndependentSubstreamCount() {
        return this.independentSubstreamCount;
    }

    public int[] getFscods() {
        return this.fscods;
    }

    public int[] getBsids() {
        return this.bsids;
    }

    public int[] getBsmods() {
        return this.bsmods;
    }

    public int[] getAcmods() {
        return this.acmods;
    }

    public boolean[] getLfeons() {
        return this.lfeons;
    }

    public int[] getDependentSubstreamCount() {
        return this.dependentSubstreamCount;
    }

    public int[] getDependentSubstreamLocation() {
        return this.dependentSubstreamLocation;
    }
}

