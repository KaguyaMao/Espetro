/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.Window
 *  com.mojang.blaze3d.vertex.PoseStack
 *  net.minecraft.ChatFormatting
 *  net.minecraft.SharedConstants
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.MouseHandler
 *  net.minecraft.client.gui.Font$DisplayMode
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.client.gui.screens.Screen
 *  net.minecraft.client.renderer.MultiBufferSource
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraft.network.chat.MutableComponent
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.InputEvent$Key
 *  net.minecraftforge.client.event.InputEvent$MouseButton$Pre
 *  net.minecraftforge.client.event.InputEvent$MouseScrollingEvent
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.client.event.ScreenEvent$CharacterTyped$Pre
 *  net.minecraftforge.client.event.ScreenEvent$KeyPressed$Pre
 *  net.minecraftforge.client.event.ScreenEvent$KeyReleased$Pre
 *  net.minecraftforge.client.event.ScreenEvent$MouseScrolled$Pre
 *  net.minecraftforge.client.event.ScreenEvent$Render$Post
 *  net.minecraftforge.client.event.ScreenEvent$Render$Pre
 *  net.minecraftforge.event.TickEvent$ClientTickEvent
 *  net.minecraftforge.event.TickEvent$Phase
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 *  org.lwjgl.glfw.GLFW
 */
package com.sighs.apricityui.client;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import com.sighs.apricityui.ApricityUI;
import com.sighs.apricityui.client.CursorReleaseController;
import com.sighs.apricityui.client.DebugAIScreenshotTicker;
import com.sighs.apricityui.client.DebugReloadWatcher;
import com.sighs.apricityui.client.MouseCoordinates;
import com.sighs.apricityui.config.ApricityUIConfig;
import com.sighs.apricityui.dev.DevTools;
import com.sighs.apricityui.dev.ResourceManager;
import com.sighs.apricityui.dev.debug.ExternalDebugServer;
import com.sighs.apricityui.dev.resource.ResourcePreviewDialog;
import com.sighs.apricityui.event.KeyEvent;
import com.sighs.apricityui.event.MouseEvent;
import com.sighs.apricityui.forge.RenderService;
import com.sighs.apricityui.init.Document;
import com.sighs.apricityui.layout.Position;
import com.sighs.apricityui.layout.Size;
import com.sighs.apricityui.render.Base;
import com.sighs.apricityui.render.DocumentLayerOrder;
import com.sighs.apricityui.render.FrameTimingHud;
import com.sighs.apricityui.render.Operation;
import com.sighs.apricityui.screen.ApricityContainerScreen;
import com.sighs.apricityui.screen.ApricityScreen;
import com.sighs.apricityui.style.Cursor;
import com.sighs.apricityui.style.Text;
import com.sighs.apricityui.task.FrameScheduler;
import com.sighs.apricityui.ui.Tooltip;
import com.sighs.apricityui.world.WorldWindow;
import java.awt.Canvas;
import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import net.minecraft.ChatFormatting;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid="apricityui", value={Dist.CLIENT})
public class Client {
    public static final HashMap<String, Integer> KEY_MAP = new HashMap();
    private static int lastWindowWidth = -1;
    private static int lastWindowHeight = -1;
    private static int lastFramebufferWidth = -1;
    private static int lastFramebufferHeight = -1;
    private static double lastGuiScale = -1.0;

    @SubscribeEvent
    public static void updateTooltipPosition(ScreenEvent.Render.Pre event) {
        Position mousePosition = new Position(event.getMouseX(), event.getMouseY());
        Tooltip.moveActiveFromScreen(mousePosition);
        DevTools.handleInspectMouseMove(mousePosition);
    }

    @SubscribeEvent
    public static void drawScreen(ScreenEvent.Render.Post event) {
        if (Minecraft.m_91087_().f_91080_ instanceof ApricityContainerScreen) {
            return;
        }
        if (Minecraft.m_91087_().f_91080_ instanceof ApricityScreen) {
            return;
        }
        if (Minecraft.m_91087_().f_91073_ == null || Minecraft.m_91087_().f_91080_ != null) {
            FrameTimingHud.beginFrame();
            try {
                Client.drawPersistentScreenDocuments(event.getGuiGraphics());
                event.getGuiGraphics().m_280262_();
                Cursor.drawPseudoCursor(event.getGuiGraphics().m_280168_());
                event.getGuiGraphics().m_280262_();
            }
            finally {
                FrameTimingHud.endFrame();
                Client.drawFrameTimingHud(event.getGuiGraphics());
            }
        }
    }

    @SubscribeEvent
    public static void drawOverlay(RenderGuiEvent.Post event) {
        if (Minecraft.m_91087_().f_91080_ == null) {
            if (Minecraft.m_91087_().f_91066_.f_92062_) {
                return;
            }
            DevTools.handleInspectMouseMove(Client.getMousePositionDirectly());
            FrameTimingHud.beginFrame();
            try {
                for (Document document : DocumentLayerOrder.backToFront(Document.getAll())) {
                    if (document == null || document.inWorld || document.isManuallyRendered()) continue;
                    Base.drawOverlayDocument(event.getGuiGraphics().m_280168_(), document);
                    ResourcePreviewDialog.draw(event.getGuiGraphics().m_280168_(), document);
                }
                event.getGuiGraphics().m_280262_();
                Cursor.drawPseudoCursor(event.getGuiGraphics().m_280168_());
                event.getGuiGraphics().m_280262_();
            }
            finally {
                FrameTimingHud.endFrame();
                Client.drawFrameTimingHud(event.getGuiGraphics());
            }
        }
    }

    public static void drawPersistentScreenDocuments(GuiGraphics guiGraphics) {
        Client.drawPersistentScreenDocuments(guiGraphics, null);
    }

    public static void drawPersistentScreenDocuments(GuiGraphics guiGraphics, Document excludedDocument) {
        for (Document document : DocumentLayerOrder.backToFront(Document.getAll())) {
            if (document == null || document == excludedDocument || document.inWorld || document.isManuallyRendered() || !document.isReloadPersistent()) continue;
            Base.drawOverlayDocument(guiGraphics.m_280168_(), document);
            ResourcePreviewDialog.draw(guiGraphics.m_280168_(), document);
        }
    }

    @SubscribeEvent
    public static void scroll(InputEvent.MouseScrollingEvent event) {
        if (Minecraft.m_91087_().f_91080_ != null) {
            return;
        }
        if (Client.handleViewportZoomAtMouse(event.getScrollDelta() > 0.0)) {
            event.setCanceled(true);
            return;
        }
        boolean nativeConsumed = Operation.scroll(event.getScrollDelta());
        for (WorldWindow window : new ArrayList<WorldWindow>(WorldWindow.windows)) {
            Position realPos = window.getRealPos();
            if (realPos == null) continue;
            MouseEvent mouseEvent = new MouseEvent("wheel", realPos);
            mouseEvent.scrollDelta = mouseEvent.deltaY = -event.getScrollDelta() * 50.0;
            mouseEvent.cancelable = true;
            MouseEvent.tiggerEvent(mouseEvent, window.document);
            nativeConsumed |= mouseEvent.isNativeConsumed();
        }
        if (nativeConsumed || CursorReleaseController.isActive()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void scroll(ScreenEvent.MouseScrolled.Pre event) {
        if (Client.handleViewportZoomAtMouse(event.getScrollDelta() > 0.0)) {
            event.setCanceled(true);
            return;
        }
        if (Operation.scroll(event.getScrollDelta())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onCharTyped(ScreenEvent.CharacterTyped.Pre event) {
        if (SharedConstants.m_136188_((char)event.getCodePoint()) && Operation.onCharTyped(event.getCodePoint())) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void mouseButton(InputEvent.MouseButton.Pre event) {
        boolean nativeConsumed = false;
        if (event.getAction() == 1) {
            nativeConsumed = Operation.onMouseDown(event.getButton());
        }
        if (event.getAction() == 0) {
            nativeConsumed = Operation.onMouseUp(event.getButton());
        }
        boolean devToolsInspectConsumed = Operation.wasDevToolsInspectConsumed();
        if (Minecraft.m_91087_().f_91080_ != null) {
            if (nativeConsumed) {
                event.setCanceled(true);
            }
            return;
        }
        if (devToolsInspectConsumed) {
            event.setCanceled(true);
            return;
        }
        for (WorldWindow window : new ArrayList<WorldWindow>(WorldWindow.windows)) {
            Position realPos = window.getRealPos();
            if (realPos == null) continue;
            MouseEvent mouseEvent = event.getAction() == 1 ? new MouseEvent("mousedown", realPos, event.getButton()) : new MouseEvent("mouseup", realPos, event.getButton());
            MouseEvent.tiggerEvent(mouseEvent, window.document);
            nativeConsumed |= mouseEvent.isNativeConsumed();
        }
        if (nativeConsumed || CursorReleaseController.isActive()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void mouseMove(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Operation.onMouseMove(Client.getMousePosition());
            for (WorldWindow window : new ArrayList<WorldWindow>(WorldWindow.windows)) {
                Position realPos = window.getRealPos();
                if (realPos == null) continue;
                MouseEvent moveEvent = new MouseEvent("mousemove", realPos);
                MouseEvent.tiggerEvent(moveEvent, window.document);
            }
        }
    }

    @SubscribeEvent
    public static void onKeyPressed(InputEvent.Key event) {
        if (Minecraft.m_91087_().f_91080_ != null) {
            return;
        }
        int action = event.getAction();
        if (action != 1 && action != 2 && action != 0) {
            return;
        }
        if (action == 1 && Client.handleViewportZoomKeyAtMouse(event.getKey(), event.getModifiers())) {
            return;
        }
        boolean canceled = Operation.handleKeyInput(event.getKey(), event.getScanCode(), action, event.getModifiers(), action == 2, KeyEvent.Source.INPUT_EVENT);
    }

    @SubscribeEvent
    public static void onScreenKeyPressed(ScreenEvent.KeyPressed.Pre event) {
        if (Client.handleViewportZoomKeyAtMouse(event.getKeyCode(), event.getModifiers())) {
            event.setCanceled(true);
            return;
        }
        int action = 1;
        boolean canceled = Operation.handleKeyInput(event.getKeyCode(), event.getScanCode(), action, event.getModifiers(), false, KeyEvent.Source.SCREEN_EVENT);
        if (canceled) {
            event.setCanceled(true);
        }
    }

    private static boolean handleViewportZoomAtMouse(boolean zoomIn) {
        if (!Client.isControlDown()) {
            return false;
        }
        Document target = Client.findViewportZoomTargetAtMouse();
        if (target == null) {
            return false;
        }
        ApricityUI.LOGGER.info("[AUI Viewport] wheel zoomIn={} target={}", (Object)zoomIn, (Object)target.getPath());
        return target.handleViewportZoom(zoomIn);
    }

    private static boolean handleViewportZoomKeyAtMouse(int keyCode, int modifiers) {
        boolean reset;
        if (!Client.isControlModifier(modifiers)) {
            return false;
        }
        boolean zoomIn = keyCode == 61 || keyCode == 334;
        boolean zoomOut = keyCode == 45 || keyCode == 333;
        boolean bl = reset = keyCode == 48 || keyCode == 320;
        if (!(zoomIn || zoomOut || reset)) {
            return false;
        }
        Document target = Client.findViewportZoomTargetAtMouse();
        if (target == null) {
            return false;
        }
        ApricityUI.LOGGER.info("[AUI Viewport] key zoomIn={} reset={} target={}", new Object[]{zoomIn, reset, target.getPath()});
        return reset ? target.resetViewportZoom() : target.handleViewportZoom(zoomIn);
    }

    private static Document findViewportZoomTargetAtMouse() {
        Position mouse = Operation.getMousePositionDirectly();
        if (mouse == null) {
            return null;
        }
        boolean passThrough = (Boolean)ApricityUIConfig.CLIENT.viewportZoomPassThrough.get();
        for (Document document : DocumentLayerOrder.frontToBack(Document.getAll())) {
            if (document == null || document.inWorld || document.isManuallyRendered() || !document.isActive() || document.hitTest(document.screenToDocumentPosition(mouse)) == null || passThrough && document.isReloadPersistent() && !document.interceptsMouseEvents()) continue;
            return document;
        }
        return null;
    }

    private static boolean isControlModifier(int modifiers) {
        return (modifiers & 2) != 0 || Client.isControlDown();
    }

    private static boolean isControlDown() {
        if (Screen.m_96637_()) {
            return true;
        }
        try {
            Window window = Minecraft.m_91087_().m_91268_();
            long handle = window == null ? 0L : window.m_85439_();
            return handle != 0L && (GLFW.glfwGetKey((long)handle, (int)341) == 1 || GLFW.glfwGetKey((long)handle, (int)345) == 1);
        }
        catch (Throwable ignored) {
            return false;
        }
    }

    @SubscribeEvent
    public static void onScreenKeyReleased(ScreenEvent.KeyReleased.Pre event) {
        int action = 0;
        Operation.handleKeyInput(event.getKeyCode(), event.getScanCode(), action, event.getModifiers(), false, KeyEvent.Source.SCREEN_EVENT);
    }

    @SubscribeEvent
    public static void tick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.START) {
            CursorReleaseController.tick();
            if (ApricityUIConfig.consumeClientReloadPending()) {
                ExternalDebugServer.reconcileConfiguration();
            }
            ExternalDebugServer.tick();
            FrameScheduler.tick();
            ResourceManager.reconcileConfiguredMode();
            DebugReloadWatcher.tick();
            DebugAIScreenshotTicker.tick();
            DevTools.drainLogs();
            RenderService.INSTANCE.reconcileFabulousChainStencil();
            Window mcWindow = Minecraft.m_91087_().m_91268_();
            int w = mcWindow.m_85443_();
            int h = mcWindow.m_85444_();
            int framebufferWidth = mcWindow.m_85441_();
            int framebufferHeight = mcWindow.m_85442_();
            double guiScale = mcWindow.m_85449_();
            if (lastWindowWidth != w || lastWindowHeight != h || lastFramebufferWidth != framebufferWidth || lastFramebufferHeight != framebufferHeight || Double.compare(lastGuiScale, guiScale) != 0) {
                lastWindowWidth = w;
                lastWindowHeight = h;
                lastFramebufferWidth = framebufferWidth;
                lastFramebufferHeight = framebufferHeight;
                lastGuiScale = guiScale;
                for (Document document : Document.getAll()) {
                    if (document == null || document.isDisposed()) continue;
                    document.applyViewport(true);
                }
                com.sighs.apricityui.init.Window.window.fireResizeEvent();
            }
        }
    }

    public static Position getMousePosition() {
        Minecraft mc = Minecraft.m_91087_();
        MouseHandler mouseHandler = mc.f_91067_;
        Window window = mc.m_91268_();
        return MouseCoordinates.toGui(mouseHandler.m_91589_(), mouseHandler.m_91594_(), window.m_85443_(), window.m_85444_(), window.m_85445_(), window.m_85446_());
    }

    public static Position getMousePositionForWorldInteraction() {
        Minecraft mc = Minecraft.m_91087_();
        Window window = mc.m_91268_();
        if (mc.f_91067_.m_91600_()) {
            return new Position((double)window.m_85445_() * 0.5, (double)window.m_85446_() * 0.5);
        }
        return Client.getMousePosition();
    }

    public static Position getMousePositionDirectly() {
        Window window = Minecraft.m_91087_().m_91268_();
        long handle = window.m_85439_();
        if (handle != 0L) {
            double[] xBuf = new double[1];
            double[] yBuf = new double[1];
            GLFW.glfwGetCursorPos((long)handle, (double[])xBuf, (double[])yBuf);
            return MouseCoordinates.toGui(xBuf[0], yBuf[0], window.m_85443_(), window.m_85444_(), window.m_85445_(), window.m_85446_());
        }
        return null;
    }

    public static boolean isKeyPressed(String keyName) {
        if (keyName == null || keyName.isEmpty()) {
            return false;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        long windowHandle = minecraft.m_91268_().m_85439_();
        if (windowHandle == 0L) {
            return false;
        }
        try {
            Integer glfwKey = KEY_MAP.get(keyName);
            if (glfwKey == null) {
                return false;
            }
            if (keyName.startsWith("key.mouse.")) {
                return GLFW.glfwGetMouseButton((long)windowHandle, (int)glfwKey) == 1;
            }
            if (keyName.startsWith("key.keyboard.")) {
                return GLFW.glfwGetKey((long)windowHandle, (int)glfwKey) == 1;
            }
            return false;
        }
        catch (Exception e) {
            return false;
        }
    }

    public static Window getWindow() {
        return Minecraft.m_91087_().m_91268_();
    }

    public static Size getWindowSize() {
        Window window = Minecraft.m_91087_().m_91268_();
        return new Size(window.m_85445_(), window.m_85446_());
    }

    public static int getDefaultFontWidth(String text) {
        return Client.getDefaultFontWidth(text, false, false, 0.0);
    }

    public static int getDefaultFontWidth(String text, boolean bold) {
        return Client.getDefaultFontWidth(text, bold, false, 0.0);
    }

    public static int getDefaultFontWidth(String text, boolean bold, boolean oblique) {
        return Client.getDefaultFontWidth(text, bold, oblique, 0.0);
    }

    public static int getDefaultFontWidth(String text, boolean bold, boolean oblique, double strokeWidth) {
        double stroke = Math.max(0.0, strokeWidth) * 2.0;
        Minecraft minecraft = Minecraft.m_91087_();
        if (minecraft == null || minecraft.f_91062_ == null) {
            int fontStyle = 0;
            if (bold) {
                fontStyle |= 1;
            }
            if (oblique) {
                fontStyle |= 2;
            }
            Font fallbackFont = new Font("Microsoft YaHei", fontStyle, 16);
            int width = new Canvas().getFontMetrics(fallbackFont).stringWidth(text == null ? "" : text);
            return (int)Math.ceil((double)width + stroke);
        }
        if (!bold && !oblique) {
            return (int)Math.ceil((double)minecraft.f_91062_.m_92895_(text) + stroke);
        }
        MutableComponent renderText = Component.m_237113_((String)text);
        if (bold) {
            renderText = renderText.m_130940_(ChatFormatting.BOLD);
        }
        if (oblique) {
            renderText = renderText.m_130940_(ChatFormatting.ITALIC);
        }
        return (int)Math.ceil((double)minecraft.f_91062_.m_92852_((FormattedText)renderText) + stroke);
    }

    public static void drawDefaultFont(PoseStack poseStack, Text text, String content, Position position) {
        int stroke;
        poseStack.m_85836_();
        poseStack.m_85837_(position.x, position.y, 0.0);
        float scale = (float)text.defaultFontScale();
        poseStack.m_85841_(scale, scale, 1.0f);
        MutableComponent renderText = Component.m_237113_((String)(content == null ? "" : content));
        if (text.isBold()) {
            renderText = renderText.m_130940_(ChatFormatting.BOLD);
        }
        if (text.isOblique()) {
            renderText = renderText.m_130940_(ChatFormatting.ITALIC);
        }
        if (text.isUnderlined()) {
            renderText = renderText.m_130940_(ChatFormatting.UNDERLINE);
        }
        if (text.isStrikethrough()) {
            renderText = renderText.m_130940_(ChatFormatting.STRIKETHROUGH);
        }
        if ((stroke = Math.max(0, (int)Math.ceil(text.strokeWidth))) > 0) {
            int strokeColor = text.strokeColor.getValue();
            for (int ox = -stroke; ox <= stroke; ++ox) {
                for (int oy = -stroke; oy <= stroke; ++oy) {
                    if (ox == 0 && oy == 0 || ox * ox + oy * oy > stroke * stroke) continue;
                    Minecraft.m_91087_().f_91062_.m_272191_(renderText.m_7532_(), (float)ox, (float)oy, strokeColor, false, poseStack.m_85850_().m_252922_(), (MultiBufferSource)Minecraft.m_91087_().m_91269_().m_110104_(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
                }
            }
        }
        Minecraft.m_91087_().f_91062_.m_272191_(renderText.m_7532_(), 0.0f, 0.0f, text.color.getValue(), false, poseStack.m_85850_().m_252922_(), (MultiBufferSource)Minecraft.m_91087_().m_91269_().m_110104_(), Font.DisplayMode.NORMAL, 0, 0xF000F0);
        poseStack.m_85849_();
    }

    public static void drawDefaultFont(PoseStack poseStack, Text text, Position position) {
        Client.drawDefaultFont(poseStack, text, text.content, position);
    }

    public static void drawFrameTimingHud(GuiGraphics guiGraphics) {
        if (guiGraphics == null || !FrameTimingHud.isEnabled()) {
            return;
        }
        String text = FrameTimingHud.frameStatsText();
        if (text == null) {
            return;
        }
        Minecraft minecraft = Minecraft.m_91087_();
        int width = minecraft.f_91062_.m_92895_(text) + 8;
        guiGraphics.m_280168_().m_85836_();
        guiGraphics.m_280168_().m_252880_(0.0f, 0.0f, 1000.0f);
        guiGraphics.m_280509_(2, 2, 2 + width, 16, -872415232);
        guiGraphics.m_280056_(minecraft.f_91062_, text, 6, 6, -16711834, false);
        guiGraphics.m_280168_().m_85849_();
    }

    static {
        KEY_MAP.put("key.keyboard.unknown", -1);
        KEY_MAP.put("key.mouse.left", 0);
        KEY_MAP.put("key.mouse.right", 1);
        KEY_MAP.put("key.mouse.middle", 2);
        KEY_MAP.put("key.mouse.4", 3);
        KEY_MAP.put("key.mouse.5", 4);
        KEY_MAP.put("key.mouse.6", 5);
        KEY_MAP.put("key.mouse.7", 6);
        KEY_MAP.put("key.mouse.8", 7);
        KEY_MAP.put("key.keyboard.0", 48);
        KEY_MAP.put("key.keyboard.1", 49);
        KEY_MAP.put("key.keyboard.2", 50);
        KEY_MAP.put("key.keyboard.3", 51);
        KEY_MAP.put("key.keyboard.4", 52);
        KEY_MAP.put("key.keyboard.5", 53);
        KEY_MAP.put("key.keyboard.6", 54);
        KEY_MAP.put("key.keyboard.7", 55);
        KEY_MAP.put("key.keyboard.8", 56);
        KEY_MAP.put("key.keyboard.9", 57);
        KEY_MAP.put("key.keyboard.a", 65);
        KEY_MAP.put("key.keyboard.b", 66);
        KEY_MAP.put("key.keyboard.c", 67);
        KEY_MAP.put("key.keyboard.d", 68);
        KEY_MAP.put("key.keyboard.e", 69);
        KEY_MAP.put("key.keyboard.f", 70);
        KEY_MAP.put("key.keyboard.g", 71);
        KEY_MAP.put("key.keyboard.h", 72);
        KEY_MAP.put("key.keyboard.i", 73);
        KEY_MAP.put("key.keyboard.j", 74);
        KEY_MAP.put("key.keyboard.k", 75);
        KEY_MAP.put("key.keyboard.l", 76);
        KEY_MAP.put("key.keyboard.m", 77);
        KEY_MAP.put("key.keyboard.n", 78);
        KEY_MAP.put("key.keyboard.o", 79);
        KEY_MAP.put("key.keyboard.p", 80);
        KEY_MAP.put("key.keyboard.q", 81);
        KEY_MAP.put("key.keyboard.r", 82);
        KEY_MAP.put("key.keyboard.s", 83);
        KEY_MAP.put("key.keyboard.t", 84);
        KEY_MAP.put("key.keyboard.u", 85);
        KEY_MAP.put("key.keyboard.v", 86);
        KEY_MAP.put("key.keyboard.w", 87);
        KEY_MAP.put("key.keyboard.x", 88);
        KEY_MAP.put("key.keyboard.y", 89);
        KEY_MAP.put("key.keyboard.z", 90);
        KEY_MAP.put("key.keyboard.f1", 290);
        KEY_MAP.put("key.keyboard.f2", 291);
        KEY_MAP.put("key.keyboard.f3", 292);
        KEY_MAP.put("key.keyboard.f4", 293);
        KEY_MAP.put("key.keyboard.f5", 294);
        KEY_MAP.put("key.keyboard.f6", 295);
        KEY_MAP.put("key.keyboard.f7", 296);
        KEY_MAP.put("key.keyboard.f8", 297);
        KEY_MAP.put("key.keyboard.f9", 298);
        KEY_MAP.put("key.keyboard.f10", 299);
        KEY_MAP.put("key.keyboard.f11", 300);
        KEY_MAP.put("key.keyboard.f12", 301);
        KEY_MAP.put("key.keyboard.f13", 302);
        KEY_MAP.put("key.keyboard.f14", 303);
        KEY_MAP.put("key.keyboard.f15", 304);
        KEY_MAP.put("key.keyboard.f16", 305);
        KEY_MAP.put("key.keyboard.f17", 306);
        KEY_MAP.put("key.keyboard.f18", 307);
        KEY_MAP.put("key.keyboard.f19", 308);
        KEY_MAP.put("key.keyboard.f20", 309);
        KEY_MAP.put("key.keyboard.f21", 310);
        KEY_MAP.put("key.keyboard.f22", 311);
        KEY_MAP.put("key.keyboard.f23", 312);
        KEY_MAP.put("key.keyboard.f24", 313);
        KEY_MAP.put("key.keyboard.f25", 314);
        KEY_MAP.put("key.keyboard.num.lock", 282);
        KEY_MAP.put("key.keyboard.keypad.0", 320);
        KEY_MAP.put("key.keyboard.keypad.1", 321);
        KEY_MAP.put("key.keyboard.keypad.2", 322);
        KEY_MAP.put("key.keyboard.keypad.3", 323);
        KEY_MAP.put("key.keyboard.keypad.4", 324);
        KEY_MAP.put("key.keyboard.keypad.5", 325);
        KEY_MAP.put("key.keyboard.keypad.6", 326);
        KEY_MAP.put("key.keyboard.keypad.7", 327);
        KEY_MAP.put("key.keyboard.keypad.8", 328);
        KEY_MAP.put("key.keyboard.keypad.9", 329);
        KEY_MAP.put("key.keyboard.keypad.add", 334);
        KEY_MAP.put("key.keyboard.keypad.decimal", 330);
        KEY_MAP.put("key.keyboard.keypad.enter", 335);
        KEY_MAP.put("key.keyboard.keypad.equal", 336);
        KEY_MAP.put("key.keyboard.keypad.multiply", 332);
        KEY_MAP.put("key.keyboard.keypad.divide", 331);
        KEY_MAP.put("key.keyboard.keypad.subtract", 333);
        KEY_MAP.put("key.keyboard.down", 264);
        KEY_MAP.put("key.keyboard.left", 263);
        KEY_MAP.put("key.keyboard.right", 262);
        KEY_MAP.put("key.keyboard.up", 265);
        KEY_MAP.put("key.keyboard.apostrophe", 39);
        KEY_MAP.put("key.keyboard.backslash", 92);
        KEY_MAP.put("key.keyboard.comma", 44);
        KEY_MAP.put("key.keyboard.equal", 61);
        KEY_MAP.put("key.keyboard.grave.accent", 96);
        KEY_MAP.put("key.keyboard.left.bracket", 91);
        KEY_MAP.put("key.keyboard.minus", 45);
        KEY_MAP.put("key.keyboard.period", 46);
        KEY_MAP.put("key.keyboard.right.bracket", 93);
        KEY_MAP.put("key.keyboard.semicolon", 59);
        KEY_MAP.put("key.keyboard.slash", 47);
        KEY_MAP.put("key.keyboard.space", 32);
        KEY_MAP.put("key.keyboard.tab", 258);
        KEY_MAP.put("key.keyboard.left.alt", 342);
        KEY_MAP.put("key.keyboard.left.control", 341);
        KEY_MAP.put("key.keyboard.left.shift", 340);
        KEY_MAP.put("key.keyboard.left.win", 343);
        KEY_MAP.put("key.keyboard.right.alt", 346);
        KEY_MAP.put("key.keyboard.right.control", 345);
        KEY_MAP.put("key.keyboard.right.shift", 344);
        KEY_MAP.put("key.keyboard.right.win", 347);
        KEY_MAP.put("key.keyboard.enter", 257);
        KEY_MAP.put("key.keyboard.escape", 256);
        KEY_MAP.put("key.keyboard.backspace", 259);
        KEY_MAP.put("key.keyboard.delete", 261);
        KEY_MAP.put("key.keyboard.end", 269);
        KEY_MAP.put("key.keyboard.home", 268);
        KEY_MAP.put("key.keyboard.insert", 260);
        KEY_MAP.put("key.keyboard.page.down", 267);
        KEY_MAP.put("key.keyboard.page.up", 266);
        KEY_MAP.put("key.keyboard.caps.lock", 280);
        KEY_MAP.put("key.keyboard.pause", 284);
        KEY_MAP.put("key.keyboard.scroll.lock", 281);
        KEY_MAP.put("key.keyboard.menu", 348);
        KEY_MAP.put("key.keyboard.print.screen", 283);
        KEY_MAP.put("key.keyboard.world.1", 161);
        KEY_MAP.put("key.keyboard.world.2", 162);
    }
}

