/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.item.ItemEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class ItemTossEvent
extends ItemEvent {
    private final Player player;

    public ItemTossEvent(ItemEntity entityItem, Player player) {
        super(entityItem);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }
}

