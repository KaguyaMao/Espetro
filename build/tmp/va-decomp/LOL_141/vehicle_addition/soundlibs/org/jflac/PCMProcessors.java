/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac;

import LOL_141.vehicle_addition.soundlibs.org.jflac.PCMProcessor;
import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.util.ByteData;
import java.util.HashSet;

class PCMProcessors
implements PCMProcessor {
    private HashSet pcmProcessors = new HashSet();

    PCMProcessors() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addPCMProcessor(PCMProcessor processor) {
        HashSet hashSet = this.pcmProcessors;
        synchronized (hashSet) {
            this.pcmProcessors.add(processor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void removePCMProcessor(PCMProcessor processor) {
        HashSet hashSet = this.pcmProcessors;
        synchronized (hashSet) {
            this.pcmProcessors.remove(processor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processStreamInfo(StreamInfo info) {
        HashSet hashSet = this.pcmProcessors;
        synchronized (hashSet) {
            for (PCMProcessor processor : this.pcmProcessors) {
                processor.processStreamInfo(info);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void processPCM(ByteData pcm) {
        HashSet hashSet = this.pcmProcessors;
        synchronized (hashSet) {
            for (PCMProcessor processor : this.pcmProcessors) {
                processor.processPCM(pcm);
            }
        }
    }
}

