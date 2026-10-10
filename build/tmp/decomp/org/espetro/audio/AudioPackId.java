/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.audio;

public final class AudioPackId {
    public static final int MAX_LENGTH = 80;

    private AudioPackId() {
    }

    public static String normalize(String value) {
        if (value == null) {
            return null;
        }
        String normalized = value.trim();
        if (normalized.isEmpty() || normalized.length() > 80 || ".".equals(normalized) || "..".equals(normalized)) {
            return null;
        }
        for (int i = 0; i < normalized.length(); ++i) {
            char c = normalized.charAt(i);
            if (c >= ' ' && c != '/' && c != '\\' && c != ':' && c != '\u0000' && c != '*' && c != '?' && c != '\"' && c != '<' && c != '>' && c != '|') continue;
            return null;
        }
        return normalized;
    }
}

