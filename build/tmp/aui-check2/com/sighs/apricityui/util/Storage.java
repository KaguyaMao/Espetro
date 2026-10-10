/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public abstract class Storage {
    protected final LinkedHashMap<String, String> data = new LinkedHashMap();

    public String getItem(String key) {
        if (key == null || key.isBlank()) {
            return null;
        }
        return this.data.get(key);
    }

    public void setItem(String key, String value) {
        if (key == null || key.isBlank()) {
            return;
        }
        this.data.put(key, value == null ? "null" : value);
    }

    public void removeItem(String key) {
        if (key == null || key.isBlank()) {
            return;
        }
        this.data.remove(key);
    }

    public void clear() {
        this.data.clear();
    }

    public int getLength() {
        return this.data.size();
    }

    public String key(int index) {
        if (index < 0 || index >= this.data.size()) {
            return null;
        }
        return new ArrayList<String>(this.data.keySet()).get(index);
    }
}

