/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.level.biome.BiomeModifications$BiomeContext
 */
package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;

public record NotFilter(BiomeFilter original) implements BiomeFilter
{
    @Override
    public boolean test(BiomeModifications.BiomeContext ctx) {
        return !this.original.test(ctx);
    }
}

