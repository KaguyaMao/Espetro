/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.container.bind;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public enum ContainerBindType {
    PLAYER("player"),
    ENTITY("entity"),
    BLOCK_ENTITY("block_entity"),
    SAVED_DATA("saved_data");

    public static final int PLAYER_SLOT_COUNT = 36;
    private static final Map<String, ContainerBindType> BY_ID;
    private final String id;

    private ContainerBindType(String id) {
        this.id = id;
    }

    public static ContainerBindType fromRaw(String rawBindType) {
        if (rawBindType == null || rawBindType.isBlank()) {
            return null;
        }
        return BY_ID.get(rawBindType.trim().toLowerCase(Locale.ROOT));
    }

    public static boolean isPlayer(ContainerBindType bindType) {
        return bindType == PLAYER;
    }

    public static boolean hasDataSource(ContainerBindType bindType) {
        return bindType != null;
    }

    public String id() {
        return this.id;
    }

    static {
        BY_ID = new HashMap<String, ContainerBindType>();
        for (ContainerBindType value : ContainerBindType.values()) {
            BY_ID.put(value.id, value);
        }
    }
}

