/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.DecoderInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Frame;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Protection;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Type;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.api.Utils;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.Box;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ChunkOffsetBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DataEntryUrlBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DataReferenceBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.DecodingTimeToSampleBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.ESDBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.MediaHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleSizeBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.SampleToChunkBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.TrackHeaderBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.DecoderSpecificInfo;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.ESDescriptor;
import java.io.EOFException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;

public abstract class Track {
    private final MP4InputStream in;
    protected final TrackHeaderBox tkhd;
    private final MediaHeaderBox mdhd;
    private final boolean inFile;
    private final List<Frame> frames;
    private URL location;
    private int currentFrame;
    protected DecoderSpecificInfo decoderSpecificInfo;
    protected DecoderInfo decoderInfo;
    protected Protection protection;

    Track(Box trak, MP4InputStream in) {
        Box stbl;
        this.in = in;
        this.tkhd = (TrackHeaderBox)trak.getChild(1953196132L);
        Box mdia = trak.getChild(1835297121L);
        this.mdhd = (MediaHeaderBox)mdia.getChild(1835296868L);
        Box minf = mdia.getChild(1835626086L);
        Box dinf = minf.getChild(1684631142L);
        DataReferenceBox dref = (DataReferenceBox)dinf.getChild(1685218662L);
        if (dref.hasChild(1970433056L)) {
            DataEntryUrlBox url = (DataEntryUrlBox)dref.getChild(1970433056L);
            this.inFile = url.isInFile();
            if (!this.inFile) {
                try {
                    this.location = new URL(url.getLocation());
                }
                catch (MalformedURLException e) {
                    DecoderInfo.LOGGER.log(Level.WARNING, "Parsing URL-Box failed: {0}, url: {1}", new String[]{e.toString(), url.getLocation()});
                    this.location = null;
                }
            }
        } else {
            this.inFile = true;
            this.location = null;
        }
        if ((stbl = minf.getChild(1937007212L)).hasChildren()) {
            this.frames = new ArrayList<Frame>();
            this.parseSampleTable(stbl);
        } else {
            this.frames = Collections.emptyList();
        }
        this.currentFrame = 0;
    }

    private void parseSampleTable(Box stbl) {
        double timeScale = this.mdhd.getTimeScale();
        Type type = this.getType();
        long[] sampleSizes = ((SampleSizeBox)stbl.getChild(1937011578L)).getSampleSizes();
        ChunkOffsetBox stco = stbl.hasChild(1937007471L) ? (ChunkOffsetBox)stbl.getChild(1937007471L) : (ChunkOffsetBox)stbl.getChild(1668232756L);
        long[] chunkOffsets = stco.getChunks();
        SampleToChunkBox stsc = (SampleToChunkBox)stbl.getChild(1937011555L);
        long[] firstChunks = stsc.getFirstChunks();
        long[] samplesPerChunk = stsc.getSamplesPerChunk();
        DecodingTimeToSampleBox stts = (DecodingTimeToSampleBox)stbl.getChild(0x73747473L);
        long[] sampleCounts = stts.getSampleCounts();
        long[] sampleDeltas = stts.getSampleDeltas();
        long[] timeOffsets = new long[sampleSizes.length];
        long tmp = 0L;
        int off = 0;
        for (int i = 0; i < sampleCounts.length; ++i) {
            int j = 0;
            while ((long)j < sampleCounts[i]) {
                timeOffsets[off + j] = tmp;
                tmp += sampleDeltas[i];
                ++j;
            }
            off = (int)((long)off + sampleCounts[i]);
        }
        int current = 0;
        long offset = 0L;
        for (int i = 0; i < firstChunks.length; ++i) {
            int lastChunk = i < firstChunks.length - 1 ? (int)firstChunks[i + 1] - 1 : chunkOffsets.length;
            for (int j = (int)firstChunks[i] - 1; j < lastChunk; ++j) {
                offset = chunkOffsets[j];
                int k = 0;
                while ((long)k < samplesPerChunk[i]) {
                    double timeStamp = (double)timeOffsets[current] / timeScale;
                    this.frames.add(new Frame(type, offset, sampleSizes[current], timeStamp));
                    offset += sampleSizes[current];
                    ++current;
                    ++k;
                }
            }
        }
        Collections.sort(this.frames);
    }

    protected void findDecoderSpecificInfo(ESDBox esds) {
        ESDescriptor ed = esds.getEntryDescriptor();
        List<Descriptor> children = ed.getChildren();
        for (Descriptor e : children) {
            List<Descriptor> children2 = e.getChildren();
            for (Descriptor e2 : children2) {
                switch (e2.getType()) {
                    case 5: {
                        this.decoderSpecificInfo = (DecoderSpecificInfo)e2;
                    }
                }
            }
        }
    }

    protected <T> void parseSampleEntry(Box sampleEntry, Class<T> clazz) {
        try {
            T type = clazz.newInstance();
            if (sampleEntry.getClass().isInstance(type)) {
                System.out.println("true");
            }
        }
        catch (InstantiationException ex) {
            ex.printStackTrace();
        }
        catch (IllegalAccessException ex) {
            ex.printStackTrace();
        }
    }

    public abstract Type getType();

    public abstract Codec getCodec();

    public boolean isEnabled() {
        return this.tkhd.isTrackEnabled();
    }

    public boolean isUsed() {
        return this.tkhd.isTrackInMovie();
    }

    public boolean isUsedForPreview() {
        return this.tkhd.isTrackInPreview();
    }

    public Date getCreationTime() {
        return Utils.getDate(this.tkhd.getCreationTime());
    }

    public Date getModificationTime() {
        return Utils.getDate(this.tkhd.getModificationTime());
    }

    public Locale getLanguage() {
        return new Locale(this.mdhd.getLanguage());
    }

    public boolean isInFile() {
        return this.inFile;
    }

    public URL getLocation() {
        return this.location;
    }

    public DecoderSpecificInfo getDecoderSpecificInfo() {
        return this.decoderSpecificInfo;
    }

    public DecoderInfo getDecoderInfo() {
        return this.decoderInfo;
    }

    public Protection getProtection() {
        return this.protection;
    }

    public boolean hasMoreFrames() {
        return this.currentFrame < this.frames.size();
    }

    public Frame readNextFrame() throws IOException {
        Frame frame = null;
        if (this.hasMoreFrames()) {
            frame = this.frames.get(this.currentFrame);
            long diff = frame.getOffset() - this.in.getOffset();
            if (diff > 0L) {
                this.in.skipBytes(diff);
            } else if (diff < 0L) {
                if (this.in.seekSupported()) {
                    this.in.seek(frame.getOffset());
                } else {
                    DecoderInfo.LOGGER.log(Level.WARNING, "readNextFrame failed: frame {0} already skipped, offset:{1}, stream:{2}", new Object[]{this.currentFrame, frame.getOffset(), this.in.getOffset()});
                    throw new IOException("frame already skipped and no random access");
                }
            }
            byte[] b = new byte[(int)frame.getSize()];
            try {
                this.in.readBytes(b);
            }
            catch (EOFException e) {
                DecoderInfo.LOGGER.log(Level.WARNING, "readNextFrame failed: tried to read {0} bytes at {1}", new Long[]{frame.getSize(), this.in.getOffset()});
                throw e;
            }
            frame.setData(b);
            ++this.currentFrame;
        }
        return frame;
    }

    public double seek(double timestamp) {
        Frame frame = null;
        for (int i = 0; i < this.frames.size(); ++i) {
            int n = i++;
            frame = this.frames.get(n);
            if (!(frame.getTime() > timestamp)) continue;
            this.currentFrame = i;
            break;
        }
        return frame == null ? -1.0 : frame.getTime();
    }

    double getNextTimeStamp() {
        return this.frames.get(this.currentFrame).getTime();
    }

    public static interface Codec {
    }
}

