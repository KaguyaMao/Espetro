/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.ObjectDescriptor;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class IPMPInfoBox
extends FullBox {
    private List<Descriptor> ipmpDescriptors;

    public IPMPInfoBox() {
        super("IPMP Info Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.ipmpDescriptors = new ArrayList<Descriptor>();
        while (this.getLeft(in) > 0L) {
            Descriptor desc = ObjectDescriptor.createDescriptor(in);
            this.ipmpDescriptors.add(desc);
        }
    }

    public List<Descriptor> getIPMPDescriptors() {
        return Collections.unmodifiableList(this.ipmpDescriptors);
    }
}

