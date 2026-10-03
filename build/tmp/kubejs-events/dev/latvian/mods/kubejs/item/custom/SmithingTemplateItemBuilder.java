/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.ChatFormatting
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.SmithingTemplateItem
 */
package dev.latvian.mods.kubejs.item.custom;

import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.item.ItemBuilder;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.SmithingTemplateItem;

public class SmithingTemplateItemBuilder
extends ItemBuilder {
    private static final List<ResourceLocation> ARMOR_ICONS = SmithingTemplateItem.m_266239_();
    private static final List<ResourceLocation> INGOTS_AND_CRYSTALS_ICONS = SmithingTemplateItem.m_266346_();
    private static final List<ResourceLocation> EQUIPMENT_ICONS = SmithingTemplateItem.m_266257_();
    private static final List<ResourceLocation> TOOL_ICONS = List.of(SmithingTemplateItem.f_265950_, SmithingTemplateItem.f_265990_, SmithingTemplateItem.f_266096_, SmithingTemplateItem.f_265932_, SmithingTemplateItem.f_265955_);
    private static final List<ResourceLocation> CRYSTAL_ICONS = List.of(SmithingTemplateItem.f_266082_, SmithingTemplateItem.f_265894_, SmithingTemplateItem.f_266085_, SmithingTemplateItem.f_265873_, SmithingTemplateItem.f_265879_, SmithingTemplateItem.f_268746_);
    private final Map<String, String> translations = new HashMap<String, String>();
    public Component appliesToText = Component.m_237113_((String)"set with .appliesToDescription(string) on your smithing_template type item").m_130940_(ChatFormatting.BLUE);
    public Component ingredientsText = Component.m_237113_((String)"set with .ingredientsDescription(string) on your smithing_template type item").m_130940_(ChatFormatting.BLUE);
    public Component appliesToSlotDescriptionText = Component.m_237113_((String)"set with .appliesToSlotDescription(string) on your smithing_template type item");
    public Component ingredientSlotDescriptionText = Component.m_237113_((String)"set with .ingredientsSlotDescription(string) on your smithing_template type item");
    public final List<ResourceLocation> appliesToEmptyIcons = new ArrayList<ResourceLocation>();
    public final List<ResourceLocation> ingredientsSlotEmptyIcons = new ArrayList<ResourceLocation>();

    public SmithingTemplateItemBuilder(ResourceLocation i) {
        super(i);
    }

    @Info(value="Sets the description text that shows in the item tooltip to describe what it can be applied to.\nUsing 'Armor' or 'Diamond Equipment' will use the vanilla language keys so it is translated into other languages automatically.\nTHIS IS PURELY VISUAL\n\nIf you wish to apply non standard formatting (like change the colour) set the `ingredientsText` field.\n")
    public SmithingTemplateItemBuilder appliesTo(String text) {
        this.appliesToText = switch (text) {
            case "Armor" -> SmithingTemplateItem.f_265948_;
            case "Diamond Equipment" -> SmithingTemplateItem.f_266054_;
            default -> this.defaultTranslateableTooltipComponent(text, "applies_to", true);
        };
        return this;
    }

    @Info(value="Sets the description text that shows in the item tooltip to describe what ingredients can be added.\nUsing 'Ingots & Crystals' or 'Netherite Ingot' will use the vanilla language keys so it is translated into other languages automatically.\nTHIS IS PURELY VISUAL\n\nIf you wish to apply non standard formatting (like change the colour) set the `ingredientsText` field.\n")
    public SmithingTemplateItemBuilder ingredients(String text) {
        this.ingredientsText = switch (text) {
            case "Ingots and Crystals", "Ingots & Crystals" -> SmithingTemplateItem.f_265960_;
            case "Netherite Ingot" -> SmithingTemplateItem.f_265856_;
            default -> this.defaultTranslateableTooltipComponent(text, "ingredients", true);
        };
        return this;
    }

    @Info(value="Sets the description text that shows when you hover over the base item slot when this item is put in smithing table as a template.\nUsing 'Add a piece of armor' or 'Add diamond armor, weapon, or tool' will use the vanilla language keys so it is translated into other languages automatically.\n\nIf you wish to apply non standard formatting (like change the colour) set the `appliesToSlotDescriptionText` field.\n")
    public SmithingTemplateItemBuilder appliesToSlotDescription(String text) {
        this.appliesToSlotDescriptionText = switch (text) {
            case "Add a piece of armor" -> SmithingTemplateItem.f_265846_;
            case "Add diamond armor, weapon, or tool" -> SmithingTemplateItem.f_265863_;
            default -> this.defaultTranslateableTooltipComponent(text, "base_slot_description", false);
        };
        return this;
    }

    @Info(value="Sets the description text that shows when you hover over the ingredient slot when this item is put in smithing table as a template.\nUsing 'Add ingot or crystal' or 'Add Netherite Ingot' will use the vanilla language keys so it is translated into other languages automatically.\n\nIf you wish to apply non standard formatting (like change the colour) set the `ingredientSlotDescriptionText` field.\n")
    public SmithingTemplateItemBuilder ingredientsSlotDescription(String text) {
        this.ingredientSlotDescriptionText = switch (text) {
            case "Add ingot or crystal" -> SmithingTemplateItem.f_265846_;
            case "Add Netherite Ingot" -> SmithingTemplateItem.f_265863_;
            default -> this.defaultTranslateableTooltipComponent(text, "ingredient_slot_description", false);
        };
        return this;
    }

    @Info(value="Adds the specified texture location to the list of base slot icons that the smithing table cycles through when this smithing template is put in.")
    public SmithingTemplateItemBuilder addAppliesToSlotIcon(ResourceLocation location) {
        this.appliesToEmptyIcons.add(location);
        return this;
    }

    @Info(value="Adds the specified texture location to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder addIngredientsSlotIcon(ResourceLocation location) {
        this.ingredientsSlotEmptyIcons.add(location);
        return this;
    }

    @Info(value="Adds all armor icons to the list of base slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder armorIcons() {
        this.appliesToEmptyIcons.addAll(ARMOR_ICONS);
        return this;
    }

    @Info(value="Adds all armor and basic tool icons to the list of base slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder equipmentIcons() {
        this.appliesToEmptyIcons.addAll(EQUIPMENT_ICONS);
        return this;
    }

    @Info(value="Adds all basic tool icons to the list of base slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder toolIcons() {
        this.appliesToEmptyIcons.addAll(TOOL_ICONS);
        return this;
    }

    @Info(value="Adds an ingot, dust, diamond, emerald, quartz, lapis lazuli and amethyst shard icons to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder ingotAndCrystalIcons() {
        this.ingredientsSlotEmptyIcons.addAll(INGOTS_AND_CRYSTALS_ICONS);
        return this;
    }

    @Info(value="Adds a dust, diamond, emerald, quartz, lapis lazuli and amethyst shard icons to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder crystalIcons() {
        this.ingredientsSlotEmptyIcons.addAll(CRYSTAL_ICONS);
        return this;
    }

    @Info(value="Adds an ingot to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder ingotIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_266026_);
    }

    @Info(value="Adds a dust to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder dustIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_266082_);
    }

    @Info(value="Adds an amethyst shard to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder shardIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_268746_);
    }

    @Info(value="Adds a diamond to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder diamondIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_265873_);
    }

    @Info(value="Adds an emerald to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder emeraldIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_266085_);
    }

    @Info(value="Adds a quartz to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder quartzIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_265894_);
    }

    @Info(value="Adds a lapis lazuli to the list of ingredient slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder lapisIcon() {
        return this.addIngredientsSlotIcon(SmithingTemplateItem.f_265879_);
    }

    @Info(value="Adds a sword to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder swordIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_266096_);
    }

    @Info(value="Adds a shovel to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder shovelIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_265932_);
    }

    @Info(value="Adds a axe to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder axeIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_265990_);
    }

    @Info(value="Adds a pickaxe to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder pickaxeIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_265955_);
    }

    @Info(value="Adds a hoe to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder hoeIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_265950_);
    }

    @Info(value="Adds a helmet to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder helmetIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_266113_);
    }

    @Info(value="Adds a chestplate to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder chestplateIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_266066_);
    }

    @Info(value="Adds leggings to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder leggingsIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_265859_);
    }

    @Info(value="Adds boots to the list of base item slot icons that the smithing table cycles through when this smithing template is put in")
    public SmithingTemplateItemBuilder bootsIcon() {
        return this.addAppliesToSlotIcon(SmithingTemplateItem.f_265917_);
    }

    private Component defaultTranslateableTooltipComponent(String text, String type, boolean tooltipDescription) {
        String translationKey = this.makeTooltipDescriptionId(type);
        this.translations.put(translationKey, text);
        MutableComponent component = Component.m_237115_((String)translationKey);
        if (tooltipDescription) {
            component.m_130940_(SmithingTemplateItem.f_265923_);
        }
        return component;
    }

    private String makeTooltipDescriptionId(String type) {
        return this.getTranslationKeyGroup() + "." + this.id.m_135827_() + ".smithing_template." + this.id.m_135815_() + "." + type;
    }

    @Info(value="Sets the name for this smithing template.\nNote that the normal display name for all smithing templates is the same and cannot be changed, this instead sets the name in the tooltip (see vanilla smithing templates for what this looks like).\n\nThis will be overridden by a lang file if it exists.\n")
    public SmithingTemplateItemBuilder displayName(Component name) {
        super.displayName((Component)name.m_6881_().m_130940_(SmithingTemplateItem.f_265906_));
        return this;
    }

    @Override
    public void generateLang(LangEventJS lang) {
        super.generateLang(lang);
        lang.addAll(this.id.m_135827_(), this.translations);
    }

    @Override
    public SmithingTemplateItem createObject() {
        return new SmithingTemplateItem(this.appliesToText, this.ingredientsText, (Component)Objects.requireNonNullElse(this.displayName, Component.m_237115_((String)this.getBuilderTranslationKey()).m_130940_(SmithingTemplateItem.f_265906_)), this.appliesToSlotDescriptionText, this.ingredientSlotDescriptionText, this.appliesToEmptyIcons, this.ingredientsSlotEmptyIcons);
    }
}

