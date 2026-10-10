/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.architectury.event.EventResult
 *  dev.architectury.event.events.common.EntityEvent
 *  net.minecraft.world.damagesource.DamageSource
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.MobSpawnType
 *  net.minecraft.world.level.BaseSpawner
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.level.LevelAccessor
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.entity;

import dev.architectury.event.EventResult;
import dev.architectury.event.events.common.EntityEvent;
import dev.latvian.mods.kubejs.bindings.event.EntityEvents;
import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnEventJS;
import dev.latvian.mods.kubejs.entity.EntitySpawnedEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtEventJS;
import dev.latvian.mods.kubejs.script.ScriptTypeHolder;
import dev.latvian.mods.kubejs.util.UtilsJS;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

public class KubeJSEntityEventHandler {
    public static void init() {
        EntityEvent.LIVING_CHECK_SPAWN.register(KubeJSEntityEventHandler::checkSpawn);
        EntityEvent.LIVING_DEATH.register(KubeJSEntityEventHandler::livingDeath);
        EntityEvent.LIVING_HURT.register(KubeJSEntityEventHandler::livingHurt);
        EntityEvent.ADD.register(KubeJSEntityEventHandler::entitySpawned);
    }

    private static EventResult checkSpawn(LivingEntity entity, LevelAccessor la, double x, double y, double z, MobSpawnType type, @Nullable BaseSpawner spawner) {
        if (la instanceof Level) {
            Level level = (Level)la;
            if ((la.m_5776_() || UtilsJS.staticServer != null) && EntityEvents.CHECK_SPAWN.hasListeners()) {
                return EntityEvents.CHECK_SPAWN.post((ScriptTypeHolder)level, (Object)entity.m_6095_(), new CheckLivingEntitySpawnEventJS(entity, level, x, y, z, type, spawner)).arch();
            }
        }
        return EventResult.pass();
    }

    private static EventResult livingDeath(LivingEntity entity, DamageSource source) {
        return EntityEvents.DEATH.hasListeners() ? EntityEvents.DEATH.post((ScriptTypeHolder)entity, (Object)entity.m_6095_(), new LivingEntityDeathEventJS(entity, source)).arch() : EventResult.pass();
    }

    private static EventResult livingHurt(LivingEntity entity, DamageSource source, float amount) {
        return EntityEvents.HURT.hasListeners() ? EntityEvents.HURT.post((ScriptTypeHolder)entity, (Object)entity.m_6095_(), new LivingEntityHurtEventJS(entity, source, amount)).arch() : EventResult.pass();
    }

    private static EventResult entitySpawned(Entity entity, Level level) {
        if ((level.m_5776_() || UtilsJS.staticServer != null) && EntityEvents.SPAWNED.hasListeners()) {
            return EntityEvents.SPAWNED.post((ScriptTypeHolder)level, (Object)entity.m_6095_(), new EntitySpawnedEventJS(entity, level)).arch();
        }
        return EventResult.pass();
    }
}

