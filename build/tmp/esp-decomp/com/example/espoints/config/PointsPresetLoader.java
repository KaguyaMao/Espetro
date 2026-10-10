/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package com.example.espoints.config;

import com.example.espoints.util.ModLogger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public final class PointsPresetLoader {
    public static final String POINTS_DIR = "Points";
    public static final String LEGACY_CAPTURE_FILE = "CapturePoints.json";
    public static final String GAME_FILE = "game.json";

    private PointsPresetLoader() {
    }

    public static String readConfiguredMode(Path esConfigDir) {
        Path gamePath = esConfigDir.resolve(GAME_FILE);
        if (!Files.isRegularFile(gamePath, new LinkOption[0])) {
            return "AAS";
        }
        try {
            JsonObject game;
            JsonObject root = JsonParser.parseString((String)Files.readString(gamePath, StandardCharsets.UTF_8)).getAsJsonObject();
            JsonObject jsonObject = game = root.has("game") && root.get("game").isJsonObject() ? root.getAsJsonObject("game") : root;
            if (!game.has("objectiveMode")) {
                return "AAS";
            }
            String raw = game.get("objectiveMode").getAsString().trim().toUpperCase(Locale.ROOT);
            if ("RAAS".equals(raw)) {
                return "RAAS";
            }
            if ("AAS".equals(raw) || raw.isEmpty()) {
                return "AAS";
            }
            throw new IllegalArgumentException("game.objectiveMode \u53ea\u80fd\u662f AAS \u6216 RAAS: " + raw);
        }
        catch (IOException e) {
            ModLogger.warn("\u8bfb\u53d6 game.json \u5931\u8d25\uff0cobjectiveMode \u56de\u9000 AAS: " + e.getMessage());
            return "AAS";
        }
    }

    public static Selection select(Path esConfigDir, String preferredMode, long seed) throws IOException {
        String mode;
        String string = mode = preferredMode == null || preferredMode.isBlank() ? "AAS" : preferredMode.trim().toUpperCase(Locale.ROOT);
        if (!"AAS".equals(mode) && !"RAAS".equals(mode)) {
            throw new IllegalArgumentException("objectiveMode \u53ea\u80fd\u662f AAS \u6216 RAAS: " + mode);
        }
        Path mapRoot = esConfigDir.getParent();
        Path pointsDir = mapRoot == null ? null : mapRoot.resolve(POINTS_DIR);
        List<Path> candidates = PointsPresetLoader.listMatchingPresets(pointsDir, mode);
        if (!candidates.isEmpty()) {
            candidates.sort(Comparator.comparing(p -> p.getFileName().toString()));
            Path chosen = candidates.get(new Random(seed).nextInt(candidates.size()));
            String json = Files.readString(chosen, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString((String)json).getAsJsonObject();
            root.addProperty("objectiveMode", mode);
            ModLogger.info("Points \u9884\u8bbe\u5df2\u9009\u62e9: " + String.valueOf(chosen.getFileName()) + " (mode=" + mode + ", candidates=" + candidates.size() + ")");
            return new Selection(mode, chosen.getFileName().toString(), root.toString(), chosen);
        }
        Path legacy = esConfigDir.resolve(LEGACY_CAPTURE_FILE);
        if (Files.isRegularFile(legacy, new LinkOption[0])) {
            ModLogger.warn("Points/ \u65e0\u5339\u914d " + mode + " \u7684\u9884\u8bbe\uff0c\u56de\u9000 CapturePoints.json");
            String json = Files.readString(legacy, StandardCharsets.UTF_8);
            JsonObject root = JsonParser.parseString((String)json).getAsJsonObject();
            root.addProperty("objectiveMode", mode);
            return new Selection(mode, LEGACY_CAPTURE_FILE, root.toString(), legacy);
        }
        throw new IOException("\u5730\u56fe\u7f3a\u5c11 Points/ \u4e2d\u9002\u7528\u4e8e " + mode + " \u7684\u636e\u70b9\u9884\u8bbe\uff0c\u4e14\u65e0 CapturePoints.json");
    }

    private static List<Path> listMatchingPresets(Path pointsDir, String mode) throws IOException {
        ArrayList<Path> matched = new ArrayList<Path>();
        if (pointsDir == null || !Files.isDirectory(pointsDir, new LinkOption[0])) {
            return matched;
        }
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(pointsDir, "*.json");){
            for (Path file : stream) {
                if (!Files.isRegularFile(file, new LinkOption[0])) continue;
                try {
                    JsonObject root = JsonParser.parseString((String)Files.readString(file, StandardCharsets.UTF_8)).getAsJsonObject();
                    if (!PointsPresetLoader.modesContain(root, mode)) continue;
                    matched.add(file);
                }
                catch (RuntimeException e) {
                    ModLogger.warn("\u8df3\u8fc7\u635f\u574f\u7684 Points \u9884\u8bbe " + String.valueOf(file.getFileName()) + ": " + e.getMessage());
                }
            }
        }
        return matched;
    }

    private static boolean modesContain(JsonObject root, String mode) {
        if (!root.has("modes")) {
            if ("RAAS".equals(mode)) {
                return root.has("raas") || "RAAS".equalsIgnoreCase(PointsPresetLoader.getString(root, "objectiveMode", ""));
            }
            return root.has("plannedPoints") || "AAS".equalsIgnoreCase(PointsPresetLoader.getString(root, "objectiveMode", "AAS"));
        }
        JsonElement element = root.get("modes");
        if (!element.isJsonArray()) {
            return false;
        }
        JsonArray array = element.getAsJsonArray();
        for (JsonElement entry : array) {
            if (!entry.isJsonPrimitive() || !mode.equalsIgnoreCase(entry.getAsString().trim())) continue;
            return true;
        }
        return false;
    }

    private static String getString(JsonObject root, String key, String fallback) {
        if (!root.has(key) || !root.get(key).isJsonPrimitive()) {
            return fallback;
        }
        return root.get(key).getAsString();
    }

    public record Selection(String mode, String sourceName, String json, Path sourcePath) {
    }
}

