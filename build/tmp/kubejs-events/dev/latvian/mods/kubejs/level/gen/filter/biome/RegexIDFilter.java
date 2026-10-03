/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.level.biome.BiomeModifications$BiomeContext
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.resources.ResourceLocation;

public record RegexIDFilter(Pattern pattern) implements BiomeFilter
{
    @Override
    public boolean test(BiomeModifications.BiomeContext ctx) {
        return ctx.getKey().map(ResourceLocation::toString).map(this.pattern::matcher).map(Matcher::find).orElse(false);
    }
}

