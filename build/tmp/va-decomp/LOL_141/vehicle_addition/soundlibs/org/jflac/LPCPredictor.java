/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

public class LPCPredictor {
    public static void restoreSignal(int[] residual, int dataLen, int[] qlpCoeff, int order, int lpQuantization, int[] data, int startAt) {
        for (int i = 0; i < dataLen; ++i) {
            int sum = 0;
            for (int j = 0; j < order; ++j) {
                sum += qlpCoeff[j] * data[startAt + i - j - 1];
            }
            data[startAt + i] = residual[i] + (sum >> lpQuantization);
        }
    }

    public static void restoreSignalWide(int[] residual, int dataLen, int[] qlpCoeff, int order, int lpQuantization, int[] data, int startAt) {
        for (int i = 0; i < dataLen; ++i) {
            long sum = 0L;
            for (int j = 0; j < order; ++j) {
                sum += (long)qlpCoeff[j] * (long)data[startAt + i - j - 1];
            }
            data[startAt + i] = residual[i] + (int)(sum >> lpQuantization);
        }
    }
}

