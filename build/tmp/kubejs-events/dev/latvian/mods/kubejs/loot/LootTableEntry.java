/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.loot.ConditionContainer;
import dev.latvian.mods.kubejs.loot.FunctionContainer;

public class LootTableEntry
implements FunctionContainer,
ConditionContainer {
    public final JsonObject json;

    public LootTableEntry(JsonObject o) {
        this.json = o;
    }

    public LootTableEntry weight(int weight) {
        this.json.addProperty("weight", (Number)weight);
        return this;
    }

    public LootTableEntry quality(int quality) {
        this.json.addProperty("quality", (Number)quality);
        return this;
    }

    @Override
    public LootTableEntry addFunction(JsonObject o) {
        JsonArray a = (JsonArray)this.json.get("functions");
        if (a == null) {
            a = new JsonArray();
            this.json.add("functions", (JsonElement)a);
        }
        a.add((JsonElement)o);
        return this;
    }

    @Override
    public LootTableEntry addCondition(JsonObject o) {
        JsonArray a = (JsonArray)this.json.get("conditions");
        if (a == null) {
            a = new JsonArray();
            this.json.add("conditions", (JsonElement)a);
        }
        a.add((JsonElement)o);
        return this;
    }
}

