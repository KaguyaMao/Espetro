/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.ImmutableList$Builder
 *  net.minecraft.client.color.block.BlockTintCache
 *  net.minecraft.client.multiplayer.ClientLevel
 *  net.minecraft.world.level.ColorResolver
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableList;
import java.util.Map;
import net.minecraft.client.color.block.BlockTintCache;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.level.ColorResolver;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

public final class ColorResolverManager {
    private static ImmutableList<ColorResolver> colorResolvers;

    @ApiStatus.Internal
    public static void init() {
        ImmutableList.Builder builder = ImmutableList.builder();
        ModLoader.get().postEvent((Event)new RegisterColorHandlersEvent.ColorResolvers((ImmutableList.Builder<ColorResolver>)builder));
        colorResolvers = builder.build();
    }

    public static void registerBlockTintCaches(ClientLevel level, Map<ColorResolver, BlockTintCache> target) {
        for (ColorResolver resolver : colorResolvers) {
            target.put(resolver, new BlockTintCache(pos -> level.m_104762_(pos, resolver)));
        }
    }

    private ColorResolverManager() {
    }
}

