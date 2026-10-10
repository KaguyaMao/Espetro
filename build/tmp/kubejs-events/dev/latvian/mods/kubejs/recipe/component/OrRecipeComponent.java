/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Either
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.OutputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.Optional;

public record OrRecipeComponent<H, L>(RecipeComponent<H> high, RecipeComponent<L> low) implements RecipeComponent<Either<H, L>>
{
    @Override
    public String componentType() {
        return "or";
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return this.high.constructorDescription(ctx).or(this.low.constructorDescription(ctx));
    }

    @Override
    public ComponentRole role() {
        if (this.high.role().isOther()) {
            return this.low.role();
        }
        return this.high.role();
    }

    @Override
    public Class<?> componentClass() {
        return Either.class;
    }

    @Override
    public JsonElement write(RecipeJS recipe, Either<H, L> value) {
        if (value.left().isPresent()) {
            return this.high.write(recipe, value.left().get());
        }
        return this.low.write(recipe, value.right().orElseThrow());
    }

    @Override
    public Either<H, L> read(RecipeJS recipe, Object from) {
        if (this.high.hasPriority(recipe, from)) {
            return Either.left(this.high.read(recipe, from));
        }
        if (this.low.hasPriority(recipe, from)) {
            return Either.right(this.low.read(recipe, from));
        }
        try {
            return Either.left(this.high.read(recipe, from));
        }
        catch (Exception ex1) {
            try {
                return Either.right(this.low.read(recipe, from));
            }
            catch (Exception ex2) {
                ConsoleJS.SERVER.error("Failed to read %s as high priority (%s)!".formatted(from, this.high), ex1);
                ConsoleJS.SERVER.error("Failed to read %s as low priority (%s)!".formatted(from, this.low), ex2);
                throw new RecipeExceptionJS("Failed to read %s as either %s or %s!".formatted(from, this.high, this.low));
            }
        }
    }

    @Override
    public boolean isInput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match) {
        Optional l = value.left();
        return l.isPresent() ? this.high.isInput(recipe, l.get(), match) : this.low.isInput(recipe, value.right().get(), match);
    }

    @Override
    public Either<H, L> replaceInput(RecipeJS recipe, Either<H, L> original, ReplacementMatch match, InputReplacement with) {
        Optional l = original.left();
        if (l.isPresent()) {
            H r = this.high.replaceInput(recipe, l.get(), match, with);
            return r == l.get() ? original : Either.left(r);
        }
        L r = this.low.replaceInput(recipe, original.right().get(), match, with);
        return r == original.right().get() ? original : Either.right(r);
    }

    @Override
    public boolean isOutput(RecipeJS recipe, Either<H, L> value, ReplacementMatch match) {
        Optional l = value.left();
        return l.isPresent() ? this.high.isOutput(recipe, l.get(), match) : this.low.isOutput(recipe, value.right().get(), match);
    }

    @Override
    public Either<H, L> replaceOutput(RecipeJS recipe, Either<H, L> original, ReplacementMatch match, OutputReplacement with) {
        Optional l = original.left();
        if (l.isPresent()) {
            H r = this.high.replaceOutput(recipe, l.get(), match, with);
            return r == l.get() ? original : Either.left(r);
        }
        L r = this.low.replaceOutput(recipe, original.right().get(), match, with);
        return r == original.right().get() ? original : Either.right(r);
    }

    @Override
    public boolean checkValueHasChanged(Either<H, L> oldValue, Either<H, L> newValue) {
        Optional left;
        if (oldValue != null && newValue != null && ((left = oldValue.left()).isPresent() ? this.high.checkValueHasChanged(left.get(), newValue.left().get()) : this.low.checkValueHasChanged(oldValue.right().get(), newValue.right().get()))) {
            return true;
        }
        return oldValue != newValue;
    }

    @Override
    public String toString() {
        return "{" + String.valueOf(this.high) + "|" + String.valueOf(this.low) + "}";
    }
}

