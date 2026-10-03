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
package cc.sighs.auratip.compat.kubejs.radiamenu;

import cc.sighs.auratip.compat.kubejs.radiamenu.RadialMenuBuilder;
import cc.sighs.auratip.data.RadialMenuData;
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

public class RadialMenuRegistrationEvent
extends EventJS {
    private final Map<String, RadialMenuBuilder> menus = new LinkedHashMap<String, RadialMenuBuilder>();
    private final Map<String, RadialMenuData> imported = new LinkedHashMap<String, RadialMenuData>();
    private static final Gson GSON = new Gson();

    @Info(value="Create a new radial menu and return its builder. The id can omit namespace (defaults to kubejs). Duplicate ids will throw.")
    public RadialMenuBuilder create(String id) {
        String key = RadialMenuRegistrationEvent.normalizeId(id);
        if (this.menus.containsKey(key)) {
            throw new IllegalStateException("Duplicate radial menu id: " + key);
        }
        if (this.imported.containsKey(key)) {
            throw new IllegalStateException("Duplicate radial menu id (already imported): " + key);
        }
        RadialMenuBuilder builder = new RadialMenuBuilder(key);
        this.menus.put(key, builder);
        return builder;
    }

    @Info(value="Remove a previously created radial menu by id. The id can omit namespace (defaults to kubejs).")
    public void remove(String id) {
        String key = RadialMenuRegistrationEvent.normalizeId(id);
        this.menus.remove(key);
        this.imported.remove(key);
    }

    @Info(value="Import a RadialMenuData from a JSON object (or JSON string) that matches RadialMenuData.CODEC. The menu id comes from the JSON.")
    public void importJson(Object json) {
        JsonElement element = RadialMenuRegistrationEvent.toJsonElement(json);
        RadialMenuData data = (RadialMenuData)RadialMenuData.CODEC.parse((DynamicOps)JsonOps.INSTANCE, (Object)element).resultOrPartial(err -> {
            throw new IllegalStateException("RadialMenuData import failed: " + err);
        }).orElseThrow();
        String key = data.id().toString();
        if (this.menus.containsKey(key) || this.imported.containsKey(key)) {
            throw new IllegalStateException("Duplicate radial menu id: " + key);
        }
        this.imported.put(key, data);
    }

    @Info(value="Internal: build all radial menus created in this event into data objects.")
    public List<RadialMenuData> buildAll() {
        ArrayList<RadialMenuData> result = new ArrayList<RadialMenuData>(this.imported.values());
        for (RadialMenuBuilder builder : this.menus.values()) {
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
            return "kubejs:radial_menu";
        }
        if (id.indexOf(58) < 0) {
            return "kubejs:" + id;
        }
        return id;
    }
}

