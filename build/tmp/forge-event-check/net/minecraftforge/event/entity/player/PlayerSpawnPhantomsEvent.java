/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Event$HasResult
 *  net.minecraftforge.eventbus.api.Event$Result
 *  org.jetbrains.annotations.NotNull
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.NotNull;

@Event.HasResult
public class PlayerSpawnPhantomsEvent
extends PlayerEvent {
    private int phantomsToSpawn;

    public PlayerSpawnPhantomsEvent(Player player, int phantomsToSpawn) {
        super(player);
        this.phantomsToSpawn = phantomsToSpawn;
    }

    public int getPhantomsToSpawn() {
        return this.phantomsToSpawn;
    }

    public void setPhantomsToSpawn(int phantomsToSpawn) {
        this.phantomsToSpawn = phantomsToSpawn;
    }

    public void setResult(@NotNull Event.Result result) {
        super.setResult(result);
    }
}

