/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.fml.DistExecutor
 *  net.minecraftforge.fml.IModLoadingState
 *  net.minecraftforge.fml.IModStateProvider
 *  net.minecraftforge.fml.IModStateTransition
 *  net.minecraftforge.fml.ModLoadingPhase
 *  net.minecraftforge.fml.ModLoadingStage
 *  net.minecraftforge.fml.ModLoadingState
 *  net.minecraftforge.fml.config.ConfigTracker
 *  net.minecraftforge.fml.config.ModConfig$Type
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package net.minecraftforge.fml.core;

import java.util.List;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.IModLoadingState;
import net.minecraftforge.fml.IModStateProvider;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModLoadingPhase;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ModLoadingState;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.core.ParallelTransition;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.loading.FMLPaths;

public class ModStateProvider
implements IModStateProvider {
    final ModLoadingState ERROR = ModLoadingState.empty((String)"ERROR", (String)"", (ModLoadingPhase)ModLoadingPhase.ERROR);
    private final ModLoadingState VALIDATE = ModLoadingState.empty((String)"VALIDATE", (String)"", (ModLoadingPhase)ModLoadingPhase.GATHER);
    final ModLoadingState CONSTRUCT = ModLoadingState.withTransition((String)"CONSTRUCT", (String)"VALIDATE", ml -> "Constructing %d mods".formatted(ml.size()), (ModLoadingPhase)ModLoadingPhase.GATHER, (IModStateTransition)new ParallelTransition(ModLoadingStage.CONSTRUCT, FMLConstructModEvent.class));
    private final ModLoadingState CONFIG_LOAD = ModLoadingState.withInline((String)"CONFIG_LOAD", (String)"", (ModLoadingPhase)ModLoadingPhase.LOAD, ml -> {
        DistExecutor.unsafeRunWhenOn((Dist)Dist.CLIENT, () -> () -> ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.CLIENT, FMLPaths.CONFIGDIR.get()));
        ConfigTracker.INSTANCE.loadConfigs(ModConfig.Type.COMMON, FMLPaths.CONFIGDIR.get());
    });
    private final ModLoadingState COMMON_SETUP = ModLoadingState.withTransition((String)"COMMON_SETUP", (String)"CONFIG_LOAD", (ModLoadingPhase)ModLoadingPhase.LOAD, (IModStateTransition)new ParallelTransition(ModLoadingStage.COMMON_SETUP, FMLCommonSetupEvent.class));
    private final ModLoadingState SIDED_SETUP = ModLoadingState.withTransition((String)"SIDED_SETUP", (String)"COMMON_SETUP", (ModLoadingPhase)ModLoadingPhase.LOAD, (IModStateTransition)new ParallelTransition(ModLoadingStage.SIDED_SETUP, (Class)DistExecutor.unsafeRunForDist(() -> () -> FMLClientSetupEvent.class, () -> () -> FMLDedicatedServerSetupEvent.class)));
    private final ModLoadingState ENQUEUE_IMC = ModLoadingState.withTransition((String)"ENQUEUE_IMC", (String)"", (ModLoadingPhase)ModLoadingPhase.COMPLETE, (IModStateTransition)new ParallelTransition(ModLoadingStage.ENQUEUE_IMC, InterModEnqueueEvent.class));
    private final ModLoadingState PROCESS_IMC = ModLoadingState.withTransition((String)"PROCESS_IMC", (String)"ENQUEUE_IMC", (ModLoadingPhase)ModLoadingPhase.COMPLETE, (IModStateTransition)new ParallelTransition(ModLoadingStage.PROCESS_IMC, InterModProcessEvent.class));
    private final ModLoadingState COMPLETE = ModLoadingState.withTransition((String)"COMPLETE", (String)"PROCESS_IMC", ml -> "completing load of %d mods".formatted(ml.size()), (ModLoadingPhase)ModLoadingPhase.COMPLETE, (IModStateTransition)new ParallelTransition(ModLoadingStage.COMPLETE, FMLLoadCompleteEvent.class));
    private final ModLoadingState DONE = ModLoadingState.empty((String)"DONE", (String)"", (ModLoadingPhase)ModLoadingPhase.DONE);

    public List<IModLoadingState> getAllStates() {
        return List.of(this.ERROR, this.VALIDATE, this.CONSTRUCT, this.CONFIG_LOAD, this.COMMON_SETUP, this.SIDED_SETUP, this.ENQUEUE_IMC, this.PROCESS_IMC, this.COMPLETE, this.DONE);
    }
}

