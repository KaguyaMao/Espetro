/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.latvian.mods.kubejs.event.EventJS
 *  dev.latvian.mods.kubejs.typings.Info
 */
package cc.sighs.auratip.compat.kubejs.tip;

import cc.sighs.auratip.compat.kubejs.tip.TipBuilder;
import cc.sighs.auratip.data.TipData;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonParser;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TipRegistrationEvent
extends EventJS {
    private final Map<String, TipBuilder> tips = new LinkedHashMap<String, TipBuilder>();
    private final Map<String, TipData> imported = new LinkedHashMap<String, TipData>();
    private static final Gson GSON = new Gson();

    @Info(value="Create a new Tip and return its builder. The id can omit namespace (defaults to kubejs). Duplicate ids will throw.")
    public TipBuilder create(String id) {
        String key = TipRegistrationEvent.normalizeId(id);
        if (this.tips.containsKey(key)) {
            throw new IllegalStateException("Duplicate tip id: " + key);
        }
        if (this.imported.containsKey(key)) {
            throw new IllegalStateException("Duplicate tip id (already imported): " + key);
        }
        TipBuilder builder = new TipBuilder(key);
        this.tips.put(key, builder);
        return builder;
    }

    @Info(value="Remove a previously created Tip by id. The id can omit namespace (defaults to kubejs).")
    public void remove(String id) {
        String key = TipRegistrationEvent.normalizeId(id);
        this.tips.remove(key);
        this.imported.remove(key);
    }

    @Info(value="Import a TipData from a JSON object (or JSON string) that matches TipData.CODEC. The tip id comes from the JSON.")
    public void importJson(Object json) {
        JsonElement element = TipRegistrationEvent.toJsonElement(json);
        TipData data = (TipData)TipData.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)element).resultOrPartial(err -> {
            throw new IllegalStateException("TipData import failed: " + err);
        }).orElseThrow();
        String key = data.id().toString();
        if (this.tips.containsKey(key) || this.imported.containsKey(key)) {
            throw new IllegalStateException("Duplicate tip id: " + key);
        }
        this.imported.put(key, data);
    }

    @Info(value="Internal: build all Tips created in this event into data objects.")
    public List<TipData> buildAll() {
        ArrayList<TipData> result = new ArrayList<TipData>(this.imported.values());
        for (TipBuilder builder : this.tips.values()) {
            result.add(builder.build());
        }
        return result;
    }

    private static JsonElement toJsonElement(Object json) {
        if (json == null) {
            return JsonNull.INSTANCE;
        }
        if (json instanceof JsonElement) {
            JsonElement e = (JsonElement)json;
            return e;
        }
        if (json instanceof CharSequence) {
            CharSequence seq = (CharSequence)json;
            String s = seq.toString();
            try {
                return JsonParser.parseString((String)s);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        return GSON.toJsonTree(json);
    }

    private static String normalizeId(String id) {
        if (id == null || id.isEmpty()) {
            return "kubejs:tip";
        }
        if (id.indexOf(58) < 0) {
            return "kubejs:" + id;
        }
        return id;
    }
}

