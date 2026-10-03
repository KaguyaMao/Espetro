/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TDataLine;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TMixer;
import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TBaseDataLine
extends TDataLine {
    public TBaseDataLine(TMixer mixer, DataLine.Info info) {
        super(mixer, info);
    }

    public TBaseDataLine(TMixer mixer, DataLine.Info info, Collection<Control> controls) {
        super(mixer, info, controls);
    }

    public void open(AudioFormat format, int nBufferSize) throws LineUnavailableException {
        if (TDebug.TraceDataLine) {
            TDebug.out("TBaseDataLine.open(AudioFormat, int): called with buffer size: " + nBufferSize);
        }
        this.setBufferSize(nBufferSize);
        this.open(format);
    }

    public void open(AudioFormat format) throws LineUnavailableException {
        if (TDebug.TraceDataLine) {
            TDebug.out("TBaseDataLine.open(AudioFormat): called");
        }
        this.setFormat(format);
        this.open();
    }

    protected void finalize() {
        if (this.isOpen()) {
            this.close();
        }
    }
}

