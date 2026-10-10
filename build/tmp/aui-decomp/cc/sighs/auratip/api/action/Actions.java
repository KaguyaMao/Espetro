/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Codec
 *  com.mojang.serialization.Dynamic
 *  javax.annotation.Nullable
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.api.action;

import cc.sighs.auratip.api.action.ActionHandlers;
import cc.sighs.auratip.api.util.Params;
import cc.sighs.auratip.data.action.Action;
import cc.sighs.auratip.data.action.ActionRegistry;
import cc.sighs.auratip.util.SerializationUtil;
import com.mojang.serialization.Codec;
import com.mojang.serialization.Dynamic;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public final class Actions {
    private static final Map<Class<?>, Consumer<?>> TYPED = new ConcurrentHashMap();

    private Actions() {
    }

    public static Action runCommand(String command) {
        return new Action.RunCommand(command == null ? "" : command);
    }

    public static Action simulateKey(int keyCode) {
        return new Action.SimulateKey(keyCode);
    }

    public static Action script(ResourceLocation type, @Nullable Map<String, ?> params) {
        if (type == null) {
            return new Action.ScriptAction(new ResourceLocation("auratip", "unknown"), Map.of());
        }
        if (params == null || params.isEmpty()) {
            return new Action.ScriptAction(type, Map.of());
        }
        HashMap<String, Object> safe = new HashMap<String, Object>();
        for (Map.Entry<String, ?> entry : params.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key == null || key.isEmpty() || value == null) continue;
            safe.put(key, value);
        }
        return new Action.ScriptAction(type, SerializationUtil.convertMapToDynamic(safe));
    }

    public static Action scriptRaw(ResourceLocation type, @Nullable Map<String, Dynamic<?>> params) {
        if (type == null) {
            return new Action.ScriptAction(new ResourceLocation("auratip", "unknown"), Map.of());
        }
        if (params == null || params.isEmpty()) {
            return new Action.ScriptAction(type, Map.of());
        }
        return new Action.ScriptAction(type, Map.copyOf(params));
    }

    public static void register(ResourceLocation type, ParamsHandler handler) {
        if (type == null || handler == null) {
            return;
        }
        ActionHandlers.register(type, (Map<String, Dynamic<?>> raw) -> handler.execute(new Params(raw)));
    }

    public static void registerRaw(ResourceLocation type, RawHandler handler) {
        if (type == null || handler == null) {
            return;
        }
        ActionHandlers.register(type, handler::execute);
    }

    public static void clear(ResourceLocation type) {
        if (type == null) {
            return;
        }
        ActionHandlers.clear(type);
    }

    public static void clearAll() {
        ActionHandlers.clearAll();
    }

    public static <T extends Action> void registerCodec(ResourceLocation type, Class<T> actionClass, Codec<T> codec) {
        ActionRegistry.registerCustomCodec(type, actionClass, codec);
    }

    public static void clearCodec(ResourceLocation type) {
        ActionRegistry.clearCustomCodec(type);
    }

    public static <T extends Action> void register(Class<T> actionClass, Consumer<T> executor) {
        if (actionClass == null || executor == null) {
            return;
        }
        TYPED.put(actionClass, executor);
    }

    public static void clear(Class<? extends Action> actionClass) {
        if (actionClass == null) {
            return;
        }
        TYPED.remove(actionClass);
    }

    public static void executeTyped(Action action) {
        if (action == null) {
            return;
        }
        Consumer<?> exact = TYPED.get(action.getClass());
        if (exact != null) {
            Consumer<?> typed = exact;
            typed.accept(action);
            return;
        }
        for (Map.Entry<Class<?>, Consumer<?>> entry : TYPED.entrySet()) {
            Class<?> type = entry.getKey();
            if (!type.isInstance(action)) continue;
            Consumer<?> typed = entry.getValue();
            typed.accept(action);
            return;
        }
    }

    @FunctionalInterface
    public static interface ParamsHandler {
        public void execute(Params var1);
    }

    @FunctionalInterface
    public static interface RawHandler {
        public void execute(Map<String, Dynamic<?>> var1);
    }
}

