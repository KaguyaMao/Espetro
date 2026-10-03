/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.NativeJavaObject
 *  dev.latvian.mods.rhino.ScriptableObject
 *  dev.latvian.mods.rhino.Wrapper
 *  net.minecraft.resources.ResourceLocation
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.ScriptableObject;
import dev.latvian.mods.rhino.Wrapper;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public record StringComponent(String error, Predicate<String> predicate) implements RecipeComponent<String>
{
    public static final RecipeComponent<String> ANY = new StringComponent("", s -> true);
    public static final RecipeComponent<String> NON_EMPTY = new StringComponent("can't be empty", s -> !s.isEmpty());
    public static final RecipeComponent<String> NON_BLANK = new StringComponent("can't be blank", s -> !s.isBlank());
    public static final RecipeComponent<String> ID = new StringComponent("invalid ID", ResourceLocation::m_135830_);
    public static final RecipeComponent<Character> CHARACTER = new RecipeComponent<Character>(){

        @Override
        public String componentType() {
            return "char";
        }

        @Override
        public Class<?> componentClass() {
            return Character.class;
        }

        @Override
        public TypeDescJS constructorDescription(DescriptionContext ctx) {
            return TypeDescJS.STRING;
        }

        @Override
        public JsonElement write(RecipeJS recipe, Character value) {
            return new JsonPrimitive(value);
        }

        @Override
        public Character read(RecipeJS recipe, Object from) {
            Character c;
            return from instanceof Character ? (c = (Character)from) : Character.valueOf(String.valueOf(from).charAt(0));
        }

        public String toString() {
            return this.componentType();
        }
    };
    public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(TypeDescJS.object().add("error", TypeDescJS.STRING, true).add("filter", TypeDescJS.ANY), (cx, scope, args) -> {
        Object object;
        String error = String.valueOf(Wrapper.unwrapped((Object)args.getOrDefault("error", "invalid string")));
        Object patt2211$temp = args.get("filter");
        if (patt2211$temp instanceof ScriptableObject) {
            ScriptableObject obj = (ScriptableObject)patt2211$temp;
            object = NativeJavaObject.createInterfaceAdapter((Context)cx, Predicate.class, (ScriptableObject)obj);
        } else {
            object = UtilsJS.ALWAYS_TRUE;
        }
        Predicate<Object> filter = object;
        return new StringComponent(error, filter);
    });

    @Override
    public String componentType() {
        return "string";
    }

    @Override
    public Class<?> componentClass() {
        return String.class;
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return TypeDescJS.STRING;
    }

    public JsonPrimitive write(RecipeJS recipe, String value) {
        return new JsonPrimitive(value);
    }

    @Override
    public String read(RecipeJS recipe, Object from) {
        String str;
        if (from instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)from;
            v0 = json.getAsString();
        } else {
            v0 = str = String.valueOf(from);
        }
        if (!this.predicate.test(str)) {
            if (this.error.isEmpty()) {
                throw new IllegalArgumentException("Invalid string '" + str + "'");
            }
            throw new IllegalArgumentException("Invalid string '" + str + "': " + this.error);
        }
        return str;
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        JsonPrimitive json;
        return from instanceof CharSequence || from instanceof JsonPrimitive && (json = (JsonPrimitive)from).isString();
    }

    @Override
    public String toString() {
        return this.componentType();
    }
}

