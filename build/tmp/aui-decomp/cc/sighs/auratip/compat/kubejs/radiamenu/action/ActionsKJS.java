/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.gson.JsonNull
 *  com.google.gson.JsonPrimitive
 *  com.mojang.serialization.Dynamic
 *  com.mojang.serialization.DynamicOps
 *  com.mojang.serialization.JsonOps
 *  dev.latvian.mods.kubejs.typings.Info
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.compat.kubejs.radiamenu.action;

import cc.sighs.auratip.api.action.ActionHandlers;
import cc.sighs.auratip.compat.kubejs.radiamenu.action.ActionScriptRegistry;
import cc.sighs.auratip.data.action.Action;
import cc.sighs.auratip.util.SerializationUtil;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import dev.latvian.mods.kubejs.typings.Info;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ActionsKJS {
    private static final ResourceLocation RUN_COMMAND = new ResourceLocation("auratip", "run_command");
    private static final ResourceLocation SIMULATE_KEY = new ResourceLocation("auratip", "simulate_key");

    @Info(value="Register a script-backed action handler. The type can then be used as a radial menu slot action to invoke the callback.")
    public static void register(String type, ActionScriptRegistry.ScriptHandler handler) {
        ActionScriptRegistry.register(type, handler);
    }

    @Info(value="Register a script-backed action handler with parameter defaults (tooling-only). paramDefaults is a map of key -> default value.")
    public static void register(String type, Map<?, ?> paramDefaults, ActionScriptRegistry.ScriptHandler handler) {
        ActionScriptRegistry.register(type, handler);
        ActionHandlers.declareParamsInternal(ActionsKJS.normalizeType(type), ActionsKJS.schemaFrom(paramDefaults));
    }

    @Info(value="Create an Action without params. Built-in run_command / simulate_key create the matching Action; other types create a script action.")
    public static Action of(String type) {
        if ("run_command".equals(type) || RUN_COMMAND.toString().equals(type)) {
            return new Action.RunCommand("");
        }
        if ("simulate_key".equals(type) || SIMULATE_KEY.toString().equals(type)) {
            return new Action.SimulateKey(0);
        }
        return new Action.ScriptAction(ActionsKJS.normalizeType(type), Map.of());
    }

    @Info(value="Create an Action with params. run_command uses params.command; simulate_key uses params.key_code; other types pass params as dynamic values.")
    public static Action of(String type, Map<?, ?> params) {
        if ("run_command".equals(type) || RUN_COMMAND.toString().equals(type)) {
            Object cmd = params == null ? null : params.get("command");
            return new Action.RunCommand(cmd == null ? "" : String.valueOf(cmd));
        }
        if ("simulate_key".equals(type) || SIMULATE_KEY.toString().equals(type)) {
            int keyCode;
            Object code;
            Object v0 = code = params == null ? null : params.get("key_code");
            if (code instanceof Number) {
                Number n = code;
                keyCode = n.intValue();
            } else {
                try {
                    keyCode = code == null ? 0 : Integer.parseInt(String.valueOf(code));
                }
                catch (NumberFormatException ignored) {
                    keyCode = 0;
                }
            }
            return new Action.SimulateKey(keyCode);
        }
        if (params == null || params.isEmpty()) {
            return new Action.ScriptAction(ActionsKJS.normalizeType(type), Map.of());
        }
        HashMap result = new HashMap();
        for (Map.Entry<?, ?> entry : params.entrySet()) {
            Object keyObj = entry.getKey();
            if (keyObj == null) continue;
            String key = String.valueOf(keyObj);
            Object valueObj = entry.getValue();
            if (valueObj == null) continue;
            result.put(key, ActionsKJS.wrap(valueObj));
        }
        return new Action.ScriptAction(ActionsKJS.normalizeType(type), result);
    }

    private static Dynamic<?> wrap(Object value) {
        JsonNull element;
        if (value == null) {
            element = JsonNull.INSTANCE;
        } else if (value instanceof Number) {
            Number number = (Number)value;
            element = new JsonPrimitive(number);
        } else if (value instanceof Boolean) {
            Boolean bool = (Boolean)value;
            element = new JsonPrimitive(bool);
        } else {
            element = new JsonPrimitive(String.valueOf(value));
        }
        return new Dynamic((DynamicOps)JsonOps.INSTANCE, (Object)element);
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

    private static ResourceLocation normalizeType(String type) {
        if (type == null || type.isEmpty()) {
            return new ResourceLocation("kubejs", "action");
        }
        if (type.indexOf(58) < 0) {
            return new ResourceLocation("kubejs", type);
        }
        return new ResourceLocation(type);
    }
}

