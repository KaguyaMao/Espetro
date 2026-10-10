/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.tags.TagsProvider$TagAppender
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 */
package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;

public interface IForgeTagAppender<T> {
    private TagsProvider.TagAppender<T> self() {
        return (TagsProvider.TagAppender)this;
    }

    default public TagsProvider.TagAppender<T> addTags(TagKey<T> ... values) {
        TagsProvider.TagAppender<T> builder = this.self();
        for (TagKey<T> value : values) {
            builder.m_206428_(value);
        }
        return builder;
    }

    default public TagsProvider.TagAppender<T> addOptionalTag(TagKey<T> value) {
        return this.self().m_176841_(value.f_203868_());
    }

    default public TagsProvider.TagAppender<T> addOptionalTags(TagKey<T> ... values) {
        TagsProvider.TagAppender<T> builder = this.self();
        for (TagKey<T> value : values) {
            builder.m_176841_(value.f_203868_());
        }
        return builder;
    }

    default public TagsProvider.TagAppender<T> replace() {
        return this.replace(true);
    }

    default public TagsProvider.TagAppender<T> replace(boolean value) {
        this.self().getInternalBuilder().replace(value);
        return this.self();
    }

    default public TagsProvider.TagAppender<T> remove(ResourceLocation location) {
        TagsProvider.TagAppender<T> builder = this.self();
        builder.getInternalBuilder().removeElement(location, builder.getModID());
        return builder;
    }

    default public TagsProvider.TagAppender<T> remove(ResourceLocation first, ResourceLocation ... locations) {
        this.remove(first);
        for (ResourceLocation location : locations) {
            this.remove(location);
        }
        return this.self();
    }

    default public TagsProvider.TagAppender<T> remove(ResourceKey<T> resourceKey) {
        this.remove(resourceKey.m_135782_());
        return this.self();
    }

    default public TagsProvider.TagAppender<T> remove(ResourceKey<T> firstResourceKey, ResourceKey<T> ... resourceKeys) {
        this.remove(firstResourceKey.m_135782_());
        for (ResourceKey<T> resourceKey : resourceKeys) {
            this.remove(resourceKey.m_135782_());
        }
        return this.self();
    }

    default public TagsProvider.TagAppender<T> remove(TagKey<T> tag) {
        TagsProvider.TagAppender<T> builder = this.self();
        builder.getInternalBuilder().removeTag(tag.f_203868_(), builder.getModID());
        return builder;
    }

    default public TagsProvider.TagAppender<T> remove(TagKey<T> first, TagKey<T> ... tags) {
        this.remove(first);
        for (TagKey<T> tag : tags) {
            this.remove(tag);
        }
        return this.self();
    }
}

