/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.PlayerInfo
 *  net.minecraft.world.level.GameType
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 */
package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;

public class ClientPlayerChangeGameTypeEvent
extends Event {
    private final PlayerInfo info;
    private final GameType currentGameType;
    private final GameType newGameType;

    @ApiStatus.Internal
    public ClientPlayerChangeGameTypeEvent(PlayerInfo info, GameType currentGameType, GameType newGameType) {
        this.info = info;
        this.currentGameType = currentGameType;
        this.newGameType = newGameType;
    }

    public PlayerInfo getInfo() {
        return this.info;
    }

    public GameType getCurrentGameType() {
        return this.currentGameType;
    }

    public GameType getNewGameType() {
        return this.newGameType;
    }
}

