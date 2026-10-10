/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonPrimitive
 *  dev.latvian.mods.rhino.mod.util.JsonUtils
 *  dev.latvian.mods.rhino.mod.util.NBTUtils
 *  net.minecraft.nbt.CompoundTag
 *  net.minecraft.nbt.Tag
 *  org.jetbrains.annotations.Nullable
 */
package dev.latvian.mods.kubejs.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import dev.latvian.mods.kubejs.util.JsonIO;
import dev.latvian.mods.rhino.mod.util.JsonUtils;
import dev.latvian.mods.rhino.mod.util.NBTUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import org.jetbrains.annotations.Nullable;

public interface MapJS {
    @Nullable
    public static Map<?, ?> of(@Nullable Object o) {
        if (o instanceof Map) {
            Map m = (Map)o;
            return m;
        }
        if (o instanceof CompoundTag) {
            CompoundTag tag = (CompoundTag)o;
            LinkedHashMap<String, Tag> map = new LinkedHashMap<String, Tag>();
            for (String key : tag.m_128431_()) {
                map.put(key, tag.m_128423_(key));
            }
            return map;
        }
        if (o instanceof JsonObject) {
            JsonObject json = (JsonObject)o;
            LinkedHashMap<String, Object> map = new LinkedHashMap<String, Object>();
            for (Map.Entry entry : json.entrySet()) {
                map.put((String)entry.getKey(), JsonUtils.toObject((JsonElement)((JsonElement)entry.getValue())));
            }
            return map;
        }
        return null;
    }

    public static Map<?, ?> orEmpty(@Nullable Object o) {
        Map<?, ?> map = MapJS.of(o);
        return map != null ? map : Map.of();
    }

    @Deprecated
    @Nullable
    public static CompoundTag nbt(@Nullable Object map) {
        return NBTUtils.toTagCompound((Object)map);
    }

    @Nullable
    public static JsonObject json(@Nullable Object map) {
        if (map instanceof JsonObject) {
            JsonObject json = (JsonObject)map;
            return json;
        }
        if (map instanceof CharSequence) {
            try {
                return (JsonObject)JsonIO.GSON.fromJson(map.toString(), JsonObject.class);
            }
            catch (Exception ex) {
                return null;
            }
        }
        Map<?, ?> m = MapJS.of(map);
        if (m != null) {
            JsonObject json = new JsonObject();
            for (Map.Entry<?, ?> entry : m.entrySet()) {
                Double d;
                Number number;
                JsonPrimitive p;
                JsonElement e = JsonIO.of(entry.getValue());
                if (e instanceof JsonPrimitive && (p = (JsonPrimitive)e).isNumber() && (number = p.getAsNumber()) instanceof Double && (d = (Double)number) <= 9.223372036854776E18 && d >= -9.223372036854776E18 && d == (double)d.longValue()) {
                    json.add(String.valueOf(entry.getKey()), (JsonElement)new JsonPrimitive((Number)d.longValue()));
                    continue;
                }
                json.add(String.valueOf(entry.getKey()), e);
            }
            return json;
        }
        return null;
    }
}

