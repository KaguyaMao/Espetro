/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.monster.EnderMan
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class EnderManAngerEvent
extends LivingEvent {
    private final Player player;

    public EnderManAngerEvent(EnderMan enderman, Player player) {
        super((LivingEntity)enderman);
        this.player = player;
    }

    public Player getPlayer() {
        return this.player;
    }

    public EnderMan getEntity() {
        return (EnderMan)super.getEntity();
    }
}

