/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  dev.latvian.mods.kubejs.typings.Info
 *  net.minecraft.network.chat.Component
 */
package cc.sighs.auratip.compat.kubejs.tip;

import dev.latvian.mods.kubejs.typings.Info;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.network.chat.Component;

public class TipVariables {
    private static final Map<String, ComponentSupplier> SUPPLIERS = new ConcurrentHashMap<String, ComponentSupplier>();

    @Info(value="Register/update a Tip variable as a plain string. If value is null, the variable is cleared.")
    public static void register(String key, String value) {
        if (key == null || key.isEmpty()) {
            return;
        }
        if (value == null) {
            SUPPLIERS.remove(key);
        } else {
            SUPPLIERS.put(key, () -> Component.m_237113_((String)value));
        }
    }

    @Info(value="Register/update a Tip variable as a Component. If value is null, the variable is cleared.")
    public static void registerComponent(String key, Component value) {
        if (key == null || key.isEmpty()) {
            return;
        }
        if (value == null) {
            SUPPLIERS.remove(key);
        } else {
            SUPPLIERS.put(key, () -> value);
        }
    }

    @Info(value="Register a dynamic Tip variable (string). The supplier is called each time a Tip is triggered.")
    public static void registerDynamic(String key, StringSupplier supplier) {
        if (key == null || key.isEmpty() || supplier == null) {
            return;
        }
        SUPPLIERS.put(key, () -> {
            String value = supplier.get();
            if (value == null) {
                return null;
            }
            return Component.m_237113_((String)value);
        });
    }

    @Info(value="Register a dynamic Tip variable (Component). The supplier is called each time a Tip is triggered.")
    public static void registerDynamicComponent(String key, ComponentSupplier supplier) {
        if (key == null || key.isEmpty() || supplier == null) {
            return;
        }
        SUPPLIERS.put(key, supplier);
    }

    @Info(value="Clear the Tip variable for the given key.")
    public static void clear(String key) {
        SUPPLIERS.remove(key);
    }

    @Info(value="Internal: snapshot of current variables (passed to the server when triggering).")
    public static Map<String, Component> snapshot() {
        if (SUPPLIERS.isEmpty()) {
            return Collections.emptyMap();
        }
        HashMap<String, Component> result = new HashMap<String, Component>();
        for (Map.Entry<String, ComponentSupplier> entry : SUPPLIERS.entrySet()) {
            Component value = entry.getValue().get();
            if (value == null) continue;
            result.put(entry.getKey(), value);
        }
        return result;
    }

    @FunctionalInterface
    public static interface ComponentSupplier {
        public Component get();
    }

    @FunctionalInterface
    public static interface StringSupplier {
        public String get();
    }
}

