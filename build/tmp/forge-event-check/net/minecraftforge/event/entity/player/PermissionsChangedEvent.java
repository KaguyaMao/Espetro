/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.world.entity.player.Player
 *  net.minecraftforge.eventbus.api.Cancelable
 */
package net.minecraftforge.event.entity.player;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.Cancelable;

@Cancelable
public class PermissionsChangedEvent
extends PlayerEvent {
    private final int newLevel;
    private final int oldLevel;

    public PermissionsChangedEvent(ServerPlayer player, int newLevel, int oldLevel) {
        super((Player)player);
        this.oldLevel = oldLevel;
        this.newLevel = newLevel;
    }

    public int getNewLevel() {
        return this.newLevel;
    }

    public int getOldLevel() {
        return this.oldLevel;
    }
}

