/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.midi;

import LOL_141.vehicle_addition.soundlibs.org.tritonus.share.TDebug;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class TMidiDevice
implements MidiDevice {
    private MidiDevice.Info m_info;
    private boolean m_bDeviceOpen;
    private boolean m_bUseTransmitter;
    private boolean m_bUseReceiver;
    private List<Receiver> m_receivers;
    private List<Transmitter> m_transmitters;

    public TMidiDevice(MidiDevice.Info info) {
        this(info, true, true);
    }

    public TMidiDevice(MidiDevice.Info info, boolean bUseTransmitter, boolean bUseReceiver) {
        this.m_info = info;
        this.m_bUseTransmitter = bUseTransmitter;
        this.m_bUseReceiver = bUseReceiver;
        this.m_bDeviceOpen = false;
        this.m_receivers = new ArrayList<Receiver>();
        this.m_transmitters = new ArrayList<Transmitter>();
    }

    @Override
    public MidiDevice.Info getDeviceInfo() {
        return this.m_info;
    }

    @Override
    public synchronized void open() throws MidiUnavailableException {
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.open(): begin");
        }
        if (!this.isOpen()) {
            this.openImpl();
            this.m_bDeviceOpen = true;
        }
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.open(): end");
        }
    }

    protected void openImpl() throws MidiUnavailableException {
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.openImpl(): begin");
        }
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.openImpl(): end");
        }
    }

    @Override
    public synchronized void close() {
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.close(): begin");
        }
        if (this.isOpen()) {
            this.closeImpl();
            this.m_bDeviceOpen = false;
        }
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.close(): end");
        }
    }

    protected void closeImpl() {
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.closeImpl(): begin");
        }
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.closeImpl(): end");
        }
    }

    @Override
    public boolean isOpen() {
        return this.m_bDeviceOpen;
    }

    protected boolean getUseTransmitter() {
        return this.m_bUseTransmitter;
    }

    protected boolean getUseReceiver() {
        return this.m_bUseReceiver;
    }

    @Override
    public long getMicrosecondPosition() {
        return -1L;
    }

    @Override
    public int getMaxReceivers() {
        int nMaxReceivers = 0;
        if (this.getUseReceiver()) {
            nMaxReceivers = -1;
        }
        return nMaxReceivers;
    }

    @Override
    public int getMaxTransmitters() {
        int nMaxTransmitters = 0;
        if (this.getUseTransmitter()) {
            nMaxTransmitters = -1;
        }
        return nMaxTransmitters;
    }

    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        if (!this.getUseReceiver()) {
            throw new MidiUnavailableException("Receivers are not supported by this device");
        }
        return new TReceiver();
    }

    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        if (!this.getUseTransmitter()) {
            throw new MidiUnavailableException("Transmitters are not supported by this device");
        }
        return new TTransmitter();
    }

    @Override
    public List<Receiver> getReceivers() {
        return Collections.unmodifiableList(this.m_receivers);
    }

    @Override
    public List<Transmitter> getTransmitters() {
        return Collections.unmodifiableList(this.m_transmitters);
    }

    protected void receive(MidiMessage message, long lTimeStamp) {
        if (TDebug.TraceMidiDevice) {
            TDebug.out("### [should be overridden] TMidiDevice.receive(): message " + message);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addReceiver(Receiver receiver) {
        List<Receiver> list = this.m_receivers;
        synchronized (list) {
            this.m_receivers.add(receiver);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeReceiver(Receiver receiver) {
        List<Receiver> list = this.m_receivers;
        synchronized (list) {
            this.m_receivers.remove(receiver);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void addTransmitter(Transmitter transmitter) {
        List<Transmitter> list = this.m_transmitters;
        synchronized (list) {
            this.m_transmitters.add(transmitter);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void removeTransmitter(Transmitter transmitter) {
        List<Transmitter> list = this.m_transmitters;
        synchronized (list) {
            this.m_transmitters.remove(transmitter);
        }
    }

    protected void sendImpl(MidiMessage message, long lTimeStamp) {
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.sendImpl(): begin");
        }
        for (TTransmitter tTransmitter : this.m_transmitters) {
            MidiMessage copiedMessage = null;
            if (message instanceof MetaMessage) {
                MetaMessage metaMessage;
                block10: {
                    MetaMessage origMessage = (MetaMessage)message;
                    metaMessage = new MetaMessage();
                    try {
                        metaMessage.setMessage(origMessage.getType(), origMessage.getData(), origMessage.getData().length);
                    }
                    catch (InvalidMidiDataException e) {
                        if (!TDebug.TraceAllExceptions) break block10;
                        TDebug.out(e);
                    }
                }
                copiedMessage = metaMessage;
            } else {
                copiedMessage = (MidiMessage)message.clone();
            }
            if (message instanceof MetaMessage) {
                if (TDebug.TraceMidiDevice) {
                    TDebug.out("TMidiDevice.sendImpl(): MetaMessage.getData().length (original): " + ((MetaMessage)message).getData().length);
                }
                if (TDebug.TraceMidiDevice) {
                    TDebug.out("TMidiDevice.sendImpl(): MetaMessage.getData().length (cloned): " + ((MetaMessage)copiedMessage).getData().length);
                }
            }
            tTransmitter.send(copiedMessage, lTimeStamp);
        }
        if (TDebug.TraceMidiDevice) {
            TDebug.out("TMidiDevice.sendImpl(): end");
        }
    }

    public static class Info
    extends MidiDevice.Info {
        public Info(String a, String b, String c, String d) {
            super(a, b, c, d);
        }
    }

    public class TTransmitter
    implements Transmitter {
        private boolean m_bOpen = true;
        private Receiver m_receiver;

        public TTransmitter() {
            TMidiDevice.this.addTransmitter(this);
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        public void setReceiver(Receiver receiver) {
            TTransmitter tTransmitter = this;
            synchronized (tTransmitter) {
                this.m_receiver = receiver;
            }
        }

        public Receiver getReceiver() {
            return this.m_receiver;
        }

        public void send(MidiMessage message, long lTimeStamp) {
            if (this.getReceiver() != null && this.m_bOpen) {
                this.getReceiver().send(message, lTimeStamp);
            }
        }

        public void close() {
            TMidiDevice.this.removeTransmitter(this);
            this.m_bOpen = false;
        }
    }

    public class TReceiver
    implements Receiver {
        private boolean m_bOpen;

        public TReceiver() {
            TMidiDevice.this.addReceiver(this);
            this.m_bOpen = true;
        }

        protected boolean isOpen() {
            return this.m_bOpen;
        }

        public void send(MidiMessage message, long lTimeStamp) {
            if (TDebug.TraceMidiDevice) {
                TDebug.out("TMidiDevice.TReceiver.send(): message " + message);
            }
            if (!this.m_bOpen) {
                throw new IllegalStateException("receiver is not open");
            }
            TMidiDevice.this.receive(message, lTimeStamp);
        }

        public void close() {
            TMidiDevice.this.removeReceiver(this);
            this.m_bOpen = false;
        }
    }
}

