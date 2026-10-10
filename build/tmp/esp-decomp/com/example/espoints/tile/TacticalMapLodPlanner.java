/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import com.example.espoints.config.MapImageQuality;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TacticalMapLodPlanner {
    public static final long STABLE_VIEW_MILLIS = 80L;
    static final long INACTIVE_VIEW_MILLIS = 1000L;
    private static final int BUDGET_DENOMINATOR = 4;
    private Viewport previousViewport;
    private long stableSince;
    private long lastPlanAt = Long.MIN_VALUE;
    private CacheKey cachedKey;
    private Plan cachedPlan;
    private long rebuildCount;

    public Plan plan(TacticalMapPyramidLayout layout, MapImageQuality quality, Viewport viewport, long nowMillis, long textureBudgetBytes, TileStateLookup states) {
        if (layout == null || quality == null || viewport == null || states == null) {
            throw new IllegalArgumentException("LOD plan arguments must be present");
        }
        this.updateStability(viewport, nowMillis);
        return this.computePlan(layout, quality, viewport, nowMillis, textureBudgetBytes, states);
    }

    public Plan planCached(TacticalMapPyramidLayout layout, MapImageQuality quality, Viewport viewport, long nowMillis, long textureBudgetBytes, long descriptorSession, long readinessRevision, TileStateLookup states) {
        if (layout == null || quality == null || viewport == null || states == null) {
            throw new IllegalArgumentException("LOD plan arguments must be present");
        }
        this.updateStability(viewport, nowMillis);
        boolean stable = this.isStable(nowMillis);
        int baseLevel = TacticalMapLodPlanner.chooseBudgetedBaseLevel(layout, viewport, textureBudgetBytes);
        List<TacticalMapPyramidLayout.TileCoordinate> visible = TacticalMapLodPlanner.visibleTiles(layout, viewport, baseLevel);
        TacticalMapPyramidLayout.TileCoordinate first = visible.get(0);
        TacticalMapPyramidLayout.TileCoordinate last = visible.get(visible.size() - 1);
        CacheKey key = new CacheKey(descriptorSession, layout.width(), layout.height(), baseLevel, first.x(), first.y(), last.x(), last.y(), viewport.screenWidth(), viewport.screenHeight(), quality, textureBudgetBytes, readinessRevision, stable);
        if (key.equals(this.cachedKey) && this.cachedPlan != null) {
            return this.cachedPlan;
        }
        this.cachedPlan = this.computePlan(layout, quality, viewport, nowMillis, textureBudgetBytes, states);
        this.cachedKey = key;
        ++this.rebuildCount;
        return this.cachedPlan;
    }

    private Plan computePlan(TacticalMapPyramidLayout layout, MapImageQuality quality, Viewport viewport, long nowMillis, long textureBudgetBytes, TileStateLookup states) {
        int baseLevel = TacticalMapLodPlanner.chooseBudgetedBaseLevel(layout, viewport, textureBudgetBytes);
        int arrivalLevel = TacticalMapLodPlanner.arrivalLevel(layout, baseLevel);
        long refinementBudget = TacticalMapLodPlanner.safeFraction(textureBudgetBytes);
        List<TacticalMapPyramidLayout.TileCoordinate> arrivalVisible = arrivalLevel > baseLevel ? TacticalMapLodPlanner.visibleTiles(layout, viewport, arrivalLevel) : List.of();
        List<TacticalMapPyramidLayout.TileCoordinate> baseVisible = TacticalMapLodPlanner.visibleTiles(layout, viewport, baseLevel);
        List<TacticalMapPyramidLayout.TileCoordinate> basePrefetch = TacticalMapLodPlanner.prefetchOnly(layout, viewport, baseLevel, baseVisible);
        ArrayList<Layer> layers = new ArrayList<Layer>();
        if (!arrivalVisible.isEmpty()) {
            layers.add(new Layer(arrivalLevel, arrivalVisible));
        }
        layers.add(new Layer(baseLevel, baseVisible));
        ArrayList<TacticalMapPyramidLayout.TileCoordinate> visibleRequests = new ArrayList<TacticalMapPyramidLayout.TileCoordinate>(TacticalMapLodPlanner.missingTiles(arrivalVisible, states));
        visibleRequests.addAll(TacticalMapLodPlanner.missingTiles(baseVisible, states));
        boolean baseReady = TacticalMapLodPlanner.allReady(baseVisible, states);
        boolean stable = this.isStable(nowMillis);
        HashSet<TacticalMapPyramidLayout.TileCoordinate> budgetedTiles = new HashSet<TacticalMapPyramidLayout.TileCoordinate>(arrivalVisible);
        budgetedTiles.addAll(baseVisible);
        budgetedTiles.add(new TacticalMapPyramidLayout.TileCoordinate(layout.maxLevel(), 0, 0));
        long estimatedBytes = TacticalMapLodPlanner.rgbaBytes(layout, budgetedTiles);
        boolean previousLayerReady = baseReady;
        if (stable && baseReady) {
            int refinements = Math.min(quality.refinementLevels(), baseLevel);
            for (int offset = 1; offset <= refinements && previousLayerReady; ++offset) {
                int level = baseLevel - offset;
                List<TacticalMapPyramidLayout.TileCoordinate> visible = TacticalMapLodPlanner.visibleTiles(layout, viewport, level);
                HashSet<TacticalMapPyramidLayout.TileCoordinate> prospective = new HashSet<TacticalMapPyramidLayout.TileCoordinate>(budgetedTiles);
                prospective.addAll(visible);
                long prospectiveBytes = TacticalMapLodPlanner.rgbaBytes(layout, prospective);
                if (prospectiveBytes > refinementBudget) break;
                budgetedTiles = prospective;
                estimatedBytes = prospectiveBytes;
                layers.add(new Layer(level, visible));
                visibleRequests.addAll(TacticalMapLodPlanner.missingTiles(visible, states));
                previousLayerReady = TacticalMapLodPlanner.allReady(visible, states);
            }
        }
        ArrayList<TacticalMapPyramidLayout.TileCoordinate> requests = new ArrayList<TacticalMapPyramidLayout.TileCoordinate>(visibleRequests);
        requests.addAll(TacticalMapLodPlanner.missingTiles(basePrefetch, states));
        LinkedHashSet<TacticalMapPyramidLayout.TileCoordinate> desired = new LinkedHashSet<TacticalMapPyramidLayout.TileCoordinate>();
        for (Layer layer : layers) {
            desired.addAll(layer.visibleTiles());
        }
        desired.addAll(basePrefetch);
        return new Plan(baseLevel, List.copyOf(layers), List.copyOf(requests), List.copyOf(desired), stable, estimatedBytes);
    }

    public void reset() {
        this.previousViewport = null;
        this.stableSince = 0L;
        this.lastPlanAt = Long.MIN_VALUE;
        this.cachedKey = null;
        this.cachedPlan = null;
        this.rebuildCount = 0L;
    }

    public long rebuildCount() {
        return this.rebuildCount;
    }

    private boolean isStable(long nowMillis) {
        return nowMillis >= this.stableSince && nowMillis - this.stableSince >= 80L;
    }

    public static int arrivalLevel(TacticalMapPyramidLayout layout, int targetLevel) {
        if (layout == null || targetLevel < 0 || targetLevel >= layout.maxLevel()) {
            return targetLevel;
        }
        int arrival = Math.min(layout.maxLevel(), targetLevel + 2);
        return arrival >= layout.maxLevel() ? targetLevel : arrival;
    }

    private static int chooseBudgetedBaseLevel(TacticalMapPyramidLayout layout, Viewport viewport, long textureBudgetBytes) {
        int baseLevel;
        long refinementBudget = TacticalMapLodPlanner.safeFraction(textureBudgetBytes);
        for (baseLevel = layout.chooseLevel(viewport.visibleFractionX(), viewport.visibleFractionY(), viewport.screenWidth(), viewport.screenHeight()); baseLevel < layout.maxLevel() && TacticalMapLodPlanner.visibleLayerBytes(layout, viewport, baseLevel, true) > refinementBudget; ++baseLevel) {
        }
        return baseLevel;
    }

    private void updateStability(Viewport viewport, long nowMillis) {
        boolean inactive;
        boolean bl = inactive = this.lastPlanAt != Long.MIN_VALUE && (nowMillis < this.lastPlanAt || nowMillis - this.lastPlanAt > 1000L);
        if (this.previousViewport == null || !this.previousViewport.equals(viewport) || inactive) {
            this.previousViewport = viewport;
            this.stableSince = nowMillis;
        }
        this.lastPlanAt = nowMillis;
    }

    private static List<TacticalMapPyramidLayout.TileCoordinate> visibleTiles(TacticalMapPyramidLayout layout, Viewport viewport, int level) {
        return layout.visibleTiles(level, viewport.minX(), viewport.minY(), viewport.maxX(), viewport.maxY(), 0);
    }

    private static List<TacticalMapPyramidLayout.TileCoordinate> prefetchOnly(TacticalMapPyramidLayout layout, Viewport viewport, int level, List<TacticalMapPyramidLayout.TileCoordinate> visible) {
        HashSet<TacticalMapPyramidLayout.TileCoordinate> visibleSet = new HashSet<TacticalMapPyramidLayout.TileCoordinate>(visible);
        ArrayList<TacticalMapPyramidLayout.TileCoordinate> result = new ArrayList<TacticalMapPyramidLayout.TileCoordinate>();
        for (TacticalMapPyramidLayout.TileCoordinate tile : layout.visibleTiles(level, viewport.minX(), viewport.minY(), viewport.maxX(), viewport.maxY(), 1)) {
            if (visibleSet.contains(tile)) continue;
            result.add(tile);
        }
        return result;
    }

    private static List<TacticalMapPyramidLayout.TileCoordinate> missingTiles(List<TacticalMapPyramidLayout.TileCoordinate> tiles, TileStateLookup states) {
        ArrayList<TacticalMapPyramidLayout.TileCoordinate> result = new ArrayList<TacticalMapPyramidLayout.TileCoordinate>();
        for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
            if (states.state(tile) != TileState.MISSING) continue;
            result.add(tile);
        }
        return result;
    }

    private static boolean allReady(List<TacticalMapPyramidLayout.TileCoordinate> tiles, TileStateLookup states) {
        for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
            if (states.state(tile) == TileState.READY) continue;
            return false;
        }
        return true;
    }

    private static long visibleLayerBytes(TacticalMapPyramidLayout layout, Viewport viewport, int level, boolean includePreview) {
        LinkedHashSet<TacticalMapPyramidLayout.TileCoordinate> tiles = new LinkedHashSet<TacticalMapPyramidLayout.TileCoordinate>(TacticalMapLodPlanner.visibleTiles(layout, viewport, level));
        if (includePreview) {
            tiles.add(new TacticalMapPyramidLayout.TileCoordinate(layout.maxLevel(), 0, 0));
        }
        return TacticalMapLodPlanner.rgbaBytes(layout, tiles);
    }

    private static long rgbaBytes(TacticalMapPyramidLayout layout, Set<TacticalMapPyramidLayout.TileCoordinate> tiles) {
        long bytes = 0L;
        for (TacticalMapPyramidLayout.TileCoordinate tile : tiles) {
            bytes += (long)layout.tileWidth(tile.level(), tile.x()) * (long)layout.tileHeight(tile.level(), tile.y()) * 4L;
        }
        return bytes;
    }

    private static long safeFraction(long bytes) {
        if (bytes <= 0L) {
            return 0L;
        }
        return bytes - bytes / 4L;
    }

    public record Viewport(double minX, double minY, double maxX, double maxY, int screenWidth, int screenHeight) {
        public double visibleFractionX() {
            return Math.max(0.0, Math.min(1.0, this.maxX) - Math.max(0.0, Math.min(1.0, this.minX)));
        }

        public double visibleFractionY() {
            return Math.max(0.0, Math.min(1.0, this.maxY) - Math.max(0.0, Math.min(1.0, this.minY)));
        }
    }

    @FunctionalInterface
    public static interface TileStateLookup {
        public TileState state(TacticalMapPyramidLayout.TileCoordinate var1);
    }

    public record Plan(int baseLevel, List<Layer> layers, List<TacticalMapPyramidLayout.TileCoordinate> requests, List<TacticalMapPyramidLayout.TileCoordinate> desiredTiles, boolean stable, long estimatedRgbaBytes) {
    }

    private record CacheKey(long descriptorSession, int sourceWidth, int sourceHeight, int baseLevel, int minTileX, int minTileY, int maxTileX, int maxTileY, int screenWidth, int screenHeight, MapImageQuality quality, long textureBudgetBytes, long readinessRevision, boolean stable) {
    }

    public record Layer(int level, List<TacticalMapPyramidLayout.TileCoordinate> visibleTiles) {
    }

    public static enum TileState {
        MISSING,
        REQUESTED,
        READY;

    }
}

