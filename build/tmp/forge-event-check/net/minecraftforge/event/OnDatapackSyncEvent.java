/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraft.server.players.PlayerList
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.event;

import java.util.List;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.Nullable;

public class OnDatapackSyncEvent
extends Event {
    private final PlayerList playerList;
    @Nullable
    private final ServerPlayer player;

    public OnDatapackSyncEvent(PlayerList playerList, @Nullable ServerPlayer player) {
        this.playerList = playerList;
        this.player = player;
    }

    public PlayerList getPlayerList() {
        return this.playerList;
    }

    @Nullable
    public ServerPlayer getPlayer() {
        return this.player;
    }

    public List<ServerPlayer> getPlayers() {
        return this.player == null ? this.playerList.m_11314_() : List.of(this.player);
    }
}

