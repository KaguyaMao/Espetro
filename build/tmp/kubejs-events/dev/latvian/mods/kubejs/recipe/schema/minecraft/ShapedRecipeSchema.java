/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package dev.latvian.mods.kubejs.recipe.schema.minecraft;

import dev.latvian.mods.kubejs.item.InputItem;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.RecipeKey;
import dev.latvian.mods.kubejs.recipe.RecipeTypeFunction;
import dev.latvian.mods.kubejs.recipe.component.BooleanComponent;
import dev.latvian.mods.kubejs.recipe.component.ComponentValueMap;
import dev.latvian.mods.kubejs.recipe.component.ItemComponents;
import dev.latvian.mods.kubejs.recipe.component.MapRecipeComponent;
import dev.latvian.mods.kubejs.recipe.component.StringComponent;
import dev.latvian.mods.kubejs.recipe.schema.RecipeSchema;
import dev.latvian.mods.kubejs.util.TinyMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.ListIterator;
import org.apache.commons.lang3.StringUtils;

public interface ShapedRecipeSchema {
    public static final RecipeKey<OutputItem> RESULT = ItemComponents.OUTPUT.key("result");
    public static final RecipeKey<String[]> PATTERN = StringComponent.NON_EMPTY.asArray().key("pattern");
    public static final RecipeKey<TinyMap<Character, InputItem>> KEY = MapRecipeComponent.ITEM_PATTERN_KEY.key("key");
    public static final RecipeKey<Boolean> KJS_MIRROR = BooleanComponent.BOOLEAN.key("kubejs:mirror").preferred("kjsMirror").optional(true).exclude();
    public static final RecipeKey<Boolean> KJS_SHRINK = BooleanComponent.BOOLEAN.key("kubejs:shrink").preferred("kjsShrink").optional(true).exclude();
    public static final RecipeKey<InputItem[][]> INGREDIENTS = ItemComponents.INPUT_ARRAY.asArray().key("ingredients");
    public static final RecipeSchema SCHEMA = new RecipeSchema(ShapedRecipeJS.class, ShapedRecipeJS::new, RESULT, PATTERN, KEY, KJS_MIRROR, KJS_SHRINK).constructor(RESULT, PATTERN, KEY).constructor((recipe, schemaType, keys, from) -> ((ShapedRecipeJS)recipe).set2DValues(from), RESULT, INGREDIENTS).uniqueOutputId(RESULT);

    public static class ShapedRecipeJS
    extends RecipeJS {
        public RecipeJS noMirror() {
            return this.setValue(KJS_MIRROR, false);
        }

        public RecipeJS noShrink() {
            return this.setValue(KJS_SHRINK, false);
        }

        private void set2DValues(ComponentValueMap from) {
            this.setValue(RESULT, from.getValue(this, RESULT));
            InputItem[][] vertical = from.getValue(this, INGREDIENTS);
            if (vertical.length == 0) {
                throw new RecipeExceptionJS("Pattern is empty!");
            }
            ArrayList<String> pattern = new ArrayList<String>();
            HashMap<Character, InputItem> key = new HashMap<Character, InputItem>();
            StringBuilder horizontalPattern = new StringBuilder();
            int id = 0;
            InputItem[][] inputItemArray = vertical;
            int n = inputItemArray.length;
            for (int i = 0; i < n; ++i) {
                InputItem[] horizontal;
                for (InputItem ingredient : horizontal = inputItemArray[i]) {
                    if (!ingredient.isEmpty()) {
                        char currentChar = (char)(65 + id++);
                        horizontalPattern.append(currentChar);
                        key.put(Character.valueOf(currentChar), ingredient);
                        continue;
                    }
                    horizontalPattern.append(' ');
                }
                pattern.add(horizontalPattern.toString());
                horizontalPattern.setLength(0);
            }
            int maxLength = pattern.stream().mapToInt(String::length).max().getAsInt();
            ListIterator<String> iterator = pattern.listIterator();
            while (iterator.hasNext()) {
                iterator.set(StringUtils.rightPad((String)((String)iterator.next()), (int)maxLength));
            }
            this.setValue(PATTERN, pattern.toArray(new String[0]));
            this.setValue(KEY, TinyMap.ofMap(key));
        }

        @Override
        public void afterLoaded() {
            super.afterLoaded();
            String[] pattern = this.getValue(PATTERN);
            TinyMap<Character, InputItem> key = this.getValue(KEY);
            if (pattern.length == 0) {
                throw new RecipeExceptionJS("Pattern is empty!");
            }
            if (key.isEmpty()) {
                throw new RecipeExceptionJS("Key map is empty!");
            }
            ArrayList<Character> airs = null;
            ArrayList entries = new ArrayList(Arrays.asList(key.entries()));
            Iterator<TinyMap.Entry<Character, InputItem>> itr = entries.iterator();
            while (itr.hasNext()) {
                TinyMap.Entry<Character, InputItem> entry = itr.next();
                if (entry.value() != null && !entry.value().isEmpty()) continue;
                if (airs == null) {
                    airs = new ArrayList<Character>(1);
                }
                airs.add(entry.key());
                itr.remove();
            }
            if (airs != null) {
                for (int i = 0; i < pattern.length; ++i) {
                    for (Character a : airs) {
                        pattern[i] = pattern[i].replace(a.charValue(), ' ');
                    }
                }
                this.setValue(PATTERN, pattern);
                this.setValue(KEY, new TinyMap(entries));
            }
        }

        @Override
        public RecipeTypeFunction getSerializationTypeFunction() {
            if (!(this.type != this.type.event.shaped || this.type.event.shaped == this.type.event.vanillaShaped || this.json.has("kubejs:actions") || this.json.has("kubejs:modify_result") || this.json.has("kubejs:stage") || this.json.has("kubejs:mirror") || this.json.has("kubejs:shrink"))) {
                return this.type.event.vanillaShaped;
            }
            return super.getSerializationTypeFunction();
        }
    }
}

