/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

public class StageCreationEvent {
    private final Player player;
    private Stages stages;

    StageCreationEvent(Player p) {
        this.player = p;
    }

    public Player getPlayer() {
        return this.player;
    }

    public void setPlayerStages(Stages s) {
        this.stages = s;
    }

    @Nullable
    public Stages getPlayerStages() {
        return this.stages;
    }
}

