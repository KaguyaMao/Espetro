/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac;

import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.AACException;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.aac.Speaker;
import LOL_141.vehicle_addition.soundlibs.net.sourceforge.jaad.util.Utils;
import java.util.List;

public enum ChannelConfiguration {
    NONE("No channel"),
    MONO("Mono", Speaker.FC),
    STEREO("Stereo", Speaker.FL, Speaker.FR),
    STEREO_PLUS_CENTER("Stereo+Center", Speaker.FC, Speaker.FL, Speaker.FR),
    STEREO_PLUS_CENTER_PLUS_REAR_MONO("Stereo+Center+Rear", Speaker.FC, Speaker.FL, Speaker.FR, Speaker.BC),
    FIVE("Five channels", Speaker.FC, Speaker.FL, Speaker.FR, Speaker.BL, Speaker.BR),
    FIVE_PLUS_ONE("Five channels+LF", Speaker.FC, Speaker.FL, Speaker.FR, Speaker.BL, Speaker.BR, Speaker.LFE),
    INVALID_SEVEN("Seven channels (invalid)", (Speaker)null),
    SEVEN_PLUS_ONE("Seven channels+LF", Speaker.FC, Speaker.FL, Speaker.FR, Speaker.BL, Speaker.BR, Speaker.FLC, Speaker.FRC, Speaker.LFE);

    static final ChannelConfiguration CHANNEL_CONFIG_UNSUPPORTED;
    static final List<ChannelConfiguration> CONFIGURATIONS;
    private final String descr;
    private final List<Speaker> speakers;

    public static ChannelConfiguration forInt(int i) {
        if (i >= 7) {
            ++i;
        }
        return CONFIGURATIONS.get(i);
    }

    public static ChannelConfiguration forChannelCount(int n) {
        if (n == 7) {
            throw new AACException("invalid channel configuration: 8");
        }
        return CONFIGURATIONS.get(n);
    }

    private ChannelConfiguration(String descr) {
        this.descr = descr;
        this.speakers = Utils.listOf(new Speaker[0]);
    }

    private ChannelConfiguration(String descr, Speaker ... speakes) {
        this.descr = descr;
        this.speakers = Utils.listOf(speakes);
        if (this.speakers.size() != this.ordinal()) {
            throw new IllegalArgumentException("invalid speaker count");
        }
    }

    private ChannelConfiguration(String descr, Speaker speaker) {
        this.descr = descr;
        this.speakers = speaker == null ? null : Utils.listOf(speaker);
    }

    public int getChannelCount() {
        return this.ordinal();
    }

    public List<Speaker> getSpeakers() {
        return this.speakers;
    }

    public String getDescription() {
        return this.descr;
    }

    public String toString() {
        return this.descr;
    }

    static {
        CHANNEL_CONFIG_UNSUPPORTED = null;
        CONFIGURATIONS = Utils.listOf(ChannelConfiguration.values());
    }
}

