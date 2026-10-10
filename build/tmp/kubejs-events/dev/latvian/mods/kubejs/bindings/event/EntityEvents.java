/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 */
package dev.latvian.mods.kubejs.bindings.event;

import dev.latvian.mods.kubejs.entity.CheckLivingEntitySpawnEventJS;
import dev.latvian.mods.kubejs.entity.EntitySpawnedEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityDeathEventJS;
import dev.latvian.mods.kubejs.entity.LivingEntityHurtEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;
import dev.latvian.mods.kubejs.event.Extra;
import dev.latvian.mods.kubejs.registry.RegistryInfo;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

public interface EntityEvents {
    public static final EventGroup GROUP = EventGroup.of("EntityEvents");
    public static final Extra SUPPORTS_ENTITY_TYPE = new Extra().transformer(EntityEvents::transformEntityType).identity().describeType(context -> context.javaType(EntityType.class));
    public static final EventHandler DEATH = GROUP.common("death", () -> LivingEntityDeathEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
    public static final EventHandler HURT = GROUP.common("hurt", () -> LivingEntityHurtEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
    public static final EventHandler CHECK_SPAWN = GROUP.common("checkSpawn", () -> CheckLivingEntitySpawnEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();
    public static final EventHandler SPAWNED = GROUP.common("spawned", () -> EntitySpawnedEventJS.class).extra(SUPPORTS_ENTITY_TYPE).hasResult();

    private static Object transformEntityType(Object o) {
        if (o == null || o instanceof EntityType) {
            return o;
        }
        ResourceLocation id = ResourceLocation.m_135820_((String)o.toString());
        return id == null ? null : RegistryInfo.ENTITY_TYPE.getValue(id);
    }
}

