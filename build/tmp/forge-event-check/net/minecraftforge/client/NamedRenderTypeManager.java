/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.ImmutableMap
 *  net.minecraft.client.renderer.RenderType
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraftforge.eventbus.api.Event
 *  net.minecraftforge.fml.ModLoader
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client;

import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.client.RenderTypeGroup;
import net.minecraftforge.client.event.RegisterNamedRenderTypesEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.fml.ModLoader;
import org.jetbrains.annotations.ApiStatus;

public final class NamedRenderTypeManager {
    private static ImmutableMap<ResourceLocation, RenderTypeGroup> RENDER_TYPES;

    public static RenderTypeGroup get(ResourceLocation name) {
        return (RenderTypeGroup)RENDER_TYPES.getOrDefault((Object)name, (Object)RenderTypeGroup.EMPTY);
    }

    @ApiStatus.Internal
    public static void init() {
        HashMap<ResourceLocation, RenderTypeGroup> renderTypes = new HashMap<ResourceLocation, RenderTypeGroup>();
        NamedRenderTypeManager.preRegisterVanillaRenderTypes(renderTypes);
        RegisterNamedRenderTypesEvent event = new RegisterNamedRenderTypesEvent(renderTypes);
        ModLoader.get().postEventWrapContainerInModOrder((Event)event);
        RENDER_TYPES = ImmutableMap.copyOf(renderTypes);
    }

    private static void preRegisterVanillaRenderTypes(Map<ResourceLocation, RenderTypeGroup> blockRenderTypes) {
        blockRenderTypes.put(new ResourceLocation("solid"), new RenderTypeGroup(RenderType.m_110451_(), ForgeRenderTypes.ITEM_LAYERED_SOLID.get()));
        blockRenderTypes.put(new ResourceLocation("cutout"), new RenderTypeGroup(RenderType.m_110463_(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get()));
        blockRenderTypes.put(new ResourceLocation("cutout_mipped"), new RenderTypeGroup(RenderType.m_110457_(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT.get()));
        blockRenderTypes.put(new ResourceLocation("cutout_mipped_all"), new RenderTypeGroup(RenderType.m_110457_(), ForgeRenderTypes.ITEM_LAYERED_CUTOUT_MIPPED.get()));
        blockRenderTypes.put(new ResourceLocation("translucent"), new RenderTypeGroup(RenderType.m_110466_(), ForgeRenderTypes.ITEM_LAYERED_TRANSLUCENT.get()));
        blockRenderTypes.put(new ResourceLocation("tripwire"), new RenderTypeGroup(RenderType.m_110503_(), ForgeRenderTypes.ITEM_LAYERED_TRANSLUCENT.get()));
    }

    private NamedRenderTypeManager() {
    }
}

