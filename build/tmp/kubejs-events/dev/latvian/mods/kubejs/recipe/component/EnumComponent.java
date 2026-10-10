/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 *  dev.latvian.mods.rhino.NativeJavaClass
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.NativeJavaClass;
import java.util.function.BiFunction;
import java.util.function.Function;

public record EnumComponent<T extends Enum<T>>(Class<T> enumType, Function<T, String> toStringFunc, BiFunction<Class<T>, String, T> toEnumFunc) implements RecipeComponent<T>
{
    public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(TypeDescJS.object().add("class", TypeDescJS.STRING.or(DescriptionContext.DEFAULT.javaType(Class.class))), (ctx, scope, args) -> {
        Class clazz;
        Object o = args.get("class");
        if (o instanceof NativeJavaClass) {
            NativeJavaClass njc = (NativeJavaClass)o;
            clazz = njc.getClassObject();
        } else if (o instanceof Class) {
            Class c;
            clazz = c = (Class)o;
        } else {
            try {
                return new EnumComponent(Class.forName(String.valueOf(o)));
            }
            catch (ClassNotFoundException e) {
                throw new RecipeExceptionJS("Error loading class " + String.valueOf(o) + " for EnumComponent", e);
            }
        }
        if (!clazz.isEnum()) {
            throw new RecipeExceptionJS("Class " + clazz.getTypeName() + " is not an enum!");
        }
        return new EnumComponent(clazz);
    });
    private static final Function<Enum<?>, String> DEFAULT_TO_STRING = e -> e.name().toLowerCase();
    private static final BiFunction<Class<? extends Enum<?>>, String, Enum<?>> DEFAULT_TO_ENUM = (c, s) -> {
        for (Enum e : (Enum[])c.getEnumConstants()) {
            if (!e.name().equalsIgnoreCase((String)s)) continue;
            return e;
        }
        return null;
    };

    public EnumComponent(Class<T> enumType) {
        this(enumType, (Function)UtilsJS.cast(DEFAULT_TO_STRING), (BiFunction)UtilsJS.cast(DEFAULT_TO_ENUM));
    }

    @Override
    public String componentType() {
        return "enum";
    }

    @Override
    public Class<?> componentClass() {
        return this.enumType;
    }

    public JsonPrimitive write(RecipeJS recipe, T value) {
        return new JsonPrimitive(this.toStringFunc.apply(value));
    }

    @Override
    public T read(RecipeJS recipe, Object from) {
        Enum e;
        if (this.enumType.isInstance(from)) {
            return (T)((Enum)from);
        }
        if (from == null) {
            v0 = null;
        } else {
            String string;
            if (from instanceof JsonPrimitive) {
                JsonPrimitive j = (JsonPrimitive)from;
                string = j.getAsString();
            } else {
                string = String.valueOf(from);
            }
            v0 = e = (Enum)this.toEnumFunc.apply(this.enumType, string);
        }
        if (e == null) {
            throw new RecipeExceptionJS("Enum value '" + String.valueOf(from) + "' of " + this.enumType.getName() + " not found");
        }
        return (T)e;
    }

    @Override
    public String toString() {
        return this.componentType();
    }
}

