/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient$Value
 *  net.minecraftforge.common.crafting.AbstractIngredient
 *  net.minecraftforge.common.crafting.CraftingHelper
 *  net.minecraftforge.common.crafting.IIngredientSerializer
 */
package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.core.IngredientKJS;
import dev.latvian.mods.kubejs.item.ItemStackJS;
import dev.latvian.mods.kubejs.item.ItemStackSet;
import java.util.stream.Stream;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.AbstractIngredient;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;

public abstract class KubeJSIngredient
extends AbstractIngredient
implements IngredientKJS {
    private static final Ingredient.Value[] EMPTY_VALUES = new Ingredient.Value[0];

    public KubeJSIngredient() {
        super(Stream.empty());
        this.f_43902_ = EMPTY_VALUES;
    }

    public ItemStack[] m_43908_() {
        if (this.f_43903_ == null) {
            this.dissolve();
        }
        return this.f_43903_;
    }

    protected void dissolve() {
        if (this.f_43903_ == null) {
            ItemStackSet stacks = new ItemStackSet();
            for (ItemStack stack : ItemStackJS.getList()) {
                if (!this.test(stack)) continue;
                stacks.add(stack);
            }
            this.f_43903_ = stacks.toArray();
        }
    }

    public boolean m_43947_() {
        return false;
    }

    public boolean isSimple() {
        return false;
    }

    @Override
    public boolean kjs$canBeUsedForMatching() {
        return true;
    }

    public final JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", CraftingHelper.getID((IIngredientSerializer)this.getSerializer()).toString());
        this.toJson(json);
        return json;
    }

    public abstract void toJson(JsonObject var1);

    public abstract void write(FriendlyByteBuf var1);
}

