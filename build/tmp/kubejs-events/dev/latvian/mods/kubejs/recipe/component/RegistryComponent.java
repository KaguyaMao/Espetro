/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonPrimitive
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.recipe.component;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.fluid.FluidStackJS;
import dev.latvian.mods.kubejs.fluid.OutputFluid;
import dev.latvian.mods.kubejs.recipe.RecipeJS;
import dev.latvian.mods.kubejs.recipe.component.RecipeComponent;
import dev.latvian.mods.kubejs.recipe.schema.DynamicRecipeComponent;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.typings.desc.DescriptionContext;
import dev.latvian.mods.kubejs.typings.desc.TypeDescJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public record RegistryComponent<T>(RegistryInfo<T> registry) implements RecipeComponent<T>
{
    public static final DynamicRecipeComponent DYNAMIC = new DynamicRecipeComponent(TypeDescJS.object(1).add("registry", TypeDescJS.STRING.or(DescriptionContext.DEFAULT.javaType(RegistryInfo.class))), (ctx, scope, args) -> {
        RegistryInfo<?> regInfo;
        Object registry = args.get("registry");
        if (registry instanceof RegistryInfo) {
            RegistryInfo<?> registryInfo;
            regInfo = registryInfo = (RegistryInfo<?>)registry;
        } else if (registry instanceof ResourceKey) {
            ResourceKey resourceKey = (ResourceKey)registry;
            regInfo = RegistryInfo.of(resourceKey);
        } else {
            regInfo = RegistryInfo.of(ResourceKey.m_135788_((ResourceLocation)new ResourceLocation(String.valueOf(registry))));
        }
        return new RegistryComponent(regInfo);
    });

    @Override
    public String componentType() {
        return "registry_element";
    }

    @Override
    public TypeDescJS constructorDescription(DescriptionContext ctx) {
        return TypeDescJS.STRING.or(ctx.javaType(this.registry.objectBaseClass));
    }

    @Override
    public Class<?> componentClass() {
        return this.registry.objectBaseClass;
    }

    @Override
    public JsonElement write(RecipeJS recipe, T value) {
        return new JsonPrimitive(this.registry.getId(value).toString());
    }

    @Override
    public T read(RecipeJS recipe, Object from) {
        if (this.registry.objectBaseClass != Object.class && this.registry.objectBaseClass.isInstance(from)) {
            return (T)from;
        }
        if (!(from instanceof CharSequence || from instanceof JsonPrimitive || from instanceof ResourceLocation)) {
            if (this.registry == RegistryInfo.ITEM) {
                if (from instanceof ItemStack) {
                    ItemStack is = (ItemStack)from;
                    return (T)is.m_41720_();
                }
                return (T)recipe.readOutputItem((Object)from).item.m_41720_();
            }
            if (this.registry == RegistryInfo.FLUID) {
                if (from instanceof FluidStackJS) {
                    FluidStackJS fs = (FluidStackJS)from;
                    return (T)fs.getFluid();
                }
                OutputFluid outputFluid = recipe.readOutputFluid(from);
                if (outputFluid instanceof FluidStackJS) {
                    FluidStackJS fs = (FluidStackJS)outputFluid;
                    return (T)fs.getFluid();
                }
            }
        }
        return this.registry.getValue(UtilsJS.getMCID(null, from));
    }

    @Override
    public boolean hasPriority(RecipeJS recipe, Object from) {
        JsonPrimitive json;
        return this.registry.objectBaseClass != Object.class && this.registry.objectBaseClass.isInstance(from) || from instanceof CharSequence && UtilsJS.getMCID(null, from.toString()) != null || from instanceof JsonPrimitive && (json = (JsonPrimitive)from).isString() && UtilsJS.getMCID(null, json.getAsString()) != null;
    }

    @Override
    public String toString() {
        return "%s{%s}".formatted(this.componentType(), this.registry);
    }
}

