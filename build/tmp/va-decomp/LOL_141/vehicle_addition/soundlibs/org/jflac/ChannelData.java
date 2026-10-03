/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyPartitionedRiceContents;

public class ChannelData {
    private int[] output;
    private int[] residual;
    private EntropyPartitionedRiceContents partitionedRiceContents;

    public ChannelData(int size) {
        this.output = new int[size];
        this.residual = new int[size];
        this.partitionedRiceContents = new EntropyPartitionedRiceContents();
    }

    public int[] getOutput() {
        return this.output;
    }

    public EntropyPartitionedRiceContents getPartitionedRiceContents() {
        return this.partitionedRiceContents;
    }

    public int[] getResidual() {
        return this.residual;
    }
}

