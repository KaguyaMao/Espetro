/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

final class AspectFit {
    private AspectFit() {
    }

    static Size within(int sourceWidth, int sourceHeight, int maxWidth, int maxHeight) {
        int safeMaxWidth = Math.max(1, maxWidth);
        int safeMaxHeight = Math.max(1, maxHeight);
        if (sourceWidth <= 0 || sourceHeight <= 0) {
            return new Size(safeMaxWidth, safeMaxHeight);
        }
        if ((long)safeMaxWidth * (long)sourceHeight <= (long)safeMaxHeight * (long)sourceWidth) {
            int height = Math.max(1, (int)((long)safeMaxWidth * (long)sourceHeight / (long)sourceWidth));
            return new Size(safeMaxWidth, height);
        }
        int width = Math.max(1, (int)((long)safeMaxHeight * (long)sourceWidth / (long)sourceHeight));
        return new Size(width, safeMaxHeight);
    }

    record Size(int width, int height) {
    }
}

