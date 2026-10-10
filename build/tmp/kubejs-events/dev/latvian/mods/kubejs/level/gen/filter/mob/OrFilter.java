/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 */
package dev.latvian.mods.kubejs.level.gen.filter.mob;

import dev.latvian.mods.kubejs.level.gen.filter.mob.MobFilter;
import java.util.List;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;

public record OrFilter(List<MobFilter> list) implements MobFilter
{
    @Override
    public boolean test(MobCategory cat, MobSpawnSettings.SpawnerData data) {
        for (MobFilter p : this.list) {
            if (p.test(cat, data)) continue;
            return true;
        }
        return false;
    }
}

