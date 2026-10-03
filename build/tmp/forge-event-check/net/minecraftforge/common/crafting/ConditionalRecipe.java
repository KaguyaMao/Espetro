/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.data.recipes.FinishedRecipe
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.item.crafting.Recipe
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.item.crafting.RecipeSerializer
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.crafting.ConditionalAdvancement;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.registries.ObjectHolder;
import org.jetbrains.annotations.Nullable;

public class ConditionalRecipe {
    @ObjectHolder(registryName="recipe_serializer", value="forge:conditional")
    public static final RecipeSerializer<Recipe<?>> SERIALZIER = null;

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private List<ICondition[]> conditions = new ArrayList<ICondition[]>();
        private List<FinishedRecipe> recipes = new ArrayList<FinishedRecipe>();
        private ResourceLocation advId;
        private ConditionalAdvancement.Builder adv;
        private List<ICondition> currentConditions = new ArrayList<ICondition>();

        public Builder addCondition(ICondition condition) {
            this.currentConditions.add(condition);
            return this;
        }

        public Builder addRecipe(Consumer<Consumer<FinishedRecipe>> callable) {
            callable.accept(this::addRecipe);
            return this;
        }

        public Builder addRecipe(FinishedRecipe recipe) {
            if (this.currentConditions.isEmpty()) {
                throw new IllegalStateException("Can not add a recipe with no conditions.");
            }
            this.conditions.add(this.currentConditions.toArray(new ICondition[this.currentConditions.size()]));
            this.recipes.add(recipe);
            this.currentConditions.clear();
            return this;
        }

        public Builder generateAdvancement() {
            return this.generateAdvancement(null);
        }

        public Builder generateAdvancement(@Nullable ResourceLocation id) {
            ConditionalAdvancement.Builder builder = ConditionalAdvancement.builder();
            for (int i = 0; i < this.recipes.size(); ++i) {
                for (ICondition cond : this.conditions.get(i)) {
                    builder = builder.addCondition(cond);
                }
                builder = builder.addAdvancement(this.recipes.get(i));
            }
            return this.setAdvancement(id, builder);
        }

        public Builder setAdvancement(ConditionalAdvancement.Builder advancement) {
            return this.setAdvancement(null, advancement);
        }

        public Builder setAdvancement(String namespace, String path, ConditionalAdvancement.Builder advancement) {
            return this.setAdvancement(new ResourceLocation(namespace, path), advancement);
        }

        public Builder setAdvancement(@Nullable ResourceLocation id, ConditionalAdvancement.Builder advancement) {
            if (this.adv != null) {
                throw new IllegalStateException("Invalid ConditionalRecipeBuilder, Advancement already set");
            }
            this.advId = id;
            this.adv = advancement;
            return this;
        }

        public void build(Consumer<FinishedRecipe> consumer, String namespace, String path) {
            this.build(consumer, new ResourceLocation(namespace, path));
        }

        public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
            if (!this.currentConditions.isEmpty()) {
                throw new IllegalStateException("Invalid ConditionalRecipe builder, Orphaned conditions");
            }
            if (this.recipes.isEmpty()) {
                throw new IllegalStateException("Invalid ConditionalRecipe builder, No recipes");
            }
            if (this.advId == null && this.adv != null) {
                this.advId = new ResourceLocation(id.m_135827_(), "recipes/" + id.m_135815_());
            }
            consumer.accept(new Finished(id, this.conditions, this.recipes, this.advId, this.adv));
        }
    }

    private static class Finished
    implements FinishedRecipe {
        private final ResourceLocation id;
        private final List<ICondition[]> conditions;
        private final List<FinishedRecipe> recipes;
        private final ResourceLocation advId;
        private final ConditionalAdvancement.Builder adv;

        private Finished(ResourceLocation id, List<ICondition[]> conditions, List<FinishedRecipe> recipes, @Nullable ResourceLocation advId, @Nullable ConditionalAdvancement.Builder adv) {
            this.id = id;
            this.conditions = conditions;
            this.recipes = recipes;
            this.advId = advId;
            this.adv = adv;
        }

        public void m_7917_(JsonObject json) {
            JsonArray array = new JsonArray();
            json.add("recipes", (JsonElement)array);
            for (int x = 0; x < this.conditions.size(); ++x) {
                JsonObject holder = new JsonObject();
                JsonArray conds = new JsonArray();
                for (ICondition c : this.conditions.get(x)) {
                    conds.add((JsonElement)CraftingHelper.serialize(c));
                }
                holder.add("conditions", (JsonElement)conds);
                holder.add("recipe", (JsonElement)this.recipes.get(x).m_125966_());
                array.add((JsonElement)holder);
            }
        }

        public ResourceLocation m_6445_() {
            return this.id;
        }

        public RecipeSerializer<?> m_6637_() {
            return SERIALZIER;
        }

        public JsonObject m_5860_() {
            return this.adv == null ? null : this.adv.write();
        }

        public ResourceLocation m_6448_() {
            return this.advId;
        }
    }

    public static class Serializer<T extends Recipe<?>>
    implements RecipeSerializer<T> {
        public T m_6729_(ResourceLocation recipeId, JsonObject json) {
            return this.fromJson(recipeId, json, ICondition.IContext.EMPTY);
        }

        public T fromJson(ResourceLocation recipeId, JsonObject json, ICondition.IContext context) {
            JsonArray items = GsonHelper.m_13933_((JsonObject)json, (String)"recipes");
            int idx = 0;
            for (JsonElement ele : items) {
                if (!ele.isJsonObject()) {
                    throw new JsonSyntaxException("Invalid recipes entry at index " + idx + " Must be JsonObject");
                }
                if (CraftingHelper.processConditions(GsonHelper.m_13933_((JsonObject)ele.getAsJsonObject(), (String)"conditions"), context)) {
                    return (T)RecipeManager.m_44045_((ResourceLocation)recipeId, (JsonObject)GsonHelper.m_13930_((JsonObject)ele.getAsJsonObject(), (String)"recipe"));
                }
                ++idx;
            }
            return null;
        }

        public T m_8005_(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            return null;
        }

        public void m_6178_(FriendlyByteBuf buffer, T recipe) {
        }
    }
}

