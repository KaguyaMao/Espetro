/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.InputConstants$Type
 *  net.minecraft.client.KeyMapping
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RegisterKeyMappingsEvent
 *  net.minecraftforge.client.settings.IKeyConflictContext
 *  net.minecraftforge.client.settings.KeyConflictContext
 *  net.minecraftforge.client.settings.KeyModifier
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 */
package com.sighs.apricityui.registry;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD, value={Dist.CLIENT}, modid="apricityui")
public class Keybindings {
    public static final KeyMapping RELEASE_MOUSE = new KeyMapping("key.apricityui.release_mouse", (IKeyConflictContext)KeyConflictContext.IN_GAME, KeyModifier.NONE, InputConstants.Type.KEYSYM, 342, "key.categories.apricityui");
    public static final KeyMapping RELOAD = new KeyMapping("key.apricityui.reload", (IKeyConflictContext)KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.Type.KEYSYM, 269, "key.categories.apricityui");
    public static final KeyMapping DEV_TOOLS = new KeyMapping("key.apricityui.dev_tools", (IKeyConflictContext)KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.Type.KEYSYM, 301, "key.categories.apricityui");
    public static final KeyMapping RESOURCE_MANAGER = new KeyMapping("key.apricityui.resource_manager", (IKeyConflictContext)KeyConflictContext.GUI, KeyModifier.NONE, InputConstants.Type.KEYSYM, 299, "key.categories.apricityui");

    @SubscribeEvent
    public static void registerKeyMapping(RegisterKeyMappingsEvent event) {
        event.register(RELEASE_MOUSE);
        event.register(RELOAD);
        event.register(DEV_TOOLS);
        event.register(RESOURCE_MANAGER);
    }
}

