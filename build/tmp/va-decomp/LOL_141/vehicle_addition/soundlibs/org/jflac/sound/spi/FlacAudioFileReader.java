/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import LOL_141.vehicle_addition.soundlibs.org.jflac.Constants;
import LOL_141.vehicle_addition.soundlibs.org.jflac.FLACDecoder;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitInputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.io.BitOutputStream;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi.FlacAudioFormat;
import LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi.FlacFileFormatType;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.net.URL;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public class FlacAudioFileReader
extends AudioFileReader {
    private static final boolean DEBUG = false;
    public static final String KEY_DURATION = "duration";
    private FLACDecoder decoder;
    private StreamInfo streamInfo;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        try (FileInputStream inputStream = null;){
            inputStream = new FileInputStream(file);
            AudioFileFormat audioFileFormat = this.getAudioFileFormat(inputStream, (int)file.length());
            return audioFileFormat;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        try (InputStream inputStream = url.openStream();){
            AudioFileFormat audioFileFormat = this.getAudioFileFormat(inputStream);
            return audioFileFormat;
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (!stream.markSupported()) {
            throw new IOException("InputStream must support mark(), but doesn't: " + stream);
        }
        stream.mark(256);
        try {
            return this.getAudioFileFormat(stream, -1);
        }
        catch (UnsupportedAudioFileException e) {
            stream.reset();
            throw e;
        }
    }

    protected AudioFileFormat getAudioFileFormat(InputStream bitStream, int mediaLength) throws UnsupportedAudioFileException, IOException {
        FlacAudioFormat format;
        try {
            this.decoder = new FLACDecoder(bitStream);
            this.streamInfo = this.decoder.readStreamInfo();
            if (this.streamInfo == null) {
                throw new UnsupportedAudioFileException("No StreamInfo found");
            }
            format = new FlacAudioFormat(this.streamInfo);
        }
        catch (IOException ioe) {
            UnsupportedAudioFileException unsupportedAudioFileException = new UnsupportedAudioFileException(ioe.getMessage());
            unsupportedAudioFileException.initCause(ioe);
            throw unsupportedAudioFileException;
        }
        HashMap<String, Object> props = new HashMap<String, Object>();
        if (this.streamInfo.getSampleRate() > 0) {
            long duration = this.streamInfo.getTotalSamples() * 1000L * 1000L / (long)this.streamInfo.getSampleRate();
            props.put(KEY_DURATION, duration);
        }
        return new AudioFileFormat(FlacFileFormatType.FLAC, format, (int)this.streamInfo.getTotalSamples(), props);
    }

    @Override
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        FileInputStream inputStream = new FileInputStream(file);
        try {
            return this.getAudioInputStream(inputStream, (int)file.length());
        }
        catch (UnsupportedAudioFileException e) {
            try {
                ((InputStream)inputStream).close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
        catch (IOException e) {
            try {
                ((InputStream)inputStream).close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        InputStream inputStream = url.openStream();
        try {
            return this.getAudioInputStream(inputStream, -1);
        }
        catch (UnsupportedAudioFileException e) {
            try {
                inputStream.close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
        catch (IOException e) {
            try {
                inputStream.close();
            }
            catch (IOException e1) {
                e1.printStackTrace();
            }
            throw e;
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream stream) throws UnsupportedAudioFileException, IOException {
        if (!stream.markSupported()) {
            throw new IOException("InputStream must support mark(), but doesn't: " + stream);
        }
        stream.mark(256);
        try {
            return this.getAudioInputStream(stream, -1);
        }
        catch (UnsupportedAudioFileException e) {
            stream.reset();
            throw e;
        }
    }

    protected AudioInputStream getAudioInputStream(InputStream inputStream, int medialength) throws UnsupportedAudioFileException, IOException {
        AudioFileFormat audioFileFormat = this.getAudioFileFormat(inputStream, medialength);
        ByteArrayOutputStream byteOutStream = new ByteArrayOutputStream();
        BitOutputStream bitOutStream = new BitOutputStream(byteOutStream);
        bitOutStream.writeByteBlock(Constants.STREAM_SYNC_STRING, Constants.STREAM_SYNC_STRING.length);
        this.streamInfo.write(bitOutStream, false);
        BitInputStream bis = this.decoder.getBitInputStream();
        int bytesLeft = bis.getInputBytesUnconsumed();
        byte[] b = new byte[bytesLeft];
        bis.readByteBlockAlignedNoCRC(b, bytesLeft);
        byteOutStream.write(b);
        ByteArrayInputStream byteInStream = new ByteArrayInputStream(byteOutStream.toByteArray());
        SequenceInputStream sequenceInputStream = new SequenceInputStream(byteInStream, inputStream);
        AudioFormat format = audioFileFormat.getFormat();
        int frameLength = medialength;
        if (format.getFrameSize() != -1 && format.getFrameSize() > 0) {
            frameLength = audioFileFormat.getFrameLength();
        }
        return new AudioInputStream(sequenceInputStream, format, frameLength);
    }
}

