/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 */
package org.espetro.mapconfig;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

public final class SquadTypesSnapshot {
    public static final String NONE_ID = "none";
    public static final String NONE_DISPLAY = "\u65e0";
    public final List<Category> categories;
    public final List<String> errors;

    public SquadTypesSnapshot(List<Category> categories, List<String> errors) {
        this.categories = List.copyOf(categories);
        this.errors = List.copyOf(errors);
    }

    public boolean isValid() {
        return this.errors.isEmpty();
    }

    public Category find(String id) {
        if (id == null || id.isBlank() || NONE_ID.equals(id)) {
            return new Category(NONE_ID, NONE_DISPLAY);
        }
        for (Category c : this.categories) {
            if (!c.id.equals(id)) continue;
            return c;
        }
        return null;
    }

    public static SquadTypesSnapshot parse(JsonObject root) {
        ArrayList<String> errors = new ArrayList<String>();
        ArrayList<Category> list = new ArrayList<Category>();
        list.add(new Category(NONE_ID, NONE_DISPLAY));
        LinkedHashSet<String> seen = new LinkedHashSet<String>();
        seen.add(NONE_ID);
        if (!root.has("types") || !root.get("types").isJsonArray()) {
            errors.add("SquadTypes.json \u7f3a\u5c11 types \u6570\u7ec4");
            return new SquadTypesSnapshot(list, errors);
        }
        JsonArray arr = root.getAsJsonArray("types");
        for (JsonElement el : arr) {
            String display;
            if (!el.isJsonObject()) {
                errors.add("types \u5143\u7d20\u5fc5\u987b\u662f\u5bf9\u8c61");
                continue;
            }
            JsonObject o = el.getAsJsonObject();
            if (!o.has("id") || !o.get("id").isJsonPrimitive()) {
                errors.add("\u5c0f\u961f\u7c7b\u522b\u7f3a\u5c11 id");
                continue;
            }
            String id = o.get("id").getAsString().trim().toLowerCase(Locale.ROOT);
            if (!id.matches("[a-z0-9_]+")) {
                errors.add("\u5c0f\u961f\u7c7b\u522b id \u975e\u6cd5: " + id);
                continue;
            }
            if (!seen.add(id)) {
                errors.add("\u91cd\u590d\u7684\u5c0f\u961f\u7c7b\u522b id: " + id);
                continue;
            }
            String string = display = o.has("display_name") && o.get("display_name").isJsonPrimitive() ? o.get("display_name").getAsString() : id;
            if (NONE_ID.equals(id)) continue;
            list.add(new Category(id, display));
        }
        return new SquadTypesSnapshot(list, errors);
    }

    public static SquadTypesSnapshot defaults() {
        List<Category> list = List.of(new Category(NONE_ID, NONE_DISPLAY), new Category("infantry", "\u6b65\u5175\u961f"), new Category("support", "\u652f\u63f4\u961f"), new Category("vehicle", "\u8f7d\u5177\u961f"), new Category("recon", "\u4fa6\u67e5\u961f"));
        return new SquadTypesSnapshot(list, List.of());
    }

    public record Category(String id, String displayName) {
        public String firstDisplayCodePoint() {
            if (SquadTypesSnapshot.NONE_ID.equals(this.id) || this.displayName == null || this.displayName.isEmpty()) {
                return "";
            }
            return new String(Character.toChars(this.displayName.codePointAt(0)));
        }
    }
}

