/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.util.Mth;
import org.espetro.client.aui.GuiElement;
import org.espetro.network.FortificationProgressPacket;

public final class FortificationProgressHud {
    private static final long VISIBLE_MS = 3000L;
    private static volatile State state = State.hidden();

    private FortificationProgressHud() {
    }

    public static void update(FortificationProgressPacket packet) {
        state = new State(packet.displayName(), Math.max(0, packet.progress()), Math.max(1, packet.required()), packet.building(), Util.m_137550_() + 3000L);
    }

    public static GuiElement createElement() {
        return new ProgressElement();
    }

    private record State(String name, int progress, int required, boolean building, long hideAt) {
        static State hidden() {
            return new State("", 0, 1, true, 0L);
        }
    }

    private static final class ProgressElement
    extends GuiElement {
        private ProgressElement() {
            super(8, 34, 144, 18);
        }

        @Override
        public void draw(GuiGraphics graphics, int x, int y, int width, int height, int mouseX, int mouseY, float partialTick) {
            int percent;
            String text;
            State snapshot = state;
            if (snapshot.hideAt <= Util.m_137550_()) {
                return;
            }
            Minecraft mc = Minecraft.m_91087_();
            int left = this.getX();
            int top = this.getY();
            graphics.m_280509_(left, top, left + 144, top + 18, -804187887);
            graphics.m_280509_(left, top, left + 144, top + 1, -1335991188);
            graphics.m_280509_(left, top + 17, left + 144, top + 18, -1335991188);
            int fill = Mth.m_14045_((int)Math.round(140.0 * (double)snapshot.progress / (double)snapshot.required), 0, 140);
            int color = snapshot.building ? -1854165 : -1881281;
            graphics.m_280509_(left + 2, top + 13, left + 142, top + 16, -13421773);
            if (fill > 0) {
                graphics.m_280509_(left + 2, top + 13, left + 2 + fill, top + 16, color);
            }
            if (mc.f_91062_.m_92895_(text = snapshot.name + " " + (percent = Mth.m_14045_((int)Math.round(100.0 * (double)snapshot.progress / (double)snapshot.required), 0, 100)) + "%  " + snapshot.progress + "/" + snapshot.required) > 138) {
                text = snapshot.name + " " + percent + "%";
            }
            graphics.m_280056_(mc.f_91062_, text, left + 3, top + 3, 0xFFFFFF, false);
        }
    }
}

