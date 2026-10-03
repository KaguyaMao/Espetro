/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class AttackEntityEvent
extends PlayerEvent {
    private final Entity target;

    public AttackEntityEvent(Player player, Entity target) {
        super(player);
        this.target = target;
    }

    public Entity getTarget() {
        return this.target;
    }
}

