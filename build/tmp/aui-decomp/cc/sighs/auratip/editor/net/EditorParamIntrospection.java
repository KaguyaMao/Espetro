/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.editor.net;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.api.action.ActionHandlers;
import cc.sighs.auratip.data.animation.AnimationType;
import cc.sighs.auratip.util.SerializationUtil;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

final class EditorParamIntrospection {
    private EditorParamIntrospection() {
    }

    static JsonObject buildInitParamPayload() {
        JsonObject out = new JsonObject();
        out.add("animation", (JsonElement)EditorParamIntrospection.buildAnimationParamDefs());
        out.add("actions", (JsonElement)EditorParamIntrospection.buildActionParamDefs());
        return out;
    }

    static JsonObject buildAnimationParamDefs() {
        JsonObject root = new JsonObject();
        root.add("transition", (JsonElement)EditorParamIntrospection.buildTransitionAnimationParams());
        root.add("hover", (JsonElement)EditorParamIntrospection.buildHoverAnimationParams());
        return root;
    }

    static JsonObject buildTransitionAnimationParams() {
        return EditorParamIntrospection.buildAnimationParams(AnimationType.listTransitionIds(), false);
    }

    static JsonObject buildHoverAnimationParams() {
        return EditorParamIntrospection.buildAnimationParams(AnimationType.listHoverIds(), true);
    }

    private static JsonObject buildAnimationParams(Set<ResourceLocation> ids, boolean hover) {
        JsonObject out = new JsonObject();
        if (ids == null || ids.isEmpty()) {
            return out;
        }
        ids.stream().sorted(Comparator.comparing(ResourceLocation::toString)).forEach(id -> {
            Map<String, SerializationUtil.CapturedParam> captured = hover ? AnimationType.getDeclaredHoverParams(id) : AnimationType.getDeclaredParams(id);
            try {
                if (captured == null || captured.isEmpty()) {
                    captured = SerializationUtil.captureParams(() -> {
                        try {
                            if (hover) {
                                AnimationType.resolveHover(id, Map.of());
                            } else {
                                AnimationType.resolve(id, Map.of());
                            }
                        }
                        catch (Throwable throwable) {
                            // empty catch block
                        }
                    });
                }
            }
            catch (Throwable t) {
                AuraTip.LOGGER.debug("Editor param introspection failed for animation {}", id, (Object)t);
                captured = Map.of();
            }
            JsonArray arr = new JsonArray();
            if (captured != null && !captured.isEmpty()) {
                captured.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
                    JsonObject p = new JsonObject();
                    p.addProperty("name", (String)e.getKey());
                    p.addProperty("kind", ((SerializationUtil.CapturedParam)e.getValue()).kind());
                    Object fallback = ((SerializationUtil.CapturedParam)e.getValue()).fallback();
                    if (fallback instanceof Number) {
                        Number n = (Number)fallback;
                        p.addProperty("default", n);
                    } else if (fallback instanceof Boolean) {
                        Boolean b = (Boolean)fallback;
                        p.addProperty("default", b);
                    } else if (fallback != null) {
                        p.addProperty("default", String.valueOf(fallback));
                    }
                    arr.add((JsonElement)p);
                });
            }
            out.add(id.toString(), (JsonElement)arr);
        });
        return out;
    }

    static JsonObject buildActionParamDefs() {
        JsonObject out = new JsonObject();
        out.add("auratip:run_command", (JsonElement)EditorParamIntrospection.params(EditorParamIntrospection.p("command", "string", "")));
        out.add("auratip:simulate_key", (JsonElement)EditorParamIntrospection.params(EditorParamIntrospection.p("key_code", "number", 0)));
        Set<ResourceLocation> types = ActionHandlers.listTypes();
        if (types != null && !types.isEmpty()) {
            types.stream().sorted(Comparator.comparing(ResourceLocation::toString)).forEach(type -> {
                Map<String, SerializationUtil.CapturedParam> schema = ActionHandlers.getDeclaredParams(type);
                if (schema == null || schema.isEmpty()) {
                    return;
                }
                JsonArray arr = new JsonArray();
                schema.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(e -> {
                    JsonObject p = new JsonObject();
                    p.addProperty("name", (String)e.getKey());
                    p.addProperty("kind", ((SerializationUtil.CapturedParam)e.getValue()).kind());
                    Object fallback = ((SerializationUtil.CapturedParam)e.getValue()).fallback();
                    if (fallback instanceof Number) {
                        Number n = (Number)fallback;
                        p.addProperty("default", n);
                    } else if (fallback instanceof Boolean) {
                        Boolean b = (Boolean)fallback;
                        p.addProperty("default", b);
                    } else if (fallback != null) {
                        p.addProperty("default", String.valueOf(fallback));
                    }
                    arr.add((JsonElement)p);
                });
                out.add(type.toString(), (JsonElement)arr);
            });
        }
        return out;
    }

    static JsonArray listActionTypes() {
        Set<ResourceLocation> custom = ActionHandlers.listTypes();
        LinkedHashMap<String, Boolean> ordered = new LinkedHashMap<String, Boolean>();
        ordered.put("auratip:run_command", true);
        ordered.put("auratip:simulate_key", true);
        if (custom != null && !custom.isEmpty()) {
            custom.stream().sorted(Comparator.comparing(ResourceLocation::toString)).forEach(id -> ordered.put(id.toString(), true));
        }
        JsonArray arr = new JsonArray();
        ordered.keySet().forEach(arg_0 -> ((JsonArray)arr).add(arg_0));
        return arr;
    }

    private static JsonArray params(JsonObject ... defs) {
        JsonArray arr = new JsonArray();
        for (JsonObject def : defs) {
            arr.add((JsonElement)def);
        }
        return arr;
    }

    private static JsonObject p(String name, String kind, Object def) {
        JsonObject o = new JsonObject();
        o.addProperty("name", name);
        o.addProperty("kind", kind);
        if (def instanceof Number) {
            Number n = (Number)def;
            o.addProperty("default", n);
        } else if (def instanceof Boolean) {
            Boolean b = (Boolean)def;
            o.addProperty("default", b);
        } else if (def != null) {
            o.addProperty("default", String.valueOf(def));
        }
        return o;
    }
}

