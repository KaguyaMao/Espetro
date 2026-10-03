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

@Info(value="Invoked when a player drops an item.\n")
public class ItemDroppedEventJS
extends PlayerEventJS {
    private final Player player;
    private final ItemEntity entity;

    public ItemDroppedEventJS(Player player, ItemEntity entity) {
        this.player = player;
        this.entity = entity;
    }

    @Override
    @Info(value="The player that dropped the item.")
    public Player getEntity() {
        return this.player;
    }

    @Info(value="The item entity that was spawned when dropping.")
    public ItemEntity getItemEntity() {
        return this.entity;
    }

    @Info(value="The item that was dropped.")
    public ItemStack getItem() {
        return this.entity.m_32055_();
    }
}

