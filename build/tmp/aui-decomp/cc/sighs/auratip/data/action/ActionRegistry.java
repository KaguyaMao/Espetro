/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonElement
 *  com.google.gson.JsonObject
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.DataResult
 *  com.mojang.serialization.DataResult$PartialResult
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  com.mojang.serialization.codecs.UnboundedMapCodec
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.data.action;

import cc.sighs.auratip.data.action.Action;
import cc.sighs.auratip.util.SerializationUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.resources.ResourceLocation;

public final class ActionRegistry {
    private static final ResourceLocation RUN_COMMAND = new ResourceLocation("auratip", "run_command");
    private static final ResourceLocation SIMULATE_KEY = new ResourceLocation("auratip", "simulate_key");
    private static final ResourceLocation UNKNOWN = new ResourceLocation("auratip", "unknown");
    private static final Map<ResourceLocation, Codec<? extends Action>> CUSTOM_CODECS = new ConcurrentHashMap<ResourceLocation, Codec<? extends Action>>();
    private static final Map<Class<?>, ResourceLocation> CUSTOM_TYPE_BY_CLASS = new ConcurrentHashMap();

    private ActionRegistry() {
    }

    public static Codec<Action> codec() {
        UnboundedMapCodec mapCodec = Codec.unboundedMap((Codec)Codec.STRING, (Codec)Codec.PASSTHROUGH);
        return mapCodec.xmap(ActionRegistry::decode, ActionRegistry::encode);
    }

    public static synchronized <T extends Action> void registerCustomCodec(ResourceLocation type, Class<T> actionClass, Codec<T> codec) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(actionClass, "actionClass");
        Objects.requireNonNull(codec, "codec");
        if (RUN_COMMAND.equals((Object)type) || SIMULATE_KEY.equals((Object)type) || UNKNOWN.equals((Object)type)) {
            throw new IllegalStateException("Action type '" + String.valueOf(type) + "' is reserved for built-ins.");
        }
        if (CUSTOM_CODECS.containsKey(type)) {
            throw new IllegalStateException("Duplicate action codec type: " + String.valueOf(type));
        }
        if (CUSTOM_TYPE_BY_CLASS.containsKey(actionClass)) {
            throw new IllegalStateException("Action class already registered: " + actionClass.getName());
        }
        CUSTOM_CODECS.put(type, codec);
        CUSTOM_TYPE_BY_CLASS.put(actionClass, type);
    }

    public static synchronized void clearCustomCodec(ResourceLocation type) {
        if (type == null) {
            return;
        }
        Codec<? extends Action> removed = CUSTOM_CODECS.remove(type);
        if (removed == null) {
            return;
        }
        CUSTOM_TYPE_BY_CLASS.entrySet().removeIf(e -> type.equals(e.getValue()));
    }

    private static Action decode(Map<String, Dynamic<?>> raw) {
        if (raw == null || raw.isEmpty()) {
            return new Action.ScriptAction(UNKNOWN, Map.of());
        }
        Dynamic<?> typeDyn = raw.get("type");
        String type = typeDyn == null ? "" : typeDyn.asString("");
        ResourceLocation id = ResourceLocation.m_135820_((String)type);
        if (id == null) {
            throw new IllegalStateException("Invalid action type id: '" + type + "'. Action.type must be a ResourceLocation string like 'modid:path'.");
        }
        if (RUN_COMMAND.equals((Object)id)) {
            String command = raw.getOrDefault("command", SerializationUtil.dynamicOf("")).asString("");
            return new Action.RunCommand(command);
        }
        if (SIMULATE_KEY.equals((Object)id)) {
            int key = raw.getOrDefault("key_code", SerializationUtil.dynamicOf(0)).asInt(0);
            return new Action.SimulateKey(key);
        }
        Codec<? extends Action> customCodec = CUSTOM_CODECS.get(id);
        if (customCodec != null) {
            JsonObject json = ActionRegistry.toJsonObject(raw, false);
            DataResult parsed = customCodec.parse((DynamicOps)JsonOps.INSTANCE, (Object)json);
            return (Action)parsed.result().orElseThrow(() -> new IllegalStateException("Failed to parse action '" + String.valueOf(id) + "': " + parsed.error().map(DataResult.PartialResult::message).orElse("unknown error")));
        }
        HashMap params = new HashMap(raw);
        params.remove("type");
        return new Action.ScriptAction(id, params);
    }

    private static Map<String, Dynamic<?>> encode(Action action) {
        HashMap out = new HashMap();
        if (action instanceof Action.RunCommand) {
            Action.RunCommand rc = (Action.RunCommand)action;
            out.put("type", SerializationUtil.dynamicOf(RUN_COMMAND.toString()));
            out.put("command", SerializationUtil.dynamicOf(rc.command()));
            return out;
        }
        if (action instanceof Action.SimulateKey) {
            Action.SimulateKey sk = (Action.SimulateKey)action;
            out.put("type", SerializationUtil.dynamicOf(SIMULATE_KEY.toString()));
            out.put("key_code", SerializationUtil.dynamicOf(sk.keyCode()));
            return out;
        }
        if (action instanceof Action.ScriptAction) {
            Action.ScriptAction sa = (Action.ScriptAction)action;
            out.put("type", SerializationUtil.dynamicOf(sa.type().toString()));
            if (sa.params() != null && !sa.params().isEmpty()) {
                out.putAll(sa.params());
            }
            return out;
        }
        ResourceLocation type = CUSTOM_TYPE_BY_CLASS.get(action.getClass());
        if (type == null) {
            for (Map.Entry<Class<?>, ResourceLocation> entry : CUSTOM_TYPE_BY_CLASS.entrySet()) {
                if (!entry.getKey().isInstance(action)) continue;
                type = entry.getValue();
                break;
            }
        }
        if (type != null) {
            ResourceLocation finalType = type;
            Codec<? extends Action> codec = CUSTOM_CODECS.get(finalType);
            if (codec == null) {
                throw new IllegalStateException("Custom action type '" + String.valueOf(finalType) + "' has no codec registered.");
            }
            DataResult encodedResult = codec.encodeStart((DynamicOps)JsonOps.INSTANCE, (Object)action);
            JsonElement encoded = (JsonElement)encodedResult.result().orElseThrow(() -> new IllegalStateException("Failed to encode action '" + String.valueOf(finalType) + "': " + encodedResult.error().map(DataResult.PartialResult::message).orElse("unknown error")));
            if (!(encoded instanceof JsonObject)) {
                throw new IllegalStateException("Action codec for '" + String.valueOf(finalType) + "' must encode to a JSON object.");
            }
            JsonObject json = (JsonObject)encoded;
            out.put("type", SerializationUtil.dynamicOf(finalType.toString()));
            for (Map.Entry entry : json.entrySet()) {
                out.put((String)entry.getKey(), new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)((JsonElement)entry.getValue())));
            }
            return out;
        }
        out.put("type", SerializationUtil.dynamicOf(UNKNOWN.toString()));
        return out;
    }

    private static JsonObject toJsonObject(Map<String, Dynamic<?>> raw, boolean includeType) {
        JsonObject obj = new JsonObject();
        for (Map.Entry<String, Dynamic<?>> entry : raw.entrySet()) {
            Dynamic<?> dyn;
            String key = entry.getKey();
            if (key == null || key.isEmpty() || !includeType && "type".equals(key) || (dyn = entry.getValue()) == null) continue;
            JsonElement value = (JsonElement)dyn.convert((DynamicOps)JsonOps.INSTANCE).getValue();
            obj.add(key, value);
        }
        return obj;
    }
}

