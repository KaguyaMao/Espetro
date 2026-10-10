/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.SampleBuffer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Decoder;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.DecoderConfig;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.adts.ADTSDemultiplexer;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4Container;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4Exception;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.AudioTrack;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Frame;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.MetaData;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Movie;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Track;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.AACAudioFileFormatType;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.AACAudioInputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.spi.javasound.MP4AudioInputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.io.BufferedInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.sound.sampled.spi.AudioFileReader;

public class AACAudioFileReader
extends AudioFileReader {
    private static AACAudioInputStream decodeAACAudioInputStream(InputStream in, Map<String, Object> fileProperties, Map<String, Object> formatProperties) throws IOException {
        return (AACAudioInputStream)AACAudioFileReader.decodeAACAudio(in, fileProperties, formatProperties, false);
    }

    private static AudioFileFormat decodeAACAudioFileFormat(InputStream in, Map<String, Object> fileProperties, Map<String, Object> formatProperties) throws IOException {
        return (AudioFileFormat)AACAudioFileReader.decodeAACAudio(in, fileProperties, formatProperties, true);
    }

    private static Object decodeAACAudio(InputStream in, Map<String, Object> fileProperties, Map<String, Object> formatProperties, boolean fileFormat) throws IOException {
        if (!AACAudioFileReader.isAdtsAac(in)) {
            throw new IllegalArgumentException("Unsupported format: Stream is not a valid ADTS AAC audio.");
        }
        ADTSDemultiplexer adts = new ADTSDemultiplexer(in);
        Decoder decoder = Decoder.create(adts.getDecoderInfo());
        SampleBuffer sampleBuffer = new SampleBuffer(decoder.getAudioFormat());
        AACAudioFileReader.dumpDecoderConfigProperties(decoder.getConfig(), formatProperties);
        AudioFormat audioFormat = AACAudioFileReader.dumpSampleBufferProperties(sampleBuffer, formatProperties);
        if (fileFormat) {
            int frames = 1;
            try {
                while (true) {
                    adts.skipNextFrame();
                    ++frames;
                }
            }
            catch (EOFException eOFException) {
                double lengthInSeconds = (double)frames * (double)decoder.getConfig().getSampleLength() / (double)decoder.getConfig().getSampleFrequency().getFrequency();
                fileProperties.put("duration", (long)(lengthInSeconds * 1000000.0));
                return new AudioFileFormat(AACAudioFileFormatType.AAC, audioFormat, -1, fileProperties);
            }
        }
        return new AACAudioInputStream(adts, decoder, sampleBuffer, in, audioFormat, -1L);
    }

    private static boolean isAdtsAac(InputStream in) throws IOException {
        in.mark(2);
        byte[] header = new byte[2];
        int read = in.read(header);
        in.reset();
        if (read < 2) {
            return false;
        }
        int b0 = header[0] & 0xFF;
        int b1 = header[1] & 0xFF;
        return b0 == 255 && (b1 & 0xF6) == 240;
    }

    private static void dumpDecoderConfigProperties(DecoderConfig config, Map<String, Object> formatProperties) {
        formatProperties.put("aac.samplelength", config.getSampleLength());
        formatProperties.put("aac.framelength", config.getFrameLength());
        formatProperties.put("aac.channelcount", config.getChannelCount());
        formatProperties.put("aac.corecoderdelay", config.getCoreCoderDelay());
        formatProperties.put("aac.dependsoncoreorder", config.isDependsOnCoreCoder());
        formatProperties.put("aac.ps", config.isPSEnabled());
        formatProperties.put("aac.sbr", config.isSBREnabled());
        formatProperties.put("aac.smallframe", config.isSmallFrameUsed());
        formatProperties.put("aac.scalefactorresilience", config.isScalefactorResilienceUsed());
        formatProperties.put("aac.sectiondataresilience", config.isSectionDataResilienceUsed());
        formatProperties.put("aac.spectraldataresilience", config.isSpectralDataResilienceUsed());
    }

    private static AudioFormat dumpSampleBufferProperties(SampleBuffer sampleBuffer, Map<String, Object> formatProperties) {
        double bitrate = sampleBuffer.getBitrate();
        int sampleRate = sampleBuffer.getSampleRate();
        int bitsPerSample = sampleBuffer.getBitsPerSample();
        int channels = sampleBuffer.getChannels();
        boolean bigEndian = sampleBuffer.isBigEndian();
        formatProperties.put("bitrate", bitrate);
        formatProperties.put("samplerate", sampleRate);
        formatProperties.put("samplesizeinbits", bitsPerSample);
        formatProperties.put("channels", channels);
        formatProperties.put("bigendian", bigEndian);
        return new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, sampleRate, bitsPerSample, channels, AACAudioFileReader.frameSize(channels, bitsPerSample), sampleRate, bigEndian, formatProperties);
    }

    private static int frameSize(int channels, int sampleSizeInBits) {
        return channels == -1 || sampleSizeInBits == -1 ? -1 : (sampleSizeInBits + 7) / 8 * channels;
    }

    private static MP4AudioInputStream decodeMP4AudioInputStream(MP4InputStream in, Map<String, Object> fileProperties, Map<String, Object> formatProperties) throws IOException, UnsupportedAudioFileException {
        return (MP4AudioInputStream)AACAudioFileReader.decodeMP4Audio(in, fileProperties, formatProperties, false);
    }

    private static AudioFileFormat decodeMP4AudioFileFormat(MP4InputStream in, Map<String, Object> fileProperties, Map<String, Object> formatProperties) throws IOException, UnsupportedAudioFileException {
        return (AudioFileFormat)AACAudioFileReader.decodeMP4Audio(in, fileProperties, formatProperties, true);
    }

    private static Object decodeMP4Audio(MP4InputStream in, Map<String, Object> fileProperties, Map<String, Object> formatProperties, boolean fileFormat) throws IOException, UnsupportedAudioFileException {
        List<Track> tracks;
        MP4Container mp4;
        try {
            mp4 = new MP4Container(in);
        }
        catch (MP4Exception e) {
            throw new UnsupportedAudioFileException(e.getMessage());
        }
        Movie movie = mp4.getMovie();
        fileProperties.put("duration", (long)(movie.getDuration() * 1000000.0));
        fileProperties.put("mp4.creationtime", movie.getCreationTime());
        fileProperties.put("mp4.modificationtime", movie.getModificationTime());
        fileProperties.put("mp4.containsmetadata", movie.containsMetaData());
        if (movie.containsMetaData()) {
            MetaData metaData = movie.getMetaData();
            for (Map.Entry<MetaData.Field<?>, Object> entry : metaData.getAll().entrySet()) {
                fileProperties.put(entry.getKey().getName().replaceAll(" ", "").toLowerCase(Locale.ROOT), entry.getValue());
            }
        }
        if ((tracks = movie.getTracks(AudioTrack.AudioCodec.AAC)).isEmpty()) {
            throw new IOException("movie does not contain any AAC track");
        }
        AudioTrack track = (AudioTrack)tracks.get(0);
        Decoder decoder = Decoder.create(track.getDecoderSpecificInfo().getData());
        if (!track.hasMoreFrames()) {
            throw new IOException("no valid frame exists");
        }
        Frame frame = track.readNextFrame();
        if (frame == null) {
            throw new IOException("no valid frame exists");
        }
        SampleBuffer sampleBuffer = new SampleBuffer();
        decoder.decodeFrame(frame.getData(), sampleBuffer);
        AACAudioFileReader.dumpDecoderConfigProperties(decoder.getConfig(), formatProperties);
        AudioFormat audioFormat = AACAudioFileReader.dumpSampleBufferProperties(sampleBuffer, formatProperties);
        if (fileFormat) {
            return new AudioFileFormat(AACAudioFileFormatType.MP4_AAC, audioFormat, -1, fileProperties);
        }
        return new MP4AudioInputStream(track, decoder, sampleBuffer, in, audioFormat, -1L);
    }

    @Override
    public AudioFileFormat getAudioFileFormat(InputStream in) throws UnsupportedAudioFileException, IOException {
        if (in instanceof MP4InputStream && ((MP4InputStream)in).seekSupported()) {
            ((MP4InputStream)in).seek(0L);
            if (AACAudioFileReader.isMP4(in)) {
                ((MP4InputStream)in).seek(0L);
                return AACAudioFileReader.decodeMP4AudioFileFormat((MP4InputStream)in, new HashMap<String, Object>(), new HashMap<String, Object>());
            }
        }
        in.mark(1000);
        try {
            if (AACAudioFileReader.isMP4(in)) {
                in.reset();
                return AACAudioFileReader.decodeMP4AudioFileFormat(MP4InputStream.open(in), new HashMap<String, Object>(), new HashMap<String, Object>());
            }
            try {
                return AACAudioFileReader.decodeAACAudioFileFormat(in, new HashMap<String, Object>(), new HashMap<String, Object>());
            }
            catch (IOException e) {
                throw new UnsupportedAudioFileException();
            }
        }
        catch (UnsupportedAudioFileException e) {
            in.reset();
            throw e;
        }
    }

    private static boolean isMP4(InputStream in) throws IOException {
        byte[] head = new byte[12];
        Utils.readNBytes(in, head);
        boolean isMP4 = new String(head, 4, 4).equals("ftyp") ? true : (head[0] == 82 && head[1] == 73 && head[2] == 70 && head[3] == 70 && head[8] == 87 && head[9] == 65 && head[10] == 86 && head[11] == 69 ? false : (head[0] == 46 && head[1] == 115 && head[2] == 110 && head[3] == 100 ? false : (head[0] == 70 && head[1] == 79 && head[2] == 82 && head[3] == 77 && head[8] == 65 && head[9] == 73 && head[10] == 70 && head[11] == 70 ? false : (head[0] == 77 | head[0] == 109 && head[1] == 65 | head[1] == 97 && head[2] == 67 | head[2] == 99 ? false : (head[0] == 70 | head[0] == 102 && head[1] == 76 | head[1] == 108 && head[2] == 65 | head[2] == 97 && head[3] == 67 | head[3] == 99 ? false : (head[0] == 73 | head[0] == 105 && head[1] == 67 | head[1] == 99 && head[2] == 89 | head[2] == 121 ? false : (head[0] == 79 | head[0] == 111 && head[1] == 71 | head[1] == 103 && head[2] == 71 | head[2] == 103 ? false : false)))))));
        return isMP4;
    }

    private AudioFileFormat getAudioFileFormatAndClose(InputStream in) throws UnsupportedAudioFileException, IOException {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        try {
            AudioFileFormat audioFileFormat = this.getAudioFileFormat(in);
            return audioFileFormat;
        }
        finally {
            in.close();
        }
    }

    @Override
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        return this.getAudioFileFormatAndClose(url.openStream());
    }

    @Override
    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        return this.getAudioFileFormatAndClose(Files.newInputStream(file.toPath(), StandardOpenOption.READ));
    }

    @Override
    public AudioInputStream getAudioInputStream(InputStream in) throws UnsupportedAudioFileException, IOException, IllegalArgumentException {
        if (!(in instanceof MP4InputStream) && !in.markSupported()) {
            throw new IllegalArgumentException("in.markSupported() == false");
        }
        try {
            if (in instanceof MP4InputStream) {
                ((MP4InputStream)in).seek(0L);
                return AACAudioFileReader.decodeMP4AudioInputStream((MP4InputStream)in, new HashMap<String, Object>(), new HashMap<String, Object>());
            }
            in.mark(1000);
            if (AACAudioFileReader.isMP4(in)) {
                in.reset();
                return AACAudioFileReader.decodeMP4AudioInputStream(MP4InputStream.open(in), new HashMap<String, Object>(), new HashMap<String, Object>());
            }
            try {
                return AACAudioFileReader.decodeAACAudioInputStream(in, new HashMap<String, Object>(), new HashMap<String, Object>());
            }
            catch (IOException e) {
                try {
                    throw new UnsupportedAudioFileException();
                }
                catch (UnsupportedAudioFileException e2) {
                    try {
                        in.reset();
                        throw e2;
                    }
                    catch (IOException e3) {
                        if ("movie does not contain any AAC track".equals(e3.getMessage())) {
                            throw new UnsupportedAudioFileException("movie does not contain any AAC track");
                        }
                        throw e3;
                    }
                }
            }
        }
        catch (Exception e) {
            throw new UnsupportedAudioFileException(e.getMessage());
        }
    }

    @Override
    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        InputStream in = url.openStream();
        try {
            return this.getAudioInputStream(in.markSupported() ? in : new BufferedInputStream(in));
        }
        catch (IOException | UnsupportedAudioFileException e) {
            in.close();
            throw e;
        }
    }

    /*
     * Loose catch block
     */
    @Override
    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        InputStream in;
        block6: {
            in = Files.newInputStream(file.toPath(), StandardOpenOption.READ);
            if (!AACAudioFileReader.isMP4(in)) break block6;
            in.close();
            return AACAudioFileReader.decodeMP4AudioInputStream(MP4InputStream.open(new RandomAccessFile(file, "r")), new HashMap<String, Object>(), new HashMap<String, Object>());
            {
                catch (IOException e) {
                    if ("movie does not contain any AAC track".equals(e.getMessage())) {
                        throw new UnsupportedAudioFileException("movie does not contain any AAC track");
                    }
                    throw e;
                }
            }
        }
        try {
            return AACAudioFileReader.decodeAACAudioInputStream(in, new HashMap<String, Object>(), new HashMap<String, Object>());
        }
        catch (IOException e) {
            throw new UnsupportedAudioFileException();
        }
    }
}

