/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.tags.TagKey
 */
package net.minecraftforge.registries.tags;

import java.util.stream.Stream;
import net.minecraft.tags.TagKey;
import net.minecraftforge.registries.tags.ITag;

public interface IReverseTag<V> {
    public Stream<TagKey<V>> getTagKeys();

    public boolean containsTag(TagKey<V> var1);

    default public boolean containsTag(ITag<V> tag) {
        return this.containsTag(tag.getKey());
    }
}

