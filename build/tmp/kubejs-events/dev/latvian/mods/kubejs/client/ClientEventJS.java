/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.player.LocalPlayer
 */
package dev.latvian.mods.kubejs.client;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class ClientEventJS
extends PlayerEventJS {
    public LocalPlayer getEntity() {
        return Minecraft.m_91087_().f_91074_;
    }

    public LocalPlayer getPlayer() {
        return Minecraft.m_91087_().f_91074_;
    }
}

