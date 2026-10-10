/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.screen;

import com.sighs.apricityui.dom.SlotContentRules;
import com.sighs.apricityui.element.Container;
import com.sighs.apricityui.element.Item;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.screen.ApricityContainerMenu;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public final class SlotDataBinder {
    private final ApricityContainerMenu menu;
    private final LinkedHashMap<Integer, SlotBinding> bindingsByGlobalIndex = new LinkedHashMap();
    private final ArrayList<com.sighs.apricityui.element.Slot> displaySlots = new ArrayList();
    private int lastBindSlotCount = -1;
    private long lastBindGeneration = -1L;
    private double viewportScaleX = 1.0;
    private double viewportScaleY = 1.0;
    private DisplayStateResolver displayStateResolver = slot -> new SlotItemState(slot.m_7993_(), null, false);

    public SlotDataBinder(ApricityContainerMenu menu) {
        this.menu = Objects.requireNonNull(menu);
    }

    public void setDisplayStateResolver(DisplayStateResolver resolver) {
        this.displayStateResolver = resolver == null ? slot -> new SlotItemState(slot.m_7993_(), null, false) : resolver;
    }

    public void bindSlotsFromDocument(Document document) {
        this.clear();
        if (document == null) {
            return;
        }
        boolean uiOnly = this.menu.getLayout().isUiOnly();
        for (Element element : document.getElements()) {
            int localIndex;
            if (!(element instanceof com.sighs.apricityui.element.Slot)) continue;
            com.sighs.apricityui.element.Slot slotElement = (com.sighs.apricityui.element.Slot)element;
            if (uiOnly) {
                this.displaySlots.add(slotElement);
                continue;
            }
            Container container = slotElement.findAncestor(Container.class);
            if (container == null) {
                this.displaySlots.add(slotElement);
                continue;
            }
            Item itemElement = SlotDataBinder.directItem(slotElement);
            if (itemElement == null) {
                this.displaySlots.add(slotElement);
                continue;
            }
            String containerId = container.getAttribute("id");
            if (containerId == null || containerId.isBlank()) {
                containerId = this.resolveImplicitContainerId(document, container);
            }
            if ((localIndex = slotElement.getSlotIndex()) < 0) {
                this.displaySlots.add(slotElement);
                continue;
            }
            Integer globalIndex = this.menu.resolveGlobalSlotIndex(containerId, localIndex);
            if (globalIndex == null || globalIndex < 0 || globalIndex >= this.menu.f_38839_.size()) {
                this.displaySlots.add(slotElement);
                continue;
            }
            SlotBinding binding = new SlotBinding(slotElement, itemElement, globalIndex, localIndex);
            this.bindingsByGlobalIndex.put(globalIndex, binding);
            slotElement.bindToMenuSlot(slotElement.isExplicitlyDisabled());
        }
        this.syncBoundSlotStates();
        this.lastBindSlotCount = SlotDataBinder.countSlotElements(document);
        this.lastBindGeneration = document.getRefreshGeneration();
    }

    public void syncAllSlotPositions(Document document, int leftPos, int topPos, boolean force) {
        if (document != null) {
            this.viewportScaleX = document.getViewportScaleX();
            this.viewportScaleY = document.getViewportScaleY();
        }
        for (SlotBinding binding : this.bindingsByGlobalIndex.values()) {
            if (binding.globalIndex() < 0 || binding.globalIndex() >= this.menu.f_38839_.size()) continue;
            Slot menuSlot = (Slot)this.menu.f_38839_.get(binding.globalIndex());
            com.sighs.apricityui.element.Slot slotElement = binding.slotElement();
            Position position = Position.of(slotElement);
            int elementX = (int)Math.round(position.x * this.viewportScaleX) - leftPos;
            int elementY = (int)Math.round(position.y * this.viewportScaleY) - topPos;
            if (force || menuSlot.f_40220_ != elementX || menuSlot.f_40221_ != elementY) {
                menuSlot.f_40220_ = elementX;
                menuSlot.f_40221_ = elementY;
            }
            if (!(menuSlot instanceof ApricityContainerMenu.UiSlot)) continue;
            ApricityContainerMenu.UiSlot uiSlot = (ApricityContainerMenu.UiSlot)menuSlot;
            uiSlot.setUiDisabled(slotElement.isExplicitlyDisabled());
            uiSlot.setUiHidden(!slotElement.shouldRenderItem());
            uiSlot.setUiSlotSize(this.scaleSlotSize(slotElement.resolveSlotSizeHint(16)));
        }
    }

    public boolean shouldRebindSlotsFromDom(Document document) {
        if (document == null) {
            return false;
        }
        if (document.getRefreshGeneration() != this.lastBindGeneration) {
            return true;
        }
        return SlotDataBinder.countSlotElements(document) != this.lastBindSlotCount;
    }

    public int findSlotIndexAt(double mouseX, double mouseY, int leftPos, int topPos) {
        Position documentMouse = this.documentPositionAt(mouseX, mouseY);
        if (documentMouse == null) {
            return -1;
        }
        ArrayList<SlotBinding> bindings = new ArrayList<SlotBinding>(this.bindingsByGlobalIndex.values());
        for (int index = bindings.size() - 1; index >= 0; --index) {
            SlotBinding binding = bindings.get(index);
            com.sighs.apricityui.element.Slot slotElement = binding.slotElement();
            if (!slotElement.canOperateBoundMenuSlot() || !slotElement.containsSlotPoint(documentMouse.x, documentMouse.y)) continue;
            return binding.globalIndex();
        }
        return -1;
    }

    public boolean isSlotPointerInteractable(Slot slot) {
        if (slot == null) {
            return false;
        }
        int index = this.menu.f_38839_.indexOf((Object)slot);
        if (index < 0) {
            return false;
        }
        SlotBinding binding = this.bindingsByGlobalIndex.get(index);
        if (binding == null) {
            return true;
        }
        return binding.slotElement().canOperateBoundMenuSlot();
    }

    public boolean isSlotBound(Slot slot) {
        return slot != null && this.bindingsByGlobalIndex.containsKey(this.menu.f_38839_.indexOf((Object)slot));
    }

    public boolean isBoundElementHovered(Slot slot, double mouseX, double mouseY) {
        if (slot == null) {
            return false;
        }
        SlotBinding binding = this.bindingsByGlobalIndex.get(this.menu.f_38839_.indexOf((Object)slot));
        if (binding == null || !binding.slotElement().canOperateBoundMenuSlot()) {
            return false;
        }
        Position documentMouse = this.documentPositionAt(mouseX, mouseY);
        return documentMouse != null && binding.slotElement().containsSlotPoint(documentMouse.x, documentMouse.y);
    }

    public SlotVisual resolveSlotVisual(Slot slot) {
        if (slot == null) {
            return SlotVisual.DEFAULT;
        }
        int index = this.menu.f_38839_.indexOf((Object)slot);
        if (index < 0) {
            return SlotVisual.DEFAULT;
        }
        SlotBinding binding = this.bindingsByGlobalIndex.get(index);
        if (binding == null) {
            return SlotVisual.DEFAULT;
        }
        com.sighs.apricityui.element.Slot slotElement = binding.slotElement();
        return new SlotVisual(!slotElement.shouldRenderItem(), slotElement.isDisabled(), slotElement.shouldRenderItem(), this.scaleSlotSize(slotElement.resolveSlotSizeHint(16)), (float)Math.max(0.01, (double)slotElement.resolveIconScale(1.0f) * this.viewportScaleX), slotElement.resolveZIndex(0));
    }

    public List<com.sighs.apricityui.element.Slot> getDisplaySlots() {
        return Collections.unmodifiableList(this.displaySlots);
    }

    public com.sighs.apricityui.element.Slot getBoundElement(Slot slot) {
        if (slot == null) {
            return null;
        }
        SlotBinding binding = this.bindingsByGlobalIndex.get(this.menu.f_38839_.indexOf((Object)slot));
        return binding == null ? null : binding.slotElement();
    }

    public Item getBoundItem(Slot slot) {
        if (slot == null) {
            return null;
        }
        SlotBinding binding = this.bindingsByGlobalIndex.get(this.menu.f_38839_.indexOf((Object)slot));
        return binding == null ? null : binding.itemElement();
    }

    public void clear() {
        for (SlotBinding binding : this.bindingsByGlobalIndex.values()) {
            binding.slotElement().clearMenuSlotBinding();
            binding.itemElement().clearDrivenState(Item.Source.MENU);
        }
        this.bindingsByGlobalIndex.clear();
        this.displaySlots.clear();
    }

    public void syncBoundSlotStates() {
        for (SlotBinding binding : this.bindingsByGlobalIndex.values()) {
            if (binding.globalIndex() < 0 || binding.globalIndex() >= this.menu.f_38839_.size()) continue;
            Slot menuSlot = (Slot)this.menu.f_38839_.get(binding.globalIndex());
            SlotItemState state = this.resolveDisplayState(menuSlot);
            boolean hidden = !menuSlot.m_6659_();
            boolean disabled = binding.slotElement().isExplicitlyDisabled();
            if (menuSlot instanceof ApricityContainerMenu.UiSlot) {
                ApricityContainerMenu.UiSlot uiSlot = (ApricityContainerMenu.UiSlot)menuSlot;
                hidden |= uiSlot.isUiHidden();
                disabled |= uiSlot.isUiDisabled();
            }
            binding.slotElement().updateBoundMenuState(disabled, hidden, state.ghost());
            binding.itemElement().setDrivenState(state.stack(), state.overlayText(), hidden, disabled, Item.Source.MENU);
        }
    }

    public void syncBoundSlotHoverStates(double mouseX, double mouseY) {
        Position documentMouse = this.documentPositionAt(mouseX, mouseY);
        for (SlotBinding binding : this.bindingsByGlobalIndex.values()) {
            com.sighs.apricityui.element.Slot slotElement = binding.slotElement();
            boolean hovered = documentMouse != null && slotElement.canOperateBoundMenuSlot() && slotElement.containsSlotPoint(documentMouse.x, documentMouse.y);
            slotElement.setHover(hovered);
        }
    }

    private Position documentPositionAt(double mouseX, double mouseY) {
        if (this.bindingsByGlobalIndex.isEmpty()) {
            return null;
        }
        SlotBinding first = this.bindingsByGlobalIndex.values().iterator().next();
        Document document = first.slotElement().document;
        if (document == null) {
            return null;
        }
        return document.screenToDocumentPosition(new Position(mouseX, mouseY));
    }

    private int scaleSlotSize(int logicalSize) {
        return Math.max(1, (int)Math.round((double)Math.max(1, logicalSize) * this.viewportScaleX));
    }

    private static Item directItem(com.sighs.apricityui.element.Slot slot) {
        Item item;
        Element element = SlotContentRules.getSlotContent(slot);
        return element instanceof Item ? (item = (Item)element) : null;
    }

    private static int countSlotElements(Document document) {
        if (document == null) {
            return 0;
        }
        int count = 0;
        for (Element element : document.getElements()) {
            if (!(element instanceof com.sighs.apricityui.element.Slot)) continue;
            ++count;
        }
        return count;
    }

    private String resolveImplicitContainerId(Document document, Container container) {
        int index = 0;
        for (Element element : document.getElements()) {
            if (!(element instanceof Container)) continue;
            Container candidate = (Container)element;
            if (candidate == container) {
                return "c" + index;
            }
            ++index;
        }
        return "c0";
    }

    private SlotItemState resolveDisplayState(Slot menuSlot) {
        SlotItemState state = this.displayStateResolver.resolve(menuSlot);
        return state == null ? new SlotItemState(menuSlot.m_7993_(), null, false) : state;
    }

    @FunctionalInterface
    public static interface DisplayStateResolver {
        public SlotItemState resolve(Slot var1);
    }

    private record SlotBinding(com.sighs.apricityui.element.Slot slotElement, Item itemElement, int globalIndex, int localIndex) {
    }

    public record SlotVisual(boolean hidden, boolean disabled, boolean renderItem, int slotSize, float iconScale, int zIndex) {
        public static final SlotVisual DEFAULT = new SlotVisual(false, false, true, 16, 1.0f, 0);
    }

    public record SlotItemState(ItemStack stack, String overlayText, boolean ghost) {
        public SlotItemState(ItemStack stack, String overlayText, boolean ghost) {
            this.stack = stack = stack == null ? ItemStack.f_41583_ : stack;
            this.overlayText = overlayText;
            this.ghost = ghost;
        }
    }
}

