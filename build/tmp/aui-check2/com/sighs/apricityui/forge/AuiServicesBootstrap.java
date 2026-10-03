/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.dom.ForgeDocumentExpander;
import com.sighs.apricityui.forge.ConfigService;
import com.sighs.apricityui.forge.ItemRenderService;
import com.sighs.apricityui.forge.NetworkService;
import com.sighs.apricityui.forge.RenderService;
import com.sighs.apricityui.forge.ScriptService;
import com.sighs.apricityui.spi.AuiServices;

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
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        try {
            AuiServices.setItems(ItemRenderService.INSTANCE);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }
}

