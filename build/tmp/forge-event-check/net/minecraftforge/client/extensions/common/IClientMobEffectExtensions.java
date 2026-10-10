/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.gui.Gui
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen
 *  net.minecraft.world.effect.MobEffect
 *  net.minecraft.world.effect.MobEffectInstance
 */
package net.minecraftforge.client.extensions.common;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public interface IClientMobEffectExtensions {
    public static final IClientMobEffectExtensions DEFAULT = new IClientMobEffectExtensions(){};

    public static IClientMobEffectExtensions of(MobEffectInstance instance) {
        return IClientMobEffectExtensions.of(instance.m_19544_());
    }

    public static IClientMobEffectExtensions of(MobEffect effect) {
        IClientMobEffectExtensions r;
        Object object = effect.getEffectRendererInternal();
        return object instanceof IClientMobEffectExtensions ? (r = (IClientMobEffectExtensions)object) : DEFAULT;
    }

    default public boolean isVisibleInInventory(MobEffectInstance instance) {
        return true;
    }

    default public boolean isVisibleInGui(MobEffectInstance instance) {
        return true;
    }

    default public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
        return false;
    }

    default public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, GuiGraphics guiGraphics, int x, int y, int blitOffset) {
        return false;
    }

    default public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, GuiGraphics guiGraphics, int x, int y, float z, float alpha) {
        return false;
    }
}

