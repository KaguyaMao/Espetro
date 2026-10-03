/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.sampled.file.AuTool;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TAudioOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TDataOutputStream;
import java.io.IOException;
import javax.sound.sampled.AudioFormat;

public class AuAudioOutputStream
extends TAudioOutputStream {
    private static String description = "Created by Tritonus";

    protected static void writeText(TDataOutputStream dos, String s) throws IOException {
        if (s.length() > 0) {
            dos.writeBytes(s);
            dos.writeByte(0);
            if (s.length() % 2 == 0) {
                dos.writeByte(0);
            }
        }
    }

    protected static int getTextLength(String s) {
        if (s.length() == 0) {
            return 0;
        }
        return s.length() + 2 & 0xFFFFFFFE;
    }

    public AuAudioOutputStream(AudioFormat audioFormat, long lLength, TDataOutputStream dataOutputStream) {
        super(audioFormat, lLength > Integer.MAX_VALUE ? -1L : lLength, dataOutputStream, dataOutputStream.supportsSeek());
        if (AuTool.getFormatCode(audioFormat) == 0) {
            throw new IllegalArgumentException("Unknown encoding/format for AU file: " + audioFormat);
        }
        this.requireSign8bit(true);
        this.requireEndianness(true);
        if (TDebug.TraceAudioOutputStream) {
            TDebug.out("Writing AU: " + audioFormat.getSampleSizeInBits() + " bits, " + audioFormat.getEncoding());
        }
    }

    protected void writeHeader() throws IOException {
        if (TDebug.TraceAudioOutputStream) {
            TDebug.out("AuAudioOutputStream.writeHeader(): called.");
        }
        AudioFormat format = this.getFormat();
        long lLength = this.getLength();
        TDataOutputStream dos = this.getDataOutputStream();
        if (TDebug.TraceAudioOutputStream) {
            TDebug.out("AuAudioOutputStream.writeHeader(): AudioFormat: " + format);
            TDebug.out("AuAudioOutputStream.writeHeader(): length: " + lLength);
        }
        dos.writeInt(779316836);
        dos.writeInt(24 + AuAudioOutputStream.getTextLength(description));
        dos.writeInt(lLength != -1L ? (int)lLength : -1);
        dos.writeInt(AuTool.getFormatCode(format));
        dos.writeInt((int)format.getSampleRate());
        dos.writeInt(format.getChannels());
        AuAudioOutputStream.writeText(dos, description);
    }

    protected void patchHeader() throws IOException {
        TDataOutputStream tdos = this.getDataOutputStream();
        tdos.seek(0L);
        this.setLengthFromCalculatedLength();
        this.writeHeader();
    }
}

