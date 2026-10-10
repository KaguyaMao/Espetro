/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.advancements.Advancement
 *  net.minecraft.commands.CommandSourceStack
 *  net.minecraft.resources.ResourceLocation
 *  net.minecraft.world.item.crafting.RecipeManager
 *  net.minecraft.world.level.Level
 *  net.minecraft.world.scores.Scoreboard
 */
package net.minecraftforge.common.extensions;

import net.minecraft.advancements.Advancement;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeManager;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.Scoreboard;

public interface IForgeCommandSourceStack {
    private CommandSourceStack self() {
        return (CommandSourceStack)this;
    }

    default public Scoreboard getScoreboard() {
        return this.self().m_81377_().m_129896_();
    }

    default public Advancement getAdvancement(ResourceLocation id) {
        return this.self().m_81377_().m_129889_().m_136041_(id);
    }

    default public RecipeManager getRecipeManager() {
        return this.self().m_81377_().m_129894_();
    }

    default public Level getUnsidedLevel() {
        return this.self().m_81372_();
    }
}

