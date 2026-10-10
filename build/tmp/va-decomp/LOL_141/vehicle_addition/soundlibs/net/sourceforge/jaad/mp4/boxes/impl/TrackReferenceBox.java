/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.BoxImpl;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackReferenceBox
extends BoxImpl {
    private String referenceType;
    private List<Long> trackIDs = new ArrayList<Long>();

    public TrackReferenceBox() {
        super("Track Reference Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        this.referenceType = in.readString(4);
        while (this.getLeft(in) > 3L) {
            this.trackIDs.add(in.readBytes(4));
        }
    }

    public String getReferenceType() {
        return this.referenceType;
    }

    public List<Long> getTrackIDs() {
        return Collections.unmodifiableList(this.trackIDs);
    }
}

