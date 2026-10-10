/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.mixer.TDataLine;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TClip
extends TDataLine
implements Clip {
    public TClip(DataLine.Info info) {
        super(null, info);
    }

    public TClip(DataLine.Info info, Collection<Control> controls) {
        super(null, info, controls);
    }

    @Override
    public void open(AudioFormat audioFormat, byte[] abData, int nOffset, int nLength) throws LineUnavailableException {
        ByteArrayInputStream bais = new ByteArrayInputStream(abData, nOffset, nLength);
        AudioInputStream audioInputStream = new AudioInputStream(bais, audioFormat, -1L);
        try {
            this.open(audioInputStream);
        }
        catch (IOException e) {
            if (TDebug.TraceAllExceptions) {
                TDebug.out(e);
            }
            throw new LineUnavailableException("IOException occured");
        }
    }

    @Override
    public void open(AudioInputStream audioInputStream) throws LineUnavailableException, IOException {
        AudioFormat audioFormat = audioInputStream.getFormat();
        DataLine.Info info = new DataLine.Info(Clip.class, audioFormat, -1);
        this.setLineInfo(info);
    }

    @Override
    public int getFrameLength() {
        return -1;
    }

    @Override
    public long getMicrosecondLength() {
        return -1L;
    }

    @Override
    public void setFramePosition(int nPosition) {
    }

    @Override
    public void setMicrosecondPosition(long lPosition) {
    }

    @Override
    public int getFramePosition() {
        return -1;
    }

    @Override
    public long getMicrosecondPosition() {
        return -1L;
    }

    @Override
    public void setLoopPoints(int nStart, int nEnd) {
    }

    @Override
    public void loop(int nCount) {
        if (TDebug.TraceClip) {
            TDebug.out("TClip.loop(int): called; count = " + nCount);
        }
        if (nCount == 0) {
            if (TDebug.TraceClip) {
                TDebug.out("TClip.loop(int): starting sample (once)");
            }
        } else if (TDebug.TraceClip) {
            TDebug.out("TClip.loop(int): starting sample (forever)");
        }
    }

    @Override
    public void flush() {
    }

    @Override
    public void drain() {
    }

    @Override
    public void close() {
    }

    @Override
    public void open() {
    }

    @Override
    public void start() {
        if (TDebug.TraceClip) {
            TDebug.out("TClip.start(): called");
        }
        if (TDebug.TraceClip) {
            TDebug.out("TClip.start(): calling 'loop(0)' [hack]");
        }
        this.loop(0);
    }

    @Override
    public void stop() {
    }

    @Override
    public int available() {
        return -1;
    }
}

