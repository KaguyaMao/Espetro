/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  cpw.mods.modlauncher.api.LamdbaExceptionUtils
 *  net.minecraftforge.fml.IModStateTransition
 *  net.minecraftforge.fml.IModStateTransition$EventGenerator
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModLoadingStage
 *  net.minecraftforge.fml.ThreadSelector
 */
package net.minecraftforge.fml.core;

import cpw.mods.modlauncher.api.LamdbaExceptionUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraftforge.fml.IModStateTransition;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.ThreadSelector;
import net.minecraftforge.fml.event.lifecycle.ParallelDispatchEvent;

record ParallelTransition(ModLoadingStage stage, Class<? extends ParallelDispatchEvent> event) implements IModStateTransition
{
    public Supplier<Stream<IModStateTransition.EventGenerator<?>>> eventFunctionStream() {
        return () -> Stream.of(IModStateTransition.EventGenerator.fromFunction((Function)LamdbaExceptionUtils.rethrowFunction(mc -> this.event.getConstructor(ModContainer.class, ModLoadingStage.class).newInstance(mc, this.stage))));
    }

    public ThreadSelector threadSelector() {
        return ThreadSelector.PARALLEL;
    }

    public BiFunction<Executor, CompletableFuture<Void>, CompletableFuture<Void>> finalActivityGenerator() {
        return (e, prev) -> prev.thenApplyAsync(t -> {
            this.stage.getDeferredWorkQueue().runTasks();
            return t;
        }, (Executor)e);
    }

    public BiFunction<Executor, ? extends IModStateTransition.EventGenerator<?>, CompletableFuture<Void>> preDispatchHook() {
        return (t, f) -> CompletableFuture.completedFuture(null);
    }

    public BiFunction<Executor, ? extends IModStateTransition.EventGenerator<?>, CompletableFuture<Void>> postDispatchHook() {
        return (t, f) -> CompletableFuture.completedFuture(null);
    }
}

