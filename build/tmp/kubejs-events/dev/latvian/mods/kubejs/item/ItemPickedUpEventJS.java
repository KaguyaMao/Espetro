/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.item.ItemStack
 */
package dev.latvian.mods.kubejs.item;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

@Info(value="Invoked when a player picks up an item. Cancelling (in `ItemEvents.canPickUp`) will prevent the item from being picked up.\n")
public class ItemPickedUpEventJS
extends PlayerEventJS {
    private final Player player;
    private final ItemEntity entity;
    private final ItemStack stack;

    public ItemPickedUpEventJS(Player player, ItemEntity entity, ItemStack stack) {
        this.player = player;
        this.entity = entity;
        this.stack = stack;
    }

    @Override
    @Info(value="The player that picked up the item.")
    public Player getEntity() {
        return this.player;
    }

    @Info(value="The item entity that was picked up.")
    public ItemEntity getItemEntity() {
        return this.entity;
    }

    @Info(value="The item that was picked up.")
    public ItemStack getItem() {
        return this.stack;
    }
}

