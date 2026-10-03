/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 */
package org.espetro.logistics;

import javax.annotation.Nullable;

public enum SupplyType {
    CONSTRUCTION("construction"),
    AMMUNITION("ammunition");

    private final String id;

    private SupplyType(String id) {
        this.id = id;
    }

    public String id() {
        return this.id;
    }

    @Nullable
    public static SupplyType fromId(@Nullable String id) {
        if (id == null) {
            return null;
        }
        for (SupplyType type : SupplyType.values()) {
            if (!type.id.equalsIgnoreCase(id)) continue;
            return type;
        }
        return null;
    }
}

