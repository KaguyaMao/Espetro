/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.Gson
 *  com.google.gson.JsonArray
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonObject
 *  com.google.gson.JsonParser
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.latvian.mods.rhino.Context
 *  dev.latvian.mods.rhino.Function
 *  dev.latvian.mods.rhino.Scriptable
 *  dev.latvian.mods.rhino.ScriptableObject
 *  io.netty.channel.ChannelHandlerContext
 *  io.netty.channel.SimpleChannelInboundHandler
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.editor.net;

import cc.sighs.auratip.AuraTip;
import cc.sighs.auratip.compat.kubejs.tip.animation.JsHoverAnimation;
import cc.sighs.auratip.compat.kubejs.tip.animation.JsTransitionAnimation;
import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.data.TipData;
import cc.sighs.auratip.data.animation.AnimationType;
import cc.sighs.auratip.editor.net.EditorParamIntrospection;
import cc.sighs.auratip.editor.net.EditorWsHub;
import cc.sighs.auratip.editor.preview.EditorPreviewApplier;
import cc.sighs.auratip.editor.preview.EditorRadialPreviewApplier;
import cc.sighs.auratip.editor.schema.EditorCodecSchemas;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Function;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

final class EditorWsHandler
extends SimpleChannelInboundHandler<String> {
    private static final Gson GSON = new Gson();
    private static final ResourceLocation TEMP_TRANSITION_ID = new ResourceLocation("auratip", "editor_temp_transition");
    private static final ResourceLocation TEMP_HOVER_ID = new ResourceLocation("auratip", "editor_temp_hover");
    private final EditorWsHub hub;
    private volatile JsonElement lastTipJson;
    private volatile JsonElement lastRadialJson;
    private volatile String mode = "tip";

    EditorWsHandler(EditorWsHub hub) {
        this.hub = hub;
    }

    public void handlerAdded(ChannelHandlerContext ctx) {
        this.hub.add(ctx.channel());
        EditorWsHandler.sendInit(ctx);
    }

    public void handlerRemoved(ChannelHandlerContext ctx) {
        this.hub.remove(ctx.channel());
    }

    protected void channelRead0(ChannelHandlerContext ctx, String text) {
        String type;
        JsonObject root;
        try {
            JsonElement parsed = JsonParser.parseString((String)text);
            if (!parsed.isJsonObject()) {
                return;
            }
            root = parsed.getAsJsonObject();
        }
        catch (Exception e) {
            AuraTip.LOGGER.warn("Editor WS: invalid json: {}", (Object)text, (Object)e);
            return;
        }
        switch (type = root.has("type") ? root.get("type").getAsString() : "") {
            case "ping": {
                EditorWsHandler.send(ctx, EditorWsHandler.json("type", "pong"));
                break;
            }
            case "set_mode": {
                String next = root.has("mode") ? root.get("mode").getAsString() : "tip";
                String string = this.mode = next == null ? "tip" : next;
                if ("radial".equalsIgnoreCase(this.mode)) {
                    EditorPreviewApplier.closePreview();
                    if (this.lastRadialJson != null) {
                        EditorRadialPreviewApplier.applyMenuJson(this.lastRadialJson);
                        break;
                    }
                    EditorRadialPreviewApplier.applyDefaultPreview();
                    break;
                }
                EditorRadialPreviewApplier.closePreview();
                if (this.lastTipJson != null) {
                    EditorPreviewApplier.applyTipJson(this.lastTipJson);
                    break;
                }
                EditorPreviewApplier.applyDefaultPreview();
                break;
            }
            case "close_preview": {
                String which;
                String string = which = root.has("mode") ? root.get("mode").getAsString() : "";
                if ("radial".equalsIgnoreCase(which)) {
                    EditorRadialPreviewApplier.closePreview();
                    break;
                }
                if ("tip".equalsIgnoreCase(which)) {
                    EditorPreviewApplier.closePreview();
                    break;
                }
                EditorPreviewApplier.closePreview();
                EditorRadialPreviewApplier.closePreview();
                break;
            }
            case "tip_update": {
                JsonElement tip = root.get("tip");
                if (tip == null) break;
                this.lastTipJson = tip;
                if ("radial".equalsIgnoreCase(this.mode)) break;
                EditorPreviewApplier.applyTipJson(tip);
                break;
            }
            case "radial_update": {
                JsonElement menu = root.get("menu");
                if (menu == null) break;
                this.lastRadialJson = menu;
                if (!"radial".equalsIgnoreCase(this.mode)) break;
                EditorRadialPreviewApplier.applyMenuJson(menu);
                break;
            }
            case "animation_apply": {
                String kind = root.has("kind") ? root.get("kind").getAsString() : "transition";
                String idRaw = root.has("id") ? root.get("id").getAsString() : "";
                JsonNull params = root.has("params") ? root.get("params") : JsonNull.INSTANCE;
                JsonObject result = new JsonObject();
                result.addProperty("type", "animation_apply_result");
                try {
                    JsonElement base;
                    ResourceLocation id = EditorWsHandler.normalizeIdOrTemp(kind, idRaw);
                    JsonElement jsonElement = base = this.lastTipJson != null ? this.lastTipJson : EditorPreviewCodec.encodeTip(EditorPreviewApplier.defaultTip());
                    if ("hover".equalsIgnoreCase(kind)) {
                        EditorWsHandler.applyStyleOverrideToPreview(base, "hover_animation_style", id.toString(), "hover_animation_params", (JsonElement)params);
                    } else {
                        EditorWsHandler.applyStyleOverrideToPreview(base, "animation_style", id.toString(), "animation_params", (JsonElement)params);
                    }
                    result.addProperty("ok", Boolean.valueOf(true));
                    result.addProperty("id", id.toString());
                }
                catch (Throwable t) {
                    AuraTip.LOGGER.warn("Editor animation_apply failed", t);
                    result.addProperty("ok", Boolean.valueOf(false));
                    result.addProperty("error", String.valueOf(t.getMessage()));
                }
                EditorWsHandler.send(ctx, result);
                break;
            }
            case "animation_test": {
                String kind = root.has("kind") ? root.get("kind").getAsString() : "transition";
                String id = root.has("id") ? root.get("id").getAsString() : "";
                String js = root.has("js") ? root.get("js").getAsString() : "";
                JsonNull params = root.has("params") ? root.get("params") : JsonNull.INSTANCE;
                this.handleAnimationTest(ctx, kind, id, js, (JsonElement)params);
                break;
            }
        }
    }

    private static void sendInit(ChannelHandlerContext ctx) {
        JsonObject init = new JsonObject();
        init.addProperty("type", "init");
        JsonObject payload = new JsonObject();
        payload.add("defaultTip", EditorPreviewCodec.encodeTip(EditorPreviewApplier.defaultTip()));
        payload.add("defaultRadialMenu", EditorPreviewCodec.encodeRadial(EditorRadialPreviewApplier.defaultMenu()));
        payload.add("transitionAnimations", (JsonElement)EditorWsHandler.encodeAnimationIds(AnimationTypeIds.transition()));
        payload.add("hoverAnimations", (JsonElement)EditorWsHandler.encodeAnimationIds(AnimationTypeIds.hover()));
        payload.add("paramMeta", (JsonElement)EditorParamIntrospection.buildInitParamPayload());
        payload.add("actionTypes", (JsonElement)EditorParamIntrospection.listActionTypes());
        payload.add("schemas", (JsonElement)EditorCodecSchemas.buildAll());
        init.add("payload", (JsonElement)payload);
        EditorWsHandler.send(ctx, init);
    }

    private void handleAnimationTest(ChannelHandlerContext ctx, String kind, String idRaw, String js, JsonElement paramsJson) {
        JsonObject result = new JsonObject();
        result.addProperty("type", "animation_test_result");
        result.addProperty("kind", kind == null ? "" : kind);
        try {
            ResourceLocation id = EditorWsHandler.normalizeIdOrTemp(kind, idRaw);
            JsEval eval = EditorWsHandler.evalJs(js);
            if ("hover".equalsIgnoreCase(kind)) {
                AnimationType.registerOrReplaceHoverInternal(id, params -> new JsHoverAnimation(EditorWsHandler.resolveAnimationObject(eval, params)));
                result.addProperty("id", id.toString());
                EditorWsHandler.applyStyleOverrideToPreview(this.lastTipJson, "hover_animation_style", id.toString(), "hover_animation_params", paramsJson);
            } else {
                AnimationType.registerOrReplaceInternal(id, params -> new JsTransitionAnimation(EditorWsHandler.resolveAnimationObject(eval, params)));
                result.addProperty("id", id.toString());
                EditorWsHandler.applyStyleOverrideToPreview(this.lastTipJson, "animation_style", id.toString(), "animation_params", paramsJson);
            }
            result.addProperty("ok", Boolean.valueOf(true));
        }
        catch (Throwable t) {
            AuraTip.LOGGER.warn("Editor animation_test failed", t);
            result.addProperty("ok", Boolean.valueOf(false));
            result.addProperty("error", String.valueOf(t.getMessage()));
        }
        EditorWsHandler.send(ctx, result);
    }

    private static JsEval evalJs(String js) {
        if (js == null || js.isBlank()) {
            throw new IllegalStateException("Empty JS");
        }
        Context cx = Context.enter();
        ScriptableObject scope = cx.initStandardObjects();
        Object out = cx.evaluateString((Scriptable)scope, "(" + js + ")", "auratip_editor_anim", 1, null);
        return new JsEval((Scriptable)scope, out);
    }

    private static Scriptable resolveAnimationObject(JsEval eval, Map<String, ?> params) {
        Context cx = Context.enter();
        Object value = eval.value();
        if (value instanceof Function) {
            Function factory = (Function)value;
            return EditorWsHandler.callFactory(cx, eval.scope(), factory, eval.scope(), params);
        }
        if (value instanceof Scriptable) {
            Scriptable scriptable = (Scriptable)value;
            Object create = scriptable.get(cx, "create", scriptable);
            if (create instanceof Function) {
                Function f = (Function)create;
                return EditorWsHandler.callFactory(cx, eval.scope(), f, scriptable, params);
            }
            return scriptable;
        }
        throw new IllegalStateException("JS did not evaluate to an object or factory function");
    }

    private static Scriptable callFactory(Context cx, Scriptable scope, Function fn, Scriptable thisObj, Map<String, ?> params) {
        Object result = fn.call(cx, scope, thisObj, new Object[]{params});
        if (result instanceof Scriptable) {
            Scriptable s = (Scriptable)result;
            return s;
        }
        throw new IllegalStateException("Factory did not return an object");
    }

    private static ResourceLocation normalizeIdOrTemp(String kind, String raw) {
        ResourceLocation parsed;
        Object s;
        Object object = s = raw == null ? "" : raw.trim();
        if (((String)s).isEmpty()) {
            return "hover".equalsIgnoreCase(kind) ? TEMP_HOVER_ID : TEMP_TRANSITION_ID;
        }
        if (((String)s).indexOf(58) < 0) {
            s = "kubejs:" + (String)s;
        }
        if ((parsed = ResourceLocation.m_135820_((String)s)) == null) {
            throw new IllegalStateException("Invalid ResourceLocation: " + (String)s);
        }
        return parsed;
    }

    private static void applyStyleOverrideToPreview(JsonElement lastTipJson, String styleKey, String styleValue, String paramsKey, JsonElement paramsValue) {
        if (lastTipJson == null || !lastTipJson.isJsonObject()) {
            return;
        }
        JsonObject copy = lastTipJson.getAsJsonObject().deepCopy();
        JsonObject visual = copy.has("visual_settings") && copy.get("visual_settings").isJsonObject() ? copy.getAsJsonObject("visual_settings") : new JsonObject();
        visual.addProperty(styleKey, styleValue);
        if (paramsKey != null && paramsValue != null && paramsValue.isJsonObject()) {
            visual.add(paramsKey, (JsonElement)paramsValue.getAsJsonObject().deepCopy());
        }
        copy.add("visual_settings", (JsonElement)visual);
        EditorPreviewApplier.applyTipJson((JsonElement)copy);
    }

    private static JsonArray encodeAnimationIds(Set<ResourceLocation> ids) {
        JsonArray arr = new JsonArray();
        ids.stream().sorted(Comparator.comparing(ResourceLocation::toString)).forEach(id -> arr.add(id.toString()));
        return arr;
    }

    private static void send(ChannelHandlerContext ctx, JsonObject obj) {
        ctx.writeAndFlush((Object)GSON.toJson((JsonElement)obj));
    }

    private static JsonObject json(String k, String v) {
        JsonObject o = new JsonObject();
        o.addProperty(k, v);
        return o;
    }

    private static final class EditorPreviewCodec {
        private EditorPreviewCodec() {
        }

        static JsonElement encodeTip(TipData tip) {
            DataResult encoded = TipData.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)tip);
            return encoded.resultOrPartial(msg -> AuraTip.LOGGER.warn("Editor tip encode error: {}", msg)).orElseGet(() -> GSON.toJsonTree(Map.of()));
        }

        static JsonElement encodeRadial(RadialMenuData menu) {
            DataResult encoded = RadialMenuData.CODEC.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)menu);
            return encoded.resultOrPartial(msg -> AuraTip.LOGGER.warn("Editor radial encode error: {}", msg)).orElseGet(() -> GSON.toJsonTree(Map.of()));
        }
    }

    private static final class AnimationTypeIds {
        private AnimationTypeIds() {
        }

        static Set<ResourceLocation> transition() {
            return AnimationType.listTransitionIds();
        }

        static Set<ResourceLocation> hover() {
            return AnimationType.listHoverIds();
        }
    }

    private record JsEval(Scriptable scope, Object value) {
    }
}

