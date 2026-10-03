/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.network.handler;

import com.sighs.apricityui.network.handler.ApricityScreenNetworkHandler;
import com.sighs.apricityui.network.handler.BindingBuilder;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;

public final class PendingMenu {
    private final ServerPlayer player;
    private final String templatePath;

    public PendingMenu(ServerPlayer player, String templatePath) {
        this.player = player;
        this.templatePath = templatePath;
    }

    public void bind(Consumer<BindingBuilder> configurator) {
        BindingBuilder builder = new BindingBuilder();
        if (configurator != null) {
            configurator.accept(builder);
        }
        ApricityScreenNetworkHandler.openScreen(this.player, this.templatePath, builder.declarations(), builder.argsById());
    }
}

