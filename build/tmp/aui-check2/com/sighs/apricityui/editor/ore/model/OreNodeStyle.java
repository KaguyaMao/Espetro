/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OreNodeStyle {
    private final Map<String, String> properties = new LinkedHashMap<String, String>();

    public Map<String, String> properties() {
        return Map.copyOf(this.properties);
    }

    public String get(String property) {
        return this.properties.get(property);
    }

    public void set(String property, String value) {
        if (property == null || property.isBlank()) {
            return;
        }
        if (value == null || value.isBlank()) {
            this.properties.remove(property.trim());
        } else {
            this.properties.put(property.trim(), value.trim());
        }
    }
}

