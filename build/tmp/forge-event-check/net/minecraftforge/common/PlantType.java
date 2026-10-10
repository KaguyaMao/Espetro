/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

public final class PlantType {
    private static final Pattern INVALID_CHARACTERS = Pattern.compile("[^a-z_]");
    private static final Map<String, PlantType> VALUES = new ConcurrentHashMap<String, PlantType>();
    public static final PlantType PLAINS = PlantType.get("plains");
    public static final PlantType DESERT = PlantType.get("desert");
    public static final PlantType BEACH = PlantType.get("beach");
    public static final PlantType CAVE = PlantType.get("cave");
    public static final PlantType WATER = PlantType.get("water");
    public static final PlantType NETHER = PlantType.get("nether");
    public static final PlantType CROP = PlantType.get("crop");
    private final String name;

    public static PlantType get(String name) {
        return VALUES.computeIfAbsent(name, e -> {
            if (INVALID_CHARACTERS.matcher((CharSequence)e).find()) {
                throw new IllegalArgumentException("PlantType.get() called with invalid name: " + name);
            }
            return new PlantType((String)e);
        });
    }

    private PlantType(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }
}

