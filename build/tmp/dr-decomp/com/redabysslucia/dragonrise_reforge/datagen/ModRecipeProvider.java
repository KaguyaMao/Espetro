/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.CriterionTriggerInstance
 *  net.minecraft.core.registries.BuiltInRegistries
 *  net.minecraft.core.registries.Registries
 *  net.minecraft.data.PackOutput
 *  net.minecraft.data.recipes.FinishedRecipe
 *  net.minecraft.data.recipes.RecipeCategory
 *  net.minecraft.data.recipes.RecipeProvider
 *  net.minecraft.data.recipes.ShapedRecipeBuilder
 *  net.minecraft.resources.ResourceKey
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.tags.TagKey
 *  net.minecraft.world.item.Item
 *  net.minecraft.world.item.Items
 *  net.minecraft.world.level.ItemLike
 *  net.minecraftforge.common.Tags$Items
 *  net.minecraftforge.common.crafting.conditions.IConditionBuilder
 *  org.jetbrains.annotations.NotNull
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import com.redabysslucia.dragonrise_reforge.init.ModItems;
import java.util.function.Consumer;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import org.jetbrains.annotations.NotNull;

public class ModRecipeProvider
extends RecipeProvider
implements IConditionBuilder {
    private static final TagKey<Item> DYES = ModRecipeProvider.commonItemTag("dyes");

    public ModRecipeProvider(PackOutput pOutput) {
        super(pOutput);
    }

    private static ResourceLocation loc(String path) {
        return new ResourceLocation("dragonrise_reforge", path);
    }

    private static TagKey<Item> commonItemTag(String path) {
        return TagKey.m_203882_((ResourceKey)Registries.f_256913_, (ResourceLocation)new ResourceLocation("c", path));
    }

    private static String getItemName(Item item) {
        return BuiltInRegistries.f_257033_.m_7981_((Object)item).m_135815_();
    }

    protected void m_245200_(@NotNull Consumer<FinishedRecipe> writer) {
        ModRecipeProvider.buildMiscRecipes(writer);
        ModRecipeProvider.buildArmorRecipes(writer);
    }

    private static void buildMiscRecipes(Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.SPRAY_CAN.get())).m_126130_("III").m_126130_("IDI").m_126130_("III").m_206416_(Character.valueOf('I'), Tags.Items.INGOTS_IRON).m_206416_(Character.valueOf('D'), DYES).m_126132_(ModRecipeProvider.m_176602_((ItemLike)Items.f_42416_), (CriterionTriggerInstance)ModRecipeProvider.m_206406_((TagKey)Tags.Items.INGOTS_IRON)).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.SPRAY_CAN.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.MISC, (ItemLike)((ItemLike)ModItems.KEVLAR.get())).m_126130_("LLL").m_126130_("LHL").m_126130_("LLL").m_126127_(Character.valueOf('L'), (ItemLike)Items.f_42454_).m_126127_(Character.valueOf('H'), (ItemLike)Items.f_42784_).m_126132_(ModRecipeProvider.m_176602_((ItemLike)Items.f_42454_), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)Items.f_42454_)).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.KEVLAR.get())));
    }

    private static void buildArmorRecipes(Consumer<FinishedRecipe> writer) {
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.MSV_CHEST.get())).m_126130_("KDK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('D'), Tags.Items.DYES_YELLOW).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.MSV_CHEST.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.GORKA3.get())).m_126130_("KFK").m_126130_("KKK").m_126130_("KKK").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('F'), Tags.Items.INGOTS_IRON).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.GORKA3.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.GORKA3_LEGGINGS.get())).m_126130_("KFK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('F'), Tags.Items.INGOTS_IRON).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.GORKA3_LEGGINGS.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.ALJIN_HELMET.get())).m_126130_("KKK").m_126130_("KFK").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('F'), Tags.Items.INGOTS_IRON).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.ALJIN_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.MED21_CHEST.get())).m_126130_("KRK").m_126130_("KDK").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('R'), Tags.Items.DYES_RED).m_206416_(Character.valueOf('D'), Tags.Items.DYES_GREEN).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.MED21_CHEST.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.T21_HELMET.get())).m_126130_("KGK").m_126130_("KDK").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('G'), Tags.Items.DYES_GRAY).m_206416_(Character.valueOf('D'), Tags.Items.DYES_GREEN).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.T21_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.FAST_HELMET.get())).m_126130_("KKK").m_126130_("KDK").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('D'), Tags.Items.DYES_YELLOW).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.FAST_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.SNIPER21_HELMET.get())).m_126130_("LOL").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_126127_(Character.valueOf('L'), (ItemLike)Items.f_41896_).m_126127_(Character.valueOf('O'), (ItemLike)Items.f_41837_).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.SNIPER21_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.PANTS21.get())).m_126130_("KGK").m_126130_("KDK").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('G'), Tags.Items.DYES_GRAY).m_206416_(Character.valueOf('D'), Tags.Items.DYES_GREEN).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.PANTS21.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.MSV_PANTS.get())).m_126130_("KDK").m_126130_("KKK").m_126130_("KKK").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('D'), Tags.Items.DYES_YELLOW).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.MSV_PANTS.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.DESERT07_HELMET.get())).m_126130_("KSK").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_126127_(Character.valueOf('S'), (ItemLike)Items.f_41830_).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.DESERT07_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.DESERT07_CHEST.get())).m_126130_("KSK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_126127_(Character.valueOf('S'), (ItemLike)Items.f_41830_).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.DESERT07_CHEST.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.DESERT07_PANTS.get())).m_126130_("KSK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_126127_(Character.valueOf('S'), (ItemLike)Items.f_41830_).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.DESERT07_PANTS.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.OCEAN07_HELMET.get())).m_126130_("KBK").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('B'), Tags.Items.DYES_BLUE).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.OCEAN07_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.OCEAN07_CHEST.get())).m_126130_("KBK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('B'), Tags.Items.DYES_BLUE).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.OCEAN07_CHEST.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.OCEAN07_PANTS.get())).m_126130_("KBK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('B'), Tags.Items.DYES_BLUE).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.OCEAN07_PANTS.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.UN_HELMET.get())).m_126130_("KLK").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('L'), Tags.Items.DYES_LIGHT_BLUE).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.UN_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.KR06_HELMET.get())).m_126130_("KNK").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('N'), Tags.Items.DYES_BLACK).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.KR06_HELMET.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.KR06_CHEST.get())).m_126130_("KNK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('N'), Tags.Items.DYES_BLACK).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.KR06_CHEST.get())));
        ShapedRecipeBuilder.m_245327_((RecipeCategory)RecipeCategory.COMBAT, (ItemLike)((ItemLike)ModItems.KR06_PANTS.get())).m_126130_("KNK").m_126130_("K K").m_126130_("K K").m_126127_(Character.valueOf('K'), (ItemLike)ModItems.KEVLAR.get()).m_206416_(Character.valueOf('N'), Tags.Items.DYES_BLACK).m_126132_(ModRecipeProvider.m_176602_((ItemLike)((ItemLike)ModItems.KEVLAR.get())), (CriterionTriggerInstance)ModRecipeProvider.m_125977_((ItemLike)((ItemLike)ModItems.KEVLAR.get()))).m_126140_(writer, ModRecipeProvider.loc(ModRecipeProvider.getItemName((Item)ModItems.KR06_PANTS.get())));
    }
}

