/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  dev.latvian.mods.kubejs.typings.Info
 *  dev.latvian.mods.rhino.Scriptable
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.compat.kubejs.tip.animation;

import cc.sighs.auratip.api.animation.HoverAnimation;
import cc.sighs.auratip.api.animation.TransitionAnimation;
import cc.sighs.auratip.compat.kubejs.tip.animation.JsHoverAnimation;
import cc.sighs.auratip.compat.kubejs.tip.animation.JsTransitionAnimation;
import cc.sighs.auratip.data.animation.AnimationType;
import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Dynamic;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.rhino.Scriptable;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;

public class TipAnimations {
    @Info(value="Register a custom Tip transition animation type. The factory must return TransitionAnimation or a JS object (auto-wrapped).")
    public static void register(String id, Function<Map<String, Dynamic<?>>, Object> factory) {
        ResourceLocation rid = TipAnimations.normalizeId(id);
        TipAnimations.register0(rid, id, factory);
    }

    @Info(value="Register a custom Tip transition animation type with parameter defaults (tooling-only). paramDefaults is a map of key -> default value.")
    public static void register(String id, Map<?, ?> paramDefaults, Function<Map<String, Dynamic<?>>, Object> factory) {
        ResourceLocation rid = TipAnimations.normalizeId(id);
        TipAnimations.register0(rid, id, factory);
        AnimationType.declareParamsInternal(rid, TipAnimations.schemaFrom(paramDefaults));
    }

    @Info(value="Register a custom Tip hover animation type. The factory must return HoverAnimation or a JS object (auto-wrapped).")
    public static void registerHover(String id, Function<Map<String, Dynamic<?>>, Object> factory) {
        ResourceLocation rid = TipAnimations.normalizeId(id);
        TipAnimations.registerHover0(rid, id, factory);
    }

    @Info(value="Register a custom Tip hover animation type with parameter defaults (tooling-only). paramDefaults is a map of key -> default value.")
    public static void registerHover(String id, Map<?, ?> paramDefaults, Function<Map<String, Dynamic<?>>, Object> factory) {
        ResourceLocation rid = TipAnimations.normalizeId(id);
        TipAnimations.registerHover0(rid, id, factory);
        AnimationType.declareHoverParamsInternal(rid, TipAnimations.schemaFrom(paramDefaults));
    }

    private static void register0(ResourceLocation rid, String id, Function<Map<String, Dynamic<?>>, Object> factory) {
        AnimationType.registerInternal(rid, params -> {
            Object obj = factory.apply(params);
            if (obj instanceof TransitionAnimation) {
                TransitionAnimation ta = (TransitionAnimation)obj;
                return ta;
            }
            if (obj instanceof Scriptable) {
                Scriptable s = (Scriptable)obj;
                return new JsTransitionAnimation(s);
            }
            throw new IllegalStateException("KJS transition animation must return an object: " + id);
        });
    }

    private static void registerHover0(ResourceLocation rid, String id, Function<Map<String, Dynamic<?>>, Object> factory) {
        AnimationType.registerHoverInternal(rid, params -> {
            Object obj = factory.apply(params);
            if (obj instanceof HoverAnimation) {
                HoverAnimation ha = (HoverAnimation)obj;
                return ha;
            }
            if (obj instanceof Scriptable) {
                Scriptable s = (Scriptable)obj;
                return new JsHoverAnimation(s);
            }
            throw new IllegalStateException("KJS hover animation must return an object: " + id);
        });
    }

    private static Map<String, SerializationUtil.CapturedParam> schemaFrom(Map<?, ?> paramDefaults) {
        if (paramDefaults == null || paramDefaults.isEmpty()) {
            return Map.of();
        }
        HashMap<String, SerializationUtil.CapturedParam> schema = new HashMap<String, SerializationUtil.CapturedParam>();
        for (Map.Entry<?, ?> entry : paramDefaults.entrySet()) {
            String key;
            Object k = entry.getKey();
            if (k == null || (key = String.valueOf(k)).isEmpty()) continue;
            Object v = entry.getValue();
            if (v instanceof Number) {
                Number n = (Number)v;
                schema.put(key, new SerializationUtil.CapturedParam("number", n));
                continue;
            }
            if (v instanceof Boolean) {
                Boolean b = (Boolean)v;
                schema.put(key, new SerializationUtil.CapturedParam("boolean", b));
                continue;
            }
            if (v != null) {
                schema.put(key, new SerializationUtil.CapturedParam("string", String.valueOf(v)));
                continue;
            }
            schema.put(key, new SerializationUtil.CapturedParam("string", ""));
        }
        return schema.isEmpty() ? Map.of() : Map.copyOf(schema);
    }

    private static ResourceLocation normalizeId(String id) {
        if (id == null || id.isEmpty()) {
            return new ResourceLocation("kubejs", "animation");
        }
        if (id.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", id);
        }
        return new ResourceLocation(id);
    }
}

