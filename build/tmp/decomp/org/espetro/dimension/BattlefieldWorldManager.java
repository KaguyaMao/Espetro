/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  net.minecraftforge.common.MinecraftForge
 *  net.minecraftforge.event.level.LevelEvent$Load
 *  net.minecraftforge.event.level.LevelEvent$Unload
 *  net.minecraftforge.eventbus.api.Event
 */
package org.espetro.dimension;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.CopyOption;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.TicketType;
import net.minecraft.server.level.progress.LoggerChunkProgressListener;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.border.BorderChangeListener;
import net.minecraft.world.level.border.WorldBorder;
import net.minecraft.world.level.chunk.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.storage.DerivedLevelData;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraft.world.level.storage.ServerLevelData;
import net.minecraft.world.level.storage.WorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.Event;
import org.espetro.Espetro;
import org.espetro.bastion.BastionManager;
import org.espetro.bastion.FortificationConfig;
import org.espetro.dimension.BattlefieldNamespaceReset;
import org.espetro.logistics.DeploySupplyStationPlacer;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.BattlefieldContext;
import org.espetro.mapconfig.ExternalConfigBootstrap;
import org.espetro.mapconfig.SpawnPointsSnapshot;
import org.espetro.mapconfig.VehSpawnSnapshot;
import org.espetro.vehicle.VehicleManager;

public final class BattlefieldWorldManager {
    private static final BattlefieldWorldManager INSTANCE = new BattlefieldWorldManager();
    private static final ExecutorService IO = Executors.newSingleThreadExecutor(runnable -> {
        Thread thread = new Thread(runnable, "Espetro-Battlefield-IO");
        thread.setDaemon(true);
        return thread;
    });
    private final AtomicBoolean busy = new AtomicBoolean(false);
    private final AtomicLong sessionGeneration = new AtomicLong();
    private final Set<ResourceLocation> availableDimensions = new LinkedHashSet<ResourceLocation>();
    private volatile ImportState state = ImportState.IDLE;
    private volatile String lastError = null;
    private volatile ActiveMapConfig lastLoaded = null;
    private volatile StartupPreparationResult startupPreparation = new StartupPreparationResult(StartupResetStatus.NOT_RUN, 0, List.of(), null);
    private final Object pendingCleanupLock = new Object();
    private boolean pendingCleanupRequested;
    private ActiveMapConfig pendingCleanupMap;
    private final List<Consumer<Result>> pendingCleanupCallbacks = new ArrayList<Consumer<Result>>();
    private static final int ACTIVATION_CHUNKS_STARTED_PER_TICK = 2;
    private static final int ACTIVATION_MAX_IN_FLIGHT = 8;
    private final ArrayDeque<ChunkPos> pendingActivationChunks = new ArrayDeque();
    private final Set<ChunkPos> activationChunks = new HashSet<ChunkPos>();
    private int activationChunksInFlight;
    private int activationChunksTotal;
    private String activationChunkFailure;
    private ServerLevel activationLevel;
    private ActiveMapConfig activationMap;
    private Consumer<Result> activationCallback;
    private long activationGeneration;
    private long activationSessionGeneration;

    private BattlefieldWorldManager() {
    }

    public static BattlefieldWorldManager getInstance() {
        return INSTANCE;
    }

    public ImportState getState() {
        return this.state;
    }

    public String getLastError() {
        return this.lastError;
    }

    public boolean isBusy() {
        return this.busy.get();
    }

    public StartupPreparationResult getStartupPreparation() {
        return this.startupPreparation;
    }

    public boolean isStartupReady() {
        return this.startupPreparation.status == StartupResetStatus.READY;
    }

    private boolean isCurrent(MinecraftServer server, long generation) {
        return this.sessionGeneration.get() == generation && (Espetro.getServer() == null || Espetro.getServer() == server);
    }

    public void onServerTick() {
        ServerLevel level = this.activationLevel;
        if (level == null) {
            return;
        }
        for (int started = 0; started < 2 && this.activationChunksInFlight < 8 && !this.pendingActivationChunks.isEmpty(); ++started) {
            ChunkPos chunk = this.pendingActivationChunks.poll();
            ++this.activationChunksInFlight;
            long generation = this.activationGeneration;
            level.m_7726_().m_8387_(TicketType.f_9447_, chunk, 1, chunk.m_45615_());
            level.m_7726_().m_8431_(chunk.f_45578_, chunk.f_45579_, ChunkStatus.f_62326_, true).whenComplete((loaded, error) -> level.m_7654_().execute(() -> {
                if (generation != this.activationGeneration || this.activationSessionGeneration != this.sessionGeneration.get()) {
                    return;
                }
                --this.activationChunksInFlight;
                if (error != null || loaded == null || loaded.left().isEmpty()) {
                    this.activationChunkFailure = "\u65e0\u6cd5\u9884\u8f7d\u533a\u5757 " + chunk + (String)(error == null ? "" : ": " + error.getMessage());
                }
                this.finishActivationPreparationIfReady();
            }));
        }
        this.finishActivationPreparationIfReady();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public StartupPreparationResult prepareAtStartup(MinecraftServer server) {
        StartupPreparationResult startupPreparationResult;
        long generation = this.sessionGeneration.incrementAndGet();
        this.busy.set(true);
        this.availableDimensions.clear();
        this.lastError = null;
        this.lastLoaded = null;
        this.state = ImportState.IDLE;
        this.startupPreparation = new StartupPreparationResult(StartupResetStatus.RESETTING, 0, List.of(), null);
        ArrayList<String> warnings = new ArrayList<String>();
        try {
            StartupPreparationResult startupPreparationResult2;
            Path worldRoot = server.f_129744_.m_78283_(LevelResource.f_78182_);
            Future<BattlefieldNamespaceReset.ResetResult> barrier = IO.submit(() -> BattlefieldNamespaceReset.reset(worldRoot, generation));
            BattlefieldNamespaceReset.ResetResult reset = barrier.get();
            warnings.addAll(reset.warnings());
            if (generation != this.sessionGeneration.get()) {
                StartupPreparationResult startupPreparationResult3 = this.failStartup("\u542f\u52a8 generation \u5df2\u5931\u6548", warnings);
                return startupPreparationResult3;
            }
            if (!reset.isolated()) {
                StartupPreparationResult startupPreparationResult4 = this.failStartup("\u6218\u573a\u542f\u52a8\u91cd\u7f6e\u5931\u8d25: " + reset.error(), warnings);
                return startupPreparationResult4;
            }
            ExternalConfigBootstrap.bootstrapIfNeeded();
            for (ActiveMapConfig map : ExternalConfigBootstrap.getUsableMaps()) {
                Path template = map.templateWorldDir.toAbsolutePath().normalize();
                if (Files.isDirectory(template, new LinkOption[0]) && Files.isDirectory(template.resolve("region"), new LinkOption[0])) {
                    this.availableDimensions.add(map.dimensionId);
                    Espetro.LOGGER.info("\u542f\u52a8\u65f6\u5df2\u9a8c\u8bc1\u6218\u573a\u5730\u56fe: {} -> {}", (Object)map.displayName, (Object)template);
                    continue;
                }
                this.lastError = "\u7f3a\u5c11 region \u76ee\u5f55: " + template;
                Espetro.LOGGER.error("\u5730\u56fe {} \u542f\u52a8\u9a8c\u8bc1\u5931\u8d25: {}", (Object)map.displayName, (Object)this.lastError);
            }
            FortificationConfig.loadServerConfig();
            FortificationConfig.PreparationResult fortifications = FortificationConfig.compileAndFreeze(server, ExternalConfigBootstrap.getUsableMaps());
            if (!fortifications.success()) {
                startupPreparationResult2 = this.failStartup("\u5de5\u4e8b registry \u65e0\u6cd5\u51bb\u7ed3: " + fortifications.error(), warnings);
                return startupPreparationResult2;
            }
            startupPreparationResult2 = this.startupPreparation = new StartupPreparationResult(StartupResetStatus.READY, this.availableDimensions.size(), List.copyOf(warnings), null);
            return startupPreparationResult2;
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            startupPreparationResult = this.failStartup("\u7b49\u5f85\u6218\u573a I/O barrier \u65f6\u88ab\u4e2d\u65ad", warnings);
            return startupPreparationResult;
        }
        catch (Exception e) {
            startupPreparationResult = this.failStartup("\u6218\u573a\u542f\u52a8\u51c6\u5907\u5f02\u5e38: " + e.getMessage(), warnings);
            return startupPreparationResult;
        }
        finally {
            this.busy.set(false);
        }
    }

    public int prepareAllAtStartup(MinecraftServer server) {
        return this.prepareAtStartup((MinecraftServer)server).preparedCount;
    }

    private StartupPreparationResult failStartup(String error, List<String> warnings) {
        this.availableDimensions.clear();
        this.lastError = error;
        this.state = ImportState.FAILED;
        this.startupPreparation = new StartupPreparationResult(StartupResetStatus.FAILED, 0, List.copyOf(warnings), error);
        Espetro.LOGGER.error("{}\uff1b\u4e3b\u57ce\u7ee7\u7eed\u8fd0\u884c\uff0c\u4f46\u672c\u6b21\u4f1a\u8bdd\u5168\u90e8\u6218\u573a\u5df2\u7981\u7528", (Object)error);
        return this.startupPreparation;
    }

    public boolean isPrepared(ActiveMapConfig map) {
        return this.isStartupReady() && map != null && this.availableDimensions.contains(map.dimensionId);
    }

    public void importAndLoad(MinecraftServer server, ActiveMapConfig map, Consumer<Result> onComplete) {
        if (!this.isStartupReady()) {
            onComplete.accept(Result.fail("\u6218\u573a\u542f\u52a8\u91cd\u7f6e\u5931\u8d25\uff0c\u672c\u6b21\u4f1a\u8bdd\u5730\u56fe\u5df2\u7981\u7528: " + this.startupPreparation.error));
            return;
        }
        if (!this.busy.compareAndSet(false, true)) {
            onComplete.accept(Result.fail("\u6218\u573a\u6b63\u5728\u5207\u6362\u4e2d"));
            return;
        }
        this.lastError = null;
        this.state = ImportState.LOADING;
        long generation = this.sessionGeneration.get();
        server.execute(() -> {
            if (!this.isCurrent(server, generation)) {
                return;
            }
            try {
                if (!this.isPrepared(map)) {
                    this.fail(server, "\u5730\u56fe\u6a21\u677f\u5728\u542f\u52a8\u9636\u6bb5\u51c6\u5907\u5931\u8d25: " + map.displayName, onComplete);
                    return;
                }
                ServerLevel level = server.m_129880_(map.dimensionKey);
                this.rebuildSelectedMap(server, map, level, onComplete);
            }
            catch (Exception e) {
                Espetro.LOGGER.error("\u6218\u573a\u5bfc\u5165\u542f\u52a8\u5931\u8d25", (Throwable)e);
                this.fail(server, e.getMessage(), onComplete);
            }
        });
    }

    public void cleanupBattlefield(MinecraftServer server, @Nullable ActiveMapConfig map, Runnable onDone) {
        this.cleanupBattlefield(server, map, (Result ignored) -> {
            if (onDone != null) {
                onDone.run();
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void cleanupBattlefield(MinecraftServer server, @Nullable ActiveMapConfig map, Consumer<Result> onComplete) {
        long generation = this.sessionGeneration.get();
        if (!this.busy.compareAndSet(false, true)) {
            Object object = this.pendingCleanupLock;
            synchronized (object) {
                this.pendingCleanupRequested = true;
                if (map != null) {
                    this.pendingCleanupMap = map;
                }
                if (onComplete != null) {
                    this.pendingCleanupCallbacks.add(onComplete);
                }
            }
            Espetro.LOGGER.warn("\u6218\u573a\u6b63\u5fd9\uff0c\u6e05\u7406\u8bf7\u6c42\u5df2\u6392\u961f");
            return;
        }
        this.state = ImportState.CLEANING;
        server.execute(() -> {
            if (!this.isCurrent(server, generation)) {
                return;
            }
            ActiveMapConfig target = map != null ? map : (this.lastLoaded != null ? this.lastLoaded : BattlefieldContext.getOrNull());
            try {
                BastionManager.getInstance().destroyAllBastionsForMatchEnd();
                VehicleManager.getInstance().reset();
                BattlefieldContext.clear();
                this.lastLoaded = null;
                if (target == null) {
                    this.finishCleanup(server, Result.ok(), onComplete);
                    return;
                }
                ServerLevel discarded = this.detachForDiscard(server, target.dimensionKey);
                Path dimPath = this.dimensionDirectory(server, target.dimensionKey);
                Path tempPath = BattlefieldWorldManager.importingPath(dimPath);
                CompletableFuture.supplyAsync(() -> this.discardAndDelete(discarded, dimPath, tempPath), IO).whenComplete((result, error) -> server.execute(() -> {
                    if (!this.isCurrent(server, generation)) {
                        return;
                    }
                    Result completed = error == null ? result : Result.fail("\u5220\u9664\u6218\u573a\u5b58\u6863\u526f\u672c\u5931\u8d25: " + error.getMessage());
                    this.finishCleanup(server, completed, onComplete);
                }));
            }
            catch (Exception e) {
                Espetro.LOGGER.error("\u6218\u573a\u6e05\u7406\u5931\u8d25", (Throwable)e);
                this.finishCleanup(server, Result.fail(e.getMessage()), onComplete);
            }
        });
    }

    private void rebuildSelectedMap(MinecraftServer server, ActiveMapConfig map, @Nullable ServerLevel existing, Consumer<Result> onComplete) {
        long generation = this.sessionGeneration.get();
        this.state = existing == null ? ImportState.COPYING : ImportState.UNLOADING;
        ServerLevel discarded = existing == null ? null : this.detachForDiscard(server, map.dimensionKey);
        Path dimPath = this.dimensionDirectory(server, map.dimensionKey);
        Path tempPath = BattlefieldWorldManager.importingPath(dimPath);
        CompletableFuture.supplyAsync(() -> {
            Result discardedResult = this.closeDiscardedLevel(discarded);
            if (!discardedResult.success()) {
                return discardedResult;
            }
            if (!this.isCurrent(server, generation)) {
                return Result.fail("\u8fc7\u671f generation \u5df2\u4e22\u5f03");
            }
            this.state = ImportState.COPYING;
            return this.copyTemplate(map, dimPath, tempPath);
        }, IO).whenComplete((result, error) -> server.execute(() -> {
            if (!this.isCurrent(server, generation)) {
                return;
            }
            if (error != null) {
                this.fail(server, "\u590d\u5236\u5730\u56fe\u5931\u8d25: " + error.getMessage(), onComplete);
                return;
            }
            if (result == null || !result.success()) {
                this.fail(server, result == null ? "\u590d\u5236\u5730\u56fe\u5931\u8d25" : result.error(), onComplete);
                return;
            }
            this.state = ImportState.LOADING;
            ServerLevel created = this.createServerLevel(server, map.dimensionKey);
            if (created == null) {
                this.fail(server, "\u521b\u5efa\u6218\u573a\u7ef4\u5ea6\u5931\u8d25: " + map.dimensionId, onComplete);
                return;
            }
            this.activate(server, map, onComplete);
        }));
    }

    private void activate(MinecraftServer server, ActiveMapConfig map, Consumer<Result> onComplete) {
        ServerLevel level = server.m_129880_(map.dimensionKey);
        if (level == null) {
            this.fail(server, "\u6218\u573a\u7ef4\u5ea6\u672a\u6302\u8f7d: " + map.dimensionId, onComplete);
            return;
        }
        this.beginActivationPreparation(level, map, onComplete);
    }

    private void beginActivationPreparation(ServerLevel level, ActiveMapConfig map, Consumer<Result> onComplete) {
        this.clearActivationPreparation();
        this.activationLevel = level;
        this.activationMap = map;
        this.activationCallback = onComplete;
        this.activationSessionGeneration = this.sessionGeneration.get();
        BattlefieldWorldManager.collectCriticalChunks(map, this.activationChunks);
        this.pendingActivationChunks.addAll(this.activationChunks);
        this.activationChunksTotal = this.activationChunks.size();
        Espetro.LOGGER.info("\u6218\u573a\u5173\u952e\u533a\u5757\u5f00\u59cb\u5206\u6279\u9884\u8f7d: {} \u4e2a", (Object)this.activationChunksTotal);
        if (this.pendingActivationChunks.isEmpty()) {
            this.finishActivationPreparationIfReady();
        }
    }

    private static void collectCriticalChunks(ActiveMapConfig map, Set<ChunkPos> output) {
        if (map.spawnPoints != null) {
            BattlefieldWorldManager.addSpawnArea(map.spawnPoints.attack, output);
            BattlefieldWorldManager.addSpawnArea(map.spawnPoints.defend, output);
        }
        if (map.vehSpawn == null) {
            return;
        }
        for (List<VehSpawnSnapshot.SpawnPoint> points : map.vehSpawn.spawnPointsByType.values()) {
            for (VehSpawnSnapshot.SpawnPoint point : points) {
                BattlefieldWorldManager.addVehiclePose(point.attack(), output);
                BattlefieldWorldManager.addVehiclePose(point.defend(), output);
            }
        }
    }

    private static void addSpawnArea(@Nullable SpawnPointsSnapshot.Point point, Set<ChunkPos> output) {
        if (point == null) {
            return;
        }
        ChunkPos center = new ChunkPos(BlockPos.m_274561_(point.x(), point.y(), point.z()));
        for (int dx = -1; dx <= 1; ++dx) {
            for (int dz = -1; dz <= 1; ++dz) {
                output.add(new ChunkPos(center.f_45578_ + dx, center.f_45579_ + dz));
            }
        }
    }

    private static void addVehiclePose(@Nullable VehSpawnSnapshot.Pose pose, Set<ChunkPos> output) {
        if (pose == null) {
            return;
        }
        output.add(new ChunkPos(BlockPos.m_274561_(pose.x(), pose.y(), pose.z())));
    }

    private void finishActivationPreparationIfReady() {
        if (this.activationLevel == null || this.activationSessionGeneration != this.sessionGeneration.get() || !this.pendingActivationChunks.isEmpty() || this.activationChunksInFlight > 0) {
            return;
        }
        ServerLevel level = this.activationLevel;
        ActiveMapConfig map = this.activationMap;
        Consumer<Result> onComplete = this.activationCallback;
        String failure = this.activationChunkFailure;
        int total = this.activationChunksTotal;
        this.clearActivationPreparation();
        if (failure != null) {
            this.fail(level.m_7654_(), failure, onComplete);
            return;
        }
        BattlefieldContext.activate(map);
        this.lastLoaded = map;
        try {
            int deployStations = DeploySupplyStationPlacer.placeAtSpawnPoints(level);
            Espetro.LOGGER.info("\u6218\u573a\u539f\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9: {} \u4e2a", (Object)deployStations);
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u9884\u653e\u539f\u90e8\u7f72\u70b9\u8865\u7ed9\u7ad9\u5931\u8d25", (Throwable)e);
        }
        try {
            VehicleManager.getInstance().scheduleMainBaseSupplyStations(level);
            Espetro.LOGGER.info("\u4e3b\u91cd\u751f\u70b9\u5f39\u836f\u8865\u7ed9\u7ad9\u5df2\u6392\u961f\uff08\u5ef6\u8fdf 5 \u79d2\u751f\u6210\uff09");
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u6392\u961f\u751f\u6210\u4e3b\u91cd\u751f\u70b9\u5f39\u836f\u8865\u7ed9\u7ad9\u5931\u8d25", (Throwable)e);
        }
        this.state = ImportState.READY;
        this.busy.set(false);
        Espetro.LOGGER.info("\u6218\u573a\u5730\u56fe\u5df2\u4ece\u53ea\u8bfb\u6a21\u677f\u526f\u672c\u5c31\u7eea: {}\uff08\u9884\u8f7d{}\u4e2a\u5173\u952e\u533a\u5757\uff09", (Object)map.dimensionId, (Object)total);
        BattlefieldWorldManager.safeAccept(onComplete, Result.ok());
        this.drainPendingCleanup(level.m_7654_());
    }

    private void clearActivationPreparation() {
        if (this.activationLevel != null) {
            for (ChunkPos chunk : this.activationChunks) {
                this.activationLevel.m_7726_().m_8438_(TicketType.f_9447_, chunk, 1, chunk.m_45615_());
            }
        }
        ++this.activationGeneration;
        this.pendingActivationChunks.clear();
        this.activationChunks.clear();
        this.activationChunksInFlight = 0;
        this.activationChunksTotal = 0;
        this.activationChunkFailure = null;
        this.activationLevel = null;
        this.activationMap = null;
        this.activationCallback = null;
    }

    private void finishCleanup(MinecraftServer server, Result result, Consumer<Result> onComplete) {
        if (result.success()) {
            this.state = ImportState.IDLE;
            this.lastError = null;
            Espetro.LOGGER.info("\u6218\u573a\u5b58\u6863\u7ef4\u5ea6\u526f\u672c\u5df2\u5220\u9664\uff0c\u7b49\u5f85\u4e0b\u4e00\u5c40\u91cd\u65b0\u5bfc\u5165");
        } else {
            this.state = ImportState.FAILED;
            this.lastError = result.error();
            Espetro.LOGGER.error("\u6218\u573a\u5b58\u6863\u7ef4\u5ea6\u526f\u672c\u5220\u9664\u5931\u8d25: {}", (Object)result.error());
        }
        this.busy.set(false);
        BattlefieldWorldManager.safeAccept(onComplete, result);
        this.drainPendingCleanup(server);
    }

    private void fail(MinecraftServer server, String error, Consumer<Result> onComplete) {
        this.lastError = error;
        this.state = ImportState.FAILED;
        BattlefieldContext.clear();
        this.busy.set(false);
        BattlefieldWorldManager.safeAccept(onComplete, Result.fail(error));
        this.drainPendingCleanup(server);
    }

    private static void safeAccept(Consumer<Result> callback, Result result) {
        if (callback == null) {
            return;
        }
        try {
            callback.accept(result);
        }
        catch (Exception callbackError) {
            Espetro.LOGGER.error("\u6218\u573a\u5207\u6362\u56de\u8c03\u6267\u884c\u5931\u8d25", (Throwable)callbackError);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void drainPendingCleanup(MinecraftServer server) {
        ArrayList<Consumer<Result>> callbacks;
        ActiveMapConfig map;
        Object object = this.pendingCleanupLock;
        synchronized (object) {
            if (!this.pendingCleanupRequested) {
                return;
            }
            this.pendingCleanupRequested = false;
            map = this.pendingCleanupMap;
            this.pendingCleanupMap = null;
            callbacks = new ArrayList<Consumer<Result>>(this.pendingCleanupCallbacks);
            this.pendingCleanupCallbacks.clear();
        }
        this.cleanupBattlefield(server, map, (Result result) -> {
            for (Consumer callback : callbacks) {
                try {
                    callback.accept(result);
                }
                catch (Exception e) {
                    Espetro.LOGGER.error("\u6392\u961f\u7684\u6218\u573a\u6e05\u7406\u56de\u8c03\u6267\u884c\u5931\u8d25", (Throwable)e);
                }
            }
        });
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void resetAfterServerStop() {
        this.sessionGeneration.incrementAndGet();
        this.clearActivationPreparation();
        this.availableDimensions.clear();
        this.state = ImportState.IDLE;
        this.lastError = null;
        this.lastLoaded = null;
        this.startupPreparation = new StartupPreparationResult(StartupResetStatus.NOT_RUN, 0, List.of(), null);
        FortificationConfig.resetForNextServer();
        BattlefieldContext.clear();
        Object object = this.pendingCleanupLock;
        synchronized (object) {
            this.pendingCleanupRequested = false;
            this.pendingCleanupMap = null;
            this.pendingCleanupCallbacks.clear();
        }
    }

    private static Path importingPath(Path dimPath) {
        return dimPath.resolveSibling(dimPath.getFileName().toString() + ".importing");
    }

    @Nullable
    private ServerLevel detachForDiscard(MinecraftServer server, ResourceKey<Level> key) {
        ServerLevel level = server.m_129880_(key);
        if (level == null) {
            return null;
        }
        BlockPos hubSpawn = server.m_129783_().m_220360_();
        for (ServerPlayer player : new ArrayList<ServerPlayer>(level.m_6907_())) {
            player.m_8999_(server.m_129783_(), (double)hubSpawn.m_123341_() + 0.5, hubSpawn.m_123342_(), (double)hubSpawn.m_123343_() + 0.5, 0.0f, 0.0f);
        }
        MinecraftForge.EVENT_BUS.post((Event)new LevelEvent.Unload((LevelAccessor)level));
        Map levels = server.forgeGetWorldMap();
        if (levels.get(key) == level) {
            levels.remove(key);
            server.markWorldsDirty();
        }
        BattlefieldWorldManager.removeBorderDelegate(server, level);
        level.f_8564_ = true;
        level.invalidateCaps();
        Espetro.LOGGER.info("\u5df2\u4ece\u670d\u52a1\u5668 Tick \u5217\u8868\u6458\u9664\u53ef\u4e22\u5f03\u6218\u573a\u7ef4\u5ea6: {}", (Object)key.m_135782_());
        return level;
    }

    private static void removeBorderDelegate(MinecraftServer server, ServerLevel level) {
        WorldBorder mainBorder = server.m_129783_().m_6857_();
        WorldBorder discardedBorder = level.m_6857_();
        mainBorder.f_61905_.removeIf(listener -> {
            if (!(listener instanceof BorderChangeListener.DelegateBorderChangeListener)) return false;
            BorderChangeListener.DelegateBorderChangeListener delegate = (BorderChangeListener.DelegateBorderChangeListener)listener;
            if (delegate.f_61864_ != discardedBorder) return false;
            return true;
        });
    }

    private Result closeDiscardedLevel(@Nullable ServerLevel level) {
        if (level == null) {
            return Result.ok();
        }
        Exception failure = null;
        try {
            level.f_143244_.f_157493_.close();
        }
        catch (Exception e) {
            failure = BattlefieldWorldManager.appendFailure(failure, e);
        }
        ServerChunkCache chunks = level.m_7726_();
        try {
            chunks.m_7827_().close();
        }
        catch (Exception e) {
            failure = BattlefieldWorldManager.appendFailure(failure, e);
        }
        try {
            chunks.f_8325_.close();
        }
        catch (Exception e) {
            failure = BattlefieldWorldManager.appendFailure(failure, e);
        }
        return failure == null ? Result.ok() : Result.fail("\u5173\u95ed\u65e7\u6218\u573a\u5b58\u50a8\u5931\u8d25: " + failure.getMessage());
    }

    private Result discardAndDelete(@Nullable ServerLevel discarded, Path dimPath, Path tempPath) {
        Result closed = this.closeDiscardedLevel(discarded);
        Exception failure = closed.success() ? null : new IOException(closed.error());
        try {
            BattlefieldWorldManager.deleteRecursively(tempPath);
            BattlefieldWorldManager.deleteRecursively(dimPath);
        }
        catch (Exception e) {
            failure = BattlefieldWorldManager.appendFailure(failure, e);
        }
        return failure == null ? Result.ok() : Result.fail("\u5220\u9664\u5b58\u6863\u6218\u573a\u7ef4\u5ea6\u5931\u8d25: " + failure.getMessage());
    }

    private static Exception appendFailure(@Nullable Exception current, Exception next) {
        if (current == null) {
            return next;
        }
        current.addSuppressed(next);
        return current;
    }

    @Nullable
    private ServerLevel createServerLevel(MinecraftServer server, ResourceKey<Level> key) {
        ServerLevel level = null;
        try {
            WorldData worldData = server.m_129910_();
            ServerLevelData overworldData = worldData.m_5996_();
            ResourceKey<LevelStem> stemKey = ResourceKey.m_135785_(Registries.f_256862_, key.m_135782_());
            LevelStem stem = server.m_206579_().m_175515_(Registries.f_256862_).m_6246_(stemKey);
            if (stem == null) {
                Espetro.LOGGER.error("\u65e0\u6cd5\u89e3\u6790\u6218\u573a LevelStem: {}", (Object)key.m_135782_());
                return null;
            }
            DerivedLevelData derived = new DerivedLevelData(worldData, overworldData);
            LoggerChunkProgressListener progress = new LoggerChunkProgressListener(11);
            level = new ServerLevel(server, Util.m_183991_(), server.f_129744_, derived, key, stem, progress, worldData.m_7513_(), BiomeManager.m_47877_(worldData.m_246337_().m_245499_()), List.of(), false, server.m_129783_().m_288231_());
            level.m_8733_(new BlockPos(0, 64, 0), 0.0f);
            level.m_7726_().m_6707_(server.m_7004_(), server.m_6998_());
            BorderChangeListener.DelegateBorderChangeListener delegate = new BorderChangeListener.DelegateBorderChangeListener(level.m_6857_());
            server.m_129783_().m_6857_().m_61929_(delegate);
            server.forgeGetWorldMap().put(key, level);
            server.markWorldsDirty();
            MinecraftForge.EVENT_BUS.post((Event)new LevelEvent.Load((LevelAccessor)level));
            Espetro.LOGGER.info("\u5df2\u6302\u8f7d\u4ece EsWorld \u91cd\u65b0\u590d\u5236\u7684\u6218\u573a\u7ef4\u5ea6: {}", (Object)key.m_135782_());
            return level;
        }
        catch (Exception e) {
            Espetro.LOGGER.error("\u521b\u5efa\u6218\u573a ServerLevel \u5931\u8d25: {}", (Object)key.m_135782_(), (Object)e);
            if (level != null) {
                ServerLevel failedLevel = level;
                server.forgeGetWorldMap().remove(key, level);
                server.markWorldsDirty();
                BattlefieldWorldManager.removeBorderDelegate(server, failedLevel);
                IO.execute(() -> this.closeDiscardedLevel(failedLevel));
            }
            return null;
        }
    }

    private Result copyTemplate(ActiveMapConfig map, Path dimPath, Path tempPath) {
        return BattlefieldWorldManager.replaceSaveCopyFast(map.templateWorldDir, dimPath, tempPath);
    }

    static Result replaceSaveCopyFast(Path template, Path dimPath, Path tempPath) {
        Result reflink = BattlefieldWorldManager.replaceSaveCopyUsingReflink(template, dimPath, tempPath);
        if (reflink.success()) {
            Espetro.LOGGER.info("\u6218\u573a\u5730\u56fe\u4f7f\u7528\u5199\u65f6\u590d\u5236\u5b8c\u6210: {}", (Object)template.getFileName());
            return reflink;
        }
        Espetro.LOGGER.info("\u5199\u65f6\u590d\u5236\u4e0d\u53ef\u7528\uff0c\u56de\u9000\u5230 Java \u6587\u4ef6\u590d\u5236: {}", (Object)reflink.error());
        return BattlefieldWorldManager.replaceSaveCopy(template, dimPath, tempPath);
    }

    private static Result replaceSaveCopyUsingReflink(Path template, Path dimPath, Path tempPath) {
        try {
            BattlefieldWorldManager.deleteRecursively(tempPath);
            Files.createDirectories(tempPath, new FileAttribute[0]);
            for (String child : List.of("region", "entities", "poi", "data", "EsConfig")) {
                Result copied;
                Path source = template.resolve(child);
                if (!Files.exists(source, new LinkOption[0])) continue;
                Path target = tempPath.resolve(child);
                if (Files.isDirectory(source, LinkOption.NOFOLLOW_LINKS)) {
                    Files.createDirectories(target, new FileAttribute[0]);
                    copied = BattlefieldWorldManager.runReflinkCopy(source.resolve("."), target);
                    if (copied.success()) continue;
                    BattlefieldWorldManager.deleteRecursively(tempPath);
                    return copied;
                }
                copied = BattlefieldWorldManager.runReflinkCopy(source, target);
                if (copied.success()) continue;
                BattlefieldWorldManager.deleteRecursively(tempPath);
                return copied;
            }
            BattlefieldWorldManager.deleteRecursively(dimPath);
            Files.createDirectories(dimPath.getParent(), new FileAttribute[0]);
            try {
                Files.move(tempPath, dimPath, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ignored) {
                Files.move(tempPath, dimPath, new CopyOption[0]);
            }
            return Result.ok();
        }
        catch (Exception e) {
            try {
                BattlefieldWorldManager.deleteRecursively(tempPath);
            }
            catch (Exception exception) {
                // empty catch block
            }
            return Result.fail("reflink \u590d\u5236\u5931\u8d25: " + e.getMessage());
        }
    }

    private static Result runReflinkCopy(Path source, Path target) {
        try {
            Process process = new ProcessBuilder("cp", "--archive", "--reflink=always", "--", source.toString(), target.toString()).redirectErrorStream(true).start();
            byte[] output = process.getInputStream().readAllBytes();
            int exit = process.waitFor();
            return exit == 0 ? Result.ok() : Result.fail("cp --reflink \u9000\u51fa\u7801 " + exit + ": " + new String(output, StandardCharsets.UTF_8).trim());
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return Result.fail("reflink \u590d\u5236\u88ab\u4e2d\u65ad");
        }
        catch (Exception e) {
            return Result.fail(e.getMessage());
        }
    }

    static Result replaceSaveCopy(Path template, Path dimPath, Path tempPath) {
        try {
            BattlefieldWorldManager.deleteRecursively(tempPath);
            Files.createDirectories(tempPath, new FileAttribute[0]);
            for (String child : List.of("region", "entities", "poi", "data", "EsConfig")) {
                Path src = template.resolve(child);
                if (!Files.exists(src, new LinkOption[0])) continue;
                BattlefieldWorldManager.copyDirectory(src, tempPath.resolve(child));
            }
            BattlefieldWorldManager.deleteRecursively(dimPath);
            Files.createDirectories(dimPath.getParent(), new FileAttribute[0]);
            try {
                Files.move(tempPath, dimPath, StandardCopyOption.ATOMIC_MOVE);
            }
            catch (AtomicMoveNotSupportedException ignored) {
                Files.move(tempPath, dimPath, new CopyOption[0]);
            }
            return Result.ok();
        }
        catch (Exception e) {
            try {
                BattlefieldWorldManager.deleteRecursively(tempPath);
            }
            catch (Exception exception) {
                // empty catch block
            }
            return Result.fail("\u590d\u5236\u6a21\u677f\u5931\u8d25: " + e.getMessage());
        }
    }

    private Path dimensionDirectory(MinecraftServer server, ResourceKey<Level> key) {
        Path worldRoot = server.f_129744_.m_78283_(LevelResource.f_78182_);
        return BattlefieldWorldManager.validateDimensionDirectory(worldRoot, server.f_129744_.m_197394_(key));
    }

    static Path validateDimensionDirectory(Path worldRoot, Path dimensionPath) {
        Path allowedRoot;
        Path normalizedWorldRoot = worldRoot.toAbsolutePath().normalize();
        Path normalizedDimension = dimensionPath.toAbsolutePath().normalize();
        if (!normalizedDimension.startsWith(allowedRoot = normalizedWorldRoot.resolve("dimensions").normalize()) || normalizedDimension.equals(allowedRoot)) {
            throw BattlefieldWorldManager.rejectedDimensionPath(normalizedDimension);
        }
        try {
            Path realAllowedRoot = BattlefieldWorldManager.resolveExistingAncestors(allowedRoot);
            Path realDimension = BattlefieldWorldManager.resolveExistingAncestors(normalizedDimension);
            if (!realDimension.startsWith(realAllowedRoot) || realDimension.equals(realAllowedRoot)) {
                throw BattlefieldWorldManager.rejectedDimensionPath(normalizedDimension);
            }
        }
        catch (IOException e) {
            throw new IllegalStateException("\u65e0\u6cd5\u5b89\u5168\u6821\u9a8c\u5b58\u6863\u7ef4\u5ea6\u76ee\u5f55: " + normalizedDimension, e);
        }
        return normalizedDimension;
    }

    private static Path resolveExistingAncestors(Path path) throws IOException {
        Path normalized;
        Path existing;
        ArrayList<Path> missingSegments = new ArrayList<Path>();
        for (existing = normalized = path.toAbsolutePath().normalize(); existing != null && !Files.exists(existing, LinkOption.NOFOLLOW_LINKS); existing = existing.getParent()) {
            Path name = existing.getFileName();
            if (name == null) continue;
            missingSegments.add(name);
        }
        if (existing == null) {
            throw new IOException("\u8def\u5f84\u6ca1\u6709\u53ef\u89e3\u6790\u7684\u73b0\u5b58\u7236\u76ee\u5f55: " + normalized);
        }
        Path resolved = existing.toRealPath(new LinkOption[0]);
        for (int i = missingSegments.size() - 1; i >= 0; --i) {
            resolved = resolved.resolve((Path)missingSegments.get(i));
        }
        return resolved.normalize();
    }

    private static IllegalStateException rejectedDimensionPath(Path dimensionPath) {
        return new IllegalStateException("\u62d2\u7edd\u8bbf\u95ee\u5b58\u6863\u7ef4\u5ea6\u76ee\u5f55\u4e4b\u5916\u7684\u8def\u5f84: " + dimensionPath);
    }

    private static void copyDirectory(final Path source, final Path target) throws IOException {
        final Path sourceRoot = source.toRealPath(new LinkOption[0]);
        Files.walkFileTree(source, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                if (Files.isSymbolicLink(dir) || !dir.toRealPath(new LinkOption[0]).startsWith(sourceRoot)) {
                    throw new IOException("\u5730\u56fe\u6a21\u677f\u5305\u542b\u8d8a\u754c\u7b26\u53f7\u94fe\u63a5\u76ee\u5f55: " + dir);
                }
                Path rel = source.relativize(dir);
                Path dest = target.resolve(rel.toString());
                Files.createDirectories(dest, new FileAttribute[0]);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (Files.isSymbolicLink(file) || !file.toRealPath(new LinkOption[0]).startsWith(sourceRoot)) {
                    throw new IOException("\u5730\u56fe\u6a21\u677f\u5305\u542b\u7b26\u53f7\u94fe\u63a5\u6216\u8d8a\u754c\u6587\u4ef6: " + file);
                }
                Path rel = source.relativize(file);
                Path dest = target.resolve(rel.toString());
                Files.createDirectories(dest.getParent(), new FileAttribute[0]);
                Files.copy(file, dest, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    private static void deleteRecursively(Path path) throws IOException {
        if (path == null || !Files.exists(path, new LinkOption[0])) {
            return;
        }
        Files.walkFileTree(path, (FileVisitor<? super Path>)new SimpleFileVisitor<Path>(){

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.deleteIfExists(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.deleteIfExists(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }

    public static enum ImportState {
        IDLE,
        UNLOADING,
        COPYING,
        LOADING,
        READY,
        FAILED,
        CLEANING;

    }

    public record StartupPreparationResult(StartupResetStatus status, int preparedCount, List<String> warnings, @Nullable String error) {
        public StartupPreparationResult {
            warnings = warnings == null ? List.of() : List.copyOf(warnings);
        }
    }

    public static enum StartupResetStatus {
        NOT_RUN,
        RESETTING,
        READY,
        FAILED;

    }

    public record Result(boolean success, @Nullable String error) {
        public static Result ok() {
            return new Result(true, null);
        }

        public static Result fail(String error) {
            return new Result(false, error);
        }
    }
}

