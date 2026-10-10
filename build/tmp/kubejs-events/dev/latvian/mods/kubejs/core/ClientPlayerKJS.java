/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.rhino.util.RemapPrefixForJS
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.AbstractClientPlayer
 *  net.minecraft.client.player.LocalPlayer
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.world.entity.player.Player
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.core;

import dev.latvian.mods.kubejs.KubeJS;
import dev.latvian.mods.kubejs.core.PlayerKJS;
import dev.latvian.mods.kubejs.net.SendDataFromClientMessage;
import dev.latvian.mods.kubejs.player.PlayerStatsJS;
import dev.latvian.mods.kubejs.util.NotificationBuilder;
import dev.latvian.mods.rhino.util.RemapPrefixForJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;

@RemapPrefixForJS(value="kjs$")
public interface ClientPlayerKJS
extends PlayerKJS {
    default public AbstractClientPlayer kjs$self() {
        return (AbstractClientPlayer)this;
    }

    default public boolean isSelf() {
        return this.kjs$self() == KubeJS.PROXY.getClientPlayer();
    }

    @Override
    default public void kjs$sendData(String channel, @Nullable CompoundTag data) {
        if (!channel.isEmpty()) {
            new SendDataFromClientMessage(channel, data).sendToServer();
        }
    }

    @Override
    default public void kjs$paint(CompoundTag tag) {
        if (this.isSelf()) {
            KubeJS.PROXY.paint(tag);
        }
    }

    @Override
    default public PlayerStatsJS kjs$getStats() {
        if (!this.isSelf()) {
            throw new IllegalStateException("Can't access other client player stats!");
        }
        return new PlayerStatsJS((Player)this.kjs$self(), ((LocalPlayer)this.kjs$self()).m_108630_());
    }

    @Override
    default public boolean kjs$isMiningBlock() {
        return this.isSelf() && Minecraft.m_91087_().f_91072_.m_105296_();
    }

    @Override
    default public void kjs$notify(NotificationBuilder notification) {
        notification.show();
    }
}

