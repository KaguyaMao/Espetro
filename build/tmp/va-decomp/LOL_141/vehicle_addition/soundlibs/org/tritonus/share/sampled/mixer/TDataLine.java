/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TLine;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TMixer;
import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TDataLine
extends TLine
implements DataLine {
    private static final int DEFAULT_BUFFER_SIZE = 128000;
    private AudioFormat m_format;
    private int m_nBufferSize;
    private boolean m_bRunning;

    public TDataLine(TMixer mixer, DataLine.Info info) {
        super(mixer, info);
        this.init(info);
    }

    public TDataLine(TMixer mixer, DataLine.Info info, Collection<Control> controls) {
        super(mixer, info, controls);
        this.init(info);
    }

    private void init(DataLine.Info info) {
        this.m_format = null;
        this.m_nBufferSize = -1;
        this.setRunning(false);
    }

    @Override
    public void start() {
        if (TDebug.TraceSourceDataLine) {
            TDebug.out("TDataLine.start(): called");
        }
        this.setRunning(true);
    }

    @Override
    public void stop() {
        if (TDebug.TraceSourceDataLine) {
            TDebug.out("TDataLine.stop(): called");
        }
        this.setRunning(false);
    }

    @Override
    public boolean isRunning() {
        return this.m_bRunning;
    }

    protected void setRunning(boolean bRunning) {
        boolean bOldValue = this.isRunning();
        this.m_bRunning = bRunning;
        if (bOldValue != this.isRunning()) {
            if (this.isRunning()) {
                this.startImpl();
                this.notifyLineEvent(LineEvent.Type.START);
            } else {
                this.stopImpl();
                this.notifyLineEvent(LineEvent.Type.STOP);
            }
        }
    }

    protected void startImpl() {
    }

    protected void stopImpl() {
    }

    @Override
    public boolean isActive() {
        return this.isRunning();
    }

    @Override
    public AudioFormat getFormat() {
        return this.m_format;
    }

    protected void setFormat(AudioFormat format) {
        if (TDebug.TraceDataLine) {
            TDebug.out("TDataLine.setFormat(): setting: " + format);
        }
        this.m_format = format;
    }

    @Override
    public int getBufferSize() {
        return this.m_nBufferSize;
    }

    protected void setBufferSize(int nBufferSize) {
        if (TDebug.TraceDataLine) {
            TDebug.out("TDataLine.setBufferSize(): setting: " + nBufferSize);
        }
        this.m_nBufferSize = nBufferSize;
    }

    @Override
    public int getFramePosition() {
        return -1;
    }

    @Override
    public long getLongFramePosition() {
        return -1L;
    }

    @Override
    public long getMicrosecondPosition() {
        return (long)((float)this.getFramePosition() * this.getFormat().getFrameRate() * 1000000.0f);
    }

    @Override
    public float getLevel() {
        return -1.0f;
    }

    protected void checkOpen() {
        if (this.getFormat() == null) {
            throw new IllegalStateException("format must be specified");
        }
        if (this.getBufferSize() == -1) {
            this.setBufferSize(this.getDefaultBufferSize());
        }
    }

    protected int getDefaultBufferSize() {
        return 128000;
    }

    @Override
    protected void notifyLineEvent(LineEvent.Type type) {
        this.notifyLineEvent(new LineEvent(this, type, this.getFramePosition()));
    }
}

