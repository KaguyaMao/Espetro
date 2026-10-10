/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.action;

import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Dynamic;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class ActionHandlers {
    private static final Map<ResourceLocation, Handler> HANDLERS = new ConcurrentHashMap<ResourceLocation, Handler>();
    private static final Map<ResourceLocation, Map<String, SerializationUtil.CapturedParam>> PARAM_SCHEMA = new ConcurrentHashMap<ResourceLocation, Map<String, SerializationUtil.CapturedParam>>();

    private ActionHandlers() {
    }

    public static void register(ResourceLocation type, Handler handler) {
        if (type == null || handler == null) {
            return;
        }
        if (HANDLERS.containsKey(type)) {
            throw new IllegalStateException("Duplicate action handler type: " + String.valueOf(type));
        }
        HANDLERS.put(type, handler);
    }

    public static void clear(ResourceLocation type) {
        if (type == null) {
            return;
        }
        HANDLERS.remove(type);
        PARAM_SCHEMA.remove(type);
    }

    public static void clearAll() {
        HANDLERS.clear();
        PARAM_SCHEMA.clear();
    }

    public static Set<ResourceLocation> listTypes() {
        return Set.copyOf(HANDLERS.keySet());
    }

    public static void declareParamsInternal(ResourceLocation type, Map<String, SerializationUtil.CapturedParam> params) {
        if (type == null || params == null || params.isEmpty()) {
            return;
        }
        PARAM_SCHEMA.put(type, Map.copyOf(params));
    }

    public static Map<String, SerializationUtil.CapturedParam> getDeclaredParams(ResourceLocation type) {
        if (type == null) {
            return Map.of();
        }
        Map<String, SerializationUtil.CapturedParam> schema = PARAM_SCHEMA.get(type);
        return schema == null ? Map.of() : schema;
    }

    public static void execute(ResourceLocation type, @Nullable Map<String, Dynamic<?>> params) {
        if (type == null) {
            return;
        }
        Handler handler = HANDLERS.get(type);
        if (handler == null) {
            return;
        }
        Map<String, Dynamic<?>> safeParams = params == null || params.isEmpty() ? Collections.emptyMap() : new HashMap(params);
        handler.execute(safeParams);
    }

    @FunctionalInterface
    public static interface Handler {
        public void execute(Map<String, Dynamic<?>> var1);
    }
}

