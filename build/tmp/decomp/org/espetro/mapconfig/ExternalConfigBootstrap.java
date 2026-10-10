/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package org.espetro.mapconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.DimensionIdUtil;
import org.espetro.mapconfig.ExampleContentInstaller;
import org.espetro.mapconfig.PathSafety;

public final class ExternalConfigBootstrap {
    public static final String DIMENSIONS_FILE = "EsDimensions.json";
    public static final String WORLD_DIR = "EsWorld";
    public static final String FACTIONS_DIR = "EsFactions";
    private static volatile boolean bootstrapped = false;
    private static List<ActiveMapConfig> allMaps = List.of();
    private static List<ActiveMapConfig> usableMaps = List.of();
    private static Map<String, Path> factionFiles = Map.of();
    private static int mapVoteSeconds = 30;
    private static final List<String> bootstrapErrors = new ArrayList<String>();
    private static final List<String> bootstrapWarnings = new ArrayList<String>();
    private static final String DEFAULT_GAME_JSON = "{\n  \"game\": {\n    \"team_select_seconds\": 60,\n    \"deploy_timeout_seconds\": 240,\n    \"deploy_warning_seconds\": 30,\n    \"defend_commander_vote_seconds\": 20,\n    \"attack_commander_vote_seconds\": 20,\n    \"defend_faction_select_seconds\": 30,\n    \"attack_faction_select_seconds\": 30,\n    \"faction_pool_size\": 6,\n    \"faction_reveal_seconds\": 5,\n    \"round_end_seconds\": 10,\n    \"respawn_invincibility_ticks\": 60,\n    \"main_base_invulnerability_radius\": 150.0,\n    \"class_switch_cooldown_seconds\": 60,\n    \"teammate_name_tag_distance\": 10.0,\n    \"waiting_y\": 200.0\n  },\n  \"troops\": {\n    \"initial_attack\": 280,\n    \"initial_defend\": 1200,\n    \"commander_death_penalty\": 2\n  },\n  \"stamina\": {\n    \"player_stamina\": 100,\n    \"sprint_cost_per_second\": 5,\n    \"jump_cost\": 15,\n    \"regen_delay_seconds\": 2,\n    \"regen_per_second\": 2,\n    \"full_recovery_seconds\": 12\n  },\n  \"governance\": {\n    \"impeachment_vote_seconds\": 60,\n    \"impeachment_cooldown_seconds\": 600,\n    \"commander_vacancy_seconds\": 180\n  }\n}\n";
    private static final String DEFAULT_SPAWN_JSON = "{\n  \"spawnPoints\": {\n    \"ATTACK\": { \"x\": 100.5, \"y\": 65, \"z\": 0.5, \"yaw\": 0 },\n    \"DEFEND\": { \"x\": -100.5, \"y\": 65, \"z\": 0.5, \"yaw\": 180 }\n  }\n}\n";
    private static final String DEFAULT_OUTPOSTS_JSON = "{\n  \"redeploy_cooldown_seconds\": 60,\n  \"outposts\": []\n}\n";
    private static final String DEFAULT_BASTION_JSON = "{\n  \"bastion\": {\n    \"cooldown_seconds\": 800,\n    \"required_planks\": 0,\n    \"armor_stand_health\": 5,\n    \"destroy_troop_penalty\": 20\n  }\n}\n";
    private static final String DEFAULT_LOGISTICS_JSON = "{\n  \"logistics\": {\n    \"max_construction\": 20000,\n    \"max_ammunition\": 20000,\n    \"pickup_cooldown_seconds\": 5,\n    \"deposit_radius\": 8.0,\n    \"radio_build_radius\": 150.0,\n    \"radio_exclusion_radius\": 400.0,\n    \"radio_teammate_count\": 0,\n    \"radio_teammate_radius\": 30.0,\n    \"require_teammate\": false,\n    \"radio\": {\n      \"allowed_phases\": [\"BATTLE\"],\n      \"require_commander\": false,\n      \"allow_squad_leader\": true,\n      \"cooldown_seconds\": -1,\n      \"required_planks\": 0,\n      \"creative_bypasses_planks\": true,\n      \"max_active_per_team\": -1,\n      \"build_radius\": 150.0,\n      \"require_target_block\": false,\n      \"exclusion_radius\": 400.0,\n      \"teammate_count\": 0,\n      \"teammate_radius\": 30.0\n    },\n    \"hab_construction_cost\": 500,\n    \"ammo_crate_construction_cost\": 100,\n    \"default_resupply_ammo_cost\": 50,\n    \"hab_activation_seconds\": 0,\n    \"hab_reactivation_seconds\": 30,\n    \"hab_disable_radio_health\": 75,\n    \"sources\": []\n  }\n}\n";
    private static final String DEFAULT_TEAM_PACK_JSON = "{\n  \"team_pack\": {\n    \"cooldown_seconds\": 120,\n    \"durability\": 1,\n    \"break_speed_multiplier\": 8.0,\n    \"wave_seconds\": 60,\n    \"minimum_respawn_seconds\": 20\n  }\n}\n";
    private static final String DEFAULT_VEH_SPAWN_JSON = "{\n  \"VehTypes\": [\"tank\", \"apc\"],\n  \"spawn_points\": {\n    \"tank\": [\n      {\n        \"id\": \"tank_1\",\n        \"attack\": {\"x\": 12, \"y\": 64, \"z\": 12, \"yaw\": 0},\n        \"defend\": {\"x\": 120, \"y\": 64, \"z\": 120, \"yaw\": 180}\n      }\n    ],\n    \"apc\": [\n      {\n        \"id\": \"apc_1\",\n        \"attack\": {\"x\": 18, \"y\": 64, \"z\": 12, \"yaw\": 0},\n        \"defend\": {\"x\": 114, \"y\": 64, \"z\": 120, \"yaw\": 180}\n      }\n    ]\n  }\n}\n";
    private static final String DEFAULT_SQUAD_TYPES_JSON = "{\n  \"types\": [\n    {\"id\": \"infantry\", \"display_name\": \"\u6b65\u5175\u961f\"},\n    {\"id\": \"support\", \"display_name\": \"\u652f\u63f4\u961f\"},\n    {\"id\": \"vehicle\", \"display_name\": \"\u8f7d\u5177\u961f\"},\n    {\"id\": \"recon\", \"display_name\": \"\u4fa6\u67e5\u961f\"}\n  ]\n}\n";
    private static final String DEFAULT_TACTICAL_MAP_JSON = "{\n  \"topLeftX\": -512,\n  \"topLeftZ\": -512,\n  \"bottomRightX\": 512,\n  \"bottomRightZ\": 512,\n  \"initialRange\": 512,\n  \"minimumRange\": 64,\n  \"backgroundImage\": \"\",\n  \"backgroundImageWidth\": 0,\n  \"backgroundImageHeight\": 0,\n  \"showGrid\": true,\n  \"showLabels\": true,\n  \"tacticalMarkerDurationSeconds\": 120,\n  \"tacticalMarkerFadeSeconds\": 120\n}\n";
    private static final String DEFAULT_CAPTURE_POINTS_JSON = "{\n  \"totalBatches\": 1,\n  \"endBehavior\": \"terminate\",\n  \"teamReinforcements\": {\n    \"ATTACK\": 280,\n    \"DEFEND\": 1200\n  },\n  \"plannedPoints\": [\n    {\n      \"name\": \"A\",\n      \"batch\": 1,\n      \"pos1\": {\"x\": -24, \"y\": 60, \"z\": -24},\n      \"pos2\": {\"x\": 24, \"y\": 72, \"z\": 24}\n    }\n  ]\n}\n";

    private ExternalConfigBootstrap() {
    }

    public static synchronized void bootstrapIfNeeded() {
        if (bootstrapped) {
            return;
        }
        Path gameDir = FMLPaths.GAMEDIR.get();
        ExternalConfigBootstrap.bootstrap(gameDir);
        bootstrapped = true;
    }

    static synchronized void bootstrap(Path gameDir) {
        int voteSeconds;
        LinkedHashMap factions;
        ArrayList<ActiveMapConfig> usable;
        ArrayList<ActiveMapConfig> maps;
        block31: {
            Path factionsRoot;
            block30: {
                bootstrapErrors.clear();
                bootstrapWarnings.clear();
                maps = new ArrayList<ActiveMapConfig>();
                usable = new ArrayList<ActiveMapConfig>();
                factions = new LinkedHashMap();
                voteSeconds = 30;
                ExampleContentInstaller.Result exampleInstall = ExampleContentInstaller.installMissing(gameDir);
                bootstrapErrors.addAll(exampleInstall.errors());
                try {
                    ExternalConfigBootstrap.ensureDirectoryLayout(gameDir);
                }
                catch (IOException e) {
                    bootstrapErrors.add("\u521b\u5efa\u5916\u90e8\u76ee\u5f55\u5931\u8d25: " + e.getMessage());
                    allMaps = List.of();
                    usableMaps = List.of();
                    factionFiles = Map.of();
                    mapVoteSeconds = voteSeconds;
                    return;
                }
                Path dimensionsPath = gameDir.resolve(DIMENSIONS_FILE);
                Path worldRoot = gameDir.resolve(WORLD_DIR);
                factionsRoot = gameDir.resolve(FACTIONS_DIR);
                try {
                    String text;
                    JsonObject root;
                    if (!Files.isRegularFile(dimensionsPath, new LinkOption[0])) {
                        ExternalConfigBootstrap.writeDefaultDimensions(dimensionsPath);
                        bootstrapWarnings.add("\u5df2\u521b\u5efa\u9ed8\u8ba4 EsDimensions.json\uff08\u65e0\u5730\u56fe\u6761\u76ee\uff09");
                    }
                    if ((root = JsonParser.parseString((String)(text = Files.readString(dimensionsPath, StandardCharsets.UTF_8))).getAsJsonObject()).has("map_vote_seconds") && root.get("map_vote_seconds").isJsonPrimitive()) {
                        voteSeconds = Math.max(5, root.get("map_vote_seconds").getAsInt());
                    }
                    LinkedHashSet<String> usedIds = new LinkedHashSet<String>();
                    LinkedHashSet<String> usedMapFolders = new LinkedHashSet<String>();
                    if (root.has("dimensions") && root.get("dimensions").isJsonArray()) {
                        JsonArray arr = root.getAsJsonArray("dimensions");
                        for (JsonElement el : arr) {
                            Path template;
                            ResourceLocation dimId;
                            String manualId;
                            if (!el.isJsonObject()) {
                                bootstrapErrors.add("dimensions \u5143\u7d20\u5fc5\u987b\u662f\u5bf9\u8c61");
                                continue;
                            }
                            JsonObject o = el.getAsJsonObject();
                            String name = o.has("name") ? o.get("name").getAsString() : null;
                            String map = o.has("map") ? o.get("map").getAsString() : null;
                            String string = manualId = o.has("dimension_id") ? o.get("dimension_id").getAsString() : null;
                            if (name == null || name.isBlank()) {
                                bootstrapErrors.add("\u7ef4\u5ea6\u6761\u76ee\u7f3a\u5c11 name");
                                continue;
                            }
                            Optional<String> mapErr = PathSafety.validateMapFolderName(map);
                            if (mapErr.isPresent()) {
                                bootstrapErrors.add(name + ": " + mapErr.get());
                                maps.add(ActiveMapConfig.rejected(name, map == null ? "" : map, DimensionIdUtil.generate(map == null ? name : map), mapErr.get()));
                                continue;
                            }
                            if (!usedMapFolders.add(map)) {
                                String message = "map \u6587\u4ef6\u5939\u91cd\u590d\u6ce8\u518c: " + map;
                                bootstrapErrors.add(name + ": " + message);
                                maps.add(ActiveMapConfig.rejected(name, map, DimensionIdUtil.generate(map), message));
                                continue;
                            }
                            if (manualId != null && !manualId.isBlank()) {
                                Optional<String> idErr = DimensionIdUtil.validateManualId(manualId, usedIds);
                                if (idErr.isPresent()) {
                                    bootstrapErrors.add(name + ": " + idErr.get());
                                    maps.add(ActiveMapConfig.rejected(name, map, DimensionIdUtil.generate(map), idErr.get()));
                                    continue;
                                }
                                dimId = DimensionIdUtil.parseOrNull(manualId);
                            } else {
                                dimId = DimensionIdUtil.generate(map);
                                if (usedIds.contains(dimId.toString())) {
                                    String msg = "\u81ea\u52a8\u751f\u6210\u7684 dimension_id \u51b2\u7a81: " + dimId;
                                    bootstrapErrors.add(name + ": " + msg);
                                    maps.add(ActiveMapConfig.rejected(name, map, dimId, msg));
                                    continue;
                                }
                            }
                            usedIds.add(dimId.toString());
                            try {
                                template = PathSafety.resolveChildDir(worldRoot, map);
                            }
                            catch (IOException e) {
                                bootstrapErrors.add(name + ": " + e.getMessage());
                                maps.add(ActiveMapConfig.rejected(name, map, dimId, e.getMessage()));
                                continue;
                            }
                            if ("_template".equals(map)) {
                                String msg = "\u4e0d\u80fd\u4f7f\u7528 _template \u4f5c\u4e3a\u4f5c\u6218\u5730\u56fe";
                                bootstrapErrors.add(name + ": " + msg);
                                maps.add(ActiveMapConfig.rejected(name, map, dimId, msg));
                                continue;
                            }
                            ActiveMapConfig cfg = ActiveMapConfig.loadFromTemplate(name, map, dimId, template);
                            maps.add(cfg);
                            cfg.deprecationWarnings().ifPresent(bootstrapWarnings::add);
                            if (cfg.usable) {
                                usable.add(cfg);
                                Espetro.LOGGER.info("\u5df2\u6ce8\u518c\u5730\u56fe: {} -> {} ({})", new Object[]{name, dimId, map});
                                continue;
                            }
                            bootstrapErrors.add(name + " \u62d2\u7edd\u6ce8\u518c: " + String.join((CharSequence)"; ", cfg.rejectionReasons));
                            Espetro.LOGGER.error("\u5730\u56fe\u62d2\u7edd\u6ce8\u518c: {} \u2014 {}", (Object)name, cfg.rejectionReasons);
                        }
                        break block30;
                    }
                    bootstrapWarnings.add("EsDimensions.json \u65e0 dimensions \u6570\u7ec4");
                }
                catch (Exception e) {
                    bootstrapErrors.add("\u89e3\u6790 EsDimensions.json \u5931\u8d25: " + e.getMessage());
                    Espetro.LOGGER.error("\u89e3\u6790 EsDimensions.json \u5931\u8d25", (Throwable)e);
                }
            }
            try {
                if (!Files.isDirectory(factionsRoot, new LinkOption[0])) break block31;
                try (Stream<Path> stream = Files.list(factionsRoot);){
                    stream.filter(p -> p.getFileName().toString().endsWith(".json")).sorted().forEach(p -> {
                        String id = p.getFileName().toString().replace(".json", "");
                        factions.put(id, p);
                    });
                }
            }
            catch (IOException e) {
                bootstrapErrors.add("\u626b\u63cf EsFactions \u5931\u8d25: " + e.getMessage());
            }
        }
        allMaps = List.copyOf(maps);
        usableMaps = List.copyOf(usable);
        factionFiles = Collections.unmodifiableMap(new LinkedHashMap(factions));
        mapVoteSeconds = voteSeconds;
        Espetro.LOGGER.info("\u5916\u90e8\u914d\u7f6e\u51bb\u7ed3: \u5730\u56fe\u603b\u6570={}, \u53ef\u7528={}, \u7f16\u5236\u6587\u4ef6={}, \u6295\u7968\u79d2={}", new Object[]{allMaps.size(), usableMaps.size(), factionFiles.size(), mapVoteSeconds});
        for (String err : bootstrapErrors) {
            Espetro.LOGGER.error("[EsDimensions] {}", (Object)err);
        }
        for (String w : bootstrapWarnings) {
            Espetro.LOGGER.warn("[EsDimensions] {}", (Object)w);
        }
    }

    private static void ensureDirectoryLayout(Path gameDir) throws IOException {
        Path worldRoot = gameDir.resolve(WORLD_DIR);
        Path factionsRoot = gameDir.resolve(FACTIONS_DIR);
        Path template = worldRoot.resolve("_template").resolve("EsConfig");
        Files.createDirectories(template, new FileAttribute[0]);
        Files.createDirectories(factionsRoot, new FileAttribute[0]);
        Path dimensionsPath = gameDir.resolve(DIMENSIONS_FILE);
        if (!Files.exists(dimensionsPath, new LinkOption[0])) {
            ExternalConfigBootstrap.writeDefaultDimensions(dimensionsPath);
        }
        ExternalConfigBootstrap.writeIfMissing(template.resolve("game.json"), DEFAULT_GAME_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("spawn_points.json"), DEFAULT_SPAWN_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("outposts.json"), DEFAULT_OUTPOSTS_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("bastion.json"), DEFAULT_BASTION_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("logistics.json"), DEFAULT_LOGISTICS_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("team_pack.json"), DEFAULT_TEAM_PACK_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("VehSpawn.json"), DEFAULT_VEH_SPAWN_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("SquadTypes.json"), DEFAULT_SQUAD_TYPES_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("TacticalMap.json"), DEFAULT_TACTICAL_MAP_JSON);
        ExternalConfigBootstrap.writeIfMissing(template.resolve("CapturePoints.json"), DEFAULT_CAPTURE_POINTS_JSON);
    }

    private static void writeDefaultDimensions(Path path) throws IOException {
        String json = "{\n  \"_comment\": \"dimension_id \u5efa\u8bae\u7701\u7565\uff0c\u7531\u7cfb\u7edf\u81ea\u52a8\u751f\u6210\uff1b\u4fee\u6539\u540e\u5fc5\u987b\u91cd\u542f\u6e38\u620f\u6216\u670d\u52a1\u7aef\u3002\",\n  \"map_vote_seconds\": 30,\n  \"dimensions\": []\n}\n";
        Files.writeString(path, (CharSequence)json, StandardCharsets.UTF_8, new OpenOption[0]);
    }

    private static void writeIfMissing(Path path, String content) throws IOException {
        if (!Files.exists(path, new LinkOption[0])) {
            Files.createDirectories(path.getParent(), new FileAttribute[0]);
            Files.writeString(path, (CharSequence)content, StandardCharsets.UTF_8, new OpenOption[0]);
        }
    }

    public static boolean isBootstrapped() {
        return bootstrapped;
    }

    public static List<ActiveMapConfig> getAllMaps() {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        return allMaps;
    }

    public static List<ActiveMapConfig> getUsableMaps() {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        return usableMaps;
    }

    public static Optional<ActiveMapConfig> findByDimensionId(ResourceLocation id) {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        for (ActiveMapConfig m : allMaps) {
            if (!m.dimensionId.equals(id)) continue;
            return Optional.of(m);
        }
        return Optional.empty();
    }

    public static Optional<ActiveMapConfig> findByMapFolder(String map) {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        for (ActiveMapConfig m : usableMaps) {
            if (!m.mapFolder.equals(map)) continue;
            return Optional.of(m);
        }
        return Optional.empty();
    }

    public static Map<String, Path> getFactionFiles() {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        return factionFiles;
    }

    public static int getMapVoteSeconds() {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        return mapVoteSeconds;
    }

    public static List<String> getBootstrapErrors() {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        return List.copyOf(bootstrapErrors);
    }

    public static List<String> getBootstrapWarnings() {
        ExternalConfigBootstrap.bootstrapIfNeeded();
        return List.copyOf(bootstrapWarnings);
    }

    static synchronized void resetForTests() {
        bootstrapped = false;
        allMaps = List.of();
        usableMaps = List.of();
        factionFiles = Map.of();
        mapVoteSeconds = 30;
        bootstrapErrors.clear();
        bootstrapWarnings.clear();
    }
}

