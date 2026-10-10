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
 *  com.google.gson.JsonPrimitive
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraftforge.common.data.ExistingFileHelper
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class GeoOBBDataProvider
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;

    public GeoOBBDataProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    public CompletableFuture<?> m_213708_(CachedOutput pOutput) {
        return CompletableFuture.runAsync(() -> {
            try {
                DirectoryStream<Path> stream;
                String workingDir = System.getProperty("user.dir").replace("\\run-data", "").replace("/run-data", "");
                Path geoPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/geo");
                Path bedrockPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/models/bedrock/vehicle");
                Path dragonriseOutputPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/data/dragonrise_reforge/sbw/vehicles");
                Path superbwarfareOutputPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/superbwarfare/sbw/vehicles");
                Path dragonriseAssetOutputPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/sbw/vehicles");
                if (!Files.exists(geoPath, new LinkOption[0]) && !Files.exists(bedrockPath, new LinkOption[0])) {
                    return;
                }
                LinkedHashSet<Path> inputFiles = new LinkedHashSet<Path>();
                if (Files.exists(geoPath, new LinkOption[0])) {
                    stream = Files.newDirectoryStream(geoPath, "*.geo.json");
                    try {
                        for (Path p : stream) {
                            inputFiles.add(p);
                        }
                    }
                    finally {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                }
                if (Files.exists(bedrockPath, new LinkOption[0])) {
                    stream = Files.newDirectoryStream(bedrockPath, "*.geo.json");
                    try {
                        for (Path p : stream) {
                            boolean duplicate = inputFiles.stream().anyMatch(e -> e.getFileName().equals(p.getFileName()));
                            if (duplicate) continue;
                            inputFiles.add(p);
                        }
                    }
                    finally {
                        if (stream != null) {
                            stream.close();
                        }
                    }
                }
                for (Path geoFile : inputFiles) {
                    this.processGeoFile(geoFile, dragonriseOutputPath, superbwarfareOutputPath, dragonriseAssetOutputPath);
                }
            }
            catch (Exception e2) {
                throw new RuntimeException("Failed to process geo files", e2);
            }
        });
    }

    /*
     * WARNING - void declaration
     */
    private void processGeoFile(Path geoFile, Path dragonriseOutputPath, Path superbwarfareOutputPath, Path dragonriseAssetOutputPath) {
        JsonObject geoJson;
        String content;
        try {
            content = Files.readString(geoFile);
        }
        catch (Exception e) {
            System.err.println("GeoOBBDataProvider: cannot read " + geoFile.getFileName() + ", skipped: " + e.getMessage());
            return;
        }
        try {
            geoJson = JsonParser.parseString((String)content).getAsJsonObject();
        }
        catch (Exception e) {
            System.err.println("GeoOBBDataProvider: malformed model " + geoFile.getFileName() + " (" + e.getMessage() + "), skipped");
            return;
        }
        try {
            boolean hasExtractableData;
            if (!GeoOBBDataProvider.hasBuildUnderObb(geoJson)) {
                return;
            }
            boolean hasSeatsPos1 = this.checkSeatsPos1(geoJson);
            boolean isAircraft = GeoOBBDataProvider.isAircraft(geoJson);
            double turretCustomPitch = this.extractTurretCustomPitch(geoJson);
            JsonArray turretPos = hasSeatsPos1 ? this.extractTurretPos(geoJson) : null;
            double turretPivotY = turretPos != null ? turretPos.get(1).getAsDouble() * 16.0 : 0.0;
            JsonArray barrelPos = hasSeatsPos1 ? this.extractBarrelPos(geoJson, turretPos, turretCustomPitch) : null;
            double barrelPosY = barrelPos != null ? barrelPos.get(1).getAsDouble() : 0.0;
            double barrelPivotY = barrelPosY * 16.0;
            JsonArray obbList = this.extractOBBList(geoJson, turretPos, turretCustomPitch);
            Map<String, JsonArray> weaponPositions = this.extractWeaponPositions(geoJson, barrelPivotY, turretPivotY, turretCustomPitch);
            Map<Integer, JsonArray> seatsPositions = this.extractSeatsPositions(geoJson);
            Map<Integer, Double> seatsOrientations = this.extractSeatsOrientations(geoJson);
            Map<Integer, JsonArray> seatsCameraPositions = this.extractSeatsCameraPositions(geoJson);
            List<JsonArray> terrainCompatPositions = this.extractTerrainCompatPositions(geoJson);
            String baseName = geoFile.getFileName().toString().replace(".geo.json", "");
            Path vehicleFile = dragonriseOutputPath.resolve(baseName + ".json");
            if (Files.exists(vehicleFile, new LinkOption[0])) {
                try {
                    String vehicleContent = Files.readString(vehicleFile);
                    JsonObject existingVehicleJson = JsonParser.parseString((String)vehicleContent).getAsJsonObject();
                    if (existingVehicleJson.has("VehicleIcon")) {
                        String iconPath = "dragonrise_reforge:textures/vehicle_icon/" + baseName + "_icon.png";
                        existingVehicleJson.addProperty("VehicleIcon", iconPath);
                        Files.writeString(vehicleFile, (CharSequence)GeoOBBDataProvider.compactJson(GSON.toJson((JsonElement)existingVehicleJson)), new OpenOption[0]);
                    }
                }
                catch (Exception vehicleContent) {
                    // empty catch block
                }
            }
            boolean bl = hasExtractableData = !obbList.isEmpty() || turretPos != null || barrelPos != null || !weaponPositions.isEmpty() || !seatsPositions.isEmpty() || !seatsCameraPositions.isEmpty() || !terrainCompatPositions.isEmpty() || isAircraft;
            if (hasExtractableData) {
                JsonArray[] pwsChain;
                Object shootPos;
                JsonObject weapon;
                JsonObject vehicleJson;
                if (Files.exists(vehicleFile, new LinkOption[0])) {
                    String vehicleContent = Files.readString(vehicleFile);
                    vehicleJson = JsonParser.parseString((String)vehicleContent).getAsJsonObject();
                } else {
                    vehicleJson = new JsonObject();
                    vehicleJson.addProperty("ID", "dragonrise_reforge:" + baseName);
                }
                if (!obbList.isEmpty()) {
                    vehicleJson.add("OBB", (JsonElement)obbList);
                }
                if (hasSeatsPos1 && turretPos != null) {
                    vehicleJson.add("TurretPos", (JsonElement)turretPos);
                }
                if (hasSeatsPos1 && barrelPos != null) {
                    vehicleJson.add("BarrelPos", (JsonElement)barrelPos);
                }
                if (!weaponPositions.isEmpty() && vehicleJson.has("Weapons")) {
                    JsonObject weapons = vehicleJson.getAsJsonObject("Weapons");
                    for (Map.Entry<String, JsonArray> entry : weaponPositions.entrySet()) {
                        String string = entry.getKey();
                        JsonArray jsonArray = entry.getValue();
                        if (!weapons.has(string)) continue;
                        weapon = weapons.getAsJsonObject(string);
                        if (weapon.has("ShootPos")) {
                            shootPos = weapon.getAsJsonObject("ShootPos");
                        } else {
                            shootPos = new JsonObject();
                            weapon.add("ShootPos", (JsonElement)shootPos);
                        }
                        String string2 = shootPos.has("Transform") ? shootPos.get("Transform").getAsString() : "";
                        JsonArray adjustedPositions = new JsonArray();
                        for (JsonElement posElement : jsonArray) {
                            JsonArray pos = posElement.getAsJsonArray();
                            JsonArray adjustedPos = new JsonArray();
                            adjustedPos.add(pos.get(0));
                            adjustedPos.add(pos.get(1));
                            adjustedPos.add(pos.get(2));
                            adjustedPositions.add((JsonElement)adjustedPos);
                        }
                        if (hasSeatsPos1 && !isAircraft) {
                            if ("Barrel".equals(string2) && barrelPos != null && turretPos != null) {
                                double barrelY = barrelPos.get(1).getAsDouble();
                                double turretY = turretPos.get(1).getAsDouble();
                                for (int i = 0; i < adjustedPositions.size(); ++i) {
                                    JsonArray pos = adjustedPositions.get(i).getAsJsonArray();
                                    this.untiltAroundTurret(pos, turretPos, turretCustomPitch);
                                    double y = pos.get(1).getAsDouble();
                                    y = y - barrelY - turretY;
                                    pos.set(1, (JsonElement)new JsonPrimitive((Number)this.round(y, 3)));
                                }
                            } else if ("Turret".equals(string2) && turretPos != null) {
                                double turretY = turretPos.get(1).getAsDouble();
                                for (int i = 0; i < adjustedPositions.size(); ++i) {
                                    JsonArray pos = adjustedPositions.get(i).getAsJsonArray();
                                    this.untiltAroundTurret(pos, turretPos, turretCustomPitch);
                                    double y = pos.get(1).getAsDouble();
                                    pos.set(1, (JsonElement)new JsonPrimitive((Number)this.round(y -= turretY, 3)));
                                }
                            }
                        }
                        shootPos.add("Positions", (JsonElement)adjustedPositions);
                    }
                }
                if (isAircraft && vehicleJson.has("Weapons")) {
                    Map<String, JsonArray> dummyWeaponPositions = this.extractDummyWeaponPositions(geoJson, vehicleJson);
                    JsonObject weapons = vehicleJson.getAsJsonObject("Weapons");
                    for (Map.Entry<String, JsonArray> entry : dummyWeaponPositions.entrySet()) {
                        String string = entry.getKey();
                        if (!weapons.has(string)) continue;
                        weapon = weapons.getAsJsonObject(string);
                        if (weapon.has("ShootPos")) {
                            shootPos = weapon.getAsJsonObject("ShootPos");
                        } else {
                            shootPos = new JsonObject();
                            weapon.add("ShootPos", (JsonElement)shootPos);
                        }
                        shootPos.add("Positions", (JsonElement)entry.getValue());
                    }
                    Map<String, JsonArray> map = this.extractNacellePositions(geoJson);
                    for (Map.Entry<String, JsonArray> entry : map.entrySet()) {
                        List<Object> targets = "__ALL__".equals(entry.getKey()) ? new ArrayList(weapons.keySet()) : Collections.singletonList(entry.getKey());
                        for (String string : targets) {
                            JsonObject shootPos2;
                            if (!weapons.has(string)) continue;
                            JsonObject weapon2 = weapons.getAsJsonObject(string);
                            if (weapon2.has("ShootPos")) {
                                JsonObject shootPos22 = weapon2.getAsJsonObject("ShootPos");
                            } else {
                                shootPos2 = new JsonObject();
                                weapon2.add("ShootPos", (JsonElement)shootPos2);
                            }
                            if (!weapon2.has("UseNacelleCamera") || !weapon2.get("UseNacelleCamera").getAsBoolean() || entry.getValue().size() <= 0) continue;
                            shootPos2.add("ViewPosition", (JsonElement)entry.getValue().get(0).getAsJsonArray());
                        }
                    }
                }
                if (hasSeatsPos1 && !seatsPositions.isEmpty() && vehicleJson.has("Seats")) {
                    JsonElement seatsElement = vehicleJson.get("Seats");
                    if (seatsElement.isJsonArray()) {
                        void var31_37;
                        JsonArray seats = seatsElement.getAsJsonArray();
                        boolean bl2 = false;
                        while (var31_37 < seats.size()) {
                            JsonObject jsonObject = seats.get((int)var31_37).getAsJsonObject();
                            void var33_54 = var31_37 + true;
                            if (seatsPositions.containsKey((int)var33_54)) {
                                String seatTransform;
                                JsonArray pos = seatsPositions.get((int)var33_54);
                                String string = seatTransform = jsonObject.has("Transform") ? jsonObject.get("Transform").getAsString() : "";
                                if (("Turret".equals(seatTransform) || "WeaponStation".equals(seatTransform)) && turretPos != null) {
                                    JsonArray jsonArray = new JsonArray();
                                    jsonArray.add((Number)this.round(pos.get(0).getAsDouble() - turretPos.get(0).getAsDouble(), 3));
                                    jsonArray.add((Number)this.round(pos.get(1).getAsDouble() - turretPos.get(1).getAsDouble(), 3));
                                    jsonArray.add((Number)this.round(pos.get(2).getAsDouble() - turretPos.get(2).getAsDouble(), 3));
                                    jsonObject.add("Position", (JsonElement)jsonArray);
                                } else {
                                    jsonObject.add("Position", (JsonElement)pos);
                                }
                                if (seatsOrientations.containsKey((int)var33_54)) {
                                    jsonObject.addProperty("Orientation", (Number)this.round(seatsOrientations.get((int)var33_54), 1));
                                }
                            }
                            if (seatsCameraPositions.containsKey((int)var33_54)) {
                                this.applyCameraPos(jsonObject, (int)var33_54, seatsPositions, seatsCameraPositions, turretPos, isAircraft);
                            }
                            ++var31_37;
                        }
                    } else if (seatsElement.isJsonObject() && seatsPositions.containsKey(1)) {
                        String string;
                        JsonObject seat = seatsElement.getAsJsonObject();
                        JsonArray jsonArray = seatsPositions.get(1);
                        String string3 = string = seat.has("Transform") ? seat.get("Transform").getAsString() : "";
                        if (("Turret".equals(string) || "WeaponStation".equals(string)) && turretPos != null) {
                            JsonArray jsonArray2 = new JsonArray();
                            jsonArray2.add((Number)this.round(jsonArray.get(0).getAsDouble() - turretPos.get(0).getAsDouble(), 3));
                            jsonArray2.add((Number)this.round(jsonArray.get(1).getAsDouble() - turretPos.get(1).getAsDouble(), 3));
                            jsonArray2.add((Number)this.round(jsonArray.get(2).getAsDouble() - turretPos.get(2).getAsDouble(), 3));
                            seat.add("Position", (JsonElement)jsonArray2);
                        } else {
                            seat.add("Position", (JsonElement)jsonArray);
                        }
                        if (seatsOrientations.containsKey(1)) {
                            seat.addProperty("Orientation", (Number)this.round(seatsOrientations.get(1), 1));
                        }
                        if (seatsCameraPositions.containsKey(1)) {
                            this.applyCameraPos(seat, 1, seatsPositions, seatsCameraPositions, turretPos, isAircraft);
                        }
                    }
                }
                if (!terrainCompatPositions.isEmpty()) {
                    JsonArray terrainCompatArray = new JsonArray();
                    for (JsonArray jsonArray : terrainCompatPositions) {
                        terrainCompatArray.add((JsonElement)jsonArray);
                    }
                    vehicleJson.add("TerrainCompat", (JsonElement)terrainCompatArray);
                }
                if ((pwsChain = this.extractPassengerWeaponStationChain(geoJson, turretCustomPitch)) != null) {
                    vehicleJson.add("PassengerWeaponStationPos", (JsonElement)pwsChain[0]);
                    vehicleJson.add("PassengerWeaponStationBarrelPos", (JsonElement)pwsChain[1]);
                }
                if (turretCustomPitch != 0.0) {
                    vehicleJson.addProperty("TurretCustomPitch", (Number)this.round(turretCustomPitch, 3));
                }
                GeoOBBDataProvider.applyModelPaths(vehicleJson, baseName);
                Files.createDirectories(vehicleFile.getParent(), new FileAttribute[0]);
                Files.writeString(vehicleFile, (CharSequence)GeoOBBDataProvider.compactJson(GSON.toJson((JsonElement)vehicleJson)), new OpenOption[0]);
            }
            this.generateSuperbwarfareVehicleConfig(superbwarfareOutputPath, baseName);
            this.generateSuperbwarfareVehicleConfig(dragonriseAssetOutputPath, baseName);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to process: " + geoFile.getFileName(), e);
        }
    }

    private static boolean hasBuildUnderObb(JsonObject geoJson) {
        if (!geoJson.has("minecraft:geometry")) {
            return false;
        }
        HashMap<String, JsonObject> boneMap = new HashMap<String, JsonObject>();
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                boneMap.put(bone.get("name").getAsString(), bone);
            }
        }
        block2: for (JsonObject bone : boneMap.values()) {
            if (!bone.get("name").getAsString().equals("Build")) continue;
            String parent = bone.has("parent") ? bone.get("parent").getAsString() : null;
            HashSet<String> visited = new HashSet<String>();
            while (parent != null && visited.add(parent)) {
                if (parent.toLowerCase().contains("obb")) {
                    return true;
                }
                JsonObject parentBone = (JsonObject)boneMap.get(parent);
                if (parentBone == null) continue block2;
                parent = parentBone.has("parent") ? parentBone.get("parent").getAsString() : null;
            }
        }
        return false;
    }

    private static boolean hasBuildBone(JsonObject geoJson) {
        if (!geoJson.has("minecraft:geometry")) {
            return false;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonObject bone = boneElement.getAsJsonObject();
                if (!bone.get("name").getAsString().equals("Build")) continue;
                return true;
            }
        }
        return false;
    }

    private static boolean isAircraft(JsonObject geoJson) {
        if (!geoJson.has("minecraft:geometry")) {
            return false;
        }
        HashMap<String, JsonObject> boneMap = new HashMap<String, JsonObject>();
        boolean hasPlane = false;
        boolean hasObb = false;
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                String name = bone.get("name").getAsString();
                boneMap.put(name, bone);
                if (name.equalsIgnoreCase("Plane")) {
                    hasPlane = true;
                }
                if (!name.toLowerCase().contains("obb")) continue;
                hasObb = true;
            }
        }
        if (!hasPlane || !hasObb) {
            return false;
        }
        block2: for (JsonObject bone : boneMap.values()) {
            if (!bone.get("name").getAsString().equalsIgnoreCase("Plane")) continue;
            String parent = bone.has("parent") ? bone.get("parent").getAsString() : null;
            HashSet<String> visited = new HashSet<String>();
            while (parent != null && visited.add(parent)) {
                if (parent.toLowerCase().contains("obb")) {
                    return true;
                }
                JsonObject parentBone = (JsonObject)boneMap.get(parent);
                if (parentBone == null) continue block2;
                parent = parentBone.has("parent") ? parentBone.get("parent").getAsString() : null;
            }
        }
        return true;
    }

    private static boolean hasObbBone(JsonObject geoJson) {
        if (!geoJson.has("minecraft:geometry")) {
            return false;
        }
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                if (!bone.get("name").getAsString().toLowerCase().contains("obb")) continue;
                return true;
            }
        }
        return false;
    }

    private void generateSuperbwarfareVehicleConfig(Path outputPath, String baseName) throws Exception {
        JsonObject vehicleJson;
        Path vehicleFile = outputPath.resolve(baseName + ".json");
        if (Files.exists(vehicleFile, new LinkOption[0])) {
            String vehicleContent = Files.readString(vehicleFile);
            vehicleJson = JsonParser.parseString((String)vehicleContent).getAsJsonObject();
        } else {
            vehicleJson = new JsonObject();
            vehicleJson.addProperty("ID", "dragonrise_reforge:" + baseName);
        }
        GeoOBBDataProvider.applyModelPaths(vehicleJson, baseName);
        Files.createDirectories(vehicleFile.getParent(), new FileAttribute[0]);
        Files.writeString(vehicleFile, (CharSequence)GeoOBBDataProvider.compactJson(GSON.toJson((JsonElement)vehicleJson)), new OpenOption[0]);
    }

    private static void applyModelPaths(JsonObject vehicleJson, String baseName) {
        String modelPath = "dragonrise_reforge:models/bedrock/vehicle/" + baseName + ".geo.json";
        String texturePath = "dragonrise_reforge:textures/entity/" + baseName + ".png";
        if (!vehicleJson.has("Model")) {
            model = new JsonObject();
            model.addProperty("Model", modelPath);
            model.addProperty("Texture", texturePath);
            vehicleJson.add("Model", (JsonElement)model);
        } else {
            model = vehicleJson.getAsJsonObject("Model");
            if (!model.has("Model")) {
                model.addProperty("Model", modelPath);
            }
            if (!model.has("Texture")) {
                model.addProperty("Texture", texturePath);
            }
        }
        if (!vehicleJson.has("Models")) {
            JsonArray models = new JsonArray();
            JsonObject entry = new JsonObject();
            entry.addProperty("Model", modelPath);
            entry.addProperty("Texture", texturePath);
            models.add((JsonElement)entry);
            vehicleJson.add("Models", (JsonElement)models);
        } else {
            boolean pointsToSelf = false;
            JsonArray models = vehicleJson.getAsJsonArray("Models");
            for (JsonElement e : models) {
                JsonObject o = e.getAsJsonObject();
                if (!o.has("Model") || !o.get("Model").getAsString().endsWith(baseName + ".geo.json")) continue;
                pointsToSelf = true;
                break;
            }
            if (!pointsToSelf) {
                JsonArray correct = new JsonArray();
                JsonObject entry = new JsonObject();
                entry.addProperty("Model", modelPath);
                entry.addProperty("Texture", texturePath);
                correct.add((JsonElement)entry);
                vehicleJson.add("Models", (JsonElement)correct);
            }
        }
    }

    private static String compactJson(String json) {
        Pattern pattern = Pattern.compile("\\[\\s*\\n((?:\\s*[-\\d.]+,\\s*\\n)*\\s*[-\\d.]+\\s*)\\]");
        Matcher matcher = pattern.matcher(json);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String inner = matcher.group(1).replaceAll("\\s+", "");
            matcher.appendReplacement(sb, "[" + inner + "]");
        }
        matcher.appendTail(sb);
        return sb.toString();
    }

    private JsonArray extractOBBList(JsonObject geoJson, JsonArray turretPos, double theta) {
        double turretPivotZ;
        JsonArray obbList = new JsonArray();
        double turretPivotX = turretPos != null ? turretPos.get(0).getAsDouble() * 16.0 : 0.0;
        double turretPivotY = turretPos != null ? turretPos.get(1).getAsDouble() * 16.0 : 0.0;
        double d = turretPivotZ = turretPos != null ? -turretPos.get(2).getAsDouble() * 16.0 : 0.0;
        if (!geoJson.has("minecraft:geometry")) {
            return obbList;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonArray rotation;
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.toLowerCase().contains("obb") || boneName.equals("Obb")) continue;
                JsonObject obbEntry = new JsonObject();
                obbEntry.add("Size", (JsonElement)this.extractSize(bone));
                obbEntry.add("Position", (JsonElement)this.extractOBBPosition(bone, turretPivotX, turretPivotY, turretPivotZ, boneName, theta));
                if (bone.has("rotation") && (rotation = bone.getAsJsonArray("rotation")).size() >= 3) {
                    double rx = rotation.get(0).getAsDouble();
                    double ry = -rotation.get(1).getAsDouble();
                    double rz = -rotation.get(2).getAsDouble();
                    if (rx != 0.0 || ry != 0.0 || rz != 0.0) {
                        JsonArray customRotate = new JsonArray();
                        customRotate.add((Number)this.round(rx, 3));
                        customRotate.add((Number)this.round(ry, 3));
                        customRotate.add((Number)this.round(rz, 3));
                        obbEntry.add("CustomRotate", (JsonElement)customRotate);
                    }
                }
                if (boneName.startsWith("MainEngineObb")) {
                    obbEntry.addProperty("Part", "MainEngine");
                } else if (boneName.startsWith("WheelRightObb")) {
                    obbEntry.addProperty("Part", "WheelRight");
                } else if (boneName.startsWith("WheelLeftObb")) {
                    obbEntry.addProperty("Part", "WheelLeft");
                } else if (boneName.startsWith("TurretObb")) {
                    obbEntry.addProperty("Part", "Turret");
                    if (!boneName.equals("TurretObb")) {
                        obbEntry.addProperty("Transform", "Turret");
                        obbEntry.addProperty("Rotation", "Turret");
                    }
                } else if (boneName.contains("CollisionObb")) {
                    obbEntry.addProperty("Part", "Collision");
                }
                obbList.add((JsonElement)obbEntry);
            }
        }
        return obbList;
    }

    private JsonArray extractOBBPosition(JsonObject bone, double turretPivotX, double turretPivotY, double turretPivotZ, String boneName, double theta) {
        JsonArray position = new JsonArray();
        if (bone.has("pivot")) {
            JsonArray pivot = bone.getAsJsonArray("pivot");
            double xValue = pivot.get(0).getAsDouble();
            double yValue = pivot.get(1).getAsDouble();
            double zValue = pivot.get(2).getAsDouble();
            if (boneName.startsWith("TurretObb") && !boneName.equals("TurretObb")) {
                xValue -= turretPivotX;
                yValue -= turretPivotY;
                zValue -= turretPivotZ;
                if (theta != 0.0) {
                    double rad = Math.toRadians(theta);
                    double c = Math.cos(rad);
                    double s = Math.sin(rad);
                    double ny = yValue * c + zValue * s;
                    double nz = -yValue * s + zValue * c;
                    yValue = ny;
                    zValue = nz;
                }
            }
            position.add((Number)this.round(xValue / 16.0, 3));
            position.add((Number)this.round(yValue / 16.0, 3));
            position.add((Number)this.round(-zValue / 16.0, 3));
        }
        return position;
    }

    private double extractTurretCustomPitch(JsonObject geoJson) {
        JsonObject tiltBone;
        if (!geoJson.has("minecraft:geometry")) {
            return 0.0;
        }
        HashMap<String, JsonObject> boneMap = new HashMap<String, JsonObject>();
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                boneMap.put(bone.get("name").getAsString(), bone);
            }
        }
        JsonObject turret = (JsonObject)boneMap.get("turret");
        if (turret == null) {
            return 0.0;
        }
        JsonArray rotation = null;
        if (turret.has("parent") && (tiltBone = (JsonObject)boneMap.get(turret.get("parent").getAsString())) != null && tiltBone.has("rotation")) {
            rotation = tiltBone.getAsJsonArray("rotation");
        }
        if (rotation == null && turret.has("rotation")) {
            rotation = turret.getAsJsonArray("rotation");
        }
        return rotation != null && rotation.size() >= 1 ? rotation.get(0).getAsDouble() : 0.0;
    }

    private void untiltAroundTurret(JsonArray pos, JsonArray turretPos, double theta) {
        if (theta == 0.0 || turretPos == null) {
            return;
        }
        double turretX = turretPos.get(0).getAsDouble() * 16.0;
        double turretY = turretPos.get(1).getAsDouble() * 16.0;
        double turretZ = -turretPos.get(2).getAsDouble() * 16.0;
        double mx = pos.get(0).getAsDouble() * 16.0;
        double my = pos.get(1).getAsDouble() * 16.0;
        double mz = -pos.get(2).getAsDouble() * 16.0;
        double ox = mx - turretX;
        double oy = my - turretY;
        double oz = mz - turretZ;
        double rad = Math.toRadians(theta);
        double c = Math.cos(rad);
        double s = Math.sin(rad);
        double ny = oy * c + oz * s;
        double nz = -oy * s + oz * c;
        pos.set(0, (JsonElement)new JsonPrimitive((Number)this.round((ox + turretX) / 16.0, 3)));
        pos.set(1, (JsonElement)new JsonPrimitive((Number)this.round((ny + turretY) / 16.0, 3)));
        pos.set(2, (JsonElement)new JsonPrimitive((Number)this.round(-(nz + turretZ) / 16.0, 3)));
    }

    private JsonArray extractTurretPos(JsonObject geoJson) {
        if (!geoJson.has("minecraft:geometry")) {
            return null;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.equalsIgnoreCase("turret") || !bone.has("pivot")) continue;
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray position = new JsonArray();
                position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                return position;
            }
        }
        return null;
    }

    private JsonArray extractBarrelPos(JsonObject geoJson, JsonArray turretPos, double theta) {
        if (!geoJson.has("minecraft:geometry")) {
            return null;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.equalsIgnoreCase("barrel") || !bone.has("pivot")) continue;
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray position = new JsonArray();
                double turretX = turretPos != null ? turretPos.get(0).getAsDouble() * 16.0 : 0.0;
                double turretY = turretPos != null ? turretPos.get(1).getAsDouble() * 16.0 : 0.0;
                double turretZ = turretPos != null ? -turretPos.get(2).getAsDouble() * 16.0 : 0.0;
                double ox = pivot.get(0).getAsDouble() - turretX;
                double oy = pivot.get(1).getAsDouble() - turretY;
                double oz = pivot.get(2).getAsDouble() - turretZ;
                if (theta != 0.0) {
                    double rad = Math.toRadians(theta);
                    double c = Math.cos(rad);
                    double s = Math.sin(rad);
                    double ny = oy * c + oz * s;
                    double nz = -oy * s + oz * c;
                    oy = ny;
                    oz = nz;
                }
                position.add((Number)this.round(ox / 16.0, 3));
                position.add((Number)this.round(oy / 16.0, 3));
                position.add((Number)this.round(-oz / 16.0, 3));
                return position;
            }
        }
        return null;
    }

    private Map<String, JsonArray> extractWeaponPositions(JsonObject geoJson, double barrelY, double turretY, double theta) {
        JsonObject geometry;
        HashMap<String, JsonArray> weaponPositions = new HashMap<String, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return weaponPositions;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        double[] pitchPivot = null;
        for (JsonElement geomElement : geometries) {
            geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                if (!bone.get("name").getAsString().equals("passengerWeaponStationPitch") || !bone.has("pivot")) continue;
                JsonArray pv = bone.getAsJsonArray("pivot");
                pitchPivot = new double[]{pv.get(0).getAsDouble(), pv.get(1).getAsDouble(), pv.get(2).getAsDouble()};
                break;
            }
            if (pitchPivot == null) continue;
            break;
        }
        for (JsonElement geomElement : geometries) {
            geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (boneName.startsWith("CannonPos") && bone.has("pivot")) {
                    this.addWeaponPosition(weaponPositions, "Cannon", boneName, "CannonPos", bone, barrelY, turretY);
                    continue;
                }
                if (boneName.startsWith("MachineGunPos") && bone.has("pivot")) {
                    this.addWeaponPosition(weaponPositions, "MachineGun", boneName, "MachineGunPos", bone, barrelY, turretY);
                    continue;
                }
                if (boneName.startsWith("MissilePos") && bone.has("pivot")) {
                    this.addWeaponPosition(weaponPositions, "Missile", boneName, "MissilePos", bone, barrelY, turretY);
                    continue;
                }
                if (boneName.startsWith("BombPos") && bone.has("pivot")) {
                    this.addWeaponPosition(weaponPositions, "Bomb", boneName, "BombPos", bone, barrelY, turretY);
                    continue;
                }
                if (boneName.startsWith("RocketPos") && bone.has("pivot")) {
                    this.addWeaponPosition(weaponPositions, "Rocket", boneName, "RocketPos", bone, barrelY, turretY);
                    continue;
                }
                if (!boneName.startsWith("PassengerMachineGunPos") || !bone.has("pivot")) continue;
                if (pitchPivot != null) {
                    JsonArray pivot = bone.getAsJsonArray("pivot");
                    double ox = pivot.get(0).getAsDouble() - pitchPivot[0];
                    double oy = pivot.get(1).getAsDouble() - pitchPivot[1];
                    double oz = pivot.get(2).getAsDouble() - pitchPivot[2];
                    if (theta != 0.0) {
                        double rad = Math.toRadians(theta);
                        double c = Math.cos(rad);
                        double s = Math.sin(rad);
                        double ny = oy * c + oz * s;
                        double nz = -oy * s + oz * c;
                        oy = ny;
                        oz = nz;
                    }
                    JsonArray pos = new JsonArray();
                    pos.add((Number)this.round(ox / 16.0, 3));
                    pos.add((Number)this.round(oy / 16.0, 3));
                    pos.add((Number)this.round(-oz / 16.0, 3));
                    weaponPositions.computeIfAbsent("PassengerMachineGun", k -> new JsonArray()).add((JsonElement)pos);
                    continue;
                }
                JsonArray pos = new JsonArray();
                this.addWeaponPosition(weaponPositions, "PassengerMachineGun", boneName, "PassengerMachineGunPos", bone, barrelY, turretY);
            }
        }
        return weaponPositions;
    }

    private Map<String, JsonArray> extractDummyWeaponPositions(JsonObject geoJson, JsonObject vehicleJson) {
        HashMap<String, JsonArray> weaponPositions = new HashMap<String, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return weaponPositions;
        }
        HashMap seatWeapons = new HashMap();
        if (vehicleJson != null && vehicleJson.has("Seats")) {
            JsonObject seat;
            JsonElement seatsElement = vehicleJson.get("Seats");
            if (seatsElement.isJsonArray()) {
                JsonArray seats = seatsElement.getAsJsonArray();
                for (int i = 0; i < seats.size(); ++i) {
                    JsonObject seat2 = seats.get(i).getAsJsonObject();
                    if (!seat2.has("Weapons") || !seat2.get("Weapons").isJsonArray()) continue;
                    ArrayList<String> names = new ArrayList<String>();
                    for (JsonElement w : seat2.getAsJsonArray("Weapons")) {
                        names.add(w.getAsString());
                    }
                    seatWeapons.put(i + 1, names);
                }
            } else if (seatsElement.isJsonObject() && (seat = seatsElement.getAsJsonObject()).has("Weapons") && seat.get("Weapons").isJsonArray()) {
                ArrayList<String> names = new ArrayList<String>();
                for (JsonElement w : seat.getAsJsonArray("Weapons")) {
                    names.add(w.getAsString());
                }
                seatWeapons.put(1, names);
            }
        }
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                List weapons;
                int weaponIndex;
                int seatIndex;
                String[] parts;
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.startsWith("dummy_") || !bone.has("pivot") || (parts = boneName.split("_")).length < 4) continue;
                try {
                    seatIndex = Integer.parseInt(parts[1]);
                    weaponIndex = Integer.parseInt(parts[2]);
                }
                catch (NumberFormatException e) {
                    continue;
                }
                if (seatIndex < 0 || weaponIndex < 0 || (weapons = (List)seatWeapons.get(seatIndex + 1)) == null || weaponIndex >= weapons.size()) continue;
                String weaponName = (String)weapons.get(weaponIndex);
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray pos = new JsonArray();
                pos.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                pos.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
                pos.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                weaponPositions.computeIfAbsent(weaponName, k -> new JsonArray()).add((JsonElement)pos);
            }
        }
        return weaponPositions;
    }

    private Map<String, JsonArray> extractNacellePositions(JsonObject geoJson) {
        HashMap<String, JsonArray> nacellePositions = new HashMap<String, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return nacellePositions;
        }
        JsonArray defaultPositions = new JsonArray();
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.startsWith("NacellePos") || !bone.has("pivot")) continue;
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray pos = new JsonArray();
                pos.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                pos.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
                pos.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                String weaponName = boneName.substring("NacellePos".length());
                if (weaponName.isEmpty()) {
                    defaultPositions.add((JsonElement)pos);
                    continue;
                }
                nacellePositions.computeIfAbsent(weaponName, k -> new JsonArray()).add((JsonElement)pos);
            }
        }
        if (!defaultPositions.isEmpty()) {
            nacellePositions.put("__ALL__", defaultPositions);
        }
        return nacellePositions;
    }

    private boolean checkSeatsPos1(JsonObject geoJson) {
        if (!geoJson.has("minecraft:geometry")) {
            return false;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.equals("SeatsPos1")) continue;
                return true;
            }
        }
        return false;
    }

    private Map<Integer, Double> extractSeatsOrientations(JsonObject geoJson) {
        HashMap<Integer, Double> seatsOrientations = new HashMap<Integer, Double>();
        if (!geoJson.has("minecraft:geometry")) {
            return seatsOrientations;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonArray rotation;
                int index;
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.startsWith("SeatsPos") || !bone.has("rotation") || (index = this.extractIndex(boneName, "SeatsPos")) <= 0 || (rotation = bone.getAsJsonArray("rotation")).size() < 2) continue;
                seatsOrientations.put(index, -rotation.get(1).getAsDouble());
            }
        }
        return seatsOrientations;
    }

    private Map<Integer, JsonArray> extractSeatsPositions(JsonObject geoJson) {
        HashMap<Integer, JsonArray> seatsPositions = new HashMap<Integer, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return seatsPositions;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                int index;
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.startsWith("SeatsPos") || !bone.has("pivot") || (index = this.extractIndex(boneName, "SeatsPos")) <= 0) continue;
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray position = new JsonArray();
                position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0 - 1.61, 3));
                position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                seatsPositions.put(index, position);
            }
        }
        return seatsPositions;
    }

    private Map<Integer, JsonArray> extractSeatsCameraPositions(JsonObject geoJson) {
        HashMap<Integer, JsonArray> seatsCameraPositions = new HashMap<Integer, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return seatsCameraPositions;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                int index;
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.startsWith("SeatsCameraPos") || !bone.has("pivot") || (index = this.extractIndex(boneName, "SeatsCameraPos")) <= 0) continue;
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray position = new JsonArray();
                position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                seatsCameraPositions.put(index, position);
            }
        }
        return seatsCameraPositions;
    }

    private void applyCameraPos(JsonObject seat, int seatIndex, Map<Integer, JsonArray> seatsPositions, Map<Integer, JsonArray> seatsCameraPositions, JsonArray turretPos, boolean isAircraft) {
        String seatTransform;
        JsonObject cameraPos;
        if (seat.has("CameraPos")) {
            cameraPos = seat.getAsJsonObject("CameraPos");
        } else {
            cameraPos = new JsonObject();
            seat.add("CameraPos", (JsonElement)cameraPos);
        }
        String string = seatTransform = seat.has("Transform") ? seat.get("Transform").getAsString() : "";
        if (seatsCameraPositions.containsKey(seatIndex)) {
            JsonArray camPos;
            JsonArray adjusted = camPos = seatsCameraPositions.get(seatIndex);
            if (("Turret".equals(seatTransform) || "WeaponStation".equals(seatTransform)) && turretPos != null) {
                adjusted = new JsonArray();
                adjusted.add((Number)this.round(camPos.get(0).getAsDouble() - turretPos.get(0).getAsDouble(), 3));
                adjusted.add((Number)this.round(camPos.get(1).getAsDouble() - turretPos.get(1).getAsDouble(), 3));
                adjusted.add((Number)this.round(camPos.get(2).getAsDouble() - turretPos.get(2).getAsDouble(), 3));
            }
            cameraPos.addProperty("UseFixedCameraPos", Boolean.valueOf(true));
            cameraPos.add("Position", (JsonElement)adjusted);
            cameraPos.add("ZoomPosition", (JsonElement)adjusted);
        } else {
            cameraPos.addProperty("UseFixedCameraPos", Boolean.valueOf(false));
            if (isAircraft) {
                cameraPos.addProperty("Transform", "Vehicle");
                if (cameraPos.has("Direction")) {
                    cameraPos.remove("Direction");
                }
            } else if ("WeaponStation".equals(seatTransform)) {
                cameraPos.addProperty("Transform", "WeaponStation");
                cameraPos.addProperty("Direction", "WeaponStationBarrel");
            } else {
                cameraPos.addProperty("Transform", "Turret");
                cameraPos.addProperty("Direction", "Barrel");
            }
            if (seatsPositions.containsKey(seatIndex)) {
                JsonArray rawPos = seatsPositions.get(seatIndex);
                double zoomX = rawPos.get(0).getAsDouble();
                double zoomY = rawPos.get(1).getAsDouble() + 1.61;
                double zoomZ = rawPos.get(2).getAsDouble();
                if (("Turret".equals(seatTransform) || "WeaponStation".equals(seatTransform)) && turretPos != null) {
                    zoomX -= turretPos.get(0).getAsDouble();
                    zoomY -= turretPos.get(1).getAsDouble();
                    zoomZ -= turretPos.get(2).getAsDouble();
                }
                JsonArray zoom = new JsonArray();
                zoom.add((Number)this.round(zoomX, 3));
                zoom.add((Number)this.round(zoomY, 3));
                zoom.add((Number)this.round(zoomZ, 3));
                cameraPos.add("ZoomPosition", (JsonElement)zoom);
            }
        }
    }

    private List<JsonArray> extractTerrainCompatPositions(JsonObject geoJson) {
        HashMap<Integer, JsonArray> tempMap = new HashMap<Integer, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return new ArrayList<JsonArray>();
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                int index;
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.startsWith("TerrainCompatPos") || !bone.has("pivot") || (index = this.extractIndex(boneName, "TerrainCompatPos")) <= 0) continue;
                JsonArray pivot = bone.getAsJsonArray("pivot");
                JsonArray position = new JsonArray();
                position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                tempMap.put(index, position);
            }
        }
        ArrayList<JsonArray> terrainCompatPositions = new ArrayList<JsonArray>();
        int maxIndex = tempMap.keySet().stream().max(Integer::compareTo).orElse(0);
        for (int i = 1; i <= maxIndex; ++i) {
            if (!tempMap.containsKey(i)) continue;
            terrainCompatPositions.add((JsonArray)tempMap.get(i));
        }
        return terrainCompatPositions;
    }

    private int extractIndex(String boneName, String prefix) {
        try {
            String numberPart = boneName.substring(prefix.length());
            return Integer.parseInt(numberPart);
        }
        catch (NumberFormatException e) {
            return -1;
        }
    }

    private JsonArray[] extractPassengerWeaponStationChain(JsonObject geoJson, double theta) {
        if (!geoJson.has("minecraft:geometry")) {
            return null;
        }
        HashMap<String, JsonObject> boneMap = new HashMap<String, JsonObject>();
        for (JsonElement geomElement : geoJson.getAsJsonArray("minecraft:geometry")) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            for (JsonElement boneElement : geometry.getAsJsonArray("bones")) {
                JsonObject bone = boneElement.getAsJsonObject();
                boneMap.put(bone.get("name").getAsString(), bone);
            }
        }
        JsonObject turret = (JsonObject)boneMap.get("turret");
        JsonObject pwsYaw = (JsonObject)boneMap.get("passengerWeaponStationYaw");
        JsonObject pitch = (JsonObject)boneMap.get("passengerWeaponStationPitch");
        if (!(turret != null && pwsYaw != null && pitch != null && turret.has("pivot") && pwsYaw.has("pivot") && pitch.has("pivot"))) {
            return null;
        }
        JsonArray turretPivot = turret.getAsJsonArray("pivot");
        JsonArray yawPivot = pwsYaw.getAsJsonArray("pivot");
        JsonArray pitchPivot = pitch.getAsJsonArray("pivot");
        double ox = yawPivot.get(0).getAsDouble() - turretPivot.get(0).getAsDouble();
        double oy = yawPivot.get(1).getAsDouble() - turretPivot.get(1).getAsDouble();
        double oz = yawPivot.get(2).getAsDouble() - turretPivot.get(2).getAsDouble();
        double bx = pitchPivot.get(0).getAsDouble() - yawPivot.get(0).getAsDouble();
        double by = pitchPivot.get(1).getAsDouble() - yawPivot.get(1).getAsDouble();
        double bz = pitchPivot.get(2).getAsDouble() - yawPivot.get(2).getAsDouble();
        if (theta != 0.0) {
            double rad = Math.toRadians(theta);
            double c = Math.cos(rad);
            double s = Math.sin(rad);
            double ny = oy * c + oz * s;
            double nz = -oy * s + oz * c;
            oy = ny;
            oz = nz;
            ny = by * c + bz * s;
            nz = -by * s + bz * c;
            by = ny;
            bz = nz;
        }
        JsonArray pos = new JsonArray();
        pos.add((Number)this.round(ox / 16.0, 3));
        pos.add((Number)this.round(oy / 16.0, 3));
        pos.add((Number)this.round(-oz / 16.0, 3));
        JsonArray barrelPos = new JsonArray();
        barrelPos.add((Number)this.round(bx / 16.0, 3));
        barrelPos.add((Number)this.round(by / 16.0, 3));
        barrelPos.add((Number)this.round(-bz / 16.0, 3));
        return new JsonArray[]{pos, barrelPos};
    }

    private void addWeaponPosition(Map<String, JsonArray> weaponPositions, String weaponName, String boneName, String prefix, JsonObject bone, double barrelY, double turretY) {
        JsonArray positions;
        if (weaponPositions.containsKey(weaponName)) {
            positions = weaponPositions.get(weaponName);
        } else {
            positions = new JsonArray();
            weaponPositions.put(weaponName, positions);
        }
        JsonArray pivot = bone.getAsJsonArray("pivot");
        JsonArray pos = new JsonArray();
        pos.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
        pos.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
        pos.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
        positions.add((JsonElement)pos);
    }

    private JsonArray extractSize(JsonObject bone) {
        JsonObject cube;
        JsonArray size = new JsonArray();
        if (bone.has("cubes") && bone.getAsJsonArray("cubes").size() > 0 && (cube = bone.getAsJsonArray("cubes").get(0).getAsJsonObject()).has("size")) {
            JsonArray originalSize = cube.getAsJsonArray("size");
            for (JsonElement element : originalSize) {
                size.add((Number)this.round(element.getAsDouble() / 32.0, 3));
            }
        }
        return size;
    }

    private JsonArray extractPosition(JsonObject bone) {
        JsonArray position = new JsonArray();
        if (bone.has("pivot")) {
            JsonArray pivot = bone.getAsJsonArray("pivot");
            position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
            position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
            position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
        }
        return position;
    }

    private double round(double value, int places) {
        double scale = Math.pow(10.0, places);
        return (double)Math.round(value * scale) / scale;
    }

    public String m_6055_() {
        return "Geo OBB Data Provider";
    }
}

