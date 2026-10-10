/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  org.apache.commons.lang3.tuple.Pair
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import java.util.Iterator;
import org.apache.commons.lang3.tuple.Pair;

public record AndRecipeComponent<A, B>(RecipeComponent<A> a, RecipeComponent<B> b) implements RecipeComponent<Pair<A, B>>
{
    @Override
    public String componentType() {
        return "and";
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return TypeDescJS.fixedArray(this.a.constructorDescription(ctx), this.b.constructorDescription(ctx));
    }

    @Override
    public ComponentRole role() {
        if (this.a.role().isOther()) {
            return this.b.role();
        }
        return this.a.role();
    }

    @Override
    public Class<?> componentClass() {
        return Pair.class;
    }

    public JsonArray write(RecipeJS recipe, Pair<A, B> value) {
        JsonArray json = new JsonArray();
        json.add(this.a.write(recipe, value.getLeft()));
        json.add(this.b.write(recipe, value.getRight()));
        return json;
    }

    @Override
    public Pair<A, B> read(RecipeJS recipe, Object from) {
        if (from instanceof Iterable) {
            Iterable iterable = (Iterable)from;
            Iterator itr = iterable.iterator();
            return Pair.of(this.a.read(recipe, itr.next()), this.b.read(recipe, itr.next()));
        }
        throw new IllegalArgumentException("Expected JSON array!");
    }

    @Override
    public String toString() {
        return "{" + String.valueOf(this.a) + "&" + String.valueOf(this.b) + "}";
    }
}

