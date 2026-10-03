/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonParseException
 */
package com.example.espoints.config;

import com.example.espoints.util.ModLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public final class TacticalMapJsonConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static TacticalMapJsonConfig instance;
    public int topLeftX = -512;
    public int topLeftZ = -512;
    public int bottomRightX = 512;
    public int bottomRightZ = 512;
    public int initialRange = 512;
    public int minimumRange = 64;
    public String backgroundImage = "";
    public int backgroundImageWidth = 0;
    public int backgroundImageHeight = 0;
    public boolean showGrid = true;
    public boolean showLabels = true;
    public int tacticalMarkerDurationSeconds = 10;
    public int tacticalMarkerFadeSeconds = 3;
    public int tacticalMarkerMaxRenderDistance = 128;
    private transient String source = "internal defaults";

    private TacticalMapJsonConfig() {
    }

    public static synchronized TacticalMapJsonConfig getInstance() {
        if (instance == null) {
            instance = TacticalMapJsonConfig.createDefault();
        }
        return instance;
    }

    public static TacticalMapJsonConfig createDefault() {
        return new TacticalMapJsonConfig();
    }

    public static TacticalMapJsonConfig fromJson(JsonElement json) {
        try {
            TacticalMapJsonConfig parsed = (TacticalMapJsonConfig)GSON.fromJson(json, TacticalMapJsonConfig.class);
            TacticalMapJsonConfig config = TacticalMapJsonConfig.createDefault();
            if (parsed != null) {
                config.copyFrom(parsed);
            }
            return config;
        }
        catch (JsonParseException e) {
            throw e;
        }
        catch (RuntimeException e) {
            throw new JsonParseException("Invalid tactical map config", (Throwable)e);
        }
    }

    public static synchronized void apply(TacticalMapJsonConfig loadedConfig, String source) {
        TacticalMapJsonConfig config = TacticalMapJsonConfig.getInstance();
        config.copyFrom(loadedConfig);
        config.source = source == null || source.isBlank() ? "unknown" : source;
        ModLogger.info("\u6218\u672f\u5730\u56feJSON\u914d\u7f6e\u5df2\u5e94\u7528: " + config.source);
    }

    public TacticalMapJsonConfig copy() {
        TacticalMapJsonConfig copy = TacticalMapJsonConfig.createDefault();
        copy.copyFrom(this);
        copy.source = this.source;
        return copy;
    }

    public void reloadIfChanged() {
    }

    public void loadConfig() {
        ModLogger.warn("\u6218\u672f\u5730\u56fe\u7531\u5f53\u524d Espetro \u5730\u56fe\u63d0\u4f9b\uff0c\u53ea\u80fd\u5728\u4e0b\u4e00\u5c40\u5207\u6362");
    }

    public void saveConfig() {
        ModLogger.warn("\u6d3b\u52a8\u5730\u56fe\u914d\u7f6e\u4e3a\u53ea\u8bfb\uff0c\u672a\u4fee\u6539 EsWorld \u6a21\u677f");
    }

    public String getSource() {
        return this.source;
    }

    public TacticalMapBounds getBounds() {
        double minX = Math.min(this.topLeftX, this.bottomRightX);
        double maxX = Math.max(this.topLeftX, this.bottomRightX);
        double minZ = Math.min(this.topLeftZ, this.bottomRightZ);
        double maxZ = Math.max(this.topLeftZ, this.bottomRightZ);
        if (maxX <= minX) {
            maxX = minX + 1.0;
        }
        if (maxZ <= minZ) {
            maxZ = minZ + 1.0;
        }
        return new TacticalMapBounds(minX, minZ, maxX, maxZ);
    }

    public double getInitialRange(TacticalMapBounds bounds) {
        return this.clampRange(this.initialRange <= 0 ? bounds.size() : (double)this.initialRange, bounds);
    }

    public double getMinimumRange(TacticalMapBounds bounds) {
        return this.clampRange(Math.max(1, this.minimumRange), bounds);
    }

    public long getTacticalMarkerDurationMillis() {
        return (long)Math.max(1, this.tacticalMarkerDurationSeconds) * 1000L;
    }

    public long getTacticalMarkerFadeMillis() {
        return Math.min(this.getTacticalMarkerDurationMillis(), (long)Math.max(1, this.tacticalMarkerFadeSeconds) * 1000L);
    }

    public double getTacticalMarkerMaxRenderDistance() {
        return Math.max(16, this.tacticalMarkerMaxRenderDistance);
    }

    private double clampRange(double range, TacticalMapBounds bounds) {
        return Math.max(1.0, Math.min(bounds.size(), range));
    }

    private void copyFrom(TacticalMapJsonConfig loadedConfig) {
        if (loadedConfig == null) {
            return;
        }
        this.topLeftX = loadedConfig.topLeftX;
        this.topLeftZ = loadedConfig.topLeftZ;
        this.bottomRightX = loadedConfig.bottomRightX;
        this.bottomRightZ = loadedConfig.bottomRightZ;
        this.initialRange = loadedConfig.initialRange;
        this.minimumRange = loadedConfig.minimumRange;
        this.backgroundImage = loadedConfig.backgroundImage == null ? "" : loadedConfig.backgroundImage;
        this.backgroundImageWidth = Math.max(0, loadedConfig.backgroundImageWidth);
        this.backgroundImageHeight = Math.max(0, loadedConfig.backgroundImageHeight);
        this.showGrid = loadedConfig.showGrid;
        this.showLabels = loadedConfig.showLabels;
        this.tacticalMarkerDurationSeconds = Math.max(1, loadedConfig.tacticalMarkerDurationSeconds);
        this.tacticalMarkerFadeSeconds = Math.max(1, loadedConfig.tacticalMarkerFadeSeconds);
        this.tacticalMarkerMaxRenderDistance = Math.max(16, loadedConfig.tacticalMarkerMaxRenderDistance);
    }

    public static final class TacticalMapBounds {
        public final double minX;
        public final double minZ;
        public final double maxX;
        public final double maxZ;

        private TacticalMapBounds(double minX, double minZ, double maxX, double maxZ) {
            this.minX = minX;
            this.minZ = minZ;
            this.maxX = maxX;
            this.maxZ = maxZ;
        }

        public double size() {
            return Math.max(this.width(), this.height());
        }

        public double width() {
            return this.maxX - this.minX;
        }

        public double height() {
            return this.maxZ - this.minZ;
        }

        public double aspectRatio() {
            return this.width() / this.height();
        }

        public double centerX() {
            return (this.minX + this.maxX) / 2.0;
        }

        public double centerZ() {
            return (this.minZ + this.maxZ) / 2.0;
        }

        public boolean contains(double x, double z) {
            return x >= this.minX && x <= this.maxX && z >= this.minZ && z <= this.maxZ;
        }

        public TacticalMapBounds expandToInclude(double x, double z, double padding) {
            double safePadding = Math.max(0.0, padding);
            return new TacticalMapBounds(Math.min(this.minX, x - safePadding), Math.min(this.minZ, z - safePadding), Math.max(this.maxX, x + safePadding), Math.max(this.maxZ, z + safePadding));
        }
    }
}

