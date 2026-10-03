/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TDataOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;

public class HeaderlessAudioOutputStream
extends TAudioOutputStream {
    public HeaderlessAudioOutputStream(AudioFormat audioFormat, long lLength, TDataOutputStream dataOutputStream) {
        super(audioFormat, lLength, dataOutputStream, false);
    }

    protected void writeHeader() throws IOException {
    }
}

