/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.player.Input
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.player.Input;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import org.jetbrains.annotations.ApiStatus;

public class MovementInputUpdateEvent
extends PlayerEvent {
    private final Input input;

    @ApiStatus.Internal
    public MovementInputUpdateEvent(Player player, Input input) {
        super(player);
        this.input = input;
    }

    public Input getInput() {
        return this.input;
    }
}

