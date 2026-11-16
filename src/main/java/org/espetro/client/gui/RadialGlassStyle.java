package org.espetro.client.gui;

import java.util.List;

/** World-overlay surfaces stay black in both light and dark menu themes. */
public final class RadialGlassStyle {
    private RadialGlassStyle() {
    }

    public static List<String> ringColors() {
        // EsRadial consumes Minecraft-style #AARRGGBB here, not CSS #RRGGBBAA.
        return List.of(UiTheme.hex(UiTheme.RADIAL_BACKGROUND),
            UiTheme.hex(UiTheme.RADIAL_BACKGROUND_OUTER));
    }

    /** Canvas constants use CSS rgba rather than EsRadial's public ARGB strings. */
    public static String css(int argb) {
        return "rgba(" + ((argb >>> 16) & 255) + "," + ((argb >>> 8) & 255) + ","
            + (argb & 255) + "," + ((argb >>> 24) & 255) / 255.0D + ")";
    }
}
