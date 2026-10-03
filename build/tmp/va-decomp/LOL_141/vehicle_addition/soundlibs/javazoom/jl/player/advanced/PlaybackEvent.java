/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.advanced;

import LOL_141.vehicle_addition.soundlibs.javazoom.jl.player.advanced.AdvancedPlayer;

public class PlaybackEvent {
    public static int STOPPED = 1;
    public static int STARTED = 2;
    private AdvancedPlayer source;
    private int frame;
    private int id;

    public PlaybackEvent(AdvancedPlayer source, int id, int frame) {
        this.id = id;
        this.source = source;
        this.frame = frame;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getFrame() {
        return this.frame;
    }

    public void setFrame(int frame) {
        this.frame = frame;
    }

    public AdvancedPlayer getSource() {
        return this.source;
    }

    public void setSource(AdvancedPlayer source) {
        this.source = source;
    }
}

