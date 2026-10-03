/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

import javax.sound.sampled.AudioFormat;

public class AudioFormats {
    private static boolean doMatch(int i1, int i2) {
        return i1 == -1 || i2 == -1 || i1 == i2;
    }

    private static boolean doMatch(float f1, float f2) {
        return f1 == -1.0f || f2 == -1.0f || (double)Math.abs(f1 - f2) < 1.0E-9;
    }

    public static boolean matches(AudioFormat format1, AudioFormat format2) {
        return format1.getEncoding().equals(format2.getEncoding()) && (format2.getSampleSizeInBits() <= 8 || format1.getSampleSizeInBits() == -1 || format2.getSampleSizeInBits() == -1 || format1.isBigEndian() == format2.isBigEndian()) && AudioFormats.doMatch(format1.getChannels(), format2.getChannels()) && AudioFormats.doMatch(format1.getSampleSizeInBits(), format2.getSampleSizeInBits()) && AudioFormats.doMatch(format1.getFrameSize(), format2.getFrameSize()) && AudioFormats.doMatch(format1.getSampleRate(), format2.getSampleRate()) && AudioFormats.doMatch(format1.getFrameRate(), format2.getFrameRate());
    }

    public static boolean equals(AudioFormat format1, AudioFormat format2) {
        return format1.getEncoding().equals(format2.getEncoding()) && format1.getChannels() == format2.getChannels() && format1.getSampleSizeInBits() == format2.getSampleSizeInBits() && format1.getFrameSize() == format2.getFrameSize() && (double)Math.abs(format1.getSampleRate() - format2.getSampleRate()) < 1.0E-9 && (double)Math.abs(format1.getFrameRate() - format2.getFrameRate()) < 1.0E-9;
    }
}

