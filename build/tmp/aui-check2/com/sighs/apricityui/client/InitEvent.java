/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.init.Window;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT})
public class InitEvent {
    private static int tickCounter = 0;

    @SubscribeEvent
    public static void localStorageInit(FMLClientSetupEvent event) {
        Window.window.localStorage.load();
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END && ++tickCounter >= 5000) {
            tickCounter = 0;
            Window.window.localStorage.save();
        }
    }
}

