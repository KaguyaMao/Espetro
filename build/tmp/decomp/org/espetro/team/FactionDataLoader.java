/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.annotations.SerializedName
 */
package org.espetro.team;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import org.espetro.Espetro;
import org.espetro.audio.AudioPackId;
import org.espetro.mapconfig.ActiveMapConfig;
import org.espetro.mapconfig.VehSpawnSnapshot;
import org.espetro.team.ClassLoadoutPreviewResolver;
import org.espetro.vehicle.VehicleSeatAccessPolicy;

public class FactionDataLoader {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final String[] EMPTY_STRING_ARRAY = new String[0];
    private static final FactionData[] EMPTY_FACTION_ARRAY = new FactionData[0];
    private static final ClassKitData[] EMPTY_CLASS_KIT_ARRAY = new ClassKitData[0];
    private static FactionDataLoader INSTANCE;
    private Map<String, FactionData> factions = new LinkedHashMap<String, FactionData>();
    private Map<String, ClassKitData> classKits = new LinkedHashMap<String, ClassKitData>();
    private final Map<String, Map<String, VehicleData>> factionVehicles = new LinkedHashMap<String, Map<String, VehicleData>>();
    private final Map<String, List<String>> factionVehicleTypes = new LinkedHashMap<String, List<String>>();
    private final Map<String, ClassKitData[]> classesByFaction = new HashMap<String, ClassKitData[]>();
    private final Map<String, String[]> classIdsByFaction = new HashMap<String, String[]>();
    private String[] factionIdArray = EMPTY_STRING_ARRAY;
    private FactionData[] factionArray = EMPTY_FACTION_ARRAY;
    private boolean loaded = false;
    private boolean warnedLegacyResupplyCost;

    public FactionDataLoader() {
        INSTANCE = this;
    }

    public static FactionDataLoader getInstance() {
        return INSTANCE;
    }

    @Deprecated
    public void load(ResourceManager resourceManager) {
        Espetro.LOGGER.debug("\u5ffd\u7565 datapack FactionDataLoader.load\uff1b\u7f16\u5236\u4ec5\u6765\u81ea EsFactions");
    }

    public void loadExternalFrozen(Map<String, Path> files) {
        this.factions.clear();
        this.classKits.clear();
        this.factionVehicles.clear();
        this.factionVehicleTypes.clear();
        this.classesByFaction.clear();
        this.classIdsByFaction.clear();
        this.factionIdArray = EMPTY_STRING_ARRAY;
        this.factionArray = EMPTY_FACTION_ARRAY;
        for (Map.Entry<String, Path> entry : files.entrySet()) {
            String factionId = entry.getKey();
            Path file = entry.getValue();
            ResourceLocation id = ResourceLocation.m_214293_("espetro", "external_factions/" + factionId + ".json");
            if (id == null) {
                Espetro.LOGGER.error("[\u7f16\u5236\u62d2\u8f7d] {}: \u6587\u4ef6\u540d\u53ea\u80fd\u4f7f\u7528\u5c0f\u5199\u82f1\u6587\u5b57\u6bcd\u3001\u6570\u5b57\u3001_\u3001-\u3001.", (Object)file);
                continue;
            }
            try {
                String rawJson = Files.readString(file, StandardCharsets.UTF_8);
                JsonObject root = JsonParser.parseString((String)rawJson).getAsJsonObject();
                int aliasCount = 0;
                if (root.has("VehTypes")) {
                    ++aliasCount;
                }
                if (root.has("vehtypes")) {
                    ++aliasCount;
                }
                if (root.has("vehicle_types")) {
                    ++aliasCount;
                }
                if (aliasCount > 1) {
                    this.warnRejected(id, "\u540c\u65f6\u51fa\u73b0 VehTypes/vehtypes/vehicle_types \u591a\u4e2a\u522b\u540d");
                    continue;
                }
                FactionJsonData data = (FactionJsonData)GSON.fromJson((JsonElement)root, FactionJsonData.class);
                if (data == null || !this.prepareAndValidateFaction(id, factionId, data) || !this.validateVehicleDeclaration(id, data)) continue;
                this.commitFaction(factionId, data);
                Espetro.LOGGER.info("\u52a0\u8f7d\u5916\u90e8\u7f16\u5236: {} ({})", (Object)file, (Object)(data.faction != null ? data.faction.name : factionId));
            }
            catch (Exception e) {
                Espetro.LOGGER.error("[\u7f16\u5236\u62d2\u8f7d] {}: {}", new Object[]{file, e.getMessage(), e});
            }
        }
        this.rebuildLookupCaches();
        this.loaded = true;
        ClassLoadoutPreviewResolver.clearCache();
        Espetro.LOGGER.info("EsFactions \u5df2\u51bb\u7ed3: {} \u4e2a\u7f16\u5236, {} \u4e2a\u804c\u4e1a", (Object)this.factions.size(), (Object)this.classKits.size());
    }

    private void commitFaction(String factionId, FactionJsonData data) {
        this.factions.put(factionId, data.faction);
        if (data.classes != null) {
            for (Map.Entry<String, ClassKitData> classEntry : data.classes.entrySet()) {
                this.classKits.put(classEntry.getKey(), classEntry.getValue());
            }
        }
        if (data.vehicles != null) {
            this.factionVehicles.put(factionId, new LinkedHashMap<String, VehicleData>(data.vehicles));
        }
        this.factionVehicleTypes.put(factionId, data.vehicleTypes == null ? List.of() : List.copyOf(data.vehicleTypes));
    }

    private boolean validateVehicleDeclaration(ResourceLocation id, FactionJsonData data) {
        if (data.vehicleTypes == null) {
            this.warnRejected(id, "\u7f3a\u5c11 VehTypes \u6570\u7ec4");
            return false;
        }
        LinkedHashSet<String> seen = new LinkedHashSet<String>();
        ArrayList<String> normalizedTypes = new ArrayList<String>();
        for (String rawType : data.vehicleTypes) {
            String type;
            String string = type = rawType == null ? "" : rawType.trim().toLowerCase(Locale.ROOT);
            if (type.isBlank() || !seen.add(type)) {
                this.warnRejected(id, "VehTypes \u542b\u7a7a\u503c\u6216\u91cd\u590d\u7c7b\u578b");
                return false;
            }
            normalizedTypes.add(type);
        }
        data.vehicleTypes = normalizedTypes;
        if (data.vehicles == null) {
            data.vehicles = new LinkedHashMap<String, VehicleData>();
        }
        LinkedHashMap<String, VehicleData> normalizedVehicles = new LinkedHashMap<String, VehicleData>();
        for (Map.Entry<String, VehicleData> entry : data.vehicles.entrySet()) {
            String type;
            String string = type = entry.getKey() == null ? "" : entry.getKey().trim().toLowerCase(Locale.ROOT);
            if (type.isBlank() || normalizedVehicles.containsKey(type)) {
                this.warnRejected(id, "vehicles \u542b\u7a7a\u7c7b\u578b\u6216\u5927\u5c0f\u5199\u5f52\u4e00\u5316\u540e\u91cd\u590d\u7684\u7c7b\u578b");
                return false;
            }
            if (!seen.contains(type)) {
                this.warnRejected(id, "vehicles." + entry.getKey() + " \u672a\u5728 VehTypes \u4e2d\u58f0\u660e");
                return false;
            }
            VehicleData vehicle = entry.getValue();
            if (vehicle == null) {
                this.warnRejected(id, "vehicles." + entry.getKey() + " \u4e3a\u7a7a");
                return false;
            }
            if ((vehicle.entities == null || vehicle.entities.isEmpty()) && vehicle.entityTypeStr != null && !vehicle.entityTypeStr.isBlank()) {
                vehicle.entities = new ArrayList<String>(List.of(vehicle.entityTypeStr));
            }
            if (vehicle.entities == null || vehicle.entities.isEmpty()) {
                this.warnRejected(id, "vehicles." + entry.getKey() + ".entity \u5fc5\u987b\u662f\u975e\u7a7a\u6570\u7ec4");
                return false;
            }
            ArrayList<String> normalizedEntities = new ArrayList<String>();
            for (String entity : vehicle.entities) {
                if (entity == null || entity.isBlank()) {
                    this.warnRejected(id, "vehicles." + entry.getKey() + ".entity \u542b\u7a7a\u5b9e\u4f53 ID");
                    return false;
                }
                normalizedEntities.add(entity.trim());
            }
            vehicle.entities = normalizedEntities;
            vehicle.perMaxCount = Math.max(1, vehicle.perMaxCount);
            if (vehicle.vehicleCrewSeats != null && vehicle.vehicleCrewSeats < 0) {
                this.warnRejected(id, "vehicles." + entry.getKey() + ".vehicle_crew_seats \u4e0d\u80fd\u5c0f\u4e8e 0");
                return false;
            }
            if (vehicle.nbt != null) {
                String trimmedNbt = vehicle.nbt.trim();
                vehicle.nbt = trimmedNbt.isEmpty() ? null : trimmedNbt;
            }
            normalizedVehicles.put(type, vehicle);
        }
        data.vehicles = normalizedVehicles;
        return true;
    }

    public boolean isCompatibleWithMap(String factionId, ActiveMapConfig map) {
        if (map == null) {
            Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: map \u4e3a\u7a7a", (Object)factionId);
            return false;
        }
        if (!map.usable) {
            Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: map.usable=false", (Object)factionId);
            return false;
        }
        if (!this.factions.containsKey(factionId)) {
            Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: factions \u4e2d\u672a\u627e\u5230", (Object)factionId);
            return false;
        }
        List<String> declared = this.factionVehicleTypes.get(factionId);
        if (declared == null) {
            Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: factionVehicleTypes \u4e2d\u65e0\u6b64\u7f16\u5236", (Object)factionId);
            return false;
        }
        for (String type : declared) {
            if (map.vehSpawn.vehicleTypes.contains(type)) continue;
            Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: VehTypes \u4e2d '{}' \u4e0d\u5728\u5730\u56fe VehSpawn ({}) \u4e2d", new Object[]{factionId, type, map.vehSpawn.vehicleTypes});
            return false;
        }
        Map vehicles = this.factionVehicles.getOrDefault(factionId, Map.of());
        for (Map.Entry entry : vehicles.entrySet()) {
            String type = (String)entry.getKey();
            VehicleData data = (VehicleData)entry.getValue();
            List<VehSpawnSnapshot.SpawnPoint> points = map.vehSpawn.spawnPointsByType.get(type);
            if (points == null) {
                Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: \u8f7d\u5177\u7c7b\u578b '{}' \u5728\u5730\u56fe spawnPointsByType \u4e2d\u65e0\u51fa\u751f\u70b9", (Object)factionId, (Object)type);
                return false;
            }
            if (data.entities == null) {
                Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: \u8f7d\u5177\u7c7b\u578b '{}' \u7684 entities \u4e3a null", (Object)factionId, (Object)type);
                return false;
            }
            if (data.entities.size() > points.size()) {
                Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: \u8f7d\u5177\u7c7b\u578b '{}' entities({}) > spawn\u70b9\u6570({})", new Object[]{factionId, type, data.entities.size(), points.size()});
                return false;
            }
            for (String entityId : data.entities) {
                ResourceLocation rl = ResourceLocation.m_135820_(entityId);
                if (rl == null) {
                    Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: \u8f7d\u5177\u5b9e\u4f53 '{}' ResourceLocation \u89e3\u6790\u5931\u8d25", (Object)factionId, (Object)entityId);
                    return false;
                }
                if (BuiltInRegistries.f_256780_.m_7804_(rl)) continue;
                Espetro.LOGGER.warn("[\u7f16\u5236\u517c\u5bb9] {} \u2192 \u5931\u8d25: \u8f7d\u5177\u5b9e\u4f53 '{}' \u4e0d\u5728 ENTITY_TYPE \u6ce8\u518c\u8868\u4e2d", (Object)factionId, (Object)entityId);
                return false;
            }
        }
        return true;
    }

    public boolean isMapPlayable(ActiveMapConfig map) {
        boolean ok;
        HashSet<String> affiliations = new HashSet<String>();
        int compatible = 0;
        for (FactionData faction : this.factionArray) {
            if (faction == null || faction.id == null || faction.factionId == null || this.getClassesForFaction(faction.id).length == 0 || !this.isCompatibleWithMap(faction.id, map)) continue;
            ++compatible;
            affiliations.add(faction.factionId);
        }
        boolean bl = ok = affiliations.size() >= 2;
        if (!ok && map != null) {
            Espetro.LOGGER.warn("\u5730\u56fe {} \u7f16\u5236\u517c\u5bb9\u6027\u4e0d\u8db3: \u517c\u5bb9\u7f16\u5236 {} \u4e2a, \u4e0d\u540c faction_id {} \u4e2a {}\uff08\u9700\u8981\u81f3\u5c11 2 \u4e2a\u4e0d\u540c faction_id\uff09", new Object[]{map.displayName, compatible, affiliations.size(), affiliations});
        }
        return ok;
    }

    private boolean prepareAndValidateFaction(ResourceLocation resourceId, String factionId, FactionJsonData data) {
        if (data.faction == null) {
            this.warnRejected(resourceId, "\u7f3a\u5c11 faction \u8282\u70b9");
            return false;
        }
        if (data.faction.factionId == null || data.faction.factionId.isBlank()) {
            this.warnRejected(resourceId, "faction.faction_id \u7f3a\u5931\u6216\u4e3a\u7a7a");
            return false;
        }
        if (data.faction.audioPack != null && !data.faction.audioPack.isBlank()) {
            String normalizedAudioPack = AudioPackId.normalize(data.faction.audioPack);
            if (normalizedAudioPack == null) {
                this.warnRejected(resourceId, "faction.audio_pack \u5fc5\u987b\u662f EsAudio \u4e0b\u7684\u5355\u5c42\u76ee\u5f55\u540d\uff0c\u4e14\u4e0d\u80fd\u5305\u542b\u8def\u5f84\u5206\u9694\u7b26\u6216\u975e\u6cd5\u5b57\u7b26");
                return false;
            }
            data.faction.audioPack = normalizedAudioPack;
        } else {
            data.faction.audioPack = null;
        }
        data.faction.id = factionId;
        if (data.classes == null) {
            return true;
        }
        for (Map.Entry<String, ClassKitData> classEntry : data.classes.entrySet()) {
            String classId = classEntry.getKey();
            ClassKitData kit = classEntry.getValue();
            if (classId == null || classId.isBlank() || kit == null) {
                this.warnRejected(resourceId, "\u5b58\u5728\u7a7a\u804c\u4e1a ID \u6216\u7a7a\u804c\u4e1a\u914d\u7f6e");
                return false;
            }
            kit.id = classId;
            kit.factionId = factionId;
            if (kit.troopValue == 0) {
                kit.troopValue = 1;
            }
            if (kit.maxPlayers < 1) {
                this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684 maxPlayers \u5fc5\u987b\u5927\u4e8e 0");
                return false;
            }
            if (kit.teamCount) {
                if (kit.maxPerSquad > 0) {
                    Espetro.LOGGER.warn("\u7f16\u5236 {} \u7684\u804c\u4e1a {} \u5df2\u542f\u7528 team_count\uff0c\u5ffd\u7565 max_per_squad={}", new Object[]{resourceId, classId, kit.maxPerSquad});
                    kit.maxPerSquad = 0;
                }
            } else {
                if (kit.maxPerSquad > 0 && kit.maxPerSquad > kit.maxPlayers) {
                    this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684 max_per_squad (" + kit.maxPerSquad + ") \u5fc5\u987b \u2264 maxPlayers (" + kit.maxPlayers + ")");
                    return false;
                }
                if (kit.maxPerSquad < 0) {
                    kit.maxPerSquad = 0;
                }
            }
            if (kit.variants == null) {
                kit.variants = new LinkedHashMap<String, ClassVariantData>();
                ClassVariantData fallback = ClassVariantData.fromLegacy(kit);
                fallback.id = "default";
                fallback.classId = classId;
                fallback.factionId = factionId;
                if (!this.validateResupply(resourceId, classId, fallback.id, fallback.resupply)) {
                    return false;
                }
                kit.variants.put(fallback.id, fallback);
                kit.legacyImplicitVariant = true;
                continue;
            }
            if (kit.variants.isEmpty()) {
                this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684 variants \u4e0d\u53ef\u4e3a\u7a7a\uff1b\u65e7\u683c\u5f0f\u517c\u5bb9\u9700\u8981\u5b8c\u5168\u7701\u7565 variants \u5b57\u6bb5");
                return false;
            }
            if (kit.hasLegacyLoadoutFields()) {
                Espetro.LOGGER.warn("\u7f16\u5236 {} \u7684\u804c\u4e1a {} \u5df2\u914d\u7f6e variants\uff1b\u804c\u4e1a\u7ea7 commands/equipment/resupply \u5c06\u88ab\u5ffd\u7565", (Object)resourceId, (Object)classId);
            }
            long variantLimitSum = 0L;
            for (Map.Entry<String, ClassVariantData> variantEntry : kit.variants.entrySet()) {
                String variantId = variantEntry.getKey();
                ClassVariantData variant = variantEntry.getValue();
                if (variantId == null || variantId.isBlank() || variant == null) {
                    this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u5b58\u5728\u7a7a\u53d8\u4f53 ID \u6216\u7a7a\u53d8\u4f53\u914d\u7f6e");
                    return false;
                }
                if (variant.maxPlayers < 1) {
                    this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684\u53d8\u4f53 " + variantId + " maxPlayers \u5fc5\u987b\u5927\u4e8e 0");
                    return false;
                }
                variant.id = variantId;
                variant.classId = classId;
                variant.factionId = factionId;
                if (variant.name == null || variant.name.isBlank()) {
                    variant.name = variantId;
                }
                if (!this.validateResupply(resourceId, classId, variantId, variant.resupply)) {
                    return false;
                }
                variantLimitSum += (long)variant.maxPlayers;
            }
            if (!kit.strictCount || variantLimitSum == (long)kit.maxPlayers) continue;
            this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " (strict_count=true) \u7684\u53d8\u4f53\u4e0a\u9650\u603b\u548c " + variantLimitSum + " \u4e0d\u7b49\u4e8e\u804c\u4e1a\u4e0a\u9650 " + kit.maxPlayers);
            return false;
        }
        return true;
    }

    private boolean validateResupply(ResourceLocation resourceId, String classId, String variantId, ResupplyData resupply) {
        if (resupply == null) {
            return true;
        }
        if (resupply.items == null || resupply.items.length == 0) {
            this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684\u53d8\u4f53 " + variantId + " resupply.items \u4e0d\u53ef\u4e3a\u7a7a");
            return false;
        }
        if (resupply.items.length > 64) {
            this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684\u53d8\u4f53 " + variantId + " resupply.items \u8d85\u8fc7 64 \u9879\u786c\u4e0a\u9650");
            return false;
        }
        if (resupply.ammoCost != null && (resupply.ammoCost < 0 || resupply.ammoCost > 1000000)) {
            this.warnRejected(resourceId, "\u804c\u4e1a " + classId + " \u7684\u53d8\u4f53 " + variantId + " \u65e7 resupply.ammo_cost \u8d85\u51fa 0..1000000");
            return false;
        }
        if (resupply.ammoCost != null && !this.warnedLegacyResupplyCost) {
            this.warnedLegacyResupplyCost = true;
            Espetro.LOGGER.warn("resupply.ammo_cost \u9876\u5c42\u8d39\u7528\u5df2\u5f03\u7528\uff1b\u8bf7\u8fc1\u79fb\u5230\u6bcf\u4e2a items[].ammo_cost");
        }
        for (int index = 0; index < resupply.items.length; ++index) {
            String registryId;
            ResupplyItem item = resupply.items[index];
            String path = "\u804c\u4e1a " + classId + " \u7684\u53d8\u4f53 " + variantId + " resupply.items[" + index + "]";
            if (item == null || item.id == null || item.id.isBlank() || item.id.length() > 512) {
                this.warnRejected(resourceId, path + ".id \u7f3a\u5931\u6216\u8fc7\u957f");
                return false;
            }
            String rawId = item.id.trim();
            int inlineTag = rawId.indexOf(123);
            String string = registryId = inlineTag >= 0 ? rawId.substring(0, inlineTag) : rawId;
            if (ResourceLocation.m_135820_(registryId) == null) {
                this.warnRejected(resourceId, path + ".id \u4e0d\u662f\u6709\u6548 ResourceLocation");
                return false;
            }
            if (item.nbt != null && item.nbt.length() > 32768) {
                this.warnRejected(resourceId, path + ".nbt \u8d85\u8fc7 32768 \u5b57\u7b26");
                return false;
            }
            if (item.count < 1 || item.count > 1000000) {
                this.warnRejected(resourceId, path + ".count \u5fc5\u987b\u5728 1..1000000");
                return false;
            }
            if (item.max < 1 || item.max > 1000000 || item.count > item.max) {
                this.warnRejected(resourceId, path + ".max \u5fc5\u987b\u5728 1..1000000 \u4e14\u4e0d\u5c11\u4e8e count");
                return false;
            }
            if (item.ammoCost == null || item.ammoCost >= 0 && item.ammoCost <= 1000000) continue;
            this.warnRejected(resourceId, path + ".ammo_cost \u5fc5\u987b\u5728 0..1000000");
            return false;
        }
        return true;
    }

    private void warnRejected(ResourceLocation resourceId, String reason) {
        Espetro.LOGGER.warn("[\u7f16\u5236\u62d2\u8f7d] {}: {}\u3002\u8be5\u7f16\u5236\u4e0d\u4f1a\u8f7d\u5165", (Object)resourceId, (Object)reason);
    }

    public void ensureLoaded(ResourceManager resourceManager) {
        if (!this.loaded) {
            Espetro.LOGGER.debug("FactionDataLoader \u5c1a\u672a\u5916\u90e8\u51bb\u7ed3\u52a0\u8f7d\uff08ensureLoaded \u5ffd\u7565 datapack\uff09");
        }
    }

    @Deprecated
    public void reload(ResourceManager resourceManager) {
        Espetro.LOGGER.warn("\u7f16\u5236\u4e0d\u652f\u6301\u70ed\u91cd\u8f7d\uff1b\u8bf7\u91cd\u542f\u4ee5\u91cd\u65b0\u8bfb\u53d6 EsFactions/");
    }

    public FactionData getFaction(String factionId) {
        return this.factions.get(factionId);
    }

    public Collection<FactionData> getAllFactions() {
        return this.factions.values();
    }

    public String[] getAllFactionIds() {
        return this.factionIdArray;
    }

    public FactionData[] getFactionArray() {
        return this.factionArray;
    }

    public ClassKitData getClassKit(String classId) {
        return this.classKits.get(classId);
    }

    public ClassVariantData getClassVariant(String classId, String variantId) {
        ClassKitData kit = this.getClassKit(classId);
        return kit != null ? kit.getVariant(variantId) : null;
    }

    public ClassKitData[] getClassesForFaction(String factionId) {
        return this.classesByFaction.getOrDefault(factionId, EMPTY_CLASS_KIT_ARRAY);
    }

    public String[] getClassIdsForFaction(String factionId) {
        return this.classIdsByFaction.getOrDefault(factionId, EMPTY_STRING_ARRAY);
    }

    private void rebuildLookupCaches() {
        this.factionIdArray = this.factions.keySet().toArray(EMPTY_STRING_ARRAY);
        this.factionArray = this.factions.values().toArray(EMPTY_FACTION_ARRAY);
        LinkedHashMap<String, List> groupedClasses = new LinkedHashMap<String, List>();
        for (String factionId : this.factions.keySet()) {
            groupedClasses.put(factionId, new ArrayList());
        }
        LinkedHashMap<String, Integer> factionClassCounts = new LinkedHashMap<String, Integer>();
        for (ClassKitData classKitData : this.classKits.values()) {
            if (classKitData == null || classKitData.factionId == null) {
                Espetro.LOGGER.warn("[\u91cd\u5efa\u7f13\u5b58\u8bca\u65ad] \u8df3\u8fc7\u4e00\u4e2a null \u6216 factionId=null \u7684\u804c\u4e1a: kit={}", (Object)classKitData);
                continue;
            }
            groupedClasses.computeIfAbsent(classKitData.factionId, ignored -> new ArrayList()).add(classKitData);
            factionClassCounts.merge(classKitData.factionId, 1, Integer::sum);
        }
        Espetro.LOGGER.info("[\u91cd\u5efa\u7f13\u5b58\u8bca\u65ad] classKits \u4e2d\u5404\u7c7b factionId \u7684\u804c\u4e1a\u6570: {}", factionClassCounts);
        for (Map.Entry entry : groupedClasses.entrySet()) {
            List kits = (List)entry.getValue();
            ClassKitData[] kitArray = kits.toArray(EMPTY_CLASS_KIT_ARRAY);
            String[] classIds = new String[kitArray.length];
            for (int i = 0; i < kitArray.length; ++i) {
                classIds[i] = kitArray[i].id;
            }
            this.classesByFaction.put((String)entry.getKey(), kitArray);
            this.classIdsByFaction.put((String)entry.getKey(), classIds);
        }
        Espetro.LOGGER.info("[\u91cd\u5efa\u7f13\u5b58\u8bca\u65ad] classesByFaction \u7684 factionId \u96c6\u5408: {}, factions.keySet: {}", this.classesByFaction.keySet(), this.factions.keySet());
    }

    public Map<String, VehicleData> getFactionVehicles(String factionId) {
        return this.factionVehicles.getOrDefault(factionId, Collections.emptyMap());
    }

    public Map<String, Map<String, VehicleData>> getAllFactionVehicles() {
        return new LinkedHashMap<String, Map<String, VehicleData>>(this.factionVehicles);
    }

    public static class FactionData {
        public transient String id;
        public String name;
        public String description;
        public String icon;
        @SerializedName(value="selection_image", alternate={"selectionImage"})
        public String selectionImage;
        @SerializedName(value="audio_pack", alternate={"audioPack", "audio_index", "audioIndex"})
        public String audioPack;
        @SerializedName(value="faction_id")
        public String factionId;
        public String team;
        public String color = "FFFFFF";
        @SerializedName(value="show_name", alternate={"showName"})
        public String showName;
        @SerializedName(value="max_habs_per_radio", alternate={"maxHabsPerRadio"})
        public int maxHabsPerRadio = 2;
    }

    public static class FactionJsonData {
        @SerializedName(value="VehTypes", alternate={"vehtypes", "vehicle_types"})
        public List<String> vehicleTypes;
        public FactionData faction;
        public Map<String, ClassKitData> classes;
        public Map<String, VehicleData> vehicles;
    }

    public static class ClassKitData {
        public transient String id;
        public transient String factionId;
        public String name;
        public String description;
        public String role;
        public String icon;
        @SerializedName(value="vehicle_crew", alternate={"vehicleCrew"})
        public Boolean vehicleCrew;
        @SerializedName(value="IconImage", alternate={"icon_image", "iconImage"})
        public String iconImage;
        public String[] commands;
        @SerializedName(value="equipment", alternate={"equipment_slots", "equipmentSlots"})
        public Map<String, String> equipment;
        @SerializedName(value="wearable_equipment", alternate={"wearableEquipment"})
        public Map<String, String> wearableEquipment;
        @SerializedName(value="auto_equip_wearables", alternate={"autoEquipWearables"})
        public Boolean autoEquipWearables;
        public ResupplyData resupply;
        public Map<String, ClassVariantData> variants;
        public transient boolean legacyImplicitVariant;
        @SerializedName(value="strict_count", alternate={"strictCount"})
        public boolean strictCount = true;
        @SerializedName(value="team_count", alternate={"teamCount"})
        public boolean teamCount = false;
        @SerializedName(value="max_per_squad", alternate={"maxPerSquad"})
        public int maxPerSquad = 0;
        @SerializedName(value="teammates_need", alternate={"teammatesNeed"})
        public int teammatesNeed = 0;
        @SerializedName(value="row", alternate={"grid_row", "gridRow"})
        public int row = 0;
        @SerializedName(value="unlock_per_n", alternate={"unlockPerN"})
        public int unlockPerN = 0;
        @SerializedName(value="unlock_min_squad", alternate={"unlockMinSquad"})
        public int unlockMinSquad = 0;
        @SerializedName(value="leader_only", alternate={"leaderOnly"})
        public boolean leaderOnly = false;
        public int maxPlayers = 5;
        public int healthBonus = 0;
        public float speedBonus = 0.0f;
        public int troopValue = 1;

        public boolean isVehicleCrew() {
            return VehicleSeatAccessPolicy.resolvesVehicleCrew(this.vehicleCrew, this.icon);
        }

        public ClassVariantData getVariant(String variantId) {
            if (this.variants == null || this.variants.isEmpty()) {
                return null;
            }
            if (variantId == null || variantId.isBlank()) {
                return this.variants.size() == 1 ? this.variants.values().iterator().next() : null;
            }
            return this.variants.get(variantId);
        }

        public boolean hasLegacyLoadoutFields() {
            return this.commands != null && this.commands.length > 0 || this.equipment != null && !this.equipment.isEmpty() || this.wearableEquipment != null && !this.wearableEquipment.isEmpty() || this.autoEquipWearables != null || this.resupply != null;
        }
    }

    public static class VehicleData {
        public static final int DEFAULT_RESPAWN_MINUTES = 5;
        public static final int DEFAULT_MAX = 1;
        @SerializedName(value="entity_type")
        public String entityTypeStr;
        @SerializedName(value="entity")
        public List<String> entities;
        @SerializedName(value="display_name")
        public String displayName;
        public int max = 0;
        @SerializedName(value="per_max_count", alternate={"perMaxCount"})
        public int perMaxCount = 1;
        @SerializedName(value="respawn_minutes")
        public int respawnMinutes = 0;
        @SerializedName(value="troop_value", alternate={"troopValue"})
        public int troopValue = 0;
        @SerializedName(value="vehicle_crew_seats", alternate={"vehicleCrewSeats"})
        public Integer vehicleCrewSeats;
        @SerializedName(value="entity_tags", alternate={"entityTags"})
        public String[] entityTags;
        @SerializedName(value="nbt", alternate={"entity_nbt", "entityNbt"})
        public String nbt;
        public VehicleDeploymentData deployment;
        @SerializedName(value="supplyveh", alternate={"supply_veh", "supplyVeh"})
        public Boolean supplyVeh;
        @SerializedName(value="fightveh", alternate={"fight_veh", "fightVeh"})
        public Boolean fightVeh;
        public Integer capacity;
        @SerializedName(value="initial_deploy_delay_seconds", alternate={"initialDeployDelaySeconds"})
        public InitialDeployDelayData initialDeployDelay;
    }

    public static class ClassVariantData {
        public transient String id;
        public transient String classId;
        public transient String factionId;
        public String name;
        public String description;
        public int maxPlayers;
        public String[] commands;
        @SerializedName(value="equipment", alternate={"equipment_slots", "equipmentSlots"})
        public Map<String, String> equipment;
        @SerializedName(value="wearable_equipment", alternate={"wearableEquipment"})
        public Map<String, String> wearableEquipment;
        @SerializedName(value="auto_equip_wearables", alternate={"autoEquipWearables"})
        public Boolean autoEquipWearables;
        public ResupplyData resupply;

        private static ClassVariantData fromLegacy(ClassKitData kit) {
            ClassVariantData variant = new ClassVariantData();
            variant.name = "\u9ed8\u8ba4\u88c5\u5907";
            variant.description = kit.description;
            variant.maxPlayers = kit.maxPlayers;
            variant.commands = kit.commands;
            variant.equipment = kit.equipment;
            variant.wearableEquipment = kit.wearableEquipment;
            variant.autoEquipWearables = kit.autoEquipWearables;
            variant.resupply = kit.resupply;
            return variant;
        }
    }

    public static class ResupplyData {
        public ResupplyItem[] items;
        @SerializedName(value="ammo_cost", alternate={"ammoCost"})
        public Integer ammoCost;
    }

    public static class ResupplyItem {
        public String id;
        public String nbt;
        public int count = 16;
        public int max = 64;
        @SerializedName(value="ammo_cost", alternate={"ammoCost"})
        public Integer ammoCost;
    }

    public static class VehicleDeploymentPointData {
        public int[] position;
        public Float yaw;
    }

    public static class VehicleDeploymentData {
        @SerializedName(value="ATTACK", alternate={"attack"})
        public VehicleDeploymentPointData attack;
        @SerializedName(value="DEFEND", alternate={"defend"})
        public VehicleDeploymentPointData defend;
    }

    public static class InitialDeployDelayData {
        @SerializedName(value="attack", alternate={"ATTACK"})
        public int attack;
        @SerializedName(value="defend", alternate={"DEFEND"})
        public int defend;
    }
}

