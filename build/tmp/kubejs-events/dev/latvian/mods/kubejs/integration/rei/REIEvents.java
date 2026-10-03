/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.integration.rei;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.integration.rei.AddREIEventJS;
import dev.latvian.mods.kubejs.integration.rei.GroupREIEntriesEventJS;
import dev.latvian.mods.kubejs.integration.rei.HideREIEventJS;
import dev.latvian.mods.kubejs.integration.rei.InformationREIEventJS;
import dev.latvian.mods.kubejs.integration.rei.RemoveREICategoryEventJS;
import dev.latvian.mods.kubejs.integration.rei.RemoveREIRecipeEventJS;

public interface REIEvents {
    public static final EventGroup GROUP = EventGroup.of("REIEvents");
    public static final EventHandler HIDE = GROUP.client("hide", () -> HideREIEventJS.class).extra(Extra.REQUIRES_ID);
    public static final EventHandler ADD = GROUP.client("add", () -> AddREIEventJS.class).extra(Extra.REQUIRES_ID);
    public static final EventHandler INFORMATION = GROUP.client("information", () -> InformationREIEventJS.class);
    public static final EventHandler REMOVE_CATEGORIES = GROUP.client("removeCategories", () -> RemoveREICategoryEventJS.class);
    public static final EventHandler REMOVE_RECIPES = GROUP.client("removeRecipes", () -> RemoveREIRecipeEventJS.class);
    public static final EventHandler GROUP_ENTRIES = GROUP.client("groupEntries", () -> GroupREIEntriesEventJS.class);

    public static void register() {
        GROUP.register();
    }
}

