/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.registry.level.biome.BiomeModifications$BiomeContext
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.regexp.NativeRegExp
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.level.gen.filter.biome;

import dev.architectury.registry.level.biome.BiomeModifications;
import dev.latvian.mods.kubejs.level.gen.filter.biome.AndFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.IDFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.NotFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.OrFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.RegexIDFilter;
import dev.latvian.mods.kubejs.level.gen.filter.biome.TagFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface BiomeFilter
extends Predicate<BiomeModifications.BiomeContext> {
    public static final BiomeFilter ALWAYS_TRUE = ctx -> true;
    public static final BiomeFilter ALWAYS_FALSE = ctx -> false;

    @Override
    public boolean test(BiomeModifications.BiomeContext var1);

    public static BiomeFilter of(Context cx, @Nullable Object o) {
        if (o == null || o == ALWAYS_TRUE) {
            return ALWAYS_TRUE;
        }
        if (o == ALWAYS_FALSE) {
            return ALWAYS_FALSE;
        }
        if (o instanceof CharSequence || o instanceof NativeRegExp || o instanceof Pattern) {
            return BiomeFilter.idFilter(cx, o.toString());
        }
        List<?> list = ListJS.orSelf(o);
        if (list.isEmpty()) {
            return ALWAYS_FALSE;
        }
        if (list.size() > 1) {
            ArrayList<BiomeFilter> filters = new ArrayList<BiomeFilter>();
            for (Object o1 : list) {
                BiomeFilter filter = BiomeFilter.of(cx, o1);
                if (filter == ALWAYS_TRUE) {
                    return ALWAYS_TRUE;
                }
                if (filter == ALWAYS_FALSE) continue;
                filters.add(filter);
            }
            return filters.isEmpty() ? ALWAYS_FALSE : (filters.size() == 1 ? (BiomeFilter)filters.get(0) : new OrFilter(filters));
        }
        Map<?, ?> map = MapJS.of(list.get(0));
        if (map == null || map.isEmpty()) {
            return ALWAYS_TRUE;
        }
        ArrayList<BiomeFilter> filters = new ArrayList<BiomeFilter>();
        if (map.get("or") != null) {
            filters.add(BiomeFilter.of(cx, map.get("or")));
        }
        if (map.get("not") != null) {
            filters.add(new NotFilter(BiomeFilter.of(cx, map.get("not"))));
        }
        try {
            if (map.get("id") != null) {
                filters.add(BiomeFilter.idFilter(cx, map.get("id").toString()));
            }
            if (map.get("type") != null) {
                filters.add(BiomeFilter.idFilter(cx, map.get("type").toString()));
            }
            if (map.get("tag") != null) {
                filters.add(new TagFilter(map.get("tag").toString()));
            }
        }
        catch (Exception ex) {
            ConsoleJS.getCurrent(cx).error("Error trying to create BiomeFilter: " + ex.getMessage());
            return ALWAYS_FALSE;
        }
        return filters.isEmpty() ? ALWAYS_TRUE : (filters.size() == 1 ? (BiomeFilter)filters.get(0) : new AndFilter(filters));
    }

    public static BiomeFilter idFilter(Context cx, String s) {
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
        return s.charAt(0) == '#' ? new TagFilter(s.substring(1)) : new IDFilter(UtilsJS.getMCID(cx, s));
    }
}

