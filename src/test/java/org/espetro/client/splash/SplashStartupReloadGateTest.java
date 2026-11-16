package org.espetro.client.splash;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ReloadInstance;
import net.minecraft.server.packs.resources.SimpleReloadInstance;
import net.minecraft.util.Unit;
import net.minecraft.util.profiling.ProfilerFiller;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SplashStartupReloadGateTest {
    @Test
    void realMinecraftReloadPreparesDuringVideoAndAppliesAfterSwap() throws Exception {
        SplashStartupReloadGate gate = new SplashStartupReloadGate();
        CompletableFuture<Void> prepared = new CompletableFuture<>();
        AtomicBoolean applied = new AtomicBoolean();
        PreparableReloadListener listener = new PreparableReloadListener() {
            public CompletableFuture<Void> reload(PreparationBarrier barrier, ResourceManager resources,
                    ProfilerFiller prepareProfiler, ProfilerFiller applyProfiler, Executor prepare, Executor apply) {
                return CompletableFuture.supplyAsync(() -> {
                    prepared.complete(null);
                    return "resource";
                }, prepare).thenCompose(barrier::wait).thenAcceptAsync(value -> applied.set(true), apply);
            }
        };
        ExecutorService background = Executors.newSingleThreadExecutor();
        try {
            ReloadInstance reload = SimpleReloadInstance.create(ResourceManager.Empty.INSTANCE,
                List.of(listener), background, Runnable::run,
                gate.hold(CompletableFuture.completedFuture(Unit.INSTANCE)), false);
            prepared.get(2, TimeUnit.SECONDS);
            assertFalse(applied.get());
            gate.requestRelease();
            assertFalse(applied.get());
            gate.releaseAfterSwap();
            reload.done().get(2, TimeUnit.SECONDS);
            assertTrue(applied.get());
        } finally {
            background.shutdownNow();
        }
    }

    @Test
    void preparedResourcesCannotApplyUntilLoadingPageWasSwapped() {
        SplashStartupReloadGate gate = new SplashStartupReloadGate();
        CompletableFuture<String> prepared = new CompletableFuture<>();
        AtomicBoolean applied = new AtomicBoolean();
        CompletableFuture<String> apply = gate.hold(prepared).thenApply(value -> {
            applied.set(true);
            return value;
        });
        prepared.complete("resource");
        assertFalse(applied.get());
        gate.releaseAfterSwap(); // normal swaps during playback do not open the gate
        assertFalse(applied.get());
        gate.requestRelease();
        assertFalse(applied.get());
        gate.releaseAfterSwap();
        assertTrue(applied.get());
        assertEquals("resource", apply.join());
    }

    @Test
    void releaseStillWaitsForOriginalInitialTaskAndLaterReloadsAreUnaffected() {
        SplashStartupReloadGate gate = new SplashStartupReloadGate();
        CompletableFuture<String> initial = new CompletableFuture<>();
        CompletableFuture<String> held = gate.hold(initial);
        gate.requestRelease();
        gate.releaseAfterSwap();
        assertFalse(held.isDone());
        initial.complete("ready");
        assertEquals("ready", held.join());
        CompletableFuture<String> laterReload = new CompletableFuture<>();
        assertSame(laterReload, gate.hold(laterReload));
    }
}
