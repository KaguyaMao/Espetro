package org.espetro.client.aui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

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

    /** 打开淡入：从黑幕到清晰（0.5s = 10 ticks）。 */
    private static final int FADE_IN_TICKS = 10;
    private static final int FADE_OUT_TICKS = 10;
    private int fadeInTicksLeft;
    private int fadeOutTicksLeft;
    private Runnable fadeOutAction;
    /** 淡出进行中（阻止投票类页面的 onClose 重开自身把淡出后的切屏顶掉）。 */
    private boolean fadeOutClosing;

    /** 滑入/滑出（职业部署菜单）：0.2 秒 = 12 ticks，整个菜单层上下平移。 */
    private static final int SLIDE_TICKS = 12;
    private int slideInTicksLeft;
    private int slideOutTicksLeft;
    private Runnable slideOutAction;
    private boolean slideFromBottom;

    protected AuiScreen(Component title) {
        super(title);
        fadeInTicksLeft = FADE_IN_TICKS;
    }

    /** 当前是否处于关闭淡出中（外部切屏前先调 {@link #startFadeOut} 等待完成）。 */
    public final boolean isFadingOut() {
        return fadeOutTicksLeft > 0;
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
        if (fadeOutTicksLeft <= 0) {
            fadeOutTicksLeft = FADE_OUT_TICKS;
            fadeOutClosing = true;
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
        fadeOutTicksLeft = 0;
        fadeOutClosing = false;
        // 屏已被外部切换时忽略残留 action（例如阶段同步包直接 setScreen）。
        if (action != null && net.minecraft.client.Minecraft.getInstance().screen == this) {
            action.run();
        }
    }

    /** 立即结束淡入（例如重复打开同一界面时避免二次黑幕闪烁）。 */
    public final void skipFadeIn() {
        fadeInTicksLeft = 0;
    }

    /** 由外部驱动的统一切屏：若当前屏允许淡出则等待，否则直接切。 */
    public static void openWithFade(Screen next, Screen current) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (current instanceof AuiScreen aui) {
            if (aui.isFadingOut()) {
                // 已在淡出中：丢弃旧关闭动作，替换为切换到 next（避免连续 setScreen 闪屏）。
                aui.fadeOutAction = () -> {
                    mc.setScreen(next);
                    if (next instanceof AuiScreen n) n.fadeInTicksLeft = FADE_IN_TICKS;
                };
            } else {
                aui.startFadeOut(() -> {
                    mc.setScreen(next);
                    if (next instanceof AuiScreen n) n.fadeInTicksLeft = FADE_IN_TICKS;
                });
            }
            return;
        }
        mc.setScreen(next);
        if (next instanceof AuiScreen n) n.fadeInTicksLeft = FADE_IN_TICKS;
    }

    /** 切到「从下方滑入/滑出」模式（职业部署菜单用）。 */
    public final void useSlideFromBottom() {
        this.slideFromBottom = true;
        this.slideInTicksLeft = SLIDE_TICKS;
        this.fadeInTicksLeft = 0;
    }

    /** 是否处于滑动模式。 */
    public final boolean isSlideMode() {
        return slideFromBottom;
    }

    /** 滑出：动画结束后执行 action。 */
    public final void startSlideOut(Runnable action) {
        slideFromBottom = true;
        if (slideOutTicksLeft <= 0) {
            slideOutTicksLeft = SLIDE_TICKS;
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
        slideOutTicksLeft = 0;
        if (action != null && net.minecraft.client.Minecraft.getInstance().screen == this) {
            action.run();
        }
    }

    /**
     * 菜单层当前应平移的像素（正数=向下）。
     * 入场：+高度 → 0（自下方滑入）；出场：0 → +高度（向下滑出）。
     * 用 partialTick 做子帧插值，避免 20Hz 的顿感。
     */
    public final double currentSlideOffset(float partialTick) {
        if (!slideFromBottom) return 0.0D;
        double span = this.height + 12.0D;
        if (slideInTicksLeft > 0) {
            double t = Math.min(1.0D, (SLIDE_TICKS - slideInTicksLeft + partialTick) / SLIDE_TICKS);
            return span * (1.0D - easeOutCubic(t));
        }
        if (slideOutTicksLeft > 0) {
            double t = Math.min(1.0D, (SLIDE_TICKS - slideOutTicksLeft + partialTick) / SLIDE_TICKS);
            return span * easeOutCubic(t);
        }
        return 0.0D;
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

    /** 淡出并关闭（screen = null）。 */
    public static void closeWithFade(Screen current) {
        net.minecraft.client.Minecraft mc = net.minecraft.client.Minecraft.getInstance();
        if (current instanceof AuiScreen aui) {
            aui.startFadeOut(() -> mc.setScreen(null));
        } else {
            mc.setScreen(null);
        }
    }

    /** 当前 fade 黑幕 alpha（1=全黑，0=透明）。 */
    public float currentFadeAlpha() {
        if (slideFromBottom) return 0f;
        if (fadeInTicksLeft > 0) {
            return Math.max(0f, fadeInTicksLeft / (float) FADE_IN_TICKS);
        }
        if (fadeOutTicksLeft > 0) {
            return 1f - fadeOutTicksLeft / (float) FADE_OUT_TICKS;
        }
        return 0f;
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
        rebuildMenuRootNow();
    }

    private void rebuildMenuRootNow() {
        if (rebuildingRoot) {
            return;
        }
        rebuildingRoot = true;
        try {
            GuiElement next = new GuiElement(0, 0, this.width, this.height);
            buildMenuRoot(next);
            this.root = next;
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
        if (slideInTicksLeft > 0) slideInTicksLeft--;
        if (slideOutTicksLeft > 0) {
            slideOutTicksLeft--;
            if (slideOutTicksLeft <= 0) completeSlideOut();
        }
        if (fadeInTicksLeft > 0) {
            fadeInTicksLeft--;
        } else if (fadeOutTicksLeft > 0) {
            fadeOutTicksLeft--;
            if (fadeOutTicksLeft <= 0) {
                completeFadeOut();
            }
        }
    }

    protected abstract void buildMenuRoot(GuiElement root);

    protected void renderBeforeMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    protected void renderAfterMenu(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        graphics.flush();
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        graphics.pose().pushPose();
        try {
            renderBeforeMenu(graphics, mouseX, mouseY, partialTick);
            graphics.pose().pushPose();
            graphics.pose().translate(0.0D, currentSlideOffset(partialTick), 0.0D);
            try {
                if (root != null) {
                    root.updateFocusState(0, 0, mouseX, mouseY);
                    root.draw(graphics, 0, 0, this.width, this.height, mouseX, mouseY, partialTick);
                    var tooltip = root.getTooltipLines();
                    if (tooltip != null && !tooltip.isEmpty()) {
                        graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                    }
                }
                renderAfterMenu(graphics, mouseX, mouseY, partialTick);
            } finally {
                graphics.pose().popPose();
            }
            // 投票页淡入淡出黑幕层
            float alpha = currentFadeAlpha();
            if (alpha > 0.001f) {
                int argb = ((int) (alpha * 255f) << 24) & 0xFF000000;
                if (argb != 0) {
                    graphics.fill(0, 0, this.width, this.height, argb);
                }
            }
            graphics.flush();
        } finally {
            graphics.flush();
            graphics.pose().popPose();
            graphics.setColor(1f, 1f, 1f, 1f);
            RenderSystem.disableBlend();
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
        if (slideFromBottom && slideOutTicksLeft <= 0) {
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
