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
    void bodyAndSecondaryTextRemainReadableInBothPalettes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            Map<String, Integer> surfaces = Map.of(
                "backdrop", UiTheme.BACKDROP_TOP,
                "surface 0", UiTheme.SURFACE_0,
                "card", UiTheme.CARD_TOP,
                "surface 2", UiTheme.SURFACE_2,
                "surface 3", UiTheme.SURFACE_3,
                "sunken", UiTheme.SUNKEN,
                "page", UiTheme.SOLID_PAGE);
            for (var surface : surfaces.entrySet()) {
                assertContrast(UiTheme.TEXT, surface.getValue(), 7,
                    mode + " body on " + surface.getKey());
                assertContrast(UiTheme.TEXT_SECOND, surface.getValue(), 4.5,
                    mode + " secondary text on " + surface.getKey());
            }
            assertContrast(UiTheme.ON_INK, UiTheme.INK_FILL, 7, mode + " inverse body");
            assertContrast(UiTheme.ON_INK_SECOND, UiTheme.INK_FILL, 4.5,
                mode + " inverse secondary text");
        }
    }

    @Test
    void accentUsesDarkInkAndDarkFillsUseLightInkInBothModes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            assertEquals(UiTheme.DARK_INK, UiTheme.contrastText(UiTheme.ACCENT),
                mode + " accent must keep dark text");
            assertEquals(UiTheme.LIGHT_INK, UiTheme.contrastText(UiTheme.DARK_INK),
                mode + " dark fill must keep light text");
            assertContrast(UiTheme.contrastText(UiTheme.ACCENT), UiTheme.ACCENT, 7,
                mode + " accent contrast");
            assertContrast(UiTheme.contrastText(UiTheme.DARK_INK), UiTheme.DARK_INK, 7,
                mode + " dark fill contrast");
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

    private static void assertContrast(int foreground, int background,
            double minimum, String label) {
        double foregroundLuminance = luminance(foreground);
        double backgroundLuminance = luminance(background);
        double ratio = (Math.max(foregroundLuminance, backgroundLuminance) + 0.05)
            / (Math.min(foregroundLuminance, backgroundLuminance) + 0.05);
        assertTrue(ratio >= minimum, () -> label + " contrast " + ratio + " < " + minimum);
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
