/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 */
package org.espetro.mapconfig;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.lang.invoke.CallSite;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import org.espetro.mapconfig.ESPointsMapSnapshot;
import org.espetro.mapconfig.GameSettingsSnapshot;
import org.espetro.mapconfig.LevelDatDimensionReader;
import org.espetro.mapconfig.SpawnPointsSnapshot;
import org.espetro.mapconfig.SquadTypesSnapshot;
import org.espetro.mapconfig.VehSpawnSnapshot;

public final class ActiveMapConfig {
    public static final List<String> REQUIRED_ES_CONFIG_FILES = List.of("game.json", "spawn_points.json", "outposts.json", "bastion.json", "logistics.json", "team_pack.json", "VehSpawn.json", "SquadTypes.json", "TacticalMap.json", "CapturePoints.json");
    public final String displayName;
    public final String mapFolder;
    public final ResourceLocation dimensionId;
    public final ResourceKey<Level> dimensionKey;
    public final Path templateWorldDir;
    public final Path esConfigDir;
    public final String dimensionJson;
    public final GameSettingsSnapshot game;
    public final SpawnPointsSnapshot spawnPoints;
    public final VehSpawnSnapshot vehSpawn;
    public final SquadTypesSnapshot squadTypes;
    public final String outpostsJson;
    public final String bastionJson;
    public final String logisticsJson;
    public final String teamPackJson;
    public final ESPointsMapSnapshot esPoints;
    public final List<String> rejectionReasons;
    public final boolean usable;

    public String capturePointsJson() {
        return this.esPoints != null ? this.esPoints.capturePointsJson : null;
    }

    public ActiveMapConfig forRound(long seed) {
        if (!this.usable || this.esPoints == null) {
            return this;
        }
        return new ActiveMapConfig(this.displayName, this.mapFolder, this.dimensionId, this.templateWorldDir, this.esConfigDir, this.dimensionJson, this.game, this.spawnPoints, this.vehSpawn, this.squadTypes, this.outpostsJson, this.bastionJson, this.logisticsJson, this.teamPackJson, this.esPoints.forRound(seed), this.rejectionReasons);
    }

    private ActiveMapConfig(String displayName, String mapFolder, ResourceLocation dimensionId, Path templateWorldDir, Path esConfigDir, String dimensionJson, GameSettingsSnapshot game, SpawnPointsSnapshot spawnPoints, VehSpawnSnapshot vehSpawn, SquadTypesSnapshot squadTypes, String outpostsJson, String bastionJson, String logisticsJson, String teamPackJson, ESPointsMapSnapshot esPoints, List<String> rejectionReasons) {
        this.displayName = displayName;
        this.mapFolder = mapFolder;
        this.dimensionId = dimensionId;
        this.dimensionKey = ResourceKey.m_135785_(Registries.f_256858_, dimensionId);
        this.templateWorldDir = templateWorldDir;
        this.esConfigDir = esConfigDir;
        this.dimensionJson = dimensionJson;
        this.game = game;
        this.spawnPoints = spawnPoints;
        this.vehSpawn = vehSpawn;
        this.squadTypes = squadTypes;
        this.outpostsJson = outpostsJson;
        this.bastionJson = bastionJson;
        this.logisticsJson = logisticsJson;
        this.teamPackJson = teamPackJson;
        this.esPoints = esPoints;
        this.rejectionReasons = List.copyOf(rejectionReasons);
        this.usable = rejectionReasons.isEmpty();
    }

    public static ActiveMapConfig rejected(String displayName, String mapFolder, ResourceLocation dimensionId, String reason) {
        return new ActiveMapConfig(displayName, mapFolder, dimensionId, null, null, null, GameSettingsSnapshot.defaults(), new SpawnPointsSnapshot(null, null, false, reason), new VehSpawnSnapshot(List.of(), Map.of(), List.of(reason)), SquadTypesSnapshot.defaults(), null, null, null, null, null, List.of(reason));
    }

    public static ActiveMapConfig loadFromTemplate(String displayName, String mapFolder, ResourceLocation dimensionId, Path templateWorldDir) {
        Path esConfig;
        Path region;
        ArrayList<String> errors = new ArrayList<String>();
        if (templateWorldDir == null || !Files.isDirectory(templateWorldDir, new LinkOption[0])) {
            return ActiveMapConfig.rejected(displayName, mapFolder, dimensionId, "\u5730\u56fe\u76ee\u5f55\u4e0d\u5b58\u5728: " + mapFolder);
        }
        if (!Files.isRegularFile(templateWorldDir.resolve("level.dat"), new LinkOption[0])) {
            errors.add("\u5730\u56fe\u7f3a\u5c11 level.dat\uff08\u5fc5\u987b\u63d0\u4f9b\u5b8c\u6574\u539f\u7248\u4e16\u754c\u5b58\u6863\uff09: " + mapFolder);
        }
        if (!Files.isDirectory(region = templateWorldDir.resolve("region"), new LinkOption[0])) {
            errors.add("\u5730\u56fe\u7f3a\u5c11 region/ \u76ee\u5f55\uff08\u4e0d\u662f\u5b8c\u6574\u4e16\u754c\uff09: " + mapFolder);
        }
        if (!Files.isDirectory(esConfig = templateWorldDir.resolve("EsConfig"), new LinkOption[0])) {
            errors.add("\u5730\u56fe\u7f3a\u5c11 EsConfig/ \u76ee\u5f55: " + mapFolder);
            return ActiveMapConfig.rejected(displayName, mapFolder, dimensionId, String.join((CharSequence)"; ", errors));
        }
        for (String required : REQUIRED_ES_CONFIG_FILES) {
            Path f = esConfig.resolve(required);
            if (Files.isRegularFile(f, new LinkOption[0])) continue;
            errors.add("\u7f3a\u5c11\u5fc5\u9700\u914d\u7f6e: EsConfig/" + required);
        }
        if (!errors.isEmpty()) {
            return ActiveMapConfig.rejected(displayName, mapFolder, dimensionId, String.join((CharSequence)"; ", errors));
        }
        try {
            Gson gson = new Gson();
            String dimensionJson = gson.toJson((JsonElement)LevelDatDimensionReader.readDimensionJson(templateWorldDir.resolve("level.dat")));
            GameSettingsSnapshot game = GameSettingsSnapshot.parse(ActiveMapConfig.readJson(esConfig.resolve("game.json")));
            if (game.deprecatedRequiredPlayersPresent) {
                // empty if block
            }
            SpawnPointsSnapshot spawnPoints = SpawnPointsSnapshot.parse(ActiveMapConfig.readJson(esConfig.resolve("spawn_points.json")));
            if (!spawnPoints.valid) {
                errors.add(spawnPoints.error);
            }
            VehSpawnSnapshot vehSpawn = VehSpawnSnapshot.parse(ActiveMapConfig.readJson(esConfig.resolve("VehSpawn.json")));
            errors.addAll(vehSpawn.errors);
            SquadTypesSnapshot squadTypes = SquadTypesSnapshot.parse(ActiveMapConfig.readJson(esConfig.resolve("SquadTypes.json")));
            errors.addAll(squadTypes.errors);
            String outpostsJson = Files.readString(esConfig.resolve("outposts.json"), StandardCharsets.UTF_8);
            String bastionJson = Files.readString(esConfig.resolve("bastion.json"), StandardCharsets.UTF_8);
            String logisticsJson = Files.readString(esConfig.resolve("logistics.json"), StandardCharsets.UTF_8);
            String teamPackJson = Files.readString(esConfig.resolve("team_pack.json"), StandardCharsets.UTF_8);
            ESPointsMapSnapshot esPoints = ESPointsMapSnapshot.load(esConfig);
            ActiveMapConfig.validateOutposts(outpostsJson, errors);
            ActiveMapConfig.validateObjectSection(bastionJson, "bastion.json", "bastion", errors);
            ActiveMapConfig.validateObjectSection(logisticsJson, "logistics.json", "logistics", errors);
            ActiveMapConfig.validateObjectSection(teamPackJson, "team_pack.json", "team_pack", errors);
            if (!errors.isEmpty()) {
                return ActiveMapConfig.rejected(displayName, mapFolder, dimensionId, String.join((CharSequence)"; ", errors));
            }
            return new ActiveMapConfig(displayName, mapFolder, dimensionId, templateWorldDir, esConfig, dimensionJson, game, spawnPoints, vehSpawn, squadTypes, outpostsJson, bastionJson, logisticsJson, teamPackJson, esPoints, List.of());
        }
        catch (Exception e) {
            return ActiveMapConfig.rejected(displayName, mapFolder, dimensionId, "\u52a0\u8f7d EsConfig \u5931\u8d25: " + e.getMessage());
        }
    }

    private static JsonObject readJson(Path path) throws IOException {
        String text = Files.readString(path, StandardCharsets.UTF_8);
        return JsonParser.parseString((String)text).getAsJsonObject();
    }

    private static JsonObject parseObjectOrError(String json, String name, List<String> errors) {
        try {
            JsonElement parsed = JsonParser.parseString((String)json);
            if (!parsed.isJsonObject()) {
                errors.add(name + " \u6839\u8282\u70b9\u5fc5\u987b\u662f JSON \u5bf9\u8c61");
                return null;
            }
            return parsed.getAsJsonObject();
        }
        catch (Exception e) {
            errors.add(name + " JSON \u8bed\u6cd5\u9519\u8bef: " + e.getMessage());
            return null;
        }
    }

    private static void validateObjectSection(String json, String fileName, String section, List<String> errors) {
        JsonObject root = ActiveMapConfig.parseObjectOrError(json, fileName, errors);
        if (root == null) {
            return;
        }
        if (!root.has(section) || !root.get(section).isJsonObject()) {
            errors.add(fileName + " \u7f3a\u5c11\u5bf9\u8c61\u8282\u70b9 " + section);
        }
    }

    private static void validateOutposts(String json, List<String> errors) {
        JsonObject root = ActiveMapConfig.parseObjectOrError(json, "outposts.json", errors);
        if (root == null) {
            return;
        }
        if (!root.has("outposts") || !root.get("outposts").isJsonArray()) {
            errors.add("outposts.json \u7f3a\u5c11 outposts \u6570\u7ec4");
            return;
        }
        int index = 0;
        for (JsonElement element : root.getAsJsonArray("outposts")) {
            ++index;
            if (!element.isJsonObject()) {
                errors.add("outposts.json \u7b2c " + index + " \u4e2a\u524d\u54e8\u5fc5\u987b\u662f\u5bf9\u8c61");
                continue;
            }
            JsonObject outpost = element.getAsJsonObject();
            for (String coordinate : List.of("x", "y", "z")) {
                if (outpost.has(coordinate) && outpost.get(coordinate).isJsonPrimitive() && outpost.getAsJsonPrimitive(coordinate).isNumber()) continue;
                errors.add("outposts.json \u7b2c " + index + " \u4e2a\u524d\u54e8\u7f3a\u5c11\u6570\u503c " + coordinate);
            }
        }
    }

    public Optional<String> deprecationWarnings() {
        ArrayList<CallSite> w = new ArrayList<CallSite>();
        if (this.game.deprecatedRequiredPlayersPresent) {
            w.add((CallSite)((Object)(this.mapFolder + ": game.required_players \u5df2\u5e9f\u5f03\uff0c\u5ffd\u7565")));
        }
        if (this.game.deprecatedTutorialPresent) {
            w.add((CallSite)((Object)(this.mapFolder + ": tutorial \u6bb5\u5df2\u5e9f\u5f03\uff0c\u5ffd\u7565")));
        }
        return w.isEmpty() ? Optional.empty() : Optional.of(String.join((CharSequence)"; ", w));
    }
}

