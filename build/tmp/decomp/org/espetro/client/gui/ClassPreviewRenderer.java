/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.espetro.network.UnifiedDeployScreenPacket;

public final class ClassPreviewRenderer {
    private UnifiedDeployScreenPacket.LoadoutPreview currentPreview;

    public void update(UnifiedDeployScreenPacket.LoadoutPreview preview) {
        UnifiedDeployScreenPacket.LoadoutPreview next;
        UnifiedDeployScreenPacket.LoadoutPreview loadoutPreview = next = preview != null ? preview : UnifiedDeployScreenPacket.LoadoutPreview.empty();
        if (ClassPreviewRenderer.samePreview(next, this.currentPreview)) {
            return;
        }
        this.currentPreview = next;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void render(GuiGraphics graphics, int centerX, int centerY, int scale, float mouseDeltaX, float mouseDeltaY) {
        UnifiedDeployScreenPacket.LoadoutPreview preview = this.currentPreview;
        LocalPlayer player = Minecraft.m_91087_().f_91074_;
        if (preview == null || player == null) {
            return;
        }
        Inventory inventory = player.m_150109_();
        int selectedSlot = inventory.f_35977_;
        ItemStack originalHead = player.m_6844_(EquipmentSlot.HEAD);
        ItemStack originalChest = player.m_6844_(EquipmentSlot.CHEST);
        ItemStack originalLegs = player.m_6844_(EquipmentSlot.LEGS);
        ItemStack originalFeet = player.m_6844_(EquipmentSlot.FEET);
        ItemStack originalOffHand = player.m_6844_(EquipmentSlot.OFFHAND);
        ItemStack originalMainHand = inventory.m_8020_(selectedSlot);
        try {
            player.m_8061_(EquipmentSlot.HEAD, preview.head);
            player.m_8061_(EquipmentSlot.CHEST, preview.chest);
            player.m_8061_(EquipmentSlot.LEGS, preview.legs);
            player.m_8061_(EquipmentSlot.FEET, preview.feet);
            player.m_8061_(EquipmentSlot.OFFHAND, preview.offHand);
            inventory.m_6836_(selectedSlot, preview.mainHand);
            InventoryScreen.m_274545_(graphics, centerX, centerY, scale, mouseDeltaX, mouseDeltaY, player);
        }
        finally {
            player.m_8061_(EquipmentSlot.HEAD, originalHead);
            player.m_8061_(EquipmentSlot.CHEST, originalChest);
            player.m_8061_(EquipmentSlot.LEGS, originalLegs);
            player.m_8061_(EquipmentSlot.FEET, originalFeet);
            player.m_8061_(EquipmentSlot.OFFHAND, originalOffHand);
            inventory.m_6836_(selectedSlot, originalMainHand);
            inventory.f_35977_ = selectedSlot;
        }
    }

    public void clear() {
        this.currentPreview = null;
    }

    public boolean isReady() {
        return this.currentPreview != null && Minecraft.m_91087_().f_91074_ != null;
    }

    private static boolean samePreview(UnifiedDeployScreenPacket.LoadoutPreview a, UnifiedDeployScreenPacket.LoadoutPreview b) {
        if (a == b) {
            return true;
        }
        if (a == null || b == null) {
            return false;
        }
        return ItemStack.m_41728_(a.head, b.head) && ItemStack.m_41728_(a.chest, b.chest) && ItemStack.m_41728_(a.legs, b.legs) && ItemStack.m_41728_(a.feet, b.feet) && ItemStack.m_41728_(a.mainHand, b.mainHand) && ItemStack.m_41728_(a.offHand, b.offHand);
    }
}

