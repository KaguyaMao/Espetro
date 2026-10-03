/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.util;

import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public class Lazy<T>
implements Supplier<T> {
    private final Supplier<T> factory;
    private T value;
    private boolean cached;
    private final long expires;

    public static <T> Lazy<T> of(Supplier<T> supplier) {
        return new Lazy<T>(supplier, 0L);
    }

    public static <T> Lazy<T> of(Supplier<T> supplier, long expiresInMs) {
        return new Lazy<T>(supplier, System.currentTimeMillis() + expiresInMs);
    }

    public static <T> Lazy<T> serviceLoader(Class<T> type) {
        return Lazy.of(() -> {
            Optional value = ServiceLoader.load(type).findFirst();
            if (value.isEmpty()) {
                throw new RuntimeException("Could not find platform implementation for %s!".formatted(type.getSimpleName()));
            }
            return value.get();
        });
    }

    private Lazy(Supplier<T> factory, long expires) {
        this.factory = factory;
        this.expires = expires;
    }

    @Override
    public T get() {
        if (this.expires > 0L && System.currentTimeMillis() > this.expires) {
            this.cached = false;
        } else if (this.cached) {
            return this.value;
        }
        this.value = this.factory.get();
        this.cached = true;
        return this.value;
    }

    public void forget() {
        this.value = null;
        this.cached = false;
    }
}

