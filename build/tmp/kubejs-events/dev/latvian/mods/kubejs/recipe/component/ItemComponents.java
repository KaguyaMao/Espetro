/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.ItemMatch;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.kubejs.recipe.component.ComponentRole;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentValue;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponentWithParent;
import dev.latvian.mods.kubejs.util.TinyMap;
import java.util.Map;

public interface ItemComponents {
    public static final RecipeComponent<InputItem> INPUT = new RecipeComponent<InputItem>(){

        @Override
        public String componentType() {
            return "input_item";
        }

        @Override
        public ComponentRole role() {
            return ComponentRole.INPUT;
        }

        @Override
        public Class<?> componentClass() {
            return InputItem.class;
        }

        @Override
        public boolean hasPriority(RecipeJS recipe, Object from) {
            return recipe.inputItemHasPriority(from);
        }

        @Override
        public JsonElement write(RecipeJS recipe, InputItem value) {
            return recipe.writeInputItem(value);
        }

        @Override
        public InputItem read(RecipeJS recipe, Object from) {
            return recipe.readInputItem(from);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public boolean isInput(RecipeJS recipe, InputItem value, ReplacementMatch match) {
            if (!(match instanceof ItemMatch)) return false;
            ItemMatch m = (ItemMatch)match;
            if (!value.validForMatching()) return false;
            if (!m.contains(value.ingredient)) return false;
            return true;
        }

        @Override
        public String checkEmpty(RecipeKey<InputItem> key, InputItem value) {
            if (value.isEmpty()) {
                return "Ingredient '" + key.name + "' can't be empty!";
            }
            return "";
        }

        @Override
        public RecipeComponent<TinyMap<Character, InputItem>> asPatternKey() {
            return MapRecipeComponent.ITEM_PATTERN_KEY;
        }

        public String toString() {
            return this.componentType();
        }
    };
    public static final RecipeComponent<InputItem[]> INPUT_ARRAY = INPUT.asArray();
    public static final RecipeComponent<InputItem[]> UNWRAPPED_INPUT_ARRAY = new RecipeComponentWithParent<InputItem[]>(){

        @Override
        public RecipeComponent<InputItem[]> parentComponent() {
            return INPUT_ARRAY;
        }

        @Override
        public JsonElement write(RecipeJS recipe, InputItem[] value) {
            JsonArray json = new JsonArray();
            for (InputItem in : value) {
                for (InputItem in1 : in.unwrap()) {
                    json.add(INPUT.write(recipe, in1));
                }
            }
            return json;
        }

        public String toString() {
            return this.parentComponent().toString();
        }
    };
    public static final RecipeComponent<OutputItem> OUTPUT = new RecipeComponent<OutputItem>(){

        @Override
        public String componentType() {
            return "output_item";
        }

        @Override
        public ComponentRole role() {
            return ComponentRole.OUTPUT;
        }

        @Override
        public Class<?> componentClass() {
            return OutputItem.class;
        }

        @Override
        public boolean hasPriority(RecipeJS recipe, Object from) {
            return recipe.outputItemHasPriority(from);
        }

        @Override
        public JsonElement write(RecipeJS recipe, OutputItem value) {
            return recipe.writeOutputItem(value);
        }

        @Override
        public OutputItem read(RecipeJS recipe, Object from) {
            return recipe.readOutputItem(from);
        }

        /*
         * Enabled force condition propagation
         * Lifted jumps to return sites
         */
        @Override
        public boolean isOutput(RecipeJS recipe, OutputItem value, ReplacementMatch match) {
            if (!(match instanceof ItemMatch)) return false;
            ItemMatch m = (ItemMatch)match;
            if (value.isEmpty()) return false;
            if (!m.contains(value.item)) return false;
            return true;
        }

        @Override
        public String checkEmpty(RecipeKey<OutputItem> key, OutputItem value) {
            if (value.isEmpty()) {
                return "ItemStack '" + key.name + "' can't be empty!";
            }
            return "";
        }

        public String toString() {
            return this.componentType();
        }
    };
    public static final RecipeComponent<OutputItem[]> OUTPUT_ARRAY = OUTPUT.asArray();
    public static final RecipeComponent<OutputItem> OUTPUT_ID_WITH_COUNT = new RecipeComponentWithParent<OutputItem>(){

        @Override
        public RecipeComponent<OutputItem> parentComponent() {
            return OUTPUT;
        }

        @Override
        public void writeToJson(RecipeJS recipe, RecipeComponentValue<OutputItem> cv, JsonObject json) {
            json.addProperty(cv.key.name, ((OutputItem)cv.value).item.kjs$getId());
            json.addProperty("count", (Number)((OutputItem)cv.value).item.m_41613_());
        }

        @Override
        public void readFromJson(RecipeJS recipe, RecipeComponentValue<OutputItem> cv, JsonObject json) {
            RecipeComponentWithParent.super.readFromJson(recipe, cv, json);
            if (cv.value != null && json.has("count")) {
                ((OutputItem)cv.value).item.m_41764_(json.get("count").getAsInt());
            }
        }

        @Override
        public void readFromMap(RecipeJS recipe, RecipeComponentValue<OutputItem> cv, Map<?, ?> map) {
            RecipeComponentWithParent.super.readFromMap(recipe, cv, map);
            if (cv.value != null && map.containsKey("count")) {
                ((OutputItem)cv.value).item.m_41764_(((Number)map.get("count")).intValue());
            }
        }

        public String toString() {
            return this.parentComponent().toString();
        }
    };
}

