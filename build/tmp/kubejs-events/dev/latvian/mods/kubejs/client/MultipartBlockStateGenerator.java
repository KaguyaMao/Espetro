/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.kubejs.client.VariantBlockStateGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class MultipartBlockStateGenerator {
    private final JsonArray multipart = new JsonArray();

    public void part(String when, Consumer<Part> consumer) {
        Part v = new Part();
        v.when = when;
        consumer.accept(v);
        this.multipart.add((JsonElement)v.toJson());
    }

    public void part(String when, String model) {
        this.part(when, (Part v) -> v.model(model));
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("multipart", (JsonElement)this.multipart);
        return json;
    }

    public static class Part {
        private String when;
        private final List<VariantBlockStateGenerator.Model> apply = new ArrayList<VariantBlockStateGenerator.Model>();

        public VariantBlockStateGenerator.Model model(String s) {
            VariantBlockStateGenerator.Model model = new VariantBlockStateGenerator.Model();
            model.model(s);
            this.apply.add(model);
            return model;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            if (!this.when.isEmpty()) {
                JsonObject whenJson = new JsonObject();
                for (String s : this.when.split(",")) {
                    String[] s1 = s.split("=", 2);
                    if (s1.length != 2 || s1[0].isEmpty() || s1[1].isEmpty()) continue;
                    whenJson.addProperty(s1[0], s1[1]);
                }
                json.add("when", (JsonElement)whenJson);
            }
            if (this.apply.size() == 1) {
                json.add("apply", (JsonElement)this.apply.get(0).toJson());
            } else {
                JsonArray a = new JsonArray();
                for (VariantBlockStateGenerator.Model m : this.apply) {
                    a.add((JsonElement)m.toJson());
                }
                json.add("apply", (JsonElement)a);
            }
            return json;
        }
    }
}

