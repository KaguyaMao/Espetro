/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package dev.latvian.mods.kubejs.stages;

import dev.latvian.mods.kubejs.stages.Stages;
import java.util.Collection;
import net.minecraft.world.entity.player.Player;

public class TagWrapperStages
extends Stages {
    public TagWrapperStages(Player player) {
        super(player);
    }

    @Override
    public boolean addNoUpdate(String stage) {
        return this.player.m_20049_(stage);
    }

    @Override
    public boolean removeNoUpdate(String stage) {
        return this.player.m_20137_(stage);
    }

    @Override
    public Collection<String> getAll() {
        return this.player.m_19880_();
    }
}

