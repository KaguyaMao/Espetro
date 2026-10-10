package com.sighs.apricityui.forge;

import com.sighs.apricityui.dom.ForgeDocumentExpander;
import com.sighs.apricityui.spi.AuiServices;

/**
 * Patched for dedicated servers: the render/item-render services only exist on
 * the client (they reference net.minecraft.client classes). On a dedicated
 * server those registrations are skipped instead of crashing the mod.
 */
public final class AuiServicesBootstrap {

    private AuiServicesBootstrap() {
    }

    public static void init() {
    }

    static {
        AuiServices.setNetwork(NetworkService.INSTANCE);
        AuiServices.setExpander(new ForgeDocumentExpander());
        AuiServices.setConfig(ConfigService.INSTANCE);
        AuiServices.setScript(ScriptService.INSTANCE);
        try {
            AuiServices.setRender(RenderService.INSTANCE);
        } catch (Throwable ignored) {
            // dedicated server: no client render classes
        }
        try {
            AuiServices.setItems(ItemRenderService.INSTANCE);
        } catch (Throwable ignored) {
            // dedicated server: no client render classes
        }
    }
}