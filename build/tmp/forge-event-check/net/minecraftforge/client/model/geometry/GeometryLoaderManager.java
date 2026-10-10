/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.model.geometry;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.model.geometry.IGeometryLoader;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class GeometryLoaderManager {
    private static ImmutableMap<ResourceLocation, IGeometryLoader<?>> LOADERS;
    private static String LOADER_LIST;

    @Nullable
    public static IGeometryLoader<?> get(ResourceLocation name) {
        return (IGeometryLoader)LOADERS.get((Object)name);
    }

    public static String getLoaderList() {
        return LOADER_LIST;
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap loaders = new HashMap();
        ModelEvent.RegisterGeometryLoaders event = new ModelEvent.RegisterGeometryLoaders(loaders);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        LOADERS = ImmutableMap.copyOf(loaders);
        LOADER_LIST = loaders.keySet().stream().map(ResourceLocation::toString).collect(Collectors.joining(", "));
    }

    private GeometryLoaderManager() {
    }
}

