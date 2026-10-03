/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  dev.latvian.mods.rhino.util.HideFromJS
 */
package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import dev.latvian.mods.rhino.util.HideFromJS;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VariantBlockStateGenerator {
    private final JsonObject variants = new JsonObject();

    public void variant(String key, Consumer<Variant> consumer) {
        Variant v = new Variant();
        v.key = key;
        consumer.accept(v);
        this.variants.add(v.key, v.toJson());
    }

    @HideFromJS
    @Deprecated
    public void variant(String key, String model) {
        this.simpleVariant(key, model);
    }

    public void simpleVariant(String key, String model) {
        this.variant(key, (Variant v) -> v.model(model));
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.add("variants", (JsonElement)this.variants);
        return json;
    }

    public static class Variant {
        private String key;
        private final List<Model> models = new ArrayList<Model>();

        public Model model(String s) {
            Model model = new Model();
            model.model(s);
            this.models.add(model);
            return model;
        }

        public JsonElement toJson() {
            if (this.models.size() == 1) {
                return this.models.get(0).toJson();
            }
            JsonArray a = new JsonArray();
            for (Model m : this.models) {
                a.add((JsonElement)m.toJson());
            }
            return a;
        }
    }

    public static class Model {
        private String model = "broken";
        private int x = 0;
        private int y = 0;
        private boolean uvlock = false;

        public Model model(String s) {
            this.model = s;
            return this;
        }

        public Model x(int _x) {
            this.x = _x;
            return this;
        }

        public Model y(int _y) {
            this.y = _y;
            return this;
        }

        public Model uvlock() {
            this.uvlock = true;
            return this;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("model", this.model);
            if (this.x != 0) {
                json.addProperty("x", (Number)this.x);
            }
            if (this.y != 0) {
                json.addProperty("y", (Number)this.y);
            }
            if (this.uvlock) {
                json.addProperty("uvlock", Boolean.valueOf(true));
            }
            return json;
        }
    }
}

