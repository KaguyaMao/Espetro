/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import java.io.IOException;

public class ObjectDescriptorBox
extends FullBox {
    private Descriptor objectDescriptor;

    public ObjectDescriptorBox() {
        super("Object Descriptor Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        this.objectDescriptor = Descriptor.createDescriptor(in);
    }

    public Descriptor getObjectDescriptor() {
        return this.objectDescriptor;
    }
}

