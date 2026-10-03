/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.render;

public final class GuiItemDepths {
    public static final float SCREEN_ITEM_MODEL_Z = 150.0f;
    public static final float SCREEN_ITEM_DECORATION_Z = 200.0f;
    public static final float SCREEN_ITEM_FOREGROUND_Z = 200.125f;
    public static final float SCREEN_FLOATING_ITEM_MODEL_Z = 200.25f;
    public static final float SCREEN_FLOATING_ITEM_DECORATION_Z = 250.25f;
    public static final float FLAT_DOCUMENT_LAYER_STEP = 251.25f;

    private GuiItemDepths() {
    }

    public static float foregroundZ(float decorationZ, boolean accumulateDepth) {
        if (accumulateDepth) {
            return 0.0f;
        }
        return decorationZ + 0.125f;
    }
}

