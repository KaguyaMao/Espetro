/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Bitstream;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Header;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.IcyListener;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.MpegAudioFileFormat;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.MpegAudioFormat;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.MpegEncoding;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.MpegFileFormatType;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.IcyInputStream;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file.TAudioFileReader;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PushbackInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.security.AccessControlException;
import java.util.HashMap;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.UnsupportedAudioFileException;

public class MpegAudioFileReader
extends TAudioFileReader {
    public static final String VERSION = "MP3SPI 1.9.5";
    private final int SYNC = -2097152;
    private String weak = null;
    private final AudioFormat.Encoding[][] sm_aEncodings = new AudioFormat.Encoding[][]{{MpegEncoding.MPEG2L1, MpegEncoding.MPEG2L2, MpegEncoding.MPEG2L3}, {MpegEncoding.MPEG1L1, MpegEncoding.MPEG1L2, MpegEncoding.MPEG1L3}, {MpegEncoding.MPEG2DOT5L1, MpegEncoding.MPEG2DOT5L2, MpegEncoding.MPEG2DOT5L3}};
    public static int INITAL_READ_LENGTH = 4096000;
    private static int MARK_LIMIT = INITAL_READ_LENGTH + 1;
    private static final String[] id3v1genres;

    public MpegAudioFileReader() {
        super(MARK_LIMIT, true);
        if (TDebug.TraceAudioFileReader) {
            TDebug.out(VERSION);
        }
        try {
            this.weak = System.getProperty("mp3spi.weak");
        }
        catch (AccessControlException accessControlException) {
            // empty catch block
        }
    }

    public AudioFileFormat getAudioFileFormat(File file) throws UnsupportedAudioFileException, IOException {
        return super.getAudioFileFormat(file);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public AudioFileFormat getAudioFileFormat(URL url) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("MpegAudioFileReader.getAudioFileFormat(URL): begin");
        }
        long lFileLengthInBytes = -1L;
        URLConnection conn = url.openConnection();
        conn.setRequestProperty("Icy-Metadata", "1");
        InputStream inputStream = conn.getInputStream();
        AudioFileFormat audioFileFormat = null;
        try {
            audioFileFormat = this.getAudioFileFormat(inputStream, lFileLengthInBytes);
        }
        finally {
            inputStream.close();
        }
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("MpegAudioFileReader.getAudioFileFormat(URL): end");
        }
        return audioFileFormat;
    }

    public AudioFileFormat getAudioFileFormat(InputStream inputStream, long mediaLength) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) {
            TDebug.out(">MpegAudioFileReader.getAudioFileFormat(InputStream inputStream, long mediaLength): begin");
        }
        HashMap<String, Object> aff_properties = new HashMap<String, Object>();
        HashMap<String, Comparable<Boolean>> af_properties = new HashMap<String, Comparable<Boolean>>();
        int mLength = (int)mediaLength;
        int size = inputStream.available();
        PushbackInputStream pis = new PushbackInputStream(inputStream, MARK_LIMIT);
        byte[] head = new byte[22];
        pis.read(head);
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("InputStream : " + inputStream + " =>" + new String(head));
        }
        if (head[0] == 82 && head[1] == 73 && head[2] == 70 && head[3] == 70 && head[8] == 87 && head[9] == 65 && head[10] == 86 && head[11] == 69) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("RIFF/WAV stream found");
            }
            int isPCM = head[21] << 8 & 0xFF00 | head[20] & 0xFF;
            if (this.weak == null && isPCM == 1) {
                throw new UnsupportedAudioFileException("WAV PCM stream found");
            }
        } else if (head[0] == 46 && head[1] == 115 && head[2] == 110 && head[3] == 100) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("AU stream found");
            }
            if (this.weak == null) {
                throw new UnsupportedAudioFileException("AU stream found");
            }
        } else if (head[0] == 70 && head[1] == 79 && head[2] == 82 && head[3] == 77 && head[8] == 65 && head[9] == 73 && head[10] == 70 && head[11] == 70) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("AIFF stream found");
            }
            if (this.weak == null) {
                throw new UnsupportedAudioFileException("AIFF stream found");
            }
        } else if (head[0] == 77 | head[0] == 109 && head[1] == 65 | head[1] == 97 && head[2] == 67 | head[2] == 99) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("APE stream found");
            }
            if (this.weak == null) {
                throw new UnsupportedAudioFileException("APE stream found");
            }
        } else if (head[0] == 70 | head[0] == 102 && head[1] == 76 | head[1] == 108 && head[2] == 65 | head[2] == 97 && head[3] == 67 | head[3] == 99) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("FLAC stream found");
            }
            if (this.weak == null) {
                throw new UnsupportedAudioFileException("FLAC stream found");
            }
        } else if (head[0] == 73 | head[0] == 105 && head[1] == 67 | head[1] == 99 && head[2] == 89 | head[2] == 121) {
            pis.unread(head);
            this.loadShoutcastInfo(pis, aff_properties);
        } else if (head[0] == 79 | head[0] == 111 && head[1] == 71 | head[1] == 103 && head[2] == 71 | head[2] == 103) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("Ogg stream found");
            }
            if (this.weak == null) {
                throw new UnsupportedAudioFileException("Ogg stream found");
            }
        } else {
            pis.unread(head);
        }
        int nVersion = -1;
        int nLayer = -1;
        int nSFIndex = -1;
        int nMode = -1;
        int FrameSize = -1;
        int nFrameSize = -1;
        int nFrequency = -1;
        int nTotalFrames = -1;
        float FrameRate = -1.0f;
        int BitRate = -1;
        int nChannels = -1;
        int nHeader = -1;
        int nTotalMS = -1;
        boolean nVBR = false;
        AudioFormat.Encoding encoding = null;
        try {
            Bitstream m_bitstream = new Bitstream(pis);
            int streamPos = m_bitstream.header_pos();
            aff_properties.put("mp3.header.pos", new Integer(streamPos));
            Header m_header = m_bitstream.readFrame();
            nVersion = m_header.version();
            if (nVersion == 2) {
                aff_properties.put("mp3.version.mpeg", Float.toString(2.5f));
            } else {
                aff_properties.put("mp3.version.mpeg", Integer.toString(2 - nVersion));
            }
            nLayer = m_header.layer();
            aff_properties.put("mp3.version.layer", Integer.toString(nLayer));
            nSFIndex = m_header.sample_frequency();
            nMode = m_header.mode();
            aff_properties.put("mp3.mode", new Integer(nMode));
            nChannels = nMode == 3 ? 1 : 2;
            aff_properties.put("mp3.channels", new Integer(nChannels));
            nVBR = m_header.vbr();
            af_properties.put("vbr", new Boolean(nVBR));
            aff_properties.put("mp3.vbr", new Boolean(nVBR));
            aff_properties.put("mp3.vbr.scale", new Integer(m_header.vbr_scale()));
            FrameSize = m_header.calculate_framesize();
            aff_properties.put("mp3.framesize.bytes", new Integer(FrameSize));
            if (FrameSize < 0) {
                throw new UnsupportedAudioFileException("Invalid FrameSize : " + FrameSize);
            }
            nFrequency = m_header.frequency();
            aff_properties.put("mp3.frequency.hz", new Integer(nFrequency));
            FrameRate = (float)(1.0 / (double)m_header.ms_per_frame() * 1000.0);
            aff_properties.put("mp3.framerate.fps", new Float(FrameRate));
            if (FrameRate < 0.0f) {
                throw new UnsupportedAudioFileException("Invalid FrameRate : " + FrameRate);
            }
            int tmpLength = mLength;
            if (streamPos > 0 && mLength != -1 && streamPos < mLength) {
                tmpLength -= streamPos;
            }
            if (mLength != -1) {
                aff_properties.put("mp3.length.bytes", new Integer(mLength));
                nTotalFrames = m_header.max_number_of_frames(tmpLength);
                aff_properties.put("mp3.length.frames", new Integer(nTotalFrames));
            }
            BitRate = m_header.bitrate();
            af_properties.put("bitrate", new Integer(BitRate));
            aff_properties.put("mp3.bitrate.nominal.bps", new Integer(BitRate));
            nHeader = m_header.getSyncHeader();
            encoding = this.sm_aEncodings[nVersion][nLayer - 1];
            aff_properties.put("mp3.version.encoding", encoding.toString());
            if (mLength != -1) {
                nTotalMS = Math.round(m_header.total_ms(tmpLength));
                aff_properties.put("duration", new Long((long)nTotalMS * 1000L));
            }
            aff_properties.put("mp3.copyright", new Boolean(m_header.copyright()));
            aff_properties.put("mp3.original", new Boolean(m_header.original()));
            aff_properties.put("mp3.crc", new Boolean(m_header.checksums()));
            aff_properties.put("mp3.padding", new Boolean(m_header.padding()));
            InputStream id3v2 = m_bitstream.getRawID3v2();
            if (id3v2 != null) {
                aff_properties.put("mp3.id3tag.v2", id3v2);
                this.parseID3v2Frames(id3v2, aff_properties);
            }
            if (TDebug.TraceAudioFileReader) {
                TDebug.out(m_header.toString());
            }
        }
        catch (Exception e) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("not a MPEG stream:" + e.getMessage());
            }
            throw new UnsupportedAudioFileException("not a MPEG stream:" + e.getMessage());
        }
        int cVersion = nHeader >> 19 & 3;
        if (cVersion == 1) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("not a MPEG stream: wrong version");
            }
            throw new UnsupportedAudioFileException("not a MPEG stream: wrong version");
        }
        int cSFIndex = nHeader >> 10 & 3;
        if (cSFIndex == 3) {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("not a MPEG stream: wrong sampling rate");
            }
            throw new UnsupportedAudioFileException("not a MPEG stream: wrong sampling rate");
        }
        if ((long)size == mediaLength && mediaLength != -1L) {
            FileInputStream fis = (FileInputStream)inputStream;
            byte[] id3v1 = new byte[128];
            long bytesSkipped = fis.skip(inputStream.available() - id3v1.length);
            int read = fis.read(id3v1, 0, id3v1.length);
            if (id3v1[0] == 84 && id3v1[1] == 65 && id3v1[2] == 71) {
                this.parseID3v1Frames(id3v1, aff_properties);
            }
        }
        MpegAudioFormat format = new MpegAudioFormat(encoding, (float)nFrequency, -1, nChannels, -1, FrameRate, true, af_properties);
        return new MpegAudioFileFormat(MpegFileFormatType.MP3, (AudioFormat)format, nTotalFrames, mLength, aff_properties);
    }

    public AudioInputStream getAudioInputStream(File file) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("getAudioInputStream(File file)");
        }
        FileInputStream inputStream = new FileInputStream(file);
        try {
            return this.getAudioInputStream(inputStream);
        }
        catch (UnsupportedAudioFileException e) {
            if (inputStream != null) {
                ((InputStream)inputStream).close();
            }
            throw e;
        }
        catch (IOException e) {
            if (inputStream != null) {
                ((InputStream)inputStream).close();
            }
            throw e;
        }
    }

    public AudioInputStream getAudioInputStream(URL url) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("MpegAudioFileReader.getAudioInputStream(URL): begin");
        }
        long lFileLengthInBytes = -1L;
        URLConnection conn = url.openConnection();
        boolean isShout = false;
        int toRead = 4;
        byte[] head = new byte[toRead];
        conn.setRequestProperty("Icy-Metadata", "1");
        BufferedInputStream bInputStream = new BufferedInputStream(conn.getInputStream());
        bInputStream.mark(toRead);
        int read = bInputStream.read(head, 0, toRead);
        if (read > 2 && head[0] == 73 | head[0] == 105 && head[1] == 67 | head[1] == 99 && head[2] == 89 | head[2] == 121) {
            isShout = true;
        }
        bInputStream.reset();
        BufferedInputStream inputStream = null;
        if (isShout) {
            IcyInputStream icyStream = new IcyInputStream(bInputStream);
            icyStream.addTagParseListener(IcyListener.getInstance());
            inputStream = icyStream;
        } else {
            String metaint = conn.getHeaderField("icy-metaint");
            if (metaint != null) {
                IcyInputStream icyStream = new IcyInputStream((InputStream)bInputStream, metaint);
                icyStream.addTagParseListener(IcyListener.getInstance());
                inputStream = icyStream;
            } else {
                inputStream = bInputStream;
            }
        }
        AudioInputStream audioInputStream = null;
        try {
            audioInputStream = this.getAudioInputStream(inputStream, lFileLengthInBytes);
        }
        catch (UnsupportedAudioFileException e) {
            ((InputStream)inputStream).close();
            throw e;
        }
        catch (IOException e) {
            ((InputStream)inputStream).close();
            throw e;
        }
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("MpegAudioFileReader.getAudioInputStream(URL): end");
        }
        return audioInputStream;
    }

    public AudioInputStream getAudioInputStream(InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("MpegAudioFileReader.getAudioInputStream(InputStream inputStream)");
        }
        if (!inputStream.markSupported()) {
            inputStream = new BufferedInputStream(inputStream);
        }
        return super.getAudioInputStream(inputStream);
    }

    protected void parseID3v1Frames(byte[] frames, HashMap props) {
        String genrev2;
        int genrev1;
        int start;
        String tag;
        block12: {
            if (TDebug.TraceAudioFileReader) {
                TDebug.out("Parsing ID3v1");
            }
            tag = null;
            try {
                tag = new String(frames, 0, frames.length, "ISO-8859-1");
            }
            catch (UnsupportedEncodingException e) {
                tag = new String(frames, 0, frames.length);
                if (!TDebug.TraceAudioFileReader) break block12;
                TDebug.out("Cannot use ISO-8859-1");
            }
        }
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("ID3v1 frame dump='" + tag + "'");
        }
        String titlev1 = this.chopSubstring(tag, start = 3, start += 30);
        String titlev2 = (String)props.get("title");
        if ((titlev2 == null || titlev2.length() == 0) && titlev1 != null) {
            props.put("title", titlev1);
        }
        String artistv1 = this.chopSubstring(tag, start, start += 30);
        String artistv2 = (String)props.get("author");
        if ((artistv2 == null || artistv2.length() == 0) && artistv1 != null) {
            props.put("author", artistv1);
        }
        String albumv1 = this.chopSubstring(tag, start, start += 30);
        String albumv2 = (String)props.get("album");
        if ((albumv2 == null || albumv2.length() == 0) && albumv1 != null) {
            props.put("album", albumv1);
        }
        String yearv1 = this.chopSubstring(tag, start, start += 4);
        String yearv2 = (String)props.get("year");
        if ((yearv2 == null || yearv2.length() == 0) && yearv1 != null) {
            props.put("date", yearv1);
        }
        String commentv1 = this.chopSubstring(tag, start, start += 28);
        String commentv2 = (String)props.get("comment");
        if ((commentv2 == null || commentv2.length() == 0) && commentv1 != null) {
            props.put("comment", commentv1);
        }
        String trackv1 = "" + (frames[126] & 0xFF);
        String trackv2 = (String)props.get("mp3.id3tag.track");
        if ((trackv2 == null || trackv2.length() == 0) && trackv1 != null) {
            props.put("mp3.id3tag.track", trackv1);
        }
        if ((genrev1 = frames[127] & 0xFF) >= 0 && genrev1 < id3v1genres.length && ((genrev2 = (String)props.get("mp3.id3tag.genre")) == null || genrev2.length() == 0)) {
            props.put("mp3.id3tag.genre", id3v1genres[genrev1]);
        }
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("ID3v1 parsed");
        }
    }

    private String chopSubstring(String s, int start, int end) {
        String str;
        block3: {
            str = null;
            try {
                str = s.substring(start, end);
                int loc = str.indexOf(0);
                if (loc != -1) {
                    str = str.substring(0, loc);
                }
            }
            catch (StringIndexOutOfBoundsException e) {
                if (!TDebug.TraceAudioFileReader) break block3;
                TDebug.out("Cannot chopSubString " + e.getMessage());
            }
        }
        return str;
    }

    protected void parseID3v2Frames(InputStream frames, HashMap props) {
        block41: {
            int size;
            byte[] bframes;
            block40: {
                if (TDebug.TraceAudioFileReader) {
                    TDebug.out("Parsing ID3v2");
                }
                bframes = null;
                size = -1;
                try {
                    size = frames.available();
                    bframes = new byte[size];
                    frames.mark(size);
                    frames.read(bframes);
                    frames.reset();
                }
                catch (IOException e) {
                    if (!TDebug.TraceAudioFileReader) break block40;
                    TDebug.out("Cannot parse ID3v2 :" + e.getMessage());
                }
            }
            if (!"ID3".equals(new String(bframes, 0, 3))) {
                TDebug.out("No ID3v2 header found!");
                return;
            }
            int v2version = bframes[3] & 0xFF;
            props.put("mp3.id3tag.v2.version", String.valueOf(v2version));
            if (v2version < 2 || v2version > 4) {
                TDebug.out("Unsupported ID3v2 version " + v2version + "!");
                return;
            }
            try {
                if (TDebug.TraceAudioFileReader) {
                    TDebug.out("ID3v2 frame dump='" + new String(bframes, 0, bframes.length) + "'");
                }
                String value = null;
                for (int i = 10; i < bframes.length && bframes[i] > 0; i += size) {
                    if (v2version == 3 || v2version == 4) {
                        String code = new String(bframes, i, 4);
                        size = bframes[i + 4] << 24 & 0xFF000000 | bframes[i + 5] << 16 & 0xFF0000 | bframes[i + 6] << 8 & 0xFF00 | bframes[i + 7] & 0xFF;
                        if (!code.equals("TALB") && !code.equals("TIT2") && !code.equals("TYER") && !code.equals("TPE1") && !code.equals("TCOP") && !code.equals("COMM") && !code.equals("TCON") && !code.equals("TRCK") && !code.equals("TPOS") && !code.equals("TDRC") && !code.equals("TCOM") && !code.equals("TIT1") && !code.equals("TENC") && !code.equals("TPUB") && !code.equals("TPE2") && !code.equals("TLEN") || (value = code.equals("COMM") ? this.parseText(bframes, i, size, 5) : this.parseText(bframes, i += 10, size, 1)) == null || value.length() <= 0) continue;
                        if (code.equals("TALB")) {
                            props.put("album", value);
                            continue;
                        }
                        if (code.equals("TIT2")) {
                            props.put("title", value);
                            continue;
                        }
                        if (code.equals("TYER")) {
                            props.put("date", value);
                            continue;
                        }
                        if (code.equals("TDRC")) {
                            props.put("date", value);
                            continue;
                        }
                        if (code.equals("TPE1")) {
                            props.put("author", value);
                            continue;
                        }
                        if (code.equals("TCOP")) {
                            props.put("copyright", value);
                            continue;
                        }
                        if (code.equals("COMM")) {
                            props.put("comment", value);
                            continue;
                        }
                        if (code.equals("TCON")) {
                            props.put("mp3.id3tag.genre", value);
                            continue;
                        }
                        if (code.equals("TRCK")) {
                            props.put("mp3.id3tag.track", value);
                            continue;
                        }
                        if (code.equals("TPOS")) {
                            props.put("mp3.id3tag.disc", value);
                            continue;
                        }
                        if (code.equals("TCOM")) {
                            props.put("mp3.id3tag.composer", value);
                            continue;
                        }
                        if (code.equals("TIT1")) {
                            props.put("mp3.id3tag.grouping", value);
                            continue;
                        }
                        if (code.equals("TENC")) {
                            props.put("mp3.id3tag.encoded", value);
                            continue;
                        }
                        if (code.equals("TPUB")) {
                            props.put("mp3.id3tag.publisher", value);
                            continue;
                        }
                        if (code.equals("TPE2")) {
                            props.put("mp3.id3tag.orchestra", value);
                            continue;
                        }
                        if (!code.equals("TLEN")) continue;
                        props.put("mp3.id3tag.length", value);
                        continue;
                    }
                    String scode = new String(bframes, i, 3);
                    size = 0 + (bframes[i + 3] << 16) + (bframes[i + 4] << 8) + bframes[i + 5];
                    if (!scode.equals("TAL") && !scode.equals("TT2") && !scode.equals("TP1") && !scode.equals("TYE") && !scode.equals("TRK") && !scode.equals("TPA") && !scode.equals("TCR") && !scode.equals("TCO") && !scode.equals("TCM") && !scode.equals("COM") && !scode.equals("TT1") && !scode.equals("TEN") && !scode.equals("TPB") && !scode.equals("TP2") && !scode.equals("TLE") || (value = scode.equals("COM") ? this.parseText(bframes, i, size, 5) : this.parseText(bframes, i += 6, size, 1)) == null || value.length() <= 0) continue;
                    if (scode.equals("TAL")) {
                        props.put("album", value);
                        continue;
                    }
                    if (scode.equals("TT2")) {
                        props.put("title", value);
                        continue;
                    }
                    if (scode.equals("TYE")) {
                        props.put("date", value);
                        continue;
                    }
                    if (scode.equals("TP1")) {
                        props.put("author", value);
                        continue;
                    }
                    if (scode.equals("TCR")) {
                        props.put("copyright", value);
                        continue;
                    }
                    if (scode.equals("COM")) {
                        props.put("comment", value);
                        continue;
                    }
                    if (scode.equals("TCO")) {
                        props.put("mp3.id3tag.genre", value);
                        continue;
                    }
                    if (scode.equals("TRK")) {
                        props.put("mp3.id3tag.track", value);
                        continue;
                    }
                    if (scode.equals("TPA")) {
                        props.put("mp3.id3tag.disc", value);
                        continue;
                    }
                    if (scode.equals("TCM")) {
                        props.put("mp3.id3tag.composer", value);
                        continue;
                    }
                    if (scode.equals("TT1")) {
                        props.put("mp3.id3tag.grouping", value);
                        continue;
                    }
                    if (scode.equals("TEN")) {
                        props.put("mp3.id3tag.encoded", value);
                        continue;
                    }
                    if (scode.equals("TPB")) {
                        props.put("mp3.id3tag.publisher", value);
                        continue;
                    }
                    if (scode.equals("TP2")) {
                        props.put("mp3.id3tag.orchestra", value);
                        continue;
                    }
                    if (!scode.equals("TLE")) continue;
                    props.put("mp3.id3tag.length", value);
                }
            }
            catch (RuntimeException e) {
                if (!TDebug.TraceAudioFileReader) break block41;
                TDebug.out("Cannot parse ID3v2 :" + e.getMessage());
            }
        }
        if (TDebug.TraceAudioFileReader) {
            TDebug.out("ID3v2 parsed");
        }
    }

    protected String parseText(byte[] bframes, int offset, int size, int skip) {
        String value;
        block2: {
            value = null;
            try {
                String[] ENC_TYPES = new String[]{"ISO-8859-1", "UTF16", "UTF-16BE", "UTF-8"};
                value = new String(bframes, offset + skip, size - skip, ENC_TYPES[bframes[offset]]);
                value = this.chopSubstring(value, 0, value.length());
            }
            catch (UnsupportedEncodingException e) {
                if (!TDebug.TraceAudioFileReader) break block2;
                TDebug.out("ID3v2 Encoding error :" + e.getMessage());
            }
        }
        return value;
    }

    protected void loadShoutcastInfo(InputStream input, HashMap props) throws IOException {
        MP3Tag[] meta;
        IcyInputStream icy = new IcyInputStream(new BufferedInputStream(input));
        HashMap metadata = icy.getTagHash();
        MP3Tag titleMP3Tag = icy.getTag("icy-name");
        if (titleMP3Tag != null) {
            props.put("title", ((String)titleMP3Tag.getValue()).trim());
        }
        if ((meta = icy.getTags()) != null) {
            StringBuffer metaStr = new StringBuffer();
            for (int i = 0; i < meta.length; ++i) {
                String key = meta[i].getName();
                String value = ((String)icy.getTag(key).getValue()).trim();
                props.put("mp3.shoutcast.metadata." + key, value);
            }
        }
    }

    static {
        String s = System.getProperty("marklimit");
        if (s != null) {
            try {
                INITAL_READ_LENGTH = Integer.parseInt(s);
                MARK_LIMIT = INITAL_READ_LENGTH + 1;
            }
            catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        id3v1genres = new String[]{"Blues", "Classic Rock", "Country", "Dance", "Disco", "Funk", "Grunge", "Hip-Hop", "Jazz", "Metal", "New Age", "Oldies", "Other", "Pop", "R&B", "Rap", "Reggae", "Rock", "Techno", "Industrial", "Alternative", "Ska", "Death Metal", "Pranks", "Soundtrack", "Euro-Techno", "Ambient", "Trip-Hop", "Vocal", "Jazz+Funk", "Fusion", "Trance", "Classical", "Instrumental", "Acid", "House", "Game", "Sound Clip", "Gospel", "Noise", "AlternRock", "Bass", "Soul", "Punk", "Space", "Meditative", "Instrumental Pop", "Instrumental Rock", "Ethnic", "Gothic", "Darkwave", "Techno-Industrial", "Electronic", "Pop-Folk", "Eurodance", "Dream", "Southern Rock", "Comedy", "Cult", "Gangsta", "Top 40", "Christian Rap", "Pop/Funk", "Jungle", "Native American", "Cabaret", "New Wave", "Psychadelic", "Rave", "Showtunes", "Trailer", "Lo-Fi", "Tribal", "Acid Punk", "Acid Jazz", "Polka", "Retro", "Musical", "Rock & Roll", "Hard Rock", "Folk", "Folk-Rock", "National Folk", "Swing", "Fast Fusion", "Bebob", "Latin", "Revival", "Celtic", "Bluegrass", "Avantgarde", "Gothic Rock", "Progressive Rock", "Psychedelic Rock", "Symphonic Rock", "Slow Rock", "Big Band", "Chorus", "Easy Listening", "Acoustic", "Humour", "Speech", "Chanson", "Opera", "Chamber Music", "Sonata", "Symphony", "Booty Brass", "Primus", "Porn Groove", "Satire", "Slow Jam", "Club", "Tango", "Samba", "Folklore", "Ballad", "Power Ballad", "Rhythmic Soul", "Freestyle", "Duet", "Punk Rock", "Drum Solo", "A Capela", "Euro-House", "Dance Hall", "Goa", "Drum & Bass", "Club-House", "Hardcore", "Terror", "Indie", "BritPop", "Negerpunk", "Polsk Punk", "Beat", "Christian Gangsta Rap", "Heavy Metal", "Black Metal", "Crossover", "Contemporary Christian", "Christian Rock", "Merengue", "Salsa", "Thrash Metal", "Anime", "JPop", "SynthPop"};
    }
}

