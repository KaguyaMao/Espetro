/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag;

import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.TagParseEvent;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.TagParseListener;
import java.util.ArrayList;

public class MP3TagParseSupport {
    ArrayList tagParseListeners = new ArrayList();

    public void addTagParseListener(TagParseListener tpl) {
        this.tagParseListeners.add(tpl);
    }

    public void removeTagParseListener(TagParseListener tpl) {
        this.tagParseListeners.add(tpl);
    }

    public void fireTagParseEvent(TagParseEvent tpe) {
        for (int i = 0; i < this.tagParseListeners.size(); ++i) {
            TagParseListener l = (TagParseListener)this.tagParseListeners.get(i);
            l.tagParsed(tpe);
        }
    }

    public void fireTagParsed(Object source, MP3Tag tag) {
        this.fireTagParseEvent(new TagParseEvent(source, tag));
    }
}

