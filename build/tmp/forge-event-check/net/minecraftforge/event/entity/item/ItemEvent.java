/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.item.ItemEntity
 */
package net.minecraftforge.event.entity.item;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraftforge.event.entity.EntityEvent;

public class ItemEvent
extends EntityEvent {
    private final ItemEntity itemEntity;

    public ItemEvent(ItemEntity itemEntity) {
        super((Entity)itemEntity);
        this.itemEntity = itemEntity;
    }

    public ItemEntity getEntity() {
        return this.itemEntity;
    }
}

