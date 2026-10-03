/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.GsonBuilder
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.google.gson.JsonPrimitive
 *  com.google.gson.internal.Streams
 *  com.google.gson.stream.JsonWriter
 *  dev.latvian.mods.rhino.mod.util.JsonUtils
 *  dev.latvian.mods.rhino.util.HideFromJS
 *  net.minecraft.nbt.CompoundTag
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonWriter;
import dev.latvian.mods.kubejs.util.JSObjectType;
import dev.latvian.mods.kubejs.util.ListJS;
import dev.latvian.mods.kubejs.util.MapJS;
import dev.latvian.mods.kubejs.util.UtilsJS;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.util.Collection;
import java.util.HexFormat;
import java.util.Map;
import java.util.Objects;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class JsonIO {
    @HideFromJS
    public static final Gson GSON = new GsonBuilder().disableHtmlEscaping().setLenient().create();

    public static JsonElement copy(@Nullable JsonElement element) {
        return JsonUtils.copy((JsonElement)element);
    }

    @Nullable
    public static JsonElement of(@Nullable Object o) {
        if (o instanceof JsonElement) {
            return (JsonElement)o;
        }
        if (o instanceof Map || o instanceof CompoundTag) {
            return MapJS.json(o);
        }
        if (o instanceof Collection) {
            return ListJS.json(o);
        }
        JsonElement e = JsonUtils.of((Object)o);
        return e == JsonNull.INSTANCE ? null : e;
    }

    public static JsonPrimitive primitiveOf(@Nullable Object o) {
        JsonPrimitive p;
        JsonElement jsonElement = JsonIO.of(o);
        return jsonElement instanceof JsonPrimitive ? (p = (JsonPrimitive)jsonElement) : null;
    }

    @Nullable
    public static Object toObject(@Nullable JsonElement json) {
        return JsonUtils.toObject((JsonElement)json);
    }

    public static String toString(JsonElement json) {
        return JsonUtils.toString((JsonElement)json);
    }

    public static String toPrettyString(JsonElement json) {
        return JsonUtils.toPrettyString((JsonElement)json);
    }

    public static JsonElement parseRaw(@Nullable String string) {
        return JsonUtils.fromString((String)string);
    }

    public static Object parse(String string) {
        return UtilsJS.wrap(JsonIO.parseRaw(string), JSObjectType.ANY);
    }

    @Nullable
    public static Object toPrimitive(@Nullable JsonElement element) {
        if (element == null || element.isJsonNull()) {
            return null;
        }
        if (element.isJsonPrimitive()) {
            JsonPrimitive p = element.getAsJsonPrimitive();
            if (p.isBoolean()) {
                return p.getAsBoolean();
            }
            if (p.isNumber()) {
                return p.getAsNumber();
            }
            try {
                Double.parseDouble(p.getAsString());
                return p.getAsNumber();
            }
            catch (Exception ex) {
                return p.getAsString();
            }
        }
        return null;
    }

    public static JsonElement readJson(Path path) throws IOException {
        if (!Files.isRegularFile(path, new LinkOption[0])) {
            return null;
        }
        try (BufferedReader fileReader = Files.newBufferedReader(path);){
            JsonElement jsonElement = JsonParser.parseReader((Reader)fileReader);
            return jsonElement;
        }
    }

    public static String readString(Path path) throws IOException {
        return JsonIO.toString(JsonIO.readJson(path));
    }

    @Nullable
    public static Map<?, ?> read(Path path) throws IOException {
        return MapJS.of(JsonIO.readJson(path));
    }

    public static void write(Path path, @Nullable JsonObject json) throws IOException {
        if (json == null || json.isJsonNull()) {
            Files.deleteIfExists(path);
            return;
        }
        try (BufferedWriter fileWriter = Files.newBufferedWriter(path, new OpenOption[0]);){
            JsonWriter jsonWriter = new JsonWriter((Writer)fileWriter);
            jsonWriter.setIndent("\t");
            jsonWriter.setSerializeNulls(true);
            jsonWriter.setLenient(true);
            Streams.write((JsonElement)json, (JsonWriter)jsonWriter);
        }
    }

    public static JsonArray toArray(JsonElement element) {
        if (element.isJsonArray()) {
            return element.getAsJsonArray();
        }
        JsonArray a = new JsonArray();
        a.add(element);
        return a;
    }

    public static void writeJsonHash(DataOutputStream stream, @Nullable JsonElement element) throws IOException {
        if (element == null || element.isJsonNull()) {
            stream.writeByte(45);
        } else if (element instanceof JsonArray) {
            JsonArray arr = (JsonArray)element;
            stream.writeByte(91);
            for (JsonElement e : arr) {
                JsonIO.writeJsonHash(stream, e);
            }
        } else if (element instanceof JsonObject) {
            JsonObject obj = (JsonObject)element;
            stream.writeByte(123);
            for (Map.Entry e : obj.entrySet()) {
                stream.writeBytes((String)e.getKey());
                JsonIO.writeJsonHash(stream, (JsonElement)e.getValue());
            }
        } else if (element instanceof JsonPrimitive) {
            JsonPrimitive primitive = (JsonPrimitive)element;
            stream.writeByte(61);
            if (primitive.isBoolean()) {
                stream.writeBoolean(element.getAsBoolean());
            } else if (primitive.isNumber()) {
                stream.writeDouble(element.getAsDouble());
            } else {
                stream.writeBytes(element.getAsString());
            }
        } else {
            stream.writeByte(63);
            stream.writeInt(element.hashCode());
        }
    }

    public static byte[] getJsonHashBytes(JsonElement json) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            JsonIO.writeJsonHash(new DataOutputStream(baos), json);
        }
        catch (IOException ex) {
            ex.printStackTrace();
            int h = json.hashCode();
            return new byte[]{(byte)(h >> 24), (byte)(h >> 16), (byte)(h >> 8), (byte)h};
        }
        return baos.toByteArray();
    }

    public static String getJsonHashString(JsonElement json) {
        try {
            MessageDigest messageDigest = Objects.requireNonNull(MessageDigest.getInstance("MD5"));
            return new BigInteger(HexFormat.of().formatHex(messageDigest.digest(JsonIO.getJsonHashBytes(json))), 16).toString(36);
        }
        catch (Exception ex) {
            return "%08x".formatted(json.hashCode());
        }
    }
}

