/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.multiplayer.MultiPlayerGameMode
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.network.Connection
 *  net.minecraftforge.eventbus.api.Event
 *  org.jetbrains.annotations.ApiStatus$Internal
 *  org.jetbrains.annotations.Nullable
 */
package net.minecraftforge.client.event;

import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraftforge.eventbus.api.Event;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

public abstract class ClientPlayerNetworkEvent
extends Event {
    private final MultiPlayerGameMode multiPlayerGameMode;
    private final LocalPlayer player;
    private final Connection connection;

    @ApiStatus.Internal
    protected ClientPlayerNetworkEvent(MultiPlayerGameMode multiPlayerGameMode, LocalPlayer player, Connection connection) {
        this.multiPlayerGameMode = multiPlayerGameMode;
        this.player = player;
        this.connection = connection;
    }

    public MultiPlayerGameMode getMultiPlayerGameMode() {
        return this.multiPlayerGameMode;
    }

    public LocalPlayer getPlayer() {
        return this.player;
    }

    public Connection getConnection() {
        return this.connection;
    }

    public static class Clone
    extends ClientPlayerNetworkEvent {
        private final LocalPlayer oldPlayer;

        @ApiStatus.Internal
        public Clone(MultiPlayerGameMode pc, LocalPlayer oldPlayer, LocalPlayer newPlayer, Connection networkManager) {
            super(pc, newPlayer, networkManager);
            this.oldPlayer = oldPlayer;
        }

        public LocalPlayer getOldPlayer() {
            return this.oldPlayer;
        }

        public LocalPlayer getNewPlayer() {
            return super.getPlayer();
        }

        @Override
        public LocalPlayer getPlayer() {
            return super.getPlayer();
        }
    }

    public static class LoggingOut
    extends ClientPlayerNetworkEvent {
        @ApiStatus.Internal
        public LoggingOut(@Nullable MultiPlayerGameMode controller, @Nullable LocalPlayer player, @Nullable Connection networkManager) {
            super(controller, player, networkManager);
        }

        @Override
        @Nullable
        public MultiPlayerGameMode getMultiPlayerGameMode() {
            return super.getMultiPlayerGameMode();
        }

        @Override
        @Nullable
        public LocalPlayer getPlayer() {
            return super.getPlayer();
        }

        @Override
        @Nullable
        public Connection getConnection() {
            return super.getConnection();
        }
    }

    public static class LoggingIn
    extends ClientPlayerNetworkEvent {
        @ApiStatus.Internal
        public LoggingIn(MultiPlayerGameMode controller, LocalPlayer player, Connection networkManager) {
            super(controller, player, networkManager);
        }
    }
}

