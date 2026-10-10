/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.loot.ConditionContainer;
import dev.latvian.mods.kubejs.loot.FunctionContainer;
import dev.latvian.mods.kubejs.loot.LootBuilderPool;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class LootBuilder
implements FunctionContainer,
ConditionContainer {
    public String type = "minecraft:generic";
    public ResourceLocation customId = null;
    public JsonArray pools = new JsonArray();
    public JsonArray functions = new JsonArray();
    public JsonArray conditions = new JsonArray();

    public LootBuilder(@Nullable JsonElement prev) {
        if (prev instanceof JsonObject) {
            JsonObject o = (JsonObject)prev;
            if (o.has("pools")) {
                this.pools = o.get("pools").getAsJsonArray();
            }
            if (o.has("functions")) {
                this.functions = o.get("functions").getAsJsonArray();
            }
            if (o.has("conditions")) {
                this.conditions = o.get("conditions").getAsJsonArray();
            }
        }
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.addProperty("type", this.type);
        if (this.pools.size() > 0) {
            json.add("pools", (JsonElement)this.pools);
        }
        if (this.functions.size() > 0) {
            json.add("functions", (JsonElement)this.functions);
        }
        if (this.conditions.size() > 0) {
            json.add("conditions", (JsonElement)this.conditions);
        }
        return json;
    }

    public void addPool(Consumer<LootBuilderPool> p) {
        LootBuilderPool pool = new LootBuilderPool();
        p.accept(pool);
        this.pools.add((JsonElement)pool.toJson());
    }

    @Override
    public LootBuilder addFunction(JsonObject o) {
        this.functions.add((JsonElement)o);
        return this;
    }

    @Override
    public LootBuilder addCondition(JsonObject o) {
        this.conditions.add((JsonElement)o);
        return this;
    }

    public void clearPools() {
        this.pools = new JsonArray();
    }

    public void clearFunctions() {
        this.functions = new JsonArray();
    }

    public void clearConditions() {
        this.conditions = new JsonArray();
    }
}

