/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.player.PlayerEventJS;
import net.minecraft.world.entity.player.Player;

public class SimplePlayerEventJS
extends PlayerEventJS {
    private final Player player;

    public SimplePlayerEventJS(Player p) {
        this.player = p;
    }

    @Override
    public Player getEntity() {
        return this.player;
    }
}

