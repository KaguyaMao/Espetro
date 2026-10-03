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
import net.minecraft.resources.ResourceLocation;

public record IDFilter(ResourceLocation id) implements BiomeFilter
{
    @Override
    public boolean test(BiomeModifications.BiomeContext ctx) {
        return ctx.getKey().map(arg_0 -> ((ResourceLocation)this.id).equals(arg_0)).orElse(false);
    }
}

