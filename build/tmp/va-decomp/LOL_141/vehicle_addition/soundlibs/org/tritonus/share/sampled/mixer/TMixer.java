/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.ArraySet;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.AudioFormats;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TLine;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Port;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TMixer
extends TLine
implements Mixer {
    private static Line.Info[] EMPTY_LINE_INFO_ARRAY = new Line.Info[0];
    private static Line[] EMPTY_LINE_ARRAY = new Line[0];
    private Mixer.Info m_mixerInfo;
    private Collection<AudioFormat> m_supportedSourceFormats;
    private Collection<AudioFormat> m_supportedTargetFormats;
    private Collection<Line.Info> m_supportedSourceLineInfos;
    private Collection<Line.Info> m_supportedTargetLineInfos;
    private Set<SourceDataLine> m_openSourceDataLines;
    private Set<TargetDataLine> m_openTargetDataLines;

    protected TMixer(Mixer.Info mixerInfo, Line.Info lineInfo) {
        this(mixerInfo, lineInfo, new ArrayList<AudioFormat>(), new ArrayList<AudioFormat>(), new ArrayList<Line.Info>(), new ArrayList<Line.Info>());
    }

    protected TMixer(Mixer.Info mixerInfo, Line.Info lineInfo, Collection<AudioFormat> supportedSourceFormats, Collection<AudioFormat> supportedTargetFormats, Collection<Line.Info> supportedSourceLineInfos, Collection<Line.Info> supportedTargetLineInfos) {
        super(null, lineInfo);
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.<init>(): begin");
        }
        this.m_mixerInfo = mixerInfo;
        this.setSupportInformation(supportedSourceFormats, supportedTargetFormats, supportedSourceLineInfos, supportedTargetLineInfos);
        this.m_openSourceDataLines = new ArraySet<SourceDataLine>();
        this.m_openTargetDataLines = new ArraySet<TargetDataLine>();
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.<init>(): end");
        }
    }

    protected void setSupportInformation(Collection<AudioFormat> supportedSourceFormats, Collection<AudioFormat> supportedTargetFormats, Collection<Line.Info> supportedSourceLineInfos, Collection<Line.Info> supportedTargetLineInfos) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.setSupportInformation(): begin");
        }
        this.m_supportedSourceFormats = supportedSourceFormats;
        this.m_supportedTargetFormats = supportedTargetFormats;
        this.m_supportedSourceLineInfos = supportedSourceLineInfos;
        this.m_supportedTargetLineInfos = supportedTargetLineInfos;
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.setSupportInformation(): end");
        }
    }

    @Override
    public Mixer.Info getMixerInfo() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getMixerInfo(): begin");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getMixerInfo(): end");
        }
        return this.m_mixerInfo;
    }

    @Override
    public Line.Info[] getSourceLineInfo() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLineInfo(): begin");
        }
        Line.Info[] infos = this.m_supportedSourceLineInfos.toArray(EMPTY_LINE_INFO_ARRAY);
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLineInfo(): end");
        }
        return infos;
    }

    @Override
    public Line.Info[] getTargetLineInfo() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLineInfo(): begin");
        }
        Line.Info[] infos = this.m_supportedTargetLineInfos.toArray(EMPTY_LINE_INFO_ARRAY);
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLineInfo(): end");
        }
        return infos;
    }

    @Override
    public Line.Info[] getSourceLineInfo(Line.Info info) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLineInfo(Line.Info): info to test: " + info);
        }
        return EMPTY_LINE_INFO_ARRAY;
    }

    @Override
    public Line.Info[] getTargetLineInfo(Line.Info info) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLineInfo(Line.Info): info to test: " + info);
        }
        return EMPTY_LINE_INFO_ARRAY;
    }

    @Override
    public boolean isLineSupported(Line.Info info) {
        Class<?> lineClass;
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.isLineSupported(): info to test: " + info);
        }
        if ((lineClass = info.getLineClass()).equals(SourceDataLine.class)) {
            return TMixer.isLineSupportedImpl(info, this.m_supportedSourceLineInfos);
        }
        if (lineClass.equals(TargetDataLine.class)) {
            return TMixer.isLineSupportedImpl(info, this.m_supportedTargetLineInfos);
        }
        if (lineClass.equals(Port.class)) {
            return TMixer.isLineSupportedImpl(info, this.m_supportedSourceLineInfos) || TMixer.isLineSupportedImpl(info, this.m_supportedTargetLineInfos);
        }
        return false;
    }

    private static boolean isLineSupportedImpl(Line.Info info, Collection supportedLineInfos) {
        for (Line.Info info2 : supportedLineInfos) {
            if (!info2.matches(info)) continue;
            return true;
        }
        return false;
    }

    @Override
    public Line getLine(Line.Info info) throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getLine(): begin");
        }
        Class<?> lineClass = info.getLineClass();
        DataLine.Info dataLineInfo = null;
        Port.Info portInfo = null;
        AudioFormat[] aFormats = null;
        if (info instanceof DataLine.Info) {
            dataLineInfo = (DataLine.Info)info;
            aFormats = dataLineInfo.getFormats();
        } else if (info instanceof Port.Info) {
            portInfo = (Port.Info)info;
        }
        AudioFormat format = null;
        Line line = null;
        if (lineClass == SourceDataLine.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: SourceDataLine");
            }
            if (dataLineInfo == null) {
                throw new IllegalArgumentException("need DataLine.Info for SourceDataLine");
            }
            format = this.getSupportedSourceFormat(aFormats);
            line = this.getSourceDataLine(format, dataLineInfo.getMaxBufferSize());
        } else if (lineClass == Clip.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: Clip");
            }
            if (dataLineInfo == null) {
                throw new IllegalArgumentException("need DataLine.Info for Clip");
            }
            format = this.getSupportedSourceFormat(aFormats);
            line = this.getClip(format);
        } else if (lineClass == TargetDataLine.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: TargetDataLine");
            }
            if (dataLineInfo == null) {
                throw new IllegalArgumentException("need DataLine.Info for TargetDataLine");
            }
            format = this.getSupportedTargetFormat(aFormats);
            line = this.getTargetDataLine(format, dataLineInfo.getMaxBufferSize());
        } else if (lineClass == Port.class) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): type: TargetDataLine");
            }
            if (portInfo == null) {
                throw new IllegalArgumentException("need Port.Info for Port");
            }
            line = this.getPort(portInfo);
        } else {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getLine(): unknown line type, will throw exception");
            }
            throw new LineUnavailableException("unknown line class: " + lineClass);
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getLine(): end");
        }
        return line;
    }

    protected SourceDataLine getSourceDataLine(AudioFormat format, int nBufferSize) throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceDataLine(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support SourceDataLines");
    }

    protected Clip getClip(AudioFormat format) throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getClip(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support Clips");
    }

    protected TargetDataLine getTargetDataLine(AudioFormat format, int nBufferSize) throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetDataLine(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support TargetDataLines");
    }

    protected Port getPort(Port.Info info) throws LineUnavailableException {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetDataLine(): begin");
        }
        throw new IllegalArgumentException("this mixer does not support Ports");
    }

    private AudioFormat getSupportedSourceFormat(AudioFormat[] aFormats) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedSourceFormat(): begin");
        }
        AudioFormat format = null;
        for (int i = 0; i < aFormats.length; ++i) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getSupportedSourceFormat(): checking " + aFormats[i] + "...");
            }
            if (this.isSourceFormatSupported(aFormats[i])) {
                if (TDebug.TraceMixer) {
                    TDebug.out("TMixer.getSupportedSourceFormat(): ...supported");
                }
                format = aFormats[i];
                break;
            }
            if (!TDebug.TraceMixer) continue;
            TDebug.out("TMixer.getSupportedSourceFormat(): ...no luck");
        }
        if (format == null) {
            throw new IllegalArgumentException("no line matchine one of the passed formats");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedSourceFormat(): end");
        }
        return format;
    }

    private AudioFormat getSupportedTargetFormat(AudioFormat[] aFormats) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedTargetFormat(): begin");
        }
        AudioFormat format = null;
        for (int i = 0; i < aFormats.length; ++i) {
            if (TDebug.TraceMixer) {
                TDebug.out("TMixer.getSupportedTargetFormat(): checking " + aFormats[i] + " ...");
            }
            if (this.isTargetFormatSupported(aFormats[i])) {
                if (TDebug.TraceMixer) {
                    TDebug.out("TMixer.getSupportedTargetFormat(): ...supported");
                }
                format = aFormats[i];
                break;
            }
            if (!TDebug.TraceMixer) continue;
            TDebug.out("TMixer.getSupportedTargetFormat(): ...no luck");
        }
        if (format == null) {
            throw new IllegalArgumentException("no line matchine one of the passed formats");
        }
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSupportedTargetFormat(): end");
        }
        return format;
    }

    @Override
    public Line[] getSourceLines() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getSourceLines(): called");
        }
        return this.m_openSourceDataLines.toArray(EMPTY_LINE_ARRAY);
    }

    @Override
    public Line[] getTargetLines() {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.getTargetLines(): called");
        }
        return this.m_openTargetDataLines.toArray(EMPTY_LINE_ARRAY);
    }

    @Override
    public void synchronize(Line[] aLines, boolean bMaintainSync) {
        throw new IllegalArgumentException("synchronization not supported");
    }

    @Override
    public void unsynchronize(Line[] aLines) {
        throw new IllegalArgumentException("synchronization not supported");
    }

    @Override
    public boolean isSynchronizationSupported(Line[] aLines, boolean bMaintainSync) {
        return false;
    }

    protected boolean isSourceFormatSupported(AudioFormat format) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.isSourceFormatSupported(): format to test: " + format);
        }
        for (AudioFormat supportedFormat : this.m_supportedSourceFormats) {
            if (!AudioFormats.matches(supportedFormat, format)) continue;
            return true;
        }
        return false;
    }

    protected boolean isTargetFormatSupported(AudioFormat format) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.isTargetFormatSupported(): format to test: " + format);
        }
        for (AudioFormat supportedFormat : this.m_supportedTargetFormats) {
            if (!AudioFormats.matches(supportedFormat, format)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void registerOpenLine(Line line) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.registerOpenLine(): line to register: " + line);
        }
        if (line instanceof SourceDataLine) {
            Set<SourceDataLine> set = this.m_openSourceDataLines;
            synchronized (set) {
                this.m_openSourceDataLines.add((SourceDataLine)line);
            }
        }
        if (line instanceof TargetDataLine) {
            Set<SourceDataLine> set = this.m_openSourceDataLines;
            synchronized (set) {
                this.m_openTargetDataLines.add((TargetDataLine)line);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    void unregisterOpenLine(Line line) {
        if (TDebug.TraceMixer) {
            TDebug.out("TMixer.unregisterOpenLine(): line to unregister: " + line);
        }
        if (line instanceof SourceDataLine) {
            Set<SourceDataLine> set = this.m_openSourceDataLines;
            synchronized (set) {
                this.m_openSourceDataLines.remove((SourceDataLine)line);
            }
        }
        if (line instanceof TargetDataLine) {
            Set<TargetDataLine> set = this.m_openTargetDataLines;
            synchronized (set) {
                this.m_openTargetDataLines.remove((TargetDataLine)line);
            }
        }
    }
}

