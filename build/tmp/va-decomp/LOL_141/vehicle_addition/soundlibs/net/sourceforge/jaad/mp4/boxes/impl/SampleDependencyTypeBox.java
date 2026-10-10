/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox;
import java.io.IOException;

public class SampleDependencyTypeBox
extends FullBox {
    private int[] sampleDependsOn;
    private int[] sampleIsDependedOn;
    private int[] sampleHasRedundancy;

    public SampleDependencyTypeBox() {
        super("Sample Dependency Type Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        long sampleCount = -1L;
        if (this.parent.hasChild(1937011578L)) {
            sampleCount = ((SampleSizeBox)this.parent.getChild(1937011578L)).getSampleCount();
        }
        this.sampleHasRedundancy = new int[(int)sampleCount];
        this.sampleIsDependedOn = new int[(int)sampleCount];
        this.sampleDependsOn = new int[(int)sampleCount];
        int i = 0;
        while ((long)i < sampleCount) {
            byte b = (byte)in.readByte();
            this.sampleHasRedundancy[i] = b & 3;
            this.sampleIsDependedOn[i] = b >> 2 & 3;
            this.sampleDependsOn[i] = b >> 4 & 3;
            ++i;
        }
    }

    public int[] getSampleDependsOn() {
        return this.sampleDependsOn;
    }

    public int[] getSampleIsDependedOn() {
        return this.sampleIsDependedOn;
    }

    public int[] getSampleHasRedundancy() {
        return this.sampleHasRedundancy;
    }
}

