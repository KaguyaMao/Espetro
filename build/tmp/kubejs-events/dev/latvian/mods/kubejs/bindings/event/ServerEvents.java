/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.command.CommandRegistryEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.loot.BlockLootEventJS;
import dev.latvian.mods.kubejs.loot.ChestLootEventJS;
import dev.latvian.mods.kubejs.loot.EntityLootEventJS;
import dev.latvian.mods.kubejs.loot.FishingLootEventJS;
import dev.latvian.mods.kubejs.loot.GenericLootEventJS;
import dev.latvian.mods.kubejs.loot.GiftLootEventJS;
import dev.latvian.mods.kubejs.recipe.AfterRecipesLoadedEventJS;
import dev.latvian.mods.kubejs.recipe.CompostableRecipesEventJS;
import dev.latvian.mods.kubejs.recipe.RecipesEventJS;
import dev.latvian.mods.kubejs.recipe.special.SpecialRecipeSerializerManager;
import dev.latvian.mods.kubejs.script.data.DataPackEventJS;
import dev.latvian.mods.kubejs.server.CommandEventJS;
import dev.latvian.mods.kubejs.server.CustomCommandEventJS;
import dev.latvian.mods.kubejs.server.ServerEventJS;
import dev.latvian.mods.kubejs.server.tag.TagEventJS;

public interface ServerEvents {
    public static final EventGroup GROUP = EventGroup.of("ServerEvents");
    public static final EventHandler LOW_DATA = GROUP.server("lowPriorityData", () -> DataPackEventJS.class);
    public static final EventHandler HIGH_DATA = GROUP.server("highPriorityData", () -> DataPackEventJS.class);
    public static final EventHandler LOADED = GROUP.server("loaded", () -> ServerEventJS.class);
    public static final EventHandler UNLOADED = GROUP.server("unloaded", () -> ServerEventJS.class);
    public static final EventHandler TICK = GROUP.server("tick", () -> ServerEventJS.class);
    public static final EventHandler TAGS = GROUP.server("tags", () -> TagEventJS.class).extra(Extra.REQUIRES_REGISTRY);
    public static final EventHandler COMMAND_REGISTRY = GROUP.server("commandRegistry", () -> CommandRegistryEventJS.class);
    public static final EventHandler COMMAND = GROUP.server("command", () -> CommandEventJS.class).extra(Extra.STRING).hasResult();
    public static final EventHandler CUSTOM_COMMAND = GROUP.server("customCommand", () -> CustomCommandEventJS.class).extra(Extra.STRING).hasResult();
    public static final EventHandler RECIPES = GROUP.server("recipes", () -> RecipesEventJS.class);
    public static final EventHandler RECIPES_AFTER_LOADED = GROUP.server("afterRecipes", () -> AfterRecipesLoadedEventJS.class);
    public static final EventHandler SPECIAL_RECIPES = GROUP.server("specialRecipeSerializers", () -> SpecialRecipeSerializerManager.class);
    public static final EventHandler COMPOSTABLE_RECIPES = GROUP.server("compostableRecipes", () -> CompostableRecipesEventJS.class);
    public static final EventHandler GENERIC_LOOT_TABLES = GROUP.server("genericLootTables", () -> GenericLootEventJS.class);
    public static final EventHandler BLOCK_LOOT_TABLES = GROUP.server("blockLootTables", () -> BlockLootEventJS.class);
    public static final EventHandler ENTITY_LOOT_TABLES = GROUP.server("entityLootTables", () -> EntityLootEventJS.class);
    public static final EventHandler GIFT_LOOT_TABLES = GROUP.server("giftLootTables", () -> GiftLootEventJS.class);
    public static final EventHandler FISHING_LOOT_TABLES = GROUP.server("fishingLootTables", () -> FishingLootEventJS.class);
    public static final EventHandler CHEST_LOOT_TABLES = GROUP.server("chestLootTables", () -> ChestLootEventJS.class);
}

