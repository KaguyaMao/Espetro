/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.darkhax.gamestages.data.GameStageSaveHandler
 *  net.darkhax.gamestages.data.IStageData
 */
package dev.latvian.mods.kubejs.integration.forge.gamestages;

import net.darkhax.gamestages.data.GameStageSaveHandler;
import net.darkhax.gamestages.data.IStageData;

public class GameStageClientHelper {
    public static void setClientData(IStageData stageData) {
        GameStageSaveHandler.setClientData((IStageData)stageData);
    }
}

