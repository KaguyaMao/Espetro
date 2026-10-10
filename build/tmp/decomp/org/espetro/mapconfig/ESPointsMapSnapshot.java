/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package org.espetro.mapconfig;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public final class ESPointsMapSnapshot {
    public static final String TACTICAL_MAP_FILE = "TacticalMap.json";
    public static final String CAPTURE_POINTS_FILE = "CapturePoints.json";
    private static final int MAX_BGR_SIDE = 30000;
    private static final long MAX_BGR_PIXELS = 100000000L;
    public final String tacticalMapJson;
    public final String capturePointsJson;
    public final String backgroundImage;
    public final String backgroundSha256;
    public final int backgroundWidth;
    public final int backgroundHeight;
    public final String objectiveMode;
    public final String objectiveLane;
    public final long objectiveSeed;
    private final byte[] background;

    private ESPointsMapSnapshot(String tacticalMapJson, String capturePointsJson, String backgroundImage, byte[] backgroundBytes, PngMetadata pngMeta, String objectiveMode, String objectiveLane, long objectiveSeed) {
        this.tacticalMapJson = tacticalMapJson;
        this.capturePointsJson = capturePointsJson;
        this.backgroundImage = backgroundImage;
        if (backgroundBytes != null && backgroundBytes.length > 0) {
            this.background = new byte[backgroundBytes.length];
            System.arraycopy(backgroundBytes, 0, this.background, 0, backgroundBytes.length);
        } else {
            this.background = new byte[0];
        }
        this.backgroundSha256 = pngMeta != null ? pngMeta.sha256 : "";
        this.backgroundWidth = pngMeta != null ? pngMeta.width : 0;
        this.backgroundHeight = pngMeta != null ? pngMeta.height : 0;
        this.objectiveMode = objectiveMode == null ? "" : objectiveMode;
        this.objectiveLane = objectiveLane == null ? "" : objectiveLane;
        this.objectiveSeed = objectiveSeed;
    }

    public static ESPointsMapSnapshot load(Path esConfigDir) throws IOException {
        Path tacticalPath = esConfigDir.resolve(TACTICAL_MAP_FILE);
        if (!Files.isRegularFile(tacticalPath, new LinkOption[0])) {
            throw new IOException("\u7f3a\u5c11 TacticalMap.json");
        }
        String tacticalJson = Files.readString(tacticalPath, StandardCharsets.UTF_8);
        JsonObject tacticalObj = JsonParser.parseString((String)tacticalJson).getAsJsonObject();
        String bgImage = "";
        if (tacticalObj.has("backgroundImage")) {
            bgImage = tacticalObj.get("backgroundImage").getAsString();
        }
        if (!bgImage.isEmpty() && (bgImage.contains("..") || bgImage.contains("\\") || bgImage.startsWith("/"))) {
            throw new IOException("backgroundImage \u5fc5\u987b\u662f\u5b89\u5168\u76f8\u5bf9\u8def\u5f84\uff0c\u7981\u6b62 \"..\" \u6216\u7edd\u5bf9\u8def\u5f84");
        }
        Path capturePath = esConfigDir.resolve(CAPTURE_POINTS_FILE);
        if (!Files.isRegularFile(capturePath, new LinkOption[0])) {
            throw new IOException("\u7f3a\u5c11 CapturePoints.json");
        }
        String captureJson = Files.readString(capturePath, StandardCharsets.UTF_8);
        JsonObject captureObj = JsonParser.parseString((String)captureJson).getAsJsonObject();
        String configuredMode = ESPointsMapSnapshot.readConfiguredMode(captureObj);
        byte[] bgBytes = null;
        PngMetadata pngMeta = null;
        if (!bgImage.isEmpty()) {
            Path bgPath = esConfigDir.resolve(bgImage);
            if (!Files.isRegularFile(bgPath, new LinkOption[0])) {
                throw new IOException("\u80cc\u666f\u56fe\u6587\u4ef6\u4e0d\u5b58\u5728: " + bgImage);
            }
            bgBytes = Files.readAllBytes(bgPath);
            pngMeta = ESPointsMapSnapshot.validatePng(bgBytes, bgPath.getFileName().toString());
        }
        return new ESPointsMapSnapshot(tacticalJson, captureJson, bgImage, bgBytes, pngMeta, configuredMode, "", 0L);
    }

    public ESPointsMapSnapshot forRound(long seed) {
        PngMetadata pngMeta = this.hasBackground() ? new PngMetadata(this.backgroundWidth, this.backgroundHeight, this.backgroundSha256) : null;
        return new ESPointsMapSnapshot(this.tacticalMapJson, this.capturePointsJson, this.backgroundImage, this.background, pngMeta, this.objectiveMode, "", seed);
    }

    private static String readConfiguredMode(JsonObject captureObj) {
        if (captureObj == null || !captureObj.has("objectiveMode")) {
            return "AAS";
        }
        String raw = captureObj.get("objectiveMode").getAsString().trim().toUpperCase();
        if ("RAAS".equals(raw)) {
            return "RAAS";
        }
        if ("AAS".equals(raw) || raw.isEmpty()) {
            return "AAS";
        }
        throw new IllegalArgumentException("objectiveMode \u53ea\u80fd\u662f AAS \u6216 RAAS\uff08\u5df2\u79fb\u9664 RANDOM\uff09: " + raw);
    }

    public boolean hasBackground() {
        return !this.backgroundImage.isEmpty() && this.background.length > 0;
    }

    public byte[] backgroundBytes() {
        byte[] copy = new byte[this.background.length];
        System.arraycopy(this.background, 0, copy, 0, this.background.length);
        return copy;
    }

    private static PngMetadata validatePng(byte[] data, String fileName) throws IOException {
        if (data.length < 24) {
            throw new IOException(fileName + " \u4e0d\u662f\u6709\u6548\u7684 PNG \u6587\u4ef6\uff08\u6570\u636e\u592a\u77ed\uff09");
        }
        byte[] signature = new byte[]{-119, 80, 78, 71, 13, 10, 26, 10};
        for (int i = 0; i < 8; ++i) {
            if (data[i] == signature[i]) continue;
            throw new IOException(fileName + " \u4e0d\u662f\u6709\u6548\u7684 PNG \u6587\u4ef6\uff08\u7b7e\u540d\u4e0d\u5339\u914d\uff09");
        }
        int width = ESPointsMapSnapshot.readInt(data, 16);
        int height = ESPointsMapSnapshot.readInt(data, 20);
        if (width <= 0 || height <= 0) {
            throw new IOException(fileName + " \u7684 PNG \u5c3a\u5bf8\u65e0\u6548 (" + width + "x" + height + ")");
        }
        if (width > 30000 || height > 30000) {
            throw new IOException(fileName + " \u7684 PNG \u5355\u8fb9\u6700\u591a 30000 \u50cf\u7d20\uff0c\u5b9e\u9645 " + Math.max(width, height));
        }
        long pixels = (long)width * (long)height;
        if (pixels > 100000000L) {
            throw new IOException(fileName + " \u7684 PNG \u50cf\u7d20\u6570\u8d85\u9650 (" + pixels + " > 100000000)");
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);
            String sha256 = HexFormat.of().formatHex(hash);
            return new PngMetadata(width, height, sha256);
        }
        catch (NoSuchAlgorithmException e) {
            throw new IOException("SHA-256 \u4e0d\u53ef\u7528", e);
        }
    }

    private static int readInt(byte[] data, int offset) {
        return (data[offset] & 0xFF) << 24 | (data[offset + 1] & 0xFF) << 16 | (data[offset + 2] & 0xFF) << 8 | data[offset + 3] & 0xFF;
    }

    private record PngMetadata(int width, int height, String sha256) {
    }
}

