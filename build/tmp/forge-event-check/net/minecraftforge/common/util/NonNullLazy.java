/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.util;

import java.util.Objects;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.common.util.NonNullSupplier;
import org.jetbrains.annotations.NotNull;

public interface NonNullLazy<T>
extends NonNullSupplier<T> {
    public static <T> NonNullLazy<T> of(@NotNull NonNullSupplier<T> supplier) {
        Lazy<Object> lazy = Lazy.of(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }

    public static <T> NonNullLazy<T> concurrentOf(@NotNull NonNullSupplier<T> supplier) {
        Lazy<Object> lazy = Lazy.concurrentOf(supplier::get);
        return () -> Objects.requireNonNull(lazy.get());
    }
}

