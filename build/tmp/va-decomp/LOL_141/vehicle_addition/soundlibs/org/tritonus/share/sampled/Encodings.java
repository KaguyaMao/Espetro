/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.StringHashedSet;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class Encodings
extends AudioFormat.Encoding {
    private static StringHashedSet<AudioFormat.Encoding> encodings = new StringHashedSet();

    Encodings(String name) {
        super(name);
    }

    public static AudioFormat.Encoding getEncoding(String name) {
        AudioFormat.Encoding res = encodings.get(name);
        if (res == null) {
            res = new Encodings(name);
            encodings.add(res);
        }
        return res;
    }

    public static boolean equals(AudioFormat.Encoding e1, AudioFormat.Encoding e2) {
        return e2.toString().equals(e1.toString());
    }

    public static AudioFormat.Encoding[] getEncodings() {
        StringHashedSet<AudioFormat.Encoding> iteratedSources = new StringHashedSet<AudioFormat.Encoding>();
        StringHashedSet<AudioFormat.Encoding> retrievedTargets = new StringHashedSet<AudioFormat.Encoding>();
        for (AudioFormat.Encoding source : encodings) {
            Encodings.iterateEncodings(source, iteratedSources, retrievedTargets);
        }
        return retrievedTargets.toArray(new AudioFormat.Encoding[retrievedTargets.size()]);
    }

    private static void iterateEncodings(AudioFormat.Encoding source, StringHashedSet<AudioFormat.Encoding> iteratedSources, StringHashedSet<AudioFormat.Encoding> retrievedTargets) {
        if (!iteratedSources.contains(source)) {
            iteratedSources.add(source);
            AudioFormat.Encoding[] targets = AudioSystem.getTargetEncodings(source);
            for (int i = 0; i < targets.length; ++i) {
                AudioFormat.Encoding target = targets[i];
                if (!retrievedTargets.add(target)) continue;
                Encodings.iterateEncodings(target, iteratedSources, retrievedTargets);
            }
        }
    }

    static {
        encodings.add(AudioFormat.Encoding.PCM_SIGNED);
        encodings.add(AudioFormat.Encoding.PCM_UNSIGNED);
        encodings.add(AudioFormat.Encoding.ULAW);
        encodings.add(AudioFormat.Encoding.ALAW);
    }
}

