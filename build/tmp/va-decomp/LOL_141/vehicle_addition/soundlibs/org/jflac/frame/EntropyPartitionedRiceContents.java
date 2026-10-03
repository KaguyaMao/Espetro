/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

public class EntropyPartitionedRiceContents {
    protected int[] parameters;
    protected int[] rawBits;
    protected int capacityByOrder = 0;

    public void ensureSize(int maxPartitionOrder) {
        if (this.capacityByOrder >= maxPartitionOrder) {
            return;
        }
        this.parameters = new int[1 << maxPartitionOrder];
        this.rawBits = new int[1 << maxPartitionOrder];
        this.capacityByOrder = maxPartitionOrder;
    }
}

