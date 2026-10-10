/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.world.entity.player.Player
 */
package com.example.espoints.integration;

import com.example.espoints.util.ModLogger;
import java.lang.reflect.Method;
import net.minecraft.world.entity.player.Player;

public final class OptionalPointsIntegration {
    private static final String API_CLASS = "com.hcrzb.hcrzbshop.api.PlayerPointsAPI";
    private static volatile Binding binding;
    private static boolean warnedUnavailable;

    private OptionalPointsIntegration() {
    }

    public static boolean add(Player player, int points, String reason) {
        Binding current = OptionalPointsIntegration.binding();
        if (!current.available()) {
            return false;
        }
        try {
            Object result = current.addWithReason() != null ? current.addWithReason().invoke(null, player, points, reason) : current.add().invoke(null, player, points);
            return Boolean.TRUE.equals(result);
        }
        catch (ReflectiveOperationException | RuntimeException error) {
            ModLogger.warn("\u79ef\u5206\u5546\u5e97 addPoints \u8c03\u7528\u5931\u8d25: " + error.getMessage());
            return false;
        }
    }

    public static boolean remove(Player player, int points) {
        Binding current = OptionalPointsIntegration.binding();
        if (!current.available()) {
            return false;
        }
        try {
            return Boolean.TRUE.equals(current.remove().invoke(null, player, points));
        }
        catch (ReflectiveOperationException | RuntimeException error) {
            ModLogger.warn("\u79ef\u5206\u5546\u5e97 removePoints \u8c03\u7528\u5931\u8d25: " + error.getMessage());
            return false;
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static Binding binding() {
        Binding current = binding;
        if (current != null) {
            return current;
        }
        Class<OptionalPointsIntegration> clazz = OptionalPointsIntegration.class;
        synchronized (OptionalPointsIntegration.class) {
            block12: {
                if (binding != null) {
                    // ** MonitorExit[var1_1] (shouldn't be in output)
                    return binding;
                }
                try {
                    Class<?> api = Class.forName(API_CLASS);
                    Method add = null;
                    Method addWithReason = null;
                    try {
                        addWithReason = api.getMethod("addPoints", Player.class, Integer.TYPE, String.class);
                    }
                    catch (NoSuchMethodException noSuchMethodException) {
                        // empty catch block
                    }
                    try {
                        add = api.getMethod("addPoints", Player.class, Integer.TYPE);
                    }
                    catch (NoSuchMethodException noSuchMethodException) {
                        // empty catch block
                    }
                    if (add == null && addWithReason == null) {
                        throw new NoSuchMethodException("addPoints");
                    }
                    Method remove = api.getMethod("removePoints", Player.class, Integer.TYPE);
                    binding = new Binding(add, addWithReason, remove);
                }
                catch (ClassNotFoundException | NoSuchMethodException error) {
                    binding = Binding.UNAVAILABLE;
                    if (warnedUnavailable) break block12;
                    warnedUnavailable = true;
                    ModLogger.warn("\u672a\u68c0\u6d4b\u5230\u517c\u5bb9\u7684\u53ef\u9009\u79ef\u5206\u5546\u5e97 API\uff1b\u672c\u6b21\u8fd0\u884c\u5c06\u8df3\u8fc7\u79ef\u5206\u5956\u52b1\u4e0e\u60e9\u7f5a");
                }
            }
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return binding;
        }
    }

    private record Binding(Method add, Method addWithReason, Method remove) {
        private static final Binding UNAVAILABLE = new Binding(null, null, null);

        private boolean available() {
            return (this.add != null || this.addWithReason != null) && this.remove != null;
        }
    }
}

