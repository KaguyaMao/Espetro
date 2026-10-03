/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.impl.fd;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.MP4InputStream;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.mp4.boxes.FullBox;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class GroupIDToNameBox
extends FullBox {
    private final Map<Long, String> map = new HashMap<Long, String>();

    public GroupIDToNameBox() {
        super("Group ID To Name Box");
    }

    @Override
    public void decode(MP4InputStream in) throws IOException {
        super.decode(in);
        int entryCount = (int)in.readBytes(2);
        for (int i = 0; i < entryCount; ++i) {
            long id = in.readBytes(4);
            String name = in.readUTFString((int)this.getLeft(in), "UTF-8");
            this.map.put(id, name);
        }
    }

    public Map<Long, String> getMap() {
        return this.map;
    }
}

