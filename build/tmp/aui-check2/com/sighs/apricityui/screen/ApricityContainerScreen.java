/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.ChatFormatting
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
 *  net.minecraft.network.chat.Component
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.screen;

import com.sighs.apricityui.client.Client;
import com.sighs.apricityui.dev.resource.ResourcePreviewDialog;
import com.sighs.apricityui.dom.SlotContentRules;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.element.MinecraftElement;
import com.sighs.apricityui.event.Event;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.DocumentLayerOrder;
import com.sighs.apricityui.render.FrameTimingHud;
import com.sighs.apricityui.render.Mask;
import com.sighs.apricityui.render.RenderNode;
import com.sighs.apricityui.screen.ApricityContainerMenu;
import com.sighs.apricityui.screen.AuiLinkedScreen;
import com.sighs.apricityui.screen.SlotDataBinder;
import com.sighs.apricityui.style.Cursor;
import com.sighs.apricityui.style.Interaction;
import com.sighs.apricityui.viewport.ApricityViewport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.Nonnull;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ApricityContainerScreen
extends AbstractContainerScreen<ApricityContainerMenu>
implements AuiLinkedScreen {
    private Document linkedDocument;
    private SlotDataBinder slotBinder;
    private final List<RenderNode.ItemNode> floatingItemNodes = new ArrayList<RenderNode.ItemNode>();

    public ApricityContainerScreen(ApricityContainerMenu menu, Inventory inventory, Component title) {
        super((AbstractContainerMenu)menu, inventory, title);
    }

    @Override
    public Document getLinkedDocument() {
        return this.linkedDocument;
    }

    public int getGuiLeft() {
        return super.getGuiLeft();
    }

    public int getGuiTop() {
        return super.getGuiTop();
    }

    public int findSlotIndexAt(double mouseX, double mouseY) {
        if (this.slotBinder == null) {
            return -1;
        }
        return this.slotBinder.findSlotIndexAt(mouseX, mouseY, this.f_97735_, this.f_97736_);
    }

    public boolean isSlotPointerInteractable(Slot slot) {
        if (this.slotBinder == null) {
            return false;
        }
        return this.slotBinder.isSlotPointerInteractable(slot);
    }

    public boolean isSlotBound(Slot slot) {
        return this.slotBinder != null && this.slotBinder.isSlotBound(slot);
    }

    public boolean isBoundElementHovered(Slot slot, double mouseX, double mouseY) {
        return this.slotBinder != null && this.slotBinder.isBoundElementHovered(slot, mouseX, mouseY);
    }

    public boolean pruneInvalidQuickCraftSlot(Slot slot) {
        if (!this.f_97738_ || this.f_97737_ == null || this.f_97737_.size() <= 1 || !this.f_97737_.contains(slot)) {
            return false;
        }
        ItemStack carried = ((ApricityContainerMenu)this.f_97732_).m_142621_();
        if (carried.m_41619_() || AbstractContainerMenu.m_38899_((Slot)slot, (ItemStack)carried, (boolean)true) && ((ApricityContainerMenu)this.f_97732_).m_5622_(slot)) {
            return false;
        }
        this.f_97737_.remove(slot);
        return true;
    }

    public void captureFloatingItem(ItemStack stack, int relativeX, int relativeY, String overlayText) {
        if (this.linkedDocument == null || stack == null) {
            return;
        }
        if (stack.m_41619_() && (overlayText == null || overlayText.isBlank())) {
            return;
        }
        int screenX = relativeX + this.f_97735_;
        int screenY = relativeY + this.f_97736_;
        ItemStack snapshot = stack.m_41777_();
        Position position = this.linkedDocument.screenToDocumentPosition(new Position(screenX, screenY));
        int decorationScreenOffset = this.f_97711_.m_41619_() ? 0 : -8;
        Position decorationPosition = this.linkedDocument.screenToDocumentPosition(new Position(screenX, screenY + decorationScreenOffset));
        this.floatingItemNodes.add(RenderNode.ItemNode.positioned(() -> snapshot, position.x, position.y, 1.0, 232, true, overlayText, decorationPosition.y - position.y, false));
    }

    protected void m_7856_() {
        this.f_97726_ = this.f_96543_;
        this.f_97727_ = this.f_96544_;
        super.m_7856_();
        if (this.linkedDocument != null) {
            this.linkedDocument.remove();
            this.linkedDocument = null;
        }
        if (this.slotBinder != null) {
            this.slotBinder.clear();
            this.slotBinder = null;
        }
        this.linkedDocument = Document.create(((ApricityContainerMenu)this.f_97732_).getTemplatePath());
        if (this.linkedDocument == null) {
            return;
        }
        this.linkedDocument.applyViewport(false);
        this.slotBinder = new SlotDataBinder((ApricityContainerMenu)this.f_97732_);
        this.slotBinder.setDisplayStateResolver(this::resolveMenuSlotDisplayState);
        this.slotBinder.bindSlotsFromDocument(this.linkedDocument);
        this.slotBinder.syncAllSlotPositions(this.linkedDocument, this.f_97735_, this.f_97736_, true);
    }

    public void m_6574_(@Nonnull Minecraft minecraft, int width, int height) {
        super.m_6574_(minecraft, width, height);
        if (this.linkedDocument != null) {
            this.linkedDocument.applyViewport(true);
        }
    }

    protected void m_7286_(@Nonnull GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
    }

    private void drawLinkedDocument(GuiGraphics guiGraphics) {
        if (this.linkedDocument == null) {
            return;
        }
        ApricityViewport viewport = this.linkedDocument.getViewport();
        guiGraphics.m_280168_().m_85836_();
        Mask.pushScissorScale(viewport.scissorScale());
        try {
            guiGraphics.m_280168_().m_85841_(viewport.renderScale(), viewport.renderScale(), 1.0f);
            Base.drawScreenDocument(guiGraphics.m_280168_(), this.linkedDocument, this.floatingItemNodes);
        }
        finally {
            Mask.popScissorScale();
            guiGraphics.m_280168_().m_85849_();
        }
        Minecraft.m_91087_().m_91269_().m_110104_().m_109911_();
    }

    protected void m_280003_(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    protected void m_280072_(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY) {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void m_88315_(@Nonnull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.floatingItemNodes.clear();
        FrameTimingHud.beginFrame();
        try {
            if (this.linkedDocument != null && this.slotBinder != null) {
                if (this.slotBinder.shouldRebindSlotsFromDom(this.linkedDocument)) {
                    this.slotBinder.bindSlotsFromDocument(this.linkedDocument);
                    this.slotBinder.syncAllSlotPositions(this.linkedDocument, this.f_97735_, this.f_97736_, true);
                } else {
                    this.slotBinder.syncAllSlotPositions(this.linkedDocument, this.f_97735_, this.f_97736_, false);
                }
                this.slotBinder.syncBoundSlotStates();
                this.slotBinder.syncBoundSlotHoverStates(mouseX, mouseY);
            }
            super.m_88315_(guiGraphics, mouseX, mouseY, partialTick);
            this.drawLinkedDocument(guiGraphics);
            ResourcePreviewDialog.draw(guiGraphics.m_280168_(), this.linkedDocument);
            this.drawSlotHoverTooltipByElement(guiGraphics, mouseX, mouseY);
            Client.drawPersistentScreenDocuments(guiGraphics, this.linkedDocument);
            guiGraphics.m_280262_();
            Cursor.drawPseudoCursor(guiGraphics.m_280168_());
            guiGraphics.m_280262_();
        }
        finally {
            this.floatingItemNodes.clear();
            FrameTimingHud.endFrame();
            Client.drawFrameTimingHud(guiGraphics);
        }
    }

    public boolean m_6050_(double mouseX, double mouseY, double delta) {
        if (ApricityContainerScreen.m_96637_() && this.handleViewportZoom(delta > 0.0)) {
            return true;
        }
        return super.m_6050_(mouseX, mouseY, delta);
    }

    public boolean m_7933_(int keyCode, int scanCode, int modifiers) {
        if (ApricityContainerScreen.isControlModifier(modifiers)) {
            if (keyCode == 61 || keyCode == 334) {
                return this.handleViewportZoom(true);
            }
            if (keyCode == 45 || keyCode == 333) {
                return this.handleViewportZoom(false);
            }
            if (keyCode == 48 || keyCode == 320) {
                return this.resetViewportZoom();
            }
        }
        return super.m_7933_(keyCode, scanCode, modifiers);
    }

    private SlotDataBinder.SlotItemState resolveMenuSlotDisplayState(Slot slot) {
        if (slot == null || !slot.m_6659_()) {
            return new SlotDataBinder.SlotItemState(ItemStack.f_41583_, null, false);
        }
        ItemStack renderStack = slot.m_7993_();
        if (slot == this.f_97706_ && !this.f_97711_.m_41619_()) {
            if (!this.f_97710_) {
                return new SlotDataBinder.SlotItemState(ItemStack.f_41583_, null, false);
            }
            if (!renderStack.m_41619_()) {
                renderStack = renderStack.m_255036_(renderStack.m_41613_() / 2);
            }
            return new SlotDataBinder.SlotItemState(renderStack, null, false);
        }
        ItemStack carried = ((ApricityContainerMenu)this.f_97732_).m_142621_();
        if (!this.f_97738_ || carried.m_41619_() || this.f_97737_ == null || !this.f_97737_.contains(slot)) {
            return new SlotDataBinder.SlotItemState(renderStack, null, false);
        }
        if (this.f_97737_.size() <= 1) {
            return new SlotDataBinder.SlotItemState(ItemStack.f_41583_, null, false);
        }
        if (!AbstractContainerMenu.m_38899_((Slot)slot, (ItemStack)carried, (boolean)true) || !((ApricityContainerMenu)this.f_97732_).m_5622_(slot)) {
            return new SlotDataBinder.SlotItemState(renderStack, null, false);
        }
        int baseCount = AbstractContainerMenu.m_278794_((Set)this.f_97737_, (int)this.f_97717_, (ItemStack)carried);
        int existingCount = renderStack.m_41619_() ? 0 : renderStack.m_41613_();
        int maxStackSize = Math.min(carried.m_41741_(), slot.m_5866_(carried));
        int placeCount = baseCount + existingCount;
        String overlayText = null;
        if (placeCount > maxStackSize) {
            placeCount = maxStackSize;
            overlayText = ChatFormatting.YELLOW + String.valueOf(maxStackSize);
        }
        return new SlotDataBinder.SlotItemState(carried.m_255036_(placeCount), overlayText, true);
    }

    private void drawSlotHoverTooltipByElement(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Element element;
        int index;
        if (this.linkedDocument == null || !((ApricityContainerMenu)this.f_97732_).m_142621_().m_41619_()) {
            return;
        }
        Position screenMouse = new Position(mouseX, mouseY);
        if (DocumentLayerOrder.hasPersistentScreenDocumentAt(Document.getAll(), this.linkedDocument, screenMouse)) {
            return;
        }
        Position documentMouse = this.linkedDocument.screenToDocumentPosition(screenMouse);
        ArrayList<Element> elements = this.linkedDocument.getElements();
        for (index = elements.size() - 1; index >= 0; --index) {
            ItemStack stack;
            com.sighs.apricityui.element.Slot slot;
            element = (Element)elements.get(index);
            if (!(element instanceof com.sighs.apricityui.element.Slot) || !Interaction.isDisplayed(slot = (com.sighs.apricityui.element.Slot)element) || !slot.isVisible || !slot.canShowItemTooltip() || !slot.containsSlotPoint(documentMouse.x, documentMouse.y)) continue;
            Item item = SlotContentRules.getDisplayItem(slot);
            ItemStack itemStack = stack = item == null ? ItemStack.f_41583_ : item.getTooltipStack();
            if (stack.m_41619_()) continue;
            item.renderTooltip(guiGraphics, mouseX, mouseY);
            return;
        }
        for (index = elements.size() - 1; index >= 0; --index) {
            ItemStack stack;
            element = (Element)elements.get(index);
            if (!(element instanceof MinecraftElement)) continue;
            MinecraftElement minecraftElement = (MinecraftElement)element;
            if (element instanceof com.sighs.apricityui.element.Slot || !minecraftElement.isHover || (stack = minecraftElement.getTooltipStack()).m_41619_()) continue;
            minecraftElement.renderTooltip(guiGraphics, mouseX, mouseY);
            return;
        }
        if (this.f_97734_ != null && this.f_97734_.m_6659_()) {
            ItemStack stack;
            Item boundItem;
            com.sighs.apricityui.element.Slot boundElement = this.slotBinder == null ? null : this.slotBinder.getBoundElement(this.f_97734_);
            Item item = boundItem = this.slotBinder == null ? null : this.slotBinder.getBoundItem(this.f_97734_);
            if (boundElement != null && boundElement.canShowItemTooltip() && boundItem != null && !(stack = boundItem.getTooltipStack()).m_41619_()) {
                boundItem.renderTooltip(guiGraphics, mouseX, mouseY);
                return;
            }
            if (boundElement == null && !(stack = this.f_97734_.m_7993_()).m_41619_()) {
                guiGraphics.m_280153_(this.f_96547_, stack, mouseX, mouseY);
            }
        }
    }

    public void m_7379_() {
        if (this.linkedDocument == null) {
            Size.clearViewportOverride();
            super.m_7379_();
            return;
        }
        if (this.linkedDocument.body != null) {
            Event.triggerSingle(new Event(this.linkedDocument.body, "unload", false));
        }
        this.linkedDocument.remove();
        Size.clearViewportOverride();
        Cursor.resetToDefault();
        super.m_7379_();
    }

    public void m_7861_() {
        if (this.linkedDocument != null) {
            this.linkedDocument.remove();
        }
        if (this.slotBinder != null) {
            this.slotBinder.clear();
        }
        Size.clearViewportOverride();
        super.m_7861_();
    }

    public boolean handleViewportZoom(boolean zoomIn) {
        if (this.linkedDocument == null || !this.linkedDocument.handleViewportZoom(zoomIn)) {
            return false;
        }
        if (this.slotBinder != null) {
            this.slotBinder.syncAllSlotPositions(this.linkedDocument, this.f_97735_, this.f_97736_, true);
        }
        return true;
    }

    public boolean resetViewportZoom() {
        if (this.linkedDocument == null || !this.linkedDocument.resetViewportZoom()) {
            return false;
        }
        if (this.slotBinder != null) {
            this.slotBinder.syncAllSlotPositions(this.linkedDocument, this.f_97735_, this.f_97736_, true);
        }
        return true;
    }

    private static boolean isControlModifier(int modifiers) {
        return (modifiers & 2) != 0;
    }
}

