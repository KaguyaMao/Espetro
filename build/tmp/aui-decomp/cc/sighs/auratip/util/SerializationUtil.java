/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 */
package cc.sighs.auratip.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import java.util.HashMap;
import java.util.Map;

public final class SerializationUtil {
    private static final ThreadLocal<ParamCapture> PARAM_CAPTURE = new ThreadLocal();

    private SerializationUtil() {
    }

    public static Map<String, Dynamic<?>> convertMapToDynamic(Map<String, Object> source) {
        HashMap result = new HashMap();
        if (source == null || source.isEmpty()) {
            return result;
        }
        Gson gson = new Gson();
        source.forEach((k, v) -> {
            JsonElement jsonElement = gson.toJsonTree(v);
            Dynamic dynamic = new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)jsonElement);
            result.put((String)k, (Dynamic<?>)dynamic);
        });
        return result;
    }

    public static Dynamic<?> dynamicOf(String value) {
        JsonNull element = value == null ? JsonNull.INSTANCE : new JsonPrimitive(value);
        return new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)element);
    }

    public static Dynamic<?> dynamicOf(int value) {
        JsonPrimitive element = new JsonPrimitive((Number)value);
        return new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)element);
    }

    public static double getDouble(Map<String, Dynamic<?>> params, String key, double fallback) {
        SerializationUtil.captureParam("number", key, fallback);
        Dynamic<?> dynamic = params.get(key);
        return dynamic == null ? fallback : dynamic.asDouble(fallback);
    }

    public static float getFloat(Map<String, Dynamic<?>> params, String key, float fallback) {
        SerializationUtil.captureParam("number", key, Float.valueOf(fallback));
        Dynamic<?> d = params.get(key);
        return d == null ? fallback : d.asFloat(fallback);
    }

    public static int getInt(Map<String, Dynamic<?>> params, String key, int fallback) {
        SerializationUtil.captureParam("number", key, fallback);
        Dynamic<?> d = params.get(key);
        return d == null ? fallback : d.asInt(fallback);
    }

    public static long getLong(Map<String, Dynamic<?>> params, String key, long fallback) {
        SerializationUtil.captureParam("number", key, fallback);
        Dynamic<?> d = params.get(key);
        return d == null ? fallback : d.asLong(fallback);
    }

    public static boolean getBoolean(Map<String, Dynamic<?>> params, String key, boolean fallback) {
        SerializationUtil.captureParam("boolean", key, fallback);
        Dynamic<?> d = params.get(key);
        return d == null ? fallback : d.asBoolean(fallback);
    }

    public static String getString(Map<String, Dynamic<?>> params, String key, String fallback) {
        SerializationUtil.captureParam("string", key, fallback);
        Dynamic<?> d = params.get(key);
        return d == null ? fallback : d.asString(fallback);
    }

    public static Map<String, CapturedParam> captureParams(Runnable action) {
        if (action == null) {
            return Map.of();
        }
        ParamCapture capture = new ParamCapture();
        PARAM_CAPTURE.set(capture);
        try {
            action.run();
        }
        finally {
            PARAM_CAPTURE.remove();
        }
        return capture.snapshot();
    }

    private static void captureParam(String kind, String key, Object fallback) {
        if (key == null || key.isEmpty()) {
            return;
        }
        ParamCapture capture = PARAM_CAPTURE.get();
        if (capture == null) {
            return;
        }
        capture.put(kind, key, fallback);
    }

    private static final class ParamCapture {
        private final Map<String, CapturedParam> params = new HashMap<String, CapturedParam>();

        private ParamCapture() {
        }

        void put(String kind, String key, Object fallback) {
            this.params.putIfAbsent(key, new CapturedParam(kind, fallback));
        }

        Map<String, CapturedParam> snapshot() {
            return this.params.isEmpty() ? Map.of() : Map.copyOf(this.params);
        }
    }

    public record CapturedParam(String kind, Object fallback) {
    }
}

