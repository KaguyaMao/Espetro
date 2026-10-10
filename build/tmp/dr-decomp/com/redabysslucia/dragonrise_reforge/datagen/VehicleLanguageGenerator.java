/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.data.CachedOutput
 *  net.minecraft.data.DataProvider
 *  net.minecraft.data.PackOutput
 *  net.minecraftforge.common.data.ExistingFileHelper
 */
package com.redabysslucia.dragonrise_reforge.datagen;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VehicleLanguageGenerator
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;

    public VehicleLanguageGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    public CompletableFuture<?> m_213708_(CachedOutput pOutput) {
        return CompletableFuture.runAsync(() -> {
            try {
                String workingDir = System.getProperty("user.dir").replace("\\run-data", "").replace("/run-data", "");
                Path vehiclesDir = Path.of(workingDir, new String[0]).resolve("src/main/resources/data/dragonrise_reforge/sbw/vehicles");
                Path langDir = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/lang");
                if (!Files.exists(vehiclesDir, new LinkOption[0])) {
                    System.out.println("Vehicles directory not found: " + vehiclesDir);
                    return;
                }
                HashMap<String, String> existingVehicleFiles = new HashMap<String, String>();
                HashMap<String, String> allNames = new HashMap<String, String>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(vehiclesDir, "*.json");){
                    for (Path vehicleFile : stream) {
                        String fileName = vehicleFile.getFileName().toString().replace(".json", "");
                        existingVehicleFiles.put("info.dragonrise_reforge." + fileName, fileName);
                        existingVehicleFiles.put("entity.dragonrise_reforge." + fileName, fileName);
                        this.extractNamesFromFile(vehicleFile, allNames);
                    }
                }
                if (!allNames.isEmpty()) {
                    this.updateLanguageFile(langDir.resolve("zh_cn.json"), allNames, existingVehicleFiles);
                    this.updateLanguageFile(langDir.resolve("en_us.json"), allNames, existingVehicleFiles);
                }
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to generate language files", e);
            }
        });
    }

    private void extractNamesFromFile(Path vehicleFile, Map<String, String> allNames) {
        try {
            String fileName = vehicleFile.getFileName().toString().replace(".json", "");
            allNames.put("info.dragonrise_reforge." + fileName, "");
            allNames.put("entity.dragonrise_reforge." + fileName, "");
            String content = Files.readString(vehicleFile);
            JsonObject vehicleJson = (JsonObject)GSON.fromJson(content, JsonObject.class);
            if (vehicleJson.has("Weapons")) {
                JsonObject weapons = vehicleJson.getAsJsonObject("Weapons");
                for (Map.Entry weaponEntry : weapons.entrySet()) {
                    JsonObject weapon = ((JsonElement)weaponEntry.getValue()).getAsJsonObject();
                    this.extractNameFromObject(weapon, allNames);
                    if (!weapon.has("AmmoType")) continue;
                    JsonElement ammoType = weapon.get("AmmoType");
                    this.extractNamesFromAmmoType(ammoType, allNames);
                }
            }
        }
        catch (Exception e) {
            System.err.println("Failed to process file: " + vehicleFile.getFileName());
            e.printStackTrace();
        }
    }

    private void extractNamesFromAmmoType(JsonElement ammoType, Map<String, String> allNames) {
        if (ammoType.isJsonArray()) {
            for (JsonElement element : ammoType.getAsJsonArray()) {
                JsonObject ammoObj;
                if (!element.isJsonObject() || !(ammoObj = element.getAsJsonObject()).has("Override")) continue;
                JsonObject override = ammoObj.getAsJsonObject("Override");
                this.extractNameFromObject(override, allNames);
            }
        }
    }

    private void extractNameFromObject(JsonObject obj, Map<String, String> allNames) {
        String nameKey;
        if (obj.has("Name") && !(nameKey = obj.get("Name").getAsString()).isEmpty() && !allNames.containsKey(nameKey)) {
            allNames.put(nameKey, "");
        }
    }

    private void updateLanguageFile(Path langFile, Map<String, String> newEntries, Map<String, String> existingVehicleFiles) {
        try {
            JsonObject langJson;
            if (Files.exists(langFile, new LinkOption[0])) {
                String content = Files.readString(langFile);
                langJson = (JsonObject)GSON.fromJson(content, JsonObject.class);
            } else {
                langJson = new JsonObject();
            }
            Iterator iterator = langJson.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry)iterator.next();
                String string = (String)entry.getKey();
                boolean shouldRemove = false;
                if ((string.startsWith("info.dragonrise_reforge.") || string.startsWith("entity.dragonrise_reforge.")) && !existingVehicleFiles.containsKey(string)) {
                    shouldRemove = true;
                } else if (string.startsWith("dragonrise_reforge:") && !newEntries.containsKey(string)) {
                    shouldRemove = true;
                }
                if (!shouldRemove) continue;
                iterator.remove();
                System.out.println("Removed orphaned key: " + string);
            }
            for (Map.Entry entry : newEntries.entrySet()) {
                String key = (String)entry.getKey();
                if (!langJson.has(key)) {
                    langJson.addProperty(key, (String)entry.getValue());
                    continue;
                }
                String existingValue = langJson.get(key).getAsString();
                if (!existingValue.equals(key)) continue;
                langJson.addProperty(key, "");
            }
            TreeMap<String, String> sortedJson = new TreeMap<String, String>();
            for (Map.Entry entry : langJson.entrySet()) {
                if (((JsonElement)entry.getValue()).isJsonPrimitive()) {
                    sortedJson.put((String)entry.getKey(), ((JsonElement)entry.getValue()).getAsString());
                    continue;
                }
                sortedJson.put((String)entry.getKey(), (String)entry.getValue());
            }
            Files.createDirectories(langFile.getParent(), new FileAttribute[0]);
            Files.writeString(langFile, (CharSequence)GSON.toJson(sortedJson), new OpenOption[0]);
            System.out.println("Updated language file: " + langFile.getFileName());
        }
        catch (IOException e) {
            System.err.println("Failed to update language file: " + langFile.getFileName());
            e.printStackTrace();
        }
    }

    public String m_6055_() {
        return "Vehicle Language Generator";
    }
}

