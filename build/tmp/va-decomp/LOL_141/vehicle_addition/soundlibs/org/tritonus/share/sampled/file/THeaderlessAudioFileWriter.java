/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.AudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.HeaderlessAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TAudioFileWriter;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TDataOutputStream;
import java.io.IOException;
import java.util.Collection;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class THeaderlessAudioFileWriter
extends TAudioFileWriter {
    protected THeaderlessAudioFileWriter(Collection<AudioFileFormat.Type> fileTypes, Collection<AudioFormat> audioFormats) {
        super(fileTypes, audioFormats);
        if (TDebug.TraceAudioFileWriter) {
            TDebug.out("THeaderlessAudioFileWriter.<init>(): begin");
        }
        if (TDebug.TraceAudioFileWriter) {
            TDebug.out("THeaderlessAudioFileWriter.<init>(): end");
        }
    }

    @Override
    protected AudioOutputStream getAudioOutputStream(AudioFormat audioFormat, long lLengthInBytes, AudioFileFormat.Type fileType, TDataOutputStream dataOutputStream) throws IOException {
        if (TDebug.TraceAudioFileWriter) {
            TDebug.out("THeaderlessAudioFileWriter.getAudioOutputStream(): begin");
        }
        HeaderlessAudioOutputStream aos = new HeaderlessAudioOutputStream(audioFormat, lLengthInBytes, dataOutputStream);
        if (TDebug.TraceAudioFileWriter) {
            TDebug.out("THeaderlessAudioFileWriter.getAudioOutputStream(): end");
        }
        return aos;
    }
}

