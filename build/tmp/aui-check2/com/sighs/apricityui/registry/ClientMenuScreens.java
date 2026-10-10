/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.screens.MenuScreens
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber$Bus
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package com.sighs.apricityui.registry;

import com.sighs.apricityui.registry.ApricityMenus;
import com.sighs.apricityui.screen.ApricityContainerMenu;
import java.lang.reflect.Constructor;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT}, bus=Mod.EventBusSubscriber.Bus.MOD)
public class ClientMenuScreens {
    private static final String CONTAINER_SCREEN_CLASS = "com.sighs.apricityui.screen.ApricityContainerScreen";

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> MenuScreens.m_96206_((MenuType)((MenuType)ApricityMenus.APRICITY_CONTAINER.get()), ClientMenuScreens::createScreen));
    }

    private static AbstractContainerScreen<ApricityContainerMenu> createScreen(ApricityContainerMenu menu, Inventory inventory, Component title) {
        try {
            Class<?> screenClass = Class.forName(CONTAINER_SCREEN_CLASS);
            Constructor<?> constructor = screenClass.getConstructor(ApricityContainerMenu.class, Inventory.class, Component.class);
            return (AbstractContainerScreen)constructor.newInstance(new Object[]{menu, inventory, title});
        }
        catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Failed to create Apricity container screen", exception);
        }
    }
}

