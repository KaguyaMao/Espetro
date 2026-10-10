/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.od.Descriptor;
import java.io.IOException;

public class ObjectDescriptor
extends Descriptor {
    private int objectDescriptorID;
    private boolean urlPresent;
    private String url;

    @Override
    void decode(MP4InputStream in) throws IOException {
        int x = (int)in.readBytes(2);
        this.objectDescriptorID = x >> 6 & 0x3FF;
        boolean bl = this.urlPresent = (x >> 5 & 1) == 1;
        if (this.urlPresent) {
            this.url = in.readString(this.size - 2);
        }
        this.readChildren(in);
    }

    public int getObjectDescriptorID() {
        return this.objectDescriptorID;
    }

    public boolean isURLPresent() {
        return this.urlPresent;
    }

    public String getURL() {
        return this.url;
    }
}

