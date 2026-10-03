/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.regexp.NativeRegExp
 *  net.minecraft.world.entity.MobCategory
 *  net.minecraft.world.level.biome.MobSpawnSettings$SpawnerData
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.level.gen.filter.mob;

import dev.latvian.mods.kubejs.level.gen.filter.mob.AndFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.CategoryFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.IDFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.NotFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.OrFilter;
import dev.latvian.mods.kubejs.level.gen.filter.mob.RegexIDFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.BiPredicate;
import java.util.regex.Pattern;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface MobFilter
extends BiPredicate<MobCategory, MobSpawnSettings.SpawnerData> {
    public static final MobFilter ALWAYS_TRUE = (cat, data) -> true;
    public static final MobFilter ALWAYS_FALSE = (cat, data) -> false;

    @Override
    public boolean test(MobCategory var1, MobSpawnSettings.SpawnerData var2);

    public static MobFilter of(Context cx, @Nullable Object o) {
        if (o == null || o == ALWAYS_TRUE) {
            return ALWAYS_TRUE;
        }
        if (o == ALWAYS_FALSE) {
            return ALWAYS_FALSE;
        }
        if (o instanceof CharSequence || o instanceof NativeRegExp || o instanceof Pattern) {
            return MobFilter.idFilter(cx, o.toString());
        }
        List<?> list = ListJS.orSelf(o);
        if (list.isEmpty()) {
            return ALWAYS_FALSE;
        }
        if (list.size() > 1) {
            ArrayList<MobFilter> filters = new ArrayList<MobFilter>();
            for (Object o1 : list) {
                MobFilter filter = MobFilter.of(cx, o1);
                if (filter == ALWAYS_TRUE) {
                    return ALWAYS_TRUE;
                }
                if (filter == ALWAYS_FALSE) continue;
                filters.add(filter);
            }
            return filters.isEmpty() ? ALWAYS_FALSE : (filters.size() == 1 ? (MobFilter)filters.get(0) : new OrFilter(filters));
        }
        Map<?, ?> map = MapJS.of(list.get(0));
        if (map == null || map.isEmpty()) {
            return ALWAYS_TRUE;
        }
        ArrayList<MobFilter> filters = new ArrayList<MobFilter>();
        if (map.get("or") != null) {
            filters.add(MobFilter.of(cx, map.get("or")));
        }
        if (map.get("not") != null) {
            filters.add(new NotFilter(MobFilter.of(cx, map.get("not"))));
        }
        try {
            if (map.get("id") != null) {
                filters.add(MobFilter.idFilter(cx, map.get("id").toString()));
            }
            if (map.get("type") != null) {
                filters.add(MobFilter.idFilter(cx, map.get("type").toString()));
            }
            if (map.get("category") != null) {
                filters.add(new CategoryFilter(UtilsJS.mobCategoryByName(map.get("category").toString())));
            }
        }
        catch (Exception ex) {
            ConsoleJS.getCurrent(cx).error("Error trying to create MobFilter: " + ex.getMessage());
            return ALWAYS_FALSE;
        }
        return filters.isEmpty() ? ALWAYS_TRUE : (filters.size() == 1 ? (MobFilter)filters.get(0) : new AndFilter(filters));
    }

    public static MobFilter idFilter(Context cx, String s) {
        if (s.equals("*")) {
            return ALWAYS_TRUE;
        }
        if (s.equals("-")) {
            return ALWAYS_FALSE;
        }
        Pattern pattern = UtilsJS.parseRegex(s);
        if (pattern != null) {
            return new RegexIDFilter(pattern);
        }
        return s.charAt(0) == '#' ? new CategoryFilter(UtilsJS.mobCategoryByName(s.substring(1))) : new IDFilter(UtilsJS.getMCID(cx, s));
    }
}

