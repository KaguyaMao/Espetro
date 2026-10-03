/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.crafting.conditions.ICondition;

public interface IConditionSerializer<T extends ICondition> {
    public void write(JsonObject var1, T var2);

    public T read(JsonObject var1);

    public ResourceLocation getID();

    default public JsonObject getJson(T value) {
        JsonObject json = new JsonObject();
        this.write(json, value);
        json.addProperty("type", value.getID().toString());
        return json;
    }
}

