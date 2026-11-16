package org.espetro.client.aui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.espetro.client.gui.UiMotion;
import org.espetro.client.gui.UiTheme;
import org.espetro.client.gui.UiForegroundLayer;

import java.util.Objects;

/**
 * Vanilla {@link Screen} host for Espetro / EsPoints menus.
 * Clicks go through {@code mouseClicked} like the old MUtil screens.
 * Does not create an AUI Document — a full-screen intercept overlay
 * would swallow vanilla and in-game clicks.
 */
public abstract class AuiScreen extends Screen {
    public static final String HOST_PATH = "screens/host.html";

    protected GuiElement root;
    private boolean rootRebuildPending;
    private boolean rebuildingRoot;
    private Object structureSignature;
    private long lastThrottleEpochSec = -1;
    private long themeRevision = -1;

    /** Real-time foreground opacity; the photo/world background is never faded to black. */
    private final UiMotion.Value foregroundOpacity = new UiMotion.Value(0f);
    private boolean fadeStarted;
    private Runnable fadeOutAction;
    /** 淡出进行中（阻止投票类页面的 onClose 重开自身把淡出后的切屏顶掉）。 */
    private boolean fadeOutClosing;

    /** 滑入/滑出（J 菜单）：0.2 秒，毫秒计时（与帧率无关）+ easeOutCubic 逐帧插值。 */
    private static final long SLIDE_MS = 200L;
    private long slideStartMs;
    private boolean slideOutPending;
    private Runnable slideOutAction;
    private boolean slideFromBottom;

    protected AuiScreen(Component title) {
        super(title);
    }

    /** 当前是否处于关闭淡出中（外部切屏前先调 {@link #startFadeOut} 等待完成）。 */
    public final boolean isFadingOut() {
        return fadeOutClosing;
    }

    /** 淡出进行中（onClose 应放行，不再重开自身）。 */
    public final boolean isFadeOutClosing() {
        return fadeOutClosing;
    }

    /**
     * 启动关闭淡出。淡出结束后执行 {@code action}（通常为 setScreen(下一个)）。
     * 已在淡出中则链式追加：前一个 action 执行后继续执行新的。
     */
    public final void startFadeOut(Runnable action) {
        if (!fadeOutClosing) {
            fadeOutClosing = true;
            foregroundOpacity.to(0f, UiTheme.MOTION_EXIT_MS);
            fadeOutAction = action;
        } else {
            // 追加：旧的完成后再执行新目标
            Runnable previous = fadeOutAction;
            fadeOutAction = () -> {
                if (previous != null) previous.run();
                if (action != null) action.run();
            };
        }
    }

    /** 淡出完成后真正执行切换（由 tick 驱动）。 */
    private void completeFadeOut() {
        Runnable action = fadeOutAction;
        fadeOutAction = null;
        fadeOutClosing = false;
        // 屏已被外部切换时忽略残留 action（例如阶段同步包直接 setScreen）。
        if (action != null && net.minecraft.client.Minecraft.getInstance().screen == this) {
            action.run();
        }
    }

    /** 立即结束前景淡入；重复打开同一界面时不重播动效。 */
    public final void skipFadeIn() {
        fadeStarted = true;
        foregroundOpacity.snap(1f);
    }

    private void beginFadeIn() {
        fadeStarted = true;
        fadeOutClosing = false;
        foregroundOpacity.snap(0f);
        foregroundOpacity.to(1f, UiTheme.MOTION_ENTER_MS);
    }

    private static void showFadingScreen(Screen next) {
        if (next instanceof AuiScreen aui) aui.beginFadeIn();
        net.minecraft.client.Minecraft.getInstance().setScreen(next);
    }

    /** 由外部驱动的统一切屏：若当前屏允许淡出则等待，否则直接切。 */
    public static void openWithFade(Screen next, Screen current) {
        if (current instanceof AuiScreen aui) {
            if (aui.isFadingOut()) {
                // 已在淡出中：丢弃旧关闭动作，替换为切换到 next（避免连续 setScreen 闪屏）。
                aui.fadeOutAction = () -> {
                    showFadingScreen(next);
                };
            } else {
                aui.startFadeOut(() -> {
                    showFadingScreen(next);
                });
            }
            return;
        }
        showFadingScreen(next);
    }

    /** 切到「从下方滑入/滑出」模式（职业部署菜单用）。 */
    public final void useSlideFromBottom() {
        this.slideFromBottom = true;
        this.slideOutPending = false;
        this.slideStartMs = System.currentTimeMillis();
        skipFadeIn();
    }

    /** 是否处于滑动模式。 */
    public final boolean isSlideMode() {
        return slideFromBottom;
    }

    /** 滑出：动画结束后执行 action。 */
    public final void startSlideOut(Runnable action) {
        slideFromBottom = true;
        if (!slideOutPending) {
            slideOutPending = true;
            slideStartMs = System.currentTimeMillis();
            slideOutAction = action;
        } else {
            Runnable previous = slideOutAction;
            slideOutAction = () -> {
                if (previous != null) previous.run();
                if (action != null) action.run();
            };
        }
    }

    private void completeSlideOut() {
        Runnable action = slideOutAction;
        slideOutAction = null;
        slideOutPending = false;
        slideStartMs = 0L;
        if (action != null && net.minecraft.client.Minecraft.getInstance().screen == this) {
            action.run();
        }
    }

    /**
     * 菜单层当前应平移的像素（正数=向下）。
     * 入场：+高度 → 0（自下方滑入）；出场：0 → +高度（向下滑出）。
     * 按真实时间逐帧取样，避免 20Hz 的顿感。
     */
    public final double currentSlideOffset() {
        if (!slideFromBottom) return 0.0D;
        double span = this.height + 12.0D;
        if (slideStartMs <= 0L) return slideOutPending ? span : 0.0D;
        double t = Math.min(1.0D, (System.currentTimeMillis() - slideStartMs) / (double) SLIDE_MS);
        double e = easeOutCubic(t);
        return slideOutPending ? span * e : span * (1.0D - e);
    }

    private static double easeOutCubic(double t) {
        double u = 1.0D - t;
        return 1.0D - u * u * u;
    }

    /** 从下方滑入并切屏（职业部署菜单的开关都走它）。 */
    public static void openWithSlideUp(Screen next, Screen current) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (current instanceof AuiScreen aui && current != next) {
            aui.startSlideOut(() -> {
                mc.setScreen(next);
                if (next instanceof AuiScreen n) n.useSlideFromBottom();
            });
            return;
        }
        mc.setScreen(next);
        if (next instanceof AuiScreen n) n.useSlideFromBottom();
    }

    /** 只让【下一个】界面从下方滑入，不改变当前界面的切换方式（投票页仍走淡入淡出）。 */
    public static void showWithSlideUp(Screen next) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        mc.setScreen(next);
        if (next instanceof AuiScreen n) n.useSlideFromBottom();
    }

    /** 淡出并关闭（screen = null）。 */
    public static void closeWithFade(Screen current) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (current instanceof AuiScreen aui) {
            aui.startFadeOut(() -> mc.setScreen(null));
        } else {
            mc.setScreen(null);
        }
    }

    /** Legacy API: amount the foreground is faded out; never draws a black overlay. */
    public float currentFadeAlpha() {
        if (slideFromBottom) return 0f;
        return 1f - foregroundOpacity.get();
    }

    public static void runWithDocument(Object ignored, Runnable action) {
        if (action != null) {
            action.run();
        }
    }

    protected boolean shadeWorld() {
        return true;
    }

    protected final boolean updateStructure(Object newSignature) {
        if (Objects.equals(structureSignature, newSignature)) {
            return false;
        }
        structureSignature = newSignature;
        rebuildMenuRoot();
        return true;
    }

    protected final Object getStructureSignature() {
        return structureSignature;
    }

    protected final boolean onceEverySecond() {
        long epochSec = System.currentTimeMillis() / 1000L;
        if (epochSec == lastThrottleEpochSec) {
            return false;
        }
        lastThrottleEpochSec = epochSec;
        return true;
    }

    protected final void rebuildMenuRoot() {
        rootRebuildPending = true;
    }

    @Override
    protected void init() {
        super.init();
        if (!fadeStarted && !slideFromBottom) beginFadeIn();
        rebuildMenuRootNow();
    }

    private void rebuildMenuRootNow() {
        if (rebuildingRoot) {
            return;
        }
        rebuildingRoot = true;
        try {
            if (root != null && themeRevision != org.espetro.client.gui.UiTheme.revision()) {
                onThemeChanged();
            }
            beforeMenuRebuild();
            GuiElement next = new GuiElement(0, 0, this.width, this.height);
            buildMenuRoot(next);
            this.root = next;
            themeRevision = org.espetro.client.gui.UiTheme.revision();
        } finally {
            rebuildingRoot = false;
        }
    }

    @Override
    public void tick() {
        super.tick();
        if (rootRebuildPending) {
            rootRebuildPending = false;
            rebuildMenuRootNow();
        }
        if (root != null) {
            root.updateAnimations();
        }
        if (slideOutPending && slideStartMs > 0L
                && System.currentTimeMillis() - slideStartMs >= SLIDE_MS) {
            completeSlideOut();
        }
        if (fadeOutClosing && foregroundOpacity.isSettled()) completeFadeOut();
    }

    protected abstract void buildMenuRoot(GuiElement root);

    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        // Chat returns to the same screen instance. Recreate color-bearing widgets
        // before the first frame, including menus held by tutorial/party previews.
        if (themeRevision != org.espetro.client.gui.UiTheme.revision()) {
            rebuildMenuRootNow();
            rootRebuildPending = false;
        }
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().pushPose();
        org.espetro.client.gui.FrostedBackdrop.beginScreen();
        try {
            if (shadeWorld()) {
                org.espetro.client.gui.FrostedBackdrop.draw(graphics, this.width, this.height);
            }
            // Glass stays behind the content; menu geometry retains its slide animation.
            graphics.pose().pushPose();
            graphics.pose().translate(0.0D, currentSlideOffset(), 0.0D);
            try {
                renderBeforeMenu(graphics, mouseX, mouseY, partialTick);
                UiForegroundLayer.draw(graphics, this.width, this.height,
                    1f - currentFadeAlpha(), () -> {
                    if (root != null) {
                        root.updateFocusState(0, 0, mouseX, mouseY);
                        root.draw(graphics, 0, 0, this.width, this.height, mouseX, mouseY, partialTick);
                        var tooltip = root.getTooltipLines();
                        if (tooltip != null && !tooltip.isEmpty()) {
                            graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                        }
                    }
                    renderAfterMenu(graphics, mouseX, mouseY, partialTick);
                });
            } finally {
                graphics.pose().popPose();
            }
            // 聊天栏渲染在本模组界面之上：界面是全屏不透明的，否则聊天消息会被完全遮住。
            if (minecraft != null && minecraft.gui != null) {
                minecraft.gui.getChat().render(graphics, minecraft.gui.getGuiTicks(), mouseX, mouseY);
            }
            graphics.flush();
        } finally {
            graphics.flush();
            graphics.pose().popPose();
            graphics.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
            org.espetro.client.gui.FrostedBackdrop.endScreen();
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (root != null) {
            root.updateFocusState(0, 0, (int) mouseX, (int) mouseY);
            if (root.onMouseClick((int) mouseX, (int) mouseY, button)) {
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    /** Refresh custom cached drawing data before rebuilding the element tree. */
    protected void onThemeChanged() {
    }

    protected void beforeMenuRebuild() {
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (root != null) {
            root.onMouseRelease((int) mouseX, (int) mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        if (root != null && root.onMouseScroll(mouseX, mouseY, delta)) {
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (root != null && root.onKeyPress(keyCode, scanCode, modifiers)) {
            return true;
        }
        // 原版聊天栏（T）与命令输入（/）在本模组的任何界面里都应能正常打开，
        // 这里显式转发，避免全屏界面把按键吞掉。
        net.minecraft.client.Minecraft minecraft = net.minecraft.client.Minecraft.getInstance();
        if (minecraft.options != null && minecraft.player != null
            && (modifiers & (org.lwjgl.glfw.GLFW.GLFW_MOD_CONTROL
                | org.lwjgl.glfw.GLFW.GLFW_MOD_ALT
                | org.lwjgl.glfw.GLFW.GLFW_MOD_SHIFT)) == 0) {
            if (keyCode == minecraft.options.keyChat.getKey().getValue()) {
                org.espetro.client.gui.ChatReturnController.rememberMenu(this);
                minecraft.setScreen(new net.minecraft.client.gui.screens.ChatScreen(""));
                return true;
            }
            if (keyCode == minecraft.options.keyCommand.getKey().getValue()) {
                org.espetro.client.gui.ChatReturnController.rememberMenu(this);
                minecraft.setScreen(new net.minecraft.client.gui.screens.ChatScreen("/"));
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (root != null && root.onKeyRelease(keyCode, scanCode, modifiers)) {
            return true;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char codePoint, int modifiers) {
        if (root != null && root.onCharType(codePoint, modifiers)) {
            return true;
        }
        return super.charTyped(codePoint, modifiers);
    }

    @Override
    public void onClose() {
        if (slideFromBottom && !slideOutPending) {
            net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
            if (mc.screen == this) {
                startSlideOut(() -> mc.setScreen(null));
                return;
            }
        }
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
