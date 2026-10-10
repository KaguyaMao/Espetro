/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.metadata;

public abstract class Metadata {
    public static final int METADATA_TYPE_STREAMINFO = 0;
    public static final int METADATA_TYPE_PADDING = 1;
    public static final int METADATA_TYPE_APPLICATION = 2;
    public static final int METADATA_TYPE_SEEKTABLE = 3;
    public static final int METADATA_TYPE_VORBIS_COMMENT = 4;
    public static final int METADATA_TYPE_CUESHEET = 5;
    public static final int METADATA_TYPE_PICTURE = 6;
    public static final int STREAM_METADATA_IS_LAST_LEN = 1;
    public static final int STREAM_METADATA_TYPE_LEN = 7;
    public static final int STREAM_METADATA_LENGTH_LEN = 24;
    protected boolean isLast;

    public Metadata(boolean isLast) {
        this.isLast = isLast;
    }

    public boolean isLast() {
        return this.isLast;
    }
}

