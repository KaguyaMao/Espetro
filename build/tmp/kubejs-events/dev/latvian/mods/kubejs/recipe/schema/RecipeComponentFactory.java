/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.BaseFunction
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.util.CustomJavaToJsWrapper
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.rhino.BaseFunction;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.util.CustomJavaToJsWrapper;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

@FunctionalInterface
public interface RecipeComponentFactory
extends CustomJavaToJsWrapper {
    @Nullable
    public RecipeComponent<?> create(Context var1, Scriptable var2, Map<String, Object> var3);

    default public Scriptable convertJavaToJs(Context cx, Scriptable scope, Class<?> staticType) {
        return new RecipeComponentFactoryJS(this);
    }

    public static class RecipeComponentFactoryJS
    extends BaseFunction {
        private final RecipeComponentFactory factory;

        public RecipeComponentFactoryJS(RecipeComponentFactory factory) {
            this.factory = factory;
        }

        public Object call(Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
            Map<String, Object> map = args.length == 0 ? Map.of() : MapJS.of(args[0]);
            RecipeComponent<?> component = this.factory.create(cx, scope, map);
            if (component == null) {
                throw new RuntimeException("Invalid dynamic recipe component arguments: " + String.valueOf(map));
            }
            return component;
        }
    }

    public record Dynamic(DynamicRecipeComponent component) implements RecipeComponentFactory
    {
        @Override
        @Nullable
        public RecipeComponent<?> create(Context cx, Scriptable scope, Map<String, Object> args) {
            return this.component.factory().create(cx, scope, args);
        }
    }

    public record Simple(RecipeComponent<?> component) implements RecipeComponentFactory
    {
        @Override
        public RecipeComponent<?> create(Context cx, Scriptable scope, Map<String, Object> args) {
            return this.component;
        }
    }
}

