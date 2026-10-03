/*
 * Decompiled with CFR 0.152.
 */
package com.example.espoints.tile;

import com.example.espoints.tile.TacticalMapLodPlanner;
import com.example.espoints.tile.TacticalMapPyramidLayout;
import java.util.List;
import java.util.function.Predicate;

public final class TacticalMapLayerPicker {
    private TacticalMapLayerPicker() {
    }

    public static TacticalMapLodPlanner.Layer finestCovering(TacticalMapPyramidLayout layout, List<TacticalMapLodPlanner.Layer> layers, Predicate<List<TacticalMapPyramidLayout.TileCoordinate>> ready, double minX, double minY, double maxX, double maxY) {
        if (layout == null || layers == null || ready == null) {
            return null;
        }
        TacticalMapLodPlanner.Layer finest = null;
        for (TacticalMapLodPlanner.Layer layer : layers) {
            if (layer == null || layer.visibleTiles() == null || layer.visibleTiles().isEmpty() || !ready.test(layer.visibleTiles()) || !layout.tilesCover(layer.level(), layer.visibleTiles(), minX, minY, maxX, maxY)) continue;
            finest = layer;
        }
        return finest;
    }
}

