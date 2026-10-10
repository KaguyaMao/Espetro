/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.advancements.Advancement$Builder
 *  net.minecraft.data.recipes.FinishedRecipe
 *  net.minecraft.util.GsonHelper
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
import java.util.function.Supplier;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import org.jetbrains.annotations.Nullable;

public class ConditionalAdvancement {
    public static Builder builder() {
        return new Builder();
    }

    @Nullable
    public static JsonObject processConditional(JsonObject json, ICondition.IContext context) {
        JsonArray entries = GsonHelper.m_13832_((JsonObject)json, (String)"advancements", null);
        if (entries == null) {
            return CraftingHelper.processConditions(json, "conditions", context) ? json : null;
        }
        int idx = 0;
        for (JsonElement ele : entries) {
            if (!ele.isJsonObject()) {
                throw new JsonSyntaxException("Invalid advancement entry at index " + idx + " Must be JsonObject");
            }
            if (CraftingHelper.processConditions(GsonHelper.m_13933_((JsonObject)ele.getAsJsonObject(), (String)"conditions"), context)) {
                return GsonHelper.m_13930_((JsonObject)ele.getAsJsonObject(), (String)"advancement");
            }
            ++idx;
        }
        return null;
    }

    public static class Builder {
        private List<ICondition[]> conditions = new ArrayList<ICondition[]>();
        private List<Supplier<JsonElement>> advancements = new ArrayList<Supplier<JsonElement>>();
        private List<ICondition> currentConditions = new ArrayList<ICondition>();
        private boolean locked = false;

        public Builder addCondition(ICondition condition) {
            if (this.locked) {
                throw new IllegalStateException("Attempted to modify finished builder");
            }
            this.currentConditions.add(condition);
            return this;
        }

        public Builder addAdvancement(Consumer<Consumer<Advancement.Builder>> callable) {
            if (this.locked) {
                throw new IllegalStateException("Attempted to modify finished builder");
            }
            callable.accept(this::addAdvancement);
            return this;
        }

        public Builder addAdvancement(Advancement.Builder advancement) {
            return this.addAdvancement(() -> ((Advancement.Builder)advancement).m_138400_());
        }

        public Builder addAdvancement(FinishedRecipe fromRecipe) {
            return this.addAdvancement(() -> ((FinishedRecipe)fromRecipe).m_5860_());
        }

        private Builder addAdvancement(Supplier<JsonElement> jsonSupplier) {
            if (this.locked) {
                throw new IllegalStateException("Attempted to modify finished builder");
            }
            if (this.currentConditions.isEmpty()) {
                throw new IllegalStateException("Can not add a advancement with no conditions.");
            }
            this.conditions.add(this.currentConditions.toArray(new ICondition[this.currentConditions.size()]));
            this.advancements.add(jsonSupplier);
            this.currentConditions.clear();
            return this;
        }

        public JsonObject write() {
            if (!this.locked) {
                if (!this.currentConditions.isEmpty()) {
                    throw new IllegalStateException("Invalid builder state: Orphaned conditions");
                }
                if (this.advancements.isEmpty()) {
                    throw new IllegalStateException("Invalid builder state: No Advancements");
                }
                this.locked = true;
            }
            JsonObject json = new JsonObject();
            JsonArray array = new JsonArray();
            json.add("advancements", (JsonElement)array);
            for (int x = 0; x < this.conditions.size(); ++x) {
                JsonObject holder = new JsonObject();
                JsonArray conds = new JsonArray();
                for (ICondition c : this.conditions.get(x)) {
                    conds.add((JsonElement)CraftingHelper.serialize(c));
                }
                holder.add("conditions", (JsonElement)conds);
                holder.add("advancement", this.advancements.get(x).get());
                array.add((JsonElement)holder);
            }
            return json;
        }
    }
}

