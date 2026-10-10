/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag;

import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.MP3Tag;
import LOL_141.vehicle_addition.soundlibs.javazoom.spi.mpeg.sampled.file.tag.TagParseListener;

public interface MP3MetadataParser {
    public void addTagParseListener(TagParseListener var1);

    public void removeTagParseListener(TagParseListener var1);

    public MP3Tag[] getTags();
}

