/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.core.Direction
 *  net.minecraft.world.phys.AABB
 */
package dev.latvian.mods.kubejs.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.Direction;
import net.minecraft.world.phys.AABB;

public class ModelGenerator {
    private String parent = "minecraft:block/cube";
    private final JsonObject textures = new JsonObject();
    private final List<Element> elements = new ArrayList<Element>();

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        if (!this.parent.isEmpty()) {
            json.addProperty("parent", this.parent);
        }
        if (this.textures.size() > 0) {
            json.add("textures", (JsonElement)this.textures);
        }
        if (!this.elements.isEmpty()) {
            JsonArray a = new JsonArray();
            for (Element e : this.elements) {
                a.add((JsonElement)e.toJson());
            }
            json.add("elements", (JsonElement)a);
        }
        return json;
    }

    public void parent(String s) {
        this.parent = s;
    }

    public void texture(String name, String texture) {
        this.textures.addProperty(name, texture);
    }

    public void textures(JsonObject json) {
        for (Map.Entry entry : json.entrySet()) {
            this.textures.add((String)entry.getKey(), (JsonElement)entry.getValue());
        }
    }

    public void element(Consumer<Element> consumer) {
        Element e = new Element();
        consumer.accept(e);
        this.elements.add(e);
    }

    public static class Element {
        private AABB box = new AABB(0.0, 0.0, 0.0, 1.0, 1.0, 1.0);
        private final JsonObject faces = new JsonObject();

        public Element box(AABB b) {
            this.box = b;
            return this;
        }

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            JsonArray f = new JsonArray();
            f.add((Number)(this.box.f_82288_ * 16.0));
            f.add((Number)(this.box.f_82289_ * 16.0));
            f.add((Number)(this.box.f_82290_ * 16.0));
            json.add("from", (JsonElement)f);
            JsonArray t = new JsonArray();
            t.add((Number)(this.box.f_82291_ * 16.0));
            t.add((Number)(this.box.f_82292_ * 16.0));
            t.add((Number)(this.box.f_82293_ * 16.0));
            json.add("to", (JsonElement)t);
            json.add("faces", (JsonElement)this.faces);
            return json;
        }

        public void face(Direction direction, Consumer<Face> consumer) {
            Face f = new Face();
            f.direction = direction;
            consumer.accept(f);
            this.faces.add(direction.m_7912_(), (JsonElement)f.toJson());
        }
    }

    public static class Face {
        private Direction direction;
        private String texture = "broken";
        private Direction cullface = null;
        private double[] uv = null;
        private int tintindex = -1;

        public JsonObject toJson() {
            JsonObject json = new JsonObject();
            json.addProperty("texture", this.texture);
            if (this.cullface != null) {
                json.addProperty("cullface", this.cullface.m_7912_());
            }
            if (this.uv != null) {
                JsonArray a = new JsonArray();
                a.add((Number)this.uv[0]);
                a.add((Number)this.uv[1]);
                a.add((Number)this.uv[2]);
                a.add((Number)this.uv[3]);
                json.add("uv", (JsonElement)a);
            }
            if (this.tintindex >= 0) {
                json.addProperty("tintindex", (Number)this.tintindex);
            }
            return json;
        }

        public Face tex(String t) {
            this.texture = t;
            return this;
        }

        public Face cull(Direction d) {
            this.cullface = d;
            return this;
        }

        public Face cull() {
            return this.cull(this.direction);
        }

        public Face uv(double u0, double v0, double u1, double v1) {
            this.uv = new double[]{u0, v0, u1, v1};
            return this;
        }

        public Face tintindex(int i) {
            this.tintindex = i;
            return this;
        }
    }
}

