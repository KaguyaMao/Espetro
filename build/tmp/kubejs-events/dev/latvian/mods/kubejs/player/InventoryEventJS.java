/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;

@Info(value="Invoked when a player opens or closes a container.\n")
public class InventoryEventJS
extends PlayerEventJS {
    private final Player player;
    private final AbstractContainerMenu menu;

    public InventoryEventJS(Player player, AbstractContainerMenu menu) {
        this.player = player;
        this.menu = menu;
    }

    @Override
    @Info(value="Gets the player that opened or closed the container.")
    public Player getEntity() {
        return this.player;
    }

    @Info(value="Gets the container that was opened or closed.")
    public AbstractContainerMenu getInventoryContainer() {
        return this.menu;
    }
}

