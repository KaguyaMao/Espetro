/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.blaze3d.platform.NativeImage
 *  net.minecraft.network.chat.Component
 */
package tech.vvp.vvp.client.firecontrol;

import com.mojang.blaze3d.platform.NativeImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import net.minecraft.network.chat.Component;

public final class TabletFontRasterizer {
    private static final int CHAR_W = 5;
    private static final int CHAR_H = 7;
    private static final int CHAR_SPACING = 1;

    private TabletFontRasterizer() {
    }

    public static void drawString(NativeImage target, Component text, int x, int y, int color) {
        TabletFontRasterizer.drawString(target, text.getString(), x, y, color);
    }

    public static void drawWrapped(NativeImage target, Component text, int x, int y, int maxWidth, int color) {
        for (String line : TabletFontRasterizer.wrap(text.getString(), maxWidth)) {
            TabletFontRasterizer.drawString(target, line, x, y, color);
            y += 8;
        }
    }

    private static void drawString(NativeImage target, String text, int x, int y, int color) {
        String upper = text.toUpperCase(Locale.ROOT);
        int cx = x;
        for (int i = 0; i < upper.length(); ++i) {
            char c = upper.charAt(i);
            TabletFontRasterizer.drawChar(target, cx, y, color, c);
            cx += 6;
        }
    }

    private static List<String> wrap(String text, int maxWidth) {
        ArrayList<String> lines = new ArrayList<String>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();
        int lineWidth = 0;
        for (String word : words) {
            int wordWidth = word.length() * 6;
            if (lineWidth > 0 && lineWidth + wordWidth > maxWidth) {
                lines.add(line.toString());
                line = new StringBuilder();
                lineWidth = 0;
            }
            if (!line.isEmpty()) {
                line.append(' ');
                lineWidth += 6;
            }
            line.append(word);
            lineWidth += wordWidth;
        }
        if (!line.isEmpty()) {
            lines.add(line.toString());
        }
        if (lines.isEmpty()) {
            lines.add("");
        }
        return lines;
    }

    private static void drawChar(NativeImage image, int x, int y, int color, char c) {
        int[] rows = TabletFontRasterizer.glyphRows(c);
        for (int row = 0; row < 7; ++row) {
            int bits = rows[row];
            for (int col = 0; col < 5; ++col) {
                if ((bits >> 4 - col & 1) != 1) continue;
                int px = x + col;
                int py = y + row;
                if (px < 0 || px >= image.m_84982_() || py < 0 || py >= image.m_85084_()) continue;
                image.m_84988_(px, py, color);
            }
        }
    }

    private static int[] glyphRows(char c) {
        return switch (c) {
            case ' ' -> TabletFontRasterizer.row(0, 0, 0, 0, 0, 0, 0);
            case '!' -> TabletFontRasterizer.row(4, 4, 4, 4, 0, 4, 0);
            case '%' -> TabletFontRasterizer.row(25, 26, 2, 4, 8, 19, 19);
            case '(' -> TabletFontRasterizer.row(2, 4, 8, 8, 8, 4, 2);
            case ')' -> TabletFontRasterizer.row(8, 4, 2, 2, 2, 4, 8);
            case '/' -> TabletFontRasterizer.row(1, 2, 4, 8, 16, 0, 0);
            case ':' -> TabletFontRasterizer.row(0, 4, 0, 0, 4, 0, 0);
            case '.' -> TabletFontRasterizer.row(0, 0, 0, 0, 0, 4, 0);
            case '-' -> TabletFontRasterizer.row(0, 0, 0, 31, 0, 0, 0);
            case ',' -> TabletFontRasterizer.row(0, 0, 0, 0, 4, 4, 8);
            case '0' -> TabletFontRasterizer.row(14, 17, 19, 21, 25, 17, 14);
            case '1' -> TabletFontRasterizer.row(4, 12, 4, 4, 4, 4, 14);
            case '2' -> TabletFontRasterizer.row(14, 17, 1, 6, 8, 16, 31);
            case '3' -> TabletFontRasterizer.row(31, 1, 2, 6, 1, 1, 30);
            case '4' -> TabletFontRasterizer.row(2, 6, 10, 18, 31, 2, 2);
            case '5' -> TabletFontRasterizer.row(31, 16, 30, 1, 1, 17, 14);
            case '6' -> TabletFontRasterizer.row(6, 8, 16, 30, 17, 17, 14);
            case '7' -> TabletFontRasterizer.row(31, 1, 2, 4, 8, 8, 8);
            case '8' -> TabletFontRasterizer.row(14, 17, 17, 14, 17, 17, 14);
            case '9' -> TabletFontRasterizer.row(14, 17, 17, 15, 1, 2, 12);
            case 'A' -> TabletFontRasterizer.row(14, 17, 17, 31, 17, 17, 17);
            case 'B' -> TabletFontRasterizer.row(30, 17, 17, 30, 17, 17, 30);
            case 'C' -> TabletFontRasterizer.row(14, 17, 16, 16, 16, 17, 14);
            case 'D' -> TabletFontRasterizer.row(28, 18, 17, 17, 17, 18, 28);
            case 'E' -> TabletFontRasterizer.row(31, 16, 16, 30, 16, 16, 31);
            case 'F' -> TabletFontRasterizer.row(31, 16, 16, 30, 16, 16, 16);
            case 'G' -> TabletFontRasterizer.row(14, 17, 16, 23, 17, 17, 15);
            case 'H' -> TabletFontRasterizer.row(17, 17, 17, 31, 17, 17, 17);
            case 'I' -> TabletFontRasterizer.row(14, 4, 4, 4, 4, 4, 14);
            case 'J' -> TabletFontRasterizer.row(7, 2, 2, 2, 18, 18, 12);
            case 'K' -> TabletFontRasterizer.row(17, 18, 20, 24, 20, 18, 17);
            case 'L' -> TabletFontRasterizer.row(16, 16, 16, 16, 16, 16, 31);
            case 'M' -> TabletFontRasterizer.row(17, 27, 21, 17, 17, 17, 17);
            case 'N' -> TabletFontRasterizer.row(17, 25, 21, 19, 17, 17, 17);
            case 'O' -> TabletFontRasterizer.row(14, 17, 17, 17, 17, 17, 14);
            case 'P' -> TabletFontRasterizer.row(30, 17, 17, 30, 16, 16, 16);
            case 'Q' -> TabletFontRasterizer.row(14, 17, 17, 17, 21, 18, 13);
            case 'R' -> TabletFontRasterizer.row(30, 17, 17, 30, 20, 18, 17);
            case 'S' -> TabletFontRasterizer.row(15, 16, 16, 14, 1, 1, 30);
            case 'T' -> TabletFontRasterizer.row(31, 4, 4, 4, 4, 4, 4);
            case 'U' -> TabletFontRasterizer.row(17, 17, 17, 17, 17, 17, 14);
            case 'V' -> TabletFontRasterizer.row(17, 17, 17, 17, 10, 10, 4);
            case 'W' -> TabletFontRasterizer.row(17, 17, 17, 21, 21, 27, 17);
            case 'X' -> TabletFontRasterizer.row(17, 17, 10, 4, 10, 17, 17);
            case 'Y' -> TabletFontRasterizer.row(17, 17, 10, 4, 4, 4, 4);
            case 'Z' -> TabletFontRasterizer.row(31, 1, 2, 4, 8, 16, 31);
            default -> TabletFontRasterizer.row(0, 0, 0, 0, 0, 0, 0);
        };
    }

    private static int[] row(int ... values) {
        return values;
    }
}

