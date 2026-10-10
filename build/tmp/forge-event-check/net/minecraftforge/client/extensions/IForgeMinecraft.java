/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.screens.Screen
 */
package net.minecraftforge.client.extensions;

import java.util.Locale;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraftforge.client.ForgeHooksClient;

public interface IForgeMinecraft {
    private Minecraft self() {
        return (Minecraft)this;
    }

    default public void pushGuiLayer(Screen screen) {
        ForgeHooksClient.pushGuiLayer(this.self(), screen);
    }

    default public void popGuiLayer() {
        ForgeHooksClient.popGuiLayer(this.self());
    }

    default public Locale getLocale() {
        return this.self().m_91102_().getJavaLocale();
    }
}

