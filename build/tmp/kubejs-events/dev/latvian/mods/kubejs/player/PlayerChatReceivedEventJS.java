/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;

public class PlayerChatReceivedEventJS
extends PlayerEventJS {
    private final ServerPlayer player;
    public final Component component;

    public PlayerChatReceivedEventJS(ServerPlayer player, Component component) {
        this.player = player;
        this.component = component;
    }

    public ServerPlayer getEntity() {
        return this.player;
    }

    public String getUsername() {
        return this.player.m_36316_().getName();
    }

    public String getMessage() {
        return this.component.getString();
    }

    public MutableComponent getComponent() {
        return this.component.m_6881_();
    }
}

