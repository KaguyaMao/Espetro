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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.concurrent.CompletableFuture;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.ExistingFileHelper;

public class VehicleJavaGenerator
implements DataProvider {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private final PackOutput output;
    private final ExistingFileHelper existingFileHelper;

    public VehicleJavaGenerator(PackOutput output, ExistingFileHelper existingFileHelper) {
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
                    this.processGeoFile(geoFile, workingDir);
                }
            }
            catch (Exception e2) {
                throw new RuntimeException("Failed to process geo files", e2);
            }
        });
    }

    private void processGeoFile(Path geoFile, String workingDir) {
        try {
            String content = Files.readString(geoFile);
            JsonObject geoJson = JsonParser.parseString((String)content).getAsJsonObject();
            if (!this.hasBuildUnderObb(geoJson)) {
                return;
            }
            String baseName = geoFile.getFileName().toString().replace(".geo.json", "");
            String entityClassName = this.toPascalCase(baseName) + "Entity";
            String rendererClassName = this.toPascalCase(baseName) + "Renderer";
            String entityConstantName = baseName.toUpperCase();
            Path javaSourcePath = Path.of(workingDir, new String[0]).resolve("src/main/java/com/redabysslucia/dragonrise_reforge");
            Path entitiesPath = javaSourcePath.resolve("entities");
            Path rendererPath = javaSourcePath.resolve("client/renderer/entity");
            Path initPath = javaSourcePath.resolve("init");
            Files.createDirectories(entitiesPath, new FileAttribute[0]);
            Files.createDirectories(rendererPath, new FileAttribute[0]);
            Files.createDirectories(initPath, new FileAttribute[0]);
            Path entityFile = entitiesPath.resolve(entityClassName + ".java");
            Path rendererFile = rendererPath.resolve(rendererClassName + ".java");
            boolean entityFileExisted = Files.exists(entityFile, new LinkOption[0]);
            if (!entityFileExisted) {
                String entityContent = this.generateEntityContent(baseName, entityClassName);
                Files.writeString(entityFile, (CharSequence)entityContent, new OpenOption[0]);
            }
            if (!Files.exists(rendererFile, new LinkOption[0])) {
                String rendererContent = this.generateRendererContent(baseName, entityClassName, rendererClassName);
                Files.writeString(rendererFile, (CharSequence)rendererContent, new OpenOption[0]);
            }
            if (!entityFileExisted) {
                this.updateModEntities(initPath, baseName, entityClassName, entityConstantName);
                this.updateModEntityRenderers(initPath, baseName, entityClassName, rendererClassName, entityConstantName);
                this.updateModTabs(initPath, baseName, entityConstantName);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Failed to process: " + geoFile.getFileName(), e);
        }
    }

    private boolean hasBuildUnderObb(JsonObject geoJson) {
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

    private boolean isAircraft(JsonObject geoJson) {
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

    private boolean hasObbBone(JsonObject geoJson) {
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

    private boolean hasBuildBone(JsonObject geoJson) {
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
                if (!boneName.equals("Build")) continue;
                return true;
            }
        }
        return false;
    }

    private void updateModEntities(Path initPath, String baseName, String entityClassName, String entityConstantName) throws Exception {
        Path modEntitiesFile = initPath.resolve("ModEntities.java");
        if (!Files.exists(modEntitiesFile, new LinkOption[0])) {
            return;
        }
        String content = Files.readString(modEntitiesFile);
        if (content.contains("public static final RegistryObject<EntityType<" + entityClassName + ">> " + entityConstantName + " = register")) {
            return;
        }
        String registrationCode = "    public static final RegistryObject<EntityType<" + entityClassName + ">> " + entityConstantName + " = register(\"" + baseName + "\",\n            EntityType.Builder.of(" + entityClassName + "::new, MobCategory.MISC)\n                    .setTrackingRange(512)\n                    .setUpdateInterval(2)\n                    .fireImmune()\n                    .sized(4.0f, 2.9f)\n    );";
        String importLine = "import com.redabysslucia.dragonrise_reforge.entities." + entityClassName + ";";
        if (!content.contains(importLine)) {
            content = content.replace("package com.redabysslucia.dragonrise_reforge.init;", "package com.redabysslucia.dragonrise_reforge.init;\n\n" + importLine + "\n");
        }
        content = this.insertAfterBlockEnd(content, "public static final RegistryObject<EntityType<", "    );", registrationCode);
        Files.writeString(modEntitiesFile, (CharSequence)content, new OpenOption[0]);
    }

    private void updateModEntityRenderers(Path initPath, String baseName, String entityClassName, String rendererClassName, String entityConstantName) throws Exception {
        Path modEntityRenderersFile = initPath.resolve("ModEntityRenderers.java");
        if (!Files.exists(modEntityRenderersFile, new LinkOption[0])) {
            return;
        }
        String content = Files.readString(modEntityRenderersFile);
        if (content.contains("event.registerEntityRenderer(ModEntities." + entityConstantName + ".get(), " + rendererClassName + "::new);")) {
            return;
        }
        String registrationCode = "        event.registerEntityRenderer(ModEntities." + entityConstantName + ".get(), " + rendererClassName + "::new);";
        String rendererImportLine = "import com.redabysslucia.dragonrise_reforge.client.renderer.entity." + rendererClassName + ";";
        if (!content.contains(rendererImportLine)) {
            content = content.replace("package com.redabysslucia.dragonrise_reforge.init;", "package com.redabysslucia.dragonrise_reforge.init;\n\n" + rendererImportLine + "\n");
        }
        content = this.insertAfterLast(content, "event.registerEntityRenderer(ModEntities.", registrationCode);
        Files.writeString(modEntityRenderersFile, (CharSequence)content, new OpenOption[0]);
    }

    private void updateModTabs(Path initPath, String baseName, String entityConstantName) throws Exception {
        Path modTabsFile = initPath.resolve("ModTabs.java");
        if (!Files.exists(modTabsFile, new LinkOption[0])) {
            return;
        }
        String content = Files.readString(modTabsFile);
        if (content.contains("output.accept(ContainerBlockItem.createInstance(ModEntities." + entityConstantName + ".get()));")) {
            return;
        }
        String tabCode = "                        output.accept(ContainerBlockItem.createInstance(ModEntities." + entityConstantName + ".get()));";
        content = this.insertAfterLast(content, "output.accept(ContainerBlockItem.createInstance(ModEntities.", tabCode);
        Files.writeString(modTabsFile, (CharSequence)content, new OpenOption[0]);
    }

    private String insertAfterLast(String content, String marker, String newBlock) {
        int lastIdx = content.lastIndexOf(marker);
        if (lastIdx < 0) {
            return content;
        }
        String eol = content.contains("\r\n") ? "\r\n" : "\n";
        String indent = this.extractIndent(content, lastIdx);
        String aligned = this.alignBlock(newBlock, indent);
        int lineEnd = content.indexOf("\n", lastIdx);
        if (lineEnd == -1) {
            return content + eol + aligned + eol;
        }
        int insertPoint = lineEnd + 1;
        return content.substring(0, insertPoint) + aligned + eol + content.substring(insertPoint);
    }

    private String insertAfterBlockEnd(String content, String marker, String blockEndMarker, String newBlock) {
        int lineEnd;
        int lastIdx = content.lastIndexOf(marker);
        if (lastIdx < 0) {
            return content;
        }
        String eol = content.contains("\r\n") ? "\r\n" : "\n";
        String indent = this.extractIndent(content, lastIdx);
        String aligned = this.alignBlock(newBlock, indent);
        int blockEndIdx = content.indexOf(blockEndMarker, lastIdx);
        int insertPoint = blockEndIdx >= 0 ? ((lineEnd = content.indexOf("\n", blockEndIdx)) >= 0 ? lineEnd + 1 : content.length()) : ((lineEnd = content.indexOf("\n", lastIdx)) >= 0 ? lineEnd + 1 : content.length());
        return content.substring(0, insertPoint) + aligned + eol + content.substring(insertPoint);
    }

    private String alignBlock(String newBlock, String indent) {
        String blockIndent = this.extractIndent(newBlock, 0);
        StringBuilder aligned = new StringBuilder();
        int start = 0;
        while (start <= newBlock.length()) {
            String line;
            int nl = newBlock.indexOf(10, start);
            String string = line = nl >= 0 ? newBlock.substring(start, nl) : newBlock.substring(start);
            if (!line.isEmpty() && line.startsWith(blockIndent)) {
                aligned.append(indent).append(line.substring(blockIndent.length()));
            } else {
                aligned.append(line);
            }
            if (nl < 0) break;
            aligned.append('\n');
            start = nl + 1;
        }
        return aligned.toString();
    }

    private String extractIndent(String text, int offset) {
        int lineStart;
        int i;
        for (i = lineStart = text.lastIndexOf(10, offset) + 1; i < text.length() && text.charAt(i) == ' '; ++i) {
        }
        return text.substring(lineStart, i);
    }

    private String toPascalCase(String name) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : name.toCharArray()) {
            if (c == '_' || c == '-' || c == ' ') {
                capitalizeNext = true;
                continue;
            }
            if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
                continue;
            }
            result.append(Character.toLowerCase(c));
        }
        return result.toString();
    }

    private String generateEntityContent(String baseName, String entityClassName) {
        String pascalName = this.toPascalCase(baseName);
        return "package com.redabysslucia.dragonrise_reforge.entities;\n\nimport com.atsuishio.superbwarfare.entity.vehicle.base.VehicleEntity;\nimport net.minecraft.world.entity.EntityType;\nimport net.minecraft.world.level.Level;\n\npublic class %s extends VehicleEntity {\n\n    public %s(EntityType<%s> type, Level world) {\n        super(type, world);\n    }\n\n}\n".formatted(pascalName + "Entity", pascalName + "Entity", pascalName + "Entity");
    }

    private String generateRendererContent(String baseName, String entityClassName, String rendererClassName) {
        String pascalName = this.toPascalCase(baseName);
        return "package com.redabysslucia.dragonrise_reforge.client.renderer.entity;\n\nimport com.atsuishio.superbwarfare.client.renderer.entity.GeoVehicleRenderer;\nimport com.redabysslucia.dragonrise_reforge.entities.%s;\nimport net.minecraft.client.renderer.entity.EntityRendererProvider;\n\npublic class %s extends GeoVehicleRenderer<%s> {\n    public %s(EntityRendererProvider.Context renderManager) {\n        super(renderManager);\n    }\n}\n".formatted(pascalName + "Entity", pascalName + "Renderer", pascalName + "Entity", pascalName + "Renderer");
    }

    public String m_6055_() {
        return "Vehicle Java Generator";
    }
}

