/*
 * Decompiled with CFR 0.152.
 */
package cc.sighs.auratip.client.render;

import cc.sighs.auratip.data.RadialMenuData;
import cc.sighs.auratip.util.ColorUtil;
import java.util.ArrayList;
import java.util.List;

final class RadialMenuSlotColors {
    private RadialMenuSlotColors() {
    }

    static List<Layer> layers(RadialMenuData.Slot slot, boolean highlighted, float hoverFill, float eased) {
        ArrayList layers = new ArrayList(1);
        if (highlighted && hoverFill > 0.01f) {
            slot.highlightColor().ifPresent(color -> {
                int rgb = ColorUtil.parseRgb(color);
                int inner = ColorUtil.withAlpha(rgb, (int)(70.0f * eased));
                int outer = ColorUtil.withAlpha(rgb, (int)(180.0f * eased));
                layers.add(new Layer(Kind.HOVER, inner, outer, hoverFill));
            });
        }
        return List.copyOf(layers);
    }

    record Layer(Kind kind, int innerArgb, int outerArgb, float fill) {
    }

    static enum Kind {
        BASE,
        HOVER;

    }
}

