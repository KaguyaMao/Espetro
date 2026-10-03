/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.StringHashedSet;
import javax.sound.sampled.AudioFileFormat;

public class AudioFileTypes
extends AudioFileFormat.Type {
    private static StringHashedSet<AudioFileFormat.Type> types = new StringHashedSet();

    AudioFileTypes(String name, String ext) {
        super(name, ext);
    }

    public static AudioFileFormat.Type getType(String name) {
        return AudioFileTypes.getType(name, null);
    }

    public static AudioFileFormat.Type getType(String name, String extension) {
        AudioFileFormat.Type res = types.get(name);
        if (res == null) {
            if (extension == null) {
                return null;
            }
            res = new AudioFileTypes(name, extension);
            types.add(res);
        }
        return res;
    }

    public static boolean equals(AudioFileFormat.Type t1, AudioFileFormat.Type t2) {
        return t2.toString().equals(t1.toString());
    }

    static {
        types.add(AudioFileFormat.Type.AIFF);
        types.add(AudioFileFormat.Type.AIFC);
        types.add(AudioFileFormat.Type.AU);
        types.add(AudioFileFormat.Type.SND);
        types.add(AudioFileFormat.Type.WAVE);
    }
}

