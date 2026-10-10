/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

import LOL_141.vehicle_addition.soundlibs.org.jflac.FrameListener;
import LOL_141.vehicle_addition.soundlibs.org.jflac.frame.Frame;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.Metadata;
import java.util.HashSet;

class FrameListeners
implements FrameListener {
    private HashSet frameListeners = new HashSet();

    FrameListeners() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addFrameListener(FrameListener listener) {
        HashSet hashSet = this.frameListeners;
        synchronized (hashSet) {
            this.frameListeners.add(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removeFrameListener(FrameListener listener) {
        HashSet hashSet = this.frameListeners;
        synchronized (hashSet) {
            this.frameListeners.remove(listener);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processMetadata(Metadata metadata) {
        HashSet hashSet = this.frameListeners;
        synchronized (hashSet) {
            for (FrameListener listener : this.frameListeners) {
                listener.processMetadata(metadata);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processFrame(Frame frame) {
        HashSet hashSet = this.frameListeners;
        synchronized (hashSet) {
            for (FrameListener listener : this.frameListeners) {
                listener.processFrame(frame);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processError(String msg) {
        HashSet hashSet = this.frameListeners;
        synchronized (hashSet) {
            for (FrameListener listener : this.frameListeners) {
                listener.processError(msg);
            }
        }
    }
}

