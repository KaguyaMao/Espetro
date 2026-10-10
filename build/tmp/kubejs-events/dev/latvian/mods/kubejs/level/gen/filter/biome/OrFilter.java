/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.level.biome.BiomeModifications$BiomeContext
 */
package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.BiomeFilter;
import java.util.List;

public record OrFilter(List<BiomeFilter> list) implements BiomeFilter
{
    @Override
    public boolean test(BiomeModifications.BiomeContext ctx) {
        for (BiomeFilter filter : this.list) {
            if (!filter.test(ctx)) continue;
            return true;
        }
        return false;
    }
}

