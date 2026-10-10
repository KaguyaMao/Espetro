/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.world.entity.player.Player;

public class StageChangeEvent {
    private final Stages stages;
    private final String stage;

    StageChangeEvent(Stages p, String s) {
        this.stages = p;
        this.stage = s;
    }

    public Player getPlayer() {
        return this.stages.player;
    }

    public String getStage() {
        return this.stage;
    }

    public Stages getPlayerStages() {
        return this.stages;
    }
}

