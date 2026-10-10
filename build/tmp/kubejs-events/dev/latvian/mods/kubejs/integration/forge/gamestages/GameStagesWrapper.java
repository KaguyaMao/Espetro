/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.darkhax.gamestages.GameStageHelper
 *  net.darkhax.gamestages.data.IStageData
 *  net.darkhax.gamestages.data.StageData
 *  net.minecraft.world.entity.player.Player
 */
package dev.latvian.mods.kubejs.integration.forge.gamestages;

import dev.latvian.mods.kubejs.integration.forge.gamestages.GameStageClientHelper;
import dev.latvian.mods.kubejs.stages.Stages;
import java.util.Collection;
import java.util.List;
import net.darkhax.gamestages.GameStageHelper;
import net.darkhax.gamestages.data.IStageData;
import net.darkhax.gamestages.data.StageData;
import net.minecraft.world.entity.player.Player;

public class GameStagesWrapper
extends Stages {
    public GameStagesWrapper(Player p) {
        super(p);
    }

    @Override
    public boolean addNoUpdate(String stage) {
        IStageData stageData = GameStageHelper.getPlayerData((Player)this.player);
        if (stageData != null && !stageData.hasStage(stage)) {
            stageData.addStage(stage);
            return true;
        }
        return false;
    }

    @Override
    public boolean removeNoUpdate(String stage) {
        IStageData stageData = GameStageHelper.getPlayerData((Player)this.player);
        if (stageData != null && stageData.hasStage(stage)) {
            stageData.removeStage(stage);
            return true;
        }
        return false;
    }

    @Override
    public Collection<String> getAll() {
        IStageData stageData = GameStageHelper.getPlayerData((Player)this.player);
        return stageData != null ? stageData.getStages() : List.of();
    }

    @Override
    public void replace(Collection<String> stages) {
        StageData stageData = new StageData();
        for (String s : stages) {
            stageData.addStage(s);
        }
        this.setClientData((IStageData)stageData);
    }

    private void setClientData(IStageData stageData) {
        GameStageClientHelper.setClientData(stageData);
    }
}

