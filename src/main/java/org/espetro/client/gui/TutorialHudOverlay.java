package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

/**
 * 自绘教程 HUD：说明卡片 + 左下角「退出教程」。
 * 不依赖 AuraTip 关卡片才能下一步。
 */
public final class TutorialHudOverlay {

    private static final int EXIT_W = 88;
    private static final int EXIT_H = 16;
    private static final int EXIT_MARGIN = 8;
    private static final int CARD_MARGIN = 10;
    private static final int CARD_MAX_W = 320;
    private static final int CARD_PAD = 10;

    private TutorialHudOverlay() {
    }

    public static void onStepChanged() {
        // 预留：可在此重置动画
    }

    public static void clear() {
        // no state
    }

    public static void render(GuiGraphics graphics, Minecraft mc, float partialTick) {
        if (!TutorialClientController.isActive() || mc == null || mc.font == null) {
            return;
        }
        Font font = mc.font;
        int sw = mc.getWindow().getGuiScaledWidth();
        int sh = mc.getWindow().getGuiScaledHeight();

        String stepId = TutorialClientController.getStepId();
        Component title = Component.translatable("tutorial.step." + stepId + ".title")
            .append(Component.literal("  "
                + TutorialClientController.getIndex() + " / "
                + TutorialClientController.getTotal()));
        Component body = Component.translatable("tutorial.step." + stepId + ".body");
        String footer = "Enter = 下一步"
            + (TutorialClientController.isAllowSkip() ? "  ·  左下角可完全退出" : "");

        int cardW = Math.min(CARD_MAX_W, Math.max(180, sw - CARD_MARGIN * 2));
        String bodyText = body.getString();
        int titleH = Math.round(font.lineHeight * UiTheme.TYPE_HEADING);
        int footerH = Math.round(font.lineHeight * UiTheme.TYPE_MICRO);
        int bodyW = cardW - CARD_PAD * 2;
        int bodyH = font.wordWrapHeight(bodyText, bodyW);
        int cardH = titleH + bodyH + footerH + 30;
        int cardX = (sw - cardW) / 2;
        int cardY = sh - cardH - EXIT_MARGIN - EXIT_H - 6;
        int titleY = cardY + 8;
        int bodyY = titleY + titleH + 6;
        int dividerY = bodyY + bodyH + 4;
        int footerY = dividerY + 4;

        // 底部居中的圆角玻璃说明卡（不挡左下退出）：金色强调条 + 发丝分割线。
        UiPaint.card(graphics, cardX, cardY, cardW, cardH);
        UiPaint.accentBarLeft(graphics, cardX + 1, cardY + 4, cardH - 8, UiTheme.GOLD);
        UiPaint.textScaled(graphics,
            EspetroAuiWidgets.trimToWidth(title.getString(), bodyW),
            cardX + CARD_PAD, titleY, UiTheme.GOLD, UiTheme.TYPE_HEADING, true);
        graphics.drawWordWrap(font, body,
            cardX + CARD_PAD, bodyY, bodyW, UiTheme.TEXT);
        UiPaint.hairline(graphics, cardX + CARD_PAD, dividerY, bodyW, UiTheme.HAIRLINE);
        UiPaint.textScaled(graphics, footer, cardX + CARD_PAD, footerY,
            UiTheme.TEXT_THIRD, UiTheme.TYPE_MICRO, false);

        // 左下「退出教程」：内凹底色 + 危险色描边，hover 只换色不位移。
        int exitX = EXIT_MARGIN;
        int exitY = sh - EXIT_MARGIN - EXIT_H;
        boolean hover = isOverExit(mc.mouseHandler.xpos(), mc.mouseHandler.ypos(), mc);
        int exitAccent = UiTheme.NEGATIVE;
        UiPaint.roundRect(graphics, exitX, exitY, EXIT_W, EXIT_H, UiTheme.RADIUS_CONTROL,
            hover ? UiTheme.NEGATIVE_SOFT : UiTheme.withAlpha(UiTheme.SURFACE_0, 0xE0));
        UiPaint.strokeRoundRect(graphics, exitX, exitY, EXIT_W, EXIT_H, UiTheme.RADIUS_CONTROL,
            hover ? exitAccent : UiTheme.withAlpha(exitAccent, 0x66));
        String exitLabel = "退出教程";
        UiPaint.textCentered(graphics, exitLabel, exitX,
            exitY + (EXIT_H - font.lineHeight) / 2, EXIT_W,
            hover ? exitAccent : UiTheme.alpha(exitAccent, 0.85f), 1f, true);
    }

    public static boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (!TutorialClientController.isActive() || button != 0) {
            return false;
        }
        Minecraft mc = Minecraft.getInstance();
        if (mc == null) {
            return false;
        }
        int sh = mc.getWindow().getGuiScaledHeight();
        int exitX = EXIT_MARGIN;
        int exitY = sh - EXIT_MARGIN - EXIT_H;
        if (mouseX >= exitX && mouseX < exitX + EXIT_W
            && mouseY >= exitY && mouseY < exitY + EXIT_H) {
            TutorialClientController.requestSkipAll();
            return true;
        }
        return false;
    }

    private static boolean isOverExit(double windowMouseX, double windowMouseY, Minecraft mc) {
        // window coords → scaled GUI coords
        double scale = mc.getWindow().getGuiScale();
        double mx = windowMouseX / scale;
        double my = windowMouseY / scale;
        int sh = mc.getWindow().getGuiScaledHeight();
        int exitX = EXIT_MARGIN;
        int exitY = sh - EXIT_MARGIN - EXIT_H;
        return mx >= exitX && mx < exitX + EXIT_W && my >= exitY && my < exitY + EXIT_H;
    }
}
