/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.midi;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.ArraySet;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.midi.TMidiDevice;
import java.io.IOException;
import java.io.InputStream;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Sequencer;
import javax.sound.midi.ShortMessage;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TSequencer
extends TMidiDevice
implements Sequencer {
    private static final float MPQ_BPM_FACTOR = 6.0E7f;
    private static final Sequencer.SyncMode[] EMPTY_SYNCMODE_ARRAY = new Sequencer.SyncMode[0];
    private boolean m_bRunning = false;
    private Sequence m_sequence = null;
    private Set<MetaEventListener> m_metaListeners = new ArraySet<MetaEventListener>();
    private Set<ControllerEventListener>[] m_aControllerListeners = new Set[128];
    private float m_fNominalTempoInMPQ;
    private float m_fTempoFactor;
    private Collection<Sequencer.SyncMode> m_masterSyncModes;
    private Collection<Sequencer.SyncMode> m_slaveSyncModes;
    private Sequencer.SyncMode m_masterSyncMode;
    private Sequencer.SyncMode m_slaveSyncMode;
    private BitSet m_muteBitSet;
    private BitSet m_soloBitSet;
    private BitSet m_enabledBitSet;
    private long m_lLoopStartPoint;
    private long m_lLoopEndPoint;
    private int m_nLoopCount;

    protected TSequencer(MidiDevice.Info info, Collection<Sequencer.SyncMode> masterSyncModes, Collection<Sequencer.SyncMode> slaveSyncModes) {
        super(info);
        this.setTempoFactor(1.0f);
        this.setTempoInMPQ(500000.0f);
        this.m_masterSyncModes = masterSyncModes;
        this.m_slaveSyncModes = slaveSyncModes;
        if (this.getMasterSyncModes().length > 0) {
            this.m_masterSyncMode = this.getMasterSyncModes()[0];
        }
        if (this.getSlaveSyncModes().length > 0) {
            this.m_slaveSyncMode = this.getSlaveSyncModes()[0];
        }
        this.m_muteBitSet = new BitSet();
        this.m_soloBitSet = new BitSet();
        this.m_enabledBitSet = new BitSet();
        this.updateEnabled();
        this.setLoopStartPoint(0L);
        this.setLoopEndPoint(-1L);
        this.setLoopCount(0);
    }

    @Override
    public void setSequence(Sequence sequence) throws InvalidMidiDataException {
        if (this.getSequence() != sequence) {
            this.m_sequence = sequence;
            this.setSequenceImpl();
            this.setTempoFactor(1.0f);
        }
    }

    protected void setSequenceImpl() {
    }

    @Override
    public void setSequence(InputStream inputStream) throws InvalidMidiDataException, IOException {
        Sequence sequence = MidiSystem.getSequence(inputStream);
        this.setSequence(sequence);
    }

    @Override
    public Sequence getSequence() {
        return this.m_sequence;
    }

    @Override
    public void setLoopStartPoint(long lTick) {
        this.m_lLoopStartPoint = lTick;
    }

    @Override
    public long getLoopStartPoint() {
        return this.m_lLoopStartPoint;
    }

    @Override
    public void setLoopEndPoint(long lTick) {
        this.m_lLoopEndPoint = lTick;
    }

    @Override
    public long getLoopEndPoint() {
        return this.m_lLoopEndPoint;
    }

    @Override
    public void setLoopCount(int nLoopCount) {
        this.m_nLoopCount = nLoopCount;
    }

    @Override
    public int getLoopCount() {
        return this.m_nLoopCount;
    }

    @Override
    public synchronized void start() {
        this.checkOpen();
        if (!this.isRunning()) {
            this.m_bRunning = true;
            this.startImpl();
        }
    }

    protected void startImpl() {
    }

    @Override
    public synchronized void stop() {
        this.checkOpen();
        if (this.isRunning()) {
            this.stopImpl();
            this.m_bRunning = false;
        }
    }

    protected void stopImpl() {
    }

    @Override
    public synchronized boolean isRunning() {
        return this.m_bRunning;
    }

    protected void checkOpen() {
        if (!this.isOpen()) {
            throw new IllegalStateException("Sequencer is not open");
        }
    }

    protected int getResolution() {
        Sequence sequence = this.getSequence();
        int nResolution = sequence != null ? sequence.getResolution() : 1;
        return nResolution;
    }

    protected void setRealTempo() {
        float fTempoFactor = this.getTempoFactor();
        if (fTempoFactor == 0.0f) {
            fTempoFactor = 0.01f;
        }
        float fRealTempo = this.getTempoInMPQ() / fTempoFactor;
        if (TDebug.TraceSequencer) {
            TDebug.out("TSequencer.setRealTempo(): real tempo: " + fRealTempo);
        }
        this.setTempoImpl(fRealTempo);
    }

    @Override
    public float getTempoInBPM() {
        float fBPM = 6.0E7f / this.getTempoInMPQ();
        return fBPM;
    }

    @Override
    public void setTempoInBPM(float fBPM) {
        float fMPQ = 6.0E7f / fBPM;
        this.setTempoInMPQ(fMPQ);
    }

    @Override
    public float getTempoInMPQ() {
        return this.m_fNominalTempoInMPQ;
    }

    @Override
    public void setTempoInMPQ(float fMPQ) {
        this.m_fNominalTempoInMPQ = fMPQ;
        this.setRealTempo();
    }

    @Override
    public void setTempoFactor(float fFactor) {
        this.m_fTempoFactor = fFactor;
        this.setRealTempo();
    }

    @Override
    public float getTempoFactor() {
        return this.m_fTempoFactor;
    }

    protected abstract void setTempoImpl(float var1);

    @Override
    public long getTickLength() {
        long lLength = 0L;
        if (this.getSequence() != null) {
            lLength = this.getSequence().getTickLength();
        }
        return lLength;
    }

    @Override
    public long getMicrosecondLength() {
        long lLength = 0L;
        if (this.getSequence() != null) {
            lLength = this.getSequence().getMicrosecondLength();
        }
        return lLength;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean addMetaEventListener(MetaEventListener listener) {
        Set<MetaEventListener> set = this.m_metaListeners;
        synchronized (set) {
            return this.m_metaListeners.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void removeMetaEventListener(MetaEventListener listener) {
        Set<MetaEventListener> set = this.m_metaListeners;
        synchronized (set) {
            this.m_metaListeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Iterator<MetaEventListener> getMetaEventListeners() {
        Set<MetaEventListener> set = this.m_metaListeners;
        synchronized (set) {
            return this.m_metaListeners.iterator();
        }
    }

    protected void sendMetaMessage(MetaMessage message) {
        Iterator<MetaEventListener> iterator = this.getMetaEventListeners();
        while (iterator.hasNext()) {
            MetaEventListener metaEventListener = iterator.next();
            MetaMessage copiedMessage = (MetaMessage)message.clone();
            metaEventListener.meta(copiedMessage);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] addControllerEventListener(ControllerEventListener listener, int[] anControllers) {
        Set<ControllerEventListener>[] setArray = this.m_aControllerListeners;
        synchronized (this.m_aControllerListeners) {
            if (anControllers == null) {
                for (int i = 0; i < 128; ++i) {
                    this.addControllerListener(i, listener);
                }
            } else {
                for (int i = 0; i < anControllers.length; ++i) {
                    this.addControllerListener(anControllers[i], listener);
                }
            }
            // ** MonitorExit[var3_3] (shouldn't be in output)
            return this.getListenedControllers(listener);
        }
    }

    private void addControllerListener(int i, ControllerEventListener listener) {
        if (this.m_aControllerListeners[i] == null) {
            this.m_aControllerListeners[i] = new ArraySet<ControllerEventListener>();
        }
        this.m_aControllerListeners[i].add(listener);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public int[] removeControllerEventListener(ControllerEventListener listener, int[] anControllers) {
        Set<ControllerEventListener>[] setArray = this.m_aControllerListeners;
        synchronized (this.m_aControllerListeners) {
            if (anControllers == null) {
                for (int i = 0; i < 128; ++i) {
                    this.removeControllerListener(i, listener);
                }
            } else {
                for (int i = 0; i < anControllers.length; ++i) {
                    this.removeControllerListener(anControllers[i], listener);
                }
            }
            // ** MonitorExit[var3_3] (shouldn't be in output)
            return this.getListenedControllers(listener);
        }
    }

    private void removeControllerListener(int i, ControllerEventListener listener) {
        if (this.m_aControllerListeners[i] != null) {
            this.m_aControllerListeners[i].add(listener);
        }
    }

    private int[] getListenedControllers(ControllerEventListener listener) {
        int[] anControllers = new int[128];
        int nIndex = 0;
        for (int nController = 0; nController < 128; ++nController) {
            if (this.m_aControllerListeners[nController] == null || !this.m_aControllerListeners[nController].contains(listener)) continue;
            anControllers[nIndex] = nController;
            ++nIndex;
        }
        int[] anResultControllers = new int[nIndex];
        System.arraycopy(anControllers, 0, anResultControllers, 0, nIndex);
        return anResultControllers;
    }

    protected void sendControllerEvent(ShortMessage message) {
        int nController = message.getData1();
        if (this.m_aControllerListeners[nController] != null) {
            for (ControllerEventListener controllerEventListener : this.m_aControllerListeners[nController]) {
                ShortMessage copiedMessage = (ShortMessage)message.clone();
                controllerEventListener.controlChange(copiedMessage);
            }
        }
    }

    protected void notifyListeners(MidiMessage message) {
        if (message instanceof MetaMessage) {
            this.sendMetaMessage((MetaMessage)message);
        } else if (message instanceof ShortMessage && ((ShortMessage)message).getCommand() == 176) {
            this.sendControllerEvent((ShortMessage)message);
        }
    }

    @Override
    public Sequencer.SyncMode getMasterSyncMode() {
        return this.m_masterSyncMode;
    }

    @Override
    public void setMasterSyncMode(Sequencer.SyncMode syncMode) {
        if (this.m_masterSyncModes.contains(syncMode)) {
            if (!this.getMasterSyncMode().equals(syncMode)) {
                this.m_masterSyncMode = syncMode;
                this.setMasterSyncModeImpl(syncMode);
            }
        } else {
            throw new IllegalArgumentException("sync mode not allowed: " + syncMode);
        }
    }

    protected void setMasterSyncModeImpl(Sequencer.SyncMode syncMode) {
    }

    @Override
    public Sequencer.SyncMode[] getMasterSyncModes() {
        Sequencer.SyncMode[] syncModes = this.m_masterSyncModes.toArray(EMPTY_SYNCMODE_ARRAY);
        return syncModes;
    }

    @Override
    public Sequencer.SyncMode getSlaveSyncMode() {
        return this.m_slaveSyncMode;
    }

    @Override
    public void setSlaveSyncMode(Sequencer.SyncMode syncMode) {
        if (this.m_slaveSyncModes.contains(syncMode)) {
            if (!this.getSlaveSyncMode().equals(syncMode)) {
                this.m_slaveSyncMode = syncMode;
                this.setSlaveSyncModeImpl(syncMode);
            }
        } else {
            throw new IllegalArgumentException("sync mode not allowed: " + syncMode);
        }
    }

    protected void setSlaveSyncModeImpl(Sequencer.SyncMode syncMode) {
    }

    @Override
    public Sequencer.SyncMode[] getSlaveSyncModes() {
        Sequencer.SyncMode[] syncModes = this.m_slaveSyncModes.toArray(EMPTY_SYNCMODE_ARRAY);
        return syncModes;
    }

    @Override
    public boolean getTrackSolo(int nTrack) {
        boolean bSoloed = false;
        if (this.getSequence() != null && nTrack < this.getSequence().getTracks().length) {
            bSoloed = this.m_soloBitSet.get(nTrack);
        }
        return bSoloed;
    }

    @Override
    public void setTrackSolo(int nTrack, boolean bSolo) {
        boolean bOldState;
        if (this.getSequence() != null && nTrack < this.getSequence().getTracks().length && bSolo != (bOldState = this.m_soloBitSet.get(nTrack))) {
            if (bSolo) {
                this.m_soloBitSet.set(nTrack);
            } else {
                this.m_soloBitSet.clear(nTrack);
            }
            this.updateEnabled();
            this.setTrackSoloImpl(nTrack, bSolo);
        }
    }

    protected void setTrackSoloImpl(int nTrack, boolean bSolo) {
    }

    @Override
    public boolean getTrackMute(int nTrack) {
        boolean bMuted = false;
        if (this.getSequence() != null && nTrack < this.getSequence().getTracks().length) {
            bMuted = this.m_muteBitSet.get(nTrack);
        }
        return bMuted;
    }

    @Override
    public void setTrackMute(int nTrack, boolean bMute) {
        boolean bOldState;
        if (this.getSequence() != null && nTrack < this.getSequence().getTracks().length && bMute != (bOldState = this.m_muteBitSet.get(nTrack))) {
            if (bMute) {
                this.m_muteBitSet.set(nTrack);
            } else {
                this.m_muteBitSet.clear(nTrack);
            }
            this.updateEnabled();
            this.setTrackMuteImpl(nTrack, bMute);
        }
    }

    protected void setTrackMuteImpl(int nTrack, boolean bMute) {
    }

    private void updateEnabled() {
        int i;
        boolean bSoloExists;
        BitSet oldEnabledBitSet = (BitSet)this.m_enabledBitSet.clone();
        boolean bl = bSoloExists = this.m_soloBitSet.length() > 0;
        if (bSoloExists) {
            this.m_enabledBitSet = (BitSet)this.m_soloBitSet.clone();
        } else {
            for (i = 0; i < this.m_muteBitSet.size(); ++i) {
                if (this.m_muteBitSet.get(i)) {
                    this.m_enabledBitSet.clear(i);
                    continue;
                }
                this.m_enabledBitSet.set(i);
            }
        }
        oldEnabledBitSet.xor(this.m_enabledBitSet);
        for (i = 0; i < oldEnabledBitSet.size(); ++i) {
            if (!oldEnabledBitSet.get(i)) continue;
            this.setTrackEnabledImpl(i, this.m_enabledBitSet.get(i));
        }
    }

    protected void setTrackEnabledImpl(int nTrack, boolean bEnabled) {
    }

    protected boolean isTrackEnabled(int nTrack) {
        return this.m_enabledBitSet.get(nTrack);
    }

    public void setLatency(int nMilliseconds) {
    }

    public int getLatency() {
        return -1;
    }
}

