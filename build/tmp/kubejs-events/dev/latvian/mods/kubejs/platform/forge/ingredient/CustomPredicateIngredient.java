/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  it.unimi.dsi.fastutil.ints.IntList
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraftforge.common.crafting.IIngredientSerializer
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.item.ingredient.IngredientJS;
import dev.latvian.mods.kubejs.item.ingredient.IngredientWithCustomPredicate;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredientSerializer;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import it.unimi.dsi.fastutil.ints.IntList;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CustomPredicateIngredient
extends KubeJSIngredient {
    public static final KubeJSIngredientSerializer<CustomPredicateIngredient> SERIALIZER = new KubeJSIngredientSerializer<CustomPredicateIngredient>(CustomPredicateIngredient::new, CustomPredicateIngredient::new);
    private final Ingredient parent;
    private final UUID uuid;
    private final boolean isServer;

    public CustomPredicateIngredient(Ingredient parent, UUID uuid) {
        this.parent = parent;
        this.uuid = uuid;
        this.isServer = true;
    }

    private CustomPredicateIngredient(JsonObject json) {
        this.parent = IngredientJS.ofJson(json.get("parent"));
        this.uuid = UUID.fromString(json.get("uuid").getAsString());
        this.isServer = false;
    }

    private CustomPredicateIngredient(FriendlyByteBuf buf) {
        this.parent = IngredientJS.ofNetwork(buf);
        this.uuid = buf.m_130259_();
        this.isServer = false;
    }

    public IIngredientSerializer<CustomPredicateIngredient> getSerializer() {
        return SERIALIZER;
    }

    @Override
    public ItemStack[] m_43908_() {
        return this.parent.m_43908_();
    }

    @NotNull
    public IntList m_43931_() {
        return this.parent.m_43931_();
    }

    public boolean test(@Nullable ItemStack target) {
        if (this.isServer && target != null && this.parent.test(target) && RecipesEventJS.customIngredientMap != null) {
            IngredientWithCustomPredicate i = RecipesEventJS.customIngredientMap.get(this.uuid);
            return i != null && i.predicate.test(target);
        }
        return false;
    }

    @Override
    public void toJson(JsonObject json) {
        json.add("parent", this.parent.m_43942_());
        json.addProperty("uuid", this.uuid.toString());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        this.parent.m_43923_(buf);
        buf.m_130077_(this.uuid);
    }
}

