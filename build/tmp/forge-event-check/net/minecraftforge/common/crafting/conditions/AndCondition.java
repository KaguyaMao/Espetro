/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonSyntaxException
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.common.base.Joiner;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.ArrayList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class AndCondition
implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "and");
    private final ICondition[] children;

    public AndCondition(ICondition ... values) {
        if (values == null || values.length == 0) {
            throw new IllegalArgumentException("Values must not be empty");
        }
        for (ICondition child : values) {
            if (child != null) continue;
            throw new IllegalArgumentException("Value must not be null");
        }
        this.children = values;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        for (ICondition child : this.children) {
            if (child.test(context)) continue;
            return false;
        }
        return true;
    }

    public String toString() {
        return Joiner.on((String)" && ").join((Object[])this.children);
    }

    public static class Serializer
    implements IConditionSerializer<AndCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, AndCondition value) {
            JsonArray values = new JsonArray();
            for (ICondition child : value.children) {
                values.add((JsonElement)CraftingHelper.serialize(child));
            }
            json.add("values", (JsonElement)values);
        }

        @Override
        public AndCondition read(JsonObject json) {
            ArrayList<ICondition> children = new ArrayList<ICondition>();
            for (JsonElement j : GsonHelper.m_13933_((JsonObject)json, (String)"values")) {
                if (!j.isJsonObject()) {
                    throw new JsonSyntaxException("And condition values must be an array of JsonObjects");
                }
                children.add(CraftingHelper.getCondition(j.getAsJsonObject()));
            }
            return new AndCondition(children.toArray(new ICondition[children.size()]));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

