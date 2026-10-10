/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi;

import LOL_141.vehicle_addition.soundlibs.org.jflac.metadata.StreamInfo;
import LOL_141.vehicle_addition.soundlibs.org.jflac.sound.spi.FlacEncoding;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFormat;

public class FlacAudioFormat
extends AudioFormat {
    public static final String KEY_FRAMESIZE_MIN = "framesize_min";
    public static final String KEY_FRAMESIZE_MAX = "framesize_max";
    public static final String KEY_BLOCKSIZE_MIN = "blocksize_min";
    public static final String KEY_BLOCKSIZE_MAX = "blocksize_max";
    private Map<String, Object> props = new HashMap<String, Object>();

    public FlacAudioFormat(StreamInfo streamInfo) {
        super(FlacEncoding.FLAC, streamInfo.getSampleRate(), streamInfo.getBitsPerSample(), streamInfo.getChannels(), -1, -1.0f, false);
        this.props.put(KEY_FRAMESIZE_MIN, streamInfo.getMinFrameSize());
        this.props.put(KEY_FRAMESIZE_MAX, streamInfo.getMaxFrameSize());
        this.props.put(KEY_BLOCKSIZE_MIN, streamInfo.getMinBlockSize());
        this.props.put(KEY_BLOCKSIZE_MAX, streamInfo.getMaxBlockSize());
    }

    @Override
    public Map<String, Object> properties() {
        if (this.props == null) {
            return Collections.emptyMap();
        }
        return Collections.unmodifiableMap(new HashMap<String, Object>(this.props));
    }

    @Override
    public Object getProperty(String key) {
        if (this.props == null) {
            return null;
        }
        return this.props.get(key);
    }
}

