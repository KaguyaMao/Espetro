/*
 * Decompiled with CFR 0.152.
 */
package com.sighs.apricityui.editor.ore.canvas;

import com.sighs.apricityui.init.Element;
import java.util.Map;
import java.util.UUID;

public final class OreCanvasHitTester {
    public UUID hit(Map<UUID, Element> elements, double x, double y) {
        UUID hit = null;
        double hitArea = Double.POSITIVE_INFINITY;
        for (Map.Entry<UUID, Element> entry : elements.entrySet()) {
            double area;
            Element.DOMRect rect = entry.getValue().getBoundingClientRect();
            if (x < rect.left || x > rect.right || y < rect.top || y > rect.bottom || !((area = rect.width * rect.height) <= hitArea)) continue;
            hit = entry.getKey();
            hitArea = area;
        }
        return hit;
    }
}

