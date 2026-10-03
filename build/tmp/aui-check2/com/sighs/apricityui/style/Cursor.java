/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.client.Minecraft
 *  org.lwjgl.glfw.GLFW
 */
package com.sighs.apricityui.style;

import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.behavior.SelectionUnits;
import com.sighs.apricityui.behavior.TextSelection;
import com.sighs.apricityui.element.AbstractText;
import com.sighs.apricityui.element.TextArea;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.init.Element;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.loader.Loader;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.DocumentLayerOrder;
import com.sighs.apricityui.render.ImageDrawer;
import com.sighs.apricityui.resource.Image;
import com.sighs.apricityui.resource.async.image.ImageAsyncHandler;
import com.sighs.apricityui.resource.async.image.ImageHandle;
import com.sighs.apricityui.spi.AuiServices;
import com.sighs.apricityui.task.AbstractAsyncHandler;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class Cursor {
    private static final float PSEUDO_CURSOR_Z = 1000.0f;
    private static final Map<Integer, Long> STANDARD = new HashMap<Integer, Long>();
    private static boolean initialized = false;
    private static long currentHandle = 0L;
    private static boolean systemCursorHidden = false;
    private static CursorUrlSpec pseudoCursorSpec = null;

    public static void init() {
        if (initialized) {
            return;
        }
        initialized = true;
        Cursor.put(221185);
        Cursor.put(221186);
        Cursor.put(221187);
        Cursor.put(221188);
        Cursor.put(221189);
        Cursor.put(221190);
    }

    private static void put(int shape) {
        long handle = 0L;
        try {
            handle = GLFW.glfwCreateStandardCursor((int)shape);
        }
        catch (Throwable throwable) {
            // empty catch block
        }
        STANDARD.put(shape, handle);
    }

    public static void applyCssCursor(String cssValue) {
        Cursor.applyCssCursor(null, cssValue);
    }

    public static void applyCssCursor(String contextPath, String cssValue) {
        Cursor.init();
        CursorUrlSpec urlSpec = Cursor.parseUrlCursor(contextPath, cssValue);
        if (urlSpec != null) {
            ImageHandle handle = ImageAsyncHandler.INSTANCE.request(urlSpec.path());
            if (handle != null && handle.state() == AbstractAsyncHandler.AsyncState.READY && handle.texture() != null) {
                Cursor.enablePseudoCursor(urlSpec);
            } else {
                Cursor.disablePseudoCursor();
                Cursor.setWindowCursor(STANDARD.getOrDefault(221185, 0L));
            }
            return;
        }
        Cursor.disablePseudoCursor();
        int shape = Cursor.mapCssToStandardCursor(cssValue);
        long handle = STANDARD.getOrDefault(shape, STANDARD.getOrDefault(221185, 0L));
        if (handle == 0L) {
            return;
        }
        Cursor.setWindowCursor(handle);
    }

    public static void resetToDefault() {
        Cursor.applyCssCursor("default");
    }

    public static void refreshFromDocuments() {
        Cursor.refreshFromDocuments(AuiServices.client().getMousePositionDirectly());
    }

    public static void refreshFromDocuments(Position mousePosition) {
        if (mousePosition == null) {
            Cursor.resetToDefault();
            return;
        }
        List<Document> documents = DocumentLayerOrder.frontToBack(Document.getAll());
        for (Document document : documents) {
            Element target;
            if (document.inWorld || document.isManuallyRendered() || (target = document.hitTest(document.screenToDocumentPosition(mousePosition))) == null || target == document.body) continue;
            Cursor.applyCssCursor(document.getPath(), Cursor.resolveCssCursor(target, document.screenToDocumentPosition(mousePosition)));
            return;
        }
        Cursor.resetToDefault();
    }

    private static String resolveCssCursor(Element target, Position mousePosition) {
        TextArea textArea;
        if (target instanceof TextArea && (textArea = (TextArea)target).isResizeHandleAt(mousePosition)) {
            return textArea.getResizeCursor();
        }
        String cached = target.getRenderer().cursor.get();
        if (cached != null) {
            return cached;
        }
        Element element = target;
        while (element != null) {
            String value = element.getComputedStyle().cursor;
            if (!(value == null || (value = value.trim()).isEmpty() || value.equalsIgnoreCase("unset") || value.equalsIgnoreCase("auto"))) {
                target.getRenderer().cursor.set(value);
                return value;
            }
            element = element.parentElement;
        }
        String fallback = Cursor.isTextCursorTarget(target, mousePosition) ? "text" : "default";
        target.getRenderer().cursor.set(fallback);
        return fallback;
    }

    private static boolean isTextCursorTarget(Element target, Position mousePosition) {
        String tag;
        if (target == null || mousePosition == null) {
            return false;
        }
        if (target instanceof AbstractText) {
            return true;
        }
        String string = tag = target.tagName == null ? "" : target.tagName.trim().toUpperCase(Locale.ROOT);
        if ("BUTTON".equals(tag) || "SELECT".equals(tag) || "OPTION".equals(tag)) {
            return false;
        }
        Element unit = SelectionUnits.resolveUnit(target);
        return unit != null && TextSelection.isPositionOverSelectableText(unit, mousePosition.x, mousePosition.y);
    }

    public static void drawPseudoCursor(PoseStack poseStack) {
        if (poseStack == null || pseudoCursorSpec == null) {
            return;
        }
        ImageHandle handle = ImageAsyncHandler.INSTANCE.request(pseudoCursorSpec.path());
        if (handle == null || handle.state() != AbstractAsyncHandler.AsyncState.READY) {
            return;
        }
        Image.ITexture texture = handle.texture();
        if (texture == null || texture.getKey() == null) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.m_91268_() == null) {
            return;
        }
        double guiScale = mc.m_91268_().m_85449_();
        if (guiScale <= 0.0) {
            guiScale = 1.0;
        }
        float width = (float)((double)texture.getWidth() / guiScale);
        float height = (float)((double)texture.getHeight() / guiScale);
        if (width <= 0.0f || height <= 0.0f) {
            return;
        }
        int hotspotX = pseudoCursorSpec.hotspotX() >= 0 ? pseudoCursorSpec.hotspotX() : texture.getHotspotX();
        int hotspotY = pseudoCursorSpec.hotspotY() >= 0 ? pseudoCursorSpec.hotspotY() : texture.getHotspotY();
        float drawHotspotX = (float)((double)hotspotX / guiScale);
        float drawHotspotY = (float)((double)hotspotY / guiScale);
        Position mouse = AuiServices.client().getMousePositionDirectly();
        if (mouse == null) {
            mouse = AuiServices.client().getMousePosition();
        }
        float drawX = (float)mouse.x - drawHotspotX;
        float drawY = (float)mouse.y - drawHotspotY;
        poseStack.m_85836_();
        Base.commitDraws();
        Base.resolveOffset(poseStack);
        poseStack.m_85837_(0.0, 0.0, 1000.0);
        ImageDrawer.drawOverlay(poseStack, AuiServices.resources().locationOf(texture.getKey()), drawX, drawY, width, height, false);
        ImageDrawer.flushBatch();
        poseStack.m_85849_();
    }

    private static void enablePseudoCursor(CursorUrlSpec spec) {
        pseudoCursorSpec = spec;
        Cursor.setSystemCursorHidden(true);
    }

    private static void disablePseudoCursor() {
        pseudoCursorSpec = null;
        Cursor.setSystemCursorHidden(false);
    }

    private static void setSystemCursorHidden(boolean hidden) {
        if (systemCursorHidden == hidden) {
            return;
        }
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.m_91268_() == null) {
            return;
        }
        long window = AuiServices.client().getWindowHandle();
        if (window == 0L) {
            return;
        }
        GLFW.glfwSetInputMode((long)window, (int)208897, (int)(hidden ? 212994 : 212993));
        systemCursorHidden = hidden;
    }

    private static void setWindowCursor(long handle) {
        if (handle == 0L || handle == currentHandle) {
            return;
        }
        currentHandle = handle;
        Minecraft mc = Minecraft.m_91087_();
        if (mc == null || mc.m_91268_() == null) {
            return;
        }
        long window = AuiServices.client().getWindowHandle();
        if (window == 0L) {
            return;
        }
        GLFW.glfwSetCursor((long)window, (long)handle);
    }

    private static int mapCssToStandardCursor(String v) {
        if (v == null) {
            return 221185;
        }
        return switch (v = v.trim().toLowerCase(Locale.ROOT)) {
            case "auto", "default" -> 221185;
            case "pointer" -> 221188;
            case "text" -> 221186;
            case "crosshair" -> 221187;
            case "ew-resize" -> 221189;
            case "ns-resize" -> 221190;
            case "se-resize", "nwse-resize" -> 221191;
            default -> 221185;
        };
    }

    private static CursorUrlSpec parseUrlCursor(String contextPath, String cssValue) {
        String[] parts;
        if (cssValue == null) {
            return null;
        }
        String v = cssValue.trim();
        if (v.isEmpty()) {
            return null;
        }
        int start = v.toLowerCase(Locale.ROOT).indexOf("url(");
        if (start < 0) {
            return null;
        }
        int end = v.indexOf(41, start + 4);
        if (end < 0) {
            return null;
        }
        String raw = v.substring(start + 4, end).replace("\"", "").replace("'", "").trim();
        if (raw.isEmpty()) {
            return null;
        }
        String resolved = contextPath == null ? raw : Loader.resolve(contextPath, raw);
        int hotspotX = -1;
        int hotspotY = -1;
        String tail = v.substring(end + 1).trim();
        if (!tail.isEmpty() && (parts = tail.split("\\s+")).length >= 2) {
            try {
                hotspotX = Integer.parseInt(parts[0]);
                hotspotY = Integer.parseInt(parts[1]);
            }
            catch (NumberFormatException numberFormatException) {
                // empty catch block
            }
        }
        return new CursorUrlSpec(resolved, hotspotX, hotspotY);
    }

    private record CursorUrlSpec(String path, int hotspotX, int hotspotY) {
    }
}

