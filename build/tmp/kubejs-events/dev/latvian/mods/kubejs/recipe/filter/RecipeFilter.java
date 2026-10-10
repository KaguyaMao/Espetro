/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.Event
 *  dev.architectury.event.EventFactory
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.regexp.NativeRegExp
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.filter;

import dev.architectury.event.Event;
import dev.architectury.event.EventFactory;
import dev.latvian.mods.kubejs.core.RecipeKJS;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.filter.AndFilter;
import dev.latvian.mods.kubejs.recipe.filter.ConstantFilter;
import dev.latvian.mods.kubejs.recipe.filter.GroupFilter;
import dev.latvian.mods.kubejs.recipe.filter.IDFilter;
import dev.latvian.mods.kubejs.recipe.filter.InputFilter;
import dev.latvian.mods.kubejs.recipe.filter.ModFilter;
import dev.latvian.mods.kubejs.recipe.filter.NotFilter;
import dev.latvian.mods.kubejs.recipe.filter.OrFilter;
import dev.latvian.mods.kubejs.recipe.filter.OutputFilter;
import dev.latvian.mods.kubejs.recipe.filter.RecipeFilterParseEvent;
import dev.latvian.mods.kubejs.recipe.filter.RegexIDFilter;
import dev.latvian.mods.kubejs.recipe.filter.TypeFilter;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.regexp.NativeRegExp;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RecipeFilter
extends Predicate<RecipeKJS> {
    public static final Event<RecipeFilterParseEvent> PARSE = EventFactory.createLoop((Object[])new RecipeFilterParseEvent[0]);

    @Override
    public boolean test(RecipeKJS var1);

    public static RecipeFilter of(Context cx, @Nullable Object o) {
        if (o == null || o == ConstantFilter.TRUE) {
            return ConstantFilter.TRUE;
        }
        if (o == ConstantFilter.FALSE) {
            return ConstantFilter.FALSE;
        }
        if (o instanceof CharSequence || o instanceof NativeRegExp || o instanceof Pattern) {
            String s = o.toString();
            if (s.equals("*")) {
                return ConstantFilter.TRUE;
            }
            if (s.equals("-")) {
                return ConstantFilter.FALSE;
            }
            Pattern r = UtilsJS.parseRegex(s);
            return r == null ? new IDFilter(UtilsJS.getMCID(cx, s)) : RegexIDFilter.of(r);
        }
        List<?> list = ListJS.orSelf(o);
        if (list.isEmpty()) {
            return ConstantFilter.FALSE;
        }
        if (list.size() > 1) {
            OrFilter predicate = new OrFilter();
            for (Object o1 : list) {
                RecipeFilter p = RecipeFilter.of(cx, o1);
                if (p == ConstantFilter.TRUE) {
                    return ConstantFilter.TRUE;
                }
                if (p == ConstantFilter.FALSE) continue;
                predicate.list.add(p);
            }
            return predicate.list.isEmpty() ? ConstantFilter.FALSE : (predicate.list.size() == 1 ? predicate.list.get(0) : predicate);
        }
        Map<?, ?> map = MapJS.of(list.get(0));
        if (map == null || map.isEmpty()) {
            return ConstantFilter.TRUE;
        }
        AndFilter predicate = new AndFilter();
        if (map.get("or") != null) {
            predicate.list.add(RecipeFilter.of(cx, map.get("or")));
        }
        if (map.get("not") != null) {
            predicate.list.add(new NotFilter(RecipeFilter.of(cx, map.get("not"))));
        }
        try {
            Object output;
            Object input;
            Object mod;
            Object group;
            Object type;
            Object id = map.get("id");
            if (id != null) {
                Pattern pattern = UtilsJS.parseRegex(id);
                predicate.list.add(pattern == null ? new IDFilter(UtilsJS.getMCID(cx, id)) : RegexIDFilter.of(pattern));
            }
            if ((type = map.get("type")) != null) {
                predicate.list.add(new TypeFilter(UtilsJS.getMCID(cx, type)));
            }
            if ((group = map.get("group")) != null) {
                predicate.list.add(new GroupFilter(group.toString()));
            }
            if ((mod = map.get("mod")) != null) {
                predicate.list.add(new ModFilter(mod.toString()));
            }
            if ((input = map.get("input")) != null) {
                predicate.list.add(new InputFilter(ReplacementMatch.of(input)));
            }
            if ((output = map.get("output")) != null) {
                predicate.list.add(new OutputFilter(ReplacementMatch.of(output)));
            }
            ((RecipeFilterParseEvent)PARSE.invoker()).parse(cx, predicate.list, map);
            return predicate.list.isEmpty() ? ConstantFilter.TRUE : (predicate.list.size() == 1 ? predicate.list.get(0) : predicate);
        }
        catch (RecipeExceptionJS rex) {
            if (rex.error) {
                ConsoleJS.getCurrent(cx).error(rex.getMessage());
            } else {
                ConsoleJS.getCurrent(cx).warn(rex.getMessage());
            }
            return ConstantFilter.FALSE;
        }
    }
}

