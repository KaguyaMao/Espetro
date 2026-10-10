/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.RandomSource
 */
package net.minecraftforge.registries.tags;

import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;

public interface ITag<V>
extends Iterable<V> {
    public TagKey<V> getKey();

    public Stream<V> stream();

    public boolean isEmpty();

    public int size();

    public boolean contains(V var1);

    public Optional<V> getRandomElement(RandomSource var1);

    public boolean isBound();
}

