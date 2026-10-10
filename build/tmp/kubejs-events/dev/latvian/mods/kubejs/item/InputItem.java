/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.mod.util.JsonSerializable
 *  dev.latvian.mods.rhino.util.RemapForJS
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 */
package dev.latvian.mods.kubejs.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientSupplierKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.OutputItem;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.platform.IngredientPlatformHelper;
import dev.latvian.mods.kubejs.platform.RecipePlatformHelper;
import dev.latvian.mods.kubejs.recipe.InputReplacement;
import dev.latvian.mods.kubejs.recipe.RecipeExceptionJS;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.ReplacementMatch;
import dev.latvian.mods.rhino.mod.util.JsonSerializable;
import dev.latvian.mods.rhino.util.RemapForJS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

public class InputItem
implements IngredientSupplierKJS,
InputReplacement,
JsonSerializable {
    public static final InputItem EMPTY = new InputItem(Ingredient.f_43901_, 0);
    public static final Map<String, InputItem> PARSE_CACHE = new HashMap<String, InputItem>();
    public final Ingredient ingredient;
    public final int count;

    public static InputItem of(Ingredient ingredient, int count) {
        return count <= 0 || ingredient == Ingredient.f_43901_ ? EMPTY : new InputItem(ingredient, count);
    }

    public static InputItem of(Object o) {
        if (o instanceof InputItem) {
            InputItem in = (InputItem)o;
            return in;
        }
        if (o instanceof ItemStack) {
            ItemStack stack = (ItemStack)o;
            return stack.m_41619_() ? EMPTY : InputItem.of(Ingredient.m_43927_((ItemStack[])new ItemStack[]{stack}), stack.m_41613_());
        }
        if (o instanceof OutputItem) {
            OutputItem out = (OutputItem)o;
            return out.isEmpty() ? EMPTY : InputItem.of(Ingredient.m_43927_((ItemStack[])new ItemStack[]{out.item}), out.getCount());
        }
        if (o instanceof CharSequence) {
            String str = o.toString();
            if (str.isEmpty() || str.equals("air")) {
                return EMPTY;
            }
            InputItem cached = PARSE_CACHE.get(str);
            if (cached != null) {
                return cached;
            }
            int x = str.indexOf(120);
            if (x > 0 && x < str.length() - 2 && str.charAt(x + 1) == ' ') {
                try {
                    Ingredient ingredient = IngredientJS.of(str.substring(x + 2));
                    if (ingredient == Ingredient.f_43901_) {
                        return EMPTY;
                    }
                    int count = Integer.parseInt(str.substring(0, x));
                    cached = InputItem.of(IngredientJS.of(str.substring(x + 2)), count);
                }
                catch (Exception ignore) {
                    throw new RecipeExceptionJS("Invalid item input: " + str);
                }
            }
            if (cached == null) {
                cached = InputItem.of(IngredientJS.of(str), 1);
            }
            PARSE_CACHE.put(str, cached);
            return cached;
        }
        if (o instanceof JsonElement) {
            JsonElement json = (JsonElement)o;
            return InputItem.ofJson(json);
        }
        return InputItem.of(IngredientJS.of(o), 1);
    }

    static InputItem ofJson(JsonElement json) {
        if (json == null || json.isJsonNull() || json.isJsonArray() && json.getAsJsonArray().isEmpty()) {
            return EMPTY;
        }
        if (json.isJsonPrimitive()) {
            return InputItem.of(json.getAsString());
        }
        if (json.isJsonObject()) {
            int count;
            JsonObject o = json.getAsJsonObject();
            boolean val = o.has("value");
            int n = count = o.has("count") ? o.get("count").getAsInt() : 1;
            if (o.has("type")) {
                try {
                    return InputItem.of(RecipePlatformHelper.get().getCustomIngredient(o), count);
                }
                catch (Exception ex) {
                    throw new RecipeExceptionJS("Failed to parse custom ingredient (" + String.valueOf(o.get("type")) + ") from " + String.valueOf(o) + ": " + String.valueOf(ex));
                }
            }
            if (val || o.has("ingredient")) {
                return InputItem.of(IngredientJS.ofJson(val ? o.get("value") : o.get("ingredient")), count);
            }
            if (o.has("tag")) {
                return IngredientPlatformHelper.get().tag(o.get("tag").getAsString()).kjs$withCount(count);
            }
            if (o.has("item")) {
                return ItemStackJS.of(o.get("item").getAsString()).m_41720_().kjs$asIngredient().kjs$withCount(count);
            }
            return EMPTY;
        }
        return InputItem.of(Ingredient.m_43917_((JsonElement)json), 1);
    }

    protected InputItem(Ingredient ingredient, int count) {
        this.ingredient = ingredient;
        this.count = count;
    }

    @Override
    public Ingredient kjs$asIngredient() {
        return this.ingredient;
    }

    public InputItem withCount(int count) {
        return count == this.count ? this : new InputItem(this.ingredient, count);
    }

    public boolean isEmpty() {
        return this == EMPTY || this.ingredient.m_43947_() || this.count <= 0;
    }

    public boolean validForMatching() {
        return !this.isEmpty() && this.ingredient.kjs$canBeUsedForMatching();
    }

    public List<InputItem> unwrap() {
        if (this.count > 1) {
            ArrayList<InputItem> list = new ArrayList<InputItem>(this.count);
            InputItem single = this.withCount(1);
            for (int i = 0; i < this.count; ++i) {
                list.add(single);
            }
            return list;
        }
        return List.of(this);
    }

    public String toString() {
        if (this.count > 1) {
            return this.count + "x " + String.valueOf(this.ingredient);
        }
        return this.ingredient.toString();
    }

    public JsonElement toJsonJS() {
        return this.toJsonJS(true);
    }

    @RemapForJS(value="toJson")
    public JsonElement toJsonJS(boolean alwaysNest) {
        if (!alwaysNest && this.count == 1) {
            return this.ingredient.m_43942_();
        }
        JsonObject o = new JsonObject();
        o.addProperty("count", (Number)this.count);
        o.add("ingredient", this.ingredient.m_43942_());
        return o;
    }

    @Override
    public Object replaceInput(RecipeJS recipe, ReplacementMatch match, InputReplacement original) {
        if (original instanceof InputItem) {
            InputItem o = (InputItem)original;
            return this.withCount(o.count);
        }
        return this;
    }
}

