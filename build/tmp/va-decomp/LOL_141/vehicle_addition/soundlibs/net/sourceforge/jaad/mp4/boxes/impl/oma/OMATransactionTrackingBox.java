/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.oma;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;

public class OMATransactionTrackingBox
extends FullBox {
    private String transactionID;

    public OMATransactionTrackingBox() {
        super("OMA DRM Transaction Tracking Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.transactionID = in.readString(16);
    }

    public String getTransactionID() {
        return this.transactionID;
    }
}

