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
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.concurrent.CompletableFuture;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VehicleSkinGenerator
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Pattern REGISTER_PATTERN = Pattern.compile("register\\(\"([a-z0-9_]+)\"");
    private static final Set<String> EXCLUDED_SUFFIXES = Set.of("icon", "glow", "icon_item", "outline");
    private static final Map<String, String> SKIN_NAMES = new TreeMap<String, String>();
    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;

    public VehicleSkinGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
        this.output = output;
        this.existingFileHelper = existingFileHelper;
    }

    public CompletableFuture<?> m_213708_(CachedOutput pOutput) {
        return CompletableFuture.runAsync(() -> {
            try {
                String workingDir = System.getProperty("user.dir").replace("\\run-data", "").replace("/run-data", "");
                Path modEntitiesFile = Path.of(workingDir, new String[0]).resolve("src/main/java/com/redabysslucia/dragonrise_reforge/init/ModEntities.java");
                Path texDir = Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/textures/entity");
                if (!Files.exists(modEntitiesFile, new LinkOption[0]) || !Files.exists(texDir, new LinkOption[0])) {
                    return;
                }
                Set<String> vehicleIds = this.readVehicleIds(modEntitiesFile);
                if (vehicleIds.isEmpty()) {
                    return;
                }
                List<String> idsByLengthDesc = vehicleIds.stream().sorted(Comparator.comparingInt(String::length).reversed()).toList();
                TreeMap<String, Set> skinsByVehicle = new TreeMap<String, Set>();
                try (DirectoryStream<Path> stream = Files.newDirectoryStream(texDir, "*.png");){
                    block7: for (Path p : stream) {
                        String name = p.getFileName().toString().replace(".png", "");
                        if (vehicleIds.contains(name)) continue;
                        for (String id : idsByLengthDesc) {
                            if (name.length() <= id.length() + 1 || !name.startsWith(id + "_")) continue;
                            String suffix = name.substring(id.length() + 1);
                            if (EXCLUDED_SUFFIXES.contains(suffix)) continue block7;
                            skinsByVehicle.computeIfAbsent(id, k -> new TreeSet()).add(suffix);
                            continue block7;
                        }
                    }
                }
                if (skinsByVehicle.isEmpty()) {
                    System.out.println("VehicleSkinGenerator: no skin textures found");
                    return;
                }
                ArrayList<Path> dataDirs = new ArrayList<Path>();
                dataDirs.add(Path.of(workingDir, new String[0]).resolve("src/main/resources/data/dragonrise_reforge/sbw/vehicle_skins"));
                dataDirs.add(Path.of(workingDir, new String[0]).resolve("src/main/resources/assets/dragonrise_reforge/sbw/vehicle_skins"));
                dataDirs.add(Path.of(workingDir, new String[0]).resolve("bin/main/data/dragonrise_reforge/sbw/vehicle_skins"));
                dataDirs.add(Path.of(workingDir, new String[0]).resolve("build/resources/main/data/dragonrise_reforge/sbw/vehicle_skins"));
                int written = 0;
                for (Map.Entry entry : skinsByVehicle.entrySet()) {
                    String id = (String)entry.getKey();
                    for (Path dir : dataDirs) {
                        Files.createDirectories(dir, new FileAttribute[0]);
                        Path skinFile = dir.resolve(id + ".json");
                        JsonObject json = this.readOrCreate(skinFile);
                        boolean changed = this.mergeSkins(json, id, (Set)entry.getValue());
                        if (!changed && Files.exists(skinFile, new LinkOption[0])) continue;
                        Files.writeString(skinFile, (CharSequence)GSON.toJson((JsonElement)json), new OpenOption[0]);
                    }
                    ++written;
                    System.out.println("VehicleSkinGenerator: " + id + " -> " + String.join((CharSequence)", ", (Iterable)entry.getValue()));
                }
                System.out.println("VehicleSkinGenerator: generated/merged skins for " + written + " vehicle(s)");
            }
            catch (Exception e) {
                throw new RuntimeException("Failed to generate vehicle skins", e);
            }
        });
    }

    private Set<String> readVehicleIds(Path modEntitiesFile) throws Exception {
        String content = Files.readString(modEntitiesFile);
        TreeSet<String> ids = new TreeSet<String>();
        Matcher m = REGISTER_PATTERN.matcher(content);
        while (m.find()) {
            ids.add(m.group(1));
        }
        return ids;
    }

    private JsonObject readOrCreate(Path skinFile) {
        try {
            if (Files.exists(skinFile, new LinkOption[0])) {
                return JsonParser.parseString((String)Files.readString(skinFile)).getAsJsonObject();
            }
        }
        catch (Exception exception) {
            // empty catch block
        }
        JsonObject json = new JsonObject();
        json.add("Skins", (JsonElement)new JsonArray());
        return json;
    }

    private boolean mergeSkins(JsonObject json, String vehicleId, Set<String> skinIds) {
        JsonArray skins;
        if (json.has("Skins") && json.get("Skins").isJsonArray()) {
            skins = json.getAsJsonArray("Skins");
        } else {
            skins = new JsonArray();
            json.add("Skins", (JsonElement)skins);
        }
        LinkedHashSet<String> existingIds = new LinkedHashSet<String>();
        for (JsonElement e : skins) {
            if (!e.isJsonObject() || !e.getAsJsonObject().has("Id")) continue;
            existingIds.add(e.getAsJsonObject().get("Id").getAsString());
        }
        boolean changed = false;
        for (String skinId : skinIds) {
            if (existingIds.contains(skinId)) continue;
            JsonObject skin = new JsonObject();
            skin.addProperty("Id", skinId);
            skin.addProperty("Name", SKIN_NAMES.getOrDefault(skinId, skinId));
            skin.addProperty("Description", "\u4f7f\u7528\u55b7\u6f06\u7f50\u55b7\u6d82\u7684 " + SKIN_NAMES.getOrDefault(skinId, skinId) + "\u6d82\u88c5");
            skin.addProperty("Texture", "dragonrise_reforge:textures/entity/" + vehicleId + "_" + skinId + ".png");
            skin.addProperty("Priority", (Number)0);
            skins.add((JsonElement)skin);
            existingIds.add(skinId);
            changed = true;
        }
        return changed;
    }

    public String m_6055_() {
        return "DragonRise Vehicle Skin Generator";
    }

    static {
        SKIN_NAMES.put("sand", "\u6c99\u6f20\u6d82\u88c5");
        SKIN_NAMES.put("green", "\u7eff\u8272\u6d82\u88c5");
        SKIN_NAMES.put("ttsko", "TTSKO \u6d82\u88c5");
        SKIN_NAMES.put("ttsko1960", "TTSKO 1960 \u6d82\u88c5");
        SKIN_NAMES.put("nato", "\u5317\u7ea6\u6d82\u88c5");
        SKIN_NAMES.put("desert", "\u6c99\u6f20\u6d82\u88c5");
        SKIN_NAMES.put("snow", "\u96ea\u5730\u6d82\u88c5");
        SKIN_NAMES.put("woodland", "\u6797\u5730\u6d82\u88c5");
    }
}

