/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

public class TConversionTool {
    private static final boolean ZEROTRAP = true;
    private static final short BIAS = 132;
    private static final int CLIP = 32635;
    private static final int[] exp_lut1 = new int[]{0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7};
    private static short[] u2l = new short[]{-32124, -31100, -30076, -29052, -28028, -27004, -25980, -24956, -23932, -22908, -21884, -20860, -19836, -18812, -17788, -16764, -15996, -15484, -14972, -14460, -13948, -13436, -12924, -12412, -11900, -11388, -10876, -10364, -9852, -9340, -8828, -8316, -7932, -7676, -7420, -7164, -6908, -6652, -6396, -6140, -5884, -5628, -5372, -5116, -4860, -4604, -4348, -4092, -3900, -3772, -3644, -3516, -3388, -3260, -3132, -3004, -2876, -2748, -2620, -2492, -2364, -2236, -2108, -1980, -1884, -1820, -1756, -1692, -1628, -1564, -1500, -1436, -1372, -1308, -1244, -1180, -1116, -1052, -988, -924, -876, -844, -812, -780, -748, -716, -684, -652, -620, -588, -556, -524, -492, -460, -428, -396, -372, -356, -340, -324, -308, -292, -276, -260, -244, -228, -212, -196, -180, -164, -148, -132, -120, -112, -104, -96, -88, -80, -72, -64, -56, -48, -40, -32, -24, -16, -8, 0, 32124, 31100, 30076, 29052, 28028, 27004, 25980, 24956, 23932, 22908, 21884, 20860, 19836, 18812, 17788, 16764, 15996, 15484, 14972, 14460, 13948, 13436, 12924, 12412, 11900, 11388, 10876, 10364, 9852, 9340, 8828, 8316, 7932, 7676, 7420, 7164, 6908, 6652, 6396, 6140, 5884, 5628, 5372, 5116, 4860, 4604, 4348, 4092, 3900, 3772, 3644, 3516, 3388, 3260, 3132, 3004, 2876, 2748, 2620, 2492, 2364, 2236, 2108, 1980, 1884, 1820, 1756, 1692, 1628, 1564, 1500, 1436, 1372, 1308, 1244, 1180, 1116, 1052, 988, 924, 876, 844, 812, 780, 748, 716, 684, 652, 620, 588, 556, 524, 492, 460, 428, 396, 372, 356, 340, 324, 308, 292, 276, 260, 244, 228, 212, 196, 180, 164, 148, 132, 120, 112, 104, 96, 88, 80, 72, 64, 56, 48, 40, 32, 24, 16, 8, 0};
    private static final byte QUANT_MASK = 15;
    private static final byte SEG_SHIFT = 4;
    private static final short[] seg_end = new short[]{255, 511, 1023, 2047, 4095, 8191, 16383, Short.MAX_VALUE};
    private static short[] a2l = new short[]{-5504, -5248, -6016, -5760, -4480, -4224, -4992, -4736, -7552, -7296, -8064, -7808, -6528, -6272, -7040, -6784, -2752, -2624, -3008, -2880, -2240, -2112, -2496, -2368, -3776, -3648, -4032, -3904, -3264, -3136, -3520, -3392, -22016, -20992, -24064, -23040, -17920, -16896, -19968, -18944, -30208, -29184, -32256, -31232, -26112, -25088, -28160, -27136, -11008, -10496, -12032, -11520, -8960, -8448, -9984, -9472, -15104, -14592, -16128, -15616, -13056, -12544, -14080, -13568, -344, -328, -376, -360, -280, -264, -312, -296, -472, -456, -504, -488, -408, -392, -440, -424, -88, -72, -120, -104, -24, -8, -56, -40, -216, -200, -248, -232, -152, -136, -184, -168, -1376, -1312, -1504, -1440, -1120, -1056, -1248, -1184, -1888, -1824, -2016, -1952, -1632, -1568, -1760, -1696, -688, -656, -752, -720, -560, -528, -624, -592, -944, -912, -1008, -976, -816, -784, -880, -848, 5504, 5248, 6016, 5760, 4480, 4224, 4992, 4736, 7552, 7296, 8064, 7808, 6528, 6272, 7040, 6784, 2752, 2624, 3008, 2880, 2240, 2112, 2496, 2368, 3776, 3648, 4032, 3904, 3264, 3136, 3520, 3392, 22016, 20992, 24064, 23040, 17920, 16896, 19968, 18944, 30208, 29184, 32256, 31232, 26112, 25088, 28160, 27136, 11008, 10496, 12032, 11520, 8960, 8448, 9984, 9472, 15104, 14592, 16128, 15616, 13056, 12544, 14080, 13568, 344, 328, 376, 360, 280, 264, 312, 296, 472, 456, 504, 488, 408, 392, 440, 424, 88, 72, 120, 104, 24, 8, 56, 40, 216, 200, 248, 232, 152, 136, 184, 168, 1376, 1312, 1504, 1440, 1120, 1056, 1248, 1184, 1888, 1824, 2016, 1952, 1632, 1568, 1760, 1696, 688, 656, 752, 720, 560, 528, 624, 592, 944, 912, 1008, 976, 816, 784, 880, 848};
    private static byte[] u2a = new byte[]{-86, -85, -88, -87, -82, -81, -84, -83, -94, -93, -96, -95, -90, -89, -92, -91, -70, -69, -72, -71, -66, -65, -68, -67, -78, -77, -80, -79, -74, -73, -76, -75, -118, -117, -120, -119, -114, -113, -116, -115, -126, -125, -128, -127, -122, -121, -124, -123, -101, -104, -103, -98, -97, -100, -99, -110, -109, -112, -111, -106, -105, -108, -107, -22, -24, -23, -18, -17, -20, -19, -30, -29, -32, -31, -26, -25, -28, -27, -6, -8, -2, -1, -4, -3, -14, -13, -16, -15, -10, -9, -12, -11, -53, -55, -49, -51, -62, -61, -64, -63, -58, -57, -60, -59, -38, -37, -40, -39, -34, -33, -36, -35, -46, -46, -45, -45, -48, -48, -47, -47, -42, -42, -41, -41, -44, -44, -43, -43, 42, 43, 40, 41, 46, 47, 44, 45, 34, 35, 32, 33, 38, 39, 36, 37, 58, 59, 56, 57, 62, 63, 60, 61, 50, 51, 48, 49, 54, 55, 52, 53, 10, 11, 8, 9, 14, 15, 12, 13, 2, 3, 0, 1, 6, 7, 4, 5, 27, 24, 25, 30, 31, 28, 29, 18, 19, 16, 17, 22, 23, 20, 21, 106, 104, 105, 110, 111, 108, 109, 98, 99, 96, 97, 102, 103, 100, 101, 122, 120, 126, 127, 124, 125, 114, 115, 112, 113, 118, 119, 116, 117, 75, 73, 79, 77, 66, 67, 64, 65, 70, 71, 68, 69, 90, 91, 88, 89, 94, 95, 92, 93, 82, 82, 83, 83, 80, 80, 81, 81, 86, 86, 87, 87, 84, 84, 85, 85};
    private static byte[] a2u = new byte[]{-86, -85, -88, -87, -82, -81, -84, -83, -94, -93, -96, -95, -90, -89, -92, -91, -71, -70, -73, -72, -67, -66, -69, -68, -79, -78, -80, -80, -75, -74, -77, -76, -118, -117, -120, -119, -114, -113, -116, -115, -126, -125, -128, -127, -122, -121, -124, -123, -102, -101, -104, -103, -98, -97, -100, -99, -110, -109, -112, -111, -106, -105, -108, -107, -30, -29, -32, -31, -26, -25, -28, -27, -35, -35, -36, -36, -33, -33, -34, -34, -12, -10, -16, -14, -4, -2, -8, -6, -22, -21, -24, -23, -18, -17, -20, -19, -56, -55, -58, -57, -52, -51, -54, -53, -64, -63, -65, -65, -60, -59, -62, -61, -42, -41, -44, -43, -38, -37, -40, -39, -49, -49, -50, -50, -46, -45, -48, -47, 42, 43, 40, 41, 46, 47, 44, 45, 34, 35, 32, 33, 38, 39, 36, 37, 57, 58, 55, 56, 61, 62, 59, 60, 49, 50, 48, 48, 53, 54, 51, 52, 10, 11, 8, 9, 14, 15, 12, 13, 2, 3, 0, 1, 6, 7, 4, 5, 26, 27, 24, 25, 30, 31, 28, 29, 18, 19, 16, 17, 22, 23, 20, 21, 98, 99, 96, 97, 102, 103, 100, 101, 93, 93, 92, 92, 95, 95, 94, 94, 116, 118, 112, 114, 124, 126, 120, 122, 106, 107, 104, 105, 110, 111, 108, 109, 72, 73, 70, 71, 76, 77, 74, 75, 64, 65, 63, 63, 68, 69, 66, 67, 86, 87, 84, 85, 90, 91, 88, 89, 79, 79, 78, 78, 82, 83, 80, 81};

    public static void convertSign8(byte[] buffer, int byteOffset, int sampleCount) {
        sampleCount += byteOffset;
        int i = byteOffset;
        while (i < sampleCount) {
            int n = i++;
            buffer[n] = (byte)(buffer[n] + 128);
        }
    }

    public static void swapOrder16(byte[] buffer, int byteOffset, int sampleCount) {
        int byteMax = sampleCount * 2 + byteOffset - 1;
        int i = byteOffset;
        while (i < byteMax) {
            byte h = buffer[i];
            buffer[i++] = buffer[i];
            buffer[i++] = h;
        }
    }

    public static void swapOrder24(byte[] buffer, int byteOffset, int sampleCount) {
        int byteMax = sampleCount * 3 + byteOffset - 2;
        for (int i = byteOffset; i < byteMax; ++i) {
            byte h = buffer[i];
            buffer[i++] = buffer[i + 1];
            buffer[++i] = h;
        }
    }

    public static void swapOrder32(byte[] buffer, int byteOffset, int sampleCount) {
        int byteMax = sampleCount * 4 + byteOffset - 3;
        for (int i = byteOffset; i < byteMax; ++i) {
            byte h = buffer[i];
            buffer[i] = buffer[i + 3];
            buffer[i + 3] = h;
            h = buffer[++i];
            buffer[i++] = buffer[i];
            buffer[i++] = h;
        }
    }

    public static void convertSign8(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
        while (sampleCount > 0) {
            outBuffer[outByteOffset++] = (byte)(inBuffer[inByteOffset++] + 128);
            --sampleCount;
        }
    }

    public static void swapOrder16(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
        while (sampleCount > 0) {
            outBuffer[outByteOffset++] = inBuffer[inByteOffset + 1];
            outBuffer[outByteOffset++] = inBuffer[inByteOffset++];
            ++inByteOffset;
            --sampleCount;
        }
    }

    public static void swapOrder24(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
        while (sampleCount > 0) {
            outBuffer[outByteOffset++] = inBuffer[inByteOffset + 2];
            int n = ++outByteOffset;
            ++outByteOffset;
            outBuffer[n] = inBuffer[inByteOffset++];
            ++inByteOffset;
            ++inByteOffset;
            --sampleCount;
        }
    }

    public static void swapOrder32(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
        while (sampleCount > 0) {
            outBuffer[outByteOffset++] = inBuffer[inByteOffset + 3];
            outBuffer[outByteOffset++] = inBuffer[inByteOffset + 2];
            outBuffer[outByteOffset++] = inBuffer[inByteOffset + 1];
            outBuffer[outByteOffset++] = inBuffer[inByteOffset++];
            ++inByteOffset;
            ++inByteOffset;
            ++inByteOffset;
            --sampleCount;
        }
    }

    public static short bytesToShort16(byte highByte, byte lowByte) {
        return (short)(highByte << 8 | lowByte & 0xFF);
    }

    public static short bytesToShort16(byte[] buffer, int byteOffset, boolean bigEndian) {
        return bigEndian ? (short)(buffer[byteOffset] << 8 | buffer[byteOffset + 1] & 0xFF) : (short)(buffer[byteOffset + 1] << 8 | buffer[byteOffset] & 0xFF);
    }

    public static int bytesToInt16(byte highByte, byte lowByte) {
        return highByte << 8 | lowByte & 0xFF;
    }

    public static int bytesToInt16(byte[] buffer, int byteOffset, boolean bigEndian) {
        return bigEndian ? buffer[byteOffset] << 8 | buffer[byteOffset + 1] & 0xFF : buffer[byteOffset + 1] << 8 | buffer[byteOffset] & 0xFF;
    }

    public static int bytesToInt24(byte[] buffer, int byteOffset, boolean bigEndian) {
        return bigEndian ? buffer[byteOffset] << 16 | (buffer[byteOffset + 1] & 0xFF) << 8 | buffer[byteOffset + 2] & 0xFF : buffer[byteOffset + 2] << 16 | (buffer[byteOffset + 1] & 0xFF) << 8 | buffer[byteOffset] & 0xFF;
    }

    public static int bytesToInt32(byte[] buffer, int byteOffset, boolean bigEndian) {
        return bigEndian ? buffer[byteOffset] << 24 | (buffer[byteOffset + 1] & 0xFF) << 16 | (buffer[byteOffset + 2] & 0xFF) << 8 | buffer[byteOffset + 3] & 0xFF : buffer[byteOffset + 3] << 24 | (buffer[byteOffset + 2] & 0xFF) << 16 | (buffer[byteOffset + 1] & 0xFF) << 8 | buffer[byteOffset] & 0xFF;
    }

    public static void shortToBytes16(short sample, byte[] buffer, int byteOffset, boolean bigEndian) {
        TConversionTool.intToBytes16(sample, buffer, byteOffset, bigEndian);
    }

    public static void intToBytes16(int sample, byte[] buffer, int byteOffset, boolean bigEndian) {
        if (bigEndian) {
            buffer[byteOffset++] = (byte)(sample >> 8);
            buffer[byteOffset] = (byte)(sample & 0xFF);
        } else {
            buffer[byteOffset++] = (byte)(sample & 0xFF);
            buffer[byteOffset] = (byte)(sample >> 8);
        }
    }

    public static void intToBytes24(int sample, byte[] buffer, int byteOffset, boolean bigEndian) {
        if (bigEndian) {
            buffer[byteOffset++] = (byte)(sample >> 16);
            buffer[byteOffset++] = (byte)(sample >>> 8 & 0xFF);
            buffer[byteOffset] = (byte)(sample & 0xFF);
        } else {
            buffer[byteOffset++] = (byte)(sample & 0xFF);
            buffer[byteOffset++] = (byte)(sample >>> 8 & 0xFF);
            buffer[byteOffset] = (byte)(sample >> 16);
        }
    }

    public static void intToBytes32(int sample, byte[] buffer, int byteOffset, boolean bigEndian) {
        if (bigEndian) {
            buffer[byteOffset++] = (byte)(sample >> 24);
            buffer[byteOffset++] = (byte)(sample >>> 16 & 0xFF);
            buffer[byteOffset++] = (byte)(sample >>> 8 & 0xFF);
            buffer[byteOffset] = (byte)(sample & 0xFF);
        } else {
            buffer[byteOffset++] = (byte)(sample & 0xFF);
            buffer[byteOffset++] = (byte)(sample >>> 8 & 0xFF);
            buffer[byteOffset++] = (byte)(sample >>> 16 & 0xFF);
            buffer[byteOffset] = (byte)(sample >> 24);
        }
    }

    public static byte linear2ulaw(int sample) {
        int mantissa;
        int exponent;
        int ulawbyte;
        if (sample > Short.MAX_VALUE) {
            sample = Short.MAX_VALUE;
        } else if (sample < Short.MIN_VALUE) {
            sample = Short.MIN_VALUE;
        }
        int sign = sample >> 8 & 0x80;
        if (sign != 0) {
            sample = -sample;
        }
        if (sample > 32635) {
            sample = 32635;
        }
        if ((ulawbyte = ~(sign | (exponent = exp_lut1[(sample += 132) >> 7 & 0xFF]) << 4 | (mantissa = sample >> exponent + 3 & 0xF))) == 0) {
            ulawbyte = 2;
        }
        return (byte)ulawbyte;
    }

    public static short ulaw2linear(byte ulawbyte) {
        return u2l[ulawbyte & 0xFF];
    }

    public static void pcm162ulaw(byte[] buffer, int byteOffset, int sampleCount, boolean bigEndian) {
        int shortIndex;
        int ulawIndex = shortIndex = byteOffset;
        if (bigEndian) {
            while (sampleCount > 0) {
                buffer[ulawIndex++] = TConversionTool.linear2ulaw(TConversionTool.bytesToInt16(buffer[shortIndex], buffer[shortIndex + 1]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                buffer[ulawIndex++] = TConversionTool.linear2ulaw(TConversionTool.bytesToInt16(buffer[shortIndex + 1], buffer[shortIndex]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        }
    }

    public static void pcm162ulaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
        int shortIndex = inByteOffset;
        int ulawIndex = outByteOffset;
        if (bigEndian) {
            while (sampleCount > 0) {
                outBuffer[ulawIndex++] = TConversionTool.linear2ulaw(TConversionTool.bytesToInt16(inBuffer[shortIndex], inBuffer[shortIndex + 1]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                outBuffer[ulawIndex++] = TConversionTool.linear2ulaw(TConversionTool.bytesToInt16(inBuffer[shortIndex + 1], inBuffer[shortIndex]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        }
    }

    public static void pcm82ulaw(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
        sampleCount += byteOffset;
        if (signed) {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = TConversionTool.linear2ulaw(buffer[i] << 8);
            }
        } else {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = TConversionTool.linear2ulaw((byte)(buffer[i] + 128) << 8);
            }
        }
    }

    public static void pcm82ulaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
        int ulawIndex = outByteOffset;
        int pcmIndex = inByteOffset;
        if (signed) {
            while (sampleCount > 0) {
                outBuffer[ulawIndex++] = TConversionTool.linear2ulaw(inBuffer[pcmIndex++] << 8);
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                outBuffer[ulawIndex++] = TConversionTool.linear2ulaw((byte)(inBuffer[pcmIndex++] + 128) << 8);
                --sampleCount;
            }
        }
    }

    public static void ulaw2pcm16(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
        int shortIndex = outByteOffset;
        int ulawIndex = inByteOffset;
        while (sampleCount > 0) {
            TConversionTool.intToBytes16(u2l[inBuffer[ulawIndex++] & 0xFF], outBuffer, shortIndex++, bigEndian);
            ++shortIndex;
            --sampleCount;
        }
    }

    public static void ulaw2pcm8(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
        sampleCount += byteOffset;
        if (signed) {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = (byte)(u2l[buffer[i] & 0xFF] >> 8 & 0xFF);
            }
        } else {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = (byte)((u2l[buffer[i] & 0xFF] >> 8) + 128);
            }
        }
    }

    public static void ulaw2pcm8(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
        int ulawIndex = inByteOffset;
        int pcmIndex = outByteOffset;
        if (signed) {
            while (sampleCount > 0) {
                outBuffer[pcmIndex++] = (byte)(u2l[inBuffer[ulawIndex++] & 0xFF] >> 8 & 0xFF);
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                outBuffer[pcmIndex++] = (byte)((u2l[inBuffer[ulawIndex++] & 0xFF] >> 8) + 128);
                --sampleCount;
            }
        }
    }

    public static byte linear2alaw(short pcm_val) {
        int mask;
        int seg = 8;
        if (pcm_val >= 0) {
            mask = -43;
        } else {
            mask = 85;
            pcm_val = (short)(-pcm_val - 8);
        }
        for (int i = 0; i < 8; ++i) {
            if (pcm_val > seg_end[i]) continue;
            seg = (byte)i;
            break;
        }
        if (seg >= 8) {
            return (byte)((0x7F ^ mask) & 0xFF);
        }
        byte aval = (byte)(seg << 4);
        aval = seg < 2 ? (byte)(aval | pcm_val >> 4 & 0xF) : (byte)(aval | pcm_val >> seg + 3 & 0xF);
        return (byte)((aval ^ mask) & 0xFF);
    }

    public static short alaw2linear(byte ulawbyte) {
        return a2l[ulawbyte & 0xFF];
    }

    public static void pcm162alaw(byte[] buffer, int byteOffset, int sampleCount, boolean bigEndian) {
        int shortIndex;
        int alawIndex = shortIndex = byteOffset;
        if (bigEndian) {
            while (sampleCount > 0) {
                buffer[alawIndex++] = TConversionTool.linear2alaw(TConversionTool.bytesToShort16(buffer[shortIndex], buffer[shortIndex + 1]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                buffer[alawIndex++] = TConversionTool.linear2alaw(TConversionTool.bytesToShort16(buffer[shortIndex + 1], buffer[shortIndex]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        }
    }

    public static void pcm162alaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
        int shortIndex = inByteOffset;
        int alawIndex = outByteOffset;
        if (bigEndian) {
            while (sampleCount > 0) {
                outBuffer[alawIndex++] = TConversionTool.linear2alaw(TConversionTool.bytesToShort16(inBuffer[shortIndex], inBuffer[shortIndex + 1]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                outBuffer[alawIndex++] = TConversionTool.linear2alaw(TConversionTool.bytesToShort16(inBuffer[shortIndex + 1], inBuffer[shortIndex]));
                ++shortIndex;
                ++shortIndex;
                --sampleCount;
            }
        }
    }

    public static void pcm82alaw(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
        sampleCount += byteOffset;
        if (signed) {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = TConversionTool.linear2alaw((short)(buffer[i] << 8));
            }
        } else {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = TConversionTool.linear2alaw((short)((byte)(buffer[i] + 128) << 8));
            }
        }
    }

    public static void pcm82alaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
        int alawIndex = outByteOffset;
        int pcmIndex = inByteOffset;
        if (signed) {
            while (sampleCount > 0) {
                outBuffer[alawIndex++] = TConversionTool.linear2alaw((short)(inBuffer[pcmIndex++] << 8));
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                outBuffer[alawIndex++] = TConversionTool.linear2alaw((short)((byte)(inBuffer[pcmIndex++] + 128) << 8));
                --sampleCount;
            }
        }
    }

    public static void alaw2pcm8(byte[] buffer, int byteOffset, int sampleCount, boolean signed) {
        sampleCount += byteOffset;
        if (signed) {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = (byte)(a2l[buffer[i] & 0xFF] >> 8 & 0xFF);
            }
        } else {
            for (int i = byteOffset; i < sampleCount; ++i) {
                buffer[i] = (byte)((a2l[buffer[i] & 0xFF] >> 8) + 128);
            }
        }
    }

    public static void alaw2pcm8(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean signed) {
        int alawIndex = inByteOffset;
        int pcmIndex = outByteOffset;
        if (signed) {
            while (sampleCount > 0) {
                outBuffer[pcmIndex++] = (byte)(a2l[inBuffer[alawIndex++] & 0xFF] >> 8 & 0xFF);
                --sampleCount;
            }
        } else {
            while (sampleCount > 0) {
                outBuffer[pcmIndex++] = (byte)((a2l[inBuffer[alawIndex++] & 0xFF] >> 8) + 128);
                --sampleCount;
            }
        }
    }

    public static void alaw2pcm16(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount, boolean bigEndian) {
        int shortIndex = outByteOffset;
        int alawIndex = inByteOffset;
        while (sampleCount > 0) {
            TConversionTool.intToBytes16(a2l[inBuffer[alawIndex++] & 0xFF], outBuffer, shortIndex++, bigEndian);
            ++shortIndex;
            --sampleCount;
        }
    }

    public static byte ulaw2alaw(byte sample) {
        return u2a[sample & 0xFF];
    }

    public static void ulaw2alaw(byte[] buffer, int byteOffset, int sampleCount) {
        sampleCount += byteOffset;
        for (int i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = u2a[buffer[i] & 0xFF];
        }
    }

    public static void ulaw2alaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
        int ulawIndex = outByteOffset;
        int alawIndex = inByteOffset;
        while (sampleCount > 0) {
            outBuffer[alawIndex++] = u2a[inBuffer[ulawIndex++] & 0xFF];
            --sampleCount;
        }
    }

    public static byte alaw2ulaw(byte sample) {
        return a2u[sample & 0xFF];
    }

    public static void alaw2ulaw(byte[] buffer, int byteOffset, int sampleCount) {
        sampleCount += byteOffset;
        for (int i = byteOffset; i < sampleCount; ++i) {
            buffer[i] = a2u[buffer[i] & 0xFF];
        }
    }

    public static void alaw2ulaw(byte[] inBuffer, int inByteOffset, byte[] outBuffer, int outByteOffset, int sampleCount) {
        int ulawIndex = outByteOffset;
        int alawIndex = inByteOffset;
        while (sampleCount > 0) {
            outBuffer[ulawIndex++] = a2u[inBuffer[alawIndex++] & 0xFF];
            --sampleCount;
        }
    }

    public static void changeOrderOrSign(byte[] buffer, int nOffset, int nByteLength, int nBytesPerSample) {
        switch (nBytesPerSample) {
            case 1: {
                TConversionTool.convertSign8(buffer, nOffset, nByteLength);
                break;
            }
            case 2: {
                TConversionTool.swapOrder16(buffer, nOffset, nByteLength / 2);
                break;
            }
            case 3: {
                TConversionTool.swapOrder24(buffer, nOffset, nByteLength / 3);
                break;
            }
            case 4: {
                TConversionTool.swapOrder32(buffer, nOffset, nByteLength / 4);
            }
        }
    }

    public static void changeOrderOrSign(byte[] inBuffer, int nInOffset, byte[] outBuffer, int nOutOffset, int nByteLength, int nBytesPerSample) {
        switch (nBytesPerSample) {
            case 1: {
                TConversionTool.convertSign8(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength);
                break;
            }
            case 2: {
                TConversionTool.swapOrder16(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength / 2);
                break;
            }
            case 3: {
                TConversionTool.swapOrder24(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength / 3);
                break;
            }
            case 4: {
                TConversionTool.swapOrder32(inBuffer, nInOffset, outBuffer, nOutOffset, nByteLength / 4);
            }
        }
    }
}

