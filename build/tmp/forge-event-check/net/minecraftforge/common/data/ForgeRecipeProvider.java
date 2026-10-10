/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  net.minecraft.advancements.Advancement$Builder
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.FinishedRecipe
 *  net.minecraft.data.recipes.ShapedRecipeBuilder$Result
 *  net.minecraft.data.recipes.ShapelessRecipeBuilder$Result
 *  net.minecraft.data.recipes.packs.VanillaRecipeProvider
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.ItemStack
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.item.crafting.Ingredient
 *  net.minecraft.world.item.crafting.Ingredient$ItemValue
 *  net.minecraft.world.item.crafting.Ingredient$TagValue
 *  net.minecraft.world.item.crafting.Ingredient$Value
 *  net.minecraft.world.level.ItemLike
 *  net.minecraft.world.level.block.Blocks
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.common.data;

import com.google.gson.JsonObject;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;
import net.minecraft.advancements.Advancement;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.data.recipes.packs.VanillaRecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class ForgeRecipeProvider
extends VanillaRecipeProvider {
    private final Map<Item, TagKey<Item>> replacements = new HashMap<Item, TagKey<Item>>();
    private final Set<ResourceLocation> excludes = new HashSet<ResourceLocation>();

    public ForgeRecipeProvider(PackOutput packOutput) {
        super(packOutput);
    }

    private void exclude(ItemLike item) {
        this.excludes.add(ForgeRegistries.ITEMS.getKey(item.m_5456_()));
    }

    private void exclude(String name) {
        this.excludes.add(new ResourceLocation(name));
    }

    private void replace(ItemLike item, TagKey<Item> tag) {
        this.replacements.put(item.m_5456_(), tag);
    }

    protected void m_245200_(Consumer<FinishedRecipe> consumer) {
        this.replace((ItemLike)Items.f_42398_, Tags.Items.RODS_WOODEN);
        this.replace((ItemLike)Items.f_42417_, Tags.Items.INGOTS_GOLD);
        this.replace((ItemLike)Items.f_42416_, Tags.Items.INGOTS_IRON);
        this.replace((ItemLike)Items.f_42418_, Tags.Items.INGOTS_NETHERITE);
        this.replace((ItemLike)Items.f_151052_, Tags.Items.INGOTS_COPPER);
        this.replace((ItemLike)Items.f_151049_, Tags.Items.GEMS_AMETHYST);
        this.replace((ItemLike)Items.f_42415_, Tags.Items.GEMS_DIAMOND);
        this.replace((ItemLike)Items.f_42616_, Tags.Items.GEMS_EMERALD);
        this.replace((ItemLike)Items.f_42009_, Tags.Items.CHESTS_WOODEN);
        this.replace((ItemLike)Blocks.f_50652_, Tags.Items.COBBLESTONE_NORMAL);
        this.replace((ItemLike)Blocks.f_152551_, Tags.Items.COBBLESTONE_DEEPSLATE);
        this.replace((ItemLike)Items.f_42401_, Tags.Items.STRING);
        this.exclude(ForgeRecipeProvider.m_176517_((ItemLike)Blocks.f_50041_, (ItemLike)Items.f_42401_));
        this.exclude((ItemLike)Blocks.f_50074_);
        this.exclude((ItemLike)Items.f_42587_);
        this.exclude((ItemLike)Blocks.f_50075_);
        this.exclude((ItemLike)Items.f_42749_);
        this.exclude((ItemLike)Blocks.f_50090_);
        this.exclude((ItemLike)Blocks.f_50268_);
        this.exclude((ItemLike)Blocks.f_50721_);
        this.exclude((ItemLike)Blocks.f_152504_);
        this.exclude((ItemLike)Blocks.f_152490_);
        this.exclude((ItemLike)Blocks.f_50157_);
        this.exclude((ItemLike)Blocks.f_50409_);
        this.exclude((ItemLike)Blocks.f_50274_);
        this.exclude((ItemLike)Blocks.f_152552_);
        this.exclude((ItemLike)Blocks.f_152553_);
        this.exclude((ItemLike)Blocks.f_152554_);
        super.m_245200_(vanilla -> {
            FinishedRecipe modified = this.enhance((FinishedRecipe)vanilla);
            if (modified != null) {
                consumer.accept(modified);
            }
        });
    }

    @Nullable
    private FinishedRecipe enhance(FinishedRecipe vanilla) {
        if (vanilla instanceof ShapelessRecipeBuilder.Result) {
            ShapelessRecipeBuilder.Result shapeless = (ShapelessRecipeBuilder.Result)vanilla;
            return this.enhance(shapeless);
        }
        if (vanilla instanceof ShapedRecipeBuilder.Result) {
            ShapedRecipeBuilder.Result shaped = (ShapedRecipeBuilder.Result)vanilla;
            return this.enhance(shaped);
        }
        return null;
    }

    @Nullable
    private FinishedRecipe enhance(ShapelessRecipeBuilder.Result vanilla) {
        List ingredients = (List)this.getField(ShapelessRecipeBuilder.Result.class, vanilla, 4);
        boolean modified = false;
        for (int x = 0; x < ingredients.size(); ++x) {
            Ingredient ing = this.enhance(vanilla.m_6445_(), (Ingredient)ingredients.get(x));
            if (ing == null) continue;
            ingredients.set(x, ing);
            modified = true;
        }
        return modified ? vanilla : null;
    }

    @Nullable
    protected CompletableFuture<?> saveAdvancement(CachedOutput output, FinishedRecipe recipe, JsonObject json) {
        return null;
    }

    protected CompletableFuture<?> m_253240_(CachedOutput output, ResourceLocation name, Advancement.Builder builder) {
        return CompletableFuture.allOf(new CompletableFuture[0]);
    }

    @Nullable
    private FinishedRecipe enhance(ShapedRecipeBuilder.Result vanilla) {
        Map ingredients = (Map)this.getField(ShapedRecipeBuilder.Result.class, vanilla, 5);
        boolean modified = false;
        for (Character x : ingredients.keySet()) {
            Ingredient ing = this.enhance(vanilla.m_6445_(), (Ingredient)ingredients.get(x));
            if (ing == null) continue;
            ingredients.put(x, ing);
            modified = true;
        }
        return modified ? vanilla : null;
    }

    @Nullable
    private Ingredient enhance(ResourceLocation name, Ingredient vanilla) {
        Ingredient.Value[] vanillaItems;
        if (this.excludes.contains(name)) {
            return null;
        }
        boolean modified = false;
        ArrayList<Object> items = new ArrayList<Object>();
        for (Ingredient.Value entry : vanillaItems = (Ingredient.Value[])this.getField(Ingredient.class, vanilla, 2)) {
            if (entry instanceof Ingredient.ItemValue) {
                ItemStack stack = entry.m_6223_().stream().findFirst().orElse(ItemStack.f_41583_);
                TagKey<Item> replacement = this.replacements.get(stack.m_41720_());
                if (replacement != null) {
                    items.add(new Ingredient.TagValue(replacement));
                    modified = true;
                    continue;
                }
                items.add(entry);
                continue;
            }
            items.add(entry);
        }
        return modified ? Ingredient.m_43938_(items.stream()) : null;
    }

    private <T, R> R getField(Class<T> clz, T inst, int index) {
        Field fld = clz.getDeclaredFields()[index];
        fld.setAccessible(true);
        try {
            return (R)fld.get(inst);
        }
        catch (IllegalAccessException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}

