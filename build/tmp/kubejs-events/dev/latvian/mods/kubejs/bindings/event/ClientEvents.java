/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.client.AtlasSpriteRegistryEventJS;
import dev.latvian.mods.kubejs.client.ClientEventJS;
import dev.latvian.mods.kubejs.client.ClientInitEventJS;
import dev.latvian.mods.kubejs.client.DebugInfoEventJS;
import dev.latvian.mods.kubejs.client.GenerateClientAssetsEventJS;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.client.painter.screen.PaintScreenEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;

public interface ClientEvents {
    public static final EventGroup GROUP = EventGroup.of("ClientEvents");
    public static final EventHandler HIGH_ASSETS = GROUP.client("highPriorityAssets", () -> GenerateClientAssetsEventJS.class);
    public static final EventHandler INIT = GROUP.startup("init", () -> ClientInitEventJS.class);
    public static final EventHandler LOGGED_IN = GROUP.client("loggedIn", () -> ClientEventJS.class);
    public static final EventHandler LOGGED_OUT = GROUP.client("loggedOut", () -> ClientEventJS.class);
    public static final EventHandler TICK = GROUP.client("tick", () -> ClientEventJS.class);
    public static final EventHandler PAINTER_UPDATED = GROUP.client("painterUpdated", () -> ClientEventJS.class);
    public static final EventHandler DEBUG_LEFT = GROUP.client("leftDebugInfo", () -> DebugInfoEventJS.class);
    public static final EventHandler DEBUG_RIGHT = GROUP.client("rightDebugInfo", () -> DebugInfoEventJS.class);
    public static final EventHandler PAINT_SCREEN = GROUP.client("paintScreen", () -> PaintScreenEventJS.class);
    public static final EventHandler ATLAS_SPRITE_REGISTRY = GROUP.client("atlasSpriteRegistry", () -> AtlasSpriteRegistryEventJS.class).extra(Extra.REQUIRES_ID);
    public static final EventHandler LANG = GROUP.client("lang", () -> LangEventJS.class).extra(Extra.REQUIRES_STRING);
}

