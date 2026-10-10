/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.server.level.ServerPlayer
 */
package dev.latvian.mods.kubejs.player;

import dev.latvian.mods.kubejs.player.AdvancementJS;
import dev.latvian.mods.kubejs.player.PlayerEventJS;
import dev.latvian.mods.kubejs.typings.Info;
import net.minecraft.advancements.Advancement;
import net.minecraft.server.level.ServerPlayer;

@Info(value="Invoked when a player gets an advancement.\n")
public class PlayerAdvancementEventJS
extends PlayerEventJS {
    private final ServerPlayer player;
    private final Advancement advancement;

    public PlayerAdvancementEventJS(ServerPlayer player, Advancement advancement) {
        this.player = player;
        this.advancement = advancement;
    }

    @Info(value="Returns the player that got the advancement.")
    public ServerPlayer getEntity() {
        return this.player;
    }

    @Info(value="Returns the advancement that was obtained.")
    public AdvancementJS getAdvancement() {
        return new AdvancementJS(this.advancement);
    }
}

