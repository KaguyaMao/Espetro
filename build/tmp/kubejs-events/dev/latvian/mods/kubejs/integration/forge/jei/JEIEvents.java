/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.integration.forge.jei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.integration.forge.jei.AddJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.HideCustomJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.HideJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.InformationJEIEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.JEISubtypesEventJS;
import dev.latvian.mods.kubejs.integration.forge.jei.RemoveJEICategoriesEvent;
import dev.latvian.mods.kubejs.integration.forge.jei.RemoveJEIRecipesEvent;

public interface JEIEvents {
    public static final EventGroup GROUP = EventGroup.of("JEIEvents");
    public static final EventHandler SUBTYPES = GROUP.client("subtypes", () -> JEISubtypesEventJS.class);
    public static final EventHandler HIDE_ITEMS = GROUP.client("hideItems", () -> HideJEIEventJS.class);
    public static final EventHandler HIDE_FLUIDS = GROUP.client("hideFluids", () -> HideJEIEventJS.class);
    public static final EventHandler HIDE_CUSTOM = GROUP.client("hideCustom", () -> HideCustomJEIEventJS.class);
    public static final EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveJEICategoriesEvent.class);
    public static final EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveJEIRecipesEvent.class);
    public static final EventHandler ADD_ITEMS = GROUP.client("addItems", () -> AddJEIEventJS.class);
    public static final EventHandler ADD_FLUIDS = GROUP.client("addFluids", () -> AddJEIEventJS.class);
    public static final EventHandler INFORMATION = GROUP.client("information", () -> InformationJEIEventJS.class);

    public static void register() {
        GROUP.register();
    }
}

