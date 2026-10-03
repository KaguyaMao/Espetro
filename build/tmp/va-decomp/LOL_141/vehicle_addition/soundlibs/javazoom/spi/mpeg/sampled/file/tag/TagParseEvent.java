/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag;

import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import java.util.EventObject;

public class TagParseEvent
extends EventObject {
    protected MP3Tag tag;

    public TagParseEvent(Object source, MP3Tag tag) {
        super(source);
        this.tag = tag;
    }

    public MP3Tag getTag() {
        return this.tag;
    }
}

