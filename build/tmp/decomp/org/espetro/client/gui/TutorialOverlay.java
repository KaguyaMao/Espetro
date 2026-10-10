/*
 * Decompiled with CFR 0.152.
 */
package org.espetro.client.gui;

import org.espetro.client.gui.TutorialClientController;

public final class TutorialOverlay {
    private TutorialOverlay() {
    }

    public static void show(String newStepId, int newIndex, int newTotal, boolean newAllowSkip) {
        TutorialClientController.show(newStepId, newIndex, newTotal, newAllowSkip);
    }

    public static void clear() {
        TutorialClientController.clear();
    }

    public static void tick() {
        TutorialClientController.tick();
    }

    public static boolean isVisible() {
        return TutorialClientController.isActive();
    }

    public static String getStepId() {
        return TutorialClientController.getStepId();
    }
}

