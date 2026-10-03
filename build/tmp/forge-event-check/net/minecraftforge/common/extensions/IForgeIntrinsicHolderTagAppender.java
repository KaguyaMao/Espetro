/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.data.tags.IntrinsicHolderTagsProvider$IntrinsicTagAppender
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 */
package net.minecraftforge.common.extensions;

import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.extensions.IForgeTagAppender;

public interface IForgeIntrinsicHolderTagAppender<T>
extends IForgeTagAppender<T> {
    private IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> self() {
        return (IntrinsicHolderTagsProvider.IntrinsicTagAppender)this;
    }

    public ResourceKey<T> getKey(T var1);

    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(T entry) {
        return this.remove(this.getKey(entry));
    }

    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(T first, T ... entries) {
        this.remove(first);
        for (T entry : entries) {
            this.remove(entry);
        }
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> addTags(TagKey<T> ... values) {
        IForgeTagAppender.super.addTags(values);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> replace() {
        IForgeTagAppender.super.replace();
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> replace(boolean value) {
        IForgeTagAppender.super.replace(value);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceLocation location) {
        IForgeTagAppender.super.remove(location);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceLocation first, ResourceLocation ... locations) {
        IForgeTagAppender.super.remove(first, locations);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceKey<T> resourceKey) {
        IForgeTagAppender.super.remove(resourceKey);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(ResourceKey<T> firstResourceKey, ResourceKey<T> ... resourceKeys) {
        IForgeTagAppender.super.remove(firstResourceKey, resourceKeys);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(TagKey<T> tag) {
        IForgeTagAppender.super.remove(tag);
        return this.self();
    }

    @Override
    default public IntrinsicHolderTagsProvider.IntrinsicTagAppender<T> remove(TagKey<T> first, TagKey<T> ... tags) {
        IForgeTagAppender.super.remove(first, tags);
        return this.self();
    }
}

