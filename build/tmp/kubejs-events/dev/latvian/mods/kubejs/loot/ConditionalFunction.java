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

public class ConditionalFunction
implements FunctionContainer,
ConditionContainer {
    public JsonObject function = null;
    public JsonArray conditions = new JsonArray();

    @Override
    public ConditionalFunction addFunction(JsonObject o) {
        this.function = o;
        return this;
    }

    @Override
    public ConditionalFunction addCondition(JsonObject o) {
        this.conditions.add((JsonElement)o);
        return this;
    }
}

