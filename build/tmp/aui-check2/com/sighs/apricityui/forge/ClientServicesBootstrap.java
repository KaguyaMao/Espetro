/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.client.event.RegisterShadersEvent
 *  net.minecraftforge.eventbus.api.IEventBus
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.dev.DevToolsLogBridge;
import com.sighs.apricityui.forge.ClientService;
import com.sighs.apricityui.forge.KeyService;
import com.sighs.apricityui.forge.RenderService;
import com.sighs.apricityui.forge.ResourceService;
import com.sighs.apricityui.forge.ShaderRegistry;
import com.sighs.apricityui.registry.ApricityUIRegistry;
import com.sighs.apricityui.spi.AuiServices;
import java.io.IOException;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public final class ClientServicesBootstrap {
    private ClientServicesBootstrap() {
    }

    public static void init(IEventBus modEventBus) {
        AuiServices.setClient(ClientService.INSTANCE);
        AuiServices.setResources(ResourceService.INSTANCE);
        AuiServices.setKeys(KeyService.INSTANCE);
        AuiServices.setRender(RenderService.INSTANCE);
        DevToolsLogBridge.install(ApricityUI.LOGGER);
        ApricityUIRegistry.register();
        modEventBus.addListener(ClientServicesBootstrap::onRegisterShaders);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        try {
            ShaderRegistry.register(event);
        }
        catch (IOException iOException) {
            // empty catch block
        }
    }
}

