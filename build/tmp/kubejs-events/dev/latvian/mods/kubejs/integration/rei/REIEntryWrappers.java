/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.Event
 *  dev.architectury.event.EventFactory
 *  dev.architectury.fluid.FluidStack
 *  me.shedaniel.rei.api.common.entry.type.EntryType
 *  me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes
 */
package dev.latvian.mods.kubejs.integration.rei;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.architectury.fluid.FluidStack;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.integration.rei.EntryWrapper;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import me.shedaniel.rei.api.common.entry.type.EntryType;
import me.shedaniel.rei.api.common.entry.type.VanillaEntryTypes;

public class REIEntryWrappers {
    public static final Event<Consumer<REIEntryWrappers>> EVENT = EventFactory.createConsumerLoop((Object[])new REIEntryWrappers[0]);
    private final Map<EntryType<?>, EntryWrapper<?, ?>> entryWrappers = new HashMap();

    public REIEntryWrappers() {
        this.add(VanillaEntryTypes.ITEM, IngredientJS::of, Function.identity(), IngredientKJS::kjs$getDisplayStacks);
        this.add(VanillaEntryTypes.FLUID, o -> FluidStackJS.of(o).getFluidStack(), fs -> arg_0 -> ((FluidStack)fs).isFluidEqual(arg_0), List::of);
        ((Consumer)EVENT.invoker()).accept(this);
    }

    public <T, C> void add(EntryType<T> type, Function<Object, C> converter, Function<C, ? extends Predicate<T>> filter, Function<C, ? extends Iterable<T>> entries) {
        this.entryWrappers.put(type, new EntryWrapper<T, C>(type, converter, filter, entries));
    }

    public <T> EntryWrapper<T, ?> getWrapper(EntryType<T> type) {
        EntryWrapper<Object, Object> wrapper = this.entryWrappers.get(type);
        if (wrapper == null) {
            wrapper = new EntryWrapper<T, Object>(type, Function.identity(), c -> c::equals, c -> List.of(c));
            this.entryWrappers.put(type, wrapper);
        }
        return wrapper;
    }

    public Collection<EntryWrapper<?, ?>> getWrappers() {
        return this.entryWrappers.values();
    }
}

