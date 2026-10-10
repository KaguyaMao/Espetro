/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.item.ItemEntity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event$HasResult
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;

@Cancelable
@Event.HasResult
public class EntityItemPickupEvent
extends PlayerEvent {
    private final ItemEntity item;

    public EntityItemPickupEvent(Player player, ItemEntity item) {
        super(player);
        this.item = item;
    }

    public ItemEntity getItem() {
        return this.item;
    }
}

