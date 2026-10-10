/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.ParametersAreNonnullByDefault
 *  net.minecraft.MethodsReturnNonnullByDefault
 *  org.apache.commons.lang3.mutable.Mutable
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.apache.logging.log4j.Level
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.util;

import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.common.util.NonNullFunction;
import net.minecraftforge.common.util.NonNullPredicate;
import net.minecraftforge.common.util.NonNullSupplier;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class LazyOptional<T> {
    private final NonNullSupplier<T> supplier;
    private final Object lock = new Object();
    private Mutable<T> resolved;
    private final Set<NonNullConsumer<LazyOptional<T>>> listeners = new HashSet<NonNullConsumer<LazyOptional<T>>>();
    private boolean isValid = true;
    @NotNull
    private static final LazyOptional<Void> EMPTY = new LazyOptional(null);
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T> LazyOptional<T> of(@Nullable NonNullSupplier<T> instanceSupplier) {
        return instanceSupplier == null ? LazyOptional.empty() : new LazyOptional<T>(instanceSupplier);
    }

    public static <T> LazyOptional<T> empty() {
        return EMPTY.cast();
    }

    public <X> LazyOptional<X> cast() {
        return this;
    }

    private LazyOptional(@Nullable NonNullSupplier<T> instanceSupplier) {
        this.supplier = instanceSupplier;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    private T getValue() {
        if (!this.isValid || this.supplier == null) {
            return null;
        }
        if (this.resolved == null) {
            Object object = this.lock;
            synchronized (object) {
                if (this.resolved == null) {
                    T temp = this.supplier.get();
                    if (temp == null) {
                        LOGGER.catching(Level.WARN, (Throwable)new NullPointerException("Supplier should not return null value"));
                    }
                    this.resolved = new MutableObject(temp);
                }
            }
        }
        return (T)this.resolved.getValue();
    }

    private T getValueUnsafe() {
        T ret = this.getValue();
        if (ret == null) {
            throw new IllegalStateException("LazyOptional is empty or otherwise returned null from getValue() unexpectedly");
        }
        return ret;
    }

    public boolean isPresent() {
        return this.supplier != null && this.isValid;
    }

    public void ifPresent(NonNullConsumer<? super T> consumer) {
        Objects.requireNonNull(consumer);
        T val = this.getValue();
        if (this.isValid && val != null) {
            consumer.accept(val);
        }
    }

    public <U> LazyOptional<U> lazyMap(NonNullFunction<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return this.isPresent() ? LazyOptional.of(() -> mapper.apply((T)this.getValueUnsafe())) : LazyOptional.empty();
    }

    public <U> Optional<U> map(NonNullFunction<? super T, ? extends U> mapper) {
        Objects.requireNonNull(mapper);
        return this.isPresent() ? Optional.of(mapper.apply(this.getValueUnsafe())) : Optional.empty();
    }

    public Optional<T> filter(NonNullPredicate<? super T> predicate) {
        Objects.requireNonNull(predicate);
        T value = this.getValue();
        return value != null && predicate.test(value) ? Optional.of(value) : Optional.empty();
    }

    public Optional<T> resolve() {
        return this.isPresent() ? Optional.of(this.getValueUnsafe()) : Optional.empty();
    }

    public T orElse(T other) {
        T val = this.getValue();
        return val != null ? val : other;
    }

    public T orElseGet(NonNullSupplier<? extends T> other) {
        T val = this.getValue();
        return val != null ? val : other.get();
    }

    public <X extends Throwable> T orElseThrow(NonNullSupplier<? extends X> exceptionSupplier) throws X {
        T val = this.getValue();
        if (val != null) {
            return val;
        }
        throw (Throwable)exceptionSupplier.get();
    }

    public void addListener(NonNullConsumer<LazyOptional<T>> listener) {
        if (this.isPresent()) {
            this.listeners.add(listener);
        } else {
            listener.accept(this);
        }
    }

    public void removeListener(NonNullConsumer<LazyOptional<T>> listener) {
        this.listeners.remove(listener);
    }

    public void invalidate() {
        if (this.isValid) {
            this.isValid = false;
            this.listeners.forEach(e -> e.accept(this));
            this.listeners.clear();
        }
    }
}

