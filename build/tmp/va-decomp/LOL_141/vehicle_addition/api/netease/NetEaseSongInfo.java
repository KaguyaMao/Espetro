/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.api.netease;

public record NetEaseSongInfo(long id, String name, String artist, boolean vip, String url, long playlistId) {
    public NetEaseSongInfo(long id, String name, String artist, boolean vip, String url) {
        this(id, name, artist, vip, url, 0L);
    }

    public String displayName() {
        return this.name + (this.vip ? " [VIP]" : "");
    }
}

