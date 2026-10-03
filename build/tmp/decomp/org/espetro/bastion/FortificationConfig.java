/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.annotations.SerializedName
 *  javax.annotation.Nullable
 *  net.minecraftforge.fml.loading.FMLPaths
 */
package org.espetro.bastion;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.loading.FMLPaths;
import org.espetro.Espetro;
import org.espetro.bastion.FortificationManager;
import org.espetro.bastion.FortificationTemplateCompiler;
import org.espetro.mapconfig.ActiveMapConfig;

public final class FortificationConfig {
    public static final int SCHEMA_VERSION = 2;
    public static final int HARD_MAX_TEMPLATE_BLOCKS = 16384;
    public static final int HARD_MAX_TEMPLATE_ENTITIES = 128;
    public static final int HARD_MAX_TEMPLATE_AXIS = 128;
    public static final int HARD_MAX_TEMPLATE_NBT_BYTES = 0x800000;
    public static final int HARD_MAX_PASSENGER_DEPTH = 8;
    private static final Set<String> REQUIRED = Set.of("espetro:radio", "espetro:hab", "espetro:ammo_crate", "espetro:vehicle_supply_station", "espetro:sandbag_wall");
    private static final Set<String> ROLES = Set.of("commander", "squad_leader", "fireteam_leader");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static volatile RegistrySet registrySet = RegistrySet.empty();
    private static volatile ParsedRoot pending = FortificationConfig.defaults();
    private static volatile boolean frozen;
    private static volatile String failure;

    private FortificationConfig() {
    }

    public static synchronized void loadServerConfig() {
        if (frozen) {
            Espetro.LOGGER.info("\u5ffd\u7565\u8fd0\u884c\u671f\u5de5\u4e8b\u914d\u7f6e\u91cd\u8f7d\uff1b\u4fee\u6539\u5c06\u5728\u4e0b\u6b21\u5b8c\u6574\u91cd\u542f\u751f\u6548");
            return;
        }
        Path configPath = FMLPaths.CONFIGDIR.get().resolve("espetro/fortifications.json");
        try {
            Files.createDirectories(configPath.getParent(), new FileAttribute[0]);
            if (!Files.isRegularFile(configPath, new LinkOption[0])) {
                Files.writeString(configPath, (CharSequence)FortificationConfig.bundledDefaultJson(), StandardCharsets.UTF_8, new OpenOption[0]);
            }
        }
        catch (Exception e) {
            failure = "\u65e0\u6cd5\u521b\u5efa\u5de5\u4e8b\u914d\u7f6e " + configPath + ": " + e.getMessage();
            pending = ParsedRoot.invalid(failure);
            return;
        }
        FortificationConfig.parseIntoPending(configPath);
    }

    public static synchronized void loadFromPath(@Nullable Path path) {
        frozen = false;
        failure = null;
        if (path == null || !Files.isRegularFile(path, new LinkOption[0])) {
            FortificationConfig.publishUncompiled(FortificationConfig.defaults());
            return;
        }
        FortificationConfig.parseIntoPending(path);
        if (FortificationConfig.pending.errors.isEmpty()) {
            FortificationConfig.publishUncompiled(pending);
        } else {
            registrySet = RegistrySet.empty();
        }
    }

    public static synchronized void loadDefaults() {
        frozen = false;
        failure = null;
        pending = FortificationConfig.defaults();
        FortificationConfig.publishUncompiled(pending);
    }

    public static synchronized PreparationResult compileAndFreeze(MinecraftServer server, List<ActiveMapConfig> maps) {
        ArrayList<String> errors;
        if (frozen) {
            return failure == null ? PreparationResult.ok(FortificationConfig.registrySet.global.size(), FortificationConfig.registrySet.byDimension.size()) : PreparationResult.fail(failure);
        }
        if (pending == null) {
            FortificationConfig.loadServerConfig();
        }
        if (!(errors = new ArrayList<String>(FortificationConfig.pending.errors)).isEmpty()) {
            return FortificationConfig.fail(errors);
        }
        try {
            Map<String, FortificationDef> globalDefs = FortificationConfig.copyDefinitions(FortificationConfig.pending.definitions);
            FortificationConfig.validateRequired(globalDefs, errors);
            FortificationConfig.compileDefinitions(server, globalDefs, FortificationConfig.pending.limits, errors, "global");
            LinkedHashMap<ResourceLocation, Map<String, FortificationDef>> perMap = new LinkedHashMap<ResourceLocation, Map<String, FortificationDef>>();
            LinkedHashMap<ResourceLocation, Map<String, String>> perMapAliases = new LinkedHashMap<ResourceLocation, Map<String, String>>();
            if (maps != null) {
                for (ActiveMapConfig map : maps) {
                    if (map == null || !map.usable) continue;
                    Map<String, FortificationDef> overridden = FortificationConfig.applyOverrides(globalDefs, map.logisticsJson, errors, map.mapFolder);
                    FortificationConfig.validateRequired(overridden, errors);
                    FortificationConfig.compileDefinitions(server, overridden, FortificationConfig.pending.limits, errors, "map " + map.mapFolder);
                    perMap.put(map.dimensionId, Collections.unmodifiableMap(overridden));
                    perMapAliases.put(map.dimensionId, Collections.unmodifiableMap(FortificationConfig.buildAliases(overridden, errors)));
                }
            }
            Map<String, String> aliases = FortificationConfig.buildAliases(globalDefs, errors);
            if (!errors.isEmpty()) {
                return FortificationConfig.fail(errors);
            }
            registrySet = new RegistrySet(Collections.unmodifiableMap(globalDefs), Collections.unmodifiableMap(aliases), Collections.unmodifiableMap(perMap), Collections.unmodifiableMap(perMapAliases), FortificationConfig.pending.vehicleService.copy(), FortificationConfig.pending.limits.copy());
            frozen = true;
            failure = null;
            Espetro.LOGGER.info("\u5de5\u4e8b JSON v2 \u5df2\u4e8b\u52a1\u51bb\u7ed3: global={} maps={} aliases={}", new Object[]{globalDefs.size(), perMap.size(), aliases.size()});
            return PreparationResult.ok(globalDefs.size(), perMap.size());
        }
        catch (Exception e) {
            errors.add(e.getMessage() == null ? e.toString() : e.getMessage());
            return FortificationConfig.fail(errors);
        }
    }

    public static synchronized void resetForNextServer() {
        frozen = false;
        failure = null;
        pending = FortificationConfig.defaults();
        registrySet = RegistrySet.empty();
    }

    public static boolean isFrozenReady() {
        return frozen && failure == null;
    }

    @Nullable
    public static String getFailure() {
        return failure;
    }

    public static Limits limits() {
        return FortificationConfig.registrySet.limits.copy();
    }

    public static VehicleServiceSettings vehicleService() {
        return FortificationConfig.registrySet.vehicleService;
    }

    public static Map<String, FortificationDef> all() {
        return FortificationConfig.registrySet.global;
    }

    public static List<FortificationDef> list() {
        return List.copyOf(FortificationConfig.registrySet.global.values());
    }

    public static List<FortificationDef> list(ResourceLocation dimension) {
        return List.copyOf(FortificationConfig.registrySet.byDimension.getOrDefault(dimension, FortificationConfig.registrySet.global).values());
    }

    @Nullable
    public static FortificationDef get(String id) {
        return FortificationConfig.resolve(FortificationConfig.registrySet.global, FortificationConfig.registrySet.aliases, id);
    }

    @Nullable
    public static FortificationDef get(ResourceLocation dimension, String id) {
        Map<String, FortificationDef> definitions = FortificationConfig.registrySet.byDimension.getOrDefault(dimension, FortificationConfig.registrySet.global);
        Map<String, String> aliases = FortificationConfig.registrySet.aliasesByDimension.getOrDefault(dimension, FortificationConfig.registrySet.aliases);
        return FortificationConfig.resolve(definitions, aliases, id);
    }

    public static float explosionDamageRatio() {
        return 0.1f;
    }

    public static float projectileHitDamageRatio() {
        return 0.1f;
    }

    public static ConstructionProfile radioConstruction() {
        FortificationDef def = FortificationConfig.get("espetro:radio");
        return def == null ? new ConstructionProfile(600, 30, 5) : new ConstructionProfile(def.construction.requiredProgress, def.construction.buildPerHit, def.construction.removePerHit);
    }

    public static ConstructionProfile habConstruction() {
        FortificationDef def = FortificationConfig.get("espetro:hab");
        return def == null ? new ConstructionProfile(200, 5, 5) : new ConstructionProfile(def.construction.requiredProgress, def.construction.buildPerHit, def.construction.removePerHit);
    }

    private static void parseIntoPending(Path path) {
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8);){
            pending = FortificationConfig.parse(JsonParser.parseReader((Reader)reader), path.toString());
            if (!FortificationConfig.pending.errors.isEmpty()) {
                failure = String.join((CharSequence)"; ", FortificationConfig.pending.errors);
                Espetro.LOGGER.error("\u5de5\u4e8b\u914d\u7f6e\u4e8b\u52a1\u89e3\u6790\u5931\u8d25\uff08\u672a\u53d1\u5e03\u4efb\u4f55\u6761\u76ee\uff09: {}", (Object)failure);
            } else {
                failure = null;
            }
        }
        catch (Exception e) {
            failure = path + ": " + e.getMessage();
            pending = ParsedRoot.invalid(failure);
        }
    }

    private static ParsedRoot parse(JsonElement input, String source) {
        JsonArray entries;
        int version;
        ArrayList<String> errors = new ArrayList<String>();
        if (input == null || !input.isJsonObject()) {
            return ParsedRoot.invalid(source + ": \u6839\u5fc5\u987b\u662f\u5bf9\u8c61");
        }
        JsonObject root = input.getAsJsonObject();
        int n = version = root.has("schema_version") ? FortificationConfig.integer(root, "schema_version", -1) : 1;
        if (version == 1) {
            root = FortificationConfig.migrateV1(root, errors, source);
            version = 2;
        }
        if (version != 2) {
            errors.add(source + ".schema_version: \u4ec5\u652f\u6301 2\uff08\u8bfb\u53d6\u5230 " + version + ")");
        }
        Limits limits = root.has("limits") && root.get("limits").isJsonObject() ? (Limits)GSON.fromJson(root.get("limits"), Limits.class) : new Limits();
        limits.normalize(errors, source + ".limits");
        VehicleServiceSettings service = root.has("vehicle_service") && root.get("vehicle_service").isJsonObject() ? (VehicleServiceSettings)GSON.fromJson(root.get("vehicle_service"), VehicleServiceSettings.class) : new VehicleServiceSettings();
        service.normalize(errors, source + ".vehicle_service");
        LinkedHashMap<String, FortificationDef> definitions = new LinkedHashMap<String, FortificationDef>();
        JsonArray jsonArray = entries = root.has("fortifications") && root.get("fortifications").isJsonArray() ? root.getAsJsonArray("fortifications") : null;
        if (entries == null) {
            errors.add(source + ".fortifications: \u7f3a\u5c11\u6570\u7ec4");
        } else if (entries.size() > 256) {
            errors.add(source + ".fortifications: \u8d85\u8fc7 256 \u6761\u786c\u4e0a\u9650");
        } else {
            for (int i = 0; i < entries.size(); ++i) {
                String path = source + ".fortifications[" + i + "]";
                if (!entries.get(i).isJsonObject()) {
                    errors.add(path + ": \u5fc5\u987b\u662f\u5bf9\u8c61");
                    continue;
                }
                try {
                    FortificationDef def = (FortificationDef)GSON.fromJson(entries.get(i), FortificationDef.class);
                    FortificationConfig.normalize(def, limits, errors, path);
                    if (def == null || def.id == null || definitions.putIfAbsent(def.id, def) == null) continue;
                    errors.add(path + ".id: \u91cd\u590d " + def.id);
                    continue;
                }
                catch (Exception e) {
                    errors.add(path + ": " + e.getMessage());
                }
            }
        }
        return new ParsedRoot(definitions, service, limits, List.copyOf(errors));
    }

    private static JsonObject migrateV1(JsonObject legacy, List<String> errors, String source) {
        Espetro.LOGGER.warn("{}: \u8bfb\u53d6\u5230\u65e7\u5de5\u4e8b\u683c\u5f0f\uff1b\u4ec5\u8fdb\u884c\u4e00\u6b21\u6027\u517c\u5bb9\u8fc1\u79fb\uff0c\u8bf7\u4fdd\u5b58\u4e3a schema_version=2", (Object)source);
        JsonObject migrated = new JsonObject();
        migrated.addProperty("schema_version", (Number)2);
        migrated.add("limits", GSON.toJsonTree((Object)new Limits()));
        migrated.add("vehicle_service", legacy.has("vehicle_service") ? legacy.get("vehicle_service").deepCopy() : GSON.toJsonTree((Object)new VehicleServiceSettings()));
        JsonArray output = new JsonArray();
        int radioRequired = 600;
        int radioBuild = 30;
        int radioRemove = 5;
        int habRequired = 200;
        int habBuild = 5;
        int habRemove = 5;
        if (legacy.has("builtin_construction") && legacy.get("builtin_construction").isJsonObject()) {
            JsonObject builtins = legacy.getAsJsonObject("builtin_construction");
            int[] radio = FortificationConfig.legacyProfile(builtins.get("radio"), radioRequired, radioBuild, radioRemove);
            int[] hab = FortificationConfig.legacyProfile(builtins.get("hab"), habRequired, habBuild, habRemove);
            radioRequired = radio[0];
            radioBuild = radio[1];
            radioRemove = radio[2];
            habRequired = hab[0];
            habBuild = hab[1];
            habRemove = hab[2];
        }
        output.add((JsonElement)FortificationConfig.v2Structure("espetro:radio", List.of("builtin_radio"), "\u7535\u53f0", "radio", "espetro:fortifications/radio", null, new int[]{0, 0, 0}, 0, radioRequired, radioBuild, radioRemove, radioRequired, false));
        output.add((JsonElement)FortificationConfig.v2Structure("espetro:hab", List.of("builtin_hab"), "\u5175\u7ad9", "hab", "espetro:fortifications/hab_attack", Map.of("attack", "espetro:fortifications/hab_attack", "defend", "espetro:fortifications/hab_defend"), new int[]{3, 0, 1}, 500, habRequired, habBuild, habRemove, habRequired, true));
        if (legacy.has("fortifications") && legacy.get("fortifications").isJsonArray()) {
            for (JsonElement element : legacy.getAsJsonArray("fortifications")) {
                JsonObject converted;
                String id;
                if (!element.isJsonObject()) continue;
                JsonObject old = element.getAsJsonObject();
                String rawId = FortificationConfig.string(old, "id", "");
                switch (id = FortificationConfig.canonicalId(rawId)) {
                    case "espetro:ammo_crate": {
                        JsonObject jsonObject = FortificationConfig.v2Structure(id, List.of("ammo_crate"), FortificationConfig.string(old, "display_name", "\u5f39\u836f\u7bb1"), "ammo_crate", "espetro:fortifications/ammo_crate", null, new int[]{0, 0, 0}, FortificationConfig.integer(old, "construction_cost", 100), FortificationConfig.integer(old, "required_progress", 100), FortificationConfig.integer(old, "build_per_hit", 5), FortificationConfig.integer(old, "remove_per_hit", 5), FortificationConfig.integer(old, "required_progress", 100), true);
                        break;
                    }
                    case "espetro:sandbag_wall": {
                        JsonObject jsonObject = FortificationConfig.v2Structure(id, List.of("sandbag_wall"), FortificationConfig.string(old, "display_name", "\u6c99\u888b\u63a9\u4f53\u5899"), "generic", "espetro:fortifications/sandbag_wall", null, new int[]{1, 0, 0}, FortificationConfig.integer(old, "construction_cost", 100), FortificationConfig.integer(old, "required_progress", 100), FortificationConfig.integer(old, "build_per_hit", 5), FortificationConfig.integer(old, "remove_per_hit", 5), FortificationConfig.integer(old, "required_progress", 100), true);
                        break;
                    }
                    case "espetro:vehicle_supply_station": {
                        JsonObject jsonObject = FortificationConfig.v2Entity(old);
                        break;
                    }
                    default: {
                        JsonObject jsonObject = converted = null;
                    }
                }
                if (converted == null) {
                    errors.add(source + ": \u65e7\u6761\u76ee " + rawId + " \u65e0\u5bf9\u5e94 Structure NBT\uff0c\u62d2\u7edd\u4ee5 inline blocks \u7ee7\u7eed\u8fd0\u884c");
                    continue;
                }
                if (old.has("icon")) {
                    converted.add("icon", (JsonElement)FortificationConfig.legacyIcon(old.get("icon")));
                }
                output.add((JsonElement)converted);
            }
        }
        migrated.add("fortifications", (JsonElement)output);
        return migrated;
    }

    private static JsonObject legacyIcon(JsonElement icon) {
        if (icon != null && icon.isJsonObject()) {
            return icon.getAsJsonObject().deepCopy();
        }
        JsonObject result = new JsonObject();
        result.addProperty("texture", icon == null ? "" : icon.getAsString());
        return result;
    }

    private static JsonObject v2Entity(JsonObject old) {
        JsonObject result = FortificationConfig.commonV2("espetro:vehicle_supply_station", List.of("vehicle_supply_station"), FortificationConfig.string(old, "display_name", "\u8f7d\u5177\u8865\u7ed9\u7ad9"), "vehicle_supply_station", FortificationConfig.integer(old, "construction_cost", 200), FortificationConfig.integer(old, "required_progress", 100), FortificationConfig.integer(old, "build_per_hit", 5), FortificationConfig.integer(old, "remove_per_hit", 5), FortificationConfig.integer(old, "required_progress", 100), true);
        JsonObject placement = new JsonObject();
        placement.addProperty("type", "entity");
        String entityType = FortificationConfig.firstNonBlank(FortificationConfig.string(old, "entity_type", ""), FortificationConfig.string(old, "entity_id", "dragonrise_reforge:ammo_supply_station"));
        placement.addProperty("entity_type", entityType);
        placement.addProperty("fallback_template", "espetro:fortifications/vehicle_supply_station_fallback");
        placement.add("spawn_offset", GSON.toJsonTree((Object)new double[]{0.5, 0.0, 0.5}));
        placement.addProperty("yaw", "player_facing");
        placement.addProperty("virtual_damageable_part", Boolean.valueOf(true));
        placement.add("entity_nbt", (JsonElement)new JsonObject());
        result.add("placement", (JsonElement)placement);
        return result;
    }

    private static JsonObject v2Structure(String id, List<String> aliases, String name, String behavior, String template, @Nullable Map<String, String> byTeam, int[] pivot, int cost, int required, int build, int remove, int structural, boolean radioRange) {
        JsonObject result = FortificationConfig.commonV2(id, aliases, name, behavior, cost, required, build, remove, structural, radioRange);
        JsonObject placement = new JsonObject();
        placement.addProperty("type", "structure");
        placement.addProperty("template", template);
        if (byTeam != null) {
            placement.add("template_by_team", GSON.toJsonTree(byTeam));
        }
        placement.add("origin_offset", GSON.toJsonTree((Object)new int[]{0, 0, 0}));
        placement.add("pivot", GSON.toJsonTree((Object)pivot));
        placement.addProperty("rotation", "player_facing");
        placement.addProperty("mirror", "none");
        placement.addProperty("air_policy", "reject_non_replaceable");
        placement.addProperty("include_entities", Boolean.valueOf(true));
        placement.addProperty("palette_index", (Number)0);
        result.add("placement", (JsonElement)placement);
        return result;
    }

    private static JsonObject commonV2(String id, List<String> aliases, String name, String behavior, int cost, int required, int build, int remove, int structural, boolean radioRange) {
        JsonObject result = new JsonObject();
        result.addProperty("id", id);
        result.add("legacy_ids", GSON.toJsonTree(aliases));
        result.addProperty("display_name", name);
        JsonObject icon = new JsonObject();
        icon.addProperty("item", "minecraft:barrier");
        result.add("icon", (JsonElement)icon);
        result.addProperty("behavior", behavior);
        JsonObject costJson = new JsonObject();
        costJson.addProperty("construction", (Number)Math.max(0, cost));
        costJson.addProperty("ammunition", (Number)0);
        result.add("cost", (JsonElement)costJson);
        JsonObject construction = new JsonObject();
        construction.addProperty("required_progress", (Number)required);
        construction.addProperty("build_per_hit", (Number)build);
        construction.addProperty("remove_per_hit", (Number)remove);
        result.add("construction", (JsonElement)construction);
        JsonObject durability = new JsonObject();
        durability.addProperty("structural_value", (Number)structural);
        durability.addProperty("repair_per_hit", (Number)build);
        durability.add("damageable_structure_entities", (JsonElement)new JsonArray());
        JsonObject reduction = new JsonObject();
        reduction.addProperty("explosion", (Number)0.9);
        reduction.addProperty("projectile", (Number)0.9);
        reduction.addProperty("direct_break", (Number)0.0);
        durability.add("damage_reduction", (JsonElement)reduction);
        result.add("durability", (JsonElement)durability);
        JsonObject requirements = new JsonObject();
        requirements.addProperty("require_radio_range", Boolean.valueOf(radioRange));
        requirements.add("usable_by", GSON.toJsonTree(List.of("commander", "squad_leader", "fireteam_leader")));
        result.add("requirements", (JsonElement)requirements);
        return result;
    }

    private static int[] legacyProfile(JsonElement element, int required, int build, int remove) {
        if (element == null || !element.isJsonObject()) {
            return new int[]{required, build, remove};
        }
        JsonObject object = element.getAsJsonObject();
        return new int[]{FortificationConfig.integer(object, "required_progress", required), FortificationConfig.integer(object, "build_per_hit", build), FortificationConfig.integer(object, "remove_per_hit", remove)};
    }

    private static void normalize(FortificationDef def, Limits limits, List<String> errors, String path) {
        if (def == null) {
            errors.add(path + ": null definition");
            return;
        }
        def.id = FortificationConfig.canonicalId(def.id);
        if (def.id == null || !ResourceLocation.m_135830_(def.id)) {
            errors.add(path + ".id: \u975e\u6cd5 namespaced id");
            return;
        }
        if (def.id.length() > 128) {
            errors.add(path + ".id: \u8d85\u8fc7 128 \u5b57\u7b26");
        }
        if ("espetro:rally".equals(def.id)) {
            errors.add(path + ".id: Rally \u4e0d\u5c5e\u4e8e\u5de5\u4e8b\u7ed3\u6784\u7cfb\u7edf");
        }
        if (def.displayName == null || def.displayName.isBlank()) {
            def.displayName = def.id;
        }
        if (def.displayName.length() > 128) {
            errors.add(path + ".display_name: \u8d85\u8fc7 128 \u5b57\u7b26");
        }
        if (def.iconData == null) {
            def.iconData = new Icon();
        }
        String string = def.icon = def.iconData.texture != null && !def.iconData.texture.isBlank() ? def.iconData.texture : def.iconData.item;
        if (def.icon == null || def.icon.isBlank()) {
            def.icon = "minecraft:barrier";
        }
        try {
            def.behaviorType = Behavior.valueOf(def.behavior == null ? "" : def.behavior.trim().toUpperCase(Locale.ROOT));
        }
        catch (Exception e) {
            errors.add(path + ".behavior: \u672a\u77e5\u884c\u4e3a " + def.behavior);
        }
        if (def.placement == null) {
            errors.add(path + ".placement: \u7f3a\u5931");
        } else {
            def.placement.normalize(errors, path + ".placement");
        }
        if (def.cost == null) {
            def.cost = new Cost();
        }
        def.cost.normalize(errors, path + ".cost");
        if (def.construction == null) {
            def.construction = new Construction();
        }
        def.construction.normalize(errors, path + ".construction");
        if (def.durability == null) {
            def.durability = new Durability();
        }
        def.durability.normalize(errors, path + ".durability");
        if (def.requirements == null) {
            def.requirements = new Requirements();
        }
        def.requirements.normalize(errors, path + ".requirements");
        def.legacyIds = def.legacyIds == null ? new ArrayList<String>() : new ArrayList<String>(new LinkedHashSet<String>(def.legacyIds));
        def.legacyIds.removeIf(value -> value == null || value.isBlank());
        for (String alias : def.legacyIds) {
            if (alias.length() <= 128) continue;
            errors.add(path + ".legacy_ids: alias too long");
        }
        def.placeType = def.placement == null ? null : def.placement.type;
        def.entityId = def.placement == null ? null : def.placement.entityId;
        def.fallbackBlockId = null;
        def.constructionCost = def.cost.construction;
        def.ammunitionCost = def.cost.ammunition;
        def.requiredProgress = def.construction.requiredProgress;
        def.buildPerHit = def.construction.buildPerHit;
        def.removePerHit = def.construction.removePerHit;
        def.requireRadioRange = def.requirements.requireRadioRange;
        def.usableBy = def.requirements.usableBy;
    }

    private static void compileDefinitions(MinecraftServer server, Map<String, FortificationDef> definitions, Limits limits, List<String> errors, String scope) {
        for (FortificationDef def : definitions.values()) {
            try {
                def.compiled = FortificationTemplateCompiler.compile(server, def, limits);
            }
            catch (Exception e) {
                errors.add(scope + "/" + def.id + "/" + (def.placement == null ? "placement" : def.placement.describeTemplates()) + ": " + e.getMessage());
            }
        }
    }

    private static void validateRequired(Map<String, FortificationDef> definitions, List<String> errors) {
        for (String required : REQUIRED) {
            if (definitions.containsKey(required)) continue;
            errors.add("\u7f3a\u5c11\u5fc5\u8981\u5de5\u4e8b\u5b9a\u4e49 " + required);
        }
        FortificationConfig.checkBehavior(definitions, "espetro:radio", Behavior.RADIO, errors);
        FortificationConfig.checkBehavior(definitions, "espetro:hab", Behavior.HAB, errors);
        FortificationConfig.checkBehavior(definitions, "espetro:ammo_crate", Behavior.AMMO_CRATE, errors);
        FortificationConfig.checkBehavior(definitions, "espetro:vehicle_supply_station", Behavior.VEHICLE_SUPPLY_STATION, errors);
    }

    private static void checkBehavior(Map<String, FortificationDef> definitions, String id, Behavior expected, List<String> errors) {
        FortificationDef def = definitions.get(id);
        if (def != null && def.behaviorType != expected) {
            errors.add(id + " \u5fc5\u987b\u4f7f\u7528 behavior=" + expected.name().toLowerCase(Locale.ROOT));
        }
    }

    private static Map<String, FortificationDef> applyOverrides(Map<String, FortificationDef> base, String logisticsJson, List<String> errors, String map) {
        Map<String, FortificationDef> result = FortificationConfig.copyDefinitions(base);
        if (logisticsJson == null || logisticsJson.isBlank()) {
            return result;
        }
        try {
            JsonObject overrides;
            JsonObject logistics;
            JsonObject root = JsonParser.parseString((String)logisticsJson).getAsJsonObject();
            JsonObject jsonObject = logistics = root.has("logistics") && root.get("logistics").isJsonObject() ? root.getAsJsonObject("logistics") : null;
            if (logistics != null) {
                FortificationConfig.applyLegacyCostOverride(result, logistics, "hab_construction_cost", "espetro:hab", map, errors);
                FortificationConfig.applyLegacyCostOverride(result, logistics, "ammo_crate_construction_cost", "espetro:ammo_crate", map, errors);
            }
            JsonObject jsonObject2 = overrides = logistics != null && logistics.has("fortification_overrides") && logistics.get("fortification_overrides").isJsonObject() ? logistics.getAsJsonObject("fortification_overrides") : null;
            if (overrides == null) {
                return result;
            }
            for (Map.Entry entry : overrides.entrySet()) {
                String id = FortificationConfig.canonicalId((String)entry.getKey());
                FortificationDef original = result.get(id);
                if (original == null) {
                    errors.add(map + ".logistics.fortification_overrides: \u672a\u77e5 id " + (String)entry.getKey());
                    continue;
                }
                if (!((JsonElement)entry.getValue()).isJsonObject()) {
                    errors.add(map + ".logistics.fortification_overrides." + (String)entry.getKey() + ": \u5fc5\u987b\u662f\u5bf9\u8c61");
                    continue;
                }
                JsonObject override = ((JsonElement)entry.getValue()).getAsJsonObject();
                if (override.has("id") || override.has("legacy_ids")) {
                    errors.add(map + ".logistics.fortification_overrides." + (String)entry.getKey() + ": \u4e0d\u5141\u8bb8\u8986\u76d6 id/legacy_ids");
                    continue;
                }
                JsonObject merged = GSON.toJsonTree((Object)original).getAsJsonObject();
                FortificationConfig.deepMerge(merged, override);
                FortificationDef changed = (FortificationDef)GSON.fromJson((JsonElement)merged, FortificationDef.class);
                FortificationConfig.normalize(changed, FortificationConfig.pending.limits, errors, map + ".logistics.fortification_overrides." + (String)entry.getKey());
                result.put(id, changed);
            }
        }
        catch (Exception e) {
            errors.add(map + ".logistics.json fortification_overrides: " + e.getMessage());
        }
        return result;
    }

    private static void applyLegacyCostOverride(Map<String, FortificationDef> result, JsonObject logistics, String field, String id, String map, List<String> errors) {
        if (!logistics.has(field)) {
            return;
        }
        try {
            int value = logistics.get(field).getAsInt();
            FortificationDef def = result.get(id);
            if (def != null) {
                def.constructionCost = def.cost.construction = FortificationConfig.boundedNonNegative(value, 1000000, map + ".logistics." + field, errors);
                Espetro.LOGGER.warn("{}: {} \u4ec5\u4f5c\u4e3a\u4e00\u6b21\u6027\u8fc1\u79fb\u8f93\u5165\uff1b\u8bf7\u6539\u5199\u5230 fortification_overrides.{}.cost.construction", new Object[]{map, field, id});
            }
        }
        catch (Exception e) {
            errors.add(map + ".logistics." + field + ": \u5fc5\u987b\u662f\u6574\u6570");
        }
    }

    private static void deepMerge(JsonObject target, JsonObject patch) {
        for (Map.Entry entry : patch.entrySet()) {
            JsonElement current = target.get((String)entry.getKey());
            if (current != null && current.isJsonObject() && ((JsonElement)entry.getValue()).isJsonObject()) {
                FortificationConfig.deepMerge(current.getAsJsonObject(), ((JsonElement)entry.getValue()).getAsJsonObject());
                continue;
            }
            target.add((String)entry.getKey(), ((JsonElement)entry.getValue()).deepCopy());
        }
    }

    private static Map<String, FortificationDef> copyDefinitions(Map<String, FortificationDef> definitions) {
        LinkedHashMap<String, FortificationDef> result = new LinkedHashMap<String, FortificationDef>();
        for (Map.Entry<String, FortificationDef> entry : definitions.entrySet()) {
            FortificationDef copy = (FortificationDef)GSON.fromJson(GSON.toJsonTree((Object)entry.getValue()), FortificationDef.class);
            ArrayList<String> errors = new ArrayList<String>();
            FortificationConfig.normalize(copy, FortificationConfig.pending.limits, errors, "copy." + entry.getKey());
            if (!errors.isEmpty()) {
                throw new IllegalStateException(String.join((CharSequence)"; ", errors));
            }
            result.put(entry.getKey(), copy);
        }
        return result;
    }

    private static Map<String, String> buildAliases(Map<String, FortificationDef> definitions, List<String> errors) {
        LinkedHashMap<String, String> result = new LinkedHashMap<String, String>();
        for (FortificationDef def : definitions.values()) {
            result.put(def.id, def.id);
            for (String raw : def.legacyIds) {
                String canonical = FortificationConfig.canonicalLookup(raw);
                String previous = result.putIfAbsent(canonical, def.id);
                if (previous == null || previous.equals(def.id)) continue;
                errors.add("legacy alias " + raw + " \u540c\u65f6\u6307\u5411 " + previous + " \u548c " + def.id);
            }
        }
        return result;
    }

    @Nullable
    private static FortificationDef resolve(Map<String, FortificationDef> definitions, Map<String, String> aliases, String raw) {
        if (raw == null) {
            return null;
        }
        String key = FortificationConfig.canonicalLookup(raw);
        return definitions.get(aliases.getOrDefault(key, key));
    }

    private static String canonicalLookup(String raw) {
        String value = raw.trim().toLowerCase(Locale.ROOT);
        return value.indexOf(58) >= 0 ? value : "espetro:" + value;
    }

    @Nullable
    private static String canonicalId(@Nullable String raw) {
        if (raw == null || raw.isBlank()) {
            return null;
        }
        return FortificationConfig.canonicalLookup(raw);
    }

    private static int integer(JsonObject object, String key, int fallback) {
        try {
            return object.has(key) ? object.get(key).getAsInt() : fallback;
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    private static String string(JsonObject object, String key, String fallback) {
        try {
            return object.has(key) ? object.get(key).getAsString() : fallback;
        }
        catch (Exception ignored) {
            return fallback;
        }
    }

    private static boolean hasText(@Nullable String value) {
        return value != null && !value.isBlank();
    }

    private static String firstNonBlank(String first, String second) {
        return FortificationConfig.hasText(first) ? first : second;
    }

    private static PreparationResult fail(List<String> errors) {
        failure = String.join((CharSequence)"; ", errors);
        registrySet = RegistrySet.empty();
        frozen = false;
        Espetro.LOGGER.error("\u5de5\u4e8b registry \u51bb\u7ed3\u5931\u8d25\uff0c\u6218\u573a gate \u5fc5\u987b\u4fdd\u6301\u5173\u95ed: {}", (Object)failure);
        return PreparationResult.fail(failure);
    }

    private static void publishUncompiled(ParsedRoot root) {
        ArrayList<String> errors = new ArrayList<String>();
        Map<String, String> aliases = FortificationConfig.buildAliases(root.definitions, errors);
        if (!errors.isEmpty()) {
            failure = String.join((CharSequence)"; ", errors);
            registrySet = RegistrySet.empty();
            return;
        }
        registrySet = new RegistrySet(Collections.unmodifiableMap(root.definitions), Collections.unmodifiableMap(aliases), Map.of(), Map.of(), root.vehicleService.copy(), root.limits.copy());
        pending = root;
    }

    private static ParsedRoot defaults() {
        try {
            return FortificationConfig.parse(JsonParser.parseString((String)FortificationConfig.bundledDefaultJson()), "bundled-default");
        }
        catch (Exception e) {
            return ParsedRoot.invalid("\u5185\u7f6e\u5de5\u4e8b\u914d\u7f6e\u4e0d\u53ef\u8bfb: " + e.getMessage());
        }
    }

    private static String bundledDefaultJson() throws Exception {
        try (InputStream stream = FortificationConfig.class.getResourceAsStream("/data/espetro/config/fortifications.json");){
            if (stream == null) {
                throw new IllegalStateException("\u7f3a\u5c11\u5185\u7f6e fortifications.json");
            }
            String string = new String(stream.readAllBytes(), StandardCharsets.UTF_8);
            return string;
        }
    }

    private static int boundedPositive(int value, int max, String path, List<String> errors) {
        if (value < 1 || value > max) {
            errors.add(path + ": \u5fc5\u987b\u5728 [1," + max + "]");
        }
        return Math.max(1, Math.min(max, value));
    }

    private static int boundedNonNegative(int value, int max, String path, List<String> errors) {
        if (value < 0 || value > max) {
            errors.add(path + ": \u5fc5\u987b\u5728 [0," + max + "]");
        }
        return Math.max(0, Math.min(max, value));
    }

    private static double reduction(double value, String path, List<String> errors) {
        if (!Double.isFinite(value) || value < 0.0 || value > 1.0) {
            errors.add(path + ": \u5fc5\u987b\u5728 [0,1]");
        }
        return Double.isFinite(value) ? Math.max(0.0, Math.min(1.0, value)) : 0.0;
    }

    private record ParsedRoot(Map<String, FortificationDef> definitions, VehicleServiceSettings vehicleService, Limits limits, List<String> errors) {
        static ParsedRoot invalid(String error) {
            return new ParsedRoot(new LinkedHashMap<String, FortificationDef>(), new VehicleServiceSettings(), new Limits(), List.of(error));
        }
    }

    private record RegistrySet(Map<String, FortificationDef> global, Map<String, String> aliases, Map<ResourceLocation, Map<String, FortificationDef>> byDimension, Map<ResourceLocation, Map<String, String>> aliasesByDimension, VehicleServiceSettings vehicleService, Limits limits) {
        static RegistrySet empty() {
            return new RegistrySet(Map.of(), Map.of(), Map.of(), Map.of(), new VehicleServiceSettings(), new Limits());
        }
    }

    public record PreparationResult(boolean success, int definitionCount, int mapCount, @Nullable String error) {
        static PreparationResult ok(int definitions, int maps) {
            return new PreparationResult(true, definitions, maps, null);
        }

        static PreparationResult fail(String error) {
            return new PreparationResult(false, 0, 0, error);
        }
    }

    public static final class Limits {
        @SerializedName(value="max_template_blocks")
        public int maxTemplateBlocks = 4096;
        @SerializedName(value="max_template_entities")
        public int maxTemplateEntities = 32;
        @SerializedName(value="max_template_axis")
        public int maxTemplateAxis = 64;
        @SerializedName(value="max_template_nbt_bytes")
        public int maxTemplateNbtBytes = 0x200000;
        @SerializedName(value="max_passenger_depth")
        public int maxPassengerDepth = 4;

        void normalize(List<String> errors, String path) {
            this.maxTemplateBlocks = FortificationConfig.boundedPositive(this.maxTemplateBlocks, 16384, path + ".max_template_blocks", errors);
            this.maxTemplateEntities = FortificationConfig.boundedPositive(this.maxTemplateEntities, 128, path + ".max_template_entities", errors);
            this.maxTemplateAxis = FortificationConfig.boundedPositive(this.maxTemplateAxis, 128, path + ".max_template_axis", errors);
            this.maxTemplateNbtBytes = FortificationConfig.boundedPositive(this.maxTemplateNbtBytes, 0x800000, path + ".max_template_nbt_bytes", errors);
            this.maxPassengerDepth = FortificationConfig.boundedPositive(this.maxPassengerDepth, 8, path + ".max_passenger_depth", errors);
        }

        Limits copy() {
            Limits copy = new Limits();
            copy.maxTemplateBlocks = this.maxTemplateBlocks;
            copy.maxTemplateEntities = this.maxTemplateEntities;
            copy.maxTemplateAxis = this.maxTemplateAxis;
            copy.maxTemplateNbtBytes = this.maxTemplateNbtBytes;
            copy.maxPassengerDepth = this.maxPassengerDepth;
            return copy;
        }
    }

    public static final class VehicleServiceSettings {
        @SerializedName(value="main_base_radius")
        public double mainBaseRadius = 40.0;
        @SerializedName(value="station_radius")
        public double stationRadius = 20.0;
        @SerializedName(value="transfer_amount")
        public int transferAmount = 100;
        @SerializedName(value="transfer_interval_ticks")
        public int transferIntervalTicks = 20;

        void normalize(List<String> errors, String path) {
            if (!Double.isFinite(this.mainBaseRadius)) {
                errors.add(path + ".main_base_radius: \u975e\u6709\u9650\u6570");
            }
            if (!Double.isFinite(this.stationRadius)) {
                errors.add(path + ".station_radius: \u975e\u6709\u9650\u6570");
            }
            this.mainBaseRadius = Math.max(1.0, Math.min(256.0, this.mainBaseRadius));
            this.stationRadius = Math.max(1.0, Math.min(128.0, this.stationRadius));
            this.transferAmount = Math.max(1, Math.min(100000, this.transferAmount));
            this.transferIntervalTicks = Math.max(1, Math.min(200, this.transferIntervalTicks));
        }

        VehicleServiceSettings copy() {
            VehicleServiceSettings copy = new VehicleServiceSettings();
            copy.mainBaseRadius = this.mainBaseRadius;
            copy.stationRadius = this.stationRadius;
            copy.transferAmount = this.transferAmount;
            copy.transferIntervalTicks = this.transferIntervalTicks;
            return copy;
        }
    }

    public static final class FortificationDef {
        public String id;
        @SerializedName(value="legacy_ids")
        public List<String> legacyIds = new ArrayList<String>();
        @SerializedName(value="display_name")
        public String displayName;
        @SerializedName(value="icon")
        public Icon iconData = new Icon();
        public String behavior;
        public Placement placement;
        public Cost cost = new Cost();
        public Construction construction = new Construction();
        public Durability durability = new Durability();
        public Requirements requirements = new Requirements();
        public transient Behavior behaviorType;
        public transient Map<String, FortificationTemplateCompiler.CompiledTemplate> compiled = Map.of();
        public transient String icon;
        public transient String placeType;
        public transient String entityId;
        public transient String fallbackBlockId;
        public transient int constructionCost;
        public transient int ammunitionCost;
        public transient int requiredProgress;
        public transient int buildPerHit;
        public transient int removePerHit;
        public transient boolean requireRadioRange;
        public transient List<String> usableBy = List.of();

        @Nullable
        public FortificationTemplateCompiler.CompiledTemplate templateFor(String team) {
            String normalized = team == null ? "default" : team.toLowerCase(Locale.ROOT);
            return this.compiled.getOrDefault(normalized, this.compiled.get("default"));
        }
    }

    public static final class ConstructionProfile {
        public int requiredProgress;
        public int buildPerHit;
        public int removePerHit;

        public ConstructionProfile(int requiredProgress, int buildPerHit, int removePerHit) {
            this.requiredProgress = requiredProgress;
            this.buildPerHit = buildPerHit;
            this.removePerHit = removePerHit;
        }

        public ConstructionProfile copy() {
            return new ConstructionProfile(this.requiredProgress, this.buildPerHit, this.removePerHit);
        }
    }

    public static final class Construction {
        @SerializedName(value="required_progress")
        public int requiredProgress = 100;
        @SerializedName(value="build_per_hit")
        public int buildPerHit = 5;
        @SerializedName(value="remove_per_hit")
        public int removePerHit = 5;

        void normalize(List<String> errors, String path) {
            this.requiredProgress = FortificationConfig.boundedPositive(this.requiredProgress, 1000000, path + ".required_progress", errors);
            this.buildPerHit = FortificationConfig.boundedPositive(this.buildPerHit, this.requiredProgress, path + ".build_per_hit", errors);
            this.removePerHit = FortificationConfig.boundedPositive(this.removePerHit, this.requiredProgress, path + ".remove_per_hit", errors);
        }
    }

    public static final class Icon {
        public String item;
        public String texture;
    }

    public static enum Behavior {
        RADIO,
        HAB,
        AMMO_CRATE,
        VEHICLE_SUPPLY_STATION,
        GENERIC;

    }

    public static final class Placement {
        public String type;
        public String template;
        @SerializedName(value="template_by_team")
        public Map<String, String> templateByTeam = new LinkedHashMap<String, String>();
        @SerializedName(value="origin_offset")
        public int[] originOffset = new int[]{0, 0, 0};
        public int[] pivot = new int[]{0, 0, 0};
        public String rotation = "player_facing";
        public String mirror = "none";
        @SerializedName(value="air_policy")
        public String airPolicy = "reject_non_replaceable";
        @SerializedName(value="include_entities")
        public boolean includeEntities;
        @SerializedName(value="palette_index")
        public int paletteIndex;
        @SerializedName(value="entity_id", alternate={"entity_type"})
        public String entityId;
        @SerializedName(value="entity_nbt")
        public JsonObject entityNbt;
        @SerializedName(value="fallback_template")
        public String fallbackTemplate;
        @SerializedName(value="spawn_offset")
        public double[] spawnOffset;
        public String yaw;
        @SerializedName(value="virtual_damageable_part")
        public boolean virtualDamageablePart;
        public transient CompoundTag sanitizedEntityNbt;

        void normalize(List<String> errors, String path) {
            String string = this.type = this.type == null ? "" : this.type.trim().toLowerCase(Locale.ROOT);
            if (!Set.of("structure", "entity").contains(this.type)) {
                errors.add(path + ".type: \u5fc5\u987b\u662f structure \u6216 entity");
                return;
            }
            if ("structure".equals(this.type)) {
                if (!"player_facing".equals(this.rotation)) {
                    errors.add(path + ".rotation: \u4ec5\u652f\u6301 player_facing");
                }
                if (!"none".equals(this.mirror)) {
                    errors.add(path + ".mirror: v2 \u4ec5\u652f\u6301 none");
                }
                if (!"reject_non_replaceable".equals(this.airPolicy)) {
                    errors.add(path + ".air_policy: \u4ec5\u652f\u6301 reject_non_replaceable");
                }
                if (this.originOffset == null || this.originOffset.length != 3) {
                    errors.add(path + ".origin_offset: \u5fc5\u987b\u67093\u9879");
                }
                if (this.pivot == null || this.pivot.length != 3) {
                    errors.add(path + ".pivot: \u5fc5\u987b\u67093\u9879");
                }
                this.paletteIndex = Math.max(0, this.paletteIndex);
                if (ResourceLocation.m_135820_(this.template) == null) {
                    errors.add(path + ".template: \u975e\u6cd5\u6216\u7f3a\u5931");
                }
                if (FortificationConfig.hasText(this.entityId) || this.entityNbt != null || this.fallbackTemplate != null || this.spawnOffset != null || FortificationConfig.hasText(this.yaw)) {
                    errors.add(path + ": structure \u4e0d\u5f97\u6df7\u7528 entity \u5b57\u6bb5");
                }
                if (this.templateByTeam == null) {
                    this.templateByTeam = new LinkedHashMap<String, String>();
                }
                for (Map.Entry<String, String> entry : this.templateByTeam.entrySet()) {
                    String side = entry.getKey().toLowerCase(Locale.ROOT);
                    if (Set.of("attack", "defend", "default").contains(side) && ResourceLocation.m_135820_(entry.getValue()) != null) continue;
                    errors.add(path + ".template_by_team." + entry.getKey() + ": \u975e\u6cd5\u6620\u5c04");
                }
            } else {
                if (ResourceLocation.m_135820_(this.entityId) == null) {
                    errors.add(path + ".entity_type: \u975e\u6cd5\u6216\u7f3a\u5931\uff08entity_id \u53ef\u4f5c\u4e3a\u540c\u4e49\u5b57\u6bb5\uff09");
                }
                if (!this.virtualDamageablePart) {
                    errors.add(path + ".virtual_damageable_part: entity \u5fc5\u987b\u4e3a true");
                }
                if (this.template != null || this.templateByTeam != null && !this.templateByTeam.isEmpty()) {
                    errors.add(path + ": entity \u4e0d\u5f97\u6df7\u7528 structure template \u5b57\u6bb5");
                }
                if (this.fallbackTemplate != null && ResourceLocation.m_135820_(this.fallbackTemplate) == null) {
                    errors.add(path + ".fallback_template: \u975e\u6cd5");
                }
                if (!(this.spawnOffset != null && this.spawnOffset.length == 3 && Double.isFinite(this.spawnOffset[0]) && Double.isFinite(this.spawnOffset[1]) && Double.isFinite(this.spawnOffset[2]))) {
                    this.spawnOffset = new double[]{0.5, 0.0, 0.5};
                }
                if (this.yaw == null || this.yaw.isBlank()) {
                    this.yaw = "player_facing";
                }
                if (!"player_facing".equals(this.yaw)) {
                    errors.add(path + ".yaw: \u4ec5\u652f\u6301 player_facing");
                }
            }
        }

        String describeTemplates() {
            if ("entity".equals(this.type)) {
                return this.entityId + " fallback=" + this.fallbackTemplate;
            }
            return this.template + " team=" + this.templateByTeam;
        }
    }

    public static final class Cost {
        public int construction;
        public int ammunition;

        void normalize(List<String> errors, String path) {
            this.construction = FortificationConfig.boundedNonNegative(this.construction, 1000000, path + ".construction", errors);
            this.ammunition = FortificationConfig.boundedNonNegative(this.ammunition, 1000000, path + ".ammunition", errors);
        }
    }

    public static final class Durability {
        @SerializedName(value="structural_value")
        public int structuralValue = 100;
        @SerializedName(value="repair_per_hit")
        public int repairPerHit = 5;
        @SerializedName(value="damageable_structure_entities")
        public List<Integer> damageableStructureEntities = new ArrayList<Integer>();
        @SerializedName(value="damage_reduction")
        public DamageReduction damageReduction = new DamageReduction();

        void normalize(List<String> errors, String path) {
            this.structuralValue = FortificationConfig.boundedPositive(this.structuralValue, 1000000, path + ".structural_value", errors);
            this.repairPerHit = FortificationConfig.boundedPositive(this.repairPerHit, this.structuralValue, path + ".repair_per_hit", errors);
            if (this.damageReduction == null) {
                this.damageReduction = new DamageReduction();
            }
            this.damageReduction.normalize(errors, path + ".damage_reduction");
            if (this.damageableStructureEntities == null) {
                this.damageableStructureEntities = new ArrayList<Integer>();
            }
            LinkedHashSet<Integer> unique = new LinkedHashSet<Integer>();
            for (Integer index : this.damageableStructureEntities) {
                if (index == null || index < 0) {
                    errors.add(path + ".damageable_structure_entities: \u975e\u6cd5\u7d22\u5f15");
                    continue;
                }
                unique.add(index);
            }
            this.damageableStructureEntities = new ArrayList<Integer>(unique);
        }
    }

    public static final class Requirements {
        @SerializedName(value="require_radio_range")
        public boolean requireRadioRange = true;
        @SerializedName(value="usable_by")
        public List<String> usableBy = new ArrayList<String>(List.of("commander", "squad_leader", "fireteam_leader"));

        void normalize(List<String> errors, String path) {
            if (this.usableBy == null || this.usableBy.isEmpty()) {
                errors.add(path + ".usable_by: \u4e0d\u5f97\u4e3a\u7a7a");
                return;
            }
            ArrayList<String> normalized = new ArrayList<String>();
            for (String raw : this.usableBy) {
                String role;
                String string = role = raw == null ? "" : raw.trim().toLowerCase(Locale.ROOT);
                if (!ROLES.contains(role)) {
                    errors.add(path + ".usable_by: \u672a\u77e5\u89d2\u8272 " + raw);
                    continue;
                }
                if (normalized.contains(role)) continue;
                normalized.add(role);
            }
            this.usableBy = normalized;
        }
    }

    public static final class DamageReduction {
        public double explosion = 0.9;
        public double projectile = 0.9;
        @SerializedName(value="direct_break")
        public double directBreak;

        void normalize(List<String> errors, String path) {
            this.explosion = FortificationConfig.reduction(this.explosion, path + ".explosion", errors);
            this.projectile = FortificationConfig.reduction(this.projectile, path + ".projectile", errors);
            this.directBreak = FortificationConfig.reduction(this.directBreak, path + ".direct_break", errors);
        }

        public double forKind(FortificationManager.DamageKind kind) {
            return switch (kind) {
                default -> throw new IncompatibleClassChangeError();
                case FortificationManager.DamageKind.EXPLOSION -> this.explosion;
                case FortificationManager.DamageKind.PROJECTILE -> this.projectile;
                case FortificationManager.DamageKind.DIRECT_BREAK -> this.directBreak;
            };
        }
    }
}

