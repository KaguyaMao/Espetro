/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.Difficulty
 *  net.minecraftforge.eventbus.api.Event
 */
package net.minecraftforge.event;

import net.minecraft.world.Difficulty;
import net.minecraftforge.eventbus.api.Event;

public class DifficultyChangeEvent
extends Event {
    private final Difficulty difficulty;
    private final Difficulty oldDifficulty;

    public DifficultyChangeEvent(Difficulty difficulty, Difficulty oldDifficulty) {
        this.difficulty = difficulty;
        this.oldDifficulty = oldDifficulty;
    }

    public Difficulty getDifficulty() {
        return this.difficulty;
    }

    public Difficulty getOldDifficulty() {
        return this.oldDifficulty;
    }
}

