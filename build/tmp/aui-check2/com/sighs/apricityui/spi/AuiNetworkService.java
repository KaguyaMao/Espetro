/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.spi;

import com.sighs.apricityui.element.ContainerDeclaration;
import com.sighs.apricityui.spi.AuiPendingMenu;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

public interface AuiNetworkService {
    public AuiPendingMenu pendingMenu(ServerPlayer var1, String var2);

    public void openScreen(ServerPlayer var1, String var2, List<ContainerDeclaration> var3);
}

