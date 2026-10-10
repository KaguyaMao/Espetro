/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Lifecycle
 *  net.minecraft.world.level.LevelSettings
 *  net.minecraft.world.level.storage.LevelSummary
 */
package net.minecraftforge.common.extensions;

import com.mojang.serialization.Lifecycle;
import net.minecraft.world.level.LevelSettings;
import net.minecraft.world.level.storage.LevelSummary;

public interface IForgeLevelSummary {
    private LevelSummary self() {
        return (LevelSummary)this;
    }

    default public boolean isLifecycleExperimental() {
        LevelSettings settings = this.self().m_164913_();
        return settings != null && settings.getLifecycle().equals(Lifecycle.experimental());
    }
}

