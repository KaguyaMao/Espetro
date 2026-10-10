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
package net.minecraftforge.client.textures;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterTextureAtlasSpriteLoadersEvent;
import net.minecraftforge.client.textures.ITextureAtlasSpriteLoader;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public final class TextureAtlasSpriteLoaderManager {
    private static ImmutableMap<ResourceLocation, ITextureAtlasSpriteLoader> LOADERS;

    @Nullable
    public static ITextureAtlasSpriteLoader get(ResourceLocation name) {
        return (ITextureAtlasSpriteLoader)LOADERS.get((Object)name);
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap<ResourceLocation, ITextureAtlasSpriteLoader> loaders = new HashMap<ResourceLocation, ITextureAtlasSpriteLoader>();
        RegisterTextureAtlasSpriteLoadersEvent event = new RegisterTextureAtlasSpriteLoadersEvent(loaders);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        LOADERS = ImmutableMap.copyOf(loaders);
    }

    private TextureAtlasSpriteLoaderManager() {
    }
}

