package org.espetro.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import net.minecraft.Util;
import org.espetro.network.FortificationProgressPacket;
import org.espetro.client.aui.GuiElement;

/** Persistent MUtil construction bar; state changes only when a server packet arrives. */
public final class FortificationProgressHud {
    private static final long VISIBLE_MS = 3_000L;
    private static volatile State state = State.hidden();

    private FortificationProgressHud() {
    }

    public static void update(FortificationProgressPacket packet) {
        state = new State(packet.displayName(), Math.max(0, packet.progress()),
            Math.max(1, packet.required()), packet.building(), Util.getMillis() + VISIBLE_MS);
    }

    public static GuiElement createElement() {
        return new ProgressElement();
    }

    private record State(String name, int progress, int required, boolean building, long hideAt) {
        static State hidden() { return new State("", 0, 1, true, 0L); }
    }

    private static final class ProgressElement extends GuiElement {
        private static final int PANEL_WIDTH = 144;
        private static final int PANEL_HEIGHT = 18;
        private static final int BAR_HEIGHT = 4;

        private ProgressElement() {
            super(8, 34, PANEL_WIDTH, PANEL_HEIGHT);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height,
                         int mouseX, int mouseY, float partialTick) {
            State snapshot = state;
            if (snapshot.hideAt <= Util.getMillis()) return;
            Minecraft mc = Minecraft.getInstance();
            int left = getX();
            int top = getY();

            // 圆角玻璃卡：阴影 + 渐变底 + 发丝描边 + 顶部内高光。
            UiPaint.card(graphics, left, top, PANEL_WIDTH, PANEL_HEIGHT);

            float ratio = snapshot.required <= 0 ? 0f
                : Math.min(1f, snapshot.progress / (float) snapshot.required);
            int accent = snapshot.building ? UiTheme.WARNING : UiTheme.NEGATIVE;
            int percent = Mth.clamp((int) Math.round(100.0D * snapshot.progress / snapshot.required), 0, 100);
            String text = snapshot.name + " " + percent + "%  " + snapshot.progress + "/" + snapshot.required;
            if (mc.font.width(text) > PANEL_WIDTH - 12) text = snapshot.name + " " + percent + "%";
            UiPaint.textShadowed(graphics, text, left + 5, top + 2, UiTheme.TEXT);

            // 施工进度：内凹槽 + 渐变填充 + 高光。
            UiPaint.progress(graphics, left + 4, top + 12, PANEL_WIDTH - 8, BAR_HEIGHT,
                ratio, accent);
        }
    }
}
