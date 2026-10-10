/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.entity.EntityType
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.client.event.RegisterEntitySpectatorShadersEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class EntitySpectatorShaderManager {
    private static Map<EntityType<?>, ResourceLocation> SHADERS;

    @Nullable
    public static ResourceLocation get(EntityType<?> entityType) {
        return SHADERS.get(entityType);
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap shaders = new HashMap();
        RegisterEntitySpectatorShadersEvent event = new RegisterEntitySpectatorShadersEvent(shaders);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        SHADERS = ImmutableMap.copyOf(shaders);
    }

    private EntitySpectatorShaderManager() {
    }
}

