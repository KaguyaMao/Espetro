/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.level.biome.BiomeModifications$BiomeContext
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.level.biome.Biome
 */
package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import dev.latvian.mods.kubejs.util.Tags;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.biome.Biome;

public record TagFilter(TagKey<Biome> tag) implements BiomeFilter
{
    public TagFilter(String id) {
        this(Tags.biome(new ResourceLocation(id)));
    }

    @Override
    public boolean test(BiomeModifications.BiomeContext ctx) {
        return ctx.hasTag(this.tag);
    }
}

