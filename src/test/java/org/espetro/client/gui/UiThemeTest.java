package org.espetro.client.gui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiThemeTest {
    private final UiTheme.Mode originalMode = UiTheme.mode();

    @AfterEach
    void restoreMode() {
        UiTheme.setMode(originalMode);
    }

    @Test
    void revisionChangesOnlyWhenTheModeChanges() {
        UiTheme.setMode(UiTheme.Mode.DARK);
        long previousRevision = UiTheme.revision();

        for (int cycle = 0; cycle < 3; cycle++) {
            UiTheme.setMode(UiTheme.Mode.DARK);
            assertEquals(previousRevision, UiTheme.revision());

            UiTheme.setMode(UiTheme.Mode.LIGHT);
            assertEquals(UiTheme.Mode.LIGHT, UiTheme.mode());
            assertEquals(++previousRevision, UiTheme.revision());

            UiTheme.setMode(UiTheme.Mode.LIGHT);
            assertEquals(previousRevision, UiTheme.revision());

            UiTheme.setMode(UiTheme.Mode.DARK);
            assertEquals(UiTheme.Mode.DARK, UiTheme.mode());
            assertEquals(++previousRevision, UiTheme.revision());
        }
    }

    @Test
    void glassControlsRemainReadableOverBrightAndDarkScenesInBothPalettes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            Map<String, Integer> scenes = Map.of("black scene", 0xFF000000,
                "white scene", 0xFFFFFFFF);
            Map<String, Integer> controls = glassControls();
            for (var scene : scenes.entrySet()) {
                int shadedScene = composite(UiTheme.FROST_SCRIM, scene.getValue());
                int panel = composite(UiTheme.GLASS_PANEL, shadedScene);
                String context = mode + " over " + scene.getKey();
                assertContrast(composite(UiTheme.TEXT, panel), panel, 4.5,
                    context + " card body");
                assertContrast(composite(UiTheme.TEXT_SECOND, panel), panel, 4.5,
                    context + " card secondary text");

                for (var control : controls.entrySet()) {
                    int renderedControl = composite(control.getValue(), panel);
                    // Disabled labels intentionally communicate lower emphasis.
                    if (!control.getKey().equals("disabled")) {
                        assertContrast(composite(UiTheme.TEXT, renderedControl),
                            renderedControl, 4.5, context + " " + control.getKey());
                    }
                }
            }
        }
    }

    @Test
    void glassSurfacesStayTranslucentAndControlStatesRemainDistinct() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            assertTranslucent(UiTheme.FROST_SCRIM, mode + " scene scrim");
            assertTranslucent(UiTheme.GLASS_PANEL, mode + " panel");
            for (var control : glassControls().entrySet()) {
                assertTranslucent(control.getValue(), mode + " " + control.getKey());
            }
            int normalAlpha = UiTheme.CONTROL_NORMAL >>> 24;
            assertTrue((UiTheme.CONTROL_HOVER >>> 24) > normalAlpha,
                mode + " hover must visibly strengthen the control tint");
            assertTrue((UiTheme.CONTROL_SELECTED >>> 24) > normalAlpha,
                mode + " selection must remain visible without a border");
            assertTrue((UiTheme.CONTROL_PRIMARY >>> 24) > normalAlpha,
                mode + " primary controls need more emphasis than normal controls");
            assertTrue((UiTheme.CONTROL_DISABLED >>> 24) < normalAlpha,
                mode + " disabled controls must have less emphasis");
        }
    }

    @Test
    void opaqueInverseSurfacesKeepReadableTextInBothPalettes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            assertContrast(UiTheme.ON_INK, UiTheme.INK_FILL, 7, mode + " inverse body");
            assertContrast(UiTheme.ON_INK_SECOND, UiTheme.INK_FILL, 4.5,
                mode + " inverse secondary text");
        }
    }

    @Test
    void contrastTextChoosesTheStrongerInkForOpaqueFillsInBothModes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            for (int fill : new int[] {UiTheme.ACCENT, UiTheme.DARK_INK, UiTheme.LIGHT_INK}) {
                double darkContrast = contrastRatio(UiTheme.DARK_INK, fill);
                double lightContrast = contrastRatio(UiTheme.LIGHT_INK, fill);
                int expected = darkContrast >= lightContrast
                    ? UiTheme.DARK_INK : UiTheme.LIGHT_INK;
                assertEquals(expected, UiTheme.contrastText(fill),
                    mode + " must select the ink with greater contrast");
                assertContrast(UiTheme.contrastText(fill), fill, 7,
                    mode + " opaque fill contrast");
            }
        }
    }

    @Test
    void everySolidSemanticFillUsesReadableContrastTextInBothModes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            Map<String, Integer> fills = Map.ofEntries(
                Map.entry("accent", UiTheme.ACCENT),
                Map.entry("gold", UiTheme.GOLD),
                Map.entry("attack", UiTheme.ATTACK),
                Map.entry("defend", UiTheme.DEFEND),
                Map.entry("positive", UiTheme.POSITIVE),
                Map.entry("warning", UiTheme.WARNING),
                Map.entry("negative", UiTheme.NEGATIVE),
                Map.entry("purple", UiTheme.PURPLE),
                Map.entry("squad", UiTheme.SQUAD));
            for (var fill : fills.entrySet()) {
                assertContrast(UiTheme.contrastText(fill.getValue()), fill.getValue(), 4.5,
                    mode + " solid " + fill.getKey());
            }
        }
    }

    @Test
    void everyMutableColorReturnsToItsOriginalValueAcrossRepeatedSwitches()
            throws IllegalAccessException {
        UiTheme.setMode(UiTheme.Mode.DARK);
        Map<String, Integer> dark = mutableColorSnapshot();
        assertFalse(dark.isEmpty());

        UiTheme.setMode(UiTheme.Mode.LIGHT);
        Map<String, Integer> light = mutableColorSnapshot();
        assertFalse(dark.equals(light), "The two palettes must differ");

        for (int cycle = 0; cycle < 3; cycle++) {
            UiTheme.setMode(UiTheme.Mode.DARK);
            assertColorsEqual(dark, mutableColorSnapshot(), "dark cycle " + cycle);
            UiTheme.setMode(UiTheme.Mode.LIGHT);
            assertColorsEqual(light, mutableColorSnapshot(), "light cycle " + cycle);
        }
    }

    private static Map<String, Integer> mutableColorSnapshot() throws IllegalAccessException {
        Map<String, Integer> colors = new TreeMap<>();
        for (var field : UiTheme.class.getFields()) {
            int modifiers = field.getModifiers();
            if (field.getType() == int.class && Modifier.isStatic(modifiers)
                    && !Modifier.isFinal(modifiers)) {
                colors.put(field.getName(), field.getInt(null));
            }
        }
        return colors;
    }

    private static void assertColorsEqual(Map<String, Integer> expected,
            Map<String, Integer> actual, String label) {
        assertEquals(expected.keySet(), actual.keySet(), label + " token names");
        for (String name : expected.keySet()) {
            assertEquals(expected.get(name), actual.get(name), label + " token " + name);
        }
    }

    private static Map<String, Integer> glassControls() {
        return Map.of("normal", UiTheme.CONTROL_NORMAL,
            "hover", UiTheme.CONTROL_HOVER,
            "selected", UiTheme.CONTROL_SELECTED,
            "disabled", UiTheme.CONTROL_DISABLED,
            "primary", UiTheme.CONTROL_PRIMARY);
    }

    private static void assertTranslucent(int color, String label) {
        int alpha = color >>> 24;
        assertTrue(alpha > 0 && alpha < 255,
            () -> label + " alpha must be visible and translucent, was " + alpha);
    }

    /** Straight-alpha source-over, matching the order used by the GUI layers. */
    private static int composite(int foreground, int background) {
        double foregroundAlpha = (foreground >>> 24) / 255.0;
        double backgroundAlpha = (background >>> 24) / 255.0;
        double outputAlpha = foregroundAlpha + backgroundAlpha * (1 - foregroundAlpha);
        if (outputAlpha == 0) return 0;
        int result = (int) Math.round(outputAlpha * 255) << 24;
        for (int shift : new int[] {16, 8, 0}) {
            double foregroundChannel = (foreground >>> shift) & 255;
            double backgroundChannel = (background >>> shift) & 255;
            int channel = (int) Math.round((foregroundChannel * foregroundAlpha
                + backgroundChannel * backgroundAlpha * (1 - foregroundAlpha)) / outputAlpha);
            result |= channel << shift;
        }
        return result;
    }

    private static void assertContrast(int foreground, int background,
            double minimum, String label) {
        double ratio = contrastRatio(foreground, background);
        assertTrue(ratio >= minimum, () -> label + " contrast " + ratio + " < " + minimum);
    }

    private static double contrastRatio(int foreground, int background) {
        double foregroundLuminance = luminance(foreground);
        double backgroundLuminance = luminance(background);
        return (Math.max(foregroundLuminance, backgroundLuminance) + 0.05)
            / (Math.min(foregroundLuminance, backgroundLuminance) + 0.05);
    }

    private static double luminance(int color) {
        return 0.2126 * linearChannel((color >>> 16) & 255)
            + 0.7152 * linearChannel((color >>> 8) & 255)
            + 0.0722 * linearChannel(color & 255);
    }

    private static double linearChannel(int channel) {
        double value = channel / 255.0;
        return value <= 0.04045 ? value / 12.92 : Math.pow((value + 0.055) / 1.055, 2.4);
    }
}
