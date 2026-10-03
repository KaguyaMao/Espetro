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
package frontline.combat.fcp.datagen;

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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
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
                String workingDir = System.getProperty("user.dir").replace("\\run-data", "").replace("/run-data", "");
                Path inputPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/fcp/geo");
                Path fcpOutputPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/data/fcp/sbw/vehicles");
                Path superbwarfareOutputPath = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/fcp/sbw/vehicles");
                if (!Files.exists(inputPath, new LinkOption[0])) {
                    return;
                }
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(inputPath, "*.geo.json");){
                    for (Path geoFile : stream) {
                        this.processGeoFile(geoFile, fcpOutputPath, superbwarfareOutputPath);
                    }
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to process geo files", e);
            }
        });
    }

    private void processGeoFile(Path geoFile, Path fcpOutputPath, Path superbwarfareOutputPath) {
        try {
            boolean hasExtractableData;
            String content = Files.readString(geoFile);
            JsonObject geoJson = JsonParser.parseString((String)content).getAsJsonObject();
            boolean hasSeatsPos1 = this.checkSeatsPos1(geoJson);
            JsonArray turretPos = hasSeatsPos1 ? this.extractTurretPos(geoJson) : null;
            double turretPivotY = turretPos != null ? turretPos.get(1).getAsDouble() * 16.0 : 0.0;
            JsonArray barrelPos = hasSeatsPos1 ? this.extractBarrelPos(geoJson) : null;
            double barrelPosY = barrelPos != null ? barrelPos.get(1).getAsDouble() : 0.0;
            double barrelPivotY = barrelPosY * 16.0;
            JsonArray obbList = this.extractOBBList(geoJson, turretPivotY);
            Map<String, JsonArray> weaponPositions = this.extractWeaponPositions(geoJson, barrelPivotY, turretPivotY);
            Map<Integer, JsonArray> seatsPositions = this.extractSeatsPositions(geoJson);
            Map<Integer, JsonArray> seatsCameraPositions = this.extractSeatsCameraPositions(geoJson);
            List<JsonArray> terrainCompatPositions = this.extractTerrainCompatPositions(geoJson);
            String baseName = geoFile.getFileName().toString().replace(".geo.json", "");
            Path vehicleFile = fcpOutputPath.resolve(baseName + ".json");
            if (Files.exists(vehicleFile, new LinkOption[0])) {
                try {
                    String vehicleContent = Files.readString(vehicleFile);
                    JsonObject existingVehicleJson = JsonParser.parseString((String)vehicleContent).getAsJsonObject();
                    if (existingVehicleJson.has("VehicleIcon")) {
                        String iconPath = "fcp:textures/vehicle_icon/" + baseName + "_icon.png";
                        existingVehicleJson.addProperty("VehicleIcon", iconPath);
                        Files.writeString(vehicleFile, (CharSequence)GSON.toJson((JsonElement)existingVehicleJson), new OpenOption[0]);
                    }
                }
                catch (Exception vehicleContent) {
                    // empty catch block
                }
            }
            boolean bl = hasExtractableData = !obbList.isEmpty() || turretPos != null || barrelPos != null || !weaponPositions.isEmpty() || !seatsPositions.isEmpty() || !seatsCameraPositions.isEmpty() || !terrainCompatPositions.isEmpty();
            if (hasExtractableData) {
                JsonObject vehicleJson;
                if (Files.exists(vehicleFile, new LinkOption[0])) {
                    String vehicleContent = Files.readString(vehicleFile);
                    vehicleJson = JsonParser.parseString((String)vehicleContent).getAsJsonObject();
                } else {
                    vehicleJson = new JsonObject();
                    vehicleJson.addProperty("ID", "fcp:" + baseName);
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
                        JsonObject shootPos;
                        String weaponName = entry.getKey();
                        JsonArray positions = entry.getValue();
                        if (!weapons.has(weaponName)) continue;
                        JsonObject weapon = weapons.getAsJsonObject(weaponName);
                        if (weapon.has("ShootPos")) {
                            shootPos = weapon.getAsJsonObject("ShootPos");
                        } else {
                            shootPos = new JsonObject();
                            weapon.add("ShootPos", (JsonElement)shootPos);
                        }
                        String transform = shootPos.has("Transform") ? shootPos.get("Transform").getAsString() : "";
                        JsonArray adjustedPositions = new JsonArray();
                        for (JsonElement posElement : positions) {
                            JsonArray pos = posElement.getAsJsonArray();
                            JsonArray adjustedPos = new JsonArray();
                            adjustedPos.add(pos.get(0));
                            adjustedPos.add(pos.get(1));
                            adjustedPos.add(pos.get(2));
                            adjustedPositions.add((JsonElement)adjustedPos);
                        }
                        if (hasSeatsPos1) {
                            if ("Barrel".equals(transform) && barrelPos != null && turretPos != null) {
                                double barrelY = barrelPos.get(1).getAsDouble();
                                double turretY = turretPos.get(1).getAsDouble();
                                for (int i = 0; i < adjustedPositions.size(); ++i) {
                                    JsonArray pos = adjustedPositions.get(i).getAsJsonArray();
                                    double y = pos.get(1).getAsDouble();
                                    y = y - barrelY - turretY;
                                    pos.set(1, (JsonElement)new JsonPrimitive((Number)this.round(y, 3)));
                                }
                            } else if ("Turret".equals(transform) && turretPos != null) {
                                double turretY = turretPos.get(1).getAsDouble();
                                for (int i = 0; i < adjustedPositions.size(); ++i) {
                                    JsonArray pos = adjustedPositions.get(i).getAsJsonArray();
                                    double y = pos.get(1).getAsDouble();
                                    pos.set(1, (JsonElement)new JsonPrimitive((Number)this.round(y -= turretY, 3)));
                                }
                            }
                        }
                        shootPos.add("Positions", (JsonElement)adjustedPositions);
                    }
                }
                if (hasSeatsPos1 && !seatsPositions.isEmpty() && vehicleJson.has("Seats")) {
                    JsonArray seats = vehicleJson.getAsJsonArray("Seats");
                    for (int i = 0; i < seats.size(); ++i) {
                        JsonObject seat = seats.get(i).getAsJsonObject();
                        int index = i + 1;
                        if (seatsPositions.containsKey(index)) {
                            String seatTransform;
                            JsonArray pos = seatsPositions.get(index);
                            String string = seatTransform = seat.has("Transform") ? seat.get("Transform").getAsString() : "";
                            if (("Turret".equals(seatTransform) || "WeaponStation".equals(seatTransform)) && turretPos != null) {
                                JsonArray adjustedPos = new JsonArray();
                                adjustedPos.add((Number)this.round(pos.get(0).getAsDouble() - turretPos.get(0).getAsDouble(), 3));
                                adjustedPos.add((Number)this.round(pos.get(1).getAsDouble() - turretPos.get(1).getAsDouble(), 3));
                                adjustedPos.add((Number)this.round(pos.get(2).getAsDouble() - turretPos.get(2).getAsDouble(), 3));
                                seat.add("Position", (JsonElement)adjustedPos);
                            } else {
                                seat.add("Position", (JsonElement)pos);
                            }
                        }
                        if (!seatsCameraPositions.containsKey(index) || !seat.has("CameraPos")) continue;
                        JsonObject cameraPos = seat.getAsJsonObject("CameraPos");
                        cameraPos.add("Position", (JsonElement)seatsCameraPositions.get(index));
                    }
                }
                if (!terrainCompatPositions.isEmpty()) {
                    JsonArray terrainCompatArray = new JsonArray();
                    for (JsonArray pos : terrainCompatPositions) {
                        terrainCompatArray.add((JsonElement)pos);
                    }
                    vehicleJson.add("TerrainCompat", (JsonElement)terrainCompatArray);
                }
                Files.createDirectories(vehicleFile.getParent(), new FileAttribute[0]);
                Files.writeString(vehicleFile, (CharSequence)GSON.toJson((JsonElement)vehicleJson), new OpenOption[0]);
            }
            this.generateSuperbwarfareVehicleConfig(superbwarfareOutputPath, baseName);
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to process: " + String.valueOf(geoFile.getFileName()), e);
        }
    }

    private void generateSuperbwarfareVehicleConfig(Path outputPath, String baseName) throws Exception {
        JsonObject model;
        JsonObject vehicleJson;
        Path vehicleFile = outputPath.resolve(baseName + ".json");
        if (Files.exists(vehicleFile, new LinkOption[0])) {
            String vehicleContent = Files.readString(vehicleFile);
            vehicleJson = JsonParser.parseString((String)vehicleContent).getAsJsonObject();
        } else {
            vehicleJson = new JsonObject();
            vehicleJson.addProperty("ID", "fcp:" + baseName);
            model = new JsonObject();
            model.addProperty("Model", "fcp:geo/" + baseName + ".geo.json");
            model.addProperty("Texture", "fcp:textures/entity/" + baseName + ".png");
            vehicleJson.add("Model", (JsonElement)model);
        }
        if (!vehicleJson.has("Model")) {
            model = new JsonObject();
            model.addProperty("Model", "fcp:geo/" + baseName + ".geo.json");
            model.addProperty("Texture", "fcp:textures/entity/" + baseName + ".png");
            vehicleJson.add("Model", (JsonElement)model);
        } else {
            model = vehicleJson.getAsJsonObject("Model");
            if (!model.has("Model")) {
                model.addProperty("Model", "fcp:geo/" + baseName + ".geo.json");
            }
            if (!model.has("Texture")) {
                model.addProperty("Texture", "fcp:textures/entity/" + baseName + ".png");
            }
        }
        Files.createDirectories(vehicleFile.getParent(), new FileAttribute[0]);
        Files.writeString(vehicleFile, (CharSequence)GSON.toJson((JsonElement)vehicleJson), new OpenOption[0]);
    }

    private JsonArray extractOBBList(JsonObject geoJson, double turretPivotY) {
        JsonArray obbList = new JsonArray();
        if (!geoJson.has("minecraft:geometry")) {
            return obbList;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
            if (!geometry.has("bones")) continue;
            JsonArray bones = geometry.getAsJsonArray("bones");
            for (JsonElement boneElement : bones) {
                JsonObject bone = boneElement.getAsJsonObject();
                String boneName = bone.get("name").getAsString();
                if (!boneName.toLowerCase().contains("obb") || boneName.equals("Obb")) continue;
                JsonObject obbEntry = new JsonObject();
                obbEntry.add("Size", (JsonElement)this.extractSize(bone));
                obbEntry.add("Position", (JsonElement)this.extractOBBPosition(bone, turretPivotY, boneName));
                if (boneName.equals("MainEngineObb")) {
                    obbEntry.addProperty("Part", "MainEngine");
                } else if (boneName.equals("WheelRightObb")) {
                    obbEntry.addProperty("Part", "WheelRight");
                } else if (boneName.equals("WheelLeftObb")) {
                    obbEntry.addProperty("Part", "WheelLeft");
                } else if (boneName.startsWith("TurretObb")) {
                    obbEntry.addProperty("Part", "Turret");
                    if (!boneName.equals("TurretObb")) {
                        obbEntry.addProperty("Transform", "Turret");
                        obbEntry.addProperty("Rotation", "Turret");
                    }
                }
                obbList.add((JsonElement)obbEntry);
            }
        }
        return obbList;
    }

    private JsonArray extractOBBPosition(JsonObject bone, double turretPivotY, String boneName) {
        JsonArray position = new JsonArray();
        if (bone.has("pivot")) {
            JsonArray pivot = bone.getAsJsonArray("pivot");
            double yValue = pivot.get(1).getAsDouble();
            if (boneName.startsWith("TurretObb") && !boneName.equals("TurretObb")) {
                yValue -= turretPivotY;
            }
            position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
            position.add((Number)this.round(yValue / 16.0, 3));
            position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
        }
        return position;
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

    private JsonArray extractBarrelPos(JsonObject geoJson) {
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
                position.add((Number)this.round(pivot.get(0).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0, 3));
                position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                return position;
            }
        }
        return null;
    }

    private Map<String, JsonArray> extractWeaponPositions(JsonObject geoJson, double barrelY, double turretY) {
        HashMap<String, JsonArray> weaponPositions = new HashMap<String, JsonArray>();
        if (!geoJson.has("minecraft:geometry")) {
            return weaponPositions;
        }
        JsonArray geometries = geoJson.getAsJsonArray("minecraft:geometry");
        for (JsonElement geomElement : geometries) {
            JsonObject geometry = geomElement.getAsJsonObject();
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
                if (!boneName.startsWith("MissilePos") || !bone.has("pivot")) continue;
                this.addWeaponPosition(weaponPositions, "Missile", boneName, "MissilePos", bone, barrelY, turretY);
            }
        }
        return weaponPositions;
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
                position.add((Number)this.round(pivot.get(1).getAsDouble() / 16.0 - 1.61, 3));
                position.add((Number)this.round(-pivot.get(2).getAsDouble() / 16.0, 3));
                seatsCameraPositions.put(index, position);
            }
        }
        return seatsCameraPositions;
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

