/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.Decoder;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.decoder.JavaLayerException;
import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.AudioDevice;

public abstract class AudioDeviceBase
implements AudioDevice {
    private boolean open = false;
    private Decoder decoder = null;

    public synchronized void open(Decoder decoder) throws JavaLayerException {
        if (!this.isOpen()) {
            this.decoder = decoder;
            this.openImpl();
            this.setOpen(true);
        }
    }

    protected void openImpl() throws JavaLayerException {
    }

    protected void setOpen(boolean open) {
        this.open = open;
    }

    public synchronized boolean isOpen() {
        return this.open;
    }

    public synchronized void close() {
        if (this.isOpen()) {
            this.closeImpl();
            this.setOpen(false);
            this.decoder = null;
        }
    }

    protected void closeImpl() {
    }

    public void write(short[] samples, int offs, int len) throws JavaLayerException {
        if (this.isOpen()) {
            this.writeImpl(samples, offs, len);
        }
    }

    protected void writeImpl(short[] samples, int offs, int len) throws JavaLayerException {
    }

    public void flush() {
        if (this.isOpen()) {
            this.flushImpl();
        }
    }

    protected void flushImpl() {
    }

    protected Decoder getDecoder() {
        return this.decoder;
    }
}

