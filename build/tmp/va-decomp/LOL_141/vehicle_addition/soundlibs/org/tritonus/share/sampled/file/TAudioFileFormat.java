/*
 * Decompiled with CFR 0.152.
 */
package LOL_141.vehicle_addition.soundlibs.org.tritonus.share.sampled.file;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class TAudioFileFormat
extends AudioFileFormat {
    private Map<String, Object> m_properties;
    private Map<String, Object> m_unmodifiableProperties;

    public TAudioFileFormat(AudioFileFormat.Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes) {
        super(type, nLengthInBytes, audioFormat, nLengthInFrames);
    }

    public TAudioFileFormat(AudioFileFormat.Type type, AudioFormat audioFormat, int nLengthInFrames, int nLengthInBytes, Map<String, Object> properties) {
        super(type, nLengthInBytes, audioFormat, nLengthInFrames);
        this.initMaps(properties);
    }

    private void initMaps(Map<String, Object> properties) {
        this.m_properties = new HashMap<String, Object>();
        this.m_properties.putAll(properties);
        this.m_unmodifiableProperties = Collections.unmodifiableMap(this.m_properties);
    }

    @Override
    public Map<String, Object> properties() {
        return this.m_unmodifiableProperties;
    }

    protected void setProperty(String key, Object value) {
        this.m_properties.put(key, value);
    }
}

