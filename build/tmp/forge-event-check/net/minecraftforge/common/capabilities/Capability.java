/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.common.capabilities;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class Capability<T> {
    private final String name;
    List<Consumer<Capability<T>>> listeners = new ArrayList<Consumer<Capability<T>>>();

    public String getName() {
        return this.name;
    }

    @NotNull
    public <R> LazyOptional<R> orEmpty(Capability<R> toCheck, LazyOptional<T> inst) {
        return this == toCheck ? inst.cast() : LazyOptional.empty();
    }

    public boolean isRegistered() {
        return this.listeners == null;
    }

    public synchronized Capability<T> addListener(Consumer<Capability<T>> listener) {
        if (this.isRegistered()) {
            listener.accept(this);
        } else {
            this.listeners.add(listener);
        }
        return this;
    }

    Capability(String name) {
        this.name = name;
    }

    void onRegister() {
        List<Consumer<Capability<Consumer>>> listeners = this.listeners;
        this.listeners = null;
        listeners.forEach(l -> l.accept(this));
    }
}

