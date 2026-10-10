/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.DeferredWorkQueue
 *  net.minecraftforge.fml.ModContainer
 *  net.minecraftforge.fml.ModLoadingStage
 */
package net.minecraftforge.fml.event.lifecycle;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModLoadingStage;
import net.minecraftforge.fml.event.lifecycle.ModLifecycleEvent;

public class ParallelDispatchEvent
extends ModLifecycleEvent {
    private final ModLoadingStage modLoadingStage;

    public ParallelDispatchEvent(ModContainer container, ModLoadingStage stage) {
        super(container);
        this.modLoadingStage = stage;
    }

    private Optional<DeferredWorkQueue> getQueue() {
        return DeferredWorkQueue.lookup(Optional.of(this.modLoadingStage));
    }

    public CompletableFuture<Void> enqueueWork(Runnable work) {
        return this.getQueue().map(q -> q.enqueueWork(this.getContainer(), work)).orElseThrow(() -> new RuntimeException("No work queue found!"));
    }

    public <T> CompletableFuture<T> enqueueWork(Supplier<T> work) {
        return this.getQueue().map(q -> q.enqueueWork(this.getContainer(), work)).orElseThrow(() -> new RuntimeException("No work queue found!"));
    }
}

