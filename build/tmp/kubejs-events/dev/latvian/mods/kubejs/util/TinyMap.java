/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.util;

import java.util.Collection;
import java.util.Map;

public record TinyMap<K, V>(Entry<K, V>[] entries) {
    public TinyMap(Collection<Entry<K, V>> collection) {
        this(collection.toArray(new Entry[collection.size()]));
    }

    public TinyMap(TinyMap<K, V> map) {
        this((Entry[])map.entries.clone());
    }

    public boolean isEmpty() {
        return this.entries.length == 0;
    }

    public static <K, V> TinyMap<K, V> ofMap(Map<K, V> map) {
        Entry[] entries = new Entry[map.size()];
        int i = 0;
        for (Map.Entry<K, V> entry : map.entrySet()) {
            entries[i++] = new Entry<K, V>(entry.getKey(), entry.getValue());
        }
        return new TinyMap<K, V>(entries);
    }

    public record Entry<K, V>(K key, V value) {
    }
}

