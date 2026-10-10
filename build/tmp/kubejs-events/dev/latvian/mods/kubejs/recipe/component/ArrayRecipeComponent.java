/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;

public record ArrayRecipeComponent<T>(RecipeComponent<T> component, boolean canWriteSelf, Class<?> arrayClass, T[] emptyArray) implements RecipeComponent<T[]>
{
    @Override
    public ComponentRole role() {
        return this.component.role();
    }

    @Override
    public String componentType() {
        return "array";
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        TypeDescJS d = this.component.constructorDescription(ctx);
        if (this.canWriteSelf) {
            return d.or(d.asArray());
        }
        return d.asArray();
    }

    @Override
    public Class<?> componentClass() {
        return this.arrayClass;
    }

    public T[] newArray(int length) {
        return length == 0 ? this.emptyArray : (Object[])Array.newInstance(this.component.componentClass(), length);
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        return from instanceof Iterable || from != null && from.getClass().isArray();
    }

    @Override
    public JsonElement write(RecipeJS recipe, T[] value) {
        if (this.canWriteSelf && value.length == 1) {
            JsonElement v1 = this.component.write(recipe, value[0]);
            return v1 == null ? new JsonArray() : v1;
        }
        JsonArray arr = new JsonArray(value.length);
        for (T v : value) {
            JsonElement v1 = this.component.write(recipe, v);
            if (v1 == null) continue;
            arr.add(v1);
        }
        return arr;
    }

    @Override
    public T[] read(RecipeJS recipe, Object from) {
        if (from.getClass() == this.arrayClass) {
            return (Object[])from;
        }
        if (from instanceof Iterable) {
            int size;
            Iterable iterable = (Iterable)from;
            if (iterable instanceof Collection) {
                Collection c = (Collection)iterable;
                size = c.size();
            } else if (iterable instanceof JsonArray) {
                JsonArray a = (JsonArray)iterable;
                size = a.size();
            } else {
                size = -1;
            }
            if (size == 0) {
                return this.emptyArray;
            }
            if (size > 0) {
                T[] arr = this.newArray(size);
                int i = 0;
                for (Object e : iterable) {
                    arr[i] = this.component.read(recipe, e);
                    ++i;
                }
                return arr;
            }
            ArrayList<T> list = new ArrayList<T>();
            for (Object e : iterable) {
                list.add(this.component.read(recipe, e));
            }
            return list.toArray(this.newArray(list.size()));
        }
        if (from.getClass().isArray()) {
            T[] arr = this.newArray(Array.getLength(from));
            for (int i = 0; i < arr.length; ++i) {
                arr[i] = this.component.read(recipe, Array.get(from, i));
            }
            return arr;
        }
        T[] arr = this.newArray(1);
        arr[0] = this.component.read(recipe, from);
        return arr;
    }

    @Override
    public boolean isInput(RecipeJS recipe, T[] value, ReplacementMatch match) {
        for (T v : value) {
            if (!this.component.isInput(recipe, v, match)) continue;
            return true;
        }
        return false;
    }

    @Override
    public T[] replaceInput(RecipeJS recipe, T[] original, ReplacementMatch match, InputReplacement with) {
        T[] arr = original;
        for (int i = 0; i < original.length; ++i) {
            T r = this.component.replaceInput(recipe, original[i], match, with);
            if (arr[i] == r) continue;
            if (arr == original) {
                arr = this.newArray(original.length);
                System.arraycopy(original, 0, arr, 0, i);
            }
            arr[i] = r;
        }
        return arr;
    }

    @Override
    public boolean isOutput(RecipeJS recipe, T[] value, ReplacementMatch match) {
        for (T v : value) {
            if (!this.component.isOutput(recipe, v, match)) continue;
            return true;
        }
        return false;
    }

    @Override
    public T[] replaceOutput(RecipeJS recipe, T[] original, ReplacementMatch match, OutputReplacement with) {
        T[] arr = original;
        for (int i = 0; i < original.length; ++i) {
            T r = this.component.replaceOutput(recipe, original[i], match, with);
            if (arr[i] == r) continue;
            if (arr == original) {
                arr = this.newArray(original.length);
                System.arraycopy(original, 0, arr, 0, i);
            }
            arr[i] = r;
        }
        return arr;
    }

    @Override
    public String toString() {
        return this.component.toString() + "[]";
    }

    public T[] add(T[] array, T value) {
        T[] arr = this.newArray(array.length + 1);
        System.arraycopy(array, 0, arr, 0, array.length);
        arr[array.length] = value;
        return arr;
    }

    public T[] addAll(T[] array, T ... values) {
        T[] arr = this.newArray(array.length + values.length);
        System.arraycopy(array, 0, arr, 0, array.length);
        System.arraycopy(values, 0, arr, array.length, values.length);
        return arr;
    }

    public T[] remove(T[] array, int index) {
        T[] arr = this.newArray(array.length - 1);
        System.arraycopy(array, 0, arr, 0, index);
        System.arraycopy(array, index + 1, arr, index, array.length - index - 1);
        return arr;
    }
}

