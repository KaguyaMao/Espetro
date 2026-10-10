/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.util.GsonHelper
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemExistsCondition
implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "item_exists");
    private final ResourceLocation item;

    public ItemExistsCondition(String location) {
        this(new ResourceLocation(location));
    }

    public ItemExistsCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public ItemExistsCondition(ResourceLocation item) {
        this.item = item;
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        return ForgeRegistries.ITEMS.containsKey(this.item);
    }

    public String toString() {
        return "item_exists(\"" + String.valueOf(this.item) + "\")";
    }

    public static class Serializer
    implements IConditionSerializer<ItemExistsCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, ItemExistsCondition value) {
            json.addProperty("item", value.item.toString());
        }

        @Override
        public ItemExistsCondition read(JsonObject json) {
            return new ItemExistsCondition(new ResourceLocation(GsonHelper.m_13906_((JsonObject)json, (String)"item")));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

