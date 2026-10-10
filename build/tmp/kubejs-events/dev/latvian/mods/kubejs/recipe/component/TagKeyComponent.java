/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonPrimitive
 *  dev.latvian.mods.rhino.Wrapper
 *  net.minecraft.core.Registry
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.entity.EntityType
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.level.biome.Biome
 *  net.minecraft.world.level.block.Block
 *  net.minecraft.world.level.material.Fluid
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.Wrapper;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;

public record TagKeyComponent<T>(ResourceKey<? extends Registry<T>> registry, Class<?> registryType) implements RecipeComponent<TagKey<T>>
{
    public static final RecipeComponent<TagKey<Block>> BLOCK = new TagKeyComponent<Block>(Registries.f_256747_, Block.class);
    public static final RecipeComponent<TagKey<Item>> ITEM = new TagKeyComponent<Item>(Registries.f_256913_, Item.class);
    public static final RecipeComponent<TagKey<EntityType<?>>> ENTITY_TYPE = new TagKeyComponent(Registries.f_256939_, EntityType.class);
    public static final RecipeComponent<TagKey<Biome>> BIOME = new TagKeyComponent<Biome>(Registries.f_256952_, Biome.class);
    public static final RecipeComponent<TagKey<Fluid>> FLUID = new TagKeyComponent<Fluid>(Registries.f_256808_, Fluid.class);
    public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(TypeDescJS.object().add("registry", TypeDescJS.STRING).add("class", TypeDescJS.STRING, true), (cx, scope, args) -> {
        ResourceKey registry = ResourceKey.m_135788_((ResourceLocation)UtilsJS.getMCID(cx, Wrapper.unwrapped(args.get("registry"))));
        Class type = Object.class;
        if (args.containsKey("class")) {
            try {
                type = Class.forName(String.valueOf(Wrapper.unwrapped(args.get("class"))));
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return new TagKeyComponent(registry, type);
    });

    @Override
    public String componentType() {
        return "tag_key";
    }

    @Override
    public Class<?> componentClass() {
        return TagKey.class;
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return TypeDescJS.STRING.or(ctx.javaType(TagKey.class).withGenerics(ctx.javaType(this.registryType)));
    }

    public JsonPrimitive write(RecipeJS recipe, TagKey<T> value) {
        return new JsonPrimitive(value.f_203868_().toString());
    }

    @Override
    public TagKey<T> read(RecipeJS recipe, Object from) {
        String s;
        if (from instanceof TagKey) {
            TagKey k = (TagKey)from;
            return k;
        }
        if (from instanceof JsonPrimitive) {
            JsonPrimitive json = (JsonPrimitive)from;
            v0 = json.getAsString();
        } else {
            v0 = s = String.valueOf(from);
        }
        if (s.startsWith("#")) {
            s = s.substring(1);
        }
        return TagKey.m_203882_(this.registry, (ResourceLocation)new ResourceLocation(s));
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        JsonPrimitive json;
        return from instanceof TagKey || from instanceof CharSequence && from.toString().startsWith("#") || from instanceof JsonPrimitive && (json = (JsonPrimitive)from).isString() && json.getAsString().startsWith("#");
    }

    @Override
    public String toString() {
        return this.componentType();
    }
}

