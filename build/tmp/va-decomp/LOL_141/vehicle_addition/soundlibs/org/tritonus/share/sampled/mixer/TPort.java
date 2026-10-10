/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TLine;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TMixer;
import java.util.Collection;
import javax.sound.sampled.Control;
import javax.sound.sampled.Line;
import javax.sound.sampled.Port;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TPort
extends TLine
implements Port {
    public TPort(TMixer mixer, Line.Info info) {
        super(mixer, info);
    }

    public TPort(TMixer mixer, Line.Info info, Collection<Control> controls) {
        super(mixer, info, controls);
    }
}

