/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import org.jetbrains.annotations.Nullable;

public interface RecipeComponentWithParent<T>
extends RecipeComponent<T> {
    public RecipeComponent<T> parentComponent();

    @Override
    default public ComponentRole role() {
        return this.parentComponent().role();
    }

    @Override
    default public String componentType() {
        return this.parentComponent().componentType();
    }

    @Override
    default public Class<?> componentClass() {
        return this.parentComponent().componentClass();
    }

    @Override
    default public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return this.parentComponent().constructorDescription(ctx);
    }

    @Override
    @Nullable
    default public JsonElement write(RecipeJS recipe, T value) {
        return this.parentComponent().write(recipe, value);
    }

    @Override
    default public T read(RecipeJS recipe, Object from) {
        return this.parentComponent().read(recipe, from);
    }

    @Override
    default public boolean hasPriority(RecipeJS recipe, Object from) {
        return this.parentComponent().hasPriority(recipe, from);
    }

    @Override
    default public boolean isInput(RecipeJS recipe, T value, ReplacementMatch match) {
        return this.parentComponent().isInput(recipe, value, match);
    }

    @Override
    default public T replaceInput(RecipeJS recipe, T original, ReplacementMatch match, InputReplacement with) {
        return this.parentComponent().replaceInput(recipe, original, match, with);
    }

    @Override
    default public boolean isOutput(RecipeJS recipe, T value, ReplacementMatch match) {
        return this.parentComponent().isOutput(recipe, value, match);
    }

    @Override
    default public T replaceOutput(RecipeJS recipe, T original, ReplacementMatch match, OutputReplacement with) {
        return this.parentComponent().replaceOutput(recipe, original, match, with);
    }

    @Override
    default public String checkEmpty(RecipeKey<T> key, T value) {
        return this.parentComponent().checkEmpty(key, value);
    }
}

