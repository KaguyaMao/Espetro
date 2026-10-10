/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonDeserializationContext
 *  com.google.gson.JsonDeserializer
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParseException
 *  com.google.gson.JsonSerializationContext
 *  com.google.gson.JsonSerializer
 *  com.google.gson.reflect.TypeToken
 *  net.minecraft.core.BlockPos
 */
package com.example.espoints.command;

import com.example.espoints.capturepoint.CapturePointManager;
import com.example.espoints.util.ModLogger;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.reflect.TypeToken;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.core.BlockPos;

public class TeamfightPresetManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().registerTypeAdapter(BlockPos.class, (Object)new BlockPosAdapter()).create();
    private static final String PRESETS_DIR = "config/espoints/presets";

    public static boolean savePreset(int presetId) {
        try {
            CapturePointManager manager = CapturePointManager.getInstance();
            Map<String, String> teamRoles = manager.getTeamRoles();
            Map<String, Integer> teamReinforcements = manager.getTeamReinforcementsMap();
            HashMap<String, SerializablePlannedPoint> serializablePlannedPoints = new HashMap<String, SerializablePlannedPoint>();
            for (Map.Entry<String, CapturePointManager.PlannedCapturePoint> entry : manager.getPlannedPointsMap().entrySet()) {
                CapturePointManager.PlannedCapturePoint plannedPoint = entry.getValue();
                try {
                    Field nameField = plannedPoint.getClass().getDeclaredField("name");
                    Field pos1Field = plannedPoint.getClass().getDeclaredField("pos1");
                    Field pos2Field = plannedPoint.getClass().getDeclaredField("pos2");
                    Field batchField = plannedPoint.getClass().getDeclaredField("batch");
                    nameField.setAccessible(true);
                    pos1Field.setAccessible(true);
                    pos2Field.setAccessible(true);
                    batchField.setAccessible(true);
                    String name = (String)nameField.get(plannedPoint);
                    BlockPos pos1 = (BlockPos)pos1Field.get(plannedPoint);
                    BlockPos pos2 = (BlockPos)pos2Field.get(plannedPoint);
                    int batch = (Integer)batchField.get(plannedPoint);
                    serializablePlannedPoints.put(name, new SerializablePlannedPoint(name, pos1, pos2, batch));
                }
                catch (Exception e) {
                    ModLogger.error("\u83b7\u53d6\u8ba1\u5212\u636e\u70b9\u5c5e\u6027\u5931\u8d25: " + e.getMessage());
                    return false;
                }
            }
            PresetData presetData = new PresetData(serializablePlannedPoints, teamRoles, teamReinforcements);
            File presetsDir = new File(PRESETS_DIR);
            if (!presetsDir.exists()) {
                presetsDir.mkdirs();
            }
            File presetFile = new File(presetsDir, "preset_" + presetId + ".json");
            try (FileWriter writer = new FileWriter(presetFile);){
                GSON.toJson((Object)presetData, (Appendable)writer);
            }
            ModLogger.info("\u884c\u52a8\u653b\u9632\u6a21\u5f0f\u9884\u8bbe " + presetId + " \u5df2\u4fdd\u5b58\uff0c\u5305\u542b " + serializablePlannedPoints.size() + " \u4e2a\u8ba1\u5212\u636e\u70b9\u3001" + teamRoles.size() + " \u4e2a\u961f\u4f0d\u89d2\u8272\u8bbe\u7f6e\u548c " + teamReinforcements.size() + " \u4e2a\u961f\u4f0d\u5175\u529b\u8bbe\u7f6e");
            return true;
        }
        catch (IOException e) {
            ModLogger.error("\u4fdd\u5b58\u9884\u8bbe\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return false;
        }
    }

    public static boolean loadPreset(int presetId) {
        try {
            PresetData presetData;
            File presetFile = new File(PRESETS_DIR, "preset_" + presetId + ".json");
            if (!presetFile.exists()) {
                ModLogger.warn("\u9884\u8bbe\u6587\u4ef6\u4e0d\u5b58\u5728: " + presetFile.getAbsolutePath());
                return false;
            }
            try (FileReader reader = new FileReader(presetFile);){
                Type presetDataType = new TypeToken<PresetData>(){}.getType();
                presetData = (PresetData)GSON.fromJson((Reader)reader, presetDataType);
            }
            if (presetData == null) {
                ModLogger.error("\u9884\u8bbe\u6587\u4ef6\u683c\u5f0f\u9519\u8bef: " + presetFile.getAbsolutePath());
                return false;
            }
            CapturePointManager manager = CapturePointManager.getInstance();
            manager.clearPlannedCapturePoints();
            manager.clearTeamRoles();
            for (Map.Entry entry : presetData.getPlannedPoints().entrySet()) {
                SerializablePlannedPoint serializablePoint = (SerializablePlannedPoint)entry.getValue();
                manager.addPlannedCapturePoint(serializablePoint.getName(), serializablePoint.getPos1(), serializablePoint.getPos2(), serializablePoint.getBatch());
            }
            manager.setTotalBatches(Math.max(1, manager.calculateTotalBatches()));
            Map<String, Integer> teamReinforcements = presetData.getTeamReinforcements();
            for (Map.Entry<String, String> entry : presetData.getTeamRoles().entrySet()) {
                String team = entry.getKey();
                String role = entry.getValue();
                int reinforcements = teamReinforcements != null ? teamReinforcements.getOrDefault(team, 50) : 50;
                manager.setTeamRole(team, role, reinforcements);
            }
            ModLogger.info("\u884c\u52a8\u653b\u9632\u6a21\u5f0f\u9884\u8bbe " + presetId + " \u5df2\u52a0\u8f7d\uff0c\u5305\u542b " + presetData.getPlannedPoints().size() + " \u4e2a\u8ba1\u5212\u636e\u70b9\u548c " + presetData.getTeamRoles().size() + " \u4e2a\u961f\u4f0d\u89d2\u8272\u8bbe\u7f6e");
            return true;
        }
        catch (IOException e) {
            ModLogger.error("\u52a0\u8f7d\u9884\u8bbe\u65f6\u53d1\u751f\u5f02\u5e38: " + e.getMessage());
            return false;
        }
    }

    private static class SerializablePlannedPoint {
        private String name;
        private BlockPos pos1;
        private BlockPos pos2;
        private int batch;

        public SerializablePlannedPoint(String name, BlockPos pos1, BlockPos pos2, int batch) {
            this.name = name;
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.batch = batch;
        }

        public String getName() {
            return this.name;
        }

        public BlockPos getPos1() {
            return this.pos1;
        }

        public BlockPos getPos2() {
            return this.pos2;
        }

        public int getBatch() {
            return this.batch;
        }
    }

    private static class PresetData {
        private Map<String, SerializablePlannedPoint> plannedPoints;
        private Map<String, String> teamRoles;
        private Map<String, Integer> teamReinforcements;

        public PresetData(Map<String, SerializablePlannedPoint> plannedPoints, Map<String, String> teamRoles, Map<String, Integer> teamReinforcements) {
            this.plannedPoints = plannedPoints;
            this.teamRoles = teamRoles;
            this.teamReinforcements = teamReinforcements;
        }

        public Map<String, SerializablePlannedPoint> getPlannedPoints() {
            return this.plannedPoints;
        }

        public Map<String, String> getTeamRoles() {
            return this.teamRoles;
        }

        public Map<String, Integer> getTeamReinforcements() {
            return this.teamReinforcements;
        }
    }

    private static class BlockPosAdapter
    implements JsonSerializer<BlockPos>,
    JsonDeserializer<BlockPos> {
        private BlockPosAdapter() {
        }

        public JsonElement serialize(BlockPos src, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("x", (Number)src.m_123341_());
            jsonObject.addProperty("y", (Number)src.m_123342_());
            jsonObject.addProperty("z", (Number)src.m_123343_());
            return jsonObject;
        }

        public BlockPos deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = json.getAsJsonObject();
            int x = jsonObject.get("x").getAsInt();
            int y = jsonObject.get("y").getAsInt();
            int z = jsonObject.get("z").getAsInt();
            return new BlockPos(x, y, z);
        }
    }
}

