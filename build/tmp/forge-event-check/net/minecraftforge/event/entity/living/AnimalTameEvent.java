/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.LivingEntity
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class AnimalTameEvent
extends LivingEvent {
    private final Animal animal;
    private final Player tamer;

    public AnimalTameEvent(Animal animal, Player tamer) {
        super((LivingEntity)animal);
        this.animal = animal;
        this.tamer = tamer;
    }

    public Animal getAnimal() {
        return this.animal;
    }

    public Player getTamer() {
        return this.tamer;
    }
}

