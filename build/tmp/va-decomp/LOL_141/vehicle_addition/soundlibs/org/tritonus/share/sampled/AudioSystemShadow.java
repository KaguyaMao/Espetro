/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file.AiffAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file.AuAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file.WaveAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.AudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TDataOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TNonSeekableDataOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TSeekableDataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

public class AudioSystemShadow {
    public static TDataOutputStream getDataOutputStream(File file) throws IOException {
        return new TSeekableDataOutputStream(file);
    }

    public static TDataOutputStream getDataOutputStream(OutputStream stream) throws IOException {
        return new TNonSeekableDataOutputStream(stream);
    }

    public static AudioOutputStream getAudioOutputStream(AudioFileFormat.Type type, AudioFormat audioFormat, long lLengthInBytes, TDataOutputStream dataOutputStream) {
        TAudioOutputStream audioOutputStream = null;
        if (type.equals(AudioFileFormat.Type.AIFF) || type.equals(AudioFileFormat.Type.AIFF)) {
            audioOutputStream = new AiffAudioOutputStream(audioFormat, type, lLengthInBytes, dataOutputStream);
        } else if (type.equals(AudioFileFormat.Type.AU)) {
            audioOutputStream = new AuAudioOutputStream(audioFormat, lLengthInBytes, dataOutputStream);
        } else if (type.equals(AudioFileFormat.Type.WAVE)) {
            audioOutputStream = new WaveAudioOutputStream(audioFormat, lLengthInBytes, dataOutputStream);
        }
        return audioOutputStream;
    }

    public static AudioOutputStream getAudioOutputStream(AudioFileFormat.Type type, AudioFormat audioFormat, long lLengthInBytes, File file) throws IOException {
        TDataOutputStream dataOutputStream = AudioSystemShadow.getDataOutputStream(file);
        AudioOutputStream audioOutputStream = AudioSystemShadow.getAudioOutputStream(type, audioFormat, lLengthInBytes, dataOutputStream);
        return audioOutputStream;
    }

    public static AudioOutputStream getAudioOutputStream(AudioFileFormat.Type type, AudioFormat audioFormat, long lLengthInBytes, OutputStream outputStream) throws IOException {
        TDataOutputStream dataOutputStream = AudioSystemShadow.getDataOutputStream(outputStream);
        AudioOutputStream audioOutputStream = AudioSystemShadow.getAudioOutputStream(type, audioFormat, lLengthInBytes, dataOutputStream);
        return audioOutputStream;
    }
}

