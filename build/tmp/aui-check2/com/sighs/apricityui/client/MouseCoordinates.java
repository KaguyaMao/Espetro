/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.client;

import com.sighs.apricityui.layout.Position;

public final class MouseCoordinates {
    private MouseCoordinates() {
    }

    public static Position toGui(double cursorX, double cursorY, int windowWidth, int windowHeight, int guiWidth, int guiHeight) {
        return new Position(MouseCoordinates.scaleAxis(cursorX, windowWidth, guiWidth), MouseCoordinates.scaleAxis(cursorY, windowHeight, guiHeight));
    }

    private static double scaleAxis(double cursor, int windowSize, int guiSize) {
        if (!Double.isFinite(cursor)) {
            return 0.0;
        }
        if (windowSize <= 0 || guiSize <= 0) {
            return cursor;
        }
        return cursor * (double)guiSize / (double)windowSize;
    }
}

