/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 *  net.minecraft.network.FriendlyByteBuf
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.Container
 *  net.minecraft.world.SimpleContainer
 *  net.minecraft.world.entity.player.Inventory
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package com.sighs.apricityui.screen;

import com.sighs.apricityui.container.PlayerInventorySlotOrder;
import com.sighs.apricityui.container.SlotLayout;
import com.sighs.apricityui.container.bind.ContainerBindType;
import com.sighs.apricityui.container.datasource.ContainerDataSource;
import com.sighs.apricityui.registry.ApricityMenus;
import java.lang.invoke.CallSite;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ApricityContainerMenu
extends AbstractContainerMenu {
    private final SlotLayout layout;
    private final Inventory playerInventory;
    private final ArrayList<ContainerDataSource> activeSources = new ArrayList();
    private final ServerPlayer owner;
    private int customSlotCount = 0;
    private int playerSlotStart = -1;
    private int playerSlotEnd = -1;

    public ApricityContainerMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData) {
        this(containerId, playerInventory, ApricityContainerMenu.readLayout(extraData), Map.of(), null);
    }

    public ApricityContainerMenu(int containerId, Inventory playerInventory, SlotLayout layout) {
        this(containerId, playerInventory, layout, Map.of(), null);
    }

    public ApricityContainerMenu(int containerId, Inventory playerInventory, SlotLayout layout, Map<String, ContainerDataSource> containerSources, ServerPlayer owner) {
        super((MenuType)ApricityMenus.APRICITY_CONTAINER.get(), containerId);
        this.playerInventory = playerInventory;
        this.layout = Objects.requireNonNull(layout, "SlotLayout \u4e0d\u80fd\u4e3a\u7a7a");
        this.owner = owner;
        this.initializeSlots(containerSources == null ? Map.of() : containerSources);
    }

    private static SlotLayout readLayout(FriendlyByteBuf extraData) {
        if (extraData == null) {
            throw new IllegalStateException("\u5bb9\u5668\u6253\u5f00\u5931\u8d25\uff1a\u670d\u52a1\u7aef\u672a\u63d0\u4f9b SlotLayout\uff08extraData \u4e3a\u7a7a\uff09");
        }
        return SlotLayout.read(extraData);
    }

    public static ApricityContainerMenu createClientOnly(Inventory playerInventory, String templatePath) {
        return new ApricityContainerMenu(-1, playerInventory, SlotLayout.createUiOnly(templatePath));
    }

    private void initializeSlots(Map<String, ContainerDataSource> containerSources) {
        this.activeSources.clear();
        this.customSlotCount = 0;
        this.playerSlotStart = -1;
        this.playerSlotEnd = -1;
        if (this.layout.isUiOnly()) {
            return;
        }
        LinkedHashSet<CallSite> initializedCustomPools = new LinkedHashSet<CallSite>();
        ArrayList<SlotLayout.ContainerEntry> sortedEntries = new ArrayList<SlotLayout.ContainerEntry>(this.layout.containers());
        sortedEntries.sort(Comparator.comparingInt(SlotLayout.ContainerEntry::baseIndex));
        for (SlotLayout.ContainerEntry entry : sortedEntries) {
            String customPoolKey;
            if (ContainerBindType.isPlayer(entry.bindType()) || entry.capacity() <= 0 || !initializedCustomPools.add((CallSite)((Object)(customPoolKey = entry.baseIndex() + ":" + entry.capacity())))) continue;
            ContainerDataSource source = containerSources.get(entry.id());
            int resolvedCapacity = entry.capacity();
            SimpleContainer fallback = source == null ? new SimpleContainer(Math.max(1, resolvedCapacity)) : null;
            for (int localIndex = 0; localIndex < resolvedCapacity; ++localIndex) {
                UiSlot slot = source == null ? new UiSlot((Container)fallback, localIndex, 0, 0) : source.createSlot(localIndex, 0, 0);
                this.m_38897_(slot);
            }
            if (source == null || this.activeSources.contains(source)) continue;
            this.activeSources.add(source);
        }
        this.customSlotCount = this.f_38839_.size();
        int playerPoolCapacity = this.resolvePlayerPoolCapacity(this.layout.containers());
        if (playerPoolCapacity > 0) {
            this.playerSlotStart = this.f_38839_.size();
            this.addPlayerInventorySlots(this.playerInventory, playerPoolCapacity);
            this.playerSlotEnd = this.f_38839_.size();
        }
    }

    private int resolvePlayerPoolCapacity(List<SlotLayout.ContainerEntry> entries) {
        int max = 0;
        for (SlotLayout.ContainerEntry entry : entries) {
            if (!ContainerBindType.isPlayer(entry.bindType())) continue;
            max = Math.max(max, entry.capacity());
        }
        return Math.min(36, Math.max(0, max));
    }

    private void addPlayerInventorySlots(Inventory playerInventory, int capacity) {
        int normalized = Math.max(0, Math.min(36, capacity));
        for (int menuRelativeIndex = 0; menuRelativeIndex < normalized; ++menuRelativeIndex) {
            int playerInventoryIndex = PlayerInventorySlotOrder.menuRelativeIndexToPlayerInventoryIndex(menuRelativeIndex, normalized);
            this.m_38897_(new UiSlot((Container)playerInventory, playerInventoryIndex, 0, 0));
        }
    }

    public SlotLayout getLayout() {
        return this.layout;
    }

    public String getTemplatePath() {
        return this.layout.templatePath();
    }

    public Inventory getPlayerInventory() {
        return this.playerInventory;
    }

    public boolean hasContainer(String containerId) {
        return this.layout.findContainer(containerId) != null;
    }

    public Integer resolveGlobalSlotIndex(String containerId, int localSlotIndex) {
        SlotLayout.ContainerEntry entry = this.layout.findContainer(containerId);
        if (entry == null) {
            return null;
        }
        if (ContainerBindType.isPlayer(entry.bindType())) {
            if (localSlotIndex < 0 || localSlotIndex >= entry.capacity()) {
                return null;
            }
            int playerPoolCapacity = this.playerSlotEnd - this.playerSlotStart;
            int menuRelativeIndex = PlayerInventorySlotOrder.playerInventoryIndexToMenuRelativeIndex(localSlotIndex, playerPoolCapacity);
            if (menuRelativeIndex < 0) {
                return null;
            }
            int resolved = this.playerSlotStart + menuRelativeIndex;
            return resolved >= 0 && resolved < this.f_38839_.size() ? Integer.valueOf(resolved) : null;
        }
        Integer resolved = entry.resolveGlobalSlotIndex(localSlotIndex);
        if (resolved == null) {
            return null;
        }
        if (resolved < 0 || resolved >= this.f_38839_.size()) {
            return null;
        }
        return resolved;
    }

    public List<ContainerSlotRef> getContainerSlotRefs(String containerId) {
        SlotLayout.ContainerEntry entry = this.layout.findContainer(containerId);
        if (entry == null || entry.capacity() <= 0) {
            return List.of();
        }
        ArrayList<ContainerSlotRef> refs = new ArrayList<ContainerSlotRef>(entry.capacity());
        for (int localIndex = 0; localIndex < entry.capacity(); ++localIndex) {
            Integer globalIndex = this.resolveGlobalSlotIndex(containerId, localIndex);
            if (globalIndex == null) continue;
            refs.add(new ContainerSlotRef(localIndex, globalIndex));
        }
        return List.copyOf(refs);
    }

    @Nonnull
    public ItemStack m_7648_(@Nonnull Player player, int slotIndex) {
        boolean moved;
        if (slotIndex < 0 || slotIndex >= this.f_38839_.size()) {
            return ItemStack.f_41583_;
        }
        Slot sourceSlot = (Slot)this.f_38839_.get(slotIndex);
        if (sourceSlot == null || !sourceSlot.m_6657_()) {
            return ItemStack.f_41583_;
        }
        ItemStack sourceStack = sourceSlot.m_7993_();
        ItemStack copied = sourceStack.m_41777_();
        SlotLayout.ContainerEntry primaryEntry = this.layout.findContainer(this.layout.primaryContainerId());
        int primaryStart = -1;
        int primaryEnd = -1;
        if (primaryEntry != null && !ContainerBindType.isPlayer(primaryEntry.bindType()) && primaryEntry.capacity() > 0) {
            primaryStart = primaryEntry.baseIndex();
            primaryEnd = primaryStart + primaryEntry.capacity();
        }
        if (this.isPlayerSlot(slotIndex)) {
            moved = primaryStart >= 0 && primaryEnd > primaryStart ? this.m_38903_(sourceStack, primaryStart, primaryEnd, false) : this.customSlotCount > 0 && this.m_38903_(sourceStack, 0, this.customSlotCount, false);
        } else if (primaryStart >= 0 && slotIndex >= primaryStart && slotIndex < primaryEnd) {
            moved = this.hasPlayerPool() && this.m_38903_(sourceStack, this.playerSlotStart, this.playerSlotEnd, true);
        } else {
            boolean bl = moved = this.hasPlayerPool() && this.m_38903_(sourceStack, this.playerSlotStart, this.playerSlotEnd, true);
        }
        if (!moved) {
            return ItemStack.f_41583_;
        }
        if (sourceStack.m_41619_()) {
            sourceSlot.m_5852_(ItemStack.f_41583_);
        } else {
            sourceSlot.m_6654_();
        }
        if (sourceStack.m_41613_() == copied.m_41613_()) {
            return ItemStack.f_41583_;
        }
        sourceSlot.m_142406_(player, sourceStack);
        return copied;
    }

    private boolean isPlayerSlot(int slotIndex) {
        return this.hasPlayerPool() && slotIndex >= this.playerSlotStart && slotIndex < this.playerSlotEnd;
    }

    private boolean hasPlayerPool() {
        return this.playerSlotStart >= 0 && this.playerSlotEnd > this.playerSlotStart;
    }

    public boolean m_6875_(@Nonnull Player player) {
        if (!(player instanceof ServerPlayer)) {
            return true;
        }
        ServerPlayer serverPlayer = (ServerPlayer)player;
        if (this.owner != null && this.owner != serverPlayer) {
            return false;
        }
        for (ContainerDataSource source : this.activeSources) {
            if (source.stillValid(serverPlayer)) continue;
            return false;
        }
        return true;
    }

    public void m_6877_(@Nonnull Player player) {
        super.m_6877_(player);
        if (player instanceof ServerPlayer) {
            ServerPlayer serverPlayer = (ServerPlayer)player;
            for (ContainerDataSource source : this.activeSources) {
                source.onClose(serverPlayer);
            }
        }
    }

    public static class UiSlot
    extends Slot {
        private boolean uiDisabled = false;
        private boolean uiHidden = false;
        private int uiSlotSize = 16;

        public UiSlot(Container container, int slot, int x, int y) {
            super(container, slot, x, y);
        }

        public boolean m_5857_(@Nonnull ItemStack stack) {
            if (this.uiDisabled) {
                return false;
            }
            return super.m_5857_(stack);
        }

        public boolean m_8010_(@Nonnull Player player) {
            if (this.uiDisabled) {
                return false;
            }
            return super.m_8010_(player);
        }

        public int getUiSlotSize() {
            return this.uiSlotSize;
        }

        public boolean isUiDisabled() {
            return this.uiDisabled;
        }

        public void setUiDisabled(boolean uiDisabled) {
            this.uiDisabled = uiDisabled;
        }

        public boolean isUiHidden() {
            return this.uiHidden;
        }

        public void setUiHidden(boolean uiHidden) {
            this.uiHidden = uiHidden;
        }

        public void setUiSlotSize(int uiSlotSize) {
            this.uiSlotSize = Math.max(1, uiSlotSize);
        }
    }

    public record ContainerSlotRef(int localSlotIndex, int globalSlotIndex) {
    }
}

