/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

public class FixedPredictor {
    private static final double M_LN2 = 0.6931471805599453;

    public static void computeResidual(int[] data, int dataLen, int order, int[] residual) {
        int idataLen = dataLen;
        switch (order) {
            case 0: {
                for (int i = 0; i < idataLen; ++i) {
                    residual[i] = data[i];
                }
                break;
            }
            case 1: {
                for (int i = 0; i < idataLen; ++i) {
                    residual[i] = data[i] - data[i - 1];
                }
                break;
            }
            case 2: {
                for (int i = 0; i < idataLen; ++i) {
                    residual[i] = data[i] - (data[i - 1] << 1) + data[i - 2];
                }
                break;
            }
            case 3: {
                for (int i = 0; i < idataLen; ++i) {
                    residual[i] = data[i] - ((data[i - 1] - data[i - 2] << 1) + (data[i - 1] - data[i - 2])) - data[i - 3];
                }
                break;
            }
            case 4: {
                for (int i = 0; i < idataLen; ++i) {
                    residual[i] = data[i] - (data[i - 1] + data[i - 3] << 2) + ((data[i - 2] << 2) + (data[i - 2] << 1)) + data[i - 4];
                }
                break;
            }
        }
    }

    public static void restoreSignal(int[] residual, int dataLen, int order, int[] data, int startAt) {
        int idataLen = dataLen;
        switch (order) {
            case 0: {
                for (int i = 0; i < idataLen; ++i) {
                    data[i + startAt] = residual[i];
                }
                break;
            }
            case 1: {
                for (int i = 0; i < idataLen; ++i) {
                    data[i + startAt] = residual[i] + data[i + startAt - 1];
                }
                break;
            }
            case 2: {
                for (int i = 0; i < idataLen; ++i) {
                    data[i + startAt] = residual[i] + (data[i + startAt - 1] << 1) - data[i + startAt - 2];
                }
                break;
            }
            case 3: {
                for (int i = 0; i < idataLen; ++i) {
                    data[i + startAt] = residual[i] + ((data[i + startAt - 1] - data[i + startAt - 2] << 1) + (data[i + startAt - 1] - data[i + startAt - 2])) + data[i + startAt - 3];
                }
                break;
            }
            case 4: {
                for (int i = 0; i < idataLen; ++i) {
                    data[i + startAt] = residual[i] + (data[i + startAt - 1] + data[i + startAt - 3] << 2) - ((data[i + startAt - 2] << 2) + (data[i + startAt - 2] << 1)) - data[i + startAt - 4];
                }
                break;
            }
        }
    }
}

