/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.recipe.component;

import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.util.WrappedJS;
import java.util.Map;
import java.util.Objects;

public final class RecipeComponentValue<T>
implements WrappedJS,
Map.Entry<RecipeKey<T>, T> {
    public static final RecipeComponentValue<?>[] EMPTY_ARRAY = new RecipeComponentValue[0];
    public final RecipeKey<T> key;
    public final int index;
    public T value;
    public boolean write;

    public RecipeComponentValue(RecipeKey<T> key, int index) {
        this.key = key;
        this.index = index;
        this.value = null;
        this.write = false;
    }

    public RecipeComponentValue<T> copy() {
        RecipeComponentValue<T> copy = new RecipeComponentValue<T>(this.key, this.index);
        copy.value = this.value;
        copy.write = this.write;
        return copy;
    }

    public boolean isInput(RecipeJS recipe, ReplacementMatch match) {
        return this.value != null && this.key.component.role().isInput() && this.key.component.isInput(recipe, this.value, match);
    }

    public boolean replaceInput(RecipeJS recipe, ReplacementMatch match, InputReplacement with) {
        Object newValue;
        if (!this.key.component.role().isInput()) {
            return false;
        }
        Object t = newValue = this.value == null ? null : (Object)this.key.component.replaceInput(recipe, this.value, match, with);
        if (this.key.component.checkValueHasChanged(this.value, newValue)) {
            this.value = newValue;
            this.write();
            return true;
        }
        return false;
    }

    public boolean isOutput(RecipeJS recipe, ReplacementMatch match) {
        return this.value != null && this.key.component.role().isOutput() && this.key.component.isOutput(recipe, this.value, match);
    }

    public boolean replaceOutput(RecipeJS recipe, ReplacementMatch match, OutputReplacement with) {
        Object newValue;
        if (!this.key.component.role().isOutput()) {
            return false;
        }
        Object t = newValue = this.value == null ? null : (Object)this.key.component.replaceOutput(recipe, this.value, match, with);
        if (this.key.component.checkValueHasChanged(this.value, newValue)) {
            this.value = newValue;
            this.write();
            return true;
        }
        return false;
    }

    @Override
    public RecipeKey<T> getKey() {
        return this.key;
    }

    public int getIndex() {
        return this.index;
    }

    @Override
    public T getValue() {
        return this.value;
    }

    @Override
    public T setValue(T newValue) {
        T v = this.value;
        this.value = newValue;
        return v;
    }

    public boolean shouldWrite() {
        return this.write;
    }

    public void write() {
        this.write = true;
    }

    public String toString() {
        return "%s = %s".formatted(this.key.name, this.value);
    }

    @Override
    public boolean equals(Object obj) {
        Map.Entry e;
        return obj == this || obj instanceof Map.Entry && this.key == (e = (Map.Entry)obj).getKey() && Objects.equals(this.value, e.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.key, this.value);
    }

    public String checkEmpty() {
        return this.key.allowEmpty ? "" : (this.value != null ? this.key.component.checkEmpty(this.key, this.value) : (this.key.optional() ? "" : "Value of '" + this.key.name + "' can't be null!"));
    }
}

