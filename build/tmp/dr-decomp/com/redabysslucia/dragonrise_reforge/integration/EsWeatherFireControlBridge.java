/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.Entity
 *  net.minecraft.world.level.Level
 *  net.minecraftforge.fml.ModList
 */
package com.redabysslucia.dragonrise_reforge.integration;

import java.lang.reflect.Method;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraftforge.fml.ModList;

public final class EsWeatherFireControlBridge {
    private static final String MOD_ID = "esweather";
    private static final String API_CLASS = "org.esweather.api.EsWeatherAPI";
    private static final String API_METHOD = "isThundersnowAt";
    private static final boolean LOADED = ModList.get().isLoaded("esweather");
    private static Method isThundersnowAtLevel;
    private static boolean lookupAttempted;

    private EsWeatherFireControlBridge() {
    }

    public static boolean isDisrupted(Entity entity) {
        if (!LOADED || entity == null) {
            return false;
        }
        Method method = EsWeatherFireControlBridge.resolveLevelMethod();
        if (method == null) {
            return false;
        }
        try {
            Boolean b;
            Object result = method.invoke(null, entity.m_9236_(), entity.m_20185_(), entity.m_20189_());
            return result instanceof Boolean && (b = (Boolean)result) != false;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    public static boolean isDisrupted(Level level, double x, double z) {
        if (!LOADED || level == null) {
            return false;
        }
        Method method = EsWeatherFireControlBridge.resolveLevelMethod();
        if (method == null) {
            return false;
        }
        try {
            Boolean b;
            Object result = method.invoke(null, level, x, z);
            return result instanceof Boolean && (b = (Boolean)result) != false;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) {
            return false;
        }
    }

    private static Method resolveLevelMethod() {
        if (lookupAttempted) {
            return isThundersnowAtLevel;
        }
        lookupAttempted = true;
        try {
            Class<?> api = Class.forName(API_CLASS);
            isThundersnowAtLevel = api.getMethod(API_METHOD, Level.class, Double.TYPE, Double.TYPE);
        }
        catch (ReflectiveOperationException ignored) {
            isThundersnowAtLevel = null;
        }
        return isThundersnowAtLevel;
    }
}

