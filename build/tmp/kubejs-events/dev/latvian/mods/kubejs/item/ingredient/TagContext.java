/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  dev.architectury.extensions.injected.InjectedRegistryEntryExtension
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.core.RegistryAccess
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.tags.TagKey
 *  net.minecraft.tags.TagManager$LoadResult
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.level.ItemLike
 *  org.apache.commons.lang3.mutable.MutableObject
 *  org.jetbrains.annotations.NotNull
 */
package dev.latvian.mods.kubejs.item.ingredient;

import com.google.common.collect.Iterables;
import dev.architectury.extensions.injected.InjectedRegistryEntryExtension;
import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.tags.TagManager;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jetbrains.annotations.NotNull;

public interface TagContext {
    public static final TagContext EMPTY = new TagContext(){

        @Override
        public <T> boolean isEmpty(TagKey<T> tag) {
            return true;
        }

        @Override
        public <T> Iterable<Holder<T>> getTag(TagKey<T> tag) {
            KubeJS.LOGGER.warn("Tried to get tag {} from an empty tag context!", (Object)tag.f_203868_());
            return List.of();
        }
    };
    public static final MutableObject<TagContext> INSTANCE = new MutableObject((Object)EMPTY);

    public static TagContext usingRegistry(final RegistryAccess registryAccess) {
        return new TagContext(){

            @NotNull
            private <T> Registry<T> registry(TagKey<T> tag) {
                return registryAccess.m_175515_(tag.f_203867_());
            }

            @Override
            public boolean areTagsBound() {
                return true;
            }

            @Override
            public <T> Iterable<Holder<T>> getTag(TagKey<T> tag) {
                return this.registry(tag).m_206058_(tag);
            }

            @Override
            public <T> boolean contains(TagKey<T> tag, T value) {
                if (value instanceof InjectedRegistryEntryExtension) {
                    InjectedRegistryEntryExtension ext = (InjectedRegistryEntryExtension)value;
                    Holder holder2 = (Holder)UtilsJS.cast(ext.arch$holder());
                    return holder2.m_203656_(tag);
                }
                Registry<T> reg = this.registry(tag);
                return reg.m_7854_(value).flatMap(arg_0 -> reg.m_203636_(arg_0)).map(holder -> holder.m_203656_(tag)).orElseGet(() -> TagContext.super.contains(tag, value));
            }
        };
    }

    public static TagContext fromLoadResult(List<TagManager.LoadResult<?>> results) {
        final Map<ResourceKey, Map> tags = results.stream().collect(Collectors.toMap(result -> (ResourceKey)UtilsJS.cast(result.f_203928_()), result -> (Map)UtilsJS.cast(result.f_203929_())));
        if (!tags.containsKey(Registries.f_256913_)) {
            ConsoleJS.getCurrent(ConsoleJS.SERVER).warn("Failed to load item tags during recipe event! Using replaceInput etc. will not work!");
            return EMPTY;
        }
        return new TagContext(){

            @Override
            public <T> Iterable<Holder<T>> getTag(TagKey<T> tag) {
                return (Iterable)UtilsJS.cast(((Map)tags.get(tag.f_203867_())).getOrDefault(tag.f_203868_(), Set.of()));
            }
        };
    }

    default public <T> boolean isEmpty(TagKey<T> tag) {
        return Iterables.isEmpty(this.getTag(tag));
    }

    default public <T> boolean contains(TagKey<T> tag, T value) {
        if (this.isEmpty(tag)) {
            return false;
        }
        for (Holder<T> holder : this.getTag(tag)) {
            if (!holder.m_203334_().equals(value)) continue;
            return true;
        }
        return false;
    }

    default public boolean areTagsBound() {
        return false;
    }

    public <T> Iterable<Holder<T>> getTag(TagKey<T> var1);

    default public Collection<ItemStack> patchIngredientTags(TagKey<Item> tag) {
        int n;
        Iterable<Holder<Item>> c = this.getTag(tag);
        if (c instanceof Collection) {
            Collection cl = (Collection)c;
            n = cl.size();
        } else {
            n = 3;
        }
        ArrayList<ItemStack> stacks = new ArrayList<ItemStack>(n);
        for (Holder<Item> holder : c) {
            stacks.add(new ItemStack((ItemLike)holder.m_203334_()));
        }
        return stacks.isEmpty() ? List.of() : stacks;
    }
}

