/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.bastion;

import java.util.Locale;
import javax.annotation.Nullable;

public enum StructureKind {
    RADIO,
    HAB;


    public static StructureKind fromStorage(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return RADIO;
        }
        try {
            return StructureKind.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        }
        catch (IllegalArgumentException ignored) {
            return RADIO;
        }
    }

    public String networkType() {
        return this.name();
    }
}

