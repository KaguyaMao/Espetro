/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.level.Level
 */
package dev.latvian.mods.kubejs.level;

import dev.latvian.mods.kubejs.level.LevelEventJS;
import net.minecraft.world.level.Level;

public class SimpleLevelEventJS
extends LevelEventJS {
    private final Level level;

    public SimpleLevelEventJS(Level l) {
        this.level = l;
    }

    @Override
    public Level getLevel() {
        return this.level;
    }
}

