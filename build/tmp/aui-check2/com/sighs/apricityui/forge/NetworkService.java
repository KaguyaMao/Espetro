/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.forge;

import com.sighs.apricityui.element.ContainerDeclaration;
import com.sighs.apricityui.network.handler.ApricityScreenNetworkHandler;
import com.sighs.apricityui.network.handler.BindingBuilder;
import com.sighs.apricityui.network.handler.PendingMenu;
import com.sighs.apricityui.spi.AuiNetworkService;
import com.sighs.apricityui.spi.AuiPendingMenu;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.server.level.ServerPlayer;

public final class NetworkService
implements AuiNetworkService {
    public static final NetworkService INSTANCE = new NetworkService();

    private NetworkService() {
    }

    @Override
    public AuiPendingMenu pendingMenu(ServerPlayer player, String templatePath) {
        return new PendingMenuAdapter(player, templatePath);
    }

    @Override
    public void openScreen(ServerPlayer player, String templatePath, List<ContainerDeclaration> declarations) {
        ApricityScreenNetworkHandler.openScreen(player, templatePath, declarations);
    }

    public static final class PendingMenuAdapter
    implements AuiPendingMenu {
        private final PendingMenu delegate;

        public PendingMenuAdapter(ServerPlayer player, String templatePath) {
            this.delegate = new PendingMenu(player, templatePath);
        }

        @Override
        public void bind(Consumer<Object> binder) {
            if (binder == null) {
                this.delegate.bind(null);
                return;
            }
            this.delegate.bind((BindingBuilder builder) -> binder.accept(builder));
        }
    }
}

