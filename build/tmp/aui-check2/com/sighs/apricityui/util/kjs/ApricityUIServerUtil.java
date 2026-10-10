/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 */
package com.sighs.apricityui.util.kjs;

import com.sighs.apricityui.element.ContainerDeclaration;
import com.sighs.apricityui.registry.annotation.KJSBindings;
import com.sighs.apricityui.spi.AuiPendingMenu;
import com.sighs.apricityui.spi.AuiServices;
import java.util.List;
import net.minecraft.server.level.ServerPlayer;

@KJSBindings(value="ApricityUI")
public class ApricityUIServerUtil {
    public static AuiPendingMenu menu(ServerPlayer player, String path) {
        return AuiServices.network().pendingMenu(player, path);
    }

    @Deprecated
    public static void openScreen(ServerPlayer player, String path, List<ContainerDeclaration> declarations) {
        AuiServices.network().openScreen(player, path, declarations);
    }

    @Deprecated
    public static void openScreen(ServerPlayer player, String path) {
        AuiServices.network().openScreen(player, path, List.of());
    }
}

