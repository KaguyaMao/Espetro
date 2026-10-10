/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.entity.player.Player
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.entity;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class EntityEventJS
extends LevelEventJS {
    public abstract Entity getEntity();

    @Nullable
    public Player getPlayer() {
        Player p;
        Entity entity = this.getEntity();
        return entity instanceof Player ? (p = (Player)entity) : null;
    }

    @Override
    public Level getLevel() {
        return this.getEntity().m_9236_();
    }
}

