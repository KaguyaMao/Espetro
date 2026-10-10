/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.event.StartupEventJS;
import dev.latvian.mods.kubejs.item.creativetab.CreativeTabEvent;
import dev.latvian.mods.kubejs.recipe.RecipeSchemaRegistryEventJS;
import dev.latvian.mods.kubejs.registry.RegistryEventJS;

public interface StartupEvents {
    public static final EventGroup GROUP = EventGroup.of("StartupEvents");
    public static final EventHandler INIT = GROUP.startup("init", () -> StartupEventJS.class);
    public static final EventHandler POST_INIT = GROUP.startup("postInit", () -> StartupEventJS.class);
    public static final EventHandler REGISTRY = GROUP.startup("registry", () -> RegistryEventJS.class).extra(Extra.REQUIRES_REGISTRY);
    public static final EventHandler RECIPE_SCHEMA_REGISTRY = GROUP.startup("recipeSchemaRegistry", () -> RecipeSchemaRegistryEventJS.class);
    public static final EventHandler MODIFY_CREATIVE_TAB = GROUP.startup("modifyCreativeTab", () -> CreativeTabEvent.class).extra(Extra.REQUIRES_ID);
}

