/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag;

import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.StringableTag;

public class IcyTag
extends MP3Tag
implements StringableTag {
    public IcyTag(String name, String stringValue) {
        super(name, stringValue);
    }

    public String getValueAsString() {
        return (String)this.getValue();
    }
}

