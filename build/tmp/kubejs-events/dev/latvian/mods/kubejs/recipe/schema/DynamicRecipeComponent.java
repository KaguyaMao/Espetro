/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Scriptable
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.schema;

import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Scriptable;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

public record DynamicRecipeComponent(TypeDescJS desc, Factory factory) {

    public static interface Factory {
        @Nullable
        public RecipeComponent<?> create(Context var1, Scriptable var2, Map<String, Object> var3);
    }
}

