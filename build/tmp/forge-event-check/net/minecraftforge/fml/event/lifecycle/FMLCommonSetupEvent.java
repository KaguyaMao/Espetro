/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModLoadingStage
 */
package net.minecraftforge.fml.event.lifecycle;

import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

public class FMLCommonSetupEvent
extends ParallelDispatchEvent {
    public FMLCommonSetupEvent(ModContainer container, ModLoadingStage stage) {
        super(container, stage);
    }
}

