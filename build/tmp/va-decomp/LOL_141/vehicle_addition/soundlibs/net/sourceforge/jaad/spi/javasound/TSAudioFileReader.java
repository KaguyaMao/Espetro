/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.SampleBuffer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.ts.TStoPCMInputStream;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public class TSAudioFileReader
extends AudioFileReader {
    public static final AudioFileFormat.Type TS = new AudioFileFormat.Type("MPEG-TS", "ts");
    private static final int TS_PACKET_SIZE = 188;
    private static final int SYNC_BYTE = 71;
    private static final int MIN_SYNC_COUNT = 3;
    private static final int PROBE_LIMIT = 0x500000;

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        if (!this.isMpegTs(stream)) {
            throw new UnsupportedAudioFileException("Not a valid MPEG-TS stream.");
        }
        stream.mark(102400);
        try {
            TStoPCMInputStream pcmStream = new TStoPCMInputStream(stream);
            SampleBuffer buf = pcmStream.getSampleBuffer();
            AudioFormat format = new AudioFormat(buf.getSampleRate(), buf.getBitsPerSample(), buf.getChannels(), true, buf.isBigEndian());
            AudioFileFormat audioFileFormat = new AudioFileFormat(TS, format, -1);
            return audioFileFormat;
        }
        catch (Exception e) {
            throw new UnsupportedAudioFileException("Found TS stream, but no valid AAC audio could be decoded.");
        }
        finally {
            stream.reset();
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (!stream.markSupported()) {
            stream = new BufferedInputStream(stream);
        }
        AudioFileFormat format = this.getAudioFileFormat(stream);
        TStoPCMInputStream pcmStream = new TStoPCMInputStream(stream);
        return new AudioInputStream(pcmStream, format.getFormat(), -1L);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean isMpegTs(InputStream stream) throws IOException {
        stream.mark(0x500000);
        try {
            long s;
            long totalId3Size = 0L;
            while (true) {
                long s2;
                int readBytes;
                int n;
                byte[] header = new byte[10];
                for (readBytes = 0; readBytes < 10 && (n = stream.read(header, readBytes, 10 - readBytes)) != -1; readBytes += n) {
                }
                if (readBytes != 10 || header[0] != 73 || header[1] != 68 || header[2] != 51 || (header[6] & 0x80) != 0 || (header[7] & 0x80) != 0 || (header[8] & 0x80) != 0 || (header[9] & 0x80) != 0) break;
                int payloadSize = (header[6] & 0x7F) << 21 | (header[7] & 0x7F) << 14 | (header[8] & 0x7F) << 7 | header[9] & 0x7F;
                for (long skipped = 0L; skipped < (long)payloadSize && (s2 = stream.skip((long)payloadSize - skipped)) > 0L; skipped += s2) {
                }
                totalId3Size += (long)(10 + payloadSize);
            }
            stream.reset();
            for (long id3Skipped = 0L; id3Skipped < totalId3Size; id3Skipped += s) {
                s = stream.skip(totalId3Size - id3Skipped);
                if (s > 0L) continue;
                boolean bl = false;
                return bl;
            }
            for (int i = 0; i < 3; ++i) {
                long s3;
                int b = stream.read();
                if (b != 71) {
                    boolean bl = false;
                    return bl;
                }
                if (i >= 2) continue;
                int toSkip = 187;
                for (long packetSkipped = 0L; packetSkipped < (long)toSkip; packetSkipped += s3) {
                    s3 = stream.skip((long)toSkip - packetSkipped);
                    if (s3 > 0L) continue;
                    boolean bl = false;
                    return bl;
                }
            }
            boolean bl = true;
            return bl;
        }
        finally {
            stream.reset();
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        try (BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]));){
            AudioFileFormat audioFileFormat = this.getAudioFileFormat(is);
            return audioFileFormat;
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        try (BufferedInputStream is = new BufferedInputStream(url.openStream());){
            AudioFileFormat audioFileFormat = this.getAudioFileFormat(is);
            return audioFileFormat;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]));
        try {
            return this.getAudioInputStream(is);
        }
        catch (Throwable e) {
            TSAudioFileReader.closeSilently(is);
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        BufferedInputStream is = new BufferedInputStream(url.openStream());
        try {
            return this.getAudioInputStream(is);
        }
        catch (Throwable e) {
            TSAudioFileReader.closeSilently(is);
            throw e;
        }
    }

    private static void closeSilently(InputStream is) {
        try {
            is.close();
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

