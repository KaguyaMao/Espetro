/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import java.io.IOException;

public class IPMPControlBox
extends FullBox {
    private Descriptor toolList;
    private Descriptor[] ipmpDescriptors;

    public IPMPControlBox() {
        super("IPMP Control Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.toolList = Descriptor.createDescriptor(in);
        int count = in.readByte();
        this.ipmpDescriptors = new Descriptor[count];
        for (int i = 0; i < count; ++i) {
            this.ipmpDescriptors[i] = Descriptor.createDescriptor(in);
        }
    }

    public Descriptor getToolList() {
        return this.toolList;
    }

    public Descriptor[] getIPMPDescriptors() {
        return this.ipmpDescriptors;
    }
}

