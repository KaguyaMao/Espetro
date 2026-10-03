/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.layout;

import java.util.Locale;

enum Align {
    START,
    CENTER,
    END,
    STRETCH;


    static Align normalize(String raw, Align fallback) {
        if (raw == null) {
            return fallback;
        }
        if ((raw = raw.trim().toLowerCase(Locale.ROOT)).isBlank() || "unset".equals(raw) || "auto".equals(raw)) {
            return fallback;
        }
        return switch (raw) {
            case "start", "flex-start", "left", "top" -> START;
            case "center" -> CENTER;
            case "end", "flex-end", "right", "bottom" -> END;
            case "stretch" -> STRETCH;
            default -> fallback;
        };
    }
}

