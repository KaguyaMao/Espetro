/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.AndRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.ArrayRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.MappingRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.OrRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentBuilder;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.SimpleMappingRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.TinyMap;
import java.lang.reflect.Array;
import java.util.Map;
import java.util.function.UnaryOperator;
import org.jetbrains.annotations.Nullable;

@Nullable
public interface RecipeComponent<T> {
    public static RecipeComponentBuilder builder() {
        return new RecipeComponentBuilder(4);
    }

    public static RecipeComponentBuilder builder(RecipeKey<?> ... key) {
        RecipeComponentBuilder b = new RecipeComponentBuilder(key.length);
        for (RecipeKey<?> k : key) {
            b.add(k);
        }
        return b;
    }

    default public RecipeKey<T> key(String name) {
        return new RecipeKey(this, name);
    }

    default public ComponentRole role() {
        return ComponentRole.OTHER;
    }

    default public String componentType() {
        return "unknown";
    }

    public Class<?> componentClass();

    default public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return ctx.javaType(this.componentClass());
    }

    public JsonElement write(RecipeJS var1, T var2);

    public T read(RecipeJS var1, Object var2);

    default public void writeToJson(RecipeJS recipe, RecipeComponentValue<T> cv, JsonObject json) {
        if (cv.key.names.size() >= 2) {
            for (String k : cv.key.names) {
                json.remove(k);
            }
        }
        json.add(cv.key.name, this.write(recipe, cv.value));
    }

    default public void readFromJson(RecipeJS recipe, RecipeComponentValue<T> cv, JsonObject json) {
        JsonElement v = json.get(cv.key.name);
        if (v != null) {
            cv.value = this.read(recipe, v);
        } else if (cv.key.names.size() >= 2) {
            for (String alt : cv.key.names) {
                v = json.get(alt);
                if (v == null) continue;
                cv.value = this.read(recipe, v);
                return;
            }
        }
    }

    default public void readFromMap(RecipeJS recipe, RecipeComponentValue<T> cv, Map<?, ?> map) {
        Object v = map.get(cv.key.name);
        if (v != null) {
            cv.value = this.read(recipe, v);
        } else if (cv.key.names.size() >= 2) {
            for (String alt : cv.key.names) {
                v = map.get(alt);
                if (v == null) continue;
                cv.value = this.read(recipe, v);
                return;
            }
        }
    }

    default public boolean hasPriority(RecipeJS recipe, Object from) {
        return false;
    }

    default public boolean isInput(RecipeJS recipe, T value, ReplacementMatch match) {
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    default public T replaceInput(RecipeJS recipe, T original, ReplacementMatch match, InputReplacement with) {
        T t;
        if (original instanceof InputReplacement) {
            InputReplacement r = (InputReplacement)original;
            if (this.isInput(recipe, original, match)) {
                t = this.read(recipe, with.replaceInput(recipe, match, r));
                return t;
            }
        }
        t = original;
        return t;
    }

    default public boolean isOutput(RecipeJS recipe, T value, ReplacementMatch match) {
        return false;
    }

    /*
     * Enabled aggressive block sorting
     */
    default public T replaceOutput(RecipeJS recipe, T original, ReplacementMatch match, OutputReplacement with) {
        T t;
        if (original instanceof OutputReplacement) {
            OutputReplacement r = (OutputReplacement)original;
            if (this.isOutput(recipe, original, match)) {
                t = this.read(recipe, with.replaceOutput(recipe, match, r));
                return t;
            }
        }
        t = original;
        return t;
    }

    default public String checkEmpty(RecipeKey<T> key, T value) {
        return "";
    }

    default public boolean checkValueHasChanged(T oldValue, T newValue) {
        return oldValue != newValue;
    }

    default public ArrayRecipeComponent<T> asArray() {
        Object[] arr = (Object[])Array.newInstance(this.componentClass(), 0);
        return new ArrayRecipeComponent<Object>(this, false, arr.getClass(), arr);
    }

    default public ArrayRecipeComponent<T> asArrayOrSelf() {
        Object[] arr = (Object[])Array.newInstance(this.componentClass(), 0);
        return new ArrayRecipeComponent<Object>(this, true, arr.getClass(), arr);
    }

    default public RecipeComponent<T> orSelf() {
        return this;
    }

    default public <K> RecipeComponent<TinyMap<K, T>> asMap(RecipeComponent<K> key) {
        return new MapRecipeComponent(key, this, false);
    }

    default public RecipeComponent<TinyMap<Character, T>> asPatternKey() {
        return new MapRecipeComponent(StringComponent.CHARACTER, this, true);
    }

    default public <O> OrRecipeComponent<T, O> or(RecipeComponent<O> other) {
        return new OrRecipeComponent(this, other);
    }

    default public <O> AndRecipeComponent<T, O> and(RecipeComponent<O> other) {
        return new AndRecipeComponent(this, other);
    }

    @Info(value="Returns a new RecipeComponent that applies the mappingTo function to the input before it is passed to this component to be read")
    default public MappingRecipeComponent<T> mapIn(UnaryOperator<Object> mappingTo) {
        return this.map(mappingTo, UnaryOperator.identity());
    }

    @Info(value="Returns a new RecipeComponent that applies the mappingFrom function after the component writes to json, before that json is saved")
    default public MappingRecipeComponent<T> mapOut(UnaryOperator<JsonElement> mappingFrom) {
        return this.map(UnaryOperator.identity(), mappingFrom);
    }

    @Info(value="Returns a new RecipeComponent that applies the mappingTo function to the input before it is passed to this component to be read, and the mappingFrom function after the component writes to json, before that json is saved")
    default public MappingRecipeComponent<T> map(UnaryOperator<Object> mappingTo, UnaryOperator<JsonElement> mappingFrom) {
        return new MappingRecipeComponent(this, mappingTo, mappingFrom);
    }

    @Info(value="Returns a new RecipeComponent that maps the keys in a JsonObject according to the provided map, both before the json gets passed to the component and after the component returns a written json object.\nThe mappings should be provided in the format `{recipe: \"component\"}` where recipe is the key as in the recipe, and component is the key as how the RecipeComponent expects it.\nAny keys not included in the provided map will be ignored, and any keys in the provided map that are not in either the input object or output object will be ignored.\nNote that if the input or output is not a JsonObject (ie its an ItemStack, or it is a JsonPrimitive) then that will pass through this without being modified.\nIf you wish to handle those situations use the actual map function")
    default public SimpleMappingRecipeComponent<T> simpleMap(Object mappings) {
        return new SimpleMappingRecipeComponent(this, mappings);
    }
}

