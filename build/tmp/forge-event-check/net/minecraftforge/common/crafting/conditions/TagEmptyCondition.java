/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.util.GsonHelper
 *  net.minecraft.world.item.Item
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonObject;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.IConditionSerializer;

public class TagEmptyCondition
implements ICondition {
    private static final ResourceLocation NAME = new ResourceLocation("forge", "tag_empty");
    private final TagKey<Item> tag;

    public TagEmptyCondition(String location) {
        this(new ResourceLocation(location));
    }

    public TagEmptyCondition(String namespace, String path) {
        this(new ResourceLocation(namespace, path));
    }

    public TagEmptyCondition(ResourceLocation tag) {
        this.tag = TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)tag);
    }

    @Override
    public ResourceLocation getID() {
        return NAME;
    }

    @Override
    public boolean test(ICondition.IContext context) {
        return context.getTag(this.tag).isEmpty();
    }

    public String toString() {
        return "tag_empty(\"" + String.valueOf(this.tag.f_203868_()) + "\")";
    }

    public static class Serializer
    implements IConditionSerializer<TagEmptyCondition> {
        public static final Serializer INSTANCE = new Serializer();

        @Override
        public void write(JsonObject json, TagEmptyCondition value) {
            json.addProperty("tag", value.tag.f_203868_().toString());
        }

        @Override
        public TagEmptyCondition read(JsonObject json) {
            return new TagEmptyCondition(new ResourceLocation(GsonHelper.m_13906_((JsonObject)json, (String)"tag")));
        }

        @Override
        public ResourceLocation getID() {
            return NAME;
        }
    }
}

