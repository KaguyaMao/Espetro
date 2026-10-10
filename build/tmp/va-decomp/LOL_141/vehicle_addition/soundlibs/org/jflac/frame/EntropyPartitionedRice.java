/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.frame;

import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyCodingMethod;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.EntropyPartitionedRiceContents;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Header;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import java.io.IOException;

public class EntropyPartitionedRice
extends EntropyCodingMethod {
    private static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE_PARAMETER_LEN = 4;
    private static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE2_PARAMETER_LEN = 5;
    private static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE_RAW_LEN = 5;
    private static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE_ESCAPE_PARAMETER = 15;
    private static final int ENTROPY_CODING_METHOD_PARTITIONED_RICE2_ESCAPE_PARAMETER = 31;
    protected int order;
    protected EntropyPartitionedRiceContents contents;

    void readResidual(BitInputStream is, int predictorOrder, int partitionOrder, Header header, int[] residual, boolean isExtended) throws IOException {
        int partitions = 1 << partitionOrder;
        int partitionSamples = partitionOrder > 0 ? header.blockSize >> partitionOrder : header.blockSize - predictorOrder;
        int pLen = isExtended ? 5 : 4;
        int pEsc = isExtended ? 31 : 15;
        this.contents.ensureSize(Math.max(6, partitionOrder));
        this.contents.parameters = new int[partitions];
        int sample = 0;
        for (int partition = 0; partition < partitions; ++partition) {
            int u;
            int riceParameter;
            this.contents.parameters[partition] = riceParameter = is.readRawUInt(pLen);
            if (riceParameter < pEsc) {
                this.contents.rawBits[partition] = 0;
                u = partitionOrder == 0 || partition > 0 ? partitionSamples : partitionSamples - predictorOrder;
                is.readRiceSignedBlock(residual, sample, u, riceParameter);
                sample += u;
                continue;
            }
            System.out.println("readResidual elsecase, pEsc = " + pEsc);
            this.contents.rawBits[partition] = riceParameter = is.readRawUInt(5);
            int n = u = partitionOrder == 0 || partition > 0 ? 0 : predictorOrder;
            while (u < partitionSamples) {
                residual[sample] = is.readRawInt(riceParameter);
                ++u;
                ++sample;
            }
        }
    }
}

