/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.core.Holder
 *  net.minecraft.core.Registry
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 */
package net.minecraftforge.common.crafting.conditions;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraftforge.common.crafting.CraftingHelper;

public interface ICondition {
    public static boolean shouldRegisterEntry(JsonElement json) {
        JsonObject obj;
        if (!(json instanceof JsonObject) || !(obj = (JsonObject)json).has("forge:conditions")) {
            return true;
        }
        return CraftingHelper.processConditions(obj, "forge:conditions", IContext.TAGS_INVALID);
    }

    public ResourceLocation getID();

    public boolean test(IContext var1);

    public static interface IContext {
        public static final IContext EMPTY = new IContext(){

            @Override
            public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
                return Collections.emptyMap();
            }
        };
        public static final IContext TAGS_INVALID = new IContext(){

            @Override
            public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> registry) {
                throw new UnsupportedOperationException("Usage of tag-based conditions is not permitted in this context!");
            }
        };

        default public <T> Collection<Holder<T>> getTag(TagKey<T> key) {
            return this.getAllTags(key.f_203867_()).getOrDefault(key.f_203868_(), Set.of());
        }

        public <T> Map<ResourceLocation, Collection<Holder<T>>> getAllTags(ResourceKey<? extends Registry<T>> var1);
    }
}

