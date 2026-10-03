/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.ArraySet;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.AudioFormats;
import java.util.Collection;
import java.util.Iterator;
import javax.sound.sampled.AudioFormat;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AudioFormatSet
extends ArraySet<AudioFormat> {
    private static final long serialVersionUID = 1L;
    protected static final AudioFormat[] EMPTY_FORMAT_ARRAY = new AudioFormat[0];

    public AudioFormatSet() {
    }

    public AudioFormatSet(Collection<AudioFormat> c) {
        super(c);
    }

    @Override
    public boolean add(AudioFormat elem) {
        if (elem == null) {
            return false;
        }
        return super.add(elem);
    }

    public boolean contains(AudioFormat elem) {
        if (elem == null) {
            return false;
        }
        AudioFormat comp = elem;
        Iterator it = this.iterator();
        while (it.hasNext()) {
            if (!AudioFormats.equals(comp, (AudioFormat)it.next())) continue;
            return true;
        }
        return false;
    }

    public AudioFormat get(AudioFormat elem) {
        if (elem == null) {
            return null;
        }
        AudioFormat comp = elem;
        for (AudioFormat thisElem : this) {
            if (!AudioFormats.equals(comp, thisElem)) continue;
            return thisElem;
        }
        return null;
    }

    public AudioFormat getAudioFormat(AudioFormat elem) {
        return this.get(elem);
    }

    public AudioFormat matches(AudioFormat elem) {
        if (elem == null) {
            return null;
        }
        for (AudioFormat thisElem : this) {
            if (!AudioFormats.matches(elem, thisElem)) continue;
            return thisElem;
        }
        return null;
    }

    public AudioFormat[] toAudioFormatArray() {
        return this.toArray(EMPTY_FORMAT_ARRAY);
    }

    @Override
    public void add(int index, AudioFormat element) {
        throw new UnsupportedOperationException("unsupported");
    }

    @Override
    public AudioFormat set(int index, AudioFormat element) {
        throw new UnsupportedOperationException("unsupported");
    }
}

