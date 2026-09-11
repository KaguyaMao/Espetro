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
            if (root != null) {
                root.updateFocusState(0, 0, mouseX, mouseY);
                root.draw(graphics, 0, 0, this.width, this.height, mouseX, mouseY, partialTick);
                var tooltip = root.getTooltipLines();
                if (tooltip != null && !tooltip.isEmpty()) {
                    graphics.renderComponentTooltip(this.font, tooltip, mouseX, mouseY);
                }
            }
            renderAfterMenu(graphics, mouseX, mouseY, partialTick);
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
    public boolean isPauseScreen() {
        return false;
    }
}
