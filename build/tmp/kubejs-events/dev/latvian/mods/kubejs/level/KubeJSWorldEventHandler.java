/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.EventResult
 *  dev.architectury.event.events.common.ExplosionEvent
 *  dev.architectury.event.events.common.LifecycleEvent
 *  dev.architectury.event.events.common.TickEvent
 *  net.minecraft.server.level.ServerLevel
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Explosion
 *  net.minecraft.world.level.Level
 */
package dev.latvian.mods.kubejs.level;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.ExplosionEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.architectury.event.events.common.TickEvent;
import dev.latvian.mods.kubejs.bindings.event.LevelEvents;
import dev.latvian.mods.kubejs.level.ExplosionEventJS;
import dev.latvian.mods.kubejs.level.SimpleLevelEventJS;
import dev.latvian.mods.kubejs.script.ScriptType;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import java.util.List;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;

public class KubeJSWorldEventHandler {
    public static void init() {
        LifecycleEvent.SERVER_LEVEL_LOAD.register(KubeJSWorldEventHandler::levelLoad);
        LifecycleEvent.SERVER_LEVEL_UNLOAD.register(KubeJSWorldEventHandler::levelUnload);
        TickEvent.SERVER_LEVEL_POST.register(KubeJSWorldEventHandler::levelPostTick);
        ExplosionEvent.PRE.register(KubeJSWorldEventHandler::preExplosion);
        ExplosionEvent.DETONATE.register(KubeJSWorldEventHandler::detonateExplosion);
    }

    private static void levelLoad(ServerLevel level) {
        if (LevelEvents.LOADED.hasListeners()) {
            LevelEvents.LOADED.post(new SimpleLevelEventJS((Level)level), level.m_46472_().m_135782_());
        }
    }

    private static void levelUnload(ServerLevel level) {
        if (LevelEvents.UNLOADED.hasListeners()) {
            LevelEvents.UNLOADED.post(new SimpleLevelEventJS((Level)level), level.m_46472_().m_135782_());
        }
    }

    private static void levelPostTick(ServerLevel level) {
        if (LevelEvents.TICK.hasListeners()) {
            LevelEvents.TICK.post((ScriptTypeHolder)ScriptType.SERVER, (Object)level.m_46472_().m_135782_(), new SimpleLevelEventJS((Level)level));
        }
    }

    private static EventResult preExplosion(Level level, Explosion explosion) {
        return LevelEvents.BEFORE_EXPLOSION.hasListeners() ? LevelEvents.BEFORE_EXPLOSION.post((ScriptTypeHolder)level, new ExplosionEventJS.Before(level, explosion)).arch() : EventResult.pass();
    }

    private static void detonateExplosion(Level level, Explosion explosion, List<Entity> affectedEntities) {
        if (LevelEvents.AFTER_EXPLOSION.hasListeners()) {
            LevelEvents.AFTER_EXPLOSION.post((ScriptTypeHolder)level, new ExplosionEventJS.After(level, explosion, affectedEntities));
        }
    }
}

