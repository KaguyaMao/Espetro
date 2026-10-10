/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.api;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;

public final class ActiveBattlefieldSnapshot {
    private final String mapId;
    private final String displayName;
    private final ResourceKey<Level> dimension;
    private final String esConfigPath;
    private final String tacticalMapJson;
    private final String capturePointsJson;
    private final String backgroundImage;
    private final byte[] backgroundBytes;
    private final String backgroundSha256;
    private final int backgroundWidth;
    private final int backgroundHeight;
    private final String objectiveMode;
    private final String objectiveLane;
    private final long objectiveSeed;

    public ActiveBattlefieldSnapshot(String mapId, String displayName, ResourceKey<Level> dimension, String tacticalMapJson, String capturePointsJson, String backgroundImage, byte[] backgroundBytes) {
        this(mapId, displayName, dimension, "", tacticalMapJson, capturePointsJson, backgroundImage, backgroundBytes, "", 0, 0, "", "", 0L);
    }

    public ActiveBattlefieldSnapshot(String mapId, String displayName, ResourceKey<Level> dimension, String tacticalMapJson, String capturePointsJson, String backgroundImage, byte[] backgroundBytes, String backgroundSha256, int backgroundWidth, int backgroundHeight) {
        this(mapId, displayName, dimension, "", tacticalMapJson, capturePointsJson, backgroundImage, backgroundBytes, backgroundSha256, backgroundWidth, backgroundHeight, "", "", 0L);
    }

    public ActiveBattlefieldSnapshot(String mapId, String displayName, ResourceKey<Level> dimension, String tacticalMapJson, String capturePointsJson, String backgroundImage, byte[] backgroundBytes, String backgroundSha256, int backgroundWidth, int backgroundHeight, String objectiveMode, String objectiveLane, long objectiveSeed) {
        this(mapId, displayName, dimension, "", tacticalMapJson, capturePointsJson, backgroundImage, backgroundBytes, backgroundSha256, backgroundWidth, backgroundHeight, objectiveMode, objectiveLane, objectiveSeed);
    }

    public ActiveBattlefieldSnapshot(String mapId, String displayName, ResourceKey<Level> dimension, String esConfigPath, String tacticalMapJson, String capturePointsJson, String backgroundImage, byte[] backgroundBytes, String backgroundSha256, int backgroundWidth, int backgroundHeight, String objectiveMode, String objectiveLane, long objectiveSeed) {
        this.mapId = mapId;
        this.displayName = displayName;
        this.dimension = dimension;
        this.esConfigPath = esConfigPath == null ? "" : esConfigPath;
        this.tacticalMapJson = tacticalMapJson;
        this.capturePointsJson = capturePointsJson;
        this.backgroundImage = backgroundImage == null ? "" : backgroundImage;
        this.backgroundBytes = backgroundBytes == null ? new byte[]{} : (byte[])backgroundBytes.clone();
        this.backgroundSha256 = backgroundSha256 == null ? "" : backgroundSha256;
        this.backgroundWidth = Math.max(0, backgroundWidth);
        this.backgroundHeight = Math.max(0, backgroundHeight);
        this.objectiveMode = objectiveMode == null ? "" : objectiveMode;
        this.objectiveLane = objectiveLane == null ? "" : objectiveLane;
        this.objectiveSeed = objectiveSeed;
    }

    public String mapId() {
        return this.mapId;
    }

    public String displayName() {
        return this.displayName;
    }

    public ResourceKey<Level> dimension() {
        return this.dimension;
    }

    public String esConfigPath() {
        return this.esConfigPath;
    }

    public String tacticalMapJson() {
        return this.tacticalMapJson;
    }

    public String capturePointsJson() {
        return this.capturePointsJson;
    }

    public String backgroundImage() {
        return this.backgroundImage;
    }

    public byte[] backgroundBytes() {
        return (byte[])this.backgroundBytes.clone();
    }

    public String backgroundSha256() {
        return this.backgroundSha256;
    }

    public int backgroundWidth() {
        return this.backgroundWidth;
    }

    public int backgroundHeight() {
        return this.backgroundHeight;
    }

    public String objectiveMode() {
        return this.objectiveMode;
    }

    public String objectiveLane() {
        return this.objectiveLane;
    }

    public long objectiveSeed() {
        return this.objectiveSeed;
    }
}

