/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.eventbus.api.IEventBus
 *  net.minecraftforge.fml.ModList
 *  net.minecraftforge.fml.ModLoadingContext
 *  net.minecraftforge.fml.common.Mod
 *  net.minecraftforge.fml.config.IConfigSpec
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.event.config.ModConfigEvent$Reloading
 *  net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext
 *  net.minecraftforge.fml.loading.FMLEnvironment
 *  net.minecraftforge.server.ServerLifecycleHooks
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.config.ApricityUIConfig;
import com.sighs.apricityui.forge.AuiServicesBootstrap;
import com.sighs.apricityui.forge.ClientServicesBootstrap;
import com.sighs.apricityui.network.NetworkPlatform;
import com.sighs.apricityui.network.api.NetworkAutoRegistration;
import com.sighs.apricityui.network.forge.NetworkManagerImpl;
import com.sighs.apricityui.registry.ApricityMenus;
import com.sighs.apricityui.registry.ApricityUIRegistry;
import com.sighs.apricityui.script.KubeJS;
import com.sighs.apricityui.util.AuiLogging;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.IConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

@Mod(value="apricityui")
public class ApricityUIForge {
    public ApricityUIForge() {
        AuiLogging.installFileAppender();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        AuiServicesBootstrap.init();
        NetworkPlatform.setCurrentServerSupplier(ServerLifecycleHooks::getCurrentServer);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientServicesBootstrap.init(modEventBus);
        }
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, (IConfigSpec)ApricityUIConfig.CLIENT_SPEC);
        modEventBus.addListener(this::onConfigReload);
        if (ModList.get().isLoaded("kubejs")) {
            KubeJS.scanPackage("com.sighs.apricityui.util.kjs");
        }
        ApricityUIRegistry.scanPackages("com.sighs.apricityui.element", "com.sighs.apricityui.element");
        ApricityMenus.register(modEventBus);
        NetworkManagerImpl.installAutoRegistrationHook();
        NetworkAutoRegistration.findAllAnnotatedPackets();
    }

    private void onConfigReload(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() != ApricityUIConfig.CLIENT_SPEC) {
            return;
        }
        ApricityUIConfig.markClientReloadPending();
    }
}

