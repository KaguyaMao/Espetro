/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.mojang.datafixers.util.Either
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import dev.latvian.mods.kubejs.fluid.FluidLike;
import dev.latvian.mods.kubejs.fluid.InputFluid;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import org.jetbrains.annotations.Nullable;

public interface FluidComponents {
    public static final RecipeComponent<InputFluid> INPUT = new RecipeComponent<InputFluid>(){

        @Override
        public String componentType() {
            return "input_fluid";
        }

        @Override
        public ComponentRole role() {
            return ComponentRole.INPUT;
        }

        @Override
        public Class<?> componentClass() {
            return InputFluid.class;
        }

        @Override
        public JsonElement write(RecipeJS recipe, InputFluid value) {
            return recipe.writeInputFluid(value);
        }

        @Override
        public InputFluid read(RecipeJS recipe, Object from) {
            return recipe.readInputFluid(from);
        }

        @Override
        public boolean hasPriority(RecipeJS recipe, Object from) {
            return recipe.inputFluidHasPriority(from);
        }

        @Override
        public boolean isInput(RecipeJS recipe, InputFluid value, ReplacementMatch match) {
            FluidLike m;
            return match instanceof FluidLike && value.matches(m = (FluidLike)match);
        }

        @Override
        public String checkEmpty(RecipeKey<InputFluid> key, InputFluid value) {
            if (value.kjs$isEmpty()) {
                return "Input fluid '" + key.name + "' can't be empty!";
            }
            return "";
        }

        public String toString() {
            return this.componentType();
        }
    };
    public static final RecipeComponent<InputFluid[]> INPUT_ARRAY = INPUT.asArray();
    public static final RecipeComponent<Either<InputFluid, InputItem>> INPUT_OR_ITEM = INPUT.or(ItemComponents.INPUT);
    public static final RecipeComponent<Either<InputFluid, InputItem>[]> INPUT_OR_ITEM_ARRAY = INPUT_OR_ITEM.asArray();
    public static final RecipeComponent<OutputFluid> OUTPUT = new RecipeComponent<OutputFluid>(){

        @Override
        public String componentType() {
            return "output_fluid";
        }

        @Override
        public ComponentRole role() {
            return ComponentRole.OUTPUT;
        }

        @Override
        public Class<?> componentClass() {
            return OutputFluid.class;
        }

        @Override
        @Nullable
        public JsonElement write(RecipeJS recipe, OutputFluid value) {
            return recipe.writeOutputFluid(value);
        }

        @Override
        public OutputFluid read(RecipeJS recipe, Object from) {
            return recipe.readOutputFluid(from);
        }

        @Override
        public boolean hasPriority(RecipeJS recipe, Object from) {
            return recipe.outputFluidHasPriority(from);
        }

        @Override
        public boolean isOutput(RecipeJS recipe, OutputFluid value, ReplacementMatch match) {
            FluidLike m;
            return match instanceof FluidLike && value.matches(m = (FluidLike)match);
        }

        @Override
        public String checkEmpty(RecipeKey<OutputFluid> key, OutputFluid value) {
            if (value.kjs$isEmpty()) {
                return "Output fluid '" + key.name + "' can't be empty!";
            }
            return "";
        }

        public String toString() {
            return this.componentType();
        }
    };
    public static final RecipeComponent<OutputFluid[]> OUTPUT_ARRAY = OUTPUT.asArray();
    public static final RecipeComponent<Either<OutputFluid, OutputItem>> OUTPUT_OR_ITEM = OUTPUT.or(ItemComponents.OUTPUT);
    public static final RecipeComponent<Either<OutputFluid, OutputItem>[]> OUTPUT_OR_ITEM_ARRAY = OUTPUT_OR_ITEM.asArray();
}

