/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import java.util.LinkedHashMap;
import java.util.Map;

public final class OreTheme {
    private final Map<String, String> overrides = new LinkedHashMap<String, String>();

    public Map<String, String> overrides() {
        return Map.copyOf(this.overrides);
    }

    public String get(String token) {
        return this.overrides.get(token);
    }

    public void set(String token, String value) {
        if (token == null || token.isBlank()) {
            return;
        }
        if (value == null || value.isBlank()) {
            this.overrides.remove(token);
        } else {
            this.overrides.put(token.trim(), value.trim());
        }
    }

    public void reset() {
        this.overrides.clear();
    }

    public String toCss() {
        StringBuilder css = new StringBuilder();
        this.overrides.forEach((token, value) -> css.append((String)token).append(':').append((String)value).append(';'));
        return css.toString();
    }
}

