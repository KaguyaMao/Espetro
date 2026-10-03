/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info(value="Invoked when a player's inventory changes.\n")
public class InventoryChangedEventJS
extends PlayerEventJS {
    private final Player player;
    private final ItemStack item;
    private final int slot;

    public InventoryChangedEventJS(Player p, ItemStack is, int s) {
        this.player = p;
        this.item = is;
        this.slot = s;
    }

    @Override
    @Info(value="Gets the player that changed their inventory.")
    public Player getEntity() {
        return this.player;
    }

    @Info(value="Gets the item that was changed.")
    public ItemStack getItem() {
        return this.item;
    }

    @Info(value="Gets the slot that was changed.")
    public int getSlot() {
        return this.slot;
    }
}

