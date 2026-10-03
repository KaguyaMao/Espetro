/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Lazy<T>
extends Supplier<T> {
    public static <T> Lazy<T> of(@NotNull Supplier<T> supplier) {
        return new Fast<T>(supplier);
    }

    public static <T> Lazy<T> concurrentOf(@NotNull Supplier<T> supplier) {
        return new Concurrent<T>(supplier);
    }

    public static final class Fast<T>
    implements Lazy<T> {
        private Supplier<T> supplier;
        private T instance;

        private Fast(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        @Override
        @Nullable
        public final T get() {
            if (this.supplier != null) {
                this.instance = this.supplier.get();
                this.supplier = null;
            }
            return this.instance;
        }
    }

    public static final class Concurrent<T>
    implements Lazy<T> {
        private volatile Object lock = new Object();
        private volatile Supplier<T> supplier;
        private volatile T instance;

        private Concurrent(Supplier<T> supplier) {
            this.supplier = supplier;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        @Override
        @Nullable
        public final T get() {
            Object localLock = this.lock;
            if (this.supplier != null) {
                Object object = localLock;
                synchronized (object) {
                    if (this.supplier != null) {
                        this.instance = this.supplier.get();
                        this.supplier = null;
                        this.lock = null;
                    }
                }
            }
            return this.instance;
        }
    }
}

