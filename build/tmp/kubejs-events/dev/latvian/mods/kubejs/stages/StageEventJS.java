/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.stages.StageChangeEvent;
import dev.latvian.mods.kubejs.stages.Stages;
import net.minecraft.world.entity.player.Player;

public class StageEventJS
extends PlayerEventJS {
    private final StageChangeEvent event;

    public StageEventJS(StageChangeEvent e) {
        this.event = e;
    }

    public Stages getPlayerStages() {
        return this.event.getPlayerStages();
    }

    @Override
    public Player getEntity() {
        return this.event.getPlayer();
    }

    public String getStage() {
        return this.event.getStage();
    }
}

