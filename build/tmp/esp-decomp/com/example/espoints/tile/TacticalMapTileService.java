/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.hexagram2021.tetrachordlib.core.container.IMultidimensional
 *  com.hexagram2021.tetrachordlib.core.container.KDTree
 *  com.hexagram2021.tetrachordlib.core.container.KDTree$BuildNode
 *  com.hexagram2021.tetrachordlib.core.container.KDTree$KDNode
 *  com.hexagram2021.tetrachordlib.core.container.impl.DoublePosition
 *  com.hexagram2021.tetrachordlib.core.container.impl.LinkedKDTree
 *  net.minecraft.server.MinecraftServer
 *  net.minecraft.server.level.ServerPlayer
 *  net.minecraftforge.fml.loading.FMLPaths
 *  org.espetro.api.ActiveBattlefieldSnapshot
 *  org.espetro.api.EspetroAPI
 */
package com.example.espoints.tile;

import com.example.espoints.ESPointsMod;
import com.example.espoints.config.ModConfig;
import com.example.espoints.network.SyncTacticalMapTileMessage;
import com.example.espoints.tile.FairTileRequestQueue;
import com.example.espoints.tile.InFlightTaskRegistry;
import com.example.espoints.tile.ProgressiveTileReadiness;
import com.example.espoints.tile.TacticalMapImageScaler;
import com.example.espoints.tile.TacticalMapLodPlanner;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import com.example.espoints.tile.TacticalMapTileKey;
import com.example.espoints.tile.TileTransferLimiter;
import com.example.espoints.tile.WeightedLruCache;
import com.hexagram2021.tetrachordlib.core.container.IMultidimensional;
import com.hexagram2021.tetrachordlib.core.container.KDTree;
import com.hexagram2021.tetrachordlib.core.container.impl.DoublePosition;
import com.hexagram2021.tetrachordlib.core.container.impl.LinkedKDTree;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.FileAttribute;
import java.nio.file.attribute.FileTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;
import javax.imageio.ImageIO;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.api.ActiveBattlefieldSnapshot;
import org.espetro.api.EspetroAPI;

public final class TacticalMapTileService {
    public static final int MAX_ENCODED_TILE_BYTES = 0x200000;
    static final String PYRAMID_CACHE_VERSION = "p3";
    static final String COMPLETE_MANIFEST = ".complete";
    private static final int WORK_QUEUE_CAPACITY = 512;
    private static final int MAX_SEND_ATTEMPTS_PER_TICK = 64;
    private static final long PREVIEW_RESEND_MILLIS = 8000L;
    private static final byte[] PNG_SIGNATURE = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
    private static final TacticalMapTileService INSTANCE = new TacticalMapTileService();
    private final AtomicLong generationSequence = new AtomicLong();
    private final ExecutorService executor = new ThreadPoolExecutor(2, 2, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<Runnable>(512), runnable -> {
        Thread thread = new Thread(runnable, "ESPoints-TacticalMapTile");
        thread.setDaemon(true);
        return thread;
    }, new ThreadPoolExecutor.AbortPolicy());
    private final InFlightTaskRegistry<TacticalMapTileKey, byte[]> inFlight = new InFlightTaskRegistry();
    private final WeightedLruCache<TacticalMapTileKey, byte[]> memory = new WeightedLruCache(0x2000000L, bytes -> ((byte[])bytes).length);
    private final TileTransferLimiter transferLimiter = new TileTransferLimiter();
    private final FairTileRequestQueue<TacticalMapTileKey> sendQueue = new FairTileRequestQueue();
    private final Map<TacticalMapTileKey, Set<UUID>> waiters = new ConcurrentHashMap<TacticalMapTileKey, Set<UUID>>();
    private final Map<UUID, ViewportHint> playerViewports = new ConcurrentHashMap<UUID, ViewportHint>();
    private final Map<UUID, Long> lastPreviewEnqueueAt = new ConcurrentHashMap<UUID, Long>();
    private volatile ActiveState active;

    private TacticalMapTileService() {
    }

    public static TacticalMapTileService get() {
        return INSTANCE;
    }

    public synchronized void activate(ActiveBattlefieldSnapshot snapshot) {
        ActiveState state;
        byte[] backgroundBytes;
        this.clearLocked();
        byte[] byArray = backgroundBytes = snapshot == null ? new byte[]{} : snapshot.backgroundBytes();
        if (snapshot == null || snapshot.backgroundSha256().isBlank() || !snapshot.backgroundSha256().matches("[0-9a-f]{64}") || snapshot.backgroundWidth() <= 0 || snapshot.backgroundHeight() <= 0 || backgroundBytes.length == 0) {
            ESPointsMod.LOGGER.error("\u6218\u672f\u5730\u56fe\u672a\u6fc0\u6d3b\uff1a\u7f3a\u5c11\u6709\u6548\u5e95\u56fe (sha='{}' {}x{} bytes={})", (Object)(snapshot == null ? "" : snapshot.backgroundSha256()), (Object)(snapshot == null ? 0 : snapshot.backgroundWidth()), (Object)(snapshot == null ? 0 : snapshot.backgroundHeight()), (Object)backgroundBytes.length);
            return;
        }
        TacticalMapPyramidLayout layout = new TacticalMapPyramidLayout(snapshot.backgroundWidth(), snapshot.backgroundHeight());
        long session = Math.max(1L, EspetroAPI.getTacticalMapStateSnapshot().battlefieldSession());
        Path root = FMLPaths.CONFIGDIR.get().resolve("espoints").resolve("cache").resolve("tactical-map");
        Path mapDirectory = root.resolve(TacticalMapTileService.cacheDirectoryName(snapshot.backgroundSha256()));
        this.active = state = new ActiveState(this.generationSequence.incrementAndGet(), new Descriptor(session, snapshot.backgroundImage(), snapshot.backgroundSha256(), layout.width(), layout.height(), 512, layout.maxLevel()), layout, mapDirectory, backgroundBytes);
        this.memory.setMaximumWeight(Math.max(8L, (long)((Integer)ModConfig.tacticalMapServerMemoryMiB.get()).intValue()) * 1024L * 1024L);
        try {
            state.build = CompletableFuture.runAsync(() -> this.buildPyramid(state, root), this.executor);
        }
        catch (RuntimeException error) {
            state.failAll(error);
            ESPointsMod.LOGGER.error("\u6218\u672f\u5730\u56fe\u6784\u5efa\u961f\u5217\u5df2\u6ee1", (Throwable)error);
            return;
        }
        ESPointsMod.LOGGER.info("\u6218\u672f\u5730\u56fe\u5df2\u6fc0\u6d3b: session={} {}x{} previewLevel={} cache={}", (Object)session, (Object)layout.width(), (Object)layout.height(), (Object)layout.maxLevel(), (Object)mapDirectory.getFileName());
    }

    public synchronized void clear() {
        this.clearLocked();
    }

    private void clearLocked() {
        ActiveState previous = this.active;
        this.active = null;
        if (previous != null) {
            previous.cancel();
        }
        this.inFlight.clear();
        this.memory.clear();
        this.transferLimiter.clear();
        this.sendQueue.clear();
        this.waiters.clear();
        this.playerViewports.clear();
        this.lastPreviewEnqueueAt.clear();
    }

    public Descriptor descriptor() {
        ActiveState state = this.active;
        return state == null ? Descriptor.EMPTY : state.descriptor;
    }

    public CompletableFuture<byte[]> request(long session, int level, int x, int y) {
        ActiveState state = this.active;
        if (state == null || state.descriptor.session() != session || !state.layout.isValid(level, x, y)) {
            return CompletableFuture.failedFuture(new IllegalArgumentException("Invalid/stale tactical tile request"));
        }
        TacticalMapTileKey key = new TacticalMapTileKey(session, level, x, y);
        byte[] cached = this.memory.get(key);
        if (cached != null) {
            return CompletableFuture.completedFuture(cached);
        }
        CompletableFuture<Path> readiness = state.readiness(key);
        if (state.registerDemand(key)) {
            this.startDemandBuild(state, key);
        }
        return this.inFlight.getOrStart(key, () -> readiness.thenApplyAsync(path -> this.readPublishedTile(state, key, (Path)path), (Executor)this.executor));
    }

    public FairTileRequestQueue.OfferResult enqueue(UUID playerId, long session, int level, int x, int y) {
        ActiveState state = this.active;
        if (playerId == null || state == null || state.descriptor.session() != session || !state.layout.isValid(level, x, y)) {
            return FairTileRequestQueue.OfferResult.GLOBAL_FULL;
        }
        TacticalMapTileKey key = new TacticalMapTileKey(session, level, x, y);
        FairTileRequestQueue.OfferResult result = this.sendQueue.offer(playerId, key);
        if (result == FairTileRequestQueue.OfferResult.ACCEPTED || result == FairTileRequestQueue.OfferResult.DUPLICATE) {
            this.waiters.computeIfAbsent(key, ignored -> ConcurrentHashMap.newKeySet()).add(playerId);
            this.request(session, level, x, y);
        }
        return result;
    }

    public void updatePlayerViewport(UUID playerId, double minX, double minY, double maxX, double maxY, int screenWidth, int screenHeight) {
        if (playerId == null) {
            return;
        }
        this.playerViewports.put(playerId, new ViewportHint(TacticalMapTileService.clamp01(minX), TacticalMapTileService.clamp01(minY), TacticalMapTileService.clamp01(maxX), TacticalMapTileService.clamp01(maxY), Math.max(1, screenWidth), Math.max(1, screenHeight)));
        this.transferLimiter.grantFirstGlance(playerId, System.currentTimeMillis());
    }

    public FairTileRequestQueue.OfferResult enqueuePreviewOnce(UUID playerId) {
        ActiveState state = this.active;
        if (playerId == null || state == null) {
            return FairTileRequestQueue.OfferResult.GLOBAL_FULL;
        }
        long now = System.currentTimeMillis();
        Long previous = this.lastPreviewEnqueueAt.get(playerId);
        if (previous != null && now - previous < 8000L) {
            return FairTileRequestQueue.OfferResult.DUPLICATE;
        }
        FairTileRequestQueue.OfferResult result = this.enqueue(playerId, state.descriptor.session(), state.descriptor.maxLevel(), 0, 0);
        if (result == FairTileRequestQueue.OfferResult.ACCEPTED || result == FairTileRequestQueue.OfferResult.DUPLICATE) {
            this.lastPreviewEnqueueAt.put(playerId, now);
        }
        return result;
    }

    public void enqueueViewport(UUID playerId) {
        ViewportHint view;
        ActiveState state = this.active;
        ViewportHint viewportHint = view = playerId == null ? null : this.playerViewports.get(playerId);
        if (playerId == null || state == null || view == null) {
            this.enqueuePreviewOnce(playerId);
            return;
        }
        TacticalMapPyramidLayout layout = state.layout;
        int target = layout.chooseLevel(Math.max(0.0, view.maxX - view.minX), Math.max(0.0, view.maxY - view.minY), view.screenWidth, view.screenHeight);
        int arrival = TacticalMapLodPlanner.arrivalLevel(layout, target);
        this.enqueuePreviewOnce(playerId);
        if (arrival > target) {
            this.enqueueTiles(playerId, state, layout.visibleTiles(arrival, view.minX, view.minY, view.maxX, view.maxY, 0));
        }
        this.enqueueTiles(playerId, state, layout.visibleTiles(target, view.minX, view.minY, view.maxX, view.maxY, 0));
    }

    private void enqueueTiles(UUID playerId, ActiveState state, List<TacticalMapPyramidLayout.TileCoordinate> tiles) {
        for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
            this.enqueue(playerId, state.descriptor.session(), tile.level(), tile.x(), tile.y());
        }
    }

    public void tick(MinecraftServer server) {
        if (server == null) {
            return;
        }
        int attempts = Math.min(64, this.sendQueue.size());
        for (int index = 0; index < attempts; ++index) {
            byte[] bytes;
            FairTileRequestQueue.Entry<TacticalMapTileKey> entry = this.sendQueue.poll(this::pickSendKey);
            if (entry == null) {
                return;
            }
            TacticalMapTileKey key = entry.key();
            ActiveState state = this.active;
            ServerPlayer player = server.m_6846_().m_11259_(entry.playerId());
            if (state == null || state.descriptor.session() != key.session() || player == null) {
                this.removeWaiter(entry.playerId(), key);
                continue;
            }
            CompletableFuture<byte[]> future = this.request(key.session(), key.level(), key.x(), key.y());
            if (!future.isDone()) {
                this.sendQueue.defer(entry);
                continue;
            }
            try {
                bytes = future.join();
            }
            catch (CancellationException | CompletionException error) {
                this.removeWaiter(entry.playerId(), key);
                continue;
            }
            if (!this.allowTransfer(entry.playerId(), bytes.length)) {
                this.sendQueue.defer(entry);
                continue;
            }
            SyncTacticalMapTileMessage.sendToPlayer(player, key.session(), key.level(), key.x(), key.y(), bytes);
            if (key.level() == state.descriptor.maxLevel() && key.x() == 0 && key.y() == 0) {
                ESPointsMod.LOGGER.info("\u53d1\u9001\u6218\u672f\u5730\u56fe\u9884\u89c8\u74e6\u7247 {} -> {} ({} bytes)", (Object)key, (Object)player.m_36316_().getName(), (Object)bytes.length);
            } else {
                ESPointsMod.LOGGER.debug("\u53d1\u9001\u6218\u672f\u5730\u56fe\u74e6\u7247 {} -> {} ({} bytes)", (Object)key, (Object)player.m_36316_().getName(), (Object)bytes.length);
            }
            this.removeWaiter(entry.playerId(), key);
        }
    }

    public void removePlayer(UUID playerId) {
        if (playerId == null) {
            return;
        }
        this.sendQueue.removePlayer(playerId);
        this.transferLimiter.removePlayer(playerId);
        this.playerViewports.remove(playerId);
        this.lastPreviewEnqueueAt.remove(playerId);
        for (Map.Entry<TacticalMapTileKey, Set<UUID>> entry : this.waiters.entrySet()) {
            entry.getValue().remove(playerId);
            if (!entry.getValue().isEmpty()) continue;
            this.waiters.remove(entry.getKey(), entry.getValue());
        }
    }

    public boolean allowTransfer(UUID playerId, int bytes) {
        long playerBudget = (long)((Integer)ModConfig.tacticalMapPlayerTransferKiBps.get()).intValue() * 1024L;
        long globalBudget = (long)((Integer)ModConfig.tacticalMapGlobalTransferKiBps.get()).intValue() * 1024L;
        return this.transferLimiter.allow(playerId, bytes, System.currentTimeMillis(), playerBudget, globalBudget);
    }

    public int tileWidth(int level, int x) {
        ActiveState state = this.active;
        return state == null ? 0 : state.layout.tileWidth(level, x);
    }

    public int tileHeight(int level, int y) {
        ActiveState state = this.active;
        return state == null ? 0 : state.layout.tileHeight(level, y);
    }

    int pendingSendCount() {
        return this.sendQueue.size();
    }

    int waiterCount(TacticalMapTileKey key) {
        Set<UUID> values = this.waiters.get(key);
        return values == null ? 0 : values.size();
    }

    private void removeWaiter(UUID playerId, TacticalMapTileKey key) {
        Set<UUID> values = this.waiters.get(key);
        if (values != null) {
            values.remove(playerId);
            if (values.isEmpty()) {
                this.waiters.remove(key, values);
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private byte[] readPublishedTile(ActiveState state, TacticalMapTileKey key, Path path) {
        if (!this.isCurrent(state)) {
            throw new IllegalStateException("Stale tactical map session");
        }
        try {
            byte[] bytes = Files.readAllBytes(path);
            TacticalMapTileService.validateEncodedBytes(bytes);
            if (!this.isCurrent(state)) {
                throw new IOException("Tactical map generation changed while reading");
            }
            TacticalMapTileService tacticalMapTileService = this;
            synchronized (tacticalMapTileService) {
                if (!this.isCurrent(state)) {
                    throw new IOException("Tactical map generation changed before caching");
                }
                this.memory.put(key, bytes);
            }
            return bytes;
        }
        catch (IOException error) {
            throw new IllegalStateException("Unable to read tactical tile", error);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void buildPyramid(ActiveState state, Path root) {
        Image source = null;
        boolean sourcePublished = false;
        try {
            Files.createDirectories(state.mapDirectory, new FileAttribute[0]);
            if (this.loadCompleteCache(state)) {
                state.finishCacheCheck(true);
                state.sourceBytes = new byte[0];
                TacticalMapTileService.touchAndPrune(state, root);
                state.manifestReady.complete(state.mapDirectory.resolve(COMPLETE_MANIFEST));
                ESPointsMod.LOGGER.info("\u6218\u672f\u5730\u56fe\u74e6\u7247\u7f13\u5b58\u547d\u4e2d: session={} tiles={} dir={}", (Object)state.descriptor.session(), (Object)TacticalMapTileService.orderedKeys(state).size(), (Object)state.mapDirectory.getFileName());
                return;
            }
            source = ImageIO.read(new ByteArrayInputStream(state.sourceBytes));
            state.sourceBytes = new byte[0];
            if (source == null || ((BufferedImage)source).getWidth() != state.layout.width() || ((BufferedImage)source).getHeight() != state.layout.height()) {
                throw new IOException("Decoded tactical map dimensions do not match descriptor");
            }
            state.publishSource((BufferedImage)source);
            sourcePublished = true;
            this.writePreviewTile(state, (BufferedImage)source);
            for (TacticalMapTileKey demanded : state.finishCacheCheck(false)) {
                this.startDemandBuild(state, demanded);
            }
            for (int level = state.layout.maxLevel(); level >= 0; --level) {
                this.requireCurrent(state);
                Image scaled = level == 0 ? source : TacticalMapImageScaler.scale((BufferedImage)source, state.layout.levelWidth(level), state.layout.levelHeight(level));
                try {
                    this.writeLevel(state, (BufferedImage)scaled, level);
                    continue;
                }
                finally {
                    if (scaled != source) {
                        scaled.flush();
                    }
                }
            }
            this.requireAllTilesReady(state);
            this.publishManifest(state);
            TacticalMapTileService.touchAndPrune(state, root);
        }
        catch (Throwable error) {
            RuntimeException runtimeException;
            if (!state.cancelled) {
                ESPointsMod.LOGGER.error("\u6218\u672f\u5730\u56fe\u74e6\u7247\u91d1\u5b57\u5854\u751f\u6210\u5931\u8d25: {}", (Object)state.descriptor.imagePath(), (Object)error);
            }
            state.failAll(error);
            if (error instanceof RuntimeException) {
                RuntimeException runtime = (RuntimeException)error;
                runtimeException = runtime;
            } else {
                runtimeException = new IllegalStateException(error);
            }
            throw runtimeException;
        }
        finally {
            state.sourceBytes = new byte[0];
            if (!sourcePublished && source != null) {
                source.flush();
            }
            state.releaseImagesWhenIdle();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writePreviewTile(ActiveState state, BufferedImage source) throws IOException {
        int previewLevel = state.layout.maxLevel();
        TacticalMapTileKey preview = new TacticalMapTileKey(state.descriptor.session(), previewLevel, 0, 0);
        if (state.isReady(preview) || !state.claimOwner(preview)) {
            return;
        }
        try {
            this.writeOwnedTile(state, state.demandLevelImage(previewLevel, source), preview);
            ESPointsMod.LOGGER.info("\u6218\u672f\u5730\u56fe\u9884\u89c8\u74e6\u7247\u5df2\u5c31\u7eea: {}", (Object)preview);
        }
        finally {
            state.releaseOwner(preview);
        }
    }

    private void startDemandBuild(ActiveState state, TacticalMapTileKey key) {
        if (!this.isCurrent(state) || state.isReady(key) || !state.claimOwner(key)) {
            return;
        }
        try {
            this.executor.execute(() -> this.buildDemandTile(state, key));
        }
        catch (RuntimeException error) {
            state.releaseOwner(key);
            state.failAll(error);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void buildDemandTile(ActiveState state, TacticalMapTileKey key) {
        BufferedImage source = null;
        try {
            this.requireCurrent(state);
            source = state.acquireSource();
            BufferedImage levelImage = state.demandLevelImage(key.level(), source);
            this.writeOwnedTile(state, levelImage, key);
        }
        catch (Throwable error) {
            state.failAll(error);
            if (!state.cancelled) {
                ESPointsMod.LOGGER.error("\u6309\u9700\u6218\u672f\u5730\u56fe\u74e6\u7247\u751f\u6210\u5931\u8d25: {}", (Object)key, (Object)error);
            }
        }
        finally {
            if (source != null) {
                state.releaseSource();
            }
            state.releaseOwner(key);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private boolean loadCompleteCache(ActiveState state) throws IOException {
        Path manifest = state.mapDirectory.resolve(COMPLETE_MANIFEST);
        if (!Files.isRegularFile(manifest, new LinkOption[0])) {
            return false;
        }
        String expected = TacticalMapTileService.manifestText(state);
        if (!expected.equals(Files.readString(manifest, StandardCharsets.UTF_8))) {
            Files.deleteIfExists(manifest);
            return false;
        }
        for (TacticalMapTileKey key : TacticalMapTileService.orderedKeys(state)) {
            this.requireCurrent(state);
            if (!state.claimOwner(key)) {
                throw new IOException("Duplicate tactical cache validator: " + String.valueOf(key));
            }
            Path tile = TacticalMapTileService.tilePath(state.mapDirectory, key.level(), key.x(), key.y());
            try {
                try {
                    TacticalMapTileService.validateTileFile(state, key, tile);
                }
                catch (IOException corrupt) {
                    Files.deleteIfExists(manifest);
                    Files.deleteIfExists(tile);
                    boolean bl = false;
                    state.releaseOwner(key);
                    return bl;
                }
                state.publishReady(key, tile);
            }
            finally {
                state.releaseOwner(key);
            }
        }
        return true;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeLevel(ActiveState state, BufferedImage image, int level) throws IOException {
        for (int y = 0; y < state.layout.rows(level); ++y) {
            for (int x = 0; x < state.layout.columns(level); ++x) {
                this.requireCurrent(state);
                TacticalMapTileKey key = new TacticalMapTileKey(state.descriptor.session(), level, x, y);
                if (state.isReady(key) || !state.claimOwner(key)) continue;
                try {
                    this.writeOwnedTile(state, image, key);
                    continue;
                }
                finally {
                    state.releaseOwner(key);
                }
            }
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void writeOwnedTile(ActiveState state, BufferedImage levelImage, TacticalMapTileKey key) throws IOException {
        this.requireCurrent(state);
        Path target = TacticalMapTileService.tilePath(state.mapDirectory, key.level(), key.x(), key.y());
        Files.createDirectories(target.getParent(), new FileAttribute[0]);
        if (Files.isRegularFile(target, new LinkOption[0])) {
            try {
                TacticalMapTileService.validateTileFile(state, key, target);
                this.requireCurrent(state);
                state.publishReady(key, target);
                return;
            }
            catch (IOException iOException) {
                // empty catch block
            }
        }
        int width = state.layout.tileWidth(key.level(), key.x());
        int height = state.layout.tileHeight(key.level(), key.y());
        BufferedImage tile = levelImage.getSubimage(key.x() * 512, key.y() * 512, width, height);
        Path temporary = TacticalMapTileService.uniqueTemporary(target, state.generation);
        try {
            if (!ImageIO.write((RenderedImage)tile, "PNG", temporary.toFile())) {
                throw new IOException("No PNG writer available");
            }
            TacticalMapTileService.validateTileFile(state, key, temporary);
            this.publishFileIfCurrent(state, temporary, target);
            TacticalMapTileService.validateTileFile(state, key, target);
            this.requireCurrent(state);
            state.publishReady(key, target);
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void publishManifest(ActiveState state) throws IOException {
        Path manifest = state.mapDirectory.resolve(COMPLETE_MANIFEST);
        Path temporary = TacticalMapTileService.uniqueTemporary(manifest, state.generation);
        try {
            Files.writeString(temporary, (CharSequence)TacticalMapTileService.manifestText(state), StandardCharsets.UTF_8, new OpenOption[0]);
            this.publishFileIfCurrent(state, temporary, manifest);
            if (!TacticalMapTileService.manifestText(state).equals(Files.readString(manifest, StandardCharsets.UTF_8))) {
                throw new IOException("Published tactical map manifest is inconsistent");
            }
            this.requireCurrent(state);
            state.manifestReady.complete(manifest);
        }
        finally {
            Files.deleteIfExists(temporary);
        }
    }

    private synchronized void publishFileIfCurrent(ActiveState state, Path temporary, Path target) throws IOException {
        this.requireCurrent(state);
        try {
            Files.move(temporary, target, StandardCopyOption.ATOMIC_MOVE, StandardCopyOption.REPLACE_EXISTING);
        }
        catch (AtomicMoveNotSupportedException ignored) {
            Files.move(temporary, target, StandardCopyOption.REPLACE_EXISTING);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void requireAllTilesReady(ActiveState state) throws IOException {
        for (TacticalMapTileKey key : TacticalMapTileService.orderedKeys(state)) {
            CompletableFuture<Path> ready = state.readiness(key);
            if (!ready.isDone() && state.claimOwner(key)) {
                try {
                    BufferedImage source = state.acquireSource();
                    try {
                        this.writeOwnedTile(state, state.demandLevelImage(key.level(), source), key);
                    }
                    finally {
                        state.releaseSource();
                    }
                }
                finally {
                    state.releaseOwner(key);
                }
            }
            try {
                TacticalMapTileService.validateTileFile(state, key, ready.join());
            }
            catch (CompletionException error) {
                throw new IOException("Tile failed before manifest publication: " + String.valueOf(key), error);
            }
        }
    }

    private static void validateTileFile(ActiveState state, TacticalMapTileKey key, Path path) throws IOException {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            throw new IOException("Missing tactical map tile: " + String.valueOf(path));
        }
        long size = Files.size(path);
        if (size < (long)PNG_SIGNATURE.length || size > 0x200000L) {
            throw new IOException("Encoded tactical tile size is invalid");
        }
        byte[] signature = new byte[PNG_SIGNATURE.length];
        try (InputStream input = Files.newInputStream(path, new OpenOption[0]);){
            if (input.read(signature) != signature.length) {
                throw new IOException("Truncated tactical map PNG");
            }
        }
        TacticalMapTileService.validateEncodedBytes(signature);
        BufferedImage decoded = ImageIO.read(path.toFile());
        if (decoded == null || decoded.getWidth() != state.layout.tileWidth(key.level(), key.x()) || decoded.getHeight() != state.layout.tileHeight(key.level(), key.y())) {
            if (decoded != null) {
                decoded.flush();
            }
            throw new IOException("Tactical map PNG dimensions are invalid");
        }
        decoded.flush();
    }

    static void validateEncodedBytes(byte[] bytes) throws IOException {
        if (bytes == null || bytes.length < PNG_SIGNATURE.length || bytes.length > 0x200000) {
            throw new IOException("Encoded tactical tile size is invalid");
        }
        for (int index = 0; index < PNG_SIGNATURE.length; ++index) {
            if (bytes[index] == PNG_SIGNATURE[index]) continue;
            throw new IOException("Encoded tactical tile is not a PNG");
        }
    }

    private static List<TacticalMapTileKey> orderedKeys(ActiveState state) {
        ArrayList<TacticalMapTileKey> keys = new ArrayList<TacticalMapTileKey>();
        for (int level = state.layout.maxLevel(); level >= 0; --level) {
            for (int y = 0; y < state.layout.rows(level); ++y) {
                for (int x = 0; x < state.layout.columns(level); ++x) {
                    keys.add(new TacticalMapTileKey(state.descriptor.session(), level, x, y));
                }
            }
        }
        return keys;
    }

    private static String manifestText(ActiveState state) {
        StringBuilder text = new StringBuilder(256);
        text.append("version=").append(PYRAMID_CACHE_VERSION).append('\n').append("sha256=").append(state.descriptor.sha256()).append('\n').append("width=").append(state.layout.width()).append('\n').append("height=").append(state.layout.height()).append('\n').append("tileSize=").append(512).append('\n').append("maxLevel=").append(state.layout.maxLevel()).append('\n');
        List<TacticalMapTileKey> keys = TacticalMapTileService.orderedKeys(state);
        text.append("tiles=").append(keys.size()).append('\n');
        for (TacticalMapTileKey key : keys) {
            text.append(key.level()).append(',').append(key.x()).append(',').append(key.y()).append(',').append(state.layout.tileWidth(key.level(), key.x())).append(',').append(state.layout.tileHeight(key.level(), key.y())).append('\n');
        }
        return text.toString();
    }

    static String cacheDirectoryName(String sha256) {
        return sha256 + "-p3";
    }

    public static Path localPreviewFile(String sha256, int maxLevel) {
        return TacticalMapTileService.localTileFile(sha256, maxLevel, 0, 0);
    }

    public static Path localTileFile(String sha256, int level, int x, int y) {
        return FMLPaths.CONFIGDIR.get().resolve("espoints").resolve("cache").resolve("tactical-map").resolve(TacticalMapTileService.cacheDirectoryName(sha256)).resolve("l" + level).resolve(x + "_" + y + ".png");
    }

    private static Path tilePath(Path mapDirectory, int level, int x, int y) {
        return mapDirectory.resolve("l" + level).resolve(x + "_" + y + ".png");
    }

    private static Path uniqueTemporary(Path target, long generation) {
        return target.resolveSibling(String.valueOf(target.getFileName()) + ".tmp." + generation + "." + String.valueOf(UUID.randomUUID()));
    }

    private boolean isCurrent(ActiveState state) {
        return this.active == state && !state.cancelled && !state.failed;
    }

    private void requireCurrent(ActiveState state) throws IOException {
        if (!this.isCurrent(state)) {
            throw new IOException("Stale tactical map generation");
        }
    }

    private static void touchAndPrune(ActiveState state, Path root) throws IOException {
        Files.setLastModifiedTime(state.mapDirectory, FileTime.fromMillis(System.currentTimeMillis()));
        TacticalMapTileService.pruneDiskCache(root, (long)((Integer)ModConfig.tacticalMapDiskCacheMiB.get()).intValue() * 1024L * 1024L, state.mapDirectory);
    }

    private static void pruneDiskCache(Path root, long budget, Path keep) throws IOException {
        List<Path> directories;
        List<Path> files;
        if (!Files.isDirectory(root, new LinkOption[0])) {
            return;
        }
        try (Stream<Path> stream = Files.walk(root, new FileVisitOption[0]);){
            files = stream.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).toList();
        }
        long total = 0L;
        for (Path file : files) {
            total += Files.size(file);
        }
        if (total <= budget) {
            return;
        }
        try (Stream<Path> stream = Files.list(root);){
            directories = stream.filter(x$0 -> Files.isDirectory(x$0, new LinkOption[0])).filter(path -> !path.equals(keep)).sorted(Comparator.comparingLong(TacticalMapTileService::lastModified)).toList();
        }
        for (Path directory : directories) {
            if (total <= budget) break;
            long removed = TacticalMapTileService.directorySize(directory);
            TacticalMapTileService.deleteDirectory(directory);
            total -= removed;
        }
    }

    private static long directorySize(Path directory) throws IOException {
        try (Stream<Path> stream = Files.walk(directory, new FileVisitOption[0]);){
            long l = stream.filter(x$0 -> Files.isRegularFile(x$0, new LinkOption[0])).mapToLong(path -> {
                try {
                    return Files.size(path);
                }
                catch (IOException ignored) {
                    return 0L;
                }
            }).sum();
            return l;
        }
    }

    private static void deleteDirectory(Path directory) throws IOException {
        ArrayList<Path> paths;
        try (Stream<Path> stream = Files.walk(directory, new FileVisitOption[0]);){
            paths = new ArrayList<Path>(stream.sorted(Comparator.reverseOrder()).toList());
        }
        for (Path path : paths) {
            Files.deleteIfExists(path);
        }
    }

    private static long lastModified(Path path) {
        try {
            return Files.getLastModifiedTime(path, new LinkOption[0]).toMillis();
        }
        catch (IOException ignored) {
            return Long.MIN_VALUE;
        }
    }

    private TacticalMapTileKey pickSendKey(UUID playerId, Set<TacticalMapTileKey> pending) {
        if (pending == null || pending.isEmpty()) {
            return null;
        }
        int coarsest = Integer.MIN_VALUE;
        for (TacticalMapTileKey tacticalMapTileKey : pending) {
            coarsest = Math.max(coarsest, tacticalMapTileKey.level());
        }
        ArrayList<TacticalMapTileKey> same = new ArrayList<TacticalMapTileKey>();
        for (TacticalMapTileKey key : pending) {
            if (key.level() != coarsest) continue;
            same.add(key);
        }
        if (same.size() == 1) {
            return (TacticalMapTileKey)same.get(0);
        }
        ViewportHint viewportHint = this.playerViewports.get(playerId);
        ActiveState state = this.active;
        if (viewportHint == null || state == null) {
            return (TacticalMapTileKey)same.get(0);
        }
        try {
            LinkedKDTree tree = KDTree.newLinkedKDTree((int)2);
            for (TacticalMapTileKey key : same) {
                double[] center = TacticalMapTileService.tileCenter(state.layout, key);
                tree.insert(KDTree.BuildNode.of((Object)key, (IMultidimensional)new DoublePosition(new double[]{center[0], center[1]})));
            }
            KDTree.KDNode closest = tree.findClosest((IMultidimensional)new DoublePosition(new double[]{(viewportHint.minX + viewportHint.maxX) * 0.5, (viewportHint.minY + viewportHint.maxY) * 0.5}));
            return closest == null ? (TacticalMapTileKey)same.get(0) : (TacticalMapTileKey)closest.other();
        }
        catch (RuntimeException ignored) {
            return (TacticalMapTileKey)same.get(0);
        }
    }

    private static double[] tileCenter(TacticalMapPyramidLayout layout, TacticalMapTileKey key) {
        double width = layout.levelWidth(key.level());
        double height = layout.levelHeight(key.level());
        return new double[]{((double)key.x() * 512.0 + (double)layout.tileWidth(key.level(), key.x()) * 0.5) / width, ((double)key.y() * 512.0 + (double)layout.tileHeight(key.level(), key.y()) * 0.5) / height};
    }

    private static double clamp01(double value) {
        if (!Double.isFinite(value)) {
            return 0.0;
        }
        return Math.max(0.0, Math.min(1.0, value));
    }

    private static final class ActiveState {
        private final long generation;
        private final Descriptor descriptor;
        private final TacticalMapPyramidLayout layout;
        private final Path mapDirectory;
        private final ProgressiveTileReadiness<TacticalMapTileKey, Path> readiness;
        private final CompletableFuture<Path> manifestReady = new CompletableFuture();
        private final Set<TacticalMapTileKey> demandOrder = new LinkedHashSet<TacticalMapTileKey>();
        private final Map<Integer, CompletableFuture<BufferedImage>> demandLevelImages = new ConcurrentHashMap<Integer, CompletableFuture<BufferedImage>>();
        private volatile byte[] sourceBytes;
        private volatile boolean cancelled;
        private volatile boolean failed;
        private boolean cacheCheckComplete;
        private boolean cacheHit;
        private BufferedImage sourceImage;
        private int sourceUsers;
        private boolean releaseImagesRequested;
        private CompletableFuture<Void> build = CompletableFuture.completedFuture(null);

        private ActiveState(long generation, Descriptor descriptor, TacticalMapPyramidLayout layout, Path mapDirectory, byte[] sourceBytes) {
            this.generation = generation;
            this.descriptor = descriptor;
            this.layout = layout;
            this.mapDirectory = mapDirectory;
            this.sourceBytes = sourceBytes == null ? new byte[]{} : sourceBytes;
            this.readiness = new ProgressiveTileReadiness(generation);
        }

        private CompletableFuture<Path> readiness(TacticalMapTileKey key) {
            return this.readiness.future(key);
        }

        private synchronized boolean registerDemand(TacticalMapTileKey key) {
            if (this.cancelled || this.failed || this.readiness.isReady(key) || !this.demandOrder.add(key)) {
                return false;
            }
            return this.cacheCheckComplete && !this.cacheHit;
        }

        private synchronized List<TacticalMapTileKey> finishCacheCheck(boolean hit) {
            this.cacheCheckComplete = true;
            this.cacheHit = hit;
            return hit ? List.of() : List.copyOf(this.demandOrder);
        }

        private synchronized void publishSource(BufferedImage source) {
            if (this.cancelled || this.failed || source == null || this.releaseImagesRequested) {
                throw new IllegalStateException("Cannot publish tactical map source");
            }
            this.sourceImage = source;
        }

        private synchronized BufferedImage acquireSource() {
            if (this.cancelled || this.failed || this.releaseImagesRequested || this.sourceImage == null) {
                throw new IllegalStateException("Tactical map source is unavailable");
            }
            ++this.sourceUsers;
            return this.sourceImage;
        }

        private synchronized void releaseSource() {
            if (this.sourceUsers <= 0) {
                throw new IllegalStateException("Unbalanced tactical map source release");
            }
            --this.sourceUsers;
            this.releaseImagesIfIdle();
        }

        private BufferedImage demandLevelImage(int level, BufferedImage source) {
            CompletableFuture<BufferedImage> selected;
            if (level == 0) {
                return source;
            }
            CompletableFuture<BufferedImage> created = new CompletableFuture<BufferedImage>();
            CompletableFuture existing = this.demandLevelImages.putIfAbsent(level, created);
            CompletableFuture<BufferedImage> completableFuture = selected = existing == null ? created : existing;
            if (existing == null) {
                try {
                    created.complete(TacticalMapImageScaler.scale(source, this.layout.levelWidth(level), this.layout.levelHeight(level)));
                }
                catch (Throwable error) {
                    created.completeExceptionally(error);
                }
            }
            return (BufferedImage)selected.join();
        }

        private synchronized void releaseImagesWhenIdle() {
            this.releaseImagesRequested = true;
            this.releaseImagesIfIdle();
        }

        private void releaseImagesIfIdle() {
            if (!this.releaseImagesRequested || this.sourceUsers != 0) {
                return;
            }
            if (this.sourceImage != null) {
                this.sourceImage.flush();
                this.sourceImage = null;
            }
            for (CompletableFuture<BufferedImage> imageFuture : this.demandLevelImages.values()) {
                if (!imageFuture.isDone() || imageFuture.isCompletedExceptionally()) continue;
                imageFuture.join().flush();
            }
            this.demandLevelImages.clear();
        }

        private void publishReady(TacticalMapTileKey key, Path path) {
            if (!this.cancelled && !this.failed && this.readiness.publish(this.generation, key, path)) {
                return;
            }
            if (!this.cancelled && !this.failed) {
                throw new IllegalStateException("Tile readiness was not owned: " + String.valueOf(key));
            }
            throw new IllegalStateException("Tactical map generation is no longer active");
        }

        private boolean claimOwner(TacticalMapTileKey key) {
            return this.readiness.claim(this.generation, key);
        }

        private void releaseOwner(TacticalMapTileKey key) {
            this.readiness.release(key);
        }

        private boolean isReady(TacticalMapTileKey key) {
            return this.readiness.isReady(key);
        }

        private void cancel() {
            this.cancelled = true;
            this.build.cancel(true);
            this.failAll(new IllegalStateException("Tactical map generation cancelled"));
            this.sourceBytes = new byte[0];
            this.releaseImagesWhenIdle();
        }

        private void failAll(Throwable error) {
            this.failed = true;
            this.readiness.fail(error);
            this.manifestReady.completeExceptionally(error);
        }
    }

    public record Descriptor(long session, String imagePath, String sha256, int width, int height, int tileSize, int maxLevel) {
        public static final Descriptor EMPTY = new Descriptor(0L, "", "", 0, 0, 512, 0);

        public boolean present() {
            return this.session > 0L && !this.sha256.isBlank() && this.width > 0 && this.height > 0;
        }
    }

    private record ViewportHint(double minX, double minY, double maxX, double maxY, int screenWidth, int screenHeight) {
    }
}

