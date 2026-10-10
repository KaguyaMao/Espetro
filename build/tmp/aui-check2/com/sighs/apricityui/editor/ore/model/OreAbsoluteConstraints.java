/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.model;

import com.sighs.apricityui.editor.ore.model.OreComponentNode;

public final class OreAbsoluteConstraints {
    private OreAbsoluteConstraints() {
    }

    public static void setOffset(OreComponentNode component, String property, String value) {
        if (component == null || property == null) {
            return;
        }
        String normalized = property.trim();
        component.style().set(normalized, value);
        if (value == null || value.isBlank()) {
            return;
        }
        switch (normalized) {
            case "left": {
                component.style().set("right", null);
                break;
            }
            case "right": {
                component.style().set("left", null);
                break;
            }
            case "top": {
                component.style().set("bottom", null);
                break;
            }
            case "bottom": {
                component.style().set("top", null);
                break;
            }
        }
    }
}

