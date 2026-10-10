/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.midi;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.midi.TSequencer;
import java.util.Collection;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Sequencer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TPreloadingSequencer
extends TSequencer {
    private static final int DEFAULT_LATENCY = 100;
    private int m_nLatency;
    private Thread m_loaderThread;

    protected TPreloadingSequencer(MidiDevice.Info info, Collection<Sequencer.SyncMode> masterSyncModes, Collection<Sequencer.SyncMode> slaveSyncModes) {
        super(info, masterSyncModes, slaveSyncModes);
        if (TDebug.TraceSequencer) {
            TDebug.out("TPreloadingSequencer.<init>(): begin");
        }
        this.m_nLatency = 100;
        if (TDebug.TraceSequencer) {
            TDebug.out("TPreloadingSequencer.<init>(): end");
        }
    }

    @Override
    public void setLatency(int nLatency) {
        this.m_nLatency = nLatency;
    }

    @Override
    public int getLatency() {
        return this.m_nLatency;
    }

    @Override
    protected void openImpl() {
        if (TDebug.TraceSequencer) {
            TDebug.out("AlsaSequencer.openImpl(): begin");
        }
    }

    public abstract void sendMessageTick(MidiMessage var1, long var2);
}

