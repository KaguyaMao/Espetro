/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.serialization.Dynamic
 *  net.minecraft.resources.ResourceLocation
 */
package cc.sighs.auratip.compat.kubejs.radiamenu.action;

import cc.sighs.auratip.api.action.ActionHandlers;
import com.mojang.serialization.Dynamic;
import java.util.Map;
import net.minecraft.resources.ResourceLocation;

public class ActionScriptRegistry {
    public static void register(String type, ScriptHandler handler) {
        ActionHandlers.register(ActionScriptRegistry.normalizeType(type), handler::execute);
    }

    public static void clear(String type) {
        ActionHandlers.clear(ActionScriptRegistry.normalizeType(type));
    }

    public static void clearAll() {
        ActionHandlers.clearAll();
    }

    public static void execute(String type, Map<String, Dynamic<?>> params) {
        ActionHandlers.execute(ActionScriptRegistry.normalizeType(type), params);
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

    @FunctionalInterface
    public static interface ScriptHandler {
        public void execute(Map<String, Dynamic<?>> var1);
    }
}

