/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.registries.tags;

import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.tags.IReverseTag;
import net.minecraftforge.registries.tags.ITag;
import org.jetbrains.annotations.NotNull;

public interface ITagManager<V>
extends Iterable<ITag<V>> {
    @NotNull
    public ITag<V> getTag(@NotNull TagKey<V> var1);

    @NotNull
    public Optional<IReverseTag<V>> getReverseTag(@NotNull V var1);

    public boolean isKnownTagName(@NotNull TagKey<V> var1);

    @NotNull
    public Stream<ITag<V>> stream();

    @NotNull
    public Stream<TagKey<V>> getTagNames();

    @NotNull
    public TagKey<V> createTagKey(@NotNull ResourceLocation var1);

    @NotNull
    public TagKey<V> createOptionalTagKey(@NotNull ResourceLocation var1, @NotNull Set<? extends Supplier<V>> var2);

    public void addOptionalTagDefaults(@NotNull TagKey<V> var1, @NotNull Set<? extends Supplier<V>> var2);
}

