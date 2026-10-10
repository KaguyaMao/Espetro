/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.gui.Font
 *  net.minecraft.client.gui.GuiGraphics
 *  net.minecraft.network.chat.Component
 *  net.minecraft.network.chat.FormattedText
 *  net.minecraftforge.api.distmarker.Dist
 *  net.minecraftforge.client.event.RenderGuiEvent$Post
 *  net.minecraftforge.eventbus.api.SubscribeEvent
 *  net.minecraftforge.fml.common.Mod$EventBusSubscriber
 */
package LOL_141.vehicle_addition.client.event;

import LOL_141.vehicle_addition.radio.LyricManager;
import LOL_141.vehicle_addition.radio.RadioManager;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid="vehicle_addition", value={Dist.CLIENT})
public final class RadioHudRenderer {
    private static final long SHOW_MS = 4000L;
    private static final long FADE_MS = 600L;
    private static final int TOP_Y = 10;
    private static final float BOTTOM_RATIO = 0.18f;
    private static volatile Component message = null;
    private static volatile long showUntil = 0L;

    private RadioHudRenderer() {
    }

    public static void show(Component msg) {
        message = msg;
        showUntil = System.currentTimeMillis() + 4000L;
    }

    public static void clear() {
        message = null;
        showUntil = 0L;
    }

    @SubscribeEvent
    public static void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.m_91087_();
        GuiGraphics gui = event.getGuiGraphics();
        Font font = mc.f_91062_;
        int width = mc.m_91268_().m_85445_();
        RadioHudRenderer.drawLyrics(gui, font, width);
        Component msg = message;
        if (msg == null) {
            return;
        }
        long remain = showUntil - System.currentTimeMillis();
        if (remain <= 0L) {
            message = null;
            return;
        }
        int textWidth = font.m_92852_((FormattedText)msg);
        int x = (width - textWidth) / 2;
        int color = 0xFFFFFF;
        if (remain < 600L) {
            int alpha = (int)((float)(255L * remain) / 600.0f);
            color = alpha << 24 | 0xFFFFFF;
        }
        gui.m_280509_(x - 4, 8, x + textWidth + 4, 20, Integer.MIN_VALUE);
        gui.m_280430_(font, msg, x, 10, color);
    }

    private static void drawLyrics(GuiGraphics gui, Font font, int width) {
        if (!LyricManager.isActive()) {
            return;
        }
        long posMs = RadioManager.playbackPositionMs();
        if (posMs < 0L) {
            return;
        }
        List<LyricManager.LyricLine> lyrics = LyricManager.lyrics();
        int idx = LyricManager.currentIndex(posMs);
        if (lyrics == null || idx < 0 || idx >= lyrics.size()) {
            return;
        }
        int guiHeight = Minecraft.m_91087_().m_91268_().m_85446_();
        int baseY = guiHeight - (int)((float)guiHeight * 0.18f);
        String current = lyrics.get(idx).text();
        int w = font.m_92895_(current);
        int lx = (width - w) / 2;
        gui.m_280509_(lx - 3, baseY - 1, lx + w + 3, baseY + 9, 0x60000000);
        gui.m_280488_(font, current, lx, baseY, 0xFFFFFF);
        if (idx > 0) {
            String prev = lyrics.get(idx - 1).text();
            int pw = font.m_92895_(prev);
            int px = (width - pw) / 2;
            gui.m_280488_(font, prev, px, baseY + 11, 0x808080);
        }
    }
}

