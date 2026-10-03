/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox;
import java.io.IOException;

public class SampleDependencyBox
extends FullBox {
    private int[] dependencyCount;
    private int[][] relativeSampleNumber;

    public SampleDependencyBox() {
        super("Sample Dependency Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int sampleCount = ((SampleSizeBox)this.parent.getChild(1937011578L)).getSampleCount();
        for (int i = 0; i < sampleCount; ++i) {
            this.dependencyCount[i] = (int)in.readBytes(2);
            for (int j = 0; j < this.dependencyCount[i]; ++j) {
                this.relativeSampleNumber[i][j] = (int)in.readBytes(2);
            }
        }
    }

    public int[] getDependencyCount() {
        return this.dependencyCount;
    }

    public int[][] getRelativeSampleNumber() {
        return this.relativeSampleNumber;
    }
}

