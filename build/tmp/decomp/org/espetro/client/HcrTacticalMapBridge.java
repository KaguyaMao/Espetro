/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraftforge.fml.ModList
 */
package org.espetro.client;

import java.lang.reflect.Method;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraftforge.fml.ModList;
import org.espetro.Espetro;

public final class HcrTacticalMapBridge {
    private static final String HUD_CLASS_NAME = "com.example.espoints.hud.TacticalMapHUD";
    private static Class<?> hudClass;
    private static Method getInstanceMethod;
    private static Method renderEmbeddedMapMethod;
    private static Method increaseRenderRangeMethod;
    private static Method decreaseRenderRangeMethod;
    private static Method setSelectedDeploymentPointMethod;
    private static Method clearSelectedDeploymentPointMethod;
    private static Object hudInstance;
    private static Boolean espointsLoaded;
    private static boolean unavailableLogged;

    private HcrTacticalMapBridge() {
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public static void renderEmbeddedMap(GuiGraphics graphics, int x, int y, int width, int height, float partialTick) {
        if (!HcrTacticalMapBridge.isAvailable()) {
            HcrTacticalMapBridge.renderFallback(graphics, x, y, width, height);
            return;
        }
        graphics.m_280588_(x, y, x + width, y + height);
        try {
            HcrTacticalMapBridge.renderEmbeddedMapMethod().invoke(HcrTacticalMapBridge.getHudInstance(), graphics, x, y, width, height, Float.valueOf(partialTick));
        }
        catch (Throwable e) {
            HcrTacticalMapBridge.renderFallback(graphics, x, y, width, height);
            HcrTacticalMapBridge.logUnavailable(e);
            return;
        }
        finally {
            graphics.m_280618_();
        }
    }

    public static void increaseRenderRange() {
        if (!HcrTacticalMapBridge.isAvailable()) {
            return;
        }
        HcrTacticalMapBridge.invokeRangeMethod(true);
    }

    public static void decreaseRenderRange() {
        if (!HcrTacticalMapBridge.isAvailable()) {
            return;
        }
        HcrTacticalMapBridge.invokeRangeMethod(false);
    }

    public static void setSelectedDeploymentPoint(double x, double z) {
        if (!HcrTacticalMapBridge.isAvailable()) {
            return;
        }
        try {
            HcrTacticalMapBridge.setSelectedDeploymentPointMethod().invoke(HcrTacticalMapBridge.getHudInstance(), x, z);
        }
        catch (Throwable e) {
            HcrTacticalMapBridge.logUnavailable(e);
        }
    }

    public static void clearSelectedDeploymentPoint() {
        if (!HcrTacticalMapBridge.isAvailable()) {
            return;
        }
        try {
            HcrTacticalMapBridge.clearSelectedDeploymentPointMethod().invoke(HcrTacticalMapBridge.getHudInstance(), new Object[0]);
        }
        catch (Throwable e) {
            HcrTacticalMapBridge.logUnavailable(e);
        }
    }

    private static void invokeRangeMethod(boolean increase) {
        try {
            Method method = increase ? HcrTacticalMapBridge.increaseRenderRangeMethod() : HcrTacticalMapBridge.decreaseRenderRangeMethod();
            method.invoke(HcrTacticalMapBridge.getHudInstance(), new Object[0]);
        }
        catch (Throwable e) {
            HcrTacticalMapBridge.logUnavailable(e);
        }
    }

    private static Object getHudInstance() throws ReflectiveOperationException {
        if (hudInstance == null) {
            hudInstance = HcrTacticalMapBridge.getInstanceMethod().invoke(null, new Object[0]);
        }
        return hudInstance;
    }

    private static Class<?> hudClass() throws ClassNotFoundException {
        if (hudClass == null) {
            hudClass = Class.forName(HUD_CLASS_NAME);
        }
        return hudClass;
    }

    private static boolean isAvailable() {
        if (espointsLoaded == null) {
            espointsLoaded = ModList.get().isLoaded("espoints");
        }
        return espointsLoaded;
    }

    private static Method getInstanceMethod() throws ReflectiveOperationException {
        if (getInstanceMethod == null) {
            getInstanceMethod = HcrTacticalMapBridge.hudClass().getMethod("getInstance", new Class[0]);
        }
        return getInstanceMethod;
    }

    private static Method renderEmbeddedMapMethod() throws ReflectiveOperationException {
        if (renderEmbeddedMapMethod == null) {
            renderEmbeddedMapMethod = HcrTacticalMapBridge.hudClass().getMethod("renderEmbeddedMap", GuiGraphics.class, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Float.TYPE);
        }
        return renderEmbeddedMapMethod;
    }

    private static Method increaseRenderRangeMethod() throws ReflectiveOperationException {
        if (increaseRenderRangeMethod == null) {
            increaseRenderRangeMethod = HcrTacticalMapBridge.hudClass().getMethod("increaseRenderRange", new Class[0]);
        }
        return increaseRenderRangeMethod;
    }

    private static Method decreaseRenderRangeMethod() throws ReflectiveOperationException {
        if (decreaseRenderRangeMethod == null) {
            decreaseRenderRangeMethod = HcrTacticalMapBridge.hudClass().getMethod("decreaseRenderRange", new Class[0]);
        }
        return decreaseRenderRangeMethod;
    }

    private static Method setSelectedDeploymentPointMethod() throws ReflectiveOperationException {
        if (setSelectedDeploymentPointMethod == null) {
            setSelectedDeploymentPointMethod = HcrTacticalMapBridge.hudClass().getMethod("setSelectedDeploymentPoint", Double.TYPE, Double.TYPE);
        }
        return setSelectedDeploymentPointMethod;
    }

    private static Method clearSelectedDeploymentPointMethod() throws ReflectiveOperationException {
        if (clearSelectedDeploymentPointMethod == null) {
            clearSelectedDeploymentPointMethod = HcrTacticalMapBridge.hudClass().getMethod("clearSelectedDeploymentPoint", new Class[0]);
        }
        return clearSelectedDeploymentPointMethod;
    }

    private static void renderFallback(GuiGraphics graphics, int x, int y, int width, int height) {
        graphics.m_280509_(x, y, x + width, y + height, -870309856);
        graphics.m_280637_(x, y, width, height, -16777216);
        String text = "\u00a77\u6218\u672f\u5730\u56fe\u4e0d\u53ef\u7528";
        int textWidth = Minecraft.m_91087_().f_91062_.m_92895_(text);
        graphics.m_280614_(Minecraft.m_91087_().f_91062_, Component.m_237113_(text), x + (width - textWidth) / 2, y + (height - 8) / 2, 0xAAAAAA, false);
    }

    private static void logUnavailable(Throwable e) {
        if (unavailableLogged) {
            return;
        }
        unavailableLogged = true;
        Espetro.LOGGER.warn("HCR AAD / ESPoints \u6218\u672f\u5730\u56fe\u6865\u63a5\u4e0d\u53ef\u7528\uff0c\u90e8\u7f72\u754c\u9762\u5c06\u663e\u793a\u5360\u4f4d\u5730\u56fe: {}", (Object)e.toString());
    }
}

