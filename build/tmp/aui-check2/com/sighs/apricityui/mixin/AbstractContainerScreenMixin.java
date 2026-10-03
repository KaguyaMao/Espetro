/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 *  org.spongepowered.asm.mixin.Mixin
 *  org.spongepowered.asm.mixin.gen.Invoker
 *  org.spongepowered.asm.mixin.injection.At
 *  org.spongepowered.asm.mixin.injection.Inject
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfo
 *  org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable
 */
package com.sighs.apricityui.mixin;

import com.sighs.apricityui.screen.ApricityContainerMenu;
import com.sighs.apricityui.screen.ApricityContainerScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value={AbstractContainerScreen.class})
public abstract class AbstractContainerScreenMixin {
    @Invoker(value="recalculateQuickCraftRemaining")
    protected abstract void apricityui$recalculateQuickCraftRemaining();

    @Inject(method={"renderSlot"}, at={@At(value="HEAD")}, cancellable=true)
    private void apricityui$cancelVanillaRenderSlot(GuiGraphics p_281607_, Slot p_282613_, CallbackInfo ci) {
        AbstractContainerScreenMixin abstractContainerScreenMixin = this;
        if (abstractContainerScreenMixin instanceof ApricityContainerScreen) {
            ApricityContainerScreen screen = (ApricityContainerScreen)((Object)abstractContainerScreenMixin);
            if (screen.pruneInvalidQuickCraftSlot(p_282613_)) {
                this.apricityui$recalculateQuickCraftRemaining();
            }
            ci.cancel();
        }
    }

    @Inject(method={"renderSlotHighlight(Lnet/minecraft/client/gui/GuiGraphics;IIII)V"}, at={@At(value="HEAD")}, cancellable=true, remap=false)
    private static void apricityui$cancelVanillaSlotHighlightLegacy(CallbackInfo ci) {
        if (Minecraft.m_91087_().f_91080_ instanceof ApricityContainerScreen) {
            ci.cancel();
        }
    }

    @Inject(method={"renderFloatingItem(Lnet/minecraft/client/gui/GuiGraphics;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"}, at={@At(value="HEAD")}, cancellable=true)
    private void apricityui$renderFloatingItem(GuiGraphics p_282567_, ItemStack p_281330_, int p_281772_, int p_281689_, String p_282568_, CallbackInfo ci) {
        AbstractContainerScreenMixin abstractContainerScreenMixin = this;
        if (abstractContainerScreenMixin instanceof ApricityContainerScreen) {
            ApricityContainerScreen screen = (ApricityContainerScreen)((Object)abstractContainerScreenMixin);
            screen.captureFloatingItem(p_281330_, p_281772_, p_281689_, p_282568_);
            ci.cancel();
        }
    }

    @Inject(method={"isHovering(Lnet/minecraft/world/inventory/Slot;DD)Z"}, at={@At(value="HEAD")}, cancellable=true)
    private void apricityui$injectSlotHovering(Slot p_97775_, double p_97776_, double p_97777_, CallbackInfoReturnable<Boolean> cir) {
        AbstractContainerScreenMixin abstractContainerScreenMixin = this;
        if (!(abstractContainerScreenMixin instanceof ApricityContainerScreen)) {
            return;
        }
        ApricityContainerScreen screen = (ApricityContainerScreen)((Object)abstractContainerScreenMixin);
        if (!screen.isSlotPointerInteractable(p_97775_)) {
            cir.setReturnValue((Object)false);
            return;
        }
        if (screen.isSlotBound(p_97775_)) {
            cir.setReturnValue((Object)screen.isBoundElementHovered(p_97775_, p_97776_, p_97777_));
            return;
        }
        int slotSize = 16;
        if (p_97775_ instanceof ApricityContainerMenu.UiSlot) {
            ApricityContainerMenu.UiSlot uiSlot = (ApricityContainerMenu.UiSlot)p_97775_;
            slotSize = Math.max(1, uiSlot.getUiSlotSize());
        }
        double localX = p_97776_ - (double)screen.getGuiLeft();
        double localY = p_97777_ - (double)screen.getGuiTop();
        cir.setReturnValue((Object)(localX >= (double)(p_97775_.f_40220_ - 1) && localX < (double)(p_97775_.f_40220_ + slotSize + 1) && localY >= (double)(p_97775_.f_40221_ - 1) && localY < (double)(p_97775_.f_40221_ + slotSize + 1) ? 1 : 0));
    }
}

