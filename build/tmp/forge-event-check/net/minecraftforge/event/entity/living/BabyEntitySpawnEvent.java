/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.AgeableMob
 *  net.minecraft.world.entity.Mob
 *  net.minecraft.world.entity.animal.Animal
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event.entity.living;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

@Cancelable
public class BabyEntitySpawnEvent
extends Event {
    private final Mob parentA;
    private final Mob parentB;
    private final Player causedByPlayer;
    private AgeableMob child;

    public BabyEntitySpawnEvent(Mob parentA, Mob parentB, @Nullable AgeableMob proposedChild) {
        ServerPlayer causedByPlayer = null;
        if (parentA instanceof Animal) {
            causedByPlayer = ((Animal)parentA).m_27592_();
        }
        if (causedByPlayer == null && parentB instanceof Animal) {
            causedByPlayer = ((Animal)parentB).m_27592_();
        }
        this.parentA = parentA;
        this.parentB = parentB;
        this.causedByPlayer = causedByPlayer;
        this.child = proposedChild;
    }

    public Mob getParentA() {
        return this.parentA;
    }

    public Mob getParentB() {
        return this.parentB;
    }

    @Nullable
    public Player getCausedByPlayer() {
        return this.causedByPlayer;
    }

    @Nullable
    public AgeableMob getChild() {
        return this.child;
    }

    public void setChild(AgeableMob proposedChild) {
        this.child = proposedChild;
    }
}

