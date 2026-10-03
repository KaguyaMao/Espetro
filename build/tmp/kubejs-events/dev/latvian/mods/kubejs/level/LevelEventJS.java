/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.world.level.Level
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.event.EventJS;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public abstract class LevelEventJS
extends EventJS {
    public abstract Level getLevel();

    @Nullable
    public MinecraftServer getServer() {
        return this.getLevel().m_7654_();
    }
}

