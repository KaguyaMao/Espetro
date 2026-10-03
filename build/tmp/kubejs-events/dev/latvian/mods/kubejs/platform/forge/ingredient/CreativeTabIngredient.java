/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.core.IdMap
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.CreativeModeTab
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraftforge.common.crafting.IIngredientSerializer
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.platform.forge.ingredient;

import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredient;
import dev.latvian.mods.kubejs.platform.forge.ingredient.KubeJSIngredientSerializer;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.core.IdMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import org.jetbrains.annotations.Nullable;

public class CreativeTabIngredient
extends KubeJSIngredient {
    public static final KubeJSIngredientSerializer<CreativeTabIngredient> SERIALIZER = new KubeJSIngredientSerializer<CreativeTabIngredient>(CreativeTabIngredient::new, CreativeTabIngredient::new);
    public final CreativeModeTab tab;

    public CreativeTabIngredient(CreativeModeTab tab) {
        this.tab = tab;
    }

    public CreativeTabIngredient(FriendlyByteBuf buf) {
        this((CreativeModeTab)buf.m_236816_((IdMap)BuiltInRegistries.f_279662_));
    }

    public CreativeTabIngredient(JsonObject json) {
        this(UtilsJS.findCreativeTab(new ResourceLocation(json.get("tab").getAsString())));
    }

    public IIngredientSerializer<? extends Ingredient> getSerializer() {
        return SERIALIZER;
    }

    public boolean test(@Nullable ItemStack stack) {
        return stack != null && this.tab.m_257694_(stack);
    }

    @Override
    public void toJson(JsonObject json) {
        json.addProperty("tab", RegistryInfo.CREATIVE_MODE_TAB.getId(this.tab).toString());
    }

    @Override
    public void write(FriendlyByteBuf buf) {
        buf.m_236818_((IdMap)BuiltInRegistries.f_279662_, (Object)this.tab);
    }
}

