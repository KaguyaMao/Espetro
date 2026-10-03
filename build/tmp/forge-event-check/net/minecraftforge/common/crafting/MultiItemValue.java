/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient$Value
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.registries.ForgeRegistries;

public class MultiItemValue
implements Ingredient.Value {
    private Collection<ItemStack> items;

    public MultiItemValue(Collection<ItemStack> items) {
        this.items = Collections.unmodifiableCollection(items);
    }

    public Collection<ItemStack> m_6223_() {
        return this.items;
    }

    public JsonObject m_6544_() {
        if (this.items.size() == 1) {
            return this.toJson(this.items.iterator().next());
        }
        JsonObject ret = new JsonObject();
        JsonArray array = new JsonArray();
        this.items.forEach(stack -> array.add((JsonElement)this.toJson((ItemStack)stack)));
        ret.add("items", (JsonElement)array);
        return ret;
    }

    private JsonObject toJson(ItemStack stack) {
        JsonObject ret = new JsonObject();
        ret.addProperty("item", ForgeRegistries.ITEMS.getKey(stack.m_41720_()).toString());
        if (stack.m_41613_() != 1) {
            ret.addProperty("count", (Number)stack.m_41613_());
        }
        if (stack.m_41783_() != null) {
            ret.addProperty("nbt", stack.m_41783_().toString());
        }
        return ret;
    }
}

