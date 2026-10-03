/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 */
package org.espetro.mapconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.IOException;
import java.nio.file.Path;
import net.minecraft.nbt.ByteArrayTag;
import net.minecraft.nbt.ByteTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.IntArrayTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongArrayTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.ShortTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

public final class LevelDatDimensionReader {
    private LevelDatDimensionReader() {
    }

    public static JsonObject readDimensionJson(Path levelDat) throws IOException {
        CompoundTag root = NbtIo.m_128937_(levelDat.toFile());
        CompoundTag data = LevelDatDimensionReader.requireCompound(root, "Data");
        CompoundTag settings = LevelDatDimensionReader.requireCompound(data, "WorldGenSettings");
        CompoundTag dimensions = LevelDatDimensionReader.requireCompound(settings, "dimensions");
        CompoundTag overworld = LevelDatDimensionReader.requireCompound(dimensions, "minecraft:overworld");
        CompoundTag generator = LevelDatDimensionReader.requireCompound(overworld, "generator");
        JsonObject result = new JsonObject();
        String dimensionType = overworld.m_128461_("type");
        result.addProperty("type", dimensionType.isBlank() ? "minecraft:overworld" : dimensionType);
        JsonElement generatorJson = LevelDatDimensionReader.toJson(generator);
        if (!generatorJson.isJsonObject() || !generatorJson.getAsJsonObject().has("type")) {
            throw new IOException("level.dat \u7684 Overworld generator \u7f3a\u5c11 type");
        }
        result.add("generator", generatorJson);
        return result;
    }

    private static CompoundTag requireCompound(CompoundTag parent, String key) throws IOException {
        Tag value = parent.m_128423_(key);
        if (!(value instanceof CompoundTag)) {
            throw new IOException("level.dat \u7f3a\u5c11\u590d\u5408\u8282\u70b9 " + key);
        }
        CompoundTag compound = (CompoundTag)value;
        return compound;
    }

    static JsonElement toJson(Tag tag) throws IOException {
        if (tag instanceof CompoundTag) {
            CompoundTag compound = (CompoundTag)tag;
            JsonObject object = new JsonObject();
            for (String key : compound.m_128431_()) {
                Tag value = compound.m_128423_(key);
                if (value == null) continue;
                object.add(key, LevelDatDimensionReader.toJson(value));
            }
            return object;
        }
        if (tag instanceof ListTag) {
            ListTag list = (ListTag)tag;
            JsonArray array = new JsonArray();
            for (Tag value : list) {
                array.add(LevelDatDimensionReader.toJson(value));
            }
            return array;
        }
        if (tag instanceof StringTag) {
            StringTag string = (StringTag)tag;
            return new JsonPrimitive(string.m_7916_());
        }
        if (tag instanceof ByteTag) {
            ByteTag value = (ByteTag)tag;
            return new JsonPrimitive(Boolean.valueOf(value.m_7063_() != 0));
        }
        if (tag instanceof IntTag) {
            IntTag value = (IntTag)tag;
            return new JsonPrimitive((Number)value.m_7047_());
        }
        if (tag instanceof LongTag) {
            LongTag value = (LongTag)tag;
            return new JsonPrimitive((Number)value.m_7046_());
        }
        if (tag instanceof ShortTag) {
            ShortTag value = (ShortTag)tag;
            return new JsonPrimitive((Number)value.m_7053_());
        }
        if (tag instanceof FloatTag) {
            FloatTag value = (FloatTag)tag;
            return new JsonPrimitive((Number)Float.valueOf(value.m_7057_()));
        }
        if (tag instanceof DoubleTag) {
            DoubleTag value = (DoubleTag)tag;
            return new JsonPrimitive((Number)value.m_7061_());
        }
        if (tag instanceof ByteArrayTag) {
            ByteArrayTag array = (ByteArrayTag)tag;
            JsonArray result = new JsonArray();
            for (byte value : array.m_128227_()) {
                result.add((Number)value);
            }
            return result;
        }
        if (tag instanceof IntArrayTag) {
            IntArrayTag array = (IntArrayTag)tag;
            JsonArray result = new JsonArray();
            for (int value : array.m_128648_()) {
                result.add((Number)value);
            }
            return result;
        }
        if (tag instanceof LongArrayTag) {
            LongArrayTag array = (LongArrayTag)tag;
            JsonArray result = new JsonArray();
            for (long value : array.m_128851_()) {
                result.add((Number)value);
            }
            return result;
        }
        if (tag instanceof NumericTag) {
            NumericTag numeric = (NumericTag)tag;
            return new JsonPrimitive(numeric.m_8103_());
        }
        throw new IOException("\u4e0d\u652f\u6301\u7684 level.dat NBT \u7c7b\u578b: " + tag.m_6458_().m_5987_());
    }
}

