/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 */
package cc.sighs.auratip.api.util;

import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nullable;

public record Params(Map<String, Dynamic<?>> raw) {
    public Params(@Nullable Map<String, Dynamic<?>> raw) {
        this.raw = raw == null || raw.isEmpty() ? Collections.emptyMap() : raw;
    }

    public double getDouble(String key, double fallback) {
        return SerializationUtil.getDouble(this.raw, key, fallback);
    }

    public float getFloat(String key, float fallback) {
        return SerializationUtil.getFloat(this.raw, key, fallback);
    }

    public int getInt(String key, int fallback) {
        return SerializationUtil.getInt(this.raw, key, fallback);
    }

    public long getLong(String key, long fallback) {
        return SerializationUtil.getLong(this.raw, key, fallback);
    }

    public boolean getBoolean(String key, boolean fallback) {
        return SerializationUtil.getBoolean(this.raw, key, fallback);
    }

    public String getString(String key, String fallback) {
        return SerializationUtil.getString(this.raw, key, fallback);
    }
}

