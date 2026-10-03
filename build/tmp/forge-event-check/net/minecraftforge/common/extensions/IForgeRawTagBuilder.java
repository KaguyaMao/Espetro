/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagBuilder
 *  net.minecraft.tags.TagEntry
 */
package net.minecraftforge.common.extensions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;

public interface IForgeRawTagBuilder {
    default public TagBuilder getRawBuilder() {
        return (TagBuilder)this;
    }

    @Deprecated(forRemoval=true, since="1.20.1")
    default public void serializeTagAdditions(JsonObject tagJson) {
    }

    default public TagBuilder remove(TagEntry tagEntry, String source) {
        return this.getRawBuilder().remove(tagEntry);
    }

    default public TagBuilder removeElement(ResourceLocation elementID, String source) {
        return this.remove(TagEntry.m_215925_((ResourceLocation)elementID), source);
    }

    default public TagBuilder removeTag(ResourceLocation tagID, String source) {
        return this.remove(TagEntry.m_215949_((ResourceLocation)tagID), source);
    }
}

