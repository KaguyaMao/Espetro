/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.inventory.AbstractContainerMenu
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class PlayerContainerEvent
extends PlayerEvent {
    private final AbstractContainerMenu container;

    public PlayerContainerEvent(Player player, AbstractContainerMenu container) {
        super(player);
        this.container = container;
    }

    public AbstractContainerMenu getContainer() {
        return this.container;
    }

    public static class Close
    extends PlayerContainerEvent {
        public Close(Player player, AbstractContainerMenu container) {
            super(player, container);
        }
    }

    public static class Open
    extends PlayerContainerEvent {
        public Open(Player player, AbstractContainerMenu container) {
            super(player, container);
        }
    }
}

