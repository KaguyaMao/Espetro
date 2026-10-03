/*
 * Decompiled with CFR 0.152.
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.level.ExplosionEventJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;

public interface LevelEvents {
    public static final EventGroup GROUP = EventGroup.of("LevelEvents");
    public static final EventHandler LOADED = GROUP.server("loaded", () -> SimpleLevelEventJS.class).extra(Extra.ID);
    public static final EventHandler UNLOADED = GROUP.server("unloaded", () -> SimpleLevelEventJS.class).extra(Extra.ID);
    public static final EventHandler TICK = GROUP.common("tick", () -> SimpleLevelEventJS.class).extra(Extra.ID);
    public static final EventHandler BEFORE_EXPLOSION = GROUP.common("beforeExplosion", () -> ExplosionEventJS.Before.class).hasResult();
    public static final EventHandler AFTER_EXPLOSION = GROUP.common("afterExplosion", () -> ExplosionEventJS.After.class);
}

