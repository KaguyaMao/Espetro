/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 *  net.minecraft.world.inventory.ClickType
 *  net.minecraft.world.inventory.MenuType
 *  net.minecraft.world.inventory.Slot
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.gui.chest;

import dev.latvian.mods.kubejs.gui.chest.ChestMenuContainerSlot;
import dev.latvian.mods.kubejs.gui.chest.ChestMenuData;
import dev.latvian.mods.kubejs.gui.chest.ChestMenuInventoryClickEvent;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import java.util.List;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class CustomChestMenu
extends AbstractContainerMenu {
    public static final MenuType[] TYPES = new MenuType[]{MenuType.f_39957_, MenuType.f_39958_, MenuType.f_39959_, MenuType.f_39960_, MenuType.f_39961_, MenuType.f_39962_};
    public ChestMenuData data;

    public CustomChestMenu(int containerId, ChestMenuData data) {
        super(TYPES[data.rows - 1], containerId);
        int x;
        int y;
        this.data = data;
        int k = (data.rows - 4) * 18;
        for (y = 0; y < data.rows; ++y) {
            for (x = 0; x < 9; ++x) {
                this.m_38897_(new ChestMenuContainerSlot(this, x + y * 9, 8 + x * 18, 18 + y * 18));
            }
        }
        if (data.playerSlots) {
            for (y = 0; y < 3; ++y) {
                for (x = 0; x < 9; ++x) {
                    this.m_38897_(new Slot(data.capturedInventory, x + y * 9 + 9, 8 + x * 18, 103 + y * 18 + k));
                }
            }
            for (x = 0; x < 9; ++x) {
                this.m_38897_(new Slot(data.capturedInventory, x, 8 + x * 18, 161 + k));
            }
        } else {
            for (y = 0; y < 3; ++y) {
                for (x = 0; x < 9; ++x) {
                    this.m_38897_(new ChestMenuContainerSlot(this, data.rows * 9 + x + y * 9, 8 + x * 18, 103 + y * 18 + k));
                }
            }
            for (x = 0; x < 9; ++x) {
                this.m_38897_(new ChestMenuContainerSlot(this, data.rows * 9 + 27 + x, 8 + x * 18, 161 + k));
            }
        }
    }

    public ItemStack m_7648_(Player player, int slot) {
        return ItemStack.f_41583_;
    }

    public void m_150399_(int slot, int button, ClickType clickType, Player player) {
        if (this.data.playerSlots && slot >= this.data.rows * 9) {
            if (this.data.inventoryClicked != null && slot >= 0 && slot < this.f_38839_.size()) {
                this.data.inventoryClicked.onClick(new ChestMenuInventoryClickEvent(this.m_38853_(slot), clickType, button));
            }
            return;
        }
        if (slot >= this.data.rows * 9) {
            super.m_150399_(slot, button, clickType, player);
        }
        try {
            this.data.handleClick(slot, clickType, button);
        }
        catch (Exception ex) {
            ConsoleJS.SERVER.error("Error handling chest gui click", ex);
        }
        this.m_182423_();
    }

    public boolean m_6875_(Player player) {
        return true;
    }

    public void m_6877_(Player player) {
        if (this.data.closed != null) {
            this.data.closed.run();
        }
        player.f_36095_.m_182423_();
    }

    public ItemStack m_142621_() {
        return this.data.mouseItem;
    }

    public void m_142503_(ItemStack stack) {
        this.data.mouseItem = stack;
    }

    public void m_182410_(int stateId, List<ItemStack> list, ItemStack carried) {
        super.m_182410_(stateId, list, carried);
        this.data.mouseItem = carried;
    }
}

