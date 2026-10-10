/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  me.shedaniel.rei.api.client.registry.entry.EntryRegistry
 *  me.shedaniel.rei.api.common.entry.EntryStack
 */
package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.event.EventResult;
import dev.latvian.mods.kubejs.integration.rei.EntryWrapper;
import java.util.ArrayList;
import java.util.List;
import me.shedaniel.rei.api.client.registry.entry.EntryRegistry;
import me.shedaniel.rei.api.common.entry.EntryStack;

public class AddREIEventJS<T, C>
extends EventJS {
    private final EntryRegistry registry;
    private final EntryWrapper<T, C> entryWrapper;
    private final List<EntryStack<T>> added;

    public AddREIEventJS(EntryRegistry registry, EntryWrapper<T, C> entryWrapper) {
        this.registry = registry;
        this.entryWrapper = entryWrapper;
        this.added = new ArrayList<EntryStack<T>>();
    }

    public void add(Object o) {
        this.added.addAll(this.entryWrapper.entryList(o));
    }

    @Override
    protected void afterPosted(EventResult result) {
        if (!this.added.isEmpty()) {
            this.registry.addEntries(this.added);
        }
    }
}

