package org.espetro.client.gui;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UiPaintTextTest {
    private final UiTheme.Mode originalMode = UiTheme.mode();

    @AfterEach
    void restoreMode() {
        UiTheme.setMode(originalMode);
    }

    @Test
    void legacyColorsResolveToTheActivePaletteInBothModes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            int baseColor = UiTheme.ON_INK;
            int[] expected = {
                UiTheme.TEXT_THIRD, UiTheme.DEFEND, UiTheme.POSITIVE, UiTheme.DEFEND,
                UiTheme.NEGATIVE, UiTheme.PURPLE, UiTheme.GOLD, UiTheme.TEXT_SECOND,
                UiTheme.TEXT_THIRD, UiTheme.DEFEND, UiTheme.POSITIVE, UiTheme.DEFEND,
                UiTheme.NEGATIVE, UiTheme.PURPLE, UiTheme.GOLD, baseColor
            };
            String codes = "0123456789abcdef";
            for (int i = 0; i < codes.length(); i++) {
                char code = codes.charAt(i);
                Component component = UiPaint.themed("\u00a7" + code + "text", baseColor);
                List<Span> spans = spans(component);

                assertEquals("text", component.getString(), mode + " legacy code " + code);
                assertEquals(1, spans.size());
                assertColor(expected[i], spans.get(0), mode + " legacy code " + code);
            }
        }
    }

    @Test
    void whiteAndResetFollowTheSuppliedInverseButtonTextColor() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            int baseColor = UiTheme.ON_INK;
            List<Span> spans = spans(UiPaint.themed(
                "plain\u00a7fwhite\u00a7cred\u00a7rreset", baseColor));

            assertEquals(List.of("plain", "white", "red", "reset"), textParts(spans));
            assertColor(baseColor, spans.get(0), mode + " initial inverse button text");
            assertColor(baseColor, spans.get(1), mode + " white uses inverse button base");
            assertColor(UiTheme.NEGATIVE, spans.get(2), mode + " explicit error text");
            assertColor(baseColor, spans.get(3), mode + " reset uses inverse button base");
        }
    }

    @Test
    void emphasisPersistsAcrossTextRunsAndResetClearsIt() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            List<Span> spans = spans(UiPaint.themed(
                "plain\u00a7lbold\u00a7oitalic\u00a7nunderlined\u00a7rreset", UiTheme.TEXT));

            assertEquals(List.of("plain", "bold", "italic", "underlined", "reset"), textParts(spans));
            assertFalse(spans.get(0).style().isBold());
            assertTrue(spans.get(1).style().isBold());
            assertFalse(spans.get(1).style().isItalic());
            assertTrue(spans.get(2).style().isBold());
            assertTrue(spans.get(2).style().isItalic());
            assertTrue(spans.get(3).style().isBold());
            assertTrue(spans.get(3).style().isItalic());
            assertTrue(spans.get(3).style().isUnderlined());
            assertFalse(spans.get(4).style().isBold());
            assertFalse(spans.get(4).style().isItalic());
            assertFalse(spans.get(4).style().isUnderlined());
            for (Span span : spans) {
                assertColor(UiTheme.TEXT, span, mode + " emphasis retains base color");
            }
        }
    }

    @Test
    void legacyColorChangeClearsPreviousBoldAsVanillaFormattingDoes() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            List<Span> spans = spans(UiPaint.themed("\u00a7lbold\u00a7cred", UiTheme.TEXT));

            assertEquals(List.of("bold", "red"), textParts(spans));
            assertTrue(spans.get(0).style().isBold());
            assertFalse(spans.get(1).style().isBold());
            assertColor(UiTheme.NEGATIVE, spans.get(1), mode + " red after bold");
        }
    }

    @Test
    void yellowBaseColorsUseReadableGoldOnLightSurfaces() {
        UiTheme.setMode(UiTheme.Mode.LIGHT);
        for (int yellow : new int[] {UiTheme.ACCENT, UiTheme.ACCENT_DIM, UiTheme.ACCENT_LINE}) {
            List<Span> spans = spans(UiPaint.themed("plain\u00a7fwhite\u00a7rreset", yellow));

            assertEquals(List.of("plain", "white", "reset"), textParts(spans));
            for (Span span : spans) {
                assertColor(UiTheme.GOLD, span, "Light palette yellow must become readable gold");
            }
        }
    }

    @Test
    void nullEmptyAndFormattingOnlyTextHaveNoVisibleRuns() {
        for (UiTheme.Mode mode : UiTheme.Mode.values()) {
            UiTheme.setMode(mode);
            for (String value : new String[] {null, "", "\u00a7l\u00a7r"}) {
                Component component = UiPaint.themed(value, UiTheme.TEXT);

                assertNotNull(component);
                assertEquals("", component.getString());
                assertTrue(spans(component).isEmpty());
            }
        }
    }

    private static List<Span> spans(Component component) {
        List<Span> result = new ArrayList<>();
        component.visit((style, text) -> {
            if (!text.isEmpty()) {
                result.add(new Span(text, style));
            }
            return Optional.empty();
        }, Style.EMPTY);
        return result;
    }

    private static List<String> textParts(List<Span> spans) {
        return spans.stream().map(Span::text).toList();
    }

    private static void assertColor(int expected, Span span, String message) {
        assertNotNull(span.style().getColor(), message);
        assertEquals(expected & 0x00FFFFFF, span.style().getColor().getValue(), message);
    }

    private record Span(String text, Style style) {
    }
}
