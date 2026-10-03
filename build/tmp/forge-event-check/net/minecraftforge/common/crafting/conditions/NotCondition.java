/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class NotCondition
implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "not");
    private final ICondition child;

    public NotCondition(ICondition child) {
        this.child = child;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        return !this.child.test(context);
    }

    public String toString() {
        return "!" + String.valueOf(this.child);
    }

    public static class Serializer
    implements IConditionSerializer<NotCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, NotCondition value) {
            json.add("value", (JsonElement)CraftingHelper.serialize(value.child));
        }

        @Override
        public NotCondition read(JsonObject json) {
            return new NotCondition(CraftingHelper.getCondition(GsonHelper.m_13930_((JsonObject)json, (String)"value")));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

