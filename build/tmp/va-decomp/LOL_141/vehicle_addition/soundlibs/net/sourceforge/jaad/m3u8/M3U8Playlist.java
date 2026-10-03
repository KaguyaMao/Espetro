/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.m3u8;

import java.net.URI;
import java.util.List;

public class M3U8Playlist {
    private final int targetDurationMs;
    private final boolean isLive;
    private final long mediaSequence;
    private final List<URI> tsUrls;

    public M3U8Playlist(int targetDurationSec, boolean isLive, long mediaSequence, List<URI> tsUrls) {
        this.targetDurationMs = targetDurationSec > 0 ? targetDurationSec * 1000 : 5000;
        this.isLive = isLive;
        this.mediaSequence = mediaSequence;
        this.tsUrls = tsUrls == null ? List.of() : List.copyOf(tsUrls);
    }

    public int getTargetDurationMs() {
        return this.targetDurationMs;
    }

    public boolean isLive() {
        return this.isLive;
    }

    public long getMediaSequence() {
        return this.mediaSequence;
    }

    public List<URI> getNewTsUrls() {
        return this.tsUrls;
    }
}

